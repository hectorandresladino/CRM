import { useState, useEffect } from 'react';
import { 
  DollarSign, RefreshCw, TrendingUp, ArrowRight, Plus
} from 'lucide-react';
import apiClient from '../services/api';

interface CurrencyRate {
  id?: number;
  base: string;
  target: string;
  rate: number;
  fetchedAt?: string;
  source?: string;
}

export default function MultiCurrency() {
  const [rates, setRates] = useState<CurrencyRate[]>([]);
  const [loading, setLoading] = useState(true);
  const [fetching, setFetching] = useState(false);
  const [convertFrom, setConvertFrom] = useState('USD');
  const [convertTo, setConvertTo] = useState('EUR');
  const [convertAmount, setConvertAmount] = useState(100);
  const [convertResult, setConvertResult] = useState<number | null>(null);
  const [showModal, setShowModal] = useState(false);
  const [newRate, setNewRate] = useState<CurrencyRate>({ base: 'USD', target: 'COP', rate: 4000 });

  const loadData = async () => {
    setLoading(true);
    try {
      const res = await apiClient.get('/api/currency');
      setRates(res.data);
    } catch (e) {
      console.error('Error loading rates:', e);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { loadData(); }, []);

  const handleFetchRates = async () => {
    setFetching(true);
    try {
      await apiClient.post('/api/currency/fetch-rates');
      await loadData();
    } catch (e) {
      console.error('Error fetching rates:', e);
    } finally {
      setFetching(false);
    }
  };

  const handleConvert = async () => {
    try {
      const res = await apiClient.get(`/api/currency/convert?from=${convertFrom}&to=${convertTo}&amount=${convertAmount}`);
      setConvertResult(res.data.result);
    } catch (e) {
      console.error('Error converting:', e);
      setConvertResult(null);
    }
  };

  const handleAddRate = async () => {
    try {
      await apiClient.post('/api/currency', newRate);
      setShowModal(false);
      loadData();
    } catch (e) {
      console.error('Error adding rate:', e);
    }
  };

  const currencies = ['USD', 'EUR', 'CAD', 'COP', 'MXN', 'GBP', 'NLG', 'CHF', 'BRL', 'ARS', 'PEN', 'CLP'];

  return (
    <div className="p-6">
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-2xl font-bold text-slate-900 flex items-center gap-2">
            <DollarSign className="w-7 h-7 text-green-600" />
            Multi-Moneda
          </h1>
          <p className="text-sm text-slate-500 mt-1">Conversión automática de divisas para operaciones internacionales</p>
        </div>
        <div className="flex gap-3">
          <button onClick={handleFetchRates} disabled={fetching} className="flex items-center gap-2 px-4 py-2 text-sm font-medium text-white bg-blue-600 rounded-lg hover:bg-blue-700 disabled:bg-slate-400">
            <RefreshCw className={`w-4 h-4 ${fetching ? 'animate-spin' : ''}`} /> Actualizar Tasas
          </button>
          <button onClick={() => setShowModal(true)} className="flex items-center gap-2 px-4 py-2 text-sm font-medium text-slate-600 bg-white border border-slate-200 rounded-lg hover:bg-slate-50">
            <Plus className="w-4 h-4" /> Agregar Tasa
          </button>
        </div>
      </div>

      {/* Converter */}
      <div className="bg-white rounded-xl border border-slate-200 p-6 mb-6">
        <h2 className="text-lg font-semibold text-slate-900 mb-4 flex items-center gap-2">
          <TrendingUp className="w-5 h-5 text-blue-600" /> Conversor de Moneda
        </h2>
        <div className="flex flex-col md:flex-row items-end gap-4">
          <div className="flex-1 w-full">
            <label className="block text-sm font-medium text-slate-700 mb-1">De</label>
            <div className="flex gap-2">
              <select className="px-3 py-2 border border-slate-200 rounded-lg text-sm" value={convertFrom} onChange={e => setConvertFrom(e.target.value)}>
                {currencies.map(c => <option key={c} value={c}>{c}</option>)}
              </select>
              <input type="number" className="flex-1 px-3 py-2 border border-slate-200 rounded-lg text-sm" value={convertAmount} onChange={e => setConvertAmount(Number(e.target.value))} />
            </div>
          </div>
          <button onClick={handleConvert} className="p-2 text-blue-600 hover:bg-blue-50 rounded-lg">
            <ArrowRight className="w-5 h-5" />
          </button>
          <div className="flex-1 w-full">
            <label className="block text-sm font-medium text-slate-700 mb-1">A</label>
            <div className="flex gap-2">
              <select className="px-3 py-2 border border-slate-200 rounded-lg text-sm" value={convertTo} onChange={e => setConvertTo(e.target.value)}>
                {currencies.map(c => <option key={c} value={c}>{c}</option>)}
              </select>
              <input type="text" readOnly className="flex-1 px-3 py-2 border border-slate-200 rounded-lg text-sm bg-slate-50 font-semibold" value={convertResult !== null ? convertResult.toFixed(2) : ''} placeholder="Resultado" />
            </div>
          </div>
        </div>
      </div>

      {/* Rates Table */}
      <div className="bg-white rounded-xl border border-slate-200 overflow-hidden">
        <table className="w-full">
          <thead className="bg-slate-50 border-b border-slate-200">
            <tr>
              <th className="text-left px-4 py-3 text-xs font-semibold text-slate-600 uppercase tracking-wider">Base</th>
              <th className="text-left px-4 py-3 text-xs font-semibold text-slate-600 uppercase tracking-wider">Destino</th>
              <th className="text-left px-4 py-3 text-xs font-semibold text-slate-600 uppercase tracking-wider">Tasa</th>
              <th className="text-left px-4 py-3 text-xs font-semibold text-slate-600 uppercase tracking-wider">Fuente</th>
              <th className="text-left px-4 py-3 text-xs font-semibold text-slate-600 uppercase tracking-wider">Actualizado</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-100">
            {rates.map((r) => (
              <tr key={r.id} className="hover:bg-slate-50">
                <td className="px-4 py-3 text-sm font-medium text-slate-900">{r.base}</td>
                <td className="px-4 py-3 text-sm font-medium text-slate-900">{r.target}</td>
                <td className="px-4 py-3 text-sm text-slate-700 font-mono">{r.rate.toFixed(4)}</td>
                <td className="px-4 py-3 text-sm text-slate-500">{r.source || 'Manual'}</td>
                <td className="px-4 py-3 text-sm text-slate-500">{r.fetchedAt ? new Date(r.fetchedAt).toLocaleString('es-CO') : '—'}</td>
              </tr>
            ))}
          </tbody>
        </table>
        {rates.length === 0 && !loading && (
          <div className="text-center py-12 text-slate-400">
            <DollarSign className="w-12 h-12 mx-auto mb-3" />
            <p>No hay tasas configuradas. Haz clic en "Actualizar Tasas" para obtener tasas en vivo.</p>
          </div>
        )}
        {loading && <div className="text-center py-12 text-slate-400">Cargando...</div>}
      </div>

      {showModal && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-xl shadow-xl max-w-md w-full p-6">
            <h2 className="text-lg font-bold text-slate-900 mb-4">Agregar Tasa Manual</h2>
            <div className="space-y-4">
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">Base</label>
                  <select className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm" value={newRate.base} onChange={e => setNewRate({ ...newRate, base: e.target.value })}>
                    {currencies.map(c => <option key={c} value={c}>{c}</option>)}
                  </select>
                </div>
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">Destino</label>
                  <select className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm" value={newRate.target} onChange={e => setNewRate({ ...newRate, target: e.target.value })}>
                    {currencies.map(c => <option key={c} value={c}>{c}</option>)}
                  </select>
                </div>
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-1">Tasa</label>
                <input type="number" step="0.0001" className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm" value={newRate.rate} onChange={e => setNewRate({ ...newRate, rate: Number(e.target.value) })} />
              </div>
            </div>
            <div className="flex justify-end gap-3 mt-6">
              <button onClick={() => setShowModal(false)} className="px-4 py-2 text-sm font-medium text-slate-600 border border-slate-200 rounded-lg hover:bg-slate-50">Cancelar</button>
              <button onClick={handleAddRate} className="px-4 py-2 text-sm font-medium text-white bg-blue-600 rounded-lg hover:bg-blue-700">Guardar</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
