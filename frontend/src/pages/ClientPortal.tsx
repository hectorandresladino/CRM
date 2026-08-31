/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
import { useState, useEffect } from 'react';
import { 
  ExternalLink, Copy, UserCircle, Ban, Plus
} from 'lucide-react';
import apiClient from '../services/api';

interface PortalAccess {
  id?: number;
  clienteId: number;
  email: string;
  portalToken: string;
  active: boolean;
  lastLoginAt?: string;
  loginCount?: number;
}

export default function ClientPortal() {
  const [accesses, setAccesses] = useState<PortalAccess[]>([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [formData, setFormData] = useState({ clienteId: 0, email: '' });

  const loadData = async () => {
    setLoading(true);
    try {
      const res = await apiClient.get('/api/client-portal');
      setAccesses(res.data);
    } catch (e) {
      console.error('Error loading portal access:', e);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { loadData(); }, []);

  const handleCreate = async () => {
    try {
      await apiClient.post('/api/client-portal', formData);
      setShowModal(false);
      loadData();
      setFormData({ clienteId: 0, email: '' });
    } catch (e) {
      console.error('Error creating access:', e);
    }
  };

  const handleRevoke = async (id: number) => {
    if (window.confirm('¿Revocar este acceso al portal?')) {
      try {
        await apiClient.patch(`/api/client-portal/${id}/revoke`);
        loadData();
      } catch (e) {
        console.error('Error revoking access:', e);
      }
    }
  };

  const copyToken = (token: string) => {
    navigator.clipboard.writeText(`${window.location.origin}/portal?token=${token}`);
  };

  return (
    <div className="p-6">
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-2xl font-bold text-slate-900 flex items-center gap-2">
            <UserCircle className="w-7 h-7 text-cyan-600" />
            Portal de Clientes
          </h1>
          <p className="text-sm text-slate-500 mt-1">Acceso self-service para que los clientes vean sus facturas, pedidos y tickets</p>
        </div>
        <button onClick={() => setShowModal(true)} className="flex items-center gap-2 px-4 py-2 text-sm font-medium text-white bg-blue-600 rounded-lg hover:bg-blue-700">
          <Plus className="w-4 h-4" /> Nuevo Acceso
        </button>
      </div>

      <div className="bg-white rounded-xl border border-slate-200 overflow-hidden">
        <table className="w-full">
          <thead className="bg-slate-50 border-b border-slate-200">
            <tr>
              <th className="text-left px-4 py-3 text-xs font-semibold text-slate-600 uppercase tracking-wider">Cliente ID</th>
              <th className="text-left px-4 py-3 text-xs font-semibold text-slate-600 uppercase tracking-wider">Email</th>
              <th className="text-left px-4 py-3 text-xs font-semibold text-slate-600 uppercase tracking-wider">Token</th>
              <th className="text-left px-4 py-3 text-xs font-semibold text-slate-600 uppercase tracking-wider">Logins</th>
              <th className="text-left px-4 py-3 text-xs font-semibold text-slate-600 uppercase tracking-wider">Último login</th>
              <th className="text-left px-4 py-3 text-xs font-semibold text-slate-600 uppercase tracking-wider">Estado</th>
              <th className="text-left px-4 py-3 text-xs font-semibold text-slate-600 uppercase tracking-wider">Acciones</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-100">
            {accesses.map((a) => (
              <tr key={a.id} className="hover:bg-slate-50">
                <td className="px-4 py-3 text-sm font-medium text-slate-900">#{a.clienteId}</td>
                <td className="px-4 py-3 text-sm text-slate-700">{a.email}</td>
                <td className="px-4 py-3">
                  <div className="flex items-center gap-2">
                    <code className="text-xs text-slate-500 font-mono truncate max-w-[120px]">{a.portalToken.substring(0, 16)}...</code>
                    <button onClick={() => copyToken(a.portalToken)} className="p-1 text-slate-400 hover:text-blue-600 rounded" title="Copiar link del portal">
                      <Copy className="w-3.5 h-3.5" />
                    </button>
                  </div>
                </td>
                <td className="px-4 py-3 text-sm text-slate-600">{a.loginCount || 0}</td>
                <td className="px-4 py-3 text-sm text-slate-500">{a.lastLoginAt ? new Date(a.lastLoginAt).toLocaleDateString('es-CO') : 'Nunca'}</td>
                <td className="px-4 py-3">
                  {a.active ? (
                    <span className="px-2 py-1 text-xs font-medium bg-green-100 text-green-700 rounded-full">Activo</span>
                  ) : (
                    <span className="px-2 py-1 text-xs font-medium bg-red-100 text-red-700 rounded-full">Revocado</span>
                  )}
                </td>
                <td className="px-4 py-3">
                  <div className="flex gap-2">
                    <a href={`/portal?token=${a.portalToken}`} target="_blank" rel="noopener" className="p-1.5 text-blue-600 hover:bg-blue-50 rounded" title="Abrir portal">
                      <ExternalLink className="w-3.5 h-3.5" />
                    </a>
                    {a.active && (
                      <button onClick={() => handleRevoke(a.id!)} className="p-1.5 text-red-600 hover:bg-red-50 rounded" title="Revocar acceso">
                        <Ban className="w-3.5 h-3.5" />
                      </button>
                    )}
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        {accesses.length === 0 && !loading && (
          <div className="text-center py-12 text-slate-400">
            <UserCircle className="w-12 h-12 mx-auto mb-3" />
            <p>No hay accesos al portal configurados</p>
          </div>
        )}
        {loading && <div className="text-center py-12 text-slate-400">Cargando...</div>}
      </div>

      {showModal && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-xl shadow-xl max-w-md w-full p-6">
            <h2 className="text-lg font-bold text-slate-900 mb-4">Nuevo Acceso al Portal</h2>
            <div className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-1">ID del Cliente</label>
                <input type="number" className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm" value={formData.clienteId || ''} onChange={e => setFormData({ ...formData, clienteId: Number(e.target.value) })} />
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-1">Email del cliente</label>
                <input type="email" className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm" value={formData.email} onChange={e => setFormData({ ...formData, email: e.target.value })} placeholder="cliente@empresa.com" />
              </div>
            </div>
            <div className="flex justify-end gap-3 mt-6">
              <button onClick={() => setShowModal(false)} className="px-4 py-2 text-sm font-medium text-slate-600 border border-slate-200 rounded-lg hover:bg-slate-50">Cancelar</button>
              <button onClick={handleCreate} className="px-4 py-2 text-sm font-medium text-white bg-blue-600 rounded-lg hover:bg-blue-700">Crear Acceso</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
