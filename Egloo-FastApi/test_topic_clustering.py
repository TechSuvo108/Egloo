import asyncio
from app.ai.topic_ai import cluster_chunks

fake_chunks = [
    {'content': 'Project Alpha launch delayed to May 10 due to QA bugs', 'chunk_metadata': {'source_type': 'gmail', 'sender': 'rohit@co.com', 'subject': 'Launch update'}},
    {'content': 'Budget for Q2 approved by CFO — 120k confirmed', 'chunk_metadata': {'source_type': 'gmail', 'sender': 'cfo@co.com', 'subject': 'Budget'}},
    {'content': 'Please review the Figma mockups before EOD today', 'chunk_metadata': {'source_type': 'slack', 'sender': 'designer', 'subject': ''}},
    {'content': 'New hire Sarah starts Monday — please set up access', 'chunk_metadata': {'source_type': 'gmail', 'sender': 'hr@co.com', 'subject': 'Onboarding'}},
    {'content': 'QA found 3 critical bugs in the payment module', 'chunk_metadata': {'source_type': 'slack', 'sender': 'qa_team', 'subject': ''}},
    {'content': 'CFO approved additional headcount for engineering team', 'chunk_metadata': {'source_type': 'gmail', 'sender': 'cfo@co.com', 'subject': 'Headcount'}},
]

async def test():
    topics = await cluster_chunks(fake_chunks, strategy='llm', max_topics=4)
    print(f'Topics found: {len(topics)}')
    for t in topics:
        print(f'  Name: {t["name"]}')
        print(f'  Summary: {t["summary"]}')
        print(f'  Chunks: {t["chunk_indices"]}')
        print()

if __name__ == "__main__":
    asyncio.run(test())
