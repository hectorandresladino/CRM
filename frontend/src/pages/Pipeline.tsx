/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
import { useState, useEffect, useCallback } from 'react';
import { 
  GripVertical, Calendar, DollarSign, User, Phone, Mail, 
  Filter, LayoutGrid
} from 'lucide-react';
import apiClient from '../services/api';
import { Prospecto, Venta } from '../types';

const PROSPECTO_STAGES = [
  { key: 'NUEVO', label: 'Nuevo', color: 'bg-blue-500', bg: 'bg-blue-50', border: 'border-blue-200' },
  { key: 'CONTACTADO', label: 'Contactado', color: 'bg-yellow-500', bg: 'bg-yellow-50', border: 'border-yellow-200' },
  { key: 'CALIFICADO', label: 'Calificado', color: 'bg-cyan-500', bg: 'bg-cyan-50', border: 'border-cyan-200' },
  { key: 'PROPUESTA', label: 'Propuesta', color: 'bg-purple-500', bg: 'bg-purple-50', border: 'border-purple-200' },
  { key: 'NEGOCIACION', label: 'NegociaciÃ³n', color: 'bg-orange-500', bg: 'bg-orange-50', border: 'border-orange-200' },
  { key: 'CERRADO', label: 'Cerrado', color: 'bg-green-500', bg: 'bg-green-50', border: 'border-green-200' },
  { key: 'PERDIDO', label: 'Perdido', color: 'bg-red-500', bg: 'bg-red-50', border: 'border-red-200' },
];

const VENTA_STAGES = [
  { key: 'PENDIENTE', label: 'Pendiente', color: 'bg-blue-500', bg: 'bg-blue-50', border: 'border-blue-200' },
  { key: 'EN_PROCESO', label: 'En Proceso', color: 'bg-yellow-500', bg: 'bg-yellow-50', border: 'border-yellow-200' },
  { key: 'CERRADA', label: 'Cerrada', color: 'bg-green-500', bg: 'bg-green-50', border: 'border-green-200' },
  { key: 'CANCELADA', label: 'Cancelada', color: 'bg-red-500', bg: 'bg-red-50', border: 'border-red-200' },
];

interface PipelineCard {
  id: number;
  type: 'prospecto' | 'venta';
  title: string;
  subtitle: string;
  value?: number;
  vendedor?: string;
  email?: string;
  telefono?: string;
  estado: string;
  fechaCreacion?: string;
}

export default function Pipeline() {
  const [view, setView] = useState<'prospectos' | 'ventas'>('prospectos');
  const [prospectos, setProspectos] = useState<Prospecto[]>([]);
  const [ventas, setVentas] = useState<Venta[]>([]);
  const [loading, setLoading] = useState(true);
  const [draggedItem, setDraggedItem] = useState<PipelineCard | null>(null);
  const [dragOverStage, setDragOverStage] = useState<string | null>(null);
  const [filterVendedor, setFilterVendedor] = useState('');

  const stages = view === 'prospectos' ? PROSPECTO_STAGES : VENTA_STAGES;

  const loadData = useCallback(async () => {
    setLoading(true);
    try {
      if (view === 'prospectos') {
        const res = await apiClient.get('/api/prospectos');
        setProspectos(res.data);
      } else {
        const res = await apiClient.get('/api/ventas');
        setVentas(res.data);
      }
    } catch (error) {
      console.error('Error loading pipeline data:', error);
    } finally {
      setLoading(false);
    }
  }, [view]);

  useEffect(() => {
    loadData();
  }, [loadData]);

  const toCard = (p: Prospecto): PipelineCard => ({
    id: p.id!,
    type: 'prospecto',
    title: `${p.nombre} ${p.apellido}`,
    subtitle: p.empresa || p.cargo || '',
    email: p.email,
    telefono: p.celular || p.telefono,
    estado: p.estado,
    vendedor: '',
    fechaCreacion: p.fechaCreacion,
  });

  const toCardVenta = (v: Venta): PipelineCard => ({
    id: v.id!,
    type: 'venta',
    title: v.codigo,
    subtitle: v.descripcion,
    value: v.total,
    vendedor: v.vendedor,
    estado: v.estado,
    fechaCreacion: v.fechaCreacion,
  });

  const allCards: PipelineCard[] = view === 'prospectos' 
    ? prospectos.map(toCard) 
    : ventas.map(toCardVenta);

  const filteredCards = filterVendedor
    ? allCards.filter(c => c.vendedor?.toLowerCase().includes(filterVendedor.toLowerCase()))
    : allCards;

  const cardsByStage = (stageKey: string) => filteredCards.filter(c => c.estado === stageKey);

  const handleDragStart = (card: PipelineCard) => {
    setDraggedItem(card);
  };

  const handleDragOver = (e: React.DragEvent, stageKey: string) => {
    e.preventDefault();
    setDragOverStage(stageKey);
  };

  const handleDrop = async (stageKey: string) => {
    if (!draggedItem) return;
    setDragOverStage(null);

    try {
      if (draggedItem.type === 'prospecto') {
        await apiClient.patch(`/api/prospectos/${draggedItem.id}/estado?estado=${stageKey}`);
        setProspectos(prev => prev.map(p => p.id === draggedItem.id ? { ...p, estado: stageKey as any } : p));
      } else {
        const venta = ventas.find(v => v.id === draggedItem.id);
        if (venta) {
          await apiClient.put(`/api/ventas/${draggedItem.id}`, { ...venta, estado: stageKey as any });
          setVentas(prev => prev.map(v => v.id === draggedItem.id ? { ...v, estado: stageKey as any } : v));
        }
      }
    } catch (error) {
      console.error('Error updating stage:', error);
    } finally {
      setDraggedItem(null);
    }
  };

  const formatCurrency = (value?: number) => {
    if (value === undefined || value === null) return '';
    return new Intl.NumberFormat('es-CO', { style: 'currency', currency: 'USD', minimumFractionDigits: 0 }).format(value);
  };

  const formatDate = (date?: string) => {
    if (!date) return '';
    return new Date(date).toLocaleDateString('es-CO', { day: '2-digit', month: 'short' });
  };

  const totalValue = filteredCards.reduce((sum, c) => sum + (c.value || 0), 0);

  return (
    <div className="p-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between mb-6 gap-4">
        <div>
          <h1 className="text-2xl font-bold text-slate-900">Pipeline de Ventas</h1>
          <p className="text-sm text-slate-500 mt-1">
            {filteredCards.length} oportunidades Â· {formatCurrency(totalValue)} en pipeline
          </p>
        </div>
        <div className="flex items-center gap-3">
          {/* Toggle prospectos/ventas */}
          <div className="flex bg-slate-100 rounded-lg p-1">
            <button
              onClick={() => setView('prospectos')}
              className={`px-4 py-2 rounded-md text-sm font-medium transition-all ${view === 'prospectos' ? 'bg-white text-slate-900 shadow-sm' : 'text-slate-500'}`}
            >
              Prospectos
            </button>
            <button
              onClick={() => setView('ventas')}
              className={`px-4 py-2 rounded-md text-sm font-medium transition-all ${view === 'ventas' ? 'bg-white text-slate-900 shadow-sm' : 'text-slate-500'}`}
            >
              Ventas
            </button>
          </div>
          {/* Filter */}
          <div className="relative">
            <Filter className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-400" />
            <input
              type="text"
              placeholder="Filtrar vendedor..."
              value={filterVendedor}
              onChange={(e) => setFilterVendedor(e.target.value)}
              className="pl-10 pr-4 py-2 text-sm bg-white border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>
        </div>
      </div>

      {/* Kanban Board */}
      <div className="overflow-x-auto pb-4">
        <div className="flex gap-4 min-w-max">
          {stages.map((stage) => {
            const cards = cardsByStage(stage.key);
            const stageValue = cards.reduce((sum, c) => sum + (c.value || 0), 0);
            return (
              <div
                key={stage.key}
                className={`w-72 flex-shrink-0 rounded-xl border-2 transition-all ${dragOverStage === stage.key ? 'border-blue-400 bg-blue-50' : 'border-slate-200 bg-slate-50'}`}
                onDragOver={(e) => handleDragOver(e, stage.key)}
                onDragLeave={() => setDragOverStage(null)}
                onDrop={() => handleDrop(stage.key)}
              >
                {/* Column header */}
                <div className={`px-4 py-3 rounded-t-xl ${stage.bg} border-b ${stage.border}`}>
                  <div className="flex items-center justify-between">
                    <div className="flex items-center gap-2">
                      <span className={`w-2.5 h-2.5 rounded-full ${stage.color}`} />
                      <h3 className="font-semibold text-sm text-slate-700">{stage.label}</h3>
                    </div>
                    <span className="text-xs font-medium text-slate-500 bg-white px-2 py-0.5 rounded-full">
                      {cards.length}
                    </span>
                  </div>
                  {view === 'ventas' && stageValue > 0 && (
                    <p className="text-xs text-slate-500 mt-1">{formatCurrency(stageValue)}</p>
                  )}
                </div>

                {/* Cards */}
                <div className="p-3 space-y-3 min-h-[200px] max-h-[calc(100vh-300px)] overflow-y-auto">
                  {cards.map((card) => (
                    <div
                      key={`${card.type}-${card.id}`}
                      draggable
                      onDragStart={() => handleDragStart(card)}
                      className="bg-white rounded-lg border border-slate-200 p-3.5 cursor-grab active:cursor-grabbing hover:shadow-md transition-all group"
                    >
                      <div className="flex items-start justify-between mb-2">
                        <div className="min-w-0 flex-1">
                          <h4 className="font-semibold text-sm text-slate-900 truncate">{card.title}</h4>
                          {card.subtitle && <p className="text-xs text-slate-500 truncate mt-0.5">{card.subtitle}</p>}
                        </div>
                        <GripVertical className="w-4 h-4 text-slate-300 opacity-0 group-hover:opacity-100 transition-opacity flex-shrink-0" />
                      </div>

                      {card.value !== undefined && card.value > 0 && (
                        <div className="flex items-center gap-1.5 text-sm font-semibold text-slate-700 mb-2">
                          <DollarSign className="w-3.5 h-3.5 text-green-600" />
                          {formatCurrency(card.value)}
                        </div>
                      )}

                      <div className="space-y-1">
                        {card.vendedor && (
                          <div className="flex items-center gap-1.5 text-xs text-slate-500">
                            <User className="w-3 h-3" />
                            {card.vendedor}
                          </div>
                        )}
                        {card.email && (
                          <div className="flex items-center gap-1.5 text-xs text-slate-500">
                            <Mail className="w-3 h-3" />
                            <span className="truncate">{card.email}</span>
                          </div>
                        )}
                        {card.telefono && (
                          <div className="flex items-center gap-1.5 text-xs text-slate-500">
                            <Phone className="w-3 h-3" />
                            {card.telefono}
                          </div>
                        )}
                        {card.fechaCreacion && (
                          <div className="flex items-center gap-1.5 text-xs text-slate-400">
                            <Calendar className="w-3 h-3" />
                            {formatDate(card.fechaCreacion)}
                          </div>
                        )}
                      </div>
                    </div>
                  ))}

                  {cards.length === 0 && !loading && (
                    <div className="text-center py-8 text-slate-300">
                      <LayoutGrid className="w-8 h-8 mx-auto mb-2" />
                      <p className="text-xs">Arrastra tarjetas aquÃ­</p>
                    </div>
                  )}

                  {loading && (
                    <div className="space-y-2">
                      {[1, 2, 3].map(i => (
                        <div key={i} className="bg-white rounded-lg border border-slate-200 p-3.5 animate-pulse">
                          <div className="h-4 bg-slate-100 rounded w-3/4 mb-2" />
                          <div className="h-3 bg-slate-100 rounded w-1/2" />
                        </div>
                      ))}
                    </div>
                  )}
                </div>
              </div>
            );
          })}
        </div>
      </div>
    </div>
  );
}
