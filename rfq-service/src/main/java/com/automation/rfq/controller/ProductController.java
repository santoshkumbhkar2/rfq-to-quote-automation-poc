package com.automation.rfq.controller;

import com.automation.rfq.dto.ProductResponse;
import com.automation.rfq.service.ProductCatalogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Product Catalog API", description = "Endpoints for browsing products in catalog")
public class ProductController {

    private final ProductCatalogService productCatalogService;

    @GetMapping
    @Operation(summary = "List all catalog products", description = "Retrieves all active products in product catalog")
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        return ResponseEntity.ok(productCatalogService.getAllProducts());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID", description = "Retrieves product details by UUID")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable UUID id) {
        return ResponseEntity.ok(productCatalogService.getProductById(id));
    }
}
