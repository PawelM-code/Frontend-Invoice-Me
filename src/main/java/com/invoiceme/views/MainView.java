package com.invoiceme.views;

import com.invoiceme.layout.MainLayout;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route(value = "", layout = MainLayout.class)
public class MainView extends VerticalLayout {
    public MainView() {
        Image image = new Image();
        image.setSrc("/img/1.png");
        image.setSizeFull();

        add(image);
    }
}
