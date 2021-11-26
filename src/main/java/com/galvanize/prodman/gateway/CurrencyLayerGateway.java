package com.galvanize.prodman.gateway;

import com.galvanize.prodman.model.Currency;
import com.galvanize.prodman.model.FxResponse;
import com.galvanize.prodman.service.FxGateway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CurrencyLayerGateway implements FxGateway {
    private static final String SUPPORTED_CURRENCIES = Stream.of(Currency.values())
            .map(Currency::name)
            .collect(Collectors.joining(","));

    @Value("${fx.api.url}")
    private String fxApiUrl;

    @Value("${fx.api.key}")
    private String fxApiKey;

    private final RestTemplate restTemplate;

    public CurrencyLayerGateway(final RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    @Cacheable("quotes")
    public Map<Currency, BigDecimal> fetchQuotes() {
        final Map<Currency, BigDecimal> result = new HashMap<>();
        final FxResponse response = requestQuotes();
        response.getQuotes().forEach((key, value) -> {
            final String currencyText = key.substring(3);
            final Currency currency = Currency.parse(currencyText);
            result.put(currency, value);
        });
        return result;
    }

    private FxResponse requestQuotes() {
        final String endPoint = String.format(
                "%s?access_key=%s&currencies=%s&format=1",
                fxApiUrl,
                fxApiKey,
                SUPPORTED_CURRENCIES);
        return restTemplate.getForObject(endPoint, FxResponse.class);
    }
}
