package com.notes.notes_app.model;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class NoteDTO {
    private Long id;
    private String title;
    private String content;
    private Long userId;
    private List<Long> categoryIds;
}