package com.barberfind.api.service;

import com.barberfind.api.dto.ProductDtos.*;
import com.barberfind.api.domain.Product;
import com.barberfind.api.repository.BarbershopRepository;
import com.barberfind.api.repository.ProductRepository;
import com.barberfind.api.shared.util.Cuid;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final BarbershopRepository barbershopRepository;

    // ── List ──────────────────────────────────────────────────────────────────

    public List<ProductResponse> listByBarbershop(String barbershopId) {
        if (!barbershopRepository.existsById(barbershopId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "barbershop_not_found");
        }

        return productRepository.findAllByBarbershopId(barbershopId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // ── Get ───────────────────────────────────────────────────────────────────

    public ProductResponse getById(String barbershopId, String productId) {
        return toResponse(findProduct(barbershopId, productId));
    }

    // ── Create ────────────────────────────────────────────────────────────────

    @Transactional
    public ProductResponse create(String barbershopId, ProductRequest req) {
        var barbershop = barbershopRepository.findById(barbershopId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "barbershop_not_found"));

        if (productRepository.existsByBarbershopIdAndName(barbershopId, req.name())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "product_name_already_exists");
        }

        var product = Product.builder()
                .id(Cuid.generate())
                .barbershop(barbershop)
                .name(req.name())
                .price(req.price())
                .stockQuantity(req.stockQuantity())
                .build();

        return toResponse(productRepository.save(product));
    }

    // ── Update ────────────────────────────────────────────────────────────────

    @Transactional
    public ProductResponse update(String barbershopId, String productId, ProductRequest req) {
        var product = findProduct(barbershopId, productId);

        boolean nameChanged = !product.getName().equals(req.name());
        if (nameChanged && productRepository.existsByBarbershopIdAndName(barbershopId, req.name())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "product_name_already_exists");
        }

        product.setName(req.name());
        product.setPrice(req.price());
        product.setStockQuantity(req.stockQuantity());

        return toResponse(productRepository.save(product));
    }

    // ── Delete ────────────────────────────────────────────────────────────────

    @Transactional
    public void delete(String barbershopId, String productId) {
        var product = findProduct(barbershopId, productId);
        productRepository.delete(product);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Product findProduct(String barbershopId, String productId) {
        return productRepository.findById(productId)
                .filter(p -> p.getBarbershop().getId().equals(barbershopId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "product_not_found"));
    }

    private ProductResponse toResponse(Product p) {
        return new ProductResponse(
                p.getId(),
                p.getBarbershop().getId(),
                p.getBarbershop().getName(),
                p.getName(),
                p.getPrice(),
                p.getStockQuantity(),
                p.getCreatedAt()
        );
    }
}