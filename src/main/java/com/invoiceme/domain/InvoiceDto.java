package com.invoiceme.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class InvoiceDto {
    private Long id;
    private String number;
    private String issueDate;
    private TaxpayerDto taxpayerDto;
    private String comments;
    private BigDecimal netTotal;
    private BigDecimal vatTotal;
    private BigDecimal grossTotal;
    private BigDecimal currencyGrossTotal;
    private InvoiceCurrency invoiceCurrency;
    private String dateOfPayment;
}
