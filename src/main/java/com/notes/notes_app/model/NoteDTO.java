package com.notes.notes_app.model;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoteDTO {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private Long userId;
    private List<Long> categoryIds;
    private List<AttachmentDTO> attachments;
}