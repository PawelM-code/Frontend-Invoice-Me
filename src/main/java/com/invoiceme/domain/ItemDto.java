package com.invoiceme.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {
    private Long id;
    private ProductDto productDto;
    private InvoiceDto invoiceDto;
    private BigDecimal netPrice;
    private BigDecimal vat;
    private BigDecimal grossPrice;
    private int quantity;
    private BigDecimal value;
}
