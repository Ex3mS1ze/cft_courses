package org.cft;

import lombok.extern.slf4j.Slf4j;
import org.cft.configuration.ServerConfiguration;
import org.cft.net.Server;

@Slf4j
public class ServerChatApp {
    public static void main(String[] args) {
        Server server = new Server(new ServerConfiguration(null));
        server.start();
    }
}