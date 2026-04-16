# Predictive Cloud Resource Orchestrator 🧠☁️

A distributed system designed to optimize cloud infrastructure through **Predictive Auto-Scaling**. This project integrates a Machine Learning-driven traffic forecasting service with a Java-based cloud simulation engine to minimize makespan and prevent over-provisioning.

## 🚀 System Overview
In traditional cloud environments, scaling is often reactive, leading to latency during sudden traffic spikes. This system implements a proactive approach:
* **Workload Forecasting:** Analyzes temporal data to predict incoming task volume.
* **Automated Scaling:** Dynamically adjusts Virtual Machine (VM) capacity based on AI predictions.
* **Efficient Scheduling:** Utilizes the **Min-Min Algorithm** to map tasks to resources for optimal performance.

## 🏗️ Architecture
The project is built using a **Microservices Architecture** to ensure separation of concerns:

* **Service A (ML Predictor):** Developed with **FastAPI (Python)**. It provides real-time workload estimations via REST endpoints.
* **Service B (Cloud Dispatcher):** Developed with **Spring Boot (Java)**. It acts as the primary orchestrator, consuming the ML API and executing the **CloudSim Plus** simulation.
* **Communication:** Synchronous RESTful communication via HTTP/JSON.

## 🛠️ Tech Stack
- **Backend Framework:** Spring Boot (Java 23)
- **AI Microservice:** FastAPI (Python)
- **Simulation Engine:** CloudSim Plus
- **Algorithm:** Min-Min Scheduling
- **API Tooling:** Swagger UI / RestTemplate

## 🚦 Getting Started

### 1. Start the Python ML Service
Navigate to the predictor directory and run:
```bash
python -m uvicorn main:app --reload --port 8001