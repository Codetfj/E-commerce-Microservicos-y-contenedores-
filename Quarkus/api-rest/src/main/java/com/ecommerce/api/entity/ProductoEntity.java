package com.ecommerce.api.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "productos")
public class ProductoEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    public Long idProducto;

    @Column(name = "nombre", nullable = false, length = 150)
    public String nombre;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    public String descripcion;

    @Column(name = "precio", nullable = false, precision = 10, scale = 2)
    public BigDecimal precio;

    @Column(name = "stock", nullable = false)
    public Integer stock;
}
