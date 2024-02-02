FROM python:3.12-slim as python-base

ENV PYTHONUNBUFFERED=1 \
    PYTHONDONTWRITEBYTECODE=1 \
    PIP_DISABLE_PIP_VERSION_CHECK=on \
    PIP_DEFAULT_TIMEOUT=100 \
    POETRY_VERSION=1.7.1 \
    POETRY_HOME="/opt/poetry" \
    POETRY_VIRTUALENVS_IN_PROJECT=true \
    POETRY_NO_INTERACTION=1 \
    PYSETUP_PATH="/opt/pysetup" \
    VENV_PATH="/opt/pysetup/.venv"

ENV PATH="$POETRY_HOME/bin:$VENV_PATH/bin:$PATH"

FROM python-base as builder-base

RUN pip install poetry==$POETRY_VERSION

WORKDIR $PYSETUP_PATH
COPY pyproject.toml poetry.lock ./

RUN --mount=type=cache,target=/root/.cache poetry install

FROM python-base as production

ENV FASTAPI_ENV=production

COPY --from=builder-base $PYSETUP_PATH $PYSETUP_PATH

COPY ./app /app/

WORKDIR /

CMD ["uvicorn", "app.main:app", "--host", "0.0.0.0", "--port", "8000"]
