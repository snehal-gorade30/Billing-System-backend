package com.billingapp.service;

import com.billingapp.dto.BillRequest;
import com.billingapp.entity.Bill;
import com.billingapp.entity.BillItem;
import com.billingapp.entity.Item;
import com.billingapp.repository.BillRepository;
import com.billingapp.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BillService {
    
    @Autowired
    private BillRepository billRepository;
    
    @Autowired
    private ItemRepository itemRepository;
    
    @Transactional
    public Bill saveBill(BillRequest billRequest) {
        // Create Bill entity
        Bill bill = new Bill();
        bill.setBillNumber(billRequest.getBillNumber());
        bill.setCustomerName(billRequest.getCustomerName());
        bill.setPhoneNumber(billRequest.getPhoneNumber());
        bill.setAddress(billRequest.getAddress());
        bill.setSubtotal(billRequest.getSubtotal());
        bill.setGrandTotal(billRequest.getGrandTotal());
        bill.setType(billRequest.getType());
        bill.setBillDate(LocalDateTime.parse(billRequest.getDate(), DateTimeFormatter.ISO_DATE_TIME));
        
        // Save bill first to get ID
        Bill savedBill = billRepository.save(bill);
        
        // Create and save bill items
        List<BillItem> billItems = new ArrayList<>();
        for (BillRequest.BillItemRequest itemRequest : billRequest.getItems()) {
            BillItem billItem = new BillItem();
            billItem.setBill(savedBill);
            billItem.setItemId(itemRequest.getItemId());
            billItem.setItemName(itemRequest.getItemName());
            billItem.setMrp(itemRequest.getMrp());
            billItem.setSellPrice(itemRequest.getSellPrice());
            billItem.setPrice(itemRequest.getPrice());
            billItem.setQuantity(itemRequest.getQuantity());
            billItem.setTotal(itemRequest.getTotal());
            billItem.setUnit(itemRequest.getUnit());
            
            billItems.add(billItem);
            
            // Update item stock
            Optional<Item> itemOptional = itemRepository.findById(itemRequest.getItemId());
            if (itemOptional.isPresent()) {
                Item item = itemOptional.get();
                int newStock = item.getCurrentStock() - itemRequest.getQuantity();
                if (newStock < 0) {
                    throw new RuntimeException("Insufficient stock for item: " + item.getItemName());
                }
                item.setCurrentStock(newStock);
                itemRepository.save(item);
            } else {
                throw new RuntimeException("Item not found with ID: " + itemRequest.getItemId());
            }
        }
        
        savedBill.setBillItems(billItems);
        return billRepository.save(savedBill);
    }
    
    public List<Bill> getAllBills() {
        return billRepository.findByOrderByBillDateDesc();
    }
    
    public Optional<Bill> getBillById(Long id) {
        return billRepository.findById(id);
    }
    
    public Optional<Bill> getBillByNumber(String billNumber) {
        return billRepository.findByBillNumber(billNumber);
    }
    
    public List<Bill> getBillsByCustomer(String customerName) {
        return billRepository.findByCustomerNameContainingIgnoreCase(customerName);
    }
    
    public List<Bill> getBillsByType(String type) {
        return billRepository.findByType(type);
    }
    
    public List<Bill> getCreditBills() {
        return billRepository.findCreditBills();
    }
    
    public List<Bill> getBillsBetweenDates(LocalDateTime startDate, LocalDateTime endDate) {
        return billRepository.findBillsBetweenDates(startDate, endDate);
    }
}
