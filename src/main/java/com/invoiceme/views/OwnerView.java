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
    private Button saveBuyerData = new Button("SAVE");
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
        getFormLayoutCompanyData();
        addSaveCompanyDataButton();
        getAddNewCompanyData();
        VerticalLayout companyDataLayout = getCompanyDataLayout();

        add(companyDataLayout, fLCompanyData);
        setSizeFull();
        setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        setAlignItems(FlexComponent.Alignment.CENTER);
    }

    private void getAddNewCompanyData() {
        addNewCompanyData = new Button("ADD NEW COMPANY DATA");
        addNewCompanyData.addClickListener(clickEvent -> {
            if (!fLCompanyData.isVisible()) {
                fLCompanyData.setVisible(true);
            } else {
                fLCompanyData.setVisible(false);
            }
        });
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
        myBankAccount = new Label("BANK ACCOUNT: " + currentOwnerDto.getBankAccount());
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
                UI.getCurrent().getPage().setLocation(APP_URL + "/mycompany");
            });
        }
    }

    private void getFormLayoutCompanyData() {
        fLCompanyData = new FormLayout();
        fLCompanyData.setVisible(false);
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
    }
}
