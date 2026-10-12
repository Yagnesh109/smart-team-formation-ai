# Viva Topics Covered

## Advanced Java

### JDBC
Used in DBConnection and DAO classes.

### Servlet
Used for login, registration, matching and requests.

### JSP
Used for web presentation.

### Session Management
Used to store logged-in student ID/name/email.

### GET and POST
GET for page navigation; POST for form submissions.

### Request/Response
Servlet reads HttpServletRequest and writes/redirects HttpServletResponse.

### DAO
Separates SQL/database code from controllers.

### PreparedStatement
Used to avoid SQL injection and handle parameters.

### Maven
Build and dependency management.

### Tomcat
Servlet/JSP container.

### REST API integration
Java sends JSON to Python and reads JSON response.

## ML

### Supervised vs unsupervised learning
This project uses a similarity/ranking approach rather than a trained labeled classifier.

### Feature engineering
Converts skills, availability and experience into numerical features.

### TF-IDF
Weights important words in text.

### Cosine similarity
Measures similarity between vector representations.

### Ranking
Candidates are sorted by final compatibility score.

### Weighted scoring
Combines multiple signals.

### Precision/recall
Not the primary metric for this current similarity-based MVP because there is no labeled ground-truth dataset.

## Advanced extension

For a stronger research version, collect historical accepted/rejected invitations and train:
- Logistic Regression
- Random Forest
- Gradient Boosting

Then compare the trained model with the current similarity baseline using precision, recall, F1 and ROC-AUC.
