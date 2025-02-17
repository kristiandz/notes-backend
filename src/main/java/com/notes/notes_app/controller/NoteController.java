package com.notes.notes_app.controller;

import com.notes.notes_app.model.Note;
import com.notes.notes_app.model.NoteDTO;
import com.notes.notes_app.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/notes")
public class NoteController {
    @Autowired
    private NoteService noteService;

    @PostMapping
    public ResponseEntity<NoteDTO> createNote(@RequestBody NoteDTO requestDTO) {
        Note note = noteService.createNote(requestDTO);
        NoteDTO responseDTO = noteService.getNoteById(note.getId());
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NoteDTO> getNoteById(@PathVariable Long id) {
        NoteDTO noteDTO = noteService.getNoteById(id);
        return ResponseEntity.ok(noteDTO);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Note>> getNotesByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(noteService.getNotesByUser(userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<NoteDTO> updateNote(@PathVariable Long id, @RequestBody NoteDTO noteDTO) {
        NoteDTO updatedNote = noteService.updateNote(id, noteDTO);
        return ResponseEntity.ok(updatedNote);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteNote(@PathVariable Long id) {
        noteService.deleteNote(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Note deleted successfully");
        return ResponseEntity.ok(response);
    }
}
