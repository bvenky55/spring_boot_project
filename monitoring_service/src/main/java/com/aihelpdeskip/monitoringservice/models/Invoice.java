package com.aihelpdeskip.monitoringservice.models;

import java.util.Date;
import java.util.Objects;

import javax.persistence.*;

import com.aihelpdeskip.monitoringservice.deserializers.InvoiceDeserializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Entity
@Table(name = "invoice")
@JsonDeserialize(using = InvoiceDeserializer.class)
public class Invoice {

    @Id
    private long id;

    protected String number;
    protected int attachment;
    private String vendor;
    private String category = "";
    private String message = "";

    @Column(name="number_confidence")
    private float numberConfidence;
    protected Date date;

    @Column(name="date_confidence")
    private float dateConfidence;

    @Column(name="page_found")
    private byte pageFound;

    @Column(name="functional_area")
    private String functionalArea;

    @Column(name="integrity")
    private String integrity = "A";

    // The attachment file is optional
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="file")
    @JsonIgnore
    private AttachmentFile file;

    // The invoice must belong to a ticket
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="ticket", nullable = false)
    @JsonIgnore
    protected Ticket ticket;

    @Override
    public String toString() {
        return "Invoice{" +
                "id='" + id + '\'' +
                ", number='" + number + '\'' +
                ", attachment=" + attachment +
                ", date=" + date +
                ", pageFound=" + pageFound +
                ", ticket(number)=" + ticket.getNumber() +
                '}';
    }

    @Override
    public int hashCode() {
        return Math.abs(Objects.hash(number, attachment, date, pageFound, ticket));
    }

    public Invoice() { }

    public Invoice(String number, Date date, Byte pageFound, AttachmentFile file) {
        this.number = number;
        this.date = date;

        this.pageFound = pageFound;

        this.file = file;
    }

    public Invoice(String number, Date date, Byte pageFound) {
        this.number = number;
        this.date = date;

        this.pageFound = pageFound;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Byte getPageFound() {
        return pageFound;
    }

    public void setPageFound(Byte pageFound) {
        this.pageFound = pageFound;
    }

    public AttachmentFile getFile() {
        return file;
    }

    public void setFile(AttachmentFile file) {
        this.file = file;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getInvoiceId() {
        return id;
    }

    public void setPageFound(byte pageFound) {
        this.pageFound = pageFound;
    }

    public float getNumberConfidence() {
        return numberConfidence;
    }

    public void setNumberConfidence(float numberConfidence) {
        this.numberConfidence = numberConfidence;
    }

    public float getDateConfidence() {
        return dateConfidence;
    }

    public void setAttachmentId(int id){
        this.attachment = id;
    }

    public void setDateConfidence(float dateConfidence) {
        this.dateConfidence = dateConfidence;
    }

    public int getAttachment() {
        return attachment;
    }

    public void setAttachment(int attachment) {
        this.attachment = attachment;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSource(){
        return this.file==null ? "text" : "attachment";
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getFunctionalArea() {
        return functionalArea;
    }

    public void setFunctionalArea(String functionalArea) {
        this.functionalArea = functionalArea;
    }

    public String getIntegrity() {
        return integrity;
    }

    public void setIntegrity(String integrity) {
        this.integrity = integrity;
    }

    public InvoiceDto convertToDto() {
        return new InvoiceDto(this);
    }
}