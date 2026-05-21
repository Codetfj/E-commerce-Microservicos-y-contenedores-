package com.ecommerce.api.service;

import com.ecommerce.api.entity.UsuarioEntity;
import com.ecommerce.api.model.UserCreate;
import com.ecommerce.api.model.Users;
import com.ecommerce.api.model.UserUpdate;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
@Transactional
public class UserService {

    public List<Users> getAllUsers() {
        return UsuarioEntity.<UsuarioEntity>listAll()
                .stream().map(this::toModel).collect(Collectors.toList());
    }

    public Users createUser(UserCreate userCreate) {
        UsuarioEntity entity = new UsuarioEntity();
        entity.nombre = userCreate.getName();
        entity.correo = userCreate.getEmail();
        entity.contrasena = userCreate.getPassword();
        entity.direccion = userCreate.getAddress();
        entity.persist();
        return toModel(entity);
    }

    public Users getUserById(Integer id) {
        UsuarioEntity entity = UsuarioEntity.findById(id.longValue());
        if (entity == null)
            throw new WebApplicationException(
                Response.status(404).entity("Usuario no encontrado").build());
        return toModel(entity);
    }

    public Users updateUser(Integer id, UserUpdate update) {
        UsuarioEntity entity = UsuarioEntity.findById(id.longValue());
        if (entity == null)
            throw new WebApplicationException(
                Response.status(404).entity("Usuario no encontrado").build());
        if (update.getName() != null)    entity.nombre    = update.getName();
        if (update.getEmail() != null)   entity.correo    = update.getEmail();
        if (update.getAddress() != null) entity.direccion = update.getAddress();
        return toModel(entity);
    }

    public void deleteUser(Integer id) {
        boolean deleted = UsuarioEntity.deleteById(id.longValue());
        if (!deleted)
            throw new WebApplicationException(
                Response.status(404).entity("Usuario no encontrado").build());
    }

    private Users toModel(UsuarioEntity e) {
        Users u = new Users();
        u.setId(e.idUser.intValue());
        u.setName(e.nombre);
        u.setEmail(e.correo);
        u.setAddress(e.direccion);
        return u;
    }
}
