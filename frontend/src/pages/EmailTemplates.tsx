/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
import { useState, useEffect } from 'react';
import { 
  Mail, Plus, Edit, Trash2, Eye, Code
} from 'lucide-react';
import apiClient from '../services/api';

interface EmailTemplate {
  id?: number;
  name: string;
  subject: string;
  bodyHtml: string;
  bodyText?: string;
  category: string;
  isActive: boolean;
  usageCount?: number;
}

const CATEGORIES: Record<string, string> = {
  WELCOME: 'Bienvenida',
  FOLLOW_UP: 'Seguimiento',
  PROPOSAL: 'Propuesta',
  NEWSLETTER: 'Newsletter',
  PROMOTION: 'PromociÃ³n',
  SUPPORT: 'Soporte',
  ONBOARDING: 'Onboarding',
  CUSTOM: 'Personalizado',
};

export default function EmailTemplates() {
  const [templates, setTemplates] = useState<EmailTemplate[]>([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [editing, setEditing] = useState<EmailTemplate | null>(null);
  const [previewMode, setPreviewMode] = useState(false);
  const [formData, setFormData] = useState<EmailTemplate>({
    name: '', subject: '', bodyHtml: '', bodyText: '', category: 'WELCOME', isActive: true,
  });

  const loadData = async () => {
    setLoading(true);
    try {
      const res = await apiClient.get('/api/email-templates');
      setTemplates(res.data);
    } catch (e) {
      console.error('Error loading templates:', e);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { loadData(); }, []);

  const handleSubmit = async () => {
    try {
      if (editing?.id) {
        await apiClient.put(`/api/email-templates/${editing.id}`, formData);
      } else {
        await apiClient.post('/api/email-templates', formData);
      }
      setShowModal(false);
      setEditing(null);
      setPreviewMode(false);
      loadData();
      setFormData({ name: '', subject: '', bodyHtml: '', bodyText: '', category: 'WELCOME', isActive: true });
    } catch (e) {
      console.error('Error saving template:', e);
    }
  };

  const handleEdit = (t: EmailTemplate) => {
    setEditing(t);
    setFormData(t);
    setPreviewMode(false);
    setShowModal(true);
  };

  const handleDelete = async (id: number) => {
    if (window.confirm('Â¿Eliminar esta plantilla?')) {
      try {
        await apiClient.delete(`/api/email-templates/${id}`);
        loadData();
      } catch (e) {
        console.error('Error deleting template:', e);
      }
    }
  };

  return (
    <div className="p-6">
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-2xl font-bold text-slate-900 flex items-center gap-2">
            <Mail className="w-7 h-7 text-blue-600" />
            Plantillas de Email
          </h1>
          <p className="text-sm text-slate-500 mt-1">Plantillas reutilizables para campaÃ±as y automatizaciones</p>
        </div>
        <button onClick={() => { setEditing(null); setFormData({ name: '', subject: '', bodyHtml: '', bodyText: '', category: 'WELCOME', isActive: true }); setPreviewMode(false); setShowModal(true); }} className="flex items-center gap-2 px-4 py-2 text-sm font-medium text-white bg-blue-600 rounded-lg hover:bg-blue-700">
          <Plus className="w-4 h-4" /> Nueva Plantilla
        </button>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        {templates.map((t) => (
          <div key={t.id} className="bg-white rounded-xl border border-slate-200 p-5 hover:shadow-md transition-all">
            <div className="flex items-start justify-between mb-3">
              <div className="flex items-center gap-2">
                <div className="bg-blue-50 p-2 rounded-lg"><Mail className="w-4 h-4 text-blue-600" /></div>
                <div>
                  <h3 className="font-semibold text-sm text-slate-900">{t.name}</h3>
                  <span className="text-xs text-slate-500">{CATEGORIES[t.category] || t.category}</span>
                </div>
              </div>
              {t.isActive ? (
                <span className="px-2 py-0.5 text-xs font-medium bg-green-100 text-green-700 rounded-full">Activa</span>
              ) : (
                <span className="px-2 py-0.5 text-xs font-medium bg-slate-100 text-slate-500 rounded-full">Inactiva</span>
              )}
            </div>
            <p className="text-sm text-slate-600 font-medium mb-2 truncate">{t.subject}</p>
            <p className="text-xs text-slate-400 line-clamp-2 mb-3">{t.bodyHtml.replace(/<[^>]*>/g, '').substring(0, 100)}</p>
            <div className="flex items-center justify-between">
              <span className="text-xs text-slate-400">{t.usageCount || 0} usos</span>
              <div className="flex gap-1">
                <button onClick={() => handleEdit(t)} className="p-1.5 text-slate-400 hover:text-blue-600 hover:bg-blue-50 rounded"><Edit className="w-3.5 h-3.5" /></button>
                <button onClick={() => handleDelete(t.id!)} className="p-1.5 text-slate-400 hover:text-red-600 hover:bg-red-50 rounded"><Trash2 className="w-3.5 h-3.5" /></button>
              </div>
            </div>
          </div>
        ))}
      </div>

      {templates.length === 0 && !loading && (
        <div className="text-center py-16 text-slate-400">
          <Mail className="w-12 h-12 mx-auto mb-3" />
          <p className="text-lg font-medium">No hay plantillas</p>
          <p className="text-sm">Crea tu primera plantilla de email</p>
        </div>
      )}
      {loading && <div className="text-center py-12 text-slate-400">Cargando...</div>}

      {showModal && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-xl shadow-xl max-w-2xl w-full p-6 max-h-[90vh] overflow-y-auto">
            <div className="flex items-center justify-between mb-4">
              <h2 className="text-lg font-bold text-slate-900">{editing?.id ? 'Editar Plantilla' : 'Nueva Plantilla'}</h2>
              <div className="flex bg-slate-100 rounded-lg p-1">
                <button onClick={() => setPreviewMode(false)} className={`px-3 py-1 text-xs font-medium rounded ${!previewMode ? 'bg-white shadow-sm' : 'text-slate-500'}`}>
                  <Code className="w-3 h-3 inline mr-1" /> Editor
                </button>
                <button onClick={() => setPreviewMode(true)} className={`px-3 py-1 text-xs font-medium rounded ${previewMode ? 'bg-white shadow-sm' : 'text-slate-500'}`}>
                  <Eye className="w-3 h-3 inline mr-1" /> Preview
                </button>
              </div>
            </div>
            <div className="space-y-4">
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">Nombre</label>
                  <input type="text" className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm" value={formData.name} onChange={e => setFormData({ ...formData, name: e.target.value })} />
                </div>
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">CategorÃ­a</label>
                  <select className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm" value={formData.category} onChange={e => setFormData({ ...formData, category: e.target.value })}>
                    {Object.entries(CATEGORIES).map(([k, v]) => <option key={k} value={k}>{v}</option>)}
                  </select>
                </div>
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-1">Asunto</label>
                <input type="text" className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm" value={formData.subject} onChange={e => setFormData({ ...formData, subject: e.target.value })} />
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-1">Contenido (HTML)</label>
                {previewMode ? (
                  <div className="w-full min-h-[200px] border border-slate-200 rounded-lg p-4 bg-slate-50 overflow-auto" dangerouslySetInnerHTML={{ __html: formData.bodyHtml || '<p style="color:#999">Sin contenido</p>' }} />
                ) : (
                  <textarea className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm font-mono" rows={10} value={formData.bodyHtml} onChange={e => setFormData({ ...formData, bodyHtml: e.target.value })} placeholder="<h1>Hola {{nombre}}</h1><p>Bienvenido a...</p>" />
                )}
              </div>
              <div className="flex items-center gap-2">
                <input type="checkbox" id="isActive" checked={formData.isActive} onChange={e => setFormData({ ...formData, isActive: e.target.checked })} />
                <label htmlFor="isActive" className="text-sm text-slate-700">Plantilla activa</label>
              </div>
            </div>
            <div className="flex justify-end gap-3 mt-6">
              <button onClick={() => setShowModal(false)} className="px-4 py-2 text-sm font-medium text-slate-600 border border-slate-200 rounded-lg hover:bg-slate-50">Cancelar</button>
              <button onClick={handleSubmit} className="px-4 py-2 text-sm font-medium text-white bg-blue-600 rounded-lg hover:bg-blue-700">Guardar</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
