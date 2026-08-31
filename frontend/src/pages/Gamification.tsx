/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
import { useState, useEffect } from 'react';
import { 
  Trophy, Plus, Trash2, Star, Award, Target, Zap, Crown, Medal, Flame
} from 'lucide-react';
import apiClient from '../services/api';

interface Badge {
  id?: number;
  name: string;
  description: string;
  icon: string;
  color: string;
  criteria: string;
  pointsRequired?: number;
  isActive: boolean;
}

const ICON_MAP: Record<string, React.ElementType> = {
  Trophy, Star, Award, Target, Zap, Crown, Medal, Flame,
};

const COLOR_OPTIONS = [
  { value: 'bg-yellow-500', label: 'Oro' },
  { value: 'bg-gray-400', label: 'Plata' },
  { value: 'bg-orange-600', label: 'Bronce' },
  { value: 'bg-purple-500', label: 'Púrpura' },
  { value: 'bg-blue-500', label: 'Azul' },
  { value: 'bg-green-500', label: 'Verde' },
  { value: 'bg-red-500', label: 'Rojo' },
];

export default function Gamification() {
  const [badges, setBadges] = useState<Badge[]>([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [formData, setFormData] = useState<Badge>({
    name: '', description: '', icon: 'Trophy', color: 'bg-yellow-500', criteria: '', pointsRequired: 100, isActive: true,
  });

  const loadData = async () => {
    setLoading(true);
    try {
      const res = await apiClient.get('/api/gamification');
      setBadges(res.data);
    } catch (e) {
      console.error('Error loading badges:', e);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { loadData(); }, []);

  const handleSubmit = async () => {
    try {
      await apiClient.post('/api/gamification', formData);
      setShowModal(false);
      loadData();
      setFormData({ name: '', description: '', icon: 'Trophy', color: 'bg-yellow-500', criteria: '', pointsRequired: 100, isActive: true });
    } catch (e) {
      console.error('Error saving badge:', e);
    }
  };

  const handleDelete = async (id: number) => {
    if (window.confirm('¿Eliminar esta insignia?')) {
      try {
        await apiClient.delete(`/api/gamification/${id}`);
        loadData();
      } catch (e) {
        console.error('Error deleting badge:', e);
      }
    }
  };

  return (
    <div className="p-6">
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-2xl font-bold text-slate-900 flex items-center gap-2">
            <Trophy className="w-7 h-7 text-yellow-500" />
            Gamificación
          </h1>
          <p className="text-sm text-slate-500 mt-1">Insignias y recompensas para motivar equipos de ventas</p>
        </div>
        <button onClick={() => setShowModal(true)} className="flex items-center gap-2 px-4 py-2 text-sm font-medium text-white bg-blue-600 rounded-lg hover:bg-blue-700">
          <Plus className="w-4 h-4" /> Nueva Insignia
        </button>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        {badges.map((b) => {
          const Icon = ICON_MAP[b.icon] || Trophy;
          return (
            <div key={b.id} className="bg-white rounded-xl border border-slate-200 p-5 text-center hover:shadow-md transition-all">
              <div className={`inline-flex p-4 rounded-full ${b.color} mb-3 shadow-lg`}>
                <Icon className="w-8 h-8 text-white" />
              </div>
              <h3 className="font-bold text-sm text-slate-900">{b.name}</h3>
              <p className="text-xs text-slate-500 mt-1 mb-2">{b.description}</p>
              <div className="text-xs text-slate-400 mb-3">
                <p className="font-medium text-slate-600">{b.pointsRequired} pts</p>
                <p className="mt-1">{b.criteria}</p>
              </div>
              <button onClick={() => handleDelete(b.id!)} className="p-1.5 text-slate-400 hover:text-red-600 hover:bg-red-50 rounded mx-auto">
                <Trash2 className="w-3.5 h-3.5" />
              </button>
            </div>
          );
        })}
      </div>

      {badges.length === 0 && !loading && (
        <div className="text-center py-16 text-slate-400">
          <Trophy className="w-12 h-12 mx-auto mb-3" />
          <p className="text-lg font-medium">No hay insignias configuradas</p>
          <p className="text-sm">Crea insignias para motivar a tu equipo</p>
        </div>
      )}
      {loading && <div className="text-center py-12 text-slate-400">Cargando...</div>}

      {showModal && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-xl shadow-xl max-w-md w-full p-6">
            <h2 className="text-lg font-bold text-slate-900 mb-4">Nueva Insignia</h2>
            <div className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-1">Nombre</label>
                <input type="text" className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm" value={formData.name} onChange={e => setFormData({ ...formData, name: e.target.value })} placeholder="Ej: Vendedor del Mes" />
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-1">Descripción</label>
                <input type="text" className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm" value={formData.description} onChange={e => setFormData({ ...formData, description: e.target.value })} placeholder="Ej: Por cerrar 20+ ventas en un mes" />
              </div>
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">Icono</label>
                  <select className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm" value={formData.icon} onChange={e => setFormData({ ...formData, icon: e.target.value })}>
                    {Object.keys(ICON_MAP).map(k => <option key={k} value={k}>{k}</option>)}
                  </select>
                </div>
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">Color</label>
                  <select className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm" value={formData.color} onChange={e => setFormData({ ...formData, color: e.target.value })}>
                    {COLOR_OPTIONS.map(c => <option key={c.value} value={c.value}>{c.label}</option>)}
                  </select>
                </div>
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-1">Puntos requeridos</label>
                <input type="number" className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm" value={formData.pointsRequired} onChange={e => setFormData({ ...formData, pointsRequired: Number(e.target.value) })} />
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-1">Criterio</label>
                <input type="text" className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm" value={formData.criteria} onChange={e => setFormData({ ...formData, criteria: e.target.value })} placeholder="Ej: Cerrar 20 ventas en 30 días" />
              </div>
            </div>
            <div className="flex justify-end gap-3 mt-6">
              <button onClick={() => setShowModal(false)} className="px-4 py-2 text-sm font-medium text-slate-600 border border-slate-200 rounded-lg hover:bg-slate-50">Cancelar</button>
              <button onClick={handleSubmit} className="px-4 py-2 text-sm font-medium text-white bg-blue-600 rounded-lg hover:bg-blue-700">Crear Insignia</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
