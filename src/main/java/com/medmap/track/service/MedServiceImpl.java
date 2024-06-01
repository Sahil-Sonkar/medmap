package com.medmap.track.service;

import com.medmap.track.dto.MedicineDto;
import com.medmap.track.dto.PurchaseOrderDto;
import com.medmap.track.exception.BadRequestException;
import com.medmap.track.model.*;
import com.medmap.track.repository.CompanyRepository;
import com.medmap.track.repository.InventoryRepository;
import com.medmap.track.repository.MedicineRepository;
import com.medmap.track.repository.PurchaseOrderRepository;
import com.medmap.track.state.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    private static void getContext(String buyerRole, PurchaseOrderContext context) {
        switch (buyerRole) {
            case "MANUFACTURER":
                context.setState(new ManufacturerState());
                break;
            case "DISTRIBUTOR":
                context.setState(new DistributorState());
                break;
            case "TRANSPORTER":
                context.setState(new TransporterState());
                break;
            case "RETAILER":
                context.setState(new RetailerState());
                break;
            case "CONSUMER":
                context.setState(new ConsumerState());
                break;
            default:
                throw new BadRequestException("Invalid role: " + buyerRole);
        }
    }

    @Override
    public Medicine saveMedicine(MedicineDto medicineDto) {
        medicineRepository.findByName(medicineDto.getName())
                .ifPresent(medicine -> {throw new BadRequestException("Medicine with this name already present");});
        Company manufacturer = companyRepository.findByCrn(medicineDto.getManufacturerCrn())
                .orElseThrow(() -> new BadRequestException("Company with this crn not present"));
        if (!StringUtils.equals(manufacturer.getOrgRole(), "MANUFACTURER")) {
            throw new BadRequestException("Only manufacturer can add medicine");
        }
        Medicine medicine = new Medicine();
        medicine.setName(medicineDto.getName());
        medicine.setManufacturer(manufacturer);
        medicine.setManufactureDate(medicineDto.getManufactureDate());
        medicine.setExpirationDate(medicineDto.getExpirationDate());
        medicine.setInitialQuantity(medicineDto.getInitialQuantity());
        Medicine savedMedicine = medicineRepository.save(medicine);

        // Add medicine to the manufacturer's inventory
        Inventory inventory = new Inventory();
        inventory.setCompany(manufacturer);
        inventory.setMedicine(savedMedicine);
        inventory.setQuantity(medicine.getInitialQuantity());
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

        PurchaseOrderContext context = new PurchaseOrderContext();

        // Set initial state based on order's current role
        getContext(purchaseOrder.getBuyer().getOrgRole(), context);

        // Update order's role in the database based on new state
        PurchaseOrder purchaseOrderSaved = purchaseOrderRepository.save(purchaseOrder);

        // Process order

        context.printStatus();
        context.nextState();
        context.printStatus();

        return purchaseOrderSaved;
    }
}
