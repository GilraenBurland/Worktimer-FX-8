package com.kn.wfx8.widgets;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;

public class NumberField extends TextField {

    public NumberField() {
        focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue == false) {
                    resetBorder();
                }
            }
        });
    }
    
    public boolean hasValue() {
        return !getText().isEmpty();
    }
    
    public int getValue() {
        return Integer.valueOf(getText());
    }

    private int minLength = 0;

    public int getMinLength() {
        return minLength;
    }

    public void setMinLength(int minLength) {
        this.minLength = minLength;
    }

    private int maxLength;

    public int getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    @Override
    public void replaceText(int start, int end, String addition) {
        String completeText = getText() + addition;
        if (completeText.matches(onlyDigitsWithMaxLength())) {
            if (completeText.length() < minLength) {
                setErrorBorder();
                super.replaceText(start, end, addition);
            } else {
                resetBorder();
                super.replaceText(start, end, addition);
            }
        } else {
            setErrorBorder();
        }

    }

    private static final String ONLY_DIGITS = "[0-9]";

    private String onlyDigitsWithMaxLength() {
        return ONLY_DIGITS + "{0," + maxLength + "}";
    }

    @Override
    public void deleteText(int start, int end) {
        int newLength = getText().length() - (end - start);
        if (newLength >= 0) {
            super.deleteText(start, end);
            if (newLength < minLength) {
                setErrorBorder();
            } else {
                resetBorder();
            }
        } else {
            setErrorBorder();
        }
    }

    private void resetBorder() {
        setStyle("-fx-border-color: null");
    }

    private void setErrorBorder() {
        setStyle("-fx-border-color: red");
    }
}
