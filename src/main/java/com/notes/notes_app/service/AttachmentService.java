package com.notes.notes_app.service;

import com.notes.notes_app.model.Attachment;
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
}
