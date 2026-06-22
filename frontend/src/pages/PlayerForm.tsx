import { useEffect, useState } from "react";
import type { FormEvent, ReactNode } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api/axios";
import type { Team } from "../types/club";
import type {
  CountryCode,
  PlayerPositionType,
  ReleaseClausePolicy,
  ReleaseClauseRule,
} from "../types/player";
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
import {
  createPositionRatings,
  positionOptions,
} from "../utils/playerPositions";
import { nationalityOptions } from "../utils/nationalityOptions";
import {
  ethnicityOptions,
  hairColorOptions,
  hairLengthOptions,
  skinToneOptions,
} from "../utils/playerAppearanceOptions";
import { getApiErrorMessage } from "../utils/errorUtils";

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
  const [nationality, setNationality] =
    useState<CountryCode>("ENGLAND");
  const [secondaryNationalities, setSecondaryNationalities] =
    useState<CountryCode[]>([]);
  const [heightCm, setHeightCm] = useState(180);
  const [weightKg, setWeightKg] = useState(75);
  const [race, setRace] = useState("UNKNOWN");
  const [hairColor, setHairColor] = useState("BLACK");
  const [hairLength, setHairLength] = useState("SHORT");
  const [skinTone, setSkinTone] = useState("FLESH");
  const [positionRatings, setPositionRatings] = useState<
    Record<PlayerPositionType, number>
  >(() => createPositionRatings("STRIKER"));
  const [attributes, setAttributes] =
    useState<AttributeMap>(defaultAttributes);
  const [contractStartDate, setContractStartDate] = useState("2026-07-01");
  const [contractEndDate, setContractEndDate] = useState("2029-06-30");
  const [contractType, setContractType] = useState("FULL_TIME");
  const [wageAmount, setWageAmount] = useState(1000);
  const [squadNumber, setSquadNumber] = useState("");
  const [releaseClauseAmount, setReleaseClauseAmount] = useState("");
  const [releaseClauseRule, setReleaseClauseRule] =
    useState<ReleaseClauseRule>("OPTIONAL");
  const [potentialMode, setPotentialMode] =
    useState<PotentialMode>("FIXED");
  const [negativePotentialLevel, setNegativePotentialLevel] = useState("-8");

  const isGoalkeeper = positionRatings.GOALKEEPER === 20;

  useEffect(() => {
    async function loadTeams() {
      try {
        const [teamsResponse, policyResponse] = await Promise.all([
          api.get<Team[]>("/teams"),
          api.get<ReleaseClausePolicy>("/contracts/release-clause-policy"),
        ]);
        setTeams(teamsResponse.data);
        setTeamId(teamsResponse.data[0]?.id ?? "");
        setReleaseClauseRule(policyResponse.data.rule);
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

  function updatePositionRating(
    position: PlayerPositionType,
    value: number
  ) {
    const nextIsGoalkeeper = position === "GOALKEEPER" && value === 20;
    const becomesOutfield = position !== "GOALKEEPER" && value === 20;

    if (nextIsGoalkeeper) {
      updateGoalkeepingAttributes(true);
    } else if (becomesOutfield || position === "GOALKEEPER") {
      updateGoalkeepingAttributes(false);
    }

    setPositionRatings((current) => {
      const next = { ...current, [position]: value };

      if (nextIsGoalkeeper) {
        positionOptions.forEach((option) => {
          if (option.value !== "GOALKEEPER") {
            next[option.value] = 1;
          }
        });
      }

      if (becomesOutfield) {
        next.GOALKEEPER = 1;
      }

      return next;
    });
  }

  function updateAttribute(key: string, value: number) {
    setAttributes((current) => ({
      ...current,
      [key]: value,
    }));
  }

  function toggleSecondaryNationality(country: CountryCode) {
    setSecondaryNationalities((current) => {
      if (current.includes(country)) {
        return current.filter((item) => item !== country);
      }
      if (country === nationality) {
        return current;
      }
      return [...current, country];
    });
  }

  async function handleSubmit(event: FormEvent) {
    event.preventDefault();
    setError("");

    if (teamId === "") {
      setError("Choose a team before creating the player.");
      return;
    }

    if (
      releaseClauseRule === "REQUIRED"
      && (!releaseClauseAmount || Number(releaseClauseAmount) <= 0)
    ) {
      setError("Release clause is required for this club country.");
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
        race,
        hairColor,
        hairLength,
        skinTone,
        heightCm,
        weightKg,
        dateOfBirth,
        birthCity,
        birthCountry: nationality,
        nationality,
        secondaryNationalities,
        languages: {
          ENGLISH: 10,
        },
        positions: positionRatings,
        attributes: finalAttributes,
        potentialMode,
        negativePotentialLevel:
          potentialMode === "NEGATIVE" ? negativePotentialLevel : null,
        estimatedValueInGbp: null,
        contractStartDate,
        contractEndDate,
        contractType,
        wageAmount,
        wageCurrency: "GBP",
        wageDisplayPeriod: "WEEKLY",
        squadNumber: squadNumber ? Number(squadNumber) : null,
        releaseClauseAmount:
          releaseClauseRule === "FORBIDDEN"
            ? null
            : releaseClauseAmount
              ? Number(releaseClauseAmount)
              : null,
        releaseClauseCurrency:
          releaseClauseRule === "FORBIDDEN"
            ? null
            : releaseClauseAmount
              ? "GBP"
              : null,
      });

      navigate(`/players/${response.data.id}`);
    } catch (requestError) {
      setError(getApiErrorMessage(
        requestError,
        "Failed to create player. Check required fields and attribute ranges."
      ));
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
              onChange={(event) => {
                const nextNationality = event.target.value as CountryCode;
                setNationality(nextNationality);
                setSecondaryNationalities((current) =>
                  current.filter((item) => item !== nextNationality)
                );
              }}
            >
              {nationalityOptions.map((option) => (
                <option key={option.value} value={option.value}>
                  {option.label}
                </option>
              ))}
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
          <Field label="Ethnicity">
            <select value={race} onChange={(event) => setRace(event.target.value)}>
              {ethnicityOptions.map((option) => (
                <option key={option.value} value={option.value}>
                  {option.label}
                </option>
              ))}
            </select>
          </Field>
          <Field label="Hair Color">
            <select
              value={hairColor}
              onChange={(event) => setHairColor(event.target.value)}
            >
              {hairColorOptions.map((option) => (
                <option key={option.value} value={option.value}>
                  {option.label}
                </option>
              ))}
            </select>
          </Field>
          <Field label="Hair Length">
            <select
              value={hairLength}
              onChange={(event) => setHairLength(event.target.value)}
            >
              {hairLengthOptions.map((option) => (
                <option key={option.value} value={option.value}>
                  {option.label}
                </option>
              ))}
            </select>
          </Field>
          <Field label="Skin Color">
            <select
              value={skinTone}
              onChange={(event) => setSkinTone(event.target.value)}
            >
              {skinToneOptions.map((option) => (
                <option key={option.value} value={option.value}>
                  {option.label}
                </option>
              ))}
            </select>
          </Field>
        </FormSection>

        <FormSection title="Secondary Nationalities">
          <div className="checkbox-grid">
            {nationalityOptions
              .filter((option) => option.value !== nationality)
              .map((option) => (
                <label key={option.value} className="checkbox-row">
                  <input
                    type="checkbox"
                    checked={secondaryNationalities.includes(option.value)}
                    onChange={() => toggleSecondaryNationality(option.value)}
                  />
                  <span>{option.label}</span>
                </label>
              ))}
          </div>
        </FormSection>

        <FormSection title="Team and Positions">
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
          <div className="form-help">
            {isGoalkeeper
              ? "Goalkeepers are locked to Goalkeeper = 20 and all outfield positions = 1."
              : "Outfield players may have multiple natural positions rated 20."}
          </div>

          <div className="position-editor-grid">
            {positionOptions.map((position) => {
              const locked =
                isGoalkeeper && position.value !== "GOALKEEPER";

              return (
                <label key={position.value} className="position-editor-row">
                  <span>{position.label}</span>
                  <input
                    required
                    type="number"
                    min={1}
                    max={20}
                    value={positionRatings[position.value]}
                    disabled={locked}
                    onChange={(event) =>
                      updatePositionRating(
                        position.value,
                        Number(event.target.value)
                      )
                    }
                  />
                </label>
              );
            })}
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
              onChange={(event) => setSquadNumber(event.target.value)}
              placeholder="Optional"
            />
          </Field>
          <div className="form-help">
            Market value is calculated automatically from the configured value
            bands, age, CA, PA, and world reputation.
          </div>
          {releaseClauseRule !== "FORBIDDEN" && (
            <Field
              label={
                releaseClauseRule === "REQUIRED"
                  ? "Release Clause GBP Required"
                  : "Release Clause GBP Optional"
              }
            >
              <input
                min={0}
                type="number"
                value={releaseClauseAmount}
                onChange={(event) => setReleaseClauseAmount(event.target.value)}
                placeholder={
                  releaseClauseRule === "REQUIRED" ? "Required" : "Optional"
                }
              />
            </Field>
          )}
          {releaseClauseRule === "FORBIDDEN" && (
            <div className="form-help">
              Release clauses are forbidden for clubs in this country.
            </div>
          )}
          {releaseClauseRule === "REQUIRED" && (
            <div className="form-help">
              Release clauses are required for clubs in this country.
            </div>
          )}
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
