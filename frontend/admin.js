const API_URL = '/api';
let muebles = [];
let variantes = [];

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

function mostrarSeccion(seccionId) {
    document.querySelectorAll('.admin-section').forEach(sec => sec.style.display = 'none');
    document.getElementById(seccionId).style.display = 'block';
    
    document.querySelectorAll('.sidebar-btn').forEach(btn => btn.classList.remove('active'));
    document.querySelector(`[data-section="${seccionId}"]`).classList.add('active');
    
    if (seccionId === 'sec-muebles') cargarMuebles();
    if (seccionId === 'sec-variantes') cargarVariantes();
    if (seccionId === 'sec-cotizaciones') cargarCotizaciones();
    if (seccionId === 'sec-ventas') cargarVentas();
}

async function cargarMuebles() {
    try {
        muebles = await fetchAPI('/muebles');
        const tbody = document.querySelector('#tabla-muebles tbody');
        
        if (muebles.length === 0) {
            tbody.innerHTML = '<tr><td colspan="9" class="empty-cell">No hay muebles registrados</td></tr>';
            return;
        }
        
        tbody.innerHTML = muebles.map(m => `
            <tr class="${m.estadoMueble === 'Inactivo' ? 'row-inactive' : ''}">
                <td>${m.idMueble}</td>
                <td>${m.nombreMueble}</td>
                <td>${m.tipoMueble}</td>
                <td>${formatearPrecio(m.precioBase)}</td>
                <td class="${m.stock < 5 ? 'stock-bajo' : ''}">${m.stock}</td>
                <td><span class="status-badge ${m.estadoMueble.toLowerCase()}">${m.estadoMueble}</span></td>
                <td>${m.tamanioMueble}</td>
                <td>${m.materialMueble}</td>
                <td class="acciones-cell">
                    <button class="btn-editar" onclick="editarMueble(${m.idMueble})">Editar</button>
                    ${m.estadoMueble === 'Activo' ? 
                        `<button class="btn-eliminar" onclick="desactivarMueble(${m.idMueble})">Desactivar</button>` : 
                        `<button class="btn-activar" onclick="activarMueble(${m.idMueble})">Activar</button>`}
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
    if (!confirm('¿Desactivar este mueble?')) return;
    
    try {
        await fetchAPI(`/muebles/${id}`, { method: 'DELETE' });
        mostrarMensaje('Mueble desactivado', 'info');
        cargarMuebles();
    } catch (error) {
        console.error('Error desactivando mueble:', error);
    }
}

async function activarMueble(id) {
    try {
        const mueble = await fetchAPI(`/muebles/${id}`);
        mueble.estadoMueble = 'Activo';
        await fetchAPI(`/muebles/${id}`, { method: 'PUT', body: JSON.stringify(mueble) });
        mostrarMensaje('Mueble activado', 'exito');
        cargarMuebles();
    } catch (error) {
        console.error('Error activando mueble:', error);
    }
}

async function cargarVariantes() {
    try {
        variantes = await fetchAPI('/variantes');
        const tbody = document.querySelector('#tabla-variantes tbody');
        
        if (variantes.length === 0) {
            tbody.innerHTML = '<tr><td colspan="5" class="empty-cell">No hay variantes registradas</td></tr>';
            return;
        }
        
        tbody.innerHTML = variantes.map(v => `
            <tr>
                <td>${v.idVariante}</td>
                <td>${v.nombreVariante}</td>
                <td>${formatearPrecio(v.precioAgregado)}</td>
                <td>${v.descripcionVariante || '-'}</td>
                <td class="acciones-cell">
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
            mostrarMensaje('Variante actualizada', 'exito');
        } else {
            await fetchAPI('/variantes', { method: 'POST', body: JSON.stringify(variante) });
            mostrarMensaje('Variante creada', 'exito');
        }
        
        cancelarFormVariante();
        cargarVariantes();
    } catch (error) {
        console.error('Error guardando variante:', error);
    }
}

async function eliminarVariante(id) {
    if (!confirm('¿Eliminar esta variante?')) return;
    
    try {
        await fetchAPI(`/variantes/${id}`, { method: 'DELETE' });
        mostrarMensaje('Variante eliminada', 'info');
        cargarVariantes();
    } catch (error) {
        console.error('Error eliminando variante:', error);
    }
}

async function cargarCotizaciones() {
    try {
        const cotizaciones = await fetchAPI('/cotizaciones');
        const tbody = document.querySelector('#tabla-cotizaciones tbody');
        
        if (cotizaciones.length === 0) {
            tbody.innerHTML = '<tr><td colspan="5" class="empty-cell">No hay cotizaciones</td></tr>';
            return;
        }
        
        tbody.innerHTML = cotizaciones.map(c => `
            <tr>
                <td>${c.idCotizacion}</td>
                <td>${c.fechaCotizacion || '-'}</td>
                <td><span class="status-badge ${c.estadoCotizacion.toLowerCase()}">${c.estadoCotizacion}</span></td>
                <td>${formatearPrecio(c.precioFinal)}</td>
                <td class="acciones-cell">
                    <button class="btn-ver" onclick="verDetalleCotizacion(${c.idCotizacion})">Ver</button>
                    ${c.estadoCotizacion === 'PENDIENTE' ? `
                        <button class="btn-confirmar-sm" onclick="confirmarVentaAdmin(${c.idCotizacion})">Confirmar</button>
                        <button class="btn-eliminar" onclick="cancelarCotizacionAdmin(${c.idCotizacion})">Cancelar</button>
                    ` : ''}
                </td>
            </tr>
        `).join('');
    } catch (error) {
        console.error('Error cargando cotizaciones:', error);
    }
}

async function verDetalleCotizacion(id) {
    try {
        const cotizacion = await fetchAPI(`/cotizaciones/${id}`);
        
        document.getElementById('modal-cotizacion-id').textContent = `#${id}`;
        
        let contenido = `
            <div class="detalle-info">
                <p><strong>Fecha:</strong> ${cotizacion.fechaCotizacion || '-'}</p>
                <p><strong>Estado:</strong> <span class="status-badge ${cotizacion.estadoCotizacion.toLowerCase()}">${cotizacion.estadoCotizacion}</span></p>
                <p><strong>Total:</strong> ${formatearPrecio(cotizacion.precioFinal)}</p>
            </div>
        `;
        
        if (cotizacion.detalles && cotizacion.detalles.length > 0) {
            contenido += `
                <table class="tabla-modal">
                    <thead>
                        <tr>
                            <th>Mueble</th>
                            <th>Variante</th>
                            <th>Cantidad</th>
                            <th>P. Unitario</th>
                            <th>Subtotal</th>
                        </tr>
                    </thead>
                    <tbody>
                        ${cotizacion.detalles.map(d => `
                            <tr>
                                <td>${d.mueble ? d.mueble.nombreMueble : 'N/A'}</td>
                                <td>${d.variante ? d.variante.nombreVariante : 'Normal'}</td>
                                <td>${d.cantidad}</td>
                                <td>${formatearPrecio(d.precioUnitario)}</td>
                                <td>${formatearPrecio(d.subtotal)}</td>
                            </tr>
                        `).join('')}
                    </tbody>
                </table>
            `;
        } else {
            contenido += '<p class="empty-message">No hay detalles en esta cotización</p>';
        }
        
        document.getElementById('modal-detalle-contenido').innerHTML = contenido;
        document.getElementById('modal-detalle-cotizacion').style.display = 'flex';
    } catch (error) {
        console.error('Error cargando detalle:', error);
    }
}

function cerrarModalDetalle() {
    document.getElementById('modal-detalle-cotizacion').style.display = 'none';
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

async function cargarVentas() {
    try {
        const [ventas, estadisticas] = await Promise.all([
            fetchAPI('/ventas'),
            fetchAPI('/ventas/estadisticas/total')
        ]);
        
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
        
        const tbody = document.querySelector('#tabla-ventas tbody');
        
        if (ventas.length === 0) {
            tbody.innerHTML = '<tr><td colspan="4" class="empty-cell">No hay ventas registradas</td></tr>';
            return;
        }
        
        tbody.innerHTML = ventas.map(v => `
            <tr>
                <td>${v.idCotizacion}</td>
                <td>${v.fechaCotizacion || '-'}</td>
                <td>${formatearPrecio(v.precioFinal)}</td>
                <td class="acciones-cell">
                    <button class="btn-ver" onclick="verDetalleVenta(${v.idCotizacion})">Ver Detalle</button>
                </td>
            </tr>
        `).join('');
    } catch (error) {
        console.error('Error cargando ventas:', error);
    }
}

async function verDetalleVenta(id) {
    await verDetalleCotizacion(id);
}

document.getElementById('modal-detalle-cotizacion').addEventListener('click', function(e) {
    if (e.target === this) {
        cerrarModalDetalle();
    }
});

document.addEventListener('DOMContentLoaded', () => {
    cargarMuebles();
});
