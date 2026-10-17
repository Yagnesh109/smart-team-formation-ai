from pathlib import Path
import json
import numpy as np
import pandas as pd
import joblib
from sklearn.model_selection import train_test_split, cross_val_score, StratifiedKFold
from sklearn.preprocessing import StandardScaler
from sklearn.pipeline import Pipeline
from sklearn.metrics import (
    accuracy_score,
    roc_auc_score,
    classification_report,
    confusion_matrix,
    mean_absolute_error,
    root_mean_squared_error,
    r2_score,
)
from sklearn.linear_model import LogisticRegression
from sklearn.neighbors import KNeighborsClassifier
from sklearn.tree import DecisionTreeClassifier
from sklearn.svm import SVC
from sklearn.naive_bayes import GaussianNB
from sklearn.ensemble import (
    RandomForestClassifier,
    RandomForestRegressor,
    GradientBoostingClassifier,
    VotingClassifier,
)
from sklearn.cluster import KMeans

BASE = Path(__file__).resolve().parent
MODEL_DIR = BASE / "models"
MODEL_DIR.mkdir(exist_ok=True)
DATA_FILE = BASE / "training_data.csv"

def train():
    print("=" * 70)
    print("SMARTTEAM AI - MACHINE LEARNING MODEL TRAINING PIPELINE")
    print("=" * 70)

    if not DATA_FILE.exists():
        print("training_data.csv not found! Running prepare_dataset.py first...")
        import prepare_dataset
        prepare_dataset.main()

    data = pd.read_csv(DATA_FILE)
    print(f"Loaded training data: {data.shape[0]} rows, {data.shape[1]} columns")

    feature_cols = [
        "skill_match",
        "interest_match",
        "availability",
        "experience",
        "communication",
        "reliability",
    ]

    X = data[feature_cols]
    y = data["is_compatible"].astype(int)

    print(f"Features: {feature_cols}")
    print(f"Class distribution:\n{y.value_counts(normalize=True).to_dict()}")

    X_train, X_test, y_train, y_test = train_test_split(
        X, y, test_size=0.20, random_state=42, stratify=y
    )

    print("\n" + "=" * 70)
    print("1. TRAINING SUPERVISED CLASSIFIERS")
    print("=" * 70)

    models = {
        "logistic_regression": Pipeline([
            ("scale", StandardScaler()),
            ("model", LogisticRegression(max_iter=1000, random_state=42))
        ]),
        "knn": Pipeline([
            ("scale", StandardScaler()),
            ("model", KNeighborsClassifier(n_neighbors=7))
        ]),
        "decision_tree": DecisionTreeClassifier(max_depth=7, min_samples_leaf=10, random_state=42),
        "svm": Pipeline([
            ("scale", StandardScaler()),
            ("model", SVC(probability=True, kernel="rbf", C=1.5, random_state=42))
        ]),
        "naive_bayes": GaussianNB(),
        "random_forest": RandomForestClassifier(
            n_estimators=200, max_depth=10, min_samples_leaf=5, random_state=42, n_jobs=-1
        ),
        "gradient_boosting": GradientBoostingClassifier(
            n_estimators=120, max_depth=5, learning_rate=0.1, random_state=42
        ),
    }

    metrics = {
        "_metadata": {
            "dataset_source": "Kaggle Dynamic Student-Project Matching & Interdisciplinary Collaboration Datasets",
            "sample_count": int(len(data)),
            "feature_names": feature_cols,
            "target": "is_compatible (binary) & compatibility_score (continuous 0-100)"
        }
    }

    cv_strategy = StratifiedKFold(n_splits=5, shuffle=True, random_state=42)

    for name, model in models.items():
        print(f"Training {name}...")
        model.fit(X_train, y_train)
        pred = model.predict(X_test)
        prob = model.predict_proba(X_test)[:, 1] if hasattr(model, "predict_proba") else None

        cv = cross_val_score(model, X, y, cv=cv_strategy, scoring="accuracy", n_jobs=-1)
        roc = float(roc_auc_score(y_test, prob)) if prob is not None else None

        metrics[name] = {
            "test_accuracy": round(float(accuracy_score(y_test, pred)), 4),
            "cv_mean": round(float(cv.mean()), 4),
            "cv_std": round(float(cv.std()), 4),
            "roc_auc": round(roc, 4) if roc is not None else None,
            "confusion_matrix": confusion_matrix(y_test, pred).tolist(),
            "classification_report": classification_report(y_test, pred, output_dict=True),
        }
        print(f"  -> Accuracy: {metrics[name]['test_accuracy']:.4f} | CV Mean: {metrics[name]['cv_mean']:.4f} | ROC-AUC: {roc:.4f}")

    print("\n" + "=" * 70)
    print("2. TRAINING ADVANCED SOFT VOTING ENSEMBLE")
    print("=" * 70)

    # Soft Voting Ensemble combining Random Forest, Gradient Boosting, SVM, LR, and KNN
    ensemble = VotingClassifier(
        estimators=[
            ("rf", models["random_forest"]),
            ("gb", models["gradient_boosting"]),
            ("svm", models["svm"]),
            ("lr", models["logistic_regression"]),
            ("knn", models["knn"]),
        ],
        voting="soft",
        weights=[3, 3, 2, 1, 1],
    )

    ensemble.fit(X_train, y_train)
    ens_pred = ensemble.predict(X_test)
    ens_prob = ensemble.predict_proba(X_test)[:, 1]
    ens_roc = float(roc_auc_score(y_test, ens_prob))
    ens_cv = cross_val_score(ensemble, X, y, cv=cv_strategy, scoring="accuracy", n_jobs=-1)

    metrics["voting_ensemble"] = {
        "test_accuracy": round(float(accuracy_score(y_test, ens_pred)), 4),
        "cv_mean": round(float(ens_cv.mean()), 4),
        "cv_std": round(float(ens_cv.std()), 4),
        "roc_auc": round(ens_roc, 4),
        "confusion_matrix": confusion_matrix(y_test, ens_pred).tolist(),
        "classification_report": classification_report(y_test, ens_pred, output_dict=True),
    }

    joblib.dump(ensemble, MODEL_DIR / "team_match_ensemble.joblib")
    print(f"Ensemble Accuracy: {metrics['voting_ensemble']['test_accuracy']:.4f} | ROC-AUC: {ens_roc:.4f}")
    print(f"Saved team_match_ensemble.joblib")

    # Feature Importance analysis from Random Forest
    rf_model = models["random_forest"]
    importances = {col: round(float(imp), 4) for col, imp in zip(feature_cols, rf_model.feature_importances_)}
    metrics["feature_importances"] = importances
    print(f"Feature Importances: {importances}")

    print("\n" + "=" * 70)
    print("3. TRAINING K-MEANS CLUSTERING (STUDENT PROFILE ARCHETYPES)")
    print("=" * 70)

    cluster_features = ["skill_match", "interest_match", "availability", "experience"]
    X_cluster = data[cluster_features]

    kmeans = KMeans(n_clusters=4, random_state=42, n_init=15)
    kmeans.fit(X_cluster)
    joblib.dump(kmeans, MODEL_DIR / "student_profile_kmeans.joblib")

    # Dynamic archetype mapping based on cluster centroids
    centroids = kmeans.cluster_centers_
    archetypes = {}
    # Rank clusters by skill_match, availability, and experience
    cluster_scores = []
    for cid in range(4):
        c = centroids[cid]
        cluster_scores.append({
            "cid": cid,
            "skill": c[0],
            "interest": c[1],
            "avail": c[2],
            "exp": c[3],
        })

    # Sort to assign intuitive personas
    cluster_scores.sort(key=lambda x: x["skill"], reverse=True)
    archetype_names = [
        ("High-Skill Specialist", "Top-tier technical capability with deep specialization in project domains."),
        ("Balanced Generalist", "Versatile skill set with consistent cross-domain capability and steady collaboration."),
        ("Dedicated Collaborator", "High availability and strong reliability, ideal for intensive project sprints."),
        ("Developing Enthusiast", "Emerging talent with high motivation and growing foundational skills.")
    ]

    archetype_map = {}
    for rank, entry in enumerate(cluster_scores):
        cid = entry["cid"]
        name, desc = archetype_names[rank]
        archetype_map[str(cid)] = {
            "name": name,
            "description": desc,
            "centroid": {
                "skill_match": round(float(entry["skill"]), 2),
                "interest_match": round(float(entry["interest"]), 2),
                "availability": round(float(entry["avail"]), 2),
                "experience": round(float(entry["exp"]), 2),
            }
        }

    (MODEL_DIR / "kmeans_archetypes.json").write_text(json.dumps(archetype_map, indent=2))
    print(f"Saved student_profile_kmeans.joblib and kmeans_archetypes.json")

    print("\n" + "=" * 70)
    print("4. TRAINING RANDOM FOREST REGRESSOR (COMPATIBILITY SCORE)")
    print("=" * 70)

    if "compatibility_score" in data.columns:
        y_reg = data["compatibility_score"]
        Xr_train, Xr_test, yr_train, yr_test = train_test_split(
            X, y_reg, test_size=0.20, random_state=42
        )

        regressor = RandomForestRegressor(
            n_estimators=200, max_depth=10, min_samples_leaf=5, random_state=42, n_jobs=-1
        )
        regressor.fit(Xr_train, yr_train)
        pred_reg = regressor.predict(Xr_test)

        mae = mean_absolute_error(yr_test, pred_reg)
        rmse = root_mean_squared_error(yr_test, pred_reg)
        r2 = r2_score(yr_test, pred_reg)

        metrics["random_forest_regressor"] = {
            "mae": round(float(mae), 4),
            "rmse": round(float(rmse), 4),
            "r2": round(float(r2), 4),
        }
        joblib.dump(regressor, MODEL_DIR / "compatibility_model.joblib")
        print(f"Regressor MAE: {mae:.2f} | RMSE: {rmse:.2f} | R²: {r2:.4f}")
        print("Saved compatibility_model.joblib")

    # Save full metrics to JSON
    (MODEL_DIR / "metrics.json").write_text(json.dumps(metrics, indent=2))
    print("\n" + "=" * 70)
    print(f"TRAINING COMPLETE! All models and metrics saved to: {MODEL_DIR}")
    print(f"Metrics summary:")
    for k, v in metrics.items():
        if isinstance(v, dict) and "test_accuracy" in v:
            print(f"  {k:20s}: Accuracy={v['test_accuracy']:.4f}, ROC-AUC={v.get('roc_auc')}")
        elif isinstance(v, dict) and "r2" in v:
            print(f"  {k:20s}: R²={v['r2']:.4f}, MAE={v['mae']:.2f}")
    print("=" * 70)

if __name__ == "__main__":
    train()
