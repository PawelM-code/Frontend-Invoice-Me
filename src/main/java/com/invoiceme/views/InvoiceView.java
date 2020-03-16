package com.invoiceme.views;

import com.invoiceme.component.DialogWindow;
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
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
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
    private FindBuyerLayout findBuyerLayout;
    private FormLayout buyerSearchLayout;
    private FormLayout completeBuyerDataLayout;
    private ValidateComponent validateComponent = new ValidateComponent();
    private FlexLayout saveLayout = new FlexLayout();
    private DialogWindow dialogWindow = new DialogWindow();
    private FormLayout invoiceNumberLayout;
    private FormLayout addOrRemoveItemLayout;
    private FormLayout currencyAndCommentLayout;
    private Select<InvoiceCurrency> currencySelect;
    private TextField invoiceNumber;
    private TextField comment;
    private Button addItem;
    private Button removeItem;
    private Button saveInvoice;
    private List<Component> componentsToValidate;

    public InvoiceView() {
        getInvoiceNumber();
        getInvoiceNumberLayout();
        getFindBuyerLayouts();
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
                buyerSearchLayout,
                completeBuyerDataLayout,
                findBuyerLayout.getCompleteBuyerDataLayout(),
                currencyAndCommentLayout,
                addOrRemoveItemLayout,
                new ItemLayout(),
                saveLayout);
    }

    private void getInvoiceNumber() {
        invoiceNumber = new TextField();
        invoiceNumber.setLabel("INVOICE NUMBER");
        invoiceNumber.setRequired(true);
    }

    private void getInvoiceNumberLayout() {
        invoiceNumberLayout = new FormLayout();
        invoiceNumberLayout.add(invoiceNumber);
        setFormLayoutInvoiceNumber();
    }

    private void getFindBuyerLayouts() {
        findBuyerLayout = new FindBuyerLayout();
        buyerSearchLayout = findBuyerLayout.getFindBuyerLayout();
        completeBuyerDataLayout = findBuyerLayout.getCompleteBuyerDataLayout();
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
        currencySelect.setLabel("OPTIONALLY, SHOW THE AMOUNT ALSO IN A FOREIGN CURRENCY");
    }

    private void getComment() {
        comment = new TextField("COMMENT");
    }

    private void getCurrencyAndCommentLayout() {
        currencyAndCommentLayout = new FormLayout();
        setFormLayoutResponsiveSteps(currencyAndCommentLayout);
        currencyAndCommentLayout.add(
                currencySelect,
                comment);
    }

    private void getAddItemButton() {
        addItem = new Button("ADD ITEM");
        addItem.addClickListener(event ->
                addComponentAtIndex(getComponentCount() - 1, new ItemLayout()));
    }

    private void getRemoveItemButton() {
        removeItem = new Button("REMOVE ITEM");
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
        saveInvoice = new Button("SAVE INVOICE");
        saveInvoice.setHeight("45px");
        saveInvoice.getStyle().set("background", "#3D94F6");
        saveInvoice.getStyle().set("color", "#FFFFFF");
        saveInvoice.getStyle().set("border", "solid #337FED 1px");

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

                dialogWindow.setDialog("<p><b>Confirm invoice save!</b></p>");

                UI.getCurrent().getPage().setLocation(APP_URL + "/invoice");
            } else {
                dialogWindow.setDialog("<p><b>Please complete required fields.</b></p>");
            }
        });
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
        saveLayout.setSizeFull();
        saveLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        saveLayout.setAlignItems(Alignment.CENTER);
        saveLayout.add(saveInvoice);
    }

    private void setFormLayoutResponsiveSteps(FormLayout formLayout) {
        formLayout.setSizeFull();
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("25em", 1),
                new FormLayout.ResponsiveStep("32em", 2),
                new FormLayout.ResponsiveStep("40em", 3));
    }
}
