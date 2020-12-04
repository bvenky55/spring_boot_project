package com.aihelpdeskip.monitoringservice.models;

import com.aihelpdeskip.monitoringservice.deserializers.InvoiceDeserializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@JsonDeserialize(using = InvoiceDeserializer.class)
public class InvoiceDto {

    private long id;

    private String number;
    private int attachment;
    private String vendor;
    private String category = "";
    private String message = "";

    private float numberConfidence;
    private Date date;
    private float dateConfidence;

    private byte pageFound;
    private String functionalArea;
    private String integrity = "A";
    private String source;

    @Override
    public String toString() {
        return "InvoiceDto{" +
                "id='" + id + '\'' +
                ", number='" + number + '\'' +
                ", attachment=" + attachment +
                ", date=" + date +
                ", pageFound=" + pageFound +
                '}';
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

    public String getSource(){
        return source;
    }

    InvoiceDto(Invoice other) {
        this.id = other.getId();
        this.number = other.getNumber();
        this.vendor = other.getVendor();
        this.category = other.getCategory();
        this.message = other.getMessage();
        this.numberConfidence = other.getNumberConfidence();
        this.date = other.getDate();
        this.dateConfidence = other.getDateConfidence();
        this.pageFound = other.getPageFound();
        this.functionalArea = other.getFunctionalArea();
        this.integrity = other.getIntegrity();
        this.source = other.getSource();
    }
}