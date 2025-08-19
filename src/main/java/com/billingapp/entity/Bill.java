package com.billingapp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "bills")
public class Bill {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Bill number is required")
    @Column(unique = true, nullable = false)
    private String billNumber;
    
    @NotBlank(message = "Customer name is required")
    @Column(nullable = false)
    private String customerName;
    
    @Column
    private String phoneNumber;
    
    @Column(columnDefinition = "TEXT")
    private String address;
    
    @NotNull(message = "Subtotal is required")
    @Positive(message = "Subtotal must be positive")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;
    
    @NotNull(message = "Grand total is required")
    @Positive(message = "Grand total must be positive")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal grandTotal;
    
    @NotBlank(message = "Bill type is required")
    @Column(nullable = false)
    private String type; // cash, credit, whatsapp
    
    @Column(nullable = false)
    private LocalDateTime billDate;
    
    @OneToMany(mappedBy = "bill", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<BillItem> billItems;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    // Constructors
    public Bill() {}
    
    public Bill(String billNumber, String customerName, String phoneNumber, String address, 
                BigDecimal subtotal, BigDecimal grandTotal, String type, LocalDateTime billDate) {
        this.billNumber = billNumber;
        this.customerName = customerName;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.subtotal = subtotal;
        this.grandTotal = grandTotal;
        this.type = type;
        this.billDate = billDate;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getBillNumber() {
        return billNumber;
    }
    
    public void setBillNumber(String billNumber) {
        this.billNumber = billNumber;
    }
    
    public String getCustomerName() {
        return customerName;
    }
    
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public BigDecimal getSubtotal() {
        return subtotal;
    }
    
    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
    
    public BigDecimal getGrandTotal() {
        return grandTotal;
    }
    
    public void setGrandTotal(BigDecimal grandTotal) {
        this.grandTotal = grandTotal;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public LocalDateTime getBillDate() {
        return billDate;
    }
    
    public void setBillDate(LocalDateTime billDate) {
        this.billDate = billDate;
    }
    
    public List<BillItem> getBillItems() {
        return billItems;
    }
    
    public void setBillItems(List<BillItem> billItems) {
        this.billItems = billItems;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
