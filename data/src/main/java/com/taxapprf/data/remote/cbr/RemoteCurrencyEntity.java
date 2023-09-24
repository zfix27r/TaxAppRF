package com.taxapprf.data.remote.cbr;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;
import java.util.Objects;


@Root(name = "ValCurs", strict = false)
public class RemoteCurrencyEntity {
    private Double rate;

    @ElementList(name = "Valute", inline = true)
    private List<RemoteRateEntity> currencyList;

    public List<RemoteRateEntity> getCurrencyList() {
        return currencyList;
    }

    public void setCurrencyList(List<RemoteRateEntity> currencyList) {
        this.currencyList = currencyList;
    }

    public Double getCurrencyRate(String valutaCode) {
        for (RemoteRateEntity currency : currencyList) {
            if (Objects.equals(currency.getCharCode(), valutaCode)) {
                if (currency.getValue() != null)
                    this.rate = currency.getValue() / currency.getNominal();
            }
        }
        return rate;
    }
}

