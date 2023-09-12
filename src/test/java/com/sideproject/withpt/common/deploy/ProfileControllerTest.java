package com.sideproject.withpt.common.deploy;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;

class ProfileControllerTest {

    @Test
    public void real_profile_check() {
        //given
        String expectedProfile = "real1";
        MockEnvironment env = new MockEnvironment();
        env.addActiveProfile(expectedProfile);
        env.addActiveProfile("db");
        env.addActiveProfile("oauth");

        ProfileController profileController = new ProfileController(env);

        //when
        String profile = profileController.profile();

        //then
        assertThat(profile).isEqualTo(expectedProfile);
    }
}