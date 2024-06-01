package com.medmap.track.repository;

import com.medmap.track.model.Company;
import com.medmap.track.model.Inventory;
import com.medmap.track.model.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Integer> {
    Optional<Inventory> findByCompanyAndMedicine(Company company, Medicine medicine);
}
