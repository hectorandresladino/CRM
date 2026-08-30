/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
import { useState, useEffect } from 'react';
import { Users, TrendingUp, DollarSign, HeadphonesIcon, ArrowUpRight, ArrowDownRight, Calendar, Phone, Mail as MailIcon, FileText, CheckCircle2, Clock, Gauge, Sparkles, AlertTriangle, RefreshCw } from 'lucide-react';
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

interface OutcomeScorecard {
  outcomeScore: number;
  calculationType: string;
  formulaVersion: string;
  period: string;
  currency: string;
  metrics: OutcomeMetrics;
  recommendedActions: RecommendedAction[];
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

  useEffect(() => {
    loadStats();
    loadOutcomes();
  }, []);

  const loadStats = async () => {
    try {
      const response = await apiClient.get('/api/dashboard/stats');
      setStats(response.data);
    } catch (error) {
      console.error('Error loading stats:', error);
    }
  };

  const loadOutcomes = async () => {
    setOutcomesLoading(true);
    setOutcomesError(false);
    try {
      const response = await apiClient.get<OutcomeScorecard>('/api/v1/outcomes/scorecard');
      setOutcomes(response.data);
    } catch (error) {
      console.error('Error loading outcome scorecard:', error);
      setOutcomesError(true);
    } finally {
      setOutcomesLoading(false);
    }
  };

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

  const monthlyData = [
    { month: 'Ene', ventas: 42000 },
    { month: 'Feb', ventas: 38500 },
    { month: 'Mar', ventas: 55200 },
    { month: 'Abr', ventas: 48900 },
    { month: 'May', ventas: 61300 },
    { month: 'Jun', ventas: 58700 },
    { month: 'Jul', ventas: 72400 },
    { month: 'Ago', ventas: 68100 },
    { month: 'Sep', ventas: 79800 },
    { month: 'Oct', ventas: 74500 },
    { month: 'Nov', ventas: 82300 },
    { month: 'Dic', ventas: 85420 },
  ];

  const pipelineData = [
    { name: 'ProspecciÃ³n', value: 30, color: '#3b82f6' },
    { name: 'CalificaciÃ³n', value: 20, color: '#06b6d4' },
    { name: 'Propuesta', value: 25, color: '#8b5cf6' },
    { name: 'NegociaciÃ³n', value: 15, color: '#f59e0b' },
    { name: 'Cierre', value: 10, color: '#10b981' },
  ];

  const topStats = [
    { 
      label: 'Clientes Totales', 
      value: stats.clientesActivos > 0 ? stats.clientesActivos.toLocaleString() : '2,543',
      icon: Users, 
      iconBg: 'bg-emerald-500/10',
      iconColor: 'text-emerald-600',
      trend: '+8.5%',
      trendUp: true,
      trendLabel: 'vs mes anterior'
    },
    { 
      label: 'Oportunidades', 
      value: stats.prospectos > 0 ? stats.prospectos.toLocaleString() : '156',
      icon: TrendingUp, 
      iconBg: 'bg-blue-500/10',
      iconColor: 'text-blue-600',
      trend: '+5.2%',
      trendUp: true,
      trendLabel: 'vs mes anterior'
    },
    { 
      label: 'Ventas del Mes', 
      value: '$85,420',
      icon: DollarSign, 
      iconBg: 'bg-violet-500/10',
      iconColor: 'text-violet-600',
      trend: '+15.3%',
      trendUp: true,
      trendLabel: 'vs mes anterior'
    },
    { 
      label: 'Tickets Abiertos', 
      value: stats.tickets > 0 ? stats.tickets.toLocaleString() : '23',
      icon: HeadphonesIcon, 
      iconBg: 'bg-amber-500/10',
      iconColor: 'text-amber-600',
      trend: '-2.1%',
      trendUp: false,
      trendLabel: 'vs mes anterior'
    },
  ];

  const actividadesRecientes = [
    { icon: Users, iconBg: 'bg-emerald-100', iconColor: 'text-emerald-600', title: 'Nueva oportunidad creada', desc: 'Empresa ABC - $12,500', time: 'Hace 5 min' },
    { icon: FileText, iconBg: 'bg-blue-100', iconColor: 'text-blue-600', title: 'CotizaciÃ³n enviada', desc: 'Cliente XYZ - #COT-00125', time: 'Hace 22 min' },
    { icon: DollarSign, iconBg: 'bg-violet-100', iconColor: 'text-violet-600', title: 'Pago recibido', desc: 'Factura #FAC-00845 - $8,200', time: 'Hace 1 hora' },
    { icon: CheckCircle2, iconBg: 'bg-cyan-100', iconColor: 'text-cyan-600', title: 'Ticket resuelto', desc: 'Mesa de Ayuda #890', time: 'Hace 2 horas' },
  ];

  const proximasActividades = [
    { icon: Phone, iconBg: 'bg-blue-100', iconColor: 'text-blue-600', title: 'Llamada de seguimiento', desc: 'Empresa ABC', time: 'Hoy 10:00 AM' },
    { icon: Calendar, iconBg: 'bg-emerald-100', iconColor: 'text-emerald-600', title: 'ReuniÃ³n con cliente', desc: 'Cliente XYZ', time: 'Hoy 3:00 PM' },
    { icon: MailIcon, iconBg: 'bg-violet-100', iconColor: 'text-violet-600', title: 'Enviar propuesta', desc: 'Empresa 123', time: 'MaÃ±ana 9:00 AM' },
    { icon: Clock, iconBg: 'bg-amber-100', iconColor: 'text-amber-600', title: 'RenovaciÃ³n de contrato', desc: 'Empresa DEF', time: 'Viernes 11:00 AM' },
  ];
  
  return (
    <div className="p-4 lg:p-6 space-y-6">
      {/* Encabezado */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3">
        <div>
          <h1 className="text-2xl font-bold text-slate-900">Dashboard</h1>
          <p className="text-slate-500 text-sm mt-0.5">VisiÃ³n general de tu negocio en tiempo real</p>
        </div>
        <div className="flex items-center gap-2 text-sm text-slate-600 bg-white border border-slate-200 px-4 py-2 rounded-lg shadow-sm">
          <Calendar className="w-4 h-4 text-blue-600" />
          <span className="font-medium">{new Date().toLocaleDateString('es-ES', { year: 'numeric', month: 'long', day: 'numeric' })}</span>
        </div>
      </div>

      {/* Tarjetas de mÃ©tricas */}
      <div className="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-4 gap-4">
        {topStats.map((stat) => {
          const Icon = stat.icon;
          return (
            <div key={stat.label} className="bg-white rounded-2xl p-5 shadow-sm border border-slate-200 hover:shadow-md hover:border-blue-200 transition-all">
              <div className="flex items-start justify-between">
                <div className={`p-2.5 rounded-xl ${stat.iconBg}`}>
                  <Icon className={`w-5 h-5 ${stat.iconColor}`} />
                </div>
                <div className={`flex items-center gap-0.5 text-xs font-bold px-2 py-1 rounded-full ${stat.trendUp ? 'text-emerald-700 bg-emerald-50' : 'text-red-700 bg-red-50'}`}>
                  {stat.trendUp ? <ArrowUpRight className="w-3 h-3" /> : <ArrowDownRight className="w-3 h-3" />}
                  {stat.trend}
                </div>
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
          <button
            type="button"
            onClick={loadOutcomes}
            disabled={outcomesLoading}
            className="inline-flex items-center justify-center gap-2 self-start rounded-lg bg-white/10 px-3 py-2 text-xs font-semibold ring-1 ring-white/20 transition hover:bg-white/20 disabled:cursor-not-allowed disabled:opacity-60 sm:self-auto"
          >
            <RefreshCw className={`h-3.5 w-3.5 ${outcomesLoading ? 'animate-spin' : ''}`} />
            Actualizar análisis
          </button>
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
                    <p className="mt-1 flex items-center gap-1 text-[11px] text-slate-400"><Gauge className="h-3 w-3" /> Fórmula explicable {outcomes.formulaVersion}</p>
                  </>
                );
              })()}
            </div>

            <div>
              <h3 className="mb-3 text-xs font-bold uppercase tracking-wider text-slate-500">Métricas determinantes</h3>
              <div className="grid grid-cols-2 gap-3">
                {[
                  { label: 'Ingresos cerrados', value: formatCurrency(outcomes.metrics.revenue, outcomes.currency) },
                  { label: 'Pipeline abierto', value: formatCurrency(outcomes.metrics.openPipeline, outcomes.currency) },
                  { label: 'Cobertura pipeline', value: `${outcomes.metrics.pipelineCoverage.toFixed(2)}x` },
                  { label: 'Tasa de cierre', value: `${outcomes.metrics.winRate.toFixed(1)}%` },
                  { label: 'Conversión de leads', value: `${outcomes.metrics.leadConversionRate.toFixed(1)}%` },
                  { label: 'Resolución de soporte', value: `${outcomes.metrics.serviceResolutionRate.toFixed(1)}%` },
                ].map((metric) => (
                  <div key={metric.label} className="rounded-xl border border-slate-200 p-3">
                    <p className="truncate text-[11px] text-slate-500" title={metric.label}>{metric.label}</p>
                    <p className="mt-1 truncate text-sm font-bold text-slate-900" title={metric.value}>{metric.value}</p>
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

      {/* GrÃ¡ficos */}
      <div className="grid grid-cols-1 lg:grid-cols-5 gap-4">
        {/* Ventas por mes */}
        <div className="lg:col-span-3 bg-white rounded-2xl p-5 shadow-sm border border-slate-200">
          <div className="flex items-center justify-between mb-4">
            <div>
              <h2 className="text-base font-bold text-slate-900">Ventas por Mes</h2>
              <p className="text-xs text-slate-400 mt-0.5">Ingresos mensuales del aÃ±o actual</p>
            </div>
            <span className="text-xs font-semibold text-emerald-700 bg-emerald-50 px-2.5 py-1 rounded-full">+15.3% anual</span>
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
            <p className="text-xs text-slate-400 mt-0.5">DistribuciÃ³n del pipeline de ventas</p>
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
                formatter={(value, name) => [`${value}%`, String(name)]}
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
            {actividadesRecientes.map((item, index) => {
              const Icon = item.icon;
              return (
                <div key={index} className="flex items-center gap-3 p-3 rounded-xl hover:bg-slate-50 transition-colors">
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
        
        {/* PrÃ³ximas Actividades */}
        <div className="bg-white rounded-2xl p-5 shadow-sm border border-slate-200">
          <div className="flex items-center justify-between mb-4">
            <h2 className="text-base font-bold text-slate-900">PrÃ³ximas Actividades</h2>
            <button className="text-xs font-semibold text-blue-600 hover:text-blue-800">Ver agenda</button>
          </div>
          <div className="space-y-1">
            {proximasActividades.map((item, index) => {
              const Icon = item.icon;
              return (
                <div key={index} className="flex items-center gap-3 p-3 rounded-xl hover:bg-slate-50 transition-colors">
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
