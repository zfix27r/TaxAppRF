package com.taxapprf.taxapp.ui.newtransaction;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateCheck {
    private String year;
    private final boolean check;

    public DateCheck(String date) {
        this.year = "";
        Pattern p = Pattern.compile("(0?[1-9]|[12][0-9]|3[01])/(0?[1-9]|1[012])/((?:19|20)[0-9][0-9])");
        Matcher matcher = p.matcher(date);
        if (matcher.find()) {
            year = matcher.group(3);
            check = true;
        } else {
            check = false;
        }
    }

    public boolean check(){
        return check;
    }

    public String getYear() {
        return year;
    }
}
