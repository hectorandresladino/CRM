/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
import { useState, useEffect } from 'react';
import { 
  CreditCard, Download, CheckCircle, Clock, AlertCircle,
  Plane, RefreshCw
} from 'lucide-react';
import apiClient from '../services/api';

interface Invoice {
  id: number;
  number: string;
  amount: number;
  currency: string;
  status: string;
  issueDate: string;
  dueDate: string;
  paidDate?: string;
  billingPeriodStart: string;
  billingPeriodEnd: string;
  pdfUrl?: string;
}

interface Subscription {
  id: number;
  status: string;
  billingCycle: string;
  currentPeriodStart: string;
  currentPeriodEnd: string;
  amount: number;
  currency: string;
  autoRenew: boolean;
  trialEnd?: string;
}

const STATUS_CONFIG: Record<string, { color: string; bg: string; icon: React.ElementType; label: string }> = {
  PENDING: { color: 'text-yellow-700', bg: 'bg-yellow-100', icon: Clock, label: 'Pendiente' },
  PAID: { color: 'text-green-700', bg: 'bg-green-100', icon: CheckCircle, label: 'Pagada' },
  OVERDUE: { color: 'text-red-700', bg: 'bg-red-100', icon: AlertCircle, label: 'Vencida' },
  CANCELLED: { color: 'text-slate-600', bg: 'bg-slate-100', icon: AlertCircle, label: 'Cancelada' },
  REFUNDED: { color: 'text-purple-700', bg: 'bg-purple-100', icon: RefreshCw, label: 'Reembolsada' },
};

const SUB_STATUS: Record<string, { color: string; bg: string; label: string }> = {
  TRIAL: { color: 'text-yellow-700', bg: 'bg-yellow-100', label: 'PerÃ­odo de Prueba' },
  ACTIVE: { color: 'text-green-700', bg: 'bg-green-100', label: 'Activa' },
  PAST_DUE: { color: 'text-orange-700', bg: 'bg-orange-100', label: 'Pago Pendiente' },
  SUSPENDED: { color: 'text-red-700', bg: 'bg-red-100', label: 'Suspendida' },
  CANCELLED: { color: 'text-slate-600', bg: 'bg-slate-100', label: 'Cancelada' },
};

export default function Billing() {
  const [invoices, setInvoices] = useState<Invoice[]>([]);
  const [subscription, setSubscription] = useState<Subscription | null>(null);
  const [loading, setLoading] = useState(true);

  const loadData = async () => {
    setLoading(true);
    try {
      const [inv, subs] = await Promise.all([
        apiClient.get('/api/v1/billing/invoices').catch(() => ({ data: [] })),
        apiClient.get('/api/v1/billing/subscription').catch(() => ({ data: null })),
      ]);
      setInvoices(inv.data);
      if (subs.data) setSubscription(subs.data);
    } catch (e) {
      console.error('Error loading billing data:', e);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { loadData(); }, []);

  const fmt = (v: number, c: string) => new Intl.NumberFormat('en-US', { style: 'currency', currency: c || 'USD' }).format(v || 0);

  return (
    <div className="p-6 space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-slate-900 flex items-center gap-2">
          <CreditCard className="w-7 h-7 text-green-600" />
          Billing y SuscripciÃ³n
        </h1>
        <p className="text-sm text-slate-500 mt-1">Gestiona tu plan, facturas y mÃ©todo de pago</p>
      </div>

      {/* Current subscription */}
      {subscription && (
        <div className="bg-gradient-to-r from-blue-600 to-indigo-600 rounded-xl p-6 text-white">
          <div className="flex items-center justify-between">
            <div>
              <div className="flex items-center gap-2 mb-2">
                <Plane className="w-5 h-5" />
                <h2 className="text-lg font-bold">Tu SuscripciÃ³n</h2>
                <span className={`px-2 py-0.5 rounded-full text-xs font-medium ${SUB_STATUS[subscription.status]?.bg || 'bg-white/20'} ${SUB_STATUS[subscription.status]?.color || 'text-white'}`}>
                  {SUB_STATUS[subscription.status]?.label || subscription.status}
                </span>
              </div>
              <p className="text-3xl font-bold">{fmt(subscription.amount, subscription.currency)}<span className="text-sm font-normal">/{subscription.billingCycle === 'YEARLY' ? 'aÃ±o' : 'mes'}</span></p>
              <p className="text-sm text-blue-100 mt-1">
                PrÃ³ximo cobro: {new Date(subscription.currentPeriodEnd).toLocaleDateString('es-CO')}
              </p>
              {subscription.trialEnd && (
                <p className="text-sm text-yellow-200 mt-1">Trial termina: {new Date(subscription.trialEnd).toLocaleDateString('es-CO')}</p>
              )}
            </div>
            <div className="text-right">
              <p className="text-sm text-blue-100">Auto-renovaciÃ³n</p>
              <p className="text-lg font-bold">{subscription.autoRenew ? 'Activada' : 'Desactivada'}</p>
            </div>
          </div>
        </div>
      )}

      {/* Payment method */}
      <div className="bg-white rounded-xl border border-slate-200 p-6">
        <h2 className="text-lg font-semibold text-slate-900 mb-4">MÃ©todo de Pago</h2>
        <div className="flex items-center justify-between p-4 border border-slate-200 rounded-lg">
          <div className="flex items-center gap-3">
            <CreditCard className="w-8 h-8 text-slate-400" />
            <div>
              <p className="text-sm font-medium text-slate-900">Sin mÃ©todo de pago configurado</p>
              <p className="text-xs text-slate-500">Agrega una tarjeta para activar tu suscripciÃ³n</p>
            </div>
          </div>
          <button className="px-4 py-2 text-sm font-medium text-white bg-blue-600 rounded-lg hover:bg-blue-700">Agregar Tarjeta</button>
        </div>
      </div>

      {/* Invoices */}
      <div className="bg-white rounded-xl border border-slate-200 overflow-hidden">
        <div className="px-6 py-4 border-b border-slate-200">
          <h2 className="text-lg font-semibold text-slate-900">Historial de Facturas</h2>
        </div>
        <table className="w-full">
          <thead className="bg-slate-50 border-b border-slate-200">
            <tr>
              <th className="text-left px-4 py-3 text-xs font-semibold text-slate-600 uppercase tracking-wider">Factura</th>
              <th className="text-left px-4 py-3 text-xs font-semibold text-slate-600 uppercase tracking-wider">PerÃ­odo</th>
              <th className="text-left px-4 py-3 text-xs font-semibold text-slate-600 uppercase tracking-wider">Monto</th>
              <th className="text-left px-4 py-3 text-xs font-semibold text-slate-600 uppercase tracking-wider">Estado</th>
              <th className="text-left px-4 py-3 text-xs font-semibold text-slate-600 uppercase tracking-wider">Fecha</th>
              <th className="text-left px-4 py-3 text-xs font-semibold text-slate-600 uppercase tracking-wider">PDF</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-100">
            {invoices.map((inv) => {
              const sc = STATUS_CONFIG[inv.status] || STATUS_CONFIG.PENDING;
              const StatusIcon = sc.icon;
              return (
                <tr key={inv.id} className="hover:bg-slate-50">
                  <td className="px-4 py-3 text-sm font-mono text-slate-700">{inv.number}</td>
                  <td className="px-4 py-3 text-sm text-slate-500">
                    {new Date(inv.billingPeriodStart).toLocaleDateString('es-CO')} - {new Date(inv.billingPeriodEnd).toLocaleDateString('es-CO')}
                  </td>
                  <td className="px-4 py-3 text-sm font-medium text-slate-900">{fmt(inv.amount, inv.currency)}</td>
                  <td className="px-4 py-3">
                    <span className={`inline-flex items-center gap-1 px-2 py-1 rounded-full text-xs font-medium ${sc.bg} ${sc.color}`}>
                      <StatusIcon className="w-3 h-3" /> {sc.label}
                    </span>
                  </td>
                  <td className="px-4 py-3 text-sm text-slate-500">{new Date(inv.issueDate).toLocaleDateString('es-CO')}</td>
                  <td className="px-4 py-3">
                    {inv.pdfUrl ? (
                      <a href={inv.pdfUrl} target="_blank" rel="noopener" className="p-1.5 text-blue-600 hover:bg-blue-50 rounded"><Download className="w-4 h-4" /></a>
                    ) : 'â€”'}
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
        {invoices.length === 0 && !loading && (
          <div className="text-center py-12 text-slate-400">
            <CreditCard className="w-12 h-12 mx-auto mb-3" />
            <p>No hay facturas registradas</p>
          </div>
        )}
        {loading && <div className="text-center py-12 text-slate-400">Cargando...</div>}
      </div>
    </div>
  );
}
