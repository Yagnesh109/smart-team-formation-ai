import pandas as pd
import joblib

from sklearn.ensemble import RandomForestRegressor
from sklearn.model_selection import train_test_split
from sklearn.metrics import mean_absolute_error, r2_score


# -------------------------------------------------
# 1. Load training data
# -------------------------------------------------

data = pd.read_csv("training_data.csv")

print("Training data loaded")
print("Number of records:", len(data))


# -------------------------------------------------
# 2. Select ML features
# -------------------------------------------------

features = [
    "skill_score",
    "experience_score",
    "availability_score",
    "matched_skills",
    "required_skills",
    "skill_gap",
    "department_match",
    "year_difference"
]

X = data[features]

y = data["compatibility_score"]


# -------------------------------------------------
# 3. Split dataset
# -------------------------------------------------

X_train, X_test, y_train, y_test = train_test_split(
    X,
    y,
    test_size=0.20,
    random_state=42
)


# -------------------------------------------------
# 4. Create Random Forest model
# -------------------------------------------------

model = RandomForestRegressor(
    n_estimators=200,
    max_depth=10,
    random_state=42
)


# -------------------------------------------------
# 5. Train model
# -------------------------------------------------

print("Training Random Forest model...")

model.fit(X_train, y_train)

print("Training completed")


# -------------------------------------------------
# 6. Test model
# -------------------------------------------------

predictions = model.predict(X_test)

mae = mean_absolute_error(
    y_test,
    predictions
)

r2 = r2_score(
    y_test,
    predictions
)


print()
print("================================")
print("MODEL PERFORMANCE")
print("================================")

print("Mean Absolute Error:", round(mae, 2))
print("R2 Score:", round(r2, 2))


# -------------------------------------------------
# 7. Save trained model
# -------------------------------------------------

joblib.dump(
    model,
    "compatibility_model.joblib"
)

print()
print("================================")
print("MODEL SAVED")
print("================================")

print(
    "compatibility_model.joblib created successfully"
)