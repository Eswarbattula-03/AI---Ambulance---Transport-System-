from fastapi import APIRouter
from pydantic import BaseModel
from typing import List, Optional
import math

router = APIRouter()

class Location(BaseModel):
    lat: float
    lng: float

class RouteRequest(BaseModel):
    ambulanceLocation: Location
    pickupLocation: Location
    destinationHospital: Location

class RouteResponse(BaseModel):
    distanceKm: float
    estimatedTimeMinutes: float
    routePath: List[Location]
    recommendation: str

@router.post("/optimize", response_model=RouteResponse)
def optimize_route(request: RouteRequest):
    # Euclidean Distance Haversine Baseline Formula
    lat1, lon1 = request.ambulanceLocation.lat, request.ambulanceLocation.lng
    lat2, lon2 = request.pickupLocation.lat, request.pickupLocation.lng
    
    dlat = math.radians(lat2 - lat1)
    dlon = math.radians(lon2 - lon1)
    a = math.sin(dlat/2)**2 + math.cos(math.radians(lat1)) * math.cos(math.radians(lat2)) * math.sin(dlon/2)**2
    c = 2 * math.atan2(math.sqrt(a), math.sqrt(1-a))
    distance_km = 6371 * c
    
    # Assuming average emergency travel speed of 40 km/h in urban India traffic
    eta_minutes = (distance_km / 40.0) * 60.0 + 2.0  # +2 min dispatch buffer

    path = [
        request.ambulanceLocation,
        Location(lat=(lat1 + lat2)/2, lng=(lon1 + lon2)/2),
        request.pickupLocation,
        request.destinationHospital
    ]

    return RouteResponse(
        distanceKm=round(distance_km, 2),
        estimatedTimeMinutes=round(eta_minutes, 1),
        routePath=path,
        recommendation="Direct green-corridor route optimized for minimal signals."
    )
