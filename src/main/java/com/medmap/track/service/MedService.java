package com.medmap.track.service;

import com.medmap.track.dto.MedicineDto;
import com.medmap.track.dto.PurchaseOrderDto;
import com.medmap.track.model.Company;
import com.medmap.track.model.Medicine;
import com.medmap.track.model.PurchaseOrder;

import java.util.List;
import java.util.Map;

public interface MedService {

    Medicine saveMedicine(MedicineDto medicineDto);

    Company saveCompany(Company company);

    List<Company> getAllCompany();

    List<Medicine> getAllMedicine();

    PurchaseOrder createPurchaseOrder(PurchaseOrderDto purchaseOrder);

    List<PurchaseOrder> getMedicineHistory(String medicineName);

    Map<String, Map<String, Object>> getMedicineDistribution(String medicineName);

    List<String> getFullMedicineHistory(String medicineName);
}
