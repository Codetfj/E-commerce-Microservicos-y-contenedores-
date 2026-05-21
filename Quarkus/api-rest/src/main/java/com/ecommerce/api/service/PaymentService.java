package com.ecommerce.api.service;

import com.ecommerce.api.entity.DetallePedidoEntity;
import com.ecommerce.api.entity.PagoEntity;
import com.ecommerce.api.model.PaymentRequest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import java.math.BigDecimal;

@ApplicationScoped
@Transactional
public class PaymentService {

    public PaymentRequest getPayment(String orderId) {
        Long pedidoId = parseId(orderId);
        PagoEntity pago = PagoEntity.find("idPedido", pedidoId).firstResult();
        if (pago == null)
            throw new WebApplicationException(
                Response.status(404).entity("Pago no encontrado para el pedido").build());
        return toModel(pago, pedidoId);
    }

    public PaymentRequest processPayment(String orderId, PaymentRequest request) {
        Long pedidoId = parseId(orderId);

        // Verificar que no exista un pago previo
        long count = PagoEntity.count("idPedido", pedidoId);
        if (count > 0)
            throw new WebApplicationException(
                Response.status(409).entity("El pedido ya tiene un pago registrado").build());

        PagoEntity entity = new PagoEntity();
        entity.idPedido   = pedidoId;
        entity.metodoPago = request.getMethod() != null ? request.getMethod().getValue() : "EFECTIVO";
        entity.estado     = "PENDIENTE";
        entity.persist();

        return toModel(entity, pedidoId);
    }

    public void updatePaymentStatus(String orderId) {
        Long pedidoId = parseId(orderId);
        PagoEntity pago = PagoEntity.find("idPedido", pedidoId).firstResult();
        if (pago == null)
            throw new WebApplicationException(
                Response.status(404).entity("Pago no encontrado").build());
        pago.estado = "PAGADO";
    }

    public void revertPayment(String orderId) {
        long deleted = PagoEntity.delete("idPedido", parseId(orderId));
        if (deleted == 0)
            throw new WebApplicationException(
                Response.status(404).entity("Pago no encontrado").build());
    }

    private PaymentRequest toModel(PagoEntity e, Long pedidoId) {
        BigDecimal amount = DetallePedidoEntity.<DetallePedidoEntity>list("idPedido", pedidoId)
                .stream()
                .map(d -> d.precioTotal != null ? d.precioTotal : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        PaymentRequest model = new PaymentRequest();
        try {
            model.setMethod(PaymentRequest.MethodEnum.fromValue(e.metodoPago));
        } catch (Exception ex) {
            model.setMethod(PaymentRequest.MethodEnum.CREDIT_CARD);
        }
        model.setAmount(amount);
        return model;
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
