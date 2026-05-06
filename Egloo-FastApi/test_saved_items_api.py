import asyncio
import httpx
import uuid

async def run_tests():
    base_url = "http://localhost:8000/api/v1"
    
    async with httpx.AsyncClient(base_url=base_url) as client:
        # Register and login
        email = f"test_saved_{uuid.uuid4().hex[:8]}@example.com"
        password = "password123"
        print(f"Registering user: {email}")
        await client.post("/auth/register", json={"email": email, "password": password, "full_name": "Saved Tester"})
        login_res = await client.post("/auth/login", json={"email": email, "password": password})
        token = login_res.json().get("access_token")
        headers = {"Authorization": f"Bearer {token}"}
        
        print("------- Test 1: Save a query result -------")
        res1 = await client.post("/saved", headers=headers, json={
            "item_type": "query_result",
            "title": "Status of Project Alpha",
            "content": "The launch has been delayed to May 10...",
            "item_metadata": {
                "question": "What is the status of Project Alpha?",
                "model_used": "gemini",
                "sources_count": 3
            }
        })
        print(f"Status: {res1.status_code}")
        item1_id = res1.json().get("id")
        
        print("------- Test 2: Save a digest -------")
        res2 = await client.post("/saved", headers=headers, json={
            "item_type": "digest",
            "title": "Digest — 2024-04-29",
            "content": "Today Pingo found activity across 4 topics...",
            "item_metadata": {
                "digest_id": str(uuid.uuid4()),
                "date": "2024-04-29",
                "action_items_count": 3,
                "topics_count": 4
            }
        })
        print(f"Status: {res2.status_code}")
        
        print("------- Test 3: Save a topic -------")
        res3 = await client.post("/saved", headers=headers, json={
            "item_type": "topic",
            "title": "Project Alpha",
            "content": "Discussions about the product launch timeline...",
            "item_metadata": {"topic_id": str(uuid.uuid4()), "item_count": 12}
        })
        print(f"Status: {res3.status_code}")
        
        print("------- Test 4: List all saved items -------")
        res4 = await client.get("/saved", headers=headers)
        print(f"Status: {res4.status_code}, Total: {res4.json().get('total')}")
        
        print("------- Test 5: Filter by type -------")
        res5a = await client.get("/saved?item_type=digest", headers=headers)
        print(f"Digest Filter Total: {len(res5a.json().get('items'))}")
        
        print("------- Test 6: Get counts breakdown -------")
        res6 = await client.get("/saved/counts", headers=headers)
        print(f"Counts: {res6.json()}")
        
        print("------- Test 7: Get single saved item -------")
        res7 = await client.get(f"/saved/{item1_id}", headers=headers)
        print(f"Status: {res7.status_code}, Title: {res7.json().get('title')}")
        
        print("------- Test 8: Invalid item_type -------")
        res8 = await client.post("/saved", headers=headers, json={"item_type": "invalid", "title": "test"})
        print(f"Status: {res8.status_code}")
        
        print("------- Test 9: Empty title -------")
        res9 = await client.post("/saved", headers=headers, json={"item_type": "digest", "title": "   "})
        print(f"Status: {res9.status_code}")
        
        print("------- Test 10: Delete one item -------")
        res10 = await client.delete(f"/saved/{item1_id}", headers=headers)
        print(f"Status: {res10.status_code}, Message: {res10.json().get('message')}")
        
        print("------- Test 11: Quick-save from query -------")
        # Note: This requires active AI keys.
        res11 = await client.post("/query/save", headers=headers, json={"question": "What are my action items today?"})
        print(f"Status: {res11.status_code}")
        if res11.status_code == 200:
            print(f"Saved ID: {res11.json().get('saved_id')}")
            
        print("------- Test 13: Delete all saved items -------")
        res13 = await client.delete("/saved/all", headers=headers)
        print(f"Status: {res13.status_code}, Message: {res13.json().get('message')}")
        
        res14 = await client.get("/saved", headers=headers)
        print(f"Final Total: {res14.json().get('total')}")

if __name__ == "__main__":
    asyncio.run(run_tests())
