package com.invoiceme.views;

import com.invoiceme.layout.MainLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

import static com.vaadin.flow.component.icon.VaadinIcon.INVOICE;

@Route(value = "", layout = MainLayout.class)
public class MainView extends VerticalLayout {
    public MainView() {
        final Icon icon = INVOICE.create();
        icon.setSize("10%");
        Label textLibrary = new Label("INVOICE APP");
        setHorizontalComponentAlignment(Alignment.CENTER, textLibrary);
        setHorizontalComponentAlignment(Alignment.CENTER, icon);
        add(icon, textLibrary);
    }
}
