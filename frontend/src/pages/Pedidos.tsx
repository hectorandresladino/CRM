/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
import { useState, useEffect } from 'react';
import { Plus, Edit, Trash2, Search } from 'lucide-react';
import apiClient from '../services/api';
import { Pedido } from '../types';
import Button from '../components/Button';
import Input from '../components/Input';
import Modal from '../components/Modal';

const Pedidos = () => {
  const [pedidos, setPedidos] = useState<Pedido[]>([]);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingPedido, setEditingPedido] = useState<Pedido | null>(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [formData, setFormData] = useState<Pedido>({
    clienteId: 0,
    codigo: '',
    descripcion: '',
    subtotal: 0,
    descuento: 0,
    impuesto: 0,
    total: 0,
    costoEnvio: 0,
    direccionEnvio: '',
    ciudadEnvio: '',
    paisEnvio: '',
    codigoPostalEnvio: '',
    fechaEntregaEstimada: new Date().toISOString().split('T')[0],
    vendedor: '',
    notas: '',
    notasEnvio: '',
    estado: 'PENDIENTE',
    metodoEnvio: 'ESTANDAR',
  });
  
  useEffect(() => {
    loadPedidos();
  }, []);
  
  const loadPedidos = async () => {
    try {
      const response = await apiClient.get('/api/pedidos');
      setPedidos(response.data);
    } catch (error) {
      console.error('Error loading pedidos:', error);
    }
  };
  
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      if (editingPedido?.id) {
        await apiClient.put(`/api/pedidos/${editingPedido.id}`, formData);
      } else {
        await apiClient.post('/api/pedidos', formData);
      }
      loadPedidos();
      setIsModalOpen(false);
      setEditingPedido(null);
    } catch (error) {
      console.error('Error saving pedido:', error);
    }
  };
  
  const handleEdit = (pedido: Pedido) => {
    setEditingPedido(pedido);
    setFormData(pedido);
    setIsModalOpen(true);
  };
  
  const handleDelete = async (id: number) => {
    if (window.confirm('¿Está seguro de eliminar este pedido?')) {
      try {
        await apiClient.delete(`/api/pedidos/${id}`);
        loadPedidos();
      } catch (error) {
        console.error('Error deleting pedido:', error);
      }
    }
  };
  
  const filteredPedidos = pedidos.filter(
    (ped) =>
      ped.codigo.toLowerCase().includes(searchTerm.toLowerCase()) ||
      ped.descripcion.toLowerCase().includes(searchTerm.toLowerCase())
  );
  
  return (
    <div>
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-3xl font-bold text-gray-900">Pedidos</h1>
        <Button onClick={() => setIsModalOpen(true)}>
          <Plus className="w-4 h-4 mr-2" />
          Nuevo Pedido
        </Button>
      </div>
      
      <div className="card mb-6">
        <div className="relative">
          <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-5 h-5" />
          <Input placeholder="Buscar pedidos..." value={searchTerm} onChange={(e) => setSearchTerm(e.target.value)} className="pl-10" />
        </div>
      </div>
      
      <div className="card">
        <table className="table">
          <thead>
            <tr>
              <th>Código</th>
              <th>Descripción</th>
              <th>Total</th>
              <th>Fecha Entrega</th>
              <th>Estado</th>
              <th>Acciones</th>
            </tr>
          </thead>
          <tbody>
            {filteredPedidos.map((ped) => (
              <tr key={ped.id}>
                <td>{ped.codigo}</td>
                <td>{ped.descripcion}</td>
                <td>${ped.total}</td>
                <td>{ped.fechaEntregaEstimada}</td>
                <td>
                  <span className={`px-2 py-1 rounded-full text-xs font-medium ${
                    ped.estado === 'ENTREGADO' ? 'bg-green-100 text-green-800' : 
                    ped.estado === 'ENVIADO' ? 'bg-blue-100 text-blue-800' : 'bg-yellow-100 text-yellow-800'
                  }`}>
                    {ped.estado}
                  </span>
                </td>
                <td>
                  <div className="flex space-x-2">
                    <Button variant="secondary" size="sm" onClick={() => handleEdit(ped)}><Edit className="w-4 h-4" /></Button>
                    <Button variant="danger" size="sm" onClick={() => handleDelete(ped.id!)}><Trash2 className="w-4 h-4" /></Button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        {filteredPedidos.length === 0 && <p className="text-center text-gray-500 py-8">No hay pedidos registrados</p>}
      </div>
      
      <Modal isOpen={isModalOpen} onClose={() => { setIsModalOpen(false); setEditingPedido(null); }} title={editingPedido?.id ? 'Editar Pedido' : 'Nuevo Pedido'}>
        <form onSubmit={handleSubmit}>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <Input label="Código" value={formData.codigo} onChange={(e) => setFormData({ ...formData, codigo: e.target.value })} required />
            <Input label="ID Cliente" type="number" value={formData.clienteId} onChange={(e) => setFormData({ ...formData, clienteId: Number(e.target.value) })} required />
            <Input label="Descripción" value={formData.descripcion} onChange={(e) => setFormData({ ...formData, descripcion: e.target.value })} required />
            <Input label="Subtotal" type="number" value={formData.subtotal} onChange={(e) => setFormData({ ...formData, subtotal: Number(e.target.value) })} required />
            <Input label="Costo Envío" type="number" value={formData.costoEnvio} onChange={(e) => setFormData({ ...formData, costoEnvio: Number(e.target.value) })} />
            <Input label="Fecha Entrega Estimada" type="date" value={formData.fechaEntregaEstimada} onChange={(e) => setFormData({ ...formData, fechaEntregaEstimada: e.target.value })} required />
            <Input label="Dirección Envío" value={formData.direccionEnvio} onChange={(e) => setFormData({ ...formData, direccionEnvio: e.target.value })} />
            <Input label="Ciudad Envío" value={formData.ciudadEnvio} onChange={(e) => setFormData({ ...formData, ciudadEnvio: e.target.value })} />
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Estado</label>
              <select className="input" value={formData.estado} onChange={(e) => setFormData({ ...formData, estado: e.target.value as any })}>
                <option value="PENDIENTE">Pendiente</option>
                <option value="PROCESANDO">Procesando</option>
                <option value="ENVIADO">Enviado</option>
                <option value="ENTREGADO">Entregado</option>
                <option value="CANCELADO">Cancelado</option>
              </select>
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Método Envío</label>
              <select className="input" value={formData.metodoEnvio} onChange={(e) => setFormData({ ...formData, metodoEnvio: e.target.value as any })}>
                <option value="ESTANDAR">Estándar</option>
                <option value="EXPRESS">Express</option>
                <option value="RECOGIDA">Recogida</option>
              </select>
            </div>
          </div>
          <div className="mt-4">
            <label className="block text-sm font-medium text-gray-700 mb-1">Notas</label>
            <textarea className="input" rows={3} value={formData.notas} onChange={(e) => setFormData({ ...formData, notas: e.target.value })} />
          </div>
          <div className="mt-6 flex justify-end space-x-3">
            <Button type="button" variant="secondary" onClick={() => { setIsModalOpen(false); setEditingPedido(null); }}>Cancelar</Button>
            <Button type="submit">Guardar</Button>
          </div>
        </form>
      </Modal>
    </div>
  );
};

export default Pedidos;
