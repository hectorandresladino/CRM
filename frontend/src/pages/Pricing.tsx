/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
import { useNavigate } from 'react-router-dom';
import { Check, Zap, Crown, Rocket } from 'lucide-react';

const PLANS = [
  {
    name: 'STARTER',
    price: 29,
    icon: Rocket,
    color: 'border-slate-200',
    headerBg: 'bg-slate-50',
    features: [
      'Hasta 5 usuarios',
      'Hasta 100 clientes',
      '1 GB almacenamiento',
      'Pipeline Kanban',
      'GestiÃ³n de prospectos',
      'Cotizaciones y pedidos',
      'Email marketing bÃ¡sico',
      'Soporte por email',
    ],
  },
  {
    name: 'PROFESSIONAL',
    price: 79,
    icon: Zap,
    color: 'border-blue-500 ring-2 ring-blue-500',
    headerBg: 'bg-blue-50',
    popular: true,
    features: [
      'Hasta 20 usuarios',
      'Hasta 1.000 clientes',
      '10 GB almacenamiento',
      'Todo lo de Starter +',
      'WhatsApp Business API',
      'IA Conversacional',
      'Automatizaciones (10)',
      'Reportes avanzados',
      'CPQ - Configure Price Quote',
      'Firma electrÃ³nica',
      'Multi-moneda',
      'Soporte prioritario',
    ],
  },
  {
    name: 'ENTERPRISE',
    price: 199,
    icon: Crown,
    color: 'border-indigo-200',
    headerBg: 'bg-indigo-50',
    features: [
      'Usuarios ilimitados',
      'Clientes ilimitados',
      '100 GB almacenamiento',
      'Todo lo de Professional +',
      'White label completo',
      'API access + Webhooks',
      'Automatizaciones ilimitadas',
      'SSO (SAML/OAuth)',
      'Portal de clientes',
      'GamificaciÃ³n',
      'SLA 99.9% uptime',
      'Soporte 24/7 + onboarding',
    ],
  },
];

export default function Pricing() {
  const navigate = useNavigate();

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-50 to-slate-100 py-12 px-4">
      <div className="max-w-6xl mx-auto">
        <div className="text-center mb-12">
          <h1 className="text-4xl font-bold text-slate-900 mb-3">Planes y Precios</h1>
          <p className="text-lg text-slate-600">Elige el plan ideal para tu empresa. 14 dÃ­as de prueba gratis, sin tarjeta de crÃ©dito.</p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-12">
          {PLANS.map((plan) => {
            const Icon = plan.icon;
            return (
              <div key={plan.name} className={`bg-white rounded-2xl border-2 ${plan.color} relative overflow-hidden`}>
                {plan.popular && (
                  <div className="absolute top-0 right-0 bg-blue-500 text-white text-xs font-bold px-3 py-1 rounded-bl-lg">MÃS POPULAR</div>
                )}
                <div className={`${plan.headerBg} p-6 border-b border-slate-100`}>
                  <div className="flex items-center gap-3 mb-2">
                    <Icon className={`w-6 h-6 ${plan.popular ? 'text-blue-600' : 'text-slate-600'}`} />
                    <h3 className="text-xl font-bold text-slate-900">{plan.name}</h3>
                  </div>
                  <div className="flex items-baseline gap-1">
                    <span className="text-4xl font-bold text-slate-900">${plan.price}</span>
                    <span className="text-slate-500">/mes</span>
                  </div>
                  <p className="text-sm text-slate-500 mt-1">FacturaciÃ³n mensual Â· Cancela cuando quieras</p>
                </div>
                <div className="p-6">
                  <ul className="space-y-3">
                    {plan.features.map((f, i) => (
                      <li key={i} className="flex items-start gap-2 text-sm">
                        <Check className={`w-4 h-4 mt-0.5 flex-shrink-0 ${plan.popular ? 'text-blue-600' : 'text-green-600'}`} />
                        <span className="text-slate-700">{f}</span>
                      </li>
                    ))}
                  </ul>
                  <button
                    onClick={() => navigate('/register', { state: { planName: plan.name } })}
                    className={`w-full mt-6 py-3 px-4 rounded-lg font-semibold transition-all ${
                      plan.popular
                        ? 'bg-blue-600 text-white hover:bg-blue-700 shadow-lg'
                        : 'bg-slate-100 text-slate-900 hover:bg-slate-200'
                    }`}
                  >
                    Empezar Trial Gratis
                  </button>
                </div>
              </div>
            );
          })}
        </div>

        <div className="bg-white rounded-xl border border-slate-200 p-8 text-center">
          <h2 className="text-xl font-bold text-slate-900 mb-2">Â¿Necesitas algo personalizado?</h2>
          <p className="text-slate-600 mb-4">Planes Enterprise con infraestructura dedicada, integraciones a medida y SLA corporativo.</p>
          <button onClick={() => navigate('/register')} className="px-6 py-2 text-sm font-medium text-white bg-slate-900 rounded-lg hover:bg-slate-800">Contactar Ventas</button>
        </div>

        <div className="text-center mt-8">
          <button onClick={() => navigate('/login')} className="text-sm text-slate-600 hover:text-slate-900 font-medium">Â¿Ya tienes cuenta? Inicia sesiÃ³n â†’</button>
        </div>
      </div>
    </div>
  );
}
