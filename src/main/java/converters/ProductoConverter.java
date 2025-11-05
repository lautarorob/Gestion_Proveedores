/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package converters;

import entidades.Producto;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;
import repositorios.repoProducto;

/**
 *
 * @author roble
 */
@FacesConverter(value = "productoConverter", managed = true)
public class ProductoConverter implements Converter<Producto>{
    
    @Inject
    private repoProducto repoProducto;

    @Override
    public Producto getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.isEmpty() || value.equals("null")) {
            return null;
        }
        try {
            Integer id = Integer.valueOf(value);
            return repoProducto.porId(id).orElse(null);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Producto producto) {
        if (producto == null || producto.getIdProducto()== null) {
            return "";
        }
        return String.valueOf(producto.getIdProducto());
    }
    
}
