package com.exam.seatplan.controller;

import com.exam.seatplan.dto.ExamSessionDto;
import com.exam.seatplan.model.ExamSession;
import com.exam.seatplan.service.ExamSessionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sessions")
@CrossOrigin(origins = "*")
public class ExamSessionController {

    private final ExamSessionService examSessionService;

    public ExamSessionController(ExamSessionService examSessionService) {
        this.examSessionService = examSessionService;
    }

    @GetMapping
    public ResponseEntity<List<ExamSessionDto>> getAllSessions() {
        return ResponseEntity.ok(examSessionService.getAllSessions());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExamSessionDto> getSessionById(@PathVariable Long id) {
        return ResponseEntity.ok(examSessionService.getSessionById(id));
    }

    @PostMapping
    public ResponseEntity<?> createSession(@RequestBody ExamSession session) {
        try {
            ExamSessionDto created = examSessionService.createSession(session);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateSession(@PathVariable Long id, @RequestBody ExamSession sessionDetails) {
        try {
            ExamSessionDto updated = examSessionService.updateSession(id, sessionDetails);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSession(@PathVariable Long id) {
        try {
            examSessionService.deleteSession(id);
            return ResponseEntity.ok(Map.of("message", "Exam Session deleted successfully."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
