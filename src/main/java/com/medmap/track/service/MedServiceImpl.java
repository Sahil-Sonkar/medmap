package com.medmap.track.service;

import com.medmap.track.model.Company;
import com.medmap.track.model.Medicine;
import com.medmap.track.repository.CompanyRepository;
import com.medmap.track.repository.MedicineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MedServiceImpl implements MedService {

    @Autowired
    private MedicineRepository medicineRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Override
    public Medicine saveMedicine(Medicine medicine) {
        return medicineRepository.save(medicine);
    }

    @Override
    public Company saveCompany(Company company) {
        return companyRepository.save(company);
    }

    @Override
    public List<Company> getAllCompany() {
        return companyRepository.findAll();
    }

    @Override
    public List<Medicine> getAllMedicine() {
        return medicineRepository.findAll();
    }
}
