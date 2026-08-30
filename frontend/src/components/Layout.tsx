/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { useState } from 'react';
import { 
  Users, 
  UserPlus, 
  DollarSign, 
  FileText, 
  Package, 
  HeadphonesIcon,
  Home,
  Megaphone,
  Mail,
  MessageCircle,
  File,
  FileCheck,
  Receipt,
  MessageSquare,
  Star,
  HelpCircle,
  LogOut,
  Search,
  Bell,
  Settings,
  Menu,
  X,
  Cloud,
  KanbanSquare,
  Shield,
  Coins,
  Zap,
  Target,
  Plug,
  Trophy,
  UserCircle,
  Bot,
  BarChart3,
  Calculator,
  PenTool,
  Key,
  Calendar,
  CreditCard
} from 'lucide-react';
import { useAuth } from '../context/AuthContext';

const Layout = ({ children }: { children: React.ReactNode }) => {
  const location = useLocation();
  const navigate = useNavigate();
  const { logout, username, role } = useAuth();
  const [sidebarOpen, setSidebarOpen] = useState(false);

  const handleLogout = () => {
    logout();
    navigate('/login');
  };
  
  const navSections = [
    {
      title: 'PRINCIPAL',
      items: [
        { path: '/', label: 'Dashboard', icon: Home },
        { path: '/clientes', label: 'Clientes', icon: Users },
        { path: '/prospectos', label: 'Prospectos', icon: UserPlus },
      ],
    },
    {
      title: 'VENTAS',
      items: [
        { path: '/pipeline', label: 'Pipeline Kanban', icon: KanbanSquare },
        { path: '/actividades', label: 'Actividades', icon: Calendar },
        { path: '/ventas', label: 'Ventas', icon: DollarSign },
        { path: '/cotizaciones', label: 'Cotizaciones', icon: FileText },
        { path: '/pedidos', label: 'Pedidos', icon: Package },
        { path: '/facturas', label: 'FacturaciÃ³n', icon: Receipt },
        { path: '/contratos', label: 'Contratos', icon: FileCheck },
        { path: '/cpq', label: 'CPQ', icon: Calculator },
        { path: '/productos', label: 'Productos', icon: Package },
        { path: '/metas', label: 'Metas/Cuotas', icon: Target },
        { path: '/lead-scoring', label: 'Lead Scoring', icon: Target },
      ],
    },
    {
      title: 'MARKETING',
      items: [
        { path: '/campanas-marketing', label: 'Marketing', icon: Megaphone },
        { path: '/email-marketing', label: 'Email Marketing', icon: Mail },
        { path: '/email-templates', label: 'Plantillas Email', icon: FileText },
        { path: '/whatsapp-business', label: 'WhatsApp Business', icon: MessageCircle },
        { path: '/whatsapp-ai', label: 'IA WhatsApp', icon: Bot },
      ],
    },
    {
      title: 'SOPORTE',
      items: [
        { path: '/servicio-cliente', label: 'Soporte', icon: HeadphonesIcon },
        { path: '/pqrs', label: 'PQRS', icon: MessageSquare },
        { path: '/mesa-ayuda', label: 'Mesa de Ayuda', icon: HelpCircle },
        { path: '/encuestas-satisfaccion', label: 'Encuestas', icon: Star },
      ],
    },
    {
      title: 'GESTIÃ“N',
      items: [
        { path: '/gestion-documental', label: 'Documentos', icon: File },
        { path: '/workflows', label: 'Automatizaciones', icon: Zap },
        { path: '/multi-currency', label: 'Multi-Moneda', icon: Coins },
        { path: '/integrations', label: 'Integraciones', icon: Plug },
        { path: '/reports', label: 'Reportes', icon: BarChart3 },
      ],
    },
    {
      title: 'COMPLIANCE & PORTAL',
      items: [
        { path: '/gdpr', label: 'GDPR', icon: Shield },
        { path: '/esignature', label: 'Firma ElectrÃ³nica', icon: PenTool },
        { path: '/client-portal', label: 'Portal Clientes', icon: UserCircle },
        { path: '/gamification', label: 'GamificaciÃ³n', icon: Trophy },
        { path: '/sso', label: 'SSO', icon: Key },
        { path: '/billing', label: 'Billing', icon: CreditCard },
        { path: '/superadmin', label: 'SuperAdmin', icon: Shield },
      ],
    },
  ];

  const initials = (username || 'U').substring(0, 2).toUpperCase();
  
  return (
    <div className="min-h-screen bg-slate-100 flex">
      {/* Overlay mÃ³vil */}
      {sidebarOpen && (
        <div className="fixed inset-0 bg-black/50 z-30 lg:hidden" onClick={() => setSidebarOpen(false)}></div>
      )}

      {/* Sidebar */}
      <aside className={`fixed inset-y-0 left-0 z-40 w-64 bg-[#0b1730] flex flex-col transform transition-transform duration-200 lg:translate-x-0 lg:static ${sidebarOpen ? 'translate-x-0' : '-translate-x-full'}`}>
        {/* Logo */}
        <div className="flex items-center gap-3 px-5 py-5 border-b border-white/10">
          <div className="bg-gradient-to-br from-blue-500 to-cyan-400 p-2 rounded-xl shadow-lg shadow-blue-500/30">
            <Cloud className="w-6 h-6 text-white" />
          </div>
          <div>
            <h1 className="text-lg font-bold text-white leading-tight">CRM SaaS</h1>
            <p className="text-[10px] text-blue-300/70 uppercase tracking-wider">Empresarial</p>
          </div>
          <button className="ml-auto lg:hidden text-white/60" onClick={() => setSidebarOpen(false)}>
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* NavegaciÃ³n */}
        <nav className="flex-1 overflow-y-auto px-3 py-4 space-y-5 [scrollbar-width:thin] [scrollbar-color:#1e3a5f_transparent]">
          {navSections.map((section) => (
            <div key={section.title}>
              <p className="px-3 mb-2 text-[10px] font-semibold tracking-widest text-blue-300/50">{section.title}</p>
              <div className="space-y-0.5">
                {section.items.map((item) => {
                  const Icon = item.icon;
                  const isActive = location.pathname === item.path;
                  return (
                    <Link
                      key={item.path}
                      to={item.path}
                      onClick={() => setSidebarOpen(false)}
                      className={`flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium transition-all ${
                        isActive
                          ? 'bg-blue-600 text-white shadow-lg shadow-blue-600/30'
                          : 'text-slate-300 hover:bg-white/5 hover:text-white'
                      }`}
                    >
                      <Icon className={`w-[18px] h-[18px] ${isActive ? 'text-white' : 'text-slate-400'}`} />
                      {item.label}
                    </Link>
                  );
                })}
              </div>
            </div>
          ))}
        </nav>

        {/* Usuario y logout */}
        <div className="border-t border-white/10 p-4">
          <div className="flex items-center gap-3 mb-3">
            <div className="w-9 h-9 rounded-full bg-gradient-to-br from-blue-500 to-cyan-400 flex items-center justify-center text-white text-xs font-bold">
              {initials}
            </div>
            <div className="min-w-0">
              <p className="text-sm font-semibold text-white truncate">{username || 'Usuario'}</p>
              <p className="text-[11px] text-blue-300/60 truncate">{role || 'SIN ROL'}</p>
            </div>
          </div>
          <button
            onClick={handleLogout}
            className="w-full flex items-center justify-center gap-2 px-3 py-2 rounded-lg text-sm font-medium text-red-300 bg-red-500/10 hover:bg-red-500/20 transition-colors"
          >
            <LogOut className="w-4 h-4" />
            Cerrar SesiÃ³n
          </button>
        </div>
      </aside>

      {/* Contenido principal */}
      <div className="flex-1 flex flex-col min-w-0">
        {/* Topbar */}
        <header className="sticky top-0 z-20 bg-white border-b border-slate-200 px-4 lg:px-6 h-16 flex items-center gap-4">
          <button className="lg:hidden text-slate-600" onClick={() => setSidebarOpen(true)}>
            <Menu className="w-6 h-6" />
          </button>

          <div className="hidden md:flex items-center flex-1 max-w-md">
            <div className="relative w-full">
              <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-400" />
              <input
                type="text"
                placeholder="Buscar clientes, ventas, documentos..."
                className="w-full pl-10 pr-4 py-2 text-sm bg-slate-100 border border-transparent rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:bg-white transition-all"
              />
            </div>
          </div>

          <div className="ml-auto flex items-center gap-2">
            <button className="relative p-2 rounded-lg text-slate-500 hover:bg-slate-100 transition-colors">
              <Bell className="w-5 h-5" />
              <span className="absolute top-1.5 right-1.5 w-2 h-2 bg-red-500 rounded-full"></span>
            </button>
            <button className="p-2 rounded-lg text-slate-500 hover:bg-slate-100 transition-colors">
              <Settings className="w-5 h-5" />
            </button>
            <div className="hidden sm:flex items-center gap-2 pl-3 border-l border-slate-200">
              <div className="w-8 h-8 rounded-full bg-gradient-to-br from-blue-600 to-cyan-500 flex items-center justify-center text-white text-xs font-bold">
                {initials}
              </div>
            </div>
          </div>
        </header>

        <main className="flex-1 overflow-y-auto">
          {children}
        </main>
      </div>
    </div>
  );
};

export default Layout;
