package com.clubops.player;

import com.clubops.player.dto.PlayerAttributeGroupResponse;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PlayerAttributeGroupResponseTests {

    @Test
    void normalUsersDoNotReceiveHiddenAttributes() {
        PlayerAttribute attribute = completeAttribute();

        PlayerAttributeGroupResponse response =
                PlayerAttributeGroupResponse.from(attribute, false);

        assertThat(response.ability()).isEmpty();
        assertThat(response.reputation()).isEmpty();
        assertThat(response.personal()).isEmpty();
        assertThat(response.mental())
                .doesNotContainKeys("consistency", "dirtiness", "importantMatches");
        assertThat(response.physical()).doesNotContainKey("injuryProneness");
        assertThat(response.technical()).doesNotContainKey("versatility");
        assertThat(response.mental()).containsEntry("decisions", 10);
    }

    @Test
    void vipUsersReceiveHiddenAttributes() {
        PlayerAttribute attribute = completeAttribute();

        PlayerAttributeGroupResponse response =
                PlayerAttributeGroupResponse.from(attribute, true);

        assertThat(response.ability()).containsEntry("currentAbility", 100);
        assertThat(response.reputation()).containsEntry("worldReputation", 10);
        assertThat(response.personal()).containsEntry("professionalism", 10);
        assertThat(response.mental()).containsEntry("consistency", 10);
        assertThat(response.physical()).containsEntry("injuryProneness", 10);
        assertThat(response.technical()).containsEntry("versatility", 10);
    }

    private PlayerAttribute completeAttribute() {
        PlayerAttribute attribute = new PlayerAttribute();

        for (var field : PlayerAttribute.class.getDeclaredFields()) {
            if (field.getType() == Integer.class) {
                field.setAccessible(true);
                try {
                    field.set(attribute, field.getName().equals("currentAbility") ? 100 : 10);
                } catch (IllegalAccessException exception) {
                    throw new IllegalStateException(exception);
                }
            }
        }

        return attribute;
    }
}
