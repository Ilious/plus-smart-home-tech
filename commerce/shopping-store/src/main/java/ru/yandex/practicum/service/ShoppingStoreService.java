package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dao.Product;
import ru.yandex.practicum.dto.shopping.store.ProductDto;
import ru.yandex.practicum.dto.shopping.store.SetProductQuantityStateRequest;
import ru.yandex.practicum.enums.ProductCategory;
import ru.yandex.practicum.enums.ProductState;
import ru.yandex.practicum.exception.EntityNotFoundException;
import ru.yandex.practicum.mapper.ProductMapper;
import ru.yandex.practicum.repo.ProductRepo;

import java.awt.print.Pageable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ShoppingStoreService {

    private final ProductRepo productRepo;

    private final ProductMapper productMapper;

    @Transactional(readOnly = true)
    public ProductDto getProduct(UUID productId) {
        log.debug("Get product by id: {}", productId);

        Product product = getActiveProductOrThrow(productId);

        return productMapper.toDto(product);
    }

    @Transactional(readOnly = true)
    public List<ProductDto> getProducts(ProductCategory category, Pageable pageable) {
        log.debug("Get products");

        return productRepo.findAllByCategory(category, pageable).stream()
                .map(productMapper::toDto)
                .toList();
    }

    public ProductDto createProduct(ProductDto dto) {
        log.debug("create product: name {}", dto.getProductName());

        Product entity = productRepo.save(productMapper.toEntity(dto));
        dto.setProductId(entity.getProductId());

        return dto;
    }

    public ProductDto updateProduct(ProductDto productDto) {
        log.debug("update product: name {}", productDto.getProductName());

        Product product = getActiveProductOrThrow(productDto.getProductId());

        productRepo.save(productMapper.updateFields(product, productDto));

        return productDto;
    }

    public void removeProductFromStore(UUID productId) {
        log.debug("remove product from store: {}", productId);
        Product product = getActiveProductOrThrow(productId);

        product.setProductState(ProductState.DEACTIVATE);
    }

    public boolean setProductQuantityState(SetProductQuantityStateRequest quantityStateRequest) {
        log.debug("set product quantity state: {}", quantityStateRequest);
        Product product = getActiveProductOrThrow(quantityStateRequest.getProductId());
        product.setQuantityState(quantityStateRequest.getQuantityState());
        return true;
    }

    public Product getActiveProductOrThrow(UUID productId) {
        log.trace("get active product or throw: {}", productId);
        Optional<Product> productById = productRepo.findById(productId);
        if (productById.isEmpty() || productById.get().getProductState().equals(ProductState.DEACTIVATE))
            throw new EntityNotFoundException(String.valueOf(productId), Product.class);

        return productById.get();
    }
}
