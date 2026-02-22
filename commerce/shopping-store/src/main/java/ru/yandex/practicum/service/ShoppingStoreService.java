package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ShoppingStoreService {

    private final ProductRepo productRepo;

    private final ProductMapper productMapper;

    @Cacheable(cacheNames = "products", key = "#productId")
    @Transactional(readOnly = true)
    public ProductDto getProduct(UUID productId) {
        log.debug("Get product by id: {}", productId);

        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException(String.valueOf(productId), Product.class));

        return productMapper.toDto(product);
    }

    @Transactional(readOnly = true)
    public Page<ProductDto> getProducts(ProductCategory category, Pageable pageable) {
        log.debug("Get products");

        return productRepo.findAllByCategory(category, pageable)
                .map(productMapper::toDto);
    }

    public ProductDto createProduct(ProductDto dto) {
        log.debug("create product: name {}", dto.getProductName());

        Product entity = productRepo.save(productMapper.toEntity(dto));
        dto.setProductId(entity.getProductId());

        return dto;
    }

    @CacheEvict(cacheNames = "products", key = "#productDto.getProductId()")
    public ProductDto updateProduct(ProductDto productDto) {
        log.debug("update product: name {}", productDto.getProductName());

        if (productDto.getProductId() == null) {
            Product newProduct = productMapper.toEntity(productDto);

            Product saved = productRepo.save(newProduct);
            return productMapper.toDto(saved);
        }

        Product product = getActiveProductOrThrow(productDto.getProductId());

        productMapper.updateFields(product, productDto);
        productRepo.save(product);

        return productDto;
    }

    @CacheEvict(cacheNames = "products", key = "#productId")
    public void removeProductFromStore(UUID productId) {
        log.debug("remove product from store: {}", productId);
        Product product = getActiveProductOrThrow(productId);

        product.setProductState(ProductState.DEACTIVATE);
    }

    @CacheEvict(cacheNames = "products", key = "#request.getProductId()")
    public boolean setProductQuantityState(SetProductQuantityStateRequest request) {
        log.debug("set product quantity state: {}", request);
        Product product = productRepo.findById(request.getProductId())
                .orElseThrow(() -> new EntityNotFoundException(String.valueOf(request.getProductId()), Product.class));
        product.setQuantityState(request.getQuantityState());
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
