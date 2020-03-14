package com.invoiceme.views;

import com.invoiceme.domain.InvoiceCurrency;
import com.invoiceme.domain.InvoiceDto;
import com.invoiceme.domain.ItemDto;
import com.invoiceme.layout.FindBuyerLayout;
import com.invoiceme.layout.ItemLayout;
import com.invoiceme.layout.MainLayout;
import com.invoiceme.service.InvoiceService;
import com.invoiceme.service.ItemService;
import com.invoiceme.service.OwnerService;
import com.invoiceme.service.TaxpayerService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.invoiceme.domain.InvoiceCurrency.PLN;
import static com.invoiceme.domain.InvoiceCurrency.values;

@Route(value = "invoice", layout = MainLayout.class)
public class InvoiceView extends VerticalLayout {
    private InvoiceService invoiceService = new InvoiceService();
    private OwnerService ownerService = new OwnerService();
    private TaxpayerService taxpayerService = new TaxpayerService();
    private ItemService itemService = new ItemService();
    private InvoiceDto invoiceDto = new InvoiceDto();
    private FormLayout fLInvoiceNumber = new FormLayout();
    private FormLayout fLAddOrRemoveItem = new FormLayout();
    private FormLayout fLBottomSection = new FormLayout();
    private FormLayout fLSave = new FormLayout();
    private VerticalLayout vLInvoiceCreator = new VerticalLayout();
    private ItemLayout itemLayout = new ItemLayout();
    private FindBuyerLayout findBuyerLayout = new FindBuyerLayout();
    private TextField invoiceNumber = getInvoiceNumber();
    private Select<InvoiceCurrency> currencySelect = new Select<>();
    private TextField comment = new TextField("Comment");
    private Button saveInvoice = new Button("Save invoice");
    private Button addItem = new Button("Add item");
    private Button removeItem = new Button("Remove item");
    private Dialog dialog = new Dialog();
    private List<Component> componentsToValidate = new ArrayList<>();


    public InvoiceView() {
        invoiceNumber.setRequired(true);
        getCurrency();
        getAddItemButton();
        getRemoveItemButton();
        getFormLayoutInvoiceNumber();
        getFormLayoutBottomSection();
        getFormLayoutAddOrRemoveItem();
        getVerticalLayoutInvoiceCreator();
        fLSave.add(saveInvoice);
        fLSave.setSizeFull();
        fLSave.setResponsiveSteps(
                new FormLayout.ResponsiveStep("25em", 1),
                new FormLayout.ResponsiveStep("32em", 2),
                new FormLayout.ResponsiveStep("40em", 3));
        itemLayout.setSizeFull();
        addComponentsToValidateList();
        getSaveInvoiceButton();

        add(vLInvoiceCreator);
    }

    private void getSaveInvoiceButton() {
        saveInvoice.addClickListener(event -> {
            if (isValid(componentsToValidate)) {
                taxpayerService.saveTaxpayer(findBuyerLayout.getTaxpayerDto());
                Long taxpayerId = taxpayerService.getTaxpayerId(Long.valueOf(findBuyerLayout.getNip().getValue()));
                findBuyerLayout.getTaxpayerDto().setId(taxpayerId);

                invoiceDto.setNumber(invoiceNumber.getValue());
                invoiceDto.setIssueDate(findBuyerLayout.getIssueDate().getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                invoiceDto.setDateOfPayment(findBuyerLayout.getPaymentDate().getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                invoiceDto.setInvoiceCurrency(currencySelect.getValue());
                invoiceDto.setOwnerDto(ownerService.getOwner((long) ownerService.getOwners().size()));
                invoiceDto.setTaxpayerDto(findBuyerLayout.getTaxpayerDto());
                invoiceDto.setComments(comment.getValue());
                invoiceService.createInvoice(invoiceDto);

                Long invoiceId = invoiceService.getInvoiceId(invoiceDto.getNumber());
                invoiceDto.setId(invoiceId);

                vLInvoiceCreator.getChildren().forEach(component -> {
                    if (component instanceof ItemLayout) {
                        ItemDto itemDto = new ItemDto();
                        itemDto.setInvoiceDto(invoiceDto);
                        itemDto.setProductDto(((ItemLayout) component).getSelectProducts().getValue());
                        itemDto.setNetPrice(new BigDecimal(((ItemLayout) component).getNetPrice().getValue()));
                        itemDto.setQuantity(((ItemLayout) component).getQuantity().getValue());

                        itemService.createItem(itemDto);
                    }
                });
                invoiceService.updateInvoice(invoiceDto);

                setDialog("<p><b>Confirm invoice save!</b></p>");

                UI.getCurrent().getPage().setLocation("http://localhost:8080/invoice");
            } else {
                setDialog("<p><b>Please complete required fields.</b></p>");
            }

        });
    }

    private void setDialog(String html) {
        dialog.setCloseOnEsc(false);
        dialog.setCloseOnOutsideClick(false);
        Button cancelButton = new Button("Cancel");
        cancelButton.addClickListener(clickEvent -> dialog.close());

        Html header = new Html(html);

        dialog.add(header, cancelButton);
        dialog.open();
    }

    private void addComponentsToValidateList() {
        componentsToValidate.add(invoiceNumber);
        componentsToValidate.add(findBuyerLayout.getIssueDate());
        componentsToValidate.add(findBuyerLayout.getPaymentDate());
        componentsToValidate.add(findBuyerLayout.getNip());
        componentsToValidate.add(findBuyerLayout.getBuyerName());
        componentsToValidate.add(findBuyerLayout.getRegon());
        componentsToValidate.add(findBuyerLayout.getBuyerWorkingAddress());
        componentsToValidate.add(currencySelect);
        componentsToValidate.add(itemLayout.getSelectProducts());
        componentsToValidate.add(itemLayout.getNetPrice());
        componentsToValidate.add(itemLayout.getQuantity());
    }

    private void getRemoveItemButton() {
        removeItem.addClickListener(event -> {
            List<Component> componentList = vLInvoiceCreator.getChildren().collect(Collectors.toList());
            List<Component> itemLayoutList = componentList.stream().filter(component -> component instanceof ItemLayout).collect(Collectors.toList());
            if (itemLayoutList.size() > 1) {
                vLInvoiceCreator.remove(itemLayoutList.get(itemLayoutList.size() - 1));
            }
        });
    }

    private void getAddItemButton() {
        addItem.addClickListener(event -> {
            vLInvoiceCreator.addComponentAtIndex(vLInvoiceCreator.getComponentCount() - 1, new ItemLayout());
        });
    }

    private void getCurrency() {
        currencySelect.setRequiredIndicatorVisible(true);
        currencySelect.setItems(values());
        currencySelect.setValue(PLN);
        currencySelect.setLabel("Currency");
    }

    private void getVerticalLayoutInvoiceCreator() {
        vLInvoiceCreator.add(
                fLInvoiceNumber,
                findBuyerLayout.getFLFindBuyer(),
                findBuyerLayout.getFLCompleteBuyerData(),
                fLBottomSection,
                fLAddOrRemoveItem,
                itemLayout,
                fLSave);
        vLInvoiceCreator.setSizeFull();
    }

    private void getFormLayoutAddOrRemoveItem() {
        fLAddOrRemoveItem.setResponsiveSteps(
                new FormLayout.ResponsiveStep("25em", 1),
                new FormLayout.ResponsiveStep("32em", 2),
                new FormLayout.ResponsiveStep("40em", 3));
        fLAddOrRemoveItem.setSizeFull();
        fLAddOrRemoveItem.add(addItem, removeItem);
    }

    private void getFormLayoutBottomSection() {
        fLBottomSection.setSizeFull();
        fLBottomSection.setResponsiveSteps(
                new FormLayout.ResponsiveStep("25em", 1),
                new FormLayout.ResponsiveStep("32em", 2),
                new FormLayout.ResponsiveStep("40em", 3));
//        fLBottomSection.getStyle().set("margin", "7px 25px 7px 25px");
        fLBottomSection.add(
                currencySelect,
                comment);
    }

    private void getFormLayoutInvoiceNumber() {
//        fLInvoiceNumber.getStyle().set("margin", "7px 25px 7px 25px");
        fLInvoiceNumber.setSizeFull();
        fLInvoiceNumber.setResponsiveSteps(
                new FormLayout.ResponsiveStep("25em", 1),
                new FormLayout.ResponsiveStep("32em", 2),
                new FormLayout.ResponsiveStep("40em", 3));
        fLInvoiceNumber.add(invoiceNumber, 1);
    }

    private boolean isValid(List<Component> componentsToValidate) {
        boolean isValid = true;

        for (Component c : componentsToValidate) {
            if (c instanceof TextField) {
                if (((TextField) c).isEmpty()) {
                    isValid = false;
                    break;
                }
            } else if (c instanceof DatePicker) {
                if (((DatePicker) c).isEmpty()) {
                    isValid = false;
                    break;
                }
            } else if (c instanceof Select) {
                if (((Select) c).isEmpty()) {
                    isValid = false;
                    break;
                }
            } else if (c instanceof NumberField) {
                if (((NumberField) c).isEmpty()) {
                    isValid = false;
                    break;
                }
            } else if (c instanceof IntegerField) {
                if (((IntegerField) c).isEmpty()) {
                    isValid = false;
                    break;
                }
            }
        }
        return isValid;
    }

    private TextField getInvoiceNumber() {
        FormLayout fLInvoiceNumber = new FormLayout();
        TextField textFieldNumber = new TextField("Invoice number");
        fLInvoiceNumber.add(textFieldNumber);
        fLInvoiceNumber.setMaxWidth("45em");
        return textFieldNumber;
    }
}
