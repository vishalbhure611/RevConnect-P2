package com.revconnect.controller;

import com.revconnect.dto.ApiResponse;
import com.revconnect.dto.ProductRequestDTO;
import com.revconnect.dto.ProductResponseDTO;
import com.revconnect.entity.Product;
import com.revconnect.entity.Role;
import com.revconnect.entity.User;
import com.revconnect.security.CustomUserDetails;
import com.revconnect.repository.ProductRepository;
import com.revconnect.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @PostMapping("/{ownerId}")
    public ResponseEntity<ApiResponse<ProductResponseDTO>> createProduct(
            @PathVariable Long ownerId,
            @RequestBody ProductRequestDTO request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null || !ownerId.equals(userDetails.getUserId())) {
            throw new RuntimeException("Not authorized to manage these products");
        }

        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (owner.getRole() != Role.BUSINESS && owner.getRole() != Role.CREATOR) {
            throw new RuntimeException("Only business or creator accounts can manage products");
        }

        Product saved = productRepository.save(Product.builder()
                .owner(owner)
                .name(normalizeRequired(request.getName(), "Product name"))
                .description(normalize(request.getDescription()))
                .url(normalize(request.getUrl()))
                .price(request.getPrice())
                .createdAt(LocalDateTime.now())
                .build());

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Product created successfully", map(saved, ownerId))
        );
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<ApiResponse<List<ProductResponseDTO>>> listProductsByOwner(
            @PathVariable Long ownerId
    ) {
        List<ProductResponseDTO> response = productRepository.findByOwnerId(ownerId)
                .stream()
                .map(product -> map(product, ownerId))
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Products fetched successfully", response)
        );
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ApiResponse<ProductResponseDTO>> updateProduct(
            @PathVariable Long productId,
            @RequestBody ProductRequestDTO request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (userDetails == null || !product.getOwner().getId().equals(userDetails.getUserId())) {
            throw new RuntimeException("Not authorized to manage this product");
        }

        if (request.getName() != null) product.setName(normalizeRequired(request.getName(), "Product name"));
        if (request.getDescription() != null) product.setDescription(normalize(request.getDescription()));
        if (request.getUrl() != null) product.setUrl(normalize(request.getUrl()));
        if (request.getPrice() != null) product.setPrice(request.getPrice());

        Product saved = productRepository.save(product);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Product updated successfully", map(saved, product.getOwner().getId()))
        );
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(
            @PathVariable Long productId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (userDetails == null || !product.getOwner().getId().equals(userDetails.getUserId())) {
            throw new RuntimeException("Not authorized to manage this product");
        }

        productRepository.delete(product);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Product deleted successfully", null)
        );
    }

    private ProductResponseDTO map(Product p, Long ownerId) {
        return ProductResponseDTO.builder()
                .id(p.getId())
                .ownerId(ownerId)
                .name(p.getName())
                .description(p.getDescription())
                .url(p.getUrl())
                .price(p.getPrice())
                .build();
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String normalizeRequired(String value, String fieldName) {
        String normalized = normalize(value);
        if (normalized == null) {
            throw new RuntimeException(fieldName + " is required");
        }
        return normalized;
    }
}
