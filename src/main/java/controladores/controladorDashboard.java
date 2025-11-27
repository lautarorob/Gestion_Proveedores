package controladores;

import jakarta.annotation.PostConstruct;
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

@Named(value = "controladorDashboard")
@ViewScoped
public class controladorDashboard implements Serializable {

    @Inject
    private repoFactura repoFactura;

    @Inject
    private repoFacturaProducto repoFacProd;

    private BarChartModel proveedoresModel;
    private BarChartModel productosModel;
    private BarChartModel cuentaCorrienteModel; // NUEVO
    private DonutChartModel formasPagoModel;
    private double deudaTotal;

    @PostConstruct
    public void init() {
        // 1. Cargar KPI
        try {
            this.deudaTotal = repoFactura.obtenerDeudaTotal();
        } catch (Exception e) {
            this.deudaTotal = 0.0;
            System.out.println("Error cargando deuda: " + e.getMessage());
        }

        // 2. Crear Gráficos
        createProveedoresModel();
        createProductosModel();
        createFormasPagoModel();
        createCuentaCorrienteModel(); // NUEVO
    }

    // ---------------------------------------------------------
    // 1. GRÁFICO DE BARRAS HORIZONTALES (Top Deuda Proveedores)
    // ---------------------------------------------------------
    private void createProveedoresModel() {
        proveedoresModel = new BarChartModel();
        ChartData data = new ChartData();
        BarChartDataSet dataSet = new BarChartDataSet();
        dataSet.setLabel("Deuda ($)");

        List<Number> values = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        try {
            List<Object[]> resultados = repoFactura.obtenerTopDeudas();
            for (Object[] fila : resultados) {
                if (fila[0] != null && fila[1] != null) {
                    labels.add(fila[0].toString());
                    values.add((Number) fila[1]);
                }
            }
        } catch (Exception e) {
            System.out.println("Error proveedores: " + e.getMessage());
        }

        dataSet.setData(values);

        // Paleta de colores degradado azul-amarillo
        List<String> bgColors = new ArrayList<>();
        bgColors.add("rgba(240, 185, 11, 0.8)");   // #F0B90B - Amarillo principal
        bgColors.add("rgba(240, 185, 11, 0.6)");   // Amarillo más suave
        bgColors.add("rgba(100, 150, 220, 0.7)");  // Azul intermedio
        bgColors.add("rgba(60, 100, 180, 0.7)");   // Azul medio
        bgColors.add("rgba(28, 37, 51, 0.8)");     // #1C2533 - Azul oscuro

        dataSet.setBackgroundColor(bgColors);
        dataSet.setBorderColor("#F0B90B");
        dataSet.setBorderWidth(2);

        data.addChartDataSet(dataSet);
        data.setLabels(labels);
        proveedoresModel.setData(data);

        // Opciones (Modo Oscuro)
        BarChartOptions options = new BarChartOptions();
        options.setIndexAxis("y"); // Horizontal
        options.setMaintainAspectRatio(false);

        CartesianScales cScales = new CartesianScales();
        CartesianLinearAxes linearAxes = new CartesianLinearAxes();
        CartesianLinearTicks ticks = new CartesianLinearTicks();
        linearAxes.setTicks(ticks);
        cScales.addXAxesData(linearAxes);
        cScales.addYAxesData(linearAxes);
        options.setScales(cScales);

        // Leyenda blanca
        Legend legend = new Legend();
        LegendLabel legendLabels = new LegendLabel();
        legendLabels.setFontColor("white");
        legend.setLabels(legendLabels);
        options.setLegend(legend);

        proveedoresModel.setOptions(options);
    }

    // ---------------------------------------------------------
    // 2. GRÁFICO DE BARRAS VERTICALES (Top Productos)
    // ---------------------------------------------------------
    private void createProductosModel() {
        productosModel = new BarChartModel();
        ChartData data = new ChartData();
        BarChartDataSet dataSet = new BarChartDataSet();
        dataSet.setLabel("Unidades Vendidas");

        List<Number> values = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        try {
            List<Object[]> resultados = repoFacProd.obtenerProductosMasVendidos();
            for (Object[] fila : resultados) {
                if (fila[0] != null && fila[1] != null) {
                    labels.add(fila[0].toString());
                    values.add((Number) fila[1]);
                }
            }
        } catch (Exception e) {
            System.out.println("Error productos: " + e.getMessage());
        }

        dataSet.setData(values);

        // Color amarillo principal con transparencia
        List<String> bgColors = new ArrayList<>();
        for (int i = 0; i < values.size(); i++) {
            bgColors.add("rgba(240, 185, 11, 0.7)"); // #F0B90B
        }
        dataSet.setBackgroundColor(bgColors);
        dataSet.setBorderColor("rgb(240, 185, 11)");
        dataSet.setBorderWidth(2);

        data.addChartDataSet(dataSet);
        data.setLabels(labels);
        productosModel.setData(data);

        // Opciones (Modo Oscuro)
        BarChartOptions options = new BarChartOptions();
        options.setMaintainAspectRatio(false);

        CartesianScales cScales = new CartesianScales();
        CartesianLinearAxes linearAxes = new CartesianLinearAxes();
        CartesianLinearTicks ticks = new CartesianLinearTicks();
        linearAxes.setTicks(ticks);
        cScales.addXAxesData(linearAxes);
        cScales.addYAxesData(linearAxes);
        options.setScales(cScales);

        Legend legend = new Legend();
        LegendLabel legendLabels = new LegendLabel();
        legendLabels.setFontColor("white");
        legend.setLabels(legendLabels);
        options.setLegend(legend);

        productosModel.setOptions(options);
    }

    // ---------------------------------------------------------
    // 3. GRÁFICO DE DONA (Contado + Desglose Cta Cte)
    // ---------------------------------------------------------
    private void createFormasPagoModel() {
        formasPagoModel = new DonutChartModel();
        ChartData data = new ChartData();
        DonutChartDataSet dataSet = new DonutChartDataSet();

        List<Number> values = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        List<String> bgColors = new ArrayList<>();

        try {
            List<Object[]> resultados = repoFactura.obtenerTotalesPorFormaPago();

            System.out.println("=== CREANDO GRÁFICO DE DONA ===");
            System.out.println("Resultados obtenidos: " + resultados.size());

            if (resultados.isEmpty()) {
                System.out.println("⚠️ NO HAY DATOS PARA EL GRÁFICO DE DONA");
                labels.add("Sin Datos");
                values.add(1);
                bgColors.add("rgba(100, 110, 130, 0.7)");
            } else {
                for (Object[] fila : resultados) {
                    if (fila[0] != null && fila[1] != null) {
                        String tipoPago = fila[0].toString().trim();
                        Number monto = (Number) fila[1];

                        System.out.println("Procesando: " + tipoPago + " = $" + monto);

                        labels.add(tipoPago);
                        values.add(monto);

                        // PALETA DE COLORES: Azules y Amarillo
                        switch (tipoPago.toLowerCase()) {
                            case "contado":
                                bgColors.add("rgba(240, 185, 11, 0.9)"); // #F0B90B - Amarillo principal
                                break;
                            case "efectivo":
                                bgColors.add("rgba(28, 37, 51, 0.9)"); // Azul grisáceo
                                break;
                            case "transferencia":
                                bgColors.add("rgba(2, 6, 24, 0.9)"); // Azul oscuro
                                break;
                            case "cheque":
                                bgColors.add("rgba(110, 70, 20, 0.9)"); // Bronce profundo

                                break;
                            case "cuenta corriente":
                                bgColors.add("rgba(28, 37, 51, 0.9)"); // #1C2533 - Azul oscuro
                                break;
                            default:
                                System.out.println("⚠️ Forma de pago no reconocida: " + tipoPago);
                                bgColors.add("rgba(100, 110, 130, 0.7)"); // Gris azulado
                                break;
                        }
                    }
                }
            }

            System.out.println("Total labels: " + labels.size());
            System.out.println("Total values: " + values.size());

        } catch (Exception e) {
            System.out.println("❌ Error en createFormasPagoModel: " + e.getMessage());
            e.printStackTrace();

            labels.add("Error");
            values.add(1);
            bgColors.add("rgba(180, 50, 50, 0.8)");
        }

        dataSet.setData(values);
        dataSet.setBackgroundColor(bgColors);

        data.addChartDataSet(dataSet);
        data.setLabels(labels);
        formasPagoModel.setData(data);

        DonutChartOptions options = new DonutChartOptions();
        options.setMaintainAspectRatio(false);

        Legend legend = new Legend();
        LegendLabel legendLabels = new LegendLabel();
        legendLabels.setFontColor("white");
        legend.setLabels(legendLabels);
        legend.setDisplay(true);
        options.setLegend(legend);

        formasPagoModel.setOptions(options);
    }

    // ---------------------------------------------------------
// 4. GRÁFICO COMPARATIVO HISTÓRICO CTA CTE VS ÓRDENES DE PAGO (VERTICAL)
// ---------------------------------------------------------
    private void createCuentaCorrienteModel() {
        cuentaCorrienteModel = new BarChartModel();
        ChartData data = new ChartData();

        List<String> labels = new ArrayList<>();
        labels.add("Facturas Cta Cte");
        labels.add("Órdenes de Pago");

        List<Number> values = new ArrayList<>();
        values.add(0); // Valor por defecto
        values.add(0); // Valor por defecto

        try {
            List<Object[]> resultados = repoFactura.obtenerComparacionCuentaCorriente();

            System.out.println("=== CREANDO GRÁFICO CUENTA CORRIENTE ===");
            System.out.println("Resultados obtenidos: " + resultados.size());

            for (Object[] fila : resultados) {
                if (fila[0] != null && fila[1] != null) {
                    String concepto = fila[0].toString();
                    Number monto = (Number) fila[1];

                    System.out.println("Procesando: " + concepto + " = $" + monto);

                    if (concepto.equals("Facturas Cta Cte")) {
                        values.set(0, monto);
                    } else if (concepto.equals("Órdenes de Pago")) {
                        values.set(1, monto);
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("❌ Error en createCuentaCorrienteModel: " + e.getMessage());
            e.printStackTrace();
        }

        // Dataset único con dos barras
        BarChartDataSet dataSet = new BarChartDataSet();
        dataSet.setLabel("Monto Histórico ($)");
        dataSet.setData(values);

        // Colores: Amarillo para total facturado, Azul para lo pagado
        List<String> bgColors = new ArrayList<>();
        bgColors.add("rgba(240, 185, 11, 0.8)");  // Amarillo para Facturas (total)
        bgColors.add("rgba(60, 100, 180, 0.8)");  // Azul para Órdenes de Pago (pagado)

        dataSet.setBackgroundColor(bgColors);

        List<String> borderColors = new ArrayList<>();
        borderColors.add("rgb(240, 185, 11)");  // Borde amarillo
        borderColors.add("rgb(60, 100, 180)");  // Borde azul

        dataSet.setBorderColor(borderColors);

        List<Number> borderWidths = new ArrayList<>();
        borderWidths.add(2);
        borderWidths.add(2);

        dataSet.setBorderWidth(borderWidths);

        data.addChartDataSet(dataSet);
        data.setLabels(labels);

        cuentaCorrienteModel.setData(data);

        // Opciones (VERTICAL)
        BarChartOptions options = new BarChartOptions();
        options.setMaintainAspectRatio(false);

        CartesianScales cScales = new CartesianScales();
        CartesianLinearAxes linearAxes = new CartesianLinearAxes();
        CartesianLinearTicks ticks = new CartesianLinearTicks();
        linearAxes.setTicks(ticks);
        cScales.addXAxesData(linearAxes);
        cScales.addYAxesData(linearAxes);
        options.setScales(cScales);

        Legend legend = new Legend();
        LegendLabel legendLabels = new LegendLabel();
        legendLabels.setFontColor("white");
        legend.setLabels(legendLabels);
        legend.setDisplay(true);
        options.setLegend(legend);

        cuentaCorrienteModel.setOptions(options);
    }

    // ---------------------------------------------------------
    // GETTERS (Lazy Loading)
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

    public BarChartModel getCuentaCorrienteModel() {
        if (cuentaCorrienteModel == null) {
            createCuentaCorrienteModel();
        }
        return cuentaCorrienteModel;
    }

    public double getDeudaTotal() {
        return deudaTotal;
    }
}
