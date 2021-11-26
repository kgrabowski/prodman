package com.galvanize.prodman.service;

import com.galvanize.prodman.model.Currency;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class FxService {
    private final FxGateway fxGateway;

    public FxService(FxGateway fxGateway) {
        this.fxGateway = fxGateway;
    }

    public BigDecimal convert(BigDecimal amountUSD, Currency targetCurrency) {
        if (targetCurrency == Currency.USD) {
            return amountUSD;
        } else {
            final BigDecimal quote = fxGateway.fetchQuote(targetCurrency);
            final BigDecimal result = amountUSD.multiply(quote);
            return result.setScale(2, RoundingMode.HALF_UP);
        }
    }
}
