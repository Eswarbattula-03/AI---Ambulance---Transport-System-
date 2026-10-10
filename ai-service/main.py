from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from routers import health, triage, route

app = FastAPI(
    title="LifeRescue AI Microservice",
    description="Emergency Triage Classification, Route Optimization, and ETA Prediction Service",
    version="1.0.0"
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

app.include_router(health.router)
app.include_router(triage.router, prefix="/api/triage", tags=["triage"])
app.include_router(route.router, prefix="/api/routes", tags=["routing"])

if __name__ == "__main__":
    import uvicorn
    uvicorn.run("main:app", host="0.0.0.0", port=8000, reload=True)
