package com.exam.seatplan.controller;

import com.exam.seatplan.dto.ArrangementRequestDto;
import com.exam.seatplan.dto.ArrangementSummaryDto;
import com.exam.seatplan.dto.SeatDto;
import com.exam.seatplan.dto.SeatSearchResultDto;
import com.exam.seatplan.repo.SeatRepository;
import com.exam.seatplan.service.DataInitializationService;
import com.exam.seatplan.service.SeatService;
import com.exam.seatplan.service.SeatingArrangementService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/seatplan")
@CrossOrigin(origins = "*")
public class SeatPlanController {

    private final SeatingArrangementService seatingArrangementService;
    private final SeatService seatService;
    private final SeatRepository seatRepository;
    private final DataInitializationService dataInitializationService;

    public SeatPlanController(SeatingArrangementService seatingArrangementService,
                              SeatService seatService,
                              SeatRepository seatRepository,
                              DataInitializationService dataInitializationService) {
        this.seatingArrangementService = seatingArrangementService;
        this.seatService = seatService;
        this.seatRepository = seatRepository;
        this.dataInitializationService = dataInitializationService;
    }

    @PostMapping("/generate")
    public ResponseEntity<?> generateArrangement(@RequestBody ArrangementRequestDto request) {
        try {
            ArrangementSummaryDto result = seatingArrangementService.generateArrangement(request);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to generate seating arrangement: " + e.getMessage()));
        }
    }

    @GetMapping("/session/{sessionId}/hall/{hallId}")
    public ResponseEntity<List<SeatDto>> getSeatsByHallAndSession(
            @PathVariable Long hallId,
            @PathVariable Long sessionId) {
        return ResponseEntity.ok(seatService.getSeatsByHallAndSession(hallId, sessionId));
    }

    @GetMapping("/session/{sessionId}/allocations")
    public ResponseEntity<List<SeatDto>> getAllocationsBySession(@PathVariable Long sessionId) {
        return ResponseEntity.ok(seatService.getAllocationsBySession(sessionId));
    }

    @DeleteMapping("/session/{sessionId}")
    public ResponseEntity<?> clearSessionAllocations(@PathVariable Long sessionId) {
        try {
            seatRepository.deleteByExamSessionId(sessionId);
            return ResponseEntity.ok(Map.of("message", "Allocations for session cleared successfully."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<SeatSearchResultDto>> searchSeat(
            @RequestParam(required = false) String rollNumber,
            @RequestParam(required = false) String query) {
        if (rollNumber != null && !rollNumber.trim().isEmpty()) {
            return ResponseEntity.ok(seatService.searchByRollNumber(rollNumber.trim()));
        }
        if (query != null && !query.trim().isEmpty()) {
            return ResponseEntity.ok(seatService.searchAllocations(query.trim()));
        }
        return ResponseEntity.ok(List.of());
    }

    @PostMapping("/seed-sample-data")
    public ResponseEntity<?> seedSampleData() {
        try {
            dataInitializationService.resetAndSeedSampleData();
            return ResponseEntity.ok(Map.of("message", "Demo sample data populated successfully!"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to populate demo data: " + e.getMessage()));
        }
    }
}
