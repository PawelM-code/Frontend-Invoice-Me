package com.invoiceme.views;

import com.invoiceme.domain.CurrencyDto;
import com.invoiceme.domain.InvoiceCurrency;
import com.invoiceme.layout.MainLayout;
import com.invoiceme.service.CurrencyService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.router.Route;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Locale;

@Route(value = "currency", layout = MainLayout.class)
public class CurrenciesView extends VerticalLayout {
    private CurrencyService currencyService = new CurrencyService();
    private CurrencyDto currencyDto;
    private Grid<CurrencyDto> currencyGrid = new Grid<>();
    private Select<InvoiceCurrency> selectCurrency = new Select<>();
    private DatePicker currencyRateDate = new DatePicker("Date");
    private Label rate = new Label();
    private Button getRate;
    private Button saveRate;
    private FormLayout fLGetRate = new FormLayout();


    public CurrenciesView() {
        getCurrencyGrid();
        refreshCurrencyGrid(currencyGrid);
        getCurrency();
        getCurrencyRateDate();
        getRateLabel();
        getRateButton();
        getSaveRateButton();
        getFormLayoutCurrencyRateFields();

        add(fLGetRate, currencyGrid);
    }

    private void getRateLabel() {
        rate.setMaxWidth("5em");
    }

    private void getFormLayoutCurrencyRateFields() {
        fLGetRate.setResponsiveSteps(
                new FormLayout.ResponsiveStep("35em", 1),
                new FormLayout.ResponsiveStep("45em", 2),
                new FormLayout.ResponsiveStep("25em", 3),
                new FormLayout.ResponsiveStep("5em", 4),
                new FormLayout.ResponsiveStep("25em", 5));
        fLGetRate.setMaxWidth("45em");
        fLGetRate.add(selectCurrency, currencyRateDate, getRate, rate, saveRate);
    }

    private void getSaveRateButton() {
        saveRate = new Button("Save", click -> {
            currencyService.saveCurrencyRate(currencyDto);
            refreshCurrencyGrid(currencyGrid);
        });
    }

    private void getRateButton() {
        getRate = new Button("Get Rate", clickEvent -> {
            currencyDto = currencyService.saveCurrencyRate(
                    selectCurrency.getValue().toString(),
                    currencyRateDate.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            rate.setText(currencyDto.getRates()[0].getMid().toString());
        });
    }

    private void getCurrencyRateDate() {
        currencyRateDate.setValue(LocalDate.now());
        currencyRateDate.setLocale(Locale.GERMANY);
    }

    private void getCurrency() {
        selectCurrency.setItems(InvoiceCurrency.values());
        selectCurrency.setLabel("Currency code");
    }

    private void getCurrencyGrid() {
        currencyGrid.addColumn(CurrencyDto::getId)
                .setHeader("ID")
                .setSortable(true)
                .setFlexGrow(0);
        currencyGrid.addColumn(currencyDto -> Arrays
                .stream(currencyDto.getRates())
                .iterator().next().getEffectiveDate())
                .setHeader("Date")
                .setSortable(true);
        currencyGrid.addColumn(CurrencyDto::getCode)
                .setHeader("Code")
                .setSortable(true);
        currencyGrid.addColumn(CurrencyDto::getCurrency)
                .setHeader("Currency")
                .setSortable(true);
        currencyGrid.addColumn(currencyDto -> Arrays
                .stream(currencyDto.getRates())
                .iterator().next().getMid())
                .setHeader("Rate")
                .setSortable(true);
    }

    private void refreshCurrencyGrid(Grid<CurrencyDto> grid) {
        grid.setItems(currencyService.getCurrencies());
    }
}
