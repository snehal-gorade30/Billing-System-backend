package com.billingapp.controller;

import com.billingapp.dto.BillRequest;
import com.billingapp.dto.BillResponse;
import com.billingapp.entity.Bill;
import com.billingapp.service.BillService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/bills")
@CrossOrigin(origins = "http://localhost:3000")
public class BillController {

    private final BillService billService;
    private static final Logger log = LoggerFactory.getLogger(BillController.class);

    @Autowired
    public BillController(BillService billService) {
        this.billService = billService;
    }

    @PostMapping
    public ResponseEntity<?> createBill(@Valid @RequestBody BillRequest billRequest) {
        try {
            // Validate bill items
            if (billRequest.getItems() == null || billRequest.getItems().isEmpty()) {
                return ResponseEntity.badRequest().body("Bill must contain at least one item");
            }
            
            // Set bill date to current time if not provided
            if (billRequest.getBillDate() == null) {
                billRequest.setBillDate(LocalDateTime.now());
            }
            
            // Generate bill number if not provided
            if (billRequest.getBillNumber() == null || billRequest.getBillNumber().trim().isEmpty()) {
                String billNumber = "BILL-" + System.currentTimeMillis();
                billRequest.setBillNumber(billNumber);
            }
            
            Bill savedBill = billService.saveBill(billRequest);
            return new ResponseEntity<>(BillResponse.fromEntity(savedBill), HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creating bill: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<BillResponse>> getAllBills() {
        List<Bill> bills = billService.findAll();
        List<BillResponse> response = bills.stream()
                .map(BillResponse::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getBillById(@PathVariable Long id) {
        return billService.findById(id)
                .map(bill -> ResponseEntity.ok(BillResponse.fromEntity(bill)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/number/{billNumber}")
    public ResponseEntity<?> getBillByNumber(@PathVariable String billNumber) {
        return billService.findByBillNumber(billNumber)
                .map(bill -> ResponseEntity.ok(BillResponse.fromEntity(bill)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchBills(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String type) {
        
        try {
            // If no search parameters provided, return all bills
            if (q == null && startDate == null && endDate == null && type == null) {
                List<Bill> allBills = billService.findAll();
                List<BillResponse> response = allBills.stream()
                        .map(BillResponse::fromEntity)
                        .collect(Collectors.toList());
                return ResponseEntity.ok(response);
            }
            
            List<Bill> bills = billService.searchBills(q, startDate, endDate, type);
            List<BillResponse> response = bills.stream()
                    .map(BillResponse::fromEntity)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error searching bills: " + e.getMessage());
        }
    }

    @GetMapping("/customer/{customerName}")
    public ResponseEntity<List<BillResponse>> getBillsByCustomer(@PathVariable String customerName) {
        List<Bill> bills = billService.searchBills(customerName, null, null, null);
        List<BillResponse> response = bills.stream()
                .map(BillResponse::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<BillResponse>> getBillsByType(@PathVariable String type) {
        List<Bill> bills = billService.searchBills(null, null, null, type);
        List<BillResponse> response = bills.stream()
                .map(BillResponse::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/credit")
    public ResponseEntity<List<BillResponse>> getCreditBills() {
        List<Bill> bills = billService.searchBills(null, null, null, "CREDIT");
        List<BillResponse> response = bills.stream()
                .map(BillResponse::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<BillResponse>> getBillsBetweenDates(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        List<Bill> bills = billService.searchBills(null, startDate, endDate, null);
        List<BillResponse> response = bills.stream()
                .map(BillResponse::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBill(@PathVariable Long id) {
        try {
            billService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error deleting bill: " + e.getMessage());
        }
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> generatePdf(@PathVariable Long id) {
        try {
            Optional<Bill> billOptional = billService.findById(id);
            if (billOptional.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            Bill bill = billOptional.get();
            byte[] pdf = billService.generatePdf(bill);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "bill_" + id + ".pdf");
            return ResponseEntity.ok().headers(headers).body(pdf);
        } catch (Exception e) {
            log.error("Error generating PDF for bill {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error generating PDF: " + e.getMessage()).getBytes());
        }
    }
}
