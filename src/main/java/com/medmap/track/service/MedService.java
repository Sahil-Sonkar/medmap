package com.medmap.track.service;

import com.medmap.track.dto.MedicineDto;
import com.medmap.track.model.Company;
import com.medmap.track.model.Medicine;
import com.medmap.track.model.PurchaseOrder;

import java.util.List;

public interface MedService {

    Medicine saveMedicine(MedicineDto medicineDto);

    Company saveCompany(Company company);

    List<Company> getAllCompany();

    List<Medicine> getAllMedicine();

    PurchaseOrder createPurchaseOrder(PurchaseOrder purchaseOrder);
}
