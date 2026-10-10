import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import api from '../api/axios';
import { Ambulance, Lock, Mail, AlertCircle } from 'lucide-react';

export const LoginPage: React.FC = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const { login } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      const response = await api.post('/auth/login', { email, password });
      const authData = response.data.data;
      login(authData);

      switch (authData.role) {
        case 'PATIENT':
          navigate('/patient');
          break;
        case 'AMBULANCE_DRIVER':
          navigate('/driver');
          break;
        case 'HOSPITAL_ADMIN':
          navigate('/hospital');
          break;
        case 'SYSTEM_ADMIN':
          navigate('/admin');
          break;
        default:
          navigate('/');
      }
    } catch (err: any) {
      setError(err.response?.data?.message || 'Login failed. Please check credentials.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-[85vh] flex items-center justify-center px-4">
      <div className="max-w-md w-full bg-white p-8 rounded-2xl shadow-xl border border-gray-100">
        <div className="text-center mb-8">
          <div className="inline-flex p-3 bg-red-100 rounded-full text-red-600 mb-3">
            <Ambulance className="w-10 h-10" />
          </div>
          <h2 className="text-2xl font-bold text-gray-900">LifeRescue Portal</h2>
          <p className="text-sm text-gray-500 mt-1">AI-Powered Ambulance & Emergency Transportation</p>
        </div>

        {error && (
          <div className="mb-4 p-3 bg-red-50 border-l-4 border-red-500 text-red-700 text-sm flex items-center gap-2">
            <AlertCircle className="w-5 h-5 flex-shrink-0" />
            <span>{error}</span>
          </div>
        )}

        <form onSubmit={handleSubmit} className="space-y-5">
          <div>
            <label className="block text-xs font-semibold text-gray-700 uppercase tracking-wider mb-2">Email Address</label>
            <div className="relative">
              <Mail className="w-5 h-5 absolute left-3 top-3 text-gray-400" />
              <input
                type="email"
                required
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="patient.suresh@liferescue.com"
                className="w-full pl-10 pr-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-red-500 focus:border-red-500 outline-none text-sm transition"
              />
            </div>
          </div>

          <div>
            <label className="block text-xs font-semibold text-gray-700 uppercase tracking-wider mb-2">Password</label>
            <div className="relative">
              <Lock className="w-5 h-5 absolute left-3 top-3 text-gray-400" />
              <input
                type="password"
                required
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="••••••••"
                className="w-full pl-10 pr-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-red-500 focus:border-red-500 outline-none text-sm transition"
              />
            </div>
          </div>

          <button
            type="submit"
            disabled={loading}
            className="w-full bg-red-600 hover:bg-red-700 text-white font-semibold py-3 rounded-lg shadow-md hover:shadow-lg transition disabled:opacity-50 text-sm"
          >
            {loading ? 'Authenticating...' : 'Sign In'}
          </button>
        </form>

        <div className="mt-6 text-center text-xs text-gray-600">
          Don't have an account?{' '}
          <Link to="/register" className="text-red-600 font-semibold hover:underline">
            Register here
          </Link>
        </div>

        <div className="mt-6 p-3 bg-gray-50 rounded-lg border border-gray-200 text-xs text-gray-600 space-y-1">
          <div className="font-semibold text-gray-700">Seed Demo Credentials (Password: Password123!)</div>
          <div>Patient: <code className="bg-gray-200 px-1 py-0.5 rounded text-red-700">patient.suresh@liferescue.com</code></div>
          <div>Driver: <code className="bg-gray-200 px-1 py-0.5 rounded text-blue-700">driver.ramesh@liferescue.com</code></div>
          <div>Hospital Admin: <code className="bg-gray-200 px-1 py-0.5 rounded text-green-700">hospital.admin@liferescue.com</code></div>
          <div>System Admin: <code className="bg-gray-200 px-1 py-0.5 rounded text-purple-700">admin@liferescue.com</code></div>
        </div>
      </div>
    </div>
  );
};
