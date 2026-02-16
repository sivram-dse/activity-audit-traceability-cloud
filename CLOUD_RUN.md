# Deploy to Google Cloud Run

Deploy the backend as a single Cloud Run service. The service also serves the built-in demo UI from `/`.

## Prerequisites

- Google Cloud project with billing enabled
- `gcloud` CLI installed and authenticated
- APIs enabled:
  - Cloud Run
  - Cloud Build

## Deploy Options

### Option A: Build image then deploy
```bash
gcloud builds submit --tag gcr.io/<PROJECT_ID>/activity-audit

gcloud run deploy activity-audit \
  --image gcr.io/<PROJECT_ID>/activity-audit \
  --platform managed \
  --region <REGION> \
  --allow-unauthenticated
```

### Option B: Deploy from source
```bash
gcloud run deploy activity-audit \
  --source ./backend \
  --region <REGION> \
  --allow-unauthenticated
```

## Service Endpoints

- Demo UI: `/`
- Audit APIs: `/api/...`
- Health: `/actuator/health`

## Database Strategy

### Demo mode (default): H2 on `/tmp`
- Default JDBC URL uses H2 file database in `/tmp`
- Works well for demos and hackathons
- Data is ephemeral and tied to instance lifecycle

Recommended Cloud Run scaling for demo consistency:
- Min instances: `1`
- Max instances: `1`

### Production mode: Cloud SQL PostgreSQL
Set env vars on Cloud Run service:
```bash
gcloud run services update activity-audit \
  --region <REGION> \
  --set-env-vars \
JDBC_DATABASE_URL="jdbc:postgresql:///<DB_NAME>?cloudSqlInstance=<PROJECT_ID>:<REGION>:<INSTANCE>&socketFactory=com.google.cloud.sql.postgres.SocketFactory",\
JDBC_DRIVER="org.postgresql.Driver",\
JDBC_USERNAME="<DB_USER>",\
JDBC_PASSWORD="<DB_PASSWORD>",\
JPA_DDL_AUTO="update"
```

Note:
- PostgreSQL/socket-factory dependencies are not included by default in this lightweight hackathon build.

## Security Notes

- H2 console is disabled by default in Cloud Run profile.
- Optional demo admin protection:
  - Set env var `DEMO_TOKEN`
  - Send `X-Demo-Token` when calling `/api/demo/admin/reset` or `/api/demo/admin/generate`

## WebSocket Notes

- Cloud Run supports WebSockets.
- Real-time audit feed uses `/ws-audit` and `/topic/audit-logs`.
- For demos, keep low scale variance to reduce long-lived connection churn.

