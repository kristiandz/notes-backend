package com.notes.notes_app.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "attachments")
public class Attachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String fileType;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(nullable = false)
    @JsonIgnore
    private byte[] data;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "note_id", nullable = false)
    private Note note;
}
