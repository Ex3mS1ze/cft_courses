package org.cft.configuration;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.ex.ConversionException;
import org.cft.exception.InitializeFailedException;
import org.cft.util.PropertyReader;
import org.jetbrains.annotations.PropertyKey;

@Slf4j
public class ServerConfiguration {
    private static final String DEFAULT_PROPERTY_FILE_NAME = "server";

    private static final int DEFAULT_PORT = 6666;
    private static final int DEFAULT_NAX_CLIENTS = 50;
    private static final String DEFAULT_INET_ADDRESS = "127.0.0.1";

    @PropertyKey(resourceBundle = DEFAULT_PROPERTY_FILE_NAME)
    private static final String PORT_PROPERTY_KEY = "server.port";
    @PropertyKey(resourceBundle = DEFAULT_PROPERTY_FILE_NAME)
    private static final String MAX_CLIENTS_PROPERTY_KEY = "server.maxClients";
    @PropertyKey(resourceBundle = DEFAULT_PROPERTY_FILE_NAME)
    private static final String INET_ADDRESS_PROPERTY_KEY = "server.inetAddress";

    private final Configuration configuration;

    /**
     * @param propertyFileName имя properties файла или {@code null}, чтобы использовать имя файла по умолчанию {@value DEFAULT_PROPERTY_FILE_NAME}
     */
    public ServerConfiguration(String propertyFileName) {
        propertyFileName = propertyFileName == null ? DEFAULT_PROPERTY_FILE_NAME : propertyFileName;

        configuration = PropertyReader.fetchProperties(propertyFileName);
        if (configuration == null || !configuration.containsKey(PORT_PROPERTY_KEY) ||
            !configuration.containsKey(MAX_CLIENTS_PROPERTY_KEY) || !configuration.containsKey(INET_ADDRESS_PROPERTY_KEY)) {
            log.error("Нет обязательных свойств в '{}.properties'", propertyFileName);
            throw new InitializeFailedException();
        }

    }

    public int getPort() {
        try {
            return configuration.getInt(PORT_PROPERTY_KEY);
        } catch (ConversionException e) {
            log.error("Значение ключа '{}' не число", PORT_PROPERTY_KEY, e);
        }

        log.warn("Используется порт по умолчанию '{}'", DEFAULT_PORT);
        return DEFAULT_PORT;
    }

    public int getMaxClients() {
        try {
            return configuration.getInt(MAX_CLIENTS_PROPERTY_KEY);
        } catch (ConversionException e) {
            log.error("Значение ключа '{}' не число", MAX_CLIENTS_PROPERTY_KEY, e);
        }

        log.warn("Используется максимальное число клиентов по умолчанию '{}'", DEFAULT_NAX_CLIENTS);
        return DEFAULT_NAX_CLIENTS;
    }

    public String getInetAddress() {
        try {
            return configuration.getString(INET_ADDRESS_PROPERTY_KEY);
        } catch (ConversionException e) {
            log.error("", e);
        }

        log.warn("Используется адрес по умолчанию '{}'", DEFAULT_INET_ADDRESS);
        return DEFAULT_INET_ADDRESS;
    }
}
