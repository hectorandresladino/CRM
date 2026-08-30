import { useState, useEffect } from 'react';
import { 
  Target, Plus, Award, Trash2
} from 'lucide-react';
import apiClient from '../services/api';

interface Meta {
  id?: number;
  vendedor?: string;
  equipo?: string;
  periodo: string;
  anio: number;
  trimestre?: number;
  mes?: number;
  montoObjetivo: number;
  montoAlcanzado?: number;
  numeroVentasObjetivo?: number;
  numeroVentasReal?: number;
  porcentajeCumplimiento?: number;
}

export default function MetasComerciales() {
  const [metas, setMetas] = useState<Meta[]>([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [formData, setFormData] = useState<Meta>({
    periodo: 'MENSUAL', anio: new Date().getFullYear(), montoObjetivo: 0,
  });

  const loadData = async () => {
    setLoading(true);
    try {
      const res = await apiClient.get('/api/metas');
      setMetas(res.data);
    } catch (e) { console.error(e); } finally { setLoading(false); }
  };

  useEffect(() => { loadData(); }, []);

  const handleSubmit = async () => {
    try {
      await apiClient.post('/api/metas', formData);
      setShowModal(false);
      loadData();
      setFormData({ periodo: 'MENSUAL', anio: new Date().getFullYear(), montoObjetivo: 0 });
    } catch (e) { console.error(e); }
  };

  const handleDelete = async (id: number) => {
    if (window.confirm('¿Eliminar esta meta?')) {
      try { await apiClient.delete(`/api/metas/${id}`); loadData(); } catch (e) { console.error(e); }
    }
  };

  const fmt = (v: number) => new Intl.NumberFormat('es-CO', { style: 'currency', currency: 'USD', minimumFractionDigits: 0 }).format(v || 0);

  const sorted = [...metas].sort((a, b) => (b.porcentajeCumplimiento || 0) - (a.porcentajeCumplimiento || 0));

  return (
    <div className="p-6 space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-slate-900 flex items-center gap-2">
            <Target className="w-7 h-7 text-orange-600" />
            Metas y Cuotas Comerciales
          </h1>
          <p className="text-sm text-slate-500 mt-1">Objetivos por vendedor, equipo y periodo con ranking</p>
        </div>
        <button onClick={() => setShowModal(true)} className="flex items-center gap-2 px-4 py-2 text-sm font-medium text-white bg-blue-600 rounded-lg hover:bg-blue-700">
          <Plus className="w-4 h-4" /> Nueva Meta
        </button>
      </div>

      {/* Ranking */}
      <div className="bg-white rounded-xl border border-slate-200 p-6">
        <h2 className="text-lg font-semibold text-slate-900 mb-4 flex items-center gap-2">
          <Award className="w-5 h-5 text-yellow-500" /> Ranking de Cumplimiento
        </h2>
        <div className="space-y-3">
          {sorted.map((m, i) => {
            const pct = m.porcentajeCumplimiento || 0;
            const rankColor = i === 0 ? 'bg-yellow-100 text-yellow-700' : i === 1 ? 'bg-slate-100 text-slate-600' : i === 2 ? 'bg-orange-100 text-orange-700' : 'bg-slate-50 text-slate-500';
            return (
              <div key={m.id} className="flex items-center gap-4">
                <div className={`w-8 h-8 rounded-full flex items-center justify-center text-sm font-bold ${rankColor}`}>{i + 1}</div>
                <div className="flex-1">
                  <div className="flex justify-between text-sm mb-1">
                    <span className="font-medium text-slate-900">{m.vendedor || m.equipo || 'General'}</span>
                    <span className="text-slate-600">{fmt(m.montoAlcanzado || 0)} / {fmt(m.montoObjetivo)}</span>
                  </div>
                  <div className="bg-slate-100 rounded h-2 relative">
                    <div className={`h-2 rounded transition-all ${pct >= 100 ? 'bg-green-500' : pct >= 50 ? 'bg-blue-500' : 'bg-orange-500'}`} style={{ width: `${Math.min(pct, 100)}%` }} />
                  </div>
                </div>
                <div className="w-16 text-right">
                  <span className={`text-sm font-bold ${pct >= 100 ? 'text-green-600' : pct >= 50 ? 'text-blue-600' : 'text-orange-600'}`}>{pct.toFixed(0)}%</span>
                </div>
                <button onClick={() => handleDelete(m.id!)} className="p-1 text-red-600 hover:bg-red-50 rounded"><Trash2 className="w-3.5 h-3.5" /></button>
              </div>
            );
          })}
          {sorted.length === 0 && !loading && <div className="text-center py-8 text-slate-400">No hay metas configuradas</div>}
        </div>
      </div>

      {showModal && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-xl shadow-xl max-w-md w-full p-6">
            <h2 className="text-lg font-bold text-slate-900 mb-4">Nueva Meta Comercial</h2>
            <div className="space-y-4">
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">Periodo</label>
                  <select className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm" value={formData.periodo} onChange={e => setFormData({ ...formData, periodo: e.target.value })}>
                    <option value="MENSUAL">Mensual</option>
                    <option value="TRIMESTRAL">Trimestral</option>
                    <option value="ANUAL">Anual</option>
                  </select>
                </div>
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">Año</label>
                  <input type="number" className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm" value={formData.anio} onChange={e => setFormData({ ...formData, anio: Number(e.target.value) })} />
                </div>
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-1">Vendedor / Equipo</label>
                <input type="text" className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm" value={formData.vendedor || ''} onChange={e => setFormData({ ...formData, vendedor: e.target.value })} placeholder="Nombre del vendedor o equipo" />
              </div>
              {formData.periodo === 'MENSUAL' && (
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">Mes (1-12)</label>
                  <input type="number" min={1} max={12} className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm" value={formData.mes || ''} onChange={e => setFormData({ ...formData, mes: Number(e.target.value) })} />
                </div>
              )}
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-1">Monto objetivo (USD)</label>
                <input type="number" className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm" value={formData.montoObjetivo} onChange={e => setFormData({ ...formData, montoObjetivo: Number(e.target.value) })} />
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-1">N° ventas objetivo</label>
                <input type="number" className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm" value={formData.numeroVentasObjetivo || ''} onChange={e => setFormData({ ...formData, numeroVentasObjetivo: Number(e.target.value) })} />
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
