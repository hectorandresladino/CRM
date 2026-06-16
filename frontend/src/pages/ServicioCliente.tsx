import { useState, useEffect } from 'react';
import { Plus, Edit, Trash2, Search } from 'lucide-react';
import apiClient from '../services/api';
import type { ServicioCliente as ServicioClienteType } from '../types';
import Button from '../components/Button';
import Input from '../components/Input';
import Modal from '../components/Modal';

const ServicioClientePage = () => {
  const [servicios, setServicios] = useState<ServicioClienteType[]>([]);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingServicio, setEditingServicio] = useState<ServicioClienteType | null>(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [formData, setFormData] = useState<ServicioClienteType> ({
    clienteId: 0,
    codigo: '',
    asunto: '',
    descripcion: '',
    tipo: 'PREGUNTA',
    prioridad: 'MEDIA',
    canal: 'EMAIL',
    estado: 'ABIERTO',
    asignadoA: '',
    resolucion: '',
    notas: '',
  });
  
  useEffect(() => {
    loadServicios();
  }, []);
  
  const loadServicios = async () => {
    try {
      const response = await apiClient.get('/api/servicio-cliente');
      setServicios(response.data);
    } catch (error) {
      console.error('Error loading servicios:', error);
    }
  };
  
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      if (editingServicio?.id) {
        await apiClient.put(`/api/servicio-cliente/${editingServicio.id}`, formData);
      } else {
        await apiClient.post('/api/servicio-cliente', formData);
      }
      loadServicios();
      setIsModalOpen(false);
      setEditingServicio(null);
    } catch (error) {
      console.error('Error saving servicio:', error);
    }
  };
  
  const handleEdit = (servicio: ServicioClienteType) => {
    setEditingServicio(servicio);
    setFormData(servicio);
    setIsModalOpen(true);
  };
  
  const handleDelete = async (id: number) => {
    if (window.confirm('¿Está seguro de eliminar este servicio?')) {
      try {
        await apiClient.delete(`/api/servicio-cliente/${id}`);
        loadServicios();
      } catch (error) {
        console.error('Error deleting servicio:', error);
      }
    }
  };
  
  const filteredServicios = servicios.filter(
    (serv) =>
      serv.asunto.toLowerCase().includes(searchTerm.toLowerCase()) ||
      serv.codigo.toLowerCase().includes(searchTerm.toLowerCase())
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
        <h1 className="text-3xl font-bold text-gray-900">Servicio al Cliente</h1>
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
              <th>Código</th>
              <th>Asunto</th>
              <th>Tipo</th>
              <th>Prioridad</th>
              <th>Canal</th>
              <th>Estado</th>
              <th>Acciones</th>
            </tr>
          </thead>
          <tbody>
            {filteredServicios.map((serv) => (
              <tr key={serv.id}>
                <td>{serv.codigo}</td>
                <td>{serv.asunto}</td>
                <td>{serv.tipo}</td>
                <td>
                  <span className={`px-2 py-1 rounded-full text-xs font-medium ${getPrioridadColor(serv.prioridad)}`}>
                    {serv.prioridad}
                  </span>
                </td>
                <td>{serv.canal}</td>
                <td>
                  <span className={`px-2 py-1 rounded-full text-xs font-medium ${
                    serv.estado === 'RESUELTO' || serv.estado === 'CERRADO' ? 'bg-green-100 text-green-800' : 'bg-yellow-100 text-yellow-800'
                  }`}>
                    {serv.estado}
                  </span>
                </td>
                <td>
                  <div className="flex space-x-2">
                    <Button variant="secondary" onClick={() => handleEdit(serv)}><Edit className="w-4 h-4" /></Button>
                    <Button variant="danger" onClick={() => handleDelete(serv.id!)}><Trash2 className="w-4 h-4" /></Button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        {filteredServicios.length === 0 && <p className="text-center text-gray-500 py-8">No hay tickets registrados</p>}
      </div>
      
      <Modal isOpen={isModalOpen} onClose={() => { setIsModalOpen(false); setEditingServicio(null); }} title={editingServicio?.id ? 'Editar Ticket' : 'Nuevo Ticket'}>
        <form onSubmit={handleSubmit}>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <Input label="Código" value={formData.codigo} onChange={(e) => setFormData({ ...formData, codigo: e.target.value })} required />
            <Input label="ID Cliente" type="number" value={formData.clienteId} onChange={(e) => setFormData({ ...formData, clienteId: Number(e.target.value) })} required />
            <Input label="Asunto" value={formData.asunto} onChange={(e) => setFormData({ ...formData, asunto: e.target.value })} required />
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Tipo</label>
              <select className="input" value={formData.tipo} onChange={(e) => setFormData({ ...formData, tipo: e.target.value as any })}>
                <option value="PREGUNTA">Pregunta</option>
                <option value="QUEJA">Queja</option>
                <option value="RECLAMO">Reclamo</option>
                <option value="SUGERENCIA">Sugerencia</option>
                <option value="FELICITACION">Felicitación</option>
              </select>
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Prioridad</label>
              <select className="input" value={formData.prioridad} onChange={(e) => setFormData({ ...formData, prioridad: e.target.value as any })}>
                <option value="BAJA">Baja</option>
                <option value="MEDIA">Media</option>
                <option value="ALTA">Alta</option>
                <option value="URGENTE">Urgente</option>
                <option value="CRITICA">Crítica</option>
              </select>
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Canal</label>
              <select className="input" value={formData.canal} onChange={(e) => setFormData({ ...formData, canal: e.target.value as any })}>
                <option value="EMAIL">Email</option>
                <option value="TELEFONO">Teléfono</option>
                <option value="CHAT">Chat</option>
                <option value="WHATSAPP">WhatsApp</option>
                <option value="RED_SOCIAL">Red Social</option>
                <option value="PRESENCIAL">Presencial</option>
              </select>
            </div>
            <Input label="Asignado A" value={formData.asignadoA} onChange={(e) => setFormData({ ...formData, asignadoA: e.target.value })} />
          </div>
          <div className="mt-4">
            <label className="block text-sm font-medium text-gray-700 mb-1">Descripción</label>
            <textarea className="input" rows={3} value={formData.descripcion} onChange={(e) => setFormData({ ...formData, descripcion: e.target.value })} required />
          </div>
          <div className="mt-4">
            <label className="block text-sm font-medium text-gray-700 mb-1">Resolución</label>
            <textarea className="input" rows={2} value={formData.resolucion} onChange={(e) => setFormData({ ...formData, resolucion: e.target.value })} />
          </div>
          <div className="mt-4">
            <label className="block text-sm font-medium text-gray-700 mb-1">Notas</label>
            <textarea className="input" rows={2} value={formData.notas} onChange={(e) => setFormData({ ...formData, notas: e.target.value })} />
          </div>
          <div className="mt-6 flex justify-end space-x-3">
            <Button type="button" variant="secondary" onClick={() => { setIsModalOpen(false); setEditingServicio(null); }}>Cancelar</Button>
            <Button type="submit">Guardar</Button>
          </div>
        </form>
      </Modal>
    </div>
  );
};

export default ServicioClientePage;
