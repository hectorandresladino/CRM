import { useState, useEffect } from 'react';
import { Plus, Edit, Trash2, Search } from 'lucide-react';
import apiClient from '../services/api';
import { CampanaMarketing } from '../types';
import Button from '../components/Button';
import Input from '../components/Input';
import Modal from '../components/Modal';

const CampanasMarketingPage = () => {
  const [campanas, setCampanas] = useState<CampanaMarketing[]>([]);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingCampana, setEditingCampana] = useState<CampanaMarketing | null>(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [formData, setFormData] = useState<CampanaMarketing>({
    nombre: '',
    descripcion: '',
    tipo: 'EMAIL',
    estado: 'ACTIVA',
    fechaInicio: '',
    fechaFin: '',
    presupuesto: 0,
    presupuestoGastado: 0,
    objetivo: '',
    segmento: '',
  });
  
  useEffect(() => {
    loadCampanas();
  }, []);
  
  const loadCampanas = async () => {
    try {
      const response = await apiClient.get('/api/campanas-marketing');
      setCampanas(response.data);
    } catch (error) {
      console.error('Error loading campanas:', error);
    }
  };
  
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      if (editingCampana?.id) {
        await apiClient.put(`/api/campanas-marketing/${editingCampana.id}`, formData);
      } else {
        await apiClient.post('/api/campanas-marketing', formData);
      }
      loadCampanas();
      setIsModalOpen(false);
      setEditingCampana(null);
    } catch (error) {
      console.error('Error saving campana:', error);
    }
  };
  
  const handleEdit = (campana: CampanaMarketing) => {
    setEditingCampana(campana);
    setFormData(campana);
    setIsModalOpen(true);
  };
  
  const handleDelete = async (id: number) => {
    if (window.confirm('¿Está seguro de eliminar esta campaña?')) {
      try {
        await apiClient.delete(`/api/campanas-marketing/${id}`);
        loadCampanas();
      } catch (error) {
        console.error('Error deleting campana:', error);
      }
    }
  };
  
  const filteredCampanas = campanas.filter(
    (camp) =>
      camp.nombre.toLowerCase().includes(searchTerm.toLowerCase()) ||
      camp.descripcion?.toLowerCase().includes(searchTerm.toLowerCase())
  );
  
  return (
    <div>
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-3xl font-bold text-gray-900">Campañas de Marketing</h1>
        <Button onClick={() => setIsModalOpen(true)}>
          <Plus className="w-4 h-4 mr-2" />
          Nueva Campaña
        </Button>
      </div>
      
      <div className="card mb-6">
        <div className="relative">
          <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-5 h-5" />
          <Input placeholder="Buscar campañas..." value={searchTerm} onChange={(e) => setSearchTerm(e.target.value)} className="pl-10" />
        </div>
      </div>
      
      <div className="card">
        <table className="table">
          <thead>
            <tr>
              <th>Nombre</th>
              <th>Tipo</th>
              <th>Estado</th>
              <th>Fecha Inicio</th>
              <th>Fecha Fin</th>
              <th>Presupuesto</th>
              <th>Acciones</th>
            </tr>
          </thead>
          <tbody>
            {filteredCampanas.map((camp) => (
              <tr key={camp.id}>
                <td>{camp.nombre}</td>
                <td>{camp.tipo}</td>
                <td>
                  <span className={`px-2 py-1 rounded-full text-xs font-medium ${
                    camp.estado === 'ACTIVA' ? 'bg-green-100 text-green-800' : 'bg-gray-100 text-gray-800'
                  }`}>
                    {camp.estado}
                  </span>
                </td>
                <td>{camp.fechaInicio}</td>
                <td>{camp.fechaFin}</td>
                <td>${camp.presupuesto}</td>
                <td>
                  <div className="flex space-x-2">
                    <Button variant="secondary" onClick={() => handleEdit(camp)}><Edit className="w-4 h-4" /></Button>
                    <Button variant="danger" onClick={() => handleDelete(camp.id!)}><Trash2 className="w-4 h-4" /></Button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        {filteredCampanas.length === 0 && <p className="text-center text-gray-500 py-8">No hay campañas registradas</p>}
      </div>
      
      <Modal isOpen={isModalOpen} onClose={() => { setIsModalOpen(false); setEditingCampana(null); }} title={editingCampana?.id ? 'Editar Campaña' : 'Nueva Campaña'}>
        <form onSubmit={handleSubmit}>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <Input label="Nombre" value={formData.nombre} onChange={(e) => setFormData({ ...formData, nombre: e.target.value })} required />
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Tipo</label>
              <select className="input" value={formData.tipo} onChange={(e) => setFormData({ ...formData, tipo: e.target.value as any })}>
                <option value="EMAIL">Email</option>
                <option value="SOCIAL">Redes Sociales</option>
                <option value="ADS">Publicidad</option>
                <option value="EVENTO">Evento</option>
              </select>
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Estado</label>
              <select className="input" value={formData.estado} onChange={(e) => setFormData({ ...formData, estado: e.target.value as any })}>
                <option value="ACTIVA">Activa</option>
                <option value="PAUSADA">Pausada</option>
                <option value="FINALIZADA">Finalizada</option>
              </select>
            </div>
            <Input label="Fecha Inicio" type="date" value={formData.fechaInicio} onChange={(e) => setFormData({ ...formData, fechaInicio: e.target.value })} />
            <Input label="Fecha Fin" type="date" value={formData.fechaFin} onChange={(e) => setFormData({ ...formData, fechaFin: e.target.value })} />
            <Input label="Presupuesto" type="number" value={formData.presupuesto} onChange={(e) => setFormData({ ...formData, presupuesto: Number(e.target.value) })} />
            <Input label="Segmento" value={formData.segmento} onChange={(e) => setFormData({ ...formData, segmento: e.target.value })} />
          </div>
          <div className="mt-4">
            <label className="block text-sm font-medium text-gray-700 mb-1">Descripción</label>
            <textarea className="input" rows={3} value={formData.descripcion} onChange={(e) => setFormData({ ...formData, descripcion: e.target.value })} />
          </div>
          <div className="mt-4">
            <label className="block text-sm font-medium text-gray-700 mb-1">Objetivo</label>
            <textarea className="input" rows={2} value={formData.objetivo} onChange={(e) => setFormData({ ...formData, objetivo: e.target.value })} />
          </div>
          <div className="mt-6 flex justify-end space-x-3">
            <Button type="button" variant="secondary" onClick={() => { setIsModalOpen(false); setEditingCampana(null); }}>Cancelar</Button>
            <Button type="submit">Guardar</Button>
          </div>
        </form>
      </Modal>
    </div>
  );
};

export default CampanasMarketingPage;
