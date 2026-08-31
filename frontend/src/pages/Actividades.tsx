/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
import { useState, useEffect, useCallback } from 'react';
import { 
  Calendar, Plus, CheckCircle, Clock, Phone, Users, Mail,
  MapPin, Trash2, Filter, ListTodo
} from 'lucide-react';
import apiClient from '../services/api';

interface Actividad {
  id?: number;
  tipo: string;
  titulo: string;
  descripcion?: string;
  clienteId?: number;
  prospectoId?: number;
  asignadoA?: string;
  fechaProgramada?: string;
  fechaCompletada?: string;
  duracionMinutos?: number;
  estado: string;
  prioridad: string;
  resultado?: string;
  ubicacion?: string;
}

const TIPO_CONFIG: Record<string, { icon: React.ElementType; color: string; bg: string }> = {
  TAREA: { icon: ListTodo, color: 'text-blue-600', bg: 'bg-blue-100' },
  LLAMADA: { icon: Phone, color: 'text-green-600', bg: 'bg-green-100' },
  REUNION: { icon: Users, color: 'text-purple-600', bg: 'bg-purple-100' },
  EMAIL: { icon: Mail, color: 'text-cyan-600', bg: 'bg-cyan-100' },
  VISITA: { icon: MapPin, color: 'text-orange-600', bg: 'bg-orange-100' },
  NOTA: { icon: Calendar, color: 'text-slate-600', bg: 'bg-slate-100' },
};

const ESTADO_CONFIG: Record<string, { color: string; bg: string; label: string }> = {
  PENDIENTE: { color: 'text-yellow-700', bg: 'bg-yellow-100', label: 'Pendiente' },
  EN_PROGRESO: { color: 'text-blue-700', bg: 'bg-blue-100', label: 'En progreso' },
  COMPLETADA: { color: 'text-green-700', bg: 'bg-green-100', label: 'Completada' },
  CANCELADA: { color: 'text-red-700', bg: 'bg-red-100', label: 'Cancelada' },
  POSTPUESTA: { color: 'text-slate-600', bg: 'bg-slate-100', label: 'Postpuesta' },
};

export default function Actividades() {
  const [actividades, setActividades] = useState<Actividad[]>([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [filterEstado, setFilterEstado] = useState('ALL');
  const [formData, setFormData] = useState<Actividad>({
    tipo: 'TAREA', titulo: '', estado: 'PENDIENTE', prioridad: 'MEDIA',
  });

  const loadData = useCallback(async () => {
    setLoading(true);
    try {
      const url = filterEstado !== 'ALL' ? `/api/actividades/estado/${filterEstado}` : '/api/actividades';
      const res = await apiClient.get(url);
      setActividades(res.data);
    } catch (e) {
      console.error('Error loading actividades:', e);
    } finally {
      setLoading(false);
    }
  }, [filterEstado]);

  useEffect(() => { loadData(); }, [loadData]);

  const handleSubmit = async () => {
    try {
      await apiClient.post('/api/actividades', formData);
      setShowModal(false);
      loadData();
      setFormData({ tipo: 'TAREA', titulo: '', estado: 'PENDIENTE', prioridad: 'MEDIA' });
    } catch (e) { console.error(e); }
  };

  const handleComplete = async (id: number) => {
    try { await apiClient.patch(`/api/actividades/${id}/completar`); loadData(); } catch (e) { console.error(e); }
  };

  const handleDelete = async (id: number) => {
    if (window.confirm('¿Eliminar esta actividad?')) {
      try { await apiClient.delete(`/api/actividades/${id}`); loadData(); } catch (e) { console.error(e); }
    }
  };

  const pendientes = actividades.filter(a => a.estado === 'PENDIENTE').length;
  const completadas = actividades.filter(a => a.estado === 'COMPLETADA').length;

  return (
    <div className="p-6 space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-slate-900 flex items-center gap-2">
            <Calendar className="w-7 h-7 text-blue-600" />
            Actividades Comerciales
          </h1>
          <p className="text-sm text-slate-500 mt-1">Tareas, llamadas, reuniones, visitas y follow-ups</p>
        </div>
        <button onClick={() => setShowModal(true)} className="flex items-center gap-2 px-4 py-2 text-sm font-medium text-white bg-blue-600 rounded-lg hover:bg-blue-700">
          <Plus className="w-4 h-4" /> Nueva Actividad
        </button>
      </div>

      <div className="grid grid-cols-3 gap-4">
        <div className="bg-white rounded-xl border border-slate-200 p-4">
          <div className="flex items-center gap-3">
            <div className="bg-yellow-50 p-2.5 rounded-lg"><Clock className="w-5 h-5 text-yellow-600" /></div>
            <div><p className="text-xl font-bold text-slate-900">{pendientes}</p><p className="text-xs text-slate-500">Pendientes</p></div>
          </div>
        </div>
        <div className="bg-white rounded-xl border border-slate-200 p-4">
          <div className="flex items-center gap-3">
            <div className="bg-green-50 p-2.5 rounded-lg"><CheckCircle className="w-5 h-5 text-green-600" /></div>
            <div><p className="text-xl font-bold text-slate-900">{completadas}</p><p className="text-xs text-slate-500">Completadas</p></div>
          </div>
        </div>
        <div className="bg-white rounded-xl border border-slate-200 p-4">
          <div className="flex items-center gap-3">
            <div className="bg-blue-50 p-2.5 rounded-lg"><ListTodo className="w-5 h-5 text-blue-600" /></div>
            <div><p className="text-xl font-bold text-slate-900">{actividades.length}</p><p className="text-xs text-slate-500">Total</p></div>
          </div>
        </div>
      </div>

      <div className="flex items-center gap-2">
        <Filter className="w-4 h-4 text-slate-400" />
        <select value={filterEstado} onChange={e => setFilterEstado(e.target.value)} className="px-3 py-1.5 text-sm border border-slate-200 rounded-lg">
          <option value="ALL">Todos</option>
          <option value="PENDIENTE">Pendientes</option>
          <option value="EN_PROGRESO">En progreso</option>
          <option value="COMPLETADA">Completadas</option>
          <option value="CANCELADA">Canceladas</option>
        </select>
      </div>

      <div className="space-y-2">
        {actividades.map((a) => {
          const tc = TIPO_CONFIG[a.tipo] || TIPO_CONFIG.TAREA;
          const ec = ESTADO_CONFIG[a.estado] || ESTADO_CONFIG.PENDIENTE;
          const TipoIcon = tc.icon;
          return (
            <div key={a.id} className="bg-white rounded-xl border border-slate-200 p-4 flex items-center gap-4 hover:shadow-sm transition-shadow">
              <div className={`p-2.5 rounded-lg ${tc.bg} flex-shrink-0`}>
                <TipoIcon className={`w-5 h-5 ${tc.color}`} />
              </div>
              <div className="flex-1 min-w-0">
                <div className="flex items-center gap-2">
                  <p className="text-sm font-medium text-slate-900 truncate">{a.titulo}</p>
                  <span className={`px-2 py-0.5 rounded-full text-xs font-medium ${ec.bg} ${ec.color}`}>{ec.label}</span>
                  <span className={`px-2 py-0.5 rounded-full text-xs font-medium ${a.prioridad === 'ALTA' ? 'bg-red-100 text-red-700' : a.prioridad === 'MEDIA' ? 'bg-yellow-100 text-yellow-700' : 'bg-slate-100 text-slate-600'}`}>{a.prioridad}</span>
                </div>
                {a.descripcion && <p className="text-xs text-slate-500 mt-1 truncate">{a.descripcion}</p>}
                <div className="flex items-center gap-3 mt-1 text-xs text-slate-400">
                  {a.asignadoA && <span>{a.asignadoA}</span>}
                  {a.fechaProgramada && <span>{new Date(a.fechaProgramada).toLocaleString('es-CO', { day: '2-digit', month: 'short', hour: '2-digit', minute: '2-digit' })}</span>}
                  {a.ubicacion && <span className="flex items-center gap-1"><MapPin className="w-3 h-3" />{a.ubicacion}</span>}
                </div>
              </div>
              <div className="flex gap-1 flex-shrink-0">
                {a.estado !== 'COMPLETADA' && (
                  <button onClick={() => handleComplete(a.id!)} className="p-1.5 text-green-600 hover:bg-green-50 rounded" title="Completar">
                    <CheckCircle className="w-4 h-4" />
                  </button>
                )}
                <button onClick={() => handleDelete(a.id!)} className="p-1.5 text-red-600 hover:bg-red-50 rounded" title="Eliminar">
                  <Trash2 className="w-4 h-4" />
                </button>
              </div>
            </div>
          );
        })}
        {actividades.length === 0 && !loading && (
          <div className="text-center py-12 text-slate-400">
            <Calendar className="w-12 h-12 mx-auto mb-3" />
            <p>No hay actividades registradas</p>
          </div>
        )}
        {loading && <div className="text-center py-12 text-slate-400">Cargando...</div>}
      </div>

      {showModal && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-xl shadow-xl max-w-md w-full p-6">
            <h2 className="text-lg font-bold text-slate-900 mb-4">Nueva Actividad</h2>
            <div className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-1">Tipo</label>
                <select className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm" value={formData.tipo} onChange={e => setFormData({ ...formData, tipo: e.target.value })}>
                  <option value="TAREA">Tarea</option>
                  <option value="LLAMADA">Llamada</option>
                  <option value="REUNION">Reunión</option>
                  <option value="EMAIL">Email</option>
                  <option value="VISITA">Visita</option>
                  <option value="NOTA">Nota</option>
                </select>
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-1">Título</label>
                <input type="text" className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm" value={formData.titulo} onChange={e => setFormData({ ...formData, titulo: e.target.value })} />
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-1">Descripción</label>
                <textarea className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm" rows={2} value={formData.descripcion || ''} onChange={e => setFormData({ ...formData, descripcion: e.target.value })} />
              </div>
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">Prioridad</label>
                  <select className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm" value={formData.prioridad} onChange={e => setFormData({ ...formData, prioridad: e.target.value })}>
                    <option value="ALTA">Alta</option>
                    <option value="MEDIA">Media</option>
                    <option value="BAJA">Baja</option>
                  </select>
                </div>
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">Fecha programada</label>
                  <input type="datetime-local" className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm" value={formData.fechaProgramada?.substring(0, 16) || ''} onChange={e => setFormData({ ...formData, fechaProgramada: e.target.value })} />
                </div>
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-1">Asignado a</label>
                <input type="text" className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm" value={formData.asignadoA || ''} onChange={e => setFormData({ ...formData, asignadoA: e.target.value })} />
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-1">Ubicación</label>
                <input type="text" className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm" value={formData.ubicacion || ''} onChange={e => setFormData({ ...formData, ubicacion: e.target.value })} />
              </div>
            </div>
            <div className="flex justify-end gap-3 mt-6">
              <button onClick={() => setShowModal(false)} className="px-4 py-2 text-sm font-medium text-slate-600 border border-slate-200 rounded-lg hover:bg-slate-50">Cancelar</button>
              <button onClick={handleSubmit} className="px-4 py-2 text-sm font-medium text-white bg-blue-600 rounded-lg hover:bg-blue-700">Guardar</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
