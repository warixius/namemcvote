package at.warix.data;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class NameMcAccessControllerTest {

    @Test
    public void when_hostIsSet_expect_completeURL() {
        NameMcAccessController controller = NameMcAccessController.getInstance();
        controller.setServerToVoteFor("mc.hypixel.net");
        assertEquals(controller.getURL_ALL_USERS(), "https://api.namemc.com/server/mc.hypixel.net/likes");

        controller.setServerToVoteFor("maficraft.de");
        assertEquals(controller.getURL_ALL_USERS(), "https://api.namemc.com/server/maficraft.de/likes");

    }

    @Test
    public void given_UserHasNotVotedForServer_when_voteIsChecked_expect_false() throws IOException {
        UUID uuidOfGoose = UUID.fromString("d0883ade-188c-403c-acd2-bf42a9b70314");

        NameMcAccessController controller = NameMcAccessController.getInstance();
        controller.setServerToVoteFor("maficraft.de");
        Assert.assertFalse(controller.verifyVote(uuidOfGoose));
    }

    @Test
    public void given_UserHasVotedForServer_when_voteIsChecked_expect_true() throws IOException {
        UUID uuidOfDuck = UUID.fromString("7335455f-587d-4129-b9be-a8473a65ce14");

        NameMcAccessController controller = NameMcAccessController.getInstance();
        controller.setServerToVoteFor("maficraft.de");
        Assert.assertTrue(controller.verifyVote(uuidOfDuck));
    }

}
