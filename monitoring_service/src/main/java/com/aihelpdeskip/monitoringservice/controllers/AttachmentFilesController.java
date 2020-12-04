package com.aihelpdeskip.monitoringservice.controllers;

import com.aihelpdeskip.monitoringservice.models.AttachmentFile;
import com.aihelpdeskip.monitoringservice.repository.AttachmentFileRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class AttachmentFilesController {

    private AttachmentFileRepository attachmentsRepository;

    public AttachmentFilesController(AttachmentFileRepository attachmentsRepository) {
        this.attachmentsRepository = attachmentsRepository;
    }

    @RequestMapping(path="/resources/{id}", method=RequestMethod.GET, produces = MediaType.APPLICATION_PDF_VALUE)
    public @ResponseBody byte[] getFile(@PathVariable int id) {
        AttachmentFile attachment = attachmentsRepository.findById(id).get();
        return attachment.getContents();
    }
}
