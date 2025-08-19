package com.billingapp.repository;

import com.billingapp.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    
    List<Item> findByItemNameContainingIgnoreCase(String itemName);
    
    List<Item> findByCategory(String category);
    
    @Query("SELECT i FROM Item i WHERE i.currentStock <= i.minStockLevel")
    List<Item> findLowStockItems();
    
    @Query("SELECT i FROM Item i WHERE i.itemName LIKE %?1% OR i.category LIKE %?1%")
    List<Item> searchItems(String searchTerm);
    
    boolean existsByItemNameIgnoreCase(String itemName);
}
