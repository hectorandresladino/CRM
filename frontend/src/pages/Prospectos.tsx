/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
import { useState, useEffect } from 'react';
import { Plus, Edit, Trash2, Search } from 'lucide-react';
import apiClient from '../services/api';
import { Prospecto } from '../types';
import Button from '../components/Button';
import Input from '../components/Input';
import Modal from '../components/Modal';

const Prospectos = () => {
  const [prospectos, setProspectos] = useState<Prospecto[]>([]);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingProspecto, setEditingProspecto] = useState<Prospecto | null>(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [formData, setFormData] = useState<Prospecto>({
    nombre: '',
    apellido: '',
    email: '',
    telefono: '',
    celular: '',
    empresa: '',
    cargo: '',
    sector: '',
    origen: '',
    interes: '',
    notas: '',
    estado: 'NUEVO',
    prioridad: 'MEDIA',
  });
  
  useEffect(() => {
    loadProspectos();
  }, []);
  
  const loadProspectos = async () => {
    try {
      const response = await apiClient.get('/api/prospectos');
      setProspectos(response.data);
    } catch (error) {
      console.error('Error loading prospectos:', error);
    }
  };
  
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      if (editingProspecto?.id) {
        await apiClient.put(`/api/prospectos/${editingProspecto.id}`, formData);
      } else {
        await apiClient.post('/api/prospectos', formData);
      }
      loadProspectos();
      setIsModalOpen(false);
      setEditingProspecto(null);
      setFormData({
        nombre: '',
        apellido: '',
        email: '',
        telefono: '',
        celular: '',
        empresa: '',
        cargo: '',
        sector: '',
        origen: '',
        interes: '',
        notas: '',
        estado: 'NUEVO',
        prioridad: 'MEDIA',
      });
    } catch (error) {
      console.error('Error saving prospecto:', error);
    }
  };
  
  const handleEdit = (prospecto: Prospecto) => {
    setEditingProspecto(prospecto);
    setFormData(prospecto);
    setIsModalOpen(true);
  };
  
  const handleDelete = async (id: number) => {
    if (window.confirm('¿Está seguro de eliminar este prospecto?')) {
      try {
        await apiClient.delete(`/api/prospectos/${id}`);
        loadProspectos();
      } catch (error) {
        console.error('Error deleting prospecto:', error);
      }
    }
  };
  
  const filteredProspectos = prospectos.filter(
    (prospecto) =>
      prospecto.nombre.toLowerCase().includes(searchTerm.toLowerCase()) ||
      prospecto.apellido.toLowerCase().includes(searchTerm.toLowerCase()) ||
      prospecto.email?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      prospecto.empresa?.toLowerCase().includes(searchTerm.toLowerCase())
  );
  
  const getEstadoColor = (estado: string) => {
    const colors: Record<string, string> = {
      NUEVO: 'bg-blue-100 text-blue-800',
      CONTACTADO: 'bg-yellow-100 text-yellow-800',
      CALIFICADO: 'bg-green-100 text-green-800',
      PROPUESTA: 'bg-purple-100 text-purple-800',
      NEGOCIACION: 'bg-orange-100 text-orange-800',
      CERRADO: 'bg-green-600 text-white',
      PERDIDO: 'bg-red-100 text-red-800',
    };
    return colors[estado] || 'bg-gray-100 text-gray-800';
  };
  
  const getPrioridadColor = (prioridad: string) => {
    const colors: Record<string, string> = {
      BAJA: 'bg-gray-100 text-gray-800',
      MEDIA: 'bg-blue-100 text-blue-800',
      ALTA: 'bg-orange-100 text-orange-800',
      URGENTE: 'bg-red-100 text-red-800',
    };
    return colors[prioridad] || 'bg-gray-100 text-gray-800';
  };
  
  return (
    <div>
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-3xl font-bold text-gray-900">Prospectos</h1>
        <Button onClick={() => setIsModalOpen(true)}>
          <Plus className="w-4 h-4 mr-2" />
          Nuevo Prospecto
        </Button>
      </div>
      
      <div className="card mb-6">
        <div className="relative">
          <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-5 h-5" />
          <Input
            placeholder="Buscar prospectos..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="pl-10"
          />
        </div>
      </div>
      
      <div className="card">
        <table className="table">
          <thead>
            <tr>
              <th>Nombre</th>
              <th>Email</th>
              <th>Empresa</th>
              <th>Estado</th>
              <th>Prioridad</th>
              <th>Acciones</th>
            </tr>
          </thead>
          <tbody>
            {filteredProspectos.map((prospecto) => (
              <tr key={prospecto.id}>
                <td>
                  {prospecto.nombre} {prospecto.apellido}
                </td>
                <td>{prospecto.email}</td>
                <td>{prospecto.empresa}</td>
                <td>
                  <span className={`px-2 py-1 rounded-full text-xs font-medium ${getEstadoColor(prospecto.estado)}`}>
                    {prospecto.estado}
                  </span>
                </td>
                <td>
                  <span className={`px-2 py-1 rounded-full text-xs font-medium ${getPrioridadColor(prospecto.prioridad)}`}>
                    {prospecto.prioridad}
                  </span>
                </td>
                <td>
                  <div className="flex space-x-2">
                    <Button variant="secondary" size="sm" onClick={() => handleEdit(prospecto)}>
                      <Edit className="w-4 h-4" />
                    </Button>
                    <Button variant="danger" size="sm" onClick={() => handleDelete(prospecto.id!)}>
                      <Trash2 className="w-4 h-4" />
                    </Button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        {filteredProspectos.length === 0 && (
          <p className="text-center text-gray-500 py-8">No hay prospectos registrados</p>
        )}
      </div>
      
      <Modal
        isOpen={isModalOpen}
        onClose={() => {
          setIsModalOpen(false);
          setEditingProspecto(null);
        }}
        title={editingProspecto?.id ? 'Editar Prospecto' : 'Nuevo Prospecto'}
      >
        <form onSubmit={handleSubmit}>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <Input
              label="Nombre"
              value={formData.nombre}
              onChange={(e) => setFormData({ ...formData, nombre: e.target.value })}
              required
            />
            <Input
              label="Apellido"
              value={formData.apellido}
              onChange={(e) => setFormData({ ...formData, apellido: e.target.value })}
              required
            />
            <Input
              label="Email"
              type="email"
              value={formData.email}
              onChange={(e) => setFormData({ ...formData, email: e.target.value })}
            />
            <Input
              label="Teléfono"
              value={formData.telefono}
              onChange={(e) => setFormData({ ...formData, telefono: e.target.value })}
            />
            <Input
              label="Empresa"
              value={formData.empresa}
              onChange={(e) => setFormData({ ...formData, empresa: e.target.value })}
            />
            <Input
              label="Cargo"
              value={formData.cargo}
              onChange={(e) => setFormData({ ...formData, cargo: e.target.value })}
            />
          </div>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mt-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Estado</label>
              <select
                className="input"
                value={formData.estado}
                onChange={(e) => setFormData({ ...formData, estado: e.target.value as any })}
              >
                <option value="NUEVO">Nuevo</option>
                <option value="CONTACTADO">Contactado</option>
                <option value="CALIFICADO">Calificado</option>
                <option value="PROPUESTA">Propuesta</option>
                <option value="NEGOCIACION">Negociación</option>
                <option value="CERRADO">Cerrado</option>
                <option value="PERDIDO">Perdido</option>
              </select>
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Prioridad</label>
              <select
                className="input"
                value={formData.prioridad}
                onChange={(e) => setFormData({ ...formData, prioridad: e.target.value as any })}
              >
                <option value="BAJA">Baja</option>
                <option value="MEDIA">Media</option>
                <option value="ALTA">Alta</option>
                <option value="URGENTE">Urgente</option>
              </select>
            </div>
          </div>
          <div className="mt-4">
            <label className="block text-sm font-medium text-gray-700 mb-1">Origen</label>
            <textarea
              className="input"
              rows={2}
              value={formData.origen}
              onChange={(e) => setFormData({ ...formData, origen: e.target.value })}
            />
          </div>
          <div className="mt-4">
            <label className="block text-sm font-medium text-gray-700 mb-1">Interés</label>
            <textarea
              className="input"
              rows={2}
              value={formData.interes}
              onChange={(e) => setFormData({ ...formData, interes: e.target.value })}
            />
          </div>
          <div className="mt-4">
            <label className="block text-sm font-medium text-gray-700 mb-1">Notas</label>
            <textarea
              className="input"
              rows={3}
              value={formData.notas}
              onChange={(e) => setFormData({ ...formData, notas: e.target.value })}
            />
          </div>
          <div className="mt-6 flex justify-end space-x-3">
            <Button
              type="button"
              variant="secondary"
              onClick={() => {
                setIsModalOpen(false);
                setEditingProspecto(null);
              }}
            >
              Cancelar
            </Button>
            <Button type="submit">Guardar</Button>
          </div>
        </form>
      </Modal>
    </div>
  );
};

export default Prospectos;
