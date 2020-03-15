package com.invoiceme.views;

import com.invoiceme.component.ValidateComponent;
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
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.invoiceme.config.InvoiceMeAddress.APP_URL;
import static com.invoiceme.domain.InvoiceCurrency.PLN;
import static com.invoiceme.domain.InvoiceCurrency.values;

@Route(value = "invoice", layout = MainLayout.class)
public class InvoiceView extends VerticalLayout {
    private InvoiceService invoiceService = new InvoiceService();
    private OwnerService ownerService = new OwnerService();
    private TaxpayerService taxpayerService = new TaxpayerService();
    private ItemService itemService = new ItemService();
    private InvoiceDto invoiceDto = new InvoiceDto();
    private FindBuyerLayout findBuyerLayout = new FindBuyerLayout();
    private ValidateComponent validateComponent = new ValidateComponent();
    private FormLayout saveLayout;
    private FormLayout invoiceNumberLayout;
    private FormLayout addOrRemoveItemLayout;
    private FormLayout currencyAndCommentLayout;
    private Select<InvoiceCurrency> currencySelect;
    private TextField invoiceNumber;
    private TextField comment;
    private Button addItem;
    private Button removeItem;
    private Button saveInvoice;
    private Dialog dialog = new Dialog();
    private List<Component> componentsToValidate;

    public InvoiceView() {
        getInvoiceNumber();
        getInvoiceNumberLayout();
        getCurrency();
        getComment();
        getCurrencyAndCommentLayout();
        getAddItemButton();
        getRemoveItemButton();
        getAddOrRemoveItemLayout();
        getSaveInvoiceButton();
        getSaveLayout();

        setSizeFull();
        add(invoiceNumberLayout,
                findBuyerLayout.getFLFindBuyer(),
                findBuyerLayout.getFLCompleteBuyerData(),
                currencyAndCommentLayout,
                addOrRemoveItemLayout,
                new ItemLayout(),
                saveLayout);
    }

    private void getInvoiceNumber() {
        invoiceNumber = new TextField();
        invoiceNumber.setLabel("Invoice number");
        invoiceNumber.setRequired(true);
    }

    private void getInvoiceNumberLayout() {
        invoiceNumberLayout = new FormLayout();
        invoiceNumberLayout.add(invoiceNumber);
        setFormLayoutInvoiceNumber();
    }

    private void setFormLayoutInvoiceNumber() {
        invoiceNumberLayout.setMaxWidth("45em");
        setFormLayoutResponsiveSteps(invoiceNumberLayout);
        invoiceNumberLayout.add(invoiceNumber, 1);
    }

    private void getCurrency() {
        currencySelect = new Select<>();
        currencySelect.setRequiredIndicatorVisible(true);
        currencySelect.setItems(values());
        currencySelect.setValue(PLN);
        currencySelect.setLabel("Currency");
    }

    private void getComment() {
        comment = new TextField("Comment");
    }

    private void getCurrencyAndCommentLayout() {
        currencyAndCommentLayout = new FormLayout();
        setFormLayoutResponsiveSteps(currencyAndCommentLayout);
        currencyAndCommentLayout.add(
                currencySelect,
                comment);
    }

    private void getAddItemButton() {
        addItem = new Button("Add item");
        addItem.addClickListener(event ->
                addComponentAtIndex(getComponentCount() - 1, new ItemLayout()));
    }

    private void getRemoveItemButton() {
        removeItem = new Button("Remove item");
        removeItem.addClickListener(event -> {
            List<Component> componentList = getChildren().collect(Collectors.toList());
            List<Component> itemLayoutList = componentList.stream().filter(component -> component instanceof ItemLayout).collect(Collectors.toList());
            if (itemLayoutList.size() > 1) {
                remove(itemLayoutList.get(itemLayoutList.size() - 1));
            }
        });
    }

    private void getAddOrRemoveItemLayout() {
        addOrRemoveItemLayout = new FormLayout();
        setFormLayoutResponsiveSteps(addOrRemoveItemLayout);
        addOrRemoveItemLayout.add(addItem, removeItem);
    }

    private void getSaveInvoiceButton() {
        saveInvoice = new Button("Save invoice");

        saveInvoice.addClickListener(event -> {
            componentsToValidate = getComponentsList();
            if (validateComponent.isComponentNotEmpty(componentsToValidate)) {
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

                getChildren().forEach(component -> {
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

                UI.getCurrent().getPage().setLocation(APP_URL + "/invoice");
            } else {
                setDialog("<p><b>Please complete required fields.</b></p>");
            }
        });
    }

    private void setDialog(String html) {
        dialog.removeAll();
        dialog.setCloseOnEsc(false);
        dialog.setCloseOnOutsideClick(false);
        Button cancelButton = new Button("Cancel");
        cancelButton.addClickListener(clickEvent -> dialog.close());

        Html header = new Html(html);

        dialog.add(header, cancelButton);
        dialog.open();
    }

    private List<Component> getComponentsList() {
        componentsToValidate = new ArrayList<>();
        componentsToValidate.add(invoiceNumber);
        componentsToValidate.add(findBuyerLayout.getIssueDate());
        componentsToValidate.add(findBuyerLayout.getPaymentDate());
        componentsToValidate.add(findBuyerLayout.getNip());
        componentsToValidate.add(findBuyerLayout.getBuyerName());
        componentsToValidate.add(findBuyerLayout.getRegon());
        componentsToValidate.add(findBuyerLayout.getBuyerWorkingAddress());
        componentsToValidate.add(currencySelect);
        getChildren().forEach(component -> {
            if (component instanceof ItemLayout) {
                componentsToValidate.add(((ItemLayout) component).getSelectProducts());
                componentsToValidate.add(((ItemLayout) component).getNetPrice());
                componentsToValidate.add(((ItemLayout) component).getQuantity());
            }
        });
        return componentsToValidate;
    }

    private void getSaveLayout() {
        saveLayout = new FormLayout();
        saveLayout.add(saveInvoice);
        setFormLayoutResponsiveSteps(saveLayout);
    }

    private void setFormLayoutResponsiveSteps(FormLayout formLayout) {
        formLayout.setSizeFull();
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("25em", 1),
                new FormLayout.ResponsiveStep("32em", 2),
                new FormLayout.ResponsiveStep("40em", 3));
    }
}
