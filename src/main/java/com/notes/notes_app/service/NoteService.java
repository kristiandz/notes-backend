package com.notes.notes_app.service;

import com.notes.notes_app.errorHandle.ResourceNotFoundException;
import com.notes.notes_app.model.*;
import com.notes.notes_app.repository.CategoryRepository;
import com.notes.notes_app.repository.NoteRepository;
import com.notes.notes_app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NoteService {
    @Autowired private NoteRepository noteRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private AttachmentService attachmentService;


    public Note createNote(NoteDTO noteDTO, List<Attachment> files) {
        if (noteDTO.getId() != null) {
            throw new IllegalArgumentException("ID must be null when creating a new note");
        }
        User user = userRepository.findById(noteDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + noteDTO.getUserId()));

        List<Category> categories = categoryRepository.findAllById(noteDTO.getCategoryIds());
        if (categories.size() != noteDTO.getCategoryIds().size()) {
            throw new ResourceNotFoundException("One or more categories not found.");
        }
        Note note = new Note();
        note.setCategories(categories);
        note.setTitle(noteDTO.getTitle());
        note.setContent(noteDTO.getContent());
        note.setUser(user);
        note = noteRepository.save(note);
        for (Attachment attachment : files) {
            attachment.setNote(note);
            attachmentService.saveAttachment(attachment);
        }
        note.setAttachments(attachmentService.getFilesByNoteId(note.getId()));
        return note;
    }

    public NoteDTO updateNote(Long id, NoteDTO noteDTO) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Note not found with id: " + id));

        if(noteDTO.getUserId() != null) {
            User user = userRepository.findById(noteDTO.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + noteDTO.getUserId()));
            note.setUser(user);
        }
        if(noteDTO.getCategoryIds() != null) {
            List<Category> categories = categoryRepository.findAllById(noteDTO.getCategoryIds());
            if (categories.isEmpty()) {
                throw new ResourceNotFoundException("One or more categories not found");
            }
            note.setCategories(categories);
        }
        if(noteDTO.getTitle() != null) {
            note.setTitle(noteDTO.getTitle());
        }
        if(noteDTO.getContent() != null) {
            note.setContent(noteDTO.getContent());
        }
        Note updatedNote = noteRepository.save(note);
        return convertToDTO(updatedNote);
    }

    public List<NoteDTO> getNotesByUser(Long userId) {
        List<Note> notes = noteRepository.findByUserId(userId);
        return notes.stream().map(note -> new NoteDTO(
                note.getId(),
                note.getTitle(),
                note.getContent(),
                note.getUser().getId(),
                note.getCategories().stream().map(Category::getId).toList(),
                attachmentService.getAttachmentMetadataByNoteId(note.getId())
        )).toList();
    }

    public void deleteNote(Long id) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Note not found with id: " + id));
        noteRepository.delete(note);
    }

    // Helper DTO methods
    private NoteDTO convertToDTO(Note note) {
        return new NoteDTO(
                note.getId(),
                note.getTitle(),
                note.getContent(),
                note.getUser().getId(),
                note.getCategories().stream().map(Category::getId).collect(Collectors.toList()),
                note.getAttachments().stream()
                        .map(attachment -> new AttachmentDTO(
                                attachment.getId(),
                                attachment.getFileName(),
                                attachment.getFileType(),
                                "/attachments/" +attachment.getId()
                        ))
                        .collect(Collectors.toList())
        );
    }

    public NoteDTO getNoteById(Long id) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Note not found for id: " + id));
        return convertToDTO(note);
    }
}
