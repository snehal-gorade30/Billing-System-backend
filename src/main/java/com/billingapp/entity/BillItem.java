package com.billingapp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import com.fasterxml.jackson.annotation.JsonBackReference;

import java.math.BigDecimal;

@Entity
@Table(name = "bill_items")
public class BillItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bill_id", nullable = false)
    @JsonBackReference
    private Bill bill;
    
    @NotNull(message = "Item ID is required")
    @Column(nullable = false)
    private Long itemId;
    
    @NotBlank(message = "Item name is required")
    @Column(nullable = false)
    private String itemName;
    
    @NotNull(message = "MRP is required")
    @Positive(message = "MRP must be positive")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal mrp;
    
    @NotNull(message = "Sell price is required")
    @Positive(message = "Sell price must be positive")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal sellPrice;
    
    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    
    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be positive")
    @Column(nullable = false)
    private Integer quantity;
    
    @NotNull(message = "Total is required")
    @Positive(message = "Total must be positive")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;
    
    @Column(nullable = false)
    private String unit;
    
    // Constructors
    public BillItem() {}
    
    public BillItem(Bill bill, Long itemId, String itemName, BigDecimal mrp, BigDecimal sellPrice,
                    BigDecimal price, Integer quantity, BigDecimal total, String unit) {
        this.bill = bill;
        this.itemId = itemId;
        this.itemName = itemName;
        this.mrp = mrp;
        this.sellPrice = sellPrice;
        this.price = price;
        this.quantity = quantity;
        this.total = total;
        this.unit = unit;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Bill getBill() {
        return bill;
    }
    
    public void setBill(Bill bill) {
        this.bill = bill;
    }
    
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
