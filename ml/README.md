# SmartTeam AI — Unified Machine Learning Microservice

This folder contains the **unified Machine Learning microservice** for the Smart Team Formation system. It consolidates all ML models, trained on real Kaggle benchmark datasets, into a single service running on **Port 8000**.

---

## 📊 Dataset Benchmark

The ML models are trained on real educational team formation & student collaboration datasets from Kaggle:
1. **Kaggle Dynamic Student-Project Matching Dataset** (`ziya07/dynamic-student-project-matching-dataset`): Real student skill profiles, project requirements, weekly commitments, past project performance, and completion records.
2. **Kaggle Interdisciplinary Team Collaboration Dataset** (`colabsss/interdisciplinary-entrepreneurship-student-dataset`): Real student peer evaluation records, communication skill metrics, teamwork ratings, and collaboration outcomes.
3. **Kaggle Hackathon Team Dynamics Dataset** (`prince7489/hackathon-team-dynamics-dataset`): Multi-member project dynamics, submission quality scores, and skill diversity indices.
4. **SFSU SETAP Benchmark Distributions**: Calibrated software engineering teamwork assessment weights.

---

## 🚀 Quick Start (Running the ML Service)

Open PowerShell in this directory:

```powershell
# 1. Navigate to the ml folder
cd ml

# 2. Activate virtual environment
.\.venv\Scripts\activate

# 3. (Optional) Re-prepare dataset & re-train all models
python prepare_dataset.py
python train_models.py

# 4. Start the unified FastAPI microservice
uvicorn main:app --reload --port 8000
```

* **Microservice Root:** `http://127.0.0.1:8000/`
* **Health Check:** `http://127.0.0.1:8000/health`
* **Model Performance Metrics:** `http://127.0.0.1:8000/metrics`
* **Interactive API Docs (Swagger UI):** `http://127.0.0.1:8000/docs`

---

## 📡 API Endpoints

### 1. `POST /predict` (Primary Endpoint used by Java Backend)
Calculates teammate compatibility probability using the **Weighted Soft Voting Ensemble Classifier**, continuous compatibility score using the **Random Forest Regressor**, and student profile archetype using **K-Means Clustering**.

* **Input JSON:**
  ```json
  {
    "skill_match": 85.0,
    "interest_match": 90.0,
    "availability": 90.0,
    "experience": 80.0,
    "communication": 85.0,
    "reliability": 90.0
  }
  ```
* **Output JSON:**
  ```json
  {
    "label": "HIGHLY_COMPATIBLE",
    "probability": 0.998,
    "percentage": 99.8,
    "compatibility_score": 81.4,
    "cluster": 2,
    "archetype": "Balanced Generalist",
    "archetype_description": "Versatile skill set with consistent cross-domain capability and steady collaboration.",
    "features": {
      "skill_match": 85.0,
      "interest_match": 90.0,
      "availability": 90.0,
      "experience": 80.0,
      "communication": 85.0,
      "reliability": 90.0
    }
  }
  ```

---

### 2. `POST /cluster` (K-Means Student Archetype Segmentation)
Categorizes candidate profiles into $k=4$ student archetypes.

* **Archetypes:**
  - `High-Skill Specialist`: Top-tier technical capability with deep specialization in project domains.
  - `Balanced Generalist`: Versatile skill set with consistent cross-domain capability and steady collaboration.
  - `Dedicated Collaborator`: High availability and strong reliability, ideal for intensive project sprints.
  - `Developing Enthusiast`: Emerging talent with high motivation and growing foundational skills.

* **Input JSON:**
  ```json
  {
    "skill_match": 85.0,
    "interest_match": 90.0,
    "availability": 90.0,
    "experience": 80.0
  }
  ```

---

### 3. `GET /metrics` (ML Model Metrics & Verification)
Returns accuracy, ROC-AUC, cross-validation scores, confusion matrices, and feature importances for all trained models.

* **Voting Ensemble Accuracy:** `91.85%` (ROC-AUC: `0.9797`)
* **Random Forest Regressor R²:** `0.9366` (MAE: `2.95`)
* **Logistic Regression:** Accuracy `92.25%` (ROC-AUC: `0.9827`)
* **Random Forest Classifier:** Accuracy `92.15%` (ROC-AUC: `0.9787`)
* **HistGradientBoosting:** Accuracy `91.45%` (ROC-AUC: `0.9771`)

---

## 📂 Folder Structure

```
ml/
├── .venv/                         # Python 3.12 Virtual Environment
├── data/                          # Downloaded Kaggle benchmark datasets
│   ├── kaggle_ds/                 # Dynamic Student-Project Matching Dataset
│   ├── interdisciplinary_ds/      # Interdisciplinary Collaboration Dataset
│   └── hackathon_ds/              # Hackathon Team Dynamics Dataset
├── models/
│   ├── compatibility_model.joblib # Random Forest Regressor (R² = 0.94)
│   ├── kmeans_archetypes.json     # Dynamic cluster archetype mappings & centroids
│   ├── metrics.json               # Cross-validation & test metrics
│   ├── student_profile_kmeans.joblib # K-Means Clustering model (k = 4)
│   └── team_match_ensemble.joblib # Soft Voting Ensemble Classifier (RF, GB, SVM, LR, KNN)
├── main.py                        # Unified FastAPI Application (Port 8000)
├── prepare_dataset.py             # Preprocessing & Kaggle dataset integration
├── training_data.csv              # Cleaned 10,000-instance training dataset
├── train_models.py                # Model training & cross-validation pipeline
├── requirements.txt               # Dependencies
└── README.md                      # Documentation & Run Guide
```
