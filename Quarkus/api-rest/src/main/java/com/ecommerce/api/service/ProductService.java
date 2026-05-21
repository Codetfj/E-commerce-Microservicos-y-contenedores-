package com.ecommerce.api.service;

import com.ecommerce.api.entity.ProductoEntity;
import com.ecommerce.api.model.Products;
import com.ecommerce.api.model.ProductUpdate;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
@Transactional
public class ProductService {

    public List<Products> getAllProducts() {
        return ProductoEntity.<ProductoEntity>listAll()
                .stream().map(this::toModel).collect(Collectors.toList());
    }

    public Products createProduct(Products product) {
        ProductoEntity entity = new ProductoEntity();
        entity.nombre     = product.getName();
        entity.descripcion = product.getDescription();
        entity.precio     = product.getPrice() != null
                ? new BigDecimal(product.getPrice().toString()) : BigDecimal.ZERO;
        entity.stock      = product.getStock() != null ? product.getStock() : 0;
        entity.persist();
        return toModel(entity);
    }

    public Products getProductById(Integer id) {
        ProductoEntity entity = ProductoEntity.findById(id.longValue());
        if (entity == null)
            throw new WebApplicationException(
                Response.status(404).entity("Producto no encontrado").build());
        return toModel(entity);
    }

    public Products updateProduct(Integer id, ProductUpdate update) {
        ProductoEntity entity = ProductoEntity.findById(id.longValue());
        if (entity == null)
            throw new WebApplicationException(
                Response.status(404).entity("Producto no encontrado").build());
        if (update.getName() != null)  entity.nombre = update.getName();
        if (update.getPrice() != null) entity.precio = update.getPrice();
        if (update.getStock() != null) entity.stock  = update.getStock();
        return toModel(entity);
    }

    public void deleteProduct(Integer id) {
        boolean deleted = ProductoEntity.deleteById(id.longValue());
        if (!deleted)
            throw new WebApplicationException(
                Response.status(404).entity("Producto no encontrado").build());
    }

    private Products toModel(ProductoEntity e) {
        Products p = new Products();
        p.setId(e.idProducto.intValue());
        p.setName(e.nombre);
        p.setDescription(e.descripcion);
        p.setPrice(e.precio != null ? e.precio.floatValue() : 0f);
        p.setStock(e.stock);
        return p;
    }
}
