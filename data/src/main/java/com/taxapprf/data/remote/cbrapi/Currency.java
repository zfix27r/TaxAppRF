package com.taxapprf.data.remote.cbrapi;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;
import java.util.Objects;


@Root(name = "ValCurs", strict = false)
public class Currency {
    private Double rate;

    @ElementList(name = "Valute", inline = true)
    private List<CurrencyRate> currencyList;

    public List<CurrencyRate> getCurrencyList() {
        return currencyList;
    }

    public void setCurrencyList(List<CurrencyRate> currencyList) {
        this.currencyList = currencyList;
    }

    public Double getCurrencyRate(String valutaCode) {
        for (CurrencyRate currency : currencyList) {
            if (Objects.equals(currency.getCharCode(), valutaCode)) {
                if (currency.getValue() != null)
                    this.rate = currency.getValue() / currency.getNominal();
            }
        }
        return rate;
    }
}

