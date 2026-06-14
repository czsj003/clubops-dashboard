# ClubOps Dashboard ERD Draft

## Entities

### users

- id
- name
- email
- password_hash
- created_at
- updated_at

### clubs

- id
- user_id
- name
- country
- league
- season
- reputation
- created_at
- updated_at

### teams

- id
- club_id
- name
- type
- created_at
- updated_at

### players

- id
- club_id
- team_id
- first_name
- last_name
- date_of_birth
- nationality
- primary_position
- secondary_positions
- preferred_foot
- height_cm
- weight_kg
- overall_rating
- potential_rating
- status
- created_at
- updated_at

### player_attributes

- id
- player_id
- finishing
- passing
- dribbling
- crossing
- first_touch
- tackling
- vision
- decisions
- composure
- work_rate
- leadership
- positioning
- pace
- acceleration
- stamina
- strength
- agility
- balance
- handling
- reflexes
- kicking
- one_on_ones
- created_at
- updated_at

### staff

- id
- club_id
- first_name
- last_name
- role
- department
- date_of_birth
- nationality
- created_at
- updated_at

### staff_attributes

- id
- staff_id
- coaching
- tactical
- technical
- mental
- fitness
- judging_ability
- judging_potential
- motivation
- discipline
- created_at
- updated_at

### contracts

- id
- club_id
- person_type
- person_id
- wage_per_week
- start_date
- end_date
- release_clause
- squad_status
- created_at
- updated_at

### finances

- id
- club_id
- balance
- transfer_budget
- wage_budget
- monthly_revenue
- monthly_expenses
- created_at
- updated_at

### facilities

- id
- club_id
- stadium_name
- stadium_capacity
- training_facility
- youth_facility
- youth_recruitment
- medical_center
- data_analysis
- scouting_network
- created_at
- updated_at

## Relationships

- User 1 - 1 Club
- Club 1 - N Team
- Club 1 - N Player
- Club 1 - N Staff
- Club 1 - N Contract
- Club 1 - 1 Finance
- Club 1 - 1 Facility
- Team 1 - N Player
- Player 1 - 1 PlayerAttribute
- Staff 1 - 1 StaffAttribute
- Contract belongs logically to either Player or Staff through person_type and person_id