package com.galvanize.prodman.service;

import com.galvanize.prodman.model.Currency;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FxServiceTest {
    @Mock
    private FxGateway fxGateway;

    @InjectMocks
    private FxService fxService;

    @Test
    void converting_USD_to_USD_returns_input_amount() {
        final BigDecimal originalAmount = BigDecimal.valueOf(12.34);
        final BigDecimal convertedAmount = fxService.convert(originalAmount, Currency.USD);

        assertThat(convertedAmount).isEqualTo(originalAmount);
    }

    @Test
    void converting_USD_to_other_currency_returns_correctly_converted_amount() {
        when(fxGateway.fetchQuote(Currency.CAD)).thenReturn(BigDecimal.valueOf(1.5));

        final BigDecimal originalAmount = BigDecimal.valueOf(10.50);
        final BigDecimal convertedAmount = fxService.convert(originalAmount, Currency.CAD);

        assertThat(convertedAmount).isEqualTo(BigDecimal.valueOf(15.75));
    }

    @Test
    void converting_USD_to_other_currency_rounds_result_to_two_decimal_places() {
        when(fxGateway.fetchQuote(Currency.GBP)).thenReturn(BigDecimal.valueOf(0.7005));

        final BigDecimal originalAmount = BigDecimal.valueOf(5.25);
        final BigDecimal convertedAmount = fxService.convert(originalAmount, Currency.GBP);

        assertThat(convertedAmount).isEqualTo(BigDecimal.valueOf(3.68));
    }
}
