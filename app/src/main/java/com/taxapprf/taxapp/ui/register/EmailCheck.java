package com.taxapprf.taxapp.ui.register;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailCheck {
    private boolean check;

    public EmailCheck(String email) {
        Pattern p = Pattern.compile("\\w+([\\.-]?\\w+)*@\\w+([\\.-]?\\w+)*\\.\\w{2,4}");
        Matcher matcher = p.matcher(email);
        check = matcher.matches();
    }

    public boolean check() {
        return check;
    }

}
