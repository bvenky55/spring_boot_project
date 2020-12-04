package com.aihelpdeskip.monitoringservice.processors;

public class TicketProcessorFR extends TicketProcessorEU {

    private final String[] availableLegalEntities = {
        "FR03 CHALON",
        "FR05 ARLES",
        "FR06 ESPALY",
        "FR07 SNCO",
        "FR08 SOCIETE GUADELOUP",
        "FR09 BARREZ",
        "FR11 ST AMAND",
        "FR12 CABOURG",
        "FR14 IP FRANCE INC",
        "FR15 IP SA",
        "FR18 COMPTOIR DES BOIS DE BRIEVE SNC",
        "FR19 IP FORET",
        "FR20 IP INDUSTRIE FRNACE",
        "FR22 IP INVESTEMENT FRANCE SA",
        "FR27 IP CONTAINER (FRANCE) HOLDING",
        "FR32 IP PAPIERS DE BUREAUX",
        "FR33 IP CELIMO",
        "FR44 IP FINANCING FRANCE"
    };
    public String[] getLegalEntities(){
        return availableLegalEntities;
    }
    public String getInvoicesLabel(){
        return "Facture(s)";
    }
}
