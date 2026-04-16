from fastapi import FastAPI
import math

app = FastAPI()


def predict_traffic_model(hour: int) -> int:
    
    peak_traffic = 5000
    base_traffic = 100
    
    
    variance = 4.0
    multiplier = math.exp(-((hour - 12) ** 2) / (2 * variance))
    
    predicted_tasks = int(base_traffic + (peak_traffic * multiplier))
    return predicted_tasks

@app.get("/api/predict")
def get_prediction(hour_of_day: int):
    """
    Spring Boot will call this URL to get the predicted number of tasks.
    """
    predicted_tasks = predict_traffic_model(hour_of_day)
    
    return {
        "status": "success",
        "hour": hour_of_day,
        "predicted_tasks": predicted_tasks,
        "recommended_action": "scale_up" if predicted_tasks > 1000 else "scale_down"
    }

# To run this: uvicorn main:app --reload --port 8001