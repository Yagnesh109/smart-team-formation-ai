from pathlib import Path
import json
import numpy as np
import pandas as pd
import joblib
from sklearn.model_selection import train_test_split, cross_val_score
from sklearn.preprocessing import StandardScaler
from sklearn.pipeline import Pipeline
from sklearn.metrics import accuracy_score, classification_report, confusion_matrix
from sklearn.linear_model import LogisticRegression
from sklearn.neighbors import KNeighborsClassifier
from sklearn.tree import DecisionTreeClassifier
from sklearn.svm import SVC
from sklearn.naive_bayes import GaussianNB
from sklearn.ensemble import RandomForestClassifier, VotingClassifier
from sklearn.cluster import KMeans

BASE = Path(__file__).resolve().parent
MODEL_DIR = BASE / "models"
MODEL_DIR.mkdir(exist_ok=True)

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
score = (0.42*X.skill_match + 0.22*X.interest_match + 0.14*X.availability +
         0.10*X.experience + 0.07*X.communication + 0.05*X.reliability + rng.normal(0, 10, n))
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

# Ensemble model used by the Java application.
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

# K-Means model for unsupervised student-profile segmentation.
cluster_features = X[["skill_match", "interest_match", "availability", "experience"]]
kmeans = KMeans(n_clusters=4, random_state=42, n_init=10)
kmeans.fit(cluster_features)
joblib.dump(kmeans, MODEL_DIR / "student_profile_kmeans.joblib")

(MODEL_DIR / "metrics.json").write_text(json.dumps(metrics, indent=2))
print("Models trained and saved to", MODEL_DIR)
print(json.dumps({k: v["test_accuracy"] for k, v in metrics.items()}, indent=2))
