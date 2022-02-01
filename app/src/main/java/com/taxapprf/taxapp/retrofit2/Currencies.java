package com.taxapprf.taxapp.retrofit2;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name="ValCurs", strict=false)
public class Currencies {
    private Double rate;

    @ElementList(name="Valute", inline=true)
    private List<CurrencyRate> currencyList;

    public List<CurrencyRate> getCurrencyList() {
        return currencyList;
    }

    public void setCurrencyList(List<CurrencyRate> currencyList) {
        this.currencyList = currencyList;
    }

    public Double getCurrencyRate(String valutaCode) {
        for (CurrencyRate currency: currencyList) {
            if (currency.getCharCode().equals(valutaCode)){
                this.rate = currency.getValue() / currency.getNominal();
            }
        }
        return rate;
    }
}

