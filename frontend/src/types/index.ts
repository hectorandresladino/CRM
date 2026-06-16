export interface Usuario {
  id?: number;
  username: string;
  password: string;
  email?: string;
  nombre?: string;
  rol?: string;
  activo?: boolean;
  createdAt?: string;
  updatedAt?: string;
}

export interface Cliente {
  id?: number;
  nombre: string;
  apellido: string;
  email?: string;
  telefono?: string;
  celular?: string;
  direccion?: string;
  ciudad?: string;
  pais?: string;
  codigoPostal?: string;
  identificacion?: string;
  tipoIdentificacion?: string;
  empresa?: string;
  cargo?: string;
  sector?: string;
  notas?: string;
  estado: 'ACTIVO' | 'INACTIVO' | 'BLOQUEADO';
  fechaCreacion?: string;
  fechaActualizacion?: string;
}

export interface Prospecto {
  id?: number;
  nombre: string;
  apellido: string;
  email?: string;
  telefono?: string;
  celular?: string;
  empresa?: string;
  cargo?: string;
  sector?: string;
  origen?: string;
  interes?: string;
  notas?: string;
  estado: 'NUEVO' | 'CONTACTADO' | 'CALIFICADO' | 'PROPUESTA' | 'NEGOCIACION' | 'CERRADO' | 'PERDIDO';
  prioridad: 'BAJA' | 'MEDIA' | 'ALTA' | 'URGENTE';
  fechaCreacion?: string;
  fechaActualizacion?: string;
  fechaContacto?: string;
  fechaConversion?: string;
  clienteId?: number;
}

export interface Venta {
  id?: number;
  cliente?: Cliente;
  clienteId?: number;
  codigo: string;
  descripcion: string;
  monto: number;
  descuento?: number;
  impuesto?: number;
  total?: number;
  comision?: number;
  vendedor?: string;
  notas?: string;
  estado: 'PENDIENTE' | 'EN_PROCESO' | 'CERRADA' | 'CANCELADA';
  metodoPago: 'EFECTIVO' | 'TARJETA' | 'TRANSFERENCIA' | 'CHEQUE';
  fechaCreacion?: string;
  fechaActualizacion?: string;
  fechaCierre?: string;
  cotizacionId?: number;
}

export interface Cotizacion {
  id?: number;
  cliente?: Cliente;
  clienteId?: number;
  codigo: string;
  descripcion: string;
  subtotal: number;
  descuento?: number;
  impuesto?: number;
  total?: number;
  margen?: number;
  vendedor?: string;
  terminos?: string;
  notas?: string;
  validez: string;
  estado: 'BORRADOR' | 'ENVIADA' | 'APROBADA' | 'RECHAZADA' | 'EXPIRADA';
  fechaCreacion?: string;
  fechaActualizacion?: string;
  fechaEnvio?: string;
  fechaAprobacion?: string;
  ventaId?: number;
}

export interface Pedido {
  id?: number;
  cliente?: Cliente;
  clienteId?: number;
  codigo: string;
  descripcion: string;
  subtotal: number;
  descuento?: number;
  impuesto?: number;
  total?: number;
  costoEnvio?: number;
  direccionEnvio?: string;
  ciudadEnvio?: string;
  paisEnvio?: string;
  codigoPostalEnvio?: string;
  fechaEntregaEstimada: string;
  fechaEntregaReal?: string;
  vendedor?: string;
  notas?: string;
  notasEnvio?: string;
  estado: 'PENDIENTE' | 'PROCESANDO' | 'ENVIADO' | 'ENTREGADO' | 'CANCELADO';
  metodoEnvio: 'ESTANDAR' | 'EXPRESS' | 'RECOGIDA';
  fechaCreacion?: string;
  fechaActualizacion?: string;
  fechaProcesamiento?: string;
  fechaEnvio?: string;
  ventaId?: number;
  cotizacionId?: number;
}

export interface ServicioCliente {
  id?: number;
  cliente?: Cliente;
  clienteId?: number;
  codigo: string;
  asunto: string;
  descripcion: string;
  tipo: 'PREGUNTA' | 'QUEJA' | 'RECLAMO' | 'SUGERENCIA' | 'FELICITACION';
  prioridad: 'BAJA' | 'MEDIA' | 'ALTA' | 'URGENTE' | 'CRITICA';
  canal: 'EMAIL' | 'TELEFONO' | 'CHAT' | 'WHATSAPP' | 'RED_SOCIAL' | 'PRESENCIAL';
  estado: 'ABIERTO' | 'ASIGNADO' | 'EN_PROCESO' | 'ESPERA_RESPUESTA' | 'RESUELTO' | 'CERRADO';
  asignadoA?: string;
  resolucion?: string;
  notas?: string;
  fechaCreacion?: string;
  fechaActualizacion?: string;
  fechaAsignacion?: string;
  fechaCierre?: string;
  fechaRespuesta?: string;
}

export interface CampanaMarketing {
  id?: number;
  nombre: string;
  descripcion?: string;
  tipo: string;
  estado: string;
  fechaInicio?: string;
  fechaFin?: string;
  presupuesto?: number;
  presupuestoGastado?: number;
  objetivo?: string;
  segmento?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface EmailMarketing {
  id?: number;
  asunto: string;
  contenido: string;
  estado: string;
  tipo: string;
  fechaEnvio?: string;
  fechaProgramada?: string;
  remitente?: string;
  listaDestinatarios?: string;
  totalEnviados?: number;
  totalAbiertos?: number;
  totalClicks?: number;
  tasaApertura?: number;
  tasaClick?: number;
  createdAt?: string;
  updatedAt?: string;
}

export interface WhatsAppBusiness {
  id?: number;
  telefono: string;
  mensaje: string;
  estado: string;
  tipo: string;
  fechaEnvio?: string;
  fechaProgramada?: string;
  plantilla?: string;
  media?: string;
  leido?: boolean;
  respondido?: boolean;
  createdAt?: string;
  updatedAt?: string;
}

export interface GestionDocumental {
  id?: number;
  nombre: string;
  descripcion?: string;
  tipo: string;
  categoria: string;
  estado: string;
  urlArchivo?: string;
  tamanoKb?: number;
  extension?: string;
  clienteId?: number;
  etiquetas?: string;
  fechaSubida?: string;
  fechaVencimiento?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface Contrato {
  id?: number;
  codigo: string;
  nombre: string;
  descripcion?: string;
  tipo: string;
  estado: string;
  clienteId?: number;
  fechaInicio?: string;
  fechaFin?: string;
  valor?: number;
  moneda?: string;
  periodoRenovacion?: string;
  urlDocumento?: string;
  condiciones?: string;
  observaciones?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface Factura {
  id?: number;
  numero: string;
  descripcion?: string;
  tipo: string;
  estado: string;
  clienteId?: number;
  ventaId?: number;
  fechaEmision?: string;
  fechaVencimiento?: string;
  fechaPago?: string;
  subtotal?: number;
  impuesto?: number;
  total?: number;
  moneda?: string;
  metodoPago?: string;
  urlFactura?: string;
  notas?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface PQRS {
  id?: number;
  codigo: string;
  asunto: string;
  descripcion: string;
  tipo: string;
  prioridad: string;
  estado: string;
  clienteId?: number;
  canal: string;
  asignadoA?: string;
  resolucion?: string;
  notas?: string;
  fechaCreacion?: string;
  fechaResolucion?: string;
  tiempoRespuestaHoras?: number;
  createdAt?: string;
  updatedAt?: string;
}

export interface EncuestaSatisfaccion {
  id?: number;
  nombre: string;
  descripcion?: string;
  tipo: string;
  estado: string;
  clienteId?: number;
  fechaEnvio?: string;
  fechaRespuesta?: string;
  calificacionGeneral?: number;
  comentarios?: string;
  pregunta1?: number;
  pregunta2?: number;
  pregunta3?: number;
  pregunta4?: number;
  pregunta5?: number;
  recomendaria?: boolean;
  createdAt?: string;
  updatedAt?: string;
}

export interface MesaAyuda {
  id?: number;
  ticket: string;
  asunto: string;
  descripcion: string;
  categoria: string;
  prioridad: string;
  estado: string;
  clienteId?: number;
  canal: string;
  asignadoA?: string;
  solucion?: string;
  notas?: string;
  fechaCreacion?: string;
  fechaCierre?: string;
  tiempoResolucionMinutos?: number;
  satisfaccionCliente?: number;
  createdAt?: string;
  updatedAt?: string;
}
