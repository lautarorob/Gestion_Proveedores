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
    }else if (!regexTelefono.test(telefono)) {
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

function validarProducto(){
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

