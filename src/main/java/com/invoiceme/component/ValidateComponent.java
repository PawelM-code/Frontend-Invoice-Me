package com.invoiceme.component;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;

import java.util.List;

public class ValidateComponent {
    public ValidateComponent() {
    }

    public boolean isComponentNotEmpty(List<Component> componentsToValidate) {
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
}
