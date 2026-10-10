import React, { useState, useEffect } from 'react';
import api from '../api/axios';
import { EmergencyResponse, Hospital, EmergencyStatus } from '../types';
import { LiveMap } from '../components/LiveMap';
import { AlertTriangle, MapPin, Mic, Phone, Clock, CheckCircle2, Ambulance, Building2, User, FileText, Navigation, ShieldCheck } from 'lucide-react';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

export const PatientDashboard: React.FC = () => {
  // Booking Form State
  const [pickupAddress, setPickupAddress] = useState('MG Road, Bengaluru');
  const [lat, setLat] = useState<number | null>(12.9715987);
  const [lng, setLng] = useState<number | null>(77.5945627);
  const [hospitalId, setHospitalId] = useState<number | ''>('');
  const [emergencyType, setEmergencyType] = useState('General Emergency');
  const [patientName, setPatientName] = useState('');
  const [patientPhone, setPatientPhone] = useState('');
  const [patientNotes, setPatientNotes] = useState('');
  const [symptoms, setSymptoms] = useState('');

  // App Data & Live Tracking
  const [hospitals, setHospitals] = useState<Hospital[]>([]);
  const [activeEmergency, setActiveEmergency] = useState<EmergencyResponse | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [listening, setListening] = useState(false);

  useEffect(() => {
    // Acquire GPS location
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(
        (position) => {
          setLat(position.coords.latitude);
          setLng(position.coords.longitude);
        },
        () => {
          setLat(12.9715987);
          setLng(77.5945627);
        }
      );
    }

    fetchHospitals();
    fetchActiveEmergency();

    // Polling fallback to keep patient tracking in sync with driver status updates
    const pollInterval = setInterval(fetchActiveEmergency, 3000);
    return () => clearInterval(pollInterval);
  }, []);

  useEffect(() => {
    if (!activeEmergency) return;

    // Connect STOMP WebSocket for real-time updates
    const client = new Client({
      webSocketFactory: () => new SockJS('http://localhost:8080/ws'),
      onConnect: () => {
        client.subscribe(`/topic/emergencies/${activeEmergency.id}`, (message) => {
          const updated: EmergencyResponse = JSON.parse(message.body);
          setActiveEmergency(updated);
        });

        if (activeEmergency.ambulanceId) {
          client.subscribe(`/topic/ambulances/${activeEmergency.ambulanceId}/location`, (message) => {
            const locData = JSON.parse(message.body);
            setActiveEmergency((prev) => (prev ? {
              ...prev,
              ambulanceLat: locData.latitude,
              ambulanceLng: locData.longitude
            } : null));
          });
        }
      },
    });

    client.activate();

    return () => {
      client.deactivate();
    };
  }, [activeEmergency?.id, activeEmergency?.ambulanceId]);

  const fetchHospitals = async () => {
    try {
      const res = await api.get('/hospitals');
      if (res.data.data) {
        setHospitals(res.data.data);
      }
    } catch (err) {
      console.error('Failed to load hospital list', err);
    }
  };

  const fetchActiveEmergency = async () => {
    try {
      const res = await api.get('/emergencies/active');
      if (res.data.data) {
        setActiveEmergency(res.data.data);
      } else {
        setActiveEmergency(null);
      }
    } catch (err) {
      console.error(err);
    }
  };

  const handleBooking = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!symptoms.trim()) {
      setError('Please describe your symptoms or medical situation.');
      return;
    }
    setError('');
    setLoading(true);

    try {
      const payload = {
        symptoms,
        pickupLat: lat || 12.9715987,
        pickupLng: lng || 77.5945627,
        pickupAddress,
        hospitalId: hospitalId !== '' ? Number(hospitalId) : undefined,
        emergencyType,
        patientName,
        patientPhone,
        patientNotes
      };

      const res = await api.post('/emergencies', payload);
      setActiveEmergency(res.data.data);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to book ambulance. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const handleVoiceInput = () => {
    const SpeechRecognition = (window as any).SpeechRecognition || (window as any).webkitSpeechRecognition;
    if (!SpeechRecognition) {
      alert('Speech recognition is not supported in this browser.');
      return;
    }

    const recognition = new SpeechRecognition();
    recognition.continuous = false;
    recognition.interimResults = false;
    recognition.lang = 'en-IN';

    recognition.onstart = () => setListening(true);
    recognition.onend = () => setListening(false);
    recognition.onresult = (event: any) => {
      const transcript = event.results[0][0].transcript;
      setSymptoms((prev) => (prev ? `${prev} ${transcript}` : transcript));
    };

    recognition.start();
  };

  const handleCancel = async () => {
    if (!activeEmergency) return;
    if (window.confirm('Are you sure you want to cancel this ambulance booking?')) {
      try {
        await api.post(`/emergencies/${activeEmergency.id}/cancel`);
        setActiveEmergency(null);
      } catch (err) {
        alert('Could not cancel emergency request.');
      }
    }
  };

  // Status step index calculation
  const getStepIndex = (status: EmergencyStatus) => {
    switch (status) {
      case 'REQUESTED':
      case 'TRIAGED':
      case 'DISPATCHED':
        return 0;
      case 'ACCEPTED':
      case 'EN_ROUTE_TO_PICKUP':
        return 1;
      case 'ARRIVED':
      case 'PATIENT_ONBOARD':
        return 2;
      case 'IN_TRANSIT':
      case 'EN_ROUTE_TO_HOSPITAL':
        return 3;
      case 'COMPLETED':
      case 'DELIVERED':
        return 4;
      default:
        return 0;
    }
  };

  const currentStep = activeEmergency ? getStepIndex(activeEmergency.status) : 0;
  const steps = [
    { label: 'REQUESTED', sub: 'Searching Driver' },
    { label: 'ACCEPTED', sub: 'Driver Assigned' },
    { label: 'ARRIVED', sub: 'Driver at Location' },
    { label: 'IN TRANSIT', sub: 'En Route to ER' },
    { label: 'COMPLETED', sub: 'Safely Delivered' }
  ];

  return (
    <div className="max-w-7xl mx-auto px-4 py-6 space-y-6">
      {/* Notice Banner */}
      <div className="bg-yellow-50 border-l-4 border-yellow-500 p-4 rounded-r-lg text-xs text-yellow-800 flex items-center justify-between">
        <div className="flex items-center gap-2">
          <AlertTriangle className="w-5 h-5 text-yellow-600 flex-shrink-0" />
          <span>
            <strong>Safety Notice:</strong> LifeRescue AI assists triage and dispatch. In severe life-threatening emergencies, dial national emergency services (112) immediately.
          </span>
        </div>
      </div>

      {!activeEmergency ? (
        <div className="grid grid-cols-1 lg:grid-cols-12 gap-8">
          {/* Booking Form Panel */}
          <div className="lg:col-span-7 bg-white p-6 sm:p-8 rounded-2xl shadow-xl border border-gray-100 space-y-6">
            <div>
              <div className="flex items-center gap-2 text-red-600 font-bold text-sm">
                <Ambulance className="w-5 h-5" />
                <span>EMERGENCY AMBULANCE BOOKING</span>
              </div>
              <h2 className="text-2xl font-extrabold text-gray-900 mt-1">Book Emergency Transportation</h2>
              <p className="text-xs text-gray-500">Provide pickup location, destination hospital, and patient symptoms for instant dispatch.</p>
            </div>

            {error && (
              <div className="p-3 bg-red-50 text-red-700 text-xs rounded-lg border border-red-200">
                {error}
              </div>
            )}

            <form onSubmit={handleBooking} className="space-y-4">
              {/* Emergency Type & Hospital */}
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div>
                  <label className="block text-xs font-semibold text-gray-700 uppercase mb-1">Emergency Type</label>
                  <select
                    value={emergencyType}
                    onChange={(e) => setEmergencyType(e.target.value)}
                    className="w-full p-2.5 border border-gray-300 rounded-lg text-sm bg-white focus:ring-2 focus:ring-red-500 outline-none"
                  >
                    <option value="General Emergency">General Emergency</option>
                    <option value="Cardiac Emergency">Cardiac / Chest Pain</option>
                    <option value="Accident & Trauma">Road Accident & Trauma</option>
                    <option value="Respiratory Distress">Respiratory / Breathing Difficulty</option>
                    <option value="Pregnancy & Labor">Pregnancy / Labor Emergency</option>
                    <option value="Severe Infection / Stroke">Stroke / High Fever</option>
                  </select>
                </div>

                <div>
                  <label className="block text-xs font-semibold text-gray-700 uppercase mb-1">Destination Hospital (Optional)</label>
                  <select
                    value={hospitalId}
                    onChange={(e) => setHospitalId(e.target.value ? Number(e.target.value) : '')}
                    className="w-full p-2.5 border border-gray-300 rounded-lg text-sm bg-white focus:ring-2 focus:ring-red-500 outline-none"
                  >
                    <option value="">Auto-Assign Nearest Hospital</option>
                    {hospitals.map((h) => (
                      <option key={h.id} value={h.id}>
                        {h.name} ({h.availableBeds} beds free)
                      </option>
                    ))}
                  </select>
                </div>
              </div>

              {/* Pickup Location */}
              <div>
                <div className="flex justify-between items-center mb-1">
                  <label className="text-xs font-semibold text-gray-700 uppercase">Pickup Location / Address</label>
                  <button
                    type="button"
                    onClick={() => {
                      if (navigator.geolocation) {
                        navigator.geolocation.getCurrentPosition((pos) => {
                          setLat(pos.coords.latitude);
                          setLng(pos.coords.longitude);
                          setPickupAddress(`GPS: ${pos.coords.latitude.toFixed(4)}, ${pos.coords.longitude.toFixed(4)}`);
                        });
                      }
                    }}
                    className="text-xs flex items-center gap-1 text-blue-600 font-semibold hover:underline"
                  >
                    <MapPin className="w-3.5 h-3.5" />
                    <span>Use Current GPS Location</span>
                  </button>
                </div>
                <input
                  type="text"
                  required
                  value={pickupAddress}
                  onChange={(e) => setPickupAddress(e.target.value)}
                  placeholder="Street address, landmark, area..."
                  className="w-full p-2.5 border border-gray-300 rounded-lg text-sm focus:ring-2 focus:ring-red-500 outline-none"
                />
              </div>

              {/* Patient Details */}
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div>
                  <label className="block text-xs font-semibold text-gray-700 uppercase mb-1">Patient Name</label>
                  <input
                    type="text"
                    value={patientName}
                    onChange={(e) => setPatientName(e.target.value)}
                    placeholder="Patient full name"
                    className="w-full p-2.5 border border-gray-300 rounded-lg text-sm focus:ring-2 focus:ring-red-500 outline-none"
                  />
                </div>

                <div>
                  <label className="block text-xs font-semibold text-gray-700 uppercase mb-1">Contact Phone Number</label>
                  <input
                    type="text"
                    value={patientPhone}
                    onChange={(e) => setPatientPhone(e.target.value)}
                    placeholder="+919876543210"
                    className="w-full p-2.5 border border-gray-300 rounded-lg text-sm focus:ring-2 focus:ring-red-500 outline-none"
                  />
                </div>
              </div>

              {/* Symptoms / Voice Input */}
              <div>
                <div className="flex justify-between items-center mb-1">
                  <label className="text-xs font-semibold text-gray-700 uppercase">Describe Symptoms / Condition</label>
                  <button
                    type="button"
                    onClick={handleVoiceInput}
                    className={`text-xs flex items-center gap-1 px-2.5 py-1 rounded-full border transition ${
                      listening ? 'bg-red-600 text-white animate-pulse' : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                    }`}
                  >
                    <Mic className="w-3.5 h-3.5" />
                    <span>{listening ? 'Listening...' : 'Voice Input'}</span>
                  </button>
                </div>
                <textarea
                  rows={3}
                  required
                  value={symptoms}
                  onChange={(e) => setSymptoms(e.target.value)}
                  placeholder="Describe patient condition, symptoms, or emergency details..."
                  className="w-full p-3 border border-gray-300 rounded-lg text-sm focus:ring-2 focus:ring-red-500 outline-none"
                />
              </div>

              {/* Additional Notes */}
              <div>
                <label className="block text-xs font-semibold text-gray-700 uppercase mb-1">Additional Notes / Instructions</label>
                <input
                  type="text"
                  value={patientNotes}
                  onChange={(e) => setPatientNotes(e.target.value)}
                  placeholder="Gate number, floor, landmark, or oxygen requirements..."
                  className="w-full p-2.5 border border-gray-300 rounded-lg text-sm focus:ring-2 focus:ring-red-500 outline-none"
                />
              </div>

              <button
                type="submit"
                disabled={loading}
                className="w-full py-4 bg-red-600 hover:bg-red-700 text-white font-extrabold text-base rounded-xl shadow-lg transition flex items-center justify-center gap-2"
              >
                <AlertTriangle className="w-5 h-5" />
                <span>{loading ? 'BOOKING AMBULANCE...' : 'CONFIRM & BOOK AMBULANCE NOW'}</span>
              </button>
            </form>
          </div>

          {/* Map Preview Panel */}
          <div className="lg:col-span-5 space-y-6">
            <div className="bg-white p-6 rounded-2xl shadow-xl border border-gray-100">
              <h3 className="text-base font-bold text-gray-900 mb-3 flex items-center gap-2">
                <Navigation className="w-5 h-5 text-red-600" />
                Pickup Location Preview
              </h3>
              <LiveMap pickupLat={lat || 12.9715987} pickupLng={lng || 77.5945627} />
            </div>

            <div className="bg-gradient-to-br from-red-600 to-red-800 text-white p-6 rounded-2xl shadow-xl space-y-3">
              <div className="flex items-center gap-2 text-yellow-300 text-xs font-bold uppercase tracking-wider">
                <ShieldCheck className="w-4 h-4" />
                24/7 AI-Powered Triage
              </div>
              <h4 className="text-lg font-extrabold">Instant Ambulance Dispatch</h4>
              <p className="text-xs text-red-100 leading-relaxed">
                Your request is saved permanently in MySQL and routed directly to available nearby drivers. Real-time telemetry streams driver updates straight to your dashboard.
              </p>
            </div>
          </div>
        </div>
      ) : (
        /* Active Booking Tracking Screen */
        <div className="space-y-6">
          {/* Status Bar Header */}
          <div className="bg-white p-6 rounded-2xl shadow-xl border border-gray-100 space-y-6">
            <div className="flex flex-wrap items-center justify-between gap-4 border-b pb-4">
              <div>
                <span className="text-xs font-bold uppercase text-red-600 tracking-wider">Active Ambulance Booking #{activeEmergency.id}</span>
                <h2 className="text-2xl font-extrabold text-gray-900">Live Booking Tracking</h2>
              </div>

              <div className="flex items-center gap-3">
                <span className={`px-4 py-1.5 rounded-full text-xs font-extrabold ${
                  activeEmergency.status === 'COMPLETED' || activeEmergency.status === 'DELIVERED'
                    ? 'bg-green-100 text-green-800'
                    : 'bg-yellow-100 text-yellow-800 animate-pulse'
                }`}>
                  STATUS: {activeEmergency.status}
                </span>

                {activeEmergency.status !== 'COMPLETED' && activeEmergency.status !== 'DELIVERED' && (
                  <button
                    onClick={handleCancel}
                    className="px-3 py-1.5 bg-gray-100 hover:bg-gray-200 text-gray-700 text-xs font-bold rounded-lg transition"
                  >
                    Cancel Booking
                  </button>
                )}
              </div>
            </div>

            {/* STATUS STEP FLOW: REQUESTED -> ACCEPTED -> ARRIVED -> IN_TRANSIT -> COMPLETED */}
            <div className="py-2">
              <div className="grid grid-cols-5 gap-2 text-center">
                {steps.map((step, idx) => {
                  const isPassed = idx <= currentStep;
                  const isCurrent = idx === currentStep;

                  return (
                    <div key={step.label} className="flex flex-col items-center">
                      <div className={`w-10 h-10 rounded-full flex items-center justify-center font-bold text-xs transition ${
                        isPassed ? 'bg-red-600 text-white shadow-lg' : 'bg-gray-100 text-gray-400 border border-gray-200'
                      } ${isCurrent ? 'ring-4 ring-red-200 scale-110' : ''}`}>
                        {isPassed ? <CheckCircle2 className="w-5 h-5" /> : idx + 1}
                      </div>
                      <span className={`text-xs font-extrabold mt-2 ${isPassed ? 'text-gray-900' : 'text-gray-400'}`}>
                        {step.label}
                      </span>
                      <span className="text-[10px] text-gray-500 hidden sm:block">{step.sub}</span>
                    </div>
                  );
                })}
              </div>

              <div className="w-full bg-gray-200 h-2 rounded-full mt-4 overflow-hidden">
                <div
                  className="bg-red-600 h-2 rounded-full transition-all duration-500"
                  style={{ width: `${((currentStep + 1) / steps.length) * 100}%` }}
                />
              </div>
            </div>
          </div>

          {/* Details & Map Grid */}
          <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
            <div className="lg:col-span-2 bg-white p-6 rounded-2xl shadow-xl border border-gray-100">
              <h3 className="text-base font-bold text-gray-900 mb-3 flex items-center gap-2">
                <Navigation className="w-5 h-5 text-red-600" />
                Live Map & Route Telemetry
              </h3>
              <LiveMap
                pickupLat={activeEmergency.pickupLat}
                pickupLng={activeEmergency.pickupLng}
                ambulanceLat={activeEmergency.ambulanceLat}
                ambulanceLng={activeEmergency.ambulanceLng}
                hospitalLat={activeEmergency.destinationLat}
                hospitalLng={activeEmergency.destinationLng}
                hospitalName={activeEmergency.hospitalName}
                vehicleNumber={activeEmergency.vehicleNumber}
              />
            </div>

            <div className="space-y-6">
              <div className="bg-white p-6 rounded-2xl shadow-xl border border-gray-100 space-y-4">
                <h3 className="font-bold text-gray-900 border-b pb-2">Booking Summary</h3>

                <div>
                  <span className="text-xs text-gray-400 font-semibold uppercase">Assigned Ambulance</span>
                  <div className="text-base font-extrabold text-gray-900 mt-0.5">
                    {activeEmergency.vehicleNumber || 'Assigning nearest ambulance...'}
                  </div>
                  {activeEmergency.driverName && (
                    <div className="text-xs text-gray-600 font-medium mt-1">
                      Driver: {activeEmergency.driverName}
                    </div>
                  )}
                  {activeEmergency.driverPhone && (
                    <a href={`tel:${activeEmergency.driverPhone}`} className="inline-flex items-center gap-1 text-xs text-blue-600 font-bold mt-1 hover:underline">
                      <Phone className="w-3.5 h-3.5" /> Call Driver ({activeEmergency.driverPhone})
                    </a>
                  )}
                </div>

                <div className="border-t pt-3">
                  <span className="text-xs text-gray-400 font-semibold uppercase">Destination Hospital</span>
                  <div className="text-sm font-bold text-gray-900 mt-0.5">
                    {activeEmergency.hospitalName || 'Auto-assigning hospital...'}
                  </div>
                  {activeEmergency.hospitalAddress && (
                    <div className="text-xs text-gray-500 mt-0.5">{activeEmergency.hospitalAddress}</div>
                  )}
                </div>

                <div className="border-t pt-3">
                  <span className="text-xs text-gray-400 font-semibold uppercase">Pickup Address</span>
                  <div className="text-xs font-medium text-gray-800 mt-0.5">{activeEmergency.pickupAddress}</div>
                </div>

                <div className="border-t pt-3">
                  <span className="text-xs text-gray-400 font-semibold uppercase">Symptoms / Case Details</span>
                  <div className="text-xs text-gray-700 bg-gray-50 p-2.5 rounded-lg mt-1">{activeEmergency.symptoms}</div>
                </div>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
