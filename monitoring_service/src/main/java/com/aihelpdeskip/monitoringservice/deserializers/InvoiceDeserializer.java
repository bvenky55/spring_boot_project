package com.aihelpdeskip.monitoringservice.deserializers;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.aihelpdeskip.monitoringservice.models.Invoice;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;


public class InvoiceDeserializer extends StdDeserializer<Invoice> {
    private static final long serialVersionUID = 1L;
    public InvoiceDeserializer() {
        this(null);
    }
    public InvoiceDeserializer(Class<?> vc) {
        super(vc);
    }
    @Override
    public Invoice deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);

        String invoice_number = !node.get("number").isNull() ? node.get("number").asText() : "";
        String invoice_date_str = node.get("date").asText().replace("[^/\\d-]","");
        float invoice_number_score = 0;
        float invoice_date_score = 0;
        String integrity = null;
        try {
            invoice_number_score = Float.parseFloat(node.get("number_score").asText());
        } catch(Exception ignored){ }
        try {
            invoice_date_score = Float.parseFloat(node.get("date_score").asText());
        } catch(Exception ignored){ }

        byte page_found = 0;

        if(node.has("page_found")){
            page_found = (byte) node.get("page_found").asInt();
        }

        Date invoice_date = null;
        String[] date_formats = {
                "MM/dd/yyyy",
                "MM/dd/yy",
                "MM-dd-yyyy",
                "MM-dd-yy"
        };
        for (String date_format : date_formats) {
            try {
                invoice_date = new SimpleDateFormat(date_format).parse(invoice_date_str);
                break;
            } catch (ParseException ignored) { }
        }


        if(invoice_date==null || invoice_number==null || invoice_number.equals("") || invoice_number.equals("null"))
            integrity = "AE";

        Invoice invoice = new Invoice(
                invoice_number,
                invoice_date,
                page_found
        );
        invoice.setNumberConfidence(invoice_number_score);
        invoice.setDateConfidence(invoice_date_score);
        if(integrity!=null)
            invoice.setIntegrity(integrity);
        if(node.has("attachment_id")){
            int attachment_id = node.get("attachment_id").asInt();
            invoice.setAttachmentId(attachment_id);
        }
        return invoice;
    }
}