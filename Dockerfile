FROM python:3.12-slim
ENV PYTHONDONTWRITEBYTECODE=1 PYTHONUNBUFFERED=1
WORKDIR /app
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt && useradd --create-home app
COPY estoque estoque
USER app
CMD ["waitress-serve", "--host=0.0.0.0", "--port=8000", "--threads=2", "--call", "estoque:create_app"]
