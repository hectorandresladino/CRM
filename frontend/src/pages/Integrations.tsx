/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
import { useState, useEffect } from 'react';
import { 
  Plug, CheckCircle2,
  CreditCard, Calendar, Key, MessageSquare, ShoppingBag, FileSpreadsheet
} from 'lucide-react';
import apiClient from '../services/api';

interface Integration {
  id?: number;
  provider: string;
  category: string;
  connected: boolean;
  syncEnabled: boolean;
  lastSyncAt?: string;
  syncFrequency?: string;
}

const PROVIDER_INFO: Record<string, { icon: React.ElementType; color: string; label: string; description: string }> = {
  STRIPE: { icon: CreditCard, color: 'bg-indigo-500', label: 'Stripe', description: 'Pagos online' },
  MERCADO_PAGO: { icon: CreditCard, color: 'bg-blue-500', label: 'Mercado Pago', description: 'Pagos LATAM' },
  GOOGLE_CALENDAR: { icon: Calendar, color: 'bg-red-500', label: 'Google Calendar', description: 'SincronizaciÃ³n de calendario' },
  GOOGLE_WORKSPACE: { icon: Key, color: 'bg-yellow-500', label: 'Google Workspace', description: 'SSO con Google' },
  AZURE_AD: { icon: Key, color: 'bg-sky-500', label: 'Azure AD', description: 'SSO con Microsoft' },
  OKTA: { icon: Key, color: 'bg-blue-600', label: 'Okta', description: 'SSO empresarial' },
  SLACK: { icon: MessageSquare, color: 'bg-purple-500', label: 'Slack', description: 'Notificaciones en Slack' },
  WHATSAPP_BUSINESS: { icon: MessageSquare, color: 'bg-green-500', label: 'WhatsApp Business', description: 'API oficial de Meta' },
  META_BUSINESS: { icon: MessageSquare, color: 'bg-blue-500', label: 'Meta Business', description: 'Facebook & Instagram' },
  SHOPIFY: { icon: ShoppingBag, color: 'bg-green-600', label: 'Shopify', description: 'E-commerce sync' },
  ALEGRA: { icon: FileSpreadsheet, color: 'bg-orange-500', label: 'Alegra', description: 'FacturaciÃ³n electrÃ³nica Colombia' },
  DIAN: { icon: FileSpreadsheet, color: 'bg-yellow-600', label: 'DIAN', description: 'FacturaciÃ³n electrÃ³nica Colombia' },
  QUICKBOOKS: { icon: FileSpreadsheet, color: 'bg-green-700', label: 'QuickBooks', description: 'Contabilidad' },
  ZAPIER: { icon: Plug, color: 'bg-orange-600', label: 'Zapier', description: 'AutomatizaciÃ³n 5000+ apps' },
  MAKE: { icon: Plug, color: 'bg-purple-600', label: 'Make', description: 'AutomatizaciÃ³n visual' },
};

export default function Integrations() {
  const [integrations, setIntegrations] = useState<Integration[]>([]);
  const [loading, setLoading] = useState(true);

  const loadData = async () => {
    setLoading(true);
    try {
      const res = await apiClient.get('/api/integrations');
      setIntegrations(res.data);
    } catch (e) {
      console.error('Error loading integrations:', e);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { loadData(); }, []);

  const handleConnect = async (provider: string, category: string) => {
    try {
      await apiClient.post('/api/integrations', { provider, category, connected: true, syncEnabled: true });
      loadData();
    } catch (e) {
      console.error('Error connecting:', e);
    }
  };

  const handleDisconnect = async (id: number) => {
    if (window.confirm('Â¿Desconectar esta integraciÃ³n?')) {
      try {
        await apiClient.patch(`/api/integrations/${id}/disconnect`);
        loadData();
      } catch (e) {
        console.error('Error disconnecting:', e);
      }
    }
  };

  const handleToggleSync = async (id: number) => {
    try {
      await apiClient.patch(`/api/integrations/${id}/toggle-sync`);
      loadData();
    } catch (e) {
      console.error('Error toggling sync:', e);
    }
  };

  const connectedProviders = new Set(integrations.filter(i => i.connected).map(i => i.provider));
  const availableProviders = Object.keys(PROVIDER_INFO).filter(p => !connectedProviders.has(p));

  return (
    <div className="p-6">
      <div className="mb-6">
        <h1 className="text-2xl font-bold text-slate-900 flex items-center gap-2">
          <Plug className="w-7 h-7 text-blue-600" />
          Integraciones
        </h1>
        <p className="text-sm text-slate-500 mt-1">Conecta tu CRM con las herramientas que ya usas</p>
      </div>

      {/* Connected integrations */}
      <h2 className="text-sm font-semibold text-slate-700 uppercase tracking-wider mb-3">Conectadas ({integrations.filter(i => i.connected).length})</h2>
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4 mb-8">
        {integrations.filter(i => i.connected).map((int) => {
          const info = PROVIDER_INFO[int.provider] || { icon: Plug, color: 'bg-slate-500', label: int.provider, description: '' };
          const Icon = info.icon;
          return (
            <div key={int.id} className="bg-white rounded-xl border border-slate-200 p-5">
              <div className="flex items-start justify-between mb-3">
                <div className="flex items-center gap-3">
                  <div className={`p-2.5 rounded-lg ${info.color}`}>
                    <Icon className="w-5 h-5 text-white" />
                  </div>
                  <div>
                    <h3 className="font-semibold text-sm text-slate-900">{info.label}</h3>
                    <p className="text-xs text-slate-500">{info.description}</p>
                  </div>
                </div>
                <CheckCircle2 className="w-5 h-5 text-green-500" />
              </div>
              <div className="flex items-center justify-between text-xs text-slate-500 mb-3">
                <span>SincronizaciÃ³n: {int.syncEnabled ? 'Activa' : 'Pausada'}</span>
                {int.lastSyncAt && <span>Ãšltima sync: {new Date(int.lastSyncAt).toLocaleDateString('es-CO')}</span>}
              </div>
              <div className="flex gap-2">
                <button onClick={() => handleToggleSync(int.id!)} className="flex-1 px-3 py-1.5 text-xs font-medium text-slate-600 border border-slate-200 rounded-lg hover:bg-slate-50">
                  {int.syncEnabled ? 'Pausar sync' : 'Activar sync'}
                </button>
                <button onClick={() => handleDisconnect(int.id!)} className="px-3 py-1.5 text-xs font-medium text-red-600 border border-red-200 rounded-lg hover:bg-red-50">
                  Desconectar
                </button>
              </div>
            </div>
          );
        })}
        {integrations.filter(i => i.connected).length === 0 && !loading && (
          <div className="col-span-full text-center py-8 text-slate-400">
            <p>No hay integraciones conectadas</p>
          </div>
        )}
      </div>

      {/* Available integrations */}
      <h2 className="text-sm font-semibold text-slate-700 uppercase tracking-wider mb-3">Disponibles ({availableProviders.length})</h2>
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        {availableProviders.map((provider) => {
          const info = PROVIDER_INFO[provider];
          if (!info) return null;
          const Icon = info.icon;
          const category = provider.includes('STRIPE') || provider.includes('MERCADO') ? 'PAYMENT' :
            provider.includes('CALENDAR') ? 'CALENDAR' : provider.includes('AZURE') || provider.includes('GOOGLE_WORKSPACE') || provider.includes('OKTA') ? 'SSO' :
            provider.includes('ALEGRA') || provider.includes('DIAN') || provider.includes('QUICKBOOKS') ? 'ACCOUNTING' :
            provider.includes('SHOPIFY') ? 'ECOMMERCE' : 'COMMUNICATION';
          return (
            <div key={provider} className="bg-white rounded-xl border border-slate-200 p-4 hover:shadow-md transition-all">
              <div className="flex items-center gap-3 mb-3">
                <div className={`p-2 rounded-lg ${info.color} opacity-80`}>
                  <Icon className="w-4 h-4 text-white" />
                </div>
                <div className="min-w-0">
                  <h3 className="font-semibold text-sm text-slate-900 truncate">{info.label}</h3>
                  <p className="text-xs text-slate-500 truncate">{info.description}</p>
                </div>
              </div>
              <button onClick={() => handleConnect(provider, category)} className="w-full px-3 py-1.5 text-xs font-medium text-white bg-blue-600 rounded-lg hover:bg-blue-700">
                Conectar
              </button>
            </div>
          );
        })}
      </div>
      {loading && <div className="text-center py-12 text-slate-400">Cargando...</div>}
    </div>
  );
}
