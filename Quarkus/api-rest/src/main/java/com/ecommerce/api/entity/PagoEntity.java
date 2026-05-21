package com.ecommerce.api.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

@Entity
@Table(name = "pagos")
public class PagoEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pago")
    public Long idPago;

    @Column(name = "id_pedido", nullable = false, unique = true)
    public Long idPedido;

    @Column(name = "metodo_pago", nullable = false, length = 50)
    public String metodoPago;

    @Column(name = "estado")
    public String estado;
}
