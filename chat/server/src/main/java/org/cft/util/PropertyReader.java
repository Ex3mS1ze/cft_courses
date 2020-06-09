package org.cft.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;

import java.io.File;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PropertyReader {
    private static final String PROPERTIES_EXTENSION = ".properties";

    public static Configuration fetchProperties(String propertiesName) {
        try {
            return new Configurations().properties(new File(propertiesName + PROPERTIES_EXTENSION));
        } catch (ConfigurationException e) {
            log.error("Не удалось прочитать файл '{}'", propertiesName);
        }

        return null;
    }
}
