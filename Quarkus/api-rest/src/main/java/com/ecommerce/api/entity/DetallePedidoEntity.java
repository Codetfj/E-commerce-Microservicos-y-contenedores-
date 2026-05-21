package com.ecommerce.api.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "detalles_pedido")
public class DetallePedidoEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle")
    public Long idDetalle;

    @Column(name = "id_pedido", nullable = false)
    public Long idPedido;

    @Column(name = "id_producto", nullable = false)
    public Long idProducto;

    @Column(name = "cantidad", nullable = false)
    public Integer cantidad;

    @Column(name = "precio_total", nullable = false, precision = 10, scale = 2)
    public BigDecimal precioTotal;
}
