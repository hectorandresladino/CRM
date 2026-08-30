/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
import { useState, useEffect } from 'react';
import { Plus, Edit, Trash2, Search } from 'lucide-react';
import apiClient from '../services/api';
import { EmailMarketing } from '../types';
import Button from '../components/Button';
import Input from '../components/Input';
import Modal from '../components/Modal';

const EmailMarketingPage = () => {
  const [emails, setEmails] = useState<EmailMarketing[]>([]);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingEmail, setEditingEmail] = useState<EmailMarketing | null>(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [formData, setFormData] = useState<EmailMarketing>({
    asunto: '',
    contenido: '',
    estado: 'BORRADOR',
    tipo: 'NEWSLETTER',
    fechaEnvio: '',
    fechaProgramada: '',
    remitente: '',
    listaDestinatarios: '',
  });
  
  useEffect(() => {
    loadEmails();
  }, []);
  
  const loadEmails = async () => {
    try {
      const response = await apiClient.get('/api/email-marketing');
      setEmails(response.data);
    } catch (error) {
      console.error('Error loading emails:', error);
    }
  };
  
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      if (editingEmail?.id) {
        await apiClient.put(`/api/email-marketing/${editingEmail.id}`, formData);
      } else {
        await apiClient.post('/api/email-marketing', formData);
      }
      loadEmails();
      setIsModalOpen(false);
      setEditingEmail(null);
    } catch (error) {
      console.error('Error saving email:', error);
    }
  };
  
  const handleEdit = (email: EmailMarketing) => {
    setEditingEmail(email);
    setFormData(email);
    setIsModalOpen(true);
  };
  
  const handleDelete = async (id: number) => {
    if (window.confirm('Â¿EstÃ¡ seguro de eliminar este email?')) {
      try {
        await apiClient.delete(`/api/email-marketing/${id}`);
        loadEmails();
      } catch (error) {
        console.error('Error deleting email:', error);
      }
    }
  };
  
  const filteredEmails = emails.filter(
    (email) =>
      email.asunto.toLowerCase().includes(searchTerm.toLowerCase())
  );
  
  return (
    <div>
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-3xl font-bold text-gray-900">Email Marketing</h1>
        <Button onClick={() => setIsModalOpen(true)}>
          <Plus className="w-4 h-4 mr-2" />
          Nuevo Email
        </Button>
      </div>
      
      <div className="card mb-6">
        <div className="relative">
          <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-5 h-5" />
          <Input placeholder="Buscar emails..." value={searchTerm} onChange={(e) => setSearchTerm(e.target.value)} className="pl-10" />
        </div>
      </div>
      
      <div className="card">
        <table className="table">
          <thead>
            <tr>
              <th>Asunto</th>
              <th>Tipo</th>
              <th>Estado</th>
              <th>Fecha EnvÃ­o</th>
              <th>Enviados</th>
              <th>Apertura</th>
              <th>Acciones</th>
            </tr>
          </thead>
          <tbody>
            {filteredEmails.map((email) => (
              <tr key={email.id}>
                <td>{email.asunto}</td>
                <td>{email.tipo}</td>
                <td>
                  <span className={`px-2 py-1 rounded-full text-xs font-medium ${
                    email.estado === 'ENVIADO' ? 'bg-green-100 text-green-800' : 'bg-gray-100 text-gray-800'
                  }`}>
                    {email.estado}
                  </span>
                </td>
                <td>{email.fechaEnvio}</td>
                <td>{email.totalEnviados}</td>
                <td>{email.tasaApertura?.toFixed(1)}%</td>
                <td>
                  <div className="flex space-x-2">
                    <Button variant="secondary" onClick={() => handleEdit(email)}><Edit className="w-4 h-4" /></Button>
                    <Button variant="danger" onClick={() => handleDelete(email.id!)}><Trash2 className="w-4 h-4" /></Button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        {filteredEmails.length === 0 && <p className="text-center text-gray-500 py-8">No hay emails registrados</p>}
      </div>
      
      <Modal isOpen={isModalOpen} onClose={() => { setIsModalOpen(false); setEditingEmail(null); }} title={editingEmail?.id ? 'Editar Email' : 'Nuevo Email'}>
        <form onSubmit={handleSubmit}>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <Input label="Asunto" value={formData.asunto} onChange={(e) => setFormData({ ...formData, asunto: e.target.value })} required />
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Tipo</label>
              <select className="input" value={formData.tipo} onChange={(e) => setFormData({ ...formData, tipo: e.target.value as any })}>
                <option value="NEWSLETTER">Newsletter</option>
                <option value="PROMOCIONAL">Promocional</option>
                <option value="INFORMATIVO">Informativo</option>
              </select>
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Estado</label>
              <select className="input" value={formData.estado} onChange={(e) => setFormData({ ...formData, estado: e.target.value as any })}>
                <option value="BORRADOR">Borrador</option>
                <option value="PROGRAMADO">Programado</option>
                <option value="ENVIADO">Enviado</option>
              </select>
            </div>
            <Input label="Fecha Programada" type="datetime-local" value={formData.fechaProgramada} onChange={(e) => setFormData({ ...formData, fechaProgramada: e.target.value })} />
            <Input label="Remitente" value={formData.remitente} onChange={(e) => setFormData({ ...formData, remitente: e.target.value })} />
            <Input label="Lista Destinatarios" value={formData.listaDestinatarios} onChange={(e) => setFormData({ ...formData, listaDestinatarios: e.target.value })} />
          </div>
          <div className="mt-4">
            <label className="block text-sm font-medium text-gray-700 mb-1">Contenido</label>
            <textarea className="input" rows={5} value={formData.contenido} onChange={(e) => setFormData({ ...formData, contenido: e.target.value })} required />
          </div>
          <div className="mt-6 flex justify-end space-x-3">
            <Button type="button" variant="secondary" onClick={() => { setIsModalOpen(false); setEditingEmail(null); }}>Cancelar</Button>
            <Button type="submit">Guardar</Button>
          </div>
        </form>
      </Modal>
    </div>
  );
};

export default EmailMarketingPage;
