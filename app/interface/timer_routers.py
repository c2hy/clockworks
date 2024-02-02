from datetime import time, datetime
from typing import Optional, List
from uuid import UUID

from fastapi import APIRouter, Query
from pydantic import BaseModel

from app.domain.timers import DelayType, ComputationDelayType, DefinitionErrorType, State, DeadlineType, CycleType

router = APIRouter()


class TimerDefinition(BaseModel):
    id: UUID
    name: Optional[str]
    note: Optional[str]
    group_id: Optional[UUID]
    owner_id: Optional[str]
    notification_key: Optional[str]
    run_on_creation: bool = True
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


class TimerDefinitionResult(BaseModel):
    id: Optional[UUID]
    is_ok: bool = True
    error_code: Optional[DefinitionErrorType]


class TimerDescription(BaseModel):
    id: Optional[UUID]
    name: Optional[str]
    note: Optional[str]


class ChangeableTimerState(BaseModel):
    id: Optional[UUID]
    state: State


class TimerScheduleDefinition(BaseModel):
    id: Optional[UUID]
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


class TimerDetails(BaseModel):
    id: UUID
    name: Optional[str]
    lasted_run_datetime: Optional[datetime]
    state: State
    note: Optional[str]
    group_id: Optional[UUID]
    group_name: Optional[str]
    owner_id: Optional[str]
    notification_key: Optional[str]
    run_on_creation: bool
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


class Timer(BaseModel):
    id: UUID
    name: Optional[str]
    lasted_run_datetime: Optional[datetime]
    state: State
    note: Optional[str]
    group_id: Optional[UUID]
    group_name: Optional[str]
    delay_type: DelayType = DelayType.NONE
    cycle_type: CycleType = CycleType.NONE
    deadline_type: DeadlineType = DeadlineType.NONE
    created_at: datetime
    updated_at: datetime


@router.post("/api/v1/timers")
def create_timer(timer_definition: TimerDefinition) -> TimerDefinitionResult:
    return TimerDefinitionResult()


@router.post("/api/v1/bulk-timers")
def create_timers(timer_definitions: List[TimerDefinition]) -> TimerDefinitionResult:
    return TimerDefinitionResult()


@router.delete("/api/v1/timers/{timer_id}")
def delete_timer(timer_id: UUID) -> None:
    return None


@router.delete("/api/v1/bulk-timers")
def delete_timers(timer_ids: List[UUID]) -> None:
    return None


@router.put("/api/v1/timers/{timer_id}/description")
def update_timer_description(timer_id: UUID, description: TimerDescription) -> None:
    return None


@router.put("/api/v1/bulk-timers/description")
def batch_update_timer_description(descriptions: List[TimerDescription]) -> None:
    return None


@router.put("/api/v1/timers/{timer_id}/state")
def update_timer_state(timer_id: UUID, state: ChangeableTimerState) -> None:
    return None


@router.put("/api/v1/bulk-timers/state")
def batch_update_timer_state(states: ChangeableTimerState) -> None:
    return None


@router.put("/api/v1/timers/{timer_id}/schedule_definition")
def update_timer_schedule_definition(timer_id: UUID, schedule_definition: TimerScheduleDefinition) -> None:
    return None


@router.put("/api/v1/bulk-timers/schedule_definition")
def batch_update_timer_schedule_definition(schedule_definitions: TimerScheduleDefinition) -> None:
    return None


@router.get("/api/v1/timers/{timer_id}/details")
def find_timer(timer_id: UUID) -> Optional[TimerDetails]:
    return None


@router.get("/api/v1/timers/total")
def count_timer(timer_ids: List[UUID] = Query([])) -> int:
    return 0


@router.get("/api/v1/timers")
def list_timer(timer_ids: List[UUID] = Query([])) -> List[Timer]:
    return []
