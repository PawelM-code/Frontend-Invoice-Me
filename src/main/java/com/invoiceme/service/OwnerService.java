package com.invoiceme.service;

import com.invoiceme.domain.OwnerDto;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

import static com.invoiceme.config.InvoiceMeAddress.BACKEND_URL;
import static java.util.Optional.ofNullable;

public class OwnerService {
    private static final String OWNER_ENDPOINT = BACKEND_URL + "/owner/";
    private RestTemplate restTemplate = new RestTemplate();

    public OwnerDto getOwner(Long id) {
        return restTemplate.getForObject(OWNER_ENDPOINT + id, OwnerDto.class);
    }

    public List<OwnerDto> getOwners() {
        OwnerDto[] ownersResponse = restTemplate.getForObject(OWNER_ENDPOINT, OwnerDto[].class);
        return Arrays.asList(ofNullable(ownersResponse).orElse(new OwnerDto[0]));
    }

    public void saveOwner(OwnerDto ownerDto) {
        restTemplate.postForObject(OWNER_ENDPOINT, ownerDto, OwnerDto.class);
    }
}
