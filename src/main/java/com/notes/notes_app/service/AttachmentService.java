package com.notes.notes_app.service;

import com.notes.notes_app.errorHandle.ResourceNotFoundException;
import com.notes.notes_app.model.Attachment;
import com.notes.notes_app.model.AttachmentDTO;
import com.notes.notes_app.repository.AttachmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AttachmentService {
    @Autowired
    private AttachmentRepository attachmentRepository;

    public List<Attachment> getFilesByNoteId(Long noteId) {
        return attachmentRepository.findByNoteId(noteId);
    }

    public Attachment getAttachmentById(Long id) {
        return attachmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attachment not found"));
    }

    public List<AttachmentDTO> getAttachmentMetadataByNoteId(Long noteId) {
        return attachmentRepository.findAttachmentMetadataByNoteId(noteId);
    }

    public void saveAttachment(Attachment attachment) {
        attachmentRepository.save(attachment);
    }
}
