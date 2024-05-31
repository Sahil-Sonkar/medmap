package com.medmap.track.service;

import com.medmap.track.model.Company;
import com.medmap.track.model.Medicine;

import java.util.List;

public interface MedService {

    Medicine saveMedicine(Medicine medicine);

    Company saveCompany(Company company);

    List<Company> getAllCompany();

    List<Medicine> getAllMedicine();
}
