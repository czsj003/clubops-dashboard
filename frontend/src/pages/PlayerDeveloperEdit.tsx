import { useEffect, useState } from "react";
import type { FormEvent } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";
import api from "../api/axios";
import { useAuth } from "../context/AuthContext";
import type {
  CountryCode,
  PlayerDetail,
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
import { getApiErrorMessage } from "../utils/errorUtils";

type AttributeMap = Record<string, number>;

function PlayerDeveloperEdit() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { user } = useAuth();
  const [player, setPlayer] = useState<PlayerDetail | null>(null);
  const [attributes, setAttributes] = useState<AttributeMap>({});
  const [positionRatings, setPositionRatings] = useState<
    Record<PlayerPositionType, number>
  >(() => createPositionRatings());
  const [secondaryNationalities, setSecondaryNationalities] =
    useState<CountryCode[]>([]);
  const [releaseClauseRule, setReleaseClauseRule] =
    useState<ReleaseClauseRule>("OPTIONAL");
  const [error, setError] = useState("");

  useEffect(() => {
    async function loadPlayer() {
      if (user?.accountType !== "VIP") {
        return;
      }

      try {
        const [response, policyResponse] = await Promise.all([
          api.get<PlayerDetail>(`/players/${id}`),
          api.get<ReleaseClausePolicy>("/contracts/release-clause-policy"),
        ]);
        setPlayer(response.data);
        setReleaseClauseRule(policyResponse.data.rule);
        setSecondaryNationalities(
          response.data.secondaryNationalities.map((item) => item.countryCode)
        );

        const loadedPositions = createPositionRatings();
        response.data.positions.forEach((position) => {
          loadedPositions[position.positionType] = position.rating;
        });
        setPositionRatings(loadedPositions);

        const flatAttributes: AttributeMap = {
          ...response.data.attributes.ability,
          ...response.data.attributes.reputation,
          ...response.data.attributes.feet,
          ...response.data.attributes.personal,
          ...response.data.attributes.mental,
          ...response.data.attributes.physical,
          ...response.data.attributes.technical,
          ...response.data.attributes.goalkeeping,
        };

        if (!response.data.isGoalkeeper) {
          goalkeepingFields.forEach((field) => {
            flatAttributes[field] = 0;
          });
        }

        setAttributes(flatAttributes);
      } catch {
        setError("Failed to load player developer data.");
      }
    }

    if (id) {
      loadPlayer();
    }
  }, [id, user?.accountType]);

  function updateAttribute(key: string, value: number) {
    setAttributes((current) => ({
      ...current,
      [key]: value,
    }));
  }

  function updatePositionRating(
    position: PlayerPositionType,
    value: number
  ) {
    if (player?.isGoalkeeper) {
      return;
    }

    const nextIsGoalkeeper = position === "GOALKEEPER" && value === 20;
    const becomesOutfield = position !== "GOALKEEPER" && value === 20;

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

    if (nextIsGoalkeeper) {
      setAttributes((current) => {
        const next = { ...current };
        goalkeepingFields.forEach((field) => {
          next[field] = next[field] > 0 ? next[field] : 10;
        });
        return next;
      });
    } else if (becomesOutfield || position === "GOALKEEPER") {
      setAttributes((current) => {
        const next = { ...current };
        goalkeepingFields.forEach((field) => {
          next[field] = 0;
        });
        return next;
      });
    }
  }

  function toggleSecondaryNationality(country: CountryCode) {
    setSecondaryNationalities((current) => {
      if (current.includes(country)) {
        return current.filter((item) => item !== country);
      }
      if (player && country === player.nationality) {
        return current;
      }
      return [...current, country];
    });
  }

  async function handleSubmit(event: FormEvent) {
    event.preventDefault();
    setError("");

    if (!player || !player.contract) {
      setError("Player contract data is required before saving.");
      return;
    }

    const isEditingGoalkeeper = positionRatings.GOALKEEPER === 20;

    try {
      const finalAttributes = { ...attributes };

      if (!isEditingGoalkeeper) {
        goalkeepingFields.forEach((field) => {
          finalAttributes[field] = 0;
        });
      }

      const languages = Object.fromEntries(
        player.languages.map((language) => [
          language.languageCode,
          language.fluency,
        ])
      );

      await api.put(`/players/${player.id}`, {
        player: {
          teamId: player.teamId,
          firstName: player.firstName,
          lastName: player.lastName,
          commonName: player.commonName,
          fullName: player.fullName,
          race: player.race,
          hairColor: player.hairColor,
          hairLength: player.hairLength,
          skinTone: player.skinTone,
          heightCm: player.heightCm,
          weightKg: player.weightKg,
          dateOfBirth: player.dateOfBirth,
          birthCity: player.birthCity,
          birthCountry: player.birthCountry,
          nationality: player.nationality,
          secondaryNationalities,
          languages,
          positions: positionRatings,
          attributes: finalAttributes,
          estimatedValueInGbp: null,
          contractStartDate: player.contract.startDate,
          contractEndDate: player.contract.endDate,
          contractType: player.contract.contractType,
          wageAmount: player.contract.wageAmount,
          wageCurrency: player.contract.wageCurrency,
          wageDisplayPeriod: player.contract.wageDisplayPeriod,
          squadNumber: player.contract.squadNumber,
          releaseClauseAmount:
            releaseClauseRule === "FORBIDDEN"
              ? null
              : player.contract.releaseClauseAmount,
          releaseClauseCurrency:
            releaseClauseRule === "FORBIDDEN"
              ? null
              : player.contract.releaseClauseCurrency,
        },
      });

      navigate(`/players/${player.id}`);
    } catch (requestError) {
      setError(getApiErrorMessage(
        requestError,
        "Failed to update player. Check positions and attribute ranges."
      ));
    }
  }

  if (user?.accountType !== "VIP") {
    return (
      <main className="content-page">
        <div className="error-message">
          Developer mode is only available for VIP users.
        </div>
      </main>
    );
  }

  if (!player && !error) {
    return <main className="content-page">Loading developer editor...</main>;
  }

  if (!player) {
    return (
      <main className="content-page">
        <div className="error-message">{error}</div>
      </main>
    );
  }

  const isEditingGoalkeeper = positionRatings.GOALKEEPER === 20;

  return (
    <main className="content-page">
      <div className="form-page-header">
        <div>
          <p className="eyebrow">Developer Mode</p>
          <h1>Edit Hidden Attributes</h1>
          <p>
            {player.displayName} ·{" "}
            {player.isGoalkeeper
              ? "Goalkeeper profile"
              : "Outfield player profile"}
          </p>
        </div>

        <Link to={`/players/${player.id}`} className="new-player-button">
          Back to Player
        </Link>
      </div>

      <div className="developer-warning">
        Hidden attributes are only visible in developer mode. Normal coach UI
        should not show CA, PA, hidden personality attributes, or raw internal
        values.
      </div>

      {!isEditingGoalkeeper && (
        <div className="developer-warning subtle">
          This is an outfield player. Goalkeeping attributes are locked at 0
          and are not shown here.
        </div>
      )}

      {error && <div className="error-message">{error}</div>}

      <form className="player-form" onSubmit={handleSubmit}>
        <section className="form-section-card">
          <h2>Secondary Nationalities</h2>

          <div className="checkbox-grid">
            {nationalityOptions
              .filter((option) => option.value !== player.nationality)
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
        </section>

        <section className="form-section-card">
          <h2>Positions</h2>

          <div className="form-help">
            {player.isGoalkeeper
              ? "Existing goalkeepers are locked to Goalkeeper = 20."
              : "Outfield players may have multiple natural positions rated 20. Setting Goalkeeper to 20 locks every outfield position to 1."}
          </div>

          <div className="position-editor-grid">
            {positionOptions.map((position) => {
              const locked =
                player.isGoalkeeper
                || (isEditingGoalkeeper && position.value !== "GOALKEEPER");

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
        </section>

        <AttributeSection
          title="Ability and Reputation"
          fields={abilityFields}
          attributes={attributes}
          onChange={updateAttribute}
        />
        <AttributeSection
          title="Feet"
          fields={footFields}
          attributes={attributes}
          onChange={updateAttribute}
        />
        <AttributeSection
          title="Personal / Hidden Attributes"
          fields={personalFields}
          attributes={attributes}
          onChange={updateAttribute}
        />
        <AttributeSection
          title="Mental Attributes"
          fields={mentalFields}
          attributes={attributes}
          onChange={updateAttribute}
        />
        <AttributeSection
          title="Physical Attributes"
          fields={physicalFields}
          attributes={attributes}
          onChange={updateAttribute}
        />
        <AttributeSection
          title="Technical Attributes"
          fields={technicalFields}
          attributes={attributes}
          onChange={updateAttribute}
        />
        {isEditingGoalkeeper && (
          <AttributeSection
            title="Goalkeeping Attributes"
            fields={goalkeepingFields}
            attributes={attributes}
            onChange={updateAttribute}
          />
        )}

        <div className="form-actions">
          <button type="submit">Save Developer Changes</button>
        </div>
      </form>
    </main>
  );
}

function AttributeSection({
  title,
  fields,
  attributes,
  onChange,
}: {
  title: string;
  fields: string[];
  attributes: AttributeMap;
  onChange: (key: string, value: number) => void;
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
              min={1}
              max={getMaxForAttribute(field)}
              value={attributes[field] ?? 1}
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

export default PlayerDeveloperEdit;
