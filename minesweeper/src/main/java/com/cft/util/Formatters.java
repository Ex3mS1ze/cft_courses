package com.cft.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.swing.text.NumberFormatter;
import java.text.NumberFormat;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Formatters {
    public static NumberFormatter createIntegerFormatter(int min, int max) {
        NumberFormatter formatter = new NumberFormatter(NumberFormat.getInstance());

        formatter.setValueClass(Integer.class);
        formatter.setAllowsInvalid(false);
        formatter.setCommitsOnValidEdit(true);
        formatter.setMinimum(min);
        formatter.setMaximum(max);

        return formatter;
    }
}
