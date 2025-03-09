package com.tsm.shop.service;

import com.squareup.square.SquareClient;
import com.squareup.square.api.CatalogApi;
import com.squareup.square.models.*;
import com.tsm.shop.config.SquareConfig;
import com.tsm.shop.model.entity.Product;
import com.squareup.square.exceptions.ApiException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class SquareService {
    private final SquareClient squareClient;
    private final CatalogApi catalogApi;
    private final SquareConfig squareConfig;

    public SquareService(SquareClient squareClient, SquareConfig squareConfig) {
        this.squareClient = squareClient;
        this.squareConfig = squareConfig;
        this.catalogApi = squareClient.getCatalogApi();
    }

    public String createCatalogItem(Product product) throws IOException {
        try {
            // Create Money amount (Square uses cents)
            Money price = new Money.Builder()
                .amount(product.getPrice().multiply(new BigDecimal("100")).longValue())
                .currency("USD")
                .build();

            // Create item variation
            CatalogItemVariation itemVariation = new CatalogItemVariation.Builder()
                .itemId("#" + product.getId())
                .name("Regular")
                .pricingType("FIXED_PRICING")
                .priceMoney(price)
                .build();

            // Create catalog object for variation
            CatalogObject variationObject = new CatalogObject.Builder("ITEM_VARIATION", "#" + product.getId() + "_var")
                .presentAtAllLocations(true)
                .itemVariationData(itemVariation)
                .build();

            // Create list of variations
            LinkedList<CatalogObject> variations = new LinkedList<>();
            variations.add(variationObject);

            // Create item data
            CatalogItem item = new CatalogItem.Builder()
                .name(product.getName())
                .description(product.getDescription())
                .variations(variations)
                .build();

            // Create catalog object for item
            CatalogObject itemObject = new CatalogObject.Builder("ITEM", "#" + product.getId())
                .presentAtAllLocations(true)
                .itemData(item)
                .build();

            // Create list of objects to batch upsert
            LinkedList<CatalogObject> objects = new LinkedList<>();
            objects.add(itemObject);

            // Create batch
            CatalogObjectBatch batch = new CatalogObjectBatch.Builder(objects)
                .build();

            // Create list of batches
            LinkedList<CatalogObjectBatch> batches = new LinkedList<>();
            batches.add(batch);

            // Create batch upsert request
            BatchUpsertCatalogObjectsRequest request = new BatchUpsertCatalogObjectsRequest.Builder(
                UUID.randomUUID().toString(),
                batches
            ).build();

            BatchUpsertCatalogObjectsResponse result = catalogApi.batchUpsertCatalogObjects(request);
            
            // Store both the item ID and the variation ID
            String itemId = result.getObjects().get(0).getId();
            product.setSquareCatalogId(itemId);
            
            // Find and store the variation ID
            if (!result.getObjects().get(0).getItemData().getVariations().isEmpty()) {
                String variationId = result.getObjects().get(0).getItemData().getVariations().get(0).getId();
                product.setSquareItemVariationId(variationId);
            }
            
            return itemId;

        } catch (ApiException e) {
            throw new RuntimeException("Error creating Square catalog item: " + e.getMessage(), e);
        }
    }

    public List<CatalogObject> listCatalogItems() throws IOException {
        try {
            // Parameters for listCatalog:
            // cursor - pagination cursor (null for first page)
            // types - we don't set this here since the method doesn't accept it directly
            // limit - maximum number of results (null for default)
            ListCatalogResponse response = catalogApi.listCatalog(null, null, null);
            
            // Return the objects or empty list if none found
            return response.getObjects() != null ? response.getObjects() : new LinkedList<>();
            
        } catch (ApiException e) {
            throw new RuntimeException("Error listing Square catalog items: " + e.getMessage(), e);
        }
    }

    public void deleteCatalogItem(String squareItemId) throws IOException {
        try {
            catalogApi.deleteCatalogObject(squareItemId);
        } catch (ApiException e) {
            throw new RuntimeException("Error deleting Square catalog item: " + e.getMessage(), e);
        }
    }
}