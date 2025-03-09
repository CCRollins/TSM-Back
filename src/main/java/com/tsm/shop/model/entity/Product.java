package com.tsm.shop.model.entity;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    private String description;
    
    @Column(nullable = false)
    private BigDecimal price;
    
    @Column(name = "image_url")
    private String imageUrl;
    
    private Integer inventory;
    
    @Column(name = "square_catalog_id")
    private String squareCatalogId;
    
    @Column(name = "square_item_variation_id")
    private String squareItemVariationId;

    // Getters
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Integer getInventory() {
        return inventory;
    }

    public String getSquareCatalogId() {
        return squareCatalogId;
    }

    public String getSquareItemVariationId() {
        return squareItemVariationId;
    }

    @ManyToMany
    @JoinTable(
        name = "product_categories",
        joinColumns = @JoinColumn(name = "product_id"),
        inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> categories = new HashSet<>();

    // Add getter and setter for categories
    public Set<Category> getCategories() {
        return categories;
    }

    public void setCategories(Set<Category> categories) {
        this.categories = categories;
    }

    @Column(nullable = false)
    private Boolean active = true;  // Default to true
    
    // Add getter and setter
    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    @Column(name = "reorder_point")
    private Integer reorderPoint;
    
    // Add getter and setter
    public Integer getReorderPoint() {
        return reorderPoint;
    }

    public void setReorderPoint(Integer reorderPoint) {
        this.reorderPoint = reorderPoint;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setInventory(Integer inventory) {
        this.inventory = inventory;
    }

    public void setSquareCatalogId(String squareCatalogId) {
        this.squareCatalogId = squareCatalogId;
    }

    public void setSquareItemVariationId(String squareItemVariationId) {
        this.squareItemVariationId = squareItemVariationId;
    }
}