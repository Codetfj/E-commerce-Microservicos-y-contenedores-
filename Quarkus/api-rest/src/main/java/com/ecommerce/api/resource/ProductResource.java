package com.ecommerce.api.resource;

import com.ecommerce.api.model.Products;
import com.ecommerce.api.model.ProductUpdate;
import com.ecommerce.api.service.ProductService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/api/v1")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProductResource {

    @Inject
    ProductService productService;

    @GET
    @Path("/products")
    public Response getAllProducts() {
        List<Products> products = productService.getAllProducts();
        return Response.ok(products).build();
    }

    @GET
    @Path("/products/{id}")
    public Response getProductById(@PathParam("id") Integer id) {
        Products product = productService.getProductById(id);
        return Response.ok(product).build();
    }

    @POST
    @Path("/products")
    public Response createProduct(@Valid Products productRequest) {
        Products productResponse = productService.createProduct(productRequest);
        return Response.status(Response.Status.CREATED).entity(productResponse).build();
    }

    @PATCH
    @Path("/products/{id}")
    public Response updateProduct(@PathParam("id") Integer id, ProductUpdate productUpdate) {
        Products productResponse = productService.updateProduct(id, productUpdate);
        return Response.ok(productResponse).build();
    }

    @DELETE
    @Path("/products/{id}")
    public Response deleteProduct(@PathParam("id") Integer id) {
        productService.deleteProduct(id);
        return Response.noContent().build();
    }
}