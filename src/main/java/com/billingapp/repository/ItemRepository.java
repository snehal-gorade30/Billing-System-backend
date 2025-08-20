package com.billingapp.repository;

import com.billingapp.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    
    List<Item> findByItemNameContainingIgnoreCase(String itemName);
    
    List<Item> findByCategory(String category);
    
    @Query("SELECT i FROM Item i WHERE i.currentStock <= i.minStockLevel")
    List<Item> findLowStockItems();
    
    @Query("SELECT i FROM Item i WHERE LOWER(i.itemName) LIKE LOWER(concat('%', :query, '%')) OR LOWER(i.barcode) LIKE LOWER(concat('%', :query, '%'))")
    List<Item> searchByNameOrBarcode(@Param("query") String query);
    
    boolean existsByItemNameIgnoreCase(String itemName);
    
    Optional<Item> findByBarcode(String barcode);
    
    boolean existsByBarcode(String barcode);
    
    @Modifying
    @Transactional
    @Query("UPDATE Item i SET i.currentStock = i.currentStock - :quantity WHERE i.id = :itemId AND i.currentStock >= :quantity")
    int reduceStock(@Param("itemId") Long itemId, @Param("quantity") int quantity);
    
    @Modifying
    @Transactional
    @Query("UPDATE Item i SET i.currentStock = i.currentStock + :quantity WHERE i.id = :itemId")
    int addStock(@Param("itemId") Long itemId, @Param("quantity") int quantity);
    
    List<Item> findByItemNameStartingWithIgnoreCase(String searchTerm);
    
    @Query("SELECT i.currentStock FROM Item i WHERE i.id = :itemId")
    Integer getCurrentStock(@Param("itemId") Long itemId);
}
