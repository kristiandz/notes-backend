package com.notes.notes_app.service;

import com.notes.notes_app.errorHandle.ResourceNotFoundException;
import com.notes.notes_app.model.Attachment;
import com.notes.notes_app.model.AttachmentDTO;
import com.notes.notes_app.model.Note;
import com.notes.notes_app.repository.AttachmentRepository;
import com.notes.notes_app.repository.NoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class AttachmentService {
    @Autowired
    private AttachmentRepository attachmentRepository;
    @Autowired
    private NoteRepository noteRepository;

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

    public void saveAttachment(MultipartFile file, Long noteId) throws IOException {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new ResourceNotFoundException("Note not found"));

        Attachment attachment = new Attachment();
        attachment.setFileName(file.getOriginalFilename());
        attachment.setFileType(file.getContentType());
        attachment.setData(file.getBytes());
        attachment.setNote(note);
        attachmentRepository.save(attachment);
    }

    public void reassociateAttachmentToNote(Long attachmentId, Long noteId) {
        Attachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Attachment not found with id: " + attachmentId));
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new ResourceNotFoundException("Note not found with id: " + noteId));
        attachment.setNote(note);
        attachmentRepository.save(attachment);
    }

    @Transactional
    public void deleteAttachmentById(Long attachmentId) {
        if (!attachmentRepository.existsById(attachmentId)) {
            throw new ResourceNotFoundException("Attachment not found with id: " + attachmentId);
        }
        attachmentRepository.deleteById(attachmentId);
    }

}
