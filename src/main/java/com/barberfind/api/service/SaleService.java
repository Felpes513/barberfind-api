package com.barberfind.api.service;

import com.barberfind.api.domain.*;
import com.barberfind.api.domain.BarberEntity;
import com.barberfind.api.dto.SaleDtos.*;
import com.barberfind.api.repository.*;
import com.barberfind.api.shared.util.Cuid;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SaleService {

    private final SaleRepository        saleRepository;
    private final ProductRepository     productRepository;
    private final BarbershopRepository  barbershopRepository;
    private final BarberRepository      barberRepository;

    // ── Criar venda ───────────────────────────────────────────────

    @Transactional
    public SaleResponse create(String barbershopId,
                               CreateSaleRequest req,
                               String authUserId,
                               String authRole) {

        var barbershop = barbershopRepository.findById(barbershopId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "barbershop_not_found"));

        assertOwnerOrBarber(barbershop, authUserId, authRole);

        // Resolve barbeiro: OWNER informa no body, BARBER usa o próprio perfil

        BarberEntity barber = resolveBarber(req.barberId(), authUserId, authRole);

        // Monta os itens validando produto e estoque
        var sale = Sale.builder()
                .id(Cuid.generate())
                .barbershop(barbershop)
                .barber(barber)
                .build();

        List<SaleItem> items = req.items().stream().map(itemReq -> {
            var product = productRepository.findById(itemReq.productId())
                    .filter(p -> p.getBarbershop().getId().equals(barbershopId))
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "product_not_found"));

            if (product.getStockQuantity() < itemReq.quantity()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "insufficient_stock");
            }

            // Deduz estoque
            product.setStockQuantity(product.getStockQuantity() - itemReq.quantity());
            productRepository.save(product);

            return SaleItem.builder()
                    .id(Cuid.generate())
                    .sale(sale)
                    .product(product)
                    .quantity(itemReq.quantity())
                    .unitPrice(product.getPrice()) // snapshot do preço atual
                    .build();
        }).toList();

        sale.setItems(items);
        sale.setTotalAmount(calculateTotal(items));

        return toResponse(saleRepository.save(sale));
    }

    // ── Listar vendas da barbearia ────────────────────────────────

    public List<SaleResponse> listByBarbershop(String barbershopId,
                                               String authUserId,
                                               String authRole) {
        var barbershop = barbershopRepository.findById(barbershopId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "barbershop_not_found"));

        assertOwnerOrBarber(barbershop, authUserId, authRole);

        return saleRepository.findAllByBarbershopId(barbershopId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // ── Buscar por ID ─────────────────────────────────────────────

    public SaleResponse getById(String barbershopId,
                                String saleId,
                                String authUserId,
                                String authRole) {

        var barbershop = barbershopRepository.findById(barbershopId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "barbershop_not_found"));

        assertOwnerOrBarber(barbershop, authUserId, authRole);

        var sale = saleRepository.findByIdAndBarbershopId(saleId, barbershopId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "sale_not_found"));

        return toResponse(sale);
    }

    // ── Deletar venda ─────────────────────────────────────────────

    @Transactional
    public void delete(String barbershopId,
                       String saleId,
                       String authUserId) {

        var barbershop = barbershopRepository.findById(barbershopId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "barbershop_not_found"));

        // Só o OWNER dono da barbearia pode deletar
        if (!barbershop.getOwnerUserId().equals(authUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "forbidden");
        }

        var sale = saleRepository.findByIdAndBarbershopId(saleId, barbershopId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "sale_not_found"));

        // Estorna o estoque ao deletar
        sale.getItems().forEach(item -> {
            var product = item.getProduct();
            product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
            productRepository.save(product);
        });

        saleRepository.delete(sale);
    }

    // ── Helpers ───────────────────────────────────────────────────

    private void assertOwnerOrBarber(Barbershop barbershop,
                                     String authUserId,
                                     String authRole) {
        boolean isOwner  = "OWNER".equals(authRole) && barbershop.getOwnerUserId().equals(authUserId);
        boolean isBarber = "BARBER".equals(authRole);

        if (!isOwner && !isBarber) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "forbidden");
        }
    }

    // resolveBarber — tipo de retorno corrigido
    private BarberEntity resolveBarber(String barberIdFromBody,
                                       String authUserId,
                                       String authRole) {
        if ("BARBER".equals(authRole)) {
            return barberRepository.findByUserId(authUserId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "barber_profile_not_found"));
        }

        if (barberIdFromBody == null) return null;

        return barberRepository.findById(barberIdFromBody)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "barber_not_found"));
    }

    private BigDecimal calculateTotal(List<SaleItem> items) {
        return items.stream()
                .map(i -> i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private SaleResponse toResponse(Sale sale) {
        List<SaleItemResponse> itemResponses = sale.getItems().stream()
                .map(i -> new SaleItemResponse(
                        i.getId(),
                        i.getProduct().getId(),
                        i.getProduct().getName(),
                        i.getQuantity(),
                        i.getUnitPrice(),
                        i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity()))
                ))
                .toList();

        return new SaleResponse(
                sale.getId(),
                sale.getBarbershop().getId(),
                sale.getBarbershop().getName(),
                sale.getBarber() != null ? sale.getBarber().getId()   : null,
                sale.getBarber() != null ? sale.getBarber().getName() : null,
                sale.getTotalAmount(),
                itemResponses,
                sale.getCreatedAt()
        );
    }
}