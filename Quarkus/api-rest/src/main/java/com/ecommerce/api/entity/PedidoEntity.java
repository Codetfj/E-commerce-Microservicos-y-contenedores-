package com.ecommerce.api.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "pedidos")
public class PedidoEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pedido")
    public Long idPedido;

    @Column(name = "id_user", nullable = false)
    public Long idUser;

    @Column(name = "estado_pedido")
    public String estadoPedido;

    @Column(name = "fecha")
    public LocalDateTime fecha;
}
