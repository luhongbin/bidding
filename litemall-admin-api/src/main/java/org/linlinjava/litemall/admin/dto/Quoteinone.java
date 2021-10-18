package org.linlinjava.litemall.admin.dto;

import org.linlinjava.litemall.db.domain.LitemallQuoteDieCasting;
import org.linlinjava.litemall.db.domain.LitemallQuoteElectronic;
import org.linlinjava.litemall.db.domain.LitemallQuoteHardware;
import org.linlinjava.litemall.db.domain.LitemallQuoteRubber;
import org.linlinjava.litemall.db.domain.LitemallQuoteBill;
import org.linlinjava.litemall.db.domain.LitemallRequote;

public class Quoteinone {
    LitemallQuoteBill quoteBill;
    LitemallRequote reQuote;
    LitemallQuoteDieCasting[] quoteDieCasting;
    LitemallQuoteElectronic[] quoteElectronic;
    LitemallQuoteHardware[] quoteHardware;
    LitemallQuoteRubber[] quoteRubber;

    public LitemallRequote getReQuote() {
        return reQuote;
    }

    public void setReQuote(LitemallRequote reQuote) {
        this.reQuote = reQuote;
    }

    public LitemallQuoteBill getQuoteBill() {
        return quoteBill;
    }

    public void setQuoteBill(LitemallQuoteBill quoteBill) {
        this.quoteBill = quoteBill;
    }

    public LitemallQuoteDieCasting[] getQuoteDieCasting() {
        return quoteDieCasting;
    }

    public void setQuoteDieCasting(LitemallQuoteDieCasting[] quoteDieCasting) {
        this.quoteDieCasting = quoteDieCasting;
    }

    public LitemallQuoteElectronic[] getQuoteElectronic() {
        return quoteElectronic;
    }

    public void setQuoteElectronic(LitemallQuoteElectronic[] quoteElectronic) {
        this.quoteElectronic = quoteElectronic;
    }

    public LitemallQuoteHardware[] getQuoteHardware() {
        return quoteHardware;
    }

    public void setQuoteHardware(LitemallQuoteHardware[] quoteHardware) {
        this.quoteHardware = quoteHardware;
    }

    public LitemallQuoteRubber[] getQuoteRubber() {
        return quoteRubber;
    }

    public void setQuoteRubber(LitemallQuoteRubber[] quoteRubber) {
        this.quoteRubber = quoteRubber;
    }

}