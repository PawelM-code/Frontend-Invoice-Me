package com.invoiceme.service;

import com.invoiceme.domain.CurrencyDto;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

import static com.invoiceme.config.InvoiceMeAddress.BACKEND_URL;
import static java.util.Optional.ofNullable;

@Service
public class CurrencyService {
    private static final String CURRENCY_ENDPOINT = BACKEND_URL + "/currency";
    private RestTemplate restTemplate = new RestTemplate();

    public List<CurrencyDto> getCurrencies() {
        CurrencyDto[] currenciesResponse = restTemplate.getForObject(CURRENCY_ENDPOINT, CurrencyDto[].class);
        return Arrays.asList(ofNullable(currenciesResponse).orElse(new CurrencyDto[0]));
    }

    public CurrencyDto saveCurrencyRate(String code, String date) {
        return restTemplate.getForObject(CURRENCY_ENDPOINT + "/A/" + code + "/" + date, CurrencyDto.class);
    }

    public void saveCurrencyRate(CurrencyDto currencyDto) {
        restTemplate.postForObject(CURRENCY_ENDPOINT + "/A/", currencyDto, CurrencyDto.class);
    }
}
