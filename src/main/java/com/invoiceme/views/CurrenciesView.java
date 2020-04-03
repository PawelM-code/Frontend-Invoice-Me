package com.invoiceme.views;

import com.invoiceme.component.DialogWindow;
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

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Locale;

import static com.invoiceme.domain.InvoiceCurrency.*;

@Route(value = "currency", layout = MainLayout.class)
public class CurrenciesView extends VerticalLayout {
    private CurrencyService currencyService = new CurrencyService();
    private DialogWindow dialogWindow = new DialogWindow();
    private CurrencyDto currencyDto;
    private FormLayout findRateLayout = new FormLayout();
    private Grid<CurrencyDto> currencyGrid = new Grid<>();
    private Select<InvoiceCurrency> selectCurrency;
    private DatePicker currencyRateDate;
    private Label rate;
    private Button getRate;
    private Button saveRate;


    public CurrenciesView() {
        addCurrencyGridColumns();
        refreshCurrencyGrid(currencyGrid);
        getCurrency();
        getCurrencyRateDate();
        getRateLabel();
        getRateButton();
        getSaveRateButton();
        getFindRateLayout();

        setSizeFull();
        add(findRateLayout, currencyGrid);
    }

    private void getRateLabel() {
        rate = new Label();
        rate.setMaxWidth("5em");
        rate.setTitle("Rate");
    }

    private void getFindRateLayout() {
        findRateLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("35em", 1),
                new FormLayout.ResponsiveStep("45em", 2),
                new FormLayout.ResponsiveStep("25em", 3),
                new FormLayout.ResponsiveStep("5em", 4),
                new FormLayout.ResponsiveStep("25em", 5));
        findRateLayout.setMaxWidth("45em");
        findRateLayout.add(selectCurrency, currencyRateDate, getRate, rate, saveRate);
    }

    private void getSaveRateButton() {
        saveRate = new Button("SAVE", click -> {
            currencyService.saveCurrencyRate(currencyDto);
            refreshCurrencyGrid(currencyGrid);
        });
    }

    private void getRateButton() {
        getRate = new Button("GET RATE", clickEvent -> {
            String currencyDate = currencyRateDate.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            if (!isWeekend(currencyDate)) {
                if (!selectCurrency.isEmpty()) {
                    currencyDto = currencyService.saveCurrencyRate(
                            selectCurrency.getValue().toString(),
                            currencyDate);
                    rate.setText(currencyDto.getRates()[0].getMid().toString());
                } else {
                    dialogWindow.setDialog("<p><b>Please complete required fields.</b></p>");
                }
            } else {
                dialogWindow.setDialog("<p><b>Please indicate a workday.</b></p>");
            }

        });
    }

    private boolean isWeekend(String invoiceDate) {
        LocalDate invoiceLocalDate = LocalDate.parse(invoiceDate);
        return invoiceLocalDate.getDayOfWeek() == DayOfWeek.SATURDAY || invoiceLocalDate.getDayOfWeek() == DayOfWeek.SUNDAY;
    }

    private void getCurrencyRateDate() {
        currencyRateDate = new DatePicker("DATE");
        currencyRateDate.setValue(LocalDate.now());
        currencyRateDate.setLocale(Locale.GERMANY);
    }

    private void getCurrency() {
        selectCurrency = new Select<>();
        selectCurrency.setItems(EUR, USD, CHF);
        selectCurrency.setLabel("CURRENCY CODE");
    }

    private void addCurrencyGridColumns() {
        currencyGrid.addColumn(CurrencyDto::getId)
                .setHeader("ID")
                .setSortable(true)
                .setFlexGrow(0);
        currencyGrid.addColumn(currencyDto -> Arrays
                .stream(currencyDto.getRates())
                .iterator().next().getEffectiveDate())
                .setHeader("DATE")
                .setSortable(true);
        currencyGrid.addColumn(CurrencyDto::getCode)
                .setHeader("CODE")
                .setSortable(true);
        currencyGrid.addColumn(CurrencyDto::getCurrency)
                .setHeader("CURRENCY")
                .setSortable(true);
        currencyGrid.addColumn(currencyDto -> Arrays
                .stream(currencyDto.getRates())
                .iterator().next().getMid())
                .setHeader("RATE")
                .setSortable(true);
    }

    private void refreshCurrencyGrid(Grid<CurrencyDto> grid) {
        grid.setItems(currencyService.getCurrencies());
    }
}
