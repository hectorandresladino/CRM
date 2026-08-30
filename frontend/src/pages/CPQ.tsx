/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
import { useState, useEffect } from 'react';
import { 
  Package, Plus, Edit, Trash2, Search, Calculator,
  CheckCircle, AlertTriangle
} from 'lucide-react';
import apiClient from '../services/api';

interface CPQProduct {
  id?: number;
  sku: string;
  name: string;
  description?: string;
  basePrice: number;
  currency: string;
  minDiscountPct?: number;
  maxDiscountPct?: number;
  costPrice?: number;
  minMarginPct?: number;
  isActive: boolean;
  category?: string;
  unit?: string;
  stock?: number;
}

export default function CPQ() {
  const [products, setProducts] = useState<CPQProduct[]>([]);
  const [pendingApprovals, setPendingApprovals] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [editing, setEditing] = useState<CPQProduct | null>(null);
  const [search, setSearch] = useState('');
  const [formData, setFormData] = useState<CPQProduct>({
    sku: '', name: '', description: '', basePrice: 0, currency: 'USD',
    minDiscountPct: 0, maxDiscountPct: 10, costPrice: 0, minMarginPct: 20,
    isActive: true, category: '', unit: 'unit', stock: 0,
  });

  const loadData = async () => {
    setLoading(true);
    try {
      const [prodRes, apprRes] = await Promise.all([
        apiClient.get('/api/cpq/products'),
        apiClient.get('/api/cpq/pending-approvals'),
      ]);
      setProducts(prodRes.data);
      setPendingApprovals(apprRes.data);
    } catch (e) {
      console.error('Error loading CPQ data:', e);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { loadData(); }, []);

  const handleSubmit = async () => {
    try {
      if (editing?.id) {
        await apiClient.put(`/api/cpq/products/${editing.id}`, formData);
      } else {
        await apiClient.post('/api/cpq/products', formData);
      }
      setShowModal(false);
      setEditing(null);
      loadData();
      setFormData({ sku: '', name: '', description: '', basePrice: 0, currency: 'USD', minDiscountPct: 0, maxDiscountPct: 10, costPrice: 0, minMarginPct: 20, isActive: true, category: '', unit: 'unit', stock: 0 });
    } catch (e) {
      console.error('Error saving product:', e);
    }
  };

  const handleDelete = async (id: number) => {
    if (window.confirm('Â¿Eliminar este producto?')) {
      try {
        await apiClient.delete(`/api/cpq/products/${id}`);
        loadData();
      } catch (e) {
        console.error('Error deleting product:', e);
      }
    }
  };

  const handleApprove = async (id: number) => {
    try {
      await apiClient.patch(`/api/cpq/quote-items/${id}/approve`, 'Agent');
      loadData();
    } catch (e) {
      console.error('Error approving:', e);
    }
  };

  const filtered = products.filter(p =>
    p.name.toLowerCase().includes(search.toLowerCase()) ||
    p.sku.toLowerCase().includes(search.toLowerCase())
  );

  const fmt = (v: number) => new Intl.NumberFormat('es-CO', { style: 'currency', currency: 'USD' }).format(v || 0);

  return (
    <div className="p-6 space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-slate-900 flex items-center gap-2">
            <Calculator className="w-7 h-7 text-purple-600" />
            CPQ - Configure, Price, Quote
          </h1>
          <p className="text-sm text-slate-500 mt-1">CatÃ¡logo de productos con reglas de pricing, descuentos y aprobaciones</p>
        </div>
        <button onClick={() => { setEditing(null); setFormData({ sku: '', name: '', description: '', basePrice: 0, currency: 'USD', minDiscountPct: 0, maxDiscountPct: 10, costPrice: 0, minMarginPct: 20, isActive: true, category: '', unit: 'unit', stock: 0 }); setShowModal(true); }} className="flex items-center gap-2 px-4 py-2 text-sm font-medium text-white bg-blue-600 rounded-lg hover:bg-blue-700">
          <Plus className="w-4 h-4" /> Nuevo Producto
        </button>
      </div>

      {/* Pending approvals */}
      {pendingApprovals.length > 0 && (
        <div className="bg-orange-50 border border-orange-200 rounded-xl p-5">
          <h2 className="text-sm font-semibold text-orange-800 mb-3 flex items-center gap-2">
            <AlertTriangle className="w-4 h-4" /> Aprobaciones Pendientes ({pendingApprovals.length})
          </h2>
          <div className="space-y-2">
            {pendingApprovals.map((item) => (
              <div key={item.id} className="flex items-center justify-between bg-white rounded-lg p-3 border border-orange-100">
                <div>
                  <p className="text-sm font-medium text-slate-900">{item.productName} ({item.sku})</p>
                  <p className="text-xs text-slate-500">Cant: {item.quantity} Â· Precio: {fmt(item.unitPrice)} Â· Descuento: {item.discountPct}% Â· Total: {fmt(item.lineTotal)}</p>
                </div>
                <button onClick={() => handleApprove(item.id)} className="px-3 py-1.5 text-xs font-medium text-white bg-green-600 rounded-lg hover:bg-green-700 flex items-center gap-1">
                  <CheckCircle className="w-3 h-3" /> Aprobar
                </button>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Search */}
      <div className="relative max-w-md">
        <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-400" />
        <input type="text" placeholder="Buscar por nombre o SKU..." value={search} onChange={e => setSearch(e.target.value)} className="w-full pl-10 pr-4 py-2 text-sm bg-white border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500" />
      </div>

      {/* Products table */}
      <div className="bg-white rounded-xl border border-slate-200 overflow-hidden">
        <table className="w-full">
          <thead className="bg-slate-50 border-b border-slate-200">
            <tr>
              <th className="text-left px-4 py-3 text-xs font-semibold text-slate-600 uppercase tracking-wider">SKU</th>
              <th className="text-left px-4 py-3 text-xs font-semibold text-slate-600 uppercase tracking-wider">Nombre</th>
              <th className="text-left px-4 py-3 text-xs font-semibold text-slate-600 uppercase tracking-wider">Precio base</th>
              <th className="text-left px-4 py-3 text-xs font-semibold text-slate-600 uppercase tracking-wider">Costo</th>
              <th className="text-left px-4 py-3 text-xs font-semibold text-slate-600 uppercase tracking-wider">Margen min.</th>
              <th className="text-left px-4 py-3 text-xs font-semibold text-slate-600 uppercase tracking-wider">Desc. max.</th>
              <th className="text-left px-4 py-3 text-xs font-semibold text-slate-600 uppercase tracking-wider">Stock</th>
              <th className="text-left px-4 py-3 text-xs font-semibold text-slate-600 uppercase tracking-wider">Acciones</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-100">
            {filtered.map((p) => {
              const margin = p.costPrice && p.basePrice ? ((p.basePrice - p.costPrice) / p.basePrice * 100).toFixed(1) : 'â€”';
              return (
                <tr key={p.id} className="hover:bg-slate-50">
                  <td className="px-4 py-3 text-sm font-mono text-slate-700">{p.sku}</td>
                  <td className="px-4 py-3 text-sm font-medium text-slate-900">{p.name}</td>
                  <td className="px-4 py-3 text-sm text-slate-700">{fmt(p.basePrice)}</td>
                  <td className="px-4 py-3 text-sm text-slate-500">{p.costPrice ? fmt(p.costPrice) : 'â€”'}</td>
                  <td className="px-4 py-3 text-sm text-slate-500">{p.minMarginPct ? `${p.minMarginPct}%` : 'â€”'} <span className="text-xs text-slate-400">({margin}%)</span></td>
                  <td className="px-4 py-3 text-sm text-slate-500">{p.maxDiscountPct ? `${p.maxDiscountPct}%` : 'â€”'}</td>
                  <td className="px-4 py-3 text-sm">
                    <span className={`px-2 py-0.5 rounded-full text-xs font-medium ${(p.stock || 0) > 0 ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'}`}>{p.stock || 0}</span>
                  </td>
                  <td className="px-4 py-3">
                    <div className="flex gap-1">
                      <button onClick={() => { setEditing(p); setFormData(p); setShowModal(true); }} className="p-1.5 text-slate-400 hover:text-blue-600 hover:bg-blue-50 rounded"><Edit className="w-3.5 h-3.5" /></button>
                      <button onClick={() => handleDelete(p.id!)} className="p-1.5 text-slate-400 hover:text-red-600 hover:bg-red-50 rounded"><Trash2 className="w-3.5 h-3.5" /></button>
                    </div>
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
        {filtered.length === 0 && !loading && (
          <div className="text-center py-12 text-slate-400">
            <Package className="w-12 h-12 mx-auto mb-3" />
            <p>No hay productos configurados</p>
          </div>
        )}
        {loading && <div className="text-center py-12 text-slate-400">Cargando...</div>}
      </div>

      {showModal && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-xl shadow-xl max-w-lg w-full p-6 max-h-[90vh] overflow-y-auto">
            <h2 className="text-lg font-bold text-slate-900 mb-4">{editing?.id ? 'Editar Producto' : 'Nuevo Producto CPQ'}</h2>
            <div className="space-y-4">
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">SKU</label>
                  <input type="text" className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm" value={formData.sku} onChange={e => setFormData({ ...formData, sku: e.target.value })} />
                </div>
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">CategorÃ­a</label>
                  <input type="text" className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm" value={formData.category} onChange={e => setFormData({ ...formData, category: e.target.value })} />
                </div>
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-1">Nombre</label>
                <input type="text" className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm" value={formData.name} onChange={e => setFormData({ ...formData, name: e.target.value })} />
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-1">DescripciÃ³n</label>
                <textarea className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm" rows={2} value={formData.description} onChange={e => setFormData({ ...formData, description: e.target.value })} />
              </div>
              <div className="grid grid-cols-3 gap-4">
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">Precio base</label>
                  <input type="number" className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm" value={formData.basePrice} onChange={e => setFormData({ ...formData, basePrice: Number(e.target.value) })} />
                </div>
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">Costo</label>
                  <input type="number" className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm" value={formData.costPrice} onChange={e => setFormData({ ...formData, costPrice: Number(e.target.value) })} />
                </div>
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">Moneda</label>
                  <select className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm" value={formData.currency} onChange={e => setFormData({ ...formData, currency: e.target.value })}>
                    <option>USD</option><option>EUR</option><option>CAD</option><option>COP</option><option>MXN</option>
                  </select>
                </div>
              </div>
              <div className="grid grid-cols-3 gap-4">
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">Desc. min. (%)</label>
                  <input type="number" className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm" value={formData.minDiscountPct} onChange={e => setFormData({ ...formData, minDiscountPct: Number(e.target.value) })} />
                </div>
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">Desc. max. (%)</label>
                  <input type="number" className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm" value={formData.maxDiscountPct} onChange={e => setFormData({ ...formData, maxDiscountPct: Number(e.target.value) })} />
                </div>
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">Margen min. (%)</label>
                  <input type="number" className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm" value={formData.minMarginPct} onChange={e => setFormData({ ...formData, minMarginPct: Number(e.target.value) })} />
                </div>
              </div>
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">Unidad</label>
                  <input type="text" className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm" value={formData.unit} onChange={e => setFormData({ ...formData, unit: e.target.value })} />
                </div>
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">Stock</label>
                  <input type="number" className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm" value={formData.stock} onChange={e => setFormData({ ...formData, stock: Number(e.target.value) })} />
                </div>
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
