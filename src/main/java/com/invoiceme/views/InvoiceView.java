package com.invoiceme.views;

import com.invoiceme.domain.InvoiceCurrency;
import com.invoiceme.domain.InvoiceDto;
import com.invoiceme.domain.ItemDto;
import com.invoiceme.layout.FindBuyerLayout;
import com.invoiceme.layout.ItemLayout;
import com.invoiceme.layout.MainLayout;
import com.invoiceme.service.InvoiceService;
import com.invoiceme.service.ItemService;
import com.invoiceme.service.TaxpayerService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;
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

@Route(value = "invoice", layout = MainLayout.class)
public class InvoiceView extends VerticalLayout {
    private InvoiceService invoiceService = new InvoiceService();
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
        getDialog();
        getAddItemButton();
        getRemoveItemButton();
        getFormLayoutInvoiceNumber();
        getFormLayoutBottomSection();
        getFormLayoutAddOrRemoveItem();
        getVerticalLayoutInvoiceCreator();
        fLSave.add(saveInvoice);
        fLSave.getStyle().set("margin", "7px 25px 7px 25px");
        itemLayout.getStyle().set("margin", "7px 25px 7px 25px");
        itemLayout.setSizeFull();
        addComponentsToValidateList();
        getSaveInvoiceButton();

        add(vLInvoiceCreator, dialog);
        setSizeFull();
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
                UI.getCurrent().getPage().reload();
//                invoiceNumber.clear();
            } else {
                dialog.open();
            }

        });
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
        removeItem.setMaxWidth("10em");
        removeItem.addClickListener(event -> {
            List<Component> componentList = vLInvoiceCreator.getChildren().collect(Collectors.toList());
            List<Component> itemLayoutList = componentList.stream().filter(component -> component instanceof ItemLayout).collect(Collectors.toList());
            if (itemLayoutList.size() > 1) {
                vLInvoiceCreator.remove(itemLayoutList.get(itemLayoutList.size() - 1));
            }
        });
    }

    private void getAddItemButton() {
        addItem.setMaxWidth("10em");
        addItem.addClickListener(event -> {
            vLInvoiceCreator.addComponentAtIndex(vLInvoiceCreator.getComponentCount() - 1, new ItemLayout());
        });
    }

    private void getDialog() {
        dialog.add(new Label("Uzupełnij wymagane pola"));
        dialog.setWidth("300px");
        dialog.setHeight("150px");
    }

    private void getCurrency() {
        currencySelect.setRequiredIndicatorVisible(true);
        currencySelect.setItems(InvoiceCurrency.values());
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
        fLAddOrRemoveItem.getStyle().set("margin", "7px 25px 7px 25px");
        fLAddOrRemoveItem.add(addItem, removeItem);
    }

    private void getFormLayoutBottomSection() {
        fLBottomSection.setMaxWidth("55em");
        fLBottomSection.setSizeFull();
        fLBottomSection.setResponsiveSteps(
                new FormLayout.ResponsiveStep("55em", 1),
                new FormLayout.ResponsiveStep("55em", 2),
                new FormLayout.ResponsiveStep("55em", 3));
        fLBottomSection.getStyle().set("margin", "7px 25px 7px 25px");
        fLBottomSection.add(
                currencySelect,
                comment);
    }

    private void getFormLayoutInvoiceNumber() {
        fLInvoiceNumber.setMaxWidth("55em");
        fLInvoiceNumber.getStyle().set("margin", "7px 25px 7px 25px");
        fLInvoiceNumber.setSizeFull();
        fLInvoiceNumber.add(invoiceNumber);
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
