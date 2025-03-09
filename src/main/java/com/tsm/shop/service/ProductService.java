package com.tsm.shop.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.squareup.square.SquareClient;
import com.tsm.shop.exception.InvalidRequestException;
import com.tsm.shop.exception.ResourceNotFoundException;
import com.tsm.shop.model.entity.Product;
import com.tsm.shop.repository.ProductRepository;

@Service
@Transactional(readOnly = true)  // Default to readOnly, individual write methods will override
public class ProductService {
    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional  // Override readOnly for this method since it modifies data
    public Product updateProduct(Product product) {
        if (product.getId() == null) {
            throw new InvalidRequestException("Cannot update product without an ID");
        }
        
        // Verify product exists before updating
        productRepository.findById(product.getId())
            .orElseThrow(() -> new ResourceNotFoundException(
                "Product not found with id: " + product.getId()));
        
        return productRepository.save(product);
    }

    // Your existing methods
    public Product getProduct(Long id) {
        return productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    public Page<Product> getAllProducts(int page, int size, String sortBy, String direction) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? 
            Sort.Direction.DESC : Sort.Direction.ASC;
        
        Pageable pageable = PageRequest.of(
            page,
            size,
            Sort.by(sortDirection, sortBy)
        );

        return productRepository.findAll(pageable);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product findBySquareCatalogId(String squareCatalogId) {
        return productRepository.findBySquareCatalogId(squareCatalogId)
            .orElse(null);
    }
}