import React, { useState, useEffect } from 'react';
import api from '../api/axios';
import { EmergencyResponse } from '../types';
import { Users, Ambulance, Building2, Activity, ShieldCheck, FileText } from 'lucide-react';

export const AdminDashboard: React.FC = () => {
  const [stats, setStats] = useState<any>(null);
  const [emergencies, setEmergencies] = useState<EmergencyResponse[]>([]);

  useEffect(() => {
    fetchAdminData();
  }, []);

  const fetchAdminData = async () => {
    try {
      const statsRes = await api.get('/admin/analytics');
      if (statsRes.data.data) {
        setStats(statsRes.data.data);
      }

      const emgRes = await api.get('/admin/emergencies');
      if (emgRes.data.data) {
        setEmergencies(emgRes.data.data);
      }
    } catch (err) {
      console.error(err);
    }
  };

  return (
    <div className="max-w-7xl mx-auto px-4 py-6 space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">System Command Center</h1>
          <p className="text-xs text-gray-500">Fleet Operations, Analytics & Emergency Control</p>
        </div>
        <span className="bg-purple-100 text-purple-800 text-xs font-bold px-3 py-1 rounded-full uppercase border border-purple-200">
          SYSTEM ADMIN ACTIVE
        </span>
      </div>

      {stats && (
        <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
          <div className="bg-white p-5 rounded-2xl shadow-md border border-gray-100 flex items-center gap-4">
            <div className="p-3 bg-blue-100 text-blue-600 rounded-xl">
              <Users className="w-6 h-6" />
            </div>
            <div>
              <div className="text-xs font-semibold text-gray-400 uppercase">Total Users</div>
              <div className="text-2xl font-extrabold text-gray-900">{stats.totalUsers}</div>
            </div>
          </div>

          <div className="bg-white p-5 rounded-2xl shadow-md border border-gray-100 flex items-center gap-4">
            <div className="p-3 bg-yellow-100 text-yellow-600 rounded-xl">
              <Ambulance className="w-6 h-6" />
            </div>
            <div>
              <div className="text-xs font-semibold text-gray-400 uppercase">Ambulance Fleet</div>
              <div className="text-2xl font-extrabold text-gray-900">{stats.totalAmbulances}</div>
            </div>
          </div>

          <div className="bg-white p-5 rounded-2xl shadow-md border border-gray-100 flex items-center gap-4">
            <div className="p-3 bg-green-100 text-green-600 rounded-xl">
              <Building2 className="w-6 h-6" />
            </div>
            <div>
              <div className="text-xs font-semibold text-gray-400 uppercase">Hospitals</div>
              <div className="text-2xl font-extrabold text-gray-900">{stats.totalHospitals}</div>
            </div>
          </div>

          <div className="bg-white p-5 rounded-2xl shadow-md border border-gray-100 flex items-center gap-4">
            <div className="p-3 bg-red-100 text-red-600 rounded-xl">
              <Activity className="w-6 h-6" />
            </div>
            <div>
              <div className="text-xs font-semibold text-gray-400 uppercase">Active Emergencies</div>
              <div className="text-2xl font-extrabold text-gray-900">{stats.activeEmergencies}</div>
            </div>
          </div>
        </div>
      )}

      {/* Audit & Emergency Monitoring */}
      <div className="bg-white rounded-2xl shadow-lg border border-gray-100 p-6 space-y-4">
        <h2 className="text-base font-bold text-gray-900 border-b pb-3 flex items-center gap-2">
          <FileText className="w-5 h-5 text-purple-600" />
          Recent System Emergency Requests
        </h2>

        <div className="overflow-x-auto">
          <table className="w-full text-left border-collapse text-xs">
            <thead>
              <tr className="border-b bg-gray-50 text-gray-500 uppercase tracking-wider">
                <th className="py-3 px-4 font-semibold">ID</th>
                <th className="py-3 px-4 font-semibold">Patient</th>
                <th className="py-3 px-4 font-semibold">Symptoms</th>
                <th className="py-3 px-4 font-semibold">Triage Priority</th>
                <th className="py-3 px-4 font-semibold">Ambulance</th>
                <th className="py-3 px-4 font-semibold">Hospital</th>
                <th className="py-3 px-4 font-semibold">Status</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-100 text-gray-700">
              {emergencies.map((emg) => (
                <tr key={emg.id} className="hover:bg-gray-50 transition">
                  <td className="py-3 px-4 font-bold text-gray-900">#{emg.id}</td>
                  <td className="py-3 px-4 font-medium">{emg.patientName}</td>
                  <td className="py-3 px-4 text-gray-600 max-w-xs truncate">{emg.symptoms}</td>
                  <td className="py-3 px-4">
                    <span className={`px-2 py-0.5 rounded font-extrabold text-[10px] ${
                      emg.triagePriority === 'CRITICAL' ? 'bg-red-100 text-red-700' : 'bg-yellow-100 text-yellow-800'
                    }`}>
                      {emg.triagePriority}
                    </span>
                  </td>
                  <td className="py-3 px-4 font-mono">{emg.vehicleNumber || 'Unassigned'}</td>
                  <td className="py-3 px-4">{emg.hospitalName || 'Unassigned'}</td>
                  <td className="py-3 px-4">
                    <span className="bg-blue-100 text-blue-800 font-bold px-2 py-0.5 rounded">
                      {emg.status}
                    </span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};
