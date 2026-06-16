import { useState, useEffect } from 'react';
import { Plus, Edit, Trash2, Search } from 'lucide-react';
import apiClient from '../services/api';
import { EncuestaSatisfaccion } from '../types';
import Button from '../components/Button';
import Input from '../components/Input';
import Modal from '../components/Modal';

const EncuestasSatisfaccionPage = () => {
  const [encuestas, setEncuestas] = useState<EncuestaSatisfaccion[]>([]);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingEncuesta, setEditingEncuesta] = useState<EncuestaSatisfaccion | null>(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [formData, setFormData] = useState<EncuestaSatisfaccion>({
    nombre: '',
    descripcion: '',
    tipo: 'PRODUCTO',
    estado: 'PENDIENTE',
    clienteId: 0,
    fechaEnvio: '',
    fechaRespuesta: '',
    calificacionGeneral: 0,
    comentarios: '',
    pregunta1: 0,
    pregunta2: 0,
    pregunta3: 0,
    pregunta4: 0,
    pregunta5: 0,
    recomendaria: false,
  });
  
  useEffect(() => {
    loadEncuestas();
  }, []);
  
  const loadEncuestas = async () => {
    try {
      const response = await apiClient.get('/api/encuestas-satisfaccion');
      setEncuestas(response.data);
    } catch (error) {
      console.error('Error loading encuestas:', error);
    }
  };
  
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      if (editingEncuesta?.id) {
        await apiClient.put(`/api/encuestas-satisfaccion/${editingEncuesta.id}`, formData);
      } else {
        await apiClient.post('/api/encuestas-satisfaccion', formData);
      }
      loadEncuestas();
      setIsModalOpen(false);
      setEditingEncuesta(null);
    } catch (error) {
      console.error('Error saving encuesta:', error);
    }
  };
  
  const handleEdit = (encuesta: EncuestaSatisfaccion) => {
    setEditingEncuesta(encuesta);
    setFormData(encuesta);
    setIsModalOpen(true);
  };
  
  const handleDelete = async (id: number) => {
    if (window.confirm('¿Está seguro de eliminar esta encuesta?')) {
      try {
        await apiClient.delete(`/api/encuestas-satisfaccion/${id}`);
        loadEncuestas();
      } catch (error) {
        console.error('Error deleting encuesta:', error);
      }
    }
  };
  
  const filteredEncuestas = encuestas.filter(
    (enc) =>
      enc.nombre.toLowerCase().includes(searchTerm.toLowerCase())
  );
  
  return (
    <div>
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-3xl font-bold text-gray-900">Encuestas de Satisfacción</h1>
        <Button onClick={() => setIsModalOpen(true)}>
          <Plus className="w-4 h-4 mr-2" />
          Nueva Encuesta
        </Button>
      </div>
      
      <div className="card mb-6">
        <div className="relative">
          <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-5 h-5" />
          <Input placeholder="Buscar encuestas..." value={searchTerm} onChange={(e) => setSearchTerm(e.target.value)} className="pl-10" />
        </div>
      </div>
      
      <div className="card">
        <table className="table">
          <thead>
            <tr>
              <th>Nombre</th>
              <th>Tipo</th>
              <th>Estado</th>
              <th>Fecha Envío</th>
              <th>Calificación</th>
              <th>Recomendaría</th>
              <th>Acciones</th>
            </tr>
          </thead>
          <tbody>
            {filteredEncuestas.map((enc) => (
              <tr key={enc.id}>
                <td>{enc.nombre}</td>
                <td>{enc.tipo}</td>
                <td>
                  <span className={`px-2 py-1 rounded-full text-xs font-medium ${
                    enc.estado === 'RESPONDIDA' ? 'bg-green-100 text-green-800' : 'bg-gray-100 text-gray-800'
                  }`}>
                    {enc.estado}
                  </span>
                </td>
                <td>{enc.fechaEnvio}</td>
                <td>{enc.calificacionGeneral}/5</td>
                <td>{enc.recomendaria ? '✓' : '✗'}</td>
                <td>
                  <div className="flex space-x-2">
                    <Button variant="secondary" onClick={() => handleEdit(enc)}><Edit className="w-4 h-4" /></Button>
                    <Button variant="danger" onClick={() => handleDelete(enc.id!)}><Trash2 className="w-4 h-4" /></Button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        {filteredEncuestas.length === 0 && <p className="text-center text-gray-500 py-8">No hay encuestas registradas</p>}
      </div>
      
      <Modal isOpen={isModalOpen} onClose={() => { setIsModalOpen(false); setEditingEncuesta(null); }} title={editingEncuesta?.id ? 'Editar Encuesta' : 'Nueva Encuesta'}>
        <form onSubmit={handleSubmit}>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <Input label="Nombre" value={formData.nombre} onChange={(e) => setFormData({ ...formData, nombre: e.target.value })} required />
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Tipo</label>
              <select className="input" value={formData.tipo} onChange={(e) => setFormData({ ...formData, tipo: e.target.value as any })}>
                <option value="PRODUCTO">Producto</option>
                <option value="SERVICIO">Servicio</option>
                <option value="ATENCION">Atención</option>
                <option value="GENERAL">General</option>
              </select>
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Estado</label>
              <select className="input" value={formData.estado} onChange={(e) => setFormData({ ...formData, estado: e.target.value as any })}>
                <option value="PENDIENTE">Pendiente</option>
                <option value="ENVIADA">Enviada</option>
                <option value="RESPONDIDA">Respondida</option>
              </select>
            </div>
            <Input label="ID Cliente" type="number" value={formData.clienteId} onChange={(e) => setFormData({ ...formData, clienteId: Number(e.target.value) })} />
            <Input label="Fecha Envío" type="date" value={formData.fechaEnvio} onChange={(e) => setFormData({ ...formData, fechaEnvio: e.target.value })} />
            <Input label="Calificación General" type="number" min="1" max="5" value={formData.calificacionGeneral} onChange={(e) => setFormData({ ...formData, calificacionGeneral: Number(e.target.value) })} />
            <div className="flex items-center mt-6">
              <input type="checkbox" checked={formData.recomendaria} onChange={(e) => setFormData({ ...formData, recomendaria: e.target.checked })} className="mr-2" />
              <label className="text-sm font-medium text-gray-700">Recomendaría el servicio</label>
            </div>
          </div>
          <div className="mt-4">
            <label className="block text-sm font-medium text-gray-700 mb-1">Descripción</label>
            <textarea className="input" rows={2} value={formData.descripcion} onChange={(e) => setFormData({ ...formData, descripcion: e.target.value })} />
          </div>
          <div className="mt-4">
            <label className="block text-sm font-medium text-gray-700 mb-1">Comentarios</label>
            <textarea className="input" rows={2} value={formData.comentarios} onChange={(e) => setFormData({ ...formData, comentarios: e.target.value })} />
          </div>
          <div className="mt-4 grid grid-cols-5 gap-2">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">P1</label>
              <Input type="number" min="1" max="5" value={formData.pregunta1} onChange={(e) => setFormData({ ...formData, pregunta1: Number(e.target.value) })} />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">P2</label>
              <Input type="number" min="1" max="5" value={formData.pregunta2} onChange={(e) => setFormData({ ...formData, pregunta2: Number(e.target.value) })} />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">P3</label>
              <Input type="number" min="1" max="5" value={formData.pregunta3} onChange={(e) => setFormData({ ...formData, pregunta3: Number(e.target.value) })} />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">P4</label>
              <Input type="number" min="1" max="5" value={formData.pregunta4} onChange={(e) => setFormData({ ...formData, pregunta4: Number(e.target.value) })} />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">P5</label>
              <Input type="number" min="1" max="5" value={formData.pregunta5} onChange={(e) => setFormData({ ...formData, pregunta5: Number(e.target.value) })} />
            </div>
          </div>
          <div className="mt-6 flex justify-end space-x-3">
            <Button type="button" variant="secondary" onClick={() => { setIsModalOpen(false); setEditingEncuesta(null); }}>Cancelar</Button>
            <Button type="submit">Guardar</Button>
          </div>
        </form>
      </Modal>
    </div>
  );
};

export default EncuestasSatisfaccionPage;
