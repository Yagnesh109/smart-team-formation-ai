from pathlib import Path
import json
import numpy as np
import pandas as pd
import joblib
from sklearn.model_selection import train_test_split, cross_val_score
from sklearn.preprocessing import StandardScaler
from sklearn.pipeline import Pipeline
from sklearn.metrics import accuracy_score, classification_report, confusion_matrix, mean_absolute_error, r2_score
from sklearn.linear_model import LogisticRegression
from sklearn.neighbors import KNeighborsClassifier
from sklearn.tree import DecisionTreeClassifier
from sklearn.svm import SVC
from sklearn.naive_bayes import GaussianNB
from sklearn.ensemble import RandomForestClassifier, RandomForestRegressor, VotingClassifier
from sklearn.cluster import KMeans

BASE = Path(__file__).resolve().parent
MODEL_DIR = BASE / "models"
MODEL_DIR.mkdir(exist_ok=True)

print("=" * 60)
print("1. TRAINING SUPERVISED CLASSIFIERS & VOTING ENSEMBLE")
print("=" * 60)

rng = np.random.default_rng(42)
n = 1800
X = pd.DataFrame({
    "skill_match": rng.uniform(0, 100, n),
    "interest_match": rng.uniform(0, 100, n),
    "availability": rng.uniform(0, 100, n),
    "experience": rng.uniform(0, 100, n),
    "communication": rng.uniform(0, 100, n),
    "reliability": rng.uniform(0, 100, n),
})
score = (0.42 * X.skill_match + 0.22 * X.interest_match + 0.14 * X.availability +
         0.10 * X.experience + 0.07 * X.communication + 0.05 * X.reliability + rng.normal(0, 10, n))
y = (score >= 62).astype(int)

X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.20, random_state=42, stratify=y)

models = {
    "logistic_regression": Pipeline([("scale", StandardScaler()), ("model", LogisticRegression(max_iter=1000))]),
    "knn": Pipeline([("scale", StandardScaler()), ("model", KNeighborsClassifier(n_neighbors=7))]),
    "decision_tree": DecisionTreeClassifier(max_depth=6, random_state=42),
    "svm": Pipeline([("scale", StandardScaler()), ("model", SVC(probability=True, kernel="rbf"))]),
    "naive_bayes": GaussianNB(),
    "random_forest": RandomForestClassifier(n_estimators=200, max_depth=8, random_state=42),
}

metrics = {}
for name, model in models.items():
    model.fit(X_train, y_train)
    pred = model.predict(X_test)
    cv = cross_val_score(model, X, y, cv=5, scoring="accuracy")
    metrics[name] = {
        "test_accuracy": round(float(accuracy_score(y_test, pred)), 4),
        "cv_mean": round(float(cv.mean()), 4),
        "confusion_matrix": confusion_matrix(y_test, pred).tolist(),
        "classification_report": classification_report(y_test, pred, output_dict=True),
    }

# Soft Voting Ensemble model (used by the Java application)
ensemble = VotingClassifier(
    estimators=[
        ("lr", models["logistic_regression"]),
        ("knn", models["knn"]),
        ("dt", models["decision_tree"]),
        ("svm", models["svm"]),
        ("nb", models["naive_bayes"]),
    ], voting="soft"
)
ensemble.fit(X_train, y_train)
pred = ensemble.predict(X_test)
metrics["voting_ensemble"] = {
    "test_accuracy": round(float(accuracy_score(y_test, pred)), 4),
    "confusion_matrix": confusion_matrix(y_test, pred).tolist(),
    "classification_report": classification_report(y_test, pred, output_dict=True),
}
joblib.dump(ensemble, MODEL_DIR / "team_match_ensemble.joblib")
print("Saved team_match_ensemble.joblib successfully")

print("\n" + "=" * 60)
print("2. TRAINING K-MEANS CLUSTERING (UNSUPERVISED)")
print("=" * 60)
cluster_features = X[["skill_match", "interest_match", "availability", "experience"]]
kmeans = KMeans(n_clusters=4, random_state=42, n_init=10)
kmeans.fit(cluster_features)
joblib.dump(kmeans, MODEL_DIR / "student_profile_kmeans.joblib")
print("Saved student_profile_kmeans.joblib successfully")

# Train Random Forest Regressor if training_data.csv exists
csv_path = BASE / "training_data.csv"
if csv_path.exists():
    print("\n" + "=" * 60)
    print("3. TRAINING RANDOM FOREST REGRESSOR (COMPATIBILITY MODEL)")
    print("=" * 60)
    data = pd.read_csv(csv_path)
    reg_features = [
        "skill_score", "experience_score", "availability_score",
        "matched_skills", "required_skills", "skill_gap",
        "department_match", "year_difference"
    ]
    if all(col in data.columns for col in reg_features + ["compatibility_score"]):
        X_reg = data[reg_features]
        y_reg = data["compatibility_score"]
        Xr_train, Xr_test, yr_train, yr_test = train_test_split(X_reg, y_reg, test_size=0.20, random_state=42)
        regressor = RandomForestRegressor(n_estimators=200, max_depth=10, random_state=42)
        regressor.fit(Xr_train, yr_train)
        pred_reg = regressor.predict(Xr_test)
        mae = mean_absolute_error(yr_test, pred_reg)
        r2 = r2_score(yr_test, pred_reg)
        metrics["random_forest_regressor"] = {
            "mae": round(float(mae), 4),
            "r2": round(float(r2), 4)
        }
        joblib.dump(regressor, MODEL_DIR / "compatibility_model.joblib")
        print(f"Saved compatibility_model.joblib (MAE: {round(mae, 2)}, R2: {round(r2, 2)})")

(MODEL_DIR / "metrics.json").write_text(json.dumps(metrics, indent=2))
print("\n" + "=" * 60)
print("TRAINING COMPLETE! All models and metrics saved to:", MODEL_DIR)
print(json.dumps({k: v.get("test_accuracy", v.get("r2")) for k, v in metrics.items()}, indent=2))
print("=" * 60)
