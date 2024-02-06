from fastapi import FastAPI

from app.interface import timer_task_routers

app = FastAPI()
app.include_router(timer_task_routers.router)

if __name__ == '__main__':
    import uvicorn
    uvicorn.run(app, host="localhost", port=8000)
