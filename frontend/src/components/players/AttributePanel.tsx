interface AttributePanelProps {
  title: string;
  attributes: Record<string, number>;
  keys?: string[];
  secondaryTitle?: string;
  secondaryAttributes?: Record<string, number>;
  secondaryKeys?: string[];
}

function AttributePanel({
  title,
  attributes,
  keys,
  secondaryTitle,
  secondaryAttributes,
  secondaryKeys,
}: AttributePanelProps) {
  return (
    <section className="fm-panel attribute-panel">
      <h3>{title}</h3>

      <div className="attribute-list">
        {buildEntries(attributes, keys).map(([key, value]) => (
          <AttributeRow key={key} label={formatAttributeLabel(key)} value={value} />
        ))}
      </div>

      {secondaryAttributes && secondaryKeys && (
        <>
          <div className="attribute-subtitle">{secondaryTitle}</div>

          <div className="attribute-list">
            {buildEntries(secondaryAttributes, secondaryKeys).map(([key, value]) => (
              <AttributeRow
                key={key}
                label={formatAttributeLabel(key)}
                value={value}
              />
            ))}
          </div>
        </>
      )}
    </section>
  );
}

function AttributeRow({ label, value }: { label: string; value: number }) {
  return (
    <div className="attribute-row">
      <span>{label}</span>
      <strong className={getAttributeClass(value)}>{value}</strong>
    </div>
  );
}

function buildEntries(attributes: Record<string, number>, keys?: string[]) {
  if (!keys) {
    return Object.entries(attributes);
  }

  return keys
    .filter((key) => attributes[key] !== undefined)
    .map((key) => [key, attributes[key]] as [string, number]);
}

function formatAttributeLabel(key: string) {
  const labels: Record<string, string> = {
    firstTouch: "First Touch",
    longShots: "Long Shots",
    freeKicks: "Free Kick Taking",
    longThrows: "Long Throws",
    penaltyTaking: "Penalty Taking",
    jumpingReach: "Jumping Reach",
    naturalFitness: "Natural Fitness",
    teamWork: "Teamwork",
    workRate: "Work Rate",
    aerialAbility: "Aerial Reach",
    commandOfArea: "Command Of Area",
    oneOnOnes: "One On Ones",
    rushingOut: "Rushing Out",
    tendencyToPunch: "Punching (Tendency)",
  };

  return (
    labels[key] ??
    key.replace(/([A-Z])/g, " $1").replace(/^./, (char) => char.toUpperCase())
  );
}

function getAttributeClass(value: number) {
  if (value >= 16) return "attr-elite";
  if (value >= 13) return "attr-good";
  if (value >= 10) return "attr-average";
  if (value >= 6) return "attr-low";
  return "attr-poor";
}

export default AttributePanel;