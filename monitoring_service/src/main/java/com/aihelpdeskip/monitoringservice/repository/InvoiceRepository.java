package com.aihelpdeskip.monitoringservice.repository;

import java.util.Date;
import java.util.List;

import com.aihelpdeskip.monitoringservice.models.Invoice;
import com.aihelpdeskip.monitoringservice.models.Ticket;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import org.springframework.data.jpa.repository.Query; 
import org.springframework.data.repository.query.Param; 


@RepositoryRestResource(exported = false)
public interface InvoiceRepository extends JpaRepository<Invoice, Long>
{
    List<Invoice> findByAttachment(int id);

    Invoice findByNumberAndTicket(String number, Ticket ticket);

    List<Invoice> findAllByNumberAndDateAndTicket(String number, Date date, Ticket ticket);
    Invoice findByNumberAndDateAndTicket(String number, Date date, Ticket ticket);
    Invoice findTopByNumberAndDateAndTicket(String number, Date date, Ticket ticket);

    Invoice findByNumberAndDateAndAttachmentAndTicket(String number, Date date, int attachment, Ticket ticket);

    boolean existsInvoiceByTicketAndMessage(Ticket ticket, String message);

    List<Invoice> findByTicketOrderByCategoryAsc(Ticket ticket);

    List<Invoice> findByNumber(String number);

    Long countByAttachmentNotNull();

    @Query(value = "SELECT COUNT(*) FROM invoice WHERE attachment!=0 AND ticket in (SELECT number FROM ticket WHERE state='ACCEPTED')", nativeQuery = true)
    Long countPdfAttachments();

    @Query(value = "SELECT COUNT(*) FROM invoice WHERE attachment=0 AND ticket in (SELECT number FROM ticket WHERE state='ACCEPTED')", nativeQuery = true)
    Long countTextAttachments();

    Long countByAttachmentIsNull();

    Long countByFunctionalArea(String functionalArea);

    Long countByFunctionalAreaAndDateGreaterThanEqualAndDateLessThanEqual(String functionalArea, Date startDate, Date endDate);

    Long countByTicketAndIntegrity(Ticket ticket, String integrity);

    Long countByTicket(Ticket ticket);

    @Query(value = "SELECT COUNT(*) FROM invoice WHERE integrity=:integrity AND ticket in (SELECT number FROM ticket WHERE state='ACCEPTED')", nativeQuery = true)
    Long countCorrectValues(@Param("integrity") String integrity);

    @Query(value = "SELECT COUNT(*) FROM invoice i WHERE integrity=:integrity AND ticket in (SELECT number FROM ticket WHERE state='ACCEPTED') AND NOT EXISTS (select 1 from invoice i2 where i.attachment=i2.attachment and i2.attachment!=0 and i.page_found=i2.page_found and i2.integrity='A')", nativeQuery = true)
    Long countRealCorrectValues(@Param("integrity") String integrity);

    @Query(value = "SELECT COUNT(*) FROM invoice WHERE integrity=:integrity AND attachment!=0 AND ticket in (SELECT number FROM ticket WHERE state='ACCEPTED')", nativeQuery = true)
    Long countCorrectValuesPDF(@Param("integrity") String integrity);

    @Query(value = "SELECT COUNT(*) FROM invoice i WHERE integrity=:integrity AND attachment!=0 AND ticket in (SELECT number FROM ticket WHERE state='ACCEPTED') AND NOT EXISTS (select 1 from invoice i2 where i.attachment=i2.attachment and i.page_found=i2.page_found and i2.integrity='A')", nativeQuery = true)
    Long countRealCorrectValuesPDF(@Param("integrity") String integrity);

    @Query(value = "SELECT COUNT(*) FROM invoice WHERE integrity=:integrity AND attachment=0 AND ticket in (SELECT number FROM ticket WHERE state='ACCEPTED')", nativeQuery = true)
    Long countCorrectValuesText(@Param("integrity") String integrity); 
}
