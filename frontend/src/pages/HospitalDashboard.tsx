import React, { useState, useEffect } from 'react';
import api from '../api/axios';
import { Hospital, EmergencyResponse } from '../types';
import { Building2, Bed, Activity, Phone, AlertCircle, RefreshCw } from 'lucide-react';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

export const HospitalDashboard: React.FC = () => {
  const [hospital, setHospital] = useState<Hospital | null>(null);
  const [incoming, setIncoming] = useState<EmergencyResponse[]>([]);
  const [availableBeds, setAvailableBeds] = useState(0);
  const [availableIcuBeds, setAvailableIcuBeds] = useState(0);
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState('');

  useEffect(() => {
    fetchHospitalData();
  }, []);

  useEffect(() => {
    if (!hospital) return;

    const wsUrl = import.meta.env.VITE_WS_URL || 'http://localhost:8080/ws';
    const client = new Client({
      webSocketFactory: () => new SockJS(wsUrl),
      onConnect: () => {
        client.subscribe(`/topic/hospitals/${hospital.id}/incoming`, (msg) => {
          const updated: EmergencyResponse = JSON.parse(msg.body);
          setIncoming((prev) => {
            const filtered = prev.filter((item) => item.id !== updated.id);
            if (['DELIVERED', 'CANCELLED', 'FAILED'].includes(updated.status)) {
              return filtered;
            }
            return [updated, ...filtered];
          });
        });
      },
    });

    client.activate();
    return () => {
      client.deactivate();
    };
  }, [hospital?.id]);

  const fetchHospitalData = async () => {
    try {
      const hRes = await api.get('/hospital/my-hospital');
      if (hRes.data.data) {
        setHospital(hRes.data.data);
        setAvailableBeds(hRes.data.data.availableBeds);
        setAvailableIcuBeds(hRes.data.data.availableIcuBeds);
      }

      const incRes = await api.get('/hospital/incoming');
      if (incRes.data.data) {
        setIncoming(incRes.data.data);
      }
    } catch (err) {
      console.error(err);
    }
  };

  const handleCapacityUpdate = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!hospital) return;
    setLoading(true);
    setMessage('');

    try {
      const res = await api.patch('/hospital/capacity', {
        availableBeds,
        availableIcuBeds,
      });
      setHospital(res.data.data);
      setMessage('Capacity updated successfully');
    } catch (err: any) {
      setMessage(err.response?.data?.message || 'Failed to update capacity');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-7xl mx-auto px-4 py-6 space-y-6">
      {hospital && (
        <div className="bg-white p-6 rounded-2xl shadow-lg border border-gray-100 flex flex-wrap items-center justify-between gap-4">
          <div className="flex items-center gap-3">
            <div className="p-3 bg-blue-100 text-blue-800 rounded-xl">
              <Building2 className="w-8 h-8" />
            </div>
            <div>
              <h1 className="text-xl font-bold text-gray-900">{hospital.name}</h1>
              <p className="text-xs text-gray-500">{hospital.address} | Contact: {hospital.contact}</p>
            </div>
          </div>

          <div className="flex gap-4 text-center">
            <div className="bg-blue-50 px-4 py-2 rounded-xl border border-blue-200">
              <div className="text-xs text-blue-800 font-semibold uppercase">Regular Beds</div>
              <div className="text-lg font-extrabold text-blue-900">{hospital.availableBeds} / {hospital.totalBeds}</div>
            </div>
            <div className="bg-red-50 px-4 py-2 rounded-xl border border-red-200">
              <div className="text-xs text-red-800 font-semibold uppercase">ICU Beds</div>
              <div className="text-lg font-extrabold text-red-900">{hospital.availableIcuBeds} / {hospital.icuBeds}</div>
            </div>
          </div>
        </div>
      )}

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Incoming Ambulances Console */}
        <div className="lg:col-span-2 space-y-4">
          <div className="bg-white p-6 rounded-2xl shadow-lg border border-gray-100">
            <div className="flex items-center justify-between pb-4 border-b">
              <h2 className="text-lg font-bold text-gray-900 flex items-center gap-2">
                <Activity className="w-5 h-5 text-red-600 animate-pulse" />
                Incoming Emergency Ambulances ({incoming.length})
              </h2>
              <button onClick={fetchHospitalData} className="text-xs text-blue-600 hover:underline flex items-center gap-1">
                <RefreshCw className="w-3.5 h-3.5" /> Refresh
              </button>
            </div>

            <div className="mt-4 space-y-3">
              {incoming.length > 0 ? (
                incoming.map((item) => (
                  <div key={item.id} className="p-4 bg-gray-50 rounded-xl border border-gray-200 flex flex-wrap items-center justify-between gap-4">
                    <div className="space-y-1">
                      <div className="flex items-center gap-2">
                        <span className={`px-2.5 py-0.5 rounded text-[10px] font-extrabold uppercase ${
                          item.triagePriority === 'CRITICAL' ? 'bg-red-100 text-red-700' : 'bg-yellow-100 text-yellow-800'
                        }`}>
                          {item.triagePriority}
                        </span>
                        <span className="text-xs font-bold text-gray-700">Req #{item.id}</span>
                        <span className="text-xs text-blue-700 font-semibold bg-blue-100 px-2 py-0.5 rounded">
                          {item.status}
                        </span>
                      </div>
                      <div className="text-sm font-bold text-gray-900">Patient: {item.patientName}</div>
                      <div className="text-xs text-gray-600">Symptoms: {item.symptoms}</div>
                    </div>

                    <div className="text-right space-y-1">
                      <div className="text-xs text-gray-500 font-semibold">Ambulance</div>
                      <div className="text-sm font-extrabold text-gray-900">{item.vehicleNumber || 'Unassigned'}</div>
                      {item.driverPhone && (
                        <div className="text-xs text-blue-600 font-bold">{item.driverPhone}</div>
                      )}
                    </div>
                  </div>
                ))
              ) : (
                <div className="text-center py-12 text-xs text-gray-400">
                  No incoming emergency ambulances right now.
                </div>
              )}
            </div>
          </div>
        </div>

        {/* Capacity Management Panel */}
        <div className="space-y-6">
          <div className="bg-white p-6 rounded-2xl shadow-lg border border-gray-100">
            <h3 className="font-bold text-gray-900 border-b pb-2 mb-4">Manage Bed Availability</h3>

            {message && (
              <div className="mb-3 p-2 bg-blue-50 text-blue-700 text-xs rounded border border-blue-200">
                {message}
              </div>
            )}

            <form onSubmit={handleCapacityUpdate} className="space-y-4">
              <div>
                <label className="block text-xs font-semibold text-gray-700 uppercase mb-1">Available General Beds</label>
                <input
                  type="number"
                  min="0"
                  max={hospital?.totalBeds || 100}
                  value={availableBeds}
                  onChange={(e) => setAvailableBeds(parseInt(e.target.value) || 0)}
                  className="w-full p-2.5 border border-gray-300 rounded-lg text-sm outline-none"
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-gray-700 uppercase mb-1">Available ICU Beds</label>
                <input
                  type="number"
                  min="0"
                  max={hospital?.icuBeds || 50}
                  value={availableIcuBeds}
                  onChange={(e) => setAvailableIcuBeds(parseInt(e.target.value) || 0)}
                  className="w-full p-2.5 border border-gray-300 rounded-lg text-sm outline-none"
                />
              </div>

              <button
                type="submit"
                disabled={loading}
                className="w-full py-2.5 bg-blue-600 hover:bg-blue-700 text-white font-semibold rounded-lg text-sm transition"
              >
                {loading ? 'Updating...' : 'Save Capacity'}
              </button>
            </form>
          </div>
        </div>
      </div>
    </div>
  );
};
