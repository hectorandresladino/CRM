import { useState, useEffect } from 'react';
import { 
  Zap, Plus, Edit, Trash2, Clock, 
  Mail, MessageCircle, Bell, ArrowRight, Webhook
} from 'lucide-react';
import apiClient from '../services/api';

interface Workflow {
  id?: number;
  name: string;
  description?: string;
  triggerType: string;
  triggerConfig?: string;
  actionType: string;
  actionConfig?: string;
  active: boolean;
  executionCount?: number;
  lastExecutedAt?: string;
}

const TRIGGER_LABELS: Record<string, string> = {
  NEW_PROSPECTO: 'Nuevo Prospecto',
  PROSPECTO_STAGE_CHANGE: 'Cambio de etapa (Prospecto)',
  NEW_VENTA: 'Nueva Venta',
  VENTA_STAGE_CHANGE: 'Cambio de estado (Venta)',
  NEW_CLIENTE: 'Nuevo Cliente',
  NEW_TICKET: 'Nuevo Ticket',
  TICKET_ESCALATION: 'Escalación de Ticket',
  SCHEDULED: 'Programado',
  WEBHOOK: 'Webhook',
};

const ACTION_LABELS: Record<string, string> = {
  SEND_EMAIL: 'Enviar Email',
  SEND_WHATSAPP: 'Enviar WhatsApp',
  CREATE_TASK: 'Crear Tarea',
  UPDATE_FIELD: 'Actualizar Campo',
  NOTIFY_USER: 'Notificar Usuario',
  CREATE_TICKET: 'Crear Ticket',
  MOVE_STAGE: 'Mover Etapa',
  WEBHOOK_CALL: 'Llamar Webhook',
};

const ACTION_ICONS: Record<string, React.ElementType> = {
  SEND_EMAIL: Mail,
  SEND_WHATSAPP: MessageCircle,
  NOTIFY_USER: Bell,
  CREATE_TASK: Plus,
  WEBHOOK_CALL: Webhook,
};

export default function WorkflowBuilder() {
  const [workflows, setWorkflows] = useState<Workflow[]>([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [editing, setEditing] = useState<Workflow | null>(null);
  const [formData, setFormData] = useState<Workflow>({
    name: '', description: '', triggerType: 'NEW_PROSPECTO', actionType: 'SEND_EMAIL', active: true,
  });

  const loadData = async () => {
    setLoading(true);
    try {
      const res = await apiClient.get('/api/workflows');
      setWorkflows(res.data);
    } catch (e) {
      console.error('Error loading workflows:', e);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { loadData(); }, []);

  const handleSubmit = async () => {
    try {
      if (editing?.id) {
        await apiClient.put(`/api/workflows/${editing.id}`, formData);
      } else {
        await apiClient.post('/api/workflows', formData);
      }
      setShowModal(false);
      setEditing(null);
      loadData();
      setFormData({ name: '', description: '', triggerType: 'NEW_PROSPECTO', actionType: 'SEND_EMAIL', active: true });
    } catch (e) {
      console.error('Error saving workflow:', e);
    }
  };

  const handleToggle = async (id: number) => {
    try {
      await apiClient.patch(`/api/workflows/${id}/toggle`);
      loadData();
    } catch (e) {
      console.error('Error toggling workflow:', e);
    }
  };

  const handleDelete = async (id: number) => {
    if (window.confirm('¿Eliminar esta automatización?')) {
      try {
        await apiClient.delete(`/api/workflows/${id}`);
        loadData();
      } catch (e) {
        console.error('Error deleting workflow:', e);
      }
    }
  };

  const handleEdit = (w: Workflow) => {
    setEditing(w);
    setFormData(w);
    setShowModal(true);
  };

  const activeCount = workflows.filter(w => w.active).length;

  return (
    <div className="p-6">
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-2xl font-bold text-slate-900 flex items-center gap-2">
            <Zap className="w-7 h-7 text-yellow-500" />
            Automatizaciones
          </h1>
          <p className="text-sm text-slate-500 mt-1">{activeCount} activas de {workflows.length} totales</p>
        </div>
        <button onClick={() => { setEditing(null); setFormData({ name: '', description: '', triggerType: 'NEW_PROSPECTO', actionType: 'SEND_EMAIL', active: true }); setShowModal(true); }} className="flex items-center gap-2 px-4 py-2 text-sm font-medium text-white bg-blue-600 rounded-lg hover:bg-blue-700">
          <Plus className="w-4 h-4" /> Nueva Automatización
        </button>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        {workflows.map((w) => {
          const ActionIcon = ACTION_ICONS[w.actionType] || Zap;
          return (
            <div key={w.id} className={`bg-white rounded-xl border p-5 transition-all ${w.active ? 'border-green-200 shadow-sm' : 'border-slate-200 opacity-60'}`}>
              <div className="flex items-start justify-between mb-3">
                <div className="flex items-center gap-2">
                  <div className={`p-2 rounded-lg ${w.active ? 'bg-green-50' : 'bg-slate-50'}`}>
                    <Zap className={`w-5 h-5 ${w.active ? 'text-green-600' : 'text-slate-400'}`} />
                  </div>
                  <div>
                    <h3 className="font-semibold text-sm text-slate-900">{w.name}</h3>
                    {w.description && <p className="text-xs text-slate-500 mt-0.5">{w.description}</p>}
                  </div>
                </div>
                <button onClick={() => handleToggle(w.id!)} className={`relative w-10 h-5 rounded-full transition-colors ${w.active ? 'bg-green-500' : 'bg-slate-300'}`}>
                  <span className={`absolute top-0.5 w-4 h-4 bg-white rounded-full transition-transform ${w.active ? 'translate-x-5' : 'translate-x-0.5'}`} />
                </button>
              </div>

              <div className="flex items-center gap-2 text-xs text-slate-600 mb-3">
                <span className="px-2 py-1 bg-blue-50 text-blue-700 rounded font-medium">{TRIGGER_LABELS[w.triggerType] || w.triggerType}</span>
                <ArrowRight className="w-3 h-3 text-slate-400" />
                <span className="px-2 py-1 bg-purple-50 text-purple-700 rounded font-medium flex items-center gap-1">
                  <ActionIcon className="w-3 h-3" /> {ACTION_LABELS[w.actionType] || w.actionType}
                </span>
              </div>

              <div className="flex items-center justify-between text-xs text-slate-400">
                <span className="flex items-center gap-1"><Clock className="w-3 h-3" /> {w.executionCount || 0} ejecuciones</span>
                <div className="flex gap-1">
                  <button onClick={() => handleEdit(w)} className="p-1.5 text-slate-400 hover:text-blue-600 hover:bg-blue-50 rounded"><Edit className="w-3.5 h-3.5" /></button>
                  <button onClick={() => handleDelete(w.id!)} className="p-1.5 text-slate-400 hover:text-red-600 hover:bg-red-50 rounded"><Trash2 className="w-3.5 h-3.5" /></button>
                </div>
              </div>
            </div>
          );
        })}
      </div>

      {workflows.length === 0 && !loading && (
        <div className="text-center py-16 text-slate-400">
          <Zap className="w-12 h-12 mx-auto mb-3" />
          <p className="text-lg font-medium">No hay automatizaciones</p>
          <p className="text-sm">Crea tu primera automatización para ahorrar tiempo</p>
        </div>
      )}
      {loading && <div className="text-center py-12 text-slate-400">Cargando...</div>}

      {showModal && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-xl shadow-xl max-w-lg w-full p-6">
            <h2 className="text-lg font-bold text-slate-900 mb-4">{editing?.id ? 'Editar Automatización' : 'Nueva Automatización'}</h2>
            <div className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-1">Nombre</label>
                <input type="text" className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm" value={formData.name} onChange={e => setFormData({ ...formData, name: e.target.value })} placeholder="Ej: Email de bienvenida a nuevos prospectos" />
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-1">Descripción</label>
                <input type="text" className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm" value={formData.description} onChange={e => setFormData({ ...formData, description: e.target.value })} />
              </div>
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">Disparador (Trigger)</label>
                  <select className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm" value={formData.triggerType} onChange={e => setFormData({ ...formData, triggerType: e.target.value })}>
                    {Object.entries(TRIGGER_LABELS).map(([k, v]) => <option key={k} value={k}>{v}</option>)}
                  </select>
                </div>
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">Acción</label>
                  <select className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm" value={formData.actionType} onChange={e => setFormData({ ...formData, actionType: e.target.value })}>
                    {Object.entries(ACTION_LABELS).map(([k, v]) => <option key={k} value={k}>{v}</option>)}
                  </select>
                </div>
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-1">Configuración del disparador (JSON)</label>
                <textarea className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm font-mono" rows={2} value={formData.triggerConfig || ''} onChange={e => setFormData({ ...formData, triggerConfig: e.target.value })} placeholder='{"stage":"CALIFICADO"}' />
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-1">Configuración de la acción (JSON)</label>
                <textarea className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm font-mono" rows={2} value={formData.actionConfig || ''} onChange={e => setFormData({ ...formData, actionConfig: e.target.value })} placeholder='{"template":"welcome","delay":"1h"}' />
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
