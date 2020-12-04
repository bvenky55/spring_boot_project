package com.aihelpdeskip.monitoringservice.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Lob;

import com.aihelpdeskip.monitoringservice.deserializers.TicketDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Entity
@Table(name="vrt_template")
@JsonDeserialize(using = TicketDeserializer.class)
public class VRTTemplate {

    public enum languages {
        en,
        es,
        it,
        fr
    }

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private int id;

    @Column(name="template_id", nullable=false)
    private String templateId;

    @Column(name="body", nullable=false)
    @Lob
    private String body;

    @Column(name="functional_area")
    private String functionalArea;
    @Column(columnDefinition = "char(3)")
    protected String language;

    public VRTTemplate() {  }

    public VRTTemplate(String templateId, String body, String functionalArea, String language) {
        this.templateId = templateId;
        this.body = body;
        this.functionalArea = functionalArea;
        this.language = language;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getFunctionalArea() {
        return functionalArea;
    }

    public void setFunctionalArea(String functionalArea) {
        this.functionalArea = functionalArea;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
