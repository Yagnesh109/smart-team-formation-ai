# SmartTeam AI - ML Service

## Setup
Use Python 3.11 for the ML environment because it is a stable choice for this college project.

```powershell
cd ml
py -3.11 -m venv .venv
.venv\Scripts\Activate.ps1
python -m pip install --upgrade pip
pip install -r requirements.txt
python train_models.py
uvicorn main:app --reload --port 8000
```

Open `http://127.0.0.1:8000/docs` for the FastAPI interactive API documentation.

## ML concepts demonstrated
- Supervised classification
- Logistic Regression
- K-Nearest Neighbours
- Decision Tree
- Support Vector Machine
- Naive Bayes
- Random Forest ensemble
- Voting ensemble
- Train/test split
- 5-fold cross-validation
- Confusion matrix
- Accuracy and classification report
- Unsupervised K-Means clustering
- Model serialization with joblib
- REST API model serving
