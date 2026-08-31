/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
import { useState, useEffect } from 'react';
import { Plus, Edit, Trash2, Search } from 'lucide-react';
import apiClient from '../services/api';
import { GestionDocumental } from '../types';
import Button from '../components/Button';
import Input from '../components/Input';
import Modal from '../components/Modal';

const GestionDocumentalPage = () => {
  const [documentos, setDocumentos] = useState<GestionDocumental[]>([]);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingDoc, setEditingDoc] = useState<GestionDocumental | null>(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [formData, setFormData] = useState<GestionDocumental>({
    nombre: '',
    descripcion: '',
    tipo: 'PDF',
    categoria: 'CONTRATO',
    estado: 'ACTIVO',
    urlArchivo: '',
    tamanoKb: 0,
    extension: '',
    clienteId: 0,
    etiquetas: '',
    fechaVencimiento: '',
  });
  
  useEffect(() => {
    loadDocumentos();
  }, []);
  
  const loadDocumentos = async () => {
    try {
      const response = await apiClient.get('/api/gestion-documental');
      setDocumentos(response.data);
    } catch (error) {
      console.error('Error loading documentos:', error);
    }
  };
  
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      if (editingDoc?.id) {
        await apiClient.put(`/api/gestion-documental/${editingDoc.id}`, formData);
      } else {
        await apiClient.post('/api/gestion-documental', formData);
      }
      loadDocumentos();
      setIsModalOpen(false);
      setEditingDoc(null);
    } catch (error) {
      console.error('Error saving documento:', error);
    }
  };
  
  const handleEdit = (doc: GestionDocumental) => {
    setEditingDoc(doc);
    setFormData(doc);
    setIsModalOpen(true);
  };
  
  const handleDelete = async (id: number) => {
    if (window.confirm('¿Está seguro de eliminar este documento?')) {
      try {
        await apiClient.delete(`/api/gestion-documental/${id}`);
        loadDocumentos();
      } catch (error) {
        console.error('Error deleting documento:', error);
      }
    }
  };
  
  const filteredDocumentos = documentos.filter(
    (doc) =>
      doc.nombre.toLowerCase().includes(searchTerm.toLowerCase()) ||
      doc.descripcion?.toLowerCase().includes(searchTerm.toLowerCase())
  );
  
  return (
    <div>
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-3xl font-bold text-gray-900">Gestión Documental</h1>
        <Button onClick={() => setIsModalOpen(true)}>
          <Plus className="w-4 h-4 mr-2" />
          Nuevo Documento
        </Button>
      </div>
      
      <div className="card mb-6">
        <div className="relative">
          <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-5 h-5" />
          <Input placeholder="Buscar documentos..." value={searchTerm} onChange={(e) => setSearchTerm(e.target.value)} className="pl-10" />
        </div>
      </div>
      
      <div className="card">
        <table className="table">
          <thead>
            <tr>
              <th>Nombre</th>
              <th>Tipo</th>
              <th>Categoría</th>
              <th>Estado</th>
              <th>Tamaño</th>
              <th>Vencimiento</th>
              <th>Acciones</th>
            </tr>
          </thead>
          <tbody>
            {filteredDocumentos.map((doc) => (
              <tr key={doc.id}>
                <td>{doc.nombre}</td>
                <td>{doc.tipo}</td>
                <td>{doc.categoria}</td>
                <td>
                  <span className={`px-2 py-1 rounded-full text-xs font-medium ${
                    doc.estado === 'ACTIVO' ? 'bg-green-100 text-green-800' : 'bg-gray-100 text-gray-800'
                  }`}>
                    {doc.estado}
                  </span>
                </td>
                <td>{doc.tamanoKb} KB</td>
                <td>{doc.fechaVencimiento}</td>
                <td>
                  <div className="flex space-x-2">
                    <Button variant="secondary" onClick={() => handleEdit(doc)}><Edit className="w-4 h-4" /></Button>
                    <Button variant="danger" onClick={() => handleDelete(doc.id!)}><Trash2 className="w-4 h-4" /></Button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        {filteredDocumentos.length === 0 && <p className="text-center text-gray-500 py-8">No hay documentos registrados</p>}
      </div>
      
      <Modal isOpen={isModalOpen} onClose={() => { setIsModalOpen(false); setEditingDoc(null); }} title={editingDoc?.id ? 'Editar Documento' : 'Nuevo Documento'}>
        <form onSubmit={handleSubmit}>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <Input label="Nombre" value={formData.nombre} onChange={(e) => setFormData({ ...formData, nombre: e.target.value })} required />
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Tipo</label>
              <select className="input" value={formData.tipo} onChange={(e) => setFormData({ ...formData, tipo: e.target.value as any })}>
                <option value="PDF">PDF</option>
                <option value="DOC">Word</option>
                <option value="XLS">Excel</option>
                <option value="IMG">Imagen</option>
              </select>
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Categoría</label>
              <select className="input" value={formData.categoria} onChange={(e) => setFormData({ ...formData, categoria: e.target.value as any })}>
                <option value="CONTRATO">Contrato</option>
                <option value="FACTURA">Factura</option>
                <option value="IDENTIDAD">Identidad</option>
                <option value="OTRO">Otro</option>
              </select>
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Estado</label>
              <select className="input" value={formData.estado} onChange={(e) => setFormData({ ...formData, estado: e.target.value as any })}>
                <option value="ACTIVO">Activo</option>
                <option value="VENCIDO">Vencido</option>
                <option value="ARCHIVADO">Archivado</option>
              </select>
            </div>
            <Input label="ID Cliente" type="number" value={formData.clienteId} onChange={(e) => setFormData({ ...formData, clienteId: Number(e.target.value) })} />
            <Input label="Tamaño (KB)" type="number" value={formData.tamanoKb} onChange={(e) => setFormData({ ...formData, tamanoKb: Number(e.target.value) })} />
            <Input label="Extensión" value={formData.extension} onChange={(e) => setFormData({ ...formData, extension: e.target.value })} />
            <Input label="Fecha Vencimiento" type="date" value={formData.fechaVencimiento} onChange={(e) => setFormData({ ...formData, fechaVencimiento: e.target.value })} />
          </div>
          <div className="mt-4">
            <label className="block text-sm font-medium text-gray-700 mb-1">Descripción</label>
            <textarea className="input" rows={2} value={formData.descripcion} onChange={(e) => setFormData({ ...formData, descripcion: e.target.value })} />
          </div>
          <div className="mt-4">
            <label className="block text-sm font-medium text-gray-700 mb-1">URL Archivo</label>
            <Input value={formData.urlArchivo} onChange={(e) => setFormData({ ...formData, urlArchivo: e.target.value })} />
          </div>
          <div className="mt-4">
            <label className="block text-sm font-medium text-gray-700 mb-1">Etiquetas</label>
            <Input value={formData.etiquetas} onChange={(e) => setFormData({ ...formData, etiquetas: e.target.value })} />
          </div>
          <div className="mt-6 flex justify-end space-x-3">
            <Button type="button" variant="secondary" onClick={() => { setIsModalOpen(false); setEditingDoc(null); }}>Cancelar</Button>
            <Button type="submit">Guardar</Button>
          </div>
        </form>
      </Modal>
    </div>
  );
};

export default GestionDocumentalPage;
