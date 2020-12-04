package com.aihelpdeskip.monitoringservice.controllers;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.aihelpdeskip.monitoringservice.models.Ticket;
import com.aihelpdeskip.monitoringservice.repository.InvoiceRepository;
import com.aihelpdeskip.monitoringservice.repository.TicketRepository;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatsController {
    private TicketRepository ticketRepository;
    private InvoiceRepository invoiceRepository;

    public StatsController(TicketRepository ticketRepository, InvoiceRepository invoiceRepository) {
        this.ticketRepository = ticketRepository;
        this.invoiceRepository = invoiceRepository;
    }

    @GetMapping("stats/tickets/status")
    public HashMap<String, Long> countTicketsByStatus(
            @RequestParam(required = false) @DateTimeFormat(pattern="dd-MM-yyyy") Date from,
            @RequestParam(required = false) @DateTimeFormat(pattern="dd-MM-yyyy") Date to
    ) {
        HashMap<String, Long> statistics = new HashMap<>();
        Ticket.TicketStatus[] statuses = Ticket.TicketStatus.values();
        for(Ticket.TicketStatus status : statuses) {
            Long count;
            if ((from == null || to == null))
                count = ticketRepository.countByState(status);
            else {
                count = ticketRepository.countByStateAndQ2rUpdatedTimeGreaterThanEqualAndQ2rUpdatedTimeLessThanEqual(status, from, getLatestHour(to));
            }
            statistics.put(status.toString(), count);
        }
        return statistics;
    }

    @GetMapping("stats/tickets/languages/{language}")
    public Long countTicketsBySpecificLanguage(
            @PathVariable("language") String language,
            @RequestParam(required = false) @DateTimeFormat(pattern="dd-MM-yyyy") Date from,
            @RequestParam(required = false) @DateTimeFormat(pattern="dd-MM-yyyy") Date to
    ) {
        Long count;
        if ((from == null || to == null))
            count = ticketRepository.countByLanguage(language);
        else
            count = ticketRepository.countByLanguageAndQ2rUpdatedTimeGreaterThanEqualAndQ2rUpdatedTimeLessThanEqual(language, from, getLatestHour(to));
        return count;
    }

    @GetMapping("stats/tickets/languages/all")
    public HashMap<String, Long> countTicketsByAllLanguage(
            @RequestParam(required = false) @DateTimeFormat(pattern="dd-MM-yyyy") Date from,
            @RequestParam(required = false) @DateTimeFormat(pattern="dd-MM-yyyy") Date to
    ) {
        HashMap<String, Long> statistics = new HashMap<>();
        String[] languages = {"eng", "spa", "ita", "fra"};
        for (String language : languages) {
            Long count;
            if ((from == null || to == null))
                count = ticketRepository.countByLanguage(language);
            else
                count = ticketRepository.countByLanguageAndQ2rUpdatedTimeGreaterThanEqualAndQ2rUpdatedTimeLessThanEqual(language, from, getLatestHour(to));
            statistics.put(language, count);
        }
        return statistics;
    }

    @GetMapping("stats/tickets/created/historical")
    public List<Object> ticketsCreatedHistorical(
            @RequestParam(required = false) @DateTimeFormat(pattern="dd-MM-yyyy") Date from,
            @RequestParam(required = false) @DateTimeFormat(pattern="dd-MM-yyyy") Date to
    ){
        if(from==null){
            to = new Date();
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.YEAR, -1);
            from = cal.getTime();
        }
        if(to==null || from.compareTo(to)>0)
            to = from;
        return ticketRepository.countsByCreationDate(from, getLatestHour(to));
    }

    @GetMapping("stats/tickets/modified/historical")
    public List<Object> ticketsModifiedHistorical(
            @RequestParam(required = false) @DateTimeFormat(pattern="dd-MM-yyyy") Date from,
            @RequestParam(required = false) @DateTimeFormat(pattern="dd-MM-yyyy") Date to
    ){
        if(from==null){
            to = new Date();
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.YEAR, -1);
            from = cal.getTime();
        }
        if(to==null || from.compareTo(to)>0)
            to = from;
        return ticketRepository.countsByModifiedDate(from, getLatestHour(to));
    }

    @GetMapping("stats/tickets/per_day")
    public Long countTicketsPerDay(Date date, @RequestParam(required = false) Ticket.TicketStatus state, @RequestParam(required = false) String language) {
        Long count;
        Calendar calendar = Calendar.getInstance();
        if(date==null){
            date = new Date();
        }
        calendar.setTime(date);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR, 0);
        Date startDate = calendar.getTime();
        calendar.add(Calendar.DATE, 1);
        Date endDate = calendar.getTime();
        if (state != null) {
            if (language != null) {
                count = ticketRepository.countByStateAndLanguageAndQ2rUpdatedTimeGreaterThanEqualAndQ2rUpdatedTimeLessThanEqual(state, language, startDate, endDate);
            }
            else {
                count = ticketRepository.countByStateAndQ2rUpdatedTimeGreaterThanEqualAndQ2rUpdatedTimeLessThanEqual(state, startDate, endDate);
            }
        }
        else {
            if (language != null) {
                count = ticketRepository.countByLanguageAndQ2rUpdatedTimeGreaterThanEqualAndQ2rUpdatedTimeLessThanEqual(language, startDate, endDate);
            }
            else {
                count = ticketRepository.countByQ2rUpdatedTimeGreaterThanEqualAndQ2rUpdatedTimeLessThanEqual(startDate, endDate);
            }
        }
        return count;
    }

    @GetMapping("stats/invoices/from_pdf")
    public Long countInvoicesFromPDF() {
        return invoiceRepository.countPdfAttachments();
    }

    @GetMapping("stats/invoices/from_text")
    public Long countInvoicesFromText() {
        return invoiceRepository.countTextAttachments();
    }

    @GetMapping("stats/invoices")
    public HashMap<String, Double> countCorrectInvoices(
            @RequestParam(required = false) @DateTimeFormat(pattern="dd-MM-yyyy") Date from,
            @RequestParam(required = false) @DateTimeFormat(pattern="dd-MM-yyyy") Date to
    ) {
        double totalAccuracy;
        double pdfAccuracy;
        double textAccuracy;
        double a;
        double ae;
        double af;

        a = (double) invoiceRepository.countCorrectValues("A");
        ae = (double) invoiceRepository.countRealCorrectValues("AE");
        af = (double) invoiceRepository.countRealCorrectValues("AF");
        totalAccuracy = (a / (a + ae + af)) * 100;
        totalAccuracy = (double) Math.round(totalAccuracy * 100d) / 100d;

        a = (double) invoiceRepository.countCorrectValuesPDF("A");
        ae = (double) invoiceRepository.countRealCorrectValuesPDF("AE");
        af = (double) invoiceRepository.countRealCorrectValuesPDF("AF");
        pdfAccuracy = (a / (a + ae + af)) * 100;
        pdfAccuracy = (double) Math.round(pdfAccuracy * 100d) / 100d;

        a = (double) invoiceRepository.countCorrectValuesText("A");
        ae = (double) invoiceRepository.countCorrectValuesText("AE");
        af = (double) invoiceRepository.countCorrectValuesText("AF");

        textAccuracy = (a / (a + ae + af)) * 100;
        textAccuracy = (double) Math.round(textAccuracy * 100d) / 100d;

        HashMap<String, Double> statistics = new HashMap<>();
        statistics.put("total_extraction_accuracy", totalAccuracy);
        statistics.put("pdf_extraction_accuracy", pdfAccuracy);
        statistics.put("text_extraction_accuracy", textAccuracy);
        return statistics;
    }

    @GetMapping("stats/tickets/avg")
    public HashMap<String, Object> countTicketsAverages() {
        HashMap<String, Object> statistics = new HashMap<>();
        HashMap<String, Long> created = new HashMap<>();
        created.put("day", ticketRepository.countByCreationDateByDay());
        created.put("week", ticketRepository.countByCreationDateByWeek());
        created.put("month", ticketRepository.countByCreationDateByMonth());
        statistics.put("created", created);
        HashMap<String, Long> modified = new HashMap<>();
        modified.put("day", ticketRepository.countByModifiedDateByDay());
        modified.put("week", ticketRepository.countByModifiedDateByWeek());
        modified.put("month", ticketRepository.countByModifiedDateByMonth());
        statistics.put("modified", modified);
        return statistics;
    }

    @GetMapping("stats/tickets/reprocessed")
    public Long countTicketsReprocessed() {
        return ticketRepository.countByManualModified(true);
    }

    @GetMapping("stats/invoices/functional_areas/{functional_area}")
    public Long countInvoiceFunctionalArea(
            @PathVariable("functional_area") String functionalArea,
            @RequestParam(required = false) @DateTimeFormat(pattern="dd-MM-yyyy") Date from,
            @RequestParam(required = false) @DateTimeFormat(pattern="dd-MM-yyyy") Date to
    ) {
        return (from == null || to == null) ?
            invoiceRepository.countByFunctionalArea(functionalArea) :
            invoiceRepository.countByFunctionalAreaAndDateGreaterThanEqualAndDateLessThanEqual(functionalArea, from, getLatestHour(to));
    }

    private Date getLatestHour(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.HOUR, 23);
        return calendar.getTime();
    }
}
