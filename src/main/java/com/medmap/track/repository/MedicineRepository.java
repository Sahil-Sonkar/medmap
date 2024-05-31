package com.medmap.track.repository;

import com.medmap.track.model.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MedicineRepository extends JpaRepository<Medicine, Integer> {
    Optional<Medicine> findByName(String name);
}
