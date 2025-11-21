package controladores;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

// Imports de PrimeFaces para Gráficos
import org.primefaces.model.charts.ChartData;
import org.primefaces.model.charts.axes.cartesian.CartesianScales;
import org.primefaces.model.charts.axes.cartesian.linear.CartesianLinearAxes;
import org.primefaces.model.charts.axes.cartesian.linear.CartesianLinearTicks;
import org.primefaces.model.charts.bar.BarChartDataSet;
import org.primefaces.model.charts.bar.BarChartModel;
import org.primefaces.model.charts.bar.BarChartOptions;
import org.primefaces.model.charts.donut.DonutChartDataSet;
import org.primefaces.model.charts.donut.DonutChartModel;
import org.primefaces.model.charts.donut.DonutChartOptions;
import org.primefaces.model.charts.optionconfig.legend.Legend;
import org.primefaces.model.charts.optionconfig.legend.LegendLabel;
import repositorios.repoFactura;
import repositorios.repoFacturaProducto;

/**
 * Controlador para el Dashboard Financiero Configurado para Jakarta EE 10 y
 * PrimeFaces 13+
 */
@Named(value = "controladorDashboard")
@ViewScoped
public class controladorDashboard implements Serializable {

    @Inject
    private repoFactura repoFactura;

    @Inject
    private repoFacturaProducto repoFacProd;

    private BarChartModel proveedoresModel;
    private BarChartModel productosModel;
    private DonutChartModel formasPagoModel;
    private double deudaTotal;

    @PostConstruct
    public void init() {
        // Cargar deuda total
        this.deudaTotal = repoFactura.obtenerDeudaTotal();

        // Generar gráficos
        createProveedoresModel();
        createProductosModel();
        createFormasPagoModel();
    }
/*
    // Método para el botón manual
    public void refrescarDatos() {
        this.deudaTotal = repoFactura.obtenerDeudaTotal();
        createProveedoresModel();
        createProductosModel();
        createFormasPagoModel();

        // Mensaje opcional
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("Datos actualizados correctamente"));
    }*/

    // --- 1. GRÁFICO DE BARRAS (Deuda por Proveedor) ---
    private void createProveedoresModel() {
        proveedoresModel = new BarChartModel();
        ChartData data = new ChartData();
        BarChartDataSet dataSet = new BarChartDataSet();
        dataSet.setLabel("Deuda ($)");

        List<Number> values = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        // LLAMADA A LA BD
        List<Object[]> resultados = repoFactura.obtenerTopDeudas();

        for (Object[] fila : resultados) {
            // fila[0] = Razón Social, fila[1] = Total (BigDecimal)
            if (fila[0] != null && fila[1] != null) {
                labels.add(fila[0].toString());
                values.add((Number) fila[1]);
            }
        }

        dataSet.setData(values);
        // Configuración visual (colores)
        List<String> bgColors = new ArrayList<>();
        bgColors.add("rgba(255, 99, 132, 0.5)");
        bgColors.add("rgba(255, 159, 64, 0.5)");
        bgColors.add("rgba(255, 205, 86, 0.5)");
        bgColors.add("rgba(75, 192, 192, 0.5)");
        bgColors.add("rgba(54, 162, 235, 0.5)");
        dataSet.setBackgroundColor(bgColors);
        dataSet.setBorderColor("white");
        dataSet.setBorderWidth(1);

        data.addChartDataSet(dataSet);
        data.setLabels(labels);
        proveedoresModel.setData(data);

        // Opciones del gráfico (Ejes, Leyenda, etc)
        BarChartOptions options = new BarChartOptions();
        options.setIndexAxis("y"); // Barras horizontales
        options.setMaintainAspectRatio(false);

        // Configurar ejes blancos para tema oscuro
        CartesianScales cScales = new CartesianScales();
        CartesianLinearAxes linearAxes = new CartesianLinearAxes();
        CartesianLinearTicks ticks = new CartesianLinearTicks();
        // ticks.setColor("white"); // Descomenta si usas tema oscuro
        linearAxes.setTicks(ticks);
        cScales.addXAxesData(linearAxes);
        cScales.addYAxesData(linearAxes);
        options.setScales(cScales);

        proveedoresModel.setOptions(options);
    }

    // --- 2. GRÁFICO DE BARRAS VERTICALES (Productos más vendidos) ---
    private void createProductosModel() {
        productosModel = new BarChartModel();
        ChartData data = new ChartData();
        BarChartDataSet dataSet = new BarChartDataSet();
        dataSet.setLabel("Unidades Vendidas");

        List<Number> values = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        // LLAMADA A LA BD
        try {
            List<Object[]> resultados = repoFacProd.obtenerProductosMasVendidos();
            for (Object[] fila : resultados) {
                // fila[0] = Nombre Producto, fila[1] = Suma Cantidad (Long)
                if (fila[0] != null && fila[1] != null) {
                    labels.add(fila[0].toString());
                    values.add((Number) fila[1]);
                }
            }
        } catch (Exception e) {
            System.out.println("Error al cargar productos: " + e.getMessage());
        }

        dataSet.setData(values);

        // Color uniforme (verde azulado)
        List<String> bgColors = new ArrayList<>();
        for (int i = 0; i < values.size(); i++) {
            bgColors.add("rgba(75, 192, 192, 0.6)");
        }
        dataSet.setBackgroundColor(bgColors);
        dataSet.setBorderColor("rgb(75, 192, 192)");
        dataSet.setBorderWidth(1);

        data.addChartDataSet(dataSet);
        data.setLabels(labels);
        productosModel.setData(data);

        // Opciones
        BarChartOptions options = new BarChartOptions();
        options.setMaintainAspectRatio(false);
        productosModel.setOptions(options);
    }

    // --- 3. GRÁFICO DE DONA (Formas de Pago) ---
    private void createFormasPagoModel() {
        formasPagoModel = new DonutChartModel();
        ChartData data = new ChartData();
        DonutChartDataSet dataSet = new DonutChartDataSet();

        List<Number> values = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        // LLAMADA A LA BD
        List<Object[]> resultados = repoFactura.obtenerTotalesPorFormaPago();

        for (Object[] fila : resultados) {
            // fila[0] = Forma Pago (String), fila[1] = Total (BigDecimal)
            if (fila[0] != null && fila[1] != null) {
                labels.add(fila[0].toString());
                values.add((Number) fila[1]);
            }
        }

        dataSet.setData(values);

        // Colores para la dona
        List<String> bgColors = new ArrayList<>();
        bgColors.add("rgb(255, 99, 132)");
        bgColors.add("rgb(54, 162, 235)");
        bgColors.add("rgb(255, 205, 86)");
        bgColors.add("rgb(75, 192, 192)");
        dataSet.setBackgroundColor(bgColors);

        data.addChartDataSet(dataSet);
        data.setLabels(labels);
        formasPagoModel.setData(data);

        // Opciones (ocultar hueco central si se prefiere pastel, o dejarlo para dona)
        DonutChartOptions options = new DonutChartOptions();
        options.setMaintainAspectRatio(false);
        formasPagoModel.setOptions(options);
    }

    // ---------------------------------------------------------
    // GETTERS (Con Lazy Loading para evitar NullPointerException)
    // ---------------------------------------------------------
    public BarChartModel getProveedoresModel() {
        if (proveedoresModel == null) {
            createProveedoresModel();
        }
        return proveedoresModel;
    }

    public BarChartModel getProductosModel() {
        if (productosModel == null) {
            createProductosModel();
        }
        return productosModel;
    }

    public DonutChartModel getFormasPagoModel() {
        if (formasPagoModel == null) {
            createFormasPagoModel();
        }
        return formasPagoModel;
    }

    public double getDeudaTotal() {
        return deudaTotal;
    }
}
