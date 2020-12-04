package com.aihelpdeskip.monitoringservice.controllers;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.aihelpdeskip.monitoringservice.models.*;
import com.aihelpdeskip.monitoringservice.repository.InvoiceRepository;
import com.aihelpdeskip.monitoringservice.repository.TicketRepository;
import com.aihelpdeskip.monitoringservice.repository.UserRepository;
import com.aihelpdeskip.monitoringservice.repository.VRTTemplateRepository;
import com.aihelpdeskip.monitoringservice.processors.TicketProcessor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class TicketsController {

    private final String CATEGORY_SPECIAL_CASE = "A";
    private final String CATEGORY_A2_CASE = "A2";
    private Logger logger = LogManager.getLogger("ms:"+ TicketsController.class.getSimpleName());

    private TicketRepository ticketRepository;
    private InvoiceRepository invoiceRepository;
    private VRTTemplateRepository vrtTemplateRepository;
    private UserRepository userRepository;
    private RabbitTemplate rabbitmqTemplate;

    public TicketsController(TicketRepository ticketRepository, InvoiceRepository invoiceRepository, VRTTemplateRepository vrtTemplateRepository, UserRepository userRepository, RabbitTemplate rabbitmqTemplate) {
        this.ticketRepository = ticketRepository;
        this.invoiceRepository = invoiceRepository;
        this.vrtTemplateRepository = vrtTemplateRepository;
        this.userRepository = userRepository;
        this.rabbitmqTemplate = rabbitmqTemplate;
    }

    @PostMapping("tickets/{id}/approve")
    @Transactional
    public TicketDto acceptTicket(@PathVariable("id") String id) {
        Ticket ticket = ticketRepository.findById(id).get();
        ticket.setUser(getCurrentUser());
        ticket.setModifiedTime(new Date());
        ticket.setState(Ticket.TicketStatus.ACCEPTED);
        ticketRepository.save(ticket);
        return ticket.convertToDto();
    }

    @PostMapping("tickets/{id}/reject")
    @Transactional
    public TicketDto rejectTicket(@PathVariable("id") String id) {
        Ticket ticket = ticketRepository.findById(id).get();
        ticket.setState(Ticket.TicketStatus.REJECTED);
        ticket.setUser(getCurrentUser());
        ticket.setModifiedTime(new Date());
        ticketRepository.save(ticket);
        return ticket.convertToDto();
    }

    @PostMapping("tickets/{id}/outofscope")
    @Transactional
    public TicketDto outOfScopeTicket(@PathVariable("id") String id) {
        Ticket ticket = ticketRepository.findById(id).get();
        ticket.setState(Ticket.TicketStatus.OUT_OF_SCOPE);
        ticket.setUser(getCurrentUser());
        ticket.setModifiedTime(new Date());
        ticketRepository.save(ticket);
        return ticket.convertToDto();
    }

    @PostMapping("tickets/{id}/reprocess")
    @Transactional
    public TicketDto reprocessTicket(@PathVariable("id") String id) {
        Ticket ticket = ticketRepository.findById(id).get();
        ticket.setState(Ticket.TicketStatus.REPROCESS);
        ticket.setManualModified(true);
        ticketRepository.save(ticket);
        return ticket.convertToDto();
    }

    @PostMapping("tickets/approve")
    public List<TicketDto> bulkApproveTickets(@RequestParam("tickets") List<String> tickets) {
        List<TicketDto> ticketList = new ArrayList<>();
        for(String id : tickets) {
            ticketList.add(acceptTicket(id));
        }
        return ticketList;
    }

    @PostMapping("tickets/reject")
    public List<TicketDto> bulkRejectTickets(@RequestParam("tickets") List<String> tickets) {
        List<TicketDto> ticketList = new ArrayList<>();
        for(String id : tickets) {
            ticketList.add(rejectTicket(id));
        }
        return ticketList;
    }

    @PostMapping("tickets/reprocess")
    public List<TicketDto> bulkReprocessTickets(@RequestParam("tickets") List<String> tickets) {
        List<TicketDto> ticketList = new ArrayList<>();
        for(String id : tickets) {
            ticketList.add(reprocessTicket(id));
        }
        return ticketList;
    }

    @PostMapping("tickets/{id}/invoices/{invoice_id}")
    @Transactional
    public Invoice updateInvoice(
            @PathVariable("id") String id,
            @PathVariable("invoice_id") long invoice_id,
            @RequestBody String raw_json) throws JsonProcessingException {
        JsonNode response_json = new ObjectMapper().readTree(raw_json);
        Ticket ticket = ticketRepository.findById(id).get();
        Invoice invoice = invoiceRepository.findById(invoice_id).get();
        Date invoiceDate;
        try {
            invoiceDate = new SimpleDateFormat("MM/dd/yyyy").parse(response_json.get("date").asText());
        } catch (Exception e) {
            logger.error("Error parsing date using format MM/dd/yyyy: " + e.getLocalizedMessage());
            invoiceDate = null;
        }
        invoice.setNumber(response_json.get("number").asText());
        invoice.setMessage("");
        invoice.setDate(invoiceDate);
        invoice.setPageFound((byte) response_json.get("page").asInt());
        if(invoice.getIntegrity()==null)
            invoice.setIntegrity("AF");
        if(!invoice.getIntegrity().equals("AE"))
            invoice.setIntegrity("AF");
        invoiceRepository.save(invoice);
        TicketProcessor processor = TicketProcessor.getInstance(ticket.getLanguage());
        generateTicketMessageAndState(ticket, processor);
        ticketRepository.save(ticket);

        return invoiceRepository.findById(invoice_id).get();
    }

    @DeleteMapping("tickets/{id}/invoices/{invoice_id}")
    @Transactional
    public Invoice deleteInvoice(
            @PathVariable("id") String id,
            @PathVariable("invoice_id") long invoice_id) {
        Ticket ticket = ticketRepository.findById(id).get();
        Invoice invoice = invoiceRepository.findById(invoice_id).get();
        ticket.removeInvoice(invoice);
        TicketProcessor processor = TicketProcessor.getInstance(ticket.getLanguage());
        generateTicketMessageAndState(ticket, processor);
        ticketRepository.save(ticket);
        return invoice;
    }

    @PostMapping("/tickets/common")
    @Transactional
    public List<String> getCommonTickets(@RequestBody List<String> ticketsFromQ2R) {
        List<Ticket> commonTickets = ticketRepository.findByNumberIn(ticketsFromQ2R);
        List<String> commonTicketsIDs = new ArrayList<>();
        commonTickets.forEach((ticket -> commonTicketsIDs.add(ticket.getNumber())));
        return commonTicketsIDs;
    }

    public void generateTicketMessageAndState(Ticket ticket, TicketProcessor processor ){
        StringBuilder emailBody = new StringBuilder();
        List<Invoice> invoiceList = invoiceRepository.findByTicketOrderByCategoryAsc(ticket);
        List<String> categories = invoiceList.stream()
                .map(Invoice::getCategory)
                .distinct()
                .collect(Collectors.toList());
        for (String category : categories ) {
            String finalCategory = category;
            List<Invoice> invoices = invoiceList.stream()
                    .filter( invoice -> invoice.getCategory().equals(category))
                    .collect(Collectors.toList());
            if (category.equals(CATEGORY_SPECIAL_CASE))
                finalCategory = verifyVendor(invoices);

            if (invoices.size() > 0) {
                emailBody.append(generateMessageByCategory(
                        invoices,
                        finalCategory,
                        ticket.getLanguage(),
                        processor
                ));
            }
        }
        if (emailBody.length() > 0) {
            ticket.setReply(emailBody.toString());
        }
        ticket.setState(Ticket.TicketStatus.REVIEW_PENDING);
    }

    private String generateMessageByCategory(List<Invoice> invoiceList, String category, String language, TicketProcessor processor) {
        String emailBody;
        List<String> invoices_numbers_list = invoiceList.stream()
                .map(Invoice::getNumber)
                .collect(Collectors.toList());
        String invoices_numbers = String.join(", ", invoices_numbers_list);
        VRTTemplate template = vrtTemplateRepository.findByTemplateIdAndLanguage(category, language);
        emailBody =  String.format("%s: %s <br><br>%s<br><br>%s<br><br>",
                processor.getInvoicesLabel(),
                invoices_numbers,
                processor.replaceInTemplate(
                        template != null ? template.getBody() : "",
                        category,
                        invoiceList
                ),
                new String(new char[100]).replace("\0", "-")
        );

        return emailBody;
    }

    public String verifyVendor(List<Invoice> invoices) {
        String category = CATEGORY_SPECIAL_CASE;
        if (invoices != null && invoices.size() > 0) {
            String firstVendor = invoices.get(0).getVendor();
            for (Invoice invoice : invoices) {
                if (!(firstVendor.equals(invoice.getVendor()))) {
                    category = CATEGORY_A2_CASE;
                    break;
                }
            }
        }
        return category;
    }


    public User getCurrentUser(){
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        User user = null;
        if (authentication != null) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String userName = userDetails.getUsername();
            Optional<User> found_user = userRepository.findByUsername( userName );
            if (found_user.isPresent()){
                user = found_user.get();
            }
        }
        return user;
    }

    private void notifyMessageBroker(String exchange, String routing_key, String message){
        rabbitmqTemplate.convertAndSend(exchange, routing_key, message);
    }
}