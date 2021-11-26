package com.galvanize.prodman.service;

import com.galvanize.prodman.model.Currency;

import java.math.BigDecimal;
import java.util.Map;

public interface FxGateway {
    Map<Currency, BigDecimal> fetchQuotes();
}
