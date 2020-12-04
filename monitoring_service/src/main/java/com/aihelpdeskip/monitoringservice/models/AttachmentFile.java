package com.aihelpdeskip.monitoringservice.models;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

import com.aihelpdeskip.monitoringservice.controllers.AttachmentFilesController;
import com.fasterxml.jackson.annotation.JsonIgnore;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;


@Entity
@Table(name = "attachment_file")
public class AttachmentFile {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    protected int id;

    @Column(name="name", nullable=false)
    private String name;

    @Column(name="attachment", nullable=false)
    protected int attachment;

    @Column(name="contents", nullable=false)
    @JsonIgnore
    @Lob
    private byte[] contents;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="ticket", nullable = false)
    @JsonIgnore
    private Ticket ticket;

    @OneToMany(mappedBy="file", cascade = CascadeType.ALL, orphanRemoval=true)
    private Set<Invoice> invoices = new HashSet<>();

    public AttachmentFile() {

    }

    public AttachmentFile(int attachment_id, String filename, String file_url, byte[] content) {
        this.attachment = attachment_id;
        this.name = filename;
        this.contents = content;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getContents() {
        return contents;
    }

    public void setContents(byte[] contents) {
        this.contents = contents;
    }

    public int getId() {
        return id;
    }

    public int getAttachmentId(){
        return attachment;
    }

    public void setAttachmentId(int id){
        this.attachment = id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public Link getUrl(){
        return WebMvcLinkBuilder.linkTo(AttachmentFilesController.class).slash("resources").slash(this.id).withSelfRel();
    }

    public Set<Invoice> getInvoices() {
        return invoices;
    }

    public void setInvoices(Set<Invoice> invoiceList) {
        this.invoices = invoiceList;
    }

    public void addInvoice(Invoice invoice){
        this.invoices.add(invoice);
        invoice.setFile(this);
    }

    public int getResourceId(){
        return this.id;
    }

}