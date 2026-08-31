/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
import { useState, useEffect } from 'react';
import { Plus, Edit, Trash2, Search } from 'lucide-react';
import apiClient from '../services/api';
import { Venta } from '../types';
import Button from '../components/Button';
import Input from '../components/Input';
import Modal from '../components/Modal';

const Ventas = () => {
  const [ventas, setVentas] = useState<Venta[]>([]);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingVenta, setEditingVenta] = useState<Venta | null>(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [formData, setFormData] = useState<Venta>({
    clienteId: 0,
    codigo: '',
    descripcion: '',
    monto: 0,
    descuento: 0,
    impuesto: 0,
    total: 0,
    comision: 0,
    vendedor: '',
    notas: '',
    estado: 'PENDIENTE',
    metodoPago: 'TRANSFERENCIA',
  });
  
  useEffect(() => {
    loadVentas();
  }, []);
  
  const loadVentas = async () => {
    try {
      const response = await apiClient.get('/api/ventas');
      setVentas(response.data);
    } catch (error) {
      console.error('Error loading ventas:', error);
    }
  };
  
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      if (editingVenta?.id) {
        await apiClient.put(`/api/ventas/${editingVenta.id}`, formData);
      } else {
        await apiClient.post('/api/ventas', formData);
      }
      loadVentas();
      setIsModalOpen(false);
      setEditingVenta(null);
    } catch (error) {
      console.error('Error saving venta:', error);
    }
  };
  
  const handleEdit = (venta: Venta) => {
    setEditingVenta(venta);
    setFormData(venta);
    setIsModalOpen(true);
  };
  
  const handleDelete = async (id: number) => {
    if (window.confirm('¿Está seguro de eliminar esta venta?')) {
      try {
        await apiClient.delete(`/api/ventas/${id}`);
        loadVentas();
      } catch (error) {
        console.error('Error deleting venta:', error);
      }
    }
  };
  
  const filteredVentas = ventas.filter(
    (venta) =>
      venta.codigo.toLowerCase().includes(searchTerm.toLowerCase()) ||
      venta.descripcion.toLowerCase().includes(searchTerm.toLowerCase()) ||
      venta.vendedor?.toLowerCase().includes(searchTerm.toLowerCase())
  );
  
  return (
    <div>
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-3xl font-bold text-gray-900">Ventas</h1>
        <Button onClick={() => setIsModalOpen(true)}>
          <Plus className="w-4 h-4 mr-2" />
          Nueva Venta
        </Button>
      </div>
      
      <div className="card mb-6">
        <div className="relative">
          <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-5 h-5" />
          <Input
            placeholder="Buscar ventas..."
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
              <th>Código</th>
              <th>Descripción</th>
              <th>Monto</th>
              <th>Total</th>
              <th>Vendedor</th>
              <th>Estado</th>
              <th>Acciones</th>
            </tr>
          </thead>
          <tbody>
            {filteredVentas.map((venta) => (
              <tr key={venta.id}>
                <td>{venta.codigo}</td>
                <td>{venta.descripcion}</td>
                <td>${venta.monto}</td>
                <td>${venta.total}</td>
                <td>{venta.vendedor}</td>
                <td>
                  <span className={`px-2 py-1 rounded-full text-xs font-medium ${
                    venta.estado === 'CERRADA' ? 'bg-green-100 text-green-800' : 'bg-yellow-100 text-yellow-800'
                  }`}>
                    {venta.estado}
                  </span>
                </td>
                <td>
                  <div className="flex space-x-2">
                    <Button variant="secondary" size="sm" onClick={() => handleEdit(venta)}>
                      <Edit className="w-4 h-4" />
                    </Button>
                    <Button variant="danger" size="sm" onClick={() => handleDelete(venta.id!)}>
                      <Trash2 className="w-4 h-4" />
                    </Button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        {filteredVentas.length === 0 && (
          <p className="text-center text-gray-500 py-8">No hay ventas registradas</p>
        )}
      </div>
      
      <Modal isOpen={isModalOpen} onClose={() => { setIsModalOpen(false); setEditingVenta(null); }} title={editingVenta?.id ? 'Editar Venta' : 'Nueva Venta'}>
        <form onSubmit={handleSubmit}>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <Input label="Código" value={formData.codigo} onChange={(e) => setFormData({ ...formData, codigo: e.target.value })} required />
            <Input label="ID Cliente" type="number" value={formData.clienteId} onChange={(e) => setFormData({ ...formData, clienteId: Number(e.target.value) })} required />
            <Input label="Descripción" value={formData.descripcion} onChange={(e) => setFormData({ ...formData, descripcion: e.target.value })} required />
            <Input label="Monto" type="number" value={formData.monto} onChange={(e) => setFormData({ ...formData, monto: Number(e.target.value) })} required />
            <Input label="Descuento" type="number" value={formData.descuento} onChange={(e) => setFormData({ ...formData, descuento: Number(e.target.value) })} />
            <Input label="Impuesto" type="number" value={formData.impuesto} onChange={(e) => setFormData({ ...formData, impuesto: Number(e.target.value) })} />
            <Input label="Vendedor" value={formData.vendedor} onChange={(e) => setFormData({ ...formData, vendedor: e.target.value })} />
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Estado</label>
              <select className="input" value={formData.estado} onChange={(e) => setFormData({ ...formData, estado: e.target.value as any })}>
                <option value="PENDIENTE">Pendiente</option>
                <option value="EN_PROCESO">En Proceso</option>
                <option value="CERRADA">Cerrada</option>
                <option value="CANCELADA">Cancelada</option>
              </select>
            </div>
          </div>
          <div className="mt-4">
            <label className="block text-sm font-medium text-gray-700 mb-1">Notas</label>
            <textarea className="input" rows={3} value={formData.notas} onChange={(e) => setFormData({ ...formData, notas: e.target.value })} />
          </div>
          <div className="mt-6 flex justify-end space-x-3">
            <Button type="button" variant="secondary" onClick={() => { setIsModalOpen(false); setEditingVenta(null); }}>Cancelar</Button>
            <Button type="submit">Guardar</Button>
          </div>
        </form>
      </Modal>
    </div>
  );
};

export default Ventas;
