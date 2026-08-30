import { useState, useEffect } from 'react';
import { 
  Shield, ShieldCheck, ShieldAlert, Download, Trash2, FileText, 
  CheckCircle, XCircle, Plus
} from 'lucide-react';
import apiClient from '../services/api';

interface GdprConsent {
  id?: number;
  clienteId?: number;
  prospectoId?: number;
  dataType: string;
  purpose: string;
  granted: boolean;
  consentText?: string;
  version?: string;
  ipAddress?: string;
  withdrawnAt?: string;
  grantedAt?: string;
}

export default function GdprCompliance() {
  const [consents, setConsents] = useState<GdprConsent[]>([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [formData, setFormData] = useState<GdprConsent>({
    dataType: 'PERSONAL_DATA',
    purpose: '',
    granted: true,
    consentText: '',
  });

  const loadData = async () => {
    setLoading(true);
    try {
      const res = await apiClient.get('/api/gdpr');
      setConsents(res.data);
    } catch (e) {
      console.error('Error loading consents:', e);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { loadData(); }, []);

  const handleGrant = async () => {
    try {
      await apiClient.post('/api/gdpr', formData);
      setShowModal(false);
      loadData();
      setFormData({ dataType: 'PERSONAL_DATA', purpose: '', granted: true, consentText: '' });
    } catch (e) {
      console.error('Error granting consent:', e);
    }
  };

  const handleWithdraw = async (id: number) => {
    if (window.confirm('¿Retirar este consentimiento?')) {
      try {
        await apiClient.patch(`/api/gdpr/${id}/withdraw`);
        loadData();
      } catch (e) {
        console.error('Error withdrawing consent:', e);
      }
    }
  };

  const handlePurge = async (clienteId: number) => {
    if (window.confirm('¿Eliminar TODOS los datos de este cliente? Esta acción es irreversible (Derecho al Olvido GDPR).')) {
      try {
        await apiClient.delete(`/api/gdpr/cliente/${clienteId}/purge`);
        loadData();
      } catch (e) {
        console.error('Error purging data:', e);
      }
    }
  };

  const handleExport = () => {
    const csv = [
      ['ID', 'Data Type', 'Purpose', 'Granted', 'Date', 'Withdrawn'],
      ...consents.map(c => [c.id, c.dataType, c.purpose, c.granted, c.grantedAt, c.withdrawnAt])
    ].map(row => row.join(',')).join('\n');
    const blob = new Blob([csv], { type: 'text/csv' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = 'gdpr_consents.csv';
    a.click();
  };

  const activeCount = consents.filter(c => c.granted).length;
  const withdrawnCount = consents.filter(c => !c.granted).length;

  return (
    <div className="p-6">
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-2xl font-bold text-slate-900 flex items-center gap-2">
            <Shield className="w-7 h-7 text-blue-600" />
            GDPR Compliance
          </h1>
          <p className="text-sm text-slate-500 mt-1">Gestión de consentimientos y derechos de datos (GDPR/PIPEDA)</p>
        </div>
        <div className="flex gap-3">
          <button onClick={handleExport} className="flex items-center gap-2 px-4 py-2 text-sm font-medium text-slate-600 bg-white border border-slate-200 rounded-lg hover:bg-slate-50">
            <Download className="w-4 h-4" /> Exportar
          </button>
          <button onClick={() => setShowModal(true)} className="flex items-center gap-2 px-4 py-2 text-sm font-medium text-white bg-blue-600 rounded-lg hover:bg-blue-700">
            <Plus className="w-4 h-4" /> Nuevo Consentimiento
          </button>
        </div>
      </div>

      {/* Stats */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
        <div className="bg-white rounded-xl border border-slate-200 p-5">
          <div className="flex items-center gap-3">
            <div className="bg-green-50 p-3 rounded-lg"><ShieldCheck className="w-6 h-6 text-green-600" /></div>
            <div><p className="text-2xl font-bold text-slate-900">{activeCount}</p><p className="text-xs text-slate-500">Consentimientos activos</p></div>
          </div>
        </div>
        <div className="bg-white rounded-xl border border-slate-200 p-5">
          <div className="flex items-center gap-3">
            <div className="bg-red-50 p-3 rounded-lg"><ShieldAlert className="w-6 h-6 text-red-600" /></div>
            <div><p className="text-2xl font-bold text-slate-900">{withdrawnCount}</p><p className="text-xs text-slate-500">Retirados</p></div>
          </div>
        </div>
        <div className="bg-white rounded-xl border border-slate-200 p-5">
          <div className="flex items-center gap-3">
            <div className="bg-blue-50 p-3 rounded-lg"><FileText className="w-6 h-6 text-blue-600" /></div>
            <div><p className="text-2xl font-bold text-slate-900">{consents.length}</p><p className="text-xs text-slate-500">Total registros</p></div>
          </div>
        </div>
      </div>

      {/* Table */}
      <div className="bg-white rounded-xl border border-slate-200 overflow-hidden">
        <table className="w-full">
          <thead className="bg-slate-50 border-b border-slate-200">
            <tr>
              <th className="text-left px-4 py-3 text-xs font-semibold text-slate-600 uppercase tracking-wider">Tipo de dato</th>
              <th className="text-left px-4 py-3 text-xs font-semibold text-slate-600 uppercase tracking-wider">Propósito</th>
              <th className="text-left px-4 py-3 text-xs font-semibold text-slate-600 uppercase tracking-wider">Cliente</th>
              <th className="text-left px-4 py-3 text-xs font-semibold text-slate-600 uppercase tracking-wider">Estado</th>
              <th className="text-left px-4 py-3 text-xs font-semibold text-slate-600 uppercase tracking-wider">Fecha</th>
              <th className="text-left px-4 py-3 text-xs font-semibold text-slate-600 uppercase tracking-wider">Acciones</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-100">
            {consents.map((c) => (
              <tr key={c.id} className="hover:bg-slate-50">
                <td className="px-4 py-3 text-sm text-slate-700">{c.dataType}</td>
                <td className="px-4 py-3 text-sm text-slate-700">{c.purpose}</td>
                <td className="px-4 py-3 text-sm text-slate-500">{c.clienteId || '—'}</td>
                <td className="px-4 py-3">
                  {c.granted ? (
                    <span className="inline-flex items-center gap-1 px-2 py-1 rounded-full text-xs font-medium bg-green-100 text-green-700">
                      <CheckCircle className="w-3 h-3" /> Otorgado
                    </span>
                  ) : (
                    <span className="inline-flex items-center gap-1 px-2 py-1 rounded-full text-xs font-medium bg-red-100 text-red-700">
                      <XCircle className="w-3 h-3" /> Retirado
                    </span>
                  )}
                </td>
                <td className="px-4 py-3 text-sm text-slate-500">{c.grantedAt ? new Date(c.grantedAt).toLocaleDateString('es-CO') : '—'}</td>
                <td className="px-4 py-3">
                  <div className="flex gap-2">
                    {c.granted && (
                      <button onClick={() => handleWithdraw(c.id!)} className="p-1.5 text-orange-600 hover:bg-orange-50 rounded-lg" title="Retirar consentimiento">
                        <ShieldAlert className="w-4 h-4" />
                      </button>
                    )}
                    {c.clienteId && (
                      <button onClick={() => handlePurge(c.clienteId!)} className="p-1.5 text-red-600 hover:bg-red-50 rounded-lg" title="Eliminar datos (Derecho al olvido)">
                        <Trash2 className="w-4 h-4" />
                      </button>
                    )}
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        {consents.length === 0 && !loading && (
          <div className="text-center py-12 text-slate-400">
            <Shield className="w-12 h-12 mx-auto mb-3" />
            <p>No hay consentimientos registrados</p>
          </div>
        )}
        {loading && <div className="text-center py-12 text-slate-400">Cargando...</div>}
      </div>

      {/* Modal */}
      {showModal && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-xl shadow-xl max-w-md w-full p-6">
            <h2 className="text-lg font-bold text-slate-900 mb-4">Nuevo Consentimiento GDPR</h2>
            <div className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-1">Tipo de dato</label>
                <select className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm" value={formData.dataType} onChange={e => setFormData({ ...formData, dataType: e.target.value })}>
                  <option value="PERSONAL_DATA">Datos personales</option>
                  <option value="MARKETING">Marketing</option>
                  <option value="ANALYTICS">Analíticas</option>
                  <option value="THIRD_PARTY">Compartir con terceros</option>
                </select>
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-1">Propósito</label>
                <input type="text" className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm" value={formData.purpose} onChange={e => setFormData({ ...formData, purpose: e.target.value })} placeholder="Ej: Envío de newsletter mensual" />
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-1">ID Cliente (opcional)</label>
                <input type="number" className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm" value={formData.clienteId || ''} onChange={e => setFormData({ ...formData, clienteId: e.target.value ? Number(e.target.value) : undefined })} />
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-1">Texto del consentimiento</label>
                <textarea className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm" rows={3} value={formData.consentText} onChange={e => setFormData({ ...formData, consentText: e.target.value })} placeholder="Texto exacto que aceptó el usuario..." />
              </div>
            </div>
            <div className="flex justify-end gap-3 mt-6">
              <button onClick={() => setShowModal(false)} className="px-4 py-2 text-sm font-medium text-slate-600 border border-slate-200 rounded-lg hover:bg-slate-50">Cancelar</button>
              <button onClick={handleGrant} className="px-4 py-2 text-sm font-medium text-white bg-blue-600 rounded-lg hover:bg-blue-700">Registrar</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
