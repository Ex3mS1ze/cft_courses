package org.cft.net;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.cft.common.net.SocketHandler;
import org.cft.common.net.client.ClientPacket;
import org.cft.common.net.client.ShutdownPacket;
import org.cft.common.net.server.ServerPacket;
import org.cft.model.Chat;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

@Slf4j
@RequiredArgsConstructor
public class ChatSocketHandler extends SocketHandler<ServerPacket, ClientPacket> implements Runnable {
    private static final long RECONNECT_TIME = 5000L;

    @Getter
    @Setter
    private volatile boolean listening = true;
    @Setter
    private volatile boolean reconnectEnabled = true;
    @NonNull
    private final Chat chat;

    public boolean connect(String host, int port) {
        try {
            this.socket = new Socket(host, port);
            this.objectWriter = new ObjectOutputStream(socket.getOutputStream());
            this.objectReader = new ObjectInputStream(socket.getInputStream());
            log.info("Сокет, input, output созданы");
        } catch (IOException e) {
            log.error("Не получилось подключиться", e);
            return false;
        }

        return true;
    }

    @Override
    public void run() {
        log.info("listen started");
        ServerPacket packet = null;
        while (listening) {
            try {
                packet = readPacket();
            } catch (IOException e) {
                log.error("Не получилось связаться с сервером", e);
                chat.handleReconnect();
            }

            if (packet != null) {
                chat.handlePacket(packet);
            }
        }

        sendPacket(new ShutdownPacket());
        closeResources();
        log.info("Поток, слушающий сокет, завершен");
    }

    public void reconnect() {
        if (socket == null) {
            return;
        }

        while (reconnectEnabled) {
            closeResources();
            if (connect(socket.getInetAddress().getHostName(), socket.getPort())) {
                log.info("Соединение восстановлено");
                break;
            }

            try {
                log.info("Следующая попытка восстановить содинение через '{}'мс", RECONNECT_TIME);
                //noinspection BusyWait
                Thread.sleep(RECONNECT_TIME);
            } catch (InterruptedException e) {
                log.error("", e);
                break;
            }
        }
    }

    public boolean isClosed() {
        return socket != null && !socket.isClosed();
    }
}
