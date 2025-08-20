package com.billingapp.service;

import com.billingapp.dto.BillRequest;
import com.billingapp.entity.Bill;
import com.billingapp.entity.BillItem;
import com.billingapp.repository.BillRepository;
import com.billingapp.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

@Slf4j
@Service
@RequiredArgsConstructor
public class BillService {

    private final BillRepository billRepository;
    private final ItemRepository itemRepository;
    private final InventoryService inventoryService;

    @Transactional
    public Bill saveBill(BillRequest billRequest) {
        try {
            Bill bill = new Bill();
            bill.setBillNumber(billRequest.getBillNumber());
            bill.setCustomerName(billRequest.getCustomerName());
            bill.setPhoneNumber(billRequest.getPhoneNumber());
            bill.setAddress(billRequest.getAddress());
            bill.setSubtotal(billRequest.getSubtotal());
            bill.setTaxAmount(billRequest.getTaxAmount());
            bill.setDiscountAmount(billRequest.getDiscountAmount());
            bill.setGrandTotal(billRequest.getGrandTotal());
            bill.setType(billRequest.getType());
            bill.setBillDate(billRequest.getBillDate() != null ? billRequest.getBillDate() : LocalDateTime.now());

            // Save bill items
            List<BillItem> items = billRequest.getItems().stream()
                    .map(itemRequest -> {
                        BillItem item = new BillItem();
                        item.setItemId(itemRequest.getItemId());
                        item.setItemName(itemRequest.getItemName());
                        item.setMrp(itemRequest.getMrp());
                        item.setSellPrice(itemRequest.getSellPrice());
                        item.setPrice(itemRequest.getPrice());
                        item.setQuantity(itemRequest.getQuantity());
                        item.setTotal(itemRequest.getTotal());
                        item.setUnit(itemRequest.getUnit());
                        item.setBill(bill);
                        return item;
                    })
                    .collect(Collectors.toList());

            bill.getItems().addAll(items);

            // Update inventory
            updateInventoryForBill(items);

            return billRepository.save(bill);
        } catch (Exception e) {
            log.error("Error saving bill: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to save bill: " + e.getMessage(), e);
        }
    }

    @Transactional
    protected void updateInventoryForBill(List<BillItem> items) {
        for (BillItem item : items) {
            inventoryService.updateStockAfterSale(item.getItemId(), item.getQuantity());
        }
    }

    public List<Bill> searchBills(String query, LocalDate startDate, LocalDate endDate, String type) {
        // Convert LocalDate to LocalDateTime for the query
        LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime endDateTime = endDate != null ? endDate.plusDays(1).atStartOfDay() : null;
        
        // Use simple queries and filter in application layer
        List<Bill> bills = billRepository.findAllWithItems();
        
        return bills.stream()
                .filter(bill -> {
                    // Filter by search query
                    if (query != null && !query.trim().isEmpty()) {
                        String lowerQuery = query.toLowerCase();
                        return bill.getBillNumber().toLowerCase().contains(lowerQuery) ||
                               bill.getCustomerName().toLowerCase().contains(lowerQuery) ||
                               (bill.getPhoneNumber() != null && bill.getPhoneNumber().toLowerCase().contains(lowerQuery));
                    }
                    return true;
                })
                .filter(bill -> {
                    // Filter by date range
                    if (startDateTime != null && bill.getBillDate().isBefore(startDateTime)) {
                        return false;
                    }
                    if (endDateTime != null && bill.getBillDate().isAfter(endDateTime)) {
                        return false;
                    }
                    return true;
                })
                .filter(bill -> {
                    // Filter by type
                    if (type != null && !type.trim().isEmpty() && !"all".equals(type)) {
                        return type.equals(bill.getType());
                    }
                    return true;
                })
                .collect(Collectors.toList());
    }

    public List<Bill> findAll() {
        // Use the optimized query with JOIN FETCH to avoid N+1 problem
        return billRepository.findAllWithItems();
    }

    public Optional<Bill> findById(Long id) {
        return billRepository.findById(id);
    }

    public Optional<Bill> findByBillNumber(String billNumber) {
        return billRepository.findByBillNumber(billNumber);
    }

    @Transactional
    public void deleteById(Long id) {
        billRepository.deleteById(id);
    }

    public byte[] generatePdf(Bill bill) {
        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);
            
            try (PDPageContentStream content = new PDPageContentStream(document, page)) {
                // Use standard PDF fonts directly
                var fontBold = PDType1Font.HELVETICA_BOLD;
                var fontNormal = PDType1Font.HELVETICA;
                
                // Add header
                addText(content, "INVOICE", 50, 750, fontBold, 18);
                addText(content, "Bill #: " + bill.getBillNumber(), 50, 720, fontNormal, 12);
                addText(content, "Date: " + bill.getBillDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), 400, 720, fontNormal, 12);
                addText(content, "Customer: " + bill.getCustomerName(), 50, 700, fontNormal, 12);
                
                // Add items
                float y = 650;
                addText(content, "#", 50, y, fontBold, 10);
                addText(content, "Item", 100, y, fontBold, 10);
                addText(content, "Qty", 350, y, fontBold, 10);
                addText(content, "Price", 400, y, fontBold, 10);
                addText(content, "Total", 450, y, fontBold, 10);
                y -= 20;
                
                int index = 1;
                for (BillItem item : bill.getItems()) {
                    addText(content, String.valueOf(index++), 50, y, fontNormal, 10);
                    addText(content, item.getItemName(), 100, y, fontNormal, 10);
                    addText(content, String.valueOf(item.getQuantity()), 350, y, fontNormal, 10);
                    addText(content, String.format("%.2f", item.getSellPrice()), 400, y, fontNormal, 10);
                    addText(content, String.format("%.2f", item.getTotal()), 450, y, fontNormal, 10);
                    y -= 15;
                }
                
                // Add totals
                y -= 20;
                addText(content, "Subtotal:", 350, y, fontNormal, 12);
                addText(content, String.format("%.2f", bill.getSubtotal()), 450, y, fontNormal, 12);
                y -= 20;
                
                if (bill.getDiscountAmount().compareTo(BigDecimal.ZERO) > 0) {
                    addText(content, "Discount:", 350, y, fontNormal, 12);
                    addText(content, String.format("-%.2f", bill.getDiscountAmount()), 450, y, fontNormal, 12);
                    y -= 20;
                }
                
                addText(content, "Total:", 350, y, fontBold, 12);
                addText(content, String.format("%.2f", bill.getGrandTotal()), 450, y, fontBold, 12);
                
                // Add footer
                addText(content, "Thank you for your business!", 50, 50, fontNormal, 10);
            }
            
            document.save(baos);
            return baos.toByteArray();
            
        } catch (Exception e) {
            log.error("Error generating PDF: {}", e.getMessage(), e);
            throw new RuntimeException("Error generating PDF", e);
        }
    }
    
    private void addText(PDPageContentStream content, String text, float x, float y, PDType1Font font, int size) throws IOException {
        content.beginText();
        content.setFont(font, size);
        content.newLineAtOffset(x, y);
        content.showText(text);
        content.endText();
    }
}
