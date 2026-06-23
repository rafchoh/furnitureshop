package com.example.furnitureshop.repo;

import com.example.furnitureshop.entity.Furniture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FurnitureRepo extends JpaRepository<Furniture, UUID> {

    List<Furniture> findByAvailableTrue();
    List<Furniture> findAll();


}
