package com.invoiceme.service;

import com.invoiceme.domain.OwnerDto;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

import static java.util.Optional.ofNullable;

public class MyCompanyService {
    private static final String OWNER_ENDPOINT = "https://immense-hollows-30003.herokuapp.com/v1/owner/";
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

    public void updateOwner(OwnerDto ownerDto) {
        restTemplate.put(OWNER_ENDPOINT, ownerDto, OwnerDto.class);
    }
}
