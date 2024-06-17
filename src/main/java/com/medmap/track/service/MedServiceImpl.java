package com.medmap.track.service;

import com.medmap.track.dto.MedicineDto;
import com.medmap.track.dto.PurchaseOrderDto;
import com.medmap.track.exception.BadRequestException;
import com.medmap.track.model.*;
import com.medmap.track.repository.CompanyRepository;
import com.medmap.track.repository.InventoryRepository;
import com.medmap.track.repository.MedicineRepository;
import com.medmap.track.repository.PurchaseOrderRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MedServiceImpl implements MedService {

    @Autowired
    private MedicineRepository medicineRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    private static final List<String> ROLE_SEQUENCE = List.of(
            "MANUFACTURER", "DISTRIBUTOR", "TRANSPORTER", "RETAILER", "CONSUMER"
    );

    private void verifyStateTransition(Company buyer, Company seller) {
        int buyerRoleIndex = ROLE_SEQUENCE.indexOf(buyer.getOrgRole());
        int sellerRoleIndex = ROLE_SEQUENCE.indexOf(seller.getOrgRole());

        if (buyerRoleIndex != sellerRoleIndex + 1) {
            throw new BadRequestException("Invalid state transition from " + seller.getOrgRole() + " to " + buyer.getOrgRole());
        }
    }

    @Override
    public Medicine saveMedicine(MedicineDto medicineDto) {
        Optional<Medicine> medicineOptional = medicineRepository.findByName(medicineDto.getName());
        Company manufacturer = companyRepository.findByCrn(medicineDto.getManufacturerCrn())
                .orElseThrow(() -> new BadRequestException("Company with this crn not present"));
        if (!StringUtils.equals(manufacturer.getOrgRole(), "MANUFACTURER")) {
            throw new BadRequestException("Only manufacturer can add medicine");
        }
        Medicine medicine;
        if (medicineOptional.isPresent()) {
            medicine = medicineOptional.get();
            medicine.setInitialQuantity(medicine.getInitialQuantity() + medicineDto.getInitialQuantity());
        } else {
            medicine = new Medicine();
            medicine.setInitialQuantity(medicineDto.getInitialQuantity());
        }
        medicine.setName(medicineDto.getName());
        medicine.setManufacturer(manufacturer);
        medicine.setManufactureDate(medicineDto.getManufactureDate());
        medicine.setExpirationDate(medicineDto.getExpirationDate());

        Medicine savedMedicine = medicineRepository.save(medicine);

        // Add medicine to the manufacturer's inventory
        Optional<Inventory> inventoryOptional = inventoryRepository.findByCompanyAndMedicine(manufacturer, medicine);
        Inventory inventory;
        if (inventoryOptional.isPresent()) {
            inventory = inventoryOptional.get();
            inventory.setQuantity(inventory.getQuantity() + medicine.getInitialQuantity());
        } else {
            inventory = new Inventory();
            inventory.setQuantity(medicine.getInitialQuantity());
        }
        inventory.setCompany(manufacturer);
        inventory.setMedicine(savedMedicine);

        inventoryRepository.save(inventory);

        return savedMedicine;
    }

    @Override
    public Company saveCompany(Company company) {
        Optional<Company> companyOptional = companyRepository.findByName(company.getName());
        if (companyOptional.isPresent()) {
            throw new BadRequestException("Company with this name already present");
        }
        if (!ROLE_SEQUENCE.contains(company.getOrgRole())) {
            throw new BadRequestException("Allowed roles are MANUFACTURER, DISTRIBUTOR, TRANSPORTER, RETAILER and CONSUMER");
        }
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
    public PurchaseOrder createPurchaseOrder(PurchaseOrderDto purchaseOrderDto) {
        Company buyer = companyRepository.findByCrn(purchaseOrderDto.getBuyerCrn())
                .orElseThrow(() -> new BadRequestException("Buyer with this crn not found"));
        Company seller = companyRepository.findByCrn(purchaseOrderDto.getSellerCrn())
                .orElseThrow(() -> new BadRequestException("Seller with this crn not found"));
        Medicine medicine = medicineRepository.findByName(purchaseOrderDto.getMedicineName())
                .orElseThrow(() -> new BadRequestException("Medicine with this name not present"));
        verifyStateTransition(buyer, seller);

        // Check if the seller has enough quantity
        Inventory sellerInventory = inventoryRepository.findByCompanyAndMedicine(seller, medicine)
                .orElseThrow(() -> new BadRequestException("Seller does not have this medicine in inventory"));

        if (sellerInventory.getQuantity() < purchaseOrderDto.getQuantity()) {
            throw new BadRequestException("Seller does not have enough quantity of the medicine");
        }

        // Deduct the quantity from the seller's inventory
        sellerInventory.setQuantity(sellerInventory.getQuantity() - purchaseOrderDto.getQuantity());
        inventoryRepository.save(sellerInventory);

        // Add the quantity to the buyer's inventory
        Inventory buyerInventory = inventoryRepository.findByCompanyAndMedicine(buyer, medicine)
                .orElseGet(() -> {
                    Inventory newInventory = new Inventory();
                    newInventory.setCompany(buyer);
                    newInventory.setMedicine(medicine);
                    newInventory.setQuantity(0);
                    return newInventory;
                });

        buyerInventory.setQuantity(buyerInventory.getQuantity() + purchaseOrderDto.getQuantity());
        inventoryRepository.save(buyerInventory);

        PurchaseOrder purchaseOrder = new PurchaseOrder();
        purchaseOrder.setBuyer(buyer);
        purchaseOrder.setSeller(seller);
        purchaseOrder.setMedicine(medicine);
        purchaseOrder.setQuantity(purchaseOrderDto.getQuantity());

        return purchaseOrderRepository.save(purchaseOrder);
    }

    @Override
    public List<PurchaseOrder> getMedicineHistory(String medicineName) {
        List<PurchaseOrder> purchaseOrders = purchaseOrderRepository.findByMedicineName(medicineName);
        if (purchaseOrders.isEmpty()) {
            throw new BadRequestException("No purchase orders found for this medicine");
        }
        return purchaseOrders;
    }

    @Override
    public List<String> getFullMedicineHistory(String medicineName) {
        List<PurchaseOrder> purchaseOrders = getMedicineHistory(medicineName);
        List<String> history = new ArrayList<>();

        for (PurchaseOrder order : purchaseOrders) {
            Company buyer = companyRepository.findByCrn(order.getBuyer().getCrn())
                    .orElseThrow(() -> new BadRequestException("Buyer not found: " + order.getBuyer().getCrn()));
            Company seller = companyRepository.findByCrn(order.getSeller().getCrn())
                    .orElseThrow(() -> new BadRequestException("Seller not found: " + order.getSeller().getCrn()));

            history.add(String.format("Medicine '%s' moved from %s to %s with quantity %d",
                    order.getMedicine().getName(),
                    seller.getName(),
                    buyer.getName(),
                    order.getQuantity()));
        }

        return history;
    }

    public Map<String, Map<String, Object>> getMedicineDistribution(String medicineName) {
        List<PurchaseOrder> purchaseOrders = getMedicineHistory(medicineName);
        Map<String, Map<String, Object>> distribution = new LinkedHashMap<>();

        // Initialize the manufacturer's quantity with the initial quantity of the medicine
        Medicine medicine = medicineRepository.findByName(medicineName)
                .orElseThrow(() -> new BadRequestException("Medicine with this name not present"));
        Company manufacturer = medicine.getManufacturer();
        distribution.put("MANUFACTURER", new HashMap<>());
        distribution.get("MANUFACTURER").put("crn", manufacturer.getCrn());
        distribution.get("MANUFACTURER").put("name", manufacturer.getName());
        distribution.get("MANUFACTURER").put("location", manufacturer.getLocation());
        distribution.get("MANUFACTURER").put("currentQuantity", medicine.getInitialQuantity());


        // Initialize quantities
        for (PurchaseOrder order : purchaseOrders) {
            Company seller = companyRepository.findByCrn(order.getSeller().getCrn())
                    .orElseThrow(() -> new BadRequestException("Seller not found: " + order.getSeller().getCrn()));
            Company buyer = companyRepository.findByCrn(order.getBuyer().getCrn())
                    .orElseThrow(() -> new BadRequestException("Buyer not found: " + order.getBuyer().getCrn()));

            distribution.computeIfAbsent(seller.getOrgRole(), k -> {
                Map<String, Object> details = new HashMap<>();
                details.put("crn", seller.getCrn());
                details.put("name", seller.getName());
                details.put("location", seller.getLocation());
                details.put("currentQuantity", 0);
                return details;
            });

            distribution.computeIfAbsent(buyer.getOrgRole(), k -> {
                Map<String, Object> details = new HashMap<>();
                details.put("crn", buyer.getCrn());
                details.put("name", buyer.getName());
                details.put("location", buyer.getLocation());
                details.put("currentQuantity", 0);
                return details;
            });

            int sellerCurrentQuantity = (int) distribution.get(seller.getOrgRole()).get("currentQuantity");
            int buyerCurrentQuantity = (int) distribution.get(buyer.getOrgRole()).get("currentQuantity");

            // Update quantities
            distribution.get(seller.getOrgRole()).put("currentQuantity", sellerCurrentQuantity - order.getQuantity());
            distribution.get(buyer.getOrgRole()).put("currentQuantity", buyerCurrentQuantity + order.getQuantity());
        }

        return distribution;
    }
}
