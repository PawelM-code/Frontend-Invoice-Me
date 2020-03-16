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

import static com.invoiceme.config.InvoiceMeAddress.APP_URL;

@Route(value = "mycompany", layout = MainLayout.class)
public class OwnerView extends VerticalLayout {
    private OwnerService ownerService = new OwnerService();
    private TextField name = new TextField("NAME");
    private TextField nip = new TextField("NIP");
    private TextField regon = new TextField("REGON");
    private TextField workingAddress = new TextField("ADDRESS");
    private TextField bankAccount = new TextField("BANK ACCOUNT");
    private TextField email = new TextField("EMAIL");
    private Button saveOwnerData = new Button("SAVE");
    private Button addNewCompanyData;
    private FormLayout companyDataLayout;
    private VerticalLayout companyDataLabelLayout;
    private VerticalLayout newCompanyDataLayout;
    private Label myName;
    private Label myNip;
    private Label myRegon;
    private Label myAddress;
    private Label myBankAccount;
    private Label myEmail;


    public OwnerView() {
        setOwnerDataLabels();
        getFormLayoutCompanyData();
        addSaveCompanyDataButton();
        getAddNewCompanyData();
        getCurrentCompanyDataLayout();
        gerNewCompanyDataLayout();

        add(companyDataLabelLayout, newCompanyDataLayout);
        setSizeFull();
        setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        setAlignItems(FlexComponent.Alignment.CENTER);
    }

    private void getAddNewCompanyData() {
        addNewCompanyData = new Button("ADD NEW COMPANY DATA");
        addNewCompanyData.addClickListener(clickEvent -> {
            if (!newCompanyDataLayout.isVisible()) {
                newCompanyDataLayout.setVisible(true);
            } else {
                newCompanyDataLayout.setVisible(false);
            }
        });
    }

    private void getCurrentCompanyDataLayout() {
        companyDataLabelLayout = new VerticalLayout();
        companyDataLabelLayout.setSizeFull();
        companyDataLabelLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        companyDataLabelLayout.setAlignItems(Alignment.CENTER);
        companyDataLabelLayout.add(myName, myNip, myAddress, myRegon, myBankAccount, myEmail, addNewCompanyData);
        companyDataLabelLayout.setSizeFull();
        companyDataLabelLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        companyDataLabelLayout.setAlignItems(Alignment.CENTER);
    }

    private void setOwnerDataLabels() {
        if (ownerService.getOwners().size() > 0) {
            OwnerDto currentOwnerDto = ownerService.getOwner((long) ownerService.getOwners().size());
            myName = new Label(currentOwnerDto.getName());
            myNip = new Label("NIP: " + currentOwnerDto.getNip().toString());
            myRegon = new Label("REGON: " + currentOwnerDto.getRegon().toString());
            myAddress = new Label(currentOwnerDto.getWorkingAddress());
            myBankAccount = new Label("BANK ACCOUNT: " + currentOwnerDto.getBankAccount());
            myEmail = new Label(currentOwnerDto.getEmail());
        } else {
            myName = new Label("");
            myNip = new Label("");
            myRegon = new Label("");
            myAddress = new Label("");
            myBankAccount = new Label("");
            myEmail = new Label("");
        }
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

            setSaveOwnerDataButton();
        } else {
            setSaveOwnerDataButton();
        }

    }

    private void setSaveOwnerDataButton() {
        saveOwnerData.setWidth("20em");
        saveOwnerData.addClickListener(event -> {
            OwnerDto ownerDto = new OwnerDto(
                    name.getValue(),
                    Long.valueOf(nip.getValue()),
                    Long.valueOf(regon.getValue()),
                    workingAddress.getValue(),
                    bankAccount.getValue(),
                    email.getValue());
            ownerService.saveOwner(ownerDto);
            UI.getCurrent().getPage().setLocation(APP_URL + "/mycompany");
        });
    }

    private void getFormLayoutCompanyData() {
        companyDataLayout = new FormLayout();
        companyDataLayout.setSizeFull();
        companyDataLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("25em", 1),
                new FormLayout.ResponsiveStep("32em", 2),
                new FormLayout.ResponsiveStep("40em", 3));

        companyDataLayout.add(
                name,
                nip,
                regon,
                workingAddress,
                bankAccount,
                email);
    }

    private void gerNewCompanyDataLayout() {
        newCompanyDataLayout = new VerticalLayout();
        newCompanyDataLayout.setVisible(false);
        newCompanyDataLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        newCompanyDataLayout.setAlignItems(Alignment.CENTER);
        newCompanyDataLayout.add(companyDataLayout, saveOwnerData);
    }
}
