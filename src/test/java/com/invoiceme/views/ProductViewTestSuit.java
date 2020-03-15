package com.invoiceme.views;

import com.invoiceme.service.ProductService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProductViewTestSuit {
    @Autowired
    private ProductService productService;

    @Test
    public void testAddProduct() {
        ProductView productView = new ProductView();
        int countStart = productService.getProducts().size();
        productView.getDescription().setValue("Test product");
        productView.getVat().setValue(23);
        productView.getButtonCreateProduct().click();
        long countEnd = productService.getProducts().size();
        Assert.assertTrue(countStart < countEnd);
    }
}
