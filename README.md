# API-UI

# 🚀 API Automation Tool (UI + Backend)

This project is a **full-stack API automation testing tool** built with:

* 🔹 **Frontend**: React (Vite)
* 🔹 **Backend**: Spring Boot (Java)
* 🔹 **Input**: Excel-based test cases
* 🔹 **Output**: HTML test report

---

# 📁 Project Structure

```
API-UI/
 ├── backend/        # Spring Boot backend
 ├── frontend/       # React frontend
 ├── .gitignore
 └── README.md
```

---

# ⚙️ Prerequisites

Make sure the following are installed:

```bash
java -version
mvn -version
node -v
npm -v
```

### Required:

* Java (JDK 17 or above)
* Maven
* Node.js (LTS)

---

# 📦 Setup Instructions

## 1️⃣ Clone the Repository

```bash
git clone https://github.com/rupeshrane/API-UI.git
cd API-UI
```

---

## 2️⃣ Backend Setup (Spring Boot)

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

### Backend runs on:

```
http://localhost:8080
```

---

## 3️⃣ Frontend Setup (React)

Open a new terminal:

```bash
cd frontend
npm install
npm run dev
```

### Frontend runs on:

```
http://localhost:5173
```

---

# 🔗 API Connection

Ensure frontend API calls point to:

```
http://localhost:8080
```

---

# 📄 Excel File (Important)

Make sure the test data file exists:

```
backend/src/main/resources/api_test_data.xlsx
```

---

# 🔄 Daily Usage

## Start Backend:

```bash
cd backend
mvn spring-boot:run
```

## Start Frontend:

```bash
cd frontend
npm run dev
```

---

# 🧹 Clean & Reinstall (if issues occur)

```bash
cd backend
mvn clean install

cd ../frontend
rm -rf node_modules
npm install
```

👉 For Windows PowerShell:

```powershell
Remove-Item -Recurse -Force node_modules
```

---

# ⚠️ Common Issues & Fixes

### ❌ Maven not recognized

* Install Maven
* Restart terminal

---

### ❌ Node modules missing

```bash
npm install
```

---

### ❌ Port already in use

#### Change backend port:

Edit `application.properties`:

```
server.port=8081
```

#### Change frontend port:

```bash
npm run dev -- --port 3000
```

---

### ❌ Backend 500 Error

* Check Excel file path
* Verify logs in terminal

---

# 📤 Git Commands (For Updates)

```bash
git add .
git commit -m "your changes"
git push
```

---

# 🛠️ Features

* Excel-driven API testing
* Automated test execution
* HTML report generation
* UI-based execution
* Error handling & logs

---

# ⭐ Future Enhancements

* Authentication support (OAuth2, Basic)
* Dashboard with charts
* Test history tracking
* CI/CD integration

---

# 👨‍💻 Author

**Rupesh Rane**

---

# 📌 Notes

* Do NOT commit:

  * `target/`
  * `node_modules/`
  * `.jar` files
* Use `.gitignore` properly

---

# 🚀 Quick Start (One View)

```bash
git clone https://github.com/rupeshrane/API-UI.git
cd API-UI

cd backend
mvn spring-boot:run

cd ../frontend
npm install
npm run dev
```

---

✨ Now your project is ready to run on any system!
