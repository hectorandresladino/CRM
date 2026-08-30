/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
import { useState, useEffect } from 'react';
import { Plus, Edit, Trash2, Search } from 'lucide-react';
import apiClient from '../services/api';
import { MesaAyuda } from '../types';
import Button from '../components/Button';
import Input from '../components/Input';
import Modal from '../components/Modal';

const MesaAyudaPage = () => {
  const [tickets, setTickets] = useState<MesaAyuda[]>([]);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingTicket, setEditingTicket] = useState<MesaAyuda | null>(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [formData, setFormData] = useState<MesaAyuda>({
    ticket: '',
    asunto: '',
    descripcion: '',
    categoria: 'TECNICO',
    prioridad: 'MEDIA',
    estado: 'ABIERTO',
    clienteId: 0,
    canal: 'EMAIL',
    asignadoA: '',
    solucion: '',
    notas: '',
  });
  
  useEffect(() => {
    loadTickets();
  }, []);
  
  const loadTickets = async () => {
    try {
      const response = await apiClient.get('/api/mesa-ayuda');
      setTickets(response.data);
    } catch (error) {
      console.error('Error loading tickets:', error);
    }
  };
  
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      if (editingTicket?.id) {
        await apiClient.put(`/api/mesa-ayuda/${editingTicket.id}`, formData);
      } else {
        await apiClient.post('/api/mesa-ayuda', formData);
      }
      loadTickets();
      setIsModalOpen(false);
      setEditingTicket(null);
    } catch (error) {
      console.error('Error saving ticket:', error);
    }
  };
  
  const handleEdit = (ticket: MesaAyuda) => {
    setEditingTicket(ticket);
    setFormData(ticket);
    setIsModalOpen(true);
  };
  
  const handleDelete = async (id: number) => {
    if (window.confirm('Â¿EstÃ¡ seguro de eliminar este ticket?')) {
      try {
        await apiClient.delete(`/api/mesa-ayuda/${id}`);
        loadTickets();
      } catch (error) {
        console.error('Error deleting ticket:', error);
      }
    }
  };
  
  const filteredTickets = tickets.filter(
    (t) =>
      t.asunto.toLowerCase().includes(searchTerm.toLowerCase()) ||
      t.ticket.toLowerCase().includes(searchTerm.toLowerCase())
  );
  
  const getPrioridadColor = (prioridad: string) => {
    const colors: Record<string, string> = {
      BAJA: 'bg-gray-100 text-gray-800',
      MEDIA: 'bg-blue-100 text-blue-800',
      ALTA: 'bg-orange-100 text-orange-800',
      URGENTE: 'bg-red-100 text-red-800',
      CRITICA: 'bg-red-600 text-white',
    };
    return colors[prioridad] || 'bg-gray-100 text-gray-800';
  };
  
  return (
    <div>
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-3xl font-bold text-gray-900">Mesa de Ayuda</h1>
        <Button onClick={() => setIsModalOpen(true)}>
          <Plus className="w-4 h-4 mr-2" />
          Nuevo Ticket
        </Button>
      </div>
      
      <div className="card mb-6">
        <div className="relative">
          <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-5 h-5" />
          <Input placeholder="Buscar tickets..." value={searchTerm} onChange={(e) => setSearchTerm(e.target.value)} className="pl-10" />
        </div>
      </div>
      
      <div className="card">
        <table className="table">
          <thead>
            <tr>
              <th>Ticket</th>
              <th>Asunto</th>
              <th>CategorÃ­a</th>
              <th>Prioridad</th>
              <th>Estado</th>
              <th>Asignado A</th>
              <th>Acciones</th>
            </tr>
          </thead>
          <tbody>
            {filteredTickets.map((t) => (
              <tr key={t.id}>
                <td>{t.ticket}</td>
                <td>{t.asunto}</td>
                <td>{t.categoria}</td>
                <td>
                  <span className={`px-2 py-1 rounded-full text-xs font-medium ${getPrioridadColor(t.prioridad)}`}>
                    {t.prioridad}
                  </span>
                </td>
                <td>
                  <span className={`px-2 py-1 rounded-full text-xs font-medium ${
                    t.estado === 'CERRADO' ? 'bg-green-100 text-green-800' : 'bg-yellow-100 text-yellow-800'
                  }`}>
                    {t.estado}
                  </span>
                </td>
                <td>{t.asignadoA}</td>
                <td>
                  <div className="flex space-x-2">
                    <Button variant="secondary" onClick={() => handleEdit(t)}><Edit className="w-4 h-4" /></Button>
                    <Button variant="danger" onClick={() => handleDelete(t.id!)}><Trash2 className="w-4 h-4" /></Button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        {filteredTickets.length === 0 && <p className="text-center text-gray-500 py-8">No hay tickets registrados</p>}
      </div>
      
      <Modal isOpen={isModalOpen} onClose={() => { setIsModalOpen(false); setEditingTicket(null); }} title={editingTicket?.id ? 'Editar Ticket' : 'Nuevo Ticket'}>
        <form onSubmit={handleSubmit}>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <Input label="Ticket" value={formData.ticket} onChange={(e) => setFormData({ ...formData, ticket: e.target.value })} required />
            <Input label="Asunto" value={formData.asunto} onChange={(e) => setFormData({ ...formData, asunto: e.target.value })} required />
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">CategorÃ­a</label>
              <select className="input" value={formData.categoria} onChange={(e) => setFormData({ ...formData, categoria: e.target.value as any })}>
                <option value="TECNICO">TÃ©cnico</option>
                <option value="FACTURACION">FacturaciÃ³n</option>
                <option value="PRODUCTO">Producto</option>
                <option value="SERVICIO">Servicio</option>
                <option value="OTRO">Otro</option>
              </select>
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Prioridad</label>
              <select className="input" value={formData.prioridad} onChange={(e) => setFormData({ ...formData, prioridad: e.target.value as any })}>
                <option value="BAJA">Baja</option>
                <option value="MEDIA">Media</option>
                <option value="ALTA">Alta</option>
                <option value="URGENTE">Urgente</option>
                <option value="CRITICA">CrÃ­tica</option>
              </select>
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Estado</label>
              <select className="input" value={formData.estado} onChange={(e) => setFormData({ ...formData, estado: e.target.value as any })}>
                <option value="ABIERTO">Abierto</option>
                <option value="ASIGNADO">Asignado</option>
                <option value="EN_PROCESO">En Proceso</option>
                <option value="CERRADO">Cerrado</option>
              </select>
            </div>
            <Input label="ID Cliente" type="number" value={formData.clienteId} onChange={(e) => setFormData({ ...formData, clienteId: Number(e.target.value) })} />
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Canal</label>
              <select className="input" value={formData.canal} onChange={(e) => setFormData({ ...formData, canal: e.target.value as any })}>
                <option value="EMAIL">Email</option>
                <option value="TELEFONO">TelÃ©fono</option>
                <option value="CHAT">Chat</option>
                <option value="WEB">Web</option>
              </select>
            </div>
            <Input label="Asignado A" value={formData.asignadoA} onChange={(e) => setFormData({ ...formData, asignadoA: e.target.value })} />
          </div>
          <div className="mt-4">
            <label className="block text-sm font-medium text-gray-700 mb-1">DescripciÃ³n</label>
            <textarea className="input" rows={3} value={formData.descripcion} onChange={(e) => setFormData({ ...formData, descripcion: e.target.value })} required />
          </div>
          <div className="mt-4">
            <label className="block text-sm font-medium text-gray-700 mb-1">SoluciÃ³n</label>
            <textarea className="input" rows={2} value={formData.solucion} onChange={(e) => setFormData({ ...formData, solucion: e.target.value })} />
          </div>
          <div className="mt-4">
            <label className="block text-sm font-medium text-gray-700 mb-1">Notas</label>
            <textarea className="input" rows={2} value={formData.notas} onChange={(e) => setFormData({ ...formData, notas: e.target.value })} />
          </div>
          <div className="mt-6 flex justify-end space-x-3">
            <Button type="button" variant="secondary" onClick={() => { setIsModalOpen(false); setEditingTicket(null); }}>Cancelar</Button>
            <Button type="submit">Guardar</Button>
          </div>
        </form>
      </Modal>
    </div>
  );
};

export default MesaAyudaPage;
