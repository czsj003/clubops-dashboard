import type { PlayerDetail } from "../../types/player";

const physicalOrder = [
  "acceleration",
  "agility",
  "balance",
  "jumpingReach",
  "naturalFitness",
  "pace",
  "stamina",
  "strength",
];

const technicalOrder = [
  "freeKicks",
  "penaltyTaking",
  "technique",
];

function GoalkeeperSidePanel({ player }: { player: PlayerDetail }) {
  const outfieldPositions = player.positions.filter(
    (position) => position.positionType !== "GOALKEEPER"
  );
  const highestOutfieldRating = outfieldPositions.length
    ? Math.max(...outfieldPositions.map((position) => position.rating))
    : 1;
  const outfieldRating = Math.max(1, Math.round(highestOutfieldRating / 2));

  return (
    <section className="fm-panel goalkeeper-side-panel">
      <h3>Physical</h3>
      <AttributeList
        keys={physicalOrder}
        attributes={player.attributes.physical}
      />

      <div className="attribute-subtitle">Technical</div>
      <AttributeList
        keys={technicalOrder}
        attributes={player.attributes.technical}
      />

      <div className="attribute-subtitle">Outfield</div>
      <div className="outfield-rating-row">
        <span>Outfield Rating</span>
        <strong>{outfieldRating} / 10</strong>
      </div>
    </section>
  );
}

function AttributeList({
  keys,
  attributes,
}: {
  keys: string[];
  attributes: Record<string, number>;
}) {
  return (
    <div className="attribute-list">
      {keys.map((key) => (
        <div key={key} className="attribute-row">
          <span>{formatAttributeLabel(key)}</span>
          <strong className={getAttributeClass(attributes[key])}>
            {attributes[key]}
          </strong>
        </div>
      ))}
    </div>
  );
}

function formatAttributeLabel(key: string) {
  const labels: Record<string, string> = {
    jumpingReach: "Jumping Reach",
    naturalFitness: "Natural Fitness",
    freeKicks: "Free Kick Taking",
    penaltyTaking: "Penalty Taking",
  };

  return labels[key]
    ?? key.replace(/([A-Z])/g, " $1").replace(/^./, (char) => char.toUpperCase());
}

function getAttributeClass(value: number) {
  if (value >= 16) return "attr-elite";
  if (value >= 13) return "attr-good";
  if (value >= 10) return "attr-average";
  if (value >= 6) return "attr-low";
  return "attr-poor";
}

export default GoalkeeperSidePanel;
