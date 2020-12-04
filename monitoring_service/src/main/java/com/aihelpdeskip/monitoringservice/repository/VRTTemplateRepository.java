package com.aihelpdeskip.monitoringservice.repository;

import com.aihelpdeskip.monitoringservice.models.VRTTemplate;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import java.util.List;

@RepositoryRestResource(collectionResourceRel = "vrttemplate", path = "vrttemplate")
public interface VRTTemplateRepository extends CrudRepository<VRTTemplate, String>
{
    VRTTemplate findByTemplateId(String template);
    VRTTemplate findByTemplateIdAndLanguage(String template, String language);
    List<VRTTemplate> findAll();
}