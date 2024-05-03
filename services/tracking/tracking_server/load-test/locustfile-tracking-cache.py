import uuid
from locust import task, FastHttpUser, between
import json

#stats.PERCENTILES_TO_CHART = [0.95, 0.99]

class TrackingCacheV1(FastHttpUser):
    wait_time = between(1, 3)
    connection_timeout = 10.0
    network_timeout = 10.0

    def on_start(self):
        # 토큰 발급 로직
        response = self.client.post("/api/tracking/v1/generate-token",
            headers={"api-key-id": "test-api-key"},
            json={
                "identifier": str(uuid.uuid4()),
                "scope": ["WRITE_TRACKING_COORDINATE", "READ_TRACKING_COORDINATE"],
                "tokenExpiresIn": 3600
            })
        if response.status_code == 200:
            result = json.loads(response.text)
            self.token = result['token']
            self.start_tracking()

    def start_tracking(self):
        headers = {'Authorization': f'Bearer {self.token}'}
        payload = {
            "startLatitude" : 89.9,
            "startLongitude" : 1.0,
            "endLatitude" : 2.0,
            "endLongitude" : 2.0,
            "estimatedArrivalTime" : 2
        }

        response = self.client.post(
            "/api/tracking/v1/start",
            json=payload,
            headers=headers
        )

    @task
    def tracking(self):
        headers = {'Authorization': f'Bearer {self.token}'}
        payload = {
            "latitude" : 33.33,
            "longitude" : 33.33
        }
        self.client.post(
            "/api/tracking/v1/tracking",
            json = payload,
            headers = headers)

        # 읽기가 동시에 일어난다고 설정
        self.client.get(
            "/api/tracking/v1/tracking",
            headers=headers)


