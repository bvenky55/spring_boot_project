package com.aihelpdeskip.monitoringservice.processors;

public class TicketProcessorES extends TicketProcessorEU {

    // Variables where legal entities must be defined
    private final String[] availableLegalEntities = {
        "ES03 CARTONAJES UNION",
        "ES05 CARTONAJES INTERNATIONAL",
        "ES10 MADRID, SPAIN RECYCLED CNBD MILL ELIM",
        "ES08 IP CONTAINER HOLDINGS SPAIN S.L."
    };

    public String getInvoicesLabel(){
        return "Factura(s)";
    }

    public String[] getLegalEntities(){
        return availableLegalEntities;
    }
}
