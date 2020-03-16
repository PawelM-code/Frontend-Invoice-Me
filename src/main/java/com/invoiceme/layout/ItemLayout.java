package com.invoiceme.layout;

import com.invoiceme.domain.ProductDto;
import com.invoiceme.service.ProductService;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import lombok.Getter;

import java.util.List;

@Getter
public class ItemLayout extends FormLayout {
    private ProductService productService = new ProductService();
    private Select<ProductDto> selectProducts = new Select<>();
    private NumberField netPrice = new NumberField("NET PRICE");
    private IntegerField quantity = new IntegerField("QUANTITY");

    public ItemLayout() {
        selectProducts.setRequiredIndicatorVisible(true);
        netPrice.setRequiredIndicatorVisible(true);
        netPrice.setPrefixComponent(new Label("PLN"));
        netPrice.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT);
        quantity.setRequiredIndicatorVisible(true);
        initLayout();
        setResponsiveSteps(
                new FormLayout.ResponsiveStep("25em", 1),
                new FormLayout.ResponsiveStep("32em", 2),
                new FormLayout.ResponsiveStep("40em", 3));
        setSizeFull();
    }

    private void initLayout() {
        selectProducts.setLabel("PRODUCT");
        List<ProductDto> productDtoList = productService.getProducts();

        selectProducts.setItemLabelGenerator(ProductDto::getDescription);
        selectProducts.setItems(productDtoList);

        add(selectProducts, netPrice, quantity);
    }
}
