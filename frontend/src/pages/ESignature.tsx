/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
import { useState, useEffect } from 'react';
import { 
  PenTool, Plus, CheckCircle, Clock, XCircle, ExternalLink, Copy
} from 'lucide-react';
import apiClient from '../services/api';

interface ESignature {
  id?: number;
  contratoId?: number;
  documentTitle: string;
  documentUrl?: string;
  signerName: string;
  signerEmail: string;
  signerPhone?: string;
  status?: string;
  signatureToken?: string;
  signedAt?: string;
  expiresAt?: string;
  createdAt?: string;
}

const STATUS_CONFIG: Record<string, { color: string; bg: string; icon: React.ElementType; label: string }> = {
  PENDING: { color: 'text-yellow-700', bg: 'bg-yellow-100', icon: Clock, label: 'Pendiente' },
  SIGNED: { color: 'text-green-700', bg: 'bg-green-100', icon: CheckCircle, label: 'Firmado' },
  EXPIRED: { color: 'text-red-700', bg: 'bg-red-100', icon: XCircle, label: 'Expirado' },
  CANCELLED: { color: 'text-slate-600', bg: 'bg-slate-100', icon: XCircle, label: 'Cancelado' },
};

export default function ESignature() {
  const [signatures, setSignatures] = useState<ESignature[]>([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [formData, setFormData] = useState<ESignature>({
    documentTitle: '', signerName: '', signerEmail: '', signerPhone: '',
  });

  const loadData = async () => {
    setLoading(true);
    try {
      const res = await apiClient.get('/api/esignature');
      setSignatures(res.data);
    } catch (e) {
      console.error('Error loading signatures:', e);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { loadData(); }, []);

  const handleCreate = async () => {
    try {
      await apiClient.post('/api/esignature', formData);
      setShowModal(false);
      loadData();
      setFormData({ documentTitle: '', signerName: '', signerEmail: '', signerPhone: '' });
    } catch (e) {
      console.error('Error creating signature request:', e);
    }
  };

  const handleCancel = async (id: number) => {
    if (window.confirm('¿Cancelar esta solicitud de firma?')) {
      try {
        await apiClient.patch(`/api/esignature/${id}/cancel`);
        loadData();
      } catch (e) {
        console.error('Error cancelling:', e);
      }
    }
  };

  const copyLink = (token: string) => {
    navigator.clipboard.writeText(`${window.location.origin}/sign?token=${token}`);
  };

  const pending = signatures.filter(s => s.status === 'PENDING').length;
  const signed = signatures.filter(s => s.status === 'SIGNED').length;

  return (
    <div className="p-6 space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-slate-900 flex items-center gap-2">
            <PenTool className="w-7 h-7 text-blue-600" />
            Firma Electrónica
          </h1>
          <p className="text-sm text-slate-500 mt-1">Envía documentos para firma legalmente válida desde el CRM</p>
        </div>
        <button onClick={() => setShowModal(true)} className="flex items-center gap-2 px-4 py-2 text-sm font-medium text-white bg-blue-600 rounded-lg hover:bg-blue-700">
          <Plus className="w-4 h-4" /> Nueva Solicitud
        </button>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <div className="bg-white rounded-xl border border-slate-200 p-5">
          <div className="flex items-center gap-3">
            <div className="bg-yellow-50 p-3 rounded-lg"><Clock className="w-6 h-6 text-yellow-600" /></div>
            <div><p className="text-2xl font-bold text-slate-900">{pending}</p><p className="text-xs text-slate-500">Pendientes</p></div>
          </div>
        </div>
        <div className="bg-white rounded-xl border border-slate-200 p-5">
          <div className="flex items-center gap-3">
            <div className="bg-green-50 p-3 rounded-lg"><CheckCircle className="w-6 h-6 text-green-600" /></div>
            <div><p className="text-2xl font-bold text-slate-900">{signed}</p><p className="text-xs text-slate-500">Firmados</p></div>
          </div>
        </div>
        <div className="bg-white rounded-xl border border-slate-200 p-5">
          <div className="flex items-center gap-3">
            <div className="bg-blue-50 p-3 rounded-lg"><PenTool className="w-6 h-6 text-blue-600" /></div>
            <div><p className="text-2xl font-bold text-slate-900">{signatures.length}</p><p className="text-xs text-slate-500">Total solicitudes</p></div>
          </div>
        </div>
      </div>

      <div className="bg-white rounded-xl border border-slate-200 overflow-hidden">
        <table className="w-full">
          <thead className="bg-slate-50 border-b border-slate-200">
            <tr>
              <th className="text-left px-4 py-3 text-xs font-semibold text-slate-600 uppercase tracking-wider">Documento</th>
              <th className="text-left px-4 py-3 text-xs font-semibold text-slate-600 uppercase tracking-wider">Firmante</th>
              <th className="text-left px-4 py-3 text-xs font-semibold text-slate-600 uppercase tracking-wider">Estado</th>
              <th className="text-left px-4 py-3 text-xs font-semibold text-slate-600 uppercase tracking-wider">Expira</th>
              <th className="text-left px-4 py-3 text-xs font-semibold text-slate-600 uppercase tracking-wider">Acciones</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-100">
            {signatures.map((s) => {
              const cfg = STATUS_CONFIG[s.status || 'PENDING'] || STATUS_CONFIG.PENDING;
              const StatusIcon = cfg.icon;
              return (
                <tr key={s.id} className="hover:bg-slate-50">
                  <td className="px-4 py-3">
                    <p className="text-sm font-medium text-slate-900">{s.documentTitle}</p>
                    {s.contratoId && <p className="text-xs text-slate-400">Contrato #{s.contratoId}</p>}
                  </td>
                  <td className="px-4 py-3">
                    <p className="text-sm text-slate-700">{s.signerName}</p>
                    <p className="text-xs text-slate-500">{s.signerEmail}</p>
                  </td>
                  <td className="px-4 py-3">
                    <span className={`inline-flex items-center gap-1 px-2 py-1 rounded-full text-xs font-medium ${cfg.bg} ${cfg.color}`}>
                      <StatusIcon className="w-3 h-3" /> {cfg.label}
                    </span>
                    {s.signedAt && <p className="text-xs text-slate-400 mt-1">{new Date(s.signedAt).toLocaleDateString('es-CO')}</p>}
                  </td>
                  <td className="px-4 py-3 text-sm text-slate-500">{s.expiresAt ? new Date(s.expiresAt).toLocaleDateString('es-CO') : '—'}</td>
                  <td className="px-4 py-3">
                    <div className="flex gap-2">
                      {s.status === 'PENDING' && (
                        <>
                          <button onClick={() => copyLink(s.signatureToken || '')} className="p-1.5 text-slate-400 hover:text-blue-600 hover:bg-blue-50 rounded" title="Copiar enlace de firma">
                            <Copy className="w-3.5 h-3.5" />
                          </button>
                          <a href={`/sign?token=${s.signatureToken}`} target="_blank" rel="noopener" className="p-1.5 text-blue-600 hover:bg-blue-50 rounded" title="Abrir página de firma">
                            <ExternalLink className="w-3.5 h-3.5" />
                          </a>
                          <button onClick={() => handleCancel(s.id!)} className="p-1.5 text-red-600 hover:bg-red-50 rounded" title="Cancelar">
                            <XCircle className="w-3.5 h-3.5" />
                          </button>
                        </>
                      )}
                    </div>
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
        {signatures.length === 0 && !loading && (
          <div className="text-center py-12 text-slate-400">
            <PenTool className="w-12 h-12 mx-auto mb-3" />
            <p>No hay solicitudes de firma</p>
          </div>
        )}
        {loading && <div className="text-center py-12 text-slate-400">Cargando...</div>}
      </div>

      {showModal && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-xl shadow-xl max-w-md w-full p-6">
            <h2 className="text-lg font-bold text-slate-900 mb-4">Nueva Solicitud de Firma</h2>
            <div className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-1">Título del documento</label>
                <input type="text" className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm" value={formData.documentTitle} onChange={e => setFormData({ ...formData, documentTitle: e.target.value })} placeholder="Ej: Contrato de servicios 2024" />
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-1">URL del documento (opcional)</label>
                <input type="text" className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm" value={formData.documentUrl || ''} onChange={e => setFormData({ ...formData, documentUrl: e.target.value })} placeholder="https://..." />
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-1">Nombre del firmante</label>
                <input type="text" className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm" value={formData.signerName} onChange={e => setFormData({ ...formData, signerName: e.target.value })} />
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-1">Email del firmante</label>
                <input type="email" className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm" value={formData.signerEmail} onChange={e => setFormData({ ...formData, signerEmail: e.target.value })} />
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-1">Teléfono (opcional)</label>
                <input type="text" className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm" value={formData.signerPhone} onChange={e => setFormData({ ...formData, signerPhone: e.target.value })} />
              </div>
            </div>
            <div className="flex justify-end gap-3 mt-6">
              <button onClick={() => setShowModal(false)} className="px-4 py-2 text-sm font-medium text-slate-600 border border-slate-200 rounded-lg hover:bg-slate-50">Cancelar</button>
              <button onClick={handleCreate} className="px-4 py-2 text-sm font-medium text-white bg-blue-600 rounded-lg hover:bg-blue-700">Enviar Solicitud</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
