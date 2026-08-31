/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
import { useState, useEffect } from 'react';
import { Plus, Edit, Trash2, Search } from 'lucide-react';
import apiClient from '../services/api';
import { Contrato } from '../types';
import Button from '../components/Button';
import Input from '../components/Input';
import Modal from '../components/Modal';

const ContratosPage = () => {
  const [contratos, setContratos] = useState<Contrato[]>([]);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingContrato, setEditingContrato] = useState<Contrato | null>(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [formData, setFormData] = useState<Contrato>({
    codigo: '',
    nombre: '',
    descripcion: '',
    tipo: 'SERVICIO',
    estado: 'ACTIVO',
    clienteId: 0,
    fechaInicio: '',
    fechaFin: '',
    valor: 0,
    moneda: 'USD',
    periodoRenovacion: 'ANUAL',
    urlDocumento: '',
    condiciones: '',
    observaciones: '',
  });
  
  useEffect(() => {
    loadContratos();
  }, []);
  
  const loadContratos = async () => {
    try {
      const response = await apiClient.get('/api/contratos');
      setContratos(response.data);
    } catch (error) {
      console.error('Error loading contratos:', error);
    }
  };
  
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      if (editingContrato?.id) {
        await apiClient.put(`/api/contratos/${editingContrato.id}`, formData);
      } else {
        await apiClient.post('/api/contratos', formData);
      }
      loadContratos();
      setIsModalOpen(false);
      setEditingContrato(null);
    } catch (error) {
      console.error('Error saving contrato:', error);
    }
  };
  
  const handleEdit = (contrato: Contrato) => {
    setEditingContrato(contrato);
    setFormData(contrato);
    setIsModalOpen(true);
  };
  
  const handleDelete = async (id: number) => {
    if (window.confirm('¿Está seguro de eliminar este contrato?')) {
      try {
        await apiClient.delete(`/api/contratos/${id}`);
        loadContratos();
      } catch (error) {
        console.error('Error deleting contrato:', error);
      }
    }
  };
  
  const filteredContratos = contratos.filter(
    (cont) =>
      cont.nombre.toLowerCase().includes(searchTerm.toLowerCase()) ||
      cont.codigo.toLowerCase().includes(searchTerm.toLowerCase())
  );
  
  return (
    <div>
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-3xl font-bold text-gray-900">Contratos</h1>
        <Button onClick={() => setIsModalOpen(true)}>
          <Plus className="w-4 h-4 mr-2" />
          Nuevo Contrato
        </Button>
      </div>
      
      <div className="card mb-6">
        <div className="relative">
          <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-5 h-5" />
          <Input placeholder="Buscar contratos..." value={searchTerm} onChange={(e) => setSearchTerm(e.target.value)} className="pl-10" />
        </div>
      </div>
      
      <div className="card">
        <table className="table">
          <thead>
            <tr>
              <th>Código</th>
              <th>Nombre</th>
              <th>Tipo</th>
              <th>Estado</th>
              <th>Fecha Inicio</th>
              <th>Fecha Fin</th>
              <th>Valor</th>
              <th>Acciones</th>
            </tr>
          </thead>
          <tbody>
            {filteredContratos.map((cont) => (
              <tr key={cont.id}>
                <td>{cont.codigo}</td>
                <td>{cont.nombre}</td>
                <td>{cont.tipo}</td>
                <td>
                  <span className={`px-2 py-1 rounded-full text-xs font-medium ${
                    cont.estado === 'ACTIVO' ? 'bg-green-100 text-green-800' : 'bg-gray-100 text-gray-800'
                  }`}>
                    {cont.estado}
                  </span>
                </td>
                <td>{cont.fechaInicio}</td>
                <td>{cont.fechaFin}</td>
                <td>{cont.moneda} {cont.valor}</td>
                <td>
                  <div className="flex space-x-2">
                    <Button variant="secondary" onClick={() => handleEdit(cont)}><Edit className="w-4 h-4" /></Button>
                    <Button variant="danger" onClick={() => handleDelete(cont.id!)}><Trash2 className="w-4 h-4" /></Button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        {filteredContratos.length === 0 && <p className="text-center text-gray-500 py-8">No hay contratos registrados</p>}
      </div>
      
      <Modal isOpen={isModalOpen} onClose={() => { setIsModalOpen(false); setEditingContrato(null); }} title={editingContrato?.id ? 'Editar Contrato' : 'Nuevo Contrato'}>
        <form onSubmit={handleSubmit}>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <Input label="Código" value={formData.codigo} onChange={(e) => setFormData({ ...formData, codigo: e.target.value })} required />
            <Input label="Nombre" value={formData.nombre} onChange={(e) => setFormData({ ...formData, nombre: e.target.value })} required />
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Tipo</label>
              <select className="input" value={formData.tipo} onChange={(e) => setFormData({ ...formData, tipo: e.target.value as any })}>
                <option value="SERVICIO">Servicio</option>
                <option value="PRODUCTO">Producto</option>
                <option value="MANTENIMIENTO">Mantenimiento</option>
                <option value="LICENCIA">Licencia</option>
              </select>
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Estado</label>
              <select className="input" value={formData.estado} onChange={(e) => setFormData({ ...formData, estado: e.target.value as any })}>
                <option value="ACTIVO">Activo</option>
                <option value="VENCIDO">Vencido</option>
                <option value="CANCELADO">Cancelado</option>
                <option value="RENOVADO">Renovado</option>
              </select>
            </div>
            <Input label="ID Cliente" type="number" value={formData.clienteId} onChange={(e) => setFormData({ ...formData, clienteId: Number(e.target.value) })} />
            <Input label="Fecha Inicio" type="date" value={formData.fechaInicio} onChange={(e) => setFormData({ ...formData, fechaInicio: e.target.value })} />
            <Input label="Fecha Fin" type="date" value={formData.fechaFin} onChange={(e) => setFormData({ ...formData, fechaFin: e.target.value })} />
            <Input label="Valor" type="number" value={formData.valor} onChange={(e) => setFormData({ ...formData, valor: Number(e.target.value) })} />
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Moneda</label>
              <select className="input" value={formData.moneda} onChange={(e) => setFormData({ ...formData, moneda: e.target.value as any })}>
                <option value="USD">USD</option>
                <option value="EUR">EUR</option>
                <option value="COP">COP</option>
              </select>
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Periodo Renovación</label>
              <select className="input" value={formData.periodoRenovacion} onChange={(e) => setFormData({ ...formData, periodoRenovacion: e.target.value as any })}>
                <option value="MENSUAL">Mensual</option>
                <option value="TRIMESTRAL">Trimestral</option>
                <option value="ANUAL">Anual</option>
              </select>
            </div>
          </div>
          <div className="mt-4">
            <label className="block text-sm font-medium text-gray-700 mb-1">Descripción</label>
            <textarea className="input" rows={2} value={formData.descripcion} onChange={(e) => setFormData({ ...formData, descripcion: e.target.value })} />
          </div>
          <div className="mt-4">
            <label className="block text-sm font-medium text-gray-700 mb-1">URL Documento</label>
            <Input value={formData.urlDocumento} onChange={(e) => setFormData({ ...formData, urlDocumento: e.target.value })} />
          </div>
          <div className="mt-4">
            <label className="block text-sm font-medium text-gray-700 mb-1">Condiciones</label>
            <textarea className="input" rows={2} value={formData.condiciones} onChange={(e) => setFormData({ ...formData, condiciones: e.target.value })} />
          </div>
          <div className="mt-4">
            <label className="block text-sm font-medium text-gray-700 mb-1">Observaciones</label>
            <textarea className="input" rows={2} value={formData.observaciones} onChange={(e) => setFormData({ ...formData, observaciones: e.target.value })} />
          </div>
          <div className="mt-6 flex justify-end space-x-3">
            <Button type="button" variant="secondary" onClick={() => { setIsModalOpen(false); setEditingContrato(null); }}>Cancelar</Button>
            <Button type="submit">Guardar</Button>
          </div>
        </form>
      </Modal>
    </div>
  );
};

export default ContratosPage;
