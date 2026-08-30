import { useState, useEffect } from 'react';
import { Users, TrendingUp, DollarSign, HeadphonesIcon, ArrowUpRight, ArrowDownRight, Calendar, Phone, Mail as MailIcon, FileText, CheckCircle2, Clock } from 'lucide-react';
import apiClient from '../services/api';
import { XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, PieChart, Pie, Cell, Legend, AreaChart, Area } from 'recharts';

const Dashboard = () => {
  const [stats, setStats] = useState({
    clientesActivos: 0,
    prospectos: 0,
    ventas: 0,
    cotizaciones: 0,
    pedidos: 0,
    tickets: 0
  });

  useEffect(() => {
    loadStats();
  }, []);

  const loadStats = async () => {
    try {
      const response = await apiClient.get('/api/dashboard/stats');
      setStats(response.data);
    } catch (error) {
      console.error('Error loading stats:', error);
    }
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
    { name: 'Prospección', value: 30, color: '#3b82f6' },
    { name: 'Calificación', value: 20, color: '#06b6d4' },
    { name: 'Propuesta', value: 25, color: '#8b5cf6' },
    { name: 'Negociación', value: 15, color: '#f59e0b' },
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
    { icon: FileText, iconBg: 'bg-blue-100', iconColor: 'text-blue-600', title: 'Cotización enviada', desc: 'Cliente XYZ - #COT-00125', time: 'Hace 22 min' },
    { icon: DollarSign, iconBg: 'bg-violet-100', iconColor: 'text-violet-600', title: 'Pago recibido', desc: 'Factura #FAC-00845 - $8,200', time: 'Hace 1 hora' },
    { icon: CheckCircle2, iconBg: 'bg-cyan-100', iconColor: 'text-cyan-600', title: 'Ticket resuelto', desc: 'Mesa de Ayuda #890', time: 'Hace 2 horas' },
  ];

  const proximasActividades = [
    { icon: Phone, iconBg: 'bg-blue-100', iconColor: 'text-blue-600', title: 'Llamada de seguimiento', desc: 'Empresa ABC', time: 'Hoy 10:00 AM' },
    { icon: Calendar, iconBg: 'bg-emerald-100', iconColor: 'text-emerald-600', title: 'Reunión con cliente', desc: 'Cliente XYZ', time: 'Hoy 3:00 PM' },
    { icon: MailIcon, iconBg: 'bg-violet-100', iconColor: 'text-violet-600', title: 'Enviar propuesta', desc: 'Empresa 123', time: 'Mañana 9:00 AM' },
    { icon: Clock, iconBg: 'bg-amber-100', iconColor: 'text-amber-600', title: 'Renovación de contrato', desc: 'Empresa DEF', time: 'Viernes 11:00 AM' },
  ];
  
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

      {/* Gráficos */}
      <div className="grid grid-cols-1 lg:grid-cols-5 gap-4">
        {/* Ventas por mes */}
        <div className="lg:col-span-3 bg-white rounded-2xl p-5 shadow-sm border border-slate-200">
          <div className="flex items-center justify-between mb-4">
            <div>
              <h2 className="text-base font-bold text-slate-900">Ventas por Mes</h2>
              <p className="text-xs text-slate-400 mt-0.5">Ingresos mensuales del año actual</p>
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
        
        {/* Próximas Actividades */}
        <div className="bg-white rounded-2xl p-5 shadow-sm border border-slate-200">
          <div className="flex items-center justify-between mb-4">
            <h2 className="text-base font-bold text-slate-900">Próximas Actividades</h2>
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
