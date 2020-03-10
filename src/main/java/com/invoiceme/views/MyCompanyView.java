package com.invoiceme.views;

import com.invoiceme.domain.OwnerDto;
import com.invoiceme.layout.MainLayout;
import com.invoiceme.service.MyCompanyService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

import java.util.List;

@Route(value = "mycompany", layout = MainLayout.class)
public class MyCompanyView extends VerticalLayout {
    private MyCompanyService myCompanyService = new MyCompanyService();
    private TextField name = new TextField("Name");
    private TextField nip = new TextField("Nip");
    private TextField regon = new TextField("Regon");
    private TextField workingAddress = new TextField("Address");
    private TextField bankAccount = new TextField("Bank Account");
    private TextField email = new TextField("Email");
    private Button saveOwnerData = new Button("Save");
    private Button updateOwnerData = new Button("Update");
    private FormLayout fLCompanyData;

    public MyCompanyView() {
        fLCompanyData = getFormLayoutCompanyData();
        addSaveOrUpdateCompanyDataButton();
        add(fLCompanyData);
    }

    private void addSaveOrUpdateCompanyDataButton() {
        List<OwnerDto> myCompanyData = myCompanyService.getOwners();
        int companyDataNewestPossition = myCompanyData.size() - 1;

        if (myCompanyData.size() > 0) {
            name.setValue(myCompanyData.get(companyDataNewestPossition).getName());
            nip.setValue(myCompanyData.get(companyDataNewestPossition).getNip().toString());
            regon.setValue(myCompanyData.get(companyDataNewestPossition).getRegon().toString());
            workingAddress.setValue(myCompanyData.get(companyDataNewestPossition).getWorkingAddress());
            bankAccount.setValue(myCompanyData.get(companyDataNewestPossition).getBankAccount());
            email.setValue(myCompanyData.get(companyDataNewestPossition).getEmail());

            fLCompanyData.add(updateOwnerData);
            updateOwnerData.setMaxWidth("8em");

            updateOwnerData.addClickListener(event -> {
                myCompanyData.get(companyDataNewestPossition).setName(name.getValue());
                myCompanyData.get(companyDataNewestPossition).setNip(Long.valueOf(nip.getValue()));
                myCompanyData.get(companyDataNewestPossition).setRegon(Long.valueOf(regon.getValue()));
                myCompanyData.get(companyDataNewestPossition).setWorkingAddress(workingAddress.getValue());
                myCompanyData.get(companyDataNewestPossition).setBankAccount(bankAccount.getValue());
                myCompanyData.get(companyDataNewestPossition).setEmail(email.getValue());

                myCompanyService.updateOwner(myCompanyData.get(companyDataNewestPossition));
            });
        } else {
            fLCompanyData.add(saveOwnerData);
            saveOwnerData.setMaxWidth("8em");

            saveOwnerData.addClickListener(event -> {
                OwnerDto ownerDto = new OwnerDto(
                        name.getValue(),
                        Long.valueOf(nip.getValue()),
                        Long.valueOf(regon.getValue()),
                        workingAddress.getValue(),
                        bankAccount.getValue(),
                        email.getValue());

                myCompanyService.saveOwner(ownerDto);
            });
        }
    }

    private FormLayout getFormLayoutCompanyData() {
        FormLayout fLCompanyData = new FormLayout();
        fLCompanyData.setResponsiveSteps(
                new FormLayout.ResponsiveStep("35em", 1));
        fLCompanyData.setMaxWidth("35em");
        fLCompanyData.getStyle().set("margin", "7px 25px 7px 25px");
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
