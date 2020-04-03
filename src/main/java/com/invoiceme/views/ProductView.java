package com.invoiceme.views;

import com.invoiceme.component.DialogWindow;
import com.invoiceme.domain.ProductDto;
import com.invoiceme.layout.MainLayout;
import com.invoiceme.service.ProductService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.router.Route;
import lombok.Getter;

import java.util.Collection;
import java.util.Collections;
import java.util.WeakHashMap;

@Getter
@PreserveOnRefresh
@Route(value = "product", layout = MainLayout.class)
public class ProductView extends VerticalLayout {
    private ProductService productService = new ProductService();
    private Grid<ProductDto> grid = new Grid<>();
    private FormLayout fLCreateProduct = new FormLayout();
    private DialogWindow dialogWindow = new DialogWindow();
    private TextField description;
    private IntegerField vat;
    private Button buttonCreateProduct;


    public ProductView() {
        getProductsGrid();
        getCreateProductFormLayout();

        refreshGridItems();
        setSizeFull();
        add(fLCreateProduct, grid);
    }

    private void getCreateProductFormLayout() {
        description = new TextField("DESCRIPTION");
        description.setRequiredIndicatorVisible(true);
        vat = new IntegerField("TAX");
        vat.setMax(100);
        vat.setRequiredIndicatorVisible(true);
        buttonCreateProduct = getButtonCreateProduct(grid, description, vat);

        fLCreateProduct.add(
                description,
                vat,
                buttonCreateProduct);
        fLCreateProduct.setResponsiveSteps(
                new FormLayout.ResponsiveStep("35em", 1),
                new FormLayout.ResponsiveStep("25em", 2));
        fLCreateProduct.setMaxWidth("35em");
    }

    private void getProductsGrid() {
        grid.setSizeFull();

        grid.addColumn(ProductDto::getId)
                .setHeader("ID")
                .setSortable(true)
                .setFlexGrow(0);
        Grid.Column<ProductDto> descriptionColumn = grid
                .addColumn(ProductDto::getDescription)
                .setSortable(true)
                .setHeader("DESCRIPTION");
        Grid.Column<ProductDto> vatColumn = grid
                .addColumn(ProductDto::getVat)
                .setSortable(true)
                .setHeader("TAX");
        addEditColumn(grid, descriptionColumn, vatColumn);
        addDeleteColumn(grid);
    }

    private Button getButtonCreateProduct(Grid<ProductDto> grid, TextField description, IntegerField vat) {
        Button buttonCreateProduct = new Button("SAVE PRODUCT");
        buttonCreateProduct.addClickListener(event -> {
            if (!description.isEmpty() && !vat.isEmpty()) {
                productService.createProduct(description.getValue(), vat.getValue());
                description.clear();

                refreshGridItems();
            } else {
                dialogWindow.setDialog("<p><b>Please complete required fields.</b></p>");
            }

        });
        return buttonCreateProduct;
    }

    private void addDeleteColumn(Grid<ProductDto> grid) {
        grid.addComponentColumn(productDto -> new Button("DELETE", click -> {
            productService.deleteProduct(productDto.getId());
            refreshGridItems();
        }));
    }

    private void addEditColumn(Grid<ProductDto> grid, Grid.Column<ProductDto> descriptionColumn, Grid.Column<ProductDto> vatColumn) {
        Binder<ProductDto> binder = new Binder<>(ProductDto.class);
        Editor<ProductDto> editor = grid.getEditor();
        editor.setBinder(binder);
        editor.setBuffered(true);

        TextField textFieldDescription2 = new TextField();
        binder.forField(textFieldDescription2).bind("description");
        descriptionColumn.setEditorComponent(textFieldDescription2);

        IntegerField integerFieldVat2 = new IntegerField();
        binder.forField(integerFieldVat2).bind("vat");
        vatColumn.setEditorComponent(integerFieldVat2);

        Collection<Button> editButtons = Collections
                .newSetFromMap(new WeakHashMap<>());

        Grid.Column<ProductDto> editorColumn = grid.addComponentColumn(productDto -> {
            Button edit = new Button("EDIT");
            edit.addClassName("edit");
            edit.addClickListener(e -> {
                editor.editItem(productDto);
                textFieldDescription2.focus();
            });
            edit.setEnabled(!editor.isOpen());
            editButtons.add(edit);
            return edit;
        });

        editor.addOpenListener(e -> editButtons
                .forEach(button -> button.setEnabled(!editor.isOpen())));
        editor.addCloseListener(e -> editButtons
                .forEach(button -> button.setEnabled(!editor.isOpen())));

        Button save = new Button("SAVE", e -> editor.save());
        save.addClassName("save");

        Button cancel = new Button("CANCEL", e -> editor.cancel());
        cancel.addClassName("cancel");

        grid.getElement().addEventListener("keyup", event -> editor.cancel())
                .setFilter("event.key === 'Escape' || event.key === 'Esc'");

        Div buttons = new Div(save, cancel);
        editorColumn.setEditorComponent(buttons);

        editor.addSaveListener(event -> {
            ProductDto productDto = new ProductDto(
                    event.getItem().getId(),
                    event.getItem().getDescription(),
                    event.getItem().getVat());
            productService.updateProduct(productDto);
        });
    }

    private void refreshGridItems() {
        grid.setItems(productService.getProducts());
    }
}
