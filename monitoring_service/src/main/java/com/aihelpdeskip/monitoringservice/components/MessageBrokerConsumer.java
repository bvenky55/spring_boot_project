package com.aihelpdeskip.monitoringservice.components;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.aihelpdeskip.monitoringservice.controllers.TicketsController;
import com.aihelpdeskip.monitoringservice.models.AttachmentFile;
import com.aihelpdeskip.monitoringservice.models.Invoice;
import com.aihelpdeskip.monitoringservice.models.Ticket;
import com.aihelpdeskip.monitoringservice.processors.TicketProcessor;
import com.aihelpdeskip.monitoringservice.repository.AttachmentFileRepository;
import com.aihelpdeskip.monitoringservice.repository.InvoiceRepository;
import com.aihelpdeskip.monitoringservice.repository.TicketRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.transaction.annotation.Transactional;

@Component
public class MessageBrokerConsumer {

    private TicketRepository ticketRepository;
    private AttachmentFileRepository attachmentRepository;
    private InvoiceRepository invoiceRepository;
    private TicketsController ticketController;
    private SimpMessagingTemplate webSocket;

    private SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
    private Logger logger = LogManager.getLogger("ms:"+MessageBrokerConsumer.class.getSimpleName());

    public MessageBrokerConsumer(TicketRepository ticketRepository, AttachmentFileRepository attachmentRepository, InvoiceRepository invoiceRepository, TicketsController ticketController, SimpMessagingTemplate webSocket) {
        this.ticketRepository = ticketRepository;
        this.attachmentRepository = attachmentRepository;
        this.invoiceRepository = invoiceRepository;
        this.ticketController = ticketController;
        this.webSocket = webSocket;
    }

    @RabbitListener(queues = "INVOICE_INFORMATION_EXTRACTED_MONITORING")
    @Transactional
    public void onInvoiceInformationExtracted(String json_information)
            throws JsonProcessingException {
        AttachmentFile attachment = null;
        JsonNode response_json = new ObjectMapper().readTree(json_information);
        ObjectMapper mapper = new ObjectMapper();
        String ticket_number = response_json.get("ticket").asText();
        if (ticket_number.equals("VA"))
            return;
        Iterator<JsonNode> invList = response_json.get("invoices").elements();
        Ticket ticket;

        ticket = ticketRepository.findById(ticket_number).get();
        logger.info("INVOICE_INFORMATION_EXTRACTED: Ticket: "+ticket);
        int lastAttachmentId = 0;
        while (invList.hasNext()) {
            JsonNode invoiceJson = invList.next();
            Invoice invoice;
            JsonNode dateFromVRT = invoiceJson.get("date");
            JsonNode integrity = invoiceJson.get("integrity");
            logger.debug("Date from VRT to parse: " + dateFromVRT);
            Date parsedDate = null;
            logger.debug("dateFromVRT != null: " + (dateFromVRT != null));
            if (dateFromVRT != null && !dateFromVRT.asText().equals("null"))
                try {
                    parsedDate = sdf.parse(dateFromVRT.asText());
                } catch (ParseException ignored) {
                    logger.warn("Unexpected date '"+dateFromVRT.asText()+"' could not be parsed. Null value will be used.");
                }
            String invNo = !invoiceJson.get("number").isNull() ? invoiceJson.get("number").asText() : "";
            Invoice found_invoice = invoiceRepository.findTopByNumberAndDateAndTicket(
                    invNo,
                    parsedDate,
                    ticket);
            logger.info("INVOICE_INFORMATION_EXTRACTED: Invoice: " + found_invoice);
            if (found_invoice == null) {
                invoice = mapper.readValue(invoiceJson.toString(), new TypeReference<Invoice>() {});
                invoice.setTicket(ticket);
                if (integrity != null) {
                    invoice.setIntegrity(integrity.asText());
                }
                if (invoiceJson.has("attachment_id")) {
                    int attachment_id = invoiceJson.get("attachment_id").asInt();
                    try {
                        if (attachment_id != lastAttachmentId) {
                            attachment = ticket.getAttachments().stream().filter((a) -> a.getAttachmentId() == attachment_id).findFirst().orElse(null);
                            lastAttachmentId = attachment_id;
                        }
                        invoice.setFile(attachment);
                        invoice.setAttachment(attachment_id);
                    } catch (Exception e) {
                        logger.warn(
                                "Either the attachment referred by invoice does not exists in database or invoice has not attachment_id set");
                    }
                }
                invoice.setId(invoice.hashCode());
                ticket.setState(Ticket.TicketStatus.EE_PENDING);
                ticket.addOrUpdateInvoice(invoice);
            }
        }
        ticketRepository.save(ticket);
    }

    @RabbitListener(queues = "RESULT_FOUND")
    @Transactional
    public void onResultFound(String json_information, @Header(required = false, name = "x-death") ArrayList<HashMap<String, Object>> xDeathHeader) {
        try {
            logger.info("RESULT_FOUND:"+json_information);
            if (xDeathHeader != null) {
                Long retriesCount = (Long) xDeathHeader.get(0).get("count");
                if (retriesCount > 5) {
                    logger.warn("Message exceeded retry count and will be discarded.");
                    return;
                }
            }
            JsonNode response_json = new ObjectMapper().readTree(json_information);

            String ticket_number = response_json.get("ticket").asText();
            if (ticket_number.equals("VA"))
                return;
            Iterator<JsonNode> invList = response_json.get("invoices").elements();
            if (!invList.hasNext())
                return;

            Ticket ticket = ticketRepository.findByNumber(ticket_number);
            if (!ticket.getState().equals(Ticket.TicketStatus.EE_PENDING) &&
                    !ticket.getState().equals(Ticket.TicketStatus.REVIEW_PENDING) &&
                    !ticket.getState().equals(Ticket.TicketStatus.REPROCESS))
                throw new AmqpRejectAndDontRequeueException("Ticket was not fully saved to DB yet. Its status is "+ticket.getState());
            TicketProcessor processor = TicketProcessor.getInstance(ticket.getLanguage());
            while (invList.hasNext()) {
                JsonNode inv = invList.next();
                String invoice_number = !inv.get("number").isNull() ? inv.get("number").asText() : "";
                JsonNode invoice_date = inv.get("date");
                String invoice_message = inv.get("message").asText();
                String invoiceVendor = inv.get("vendor").asText();
                String ticketClass = inv.get("ticket_class").asText();
                Invoice foundInv;
                logger.debug("Date from VRT to parse: " + invoice_date);
                Date parsedDate = null;
                if (invoice_date != null && !invoice_date.asText().equals("null"))
                    try {
                        parsedDate = sdf.parse(invoice_date.asText());
                    } catch (ParseException ignored) {
                        logger.warn("Unexpected date "+invoice_date.asText()+" could not be parsed. Null value will be used.");
                    }
                foundInv = invoiceRepository.findTopByNumberAndDateAndTicket(invoice_number, parsedDate, ticket);
                foundInv.setMessage(invoice_message);
                foundInv.setVendor(invoiceVendor);

                ticket.setTicketClass(ticketClass);
                String invoice_category = processor.getInvoiceCategory(foundInv);
                String invoiceFunctionalArea = processor.getFunctionalArea(foundInv);
                String referenceNumber = processor.getReferenceNumber(ticket.getInvoices().size(), foundInv);
                String legalEntity = processor.getLegalEntity(invoice_message);

                foundInv.setCategory(invoice_category);
                foundInv.setFunctionalArea(invoiceFunctionalArea);
                ticket.setReferenceNumber(referenceNumber);
                ticket.setLegalEntity(legalEntity);
                notifyNewInvoice(foundInv);
                ticket.addOrUpdateInvoice(foundInv);
            }
            ticketController.generateTicketMessageAndState(ticket, processor);
            Map<String, Integer> functionalAreasCounter = new HashMap<>();
            for (Invoice inv : ticket.getInvoices()) {
                if(functionalAreasCounter.containsKey(inv.getFunctionalArea())) {
                    functionalAreasCounter.put(inv.getFunctionalArea(), functionalAreasCounter.get(inv.getFunctionalArea()) + 1);
                }
                else {
                    functionalAreasCounter.put(inv.getFunctionalArea(), 1);
                }
            }
            String ticketFinalFA = "";
            Map.Entry<String, Integer> maxEntry = null;
            for (Map.Entry<String, Integer> entry : functionalAreasCounter.entrySet()) {
                if (maxEntry == null || entry.getValue() > maxEntry.getValue()) {
                    ticketFinalFA = entry.getKey();
                }
            }
            ticket.setFinalFunctionalArea(ticketFinalFA);
            logger.info("Notification about new ticket is being sent; content: "+ticket);
            notifyNewTicket(ticket);
            ticketRepository.save(ticket);
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException(e);
        }
    }

    @RabbitListener(queues = "TICKET_RECEIVED_MONITORING", containerFactory = "rabbitListenerContainerFactory")
    @Transactional
    public void onTicketReceived(String json_information) throws JsonProcessingException {
        JsonNode response_json = new ObjectMapper().readTree(json_information);
        logger.info("TICKET_RECEIVED: "+ response_json.get("ticket_number").asText() + ": "+ response_json.get("ticket_title").asText());
        ObjectMapper mapper = new ObjectMapper();
        Ticket ticket = mapper.readValue(json_information, new TypeReference<Ticket>() {});
        logger.info("Ticket: "+ticket);
        if(ticket.getAttachments().size()>0){
            try {
                for (AttachmentFile attachment_file : ticket.getAttachments()) {
                    try {
                        Set<Invoice> invoices = new HashSet<>();
                        List<Invoice> attachment_invoices = invoiceRepository.findByAttachment(attachment_file.getAttachmentId());
                        for (Invoice invoice : attachment_invoices) {
                            if (invoice.getFile() == null) {
                                invoice.setFile(attachment_file);
                            }
                            invoices.add(invoice);
                        }
                        attachment_file.setInvoices(invoices);
                    } catch(Exception e){
                        logger.warn(e.getMessage());
                    }

                    try {
                        AttachmentFile att = attachmentRepository.findByAttachment(attachment_file.getAttachmentId()).get(0);
                        ticket.removeAttachment(att);
                    } catch(Exception ignored){
                    }
                }
            } catch(Exception e){
                logger.warn(e.getMessage());
            }
        }
        logger.info("TICKET_RECEIVED: Ticket to be saved to DB: "+ticket);
        ticketRepository.save(ticket);
    }


    private void notifyWebSocket(String channel, Map<String, Object> hash_map) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(hash_map);
        webSocket.convertAndSend(channel, json);
    }

    private void notifyNewTicket(Ticket ticket) throws JsonProcessingException {
        Map<String, Object> data = new HashMap<>();
        data.put( "type", "ticket" );
        data.put( "ticket_number", ticket.getNumber() );
        notifyWebSocket("/topic/public", data);
    }

    private void notifyNewInvoice(Invoice invoice) throws JsonProcessingException {
        AttachmentFile file = invoice.getFile();
        Map<String, Object> data = new HashMap<>();
        data.put( "type", "invoice" );
        data.put( "ticket_number",
                invoice.getTicket() != null ? invoice.getTicket().getNumber() : "");
        data.put( "invoice_number",  invoice.getNumber() );
        data.put( "invoice_date", invoice.getDate()!=null ? new SimpleDateFormat("MM/dd/yyyy").format(invoice.getDate()) : "");
        data.put( "file_url", file != null ? file.getUrl().getHref() : "" );
        notifyWebSocket("/topic/public", data);
    }
}