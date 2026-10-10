import React, { useEffect } from 'react';
import { MapContainer, TileLayer, Marker, Popup, useMap } from 'react-leaflet';
import L from 'leaflet';
import 'leaflet/dist/leaflet.css';

// Fix leafet icon paths
delete (L.Icon.Default.prototype as any)._getIconUrl;
L.Icon.Default.mergeOptions({
  iconUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon.png',
  iconRetinaUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon-2x.png',
  shadowUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png',
});

// Custom icons
const patientIcon = new L.Icon({
  iconUrl: 'https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-2x-red.png',
  shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/0.7.7/images/marker-shadow.png',
  iconSize: [25, 41],
  iconAnchor: [12, 41],
  popupAnchor: [1, -34],
  shadowSize: [41, 41]
});

const ambulanceIcon = new L.Icon({
  iconUrl: 'https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-2x-gold.png',
  shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/0.7.7/images/marker-shadow.png',
  iconSize: [25, 41],
  iconAnchor: [12, 41],
  popupAnchor: [1, -34],
  shadowSize: [41, 41]
});

const hospitalIcon = new L.Icon({
  iconUrl: 'https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-2x-blue.png',
  shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/0.7.7/images/marker-shadow.png',
  iconSize: [25, 41],
  iconAnchor: [12, 41],
  popupAnchor: [1, -34],
  shadowSize: [41, 41]
});

interface LiveMapProps {
  pickupLat: number;
  pickupLng: number;
  ambulanceLat?: number;
  ambulanceLng?: number;
  hospitalLat?: number;
  hospitalLng?: number;
  hospitalName?: string;
  vehicleNumber?: string;
}

const MapRecenter: React.FC<{ lat: number; lng: number }> = ({ lat, lng }) => {
  const map = useMap();
  useEffect(() => {
    map.setView([lat, lng], 13);
  }, [lat, lng, map]);
  return null;
};

export const LiveMap: React.FC<LiveMapProps> = ({
  pickupLat,
  pickupLng,
  ambulanceLat,
  ambulanceLng,
  hospitalLat,
  hospitalLng,
  hospitalName,
  vehicleNumber,
}) => {
  const centerLat = ambulanceLat || pickupLat;
  const centerLng = ambulanceLng || pickupLng;

  return (
    <div className="h-[400px] w-full rounded-xl overflow-hidden shadow-md border border-gray-200">
      <MapContainer center={[centerLat, centerLng]} zoom={13} style={{ height: '100%', width: '100%' }}>
        <TileLayer
          attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
          url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
        />

        <MapRecenter lat={centerLat} lng={centerLng} />

        {/* Patient Marker */}
        <Marker position={[pickupLat, pickupLng]} icon={patientIcon}>
          <Popup>
            <div className="font-semibold text-red-600">Patient SOS Location</div>
          </Popup>
        </Marker>

        {/* Ambulance Marker */}
        {ambulanceLat && ambulanceLng && (
          <Marker position={[ambulanceLat, ambulanceLng]} icon={ambulanceIcon}>
            <Popup>
              <div className="font-semibold text-yellow-700">Ambulance #{vehicleNumber || 'KA-01-9999'}</div>
              <div className="text-xs text-gray-500">Live Telemetry Coordinates</div>
            </Popup>
          </Marker>
        )}

        {/* Hospital Marker */}
        {hospitalLat && hospitalLng && (
          <Marker position={[hospitalLat, hospitalLng]} icon={hospitalIcon}>
            <Popup>
              <div className="font-semibold text-blue-700">{hospitalName || 'Destination Hospital'}</div>
            </Popup>
          </Marker>
        )}
      </MapContainer>
    </div>
  );
};
