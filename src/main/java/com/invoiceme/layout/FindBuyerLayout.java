package com.invoiceme.layout;

import com.invoiceme.domain.TaxpayerDto;
import com.invoiceme.service.TaxpayerService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;
import lombok.Getter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Getter
public class FindBuyerLayout extends FormLayout {
    private TaxpayerService taxpayerService = new TaxpayerService();
    private TaxpayerDto taxpayerDto = new TaxpayerDto();
    private FormLayout findBuyerLayout = new FormLayout();
    private FormLayout completeBuyerDataLayout = new FormLayout();
    private DatePicker issueDate = setDatePicker("DATE OF ISSUE");
    private DatePicker paymentDate = setDatePicker("DATE OF PAYMENT");
    private TextField nip = new TextField("BUYER NIP");
    private TextField buyerName = new TextField("BUYER NAME");
    private TextField regon = new TextField("BUYER REGON");
    private TextField buyerWorkingAddress = new TextField("BUYER ADDRESS");
    private Button findTaxpayer = new Button("SEARCH");

    public FindBuyerLayout() {
        nip.setRequired(true);
        setFindTaxpayerButton();
        getFormLayoutFindBuyer();
        getFormLayoutCompleteBuyerData();
    }

    private void getFormLayoutCompleteBuyerData() {
        completeBuyerDataLayout.setSizeFull();
        completeBuyerDataLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("25em", 1),
                new FormLayout.ResponsiveStep("32em", 2),
                new FormLayout.ResponsiveStep("40em", 3));

        completeBuyerDataLayout.add(
                buyerName,
                regon,
                buyerWorkingAddress);
    }

    private void getFormLayoutFindBuyer() {
        findBuyerLayout.setSizeFull();
        findBuyerLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("25em", 1),
                new FormLayout.ResponsiveStep("25em", 2),
                new FormLayout.ResponsiveStep("25em", 3));
        findBuyerLayout.add(
                nip,
                issueDate,
                paymentDate,
                findTaxpayer);
        findBuyerLayout.setColspan(findTaxpayer, 1);

    }

    private void setFindTaxpayerButton() {
        findTaxpayer.setDisableOnClick(true);

        findTaxpayer.addClickListener(event ->
        {
            taxpayerDto = getTaxpayerDto(issueDate, nip);

            buyerName.setValue(taxpayerDto.getName());
            regon.setValue(taxpayerDto.getRegon().toString());
            buyerWorkingAddress.setValue(taxpayerDto.getWorkingAddress());
        });
    }

    private TaxpayerDto getTaxpayerDto(DatePicker datePickerCreateInvoice, TextField textFieldNip) {
        return taxpayerService.getTaxpayer(
                Long.parseLong(textFieldNip.getValue()),
                datePickerCreateInvoice.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    }

    private DatePicker setDatePicker(String label) {
        DatePicker datePickerCreateInvoice = new DatePicker(label);
        datePickerCreateInvoice.setValue(LocalDate.now());
        datePickerCreateInvoice.setLocale(Locale.GERMANY);
        return datePickerCreateInvoice;
    }
}
