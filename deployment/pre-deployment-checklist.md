# Pre-Deployment Checklist

## Security

- [ ] Set a long random `JWT_SECRET`
- [ ] Set production database credentials
- [ ] Confirm local admin files are not tracked or deployed
- [ ] Confirm no `.env.local` or `.env.production` files are committed
- [ ] Confirm NORMAL users cannot update or delete players
- [ ] Confirm only VIP users can access developer mode

## Backend

- [ ] Set `SPRING_PROFILES_ACTIVE=production`
- [ ] Set `JPA_DDL_AUTO=update`
- [ ] Set `JPA_SHOW_SQL=false`
- [ ] Set the deployed frontend URL in `CORS_ALLOWED_ORIGINS`
- [ ] Run the backend test suite
- [ ] Build and start the packaged JAR

## Frontend

- [ ] Set `VITE_API_BASE_URL` before building
- [ ] Run lint
- [ ] Run the production build
- [ ] Test the production preview
- [ ] Verify login, registration, squad, players, contracts, and finance

## Data

- [ ] Create the demo user
- [ ] Create a club with first and youth teams
- [ ] Add representative player and contract scenarios
- [ ] Add country value bands required by the demo
- [ ] Verify England and China value calculations

## Repository

- [ ] Confirm Git status is clean
- [ ] Confirm no credentials are tracked
- [ ] Confirm deployment documentation is current
