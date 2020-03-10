package com.invoiceme.views;

import com.invoiceme.layout.MainLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route(value = "logout", layout = MainLayout.class)
public class LogoutView extends VerticalLayout {
    public LogoutView() {
    }
}
