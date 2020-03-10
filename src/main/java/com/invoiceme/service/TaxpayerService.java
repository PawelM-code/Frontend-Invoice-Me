package com.invoiceme.service;

import com.invoiceme.domain.TaxpayerDto;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

import static java.util.Optional.ofNullable;

@Service
public class TaxpayerService {
    private static final String TAXPAYER_ENDPOINT = "https://immense-hollows-30003.herokuapp.com/v1/taxpayers/";
    private RestTemplate restTemplate = new RestTemplate();

    public TaxpayerDto getTaxpayer(Long nip, String date) {
        return restTemplate.getForObject(TAXPAYER_ENDPOINT + nip + "&" + date, TaxpayerDto.class);
    }

    public Long getTaxpayerId(Long nip) {
        return restTemplate.getForObject(TAXPAYER_ENDPOINT + "id/" + nip, Long.class);
    }

    public List<TaxpayerDto> getTaxpayers() {
        TaxpayerDto[] taxpayerDtoResponse = restTemplate.getForObject(TAXPAYER_ENDPOINT, TaxpayerDto[].class);
        return Arrays.asList(ofNullable(taxpayerDtoResponse).orElse(new TaxpayerDto[0]));
    }

    public void saveTaxpayer(TaxpayerDto taxpayerDto) {
        restTemplate.postForObject(TAXPAYER_ENDPOINT, taxpayerDto, TaxpayerDto.class);
    }
}
