from fastapi import FastAPI
from pydantic import BaseModel
from typing import List
import joblib
import pandas as pd


# ============================================================
# FastAPI Application
# ============================================================

app = FastAPI(
    title="Smart Team Formation ML Service",
    description="AI-powered student compatibility prediction service",
    version="2.0.0"
)


# ============================================================
# Load trained ML model
# ============================================================

MODEL_PATH = "compatibility_model.joblib"

model = joblib.load(MODEL_PATH)


# ============================================================
# Request Data Model
# ============================================================

class StudentData(BaseModel):

    student_id: int
    name: str

    skill_score: float
    experience_score: float
    availability_score: float

    matched_skills: int
    required_skills: int
    skill_gap: int

    department_match: int
    year_difference: int


class MatchingRequest(BaseModel):

    students: List[StudentData]


# ============================================================
# Home Endpoint
# ============================================================

@app.get("/")
def home():

    return {
        "service": "Smart Team Formation ML Service",
        "status": "ONLINE",
        "version": "2.0.0",
        "model": "Random Forest Regressor"
    }


# ============================================================
# Health Check
# ============================================================

@app.get("/health")
def health():

    return {
        "status": "healthy",
        "model_loaded": model is not None
    }


# ============================================================
# ML Prediction Endpoint
# ============================================================

@app.post("/predict")
def predict(request: MatchingRequest):

    results = []

    for student in request.students:

        # --------------------------------------------
        # Prepare feature data
        # --------------------------------------------

        features = pd.DataFrame([{

            "skill_score":
                student.skill_score,

            "experience_score":
                student.experience_score,

            "availability_score":
                student.availability_score,

            "matched_skills":
                student.matched_skills,

            "required_skills":
                student.required_skills,

            "skill_gap":
                student.skill_gap,

            "department_match":
                student.department_match,

            "year_difference":
                student.year_difference

        }])


        # --------------------------------------------
        # Predict compatibility
        # --------------------------------------------

        prediction = model.predict(features)[0]


        # --------------------------------------------
        # Keep score between 0 and 100
        # --------------------------------------------

        prediction = max(
            0,
            min(100, prediction)
        )


        prediction = round(
            float(prediction),
            2
        )


        # --------------------------------------------
        # Store result
        # --------------------------------------------

        results.append({

            "student_id":
                student.student_id,

            "name":
                student.name,

            "predicted_score":
                prediction
        })


    # =================================================
    # Sort highest compatibility first
    # =================================================

    results.sort(
        key=lambda x: x["predicted_score"],
        reverse=True
    )


    return {

        "model":
            "Random Forest Compatibility Model",

        "recommendations":
            results
    }