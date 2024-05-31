package com.medmap.track.controller;

import com.medmap.track.model.Medicine;
import com.medmap.track.service.MedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "api/v1/medmap")
public class MedController {

    @Autowired
    private MedService medService;

    @PostMapping("medicine")
    public ResponseEntity<Medicine> addMedicine(@RequestBody Medicine medicine) {
        return ResponseEntity.status(HttpStatus.CREATED).body(medService.saveMedicine(medicine));
    }
}
