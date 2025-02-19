package com.notes.notes_app.controller;

import com.notes.notes_app.errorHandle.ResourceNotFoundException;
import com.notes.notes_app.model.Attachment;
import com.notes.notes_app.repository.AttachmentRepository;
import com.notes.notes_app.service.AttachmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/attachments")
public class AttachmentController {

    @Autowired
    AttachmentService attachmentService;

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> downloadAttachment(@PathVariable Long id) {
        Attachment attachment = attachmentService.getAttachmentById(id);
        System.out.println("TEST");
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(attachment.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + attachment.getFileName() + "\"")
                .body(attachment.getData());
    }
}
