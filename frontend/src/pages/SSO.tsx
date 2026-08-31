/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
import { useState, useEffect } from 'react';
import { 
  Key, Plus, Edit, Trash2, RefreshCw, Power, 
  Shield, CheckCircle, Cloud
} from 'lucide-react';
import apiClient from '../services/api';

interface SSOConfig {
  id?: number;
  provider: string;
  protocol: string;
  clientId?: string;
  clientSecret?: string;
  tenantUuid?: string;
  redirectUri?: string;
  metadataUrl?: string;
  idpEntityId?: string;
  idpSsoUrl?: string;
  idpCertificate?: string;
  spEntityId?: string;
  attributeMapping?: string;
  isActive: boolean;
  autoProvision: boolean;
  defaultRole: string;
  lastSyncAt?: string;
}

const PROVIDERS = [
  { value: 'AZURE_AD', label: 'Microsoft Azure AD', icon: Cloud, color: 'bg-blue-600' },
  { value: 'GOOGLE_WORKSPACE', label: 'Google Workspace', icon: Shield, color: 'bg-red-500' },
  { value: 'OKTA', label: 'Okta', icon: Key, color: 'bg-blue-500' },
  { value: 'AUTH0', label: 'Auth0', icon: Shield, color: 'bg-orange-500' },
  { value: 'ONELOGIN', label: 'OneLogin', icon: Key, color: 'bg-green-500' },
  { value: 'CUSTOM', label: 'Personalizado', icon: Key, color: 'bg-slate-500' },
];

export default function SSO() {
  const [configs, setConfigs] = useState<SSOConfig[]>([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [editing, setEditing] = useState<SSOConfig | null>(null);
  const [formData, setFormData] = useState<SSOConfig>({
    provider: 'AZURE_AD', protocol: 'SAML', isActive: true, autoProvision: true, defaultRole: 'VENDEDOR',
  });

  const loadData = async () => {
    setLoading(true);
    try {
      const res = await apiClient.get('/api/sso');
      setConfigs(res.data);
    } catch (e) {
      console.error('Error loading SSO configs:', e);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { loadData(); }, []);

  const handleSubmit = async () => {
    try {
      if (editing?.id) {
        await apiClient.put(`/api/sso/${editing.id}`, formData);
      } else {
        await apiClient.post('/api/sso', formData);
      }
      setShowModal(false);
      setEditing(null);
      loadData();
      setFormData({ provider: 'AZURE_AD', protocol: 'SAML', isActive: true, autoProvision: true, defaultRole: 'VENDEDOR' });
    } catch (e) {
      console.error('Error saving SSO config:', e);
    }
  };

  const handleToggle = async (id: number) => {
    try { await apiClient.patch(`/api/sso/${id}/toggle`); loadData(); } catch (e) { console.error(e); }
  };

  const handleSync = async (id: number) => {
    try { await apiClient.post(`/api/sso/${id}/sync`); loadData(); } catch (e) { console.error(e); }
  };

  const handleDelete = async (id: number) => {
    if (window.confirm('¿Eliminar esta configuración SSO?')) {
      try { await apiClient.delete(`/api/sso/${id}`); loadData(); } catch (e) { console.error(e); }
    }
  };

  return (
    <div className="p-6 space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-slate-900 flex items-center gap-2">
            <Key className="w-7 h-7 text-indigo-600" />
            SSO - Single Sign-On
          </h1>
          <p className="text-sm text-slate-500 mt-1">Integración con Azure AD, Google Workspace, Okta y más</p>
        </div>
        <button onClick={() => { setEditing(null); setFormData({ provider: 'AZURE_AD', protocol: 'SAML', isActive: true, autoProvision: true, defaultRole: 'VENDEDOR' }); setShowModal(true); }} className="flex items-center gap-2 px-4 py-2 text-sm font-medium text-white bg-blue-600 rounded-lg hover:bg-blue-700">
          <Plus className="w-4 h-4" /> Nueva Configuración
        </button>
      </div>

      {/* Available providers */}
      <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-3">
        {PROVIDERS.map(p => {
          const Icon = p.icon;
          const configured = configs.find(c => c.provider === p.value);
          return (
            <div key={p.value} className={`bg-white rounded-xl border p-4 text-center ${configured ? 'border-green-200' : 'border-slate-200'}`}>
              <div className={`w-10 h-10 rounded-lg ${p.color} flex items-center justify-center mx-auto mb-2`}>
                <Icon className="w-5 h-5 text-white" />
              </div>
              <p className="text-xs font-medium text-slate-700">{p.label}</p>
              {configured && <p className="text-xs text-green-600 mt-1 flex items-center justify-center gap-1"><CheckCircle className="w-3 h-3" /> Configurado</p>}
            </div>
          );
        })}
      </div>

      {/* Configured SSO list */}
      <div className="bg-white rounded-xl border border-slate-200 overflow-hidden">
        <table className="w-full">
          <thead className="bg-slate-50 border-b border-slate-200">
            <tr>
              <th className="text-left px-4 py-3 text-xs font-semibold text-slate-600 uppercase tracking-wider">Proveedor</th>
              <th className="text-left px-4 py-3 text-xs font-semibold text-slate-600 uppercase tracking-wider">Protocolo</th>
              <th className="text-left px-4 py-3 text-xs font-semibold text-slate-600 uppercase tracking-wider">Client ID</th>
              <th className="text-left px-4 py-3 text-xs font-semibold text-slate-600 uppercase tracking-wider">Rol por defecto</th>
              <th className="text-left px-4 py-3 text-xs font-semibold text-slate-600 uppercase tracking-wider">Auto-provision</th>
              <th className="text-left px-4 py-3 text-xs font-semibold text-slate-600 uppercase tracking-wider">Estado</th>
              <th className="text-left px-4 py-3 text-xs font-semibold text-slate-600 uppercase tracking-wider">Última sync</th>
              <th className="text-left px-4 py-3 text-xs font-semibold text-slate-600 uppercase tracking-wider">Acciones</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-100">
            {configs.map((c) => (
              <tr key={c.id} className="hover:bg-slate-50">
                <td className="px-4 py-3 text-sm font-medium text-slate-900">{PROVIDERS.find(p => p.value === c.provider)?.label || c.provider}</td>
                <td className="px-4 py-3"><span className="px-2 py-0.5 rounded-full text-xs font-medium bg-indigo-100 text-indigo-700">{c.protocol}</span></td>
                <td className="px-4 py-3 text-sm text-slate-500 font-mono">{c.clientId ? `${c.clientId.substring(0, 12)}...` : '—'}</td>
                <td className="px-4 py-3 text-sm text-slate-700">{c.defaultRole}</td>
                <td className="px-4 py-3">
                  <span className={`px-2 py-0.5 rounded-full text-xs font-medium ${c.autoProvision ? 'bg-green-100 text-green-700' : 'bg-slate-100 text-slate-500'}`}>{c.autoProvision ? 'Sí' : 'No'}</span>
                </td>
                <td className="px-4 py-3">
                  <span className={`px-2 py-0.5 rounded-full text-xs font-medium ${c.isActive ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'}`}>{c.isActive ? 'Activo' : 'Inactivo'}</span>
                </td>
                <td className="px-4 py-3 text-sm text-slate-500">{c.lastSyncAt ? new Date(c.lastSyncAt).toLocaleDateString('es-CO') : 'Nunca'}</td>
                <td className="px-4 py-3">
                  <div className="flex gap-1">
                    <button onClick={() => handleSync(c.id!)} className="p-1.5 text-slate-400 hover:text-blue-600 hover:bg-blue-50 rounded" title="Sincronizar"><RefreshCw className="w-3.5 h-3.5" /></button>
                    <button onClick={() => handleToggle(c.id!)} className="p-1.5 text-slate-400 hover:text-yellow-600 hover:bg-yellow-50 rounded" title="Activar/Desactivar"><Power className="w-3.5 h-3.5" /></button>
                    <button onClick={() => { setEditing(c); setFormData(c); setShowModal(true); }} className="p-1.5 text-slate-400 hover:text-blue-600 hover:bg-blue-50 rounded"><Edit className="w-3.5 h-3.5" /></button>
                    <button onClick={() => handleDelete(c.id!)} className="p-1.5 text-slate-400 hover:text-red-600 hover:bg-red-50 rounded"><Trash2 className="w-3.5 h-3.5" /></button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        {configs.length === 0 && !loading && (
          <div className="text-center py-12 text-slate-400">
            <Key className="w-12 h-12 mx-auto mb-3" />
            <p>No hay configuraciones SSO. Crea una para habilitar login corporativo.</p>
          </div>
        )}
        {loading && <div className="text-center py-12 text-slate-400">Cargando...</div>}
      </div>

      {showModal && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-xl shadow-xl max-w-lg w-full p-6 max-h-[90vh] overflow-y-auto">
            <h2 className="text-lg font-bold text-slate-900 mb-4">{editing?.id ? 'Editar Configuración SSO' : 'Nueva Configuración SSO'}</h2>
            <div className="space-y-4">
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">Proveedor</label>
                  <select className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm" value={formData.provider} onChange={e => setFormData({ ...formData, provider: e.target.value })}>
                    {PROVIDERS.map(p => <option key={p.value} value={p.value}>{p.label}</option>)}
                  </select>
                </div>
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">Protocolo</label>
                  <select className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm" value={formData.protocol} onChange={e => setFormData({ ...formData, protocol: e.target.value })}>
                    <option value="SAML">SAML 2.0</option>
                    <option value="OAUTH2">OAuth 2.0</option>
                    <option value="OIDC">OpenID Connect</option>
                  </select>
                </div>
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-1">Client ID / Entity ID</label>
                <input type="text" className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm" value={formData.clientId || ''} onChange={e => setFormData({ ...formData, clientId: e.target.value })} />
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-1">Client Secret / Certificate</label>
                <input type="password" className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm" value={formData.clientSecret || ''} onChange={e => setFormData({ ...formData, clientSecret: e.target.value })} />
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-1">Tenant UUID (Azure AD)</label>
                <input type="text" className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm" value={formData.tenantUuid || ''} onChange={e => setFormData({ ...formData, tenantUuid: e.target.value })} />
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-1">Redirect URI</label>
                <input type="text" className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm" value={formData.redirectUri || ''} onChange={e => setFormData({ ...formData, redirectUri: e.target.value })} placeholder="https://crm.tuempresa.com/api/sso/callback" />
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-1">Metadata URL (opcional)</label>
                <input type="text" className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm" value={formData.metadataUrl || ''} onChange={e => setFormData({ ...formData, metadataUrl: e.target.value })} />
              </div>
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">Rol por defecto</label>
                  <select className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm" value={formData.defaultRole} onChange={e => setFormData({ ...formData, defaultRole: e.target.value })}>
                    <option value="SUPERADMIN">Super Admin</option>
                    <option value="ADMIN">Admin</option>
                    <option value="VENDEDOR">Vendedor</option>
                    <option value="MARKETING">Marketing</option>
                    <option value="SOPORTE">Soporte</option>
                  </select>
                </div>
                <div className="flex items-end gap-4 pb-2">
                  <label className="flex items-center gap-2">
                    <input type="checkbox" checked={formData.autoProvision} onChange={e => setFormData({ ...formData, autoProvision: e.target.checked })} className="w-4 h-4 rounded" />
                    <span className="text-sm text-slate-700">Auto-provisionar usuarios</span>
                  </label>
                </div>
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
