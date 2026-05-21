package com.ecommerce.api.service;

import com.ecommerce.api.entity.CarritoEntity;
import com.ecommerce.api.entity.ProductoEntity;
import com.ecommerce.api.model.Cart;
import com.ecommerce.api.model.CartItemRequest;
import com.ecommerce.api.model.CartItemsItemIdPatchRequest;
import com.ecommerce.api.model.CartProduct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
@Transactional
public class CartService {

    private static final Long DEFAULT_USER_ID = 1L;

    public Cart getCart() {
        List<CarritoEntity> items = CarritoEntity.list("idUser", DEFAULT_USER_ID);

        List<CartProduct> cartItems = items.stream().map(item -> {
            CartProduct cp = new CartProduct();
            cp.setItemId(item.idCarrito.intValue());
            cp.setProductId(item.idProducto.intValue());
            cp.setQuantity(item.cantidad);
            cp.setSubtotal(item.total);
            ProductoEntity producto = ProductoEntity.findById(item.idProducto);
            if (producto != null) cp.setProductName(producto.nombre);
            return cp;
        }).collect(Collectors.toList());

        BigDecimal total = items.stream()
                .map(i -> i.total != null ? i.total : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Cart cart = new Cart();
        cart.setIdCart(DEFAULT_USER_ID.intValue());
        cart.setUserId(DEFAULT_USER_ID.intValue());
        cart.setItems(cartItems);
        cart.setTotalPrice(total);
        return cart;
    }

    public void clearCart() {
        CarritoEntity.delete("idUser", DEFAULT_USER_ID);
    }

    public CartProduct addItem(CartItemRequest request) {
        ProductoEntity producto = ProductoEntity.findById(request.getProductId().longValue());
        if (producto == null)
            throw new WebApplicationException(
                Response.status(404).entity("Producto no encontrado").build());

        BigDecimal subtotal = producto.precio.multiply(new BigDecimal(request.getQuantity()));

        CarritoEntity entity = new CarritoEntity();
        entity.idUser     = DEFAULT_USER_ID;
        entity.idProducto = request.getProductId().longValue();
        entity.cantidad   = request.getQuantity();
        entity.total      = subtotal;
        entity.persist();

        CartProduct response = new CartProduct();
        response.setItemId(entity.idCarrito.intValue());
        response.setProductId(request.getProductId());
        response.setProductName(producto.nombre);
        response.setQuantity(request.getQuantity());
        response.setSubtotal(subtotal);
        return response;
    }

    public CartProduct updateItemQuantity(Integer itemId, CartItemsItemIdPatchRequest request) {
        CarritoEntity entity = CarritoEntity.findById(itemId.longValue());
        if (entity == null)
            throw new WebApplicationException(
                Response.status(404).entity("Item no encontrado en carrito").build());

        entity.cantidad = request.getQuantity();
        ProductoEntity producto = ProductoEntity.findById(entity.idProducto);
        if (producto != null)
            entity.total = producto.precio.multiply(new BigDecimal(request.getQuantity()));

        CartProduct response = new CartProduct();
        response.setItemId(entity.idCarrito.intValue());
        response.setProductId(entity.idProducto.intValue());
        response.setQuantity(entity.cantidad);
        response.setSubtotal(entity.total);
        if (producto != null) response.setProductName(producto.nombre);
        return response;
    }

    public void removeItem(Integer itemId) {
        boolean deleted = CarritoEntity.deleteById(itemId.longValue());
        if (!deleted)
            throw new WebApplicationException(
                Response.status(404).entity("Item no encontrado en carrito").build());
    }
}
