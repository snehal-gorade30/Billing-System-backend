package com.billingapp.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "bill_items")
public class BillItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "item_id", nullable = false)
    private Long itemId;
    
    @Column(nullable = false)
    private String itemName;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal mrp;
    
    @Column(name = "sell_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal sellPrice;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    
    @Column(nullable = false)
    private Integer quantity;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;
    
    @Column(nullable = false)
    private String unit;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bill_id", nullable = false)
    @JsonBackReference
    private Bill bill;
    
    // Helper method to calculate total
    public void calculateTotal() {
        if (price != null && quantity != null) {
            this.total = price.multiply(BigDecimal.valueOf(quantity));
        }
    }
}
