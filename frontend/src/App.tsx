/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Layout from './components/Layout';
import Dashboard from './pages/Dashboard';
import Clientes from './pages/Clientes';
import Prospectos from './pages/Prospectos';
import Ventas from './pages/Ventas';
import Cotizaciones from './pages/Cotizaciones';
import Pedidos from './pages/Pedidos';
import ServicioClientePage from './pages/ServicioCliente';
import CampanasMarketingPage from './pages/CampanasMarketing';
import EmailMarketingPage from './pages/EmailMarketing';
import WhatsAppBusinessPage from './pages/WhatsAppBusiness';
import GestionDocumentalPage from './pages/GestionDocumental';
import ContratosPage from './pages/Contratos';
import FacturasPage from './pages/Facturas';
import PQRSPage from './pages/PQRS';
import EncuestasSatisfaccionPage from './pages/EncuestasSatisfaccion';
import MesaAyudaPage from './pages/MesaAyuda';
import Pipeline from './pages/Pipeline';
import GdprCompliance from './pages/GdprCompliance';
import MultiCurrency from './pages/MultiCurrency';
import WorkflowBuilder from './pages/WorkflowBuilder';
import LeadScoring from './pages/LeadScoring';
import EmailTemplates from './pages/EmailTemplates';
import Integrations from './pages/Integrations';
import Gamification from './pages/Gamification';
import ClientPortal from './pages/ClientPortal';
import WhatsAppAI from './pages/WhatsAppAI';
import AdvancedReports from './pages/AdvancedReports';
import CPQ from './pages/CPQ';
import ESignature from './pages/ESignature';
import SSO from './pages/SSO';
import Actividades from './pages/Actividades';
import MetasComerciales from './pages/MetasComerciales';
import ProductosServicios from './pages/ProductosServicios';
import SuperAdmin from './pages/SuperAdmin';
import Pricing from './pages/Pricing';
import Billing from './pages/Billing';
import Login from './pages/Login';
import Register from './pages/Register';
import { AuthProvider } from './context/AuthContext';
import ProtectedRoute from './components/ProtectedRoute';

function App() {
  return (
    <AuthProvider>
      <Router>
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
      </Router>
    </AuthProvider>
  );
}

export default App;
