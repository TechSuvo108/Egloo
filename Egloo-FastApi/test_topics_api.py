import asyncio
import httpx
import uuid

async def run_tests():
    base_url = "http://localhost:8000/api/v1"
    
    async with httpx.AsyncClient(base_url=base_url) as client:
        # Register a test user
        email = f"test_topics_{uuid.uuid4().hex[:8]}@example.com"
        password = "password123"
        print(f"Registering user: {email}")
        reg_res = await client.post("/auth/register", json={"email": email, "password": password, "full_name": "Topic Tester"})
        if reg_res.status_code != 201:
            print("Registration failed:", reg_res.text)
            return
            
        # Login
        print("Logging in...")
        login_res = await client.post("/auth/login", json={"email": email, "password": password})
        if login_res.status_code != 200:
            print("Login failed:", login_res.text)
            return
            
        token = login_res.json().get("access_token")
        headers = {"Authorization": f"Bearer {token}"}
        
        print("------- Test A: List topics before refresh (empty) -------")
        res_a = await client.get("/topics", headers=headers)
        print(f"Status: {res_a.status_code}")
        print(f"Response: {res_a.json()}")
        print()
        
        print("------- Test B: Refresh topics with no data -------")
        res_b = await client.post("/topics/refresh", json={"strategy": "auto", "max_topics": 8}, headers=headers)
        print(f"Status: {res_b.status_code}")
        print(f"Response: {res_b.json()}")
        print()

if __name__ == "__main__":
    asyncio.run(run_tests())
