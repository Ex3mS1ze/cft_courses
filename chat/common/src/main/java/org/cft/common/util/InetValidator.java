package org.cft.common.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class InetValidator {
    public static boolean validatePort(int port) {
        return port >= 0 && port <= 65535;
    }

    public static boolean validateHost(String host) {
        try {
            //noinspection ResultOfMethodCallIgnored
            InetAddress.getByName(host);
        } catch (UnknownHostException e) {
            log.error("host '{}' не может быть определен", host, e);
            return false;
        }

        return true;
    }
}
