package com.invoiceme.service;

import com.invoiceme.domain.ProductDto;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

import static java.util.Optional.ofNullable;

@Service
public class ProductService {
    private static final String PRODUCTS_ENDPOINT = "https://immense-hollows-30003.herokuapp.com/v1/products";
    private RestTemplate restTemplate = new RestTemplate();

    public void createProduct(String description, int vat) {
        ProductDto productDto = new ProductDto();
        productDto.setDescription(description);
        productDto.setVat(vat);

        restTemplate.postForObject(PRODUCTS_ENDPOINT, productDto, ProductDto.class);
    }

    public void updateProduct(ProductDto productDto) {
        restTemplate.put(PRODUCTS_ENDPOINT, productDto, ProductDto.class);
    }

    public void deleteProduct(Long id) {
        restTemplate.delete(PRODUCTS_ENDPOINT + "/" + id, ProductDto.class);
    }

    public List<ProductDto> getProducts() {
        ProductDto[] productsResponse = restTemplate.getForObject(PRODUCTS_ENDPOINT, ProductDto[].class);
        return Arrays.asList(ofNullable(productsResponse).orElse(new ProductDto[0]));
    }
}
