/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
import { useState, useEffect } from 'react';
import { Plus, Edit, Trash2, Search } from 'lucide-react';
import apiClient from '../services/api';
import { Factura } from '../types';
import Button from '../components/Button';
import Input from '../components/Input';
import Modal from '../components/Modal';

const FacturasPage = () => {
  const [facturas, setFacturas] = useState<Factura[]>([]);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingFactura, setEditingFactura] = useState<Factura | null>(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [formData, setFormData] = useState<Factura>({
    numero: '',
    descripcion: '',
    tipo: 'VENTA',
    estado: 'PENDIENTE',
    clienteId: 0,
    ventaId: 0,
    fechaEmision: '',
    fechaVencimiento: '',
    fechaPago: '',
    subtotal: 0,
    impuesto: 0,
    total: 0,
    moneda: 'USD',
    metodoPago: 'TRANSFERENCIA',
    urlFactura: '',
    notas: '',
  });
  
  useEffect(() => {
    loadFacturas();
  }, []);
  
  const loadFacturas = async () => {
    try {
      const response = await apiClient.get('/api/facturas');
      setFacturas(response.data);
    } catch (error) {
      console.error('Error loading facturas:', error);
    }
  };
  
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      if (editingFactura?.id) {
        await apiClient.put(`/api/facturas/${editingFactura.id}`, formData);
      } else {
        await apiClient.post('/api/facturas', formData);
      }
      loadFacturas();
      setIsModalOpen(false);
      setEditingFactura(null);
    } catch (error) {
      console.error('Error saving factura:', error);
    }
  };
  
  const handleEdit = (factura: Factura) => {
    setEditingFactura(factura);
    setFormData(factura);
    setIsModalOpen(true);
  };
  
  const handleDelete = async (id: number) => {
    if (window.confirm('Â¿EstÃ¡ seguro de eliminar esta factura?')) {
      try {
        await apiClient.delete(`/api/facturas/${id}`);
        loadFacturas();
      } catch (error) {
        console.error('Error deleting factura:', error);
      }
    }
  };
  
  const filteredFacturas = facturas.filter(
    (fact) =>
      fact.numero.toLowerCase().includes(searchTerm.toLowerCase())
  );
  
  return (
    <div>
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-3xl font-bold text-gray-900">FacturaciÃ³n</h1>
        <Button onClick={() => setIsModalOpen(true)}>
          <Plus className="w-4 h-4 mr-2" />
          Nueva Factura
        </Button>
      </div>
      
      <div className="card mb-6">
        <div className="relative">
          <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-5 h-5" />
          <Input placeholder="Buscar facturas..." value={searchTerm} onChange={(e) => setSearchTerm(e.target.value)} className="pl-10" />
        </div>
      </div>
      
      <div className="card">
        <table className="table">
          <thead>
            <tr>
              <th>NÃºmero</th>
              <th>Tipo</th>
              <th>Estado</th>
              <th>Fecha EmisiÃ³n</th>
              <th>Fecha Vencimiento</th>
              <th>Total</th>
              <th>Acciones</th>
            </tr>
          </thead>
          <tbody>
            {filteredFacturas.map((fact) => (
              <tr key={fact.id}>
                <td>{fact.numero}</td>
                <td>{fact.tipo}</td>
                <td>
                  <span className={`px-2 py-1 rounded-full text-xs font-medium ${
                    fact.estado === 'PAGADA' ? 'bg-green-100 text-green-800' : fact.estado === 'VENCIDA' ? 'bg-red-100 text-red-800' : 'bg-gray-100 text-gray-800'
                  }`}>
                    {fact.estado}
                  </span>
                </td>
                <td>{fact.fechaEmision}</td>
                <td>{fact.fechaVencimiento}</td>
                <td>{fact.moneda} {fact.total}</td>
                <td>
                  <div className="flex space-x-2">
                    <Button variant="secondary" onClick={() => handleEdit(fact)}><Edit className="w-4 h-4" /></Button>
                    <Button variant="danger" onClick={() => handleDelete(fact.id!)}><Trash2 className="w-4 h-4" /></Button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        {filteredFacturas.length === 0 && <p className="text-center text-gray-500 py-8">No hay facturas registradas</p>}
      </div>
      
      <Modal isOpen={isModalOpen} onClose={() => { setIsModalOpen(false); setEditingFactura(null); }} title={editingFactura?.id ? 'Editar Factura' : 'Nueva Factura'}>
        <form onSubmit={handleSubmit}>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <Input label="NÃºmero" value={formData.numero} onChange={(e) => setFormData({ ...formData, numero: e.target.value })} required />
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Tipo</label>
              <select className="input" value={formData.tipo} onChange={(e) => setFormData({ ...formData, tipo: e.target.value as any })}>
                <option value="VENTA">Venta</option>
                <option value="SERVICIO">Servicio</option>
                <option value="SUSCRIPCION">SuscripciÃ³n</option>
              </select>
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Estado</label>
              <select className="input" value={formData.estado} onChange={(e) => setFormData({ ...formData, estado: e.target.value as any })}>
                <option value="PENDIENTE">Pendiente</option>
                <option value="PAGADA">Pagada</option>
                <option value="VENCIDA">Vencida</option>
                <option value="ANULADA">Anulada</option>
              </select>
            </div>
            <Input label="ID Cliente" type="number" value={formData.clienteId} onChange={(e) => setFormData({ ...formData, clienteId: Number(e.target.value) })} />
            <Input label="ID Venta" type="number" value={formData.ventaId} onChange={(e) => setFormData({ ...formData, ventaId: Number(e.target.value) })} />
            <Input label="Fecha EmisiÃ³n" type="date" value={formData.fechaEmision} onChange={(e) => setFormData({ ...formData, fechaEmision: e.target.value })} />
            <Input label="Fecha Vencimiento" type="date" value={formData.fechaVencimiento} onChange={(e) => setFormData({ ...formData, fechaVencimiento: e.target.value })} />
            <Input label="Subtotal" type="number" value={formData.subtotal} onChange={(e) => setFormData({ ...formData, subtotal: Number(e.target.value) })} />
            <Input label="Impuesto" type="number" value={formData.impuesto} onChange={(e) => setFormData({ ...formData, impuesto: Number(e.target.value) })} />
            <Input label="Total" type="number" value={formData.total} onChange={(e) => setFormData({ ...formData, total: Number(e.target.value) })} />
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Moneda</label>
              <select className="input" value={formData.moneda} onChange={(e) => setFormData({ ...formData, moneda: e.target.value as any })}>
                <option value="USD">USD</option>
                <option value="EUR">EUR</option>
                <option value="COP">COP</option>
              </select>
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">MÃ©todo Pago</label>
              <select className="input" value={formData.metodoPago} onChange={(e) => setFormData({ ...formData, metodoPago: e.target.value as any })}>
                <option value="TRANSFERENCIA">Transferencia</option>
                <option value="TARJETA">Tarjeta</option>
                <option value="EFECTIVO">Efectivo</option>
                <option value="CHEQUE">Cheque</option>
              </select>
            </div>
          </div>
          <div className="mt-4">
            <label className="block text-sm font-medium text-gray-700 mb-1">DescripciÃ³n</label>
            <textarea className="input" rows={2} value={formData.descripcion} onChange={(e) => setFormData({ ...formData, descripcion: e.target.value })} />
          </div>
          <div className="mt-4">
            <label className="block text-sm font-medium text-gray-700 mb-1">URL Factura</label>
            <Input value={formData.urlFactura} onChange={(e) => setFormData({ ...formData, urlFactura: e.target.value })} />
          </div>
          <div className="mt-4">
            <label className="block text-sm font-medium text-gray-700 mb-1">Notas</label>
            <textarea className="input" rows={2} value={formData.notas} onChange={(e) => setFormData({ ...formData, notas: e.target.value })} />
          </div>
          <div className="mt-6 flex justify-end space-x-3">
            <Button type="button" variant="secondary" onClick={() => { setIsModalOpen(false); setEditingFactura(null); }}>Cancelar</Button>
            <Button type="submit">Guardar</Button>
          </div>
        </form>
      </Modal>
    </div>
  );
};

export default FacturasPage;
