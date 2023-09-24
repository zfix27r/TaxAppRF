package com.taxapprf.data.remote.cbr;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "Valute", strict = false)
public class RemoteRateEntity {

    @Element(name = "CharCode")
    private String charCode;

    @Element(name = "Nominal")
    private int nominal;

    @Element(name = "Name")
    private String name;

    @Element(name = "Value")
    private String valueString;

    public String getCharCode() {
        return charCode;
    }

    public void setCharCode(String charCode) {
        this.charCode = charCode;
    }

    public int getNominal() {
        return nominal;
    }

    public void setNominal(int nominal) {
        this.nominal = nominal;
    }

    public String getValueString() {
        return valueString;
    }

    public void setValueString(String value) {
        this.valueString = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getValue() {
        String valueStr = valueString.replace(',','.');
        Double value = Double.parseDouble(valueStr);
        return value;
    }

}

