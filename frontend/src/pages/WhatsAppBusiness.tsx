/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
import { useState, useEffect } from 'react';
import { Plus, Edit, Trash2, Search } from 'lucide-react';
import apiClient from '../services/api';
import { WhatsAppBusiness } from '../types';
import Button from '../components/Button';
import Input from '../components/Input';
import Modal from '../components/Modal';

const WhatsAppBusinessPage = () => {
  const [messages, setMessages] = useState<WhatsAppBusiness[]>([]);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingMessage, setEditingMessage] = useState<WhatsAppBusiness | null>(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [formData, setFormData] = useState<WhatsAppBusiness>({
    telefono: '',
    mensaje: '',
    estado: 'PENDIENTE',
    tipo: 'TEXTO',
    fechaEnvio: '',
    fechaProgramada: '',
    plantilla: '',
    media: '',
  });
  
  useEffect(() => {
    loadMessages();
  }, []);
  
  const loadMessages = async () => {
    try {
      const response = await apiClient.get('/api/whatsapp-business');
      setMessages(response.data);
    } catch (error) {
      console.error('Error loading messages:', error);
    }
  };
  
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      if (editingMessage?.id) {
        await apiClient.put(`/api/whatsapp-business/${editingMessage.id}`, formData);
      } else {
        await apiClient.post('/api/whatsapp-business', formData);
      }
      loadMessages();
      setIsModalOpen(false);
      setEditingMessage(null);
    } catch (error) {
      console.error('Error saving message:', error);
    }
  };
  
  const handleEdit = (message: WhatsAppBusiness) => {
    setEditingMessage(message);
    setFormData(message);
    setIsModalOpen(true);
  };
  
  const handleDelete = async (id: number) => {
    if (window.confirm('Â¿EstÃ¡ seguro de eliminar este mensaje?')) {
      try {
        await apiClient.delete(`/api/whatsapp-business/${id}`);
        loadMessages();
      } catch (error) {
        console.error('Error deleting message:', error);
      }
    }
  };
  
  const filteredMessages = messages.filter(
    (msg) =>
      msg.telefono.includes(searchTerm) ||
      msg.mensaje.toLowerCase().includes(searchTerm.toLowerCase())
  );
  
  return (
    <div>
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-3xl font-bold text-gray-900">WhatsApp Business</h1>
        <Button onClick={() => setIsModalOpen(true)}>
          <Plus className="w-4 h-4 mr-2" />
          Nuevo Mensaje
        </Button>
      </div>
      
      <div className="card mb-6">
        <div className="relative">
          <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-5 h-5" />
          <Input placeholder="Buscar mensajes..." value={searchTerm} onChange={(e) => setSearchTerm(e.target.value)} className="pl-10" />
        </div>
      </div>
      
      <div className="card">
        <table className="table">
          <thead>
            <tr>
              <th>TelÃ©fono</th>
              <th>Mensaje</th>
              <th>Tipo</th>
              <th>Estado</th>
              <th>LeÃ­do</th>
              <th>Respondido</th>
              <th>Acciones</th>
            </tr>
          </thead>
          <tbody>
            {filteredMessages.map((msg) => (
              <tr key={msg.id}>
                <td>{msg.telefono}</td>
                <td>{msg.mensaje.substring(0, 50)}...</td>
                <td>{msg.tipo}</td>
                <td>
                  <span className={`px-2 py-1 rounded-full text-xs font-medium ${
                    msg.estado === 'ENVIADO' ? 'bg-green-100 text-green-800' : 'bg-gray-100 text-gray-800'
                  }`}>
                    {msg.estado}
                  </span>
                </td>
                <td>{msg.leido ? 'âœ“' : 'âœ—'}</td>
                <td>{msg.respondido ? 'âœ“' : 'âœ—'}</td>
                <td>
                  <div className="flex space-x-2">
                    <Button variant="secondary" onClick={() => handleEdit(msg)}><Edit className="w-4 h-4" /></Button>
                    <Button variant="danger" onClick={() => handleDelete(msg.id!)}><Trash2 className="w-4 h-4" /></Button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        {filteredMessages.length === 0 && <p className="text-center text-gray-500 py-8">No hay mensajes registrados</p>}
      </div>
      
      <Modal isOpen={isModalOpen} onClose={() => { setIsModalOpen(false); setEditingMessage(null); }} title={editingMessage?.id ? 'Editar Mensaje' : 'Nuevo Mensaje'}>
        <form onSubmit={handleSubmit}>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <Input label="TelÃ©fono" value={formData.telefono} onChange={(e) => setFormData({ ...formData, telefono: e.target.value })} required />
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Tipo</label>
              <select className="input" value={formData.tipo} onChange={(e) => setFormData({ ...formData, tipo: e.target.value as any })}>
                <option value="TEXTO">Texto</option>
                <option value="IMAGEN">Imagen</option>
                <option value="DOCUMENTO">Documento</option>
                <option value="PLANTILLA">Plantilla</option>
              </select>
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Estado</label>
              <select className="input" value={formData.estado} onChange={(e) => setFormData({ ...formData, estado: e.target.value as any })}>
                <option value="PENDIENTE">Pendiente</option>
                <option value="ENVIADO">Enviado</option>
                <option value="FALLIDO">Fallido</option>
              </select>
            </div>
            <Input label="Fecha Programada" type="datetime-local" value={formData.fechaProgramada} onChange={(e) => setFormData({ ...formData, fechaProgramada: e.target.value })} />
            <Input label="Plantilla" value={formData.plantilla} onChange={(e) => setFormData({ ...formData, plantilla: e.target.value })} />
            <Input label="Media (URL)" value={formData.media} onChange={(e) => setFormData({ ...formData, media: e.target.value })} />
          </div>
          <div className="mt-4">
            <label className="block text-sm font-medium text-gray-700 mb-1">Mensaje</label>
            <textarea className="input" rows={4} value={formData.mensaje} onChange={(e) => setFormData({ ...formData, mensaje: e.target.value })} required />
          </div>
          <div className="mt-6 flex justify-end space-x-3">
            <Button type="button" variant="secondary" onClick={() => { setIsModalOpen(false); setEditingMessage(null); }}>Cancelar</Button>
            <Button type="submit">Guardar</Button>
          </div>
        </form>
      </Modal>
    </div>
  );
};

export default WhatsAppBusinessPage;
