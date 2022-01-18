package com.taxapprf.taxapp.ui.newtransaction;

public class DoubleCheck {
    private boolean check;
    private double numDouble;

    public DoubleCheck(String string) {
        String string1 = string;
        string = string.replaceAll("," , "\\.");
        try {
            numDouble = Double.parseDouble(string);
            check = true;
        } catch (NumberFormatException e) {
            check = false;
        }
    }

    public boolean isCheck(){
        return check;
    }

    public double getNumDouble() {
        return numDouble;
    }
}
