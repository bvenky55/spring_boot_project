package com.aihelpdeskip.monitoringservice.models;

import com.aihelpdeskip.monitoringservice.deserializers.TicketDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;


@JsonDeserialize(using = TicketDeserializer.class)
public class TicketDto {

    private String number;
    private String subject;
    private Date q2rCreatedTime;
    private Date q2rUpdatedTime;
    private String language;
    private String vendorMessage;
    private String reply;
    private Ticket.TicketStatus state;

    private boolean manualModified = false;

    private Set<AttachmentFile> attachments = new HashSet<>();

    private Set<InvoiceDto> invoices = new HashSet<>();

    private Date modifiedTime;
    protected User user;

    private String ticketClass;
    private String suggestedClass;
    private String finalFunctionalArea = "";

    private String referenceNumber;
    private String legalEntity;
    private float class_certainty;

    public String toString() {
        return "TicketDto{" +
                "number='" + number + '\'' +
                ", attachments(len)=" + attachments.size() +
                ", invoices(len)=" + invoices.size() +
                ", suggestedClass='" + suggestedClass + '\'' +
                '}';
    }

    TicketDto(Ticket other) {
        this.number = other.getNumber();
        this.subject = other.getSubject();
        this.q2rCreatedTime = other.getQ2rCreatedTime();
        this.q2rUpdatedTime = other.getQ2rUpdatedTime();
        this.language = other.getLanguage();
        this.vendorMessage = other.getVendorMessage();
        this.reply = other.getReply();
        this.state = other.getState();
        this.manualModified = other.getManualModified();
        this.attachments = new HashSet<>();
        this.invoices = new HashSet<>();
        this.modifiedTime = other.getModifiedTime();
        this.user = other.getUser();
        this.ticketClass = other.getTicketClass();
        this.finalFunctionalArea = other.getFinalFunctionalArea();
        this.referenceNumber = other.getReferenceNumber();
        this.legalEntity = other.getLegalEntity();
        other.getInvoices().forEach((invoice -> this.invoices.add(invoice.convertToDto())));
    }

    public String getTicketId(){
        return number;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Date getQ2rUpdatedTime() {
        return q2rUpdatedTime;
    }

    public Date getQ2rCreatedTime() {
        return q2rCreatedTime;
    }

    public String getFormattedDate(){
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(this.q2rUpdatedTime);
    }

    public void setQ2rUpdatedTime(Date q2rUpdatedTime) {
        this.q2rUpdatedTime = q2rUpdatedTime;
    }

    public String getVendorMessage() {
        return vendorMessage;
    }

    public void setVendorMessage(String vendorMessage) {
        this.vendorMessage = vendorMessage;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public Ticket.TicketStatus getState() {
        return state;
    }

    public void setState(Ticket.TicketStatus state) {
        this.state = state;
    }

    public Set<AttachmentFile> getAttachments() {
        return attachments;
    }

    public void setAttachments(Set<AttachmentFile> fileList) {
        this.attachments = fileList;
    }

    public Set<InvoiceDto> getInvoices() {
        return invoices;
    }

    public String getTicketClass() {
        return ticketClass;
    }

    public void setTicketClass(String ticket_class) {
        this.ticketClass = ticket_class;
    }

    public String getSuggestedClass() {
        return suggestedClass;
    }

    public void setSuggestedClass(String suggestedClass) {
        this.suggestedClass = suggestedClass;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getModifiedUser(){
        if(this.user==null){
            return "";
        }
        return this.user.getName();
    }

    public Date getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(Date modifiedtime) {
        this.modifiedTime = modifiedtime;
    }

    public void setFinalFunctionalArea(String finalFunctionalArea) {
        this.finalFunctionalArea = finalFunctionalArea;
    }

    public String getFinalFunctionalArea() {
        return this.finalFunctionalArea;
    }


    public boolean getManualModified(){
        return this.manualModified;
    }

    public void setManualModified(boolean manual_modified){
        this.manualModified = manual_modified;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public String getLegalEntity() {
        return legalEntity;
    }

    public void setLegalEntity(String legalEntity) {
        this.legalEntity = legalEntity;
    }

    public float getClassCertainty() {
        return this.class_certainty;
    }

    public void setClassCertainty(float certainty) {
        this.class_certainty = certainty;
    }
}
