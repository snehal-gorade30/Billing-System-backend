package com.billingapp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "items")
public class Item {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Item name is required")
    @Column(name = "item_name", nullable = false)
    private String itemName;
    
    @Column(name = "category")
    private String category;
    
    @NotNull(message = "Purchase price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Purchase price must be greater than 0")
    @Column(name = "purchase_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal purchasePrice;
    
    @NotNull(message = "MRP is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "MRP must be greater than 0")
    @Column(name = "mrp", nullable = false, precision = 10, scale = 2)
    private BigDecimal mrp;
    
    @NotNull(message = "Sell price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Sell price must be greater than 0")
    @Column(name = "sell_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal sellPrice;
    
    @DecimalMin(value = "0.0", message = "Min sell price must be greater than or equal to 0")
    @Column(name = "min_sell_price", precision = 10, scale = 2)
    private BigDecimal minSellPrice;
    
    @NotNull(message = "Current stock is required")
    @Min(value = 0, message = "Current stock must be greater than or equal to 0")
    @Column(name = "current_stock", nullable = false)
    private Integer currentStock;
    
    @NotNull(message = "Min stock level is required")
    @Min(value = 0, message = "Min stock level must be greater than or equal to 0")
    @Column(name = "min_stock_level", nullable = false)
    private Integer minStockLevel;
    
    @NotBlank(message = "Unit is required")
    @Column(name = "unit", nullable = false)
    private String unit;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructors
    public Item() {}
    
    public Item(String itemName, String category, BigDecimal purchasePrice, BigDecimal mrp, 
                BigDecimal sellPrice, BigDecimal minSellPrice, Integer currentStock, 
                Integer minStockLevel, String unit) {
        this.itemName = itemName;
        this.category = category;
        this.purchasePrice = purchasePrice;
        this.mrp = mrp;
        this.sellPrice = sellPrice;
        this.minSellPrice = minSellPrice;
        this.currentStock = currentStock;
        this.minStockLevel = minStockLevel;
        this.unit = unit;
    }
    
    // Lifecycle methods
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Calculated field
    public BigDecimal getStockValue() {
        return purchasePrice.multiply(new BigDecimal(currentStock));
    }
    
    public boolean isLowStock() {
        return currentStock <= minStockLevel;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public BigDecimal getPurchasePrice() { return purchasePrice; }
    public void setPurchasePrice(BigDecimal purchasePrice) { this.purchasePrice = purchasePrice; }
    
    public BigDecimal getMrp() { return mrp; }
    public void setMrp(BigDecimal mrp) { this.mrp = mrp; }
    
    public BigDecimal getSellPrice() { return sellPrice; }
    public void setSellPrice(BigDecimal sellPrice) { this.sellPrice = sellPrice; }
    
    public BigDecimal getMinSellPrice() { return minSellPrice; }
    public void setMinSellPrice(BigDecimal minSellPrice) { this.minSellPrice = minSellPrice; }
    
    public Integer getCurrentStock() { return currentStock; }
    public void setCurrentStock(Integer currentStock) { this.currentStock = currentStock; }
    
    public Integer getMinStockLevel() { return minStockLevel; }
    public void setMinStockLevel(Integer minStockLevel) { this.minStockLevel = minStockLevel; }
    
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
