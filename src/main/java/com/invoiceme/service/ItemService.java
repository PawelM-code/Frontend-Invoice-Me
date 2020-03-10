package com.invoiceme.service;

import com.invoiceme.domain.ItemDto;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

import static java.util.Optional.ofNullable;

@Service
public class ItemService {
    private static final String ITEM_ENDPOINT = "https://immense-hollows-30003.herokuapp.com/v1/items";
    private RestTemplate restTemplate = new RestTemplate();

    public void createItem(ItemDto itemDto) {
        restTemplate.postForObject(ITEM_ENDPOINT, itemDto, ItemDto.class);
    }

    public List<ItemDto> getItemsByInvoiceId(Long id) {
        ItemDto[] itemDtoResponse = restTemplate.getForObject(ITEM_ENDPOINT + "/invoice/" + id, ItemDto[].class);
        return Arrays.asList(ofNullable(itemDtoResponse).orElse(new ItemDto[0]));

    }

}
