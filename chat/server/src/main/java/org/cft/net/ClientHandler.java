package org.cft.net;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.cft.common.dto.Participant;
import org.cft.common.net.ClientPacketVisitor;
import org.cft.common.net.SocketHandler;
import org.cft.common.net.client.ClientPacket;
import org.cft.common.net.server.*;
import org.cft.exception.InitializeFailedException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;

@Slf4j
public class ClientHandler extends SocketHandler<ClientPacket, ServerPacket> implements Runnable {
    private static final int MAX_QUANTITY_TIMEOUTS_IN_A_ROW = 1;

    @Getter
    private final Participant author = new Participant();
    @Getter
    private final Server server;

    @Getter
    private volatile boolean authorFetched = false;
    @Setter
    private volatile boolean listening = true;

    private final Object lock = new Object();

    private final ClientPacketVisitor visitor = new ClientPacketVisitorImpl(this);

    ClientHandler(Socket clientSocket, Server server) {
        try {
            this.objectWriter = new ObjectOutputStream(clientSocket.getOutputStream());
            this.objectReader = new ObjectInputStream(clientSocket.getInputStream());
        } catch (IOException e) {
            log.error("", e);
            throw new InitializeFailedException();
        }

        this.socket = clientSocket;
        this.server = server;
    }

    @Override
    public void run() {
        log.info("Поток '{}' запущен", Thread.currentThread().getName());

        int timeoutCounter = 0;
        try {
            ClientPacket receivedObject;
            while (listening && server.isRunning()) {
                try {
                    receivedObject = readPacket();
                    timeoutCounter = 0;
                    log.info("Получен объект");
                    handlePacket(receivedObject);
                } catch (SocketTimeoutException e) {
                    timeoutCounter++;
                    log.trace("Клиент давно не связывался");

                    if (timeoutCounter > MAX_QUANTITY_TIMEOUTS_IN_A_ROW) {
                        log.warn("Время ожидания ответа истекло");
                        listening = false;
                        break;
                    } else {
                        sendPacket(new PingPacket());
                    }
                }
            }
        } catch (IOException e) {
            log.error("Соединение разорвано", e);
        } finally {
            shutdown();
        }
    }

    void fetchAuthor(String name) {
        boolean nameAvailable = server.isNameAvailable(name);
        if (nameAvailable) {
            log.info("Имя '{}' доступно", name);
            author.setName(name);

            authorFetched = true;
            sendPacket(new NameStatusPacket(name));
            sendPacket(new ParticipantsPacket(server.getConnectedParticipants()));

            log.info("Рассылается оповещение о подключении нового клиента '{}'", author.getName());
            server.sendToAll(new ParticipantStatusPacket(author, true));
        } else {
            log.info("'{}' имя уже используется", name);
            sendPacket(new NameStatusPacket(null));
        }
    }

    private void handlePacket(ClientPacket packet) {
        log.info("Обработка пакета '{}'", packet.getClass().getSimpleName());
        packet.accept(visitor);
    }

    @Override
    public void sendPacket(ServerPacket packet) {
        synchronized (lock) {
            try {
                objectWriter.writeObject(packet);
                objectWriter.flush();
                log.info("Отправлен пакет '{}' to '{}'", packet.getClass().getSimpleName(), author.getName());
            } catch (IOException e) {
                log.error("Не удалось отправить пакет", e);
                listening = false;
            }
        }
    }

    private void stopListening() {
        listening = false;
        server.removeClient(this);

        if (server.isRunning() && authorFetched) {
            log.info("Рассылается оповещение об отключении клиента '{}'", author.getName());
            server.sendToAll(new ParticipantStatusPacket(author, false));
        }
    }

    private void shutdown() {
        stopListening();
        closeResources();
        log.info("'{}' stopped", author.getName());
    }
}
