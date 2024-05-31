package com.medmap.track.repository;

import com.medmap.track.model.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedRepository extends JpaRepository<Medicine, Integer> {
}
