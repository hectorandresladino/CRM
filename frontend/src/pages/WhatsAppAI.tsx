/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
import { useState, useEffect, useCallback } from 'react';
import { 
  Bot, MessageCircle, Send, Settings, 
  User, CheckCircle, AlertCircle, Users,
  Power, Save
} from 'lucide-react';
import apiClient from '../services/api';

interface Conversation {
  id?: number;
  contactPhone: string;
  contactName?: string;
  direction: string;
  message: string;
  messageType?: string;
  aiResponse?: boolean;
  aiIntent?: string;
  aiHandled?: boolean;
  humanTakenOver?: boolean;
  assignedAgent?: string;
  status: string;
  sentAt?: string;
}

interface AIConfig {
  enabled: boolean;
  autoReply: boolean;
  businessName: string;
  welcomeMessage: string;
  fallbackMessage: string;
  hoursStart: string;
  hoursEnd: string;
  outOfHoursMessage: string;
  qualifyLeads: boolean;
  transcribeAudio: boolean;
  language: string;
  personality: string;
  systemPrompt: string;
}

interface Stats {
  total: number;
  aiHandled: number;
  humanHandled: number;
  waitingAgent: number;
  resolved: number;
  uniqueContacts: number;
}

const INTENT_LABELS: Record<string, string> = {
  GREETING: 'Saludo',
  PRICING_INQUIRY: 'Consulta de precios',
  INFO_REQUEST: 'Solicitud de info',
  DEMO_REQUEST: 'Solicitud de demo',
  PURCHASE_INTENT: 'IntenciÃ³n de compra',
  SUPPORT_REQUEST: 'Soporte',
  SATISFACTION: 'SatisfacciÃ³n',
  HUMAN_AGENT: 'Derivar a agente',
  OUT_OF_HOURS: 'Fuera de horario',
  GENERAL: 'General',
};

const INTENT_COLORS: Record<string, string> = {
  GREETING: 'bg-blue-100 text-blue-700',
  PRICING_INQUIRY: 'bg-green-100 text-green-700',
  INFO_REQUEST: 'bg-cyan-100 text-cyan-700',
  DEMO_REQUEST: 'bg-purple-100 text-purple-700',
  PURCHASE_INTENT: 'bg-green-600 text-white',
  SUPPORT_REQUEST: 'bg-orange-100 text-orange-700',
  HUMAN_AGENT: 'bg-red-100 text-red-700',
  OUT_OF_HOURS: 'bg-slate-100 text-slate-600',
  GENERAL: 'bg-slate-100 text-slate-600',
};

export default function WhatsAppAI() {
  const [tab, setTab] = useState<'conversations' | 'config' | 'stats'>('conversations');
  const [conversations, setConversations] = useState<Conversation[]>([]);
  const [config, setConfig] = useState<AIConfig | null>(null);
  const [stats, setStats] = useState<Stats | null>(null);
  const [loading, setLoading] = useState(true);
  const [selectedPhone, setSelectedPhone] = useState<string | null>(null);
  const [replyText, setReplyText] = useState('');
  const [saving, setSaving] = useState(false);

  const loadData = useCallback(async () => {
    setLoading(true);
    try {
      const [convRes, cfgRes, statsRes] = await Promise.all([
        apiClient.get('/api/whatsapp-ai/conversations'),
        apiClient.get('/api/whatsapp-ai/config'),
        apiClient.get('/api/whatsapp-ai/stats'),
      ]);
      setConversations(convRes.data);
      setConfig(cfgRes.data);
      setStats(statsRes.data);
    } catch (e) {
      console.error('Error loading WhatsApp AI data:', e);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { loadData(); }, [loadData]);

  const uniqueContacts = Array.from(new Set(conversations.map(c => c.contactPhone)));
  const conversationThread = selectedPhone ? conversations.filter(c => c.contactPhone === selectedPhone) : [];

  const handleSend = async () => {
    if (!replyText.trim() || !selectedPhone) return;
    try {
      await apiClient.post('/api/whatsapp-ai/send', { phone: selectedPhone, message: replyText, agent: 'Agent' });
      setReplyText('');
      loadData();
    } catch (e) {
      console.error('Error sending message:', e);
    }
  };

  const handleTakeOver = async (id: number) => {
    try {
      await apiClient.patch(`/api/whatsapp-ai/conversations/${id}/takeover`, { agent: 'Agent' });
      loadData();
    } catch (e) {
      console.error('Error taking over:', e);
    }
  };

  const handleResolve = async (id: number) => {
    try {
      await apiClient.patch(`/api/whatsapp-ai/conversations/${id}/resolve`);
      loadData();
    } catch (e) {
      console.error('Error resolving:', e);
    }
  };

  const handleSaveConfig = async () => {
    if (!config) return;
    setSaving(true);
    try {
      await apiClient.put('/api/whatsapp-ai/config', config);
      loadData();
    } catch (e) {
      console.error('Error saving config:', e);
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="p-6">
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-2xl font-bold text-slate-900 flex items-center gap-2">
            <Bot className="w-7 h-7 text-green-600" />
            IA WhatsApp Business
          </h1>
          <p className="text-sm text-slate-500 mt-1">Asistente IA 24/7 que cualifica leads y responde clientes automÃ¡ticamente</p>
        </div>
        <div className="flex bg-slate-100 rounded-lg p-1">
          {(['conversations', 'config', 'stats'] as const).map(t => (
            <button key={t} onClick={() => setTab(t)} className={`px-4 py-2 rounded-md text-sm font-medium transition-all ${tab === t ? 'bg-white text-slate-900 shadow-sm' : 'text-slate-500'}`}>
              {t === 'conversations' ? 'Conversaciones' : t === 'config' ? 'ConfiguraciÃ³n' : 'EstadÃ­sticas'}
            </button>
          ))}
        </div>
      </div>

      {/* Tab: Conversations */}
      {tab === 'conversations' && (
        <div className="flex gap-4 h-[calc(100vh-220px)]">
          {/* Contact list */}
          <div className="w-72 bg-white rounded-xl border border-slate-200 overflow-hidden flex-shrink-0">
            <div className="p-3 border-b border-slate-200">
              <h3 className="font-semibold text-sm text-slate-700">Contactos ({uniqueContacts.length})</h3>
            </div>
            <div className="overflow-y-auto" style={{ maxHeight: 'calc(100% - 50px)' }}>
              {uniqueContacts.map(phone => {
                const latest = conversations.find(c => c.contactPhone === phone);
                const waiting = conversations.some(c => c.contactPhone === phone && c.status === 'WAITING_AGENT');
                return (
                  <button key={phone} onClick={() => setSelectedPhone(phone)} className={`w-full text-left p-3 border-b border-slate-100 hover:bg-slate-50 transition-colors ${selectedPhone === phone ? 'bg-blue-50' : ''}`}>
                    <div className="flex items-center justify-between">
                      <div className="min-w-0">
                        <p className="text-sm font-medium text-slate-900 truncate">{latest?.contactName || phone}</p>
                        <p className="text-xs text-slate-500 truncate">{latest?.message.substring(0, 40)}</p>
                      </div>
                      {waiting && <span className="w-2 h-2 bg-red-500 rounded-full flex-shrink-0" />}
                    </div>
                  </button>
                );
              })}
              {uniqueContacts.length === 0 && !loading && (
                <div className="text-center py-8 text-slate-400 text-sm">No hay conversaciones</div>
              )}
            </div>
          </div>

          {/* Chat thread */}
          <div className="flex-1 bg-white rounded-xl border border-slate-200 flex flex-col">
            {selectedPhone ? (
              <>
                <div className="p-3 border-b border-slate-200 flex items-center justify-between">
                  <div className="flex items-center gap-2">
                    <div className="w-8 h-8 rounded-full bg-green-500 flex items-center justify-center text-white text-xs font-bold">
                      {(conversations.find(c => c.contactPhone === selectedPhone)?.contactName || selectedPhone).substring(0, 2).toUpperCase()}
                    </div>
                    <div>
                      <p className="text-sm font-semibold text-slate-900">{conversations.find(c => c.contactPhone === selectedPhone)?.contactName || selectedPhone}</p>
                      <p className="text-xs text-slate-500">{selectedPhone}</p>
                    </div>
                  </div>
                  {conversationThread.some(c => c.status === 'WAITING_AGENT') && (
                    <button onClick={() => { const w = conversationThread.find(c => c.status === 'WAITING_AGENT'); if (w?.id) handleTakeOver(w.id); }} className="px-3 py-1.5 text-xs font-medium text-white bg-blue-600 rounded-lg hover:bg-blue-700">
                      Tomar control
                    </button>
                  )}
                </div>
                <div className="flex-1 overflow-y-auto p-4 space-y-3 bg-slate-50">
                  {conversationThread.map((msg) => (
                    <div key={msg.id} className={`flex ${msg.direction === 'INBOUND' ? 'justify-start' : 'justify-end'}`}>
                      <div className={`max-w-[70%] rounded-lg px-3 py-2 ${msg.direction === 'INBOUND' ? 'bg-white border border-slate-200' : 'bg-green-500 text-white'}`}>
                        <p className="text-sm">{msg.message}</p>
                        <div className={`flex items-center gap-1 mt-1 text-xs ${msg.direction === 'INBOUND' ? 'text-slate-400' : 'text-green-100'}`}>
                          {msg.aiResponse && <Bot className="w-3 h-3" />}
                          {!msg.aiResponse && msg.direction === 'OUTBOUND' && <User className="w-3 h-3" />}
                          <span>{msg.sentAt ? new Date(msg.sentAt).toLocaleTimeString('es-CO', { hour: '2-digit', minute: '2-digit' }) : ''}</span>
                          {msg.aiIntent && <span className={`px-1.5 py-0.5 rounded text-[10px] font-medium ${INTENT_COLORS[msg.aiIntent] || 'bg-slate-100 text-slate-600'}`}>{INTENT_LABELS[msg.aiIntent] || msg.aiIntent}</span>}
                        </div>
                      </div>
                    </div>
                  ))}
                </div>
                <div className="p-3 border-t border-slate-200 flex gap-2">
                  <input type="text" value={replyText} onChange={e => setReplyText(e.target.value)} onKeyDown={e => e.key === 'Enter' && handleSend()} placeholder="Escribe un mensaje..." className="flex-1 px-3 py-2 border border-slate-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-green-500" />
                  <button onClick={handleSend} className="p-2 bg-green-500 text-white rounded-lg hover:bg-green-600">
                    <Send className="w-4 h-4" />
                  </button>
                  {conversationThread.length > 0 && conversationThread[0]?.id && (
                    <button onClick={() => handleResolve(conversationThread[0].id!)} className="px-3 py-2 text-xs font-medium text-slate-600 border border-slate-200 rounded-lg hover:bg-slate-50">
                      Resolver
                    </button>
                  )}
                </div>
              </>
            ) : (
              <div className="flex-1 flex items-center justify-center text-slate-400">
                <div className="text-center">
                  <MessageCircle className="w-12 h-12 mx-auto mb-3" />
                  <p>Selecciona un contacto para ver la conversaciÃ³n</p>
                </div>
              </div>
            )}
          </div>
        </div>
      )}

      {/* Tab: Config */}
      {tab === 'config' && config && (
        <div className="max-w-2xl space-y-6">
          {/* AI Status */}
          <div className="bg-white rounded-xl border border-slate-200 p-6">
            <h2 className="text-lg font-semibold text-slate-900 mb-4 flex items-center gap-2">
              <Power className="w-5 h-5 text-green-600" /> Estado del Asistente IA
            </h2>
            <div className="space-y-3">
              <label className="flex items-center gap-3">
                <input type="checkbox" checked={config.enabled} onChange={e => setConfig({ ...config, enabled: e.target.checked })} className="w-4 h-4 rounded" />
                <span className="text-sm text-slate-700">IA activada (responde automÃ¡ticamente)</span>
              </label>
              <label className="flex items-center gap-3">
                <input type="checkbox" checked={config.autoReply} onChange={e => setConfig({ ...config, autoReply: e.target.checked })} className="w-4 h-4 rounded" />
                <span className="text-sm text-slate-700">Auto-respuesta inmediata</span>
              </label>
              <label className="flex items-center gap-3">
                <input type="checkbox" checked={config.qualifyLeads} onChange={e => setConfig({ ...config, qualifyLeads: e.target.checked })} className="w-4 h-4 rounded" />
                <span className="text-sm text-slate-700">Cualificar leads automÃ¡ticamente</span>
              </label>
              <label className="flex items-center gap-3">
                <input type="checkbox" checked={config.transcribeAudio} onChange={e => setConfig({ ...config, transcribeAudio: e.target.checked })} className="w-4 h-4 rounded" />
                <span className="text-sm text-slate-700">Transcribir notas de voz a texto</span>
              </label>
            </div>
          </div>

          {/* Business Info */}
          <div className="bg-white rounded-xl border border-slate-200 p-6">
            <h2 className="text-lg font-semibold text-slate-900 mb-4 flex items-center gap-2">
              <Settings className="w-5 h-5 text-blue-600" /> InformaciÃ³n del Negocio
            </h2>
            <div className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-1">Nombre de la empresa</label>
                <input type="text" className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm" value={config.businessName} onChange={e => setConfig({ ...config, businessName: e.target.value })} />
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-1">Mensaje de bienvenida</label>
                <textarea className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm" rows={2} value={config.welcomeMessage} onChange={e => setConfig({ ...config, welcomeMessage: e.target.value })} />
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-1">Mensaje de respaldo (cuando no entiende)</label>
                <textarea className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm" rows={2} value={config.fallbackMessage} onChange={e => setConfig({ ...config, fallbackMessage: e.target.value })} />
              </div>
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">Hora inicio</label>
                  <input type="time" className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm" value={config.hoursStart} onChange={e => setConfig({ ...config, hoursStart: e.target.value })} />
                </div>
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">Hora fin</label>
                  <input type="time" className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm" value={config.hoursEnd} onChange={e => setConfig({ ...config, hoursEnd: e.target.value })} />
                </div>
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-1">Mensaje fuera de horario</label>
                <textarea className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm" rows={2} value={config.outOfHoursMessage} onChange={e => setConfig({ ...config, outOfHoursMessage: e.target.value })} />
              </div>
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">Idioma</label>
                  <select className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm" value={config.language} onChange={e => setConfig({ ...config, language: e.target.value })}>
                    <option value="es">EspaÃ±ol</option>
                    <option value="en">InglÃ©s</option>
                    <option value="pt">PortuguÃ©s</option>
                    <option value="fr">FrancÃ©s</option>
                    <option value="nl">NeerlandÃ©s</option>
                    <option value="de">AlemÃ¡n</option>
                  </select>
                </div>
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">Personalidad</label>
                  <select className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm" value={config.personality} onChange={e => setConfig({ ...config, personality: e.target.value })}>
                    <option value="professional">Profesional</option>
                    <option value="friendly">Amigable</option>
                    <option value="casual">Casual</option>
                    <option value="formal">Formal</option>
                  </select>
                </div>
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-1">Prompt del sistema (instrucciones para la IA)</label>
                <textarea className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm font-mono" rows={4} value={config.systemPrompt} onChange={e => setConfig({ ...config, systemPrompt: e.target.value })} />
              </div>
            </div>
            <button onClick={handleSaveConfig} disabled={saving} className="mt-4 flex items-center gap-2 px-4 py-2 text-sm font-medium text-white bg-blue-600 rounded-lg hover:bg-blue-700 disabled:bg-slate-400">
              <Save className="w-4 h-4" /> {saving ? 'Guardando...' : 'Guardar ConfiguraciÃ³n'}
            </button>
          </div>
        </div>
      )}

      {/* Tab: Stats */}
      {tab === 'stats' && stats && (
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div className="bg-white rounded-xl border border-slate-200 p-5">
            <div className="flex items-center gap-3">
              <div className="bg-blue-50 p-3 rounded-lg"><MessageCircle className="w-6 h-6 text-blue-600" /></div>
              <div><p className="text-2xl font-bold text-slate-900">{stats.total}</p><p className="text-xs text-slate-500">Total mensajes</p></div>
            </div>
          </div>
          <div className="bg-white rounded-xl border border-slate-200 p-5">
            <div className="flex items-center gap-3">
              <div className="bg-green-50 p-3 rounded-lg"><Bot className="w-6 h-6 text-green-600" /></div>
              <div><p className="text-2xl font-bold text-slate-900">{stats.aiHandled}</p><p className="text-xs text-slate-500">Manejados por IA</p></div>
            </div>
          </div>
          <div className="bg-white rounded-xl border border-slate-200 p-5">
            <div className="flex items-center gap-3">
              <div className="bg-purple-50 p-3 rounded-lg"><User className="w-6 h-6 text-purple-600" /></div>
              <div><p className="text-2xl font-bold text-slate-900">{stats.humanHandled}</p><p className="text-xs text-slate-500">Por agente humano</p></div>
            </div>
          </div>
          <div className="bg-white rounded-xl border border-slate-200 p-5">
            <div className="flex items-center gap-3">
              <div className="bg-red-50 p-3 rounded-lg"><AlertCircle className="w-6 h-6 text-red-600" /></div>
              <div><p className="text-2xl font-bold text-slate-900">{stats.waitingAgent}</p><p className="text-xs text-slate-500">Esperando agente</p></div>
            </div>
          </div>
          <div className="bg-white rounded-xl border border-slate-200 p-5">
            <div className="flex items-center gap-3">
              <div className="bg-green-50 p-3 rounded-lg"><CheckCircle className="w-6 h-6 text-green-600" /></div>
              <div><p className="text-2xl font-bold text-slate-900">{stats.resolved}</p><p className="text-xs text-slate-500">Resueltos</p></div>
            </div>
          </div>
          <div className="bg-white rounded-xl border border-slate-200 p-5">
            <div className="flex items-center gap-3">
              <div className="bg-cyan-50 p-3 rounded-lg"><Users className="w-6 h-6 text-cyan-600" /></div>
              <div><p className="text-2xl font-bold text-slate-900">{stats.uniqueContacts}</p><p className="text-xs text-slate-500">Contactos Ãºnicos</p></div>
            </div>
          </div>
        </div>
      )}

      {loading && <div className="text-center py-12 text-slate-400">Cargando...</div>}
    </div>
  );
}
