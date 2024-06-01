package com.medmap.track.service;

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

    private static void verifyRole(String role) {
        if (!ROLE_SEQUENCE.contains(role)) {
            throw new BadRequestException("Allowed roles are MANUFACTURER, DISTRIBUTOR, TRANSPORTER, RETAILER, and CONSUMER");
        }
    }

    private void verifyStateTransition(Company buyer, Company seller) {
        int buyerRoleIndex = ROLE_SEQUENCE.indexOf(buyer.getOrgRole());
        int sellerRoleIndex = ROLE_SEQUENCE.indexOf(seller.getOrgRole());

        if (buyerRoleIndex != sellerRoleIndex + 1) {
            throw new BadRequestException("Invalid state transition from " + seller.getOrgRole() + " to " + buyer.getOrgRole());
        }
    }

    @Override
    public Medicine saveMedicine(Medicine medicine) {
        Optional<Medicine> medicineOptional = medicineRepository.findByName(medicine.getName());
        if (medicineOptional.isPresent()) {
            throw new BadRequestException("Medicine with this name already present");
        }
        Optional<Company> companyOptional = companyRepository.findByCrn(medicine.getManufacturer().getCrn());
        if (companyOptional.isEmpty()) {
            throw new BadRequestException("Company with this crn not present");
        }
        Company company = companyOptional.get();
        if (!StringUtils.equals(company.getOrgRole(), "MANUFACTURER")) {
            throw new BadRequestException("Only manufacturer can add medicine");
        }
        Medicine savedMedicine = medicineRepository.save(medicine);

        // Add medicine to the manufacturer's inventory
        Inventory inventory = new Inventory();
        inventory.setCompany(company);
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
        Company buyer = companyRepository.findByCrn(purchaseOrder.getBuyer().getCrn())
                .orElseThrow(() -> new BadRequestException("Buyer with this crn not found"));
        Company seller = companyRepository.findByCrn(purchaseOrder.getSeller().getCrn())
                .orElseThrow(() -> new BadRequestException("Seller with this crn not found"));
        Medicine medicine = medicineRepository.findByName(purchaseOrder.getMedicine().getName())
                .orElseThrow(() -> new BadRequestException("Medicine with this name not present"));
        verifyRole(purchaseOrder.getOrgRole());
        verifyStateTransition(buyer, seller);

//        PurchaseOrderContext context = new PurchaseOrderContext();
//
//        // Set initial state based on order's current role
//        getContext(purchaseOrder, context);
//
//        // Update order's role in the database based on new state
//        purchaseOrder.setOrgRole(context.getState().toString());
//        PurchaseOrder purchaseOrderSaved = purchaseOrderRepository.save(purchaseOrder);
//
//        // Process order
//
//        context.printStatus();
//        context.nextState();
//        context.printStatus();

        // Check if the seller has enough quantity
        Inventory sellerInventory = inventoryRepository.findByCompanyAndMedicine(seller, medicine)
                .orElseThrow(() -> new BadRequestException("Seller does not have this medicine in inventory"));

        if (sellerInventory.getQuantity() < purchaseOrder.getQuantity()) {
            throw new BadRequestException("Seller does not have enough quantity of the medicine");
        }

        // Deduct the quantity from the seller's inventory
        sellerInventory.setQuantity(sellerInventory.getQuantity() - purchaseOrder.getQuantity());
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

        buyerInventory.setQuantity(buyerInventory.getQuantity() + purchaseOrder.getQuantity());
        inventoryRepository.save(buyerInventory);

        return purchaseOrderRepository.save(purchaseOrder);

//        return purchaseOrderSaved;
    }

    private static void getContext(PurchaseOrder purchaseOrder, PurchaseOrderContext context) {
        switch (purchaseOrder.getOrgRole()) {
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
                throw new BadRequestException("Invalid role: " + purchaseOrder.getOrgRole());
        }
    }
}
