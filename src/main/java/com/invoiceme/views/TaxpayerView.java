package com.invoiceme.views;

import com.invoiceme.domain.TaxpayerDto;
import com.invoiceme.layout.MainLayout;
import com.invoiceme.service.TaxpayerService;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route(value = "buyer", layout = MainLayout.class)
public class TaxpayerView extends VerticalLayout {
    private Grid<TaxpayerDto> taxpayerGrid = new Grid<>();

    public TaxpayerView(){
        taxpayerGrid.setSizeFull();
        TaxpayerService taxpayerService = new TaxpayerService();
        taxpayerGrid.setItems(taxpayerService.getTaxpayers());
        addTaxpayerGridColumns();
        add(taxpayerGrid);
        setSizeFull();
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
