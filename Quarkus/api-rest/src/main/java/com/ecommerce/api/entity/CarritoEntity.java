package com.ecommerce.api.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "carrito")
public class CarritoEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_carrito")
    public Long idCarrito;

    @Column(name = "id_user", nullable = false)
    public Long idUser;

    @Column(name = "id_producto", nullable = false)
    public Long idProducto;

    @Column(name = "cantidad", nullable = false)
    public Integer cantidad;

    @Column(name = "total", precision = 10, scale = 2)
    public BigDecimal total;
}
