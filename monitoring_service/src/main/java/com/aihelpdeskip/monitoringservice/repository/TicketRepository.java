package com.aihelpdeskip.monitoringservice.repository;

import java.util.Date;
import java.util.List;

import com.aihelpdeskip.monitoringservice.models.Ticket;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.PathVariable;

@RepositoryRestResource(collectionResourceRel = "ticket", path = "tickets")
public interface TicketRepository extends PagingAndSortingRepository<Ticket, String>
{
    Page<Ticket> findByState(
            @PathVariable("state") Ticket.TicketStatus state,
            Pageable pageable
    );

    Page<Ticket> findByStateAndTicketClass(
            @PathVariable("state") Ticket.TicketStatus state,
            String ticketClass,
            Pageable pageable
    );

    Page<Ticket> findByStateAndLanguageIn(
            @PathVariable("state") Ticket.TicketStatus state,
            List<String> languages,
            Pageable pageable
    );

    Page<Ticket> findByStateAndTicketClassAndLanguageIn(
            @PathVariable("state") Ticket.TicketStatus state,
            String ticketClass,
            List<String> languages,
            Pageable pageable
    );

    Ticket findByNumber(String number);

    Ticket findByLanguage(String language);

    Long countByState(Ticket.TicketStatus state);

    Long countByStateAndQ2rUpdatedTimeGreaterThanEqualAndQ2rUpdatedTimeLessThanEqual(Ticket.TicketStatus state, Date startDate, Date endDate);

    Long countByLanguage(String language);

    Long countByLanguageAndQ2rUpdatedTimeGreaterThanEqualAndQ2rUpdatedTimeLessThanEqual(String language, Date startDate, Date endDate);

    Long countByStateAndLanguageAndQ2rUpdatedTimeGreaterThanEqualAndQ2rUpdatedTimeLessThanEqual(Ticket.TicketStatus state, String language, Date startDate, Date endDate);

    Long countByQ2rUpdatedTimeGreaterThanEqualAndQ2rUpdatedTimeLessThanEqual(Date startDate, Date endDate);

    @Query("SELECT DATE(q2r_updated_time), count(*) from Ticket where DATE(q2r_updated_time) BETWEEN ?1 AND ?2 GROUP BY DATE(q2r_updated_time) ORDER BY DATE(q2r_updated_time)")
    List<Object> countsByCreationDate(Date from, Date to);

    @Query("SELECT DATE(modified_time), count(*) from Ticket where DATE(modified_time) BETWEEN ?1 AND ?2 GROUP BY DATE(modified_time) ORDER BY DATE(modified_time)")
    List<Object> countsByModifiedDate(Date from, Date to);

    @Query(value="SELECT AVG(q.count) from (SELECT DATE(q2r_updated_time) as date, count(*) as count from ticket where DATE(q2r_updated_time) GROUP BY DATE(q2r_updated_time) ORDER BY DATE(q2r_updated_time)) q", nativeQuery=true)
    Long countByCreationDateByDay();

    @Query(value="SELECT AVG(q.count) from (SELECT WEEK(q2r_updated_time) as date, count(*) as count from ticket where WEEK(q2r_updated_time) GROUP BY WEEK(q2r_updated_time) ORDER BY WEEK(q2r_updated_time)) q", nativeQuery=true)
    Long countByCreationDateByWeek();

    @Query(value="SELECT AVG(q.count) from (SELECT MONTH(q2r_updated_time) as date, count(*) as count from ticket where MONTH(q2r_updated_time) GROUP BY MONTH(q2r_updated_time) ORDER BY MONTH(q2r_updated_time)) q", nativeQuery=true)
    Long countByCreationDateByMonth();

    @Query(value="SELECT AVG(q.count) from (SELECT DATE(modified_time) as date, count(*) as count from ticket where DATE(modified_time) GROUP BY DATE(modified_time) ORDER BY DATE(modified_time)) q", nativeQuery=true)
    Long countByModifiedDateByDay();

    @Query(value="SELECT AVG(q.count) from (SELECT WEEK(modified_time) as date, count(*) as count from ticket where WEEK(modified_time) GROUP BY WEEK(modified_time) ORDER BY WEEK(modified_time)) q", nativeQuery=true)
    Long countByModifiedDateByWeek();

    @Query(value="SELECT AVG(q.count) from (SELECT MONTH(modified_time) as date, count(*) as count from ticket where MONTH(modified_time) GROUP BY MONTH(modified_time) ORDER BY MONTH(modified_time)) q", nativeQuery=true)
    Long countByModifiedDateByMonth();

    Long countByManualModified(boolean manual_modified);

    long count();

    List<Ticket> findByNumberIn(List<String> ticketNumbers);
}
