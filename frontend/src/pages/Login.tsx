import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import apiClient from '../services/api';
import { Usuario } from '../types';

export default function Login() {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    username: '',
    password: '',
  });
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setLoading(true);

    try {
      const response = await apiClient.post('/api/auth/login', formData);
      const usuario: Usuario = response.data;
      
      // Guardar usuario en localStorage
      localStorage.setItem('usuario', JSON.stringify(usuario));
      
      // Redirigir al dashboard
      navigate('/');
    } catch (error: any) {
      console.error('Error en login:', error);
      setError(error.response?.data || 'Error al iniciar sesión');
      // Limpiar campos del formulario cuando falla el login
      setFormData({ username: '', password: '' });
    } finally {
      setLoading(false);
    }
  };

  return (
    <div 
      className="min-h-screen flex items-center justify-center bg-cover bg-center bg-no-repeat relative"
      style={{ 
        backgroundImage: 'url(https://www.omniumdigital.com/wp-content/uploads/2017/04/estrategia-digital-crm.jpg)',
        backgroundSize: 'cover',
        backgroundPosition: 'center'
      }}
    >
      <div className="absolute inset-0 bg-black bg-opacity-50"></div>
      
      <div className="relative bg-white p-8 rounded-2xl shadow-2xl w-full max-w-md border border-gray-200">
        <div className="text-center mb-8">
          <h1 className="text-4xl font-bold bg-gradient-to-r from-gray-700 to-gray-900 bg-clip-text text-transparent mb-2">
            CRM
          </h1>
          <p className="text-gray-600 text-sm">Sistema de Gestión de Clientes</p>
        </div>
        
        {error && (
          <div className="mb-4 p-3 bg-red-50 border border-red-200 text-red-700 rounded-lg">
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit} className="space-y-5">
          <div>
            <label className="block text-sm font-semibold text-gray-700 mb-2">
              Usuario
            </label>
            <input
              type="text"
              value={formData.username}
              onChange={(e) => setFormData({ ...formData, username: e.target.value })}
              className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-gray-500 focus:border-transparent transition-all"
              placeholder="Ingresa tu usuario"
              required
            />
          </div>

          <div>
            <label className="block text-sm font-semibold text-gray-700 mb-2">
              Contraseña
            </label>
            <input
              type="password"
              value={formData.password}
              onChange={(e) => setFormData({ ...formData, password: e.target.value })}
              className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-gray-500 focus:border-transparent transition-all"
              placeholder="Ingresa tu contraseña"
              required
            />
          </div>

          <button
            type="submit"
            disabled={loading}
            className="w-full bg-gradient-to-r from-gray-700 to-gray-900 text-white py-3 px-4 rounded-lg hover:from-gray-800 hover:to-black disabled:from-gray-400 disabled:to-gray-500 transition-all font-semibold shadow-lg hover:shadow-xl"
          >
            {loading ? 'Iniciando sesión...' : 'Iniciar Sesión'}
          </button>
        </form>

        <div className="mt-6 text-center text-sm text-gray-500">
          <p>¿No tienes cuenta? Contacta al administrador</p>
        </div>
      </div>
    </div>
  );
}
