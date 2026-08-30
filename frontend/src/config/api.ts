/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
const API_BASE_URL = import.meta.env.VITE_API_URL || (import.meta.env.DEV ? 'http://localhost:8080' : '');

export const API = {
  base: API_BASE_URL,
  
  clientes: {
    getAll: () => `${API_BASE_URL}/api/clientes`,
    getById: (id: number) => `${API_BASE_URL}/api/clientes/${id}`,
    create: () => `${API_BASE_URL}/api/clientes`,
    update: (id: number) => `${API_BASE_URL}/api/clientes/${id}`,
    delete: (id: number) => `${API_BASE_URL}/api/clientes/${id}`,
    byEstado: (estado: string) => `${API_BASE_URL}/api/clientes/estado/${estado}`,
    buscar: (params: string) => `${API_BASE_URL}/api/clientes/buscar?${params}`,
  },
  
  prospectos: {
    getAll: () => `${API_BASE_URL}/api/prospectos`,
    getById: (id: number) => `${API_BASE_URL}/api/prospectos/${id}`,
    create: () => `${API_BASE_URL}/api/prospectos`,
    update: (id: number) => `${API_BASE_URL}/api/prospectos/${id}`,
    delete: (id: number) => `${API_BASE_URL}/api/prospectos/${id}`,
    byEstado: (estado: string) => `${API_BASE_URL}/api/prospectos/estado/${estado}`,
    byPrioridad: (prioridad: string) => `${API_BASE_URL}/api/prospectos/prioridad/${prioridad}`,
    buscar: (params: string) => `${API_BASE_URL}/api/prospectos/buscar?${params}`,
    actualizarEstado: (id: number, estado: string) => `${API_BASE_URL}/api/prospectos/${id}/estado?estado=${estado}`,
  },
  
  ventas: {
    getAll: () => `${API_BASE_URL}/api/ventas`,
    getById: (id: number) => `${API_BASE_URL}/api/ventas/${id}`,
    create: () => `${API_BASE_URL}/api/ventas`,
    update: (id: number) => `${API_BASE_URL}/api/ventas/${id}`,
    delete: (id: number) => `${API_BASE_URL}/api/ventas/${id}`,
    byCliente: (clienteId: number) => `${API_BASE_URL}/api/ventas/cliente/${clienteId}`,
    byEstado: (estado: string) => `${API_BASE_URL}/api/ventas/estado/${estado}`,
    byVendedor: (vendedor: string) => `${API_BASE_URL}/api/ventas/vendedor/${vendedor}`,
    cerrar: (id: number) => `${API_BASE_URL}/api/ventas/${id}/cerrar`,
    totalCerradas: () => `${API_BASE_URL}/api/ventas/total-cerradas`,
  },
  
  cotizaciones: {
    getAll: () => `${API_BASE_URL}/api/cotizaciones`,
    getById: (id: number) => `${API_BASE_URL}/api/cotizaciones/${id}`,
    create: () => `${API_BASE_URL}/api/cotizaciones`,
    update: (id: number) => `${API_BASE_URL}/api/cotizaciones/${id}`,
    delete: (id: number) => `${API_BASE_URL}/api/cotizaciones/${id}`,
    byCliente: (clienteId: number) => `${API_BASE_URL}/api/cotizaciones/cliente/${clienteId}`,
    byEstado: (estado: string) => `${API_BASE_URL}/api/cotizaciones/estado/${estado}`,
    byVendedor: (vendedor: string) => `${API_BASE_URL}/api/cotizaciones/vendedor/${vendedor}`,
    enviar: (id: number) => `${API_BASE_URL}/api/cotizaciones/${id}/enviar`,
    aprobar: (id: number) => `${API_BASE_URL}/api/cotizaciones/${id}/aprobar`,
    expiradas: () => `${API_BASE_URL}/api/cotizaciones/expiradas`,
  },
  
  pedidos: {
    getAll: () => `${API_BASE_URL}/api/pedidos`,
    getById: (id: number) => `${API_BASE_URL}/api/pedidos/${id}`,
    create: () => `${API_BASE_URL}/api/pedidos`,
    update: (id: number) => `${API_BASE_URL}/api/pedidos/${id}`,
    delete: (id: number) => `${API_BASE_URL}/api/pedidos/${id}`,
    byCliente: (clienteId: number) => `${API_BASE_URL}/api/pedidos/cliente/${clienteId}`,
    byEstado: (estado: string) => `${API_BASE_URL}/api/pedidos/estado/${estado}`,
    byVendedor: (vendedor: string) => `${API_BASE_URL}/api/pedidos/vendedor/${vendedor}`,
    procesar: (id: number) => `${API_BASE_URL}/api/pedidos/${id}/procesar`,
    enviar: (id: number) => `${API_BASE_URL}/api/pedidos/${id}/enviar`,
    entregar: (id: number) => `${API_BASE_URL}/api/pedidos/${id}/entregar`,
    atrasados: () => `${API_BASE_URL}/api/pedidos/atrasados`,
  },
  
  servicioCliente: {
    getAll: () => `${API_BASE_URL}/api/servicio-cliente`,
    getById: (id: number) => `${API_BASE_URL}/api/servicio-cliente/${id}`,
    create: () => `${API_BASE_URL}/api/servicio-cliente`,
    update: (id: number) => `${API_BASE_URL}/api/servicio-cliente/${id}`,
    delete: (id: number) => `${API_BASE_URL}/api/servicio-cliente/${id}`,
    byCliente: (clienteId: number) => `${API_BASE_URL}/api/servicio-cliente/cliente/${clienteId}`,
    byEstado: (estado: string) => `${API_BASE_URL}/api/servicio-cliente/estado/${estado}`,
    byTipo: (tipo: string) => `${API_BASE_URL}/api/servicio-cliente/tipo/${tipo}`,
    byPrioridad: (prioridad: string) => `${API_BASE_URL}/api/servicio-cliente/prioridad/${prioridad}`,
    byAsignado: (asignadoA: string) => `${API_BASE_URL}/api/servicio-cliente/asignado/${asignadoA}`,
    asignar: (id: number, asignadoA: string) => `${API_BASE_URL}/api/servicio-cliente/${id}/asignar?asignadoA=${asignadoA}`,
    resolver: (id: number, resolucion: string) => `${API_BASE_URL}/api/servicio-cliente/${id}/resolver?resolucion=${resolucion}`,
    cerrar: (id: number) => `${API_BASE_URL}/api/servicio-cliente/${id}/cerrar`,
    urgentes: () => `${API_BASE_URL}/api/servicio-cliente/urgentes`,
  },
};
