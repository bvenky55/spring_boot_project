package com.aihelpdeskip.monitoringservice.processors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.aihelpdeskip.monitoringservice.models.Invoice;

public class TicketProcessor {
    private String[] availableLegalEntities;
    private final String LEGAL_ENTITY_REGEX = "CC#(.{2}\\d{2})";

    protected final String messageA = "Clearing";
    protected final String messageB = "Please view FileNet and Pins for more detail. FileNet Status: Active";
    protected final String messageC = "Invoice found in FileNet but No Pins created. FileNet Status: Active";
    protected final String messageD = "No Records Found";
    protected final String messageE = "VRT skipped - missing information";
    protected final String messageF1 = "Please view FileNet and Pins for more detail. FileNet Status: Completed";
    protected final String messageF2 = "Invoice found in FileNet but No Pins created. FileNet Status: Completed";
    protected final String messageF3 = "items displayed - Please process manually";
    protected final String messageF4 = "Block";
    protected final String messageG = "Contracted Services Audit";

    private final String[] availableCategories = { "A", "B", "C", "D", "E", "F", "G" };
    
    public static TicketProcessor getInstance( String language ){
        switch (language) {
            case "fra":
                return new TicketProcessorFR();
            case "ita":
                return new TicketProcessorIT();
            case "spa":
                return new TicketProcessorES();
            default:
                return new TicketProcessor();
        }
    }

    final String[] availableFunctionalAreas = {
        "Invoice status provided",
        "Invoice awaiting processing",
        "Invoice missing",
        "Invoice blocked",
        ""};

    public String getInvoicesLabel(){
        return "Invoice(s)";
    }

    public String getInvoiceCategory( Invoice invoice ){
        String message = invoice.getMessage();
        int categoryIndex = 5;

        if (message.contains(messageG))
            categoryIndex = 6;
        else if (message.contains(messageA))
            categoryIndex = 0;
        else if (message.contains(messageB))
            categoryIndex = 1;
        else if (message.contains(messageC))
            categoryIndex = 2;
        else if (message.contains(messageD))
            categoryIndex = 3;

        return availableCategories[categoryIndex];
    }

    public String getFunctionalArea(Invoice invoice){
        String message = invoice.getMessage();
        int functionalAreaIndex = 4;

        if(message.contains(messageA))
            functionalAreaIndex = 0;
        else if(message.contains(messageB) || message.contains(messageC))
            functionalAreaIndex = 1;
        else if(message.contains(messageD))
            functionalAreaIndex = 2;
        else if(message.contains(messageF3) || message.contains(messageF4))
            functionalAreaIndex = 3;
        else if(message.contains(messageG))
            functionalAreaIndex = 0;

        return availableFunctionalAreas[functionalAreaIndex];
    }

    public String getReferenceNumber(int invoiceCount, Invoice invoice){

        return "";
    }

    public String[] getLegalEntities(){
        return availableLegalEntities;
    }

    public String getLegalEntity(String message){
        String[] legalEntities = getLegalEntities();

        if (legalEntities == null)
            return "";

        Pattern p = Pattern.compile( LEGAL_ENTITY_REGEX );
        Matcher m = p.matcher(message);
        if (m.find()){
            String extracted_legal_entity = m.group(1);
            for (String legal_entity : legalEntities) {
                if (legal_entity.contains(extracted_legal_entity))
                    return legal_entity;
            }
        }

        return "";
    }

    public String replaceInTemplate(String template, String category, List<Invoice> invoiceList){
        if(category.equals("A")) {
            List<String> vendorList = invoiceList.stream()
                    .map(Invoice::getVendor).sorted().collect(Collectors.toList());

            List<String> vendors = new ArrayList<>();
            String previousVendor = "";

            for (String vendor : vendorList) {
                if (!(vendor.equals(previousVendor))) {
                    vendors.add(vendor);
                    previousVendor = vendor;
                }
            }
            return template.replace("{}", String.join(", ",vendors));
        }
        return template;
    }
}
