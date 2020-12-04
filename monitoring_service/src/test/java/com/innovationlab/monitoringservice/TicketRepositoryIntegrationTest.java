package com.innovationlab.monitoringservice;

import static org.assertj.core.api.Assertions.assertThat;

import com.aihelpdeskip.monitoringservice.MonitoringServiceApplication;
import com.aihelpdeskip.monitoringservice.models.Ticket;
import com.aihelpdeskip.monitoringservice.repository.TicketRepository;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = MonitoringServiceApplication.class)
@SpringBootTest
class TicketRepositoryIntegrationTest {
    
    private TicketRepository ticketRepository;

    @Autowired
    public void TicketRepositoryClass(TicketRepository ticketRepository){
        this.ticketRepository = ticketRepository;
    }

    @Test
    public void shouldSaveTicket() {
        Ticket newItem = new Ticket();
        newItem.setNumber("ABC123");

        ticketRepository.save(newItem);
        
        Ticket savedItem = ticketRepository.findByNumber(newItem.getNumber());

        assertThat(savedItem.getNumber())
            .isEqualTo(newItem.getNumber());
    }

}