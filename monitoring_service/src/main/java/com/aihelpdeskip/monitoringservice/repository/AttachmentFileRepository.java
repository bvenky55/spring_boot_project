package com.aihelpdeskip.monitoringservice.repository;

import java.util.List;

import com.aihelpdeskip.monitoringservice.models.AttachmentFile;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(exported = false)
public interface AttachmentFileRepository extends CrudRepository<AttachmentFile, Integer>
{
    List<AttachmentFile> findByAttachment(int id);
}