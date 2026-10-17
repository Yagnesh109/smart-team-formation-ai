import json
import re
from pathlib import Path
from typing import Optional, List, Dict, Any, Union
import joblib
import numpy as np
import pandas as pd
from fastapi import FastAPI, HTTPException, Body
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel, Field
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity

BASE = Path(__file__).resolve().parent
MODEL_DIR = BASE / "models"
ENSEMBLE_FILE = MODEL_DIR / "team_match_ensemble.joblib"
KMEANS_FILE = MODEL_DIR / "student_profile_kmeans.joblib"
REGRESSOR_FILE = MODEL_DIR / "compatibility_model.joblib"
ARCHETYPES_FILE = MODEL_DIR / "kmeans_archetypes.json"
METRICS_FILE = MODEL_DIR / "metrics.json"

app = FastAPI(
    title="SmartTeam AI - Unified ML Microservice",
    description="Production Machine Learning API for Student Teammate Recommendation & Compatibility Prediction, trained on Kaggle benchmark datasets.",
    version="3.5.0"
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Load pre-trained models and metadata
ensemble_model = joblib.load(ENSEMBLE_FILE) if ENSEMBLE_FILE.exists() else None
kmeans_model = joblib.load(KMEANS_FILE) if KMEANS_FILE.exists() else None
regressor_model = joblib.load(REGRESSOR_FILE) if REGRESSOR_FILE.exists() else None
archetypes_data = json.loads(ARCHETYPES_FILE.read_text()) if ARCHETYPES_FILE.exists() else {}
metrics_data = json.loads(METRICS_FILE.read_text()) if METRICS_FILE.exists() else {}


# =====================================================================
# PYDANTIC SCHEMAS
# =====================================================================

class MatchFeatures(BaseModel):
    skill_match: float = Field(ge=0, le=100)
    interest_match: float = Field(ge=0, le=100)
    availability: float = Field(ge=0, le=100)
    experience: float = Field(ge=0, le=100)
    communication: float = Field(default=70.0, ge=0, le=100)
    reliability: float = Field(default=70.0, ge=0, le=100)


class ClusterFeatures(BaseModel):
    skill_match: float = Field(ge=0, le=100)
    interest_match: float = Field(ge=0, le=100)
    availability: float = Field(ge=0, le=100)
    experience: float = Field(ge=0, le=100)


class TfidfMatchRequest(BaseModel):
    project_name: Optional[str] = ""
    required_skills: Optional[str] = ""
    project_availability: Optional[str] = ""
    candidate_skills: Optional[str] = ""
    candidate_interests: Optional[str] = ""
    candidate_availability: Optional[str] = ""
    candidate_experience: Optional[str] = "intermediate"


# =====================================================================
# NLP & SIMILARITY HELPER FUNCTIONS
# =====================================================================

def normalize_text(text: str) -> str:
    text = str(text or "").lower()
    text = re.sub(r"[^a-z0-9+#.*\- ]", " ", text)
    return re.sub(r"\s+", " ", text).strip()


def extract_tokens(text: str) -> set:
    text = normalize_text(text)
    return {x.strip() for x in re.split(r"[,;/|]+|\s{2,}", text) if x.strip()}


def compute_skill_overlap(required: str, candidate: str) -> float:
    req = extract_tokens(required)
    cand = extract_tokens(candidate)
    if not req:
        return 0.0
    matched = sum(1 for r in req if any(r == c or r in c or c in r for c in cand))
    return matched / len(req)


def compute_availability_overlap(p_avail: str, c_avail: str) -> float:
    p = extract_tokens(p_avail)
    c = extract_tokens(c_avail)
    if not p or not c:
        return 0.0
    return len(p.intersection(c)) / len(p)


def compute_experience_score(value: str) -> float:
    value = normalize_text(value)
    mapping = {"beginner": 0.33, "intermediate": 0.66, "advanced": 1.0}
    for k, v in mapping.items():
        if k in value:
            return v
    return 0.50


def compute_tfidf_similarity(project_text: str, candidate_text: str) -> float:
    p_text = normalize_text(project_text)
    c_text = normalize_text(candidate_text)
    if not p_text or not c_text:
        return 0.0
    try:
        vectorizer = TfidfVectorizer(ngram_range=(1, 2))
        matrix = vectorizer.fit_transform([p_text, c_text])
        return float(cosine_similarity(matrix[0:1], matrix[1:2])[0][0])
    except Exception:
        return 0.0


# =====================================================================
# REST API ENDPOINTS
# =====================================================================

@app.get("/")
def root():
    return {
        "service": "SmartTeam AI Unified ML Service",
        "status": "RUNNING",
        "dataset": "Kaggle Dynamic Student-Project Matching & Interdisciplinary Collaboration Benchmark",
        "models": {
            "voting_ensemble": ensemble_model is not None,
            "kmeans_clustering": kmeans_model is not None,
            "random_forest_regressor": regressor_model is not None
        }
    }


@app.get("/health")
def health():
    return {
        "status": "UP",
        "ensemble_loaded": ensemble_model is not None,
        "kmeans_loaded": kmeans_model is not None,
        "regressor_loaded": regressor_model is not None
    }


@app.get("/metrics")
def get_metrics():
    if not metrics_data:
        raise HTTPException(status_code=404, detail="Metrics file not found. Run train_models.py.")
    return metrics_data


# Primary endpoint called by Java backend (MatchingService.java)
@app.post("/predict")
def predict(features: MatchFeatures):
    if ensemble_model is None:
        raise HTTPException(
            status_code=503,
            detail="Voting Ensemble Model not loaded. Run 'python train_models.py' first."
        )

    df = pd.DataFrame([{
        "skill_match": features.skill_match,
        "interest_match": features.interest_match,
        "availability": features.availability,
        "experience": features.experience,
        "communication": features.communication,
        "reliability": features.reliability
    }])

    # 1. Ensemble Classification Probability
    probability = float(ensemble_model.predict_proba(df)[0][1])

    # 2. Continuous Compatibility Score from Regressor
    if regressor_model is not None:
        compat_score = float(regressor_model.predict(df)[0])
        compat_score = round(max(5.0, min(100.0, compat_score)), 1)
    else:
        compat_score = round(probability * 100.0, 1)

    # 3. K-Means Archetype Profiling
    cluster_id = None
    archetype_name = "General Collaborator"
    archetype_desc = ""
    if kmeans_model is not None:
        df_cluster = df[["skill_match", "interest_match", "availability", "experience"]]
        cluster_id = int(kmeans_model.predict(df_cluster)[0])
        if str(cluster_id) in archetypes_data:
            archetype_name = archetypes_data[str(cluster_id)].get("name", archetype_name)
            archetype_desc = archetypes_data[str(cluster_id)].get("description", "")

    # Multi-tier compatibility classification
    if probability >= 0.75:
        label = "HIGHLY_COMPATIBLE"
    elif probability >= 0.50:
        label = "COMPATIBLE"
    else:
        label = "LOW_COMPATIBILITY"

    return {
        "label": label,
        "probability": round(probability, 4),
        "percentage": round(probability * 100.0, 2),
        "compatibility_score": compat_score,
        "cluster": cluster_id,
        "archetype": archetype_name,
        "archetype_description": archetype_desc,
        "features": {
            "skill_match": features.skill_match,
            "interest_match": features.interest_match,
            "availability": features.availability,
            "experience": features.experience,
            "communication": features.communication,
            "reliability": features.reliability
        }
    }


# K-Means clustering endpoint for profile segmentation
@app.post("/cluster")
def cluster(features: ClusterFeatures):
    if kmeans_model is None:
        raise HTTPException(
            status_code=503,
            detail="K-Means Cluster Model not loaded. Run 'python train_models.py' first."
        )

    df = pd.DataFrame([{
        "skill_match": features.skill_match,
        "interest_match": features.interest_match,
        "availability": features.availability,
        "experience": features.experience
    }])

    cluster_id = int(kmeans_model.predict(df)[0])
    archetype_info = archetypes_data.get(str(cluster_id), {})
    archetype_name = archetype_info.get("name", "General Student")
    archetype_desc = archetype_info.get("description", "Student profile segment generated by K-Means")

    return {
        "cluster": cluster_id,
        "archetype": archetype_name,
        "description": archetype_desc,
        "centroid": archetype_info.get("centroid", {})
    }


# TF-IDF Cosine Similarity endpoint
@app.post("/predict-match")
def predict_match(data: TfidfMatchRequest):
    project_text = f"{data.project_name} {data.required_skills}"
    candidate_text = f"{data.candidate_skills} {data.candidate_interests}"

    skill = compute_skill_overlap(data.required_skills, data.candidate_skills)
    text_sim = compute_tfidf_similarity(project_text, candidate_text)
    avail = compute_availability_overlap(data.project_availability, data.candidate_availability)
    exp = compute_experience_score(data.candidate_experience)

    final_score = (
        0.50 * skill
        + 0.25 * text_sim
        + 0.15 * avail
        + 0.10 * exp
    )

    return {
        "match_score": round(final_score * 100, 2),
        "components": {
            "skill_score": round(skill * 100, 2),
            "tfidf_similarity": round(text_sim * 100, 2),
            "availability_score": round(avail * 100, 2),
            "experience_score": round(exp * 100, 2)
        }
    }


if __name__ == "__main__":
    import uvicorn
    uvicorn.run("main:app", host="127.0.0.1", port=8000, reload=True)
