package com.tsm.shop.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.squareup.square.models.CatalogObject;
import com.tsm.shop.model.entity.Product;
import com.tsm.shop.service.ProductService;
import com.tsm.shop.service.SquareService;

import java.util.List;

@RestController
@RequestMapping("/api/square")
public class SquareController {
    private final SquareService squareService;
    private final ProductService productService;

    public SquareController(SquareService squareService, ProductService productService) {
        this.squareService = squareService;
        this.productService = productService;
    }

    @PostMapping("/sync-product/{productId}")
    public ResponseEntity<String> syncProductToSquare(@PathVariable Long productId) {
        try {
            Product product = productService.getProduct(productId);
            String squareId = squareService.createCatalogItem(product);
            
            // Update product with Square ID
            product.setSquareCatalogId(squareId);
            productService.updateProduct(product);
            
            return ResponseEntity.ok(squareId);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error syncing product: " + e.getMessage());
        }
    }

    @GetMapping("/catalog")
    public ResponseEntity<List<CatalogObject>> listCatalog() {
        try {
            List<CatalogObject> catalog = squareService.listCatalogItems();
            return ResponseEntity.ok(catalog);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @DeleteMapping("/catalog/{squareItemId}")
    public ResponseEntity<Void> deleteCatalogItem(@PathVariable String squareItemId) {
        try {
            // First find the product in your database
            Product product = productService.findBySquareCatalogId(squareItemId);
            if (product != null) {
                // Clear the Square IDs
                product.setSquareCatalogId(null);
                product.setSquareItemVariationId(null);
                productService.updateProduct(product);
            }
            
            // Then delete from Square
            squareService.deleteCatalogItem(squareItemId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}