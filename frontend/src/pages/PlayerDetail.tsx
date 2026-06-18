import { useEffect, useMemo, useState } from "react";
import { Link, useParams } from "react-router-dom";
import api from "../api/axios";
import AttributePanel from "../components/players/AttributePanel";
import ContractPanel from "../components/players/ContractPanel";
import PlayerHeader from "../components/players/PlayerHeader";
import PlayerInfoPanel from "../components/players/PlayerInfoPanel";
import PositionPanel from "../components/players/PositionPanel";
import type {
    CurrencyCode,
    PlayerDetail as PlayerDetailType,
} from "../types/player";
import {
    goalkeeperMentalOrder,
    goalkeeperOrder,
    goalkeeperPhysicalOrder,
    goalkeeperTechnicalOrder,
    mentalOrder,
    outfieldSetPiecesOrder,
    outfieldTechnicalOrder,
    physicalOrder,
} from "../utils/playerAttributeOrders";

function PlayerDetail() {
    const { id } = useParams();

    const [player, setPlayer] = useState<PlayerDetailType | null>(null);
    const [selectedCurrency, setSelectedCurrency] = useState<CurrencyCode>("GBP");
    const [showContract, setShowContract] = useState(false);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    useEffect(() => {
        async function loadPlayer() {
            try {
                const response = await api.get<PlayerDetailType>(`/players/${id}`);
                setPlayer(response.data);
                setSelectedCurrency(response.data.defaultCurrency);
            } catch (err) {
                setError("Failed to load player detail.");
            } finally {
                setLoading(false);
            }
        }

        loadPlayer();
    }, [id]);

    const selectedRate = useMemo(() => {
        if (!player) return 1;

        return (
            player.availableCurrencies.find(
                (currency) => currency.code === selectedCurrency
            )?.rateFromGbp ?? 1
        );
    }, [player, selectedCurrency]);

    if (loading) {
        return <main className="content-page">Loading player...</main>;
    }

    if (error || !player) {
        return (
            <main className="content-page">
                <div className="error-message">{error || "Player not found."}</div>
            </main>
        );
    }

    return (
        <main className="content-page player-detail-page">
            <div className="detail-actions">
                <Link to="/squad">← Back to Squad</Link>

                <select
                    value={selectedCurrency}
                    onChange={(event) =>
                        setSelectedCurrency(event.target.value as CurrencyCode)
                    }
                >
                    {player.availableCurrencies.map((currency) => (
                        <option key={currency.code} value={currency.code}>
                            {currency.code} - {currency.name}
                        </option>
                    ))}
                </select>
            </div>

            <PlayerHeader
                player={player}
                selectedCurrency={selectedCurrency}
                selectedRate={selectedRate}
                onOpenContract={() => setShowContract(true)}
            />

            <section
                className={
                    player.isGoalkeeper
                        ? "player-panel-grid goalkeeper"
                        : "player-panel-grid"
                }
            >
                <PositionPanel
                    positions={player.positions}
                    isGoalkeeper={player.isGoalkeeper}
                />

                {player.isGoalkeeper ? (
                    <>
                        <AttributePanel
                            title="Goalkeeping"
                            attributes={{
                                ...player.attributes.goalkeeping,
                                firstTouch: player.attributes.technical.firstTouch,
                                passing: player.attributes.technical.passing,
                            }}
                            keys={goalkeeperOrder}
                        />

                        <AttributePanel
                            title="Mental"
                            attributes={player.attributes.mental}
                            keys={goalkeeperMentalOrder}
                        />

                        <AttributePanel
                            title="Physical"
                            attributes={player.attributes.physical}
                            keys={goalkeeperPhysicalOrder}
                        />

                        <AttributePanel
                            title="Technical"
                            attributes={player.attributes.technical}
                            keys={goalkeeperTechnicalOrder}
                        />
                    </>
                ) : (
                    <>
                        <AttributePanel
                            title="Technical"
                            attributes={player.attributes.technical}
                            keys={outfieldTechnicalOrder}
                            secondaryTitle="Set Pieces"
                            secondaryAttributes={player.attributes.technical}
                            secondaryKeys={outfieldSetPiecesOrder}
                        />

                        <AttributePanel
                            title="Mental"
                            attributes={player.attributes.mental}
                            keys={mentalOrder}
                        />

                        <AttributePanel
                            title="Physical"
                            attributes={player.attributes.physical}
                            keys={physicalOrder}
                        />
                    </>
                )}

                <PlayerInfoPanel player={player} />
            </section>

            {showContract && (
                <ContractPanel
                    player={player}
                    selectedCurrency={selectedCurrency}
                    selectedRate={selectedRate}
                    onClose={() => setShowContract(false)}
                />
            )}
        </main>
    );
}

export default PlayerDetail;