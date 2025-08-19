package com.billingapp.controller;

import com.billingapp.dto.BillRequest;
import com.billingapp.entity.Bill;
import com.billingapp.service.BillService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/bills")
@CrossOrigin(origins = "http://localhost:3000")
public class BillController {
    
    @Autowired
    private BillService billService;
    
    @PostMapping
    public ResponseEntity<?> createBill(@Valid @RequestBody BillRequest billRequest) {
        try {
            Bill savedBill = billService.saveBill(billRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedBill);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error creating bill: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal server error: " + e.getMessage());
        }
    }
    
    @GetMapping
    public ResponseEntity<List<Bill>> getAllBills() {
        List<Bill> bills = billService.getAllBills();
        return ResponseEntity.ok(bills);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Bill> getBillById(@PathVariable Long id) {
        Optional<Bill> bill = billService.getBillById(id);
        return bill.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/number/{billNumber}")
    public ResponseEntity<Bill> getBillByNumber(@PathVariable String billNumber) {
        Optional<Bill> bill = billService.getBillByNumber(billNumber);
        return bill.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/customer/{customerName}")
    public ResponseEntity<List<Bill>> getBillsByCustomer(@PathVariable String customerName) {
        List<Bill> bills = billService.getBillsByCustomer(customerName);
        return ResponseEntity.ok(bills);
    }
    
    @GetMapping("/type/{type}")
    public ResponseEntity<List<Bill>> getBillsByType(@PathVariable String type) {
        List<Bill> bills = billService.getBillsByType(type);
        return ResponseEntity.ok(bills);
    }
    
    @GetMapping("/credit")
    public ResponseEntity<List<Bill>> getCreditBills() {
        List<Bill> creditBills = billService.getCreditBills();
        return ResponseEntity.ok(creditBills);
    }
    
    @GetMapping("/date-range")
    public ResponseEntity<List<Bill>> getBillsBetweenDates(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            LocalDateTime start = LocalDateTime.parse(startDate);
            LocalDateTime end = LocalDateTime.parse(endDate);
            List<Bill> bills = billService.getBillsBetweenDates(start, end);
            return ResponseEntity.ok(bills);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
