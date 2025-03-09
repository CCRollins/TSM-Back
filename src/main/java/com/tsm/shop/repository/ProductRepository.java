package com.tsm.shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tsm.shop.model.entity.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // Basic finder methods
    Optional<Product> findByNameIgnoreCase(String name);
    List<Product> findByCategories_Name(String categoryName);
    
    // Inventory management
    List<Product> findByInventoryLessThan(Integer threshold);
    List<Product> findByInventoryGreaterThan(Integer threshold);
    
    // Price range queries
    List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
    
    // Search functionality
    @Query("SELECT p FROM Product p WHERE " +
           "LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Product> searchProducts(@Param("searchTerm") String searchTerm);
    
    // Custom query for featured or active products
    List<Product> findByActiveTrue();
    
    // Find products that need inventory restock
    @Query("SELECT p FROM Product p WHERE p.inventory <= p.reorderPoint")
    List<Product> findProductsNeedingRestock();

    Optional<Product> findBySquareCatalogId(String squareCatalogId);
}