import type { PlayerPosition } from "../../types/player";
import { formatPosition } from "../../utils/formatters";
import MiniPitch from "./MiniPitch";

interface PositionPanelProps {
    positions: PlayerPosition[];
    isGoalkeeper: boolean;
}

function PositionPanel({ positions, isGoalkeeper }: PositionPanelProps) {
    const sortedPositions = [...positions].sort((a, b) => b.rating - a.rating);

    const naturalPositions = sortedPositions
        .filter((position) => position.rating === 20)
        .map((position) => formatPosition(position.positionType))
        .join(", ");

    return (
        <section className="fm-panel position-panel">
            <h3>Positions</h3>

            <div className="position-tags">
                {sortedPositions.map((position) => (
                    <span key={position.id}>
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

export default PositionPanel;