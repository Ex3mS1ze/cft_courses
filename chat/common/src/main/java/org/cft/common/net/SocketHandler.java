package org.cft.common.net;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 *
 * @param <R> класс читаемых объектов.
 * @param <S> класс отправляемых отбъектов.
 */
@Slf4j
public class SocketHandler<R, S> {
    protected Socket socket;

    protected ObjectOutputStream objectWriter;
    protected ObjectInputStream objectReader;

    protected R readPacket() throws IOException {
        R packet = null;
        Object receivedObject;
        try {
            receivedObject = objectReader.readObject();
            //noinspection unchecked
            packet = (R) receivedObject;
            log.info("Пакет получен '{}'", packet.getClass().getSimpleName());
        } catch (ClassNotFoundException e) {
            log.error("Не получилось прочитать объект", e);
        } catch (ClassCastException e) {
            log.warn("Получен пакет, который не может быть приведен к Packet");
        }

        return packet;
    }

    public void sendPacket(S packet) {
        try {
            objectWriter.writeObject(packet);
            objectWriter.flush();
            log.info("Пакет '{}' отправлен", packet.getClass().getSimpleName());
        } catch (IOException e) {
            log.error("Не удалось отправить пакет", e);
        }
    }

    protected void closeResources() {
        try {
            if (objectWriter != null) {
                objectWriter.close();
            }
        } catch (IOException e) {
            log.error("", e);
        }

        try {
            if (objectReader != null) {
                objectReader.close();
            }
        } catch (IOException e) {
            log.error("", e);
        }

        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            log.error("", e);
        }

        log.info("Ресуры закрыты");
    }
}
