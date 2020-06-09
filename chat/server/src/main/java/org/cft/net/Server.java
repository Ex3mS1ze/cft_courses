package org.cft.net;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.cft.common.dto.Participant;
import org.cft.common.net.server.ServerPacket;
import org.cft.common.util.InetValidator;
import org.cft.configuration.ServerConfiguration;
import org.cft.exception.InitializeFailedException;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class Server {
    private static final int TIMEOUT_TIME = 10_000;

    private final Object lock = new Object();
    private volatile boolean hasAvailableThreads = true;
    @Getter
    private volatile boolean isRunning = true;

    private final int maximumThreads;
    private final ServerSocket serverSocket;
    private final CopyOnWriteArrayList<ClientHandler> clientHandlerList;
    private final ExecutorService fixedThreadPool;

    public Server(ServerConfiguration configuration) {
        this(configuration.getPort(), configuration.getInetAddress(), configuration.getMaxClients());
    }

    private Server(int port, String inetAddress, int maximumThreads) {
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
        if (!InetValidator.validatePort(port) || !InetValidator.validateHost(inetAddress) || maximumThreads <= 0) {
            throw new IllegalArgumentException();
        }

        try {
            this.serverSocket = new ServerSocket(port, 0, InetAddress.getByName(inetAddress));
            this.serverSocket.setReuseAddress(true);
            this.maximumThreads = maximumThreads;
        } catch (IllegalArgumentException | IOException | SecurityException e) {
            log.error("", e);
            throw new InitializeFailedException();
        }

        this.clientHandlerList = new CopyOnWriteArrayList<>();
        this.fixedThreadPool = Executors.newFixedThreadPool(maximumThreads);
        log.info("Сервер создан, port = {}, InetAddress = {}", serverSocket.getLocalPort(), serverSocket.getInetAddress());
    }

    public void start() {
        log.info("Сервер запущен");

        Socket clientSocket;
        while (isRunning) {
            try {
                clientSocket = serverSocket.accept();
                clientSocket.setSoTimeout(TIMEOUT_TIME);
                log.info("Сервер получил запрос на подключение");
            } catch (IOException e) {
                log.error("", e);
                continue;
            }

            ClientHandler newClientHandler = new ClientHandler(clientSocket, this);
            fixedThreadPool.submit(newClientHandler);
            clientHandlerList.add(newClientHandler);
            log.info("Добавлен новый клиент '{}'", clientSocket.getRemoteSocketAddress());

            synchronized (lock) {
                hasAvailableThreads = clientHandlerList.size() < maximumThreads;
                if (!hasAvailableThreads) {
                    log.warn("Достигнуто макимальное число клиентов, подключения не принимаются");
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        log.error("", e);
                    }
                }
            }
        }
    }

    void sendToAll(ServerPacket packet) {
        clientHandlerList.stream().filter(ClientHandler::isAuthorFetched).forEach(clientHandler -> clientHandler.sendPacket(packet));
    }

    void removeClient(ClientHandler clientHandler) {
        if (clientHandlerList.remove(clientHandler)) {
            log.info("Клиент '{}' удален, свободных мест '{}'", clientHandler.getAuthor().getName(), maximumThreads - clientHandlerList.size());
            synchronized (lock) {
                if (!hasAvailableThreads) {
                    hasAvailableThreads = clientHandlerList.size() < maximumThreads;
                    if (hasAvailableThreads) {
                        lock.notify();
                    }
                }
            }
        } else {
            log.warn("Не получилось удалить клиента '{}'", clientHandler.getAuthor().getName());
        }
    }

    boolean isNameAvailable(String name) {
        return clientHandlerList.stream()
                                .filter(ClientHandler::isAuthorFetched)
                                .noneMatch(clientHandler -> clientHandler.getAuthor().getName().equalsIgnoreCase(name));
    }

    Participant[] getConnectedParticipants() {
        return clientHandlerList.stream()
                                .filter(ClientHandler::isAuthorFetched)
                                .map(ClientHandler::getAuthor)
                                .toArray(Participant[]::new);
    }

    private void shutdown() {
        log.info("shutdown server");
        isRunning = false;

        //если не использовать - 1 клиент успеет подключиться
        //если использовать - клиенты не подключататся, но вылетит java.util.concurrent.RejectedExecutionException
        fixedThreadPool.shutdown();

        while (!clientHandlerList.isEmpty()) {
            try {
                log.info("Сервер ждет закрытия клиентских сокетов");
                //noinspection BusyWait
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                log.error("", e);
            }
        }
        try {
            serverSocket.close();
        } catch (IOException e) {
            log.error("", e);
        }

        log.info("server is dead");
    }
}
