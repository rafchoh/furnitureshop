package com.example.furnitureshop.repo;

import com.example.furnitureshop.entity.Booking;
import com.example.furnitureshop.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BookingRepo extends JpaRepository<Booking, UUID> {

    List<Booking> findByUserAndStatus(Users user, String status);
    List<Booking> findByUserAndStatusNot(Users user, String status);
    Optional<Booking> findByUserAndFurnitureIdAndStatus(Users user, UUID furnitureId, String status);
    void deleteByFurnitureId(UUID id);

}
