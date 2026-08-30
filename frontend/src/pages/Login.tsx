import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { Cloud, Lock, User, ShieldCheck, Building2, TrendingUp, Eye, EyeOff, Wand2 } from 'lucide-react';
import apiClient from '../services/api';
import { useAuth } from '../context/AuthContext';

export default function Login() {
  const navigate = useNavigate();
  const { login } = useAuth();
  const [formData, setFormData] = useState({
    username: '',
    password: '',
  });
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const [showPassword, setShowPassword] = useState(false);

  const fillDemoCredentials = async () => {
    const creds = { username: 'superadmin', password: 'SuperAdmin123!' };
    setFormData(creds);
    setError(null);
    setLoading(true);
    try {
      const response = await apiClient.post('/api/auth/login', creds);
      const { accessToken, refreshToken, role, tenantId } = response.data;
      login(accessToken, refreshToken, role, tenantId, creds.username);
      navigate('/');
    } catch (error: any) {
      console.error('Error en login:', error);
      const msg = typeof error.response?.data === 'string'
        ? error.response.data
        : error.response?.data?.message || 'Error al iniciar sesión';
      setError(msg);
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setLoading(true);

    try {
      const response = await apiClient.post('/api/auth/login', formData);
      const { accessToken, refreshToken, role, tenantId } = response.data;
      login(accessToken, refreshToken, role, tenantId, formData.username);
      navigate('/');
    } catch (error: any) {
      console.error('Error en login:', error);
      const msg = typeof error.response?.data === 'string' 
        ? error.response.data 
        : error.response?.data?.message || 'Error al iniciar sesión';
      setError(msg);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex bg-[#0b1730]">
      {/* Panel izquierdo - Branding */}
      <div className="hidden lg:flex lg:w-1/2 flex-col justify-between p-12 relative overflow-hidden">
        {/* Decoración de fondo */}
        <div className="absolute inset-0">
          <div className="absolute top-20 -left-20 w-96 h-96 bg-blue-600/20 rounded-full blur-3xl"></div>
          <div className="absolute bottom-20 right-0 w-80 h-80 bg-cyan-500/10 rounded-full blur-3xl"></div>
        </div>

        <div className="relative">
          <div className="flex items-center gap-3">
            <div className="bg-gradient-to-br from-blue-500 to-cyan-400 p-2.5 rounded-xl shadow-lg shadow-blue-500/30">
              <Cloud className="w-7 h-7 text-white" />
            </div>
            <div>
              <h1 className="text-xl font-bold text-white">CRM SaaS Empresarial</h1>
              <p className="text-xs text-blue-300/70">Sistema de Gestión de Relaciones con Clientes</p>
            </div>
          </div>
        </div>

        <div className="relative space-y-6">
          <h2 className="text-4xl font-bold text-white leading-tight">
            Un CRM completo,<br />
            <span className="bg-gradient-to-r from-blue-400 to-cyan-300 bg-clip-text text-transparent">seguro y escalable</span><br />
            para hacer crecer tu negocio
          </h2>
          <div className="space-y-4">
            <div className="flex items-center gap-3 text-slate-300">
              <div className="bg-blue-500/10 p-2 rounded-lg border border-blue-500/20">
                <Building2 className="w-5 h-5 text-blue-400" />
              </div>
              <div>
                <p className="text-sm font-semibold text-white">Multiempresa</p>
                <p className="text-xs text-slate-400">Aislamiento total de datos</p>
              </div>
            </div>
            <div className="flex items-center gap-3 text-slate-300">
              <div className="bg-blue-500/10 p-2 rounded-lg border border-blue-500/20">
                <ShieldCheck className="w-5 h-5 text-blue-400" />
              </div>
              <div>
                <p className="text-sm font-semibold text-white">Seguridad Avanzada</p>
                <p className="text-xs text-slate-400">JWT, Roles, Permisos y Auditoría</p>
              </div>
            </div>
            <div className="flex items-center gap-3 text-slate-300">
              <div className="bg-blue-500/10 p-2 rounded-lg border border-blue-500/20">
                <TrendingUp className="w-5 h-5 text-blue-400" />
              </div>
              <div>
                <p className="text-sm font-semibold text-white">Escalable e Inteligente</p>
                <p className="text-xs text-slate-400">Diseñado para crecer contigo</p>
              </div>
            </div>
          </div>
        </div>

        <p className="relative text-xs text-slate-500">Disponibilidad 99.9% Uptime Garantizado</p>
      </div>

      {/* Panel derecho - Formulario */}
      <div className="flex-1 flex items-center justify-center p-6 bg-slate-50 lg:rounded-l-[2.5rem]">
        <div className="w-full max-w-md">
          {/* Logo móvil */}
          <div className="lg:hidden flex items-center justify-center gap-3 mb-8">
            <div className="bg-gradient-to-br from-blue-500 to-cyan-400 p-2.5 rounded-xl shadow-lg">
              <Cloud className="w-7 h-7 text-white" />
            </div>
            <h1 className="text-xl font-bold text-slate-900">CRM SaaS</h1>
          </div>

          <div className="bg-white p-8 rounded-2xl shadow-xl border border-slate-200">
            <div className="mb-8">
              <h2 className="text-2xl font-bold text-slate-900">Bienvenido de nuevo</h2>
              <p className="text-slate-500 text-sm mt-1">Ingresa tus credenciales para continuar</p>
            </div>
            
            {error && (
              <div className="mb-5 p-3 bg-red-50 border border-red-200 text-red-700 rounded-xl text-sm">
                {error}
              </div>
            )}

            <form onSubmit={handleSubmit} className="space-y-5">
              <div>
                <label className="block text-sm font-semibold text-slate-700 mb-2">
                  Usuario
                </label>
                <div className="relative">
                  <User className="absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-400 w-[18px] h-[18px]" />
                  <input
                    type="text"
                    value={formData.username}
                    onChange={(e) => setFormData({ ...formData, username: e.target.value })}
                    className="w-full pl-11 pr-4 py-3 border border-slate-300 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all text-sm"
                    placeholder="Ingresa tu usuario"
                    required
                  />
                </div>
              </div>

              <div>
                <label className="block text-sm font-semibold text-slate-700 mb-2">
                  Contraseña
                </label>
                <div className="relative">
                  <Lock className="absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-400 w-[18px] h-[18px]" />
                  <input
                    type={showPassword ? 'text' : 'password'}
                    value={formData.password}
                    onChange={(e) => setFormData({ ...formData, password: e.target.value })}
                    className="w-full pl-11 pr-11 py-3 border border-slate-300 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all text-sm"
                    placeholder="Ingresa tu contraseña"
                    required
                  />
                  <button
                    type="button"
                    onClick={() => setShowPassword(!showPassword)}
                    className="absolute right-3 top-1/2 -translate-y-1/2 text-slate-400 hover:text-slate-600 focus:outline-none"
                    tabIndex={-1}
                  >
                    {showPassword ? <EyeOff className="w-5 h-5" /> : <Eye className="w-5 h-5" />}
                  </button>
                </div>
              </div>

              <button
                type="submit"
                disabled={loading}
                className="w-full bg-gradient-to-r from-blue-600 to-blue-700 text-white py-3 px-4 rounded-xl hover:from-blue-700 hover:to-blue-800 disabled:from-slate-400 disabled:to-slate-500 transition-all font-semibold shadow-lg shadow-blue-600/25 hover:shadow-xl text-sm"
              >
                {loading ? 'Iniciando sesión...' : 'Iniciar Sesión'}
              </button>

              <button
                type="button"
                onClick={fillDemoCredentials}
                className="w-full flex items-center justify-center gap-2 py-2.5 px-4 rounded-xl border border-slate-300 text-slate-600 hover:bg-slate-50 hover:text-blue-600 transition-all text-sm font-medium"
              >
                <Wand2 className="w-4 h-4" />
                Usar credenciales de prueba (SuperAdmin)
              </button>
            </form>

            <div className="mt-6 text-center text-sm space-y-2">
              <p className="text-slate-500 text-xs">
                Credenciales de prueba: <strong className="text-slate-700">superadmin</strong> / <strong className="text-slate-700">SuperAdmin123!</strong>
              </p>
              <Link to="/register" className="block text-blue-600 hover:text-blue-800 font-semibold">
                ¿No tienes cuenta? Registra tu empresa
              </Link>
              <Link to="/pricing" className="block text-slate-500 hover:text-slate-700 font-medium">
                Ver planes y precios →
              </Link>
            </div>
          </div>

          <p className="text-center text-xs text-slate-400 mt-6">
            Multiempresa · Seguro · Escalable · Inteligente
          </p>
        </div>
      </div>
    </div>
  );
}
