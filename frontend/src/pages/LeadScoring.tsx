import { useState, useEffect } from 'react';
import { 
  Trophy, Star, Award, Target, Crown, Medal, Flame
} from 'lucide-react';
import apiClient from '../services/api';

interface LeadScore {
  id?: number;
  prospectoId: number;
  score: number;
  grade: string;
  factors?: string;
  emailEngagement?: number;
  websiteVisits?: number;
  whatsappInteractions?: number;
}

const GRADE_CONFIG: Record<string, { color: string; bg: string; label: string; icon: React.ElementType }> = {
  A: { color: 'text-green-700', bg: 'bg-green-100', label: 'Hot Lead', icon: Flame },
  B: { color: 'text-blue-700', bg: 'bg-blue-100', label: 'Warm Lead', icon: Target },
  C: { color: 'text-yellow-700', bg: 'bg-yellow-100', label: 'Tibio', icon: Star },
  D: { color: 'text-orange-700', bg: 'bg-orange-100', label: 'Frío', icon: Medal },
  F: { color: 'text-red-700', bg: 'bg-red-100', label: 'No calificado', icon: Crown },
};

export default function LeadScoring() {
  const [scores, setScores] = useState<LeadScore[]>([]);
  const [loading, setLoading] = useState(true);

  const loadData = async () => {
    setLoading(true);
    try {
      const res = await apiClient.get('/api/lead-scores');
      setScores(res.data);
    } catch (e) {
      console.error('Error loading scores:', e);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { loadData(); }, []);

  const gradeA = scores.filter(s => s.grade === 'A').length;
  const gradeB = scores.filter(s => s.grade === 'B').length;
  const avgScore = scores.length > 0 ? Math.round(scores.reduce((sum, s) => sum + s.score, 0) / scores.length) : 0;

  return (
    <div className="p-6">
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-2xl font-bold text-slate-900 flex items-center gap-2">
            <Target className="w-7 h-7 text-purple-600" />
            Lead Scoring
          </h1>
          <p className="text-sm text-slate-500 mt-1">Puntuación automática de prospectos basada en engagement</p>
        </div>
      </div>

      {/* Stats */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4 mb-6">
        <div className="bg-white rounded-xl border border-slate-200 p-5">
          <div className="flex items-center gap-3">
            <div className="bg-green-50 p-3 rounded-lg"><Flame className="w-6 h-6 text-green-600" /></div>
            <div><p className="text-2xl font-bold text-slate-900">{gradeA}</p><p className="text-xs text-slate-500">Hot Leads (A)</p></div>
          </div>
        </div>
        <div className="bg-white rounded-xl border border-slate-200 p-5">
          <div className="flex items-center gap-3">
            <div className="bg-blue-50 p-3 rounded-lg"><Target className="w-6 h-6 text-blue-600" /></div>
            <div><p className="text-2xl font-bold text-slate-900">{gradeB}</p><p className="text-xs text-slate-500">Warm Leads (B)</p></div>
          </div>
        </div>
        <div className="bg-white rounded-xl border border-slate-200 p-5">
          <div className="flex items-center gap-3">
            <div className="bg-purple-50 p-3 rounded-lg"><Trophy className="w-6 h-6 text-purple-600" /></div>
            <div><p className="text-2xl font-bold text-slate-900">{avgScore}</p><p className="text-xs text-slate-500">Score promedio</p></div>
          </div>
        </div>
        <div className="bg-white rounded-xl border border-slate-200 p-5">
          <div className="flex items-center gap-3">
            <div className="bg-orange-50 p-3 rounded-lg"><Award className="w-6 h-6 text-orange-600" /></div>
            <div><p className="text-2xl font-bold text-slate-900">{scores.length}</p><p className="text-xs text-slate-500">Total leads</p></div>
          </div>
        </div>
      </div>

      {/* Score list */}
      <div className="bg-white rounded-xl border border-slate-200 overflow-hidden">
        <table className="w-full">
          <thead className="bg-slate-50 border-b border-slate-200">
            <tr>
              <th className="text-left px-4 py-3 text-xs font-semibold text-slate-600 uppercase tracking-wider">Prospecto ID</th>
              <th className="text-left px-4 py-3 text-xs font-semibold text-slate-600 uppercase tracking-wider">Score</th>
              <th className="text-left px-4 py-3 text-xs font-semibold text-slate-600 uppercase tracking-wider">Grade</th>
              <th className="text-left px-4 py-3 text-xs font-semibold text-slate-600 uppercase tracking-wider">Email</th>
              <th className="text-left px-4 py-3 text-xs font-semibold text-slate-600 uppercase tracking-wider">WhatsApp</th>
              <th className="text-left px-4 py-3 text-xs font-semibold text-slate-600 uppercase tracking-wider">Web Visits</th>
              <th className="text-left px-4 py-3 text-xs font-semibold text-slate-600 uppercase tracking-wider">Factores</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-100">
            {scores.map((s) => {
              const cfg = GRADE_CONFIG[s.grade] || GRADE_CONFIG.F;
              const GradeIcon = cfg.icon;
              return (
                <tr key={s.id} className="hover:bg-slate-50">
                  <td className="px-4 py-3 text-sm font-medium text-slate-900">#{s.prospectoId}</td>
                  <td className="px-4 py-3">
                    <div className="flex items-center gap-2">
                      <div className="w-16 bg-slate-100 rounded-full h-2">
                        <div className={`h-2 rounded-full ${s.score >= 80 ? 'bg-green-500' : s.score >= 60 ? 'bg-blue-500' : s.score >= 40 ? 'bg-yellow-500' : 'bg-red-500'}`} style={{ width: `${s.score}%` }} />
                      </div>
                      <span className="text-sm font-semibold text-slate-700">{s.score}</span>
                    </div>
                  </td>
                  <td className="px-4 py-3">
                    <span className={`inline-flex items-center gap-1 px-2 py-1 rounded-full text-xs font-medium ${cfg.bg} ${cfg.color}`}>
                      <GradeIcon className="w-3 h-3" /> {s.grade} - {cfg.label}
                    </span>
                  </td>
                  <td className="px-4 py-3 text-sm text-slate-600">{s.emailEngagement || 0}</td>
                  <td className="px-4 py-3 text-sm text-slate-600">{s.whatsappInteractions || 0}</td>
                  <td className="px-4 py-3 text-sm text-slate-600">{s.websiteVisits || 0}</td>
                  <td className="px-4 py-3 text-xs text-slate-500 max-w-xs truncate">{s.factors}</td>
                </tr>
              );
            })}
          </tbody>
        </table>
        {scores.length === 0 && !loading && (
          <div className="text-center py-12 text-slate-400">
            <Target className="w-12 h-12 mx-auto mb-3" />
            <p>No hay scores calculados. Los scores se generan automáticamente al evaluar prospectos.</p>
          </div>
        )}
        {loading && <div className="text-center py-12 text-slate-400">Cargando...</div>}
      </div>
    </div>
  );
}
