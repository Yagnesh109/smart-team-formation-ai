
from flask import Flask, request, jsonify
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity
import re

app = Flask(__name__)


def normalize(text):
    text = str(text or "").lower()
    text = re.sub(r"[^a-z0-9+#.*\- ]", " ", text)
    return re.sub(r"\s+", " ", text).strip()


def tokens(text):
    text = normalize(text)
    return {
        x.strip()
        for x in re.split(r"[,;/|]+|\s{2,}", text)
        if x.strip()
    }


def skill_score(required, candidate):
    req = tokens(required)
    cand = tokens(candidate)

    if not req:
        return 0.0

    matched = sum(
        1 for r in req
        if any(r == c or r in c or c in r for c in cand)
    )

    return matched / len(req)


def availability_score(project_availability, candidate_availability):
    p = tokens(project_availability)
    c = tokens(candidate_availability)

    if not p or not c:
        return 0.0

    return len(p.intersection(c)) / len(p)


def experience_score(value):
    value = normalize(value)

    mapping = {
        "beginner": 0.33,
        "intermediate": 0.66,
        "advanced": 1.0
    }

    for key, score in mapping.items():
        if key in value:
            return score

    return 0.5


def tfidf_similarity(project_text, candidate_text):
    project_text = normalize(project_text)
    candidate_text = normalize(candidate_text)

    if not project_text or not candidate_text:
        return 0.0

    vectorizer = TfidfVectorizer(ngram_range=(1, 2))

    matrix = vectorizer.fit_transform(
        [project_text, candidate_text]
    )

    return float(
        cosine_similarity(matrix[0:1], matrix[1:2])[0][0]
    )


def normalize_numeric_score(value):
    """Convert a numeric score from 0–100 or 0–1 into 0–1."""
    try:
        score = float(value)
    except (TypeError, ValueError):
        return 0.0

    if score > 1:
        score /= 100.0

    return max(0.0, min(score, 1.0))


@app.get("/health")
def health():
    return jsonify({
        "status": "ok",
        "service": "smart-team-ml"
    })


# Existing single-candidate endpoint
@app.post("/predict-match")
def predict_match():
    data = request.get_json(silent=True) or {}

    project_name = data.get("project_name", "")
    required_skills = data.get("required_skills", "")
    project_availability = data.get("project_availability", "")
    candidate_skills = data.get("candidate_skills", "")
    candidate_interests = data.get("candidate_interests", "")
    candidate_availability = data.get("candidate_availability", "")
    candidate_experience = data.get("candidate_experience", "")

    project_text = f"{project_name} {required_skills}"
    candidate_text = f"{candidate_skills} {candidate_interests}"

    skill = skill_score(required_skills, candidate_skills)
    text = tfidf_similarity(project_text, candidate_text)
    availability = availability_score(
        project_availability,
        candidate_availability
    )
    experience = experience_score(candidate_experience)

    final_score = (
        0.50 * skill
        + 0.25 * text
        + 0.15 * availability
        + 0.10 * experience
    )

    return jsonify({
        "match_score": round(final_score * 100, 2),
        "components": {
            "skill_score": round(skill * 100, 2),
            "text_similarity": round(text * 100, 2),
            "availability_score": round(availability * 100, 2),
            "experience_score": round(experience * 100, 2)
        }
    })


# Batch endpoint used by Java PythonMLService.java
@app.post("/predict")
def predict():
    data = request.get_json(silent=True)

    if not isinstance(data, dict):
        return jsonify({
            "error": "Request body must be a JSON object."
        }), 400

    students = data.get("students", [])

    if not isinstance(students, list):
        return jsonify({
            "error": "'students' must be a list."
        }), 400

    recommendations = []

    for student in students:
        if not isinstance(student, dict):
            continue

        skill = normalize_numeric_score(
            student.get("skill_score", 0)
        )
        experience = normalize_numeric_score(
            student.get("experience_score", 0)
        )
        availability = normalize_numeric_score(
            student.get("availability_score", 0)
        )

        # Uses the scores provided by the Java matching engine.
        # The text similarity calculation is available through
        # /predict-match when textual profile fields are provided.
        final_score = (
            0.70 * skill
            + 0.15 * experience
            + 0.15 * availability
        )

        recommendations.append({
            "student_id": student.get("student_id"),
            "name": student.get("name"),
            "predicted_score": round(final_score * 100, 2),
            "components": {
                "skill_score": round(skill * 100, 2),
                "experience_score": round(experience * 100, 2),
                "availability_score": round(availability * 100, 2)
            }
        })

    recommendations.sort(
        key=lambda x: x["predicted_score"],
        reverse=True
    )

    return jsonify({
        "project_id": data.get("project_id"),
        "recommendations": recommendations,
        "count": len(recommendations)
    })


if __name__ == "__main__":
    app.run(
        host="127.0.0.1",
        port=5000,
        debug=True
    )

