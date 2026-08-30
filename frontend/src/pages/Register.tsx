/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import apiClient from '../services/api';
import { useAuth } from '../context/AuthContext';

export default function Register() {
  const navigate = useNavigate();
  const { login } = useAuth();
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const [formData, setFormData] = useState({
    companyName: '',
    adminName: '',
    adminUsername: '',
    adminEmail: '',
    adminPassword: '',
    country: 'Colombia',
    currency: 'COP',
    timezone: 'America/Bogota',
    locale: 'es',
    planName: 'STARTER',
  });

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setLoading(true);

    try {
      const response = await apiClient.post('/api/v1/auth/register', formData);
      const { accessToken, refreshToken, role, tenantId } = response.data;
      login(accessToken, refreshToken, role, tenantId, formData.adminUsername);
      navigate('/');
    } catch (error: any) {
      setError(error.response?.data || 'Error al registrar empresa');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-gray-800 to-black py-8">
      <div className="relative bg-white p-8 rounded-2xl shadow-2xl w-full max-w-2xl border border-gray-200 my-8">
        <div className="text-center mb-6">
          <h1 className="text-3xl font-bold bg-gradient-to-r from-gray-700 to-gray-900 bg-clip-text text-transparent mb-2">
            Registrar Empresa
          </h1>
          <p className="text-gray-600 text-sm">Crea tu cuenta SaaS con 14 dÃ­as de trial gratis</p>
        </div>

        {error && (
          <div className="mb-4 p-3 bg-red-50 border border-red-200 text-red-700 rounded-lg">
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-semibold text-gray-700 mb-1">Nombre de Empresa</label>
              <input type="text" name="companyName" value={formData.companyName} onChange={handleChange}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-gray-500"
                placeholder="Mi Empresa SAS" required />
            </div>
            <div>
              <label className="block text-sm font-semibold text-gray-700 mb-1">Nombre del Admin</label>
              <input type="text" name="adminName" value={formData.adminName} onChange={handleChange}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-gray-500"
                placeholder="Juan Perez" required />
            </div>
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-semibold text-gray-700 mb-1">Usuario Admin</label>
              <input type="text" name="adminUsername" value={formData.adminUsername} onChange={handleChange}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-gray-500"
                placeholder="jperez" required />
            </div>
            <div>
              <label className="block text-sm font-semibold text-gray-700 mb-1">Email Admin</label>
              <input type="email" name="adminEmail" value={formData.adminEmail} onChange={handleChange}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-gray-500"
                placeholder="juan@empresa.com" required />
            </div>
          </div>

          <div>
            <label className="block text-sm font-semibold text-gray-700 mb-1">ContraseÃ±a</label>
            <input type="password" name="adminPassword" value={formData.adminPassword} onChange={handleChange}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-gray-500"
              placeholder="Minimo 8 caracteres" required minLength={8} />
          </div>

          <div className="grid grid-cols-3 gap-4">
            <div>
              <label className="block text-sm font-semibold text-gray-700 mb-1">Pais</label>
              <select name="country" value={formData.country} onChange={handleChange}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-gray-500">
                <option value="Colombia">Colombia</option>
                <option value="Mexico">Mexico</option>
                <option value="Argentina">Argentina</option>
                <option value="Espana">Espana</option>
                <option value="Chile">Chile</option>
                <option value="Peru">Peru</option>
                <option value="Ecuador">Ecuador</option>
                <option value="USA">USA</option>
              </select>
            </div>
            <div>
              <label className="block text-sm font-semibold text-gray-700 mb-1">Moneda</label>
              <select name="currency" value={formData.currency} onChange={handleChange}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-gray-500">
                <option value="COP">COP</option>
                <option value="MXN">MXN</option>
                <option value="ARS">ARS</option>
                <option value="EUR">EUR</option>
                <option value="USD">USD</option>
                <option value="CLP">CLP</option>
                <option value="PEN">PEN</option>
              </select>
            </div>
            <div>
              <label className="block text-sm font-semibold text-gray-700 mb-1">Plan</label>
              <select name="planName" value={formData.planName} onChange={handleChange}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-gray-500">
                <option value="STARTER">Starter - $29/mes</option>
                <option value="BUSINESS">Business - $79/mes</option>
                <option value="ENTERPRISE">Enterprise - $199/mes</option>
                <option value="AGENCY">Agency - $399/mes</option>
              </select>
            </div>
          </div>

          <button
            type="submit"
            disabled={loading}
            className="w-full bg-gradient-to-r from-gray-700 to-gray-900 text-white py-3 px-4 rounded-lg hover:from-gray-800 hover:to-black disabled:from-gray-400 disabled:to-gray-500 transition-all font-semibold shadow-lg"
          >
            {loading ? 'Creando empresa...' : 'Crear Empresa + Trial Gratis'}
          </button>
        </form>

        <div className="mt-4 text-center text-sm">
          <Link to="/login" className="text-blue-600 hover:text-blue-800 font-semibold">
            Ya tienes cuenta? Inicia sesion
          </Link>
        </div>
      </div>
    </div>
  );
}
