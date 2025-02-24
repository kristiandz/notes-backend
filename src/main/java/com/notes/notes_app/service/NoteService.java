package com.notes.notes_app.service;

import com.notes.notes_app.errorHandle.ResourceNotFoundException;
import com.notes.notes_app.model.*;
import com.notes.notes_app.repository.CategoryRepository;
import com.notes.notes_app.repository.NoteRepository;
import com.notes.notes_app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NoteService {
    @Autowired private NoteRepository noteRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private AttachmentService attachmentService;

    @Transactional
    public Note createNote(NoteDTO noteDTO, List<MultipartFile > files) {
        if (noteDTO.getId() != null) {
            throw new IllegalArgumentException("ID must be null when creating a new note");
        }
        User user = userRepository.findById(noteDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + noteDTO.getUserId()));

        List<Category> categories;
        if (noteDTO.getCategories() != null && !noteDTO.getCategories().isEmpty()) {
            categories = categoryRepository.findAllById(noteDTO.getCategories().stream().map(Category::getId).toList());
            for (Category category : noteDTO.getCategories()) {
                if (!categories.stream().anyMatch(cat -> cat.getId().equals(category.getId()))) {
                    Category newCategory = new Category();
                    newCategory.setName(category.getName());
                    categories.add(categoryRepository.save(newCategory));
                }
            }
            if (categories.isEmpty()) {
                Category generalCategory = categoryRepository.findByName("General")
                        .orElseThrow(() -> new ResourceNotFoundException("General category not found. Ensure it's created."));
                categories.add(generalCategory);
            }
        } else {
            Category generalCategory = categoryRepository.findByName("General")
                    .orElseThrow(() -> new ResourceNotFoundException("General category not found. Ensure it's created."));
            categories = List.of(generalCategory);
        }

        Note note = new Note();
        note.setCategories(categories);
        note.setTitle(noteDTO.getTitle());
        note.setContent(noteDTO.getContent());
        note.setUser(user);
        note.setId(null);
        note = noteRepository.save(note);

        for (MultipartFile file : files) {
            try {
                attachmentService.saveAttachment(file, note.getId()); // Passing noteId for the relationship
            } catch (IOException e) {
                throw new IllegalStateException("Failed to save attachment", e);
            }
        }
        note.setAttachments(attachmentService.getFilesByNoteId(note.getId()));
        return note;
    }

    @Transactional
    public NoteDTO updateNote(Long id, String title, String content, List<Long> categoryIds) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Note not found with id: " + id));

        if (categoryIds != null && !categoryIds.isEmpty()) {
            List<Category> categories = categoryRepository.findAllById(categoryIds);
            if (categories.isEmpty()) {
                Category generalCategory = categoryRepository.findByName("General")
                        .orElseThrow(() -> new ResourceNotFoundException("General category not found. Ensure it's created."));
                categories.add(generalCategory);
            }
            note.setCategories(categories);
        }
        if (title != null) {
            note.setTitle(title);
        }
        if (content != null) {
            note.setContent(content);
        }
        Note updatedNote = noteRepository.save(note);
        return convertToDTO(updatedNote);
    }

    public List<NoteDTO> getNotesByUser(Long userId) {
        return noteRepository.findByUserId(userId).stream()
                .map(this::convertToDTO)
                .toList();
    }

    public void deleteNote(Long id) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Note not found with id: " + id));
        noteRepository.delete(note);
    }

    private NoteDTO convertToDTO(Note note) {
        return NoteDTO.builder()
                .id(note.getId())
                .title(note.getTitle())
                .content(note.getContent())
                .createdAt(note.getCreatedAt())
                .userId(note.getUser().getId())
                .categories(note.getCategories())
                .attachments(attachmentService.getAttachmentMetadataByNoteId(note.getId()).stream()
                        .map(attachment -> new AttachmentDTO(
                                attachment.getId(),
                                attachment.getFileName(),
                                attachment.getFileType(),
                                "/attachments/" + attachment.getId()))
                        .toList())
                .build();
    }

    public NoteDTO getNoteById(Long id) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Note not found for id: " + id));
        return convertToDTO(note);
    }

    @Transactional
    public void handleAttachmentsForNote(Long noteId, List<MultipartFile> attachments, List<Long> attachmentIds) throws IOException {
        if (attachments != null && !attachments.isEmpty()) {
            for (MultipartFile file : attachments) {
                attachmentService.saveAttachment(file, noteId);
            }
        }
        if (attachmentIds != null && !attachmentIds.isEmpty()) {
            for (Long attachmentId : attachmentIds) {
                attachmentService.reassociateAttachmentToNote(attachmentId, noteId);
            }
        }
    }

    public List<NoteDTO> getNotesByUsername(String username) {
        List<Note> notes = noteRepository.findByUserUsername(username);
        if (notes.isEmpty()) {
            throw new ResourceNotFoundException("No notes found for user: " + username);
        }
        return notes.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
}
