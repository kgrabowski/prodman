package com.galvanize.prodman.service;

import com.galvanize.prodman.exception.MissingConversionRateException;
import com.galvanize.prodman.model.Currency;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FxServiceTest {
    @Mock
    private FxGateway fxGateway;

    @InjectMocks
    private FxService fxService;

    @Test
    void converting_USD_to_USD_returns_if_configured_correctly() {
        final Map<Currency, BigDecimal> quotes = new HashMap<>();
        quotes.put(Currency.USD, BigDecimal.valueOf(1.0));

        when(fxGateway.fetchQuotes()).thenReturn(quotes);

        final BigDecimal originalAmount = BigDecimal.valueOf(12.34);
        final BigDecimal convertedAmount = fxService.convert(originalAmount, Currency.USD);

        assertThat(convertedAmount).isEqualTo(originalAmount);
    }

    @Test
    void converting_USD_to_other_currency_returns_correctly_converted_amount() {
        final Map<Currency, BigDecimal> quotes = new HashMap<>();
        quotes.put(Currency.CAD, BigDecimal.valueOf(1.5));

        when(fxGateway.fetchQuotes()).thenReturn(quotes);

        final BigDecimal originalAmount = BigDecimal.valueOf(10.50);
        final BigDecimal convertedAmount = fxService.convert(originalAmount, Currency.CAD);

        assertThat(convertedAmount).isEqualTo(BigDecimal.valueOf(15.75));
    }

    @Test
    void converting_USD_to_other_currency_rounds_result_to_two_decimal_places() {
        final Map<Currency, BigDecimal> quotes = new HashMap<>();
        quotes.put(Currency.GBP, BigDecimal.valueOf(0.7005));

        when(fxGateway.fetchQuotes()).thenReturn(quotes);

        final BigDecimal originalAmount = BigDecimal.valueOf(5.25);
        final BigDecimal convertedAmount = fxService.convert(originalAmount, Currency.GBP);

        assertThat(convertedAmount).isEqualTo(BigDecimal.valueOf(3.68));
    }

    @Test
    void throws_exception_if_missing_required_currency_conversion() {
        final Map<Currency, BigDecimal> quotes = new HashMap<>();
        quotes.put(Currency.USD, null);

        when(fxGateway.fetchQuotes()).thenReturn(quotes);

        final BigDecimal originalAmount = BigDecimal.valueOf(1.0);
        final Throwable exception = catchThrowable(() -> fxService.convert(originalAmount, Currency.EUR));

        assertThat(exception)
                .isInstanceOf(MissingConversionRateException.class)
                .hasMessage("Couldn't find conversion rate from USD to EUR");
    }
}
