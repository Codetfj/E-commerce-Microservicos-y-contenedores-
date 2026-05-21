package com.ecommerce.api.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

@Entity
@Table(name = "usuarios")
public class UsuarioEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_user")
    public Long idUser;

    @Column(name = "nombre", nullable = false, length = 100)
    public String nombre;

    @Column(name = "correo", nullable = false, unique = true, length = 150)
    public String correo;

    @Column(name = "contrasena", nullable = false, length = 255)
    public String contrasena;

    @Column(name = "direccion", length = 255)
    public String direccion;
}
