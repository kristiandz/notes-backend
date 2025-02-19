package com.notes.notes_app.repository;

import com.notes.notes_app.model.Attachment;
import com.notes.notes_app.model.AttachmentDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
    List<Attachment> findByNoteId(Long noteId);

    @Query("SELECT new com.notes.notes_app.model.AttachmentDTO(a.id, a.fileName, a.fileType, CONCAT('/attachments/', a.id)) FROM Attachment a WHERE a.note.id = :noteId")
    List<AttachmentDTO> findAttachmentMetadataByNoteId(@Param("noteId") Long noteId);
}