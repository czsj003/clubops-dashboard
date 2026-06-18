import type { PlayerDetail } from "../../types/player";
import {
    formatEnum,
    formatFootAbility,
    formatHeight,
    formatLanguageFluency,
    formatReputation,
    formatWeight,
} from "../../utils/formatters";

interface PlayerInfoPanelProps {
    player: PlayerDetail;
}

function PlayerInfoPanel({ player }: PlayerInfoPanelProps) {
    return (
        <section className="fm-panel info-panel">
            <h3>Info</h3>

            <div className="info-grid">
                <div>
                    <span>Height</span>
                    <strong>{formatHeight(player.heightCm)}</strong>
                </div>

                <div>
                    <span>Weight</span>
                    <strong>{formatWeight(player.weightKg)}</strong>
                </div>

                <div>
                    <span>Reputation</span>
                    <strong>
                        {formatReputation(player.attributes.reputation.worldReputation)}
                    </strong>
                </div>

                <div>
                    <span>Personality</span>
                    <strong>{formatEnum(player.personality)}</strong>
                </div>

                <div>
                    <span>Media Style</span>
                    <strong>{formatEnum(player.mediaHandlingStyle)}</strong>
                </div>

                <div>
                    <span>Birthplace</span>
                    <strong>
                        {player.birthCity}, {player.birthCountry}
                    </strong>
                </div>
            </div>

            <div className="feet-box">
                <div>
                    <span>Left Foot</span>
                    <strong>{formatFootAbility(player.attributes.feet.leftFoot)}</strong>
                </div>
                <div>
                    <span>Right Foot</span>
                    <strong>{formatFootAbility(player.attributes.feet.rightFoot)}</strong>
                </div>
            </div>

            <div className="language-box">
                <h4>Languages</h4>

                {player.languages.length === 0 ? (
                    <span>None</span>
                ) : (
                    player.languages.map((language) => (
                        <span key={language.languageCode}>
                            {formatEnum(language.languageCode)}{" "}
                            {formatLanguageFluency(language.fluency)}
                        </span>
                    ))
                )}
            </div>
        </section>
    );
}

export default PlayerInfoPanel;