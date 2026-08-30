/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
import { useState, useEffect } from 'react';
import { Plus, Edit, Trash2, Search } from 'lucide-react';
import apiClient from '../services/api';
import { Cliente } from '../types';
import Button from '../components/Button';
import Input from '../components/Input';
import Modal from '../components/Modal';

const Clientes = () => {
  const [clientes, setClientes] = useState<Cliente[]>([]);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingCliente, setEditingCliente] = useState<Cliente | null>(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [formData, setFormData] = useState<Cliente>({
    nombre: '',
    apellido: '',
    email: '',
    telefono: '',
    celular: '',
    direccion: '',
    ciudad: '',
    pais: '',
    codigoPostal: '',
    identificacion: '',
    tipoIdentificacion: '',
    empresa: '',
    cargo: '',
    sector: '',
    notas: '',
    estado: 'ACTIVO',
  });
  
  useEffect(() => {
    loadClientes();
  }, []);
  
  const loadClientes = async () => {
    try {
      const response = await apiClient.get('/api/clientes');
      setClientes(response.data);
    } catch (err) {
      console.error('Error loading clientes:', err);
    }
  };
  
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    try {
      console.log('Enviando datos:', formData);
      if (editingCliente?.id) {
        await apiClient.put(`/api/clientes/${editingCliente.id}`, formData);
      } else {
        await apiClient.post('/api/clientes', formData);
      }
      loadClientes();
      setIsModalOpen(false);
      setEditingCliente(null);
      setFormData({
        nombre: '',
        apellido: '',
        email: '',
        telefono: '',
        celular: '',
        direccion: '',
        ciudad: '',
        pais: '',
        codigoPostal: '',
        identificacion: '',
        tipoIdentificacion: '',
        empresa: '',
        cargo: '',
        sector: '',
        notas: '',
        estado: 'ACTIVO',
      });
    } catch (error: any) {
      console.error('Error saving cliente:', error);
      console.error('Error response:', error.response);
      console.error('Error data:', error.response?.data);
      const errorMessage = error.response?.data || error.message || 'Error al guardar el cliente';
      setError(typeof errorMessage === 'string' ? errorMessage : JSON.stringify(errorMessage));
    }
  };
  
  const handleEdit = (cliente: Cliente) => {
    setEditingCliente(cliente);
    setFormData(cliente);
    setError(null);
    setIsModalOpen(true);
  };
  
  const handleDelete = async (id: number) => {
    if (window.confirm('Â¿EstÃ¡ seguro de eliminar este cliente?')) {
      try {
        await apiClient.delete(`/api/clientes/${id}`);
        loadClientes();
      } catch (error) {
        console.error('Error deleting cliente:', error);
      }
    }
  };
  
  const filteredClientes = clientes.filter(
    (cliente) =>
      cliente.nombre.toLowerCase().includes(searchTerm.toLowerCase()) ||
      cliente.apellido.toLowerCase().includes(searchTerm.toLowerCase()) ||
      cliente.email?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      cliente.empresa?.toLowerCase().includes(searchTerm.toLowerCase())
  );
  
  return (
    <div>
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-3xl font-bold text-gray-900">Clientes</h1>
        <Button onClick={() => { setIsModalOpen(true); setError(null); }}>
          <Plus className="w-4 h-4 mr-2" />
          Nuevo Cliente
        </Button>
      </div>
      
      <div className="card mb-6">
        <div className="relative">
          <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-5 h-5" />
          <Input
            placeholder="Buscar clientes..."
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
              <th>TelÃ©fono</th>
              <th>Empresa</th>
              <th>Estado</th>
              <th>Acciones</th>
            </tr>
          </thead>
          <tbody>
            {filteredClientes.map((cliente) => (
              <tr key={cliente.id}>
                <td>
                  {cliente.nombre} {cliente.apellido}
                </td>
                <td>{cliente.email}</td>
                <td>{cliente.telefono || cliente.celular}</td>
                <td>{cliente.empresa}</td>
                <td>
                  <span
                    className={`px-2 py-1 rounded-full text-xs font-medium ${
                      cliente.estado === 'ACTIVO'
                        ? 'bg-green-100 text-green-800'
                        : cliente.estado === 'INACTIVO'
                        ? 'bg-gray-100 text-gray-800'
                        : 'bg-red-100 text-red-800'
                    }`}
                  >
                    {cliente.estado}
                  </span>
                </td>
                <td>
                  <div className="flex space-x-2">
                    <Button
                      variant="secondary"
                      size="sm"
                      onClick={() => handleEdit(cliente)}
                    >
                      <Edit className="w-4 h-4" />
                    </Button>
                    <Button
                      variant="danger"
                      size="sm"
                      onClick={() => handleDelete(cliente.id!)}
                    >
                      <Trash2 className="w-4 h-4" />
                    </Button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        {filteredClientes.length === 0 && (
          <p className="text-center text-gray-500 py-8">No hay clientes registrados</p>
        )}
      </div>
      
      <Modal
        isOpen={isModalOpen}
        onClose={() => {
          setIsModalOpen(false);
          setEditingCliente(null);
          setError(null);
        }}
        title={editingCliente?.id ? 'Editar Cliente' : 'Nuevo Cliente'}
      >
        {error && (
          <div className="mb-4 p-3 bg-red-100 border border-red-400 text-red-700 rounded">
            {error}
          </div>
        )}
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
              label="TelÃ©fono"
              value={formData.telefono}
              onChange={(e) => setFormData({ ...formData, telefono: e.target.value })}
            />
            <Input
              label="Celular"
              value={formData.celular}
              onChange={(e) => setFormData({ ...formData, celular: e.target.value })}
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
            <Input
              label="IdentificaciÃ³n"
              value={formData.identificacion}
              onChange={(e) => setFormData({ ...formData, identificacion: e.target.value })}
            />
            <Input
              label="Ciudad"
              value={formData.ciudad}
              onChange={(e) => setFormData({ ...formData, ciudad: e.target.value })}
            />
            <Input
              label="PaÃ­s"
              value={formData.pais}
              onChange={(e) => setFormData({ ...formData, pais: e.target.value })}
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
          <div className="mt-4">
            <label className="block text-sm font-medium text-gray-700 mb-1">Estado</label>
            <select
              className="input"
              value={formData.estado}
              onChange={(e) => setFormData({ ...formData, estado: e.target.value as any })}
            >
              <option value="ACTIVO">Activo</option>
              <option value="INACTIVO">Inactivo</option>
              <option value="BLOQUEADO">Bloqueado</option>
            </select>
          </div>
          <div className="mt-6 flex justify-end space-x-3">
            <Button
              type="button"
              variant="secondary"
              onClick={() => {
                setIsModalOpen(false);
                setEditingCliente(null);
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

export default Clientes;
