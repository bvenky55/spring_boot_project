package com.aihelpdeskip.monitoringservice.models;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

import com.aihelpdeskip.monitoringservice.deserializers.TicketDeserializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.text.SimpleDateFormat;

@Entity
@Table(name="ticket")
@JsonDeserialize(using = TicketDeserializer.class)
public class Ticket {

    public enum TicketStatus {
        TP_PENDING, // When we are waiting for events from Ticket Processor.
        EE_PENDING, // When we are waiting for event the Executive Engine to complete the invoices information
        REVIEW_PENDING, // When the Ticket info is complete and it's waiting for review.
        ACCEPTED, // When the Ticket has been reviewed and approved by a user.
        REJECTED, // When the Ticket has been reviewed and rejected by a user.
        REPROCESS,  // When the Ticket has been reviewed and modified by a user.
        OUT_OF_SCOPE // When the Ticket has been reviewed and marked as out of scope by a user.
    }

    @Id
    @Column(columnDefinition = "varchar(63)")
    private String number;
    private String subject;
    @Column(name = "q2r_created_time")
    private Date q2rCreatedTime;
    @Column(name = "q2r_updated_time")
    private Date q2rUpdatedTime;
    @Column(columnDefinition = "char(3)", nullable = false)
    private String language;
    @Lob
    private String vendorMessage;
    @Lob
    private String reply;
    @Enumerated(EnumType.STRING)
    private TicketStatus state;

    @Column(name="manual_modified", columnDefinition = "boolean default false")
    private boolean manualModified = false;

    @OneToMany(mappedBy="ticket", cascade = CascadeType.ALL)
    private Set<AttachmentFile> attachments = new HashSet<>();

    @OneToMany(mappedBy="ticket", cascade = CascadeType.ALL, orphanRemoval=true)
    private Set<Invoice> invoices = new HashSet<>();

    @Column(name="modified_time")
    private Date modifiedTime;

    @ManyToOne
    @JoinColumn(name ="user_id")
    @JsonIgnore
    protected User user;

    @Column(name="ticket_class", columnDefinition="varchar(31)")
    private String ticketClass;
    @Column(name="final_functional_area")
    private String finalFunctionalArea = "";

    @Column(name="reference_number")
    private String referenceNumber;
    @Column(name="legal_entity")
    private String legalEntity;

    @Override
    public String toString() {
        return "Ticket{" +
                "number='" + number + '\'' +
                ", attachments(len)=" + attachments.size() +
                ", invoices(len)=" + invoices.size() +
                '}';
    }

    public Ticket() {

    }

    public Ticket(String number, String subject, Date q2rUpdatedTime, TicketStatus state) {
        this.number = number;
        this.subject = subject;
        this.q2rUpdatedTime = q2rUpdatedTime;

        this.state = state;

        this.vendorMessage = "";
        this.reply = "";
    }

    public Ticket(String number, String subject, Date date, String language, String description, String ticketClass, TicketStatus state) {
        this(number,subject, date, state);
        this.language = language;
        this.vendorMessage = description;
        this.ticketClass = ticketClass;
    }

    public Ticket(String number, String subject, Date createdDate, Date updatedDate, String language, String description, String ticketClass, TicketStatus state) {
        this(number, subject, updatedDate, language, description, ticketClass, state);
        this.q2rCreatedTime = createdDate;

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

    public TicketStatus getState() {
        return state;
    }

    public void setState(TicketStatus state) {
        this.state = state;
    }

    public Set<AttachmentFile> getAttachments() {
        return attachments;
    }

    public void setAttachments(Set<AttachmentFile> fileList) {
        this.attachments = fileList;
    }

    public void addAttachment(AttachmentFile attachment){
        boolean alreadyAdded = false;
        for(AttachmentFile att : this.attachments){
            alreadyAdded |= att.getAttachmentId()==attachment.getAttachmentId();
        }
        if(!alreadyAdded){
            attachment.setTicket(this);
            this.attachments.add(attachment);
        }
    }

    public void removeAttachment(AttachmentFile attachment){
        for(AttachmentFile att : this.attachments){
            if(att.getAttachmentId()==attachment.getAttachmentId()){
                this.attachments.remove(att);
            }
        }
    }
    public Set<Invoice> getInvoices() {
        return invoices;
    }

    public void addOrUpdateInvoice(Invoice invoice) {
        this.invoices.add(invoice);
        invoice.setTicket(this);
    }
    public void removeInvoice(Invoice invoice){
        this.invoices.remove(invoice);
    }

    public String getTicketClass() {
        return ticketClass;
    }

    public void setTicketClass(String ticket_class) {
        this.ticketClass = ticket_class;
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

    public TicketDto convertToDto() {
        return new TicketDto(this);
    }
}
