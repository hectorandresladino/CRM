/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
import { useState, useEffect } from 'react';
import { 
  Package, Plus, Edit, Trash2, Search, Tag
} from 'lucide-react';
import apiClient from '../services/api';

interface Producto {
  id?: number;
  codigo: string;
  nombre: string;
  descripcion?: string;
  familia: string;
  subFamilia?: string;
  tipo: string;
  precioBase: number;
  moneda: string;
  costo?: number;
  impuestoPct?: number;
  descuentoMaxPct?: number;
  unidadMedida?: string;
  stock?: number;
  stockMinimo?: number;
  esActivo: boolean;
  imagenUrl?: string;
}

export default function ProductosServicios() {
  const [productos, setProductos] = useState<Producto[]>([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [editing, setEditing] = useState<Producto | null>(null);
  const [search, setSearch] = useState('');
  const [formData, setFormData] = useState<Producto>({
    codigo: '', nombre: '', familia: '', tipo: 'PRODUCTO', precioBase: 0, moneda: 'USD', esActivo: true, unidadMedida: 'unidad',
  });

  const loadData = async () => {
    setLoading(true);
    try {
      const res = await apiClient.get('/api/productos');
      setProductos(res.data);
    } catch (e) { console.error(e); } finally { setLoading(false); }
  };

  useEffect(() => { loadData(); }, []);

  const handleSubmit = async () => {
    try {
      if (editing?.id) {
        await apiClient.put(`/api/productos/${editing.id}`, formData);
      } else {
        await apiClient.post('/api/productos', formData);
      }
      setShowModal(false);
      setEditing(null);
      loadData();
      setFormData({ codigo: '', nombre: '', familia: '', tipo: 'PRODUCTO', precioBase: 0, moneda: 'USD', esActivo: true, unidadMedida: 'unidad' });
    } catch (e) { console.error(e); }
  };

  const handleDelete = async (id: number) => {
    if (window.confirm('Â¿Eliminar este producto?')) {
      try { await apiClient.delete(`/api/productos/${id}`); loadData(); } catch (e) { console.error(e); }
    }
  };

  const filtered = productos.filter(p =>
    p.nombre.toLowerCase().includes(search.toLowerCase()) ||
    p.codigo.toLowerCase().includes(search.toLowerCase()) ||
    p.familia.toLowerCase().includes(search.toLowerCase())
  );

  const familias = Array.from(new Set(productos.map(p => p.familia)));
  const fmt = (v: number) => new Intl.NumberFormat('es-CO', { style: 'currency', currency: 'USD' }).format(v || 0);

  return (
    <div className="p-6 space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-slate-900 flex items-center gap-2">
            <Package className="w-7 h-7 text-indigo-600" />
            Productos y Servicios
          </h1>
          <p className="text-sm text-slate-500 mt-1">CatÃ¡logo con familias, listas de precios, impuestos y descuentos</p>
        </div>
        <button onClick={() => { setEditing(null); setFormData({ codigo: '', nombre: '', familia: '', tipo: 'PRODUCTO', precioBase: 0, moneda: 'USD', esActivo: true, unidadMedida: 'unidad' }); setShowModal(true); }} className="flex items-center gap-2 px-4 py-2 text-sm font-medium text-white bg-blue-600 rounded-lg hover:bg-blue-700">
          <Plus className="w-4 h-4" /> Nuevo Producto
        </button>
      </div>

      {familias.length > 0 && (
        <div className="flex flex-wrap gap-2">
          {familias.map(f => (
            <span key={f} className="px-3 py-1 text-xs font-medium bg-indigo-50 text-indigo-700 rounded-full flex items-center gap-1">
              <Tag className="w-3 h-3" /> {f}
            </span>
          ))}
        </div>
      )}

      <div className="relative max-w-md">
        <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-400" />
        <input type="text" placeholder="Buscar producto..." value={search} onChange={e => setSearch(e.target.value)} className="w-full pl-10 pr-4 py-2 text-sm bg-white border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500" />
      </div>

      <div className="bg-white rounded-xl border border-slate-200 overflow-hidden">
        <table className="w-full">
          <thead className="bg-slate-50 border-b border-slate-200">
            <tr>
              <th className="text-left px-4 py-3 text-xs font-semibold text-slate-600 uppercase tracking-wider">CÃ³digo</th>
              <th className="text-left px-4 py-3 text-xs font-semibold text-slate-600 uppercase tracking-wider">Nombre</th>
              <th className="text-left px-4 py-3 text-xs font-semibold text-slate-600 uppercase tracking-wider">Familia</th>
              <th className="text-left px-4 py-3 text-xs font-semibold text-slate-600 uppercase tracking-wider">Tipo</th>
              <th className="text-left px-4 py-3 text-xs font-semibold text-slate-600 uppercase tracking-wider">Precio</th>
              <th className="text-left px-4 py-3 text-xs font-semibold text-slate-600 uppercase tracking-wider">IVA</th>
              <th className="text-left px-4 py-3 text-xs font-semibold text-slate-600 uppercase tracking-wider">Stock</th>
              <th className="text-left px-4 py-3 text-xs font-semibold text-slate-600 uppercase tracking-wider">Acciones</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-100">
            {filtered.map((p) => (
              <tr key={p.id} className="hover:bg-slate-50">
                <td className="px-4 py-3 text-sm font-mono text-slate-700">{p.codigo}</td>
                <td className="px-4 py-3 text-sm font-medium text-slate-900">{p.nombre}</td>
                <td className="px-4 py-3 text-sm text-slate-600">{p.familia}</td>
                <td className="px-4 py-3"><span className="px-2 py-0.5 rounded-full text-xs font-medium bg-slate-100 text-slate-600">{p.tipo}</span></td>
                <td className="px-4 py-3 text-sm font-medium text-slate-900">{fmt(p.precioBase)}</td>
                <td className="px-4 py-3 text-sm text-slate-500">{p.impuestoPct ? `${p.impuestoPct}%` : 'â€”'}</td>
                <td className="px-4 py-3 text-sm">
                  <span className={`px-2 py-0.5 rounded-full text-xs font-medium ${(p.stock || 0) > (p.stockMinimo || 0) ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'}`}>{p.stock || 0}</span>
                </td>
                <td className="px-4 py-3">
                  <div className="flex gap-1">
                    <button onClick={() => { setEditing(p); setFormData(p); setShowModal(true); }} className="p-1.5 text-slate-400 hover:text-blue-600 hover:bg-blue-50 rounded"><Edit className="w-3.5 h-3.5" /></button>
                    <button onClick={() => handleDelete(p.id!)} className="p-1.5 text-slate-400 hover:text-red-600 hover:bg-red-50 rounded"><Trash2 className="w-3.5 h-3.5" /></button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        {filtered.length === 0 && !loading && (
          <div className="text-center py-12 text-slate-400">
            <Package className="w-12 h-12 mx-auto mb-3" />
            <p>No hay productos registrados</p>
          </div>
        )}
        {loading && <div className="text-center py-12 text-slate-400">Cargando...</div>}
      </div>

      {showModal && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-xl shadow-xl max-w-lg w-full p-6 max-h-[90vh] overflow-y-auto">
            <h2 className="text-lg font-bold text-slate-900 mb-4">{editing?.id ? 'Editar Producto' : 'Nuevo Producto'}</h2>
            <div className="space-y-4">
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">CÃ³digo</label>
                  <input type="text" className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm" value={formData.codigo} onChange={e => setFormData({ ...formData, codigo: e.target.value })} />
                </div>
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">Tipo</label>
                  <select className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm" value={formData.tipo} onChange={e => setFormData({ ...formData, tipo: e.target.value })}>
                    <option value="PRODUCTO">Producto</option>
                    <option value="SERVICIO">Servicio</option>
                    <option value="SUSCRIPCION">SuscripciÃ³n</option>
                    <option value="BUNDLE">Bundle</option>
                  </select>
                </div>
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-1">Nombre</label>
                <input type="text" className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm" value={formData.nombre} onChange={e => setFormData({ ...formData, nombre: e.target.value })} />
              </div>
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">Familia</label>
                  <input type="text" className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm" value={formData.familia} onChange={e => setFormData({ ...formData, familia: e.target.value })} />
                </div>
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">Sub-familia</label>
                  <input type="text" className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm" value={formData.subFamilia || ''} onChange={e => setFormData({ ...formData, subFamilia: e.target.value })} />
                </div>
              </div>
              <div className="grid grid-cols-3 gap-4">
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">Precio base</label>
                  <input type="number" className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm" value={formData.precioBase} onChange={e => setFormData({ ...formData, precioBase: Number(e.target.value) })} />
                </div>
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">Costo</label>
                  <input type="number" className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm" value={formData.costo || ''} onChange={e => setFormData({ ...formData, costo: Number(e.target.value) })} />
                </div>
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">Moneda</label>
                  <select className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm" value={formData.moneda} onChange={e => setFormData({ ...formData, moneda: e.target.value })}>
                    <option>USD</option><option>EUR</option><option>CAD</option><option>COP</option><option>MXN</option>
                  </select>
                </div>
              </div>
              <div className="grid grid-cols-3 gap-4">
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">IVA (%)</label>
                  <input type="number" className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm" value={formData.impuestoPct || ''} onChange={e => setFormData({ ...formData, impuestoPct: Number(e.target.value) })} />
                </div>
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">Desc. max (%)</label>
                  <input type="number" className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm" value={formData.descuentoMaxPct || ''} onChange={e => setFormData({ ...formData, descuentoMaxPct: Number(e.target.value) })} />
                </div>
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">Unidad</label>
                  <input type="text" className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm" value={formData.unidadMedida || ''} onChange={e => setFormData({ ...formData, unidadMedida: e.target.value })} />
                </div>
              </div>
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">Stock</label>
                  <input type="number" className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm" value={formData.stock || ''} onChange={e => setFormData({ ...formData, stock: Number(e.target.value) })} />
                </div>
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">Stock mÃ­nimo</label>
                  <input type="number" className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm" value={formData.stockMinimo || ''} onChange={e => setFormData({ ...formData, stockMinimo: Number(e.target.value) })} />
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
