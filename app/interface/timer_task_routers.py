from datetime import time, datetime
from typing import Optional, List
from uuid import UUID

from fastapi import APIRouter, Query
from pydantic import BaseModel

from app.domain.timer_tasks import DelayType, ComputationDelayType, DefinitionErrorType, State, DeadlineType, CycleType

router = APIRouter()


class TimerTaskDefinition(BaseModel):
    owner_id: str
    union_code: Optional[str]
    group_id: Optional[UUID]
    notification_key: Optional[str]
    run_on_creation: bool = True
    name: Optional[str]
    note: Optional[str]
    delay_type: DelayType = DelayType.NONE
    fixed_delay_seconds: Optional[int]
    computation_delay_type: Optional[ComputationDelayType]
    computation_delay_current_day_specific_time: Optional[time]
    computation_delay_current_week_specific_day: Optional[int]
    computation_delay_current_month_specific_day: Optional[int]
    computation_delay_current_year_specific_day: Optional[int]
    computation_delay_specific_datetime: Optional[datetime]
    cycle_type: CycleType = CycleType.NONE
    cycle_interval: Optional[int]
    deadline_type: DeadlineType = DeadlineType.NONE
    deadline_specific_datetime: Optional[datetime]
    deadline_on_ran_seconds: Optional[int]


class CreationTimerTaskResult(BaseModel):
    task_id: Optional[UUID]
    is_ok: bool = True
    error_code: Optional[DefinitionErrorType]


class TimerDescription(BaseModel):
    task_id: Optional[UUID]
    name: Optional[str]
    note: Optional[str]


class ChangeableTimerState(BaseModel):
    task_id: Optional[UUID]
    state: State


class TimerDefinition(BaseModel):
    task_id: Optional[UUID]
    delay_type: DelayType = DelayType.NONE
    fixed_delay_seconds: Optional[int]
    computation_delay_type: Optional[ComputationDelayType]
    computation_delay_current_day_specific_time: Optional[time]
    computation_delay_current_week_specific_day: Optional[int]
    computation_delay_current_month_specific_day: Optional[int]
    computation_delay_current_year_specific_day: Optional[int]
    computation_delay_specific_datetime: Optional[datetime]
    cycle_type: CycleType = CycleType.NONE
    cycle_interval: Optional[int]
    deadline_type: DeadlineType = DeadlineType.NONE
    deadline_specific_datetime: Optional[datetime]
    deadline_on_ran_seconds: Optional[int]


class TimerTaskDetails(BaseModel):
    task_id: UUID
    state: State
    lasted_run_datetime: Optional[datetime]
    owner_id: str
    union_code: Optional[str]
    group_id: Optional[UUID]
    group_name: Optional[str]
    notification_key: Optional[str]
    run_on_creation: bool
    name: Optional[str]
    note: Optional[str]
    delay_type: DelayType = DelayType.NONE
    fixed_delay_seconds: Optional[int]
    computation_delay_type: Optional[ComputationDelayType]
    computation_delay_current_day_specific_time: Optional[time]
    computation_delay_current_week_specific_day: Optional[int]
    computation_delay_current_month_specific_day: Optional[int]
    computation_delay_current_year_specific_day: Optional[int]
    computation_delay_specific_datetime: Optional[datetime]
    cycle_type: CycleType = CycleType.NONE
    cycle_interval: Optional[int]
    deadline_type: DeadlineType = DeadlineType.NONE
    deadline_specific_datetime: Optional[datetime]
    deadline_on_ran_seconds: Optional[int]
    created_at: datetime
    updated_at: datetime


class TimerTask(BaseModel):
    id: UUID
    state: State
    lasted_run_datetime: Optional[datetime]
    owner_id: str
    union_code: Optional[str]
    group_id: Optional[UUID]
    group_name: Optional[str]
    name: Optional[str]
    note: Optional[str]
    delay_type: DelayType = DelayType.NONE
    cycle_type: CycleType = CycleType.NONE
    deadline_type: DeadlineType = DeadlineType.NONE
    created_at: datetime
    updated_at: datetime


@router.post("/api/v1/timer-tasks")
def create_timer_task(timer_definition: TimerTaskDefinition) -> CreationTimerTaskResult:
    return CreationTimerTaskResult()


@router.post("/api/v1/bulk-timer-tasks")
def create_timer_tasks(timer_definitions: List[TimerTaskDefinition]) -> CreationTimerTaskResult:
    return CreationTimerTaskResult()


@router.delete("/api/v1/timer-tasks/{timer_id}")
def delete_timer_task(timer_id: UUID) -> None:
    return None


@router.delete("/api/v1/bulk-timer-tasks")
def delete_timer_tasks(timer_ids: List[UUID]) -> None:
    return None


@router.put("/api/v1/timer-tasks/{timer_id}/description")
def update_timer_task_description(timer_id: UUID, description: TimerDescription) -> None:
    return None


@router.put("/api/v1/bulk-timer-tasks/description")
def batch_update_timer_task_description(descriptions: List[TimerDescription]) -> None:
    return None


@router.put("/api/v1/timer-tasks/{timer_id}/state")
def update_timer_task_state(timer_id: UUID, state: ChangeableTimerState) -> None:
    return None


@router.put("/api/v1/bulk-timer-tasks/state")
def batch_update_timer_task_state(states: ChangeableTimerState) -> None:
    return None


@router.put("/api/v1/timer-tasks/{timer_id}/timer_definition")
def update_timer_task_timer_definition(timer_id: UUID, schedule_definition: TimerDefinition) -> None:
    return None


@router.put("/api/v1/bulk-timers/timer_definition")
def batch_update_timer_task_timer_definition(schedule_definitions: TimerDefinition) -> None:
    return None


@router.get("/api/v1/timer-tasks/{timer_id}/details")
def find_timer_task(timer_id: UUID) -> Optional[TimerTaskDetails]:
    return None


@router.get("/api/v1/timer-tasks/total")
def count_timer_task(timer_ids: List[UUID] = Query([])) -> int:
    return 0


@router.get("/api/v1/timer-tasks")
def list_timer_task(timer_ids: List[UUID] = Query([])) -> List[TimerTask]:
    return []
