package com.galvanize.prodman.service;

import com.galvanize.prodman.model.Currency;

import java.math.BigDecimal;

public interface FxGateway {
    BigDecimal fetchQuote(Currency currency);
}
