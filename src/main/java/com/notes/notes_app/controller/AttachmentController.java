package com.notes.notes_app.controller;

import com.notes.notes_app.errorHandle.ResourceNotFoundException;
import com.notes.notes_app.model.Attachment;
import com.notes.notes_app.repository.AttachmentRepository;
import com.notes.notes_app.service.AttachmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/attachments")
public class AttachmentController {

    @Autowired
    AttachmentService attachmentService;

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> downloadAttachment(@PathVariable Long id) {
        Attachment attachment = attachmentService.getAttachmentById(id);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(attachment.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + attachment.getFileName() + "\"")
                .body(attachment.getData());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAttachment(@PathVariable Long id) {
        try {
            attachmentService.deleteAttachmentById(id);
            return ResponseEntity.ok("Attachment deleted successfully");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while deleting the attachment: " + e.getMessage());
        }
    }
}
