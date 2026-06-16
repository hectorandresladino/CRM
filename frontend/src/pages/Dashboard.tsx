import { useState, useEffect } from 'react';
import { Users, UserPlus, DollarSign, FileText, Package, HeadphonesIcon, ArrowUpRight, ArrowDownRight, Activity, Calendar, TrendingUp } from 'lucide-react';
import apiClient from '../services/api';
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, LineChart, Line, PieChart, Pie, Cell, Legend } from 'recharts';

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
    { month: 'Ene', ventas: 4000, clientes: 240 },
    { month: 'Feb', ventas: 3000, clientes: 139 },
    { month: 'Mar', ventas: 2000, clientes: 980 },
    { month: 'Abr', ventas: 2780, clientes: 390 },
    { month: 'May', ventas: 1890, clientes: 480 },
    { month: 'Jun', ventas: 2390, clientes: 380 },
  ];

  const pieData = [
    { name: 'Clientes', value: stats.clientesActivos, color: '#3b82f6' },
    { name: 'Prospectos', value: stats.prospectos, color: '#10b981' },
    { name: 'Ventas', value: stats.ventas, color: '#8b5cf6' },
    { name: 'Cotizaciones', value: stats.cotizaciones, color: '#f59e0b' },
  ];

  const topStats = [
    { 
      label: 'Ingresos Totales', 
      value: '$125,430', 
      icon: DollarSign, 
      color: 'text-emerald-600',
      bgColor: 'bg-emerald-100',
      trend: '+24.5%',
      trendUp: true
    },
    { 
      label: 'Nuevos Clientes', 
      value: stats.clientesActivos.toString(), 
      icon: Users, 
      color: 'text-blue-600',
      bgColor: 'bg-blue-100',
      trend: '+12%',
      trendUp: true
    },
    { 
      label: 'Ventas', 
      value: stats.ventas.toString(), 
      icon: TrendingUp, 
      color: 'text-purple-600',
      bgColor: 'bg-purple-100',
      trend: '+18%',
      trendUp: true
    },
  ];

  const secondaryStats = [
    { 
      label: 'Prospectos', 
      value: stats.prospectos, 
      icon: UserPlus, 
      color: 'from-orange-400 to-orange-500'
    },
    { 
      label: 'Cotizaciones', 
      value: stats.cotizaciones, 
      icon: FileText, 
      color: 'from-pink-400 to-pink-500'
    },
    { 
      label: 'Pedidos', 
      value: stats.pedidos, 
      icon: Package, 
      color: 'from-cyan-400 to-cyan-500'
    },
    { 
      label: 'Tickets', 
      value: stats.tickets, 
      icon: HeadphonesIcon, 
      color: 'from-rose-400 to-rose-500'
    },
  ];
  
  return (
    <div className="bg-gray-50 min-h-screen">
      <div className="bg-white border-b border-gray-200 px-8 py-6">
        <div className="flex items-center justify-between">
          <div>
            <h1 className="text-2xl font-bold text-gray-900">Dashboard Financiero</h1>
            <p className="text-gray-500 text-sm mt-1">Visión general de tu negocio</p>
          </div>
          <div className="flex items-center space-x-4">
            <div className="flex items-center space-x-2 text-sm text-gray-500 bg-gray-100 px-4 py-2 rounded-lg">
              <Calendar className="w-4 h-4" />
              <span>{new Date().toLocaleDateString('es-ES', { year: 'numeric', month: 'long', day: 'numeric' })}</span>
            </div>
          </div>
        </div>
      </div>

      <div className="p-8">
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
          {topStats.map((stat) => {
            const Icon = stat.icon;
            return (
              <div key={stat.label} className="bg-white rounded-2xl p-6 shadow-sm border border-gray-100 hover:shadow-md transition-shadow">
                <div className="flex items-center justify-between mb-4">
                  <div className={`p-3 rounded-xl ${stat.bgColor}`}>
                    <Icon className={`w-6 h-6 ${stat.color}`} />
                  </div>
                  <div className="flex items-center">
                    {stat.trendUp ? (
                      <ArrowUpRight className="w-4 h-4 text-emerald-500 mr-1" />
                    ) : (
                      <ArrowDownRight className="w-4 h-4 text-red-500 mr-1" />
                    )}
                    <span className={`text-sm font-semibold ${stat.trendUp ? 'text-emerald-600' : 'text-red-600'}`}>
                      {stat.trend}
                    </span>
                  </div>
                </div>
                <p className="text-3xl font-bold text-gray-900">{stat.value}</p>
                <p className="text-sm text-gray-500 mt-1">{stat.label}</p>
              </div>
            );
          })}
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-8">
          <div className="bg-white rounded-2xl p-6 shadow-sm border border-gray-100">
            <div className="flex items-center justify-between mb-6">
              <h2 className="text-lg font-semibold text-gray-900">Ventas Mensuales</h2>
            </div>
            <ResponsiveContainer width="100%" height={300}>
              <BarChart data={monthlyData}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="month" />
                <YAxis />
                <Tooltip />
                <Bar dataKey="ventas" fill="#3b82f6" />
                <Bar dataKey="clientes" fill="#10b981" />
              </BarChart>
            </ResponsiveContainer>
          </div>
          
          <div className="bg-white rounded-2xl p-6 shadow-sm border border-gray-100">
            <div className="flex items-center justify-between mb-6">
              <h2 className="text-lg font-semibold text-gray-900">Distribución</h2>
            </div>
            <ResponsiveContainer width="100%" height={300}>
              <PieChart>
                <Pie
                  data={pieData}
                  cx="50%"
                  cy="50%"
                  labelLine={false}
                  label={({ name, percent }) => `${name} ${percent ? (percent * 100).toFixed(0) : 0}%`}
                  outerRadius={80}
                  fill="#8884d8"
                  dataKey="value"
                >
                  {pieData.map((entry, index) => (
                    <Cell key={`cell-${index}`} fill={entry.color} />
                  ))}
                </Pie>
                <Tooltip />
                <Legend />
              </PieChart>
            </ResponsiveContainer>
          </div>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-8">
          <div className="bg-white rounded-2xl p-6 shadow-sm border border-gray-100">
            <div className="flex items-center justify-between mb-6">
              <h2 className="text-lg font-semibold text-gray-900">Tendencia de Ventas</h2>
            </div>
            <ResponsiveContainer width="100%" height={300}>
              <LineChart data={monthlyData}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="month" />
                <YAxis />
                <Tooltip />
                <Line type="monotone" dataKey="ventas" stroke="#8b5cf6" strokeWidth={2} />
                <Line type="monotone" dataKey="clientes" stroke="#10b981" strokeWidth={2} />
              </LineChart>
            </ResponsiveContainer>
          </div>

          <div className="bg-white rounded-2xl p-6 shadow-sm border border-gray-100">
            <h2 className="text-lg font-semibold text-gray-900 mb-6">Estadísticas Detalladas</h2>
            <div className="space-y-4">
              {secondaryStats.map((stat) => {
                const Icon = stat.icon;
                return (
                  <div key={stat.label} className="flex items-center justify-between p-4 bg-gray-50 rounded-xl">
                    <div className="flex items-center">
                      <div className={`bg-gradient-to-br ${stat.color} p-2 rounded-lg mr-3`}>
                        <Icon className="w-5 h-5 text-white" />
                      </div>
                      <span className="text-sm font-medium text-gray-700">{stat.label}</span>
                    </div>
                    <span className="text-lg font-bold text-gray-900">{stat.value}</span>
                  </div>
                );
              })}
            </div>
          </div>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          <div className="bg-white rounded-2xl p-6 shadow-sm border border-gray-100">
            <div className="flex items-center justify-between mb-6">
              <h2 className="text-lg font-semibold text-gray-900">Actividad Reciente</h2>
              <Activity className="w-5 h-5 text-gray-400" />
            </div>
            <div className="space-y-3">
              {[
                { action: 'Nuevo cliente registrado', time: 'Hace 2 horas', type: 'success' },
                { action: 'Venta completada #1234', time: 'Hace 4 horas', type: 'success' },
                { action: 'Cotización enviada #567', time: 'Hace 6 horas', type: 'info' },
                { action: 'Ticket creado #890', time: 'Hace 8 horas', type: 'warning' },
              ].map((item, index) => (
                <div key={index} className="flex items-center justify-between p-3 bg-gray-50 rounded-lg hover:bg-gray-100 transition-colors">
                  <div className="flex items-center">
                    <div className={`w-2 h-2 rounded-full mr-3 ${
                      item.type === 'success' ? 'bg-emerald-500' : 
                      item.type === 'warning' ? 'bg-amber-500' : 'bg-blue-500'
                    }`}></div>
                    <span className="text-sm text-gray-700">{item.action}</span>
                  </div>
                  <span className="text-xs text-gray-400">{item.time}</span>
                </div>
              ))}
            </div>
          </div>
          
          <div className="bg-white rounded-2xl p-6 shadow-sm border border-gray-100">
            <h2 className="text-lg font-semibold text-gray-900 mb-6">Tareas Pendientes</h2>
            <div className="space-y-3">
              {[
                { task: 'Llamar a prospecto ABC', priority: 'Alta' },
                { task: 'Revisar cotización #456', priority: 'Media' },
                { task: 'Actualizar cliente XYZ', priority: 'Baja' },
                { task: 'Enviar reporte mensual', priority: 'Alta' },
              ].map((item, index) => (
                <div key={index} className="flex items-center justify-between p-3 bg-gray-50 rounded-lg hover:bg-gray-100 transition-colors">
                  <span className="text-sm text-gray-700">{item.task}</span>
                  <span className={`text-xs px-3 py-1 rounded-full font-medium ${
                    item.priority === 'Alta' ? 'bg-red-100 text-red-600' :
                    item.priority === 'Media' ? 'bg-amber-100 text-amber-600' :
                    'bg-emerald-100 text-emerald-600'
                  }`}>{item.priority}</span>
                </div>
              ))}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;
