import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";
import { AuthProvider } from "./context/AuthContext";
import ProtectedRoute from "./routes/ProtectedRoute";
import AppLayout from "./components/layout/AppLayout";
import Login from "./pages/Login";
import Register from "./pages/Register";
import Squad from "./pages/Squad";
import PlayerDetail from "./pages/PlayerDetail";
import { CurrencyProvider } from "./context/CurrencyContext";
import PlayerForm from "./pages/PlayerForm";
import PlayerDeveloperEdit from "./pages/PlayerDeveloperEdit";
import Contracts from "./pages/Contracts";
import Finance from "./pages/Finance";
import "./App.css";

function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />

          <Route
            element={
              <ProtectedRoute>
                <CurrencyProvider>
                  <AppLayout />
                </CurrencyProvider>
              </ProtectedRoute>
            }
          >
            <Route path="/" element={<Navigate to="/squad" replace />} />
            <Route path="/dashboard" element={<Navigate to="/squad" replace />} />
            <Route path="/squad" element={<Squad />} />
            <Route path="/contracts" element={<Contracts />} />
            <Route path="/finance" element={<Finance />} />
            <Route path="/players/:id" element={<PlayerDetail />} />
            <Route path="/players/new" element={<PlayerForm />} />
            <Route path="/players/:id/dev" element={<PlayerDeveloperEdit />} />
          </Route>
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  );
}

export default App;
