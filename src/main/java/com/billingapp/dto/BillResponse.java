package com.billingapp.dto;

import com.billingapp.entity.Bill;
import com.billingapp.entity.BillItem;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BillResponse {
    private Long id;
    private String billNumber;
    private String customerName;
    private String phoneNumber;
    private String address;
    private BigDecimal subtotal;
    private BigDecimal taxAmount;
    private BigDecimal discountAmount;
    private BigDecimal grandTotal;
    private String type;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime billDate;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    
    private List<BillItemResponse> items;
    
    public static BillResponse fromEntity(Bill bill) {
        BillResponse response = new BillResponse();
        response.setId(bill.getId());
        response.setBillNumber(bill.getBillNumber());
        response.setCustomerName(bill.getCustomerName());
        response.setPhoneNumber(bill.getPhoneNumber());
        response.setAddress(bill.getAddress());
        response.setSubtotal(bill.getSubtotal());
        response.setTaxAmount(bill.getTaxAmount());
        response.setDiscountAmount(bill.getDiscountAmount());
        response.setGrandTotal(bill.getGrandTotal());
        response.setType(bill.getType());
        response.setBillDate(bill.getBillDate());
        response.setCreatedAt(bill.getCreatedAt());
        response.setUpdatedAt(bill.getUpdatedAt());
        
        if (bill.getItems() != null && !bill.getItems().isEmpty()) {
            response.setItems(bill.getItems().stream()
                    .map(BillItemResponse::fromEntity)
                    .collect(Collectors.toList()));
        }
        
        return response;
    }
    
    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class BillItemResponse {
        private Long id;
        private Long itemId;
        private String itemName;
        private BigDecimal mrp;
        private BigDecimal sellPrice;
        private BigDecimal price;
        private Integer quantity;
        private BigDecimal total;
        private String unit;
        
        public static BillItemResponse fromEntity(BillItem item) {
            BillItemResponse response = new BillItemResponse();
            response.setId(item.getId());
            response.setItemId(item.getItemId());
            response.setItemName(item.getItemName());
            response.setMrp(item.getMrp());
            response.setSellPrice(item.getSellPrice());
            response.setPrice(item.getPrice());
            response.setQuantity(item.getQuantity());
            response.setTotal(item.getTotal());
            response.setUnit(item.getUnit());
            return response;
        }
    }
}
