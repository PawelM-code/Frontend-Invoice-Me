package com.invoiceme.layout;

import com.invoiceme.views.*;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Viewport;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

import java.util.HashMap;
import java.util.Map;

import static com.vaadin.flow.component.icon.VaadinIcon.*;

@Theme(value = Lumo.class)
@Viewport("width=device-width, minimum-scale=1, initial-scale=1, currencies-scalable=yes, viewport-fit=cover")
public class MainLayout extends AppLayout {
    private static final String LOGO_PNG = "logo_invoice.png";
    private Map<Tab, Component> tab2Workspace = new HashMap<>();

    public MainLayout() {
        Image img = new Image(LOGO_PNG, "Invoice.Me Logo");
        img.setHeight("80px");

        addToNavbar(img);

        final Tabs tabs = new Tabs(home(), invoiceCreator(), invoices(), product(), currencies(), taxpayer(), mycompany(), logout());
        tabs.setOrientation(Tabs.Orientation.HORIZONTAL);
        tabs.addSelectedChangeListener(event -> {
            final Tab selectedTab = event.getSelectedTab();
            final Component component = tab2Workspace.get(selectedTab);
            if (component instanceof InvoiceView) {
                getUI().ifPresent(ui -> ui.navigate("invoice"));
            } else if (component instanceof InvoiceListView) {
                getUI().ifPresent(ui -> ui.navigate("invoices"));
            } else if (component instanceof ProductView) {
                getUI().ifPresent(ui -> ui.navigate("product"));
            } else if (component instanceof CurrenciesView) {
                getUI().ifPresent(ui -> ui.navigate("currency"));
            } else if (component instanceof MainView) {
                getUI().ifPresent(ui -> ui.navigate(""));
            } else if (component instanceof OwnerView) {
                getUI().ifPresent(ui -> ui.navigate("mycompany"));
            } else if (component instanceof TaxpayerView) {
                getUI().ifPresent(ui -> ui.navigate("buyer"));
            } else if (component instanceof LogoutView) {
                UI.getCurrent().getPage().setLocation("http://localhost:8080/logout");
            }
        });

        FlexLayout centeredLayout = new FlexLayout();
        centeredLayout.setSizeFull();
        centeredLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        centeredLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        centeredLayout.add(tabs);

        addToNavbar(centeredLayout);
    }

    private Tab home() {
        final Label label = new Label("Home");
        final Icon icon = HOME_O.create();
        final Tab tab = new Tab(getVerticalMenuBarLayout(label, icon));
        tab2Workspace.put(tab, new MainView());
        return tab;
    }

    private Tab invoiceCreator() {
        final Label label = new Label("Invoice creator");
        final Icon icon = INVOICE.create();
        final Tab tab = new Tab(getVerticalMenuBarLayout(label, icon));
        tab2Workspace.put(tab, new InvoiceView());
        return tab;
    }

    private Tab invoices() {
        final Label label = new Label("Invoices");
        final Icon icon = LINES_LIST.create();
        final Tab tab = new Tab(getVerticalMenuBarLayout(label, icon));
        tab2Workspace.put(tab, new InvoiceListView());
        return tab;
    }

    private Tab product() {
        final Label label = new Label("Product");
        final Icon icon = CART_O.create();
        final Tab tab = new Tab(getVerticalMenuBarLayout(label, icon));
        tab2Workspace.put(tab, new ProductView());
        return tab;
    }

    private Tab currencies() {
        final Label label = new Label("Currency");
        final Icon icon = DOLLAR.create();
        final Tab tab = new Tab(getVerticalMenuBarLayout(label, icon));
        tab2Workspace.put(tab, new CurrenciesView());
        return tab;
    }

    private Tab mycompany() {
        final Label label = new Label("My Company");
        final Icon icon = USER_CARD.create();
        final Tab tab = new Tab(getVerticalMenuBarLayout(label, icon));
        tab2Workspace.put(tab, new OwnerView());
        return tab;
    }

    private Tab taxpayer() {
        final Label label = new Label("Buyers");
        final Icon icon = DATABASE.create();
        final Tab tab = new Tab(getVerticalMenuBarLayout(label, icon));
        tab2Workspace.put(tab, new TaxpayerView());
        return tab;
    }

    private Tab logout() {
        final Label label = new Label("Logout");
        final Icon icon = OUT.create();
        final Tab tab = new Tab(getVerticalMenuBarLayout(label, icon));
        tab2Workspace.put(tab, new LogoutView());
        return tab;
    }


    private VerticalLayout getVerticalMenuBarLayout(Label label, Icon icon) {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        verticalLayout.add(icon, label);
        return verticalLayout;
    }
}
