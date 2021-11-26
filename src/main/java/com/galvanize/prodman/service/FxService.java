package com.galvanize.prodman.service;

import com.galvanize.prodman.exception.MissingConversionRateException;
import com.galvanize.prodman.model.Currency;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

@Service
public class FxService {
    private final FxGateway fxGateway;

    public FxService(FxGateway fxGateway) {
        this.fxGateway = fxGateway;
    }

    public BigDecimal convert(BigDecimal amountUSD, Currency targetCurrency) {
        final Map<Currency, BigDecimal> quotes = fxGateway.fetchQuotes();
        final BigDecimal quote = quotes.get(targetCurrency);
        if (quote == null) {
            throw new MissingConversionRateException("Couldn't find conversion rate from USD to " + targetCurrency);
        }
        final BigDecimal result = amountUSD.multiply(quote);
        return result.setScale(2, RoundingMode.HALF_UP);
    }
}
