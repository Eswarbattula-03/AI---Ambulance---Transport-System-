import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import { Navbar } from './components/Navbar';
import { ProtectedRoute } from './components/ProtectedRoute';

import { LoginPage } from './pages/LoginPage';
import { RegisterPage } from './pages/RegisterPage';
import { PatientDashboard } from './pages/PatientDashboard';
import { DriverDashboard } from './pages/DriverDashboard';
import { HospitalDashboard } from './pages/HospitalDashboard';
import { AdminDashboard } from './pages/AdminDashboard';

const HomeRedirect: React.FC = () => {
  const { user, isAuthenticated } = useAuth();
  if (!isAuthenticated || !user) {
    return <Navigate to="/login" replace />;
  }

  switch (user.role) {
    case 'PATIENT':
      return <Navigate to="/patient" replace />;
    case 'AMBULANCE_DRIVER':
      return <Navigate to="/driver" replace />;
    case 'HOSPITAL_ADMIN':
      return <Navigate to="/hospital" replace />;
    case 'SYSTEM_ADMIN':
      return <Navigate to="/admin" replace />;
    default:
      return <Navigate to="/login" replace />;
  }
};

export const App: React.FC = () => {
  return (
    <AuthProvider>
      <Router>
        <div className="min-h-screen bg-gray-50 flex flex-col">
          <Navbar />
          <main className="flex-1">
            <Routes>
              <Route path="/" element={<HomeRedirect />} />
              <Route path="/login" element={<LoginPage />} />
              <Route path="/register" element={<RegisterPage />} />

              <Route
                path="/patient"
                element={
                  <ProtectedRoute allowedRoles={['PATIENT', 'SYSTEM_ADMIN']}>
                    <PatientDashboard />
                  </ProtectedRoute>
                }
              />

              <Route
                path="/driver"
                element={
                  <ProtectedRoute allowedRoles={['AMBULANCE_DRIVER', 'SYSTEM_ADMIN']}>
                    <DriverDashboard />
                  </ProtectedRoute>
                }
              />

              <Route
                path="/hospital"
                element={
                  <ProtectedRoute allowedRoles={['HOSPITAL_ADMIN', 'SYSTEM_ADMIN']}>
                    <HospitalDashboard />
                  </ProtectedRoute>
                }
              />

              <Route
                path="/admin"
                element={
                  <ProtectedRoute allowedRoles={['SYSTEM_ADMIN']}>
                    <AdminDashboard />
                  </ProtectedRoute>
                }
              />

              <Route path="*" element={<Navigate to="/" replace />} />
            </Routes>
          </main>
        </div>
      </Router>
    </AuthProvider>
  );
};

export default App;
