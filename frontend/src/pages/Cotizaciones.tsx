/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
import { useState, useEffect } from 'react';
import { Plus, Edit, Trash2, Search } from 'lucide-react';
import apiClient from '../services/api';
import { Cotizacion } from '../types';
import Button from '../components/Button';
import Input from '../components/Input';
import Modal from '../components/Modal';

const Cotizaciones = () => {
  const [cotizaciones, setCotizaciones] = useState<Cotizacion[]>([]);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingCotizacion, setEditingCotizacion] = useState<Cotizacion | null>(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [formData, setFormData] = useState<Cotizacion>({
    clienteId: 0,
    codigo: '',
    descripcion: '',
    subtotal: 0,
    descuento: 0,
    impuesto: 0,
    total: 0,
    margen: 0,
    vendedor: '',
    terminos: '',
    notas: '',
    validez: new Date().toISOString().split('T')[0],
    estado: 'BORRADOR',
  });
  
  useEffect(() => {
    loadCotizaciones();
  }, []);
  
  const loadCotizaciones = async () => {
    try {
      const response = await apiClient.get('/api/cotizaciones');
      setCotizaciones(response.data);
    } catch (error) {
      console.error('Error loading cotizaciones:', error);
    }
  };
  
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      if (editingCotizacion?.id) {
        await apiClient.put(`/api/cotizaciones/${editingCotizacion.id}`, formData);
      } else {
        await apiClient.post('/api/cotizaciones', formData);
      }
      loadCotizaciones();
      setIsModalOpen(false);
      setEditingCotizacion(null);
    } catch (error) {
      console.error('Error saving cotizacion:', error);
    }
  };
  
  const handleEdit = (cotizacion: Cotizacion) => {
    setEditingCotizacion(cotizacion);
    setFormData(cotizacion);
    setIsModalOpen(true);
  };
  
  const handleDelete = async (id: number) => {
    if (window.confirm('Â¿EstÃ¡ seguro de eliminar esta cotizaciÃ³n?')) {
      try {
        await apiClient.delete(`/api/cotizaciones/${id}`);
        loadCotizaciones();
      } catch (error) {
        console.error('Error deleting cotizacion:', error);
      }
    }
  };
  
  const filteredCotizaciones = cotizaciones.filter(
    (cot) =>
      cot.codigo.toLowerCase().includes(searchTerm.toLowerCase()) ||
      cot.descripcion.toLowerCase().includes(searchTerm.toLowerCase())
  );
  
  return (
    <div>
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-3xl font-bold text-gray-900">Cotizaciones</h1>
        <Button onClick={() => setIsModalOpen(true)}>
          <Plus className="w-4 h-4 mr-2" />
          Nueva CotizaciÃ³n
        </Button>
      </div>
      
      <div className="card mb-6">
        <div className="relative">
          <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-5 h-5" />
          <Input placeholder="Buscar cotizaciones..." value={searchTerm} onChange={(e) => setSearchTerm(e.target.value)} className="pl-10" />
        </div>
      </div>
      
      <div className="card">
        <table className="table">
          <thead>
            <tr>
              <th>CÃ³digo</th>
              <th>DescripciÃ³n</th>
              <th>Subtotal</th>
              <th>Total</th>
              <th>Validez</th>
              <th>Estado</th>
              <th>Acciones</th>
            </tr>
          </thead>
          <tbody>
            {filteredCotizaciones.map((cot) => (
              <tr key={cot.id}>
                <td>{cot.codigo}</td>
                <td>{cot.descripcion}</td>
                <td>${cot.subtotal}</td>
                <td>${cot.total}</td>
                <td>{cot.validez}</td>
                <td>
                  <span className={`px-2 py-1 rounded-full text-xs font-medium ${
                    cot.estado === 'APROBADA' ? 'bg-green-100 text-green-800' : 
                    cot.estado === 'ENVIADA' ? 'bg-blue-100 text-blue-800' : 'bg-gray-100 text-gray-800'
                  }`}>
                    {cot.estado}
                  </span>
                </td>
                <td>
                  <div className="flex space-x-2">
                    <Button variant="secondary" size="sm" onClick={() => handleEdit(cot)}><Edit className="w-4 h-4" /></Button>
                    <Button variant="danger" size="sm" onClick={() => handleDelete(cot.id!)}><Trash2 className="w-4 h-4" /></Button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        {filteredCotizaciones.length === 0 && <p className="text-center text-gray-500 py-8">No hay cotizaciones registradas</p>}
      </div>
      
      <Modal isOpen={isModalOpen} onClose={() => { setIsModalOpen(false); setEditingCotizacion(null); }} title={editingCotizacion?.id ? 'Editar CotizaciÃ³n' : 'Nueva CotizaciÃ³n'}>
        <form onSubmit={handleSubmit}>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <Input label="CÃ³digo" value={formData.codigo} onChange={(e) => setFormData({ ...formData, codigo: e.target.value })} required />
            <Input label="ID Cliente" type="number" value={formData.clienteId} onChange={(e) => setFormData({ ...formData, clienteId: Number(e.target.value) })} required />
            <Input label="DescripciÃ³n" value={formData.descripcion} onChange={(e) => setFormData({ ...formData, descripcion: e.target.value })} required />
            <Input label="Subtotal" type="number" value={formData.subtotal} onChange={(e) => setFormData({ ...formData, subtotal: Number(e.target.value) })} required />
            <Input label="Descuento" type="number" value={formData.descuento} onChange={(e) => setFormData({ ...formData, descuento: Number(e.target.value) })} />
            <Input label="Impuesto" type="number" value={formData.impuesto} onChange={(e) => setFormData({ ...formData, impuesto: Number(e.target.value) })} />
            <Input label="Validez" type="date" value={formData.validez} onChange={(e) => setFormData({ ...formData, validez: e.target.value })} required />
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Estado</label>
              <select className="input" value={formData.estado} onChange={(e) => setFormData({ ...formData, estado: e.target.value as any })}>
                <option value="BORRADOR">Borrador</option>
                <option value="ENVIADA">Enviada</option>
                <option value="APROBADA">Aprobada</option>
                <option value="RECHAZADA">Rechazada</option>
                <option value="EXPIRADA">Expirada</option>
              </select>
            </div>
          </div>
          <div className="mt-4">
            <label className="block text-sm font-medium text-gray-700 mb-1">TÃ©rminos</label>
            <textarea className="input" rows={2} value={formData.terminos} onChange={(e) => setFormData({ ...formData, terminos: e.target.value })} />
          </div>
          <div className="mt-4">
            <label className="block text-sm font-medium text-gray-700 mb-1">Notas</label>
            <textarea className="input" rows={3} value={formData.notas} onChange={(e) => setFormData({ ...formData, notas: e.target.value })} />
          </div>
          <div className="mt-6 flex justify-end space-x-3">
            <Button type="button" variant="secondary" onClick={() => { setIsModalOpen(false); setEditingCotizacion(null); }}>Cancelar</Button>
            <Button type="submit">Guardar</Button>
          </div>
        </form>
      </Modal>
    </div>
  );
};

export default Cotizaciones;
