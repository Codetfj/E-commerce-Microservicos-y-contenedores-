package com.ecommerce.api.service;

import com.ecommerce.api.entity.CarritoEntity;
import com.ecommerce.api.entity.DetallePedidoEntity;
import com.ecommerce.api.entity.PedidoEntity;
import com.ecommerce.api.model.Order;
import com.ecommerce.api.model.OrderItem;
import com.ecommerce.api.model.OrdersIdPatchRequest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
@Transactional
public class OrderService {

    private static final Long DEFAULT_USER_ID = 1L;

    private static final Map<String, String> API_TO_DB = Map.of(
        "pending",   "PENDIENTE",
        "shipped",   "ENVIADO",
        "completed", "ENTREGADO"
    );

    private static final Map<String, String> DB_TO_API = Map.of(
        "PENDIENTE",  "pending",
        "CONFIRMADO", "pending",
        "ENVIADO",    "shipped",
        "ENTREGADO",  "completed",
        "CANCELADO",  "pending"
    );

    public List<Order> getAllOrders() {
        return PedidoEntity.<PedidoEntity>listAll()
                .stream().map(this::toModel).collect(Collectors.toList());
    }

    public Order createOrder() {
        PedidoEntity pedido = new PedidoEntity();
        pedido.idUser       = DEFAULT_USER_ID;
        pedido.estadoPedido = "PENDIENTE";
        pedido.fecha        = LocalDateTime.now();
        pedido.persistAndFlush();

        // Pasar items del carrito a detalles_pedido
        List<CarritoEntity> carrito = CarritoEntity.list("idUser", DEFAULT_USER_ID);
        for (CarritoEntity item : carrito) {
            DetallePedidoEntity detalle = new DetallePedidoEntity();
            detalle.idPedido    = pedido.idPedido;
            detalle.idProducto  = item.idProducto;
            detalle.cantidad    = item.cantidad;
            detalle.precioTotal = item.total != null ? item.total : BigDecimal.ZERO;
            detalle.persist();
        }
        CarritoEntity.delete("idUser", DEFAULT_USER_ID);

        return toModel(pedido);
    }

    public Order getOrderById(String id) {
        PedidoEntity entity = PedidoEntity.findById(parseId(id));
        if (entity == null)
            throw new WebApplicationException(
                Response.status(404).entity("Pedido no encontrado").build());
        return toModel(entity);
    }

    public Order updateOrderStatus(String id, OrdersIdPatchRequest request) {
        PedidoEntity entity = PedidoEntity.findById(parseId(id));
        if (entity == null)
            throw new WebApplicationException(
                Response.status(404).entity("Pedido no encontrado").build());
        if (request.getStatus() != null) {
            String apiStatus = request.getStatus().getValue();
            entity.estadoPedido = API_TO_DB.getOrDefault(apiStatus.toLowerCase(), apiStatus.toUpperCase());
        }
        return toModel(entity);
    }

    public void cancelOrder(String id) {
        PedidoEntity entity = PedidoEntity.findById(parseId(id));
        if (entity == null)
            throw new WebApplicationException(
                Response.status(404).entity("Pedido no encontrado").build());
        entity.estadoPedido = "CANCELADO";
    }

    public List<OrderItem> getOrderItems(String orderId) {
        return DetallePedidoEntity.<DetallePedidoEntity>list("idPedido", parseId(orderId))
                .stream().map(this::toItemModel).collect(Collectors.toList());
    }

    public OrderItem getOrderItem(String orderId, String itemId) {
        DetallePedidoEntity entity = DetallePedidoEntity.findById(parseId(itemId));
        if (entity == null || !entity.idPedido.equals(parseId(orderId)))
            throw new WebApplicationException(
                Response.status(404).entity("Item de pedido no encontrado").build());
        return toItemModel(entity);
    }

    private Order toModel(PedidoEntity e) {
        BigDecimal total = DetallePedidoEntity.<DetallePedidoEntity>list("idPedido", e.idPedido)
                .stream()
                .map(d -> d.precioTotal != null ? d.precioTotal : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order o = new Order();
        o.setId(String.valueOf(e.idPedido));
        o.setUserId(e.idUser.intValue());
        o.setStatus(DB_TO_API.getOrDefault(e.estadoPedido, "pending"));
        o.setTotal(total);
        o.setCreatedAt(e.fecha != null
                ? e.fecha.atOffset(ZoneOffset.of("-06:00"))
                : OffsetDateTime.now());
        return o;
    }

    private OrderItem toItemModel(DetallePedidoEntity e) {
        OrderItem item = new OrderItem();
        item.setItemId(String.valueOf(e.idDetalle));
        item.setProductId(e.idProducto.intValue());
        item.setQuantity(e.cantidad);
        item.setPriceAtPurchase(e.cantidad > 0
                ? e.precioTotal.divide(new BigDecimal(e.cantidad), 2, java.math.RoundingMode.HALF_UP)
                : BigDecimal.ZERO);
        return item;
    }

    private Long parseId(String id) {
        try {
            return Long.parseLong(id);
        } catch (NumberFormatException e) {
            throw new WebApplicationException(
                Response.status(400).entity("ID inválido: " + id).build());
        }
    }
}
