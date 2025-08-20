package com.billingapp.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BillRequest {
    
    @NotBlank(message = "Bill number is required")
    private String billNumber;
    
    @NotBlank(message = "Customer name is required")
    private String customerName;
    
    private String phoneNumber;
    private String address;
    
    @NotEmpty(message = "Items list cannot be empty")
    @Valid
    private List<BillItemRequest> items = new ArrayList<>();
    
    @NotNull(message = "Subtotal is required")
    @Positive(message = "Subtotal must be positive")
    private BigDecimal subtotal;
    
    @NotNull(message = "Tax amount is required")
    @PositiveOrZero(message = "Tax amount must be positive or zero")
    private BigDecimal taxAmount;
    
    @NotNull(message = "Discount amount is required")
    @PositiveOrZero(message = "Discount amount must be positive or zero")
    private BigDecimal discountAmount;
    
    @NotNull(message = "Grand total is required")
    @Positive(message = "Grand total must be positive")
    private BigDecimal grandTotal;
    
    @NotBlank(message = "Type is required")
    private String type;
    
    @NotNull(message = "Bill date is required")
    private LocalDateTime billDate;
    
    // Constructors
    public BillRequest() {}
    
    // Getters and Setters
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
    
    public List<BillItemRequest> getItems() {
        return items;
    }
    
    public void setItems(List<BillItemRequest> items) {
        this.items = items;
    }
    
    public BigDecimal getSubtotal() {
        return subtotal;
    }
    
    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
    
    public BigDecimal getTaxAmount() {
        return taxAmount;
    }
    
    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }
    
    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }
    
    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
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
    
    public static class BillItemRequest {
        @NotNull(message = "Item ID is required")
        private Long itemId;
        
        @NotBlank(message = "Item name is required")
        private String itemName;
        
        @NotNull(message = "MRP is required")
        @Positive(message = "MRP must be positive")
        private BigDecimal mrp;
        
        @NotNull(message = "Sell price is required")
        @Positive(message = "Sell price must be positive")
        private BigDecimal sellPrice;
        
        @NotNull(message = "Price is required")
        @Positive(message = "Price must be positive")
        private BigDecimal price;
        
        @NotNull(message = "Quantity is required")
        @Positive(message = "Quantity must be positive")
        private Integer quantity;
        
        @NotNull(message = "Total is required")
        @Positive(message = "Total must be positive")
        private BigDecimal total;
        
        @NotBlank(message = "Unit is required")
        private String unit;
        
        // Getters and Setters
        public Long getItemId() {
            return itemId;
        }
        
        public void setItemId(Long itemId) {
            this.itemId = itemId;
        }
        
        public String getItemName() {
            return itemName;
        }
        
        public void setItemName(String itemName) {
            this.itemName = itemName;
        }
        
        public BigDecimal getMrp() {
            return mrp;
        }
        
        public void setMrp(BigDecimal mrp) {
            this.mrp = mrp;
        }
        
        public BigDecimal getSellPrice() {
            return sellPrice;
        }
        
        public void setSellPrice(BigDecimal sellPrice) {
            this.sellPrice = sellPrice;
        }
        
        public BigDecimal getPrice() {
            return price;
        }
        
        public void setPrice(BigDecimal price) {
            this.price = price;
        }
        
        public Integer getQuantity() {
            return quantity;
        }
        
        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }
        
        public BigDecimal getTotal() {
            return total;
        }
        
        public void setTotal(BigDecimal total) {
            this.total = total;
        }
        
        public String getUnit() {
            return unit;
        }
        
        public void setUnit(String unit) {
            this.unit = unit;
        }
    }
}
