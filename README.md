# Profile App (Release 2 and 3)

## Accounts (instructor only)
- Owner A (profile p01): username `ownerA`
- Owner B (profile p02): username `ownerB`
- Passwords are supplied privately via Moodle.

## Run locally
Set these environment variables before running:
- `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`
- `OWNER_A_PASSWORD`, `OWNER_B_PASSWORD`

Then:

.\mvnw.cmd spring-boot:run


## Deployment
Deployed on Render (free tier). The first request after 15 minutes of inactivity may take ~1 minute to wake up. The build is redeployed from GitHub; data persists in Neon PostgreSQL across redeploys.