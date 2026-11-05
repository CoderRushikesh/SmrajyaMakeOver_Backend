s

## Deployment (Render + Supabase/Neon, free tier)

Required environment variables (see `.env.example`):
`DB_URL`, `DB_USER`, `DB_PASSWORD`, `JWT_SECRET`, `CORS_ALLOWED_ORIGINS`

- Database: PostgreSQL (Supabase / Neon)
- Build: Docker (`Dockerfile` in root)
- Health check path: `/health`
- Public endpoints: `POST /auth/register`, `POST /auth/login`
