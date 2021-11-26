package com.galvanize.prodman.model;

import com.galvanize.prodman.exception.UnsupportedCurrencyException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CurrencyTest {
    @ParameterizedTest
    @MethodSource("validCurrencies")
    void parse_valid_currency(String input, Currency expectedCurrency) {
        final Currency actualCurrency = Currency.parse(input);
        assertThat(actualCurrency).isEqualTo(expectedCurrency);
    }

    @Test
    void parse_empty_text() {
        assertThatThrownBy(() -> Currency.parse(""))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void parse_unsupported_currency() {
        assertThatThrownBy(() -> Currency.parse("CHF"))
                .isInstanceOf(UnsupportedCurrencyException.class)
                .hasMessage("Unsupported currency 'CHF'; valid currencies include: USD, CAD, EUR, GBP");
    }

    private static Stream<Arguments> validCurrencies() {
        return Stream.of(
                Arguments.of("cad", Currency.CAD),
                Arguments.of("Cad", Currency.CAD),
                Arguments.of("USD", Currency.USD),
                Arguments.of("gBP", Currency.GBP),
                Arguments.of("EuR", Currency.EUR)
        );
    }
}
