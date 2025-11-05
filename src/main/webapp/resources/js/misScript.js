/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/ClientSide/javascript.js to edit this template
 */


function validarProveedor() {
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
        alert("Falta razon social");
        isValid = false;
    }

    if (cuit === "") {
        alert("Falta cuit");
        isValid = false;
    } else if (!regexCuit.test(cuit)) {
        alert("Cuit Invalido");
        isValid = false;
    }

    if (nombreComercial === "") {
        alert("Falta nombre Comercial");
        isValid = false;
    }

    if (telefono === "") {
        alert("Falta telefono");
        isValid = false;
    } else if (!regexTelefono.test(telefono)) {
        alert("Telefono Invalido");
        isValid = false;
    }

    if (email === "") {
        alert("Falta email");
        isValid = false;
    } else if (!regexEmail.test(email)) {
        alert("Email Invalido");
        isValid = false;
    }

    if (tipoIVA === "") {
        alert("Falta tipoIVA");
        isValid = false;
    }

    return isValid;
}

function validarProducto() {
    //objetos
    const codProdObj = document.getElementById("formulario:codProd");
    const descripcionObj = document.getElementById("formulario:descripcion");
    const nombreObj = document.getElementById("formulario:nombre");
    const precioReferenciaObj = document.getElementById("formulario:precioReferencia");
    const unidadMedidaObj = document.getElementById("formulario:unidadMedida");
    const idProveedorObj = document.getElementById("formulario:idProveedor");

    const codProd = codProdObj.value.trim();
    const descripcion = descripcionObj.value.trim();
    const nombre = nombreObj.value.trim();
    const precioReferencia = precioReferenciaObj.value.trim();
    const unidadMedida = unidadMedidaObj.value.trim();
    const idProveedor = idProveedorObj.value.trim();

    let isValid = true;


    if (codProd === "") {
        alert("Falta codProd");
        isValid = false;
    }

    if (descripcion === "") {
        alert("Falta descripcion");
        isValid = false;
    }

    if (nombre === "") {
        alert("Falta nombre");
        isValid = false;
    }

    if (precioReferencia === "") {
        alert("Falta precioReferencia");
        isValid = false;
    }

    if (unidadMedida === "") {
        alert("Falta unidadMedida");
        isValid = false;
    }

    if (idProveedor === "") {
        alert("Falta idProveedor");
        isValid = false;
    }

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
function actualizarDescripcionDirecta(selectElement) {
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
        if (inputDescripcion) inputDescripcion.value = '';
        if (inputPrecio) inputPrecio.value = '';
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