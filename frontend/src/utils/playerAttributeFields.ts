export const abilityFields = [
  "currentAbility",
  "potentialAbility",
  "currentReputation",
  "homeReputation",
  "worldReputation",
];

export const footFields = ["leftFoot", "rightFoot"];

export const personalFields = [
  "adaptability",
  "ambition",
  "controversy",
  "loyalty",
  "pressure",
  "professionalism",
  "sportsmanship",
  "temperament",
];

export const mentalFields = [
  "aggression",
  "anticipation",
  "bravery",
  "composure",
  "concentration",
  "consistency",
  "decisions",
  "determination",
  "dirtiness",
  "flair",
  "importantMatches",
  "leadership",
  "movement",
  "positioning",
  "teamWork",
  "vision",
  "workRate",
];

export const physicalFields = [
  "acceleration",
  "agility",
  "balance",
  "injuryProneness",
  "jumpingReach",
  "naturalFitness",
  "pace",
  "stamina",
  "strength",
];

export const technicalFields = [
  "corners",
  "crossing",
  "dribbling",
  "finishing",
  "firstTouch",
  "freeKicks",
  "heading",
  "longShots",
  "longThrows",
  "marking",
  "passing",
  "penaltyTaking",
  "tackling",
  "technique",
  "versatility",
];

export const goalkeepingFields = [
  "aerialAbility",
  "commandOfArea",
  "communication",
  "eccentricity",
  "handling",
  "kicking",
  "oneOnOnes",
  "reflexes",
  "rushingOut",
  "tendencyToPunch",
  "throwing",
];

export const allNonGoalkeepingAttributeFields = [
  ...abilityFields,
  ...footFields,
  ...personalFields,
  ...mentalFields,
  ...physicalFields,
  ...technicalFields,
];

export const allAttributeFields = [
  ...allNonGoalkeepingAttributeFields,
  ...goalkeepingFields,
];

export function formatAttributeName(key: string) {
  const labels: Record<string, string> = {
    currentAbility: "Current Ability",
    potentialAbility: "Potential Ability",
    currentReputation: "Current Reputation",
    homeReputation: "Home Reputation",
    worldReputation: "World Reputation",
    leftFoot: "Left Foot",
    rightFoot: "Right Foot",
    importantMatches: "Important Matches",
    teamWork: "Teamwork",
    workRate: "Work Rate",
    injuryProneness: "Injury Proneness",
    jumpingReach: "Jumping Reach",
    naturalFitness: "Natural Fitness",
    firstTouch: "First Touch",
    freeKicks: "Free Kick Taking",
    longShots: "Long Shots",
    longThrows: "Long Throws",
    penaltyTaking: "Penalty Taking",
    aerialAbility: "Aerial Ability",
    commandOfArea: "Command Of Area",
    oneOnOnes: "One On Ones",
    rushingOut: "Rushing Out",
    tendencyToPunch: "Tendency To Punch",
  };

  return (
    labels[key] ??
    key.replace(/([A-Z])/g, " $1").replace(/^./, (char) => char.toUpperCase())
  );
}

export function getMaxForAttribute(key: string) {
  if (abilityFields.includes(key)) {
    return 200;
  }

  return 20;
}
