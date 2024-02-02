import uvicorn
from fastapi import FastAPI

from app.interface import timer_routers

app = FastAPI()
app.include_router(timer_routers.router)

if __name__ == '__main__':
    uvicorn.run(app, host="localhost", port=8000)
