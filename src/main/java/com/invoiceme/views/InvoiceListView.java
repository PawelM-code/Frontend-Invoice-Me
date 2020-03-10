package com.invoiceme.views;

import com.invoiceme.domain.InvoiceDto;
import com.invoiceme.domain.ItemDto;
import com.invoiceme.domain.OwnerDto;
import com.invoiceme.layout.MainLayout;
import com.invoiceme.service.InvoiceService;
import com.invoiceme.service.ItemService;
import com.invoiceme.service.MyCompanyService;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Route(value = "invoices", layout = MainLayout.class)
public class InvoiceListView extends VerticalLayout {
    private MyCompanyService myCompanyService = new MyCompanyService();
    private InvoiceService invoiceService = new InvoiceService();
    private ItemService itemService = new ItemService();
    private Grid<InvoiceDto> grid = new Grid<>();
    private TextField filter = new TextField();
    private DatePicker invoiceIssueDateFilter = new DatePicker();
    private Notification notification = new Notification();

    public InvoiceListView() {
        setFilter();
        setInvoiceIssueDateFilter();

        addGridColumns();
        setGridItemsDetails();
        setGridSettings();
        refreshGridItems(grid);

        setSizeFull();
        add(filter, invoiceIssueDateFilter, grid, notification);
    }

    private void setInvoiceIssueDateFilter() {
        invoiceIssueDateFilter.setLabel("Filter by invoice date: ");
        invoiceIssueDateFilter.setClearButtonVisible(true);
        invoiceIssueDateFilter.addValueChangeListener(event -> dateFilter());
    }

    private void setFilter() {
        filter.setPlaceholder("Filter by buyer, number...");
        filter.setClearButtonVisible(true);
        filter.setValueChangeMode(ValueChangeMode.EAGER);
        filter.addValueChangeListener(e -> updateListFilterByNameOrValue());
    }

    private void setGridSettings() {
        grid.setHeightFull();
        grid.setSelectionMode(Grid.SelectionMode.NONE);
        grid.setDetailsVisibleOnClick(false);
    }

    private void setGridItemsDetails() {
        grid.setItemDetailsRenderer(new ComponentRenderer<>(invoiceDto -> {
            VerticalLayout verticalLayoutInvoiceDetails = new VerticalLayout();
            List<ItemDto> itemsByInvoiceId = itemService.getItemsByInvoiceId(invoiceDto.getId());

            Grid<ItemDto> details = new Grid<>();
            details.setSizeFull();
            details.addThemeNames("no-row-borders", "row-stripes", "no-headers");
            if (!itemsByInvoiceId.isEmpty()) {
                details.setItems(itemsByInvoiceId);
                details.addColumn(itemDto -> itemDto.getProductDto().getDescription()).setHeader("Description");
                details.addColumn(ItemDto::getNetPrice).setHeader("Net price PLN");
                details.addColumn(ItemDto::getVat).setHeader("VAT PLN");
                details.addColumn(ItemDto::getGrossPrice).setHeader("Gross price PLN");
                details.addColumn(ItemDto::getQuantity).setHeader("Quantity");
                details.addColumn(ItemDto::getValue).setHeader("Total PLN");
                details.setHeightByRows(true);
                verticalLayoutInvoiceDetails.setSizeFull();
                verticalLayoutInvoiceDetails.add(details);
            }
            return verticalLayoutInvoiceDetails;
        }));
    }

    private void addGridColumns() {
        grid.addColumn(InvoiceDto::getId).setHeader("ID").setSortable(true);
        grid.addColumn(InvoiceDto::getNumber).setHeader("Number").setSortable(true);
        grid.addColumn(InvoiceDto::getIssueDate).setHeader("Date of issue").setSortable(true);
        grid.addColumn(InvoiceDto::getDateOfPayment).setHeader("Date of payment").setSortable(true);
        grid.addColumn(invoiceDto -> invoiceDto.getTaxpayerDto().getName()).setHeader("Buyer").setSortable(true);
        grid.addColumn(InvoiceDto::getNetTotal).setHeader("Net PLN").setSortable(true);
        grid.addColumn(InvoiceDto::getVatTotal).setHeader("VAT PLN").setSortable(true);
        grid.addColumn(InvoiceDto::getGrossTotal).setHeader("Gross PLN").setSortable(true);
        grid.addColumn(InvoiceDto::getCurrencyGrossTotal).setHeader("Currency gross total").setSortable(true);
        grid.addColumn(InvoiceDto::getInvoiceCurrency).setHeader("Currency").setSortable(true);
        grid.addComponentColumn(invoiceDto -> new Button("Delete", click -> {
            invoiceService.deleteInvoice(invoiceDto.getId());
            refreshGridItems(grid);
        }));
//        grid.addComponentColumn(invoiceDto -> new Button("Edit", click -> {
//            getUI().ifPresent(ui -> ui.navigate("invoice"));
//        }));
        grid.addComponentColumn(invoiceDto -> new Button("Details",
                click -> grid
                        .setDetailsVisible(
                                invoiceDto,
                                !grid.isDetailsVisible(invoiceDto))));
        grid.addComponentColumn(invoiceDto -> {
                    Icon file = new Icon(VaadinIcon.FILE);
                    file.addClickListener(event -> {
                        File in = null;
                        try {
                            in = File.createTempFile("invoice",".pdf");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        DownloadLink downloadLink = new DownloadLink(in);
                        downloadLink.setVisible(false);
                        try {
                            generatePDF(invoiceDto, in);
                            UI.getCurrent().getPage().open(downloadLink.getAnchor().getHref());
                        } catch (IOException | DocumentException e) {
                            e.printStackTrace();
                        }
                        add(downloadLink);
                    });

                    return file;
                }
        );
    }

    private void generatePDF(InvoiceDto invoiceDto, File file) throws IOException, DocumentException {
        List<ItemDto> itemsByInvoiceId = itemService.getItemsByInvoiceId(invoiceDto.getId());

        PdfReader pdfTemplate = new PdfReader("META-INF/resources/template.pdf");
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        PdfStamper stamper = new PdfStamper(pdfTemplate, fileOutputStream);
        stamper.setFormFlattening(true);
        stamper.getAcroFields().setField("date", invoiceDto.getIssueDate());
        stamper.getAcroFields().setField("invoice", invoiceDto.getNumber());
        stamper.getAcroFields().setField("buyerName", invoiceDto.getTaxpayerDto().getName());
        stamper.getAcroFields().setField("buyerAddress", invoiceDto.getTaxpayerDto().getWorkingAddress());
        stamper.getAcroFields().setField("buyerNip", invoiceDto.getTaxpayerDto().getNip().toString());
        stamper.getAcroFields().setField("net", invoiceDto.getNetTotal().toString() + " PLN");
        stamper.getAcroFields().setField("vat", invoiceDto.getVatTotal().toString() + " PLN");
        stamper.getAcroFields().setField("gross", invoiceDto.getGrossTotal().toString() + " PLN");
        stamper.getAcroFields().setField("currency", invoiceDto.getCurrencyGrossTotal().toString() + " " + invoiceDto.getInvoiceCurrency());
        stamper.getAcroFields().setField("payment", invoiceDto.getDateOfPayment());

        List<OwnerDto> myCompanyData = myCompanyService.getOwners();
        int companyDataNewestPossition = myCompanyData.size() - 1;

        if (myCompanyData.size() > 0) {
            stamper.getAcroFields().setField("myname", myCompanyData.get(companyDataNewestPossition).getName());
            stamper.getAcroFields().setField("myaddress", myCompanyData.get(companyDataNewestPossition).getWorkingAddress());
            stamper.getAcroFields().setField("mynip", myCompanyData.get(companyDataNewestPossition).getNip().toString());
            stamper.getAcroFields().setField("account", myCompanyData.get(companyDataNewestPossition).getBankAccount());

        }

        if (!itemsByInvoiceId.isEmpty()) {
            for (int i = 0; i < itemsByInvoiceId.size(); i++) {
                int number = i + 1;
                stamper.getAcroFields().setField("item" + number, itemsByInvoiceId.get(i).getProductDto().getDescription());
                stamper.getAcroFields().setField("price" + number, itemsByInvoiceId.get(i).getGrossPrice().toString() + " PLN");
                stamper.getAcroFields().setField("qty" + number, String.valueOf(itemsByInvoiceId.get(i).getQuantity()));
                stamper.getAcroFields().setField("total" + number, itemsByInvoiceId.get(i).getValue().toString() + " PLN");
            }
        }

        stamper.close();
        pdfTemplate.close();
    }

    private void updateListFilterByNameOrValue() {
        grid.setItems(invoiceService.getInvoices()
                .stream()
                .filter(e -> e.getTaxpayerDto().getName().contains(filter.getValue()) ||
                        e.getNumber().contains(filter.getValue())));
    }

    private void dateFilter() {
        if (invoiceIssueDateFilter != null) {
            try {
                List<InvoiceDto> invoiceDtoList = invoiceService.getInvoices()
                        .stream()
                        .filter(e -> e.getIssueDate()
                                .equals(invoiceIssueDateFilter.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))))
                        .collect(Collectors.toList());
                grid.setItems(invoiceDtoList);
                if (!invoiceIssueDateFilter.isClearButtonVisible() || invoiceDtoList.size() == 0) {
                    notification.setText("No filtering result");
                    notification.setDuration(3000);
                    notification.setPosition(Notification.Position.MIDDLE);
                    notification.open();
                }
            } catch (NullPointerException e) {
                grid.setItems(invoiceService.getInvoices());
            }
        } else {
            grid.setItems(invoiceService.getInvoices());
        }
    }

    private void refreshGridItems(Grid<InvoiceDto> grid) {
        grid.setItems(invoiceService.getInvoices());
    }
}