import { useEffect, useMemo, useState } from "react";
import type { FormEvent, ReactNode } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api/axios";
import type { Team } from "../types/club";
import type { PlayerPositionType } from "../types/player";
import {
  abilityFields,
  footFields,
  formatAttributeName,
  getMaxForAttribute,
  goalkeepingFields,
  mentalFields,
  personalFields,
  physicalFields,
  technicalFields,
} from "../utils/playerAttributeFields";

type AttributeMap = Record<string, number>;

const defaultAttributes: AttributeMap = {
  currentAbility: 100,
  potentialAbility: 140,
  currentReputation: 50,
  homeReputation: 50,
  worldReputation: 50,
  leftFoot: 10,
  rightFoot: 20,
  adaptability: 10,
  ambition: 10,
  controversy: 5,
  loyalty: 10,
  pressure: 10,
  professionalism: 10,
  sportsmanship: 10,
  temperament: 10,
  aggression: 10,
  anticipation: 10,
  bravery: 10,
  composure: 10,
  concentration: 10,
  consistency: 10,
  decisions: 10,
  determination: 10,
  dirtiness: 5,
  flair: 10,
  importantMatches: 10,
  leadership: 10,
  movement: 10,
  positioning: 10,
  teamWork: 10,
  vision: 10,
  workRate: 10,
  acceleration: 10,
  agility: 10,
  balance: 10,
  injuryProneness: 5,
  jumpingReach: 10,
  naturalFitness: 10,
  pace: 10,
  stamina: 10,
  strength: 10,
  corners: 10,
  crossing: 10,
  dribbling: 10,
  finishing: 10,
  firstTouch: 10,
  freeKicks: 10,
  heading: 10,
  longShots: 10,
  longThrows: 10,
  marking: 10,
  passing: 10,
  penaltyTaking: 10,
  tackling: 10,
  technique: 10,
  versatility: 10,
  aerialAbility: 0,
  commandOfArea: 0,
  communication: 0,
  eccentricity: 0,
  handling: 0,
  kicking: 0,
  oneOnOnes: 0,
  reflexes: 0,
  rushingOut: 0,
  tendencyToPunch: 0,
  throwing: 0,
};

const positionOptions: { value: PlayerPositionType; label: string }[] = [
  { value: "GOALKEEPER", label: "Goalkeeper" },
  { value: "DEFENDER_LEFT", label: "Defender Left" },
  { value: "DEFENDER_CENTRAL", label: "Defender Central" },
  { value: "DEFENDER_RIGHT", label: "Defender Right" },
  { value: "DEFENSIVE_MIDFIELDER", label: "Defensive Midfielder" },
  { value: "WING_BACK_LEFT", label: "Wing-Back Left" },
  { value: "WING_BACK_RIGHT", label: "Wing-Back Right" },
  { value: "MIDFIELDER_LEFT", label: "Midfielder Left" },
  { value: "MIDFIELDER_CENTRAL", label: "Midfielder Central" },
  { value: "MIDFIELDER_RIGHT", label: "Midfielder Right" },
  {
    value: "ATTACKING_MIDFIELDER_LEFT",
    label: "Attacking Midfielder Left",
  },
  {
    value: "ATTACKING_MIDFIELDER_CENTRAL",
    label: "Attacking Midfielder Central",
  },
  {
    value: "ATTACKING_MIDFIELDER_RIGHT",
    label: "Attacking Midfielder Right",
  },
  { value: "STRIKER", label: "Striker" },
];

const negativePotentialOptions = [
  "-10",
  "-9.5",
  "-9",
  "-8.5",
  "-8",
  "-7.5",
  "-7",
  "-6.5",
  "-6",
  "-5.5",
  "-5",
  "-4.5",
  "-4",
  "-3.5",
  "-3",
  "-2.5",
  "-2",
  "-1.5",
  "-1",
];

type PotentialMode = "FIXED" | "RANDOM" | "NEGATIVE";

function PlayerForm() {
  const navigate = useNavigate();
  const [teams, setTeams] = useState<Team[]>([]);
  const [error, setError] = useState("");
  const [teamId, setTeamId] = useState<number | "">("");
  const [firstName, setFirstName] = useState("New");
  const [lastName, setLastName] = useState("Player");
  const [commonName, setCommonName] = useState("");
  const [fullName, setFullName] = useState("New Player");
  const [dateOfBirth, setDateOfBirth] = useState("2004-01-01");
  const [birthCity, setBirthCity] = useState("London");
  const [nationality, setNationality] = useState("ENGLAND");
  const [heightCm, setHeightCm] = useState(180);
  const [weightKg, setWeightKg] = useState(75);
  const [mainPosition, setMainPosition] =
    useState<PlayerPositionType>("STRIKER");
  const [attributes, setAttributes] =
    useState<AttributeMap>(defaultAttributes);
  const [estimatedValueInGbp, setEstimatedValueInGbp] = useState(1_000_000);
  const [contractStartDate, setContractStartDate] = useState("2026-07-01");
  const [contractEndDate, setContractEndDate] = useState("2029-06-30");
  const [contractType, setContractType] = useState("FULL_TIME");
  const [wageAmount, setWageAmount] = useState(1000);
  const [squadNumber, setSquadNumber] = useState(30);
  const [releaseClauseAmount, setReleaseClauseAmount] = useState("");
  const [potentialMode, setPotentialMode] =
    useState<PotentialMode>("FIXED");
  const [negativePotentialLevel, setNegativePotentialLevel] = useState("-8");

  const isGoalkeeper = mainPosition === "GOALKEEPER";

  useEffect(() => {
    async function loadTeams() {
      try {
        const response = await api.get<Team[]>("/teams");
        setTeams(response.data);
        setTeamId(response.data[0]?.id ?? "");
      } catch {
        setError("Failed to load teams.");
      }
    }

    loadTeams();
  }, []);

  function updateGoalkeepingAttributes(nextIsGoalkeeper: boolean) {
    setAttributes((current) => {
      const updated = { ...current };

      goalkeepingFields.forEach((field) => {
        if (nextIsGoalkeeper) {
          if (!updated[field] || updated[field] === 0) {
            updated[field] = 10;
          }
        } else {
          updated[field] = 0;
        }
      });

      return updated;
    });
  }

  function updateFirstName(value: string) {
    setFirstName(value);
    setFullName(`${value} ${lastName}`.trim());
  }

  function updateLastName(value: string) {
    setLastName(value);
    setFullName(`${firstName} ${value}`.trim());
  }

  function updateMainPosition(value: PlayerPositionType) {
    setMainPosition(value);
    updateGoalkeepingAttributes(value === "GOALKEEPER");
  }

  const positions = useMemo<Record<string, number>>(
    () => ({ [mainPosition]: 20 }),
    [mainPosition]
  );

  function updateAttribute(key: string, value: number) {
    setAttributes((current) => ({
      ...current,
      [key]: value,
    }));
  }

  async function handleSubmit(event: FormEvent) {
    event.preventDefault();
    setError("");

    if (teamId === "") {
      setError("Choose a team before creating the player.");
      return;
    }

    try {
      const finalAttributes = { ...attributes };

      if (!isGoalkeeper) {
        goalkeepingFields.forEach((field) => {
          finalAttributes[field] = 0;
        });
      }

      const response = await api.post("/players", {
        teamId,
        firstName,
        lastName,
        commonName: commonName || null,
        fullName,
        race: "UNKNOWN",
        hairColor: "UNKNOWN",
        hairLength: "UNKNOWN",
        skinTone: "UNKNOWN",
        heightCm,
        weightKg,
        dateOfBirth,
        birthCity,
        birthCountry: nationality,
        nationality,
        secondaryNationalities: [],
        languages: {
          ENGLISH: 10,
        },
        positions,
        attributes: finalAttributes,
        potentialMode,
        negativePotentialLevel:
          potentialMode === "NEGATIVE" ? negativePotentialLevel : null,
        estimatedValueInGbp,
        contractStartDate,
        contractEndDate,
        contractType,
        wageAmount,
        wageCurrency: "GBP",
        wageDisplayPeriod: "WEEKLY",
        squadNumber,
        releaseClauseAmount: releaseClauseAmount
          ? Number(releaseClauseAmount)
          : null,
        releaseClauseCurrency: releaseClauseAmount ? "GBP" : null,
      });

      navigate(`/players/${response.data.id}`);
    } catch {
      setError(
        "Failed to create player. Check required fields and attribute ranges."
      );
    }
  }

  return (
    <main className="content-page">
      <div className="form-page-header">
        <div>
          <p className="eyebrow">Coach Tool</p>
          <h1>Create New Player</h1>
          <p>
            Create a player with personal information, attributes, position,
            and contract.
          </p>
        </div>
      </div>

      {error && <div className="error-message">{error}</div>}

      <form className="player-form" onSubmit={handleSubmit}>
        <FormSection title="Personal Information">
          <Field label="First Name">
            <input
              required
              value={firstName}
              onChange={(event) => updateFirstName(event.target.value)}
            />
          </Field>
          <Field label="Last Name">
            <input
              required
              value={lastName}
              onChange={(event) => updateLastName(event.target.value)}
            />
          </Field>
          <Field label="Common Name">
            <input
              value={commonName}
              onChange={(event) => setCommonName(event.target.value)}
            />
          </Field>
          <Field label="Full Name">
            <input
              required
              value={fullName}
              onChange={(event) => setFullName(event.target.value)}
            />
          </Field>
          <Field label="Date of Birth">
            <input
              required
              type="date"
              value={dateOfBirth}
              onChange={(event) => setDateOfBirth(event.target.value)}
            />
          </Field>
          <Field label="Birth City">
            <input
              required
              value={birthCity}
              onChange={(event) => setBirthCity(event.target.value)}
            />
          </Field>
          <Field label="Nationality">
            <select
              value={nationality}
              onChange={(event) => setNationality(event.target.value)}
            >
              <option value="ENGLAND">England</option>
              <option value="PORTUGAL">Portugal</option>
              <option value="FRANCE">France</option>
              <option value="SPAIN">Spain</option>
              <option value="GERMANY">Germany</option>
              <option value="ITALY">Italy</option>
              <option value="BRAZIL">Brazil</option>
              <option value="ARGENTINA">Argentina</option>
            </select>
          </Field>
          <Field label="Height cm">
            <input
              required
              min={1}
              type="number"
              value={heightCm}
              onChange={(event) => setHeightCm(Number(event.target.value))}
            />
          </Field>
          <Field label="Weight kg">
            <input
              required
              min={1}
              type="number"
              value={weightKg}
              onChange={(event) => setWeightKg(Number(event.target.value))}
            />
          </Field>
        </FormSection>

        <FormSection title="Team and Position">
          <Field label="Team">
            <select
              required
              value={teamId}
              onChange={(event) => setTeamId(Number(event.target.value))}
            >
              {teams.map((team) => (
                <option key={team.id} value={team.id}>
                  {team.name}
                </option>
              ))}
            </select>
          </Field>
          <Field label="Main Position">
            <select
              value={mainPosition}
              onChange={(event) =>
                updateMainPosition(event.target.value as PlayerPositionType)
              }
            >
              {positionOptions.map((position) => (
                <option key={position.value} value={position.value}>
                  {position.label}
                </option>
              ))}
            </select>
          </Field>
          <div className="form-help">
            {isGoalkeeper
              ? "Goalkeeper players are locked to Goalkeeper = 20."
              : "Outfield players cannot have Goalkeeper = 20. Goalkeeping attributes are set to 0."}
          </div>
        </FormSection>

        <FormSection title="Ability and Potential Setup">
          <Field label="Potential Mode">
            <select
              value={potentialMode}
              onChange={(event) =>
                setPotentialMode(event.target.value as PotentialMode)
              }
            >
              <option value="FIXED">Fixed PA</option>
              <option value="RANDOM">Random PA</option>
              <option value="NEGATIVE">Negative Potential</option>
            </select>
          </Field>

          {potentialMode === "NEGATIVE" && (
            <Field label="Negative Potential Level">
              <select
                value={negativePotentialLevel}
                onChange={(event) =>
                  setNegativePotentialLevel(event.target.value)
                }
              >
                {negativePotentialOptions.map((level) => (
                  <option key={level} value={level}>
                    {level}
                  </option>
                ))}
              </select>
            </Field>
          )}

          <div className="form-help">
            CA or PA can be set to 0 during creation. Zero means random.
            Negative potential is only allowed for players under 22 and its
            range must be able to produce PA greater than or equal to CA.
          </div>
        </FormSection>

        <AttributeSection
          title="Ability and Reputation"
          fields={abilityFields}
          attributes={attributes}
          onChange={updateAttribute}
          allowZero
        />
        <AttributeSection
          title="Feet"
          fields={footFields}
          attributes={attributes}
          onChange={updateAttribute}
          allowZero
        />
        <AttributeSection
          title="Personal Attributes"
          fields={personalFields}
          attributes={attributes}
          onChange={updateAttribute}
          allowZero
        />
        <AttributeSection
          title="Mental Attributes"
          fields={mentalFields}
          attributes={attributes}
          onChange={updateAttribute}
          allowZero
        />
        <AttributeSection
          title="Physical Attributes"
          fields={physicalFields}
          attributes={attributes}
          onChange={updateAttribute}
          allowZero
        />
        <AttributeSection
          title="Technical Attributes"
          fields={technicalFields}
          attributes={attributes}
          onChange={updateAttribute}
          allowZero
        />
        {isGoalkeeper && (
          <AttributeSection
            title="Goalkeeping Attributes"
            fields={goalkeepingFields}
            attributes={attributes}
            onChange={updateAttribute}
            allowZero
          />
        )}

        <FormSection title="Contract">
          <Field label="Contract Start">
            <input
              required
              type="date"
              value={contractStartDate}
              onChange={(event) => setContractStartDate(event.target.value)}
            />
          </Field>
          <Field label="Contract End">
            <input
              required
              type="date"
              value={contractEndDate}
              onChange={(event) => setContractEndDate(event.target.value)}
            />
          </Field>
          <Field label="Contract Type">
            <select
              value={contractType}
              onChange={(event) => setContractType(event.target.value)}
            >
              <option value="FULL_TIME">Full Time</option>
              <option value="PART_TIME">Part Time</option>
              <option value="AMATEUR">Amateur</option>
              <option value="YOUTH">Youth</option>
            </select>
          </Field>
          <Field label="Weekly Wage GBP">
            <input
              required
              min={0}
              type="number"
              value={wageAmount}
              onChange={(event) => setWageAmount(Number(event.target.value))}
            />
          </Field>
          <Field label="Squad Number">
            <input
              min={1}
              max={99}
              type="number"
              value={squadNumber}
              onChange={(event) => setSquadNumber(Number(event.target.value))}
            />
          </Field>
          <Field label="Estimated Value GBP">
            <input
              min={0}
              type="number"
              value={estimatedValueInGbp}
              onChange={(event) =>
                setEstimatedValueInGbp(Number(event.target.value))
              }
            />
          </Field>
          <Field label="Release Clause GBP">
            <input
              min={0}
              type="number"
              value={releaseClauseAmount}
              onChange={(event) => setReleaseClauseAmount(event.target.value)}
              placeholder="Optional"
            />
          </Field>
        </FormSection>

        <div className="form-actions">
          <button type="submit">Create Player</button>
        </div>
      </form>
    </main>
  );
}

function FormSection({
  title,
  children,
}: {
  title: string;
  children: ReactNode;
}) {
  return (
    <section className="form-section-card">
      <h2>{title}</h2>
      <div className="form-section-grid">{children}</div>
    </section>
  );
}

function Field({ label, children }: { label: string; children: ReactNode }) {
  return (
    <label className="form-field">
      <span>{label}</span>
      {children}
    </label>
  );
}

function AttributeSection({
  title,
  fields,
  attributes,
  onChange,
  allowZero = false,
}: {
  title: string;
  fields: string[];
  attributes: AttributeMap;
  onChange: (key: string, value: number) => void;
  allowZero?: boolean;
}) {
  return (
    <section className="form-section-card">
      <h2>{title}</h2>
      <div className="attribute-input-grid">
        {fields.map((field) => (
          <label key={field} className="attribute-input-row">
            <span>{formatAttributeName(field)}</span>
            <input
              required
              type="number"
              min={allowZero ? 0 : 1}
              max={getMaxForAttribute(field)}
              value={attributes[field]}
              onChange={(event) =>
                onChange(field, Number(event.target.value))
              }
            />
          </label>
        ))}
      </div>
    </section>
  );
}

export default PlayerForm;
