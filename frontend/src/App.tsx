import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
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
import Login from './pages/Login';

function ProtectedRoute({ children }: { children: React.ReactNode }) {
  const usuario = localStorage.getItem('usuario');
  if (!usuario) {
    return <Navigate to="/login" />;
  }
  return <>{children}</>;
}

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/login" element={<Login />} />
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
                </Routes>
              </Layout>
            </ProtectedRoute>
          }
        />
      </Routes>
    </Router>
  );
}

export default App;
