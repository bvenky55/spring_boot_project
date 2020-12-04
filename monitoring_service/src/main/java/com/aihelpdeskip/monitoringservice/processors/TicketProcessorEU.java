package com.aihelpdeskip.monitoringservice.processors;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.aihelpdeskip.monitoringservice.models.Invoice;

public class TicketProcessorEU extends TicketProcessor {

    final String messageA = "Clearing:";
    final String messageB = "No Records Found";
    final String messageC = "Block:  A";
    final String messageD = "No Clearing details";
    
    final String messageM1 = "Contracted Services Audit";
    final String messageM2 = "Direct Debit - Manual Process";
    final String messageM3 = "Vendor Balances Error";
    final String messageM4 = "Check has NOT been cashed";
    final String messageM5 = "Status Clearing:";
    final String messageM6 = "Block:  A  User:";
   
    private final String[] availableCategories = { "A1", "A2", "B", "C1", "C2", "D1", "D2", "F" };

    public String[] getAvailableCategories(){
        return availableCategories;
    }

    public String getReferenceNumber(int invoiceCount, Invoice invoice){

        String referenceNumber;

        if (invoiceCount > 1)
            referenceNumber = "MULTI";
        else
            referenceNumber = invoice.getNumber();

        return referenceNumber;
    }
    public String getInvoiceCategory( Invoice invoice ){
        String message = invoice.getMessage();
        int categoryIndex = 7;
        if(message.contains(messageA)) {
            categoryIndex = 0;
            if(invoice.getTicket().getInvoices().size()>=2)
                categoryIndex = 1;
        } else if(message.contains(messageB))  {
            if(invoice.getDate()!=null){
                long invoice_date = invoice.getDate().getTime();
                long ticket_date = invoice.getTicket().getQ2rUpdatedTime().getTime();
                
                long millies_diff = Math.abs(invoice_date - ticket_date);
                long days_diff = TimeUnit.DAYS.convert(millies_diff, TimeUnit.MILLISECONDS);
                
                if(days_diff >= 30){
                    categoryIndex = 2;
                } else {
                    categoryIndex = 7;
                }
            }
        } else if(message.contains(messageC)) {
            categoryIndex = 3;
            if(invoice.getTicket().getInvoices().size()>=2)
                categoryIndex = 4;
        } else if(message.contains(messageD)) {
            categoryIndex = 5;
            if(invoice.getTicket().getInvoices().size()>=2)
            categoryIndex = 6;
        }
        return availableCategories[categoryIndex];
    }

    public String getFunctionalArea( Invoice invoice ){
        String message = invoice.getMessage();
        int functionalAreaIndex = 4;
        if(
            message.contains(messageM1)
            || message.contains(messageM2)
            || message.contains(messageM3)
            || message.contains(messageM4)
            || message.contains(messageM5)
            || message.contains(messageM6)
        )
            functionalAreaIndex = 4;
        else if(message.contains(messageA) || message.contains(messageD))
            functionalAreaIndex = 0;
        else if(message.contains(messageB))
            functionalAreaIndex = 2;
        else if(message.contains(messageC))
            functionalAreaIndex = 3;

        return availableFunctionalAreas[functionalAreaIndex];
    }

    public String replaceInTemplate(String template, String category, List<Invoice> invoiceList){
        if(
            category.equals("A1") || 
            category.equals("A2") || 
            category.equals("D1") ||
            category.equals("D2")
        ) {
            List<String> messagesList = invoiceList.stream()
                .map(Invoice::getMessage)
                .collect(Collectors.toList());

            List<String> dates = new ArrayList<>();

            for (String message : messagesList) {
                Pattern p = Pattern.compile("(\\d{2}/\\d{2}/\\d{4})");
                Matcher m = p.matcher(message);
                if (m.find())
                    dates.add(m.group(1));

            }
            return template.replace("{}", String.join(", ", dates));
        } else if(
            category.equals("B") || 
            category.equals("C1")
        ) {
            List<String> numbers = invoiceList.stream()
                .map(Invoice::getNumber)
                .collect(Collectors.toList());
                return template.replace("{}", String.join(", ", numbers));
        }
        return template;
    }
}
