from locust import HttpUser, task
import uuid

class HelloWorldUser(HttpUser):
    @task
    def hello_world(self):
        self.client.post("/publish-kafka-message", json={"message": str(uuid.uuid4())})