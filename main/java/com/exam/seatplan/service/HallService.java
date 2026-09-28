package com.exam.seatplan.service;

import com.exam.seatplan.dto.HallDto;
import com.exam.seatplan.model.Hall;
import com.exam.seatplan.repo.HallRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HallService {

    private final HallRepository hallRepository;

    public HallService(HallRepository hallRepository) {
        this.hallRepository = hallRepository;
    }

    public List<HallDto> getAllHalls() {
        return hallRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<HallDto> getActiveHalls() {
        return hallRepository.findByIsActiveTrueOrderByHallNumberAsc().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public HallDto getHallById(Long id) {
        Hall hall = hallRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Exam Hall not found with id: " + id));
        return toDto(hall);
    }

    public Hall getHallEntity(Long id) {
        return hallRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Exam Hall not found with id: " + id));
    }

    @Transactional
    public HallDto createHall(Hall hall) {
        if (hall.getHallNumber() == null || hall.getHallNumber().trim().isEmpty()) {
            throw new IllegalArgumentException("Hall number cannot be empty.");
        }
        if (hallRepository.existsByHallNumber(hall.getHallNumber().trim())) {
            throw new IllegalArgumentException("Hall number '" + hall.getHallNumber() + "' already exists.");
        }
        if (hall.getTotalRows() <= 0 || hall.getTotalColumns() <= 0) {
            throw new IllegalArgumentException("Rows and Columns must be greater than 0.");
        }
        hall.setHallNumber(hall.getHallNumber().trim().toUpperCase());
        int gridMax = hall.getTotalRows() * hall.getTotalColumns();
        if (hall.getCapacity() > 0) {
            hall.setCapacity(Math.min(hall.getCapacity(), gridMax));
        } else {
            hall.setCapacity(gridMax);
        }
        Hall saved = hallRepository.save(hall);
        return toDto(saved);
    }

    @Transactional
    public HallDto updateHall(Long id, Hall hallDetails) {
        Hall hall = hallRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Exam Hall not found with id: " + id));

        String newHallNum = hallDetails.getHallNumber() != null ? hallDetails.getHallNumber().trim().toUpperCase() : hall.getHallNumber();
        if (!hall.getHallNumber().equalsIgnoreCase(newHallNum) && hallRepository.existsByHallNumber(newHallNum)) {
            throw new IllegalArgumentException("Hall number '" + newHallNum + "' already exists.");
        }

        hall.setHallNumber(newHallNum);
        hall.setName(hallDetails.getName());
        hall.setTotalRows(hallDetails.getTotalRows());
        hall.setTotalColumns(hallDetails.getTotalColumns());
        int gridMax = hallDetails.getTotalRows() * hallDetails.getTotalColumns();
        if (hallDetails.getCapacity() > 0) {
            hall.setCapacity(Math.min(hallDetails.getCapacity(), gridMax));
        } else {
            hall.setCapacity(gridMax);
        }
        hall.setActive(hallDetails.isActive());

        Hall updated = hallRepository.save(hall);
        return toDto(updated);
    }

    @Transactional
    public void deleteHall(Long id) {
        if (!hallRepository.existsById(id)) {
            throw new IllegalArgumentException("Exam Hall not found with id: " + id);
        }
        hallRepository.deleteById(id);
    }

    public HallDto toDto(Hall hall) {
        return new HallDto(
                hall.getId(),
                hall.getHallNumber(),
                hall.getName(),
                hall.getTotalRows(),
                hall.getTotalColumns(),
                hall.getCapacity(),
                hall.isActive(),
                0
        );
    }
}
