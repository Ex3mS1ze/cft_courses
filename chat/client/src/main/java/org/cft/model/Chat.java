package org.cft.model;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.cft.common.dto.Participant;
import org.cft.common.dto.TextMessage;
import org.cft.common.net.ServerPacketVisitor;
import org.cft.common.net.client.ClientMessagePacket;
import org.cft.common.net.client.SubmitNamePacket;
import org.cft.common.net.server.ServerPacket;
import org.cft.common.util.InetValidator;
import org.cft.net.ChatSocketHandler;
import org.cft.net.ServerPacketVisitorImpl;
import org.cft.view.ChatView;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Slf4j
public class Chat {
    @Getter
    private final ChatViewNotifier viewNotifier;
    @Getter
    private final ChatSocketHandler socketHandler;

    @Getter
    private final Participant author = new Participant();
    private Future<?> socketHandlerTask;

    private final ServerPacketVisitor packetVisitor = new ServerPacketVisitorImpl(this);

    public Chat() {
        this.viewNotifier = new ChatViewNotifier();
        this.socketHandler = new ChatSocketHandler(this);
    }

    public void start() {
        viewNotifier.fetchConnectionSettings();
        socketHandlerTask = Executors.newSingleThreadExecutor().submit(socketHandler);
        log.info("Запущен новый поток для прослушивания сокета");
        viewNotifier.fetchUsername();
    }

    public void handlePacket(ServerPacket packet) {
        packet.accept(packetVisitor);
    }

    public void setConnectionSettings(String host, String port) {
        if (!NumberUtils.isParsable(port) || !InetValidator.validatePort(Integer.parseInt(port)) ||
            !InetValidator.validateHost(host)) {
            log.info("Невалидный порт или хост");
            viewNotifier.fetchConnectionSettings();
            return;
        }

        if (!socketHandler.connect(host, Integer.parseInt(port))) {
            viewNotifier.fetchConnectionSettings();
        }
    }

    public void handleReconnect() {
        viewNotifier.notifyDisconnected();

        log.info("Попытка восстановить соединение");
        socketHandler.reconnect();
        if (socketHandler.isClosed()) {
            if (author.getName() == null) {
                viewNotifier.fetchUsername();
            } else {
                confirmName(author.getName());
            }
        } else {
            exit();
        }
    }

    public void confirmName(String name) {
        name = StringUtils.normalizeSpace(name);
        if (StringUtils.isBlank(name) || !StringUtils.isAlphanumericSpace(name)) {
            log.info("Имя содержит недопустимые символы или пустое");
            viewNotifier.fetchUsername();
            return;
        }

        socketHandler.sendPacket(new SubmitNamePacket(name));
    }

    public void attachView(ChatView chatView) {
        viewNotifier.attachView(chatView);
    }

    public void sendMessage(String message) {
        if (author.getName() == null) {
            log.info("Пользователь не указал имя");
            return;
        }

        if (StringUtils.isBlank(message)) {
            log.info("Пустое сообщение не отправлено");
            return;
        }

        socketHandler.sendPacket(new ClientMessagePacket(new TextMessage(author, message)));
    }

    public void exit() {
        socketHandler.setListening(false);
        socketHandler.setReconnectEnabled(false);

        while (!socketHandlerTask.isDone()) {
            try {
                log.info("Ожидание закрытие ресурсов");
                //noinspection BusyWait
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                log.error("", e);
            }
        }

        System.exit(0);
    }
}
