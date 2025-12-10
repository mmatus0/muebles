const API_URL = '/api';
let cotizacionActualId = null;
let muebles = [];
let variantes = [];
let detallesCotizacion = [];

function mostrarMensaje(texto, tipo = 'info') {
    const msg = document.getElementById('mensaje');
    msg.textContent = texto;
    msg.className = `mensaje ${tipo}`;
    msg.style.display = 'block';
    setTimeout(() => msg.style.display = 'none', 3000);
}

async function fetchAPI(endpoint, opciones = {}) {
    try {
        const response = await fetch(`${API_URL}${endpoint}`, {
            headers: { 'Content-Type': 'application/json' },
            ...opciones
        });
        
        const contentType = response.headers.get('content-type');
        if (contentType && contentType.includes('application/json')) {
            const data = await response.json();
            if (!response.ok) throw new Error(data.error || data.message || 'Error en la petición');
            return data;
        } else {
            const text = await response.text();
            if (!response.ok) throw new Error(text);
            return text;
        }
    } catch (error) {
        mostrarMensaje(error.message, 'error');
        throw error;
    }
}

function formatearPrecio(precio) {
    return new Intl.NumberFormat('es-CL', { style: 'currency', currency: 'CLP' }).format(precio || 0);
}

async function cargarCatalogo() {
    try {
        muebles = await fetchAPI('/muebles');
        filtrarCatalogo();
    } catch (error) {
        console.error('Error cargando catálogo:', error);
    }
}

function filtrarCatalogo() {
    const tipoFiltro = document.getElementById('filtro-tipo').value;
    const tamanioFiltro = document.getElementById('filtro-tamanio').value;
    
    let mueblesActivos = muebles.filter(m => m.estadoMueble && m.estadoMueble.toLowerCase() === 'activo');
    
    if (tipoFiltro) {
        mueblesActivos = mueblesActivos.filter(m => m.tipoMueble === tipoFiltro);
    }
    
    if (tamanioFiltro) {
        mueblesActivos = mueblesActivos.filter(m => m.tamanioMueble === tamanioFiltro);
    }
    
    const catalogo = document.getElementById('catalogo-cliente');
    
    if (mueblesActivos.length === 0) {
        catalogo.innerHTML = '<p class="empty-message">No hay muebles disponibles con los filtros seleccionados.</p>';
        return;
    }
    
    catalogo.innerHTML = mueblesActivos.map(m => {
        let stockClass = 'disponible';
        let stockTexto = `Stock: ${m.stock} unidades`;
        if (m.stock === 0) {
            stockClass = 'agotado';
            stockTexto = 'Agotado';
        } else if (m.stock < 5) {
            stockClass = 'bajo';
            stockTexto = `Últimas ${m.stock} unidades`;
        }
        
        return `
            <div class="card-mueble ${m.stock === 0 ? 'agotado' : ''}">
                <span class="tipo-badge">${m.tipoMueble}</span>
                <h3>${m.nombreMueble}</h3>
                <p class="precio">${formatearPrecio(m.precioBase)}</p>
                <p class="info">${m.tamanioMueble} | ${m.materialMueble}</p>
                <p class="stock ${stockClass}">${stockTexto}</p>
                ${m.stock > 0 && cotizacionActualId ? 
                    `<button onclick="agregarRapido(${m.idMueble})" class="btn-agregar">Agregar a cotización</button>` : 
                    ''}
            </div>
        `;
    }).join('');
}

async function cargarSelectores() {
    try {
        muebles = await fetchAPI('/muebles');
        variantes = await fetchAPI('/variantes');
        
        const selectMueble = document.getElementById('select-mueble');
        const selectVariante = document.getElementById('select-variante');
        
        const mueblesActivos = muebles.filter(m => m.estadoMueble && m.estadoMueble.toLowerCase() === 'activo' && m.stock > 0);
        
        selectMueble.innerHTML = mueblesActivos.map(m => 
            `<option value="${m.idMueble}">${m.nombreMueble} - ${formatearPrecio(m.precioBase)} (Stock: ${m.stock})</option>`
        ).join('');
        
        selectVariante.innerHTML = '<option value="">Normal (sin variante)</option>' +
            variantes.map(v => 
                `<option value="${v.idVariante}">${v.nombreVariante} (+${formatearPrecio(v.precioAgregado)})</option>`
            ).join('');
    } catch (error) {
        console.error('Error cargando selectores:', error);
    }
}

async function crearNuevaCotizacion() {
    try {
        const cotizacion = await fetchAPI('/cotizaciones', { method: 'POST' });
        cotizacionActualId = cotizacion.idCotizacion;
        
        document.getElementById('cotizacion-estado').style.display = 'none';
        document.getElementById('cotizacion-activa').style.display = 'block';
        document.getElementById('agregar-producto').style.display = 'block';
        
        document.getElementById('cotizacion-numero').textContent = `Cotización #${cotizacionActualId}`;
        document.getElementById('cotizacion-status').textContent = 'Pendiente';
        document.getElementById('cotizacion-status').className = 'status-badge pendiente';
        document.getElementById('lista-detalles').innerHTML = '';
        document.getElementById('cotizacion-total').textContent = formatearPrecio(0);
        
        await cargarSelectores();
        filtrarCatalogo();
        
        mostrarMensaje('Cotización creada exitosamente', 'exito');
    } catch (error) {
        console.error('Error creando cotización:', error);
    }
}

async function agregarDetalle(event) {
    event.preventDefault();
    
    if (!cotizacionActualId) {
        mostrarMensaje('Primero debe crear una cotización', 'error');
        return;
    }
    
    const idMueble = document.getElementById('select-mueble').value;
    const idVariante = document.getElementById('select-variante').value || null;
    const cantidad = parseInt(document.getElementById('input-cantidad').value);
    
    try {
        await fetchAPI(`/cotizaciones/${cotizacionActualId}/detalles`, {
            method: 'POST',
            body: JSON.stringify({ idMueble, idVariante, cantidad })
        });
        
        mostrarMensaje('Producto agregado a la cotización', 'exito');
        await cargarDetalleCotizacion();
        await cargarSelectores();
        filtrarCatalogo();
        document.getElementById('input-cantidad').value = 1;
    } catch (error) {
        console.error('Error agregando detalle:', error);
    }
}

async function agregarRapido(idMueble) {
    if (!cotizacionActualId) {
        mostrarMensaje('Primero debe crear una cotización', 'error');
        return;
    }
    
    try {
        await fetchAPI(`/cotizaciones/${cotizacionActualId}/detalles`, {
            method: 'POST',
            body: JSON.stringify({ idMueble, idVariante: null, cantidad: 1 })
        });
        
        mostrarMensaje('Producto agregado', 'exito');
        await cargarDetalleCotizacion();
        await cargarSelectores();
        filtrarCatalogo();
    } catch (error) {
        console.error('Error agregando producto:', error);
    }
}

async function cargarDetalleCotizacion() {
    if (!cotizacionActualId) return;
    
    try {
        const cotizacion = await fetchAPI(`/cotizaciones/${cotizacionActualId}`);
        
        document.getElementById('cotizacion-total').textContent = formatearPrecio(cotizacion.precioFinal);
        
        if (cotizacion.detalles && cotizacion.detalles.length > 0) {
            document.getElementById('lista-detalles').innerHTML = `
                <table class="tabla-detalles">
                    <thead>
                        <tr>
                            <th>Producto</th>
                            <th>Variante</th>
                            <th>Cant.</th>
                            <th>Subtotal</th>
                            <th></th>
                        </tr>
                    </thead>
                    <tbody>
                        ${cotizacion.detalles.map(d => `
                            <tr>
                                <td>${d.mueble ? d.mueble.nombreMueble : 'N/A'}</td>
                                <td>${d.variante ? d.variante.nombreVariante : 'Normal'}</td>
                                <td>${d.cantidad}</td>
                                <td>${formatearPrecio(d.subtotal)}</td>
                                <td>
                                    <button onclick="eliminarDetalle(${d.idDetalleCoti})" class="btn-eliminar-sm">×</button>
                                </td>
                            </tr>
                        `).join('')}
                    </tbody>
                </table>
            `;
        } else {
            document.getElementById('lista-detalles').innerHTML = '<p class="empty-message">No hay productos agregados</p>';
        }
    } catch (error) {
        console.error('Error cargando detalle:', error);
    }
}

async function eliminarDetalle(idDetalle) {
    if (!confirm('¿Eliminar este producto de la cotización?')) return;
    
    try {
        await fetchAPI(`/cotizaciones/detalles/${idDetalle}`, { method: 'DELETE' });
        mostrarMensaje('Producto eliminado', 'info');
        await cargarDetalleCotizacion();
        await cargarSelectores();
        filtrarCatalogo();
    } catch (error) {
        console.error('Error eliminando detalle:', error);
    }
}

async function confirmarVenta() {
    if (!cotizacionActualId) {
        mostrarMensaje('No hay cotización activa', 'error');
        return;
    }
    
    if (!confirm('¿Confirmar esta cotización como compra?')) return;
    
    try {
        const resultado = await fetchAPI(`/ventas/confirmar/${cotizacionActualId}`, { method: 'POST' });
        mostrarMensaje(`Compra confirmada. Total: ${formatearPrecio(resultado.total)}`, 'exito');
        resetearCotizacion();
    } catch (error) {
        console.error('Error confirmando venta:', error);
    }
}

async function cancelarCotizacion() {
    if (!cotizacionActualId) return;
    
    if (!confirm('¿Cancelar esta cotización?')) return;
    
    try {
        await fetchAPI(`/cotizaciones/${cotizacionActualId}/cancelar`, { method: 'PUT' });
        mostrarMensaje('Cotización cancelada', 'info');
        resetearCotizacion();
    } catch (error) {
        console.error('Error cancelando cotización:', error);
    }
}

function resetearCotizacion() {
    cotizacionActualId = null;
    document.getElementById('cotizacion-estado').style.display = 'block';
    document.getElementById('cotizacion-activa').style.display = 'none';
    document.getElementById('agregar-producto').style.display = 'none';
    cargarCatalogo();
}

document.addEventListener('DOMContentLoaded', () => {
    cargarCatalogo();
});