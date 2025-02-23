package com.notes.notes_app.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.notes.notes_app.model.*;
import com.notes.notes_app.service.AttachmentService;
import com.notes.notes_app.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/notes")
public class NoteController {
    @Autowired
    private NoteService noteService;
    @Autowired
    private AttachmentService attachmentService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<NoteDTO> createNote(@RequestPart("note") String noteJson,
                                              @RequestPart(value="attachments", required = false) List<MultipartFile> files) {
        ObjectMapper objectMapper = new ObjectMapper();
        NoteDTO requestDTO;
        try {
            requestDTO = objectMapper.readValue(noteJson, NoteDTO.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Invalid note JSON format", e);
        }
        List<MultipartFile> multipartFiles = (files != null) ? files : new ArrayList<>();
        Note note = noteService.createNote(requestDTO, multipartFiles);
        List<AttachmentDTO> attachmentDTO = note.getAttachments().stream()
                .map(attachment -> new AttachmentDTO(
                        attachment.getId(),
                        attachment.getFileName(),
                        attachment.getFileType(),
                        "/attachments/" + attachment.getId()
                ))
                .toList();
        NoteDTO responseDTO = new NoteDTO(
                note.getId(), note.getTitle(), note.getContent(), note.getCreatedAt(),
                note.getUser().getId(), note.getCategories(),
                attachmentDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NoteDTO> getNoteById(@PathVariable Long id) {
        NoteDTO noteDTO = noteService.getNoteById(id);
        return ResponseEntity.ok(noteDTO);
    }

    @GetMapping("/user/id/{userId}")
    public ResponseEntity<List<NoteDTO>> getNotesByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(noteService.getNotesByUser(userId));
    }

    @GetMapping("/user/{username}")
    public List<NoteDTO> getNotesByUser(@PathVariable String username) {
        return noteService.getNotesByUsername(username);
    }

    @PutMapping("/{id}")
    public ResponseEntity<NoteDTO> updateNote(
            @PathVariable Long id,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "content", required = false) String content,
            @RequestParam(value = "categories[]", required = false) List<Long> categoryIds,
            @RequestParam(value = "attachments[]", required = false) List<MultipartFile> attachments,
            @RequestParam(value = "attachmentIds[]", required = false) List<Long> attachmentIds) {

        System.out.println("New Attachments: " + (attachments != null ? attachments.size() : "None"));

        try {
            noteService.handleAttachmentsForNote(id, attachments, attachmentIds);
        } catch (IOException e) {
            return ResponseEntity.status(500).body(null);
        }
        NoteDTO updatedNoteDTO = noteService.updateNote(id, title, content, categoryIds);
        return ResponseEntity.ok(updatedNoteDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteNote(@PathVariable Long id) {
        noteService.deleteNote(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Note deleted successfully");
        return ResponseEntity.ok(response);
    }
}
