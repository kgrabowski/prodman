package com.galvanize.prodman.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Map;

@Getter
@Setter
public class FxResponse {

    private Map<String, BigDecimal> quotes;

    @Override
    public String toString() {
        return quotes.toString();
    }
}
