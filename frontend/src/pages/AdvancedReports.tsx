import { useState, useEffect } from 'react';
import { 
  BarChart3, TrendingUp, DollarSign, 
  Users, Target, Filter, Download, PieChart
} from 'lucide-react';
import apiClient from '../services/api';

interface SalesSummary {
  totalClosed: number;
  totalVentas: number;
  totalClientes: number;
  totalProspectos: number;
  totalCotizaciones: number;
  totalPedidos: number;
}

interface Forecast {
  closedValue: number;
  pendingValue: number;
  inProcessValue: number;
  weightedForecast: number;
  bestCase: number;
  worstCase: number;
}

interface Funnel {
  nuevos: number;
  contactados: number;
  calificados: number;
  propuesta: number;
  negociacion: number;
  cerrados: number;
  perdidos: number;
  total: number;
  conversionRate: number;
}

export default function AdvancedReports() {
  const [summary, setSummary] = useState<SalesSummary | null>(null);
  const [salesByStatus, setSalesByStatus] = useState<Record<string, number>>({});
  const [prospectosByStage, setProspectosByStage] = useState<Record<string, number>>({});
  const [forecast, setForecast] = useState<Forecast | null>(null);
  const [funnel, setFunnel] = useState<Funnel | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const load = async () => {
      try {
        const [sumRes, statusRes, stageRes, fcRes, fnRes] = await Promise.all([
          apiClient.get('/api/reports/sales-summary'),
          apiClient.get('/api/reports/sales-by-status'),
          apiClient.get('/api/reports/prospectos-by-stage'),
          apiClient.get('/api/reports/forecasting'),
          apiClient.get('/api/reports/conversion-funnel'),
        ]);
        setSummary(sumRes.data);
        setSalesByStatus(statusRes.data);
        setProspectosByStage(stageRes.data);
        setForecast(fcRes.data);
        setFunnel(fnRes.data);
      } catch (e) {
        console.error('Error loading reports:', e);
      } finally {
        setLoading(false);
      }
    };
    load();
  }, []);

  const fmt = (v: number) => new Intl.NumberFormat('es-CO', { style: 'currency', currency: 'USD', minimumFractionDigits: 0 }).format(v || 0);

  const funnelStages = [
    { key: 'nuevos', label: 'Nuevos', color: 'bg-blue-500' },
    { key: 'contactados', label: 'Contactados', color: 'bg-cyan-500' },
    { key: 'calificados', label: 'Calificados', color: 'bg-teal-500' },
    { key: 'propuesta', label: 'Propuesta', color: 'bg-purple-500' },
    { key: 'negociacion', label: 'Negociación', color: 'bg-orange-500' },
    { key: 'cerrados', label: 'Cerrados', color: 'bg-green-500' },
  ];

  const maxFunnel = funnel ? Math.max(...funnelStages.map(s => (funnel as any)[s.key] || 0), 1) : 1;

  if (loading) return <div className="p-6 text-center text-slate-400">Cargando reportes...</div>;

  return (
    <div className="p-6 space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-slate-900 flex items-center gap-2">
            <BarChart3 className="w-7 h-7 text-blue-600" />
            Reportes & Analytics
          </h1>
          <p className="text-sm text-slate-500 mt-1">Dashboards avanzados con forecasting y embudo de conversión</p>
        </div>
        <button className="flex items-center gap-2 px-4 py-2 text-sm font-medium text-slate-600 bg-white border border-slate-200 rounded-lg hover:bg-slate-50">
          <Download className="w-4 h-4" /> Exportar
        </button>
      </div>

      {/* KPI Cards */}
      {summary && (
        <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-4">
          {[
            { label: 'Ventas cerradas', value: fmt(summary.totalClosed), icon: DollarSign, color: 'text-green-600', bg: 'bg-green-50' },
            { label: 'Total ventas', value: summary.totalVentas, icon: TrendingUp, color: 'text-blue-600', bg: 'bg-blue-50' },
            { label: 'Clientes', value: summary.totalClientes, icon: Users, color: 'text-purple-600', bg: 'bg-purple-50' },
            { label: 'Prospectos', value: summary.totalProspectos, icon: Target, color: 'text-orange-600', bg: 'bg-orange-50' },
            { label: 'Cotizaciones', value: summary.totalCotizaciones, icon: BarChart3, color: 'text-cyan-600', bg: 'bg-cyan-50' },
            { label: 'Pedidos', value: summary.totalPedidos, icon: PieChart, color: 'text-indigo-600', bg: 'bg-indigo-50' },
          ].map((kpi, i) => {
            const Icon = kpi.icon;
            return (
              <div key={i} className="bg-white rounded-xl border border-slate-200 p-4">
                <div className={`p-2 rounded-lg ${kpi.bg} w-fit mb-2`}>
                  <Icon className={`w-5 h-5 ${kpi.color}`} />
                </div>
                <p className="text-xl font-bold text-slate-900">{kpi.value}</p>
                <p className="text-xs text-slate-500">{kpi.label}</p>
              </div>
            );
          })}
        </div>
      )}

      {/* Forecasting */}
      {forecast && (
        <div className="bg-white rounded-xl border border-slate-200 p-6">
          <h2 className="text-lg font-semibold text-slate-900 mb-4 flex items-center gap-2">
            <TrendingUp className="w-5 h-5 text-blue-600" /> Forecasting de Ventas
          </h2>
          <div className="grid grid-cols-1 md:grid-cols-5 gap-4">
            <div className="p-4 bg-green-50 rounded-lg">
              <p className="text-xs text-slate-500 mb-1">Cerrado</p>
              <p className="text-lg font-bold text-green-700">{fmt(forecast.closedValue)}</p>
            </div>
            <div className="p-4 bg-yellow-50 rounded-lg">
              <p className="text-xs text-slate-500 mb-1">Pendiente</p>
              <p className="text-lg font-bold text-yellow-700">{fmt(forecast.pendingValue)}</p>
            </div>
            <div className="p-4 bg-blue-50 rounded-lg">
              <p className="text-xs text-slate-500 mb-1">En proceso</p>
              <p className="text-lg font-bold text-blue-700">{fmt(forecast.inProcessValue)}</p>
            </div>
            <div className="p-4 bg-purple-50 rounded-lg border-2 border-purple-200">
              <p className="text-xs text-slate-500 mb-1">Forecast ponderado</p>
              <p className="text-lg font-bold text-purple-700">{fmt(forecast.weightedForecast)}</p>
            </div>
            <div className="p-4 bg-slate-50 rounded-lg">
              <p className="text-xs text-slate-500 mb-1">Mejor caso</p>
              <p className="text-lg font-bold text-slate-700">{fmt(forecast.bestCase)}</p>
              <p className="text-xs text-slate-400 mt-1">Peor: {fmt(forecast.worstCase)}</p>
            </div>
          </div>
        </div>
      )}

      {/* Conversion Funnel */}
      {funnel && (
        <div className="bg-white rounded-xl border border-slate-200 p-6">
          <h2 className="text-lg font-semibold text-slate-900 mb-4 flex items-center gap-2">
            <Filter className="w-5 h-5 text-orange-600" /> Embudo de Conversión
          </h2>
          <div className="space-y-3">
            {funnelStages.map((stage) => {
              const value = (funnel as any)[stage.key] || 0;
              const pct = (value / maxFunnel) * 100;
              const conversionPct = funnel.total > 0 ? ((funnel.cerrados / value) * 100).toFixed(1) : '0';
              return (
                <div key={stage.key} className="flex items-center gap-4">
                  <div className="w-28 text-sm font-medium text-slate-700 text-right">{stage.label}</div>
                  <div className="flex-1 bg-slate-100 rounded-lg h-8 relative overflow-hidden">
                    <div className={`h-full ${stage.color} rounded-lg flex items-center px-3 transition-all`} style={{ width: `${Math.max(pct, 5)}%` }}>
                      <span className="text-xs font-semibold text-white">{value}</span>
                    </div>
                  </div>
                  <div className="w-20 text-xs text-slate-500">{conversionPct}% conversión</div>
                </div>
              );
            })}
          </div>
          <div className="mt-4 flex items-center justify-between pt-4 border-t border-slate-100">
            <div className="text-sm text-slate-600">
              <span className="font-semibold text-slate-900">{funnel.cerrados}</span> cerrados de <span className="font-semibold text-slate-900">{funnel.total}</span> prospectos
            </div>
            <div className="text-sm">
              <span className="text-slate-500">Tasa de conversión: </span>
              <span className="font-bold text-green-600">{funnel.conversionRate.toFixed(1)}%</span>
            </div>
          </div>
        </div>
      )}

      {/* Sales by Status & Prospectos by Stage */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div className="bg-white rounded-xl border border-slate-200 p-6">
          <h2 className="text-lg font-semibold text-slate-900 mb-4">Ventas por Estado</h2>
          <div className="space-y-3">
            {Object.entries(salesByStatus).map(([status, count]) => {
              const total = Object.values(salesByStatus).reduce((a, b) => a + b, 0) || 1;
              const pct = (count / total) * 100;
              const colors: Record<string, string> = { PENDIENTE: 'bg-blue-500', EN_PROCESO: 'bg-yellow-500', CERRADA: 'bg-green-500', CANCELADA: 'bg-red-500' };
              return (
                <div key={status}>
                  <div className="flex justify-between text-sm mb-1">
                    <span className="text-slate-700">{status.replace(/_/g, ' ')}</span>
                    <span className="font-medium text-slate-900">{count}</span>
                  </div>
                  <div className="bg-slate-100 rounded h-2">
                    <div className={`h-2 rounded ${colors[status] || 'bg-slate-400'}`} style={{ width: `${pct}%` }} />
                  </div>
                </div>
              );
            })}
          </div>
        </div>

        <div className="bg-white rounded-xl border border-slate-200 p-6">
          <h2 className="text-lg font-semibold text-slate-900 mb-4">Prospectos por Etapa</h2>
          <div className="space-y-3">
            {Object.entries(prospectosByStage).map(([stage, count]) => {
              const total = Object.values(prospectosByStage).reduce((a, b) => a + b, 0) || 1;
              const pct = (count / total) * 100;
              const colors: Record<string, string> = { NUEVO: 'bg-blue-500', CONTACTADO: 'bg-yellow-500', CALIFICADO: 'bg-cyan-500', PROPUESTA: 'bg-purple-500', NEGOCIACION: 'bg-orange-500', CERRADO: 'bg-green-500', PERDIDO: 'bg-red-500' };
              return (
                <div key={stage}>
                  <div className="flex justify-between text-sm mb-1">
                    <span className="text-slate-700">{stage.replace(/_/g, ' ')}</span>
                    <span className="font-medium text-slate-900">{count}</span>
                  </div>
                  <div className="bg-slate-100 rounded h-2">
                    <div className={`h-2 rounded ${colors[stage] || 'bg-slate-400'}`} style={{ width: `${pct}%` }} />
                  </div>
                </div>
              );
            })}
          </div>
        </div>
      </div>
    </div>
  );
}
