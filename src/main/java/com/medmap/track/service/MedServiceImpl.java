package com.medmap.track.service;

import com.medmap.track.exception.BadRequestException;
import com.medmap.track.model.Company;
import com.medmap.track.model.Medicine;
import com.medmap.track.model.PurchaseOrder;
import com.medmap.track.repository.CompanyRepository;
import com.medmap.track.repository.MedicineRepository;
import com.medmap.track.repository.PurchaseOrderRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class MedServiceImpl implements MedService {

    @Autowired
    private MedicineRepository medicineRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    private static final Set<String> ALLOWED_ROLES = Set.of(
            "MANUFACTURER", "DISTRIBUTOR", "TRANSPORTER", "RETAILER", "CONSUMER"
    );

    private static void verifyRole(String role) {
        if (!ALLOWED_ROLES.contains(role)) {
            throw new BadRequestException("Allowed roles are MANUFACTURER, DISTRIBUTOR, TRANSPORTER, RETAILER, and CONSUMER");
        }
    }

    @Override
    public Medicine saveMedicine(Medicine medicine) {
        Optional<Medicine> medicineOptional = medicineRepository.findByName(medicine.getName());
        if (medicineOptional.isPresent()) {
            throw new BadRequestException("Medicine with this name already present");
        }
        Optional<Company> companyOptional = companyRepository.findByCrn(medicine.getCompanyCRN());
        if (companyOptional.isEmpty()) {
            throw new BadRequestException("Company with this crn not present");
        }
        Company company = companyOptional.get();
        if (!StringUtils.equals(company.getOrgRole(), "MANUFACTURER")) {
            throw new BadRequestException("Only manufacturer can add medicine");
        }
        return medicineRepository.save(medicine);
    }

    @Override
    public Company saveCompany(Company company) {
        Optional<Company> companyOptional = companyRepository.findByName(company.getName());
        if (companyOptional.isPresent()) {
            throw new BadRequestException("Company with this name already present");
        }
        verifyRole(company.getOrgRole());
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

    @Override
    public PurchaseOrder createPurchaseOrder(PurchaseOrder purchaseOrder) {
        Integer buyerCrn = purchaseOrder.getBuyerCrn();
        Optional<Company> buyerOptional = companyRepository.findByCrn(buyerCrn);
        if (buyerOptional.isEmpty()) {
            throw new BadRequestException("Buyer with this crn is not present in db");
        }
        Integer sellerCrn = purchaseOrder.getSellerCrn();
        Optional<Company> sellerOptional = companyRepository.findByCrn(sellerCrn);
        if (sellerOptional.isEmpty()) {
            throw new BadRequestException("Seller with this crn is not present in db");
        }
        String medicineName = purchaseOrder.getMedicineName();
        Optional<Medicine> medicineOptional = medicineRepository.findByName(medicineName);
        if (medicineOptional.isEmpty()) {
            throw new BadRequestException("Medicine with this name not present");
        }
        verifyRole(purchaseOrder.getOrgRole());
        return purchaseOrderRepository.save(purchaseOrder);
    }
}
