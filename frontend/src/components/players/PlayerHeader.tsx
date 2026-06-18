import type { CurrencyCode, PlayerDetail } from "../../types/player";
import {
  convertFromGbp,
  formatDateShort,
  formatMoney,
  formatPosition,
} from "../../utils/formatters";

interface PlayerHeaderProps {
  player: PlayerDetail;
  selectedCurrency: CurrencyCode;
  selectedRate: number;
  onOpenContract: () => void;
}

function PlayerHeader({
  player,
  selectedCurrency,
  selectedRate,
  onOpenContract,
}: PlayerHeaderProps) {
  const mainPositions = player.positions
    .filter((position) => position.rating === 20)
    .map((position) => formatPosition(position.positionType))
    .join(", ");

  const convertedValue = convertFromGbp(
    player.value?.estimatedValueInGbp,
    selectedRate
  );

  return (
    <section className="player-hero">
      <div className="player-silhouette" />

      <div className="player-hero-main">
        <div className="player-title-row">
          <h1>{player.displayName}</h1>

          {player.contract?.squadNumber && (
            <span className="squad-number-badge">
              {player.contract.squadNumber}
            </span>
          )}
        </div>

        <p>
          {mainPositions || "No Position"} · {player.age} years old (
          {formatDateShort(player.dateOfBirth)})
        </p>

        <div className="player-meta-row">
          <span>{player.nationality}</span>
          <span>{player.clubName}</span>
          <span>{player.teamName}</span>
        </div>
      </div>

      <div className="player-hero-card">
        <span>Value</span>
        <strong>{formatMoney(convertedValue, selectedCurrency)}</strong>
      </div>

      <button className="player-hero-card wage-card" onClick={onOpenContract}>
        <span>Wage</span>
        <strong>
          {player.contract
            ? `${formatMoney(
              convertFromGbp(player.contract.wageAmount, selectedRate),
              selectedCurrency
            )} p/w`
            : "N/A"}
        </strong>
      </button>
    </section>
  );
}

export default PlayerHeader;