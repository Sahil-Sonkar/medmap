package com.medmap.track.service;

import com.medmap.track.model.Medicine;
import com.medmap.track.repository.MedRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MedServiceImpl implements MedService {

    @Autowired
    private MedRepository medRepository;

    @Override
    public Medicine saveMedicine(Medicine medicine) {
        return medRepository.save(medicine);
    }
}
