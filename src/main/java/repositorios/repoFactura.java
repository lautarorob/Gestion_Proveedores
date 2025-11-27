/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package repositorios;

import entidades.Factura;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author roble
 */
@Stateless
public class repoFactura {

    @Inject
    EntityManager em;

    public void Guardar(Factura f) {
        if (f.getIdFactura() != null && f.getIdFactura() > 0) {
            em.merge(f);
        } else {
            em.persist(f);
        }
    }

    /*
    public void BajaLogica(Integer id) {
        porId(id).ifPresent(f -> {
            f.setEstado(false);
            em.merge(f);
        });
    }
    
    public void Reactivar(Integer id) {
        porId(id).ifPresent(f -> {
            f.setEstado(true);
            em.merge(f);
        });
    }
     */
    public void Eliminar(Integer id) {
        porId(id).ifPresent(p -> {
            em.remove(p);
        });
    }

    public Optional<Factura> porId(Integer id) {
        return Optional.ofNullable(em.find(Factura.class, id));
    }

    public List<Factura> Listar() {
        return em.createQuery("SELECT f FROM Factura f", Factura.class).getResultList();
    }

    public String obtenerUltimoComprobante() {
        try {
            String jpql = "SELECT f.nroComprobante FROM Factura f ORDER BY f.idFactura DESC";
            List<String> resultados = em.createQuery(jpql, String.class)
                    .setMaxResults(1)
                    .getResultList();

            if (resultados != null && !resultados.isEmpty()) {
                return resultados.get(0);
            }
            return null; // No hay facturas previas
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Optional<Factura> obtenerFacturaConProductos(Integer id) {
        try {
            List<Factura> resultados = em.createQuery(
                    "SELECT f FROM Factura f LEFT JOIN FETCH f.facturaProductoList fp LEFT JOIN FETCH fp.producto WHERE f.idFactura = :id",
                    Factura.class)
                    .setParameter("id", id)
                    .getResultList(); // no usar getSingleResult

            if (!resultados.isEmpty()) {
                return Optional.of(resultados.get(0));
            } else {
                return Optional.empty();
            }
        } catch (Exception e) {
            System.out.println("Error cargando factura con productos: " + e.getMessage());
            return Optional.empty();
        }
    }

    public List<Factura> listarPorProveedor(Integer idProveedor) {
        try {
            return em.createQuery(
                    "SELECT f FROM Factura f WHERE f.idProveedor.idProveedor = :idProv ORDER BY f.fechaComprobante",
                    Factura.class)
                    .setParameter("idProv", idProveedor)
                    .getResultList();
        } catch (Exception e) {
            System.out.println("Error al listar facturas por proveedor: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public BigDecimal getSaldoPendiente(Integer idProveedor) {
        TypedQuery<BigDecimal> query = em.createQuery(
                "SELECT SUM(f.total) FROM Factura f WHERE f.idProveedor.idProveedor = :id AND f.estado = 'Impaga'",
                BigDecimal.class
        );
        query.setParameter("id", idProveedor);
        BigDecimal saldo = query.getSingleResult();
        return (saldo != null) ? saldo : BigDecimal.ZERO;
    }

    public List<Factura> listarImpagasPorProveedor(Integer idProveedor) {
        try {
            List<Factura> lista = em.createQuery(
                    "SELECT f FROM Factura f WHERE f.idProveedor.idProveedor = :prov AND (f.estado IS NULL OR f.estado <> 'Pagada')",
                    Factura.class)
                    .setParameter("prov", idProveedor)
                    .getResultList();

            System.out.println("RepoFactura: cargadas " + lista.size() + " facturas para proveedor ID " + idProveedor);
            return lista;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<Factura> listarImpagas() {
        try {
            List<Factura> lista = em.createQuery(
                    "SELECT f FROM Factura f WHERE f.estado <> 'Pagada'",
                    Factura.class)
                    .getResultList();
            return lista;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // 1. Top Proveedores con Deuda
    public List<Object[]> obtenerTopDeudas() {
        String jpql = "SELECT f.idProveedor.razonSocial, SUM(f.total) "
                + "FROM Factura f "
                + "WHERE f.estado = 'Impaga' "
                + "GROUP BY f.idProveedor.razonSocial "
                + "ORDER BY SUM(f.total) DESC";

        return em.createQuery(jpql, Object[].class)
                .setMaxResults(5) // Limitamos a los 5 mayores
                .getResultList();
    }

    public List<Object[]> obtenerTotalesPorFormaPago() {
        try {
            String sql
                    = // Facturas de Contado
                    "SELECT 'Contado' as forma_pago, SUM(total) as total "
                    + "FROM facturas "
                    + "WHERE forma_pago = 'Contado' "
                    + "GROUP BY forma_pago "
                    + "UNION ALL "
                    + // Facturas de Cuenta Corriente con forma de pago de la orden
                    "SELECT op.forma_pago, SUM(f.total) "
                    + "FROM facturas f "
                    + "INNER JOIN ordenes_pago op ON f.id_orden_pago = op.id_orden_pago "
                    + "WHERE f.forma_pago = 'Cuenta Corriente' AND op.forma_pago IS NOT NULL "
                    + "GROUP BY op.forma_pago "
                    + "ORDER BY total DESC";

            List<Object[]> resultados = em.createNativeQuery(sql).getResultList();

            System.out.println("=== RESULTADOS FORMAS DE PAGO ===");
            for (Object[] fila : resultados) {
                System.out.println("Forma: " + fila[0] + " | Total: " + fila[1]);
            }

            return resultados;
        } catch (Exception e) {
            System.out.println("Error en obtenerTotalesPorFormaPago: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // 3. Deuda Total (Tarjeta pequeña del dashboard)
    public Double obtenerDeudaTotal() {
        String jpql = "SELECT SUM(f.total) FROM Factura f WHERE f.estado = 'Impaga'";
        try {
            Number resultado = em.createQuery(jpql, Number.class).getSingleResult();
            return resultado != null ? resultado.doubleValue() : 0.0;
        } catch (Exception e) {
            return 0.0;
        }
    }
    
    // 4. Comparación Histórico Cuenta Corriente vs Órdenes de Pago
    public List<Object[]> obtenerComparacionCuentaCorriente() {
        try {
            String sql
                    = // Total HISTÓRICO de facturas en cuenta corriente (pagadas + impagas)
                    "SELECT 'Facturas Cta Cte' as concepto, SUM(total) as monto "
                    + "FROM facturas "
                    + "WHERE forma_pago = 'Cuenta Corriente' "
                    + "UNION ALL "
                    + // Total de órdenes de pago generadas (lo que ya se pagó)
                    "SELECT 'Órdenes de Pago' as concepto, SUM(monto_total) as monto "
                    + "FROM ordenes_pago";

            List<Object[]> resultados = em.createNativeQuery(sql).getResultList();

            System.out.println("=== COMPARACIÓN CUENTA CORRIENTE ===");
            for (Object[] fila : resultados) {
                System.out.println("Concepto: " + fila[0] + " | Monto: " + fila[1]);
            }

            return resultados;
        } catch (Exception e) {
            System.out.println("Error en obtenerComparacionCuentaCorriente: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    public List<Factura> listarPagas() {
        try {
            List<Factura> lista = em.createQuery(
                    "SELECT f FROM Factura f WHERE f.formaPago 'cuenta corriente'",
                    Factura.class)
                    .getResultList();
            return lista;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    public List<Factura> filtrar(String tipo, Date fechaDesde, Date fechaHasta) {
        // 1. Definimos la consulta base (manteniendo tu filtro de estado)
        String jpql = "SELECT f FROM Factura f WHERE f.estado <> 'Pagada'";

        // 2. Concatenamos las condiciones dinámicamente
        if (tipo != null && !tipo.isEmpty()) {
            jpql += " AND f.tipo = :tipo";
        }
        if (fechaDesde != null) {
            jpql += " AND f.fechaRegistro >= :fechaDesde";
        }
        if (fechaHasta != null) {
            jpql += " AND f.fechaRegistro <= :fechaHasta";
        }

        // Opcional: Ordenar por fecha como en el ejemplo de Auditoria
        jpql += " ORDER BY f.fechaRegistro DESC";

        // 3. Creamos la query UNA VEZ que el String está completo
        var query = em.createQuery(jpql, Factura.class);

        // 4. Asignamos los valores a los parámetros si corresponde
        if (tipo != null && !tipo.isEmpty()) {
            query.setParameter("tipo", tipo);
        }
        if (fechaDesde != null) {
            query.setParameter("fechaDesde", fechaDesde);
        }
        if (fechaHasta != null) {
            query.setParameter("fechaHasta", fechaHasta);
        }

        return query.getResultList();
    }
}
