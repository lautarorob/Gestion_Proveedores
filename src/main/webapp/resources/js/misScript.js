/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/ClientSide/javascript.js to edit this template
 */


function validarProveedor() {

    const errorRZ = document.getElementById("formulario:errorRazonSoc");
    const errorCuit = document.getElementById("formulario:errorCuit");
    const errorNC = document.getElementById("formulario:errorNombreCom");
    const errorTel = document.getElementById("formulario:errorTel");
    const errorEmail = document.getElementById("formulario:errorEmail");
    const errorIVA = document.getElementById("formulario:errorTipoIVA");


    //objetos
    const razonSocialObj = document.getElementById("formulario:razonSocial");
    const cuitObj = document.getElementById("formulario:cuit");
    const nombreComercialObj = document.getElementById("formulario:nombreComercial");
    const telefonoObj = document.getElementById("formulario:telefono");
    const emailObj = document.getElementById("formulario:email");
    const tipoIVAObj = document.getElementById("formulario:tipoIva");

    const razonSocial = razonSocialObj.value.trim();
    const cuit = cuitObj.value.trim();
    const nombreComercial = nombreComercialObj.value.trim();
    const telefono = telefonoObj.value.trim();
    const email = emailObj.value.trim();
    const tipoIVA = tipoIVAObj.value.trim();

    // formato CUIT XX-XXXXXXXX-X (con guiones)
    const regexCuit = /^\d{2}-\d{8}-\d{1}$/;

    // formato email simple algo@algo.algo
    const regexEmail = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/;

    // formato de telefono, tambien admite fijos
    const regexTelefono = /^\+54(?:9\d{10}|\d{10})$/;

    let isValid = true;


    if (razonSocial === "") {
        errorRZ.textContent = "Campo Obligatorio";
        isValid = false;
    }

    if (cuit === "") {
        errorCuit.textContent = "Campo Obligatorio";
        isValid = false;
    } else if (!regexCuit.test(cuit)) {
        errorCuit.textContent = "Fomato Invalido";
        isValid = false;
    }

    if (nombreComercial === "") {
        errorNC.textContent = "Campo Obligatorio";
        isValid = false;
    }

    if (telefono === "") {
        errorTel.textContent = "Campo Obligatorio";
        isValid = false;
    } else if (!regexTelefono.test(telefono)) {
        errorTel.textContent = "Fomato Invalido";
        isValid = false;
    }

    if (email === "") {
        errorEmail.textContent = "Campo Obligatorio";
        isValid = false;
    } else if (!regexEmail.test(email)) {
        errorEmail.textContent = "Fomato Invalido";
        isValid = false;
    }

    if (tipoIVA === "") {
        errorIVA.textContent = "Campo Obligatorio";
        isValid = false;
    }

    return isValid;
}

function validarProducto() {
    // Limpiar todos los mensajes de error previos
    const errorCod = document.getElementById("formulario:errorCodProd");
    const errorDesc = document.getElementById("formulario:errorDesc");
    const errorNombre = document.getElementById("formulario:errorNombre");
    const errorPrecio = document.getElementById("formulario:errorPrecRef");
    const errorUnidadM = document.getElementById("formulario:errorUnidad");
    const errorProv = document.getElementById("formulario:errorProveedor");

    // Limpiar mensajes previos
    if (errorCod)
        errorCod.textContent = "";
    if (errorDesc)
        errorDesc.textContent = "";
    if (errorNombre)
        errorNombre.textContent = "";
    if (errorPrecio)
        errorPrecio.textContent = "";
    if (errorUnidadM)
        errorUnidadM.textContent = "";
    if (errorProv)
        errorProv.textContent = "";

    // Obtener objetos del DOM
    const codProdObj = document.getElementById("formulario:codProd");
    const descripcionObj = document.getElementById("formulario:descripcion");
    const nombreObj = document.getElementById("formulario:nombre");
    const precioReferenciaObj = document.getElementById("formulario:precioReferencia");
    const unidadMedidaObj = document.getElementById("formulario:unidadMedida");
    const idProveedorObj = document.getElementById("formulario:idProveedor");

    // Obtener valores
    const codProd = codProdObj ? codProdObj.value.trim() : "";
    const descripcion = descripcionObj ? descripcionObj.value.trim() : "";
    const nombre = nombreObj ? nombreObj.value.trim() : "";
    const precioReferencia = precioReferenciaObj ? precioReferenciaObj.value.trim() : "";
    const unidadMedida = unidadMedidaObj ? unidadMedidaObj.value.trim() : "";
    const idProveedor = idProveedorObj ? idProveedorObj.value.trim() : "";

    let isValid = true;

    // Validar Código de Producto (FORMATO SOLAMENTE)
    const regex = /^[A-Z]{3}-\d{4}$/;
    if (codProd === "") {
        if (errorCod)
            errorCod.textContent = "Campo Obligatorio";
        isValid = false;
    } else if (!regex.test(codProd)) {
        if (errorCod)
            errorCod.textContent = "Formato Inválido (Ejemplo: ABC-1234)";
        isValid = false;
    }
    // NO validamos duplicidad aquí - eso lo hace el servidor

    // Validar Descripción
    if (descripcion === "") {
        if (errorDesc)
            errorDesc.textContent = "Campo Obligatorio";
        isValid = false;
    }

    // Validar Nombre
    if (nombre === "") {
        if (errorNombre)
            errorNombre.textContent = "Campo Obligatorio";
        isValid = false;
    }

    // Validar Precio de Referencia
    if (precioReferencia === "") {
        if (errorPrecio)
            errorPrecio.textContent = "Campo Obligatorio";
        isValid = false;
    } else if (precioReferencia <= 0) {
        if (errorPrecio)
            errorPrecio.textContent = "Debe ser un número válido mayor a 0";
        isValid = false;
    }

    // Validar Unidad de Medida
    if (unidadMedida === "") {
        if (errorUnidadM)
            errorUnidadM.textContent = "Campo Obligatorio";
        isValid = false;
    }

    // Validar Proveedor
    if (idProveedor === "") {
        if (errorProv)
            errorProv.textContent = "Campo Obligatorio";
        isValid = false;
    }

    // Si pasa las validaciones de formato, permitir que se envíe al servidor
    // El servidor validará la duplicidad y mostrará el mensaje JSF
    return isValid;
}
// facturaProducto.js

// Mapa para almacenar los productos
var productosMap = {};

// Función para inicializar el mapa de productos
function initProductosMap(productos) {
    productosMap = {};
    productos.forEach(function (prod) {
        productosMap[prod.id] = {
            descripcion: prod.descripcion,
            precioReferencia: prod.precioReferencia
        };
    });
    console.log('Productos cargados:', productosMap);
}

// FUNCIÓN usando data attributes
function actualizarDescripcion(selectElement) {
    console.log('=== actualizarDescripcionDirecta llamado ===');

    var inputDescripcion = document.getElementById('formulario:descripcion');
    var inputPrecio = document.getElementById('formulario:precioUnitario');

    if (selectElement && selectElement.selectedIndex > 0) {
        var optionSeleccionada = selectElement.options[selectElement.selectedIndex];

        // Intentar obtener del atributo title (itemDescription)
        var descripcionData = optionSeleccionada.title || optionSeleccionada.getAttribute('title');

        console.log('Option seleccionada:', optionSeleccionada);
        console.log('Title/Description:', descripcionData);
        console.log('Todos los atributos:', {
            value: optionSeleccionada.value,
            text: optionSeleccionada.text,
            title: optionSeleccionada.title,
            label: optionSeleccionada.label
        });

        if (descripcionData && descripcionData.includes('|')) {
            var partes = descripcionData.split('|');
            if (inputDescripcion) {
                inputDescripcion.value = partes[0] || '';
                console.log('Descripción actualizada:', partes[0]);
            }
            if (inputPrecio) {
                inputPrecio.value = partes[1] || '0';
                console.log('Precio actualizado:', partes[1]);
            }
        } else {
            console.log('No se encontró descripción en formato esperado');
            // Intentar con el mapa como fallback
            var valorSeleccionado = optionSeleccionada.value;
            if (productosMap[valorSeleccionado]) {
                var datos = productosMap[valorSeleccionado];
                if (inputDescripcion) {
                    inputDescripcion.value = datos.descripcion || '';
                }
                if (inputPrecio) {
                    inputPrecio.value = datos.precioReferencia || '0';
                }
            }
        }
    } else {
        console.log('No hay selección válida');
        // Limpiar campos
        if (inputDescripcion)
            inputDescripcion.value = '';
        if (inputPrecio)
            inputPrecio.value = '';
    }
}

// Ejecutar al cargar la página
document.addEventListener('DOMContentLoaded', function () {
    console.log('DOM cargado, inicializando...');

    var selectProducto = document.getElementById('formulario:producto');
    if (selectProducto) {
        console.log('Select de producto encontrado');
        actualizarDescripcion(selectProducto);
    } else {
        console.log('No se encontró el select de producto');
    }
});

function validarProductoFactura() {
    const productoObj = document.getElementById("formulario:producto");
    const cantidadObj = document.getElementById("formulario:cantidad");

    if (!productoObj || !cantidadObj) {
        return false;
    }

    const producto = productoObj.value.trim();
    const cantidad = cantidadObj.value.trim();

    let isValid = true;

    if (producto === "" || producto === "null") {
        alert("Debe seleccionar un producto");
        isValid = false;
    }

    if (cantidad === "") {
        alert("Debe ingresar una cantidad");
        isValid = false;
    } else {
        const cantidadNum = parseInt(cantidad);
        if (isNaN(cantidadNum) || cantidadNum <= 0) {
            alert("La cantidad debe ser un número mayor a cero");
            isValid = false;
        }
    }

    return isValid;
}


function validarComprobante(input) {
    const valor = input.value.trim();
    const mensaje = document.getElementById("formulario:errorComprobante");

    // limpiar mensaje previo
    mensaje.textContent = "";

    const patron = /^\d{4}-\d{8}$/;

    if (!patron.test(valor)) {
        mensaje.textContent = "Formato incorrecto: debe ser ####-########";
        return false;
    }

    return true;
}

// Función para toggle del sidebar (móviles)
function toggleSidebar() {
    const sidebar = document.getElementById('sidebar');
    const overlay = document.getElementById('sidebarOverlay');
    sidebar.classList.toggle('show');
    overlay.classList.toggle('show');
}

// Función para cerrar el sidebar (móviles)
function closeSidebar() {
    const sidebar = document.getElementById('sidebar');
    const overlay = document.getElementById('sidebarOverlay');
    sidebar.classList.remove('show');
    overlay.classList.remove('show');
}

// Función para colapsar/expandir el sidebar (desktop)
function toggleCollapse() {
    const sidebar = document.getElementById('sidebar');
    const mainContent = document.getElementById('mainContent');
    const collapseIcon = document.getElementById('collapseIcon');
    
    // 1. Alternar clases
    sidebar.classList.toggle('collapsed');
    mainContent.classList.toggle('expanded');
    
    // 2. Gestionar Icono y LocalStorage
    if (sidebar.classList.contains('collapsed')) {
        collapseIcon.classList.remove('bi-chevron-left');
        collapseIcon.classList.add('bi-chevron-right');
        localStorage.setItem('sidebarCollapsed', 'true');
    } else {
        collapseIcon.classList.remove('bi-chevron-right');
        collapseIcon.classList.add('bi-chevron-left');
        localStorage.setItem('sidebarCollapsed', 'false');
    }

    // 3. FORZAR REDIBUJADO DE GRÁFICOS
    // Disparamos el evento resize 3 veces: al inicio, mitad y final de la transición (300ms)
    setTimeout(() => { window.dispatchEvent(new Event('resize')); }, 50);
    setTimeout(() => { window.dispatchEvent(new Event('resize')); }, 150);
    setTimeout(() => { window.dispatchEvent(new Event('resize')); }, 310);
}

// Restaurar el estado del sidebar al cargar la página
document.addEventListener('DOMContentLoaded', function () {
    // Restaurar estado colapsado
    const isCollapsed = localStorage.getItem('sidebarCollapsed') === 'true';
    if (isCollapsed) {
        const sidebar = document.getElementById('sidebar');
        const mainContent = document.getElementById('mainContent');
        const collapseIcon = document.getElementById('collapseIcon');

        sidebar.classList.add('collapsed');
        mainContent.classList.add('expanded');
        collapseIcon.classList.remove('bi-chevron-left');
        collapseIcon.classList.add('bi-chevron-right');
    }

    // Cerrar sidebar en móviles al hacer clic en un link
    if (window.innerWidth <= 768) {
        const sidebarLinks = document.querySelectorAll('.sidebar .nav-link');
        sidebarLinks.forEach(link => {
            link.addEventListener('click', closeSidebar);
        });
    }
});
