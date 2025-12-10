const API_URL = '/api';
let cotizacionActualId = null;
let muebles = [];
let variantes = [];

// ==================== UTILIDADES ====================

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

// ==================== NAVEGACIÓN ====================

function mostrarVista(vista) {
    document.getElementById('vista-cliente').style.display = vista === 'cliente' ? 'block' : 'none';
    document.getElementById('vista-admin').style.display = vista === 'admin' ? 'block' : 'none';
    
    document.getElementById('btn-cliente').classList.toggle('active', vista === 'cliente');
    document.getElementById('btn-admin').classList.toggle('active', vista === 'admin');
    
    if (vista === 'cliente') {
        cargarCatalogoCliente();
        cargarSelectores();
    } else {
        cargarMuebles();
        cargarVariantes();
    }
}

function mostrarSeccion(seccionId) {
    document.querySelectorAll('.admin-section').forEach(sec => sec.style.display = 'none');
    document.getElementById(seccionId).style.display = 'block';
    
    document.querySelectorAll('.tab-btn').forEach(btn => btn.classList.remove('active'));
    event.target.classList.add('active');
    
    if (seccionId === 'sec-muebles') cargarMuebles();
    if (seccionId === 'sec-variantes') cargarVariantes();
    if (seccionId === 'sec-cotizaciones') cargarCotizaciones();
    if (seccionId === 'sec-ventas') cargarVentas();
}

// ==================== Ver catalogo desde cliente ====================

async function cargarCatalogoCliente() {
    try {
        muebles = await fetchAPI('/muebles');
        const catalogo = document.getElementById('catalogo-cliente');
        
        const mueblesActivos = muebles.filter(m => m.estadoMueble === 'Activo');
        
        if (mueblesActivos.length === 0) {
            catalogo.innerHTML = '<p>No hay muebles disponibles en este momento.</p>';
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
                stockTexto = `¡Últimas ${m.stock} unidades!`;
            }
            
            return `
                <div class="card-mueble">
                    <span class="tipo">${m.tipoMueble}</span>
                    <h3>${m.nombreMueble}</h3>
                    <p class="precio">${formatearPrecio(m.precioBase)}</p>
                    <p class="info">${m.tamanioMueble} | ${m.materialMueble}</p>
                    <p class="stock ${stockClass}">${stockTexto}</p>
                </div>
            `;
        }).join('');
    } catch (error) {
        console.error('Error cargando catálogo:', error);
    }
}

async function cargarSelectores() {
    try {
        muebles = await fetchAPI('/muebles');
        variantes = await fetchAPI('/variantes');
        
        const selectMueble = document.getElementById('select-mueble');
        const selectVariante = document.getElementById('select-variante');
        
        const mueblesActivos = muebles.filter(m => m.estadoMueble === 'Activo' && m.stock > 0);
        
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
        
        document.getElementById('cotizacion-actual').innerHTML = `
            <strong>Cotización #${cotizacionActualId}</strong> - Estado: ${cotizacion.estadoCotizacion}
        `;
        document.getElementById('cotizacion-actual').classList.add('pendiente');
        document.getElementById('acciones-cotizacion').style.display = 'block';
        document.getElementById('detalles-cotizacion').innerHTML = '';
        
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
        document.getElementById('input-cantidad').value = 1;
    } catch (error) {
        console.error('Error agregando detalle:', error);
    }
}

async function cargarDetalleCotizacion() {
    if (!cotizacionActualId) return;
    
    try {
        const cotizacion = await fetchAPI(`/cotizaciones/${cotizacionActualId}`);
        
        document.getElementById('cotizacion-actual').innerHTML = `
            <strong>Cotización #${cotizacionActualId}</strong> - 
            Estado: ${cotizacion.estadoCotizacion} - 
            <strong>Total: ${formatearPrecio(cotizacion.precioFinal)}</strong>
        `;
    } catch (error) {
        console.error('Error cargando detalle:', error);
    }
}

async function confirmarVenta() {
    if (!cotizacionActualId) {
        mostrarMensaje('No hay cotización activa', 'error');
        return;
    }
    
    if (!confirm('¿confirmar cotización como venta?')) return;
    
    try {
        const resultado = await fetchAPI(`/ventas/confirmar/${cotizacionActualId}`, { method: 'POST' });
        mostrarMensaje(`¡Venta confirmada! Total: ${formatearPrecio(resultado.total)}`, 'exito');
        
        // Resetear estado
        cotizacionActualId = null;
        document.getElementById('cotizacion-actual').innerHTML = 
            '<p>No hay cotización activa. <button onclick="crearNuevaCotizacion()">Crear Nueva Cotización</button></p>';
        document.getElementById('cotizacion-actual').classList.remove('pendiente');
        document.getElementById('acciones-cotizacion').style.display = 'none';
        document.getElementById('detalles-cotizacion').innerHTML = '';
        
        cargarCatalogoCliente();
        cargarSelectores();
    } catch (error) {
        console.error('Error confirmando venta:', error);
    }
}

async function cancelarCotizacion() {
    if (!cotizacionActualId) return;
    
    if (!confirm('¿cancelar cotización?')) return;
    
    try {
        await fetchAPI(`/cotizaciones/${cotizacionActualId}/cancelar`, { method: 'PUT' });
        mostrarMensaje('Cotización cancelada', 'info');
        
        cotizacionActualId = null;
        document.getElementById('cotizacion-actual').innerHTML = 
            '<p>No hay cotización activa. <button onclick="crearNuevaCotizacion()">Crear Nueva Cotización</button></p>';
        document.getElementById('cotizacion-actual').classList.remove('pendiente');
        document.getElementById('acciones-cotizacion').style.display = 'none';
        document.getElementById('detalles-cotizacion').innerHTML = '';
    } catch (error) {
        console.error('Error cancelando cotización:', error);
    }
}

// ==================== ADMIN: MUEBLES ====================

async function cargarMuebles() {
    try {
        muebles = await fetchAPI('/muebles');
        const tbody = document.querySelector('#tabla-muebles tbody');
        
        tbody.innerHTML = muebles.map(m => `
            <tr>
                <td>${m.idMueble}</td>
                <td>${m.nombreMueble}</td>
                <td>${m.tipoMueble}</td>
                <td>${formatearPrecio(m.precioBase)}</td>
                <td>${m.stock}</td>
                <td>${m.estadoMueble}</td>
                <td>${m.tamanioMueble}</td>
                <td>${m.materialMueble}</td>
                <td>
                    <button class="btn-editar" onclick="editarMueble(${m.idMueble})">Editar</button>
                    <button class="btn-eliminar" onclick="desactivarMueble(${m.idMueble})">Desactivar</button>
                </td>
            </tr>
        `).join('');
    } catch (error) {
        console.error('Error cargando muebles:', error);
    }
}

function mostrarFormMueble() {
    document.getElementById('form-mueble-container').style.display = 'block';
    document.getElementById('titulo-form-mueble').textContent = 'Nuevo Mueble';
    document.getElementById('form-mueble').reset();
    document.getElementById('mueble-id').value = '';
}

function cancelarFormMueble() {
    document.getElementById('form-mueble-container').style.display = 'none';
    document.getElementById('form-mueble').reset();
}

async function editarMueble(id) {
    try {
        const mueble = await fetchAPI(`/muebles/${id}`);
        
        document.getElementById('mueble-id').value = mueble.idMueble;
        document.getElementById('mueble-nombre').value = mueble.nombreMueble;
        document.getElementById('mueble-tipo').value = mueble.tipoMueble;
        document.getElementById('mueble-precio').value = mueble.precioBase;
        document.getElementById('mueble-stock').value = mueble.stock;
        document.getElementById('mueble-estado').value = mueble.estadoMueble;
        document.getElementById('mueble-tamanio').value = mueble.tamanioMueble;
        document.getElementById('mueble-material').value = mueble.materialMueble;
        
        document.getElementById('form-mueble-container').style.display = 'block';
        document.getElementById('titulo-form-mueble').textContent = 'Editar Mueble';
    } catch (error) {
        console.error('Error cargando mueble:', error);
    }
}

async function guardarMueble(event) {
    event.preventDefault();
    
    const id = document.getElementById('mueble-id').value;
    const mueble = {
        nombreMueble: document.getElementById('mueble-nombre').value,
        tipoMueble: document.getElementById('mueble-tipo').value,
        precioBase: parseFloat(document.getElementById('mueble-precio').value),
        stock: parseInt(document.getElementById('mueble-stock').value),
        estadoMueble: document.getElementById('mueble-estado').value,
        tamanioMueble: document.getElementById('mueble-tamanio').value,
        materialMueble: document.getElementById('mueble-material').value
    };
    
    try {
        if (id) {
            await fetchAPI(`/muebles/${id}`, { method: 'PUT', body: JSON.stringify(mueble) });
            mostrarMensaje('Mueble actualizado', 'exito');
        } else {
            await fetchAPI('/muebles', { method: 'POST', body: JSON.stringify(mueble) });
            mostrarMensaje('Mueble creado', 'exito');
        }
        
        cancelarFormMueble();
        cargarMuebles();
    } catch (error) {
        console.error('Error guardando mueble:', error);
    }
}

async function desactivarMueble(id) {
    if (!confirm('¿Desactivar mueble?')) return;
    
    try {
        await fetchAPI(`/muebles/${id}`, { method: 'DELETE' });
        mostrarMensaje('Mueble desactivado', 'info');
        cargarMuebles();
    } catch (error) {
        console.error('Error desactivando mueble:', error);
    }
}

// ==================== Ver variantes como admin ====================

async function cargarVariantes() {
    try {
        variantes = await fetchAPI('/variantes');
        const tbody = document.querySelector('#tabla-variantes tbody');
        
        tbody.innerHTML = variantes.map(v => `
            <tr>
                <td>${v.idVariante}</td>
                <td>${v.nombreVariante}</td>
                <td>${formatearPrecio(v.precioAgregado)}</td>
                <td>${v.descripcionVariante || '-'}</td>
                <td>
                    <button class="btn-editar" onclick="editarVariante(${v.idVariante})">Editar</button>
                    <button class="btn-eliminar" onclick="eliminarVariante(${v.idVariante})">Eliminar</button>
                </td>
            </tr>
        `).join('');
    } catch (error) {
        console.error('Error cargando variantes:', error);
    }
}

function mostrarFormVariante() {
    document.getElementById('form-variante-container').style.display = 'block';
    document.getElementById('titulo-form-variante').textContent = 'Nueva Variante';
    document.getElementById('form-variante').reset();
    document.getElementById('variante-id').value = '';
}

function cancelarFormVariante() {
    document.getElementById('form-variante-container').style.display = 'none';
    document.getElementById('form-variante').reset();
}

async function editarVariante(id) {
    try {
        const variante = await fetchAPI(`/variantes/${id}`);
        
        document.getElementById('variante-id').value = variante.idVariante;
        document.getElementById('variante-nombre').value = variante.nombreVariante;
        document.getElementById('variante-precio').value = variante.precioAgregado;
        document.getElementById('variante-descripcion').value = variante.descripcionVariante || '';
        
        document.getElementById('form-variante-container').style.display = 'block';
        document.getElementById('titulo-form-variante').textContent = 'Editar Variante';
    } catch (error) {
        console.error('Error cargando variante:', error);
    }
}

async function guardarVariante(event) {
    event.preventDefault();
    
    const id = document.getElementById('variante-id').value;
    const variante = {
        nombreVariante: document.getElementById('variante-nombre').value,
        precioAgregado: parseFloat(document.getElementById('variante-precio').value),
        descripcionVariante: document.getElementById('variante-descripcion').value
    };
    
    try {
        if (id) {
            await fetchAPI(`/variantes/${id}`, { method: 'PUT', body: JSON.stringify(variante) });
            mostrarMensaje('Variante actualizada!', 'exito');
        } else {
            await fetchAPI('/variantes', { method: 'POST', body: JSON.stringify(variante) });
            mostrarMensaje('Variante creada!', 'exito');
        }
        
        cancelarFormVariante();
        cargarVariantes();
    } catch (error) {
        console.error('Error guardando variante:', error);
    }
}

async function eliminarVariante(id) {
    if (!confirm('¿Desea eliminar variante?')) return;
    
    try {
        await fetchAPI(`/variantes/${id}`, { method: 'DELETE' });
        mostrarMensaje('Variante eliminada', 'info');
        cargarVariantes();
    } catch (error) {
        console.error('Error eliminando variante:', error);
    }
}

// ==================== Ver cotizaciones desde admin ====================

async function cargarCotizaciones() {
    try {
        const cotizaciones = await fetchAPI('/cotizaciones');
        const tbody = document.querySelector('#tabla-cotizaciones tbody');
        
        tbody.innerHTML = cotizaciones.map(c => `
            <tr>
                <td>${c.idCotizacion}</td>
                <td>${c.fechaCotizacion || '-'}</td>
                <td>${c.estadoCotizacion}</td>
                <td>${formatearPrecio(c.precioFinal)}</td>
                <td>
                    ${c.estadoCotizacion === 'Pendiente' ? `
                        <button class="btn-editar" onclick="confirmarVentaAdmin(${c.idCotizacion})">Confirmar Venta</button>
                        <button class="btn-eliminar" onclick="cancelarCotizacionAdmin(${c.idCotizacion})">Cancelar</button>
                    ` : '-'}
                </td>
            </tr>
        `).join('');
    } catch (error) {
        console.error('Error cargando cotizaciones:', error);
    }
}

async function confirmarVentaAdmin(id) {
    if (!confirm('¿Confirmar esta cotización como venta?')) return;
    
    try {
        await fetchAPI(`/ventas/confirmar/${id}`, { method: 'POST' });
        mostrarMensaje('Venta confirmada', 'exito');
        cargarCotizaciones();
    } catch (error) {
        console.error('Error confirmando venta:', error);
    }
}

async function cancelarCotizacionAdmin(id) {
    if (!confirm('¿Cancelar esta cotización?')) return;
    
    try {
        await fetchAPI(`/cotizaciones/${id}/cancelar`, { method: 'PUT' });
        mostrarMensaje('Cotización cancelada', 'info');
        cargarCotizaciones();
    } catch (error) {
        console.error('Error cancelando cotización:', error);
    }
}

// ==================== Cargar las ventas ====================

async function cargarVentas() {
    try {
        const [ventas, estadisticas] = await Promise.all([
            fetchAPI('/ventas'),
            fetchAPI('/ventas/estadisticas/total')
        ]);
        
        // Estadísticas
        document.getElementById('estadisticas-ventas').innerHTML = `
            <div class="stat-card">
                <h4>Total Ventas</h4>
                <div class="valor">${formatearPrecio(estadisticas.totalVentas)}</div>
            </div>
            <div class="stat-card">
                <h4>Cantidad de Ventas</h4>
                <div class="valor">${estadisticas.cantidadVentas}</div>
            </div>
            <div class="stat-card">
                <h4>Promedio por Venta</h4>
                <div class="valor">${formatearPrecio(estadisticas.promedioVenta)}</div>
            </div>
        `;
        
        // Tabla
        const tbody = document.querySelector('#tabla-ventas tbody');
        tbody.innerHTML = ventas.map(v => `
            <tr>
                <td>${v.idCotizacion}</td>
                <td>${v.fechaCotizacion || '-'}</td>
                <td>${formatearPrecio(v.precioFinal)}</td>
            </tr>
        `).join('');
    } catch (error) {
        console.error('Error cargando ventas:', error);
    }
}

// ==================== INICIALIZACIÓN ====================

document.addEventListener('DOMContentLoaded', () => {
    mostrarVista('cliente');
});
