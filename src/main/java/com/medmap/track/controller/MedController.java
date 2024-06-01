package com.medmap.track.controller;

import com.medmap.track.dto.MedicineDto;
import com.medmap.track.model.Company;
import com.medmap.track.model.Medicine;
import com.medmap.track.model.PurchaseOrder;
import com.medmap.track.service.MedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/v1/medmap")
public class MedController {

    @Autowired
    private MedService medService;

    @PostMapping("medicine")
    public ResponseEntity<Medicine> addMedicine(@RequestBody MedicineDto medicineDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(medService.saveMedicine(medicineDto));
    }

    @PostMapping("company")
    public ResponseEntity<Company> addCompany(@RequestBody Company company) {
        return ResponseEntity.status(HttpStatus.CREATED).body(medService.saveCompany(company));
    }

    @GetMapping("company")
    public ResponseEntity<List<Company>> getAllCompany() {
        return ResponseEntity.status(HttpStatus.OK).body(medService.getAllCompany());
    }

    @GetMapping("medicine")
    public ResponseEntity<List<Medicine>> getAllMedicine() {
        return ResponseEntity.status(HttpStatus.OK).body(medService.getAllMedicine());
    }

    @PostMapping("purchaseOrder")
    public ResponseEntity<PurchaseOrder> addPurchaseOrder(@RequestBody PurchaseOrder purchaseOrder) {
        return ResponseEntity.status(HttpStatus.CREATED).body(medService.createPurchaseOrder(purchaseOrder));
    }
}
