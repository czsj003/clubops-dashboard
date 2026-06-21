import type { PlayerPosition, PlayerPositionType } from "../../types/player";

interface MiniPitchProps {
    positions: PlayerPosition[];
    isGoalkeeper: boolean;
}

const coordinateMap: Record<PlayerPositionType, { x: number; y: number }> = {
    GOALKEEPER: { x: 8, y: 50 },

    DEFENDER_LEFT: { x: 25, y: 18 },
    DEFENDER_CENTRAL: { x: 25, y: 50 },
    DEFENDER_RIGHT: { x: 25, y: 82 },

    WING_BACK_LEFT: { x: 38, y: 12 },
    WING_BACK_RIGHT: { x: 38, y: 88 },

    DEFENSIVE_MIDFIELDER: { x: 42, y: 50 },

    MIDFIELDER_LEFT: { x: 56, y: 20 },
    MIDFIELDER_CENTRAL: { x: 56, y: 50 },
    MIDFIELDER_RIGHT: { x: 56, y: 80 },

    ATTACKING_MIDFIELDER_LEFT: { x: 73, y: 20 },
    ATTACKING_MIDFIELDER_CENTRAL: { x: 73, y: 50 },
    ATTACKING_MIDFIELDER_RIGHT: { x: 73, y: 80 },

    STRIKER: { x: 90, y: 50 },
};

function MiniPitch({ positions }: MiniPitchProps) {
    return (
        <div className="mini-pitch">
            <div className="pitch-line center-line" />
            <div className="pitch-circle" />

            {Object.entries(coordinateMap).map(([positionType, coordinates]) => {
                const position = positions.find((p) => p.positionType === positionType);
                const rating = position?.rating ?? 0;

                return (
                    <span
                        key={positionType}
                        className={`pitch-dot ${getDotClass(rating)}`}
                        style={{
                            left: `${coordinates.x}%`,
                            top: `${coordinates.y}%`,
                        }}
                        title={`${positionType}: ${rating || "N/A"}`}
                    />
                );
            })}
        </div>
    );
}

function getDotClass(rating: number) {
    if (rating === 20) return "natural";
    if (rating >= 15) return "strong";
    if (rating >= 10) return "ok";
    if (rating >= 2) return "weak";
    return "none";
}

export default MiniPitch;
