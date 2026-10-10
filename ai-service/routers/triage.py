from fastapi import APIRouter
from pydantic import BaseModel, Field
from typing import Optional

router = APIRouter()

class TriageRequest(BaseModel):
    symptoms: str = Field(..., example="Chest pain, difficulty breathing, dizziness")
    pickupLat: Optional[float] = Field(default=None, example=12.9716)
    pickupLng: Optional[float] = Field(default=None, example=77.5946)

class TriageResponse(BaseModel):
    priority: str = Field(..., example="CRITICAL")
    score: float = Field(..., example=95.0)
    explanation: str
    confidence: float = Field(..., example=0.92)
    modelVersion: str = Field(default="NLP_BASELINE_v1.0")

@router.post("/predict", response_model=TriageResponse)
def predict_triage(request: TriageRequest):
    symptoms_lower = request.symptoms.lower()
    
    # NLP / Keyword Rule Engine Baseline Model
    if any(k in symptoms_lower for k in ["chest pain", "heart attack", "unconscious", "stopped breathing", "cardiac", "stroke"]):
        return TriageResponse(
            priority="CRITICAL",
            score=95.0,
            explanation="Critical emergency identified. Symptoms suggest acute cardiac or respiratory life-threat.",
            confidence=0.95,
            modelVersion="NLP_BASELINE_v1.0"
        )
    elif any(k in symptoms_lower for k in ["bleeding", "fracture", "accident", "severe", "head injury", "burn"]):
        return TriageResponse(
            priority="HIGH",
            score=75.0,
            explanation="High priority emergency. Physical trauma or severe symptoms requiring immediate dispatch.",
            confidence=0.88,
            modelVersion="NLP_BASELINE_v1.0"
        )
    elif any(k in symptoms_lower for k in ["fever", "stomach pain", "vomiting", "dizziness"]):
        return TriageResponse(
            priority="MEDIUM",
            score=50.0,
            explanation="Moderate symptoms detected. Urgency required, non-immediate life threat.",
            confidence=0.82,
            modelVersion="NLP_BASELINE_v1.0"
        )
    else:
        return TriageResponse(
            priority="LOW",
            score=25.0,
            explanation="Mild symptoms reported. Assigned standard triage priority.",
            confidence=0.78,
            modelVersion="NLP_BASELINE_v1.0"
        )
