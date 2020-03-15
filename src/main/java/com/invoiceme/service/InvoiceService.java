package com.invoiceme.service;

import com.invoiceme.domain.InvoiceDto;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

import static com.invoiceme.config.InvoiceMeAddress.BACKEND_URL;
import static java.util.Optional.ofNullable;

@Service
public class InvoiceService {
    private static final String INVOICE_ENDPOINT = BACKEND_URL + "/invoices";
    private RestTemplate restTemplate = new RestTemplate();

    public void createInvoice(InvoiceDto invoiceDto) {
        restTemplate.postForObject(INVOICE_ENDPOINT, invoiceDto, InvoiceDto.class);
    }

    public void updateInvoice(InvoiceDto invoiceDto) {
        restTemplate.put(INVOICE_ENDPOINT, invoiceDto, InvoiceDto.class);
    }

    public List<InvoiceDto> getInvoices() {
        InvoiceDto[] invoicesResponse = restTemplate.getForObject(INVOICE_ENDPOINT, InvoiceDto[].class);
        return Arrays.asList(ofNullable(invoicesResponse).orElse(new InvoiceDto[0]));
    }

    public Long getInvoiceId(String number) {
        return restTemplate.getForObject(INVOICE_ENDPOINT + "/id?number=" + number, Long.class);
    }

    public void deleteInvoice(Long id) {
        restTemplate.delete(INVOICE_ENDPOINT + "/" + id, InvoiceDto.class);
    }
}
