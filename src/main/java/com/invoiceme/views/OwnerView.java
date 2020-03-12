package com.invoiceme.views;

import com.invoiceme.domain.OwnerDto;
import com.invoiceme.layout.MainLayout;
import com.invoiceme.service.OwnerService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

import java.util.List;

@Route(value = "mycompany", layout = MainLayout.class)
public class OwnerView extends VerticalLayout {
    private OwnerService ownerService = new OwnerService();
    private TextField name = new TextField("Name");
    private TextField nip = new TextField("Nip");
    private TextField regon = new TextField("Regon");
    private TextField workingAddress = new TextField("Address");
    private TextField bankAccount = new TextField("Bank Account");
    private TextField email = new TextField("Email");
    private Button saveBuyerData = new Button("Save");
    private Button addNewCompanyData;
    private FormLayout fLCompanyData;
    private Label myName;
    private Label myNip;
    private Label myRegon;
    private Label myAddress;
    private Label myBankAccount;
    private Label myEmail;


    public OwnerView() {
        setOwnerDataLabels();
        fLCompanyData = getFormLayoutCompanyData();
        fLCompanyData.setVisible(false);
        addSaveCompanyDataButton();

        addNewCompanyData = getAddNewCompanyData();
        VerticalLayout companyDataLayout = getCompanyDataLayout();

        add(companyDataLayout, fLCompanyData);
        setSizeFull();
        setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        setAlignItems(FlexComponent.Alignment.CENTER);
    }

    private Button getAddNewCompanyData() {
        addNewCompanyData = new Button("Add new company data");
        addNewCompanyData.addClickListener(clickEvent -> {
            if (!fLCompanyData.isVisible()) {
                fLCompanyData.setVisible(true);
            } else {
                fLCompanyData.setVisible(false);
            }
        });
        return addNewCompanyData;
    }

    private VerticalLayout getCompanyDataLayout() {
        VerticalLayout companyDataLayout = new VerticalLayout();
        companyDataLayout.setSizeFull();
        companyDataLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        companyDataLayout.setAlignItems(Alignment.CENTER);
        companyDataLayout.add(myName, myNip, myAddress, myRegon, myBankAccount, myEmail, addNewCompanyData);
        companyDataLayout.setSizeFull();
        companyDataLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        companyDataLayout.setAlignItems(Alignment.CENTER);
        return companyDataLayout;
    }

    private void setOwnerDataLabels() {
        OwnerDto currentOwnerDto = ownerService.getOwner((long) ownerService.getOwners().size());
        myName = new Label(currentOwnerDto.getName());
        myNip = new Label("NIP: " + currentOwnerDto.getNip().toString());
        myRegon = new Label("REGON: " + currentOwnerDto.getRegon().toString());
        myAddress = new Label(currentOwnerDto.getWorkingAddress());
        myBankAccount = new Label("Bank Account: " + currentOwnerDto.getBankAccount());
        myEmail = new Label(currentOwnerDto.getEmail());
    }

    private void addSaveCompanyDataButton() {
        List<OwnerDto> myCompanyData = ownerService.getOwners();
        int companyDataNewestPossition = myCompanyData.size() - 1;

        if (myCompanyData.size() > 0) {
            name.setValue(myCompanyData.get(companyDataNewestPossition).getName());
            nip.setValue(myCompanyData.get(companyDataNewestPossition).getNip().toString());
            regon.setValue(myCompanyData.get(companyDataNewestPossition).getRegon().toString());
            workingAddress.setValue(myCompanyData.get(companyDataNewestPossition).getWorkingAddress());
            bankAccount.setValue(myCompanyData.get(companyDataNewestPossition).getBankAccount());
            email.setValue(myCompanyData.get(companyDataNewestPossition).getEmail());

            fLCompanyData.add(saveBuyerData);
            saveBuyerData.setMaxWidth("25em");

            saveBuyerData.addClickListener(event -> {
                OwnerDto ownerDto = new OwnerDto(
                        name.getValue(),
                        Long.valueOf(nip.getValue()),
                        Long.valueOf(regon.getValue()),
                        workingAddress.getValue(),
                        bankAccount.getValue(),
                        email.getValue());
                ownerService.saveOwner(ownerDto);
                UI.getCurrent().getPage().setLocation("http://localhost:8080/mycompany");
            });
        }
    }

    private FormLayout getFormLayoutCompanyData() {
        fLCompanyData = new FormLayout();
        fLCompanyData.setMaxWidth("120em");
        fLCompanyData.setResponsiveSteps(
                new FormLayout.ResponsiveStep("35em", 1),
                new FormLayout.ResponsiveStep("35em", 2),
                new FormLayout.ResponsiveStep("35em", 3));
        fLCompanyData.setMaxWidth("45em");
        fLCompanyData.getStyle().set("margin", "auto");

        fLCompanyData.add(
                name,
                nip,
                regon,
                workingAddress,
                bankAccount,
                email);
        return fLCompanyData;
    }
}
