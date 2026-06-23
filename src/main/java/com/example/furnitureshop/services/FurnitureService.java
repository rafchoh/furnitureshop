package com.example.furnitureshop.services;

import com.example.furnitureshop.entity.Booking;
import com.example.furnitureshop.entity.Furniture;
import com.example.furnitureshop.repo.BookingRepo;
import com.example.furnitureshop.repo.FurnitureRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class FurnitureService {

    private final FurnitureRepo furnitureRepository;
    private final BookingRepo bookingRepository;

    public FurnitureService(FurnitureRepo furnitureRepository, BookingRepo bookingRepository) {
        this.furnitureRepository = furnitureRepository;
        this.bookingRepository = bookingRepository;
    }

    public List<Furniture> getAvailableFurniture() {
        return furnitureRepository.findByAvailableTrue();
    }

    public List<Furniture> getAllFurniture() {
        return furnitureRepository.findAll();
    }

    public Furniture getFurnitureById(UUID id) {
        return furnitureRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Furniture not found."));
    }

    @Transactional
    public void saveFurniture(Furniture furniture) {
        furnitureRepository.saveAndFlush(furniture);
    }

    @Transactional
    public void deleteFurniture(UUID id) {
        Furniture furniture = furnitureRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Furniture not found"));

        bookingRepository.deleteByFurnitureId(id);

        furnitureRepository.delete(furniture);
    }
}