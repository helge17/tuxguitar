package org.herac.tuxguitar.io.tef3.base;

public class TEPrintMetadata {
    private String pageFooter;
    private String firstPageHeader;
    private String secondaryPageHeader;

    public TEPrintMetadata(String pageFooter, String firstPageHeader, String secondaryPageHeader)
    {
        this.pageFooter = pageFooter;
        this.firstPageHeader = firstPageHeader;
        this.secondaryPageHeader = secondaryPageHeader;
    }

    public String getPageFooter() {
        return this.pageFooter;
    }

    public String getFirstPageHeader() {
        return this.firstPageHeader;
    }

    public String getSecondaryPageHeader() {
        return this.secondaryPageHeader;
    }
}
