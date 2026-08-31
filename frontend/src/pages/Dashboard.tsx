/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
import { useState, useEffect, useCallback } from 'react';
import { Users, TrendingUp, DollarSign, HeadphonesIcon, ArrowUpRight, ArrowDownRight, Calendar, Phone, Mail as MailIcon, CheckCircle2, Gauge, Sparkles, AlertTriangle, RefreshCw } from 'lucide-react';
import apiClient from '../services/api';
import { XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, PieChart, Pie, Cell, Legend, AreaChart, Area } from 'recharts';

interface OutcomeMetrics {
  revenue: number;
  openPipeline: number;
  pipelineCoverage: number;
  winRate: number;
  leadConversionRate: number;
  activeClientRate: number;
  serviceResolutionRate: number;
  wonDeals: number;
  lostDeals: number;
  openDeals: number;
  prospects: number;
  convertedProspects: number;
}

interface RecommendedAction {
  priority: 'HIGH' | 'MEDIUM' | 'LOW';
  code: string;
  message: string;
}

interface DashboardActivity {
  id: number;
  tipo: string;
  titulo: string;
  descripcion?: string;
  estado: string;
  fechaProgramada?: string;
  updatedAt?: string;
}

interface OutcomeScorecard {
  outcomeScore: number;
  calculationType: string;
  formulaVersion: string;
  period: string;
  currency: string;
  metrics: OutcomeMetrics;
  recommendedActions: RecommendedAction[];
  comparison?: {
    direction: 'IMPROVING' | 'DECLINING' | 'STABLE';
    scoreChange: number;
    revenueChangePct: number | null;
    pipelineChangePct: number | null;
    winRateChange: number;
    leadConversionChange: number;
    serviceResolutionChange: number;
    previousOutcomeScore: number;
  };
  generatedAt: string;
}

const Dashboard = () => {
  const [stats, setStats] = useState({
    clientesActivos: 0,
    prospectos: 0,
    ventas: 0,
    cotizaciones: 0,
    pedidos: 0,
    tickets: 0
  });
  const [outcomes, setOutcomes] = useState<OutcomeScorecard | null>(null);
  const [outcomesLoading, setOutcomesLoading] = useState(true);
  const [outcomesError, setOutcomesError] = useState(false);
  const [outcomePeriodDays, setOutcomePeriodDays] = useState(90);
  const [revenueByMonth, setRevenueByMonth] = useState<Record<string, number>>({});
  const [prospectsByStage, setProspectsByStage] = useState<Record<string, number>>({});
  const [dashboardError, setDashboardError] = useState(false);
  const [activities, setActivities] = useState<DashboardActivity[]>([]);

  const loadStats = async () => {
    try {
      setDashboardError(false);
      const [statsResponse, salesResponse, stagesResponse, activitiesResponse] = await Promise.all([
        apiClient.get('/api/dashboard/stats'),
        apiClient.get('/api/v1/analytics/sales'),
        apiClient.get('/api/reports/prospectos-by-stage'),
        apiClient.get<DashboardActivity[]>('/api/actividades'),
      ]);
      setStats(statsResponse.data);
      setRevenueByMonth(salesResponse.data.revenueByMonth || {});
      setProspectsByStage(stagesResponse.data || {});
      setActivities(activitiesResponse.data || []);
    } catch (error) {
      console.error('Error loading stats:', error);
      setDashboardError(true);
    }
  };

  const loadOutcomes = useCallback(async (days: number) => {
    setOutcomesLoading(true);
    setOutcomesError(false);
    try {
      const response = await apiClient.get<OutcomeScorecard>(`/api/v1/outcomes/scorecard?days=${days}`);
      setOutcomes(response.data);
    } catch (error) {
      console.error('Error loading outcome scorecard:', error);
      setOutcomesError(true);
    } finally {
      setOutcomesLoading(false);
    }
  }, []);

  useEffect(() => {
    loadStats();
  }, []);

  useEffect(() => {
    loadOutcomes(outcomePeriodDays);
  }, [loadOutcomes, outcomePeriodDays]);

  const formatCurrency = (value: number, currency: string) => {
    try {
      return new Intl.NumberFormat('es-CO', {
        style: 'currency',
        currency: currency || 'COP',
        maximumFractionDigits: 0,
      }).format(value);
    } catch {
      return `${currency || 'COP'} ${Number(value).toLocaleString('es-CO')}`;
    }
  };

  const scoreTone = (score: number) => {
    if (score >= 75) return { text: 'text-emerald-700', ring: 'stroke-emerald-500', label: 'Sólido' };
    if (score >= 50) return { text: 'text-amber-700', ring: 'stroke-amber-500', label: 'En desarrollo' };
    return { text: 'text-red-700', ring: 'stroke-red-500', label: 'Requiere atención' };
  };

  const monthlyData = Array.from({ length: 12 }, (_, index) => {
    const date = new Date();
    date.setDate(1);
    date.setMonth(date.getMonth() - (11 - index));
    const key = `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}`;
    return {
      month: new Intl.DateTimeFormat('es-CO', { month: 'short' }).format(date).replace('.', ''),
      ventas: Number(revenueByMonth[key] || 0),
    };
  });

  const pipelineData = [
    { name: 'Nuevos', value: prospectsByStage.NUEVO || 0, color: '#3b82f6' },
    { name: 'Contactados', value: prospectsByStage.CONTACTADO || 0, color: '#06b6d4' },
    { name: 'Calificados', value: prospectsByStage.CALIFICADO || 0, color: '#14b8a6' },
    { name: 'Propuesta', value: prospectsByStage.PROPUESTA || 0, color: '#8b5cf6' },
    { name: 'Negociación', value: prospectsByStage.NEGOCIACION || 0, color: '#f59e0b' },
    { name: 'Cerrados', value: prospectsByStage.CERRADO || 0, color: '#10b981' },
  ];

  const currentMonthRevenue = monthlyData[monthlyData.length - 1]?.ventas || 0;
  const revenueTrend = outcomes?.comparison?.revenueChangePct;

  const topStats = [
    { 
      label: 'Clientes Totales', 
      value: stats.clientesActivos.toLocaleString(),
      icon: Users, 
      iconBg: 'bg-emerald-500/10',
      iconColor: 'text-emerald-600',
      trend: null,
      trendUp: true,
      trendLabel: 'Datos actuales del tenant'
    },
    { 
      label: 'Oportunidades', 
      value: stats.prospectos.toLocaleString(),
      icon: TrendingUp, 
      iconBg: 'bg-blue-500/10',
      iconColor: 'text-blue-600',
      trend: null,
      trendUp: true,
      trendLabel: 'Datos actuales del tenant'
    },
    { 
      label: 'Ventas del Mes', 
      value: formatCurrency(currentMonthRevenue, outcomes?.currency || 'USD'),
      icon: DollarSign, 
      iconBg: 'bg-violet-500/10',
      iconColor: 'text-violet-600',
      trend: revenueTrend == null ? null : `${revenueTrend > 0 ? '+' : ''}${revenueTrend.toFixed(1)}%`,
      trendUp: revenueTrend == null || revenueTrend >= 0,
      trendLabel: revenueTrend == null ? 'Mes actual' : `vs periodo anterior (${outcomePeriodDays}d)`
    },
    { 
      label: 'Tickets Abiertos', 
      value: stats.tickets.toLocaleString(),
      icon: HeadphonesIcon, 
      iconBg: 'bg-amber-500/10',
      iconColor: 'text-amber-600',
      trend: null,
      trendUp: false,
      trendLabel: 'Tickets sin resolver'
    },
  ];

  const activityVisual = (type: string) => {
    if (type === 'LLAMADA') return { icon: Phone, iconBg: 'bg-blue-100', iconColor: 'text-blue-600' };
    if (type === 'EMAIL') return { icon: MailIcon, iconBg: 'bg-violet-100', iconColor: 'text-violet-600' };
    if (type === 'REUNION' || type === 'VISITA') return { icon: Calendar, iconBg: 'bg-emerald-100', iconColor: 'text-emerald-600' };
    return { icon: CheckCircle2, iconBg: 'bg-cyan-100', iconColor: 'text-cyan-600' };
  };

  const actividadesRecientes = [...activities]
    .sort((a, b) => new Date(b.updatedAt || b.fechaProgramada || 0).getTime() - new Date(a.updatedAt || a.fechaProgramada || 0).getTime())
    .slice(0, 4)
    .map((activity) => ({
      ...activityVisual(activity.tipo),
      id: activity.id,
      title: activity.titulo,
      desc: activity.descripcion || activity.tipo.replace(/_/g, ' '),
      time: activity.updatedAt ? new Date(activity.updatedAt).toLocaleString('es-CO', { day: '2-digit', month: 'short', hour: '2-digit', minute: '2-digit' }) : 'Sin fecha',
    }));

  const proximasActividades = activities
    .filter((activity) => !['COMPLETADA', 'CANCELADA'].includes(activity.estado) && activity.fechaProgramada)
    .sort((a, b) => new Date(a.fechaProgramada || 0).getTime() - new Date(b.fechaProgramada || 0).getTime())
    .slice(0, 4)
    .map((activity) => ({
      ...activityVisual(activity.tipo),
      id: activity.id,
      title: activity.titulo,
      desc: activity.descripcion || activity.tipo.replace(/_/g, ' '),
      time: new Date(activity.fechaProgramada!).toLocaleString('es-CO', { day: '2-digit', month: 'short', hour: '2-digit', minute: '2-digit' }),
    }));
  
  return (
    <div className="p-4 lg:p-6 space-y-6">
      {/* Encabezado */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3">
        <div>
          <h1 className="text-2xl font-bold text-slate-900">Dashboard</h1>
          <p className="text-slate-500 text-sm mt-0.5">Visión general de tu negocio en tiempo real</p>
        </div>
        <div className="flex items-center gap-2 text-sm text-slate-600 bg-white border border-slate-200 px-4 py-2 rounded-lg shadow-sm">
          <Calendar className="w-4 h-4 text-blue-600" />
          <span className="font-medium">{new Date().toLocaleDateString('es-ES', { year: 'numeric', month: 'long', day: 'numeric' })}</span>
        </div>
      </div>

      {dashboardError && (
        <div className="flex items-center gap-2 rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700" role="alert">
          <AlertTriangle className="h-4 w-4 shrink-0" />
          No fue posible actualizar todas las métricas. No se muestran valores de demostración.
        </div>
      )}

      {/* Tarjetas de métricas */}
      <div className="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-4 gap-4">
        {topStats.map((stat) => {
          const Icon = stat.icon;
          return (
            <div key={stat.label} className="bg-white rounded-2xl p-5 shadow-sm border border-slate-200 hover:shadow-md hover:border-blue-200 transition-all">
              <div className="flex items-start justify-between">
                <div className={`p-2.5 rounded-xl ${stat.iconBg}`}>
                  <Icon className={`w-5 h-5 ${stat.iconColor}`} />
                </div>
                {stat.trend && (
                  <div className={`flex items-center gap-0.5 text-xs font-bold px-2 py-1 rounded-full ${stat.trendUp ? 'text-emerald-700 bg-emerald-50' : 'text-red-700 bg-red-50'}`}>
                    {stat.trendUp ? <ArrowUpRight className="w-3 h-3" /> : <ArrowDownRight className="w-3 h-3" />}
                    {stat.trend}
                  </div>
                )}
              </div>
              <p className="text-3xl font-bold text-slate-900 mt-4">{stat.value}</p>
              <p className="text-sm text-slate-500 mt-1">{stat.label}</p>
              <p className="text-[11px] text-slate-400 mt-0.5">{stat.trendLabel}</p>
            </div>
          );
        })}
      </div>

      {/* Motor de resultados: métricas explicables calculadas con datos reales del tenant */}
      <section className="overflow-hidden rounded-2xl border border-indigo-200 bg-white shadow-sm" aria-labelledby="outcome-title">
        <div className="flex flex-col gap-3 border-b border-indigo-100 bg-gradient-to-r from-indigo-950 via-blue-900 to-cyan-800 px-5 py-5 text-white sm:flex-row sm:items-center sm:justify-between">
          <div className="flex items-center gap-3">
            <div className="rounded-xl bg-white/10 p-2.5 ring-1 ring-white/20">
              <Sparkles className="h-5 w-5 text-cyan-200" />
            </div>
            <div>
              <div className="flex flex-wrap items-center gap-2">
                <h2 id="outcome-title" className="text-lg font-bold">Inteligencia de Resultados</h2>
                <span className="rounded-full bg-cyan-300/15 px-2 py-0.5 text-[10px] font-bold uppercase tracking-wider text-cyan-100 ring-1 ring-cyan-200/20">Datos reales</span>
              </div>
              <p className="mt-0.5 text-xs text-blue-100/75">Una lectura explicable de ventas, conversión, clientes y servicio.</p>
            </div>
          </div>
          <div className="flex flex-wrap items-center gap-2 self-start sm:self-auto">
            <div className="flex rounded-lg bg-black/15 p-1 ring-1 ring-white/15" aria-label="Periodo de análisis">
              {[
                { days: 30, label: '30d' },
                { days: 90, label: '90d' },
                { days: 365, label: '1a' },
                { days: 0, label: 'Todo' },
              ].map((period) => (
                <button
                  key={period.days}
                  type="button"
                  onClick={() => setOutcomePeriodDays(period.days)}
                  className={`rounded-md px-2.5 py-1.5 text-[11px] font-bold transition ${outcomePeriodDays === period.days ? 'bg-white text-blue-900 shadow-sm' : 'text-blue-100 hover:bg-white/10'}`}
                  aria-pressed={outcomePeriodDays === period.days}
                >
                  {period.label}
                </button>
              ))}
            </div>
            <button
              type="button"
              onClick={() => loadOutcomes(outcomePeriodDays)}
              disabled={outcomesLoading}
              className="inline-flex items-center justify-center gap-2 rounded-lg bg-white/10 px-3 py-2 text-xs font-semibold ring-1 ring-white/20 transition hover:bg-white/20 disabled:cursor-not-allowed disabled:opacity-60"
            >
              <RefreshCw className={`h-3.5 w-3.5 ${outcomesLoading ? 'animate-spin' : ''}`} />
              Actualizar
            </button>
          </div>
        </div>

        {outcomesLoading && !outcomes ? (
          <div className="flex min-h-56 items-center justify-center gap-3 p-6 text-sm text-slate-500" role="status">
            <RefreshCw className="h-5 w-5 animate-spin text-blue-600" />
            Calculando resultados del negocio...
          </div>
        ) : outcomesError && !outcomes ? (
          <div className="m-5 flex min-h-32 flex-col items-center justify-center rounded-xl border border-red-200 bg-red-50 p-6 text-center">
            <AlertTriangle className="mb-2 h-6 w-6 text-red-600" />
            <p className="font-semibold text-red-800">No fue posible calcular los resultados.</p>
            <p className="mt-1 text-xs text-red-600">Conservamos el dashboard disponible; intenta actualizar el análisis.</p>
          </div>
        ) : outcomes ? (
          <div className="grid grid-cols-1 gap-5 p-5 xl:grid-cols-[220px_1fr_1.15fr]">
            <div className="flex flex-col items-center justify-center rounded-xl bg-slate-50 p-4 text-center ring-1 ring-slate-200">
              {(() => {
                const tone = scoreTone(outcomes.outcomeScore);
                const circumference = 2 * Math.PI * 48;
                const progress = circumference - (Math.min(100, Math.max(0, outcomes.outcomeScore)) / 100) * circumference;
                return (
                  <>
                    <div className="relative h-32 w-32">
                      <svg className="h-full w-full -rotate-90" viewBox="0 0 112 112" aria-hidden="true">
                        <circle cx="56" cy="56" r="48" fill="none" strokeWidth="9" className="stroke-slate-200" />
                        <circle cx="56" cy="56" r="48" fill="none" strokeWidth="9" strokeLinecap="round" className={tone.ring} strokeDasharray={circumference} strokeDashoffset={progress} />
                      </svg>
                      <div className="absolute inset-0 flex flex-col items-center justify-center">
                        <span className={`text-3xl font-black ${tone.text}`}>{Math.round(outcomes.outcomeScore)}</span>
                        <span className="text-[10px] font-bold uppercase tracking-wider text-slate-400">de 100</span>
                      </div>
                    </div>
                    <p className={`mt-2 text-sm font-bold ${tone.text}`}>{tone.label}</p>
                    {outcomes.comparison && (
                      <span className={`mt-2 rounded-full px-2.5 py-1 text-[10px] font-bold ${outcomes.comparison.direction === 'IMPROVING' ? 'bg-emerald-100 text-emerald-700' : outcomes.comparison.direction === 'DECLINING' ? 'bg-red-100 text-red-700' : 'bg-slate-200 text-slate-600'}`}>
                        {outcomes.comparison.scoreChange > 0 ? '+' : ''}{outcomes.comparison.scoreChange.toFixed(1)} pts vs periodo anterior
                      </span>
                    )}
                    <p className="mt-1 flex items-center gap-1 text-[11px] text-slate-400"><Gauge className="h-3 w-3" /> Fórmula explicable {outcomes.formulaVersion}</p>
                  </>
                );
              })()}
            </div>

            <div>
              <h3 className="mb-3 text-xs font-bold uppercase tracking-wider text-slate-500">Métricas determinantes</h3>
              <div className="grid grid-cols-2 gap-3">
                {[
                  { label: 'Ingresos cerrados', value: formatCurrency(outcomes.metrics.revenue, outcomes.currency), change: outcomes.comparison?.revenueChangePct },
                  { label: 'Pipeline abierto', value: formatCurrency(outcomes.metrics.openPipeline, outcomes.currency), change: outcomes.comparison?.pipelineChangePct },
                  { label: 'Cobertura pipeline', value: `${outcomes.metrics.pipelineCoverage.toFixed(2)}x` },
                  { label: 'Tasa de cierre', value: `${outcomes.metrics.winRate.toFixed(1)}%`, change: outcomes.comparison?.winRateChange },
                  { label: 'Conversión de leads', value: `${outcomes.metrics.leadConversionRate.toFixed(1)}%`, change: outcomes.comparison?.leadConversionChange },
                  { label: 'Resolución de soporte', value: `${outcomes.metrics.serviceResolutionRate.toFixed(1)}%`, change: outcomes.comparison?.serviceResolutionChange },
                ].map((metric) => (
                  <div key={metric.label} className="rounded-xl border border-slate-200 p-3">
                    <p className="truncate text-[11px] text-slate-500" title={metric.label}>{metric.label}</p>
                    <div className="mt-1 flex items-end justify-between gap-1">
                      <p className="truncate text-sm font-bold text-slate-900" title={metric.value}>{metric.value}</p>
                      {metric.change != null && (
                        <span className={`shrink-0 text-[10px] font-bold ${metric.change > 0 ? 'text-emerald-600' : metric.change < 0 ? 'text-red-600' : 'text-slate-400'}`}>
                          {metric.change > 0 ? '+' : ''}{metric.change.toFixed(1)}{metric.label.includes('Ingresos') || metric.label.includes('Pipeline abierto') ? '%' : ' pts'}
                        </span>
                      )}
                    </div>
                  </div>
                ))}
              </div>
            </div>

            <div>
              <h3 className="mb-3 text-xs font-bold uppercase tracking-wider text-slate-500">Acciones prioritarias</h3>
              <div className="space-y-2">
                {outcomes.recommendedActions.map((action) => {
                  const priorityStyle = action.priority === 'HIGH'
                    ? 'bg-red-50 text-red-700 ring-red-200'
                    : action.priority === 'MEDIUM'
                      ? 'bg-amber-50 text-amber-700 ring-amber-200'
                      : 'bg-emerald-50 text-emerald-700 ring-emerald-200';
                  const priorityLabel = action.priority === 'HIGH' ? 'Alta' : action.priority === 'MEDIUM' ? 'Media' : 'Baja';
                  return (
                    <div key={action.code} className="flex items-start gap-3 rounded-xl border border-slate-200 p-3.5">
                      <CheckCircle2 className="mt-0.5 h-4 w-4 shrink-0 text-blue-600" />
                      <div className="min-w-0 flex-1">
                        <p className="text-sm font-medium leading-5 text-slate-700">{action.message}</p>
                        <span className={`mt-2 inline-flex rounded-full px-2 py-0.5 text-[10px] font-bold uppercase ring-1 ${priorityStyle}`}>Prioridad {priorityLabel}</span>
                      </div>
                    </div>
                  );
                })}
              </div>
              <p className="mt-3 text-[10px] text-slate-400">Periodo: histórico completo · Calculado {new Date(outcomes.generatedAt).toLocaleString('es-CO')}</p>
            </div>
          </div>
        ) : null}
      </section>

      {/* Gráficos */}
      <div className="grid grid-cols-1 lg:grid-cols-5 gap-4">
        {/* Ventas por mes */}
        <div className="lg:col-span-3 bg-white rounded-2xl p-5 shadow-sm border border-slate-200">
          <div className="flex items-center justify-between mb-4">
            <div>
              <h2 className="text-base font-bold text-slate-900">Ventas por Mes</h2>
              <p className="text-xs text-slate-400 mt-0.5">Ingresos reales de los últimos 12 meses</p>
            </div>
            <span className="text-xs font-semibold text-blue-700 bg-blue-50 px-2.5 py-1 rounded-full">Datos del tenant</span>
          </div>
          <ResponsiveContainer width="100%" height={280}>
            <AreaChart data={monthlyData}>
              <defs>
                <linearGradient id="ventasGradient" x1="0" y1="0" x2="0" y2="1">
                  <stop offset="0%" stopColor="#3b82f6" stopOpacity={0.25} />
                  <stop offset="100%" stopColor="#3b82f6" stopOpacity={0} />
                </linearGradient>
              </defs>
              <CartesianGrid strokeDasharray="3 3" stroke="#e2e8f0" vertical={false} />
              <XAxis dataKey="month" tick={{ fontSize: 12, fill: '#94a3b8' }} axisLine={false} tickLine={false} />
              <YAxis tick={{ fontSize: 12, fill: '#94a3b8' }} axisLine={false} tickLine={false} tickFormatter={(v) => `$${(v / 1000).toFixed(0)}k`} />
              <Tooltip 
                formatter={(value) => [`$${Number(value).toLocaleString()}`, 'Ventas']}
                contentStyle={{ borderRadius: '12px', border: '1px solid #e2e8f0', boxShadow: '0 4px 12px rgba(0,0,0,0.08)' }}
              />
              <Area type="monotone" dataKey="ventas" stroke="#3b82f6" strokeWidth={2.5} fill="url(#ventasGradient)" dot={{ r: 3, fill: '#3b82f6' }} activeDot={{ r: 5 }} />
            </AreaChart>
          </ResponsiveContainer>
        </div>
        
        {/* Oportunidades por etapa */}
        <div className="lg:col-span-2 bg-white rounded-2xl p-5 shadow-sm border border-slate-200">
          <div className="mb-4">
            <h2 className="text-base font-bold text-slate-900">Oportunidades por Etapa</h2>
            <p className="text-xs text-slate-400 mt-0.5">Distribución del pipeline de ventas</p>
          </div>
          <ResponsiveContainer width="100%" height={280}>
            <PieChart>
              <Pie
                data={pipelineData}
                cx="50%"
                cy="45%"
                innerRadius={60}
                outerRadius={90}
                paddingAngle={3}
                dataKey="value"
              >
                {pipelineData.map((entry, index) => (
                  <Cell key={`cell-${index}`} fill={entry.color} />
                ))}
              </Pie>
              <Tooltip 
                formatter={(value, name) => [`${value} prospectos`, String(name)]}
                contentStyle={{ borderRadius: '12px', border: '1px solid #e2e8f0', boxShadow: '0 4px 12px rgba(0,0,0,0.08)' }}
              />
              <Legend 
                verticalAlign="bottom" 
                iconType="circle" 
                iconSize={8}
                formatter={(value) => <span className="text-xs text-slate-600">{value}</span>}
              />
            </PieChart>
          </ResponsiveContainer>
        </div>
      </div>

      {/* Actividades */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-4">
        {/* Actividades Recientes */}
        <div className="bg-white rounded-2xl p-5 shadow-sm border border-slate-200">
          <div className="flex items-center justify-between mb-4">
            <h2 className="text-base font-bold text-slate-900">Actividades Recientes</h2>
            <button className="text-xs font-semibold text-blue-600 hover:text-blue-800">Ver todas</button>
          </div>
          <div className="space-y-1">
            {actividadesRecientes.length === 0 && <p className="py-8 text-center text-sm text-slate-400">No hay actividades registradas.</p>}
            {actividadesRecientes.map((item, index) => {
              const Icon = item.icon;
              return (
                <div key={item.id || index} className="flex items-center gap-3 p-3 rounded-xl hover:bg-slate-50 transition-colors">
                  <div className={`p-2 rounded-lg ${item.iconBg} shrink-0`}>
                    <Icon className={`w-4 h-4 ${item.iconColor}`} />
                  </div>
                  <div className="min-w-0 flex-1">
                    <p className="text-sm font-semibold text-slate-800 truncate">{item.title}</p>
                    <p className="text-xs text-slate-500 truncate">{item.desc}</p>
                  </div>
                  <span className="text-[11px] text-slate-400 shrink-0">{item.time}</span>
                </div>
              );
            })}
          </div>
        </div>
        
        {/* Próximas Actividades */}
        <div className="bg-white rounded-2xl p-5 shadow-sm border border-slate-200">
          <div className="flex items-center justify-between mb-4">
            <h2 className="text-base font-bold text-slate-900">Próximas Actividades</h2>
            <button className="text-xs font-semibold text-blue-600 hover:text-blue-800">Ver agenda</button>
          </div>
          <div className="space-y-1">
            {proximasActividades.length === 0 && <p className="py-8 text-center text-sm text-slate-400">No hay actividades próximas.</p>}
            {proximasActividades.map((item, index) => {
              const Icon = item.icon;
              return (
                <div key={item.id || index} className="flex items-center gap-3 p-3 rounded-xl hover:bg-slate-50 transition-colors">
                  <div className={`p-2 rounded-lg ${item.iconBg} shrink-0`}>
                    <Icon className={`w-4 h-4 ${item.iconColor}`} />
                  </div>
                  <div className="min-w-0 flex-1">
                    <p className="text-sm font-semibold text-slate-800 truncate">{item.title}</p>
                    <p className="text-xs text-slate-500 truncate">{item.desc}</p>
                  </div>
                  <span className="text-[11px] font-medium text-blue-600 bg-blue-50 px-2 py-1 rounded-md shrink-0">{item.time}</span>
                </div>
              );
            })}
          </div>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;
