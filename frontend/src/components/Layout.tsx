import { Link, useLocation, useNavigate } from 'react-router-dom';
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
  LogOut
} from 'lucide-react';

const Layout = ({ children }: { children: React.ReactNode }) => {
  const location = useLocation();
  const navigate = useNavigate();

  const handleLogout = () => {
    localStorage.removeItem('usuario');
    navigate('/login');
  };
  
  const navItems = [
    { path: '/', label: 'Dashboard', icon: Home },
    { path: '/clientes', label: 'Clientes', icon: Users },
    { path: '/prospectos', label: 'Prospectos', icon: UserPlus },
    { path: '/ventas', label: 'Ventas', icon: DollarSign },
    { path: '/cotizaciones', label: 'Cotizaciones', icon: FileText },
    { path: '/pedidos', label: 'Pedidos', icon: Package },
    { path: '/servicio-cliente', label: 'Servicio al Cliente', icon: HeadphonesIcon },
    { path: '/campanas-marketing', label: 'Campañas', icon: Megaphone },
    { path: '/email-marketing', label: 'Email Marketing', icon: Mail },
    { path: '/whatsapp-business', label: 'WhatsApp', icon: MessageCircle },
    { path: '/gestion-documental', label: 'Documentos', icon: File },
    { path: '/contratos', label: 'Contratos', icon: FileCheck },
    { path: '/facturas', label: 'Facturación', icon: Receipt },
    { path: '/pqrs', label: 'PQRS', icon: MessageSquare },
    { path: '/encuestas-satisfaccion', label: 'Encuestas', icon: Star },
    { path: '/mesa-ayuda', label: 'Mesa de Ayuda', icon: HelpCircle },
  ];
  
  return (
    <div className="min-h-screen bg-gray-50">
      <nav className="bg-white shadow-lg">
        <div className="max-w-7xl mx-auto px-4">
          <div className="flex justify-between h-16">
            <div className="flex">
              <div className="flex-shrink-0 flex items-center">
                <h1 className="text-2xl font-bold text-primary-600">CRM</h1>
              </div>
              <div className="hidden sm:ml-6 sm:flex sm:space-x-8">
                {navItems.map((item) => {
                  const Icon = item.icon;
                  const isActive = location.pathname === item.path;
                  return (
                    <Link
                      key={item.path}
                      to={item.path}
                      className={`inline-flex items-center px-1 pt-1 border-b-2 text-sm font-medium ${
                        isActive
                          ? 'border-primary-500 text-gray-900'
                          : 'border-transparent text-gray-500 hover:border-gray-300 hover:text-gray-700'
                      }`}
                    >
                      <Icon className="w-4 h-4 mr-2" />
                      {item.label}
                    </Link>
                  );
                })}
              </div>
            </div>
            <div className="flex items-center">
              <button
                onClick={handleLogout}
                className="inline-flex items-center px-3 py-2 border border-transparent text-sm font-medium rounded-md text-white bg-red-600 hover:bg-red-700"
              >
                <LogOut className="w-4 h-4 mr-2" />
                Cerrar Sesión
              </button>
            </div>
          </div>
        </div>
      </nav>
      
      <main className="max-w-7xl mx-auto py-6 px-4 sm:px-6 lg:px-8">
        {children}
      </main>
    </div>
  );
};

export default Layout;
