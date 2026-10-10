import React, { useState, useEffect } from 'react';
import api from '../api/axios';
import { EmergencyResponse, EmergencyStatus } from '../types';
import { LiveMap } from '../components/LiveMap';
import { Ambulance, MapPin, Navigation, Phone, CheckCircle, ArrowRight, ShieldAlert, AlertCircle, Clock } from 'lucide-react';

export const DriverDashboard: React.FC = () => {
  const [assignment, setAssignment] = useState<EmergencyResponse | null>(null);
  const [pendingRequests, setPendingRequests] = useState<EmergencyResponse[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [successMsg, setSuccessMsg] = useState('');

  useEffect(() => {
    fetchDriverConsole();
    const interval = setInterval(fetchDriverConsole, 3000);
    return () => clearInterval(interval);
  }, []);

  // Simulate live GPS telemetry streaming to backend when assigned
  useEffect(() => {
    if (!assignment) return;

    const gpsInterval = setInterval(() => {
      if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition((pos) => {
          api.post('/driver/location', {
            latitude: pos.coords.latitude,
            longitude: pos.coords.longitude,
          }).catch(console.error);
        });
      }
    }, 4000);

    return () => clearInterval(gpsInterval);
  }, [assignment?.id]);

  const fetchDriverConsole = async () => {
    try {
      const activeRes = await api.get('/driver/active-assignment');
      if (activeRes.data.data) {
        setAssignment(activeRes.data.data);
      } else {
        setAssignment(null);
      }

      const pendingRes = await api.get('/driver/pending');
      if (pendingRes.data.data) {
        setPendingRequests(pendingRes.data.data);
      }
    } catch (err) {
      console.error(err);
    }
  };

  const handleAcceptRequest = async (requestId: number) => {
    setError('');
    setSuccessMsg('');
    setLoading(true);
    try {
      const res = await api.post(`/driver/assignments/${requestId}/accept`);
      setAssignment(res.data.data);
      setSuccessMsg(`Successfully accepted Emergency Request #${requestId}`);
      fetchDriverConsole();
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to accept request. Another driver may have accepted it first.');
    } finally {
      setLoading(false);
    }
  };

  const handleUpdateStatus = async (nextStatus: EmergencyStatus) => {
    if (!assignment) return;
    setError('');
    setSuccessMsg('');
    setLoading(true);
    try {
      const res = await api.patch(`/driver/assignments/${assignment.id}/status`, {
        status: nextStatus,
        notes: `Status changed to ${nextStatus} by driver`,
      });
      setAssignment(res.data.data);
      setSuccessMsg(`Status updated to ${nextStatus}`);
      if (nextStatus === 'COMPLETED' || nextStatus === 'DELIVERED') {
        setTimeout(fetchDriverConsole, 1500);
      }
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to update status');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-7xl mx-auto px-4 py-6 space-y-6">
      {/* Driver Console Header */}
      <div className="bg-white p-6 rounded-2xl shadow-lg border border-gray-100 flex items-center justify-between">
        <div className="flex items-center gap-3">
          <div className="p-3 bg-yellow-100 text-yellow-800 rounded-xl">
            <Ambulance className="w-8 h-8" />
          </div>
          <div>
            <h1 className="text-xl font-bold text-gray-900">Ambulance Fleet Driver Console</h1>
            <p className="text-xs text-gray-500">Live Dispatch, Driver Actions & GPS Navigation</p>
          </div>
        </div>

        <div className="flex items-center gap-2">
          <span className="w-3 h-3 bg-green-500 rounded-full animate-ping"></span>
          <span className="text-xs font-bold text-green-700 uppercase">Telemetry Active</span>
        </div>
      </div>

      {error && (
        <div className="p-4 bg-red-50 text-red-700 text-sm rounded-xl border border-red-200 flex items-center gap-2">
          <AlertCircle className="w-5 h-5 flex-shrink-0" />
          <span>{error}</span>
        </div>
      )}

      {successMsg && (
        <div className="p-4 bg-green-50 text-green-700 text-sm rounded-xl border border-green-200 flex items-center gap-2">
          <CheckCircle className="w-5 h-5 flex-shrink-0" />
          <span>{successMsg}</span>
        </div>
      )}

      {/* Active Assignment Panel */}
      {assignment ? (
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          <div className="lg:col-span-2 space-y-6">
            <div className="bg-white p-6 rounded-2xl shadow-lg border border-gray-100">
              <div className="flex items-center justify-between pb-4 border-b">
                <div>
                  <span className="text-xs text-gray-400 font-semibold uppercase">Active Assignment #{assignment.id}</span>
                  <h2 className="text-lg font-bold text-gray-900">Patient Pickup & Destination Route</h2>
                </div>
                <span className={`px-3 py-1 rounded-full text-xs font-extrabold ${
                  assignment.triagePriority === 'CRITICAL' ? 'bg-red-100 text-red-700' : 'bg-yellow-100 text-yellow-800'
                }`}>
                  {assignment.triagePriority || 'HIGH'} PRIORITY
                </span>
              </div>

              <div className="mt-4">
                <LiveMap
                  pickupLat={assignment.pickupLat}
                  pickupLng={assignment.pickupLng}
                  ambulanceLat={assignment.ambulanceLat}
                  ambulanceLng={assignment.ambulanceLng}
                  hospitalLat={assignment.destinationLat}
                  hospitalLng={assignment.destinationLng}
                  hospitalName={assignment.hospitalName}
                  vehicleNumber={assignment.vehicleNumber}
                />
              </div>
            </div>
          </div>

          <div className="space-y-6">
            <div className="bg-white p-6 rounded-2xl shadow-lg border border-gray-100 space-y-4">
              <h3 className="font-bold text-gray-900 border-b pb-2">Active Booking Info</h3>

              <div>
                <div className="text-xs text-gray-400 uppercase font-semibold">Patient Name</div>
                <div className="text-base font-bold text-gray-900">{assignment.patientName}</div>
                {assignment.patientPhone && (
                  <a href={`tel:${assignment.patientPhone}`} className="text-xs text-blue-600 font-bold flex items-center gap-1 mt-1">
                    <Phone className="w-3.5 h-3.5" /> Call Patient ({assignment.patientPhone})
                  </a>
                )}
              </div>

              <div>
                <div className="text-xs text-gray-400 uppercase font-semibold">Symptoms / Case Info</div>
                <div className="text-xs text-gray-700 p-2.5 bg-gray-50 rounded-lg mt-1">{assignment.symptoms}</div>
              </div>

              <div>
                <div className="text-xs text-gray-400 uppercase font-semibold">Pickup Address</div>
                <div className="text-xs font-medium text-gray-800 mt-1">{assignment.pickupAddress || 'Specified via GPS'}</div>
              </div>

              <div>
                <div className="text-xs text-gray-400 uppercase font-semibold">Destination Hospital</div>
                <div className="text-xs font-bold text-gray-900 mt-1">{assignment.hospitalName || 'Auto-assigning hospital...'}</div>
              </div>

              {/* DRIVER ACTION BUTTONS matching STATUS FLOW */}
              <div className="pt-4 space-y-3 border-t">
                <div className="text-xs font-bold text-gray-700 uppercase">Driver Action Lifecycle</div>

                {(assignment.status === 'REQUESTED' || assignment.status === 'TRIAGED' || assignment.status === 'DISPATCHED') && (
                  <button
                    onClick={() => handleAcceptRequest(assignment.id)}
                    disabled={loading}
                    className="w-full py-3 bg-green-600 hover:bg-green-700 text-white font-extrabold rounded-xl text-sm shadow transition"
                  >
                    1. ACCEPT REQUEST
                  </button>
                )}

                {(assignment.status === 'ACCEPTED' || assignment.status === 'EN_ROUTE_TO_PICKUP') && (
                  <button
                    onClick={() => handleUpdateStatus('ARRIVED')}
                    disabled={loading}
                    className="w-full py-3 bg-blue-600 hover:bg-blue-700 text-white font-extrabold rounded-xl text-sm shadow transition"
                  >
                    2. REACHED PICKUP (MARK ARRIVED)
                  </button>
                )}

                {(assignment.status === 'ARRIVED' || assignment.status === 'PATIENT_ONBOARD') && (
                  <button
                    onClick={() => handleUpdateStatus('IN_TRANSIT')}
                    disabled={loading}
                    className="w-full py-3 bg-purple-600 hover:bg-purple-700 text-white font-extrabold rounded-xl text-sm shadow transition"
                  >
                    3. START TRIP (MARK IN TRANSIT)
                  </button>
                )}

                {(assignment.status === 'IN_TRANSIT' || assignment.status === 'EN_ROUTE_TO_HOSPITAL') && (
                  <button
                    onClick={() => handleUpdateStatus('COMPLETED')}
                    disabled={loading}
                    className="w-full py-3 bg-emerald-600 hover:bg-emerald-700 text-white font-extrabold rounded-xl text-sm shadow transition"
                  >
                    4. COMPLETE TRIP (DELIVER TO ER)
                  </button>
                )}

                {(assignment.status === 'COMPLETED' || assignment.status === 'DELIVERED') && (
                  <div className="p-3 bg-green-50 text-green-800 text-xs font-bold rounded-lg border border-green-200 text-center">
                    Trip Completed & Saved Permanently to MySQL
                  </div>
                )}
              </div>
            </div>
          </div>
        </div>
      ) : null}

      {/* Pending Requests Section */}
      <div className="bg-white p-6 rounded-2xl shadow-lg border border-gray-100 space-y-4">
        <div className="flex items-center justify-between border-b pb-3">
          <div className="flex items-center gap-2">
            <Clock className="w-5 h-5 text-yellow-600" />
            <h2 className="text-lg font-bold text-gray-900">Pending Ambulance Booking Requests</h2>
          </div>
          <span className="px-2.5 py-1 bg-yellow-100 text-yellow-800 rounded-full text-xs font-bold">
            {pendingRequests.length} Pending
          </span>
        </div>

        {pendingRequests.length === 0 ? (
          <div className="text-center py-8 text-gray-500 text-xs">
            No pending ambulance requests right now. Standing by for incoming bookings...
          </div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            {pendingRequests.map((req) => (
              <div key={req.id} className="p-4 border border-gray-200 rounded-xl hover:shadow-md transition space-y-3 bg-gray-50">
                <div className="flex justify-between items-start">
                  <span className="text-xs font-bold text-red-600">Booking #{req.id}</span>
                  <span className={`px-2 py-0.5 rounded text-[10px] font-bold ${
                    req.triagePriority === 'CRITICAL' ? 'bg-red-100 text-red-700' : 'bg-yellow-100 text-yellow-800'
                  }`}>
                    {req.triagePriority || 'HIGH'}
                  </span>
                </div>

                <div>
                  <div className="text-sm font-bold text-gray-900">{req.patientName}</div>
                  <div className="text-xs text-gray-600">Phone: {req.patientPhone}</div>
                </div>

                <div className="text-xs text-gray-700 bg-white p-2 rounded border border-gray-100">
                  <strong>Symptoms/Type:</strong> {req.symptoms}
                </div>

                <div className="text-xs text-gray-600">
                  <strong>Pickup:</strong> {req.pickupAddress || 'GPS Location'}
                </div>

                {req.hospitalName && (
                  <div className="text-xs text-blue-700 font-semibold">
                    <strong>Hospital:</strong> {req.hospitalName}
                  </div>
                )}

                <button
                  onClick={() => handleAcceptRequest(req.id)}
                  disabled={loading}
                  className="w-full py-2.5 bg-green-600 hover:bg-green-700 text-white font-bold text-xs rounded-lg shadow transition flex items-center justify-center gap-1"
                >
                  <CheckCircle className="w-4 h-4" />
                  <span>ACCEPT REQUEST</span>
                </button>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};
