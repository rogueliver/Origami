package io.github.btarg.origami;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for OrigamiMain using MockBukkit.
 * 
 * NOTE: These tests are currently DISABLED due to a known compatibility issue between
 * MockBukkit and Paper 1.21's registry system (see MockBukkit issue #1032).
 * 
 * The error is: "RegistryKeyImpl[key=minecraft:attribute] points to a registry that is not available yet"
 * 
 * This is fixed in MockBukkit's v1.21 branch but not yet released in a stable version.
 * Once a fixed version is released (or the JitPack snapshot includes the fix),
 * remove the @Disabled annotations to enable these tests.
 * 
 * Test setup:
 * - JUnit 5 (5.11.3)
 * - MockBukkit for Paper 1.21 (org.mockbukkit.mockbukkit:mockbukkit-v1.21:4.107.0 or JitPack snapshot)
 * - Run with: ./gradlew test
 */
@Disabled("MockBukkit 1.21 registry compatibility issue - see MockBukkit #1032")
class OrigamiMainTest {

    private ServerMock server;
    private OrigamiMain plugin;
    private PlayerMock player;

    @BeforeEach
    void setUp() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(OrigamiMain.class);
        player = server.addPlayer();
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    void testPluginLoads() {
        assertNotNull(plugin);
        assertTrue(plugin.isEnabled());
    }

    @Test
    void testPluginInstance() {
        assertEquals(plugin, OrigamiMain.getInstance());
    }

    @Test
    void testCommandRegistered() {
        assertNotNull(server.getCommandMap().getCommand("origami"));
    }

    @Test
    void testPlayerCanJoin() {
        assertNotNull(player);
        assertTrue(player.isOnline());
    }
}