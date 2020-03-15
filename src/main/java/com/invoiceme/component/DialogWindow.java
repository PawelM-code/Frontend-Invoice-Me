package com.invoiceme.component;

import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;

public class DialogWindow {
    private Dialog dialog = new Dialog();
    public DialogWindow(){
    }

    public void setDialog(String html) {
        dialog.removeAll();
        dialog.setCloseOnEsc(false);
        dialog.setCloseOnOutsideClick(false);
        Button cancelButton = new Button("Cancel");
        cancelButton.addClickListener(clickEvent -> dialog.close());

        Html header = new Html(html);

        dialog.add(header, cancelButton);
        dialog.open();
    }
}
