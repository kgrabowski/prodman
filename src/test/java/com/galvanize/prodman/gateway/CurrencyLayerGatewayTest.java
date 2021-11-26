package com.galvanize.prodman.gateway;

import com.galvanize.prodman.exception.FxGatewayException;
import com.galvanize.prodman.model.Currency;
import com.galvanize.prodman.model.FxResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CurrencyLayerGatewayTest {
    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private CurrencyLayerGateway gateway;

    @Test
    void returns_quotes_when_rest_call_succeeds() {
        final Map<String, BigDecimal> onlineQuotes = new HashMap<>();
        onlineQuotes.put("USDUSD", BigDecimal.valueOf(1.0));
        onlineQuotes.put("USDCAD", BigDecimal.valueOf(1.25));
        onlineQuotes.put("USDGBP", BigDecimal.valueOf(0.7102));
        onlineQuotes.put("USDEUR", BigDecimal.valueOf(0.8001));

        final FxResponse response = new FxResponse();
        response.setQuotes(onlineQuotes);

        when(restTemplate.getForEntity(anyString(), eq(FxResponse.class)))
                .thenReturn(ResponseEntity.ok(response));

        final Map<Currency, BigDecimal> actualQuotes = gateway.fetchQuotes();

        assertThat(actualQuotes).containsOnly(
                entry(Currency.USD, BigDecimal.valueOf(1.0)),
                entry(Currency.CAD, BigDecimal.valueOf(1.25)),
                entry(Currency.GBP, BigDecimal.valueOf(0.7102)),
                entry(Currency.EUR, BigDecimal.valueOf(0.8001))
        );
    }

    @Test
    void throws_exception_when_rest_call_fails() {
        when(restTemplate.getForEntity(anyString(), eq(FxResponse.class)))
                .thenReturn(ResponseEntity.internalServerError().build());

        final Throwable exception = catchThrowable(() -> gateway.fetchQuotes());

        assertThat(exception)
                .isInstanceOf(FxGatewayException.class)
                .hasMessageContaining("Couldn't fetch quotes from the remote server.");
    }

    @Test
    void throws_exception_when_error_returned_from_rest_call() {
        // CurrencyLayer returns 200 OK when e.g. rate limit is exceeded; we should handle this weird response
        when(restTemplate.getForEntity(anyString(), eq(FxResponse.class)))
                .thenReturn(ResponseEntity.ok(new FxResponse()));

        final Throwable exception = catchThrowable(() -> gateway.fetchQuotes());

        assertThat(exception)
                .isInstanceOf(FxGatewayException.class)
                .hasMessageContaining("Couldn't fetch quotes from the remote server.");
    }
}
