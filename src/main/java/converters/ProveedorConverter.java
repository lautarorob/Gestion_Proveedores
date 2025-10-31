package converters;

import entidades.Proveedor;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;
import repositorios.repoProveedor;

@FacesConverter(value = "proveedorConverter", managed = true)
public class ProveedorConverter implements Converter<Proveedor> {

    @Inject
    private repoProveedor repoProveedor;

    @Override
    public Proveedor getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.isEmpty() || value.equals("null")) {
            return null;
        }
        try {
            Integer id = Integer.valueOf(value);
            return repoProveedor.porId(id).orElse(null);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Proveedor proveedor) {
        if (proveedor == null || proveedor.getIdProveedor() == null) {
            return "";
        }
        return String.valueOf(proveedor.getIdProveedor());
    }
}
