package org.cft.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.validator.routines.UrlValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class HtmlWrapper {
    private static final UrlValidator URL_VALIDATOR = new UrlValidator(UrlValidator.ALLOW_ALL_SCHEMES);
    private static final Pattern HOST_PATTERN = Pattern.compile("/{2}([^/\\s]*)");

    public static List<String> wrapUrl(String string) {
        List<String> result = new ArrayList<>();

        for (String line : string.split("\r?\n")) {
            StringBuilder builder = new StringBuilder();
            for (String word : line.split(" ")) {
                if (URL_VALIDATOR.isValid(word)) {
                    Matcher matcher = HOST_PATTERN.matcher(word);
                    if (matcher.find()) {
                        builder.append("<a href=\"").append(word).append("\">").append(matcher.group(1)).append("</a>").append(" ");
                        continue;
                    }
                }

                builder.append(word).append(" ");
            }
            result.add(builder.toString().trim());
        }

        return result;
    }
}
