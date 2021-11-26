package com.galvanize.prodman.model;

import com.galvanize.prodman.exception.UnsupportedCurrencyException;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum Currency {
    USD, CAD, EUR, GBP;

    public static Currency parse(String text) {
        if (text.isEmpty()) throw new IllegalArgumentException("text");

        return Stream.of(values())
                .filter(value -> value.name().equalsIgnoreCase(text))
                .findFirst()
                .orElseThrow(() -> unsupportedCurrency(text));
    }

    private static RuntimeException unsupportedCurrency(String text) {
        final String choices = Stream.of(values()).map(Currency::name).collect(Collectors.joining(", "));
        final String message = String.format("Unsupported currency '%s'; valid currencies include: %s", text, choices);
        return new UnsupportedCurrencyException(message);
    }
}
