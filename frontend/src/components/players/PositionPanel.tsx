import type { PlayerPosition } from "../../types/player";
import { formatPosition } from "../../utils/formatters";
import MiniPitch from "./MiniPitch";

interface PositionPanelProps {
    positions: PlayerPosition[];
    isGoalkeeper: boolean;
}

function PositionPanel({ positions, isGoalkeeper }: PositionPanelProps) {
    const sortedPositions = [...positions].sort((a, b) => {
        if (b.rating !== a.rating) return b.rating - a.rating;
        return formatPosition(a.positionType).localeCompare(
            formatPosition(b.positionType)
        );
    });
    const visiblePositions = sortedPositions.filter(
        (position) => position.rating >= 12
    );

    const naturalPositions = sortedPositions
        .filter((position) => position.rating === 20)
        .map((position) => formatPosition(position.positionType))
        .join(" / ");

    return (
        <section className="fm-panel position-panel">
            <h3>Positions</h3>

            <div className="position-tags">
                {visiblePositions.map((position) => (
                    <span
                        key={position.id}
                        className={`position-rating-tag ${getPositionRatingClass(
                            position.rating
                        )}`}
                    >
                        {formatPosition(position.positionType)} {position.rating}
                    </span>
                ))}
            </div>

            <MiniPitch positions={positions} isGoalkeeper={isGoalkeeper} />

            <div className="position-name">
                {isGoalkeeper ? "Goalkeeper" : naturalPositions || "Natural Position"}
            </div>
        </section>
    );
}

function getPositionRatingClass(value: number) {
    if (value >= 18) return "pos-rating-green";
    if (value >= 15) return "pos-rating-light-green";
    if (value >= 12) return "pos-rating-light-yellow";
    if (value >= 9) return "pos-rating-orange";
    if (value >= 5) return "pos-rating-dark-orange";
    if (value >= 2) return "pos-rating-red";
    return "pos-rating-none";
}

export default PositionPanel;
