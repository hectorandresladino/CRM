/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
import { useState, useEffect } from 'react';
import { Plus, Edit, Trash2, Search } from 'lucide-react';
import apiClient from '../services/api';
import { PQRS } from '../types';
import Button from '../components/Button';
import Input from '../components/Input';
import Modal from '../components/Modal';

const PQRSPage = () => {
  const [pqrs, setPqrs] = useState<PQRS[]>([]);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingPqrs, setEditingPqrs] = useState<PQRS | null>(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [formData, setFormData] = useState<PQRS>({
    codigo: '',
    asunto: '',
    descripcion: '',
    tipo: 'QUEJA',
    prioridad: 'MEDIA',
    estado: 'ABIERTO',
    clienteId: 0,
    canal: 'EMAIL',
    asignadoA: '',
    resolucion: '',
    notas: '',
  });
  
  useEffect(() => {
    loadPqrs();
  }, []);
  
  const loadPqrs = async () => {
    try {
      const response = await apiClient.get('/api/pqrs');
      setPqrs(response.data);
    } catch (error) {
      console.error('Error loading pqrs:', error);
    }
  };
  
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      if (editingPqrs?.id) {
        await apiClient.put(`/api/pqrs/${editingPqrs.id}`, formData);
      } else {
        await apiClient.post('/api/pqrs', formData);
      }
      loadPqrs();
      setIsModalOpen(false);
      setEditingPqrs(null);
    } catch (error) {
      console.error('Error saving pqrs:', error);
    }
  };
  
  const handleEdit = (pqrs: PQRS) => {
    setEditingPqrs(pqrs);
    setFormData(pqrs);
    setIsModalOpen(true);
  };
  
  const handleDelete = async (id: number) => {
    if (window.confirm('Â¿EstÃ¡ seguro de eliminar este PQRS?')) {
      try {
        await apiClient.delete(`/api/pqrs/${id}`);
        loadPqrs();
      } catch (error) {
        console.error('Error deleting pqrs:', error);
      }
    }
  };
  
  const filteredPqrs = pqrs.filter(
    (p) =>
      p.asunto.toLowerCase().includes(searchTerm.toLowerCase()) ||
      p.codigo.toLowerCase().includes(searchTerm.toLowerCase())
  );
  
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
        <h1 className="text-3xl font-bold text-gray-900">PQRS</h1>
        <Button onClick={() => setIsModalOpen(true)}>
          <Plus className="w-4 h-4 mr-2" />
          Nuevo PQRS
        </Button>
      </div>
      
      <div className="card mb-6">
        <div className="relative">
          <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-5 h-5" />
          <Input placeholder="Buscar PQRS..." value={searchTerm} onChange={(e) => setSearchTerm(e.target.value)} className="pl-10" />
        </div>
      </div>
      
      <div className="card">
        <table className="table">
          <thead>
            <tr>
              <th>CÃ³digo</th>
              <th>Asunto</th>
              <th>Tipo</th>
              <th>Prioridad</th>
              <th>Estado</th>
              <th>Canal</th>
              <th>Acciones</th>
            </tr>
          </thead>
          <tbody>
            {filteredPqrs.map((p) => (
              <tr key={p.id}>
                <td>{p.codigo}</td>
                <td>{p.asunto}</td>
                <td>{p.tipo}</td>
                <td>
                  <span className={`px-2 py-1 rounded-full text-xs font-medium ${getPrioridadColor(p.prioridad)}`}>
                    {p.prioridad}
                  </span>
                </td>
                <td>
                  <span className={`px-2 py-1 rounded-full text-xs font-medium ${
                    p.estado === 'RESUELTO' ? 'bg-green-100 text-green-800' : 'bg-yellow-100 text-yellow-800'
                  }`}>
                    {p.estado}
                  </span>
                </td>
                <td>{p.canal}</td>
                <td>
                  <div className="flex space-x-2">
                    <Button variant="secondary" onClick={() => handleEdit(p)}><Edit className="w-4 h-4" /></Button>
                    <Button variant="danger" onClick={() => handleDelete(p.id!)}><Trash2 className="w-4 h-4" /></Button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        {filteredPqrs.length === 0 && <p className="text-center text-gray-500 py-8">No hay PQRS registrados</p>}
      </div>
      
      <Modal isOpen={isModalOpen} onClose={() => { setIsModalOpen(false); setEditingPqrs(null); }} title={editingPqrs?.id ? 'Editar PQRS' : 'Nuevo PQRS'}>
        <form onSubmit={handleSubmit}>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <Input label="CÃ³digo" value={formData.codigo} onChange={(e) => setFormData({ ...formData, codigo: e.target.value })} required />
            <Input label="Asunto" value={formData.asunto} onChange={(e) => setFormData({ ...formData, asunto: e.target.value })} required />
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Tipo</label>
              <select className="input" value={formData.tipo} onChange={(e) => setFormData({ ...formData, tipo: e.target.value as any })}>
                <option value="PREGUNTA">Pregunta</option>
                <option value="QUEJA">Queja</option>
                <option value="RECLAMO">Reclamo</option>
                <option value="SUGERENCIA">Sugerencia</option>
              </select>
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Prioridad</label>
              <select className="input" value={formData.prioridad} onChange={(e) => setFormData({ ...formData, prioridad: e.target.value as any })}>
                <option value="BAJA">Baja</option>
                <option value="MEDIA">Media</option>
                <option value="ALTA">Alta</option>
                <option value="URGENTE">Urgente</option>
              </select>
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Estado</label>
              <select className="input" value={formData.estado} onChange={(e) => setFormData({ ...formData, estado: e.target.value as any })}>
                <option value="ABIERTO">Abierto</option>
                <option value="EN_PROCESO">En Proceso</option>
                <option value="RESUELTO">Resuelto</option>
                <option value="CERRADO">Cerrado</option>
              </select>
            </div>
            <Input label="ID Cliente" type="number" value={formData.clienteId} onChange={(e) => setFormData({ ...formData, clienteId: Number(e.target.value) })} />
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Canal</label>
              <select className="input" value={formData.canal} onChange={(e) => setFormData({ ...formData, canal: e.target.value as any })}>
                <option value="EMAIL">Email</option>
                <option value="TELEFONO">TelÃ©fono</option>
                <option value="WEB">Web</option>
                <option value="PRESENCIAL">Presencial</option>
              </select>
            </div>
            <Input label="Asignado A" value={formData.asignadoA} onChange={(e) => setFormData({ ...formData, asignadoA: e.target.value })} />
          </div>
          <div className="mt-4">
            <label className="block text-sm font-medium text-gray-700 mb-1">DescripciÃ³n</label>
            <textarea className="input" rows={3} value={formData.descripcion} onChange={(e) => setFormData({ ...formData, descripcion: e.target.value })} required />
          </div>
          <div className="mt-4">
            <label className="block text-sm font-medium text-gray-700 mb-1">ResoluciÃ³n</label>
            <textarea className="input" rows={2} value={formData.resolucion} onChange={(e) => setFormData({ ...formData, resolucion: e.target.value })} />
          </div>
          <div className="mt-4">
            <label className="block text-sm font-medium text-gray-700 mb-1">Notas</label>
            <textarea className="input" rows={2} value={formData.notas} onChange={(e) => setFormData({ ...formData, notas: e.target.value })} />
          </div>
          <div className="mt-6 flex justify-end space-x-3">
            <Button type="button" variant="secondary" onClick={() => { setIsModalOpen(false); setEditingPqrs(null); }}>Cancelar</Button>
            <Button type="submit">Guardar</Button>
          </div>
        </form>
      </Modal>
    </div>
  );
};

export default PQRSPage;
