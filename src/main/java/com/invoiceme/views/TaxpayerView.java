package com.invoiceme.views;

import com.invoiceme.domain.TaxpayerDto;
import com.invoiceme.layout.MainLayout;
import com.invoiceme.service.TaxpayerService;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;

@Route(value = "buyer", layout = MainLayout.class)
public class TaxpayerView extends VerticalLayout {
    private TaxpayerService taxpayerService = new TaxpayerService();
    private Grid<TaxpayerDto> taxpayerGrid = new Grid<>();
    private TextField filter = new TextField();

    public TaxpayerView() {
        setFilter();
        setTaxpayerGrid();
        addTaxpayerGridColumns();

        add(filter, taxpayerGrid);
        setSizeFull();
    }

    private void setFilter() {
        filter.setMinWidth("20em");
        filter.setPlaceholder("Filter by name, address, nip, regon");
        filter.setClearButtonVisible(true);
        filter.setValueChangeMode(ValueChangeMode.EAGER);
        filter.addValueChangeListener(e -> updateListFilterByNameOrValue());
    }

    private void setTaxpayerGrid() {
        taxpayerGrid.setSizeFull();
        taxpayerGrid.setItems(taxpayerService.getTaxpayers());
    }

    private void updateListFilterByNameOrValue() {
        taxpayerGrid.setItems(taxpayerService.getTaxpayers()
                .stream()
                .filter(t -> t.getName().contains(filter.getValue()) ||
                        t.getNip().toString().contains(filter.getValue()) ||
                        t.getRegon().toString().contains(filter.getValue()) ||
                        t.getWorkingAddress().contains(filter.getValue())));
    }

    private void addTaxpayerGridColumns() {
        taxpayerGrid.addColumn(TaxpayerDto::getId)
                .setHeader("ID")
                .setSortable(true)
                .setFlexGrow(0);
        taxpayerGrid.addColumn(TaxpayerDto::getName)
                .setHeader("Name")
                .setSortable(true)
                .setFlexGrow(1);
        taxpayerGrid.addColumn(TaxpayerDto::getWorkingAddress)
                .setHeader("Address")
                .setSortable(true)
                .setFlexGrow(3);
        taxpayerGrid.addColumn(TaxpayerDto::getNip)
                .setHeader("Nip")
                .setSortable(true)
                .setFlexGrow(1);
        taxpayerGrid.addColumn(TaxpayerDto::getRegon)
                .setHeader("Regon")
                .setSortable(true)
                .setFlexGrow(1);
    }
}
