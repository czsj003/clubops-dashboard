import { useEffect, useState } from "react";
import type { FormEvent } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";
import api from "../api/axios";
import type { PlayerDetail } from "../types/player";
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

function PlayerDeveloperEdit() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [player, setPlayer] = useState<PlayerDetail | null>(null);
  const [attributes, setAttributes] = useState<AttributeMap>({});
  const [error, setError] = useState("");

  useEffect(() => {
    async function loadPlayer() {
      try {
        const response = await api.get<PlayerDetail>(`/players/${id}`);
        setPlayer(response.data);

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
  }, [id]);

  function updateAttribute(key: string, value: number) {
    setAttributes((current) => ({
      ...current,
      [key]: value,
    }));
  }

  async function handleSubmit(event: FormEvent) {
    event.preventDefault();
    setError("");

    if (!player || !player.contract) {
      setError("Player contract data is required before saving.");
      return;
    }

    try {
      const finalAttributes = { ...attributes };

      if (!player.isGoalkeeper) {
        goalkeepingFields.forEach((field) => {
          finalAttributes[field] = 0;
        });
      }

      const positions = Object.fromEntries(
        player.positions.map((position) => [
          position.positionType,
          position.rating,
        ])
      );

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
          secondaryNationalities: player.secondaryNationalities.map(
            (item) => item.countryCode
          ),
          languages,
          positions,
          attributes: finalAttributes,
          estimatedValueInGbp: player.value?.estimatedValueInGbp ?? 0,
          contractStartDate: player.contract.startDate,
          contractEndDate: player.contract.endDate,
          contractType: player.contract.contractType,
          wageAmount: player.contract.wageAmount,
          wageCurrency: player.contract.wageCurrency,
          wageDisplayPeriod: player.contract.wageDisplayPeriod,
          squadNumber: player.contract.squadNumber,
          releaseClauseAmount: player.contract.releaseClauseAmount,
          releaseClauseCurrency: player.contract.releaseClauseCurrency,
        },
      });

      navigate(`/players/${player.id}`);
    } catch {
      setError(
        "Failed to update player. Outfield goalkeeper attributes must stay 0; editable attributes must be at least 1."
      );
    }
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

      {!player.isGoalkeeper && (
        <div className="developer-warning subtle">
          This is an outfield player. Goalkeeping attributes are locked at 0
          and are not shown here.
        </div>
      )}

      {error && <div className="error-message">{error}</div>}

      <form className="player-form" onSubmit={handleSubmit}>
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
        {player.isGoalkeeper && (
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
