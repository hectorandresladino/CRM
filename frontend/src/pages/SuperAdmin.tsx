/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
import { useState, useEffect } from 'react';
import { 
  Building2, Users, DollarSign, TrendingUp, AlertCircle,
  CheckCircle, XCircle, Clock, Activity, Shield,
  Search
} from 'lucide-react';
import apiClient from '../services/api';

interface Tenant {
  id: number;
  name: string;
  slug: string;
  country: string;
  currency: string;
  status: string;
  planId?: number;
  trialEndsAt?: string;
  maxUsers: number;
  maxClients: number;
  createdAt?: string;
}

interface Plan {
  id: number;
  name: string;
  priceMonthly: number;
  priceYearly: number;
  maxUsers: number;
  maxClients: number;
  hasWhatsapp: boolean;
  hasAiFeatures: boolean;
  hasApiAccess: boolean;
  hasWhiteLabel: boolean;
  active: boolean;
}

interface Metrics {
  totalTenants: number;
  activeTenants: number;
  trialTenants: number;
  suspendedTenants: number;
  totalUsers: number;
  totalRevenue: number;
  totalPayments: number;
  activeSubscriptions: number;
}

const STATUS_CONFIG: Record<string, { color: string; bg: string; icon: React.ElementType; label: string }> = {
  TRIAL: { color: 'text-yellow-700', bg: 'bg-yellow-100', icon: Clock, label: 'Trial' },
  ACTIVE: { color: 'text-green-700', bg: 'bg-green-100', icon: CheckCircle, label: 'Activo' },
  SUSPENDED: { color: 'text-red-700', bg: 'bg-red-100', icon: XCircle, label: 'Suspendido' },
  CANCELLED: { color: 'text-slate-600', bg: 'bg-slate-100', icon: XCircle, label: 'Cancelado' },
  EXPIRED: { color: 'text-orange-700', bg: 'bg-orange-100', icon: AlertCircle, label: 'Expirado' },
};

export default function SuperAdmin() {
  const [tenants, setTenants] = useState<Tenant[]>([]);
  const [plans, setPlans] = useState<Plan[]>([]);
  const [metrics, setMetrics] = useState<Metrics | null>(null);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [tab, setTab] = useState<'tenants' | 'plans' | 'metrics'>('tenants');

  const loadData = async () => {
    setLoading(true);
    try {
      const [t, p, m] = await Promise.all([
        apiClient.get('/api/v1/superadmin/tenants'),
        apiClient.get('/api/v1/superadmin/plans'),
        apiClient.get('/api/v1/superadmin/metrics'),
      ]);
      setTenants(t.data);
      setPlans(p.data);
      setMetrics(m.data);
    } catch (e) {
      console.error('Error loading superadmin data:', e);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { loadData(); }, []);

  const handleSuspend = async (id: number) => {
    const reason = window.prompt('Motivo de suspensiÃ³n:');
    if (reason) {
      try {
        await apiClient.put(`/api/v1/superadmin/tenants/${id}/suspend`, { reason });
        loadData();
      } catch (e) { console.error(e); }
    }
  };

  const handleActivate = async (id: number) => {
    try {
      await apiClient.put(`/api/v1/superadmin/tenants/${id}/activate`);
      loadData();
    } catch (e) { console.error(e); }
  };

  const filtered = tenants.filter(t =>
    t.name.toLowerCase().includes(search.toLowerCase()) ||
    t.slug.toLowerCase().includes(search.toLowerCase()) ||
    t.country.toLowerCase().includes(search.toLowerCase())
  );

  const fmt = (v: number) => new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(v || 0);

  return (
    <div className="p-6 space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-slate-900 flex items-center gap-2">
          <Shield className="w-7 h-7 text-indigo-600" />
          Panel SuperAdmin
        </h1>
        <p className="text-sm text-slate-500 mt-1">GestiÃ³n de tenants, planes, suscripciones y mÃ©tricas SaaS</p>
      </div>

      {/* Tabs */}
      <div className="flex gap-1 bg-slate-100 p-1 rounded-lg w-fit">
        <button onClick={() => setTab('tenants')} className={`px-4 py-2 text-sm font-medium rounded-md transition ${tab === 'tenants' ? 'bg-white text-slate-900 shadow' : 'text-slate-500'}`}>Tenants</button>
        <button onClick={() => setTab('plans')} className={`px-4 py-2 text-sm font-medium rounded-md transition ${tab === 'plans' ? 'bg-white text-slate-900 shadow' : 'text-slate-500'}`}>Planes</button>
        <button onClick={() => setTab('metrics')} className={`px-4 py-2 text-sm font-medium rounded-md transition ${tab === 'metrics' ? 'bg-white text-slate-900 shadow' : 'text-slate-500'}`}>MÃ©tricas</button>
      </div>

      {/* Metrics tab */}
      {tab === 'metrics' && metrics && (
        <div className="space-y-6">
          <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
            <div className="bg-white rounded-xl border border-slate-200 p-5">
              <div className="flex items-center gap-3"><div className="bg-blue-50 p-3 rounded-lg"><Building2 className="w-6 h-6 text-blue-600" /></div><div><p className="text-2xl font-bold text-slate-900">{metrics.totalTenants}</p><p className="text-xs text-slate-500">Total Empresas</p></div></div>
            </div>
            <div className="bg-white rounded-xl border border-slate-200 p-5">
              <div className="flex items-center gap-3"><div className="bg-green-50 p-3 rounded-lg"><CheckCircle className="w-6 h-6 text-green-600" /></div><div><p className="text-2xl font-bold text-slate-900">{metrics.activeTenants}</p><p className="text-xs text-slate-500">Activas</p></div></div>
            </div>
            <div className="bg-white rounded-xl border border-slate-200 p-5">
              <div className="flex items-center gap-3"><div className="bg-yellow-50 p-3 rounded-lg"><Clock className="w-6 h-6 text-yellow-600" /></div><div><p className="text-2xl font-bold text-slate-900">{metrics.trialTenants}</p><p className="text-xs text-slate-500">En Trial</p></div></div>
            </div>
            <div className="bg-white rounded-xl border border-slate-200 p-5">
              <div className="flex items-center gap-3"><div className="bg-red-50 p-3 rounded-lg"><XCircle className="w-6 h-6 text-red-600" /></div><div><p className="text-2xl font-bold text-slate-900">{metrics.suspendedTenants}</p><p className="text-xs text-slate-500">Suspendidas</p></div></div>
            </div>
          </div>
          <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
            <div className="bg-white rounded-xl border border-slate-200 p-5">
              <div className="flex items-center gap-3"><div className="bg-indigo-50 p-3 rounded-lg"><Users className="w-6 h-6 text-indigo-600" /></div><div><p className="text-2xl font-bold text-slate-900">{metrics.totalUsers}</p><p className="text-xs text-slate-500">Total Usuarios</p></div></div>
            </div>
            <div className="bg-white rounded-xl border border-slate-200 p-5">
              <div className="flex items-center gap-3"><div className="bg-green-50 p-3 rounded-lg"><TrendingUp className="w-6 h-6 text-green-600" /></div><div><p className="text-2xl font-bold text-slate-900">{metrics.activeSubscriptions}</p><p className="text-xs text-slate-500">Suscripciones Activas</p></div></div>
            </div>
            <div className="bg-white rounded-xl border border-slate-200 p-5">
              <div className="flex items-center gap-3"><div className="bg-emerald-50 p-3 rounded-lg"><DollarSign className="w-6 h-6 text-emerald-600" /></div><div><p className="text-2xl font-bold text-slate-900">{fmt(metrics.totalRevenue)}</p><p className="text-xs text-slate-500">Ingresos Totales</p></div></div>
            </div>
            <div className="bg-white rounded-xl border border-slate-200 p-5">
              <div className="flex items-center gap-3"><div className="bg-purple-50 p-3 rounded-lg"><Activity className="w-6 h-6 text-purple-600" /></div><div><p className="text-2xl font-bold text-slate-900">{metrics.totalPayments}</p><p className="text-xs text-slate-500">Pagos Procesados</p></div></div>
            </div>
          </div>
        </div>
      )}

      {/* Plans tab */}
      {tab === 'plans' && (
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          {plans.map((p) => (
            <div key={p.id} className="bg-white rounded-xl border border-slate-200 p-6">
              <div className="flex items-center justify-between mb-4">
                <h3 className="text-lg font-bold text-slate-900">{p.name}</h3>
                <span className={`px-2 py-0.5 rounded-full text-xs font-medium ${p.active ? 'bg-green-100 text-green-700' : 'bg-slate-100 text-slate-500'}`}>{p.active ? 'Activo' : 'Inactivo'}</span>
              </div>
              <div className="mb-4">
                <p className="text-3xl font-bold text-slate-900">{fmt(p.priceMonthly)}<span className="text-sm font-normal text-slate-500">/mes</span></p>
                <p className="text-sm text-slate-500">{fmt(p.priceYearly)}/aÃ±o</p>
              </div>
              <div className="space-y-2 text-sm">
                <div className="flex justify-between"><span className="text-slate-500">Usuarios</span><span className="font-medium text-slate-900">{p.maxUsers}</span></div>
                <div className="flex justify-between"><span className="text-slate-500">Clientes</span><span className="font-medium text-slate-900">{p.maxClients}</span></div>
                <div className="flex flex-wrap gap-1 mt-3">
                  {p.hasWhatsapp && <span className="px-2 py-0.5 bg-blue-50 text-blue-600 text-xs rounded">WhatsApp</span>}
                  {p.hasAiFeatures && <span className="px-2 py-0.5 bg-purple-50 text-purple-600 text-xs rounded">IA</span>}
                  {p.hasApiAccess && <span className="px-2 py-0.5 bg-green-50 text-green-600 text-xs rounded">API</span>}
                  {p.hasWhiteLabel && <span className="px-2 py-0.5 bg-indigo-50 text-indigo-600 text-xs rounded">White Label</span>}
                </div>
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Tenants tab */}
      {tab === 'tenants' && (
        <>
          <div className="relative max-w-md">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-400" />
            <input type="text" placeholder="Buscar empresa..." value={search} onChange={e => setSearch(e.target.value)} className="w-full pl-10 pr-4 py-2 text-sm bg-white border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500" />
          </div>

          <div className="bg-white rounded-xl border border-slate-200 overflow-hidden">
            <table className="w-full">
              <thead className="bg-slate-50 border-b border-slate-200">
                <tr>
                  <th className="text-left px-4 py-3 text-xs font-semibold text-slate-600 uppercase tracking-wider">Empresa</th>
                  <th className="text-left px-4 py-3 text-xs font-semibold text-slate-600 uppercase tracking-wider">PaÃ­s</th>
                  <th className="text-left px-4 py-3 text-xs font-semibold text-slate-600 uppercase tracking-wider">Estado</th>
                  <th className="text-left px-4 py-3 text-xs font-semibold text-slate-600 uppercase tracking-wider">LÃ­mites</th>
                  <th className="text-left px-4 py-3 text-xs font-semibold text-slate-600 uppercase tracking-wider">Trial hasta</th>
                  <th className="text-left px-4 py-3 text-xs font-semibold text-slate-600 uppercase tracking-wider">Acciones</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {filtered.map((t) => {
                  const sc = STATUS_CONFIG[t.status] || STATUS_CONFIG.TRIAL;
                  const StatusIcon = sc.icon;
                  return (
                    <tr key={t.id} className="hover:bg-slate-50">
                      <td className="px-4 py-3">
                        <p className="text-sm font-medium text-slate-900">{t.name}</p>
                        <p className="text-xs text-slate-400 font-mono">{t.slug}</p>
                      </td>
                      <td className="px-4 py-3 text-sm text-slate-600">{t.country}</td>
                      <td className="px-4 py-3">
                        <span className={`inline-flex items-center gap-1 px-2 py-1 rounded-full text-xs font-medium ${sc.bg} ${sc.color}`}>
                          <StatusIcon className="w-3 h-3" /> {sc.label}
                        </span>
                      </td>
                      <td className="px-4 py-3 text-sm text-slate-500">
                        <span>{t.maxUsers} users</span> Â· <span>{t.maxClients} clients</span>
                      </td>
                      <td className="px-4 py-3 text-sm text-slate-500">{t.trialEndsAt ? new Date(t.trialEndsAt).toLocaleDateString('es-CO') : 'â€”'}</td>
                      <td className="px-4 py-3">
                        <div className="flex gap-1">
                          {t.status !== 'SUSPENDED' ? (
                            <button onClick={() => handleSuspend(t.id)} className="px-2 py-1 text-xs text-red-600 hover:bg-red-50 rounded font-medium">Suspender</button>
                          ) : (
                            <button onClick={() => handleActivate(t.id)} className="px-2 py-1 text-xs text-green-600 hover:bg-green-50 rounded font-medium">Activar</button>
                          )}
                        </div>
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
            {filtered.length === 0 && !loading && <div className="text-center py-12 text-slate-400"><Building2 className="w-12 h-12 mx-auto mb-3" /><p>No hay empresas registradas</p></div>}
            {loading && <div className="text-center py-12 text-slate-400">Cargando...</div>}
          </div>
        </>
      )}
    </div>
  );
}
