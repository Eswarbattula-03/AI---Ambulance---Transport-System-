from fastapi import APIRouter

router = APIRouter()

@router.get("/health")
def health_check():
    return {
        "status": "UP",
        "service": "LifeRescue AI Microservice",
        "version": "1.0.0"
    }
