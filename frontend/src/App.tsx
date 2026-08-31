/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
import { lazy, Suspense } from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Layout from './components/Layout';
import { AuthProvider } from './context/AuthContext';
import ProtectedRoute from './components/ProtectedRoute';

const Dashboard = lazy(() => import('./pages/Dashboard'));
const Clientes = lazy(() => import('./pages/Clientes'));
const Prospectos = lazy(() => import('./pages/Prospectos'));
const Ventas = lazy(() => import('./pages/Ventas'));
const Cotizaciones = lazy(() => import('./pages/Cotizaciones'));
const Pedidos = lazy(() => import('./pages/Pedidos'));
const ServicioClientePage = lazy(() => import('./pages/ServicioCliente'));
const CampanasMarketingPage = lazy(() => import('./pages/CampanasMarketing'));
const EmailMarketingPage = lazy(() => import('./pages/EmailMarketing'));
const WhatsAppBusinessPage = lazy(() => import('./pages/WhatsAppBusiness'));
const GestionDocumentalPage = lazy(() => import('./pages/GestionDocumental'));
const ContratosPage = lazy(() => import('./pages/Contratos'));
const FacturasPage = lazy(() => import('./pages/Facturas'));
const PQRSPage = lazy(() => import('./pages/PQRS'));
const EncuestasSatisfaccionPage = lazy(() => import('./pages/EncuestasSatisfaccion'));
const MesaAyudaPage = lazy(() => import('./pages/MesaAyuda'));
const Pipeline = lazy(() => import('./pages/Pipeline'));
const GdprCompliance = lazy(() => import('./pages/GdprCompliance'));
const MultiCurrency = lazy(() => import('./pages/MultiCurrency'));
const WorkflowBuilder = lazy(() => import('./pages/WorkflowBuilder'));
const LeadScoring = lazy(() => import('./pages/LeadScoring'));
const EmailTemplates = lazy(() => import('./pages/EmailTemplates'));
const Integrations = lazy(() => import('./pages/Integrations'));
const Gamification = lazy(() => import('./pages/Gamification'));
const ClientPortal = lazy(() => import('./pages/ClientPortal'));
const WhatsAppAI = lazy(() => import('./pages/WhatsAppAI'));
const AdvancedReports = lazy(() => import('./pages/AdvancedReports'));
const CPQ = lazy(() => import('./pages/CPQ'));
const ESignature = lazy(() => import('./pages/ESignature'));
const SSO = lazy(() => import('./pages/SSO'));
const Actividades = lazy(() => import('./pages/Actividades'));
const MetasComerciales = lazy(() => import('./pages/MetasComerciales'));
const ProductosServicios = lazy(() => import('./pages/ProductosServicios'));
const SuperAdmin = lazy(() => import('./pages/SuperAdmin'));
const Pricing = lazy(() => import('./pages/Pricing'));
const Billing = lazy(() => import('./pages/Billing'));
const Login = lazy(() => import('./pages/Login'));
const Register = lazy(() => import('./pages/Register'));

function PageLoader() {
  return (
    <div className="min-h-screen bg-slate-50 flex items-center justify-center" role="status" aria-live="polite">
      <div className="text-center">
        <div className="mx-auto h-10 w-10 animate-spin rounded-full border-4 border-blue-200 border-t-blue-600" />
        <p className="mt-3 text-sm font-medium text-slate-600">Cargando módulo…</p>
      </div>
    </div>
  );
}

function App() {
  return (
    <AuthProvider>
      <Router>
        <Suspense fallback={<PageLoader />}>
          <Routes>
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="/pricing" element={<Pricing />} />
          <Route
            path="/*"
            element={
              <ProtectedRoute>
                <Layout>
                  <Routes>
                    <Route path="/" element={<Dashboard />} />
                    <Route path="/clientes" element={<Clientes />} />
                    <Route path="/prospectos" element={<Prospectos />} />
                    <Route path="/ventas" element={<Ventas />} />
                    <Route path="/cotizaciones" element={<Cotizaciones />} />
                    <Route path="/pedidos" element={<Pedidos />} />
                    <Route path="/servicio-cliente" element={<ServicioClientePage />} />
                    <Route path="/campanas-marketing" element={<CampanasMarketingPage />} />
                    <Route path="/email-marketing" element={<EmailMarketingPage />} />
                    <Route path="/whatsapp-business" element={<WhatsAppBusinessPage />} />
                    <Route path="/gestion-documental" element={<GestionDocumentalPage />} />
                    <Route path="/contratos" element={<ContratosPage />} />
                    <Route path="/facturas" element={<FacturasPage />} />
                    <Route path="/pqrs" element={<PQRSPage />} />
                    <Route path="/encuestas-satisfaccion" element={<EncuestasSatisfaccionPage />} />
                    <Route path="/mesa-ayuda" element={<MesaAyudaPage />} />
                    <Route path="/pipeline" element={<Pipeline />} />
                    <Route path="/gdpr" element={<GdprCompliance />} />
                    <Route path="/multi-currency" element={<MultiCurrency />} />
                    <Route path="/workflows" element={<WorkflowBuilder />} />
                    <Route path="/lead-scoring" element={<LeadScoring />} />
                    <Route path="/email-templates" element={<EmailTemplates />} />
                    <Route path="/integrations" element={<Integrations />} />
                    <Route path="/gamification" element={<Gamification />} />
                    <Route path="/client-portal" element={<ClientPortal />} />
                    <Route path="/whatsapp-ai" element={<WhatsAppAI />} />
                    <Route path="/reports" element={<AdvancedReports />} />
                    <Route path="/cpq" element={<CPQ />} />
                    <Route path="/esignature" element={<ESignature />} />
                    <Route path="/sso" element={<SSO />} />
                    <Route path="/actividades" element={<Actividades />} />
                    <Route path="/metas" element={<MetasComerciales />} />
                    <Route path="/productos" element={<ProductosServicios />} />
                    <Route path="/superadmin" element={<SuperAdmin />} />
                    <Route path="/billing" element={<Billing />} />
                  </Routes>
                </Layout>
              </ProtectedRoute>
            }
          />
          </Routes>
        </Suspense>
      </Router>
    </AuthProvider>
  );
}

export default App;
