package com.aihelpdeskip.monitoringservice.deserializers;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

import com.aihelpdeskip.monitoringservice.models.AttachmentFile;
import com.aihelpdeskip.monitoringservice.models.Ticket;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;


public class TicketDeserializer extends StdDeserializer<Ticket> {
    private static final long serialVersionUID = 1L;
    public TicketDeserializer() {
        this(null);
    }
    public TicketDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Ticket deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);
        String number = node.get("ticket_number").asText();
        String subject = node.get("ticket_title").asText();
        String date_str = node.get("updated").asText();
        String created_date_str = node.get("created").asText();
        String description = node.get("description").asText();
        String language = node.get("language").asText();
        String ticketClass = node.get("ticket_class").asText();

        Date date = new Date();
        try {
            date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").parse(date_str);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Date createdDate = new Date();
        try {
            createdDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").parse(created_date_str);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Ticket ticket = new Ticket(
                number,
                subject,
                createdDate,
                date,
                language,
                description,
                ticketClass,
                Ticket.TicketStatus.TP_PENDING
        );
        ArrayNode attachments = (ArrayNode)node.get("files");
        if(attachments.isArray()) {
            for(JsonNode attachment : attachments) {
                String filename = attachment.get("filename").asText();
                String file_url = attachment.get("file_url").asText();
                int attachment_id = attachment.get("id").asInt();
                byte[] content = Base64.getMimeDecoder().decode(attachment.get("content").asText().getBytes());
                AttachmentFile attachment_file = new AttachmentFile(
                        attachment_id,
                        filename,
                        file_url,
                        content
                );
                ticket.addAttachment(attachment_file);
            }
        }
        return ticket;
    }
}