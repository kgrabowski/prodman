package com.galvanize.prodman.service;

import com.galvanize.prodman.model.Currency;
import com.galvanize.prodman.model.FxResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class FxService {
    private static final String SUPPORTED_CURRENCIES = Stream.of(Currency.values())
            .map(Currency::name)
            .collect(Collectors.joining(","));

    @Value("${fx.api.url}")
    private String fxApiUrl;

    @Value("${fx.api.key}")
    private String fxApiKey;

    private final RestTemplate restTemplate;

    public FxService(final RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public FxResponse getQuotes() {
        final String endPoint = String.format(
                "%s?access_key=%s&currencies=%s&format=1",
                fxApiUrl,
                fxApiKey,
                SUPPORTED_CURRENCIES);
        return restTemplate.getForObject(endPoint, FxResponse.class);
    }
}
