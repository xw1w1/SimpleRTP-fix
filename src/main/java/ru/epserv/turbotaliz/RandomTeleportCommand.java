package ru.epserv.turbotaliz;

import cloud.commandframework.arguments.standard.IntegerArgument;
import cloud.commandframework.paper.PaperCommandManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


public class RandomTeleportCommand {
    public enum TeleportationType {
        ZERO_ORIGIN,
        PLAYER_ORIGIN
    }

    public RandomTeleportCommand(@NotNull PaperCommandManager<CommandSender> manager) {
        manager.command(manager
                .commandBuilder("rtp", "randomteleport", "randtp")
                .argument(IntegerArgument.<CommandSender>builder("bound").asOptional())
                .argument(TeleportationTypeArgument.<CommandSender>builder("type").asOptional())
                .senderType(Player.class)
                .handler(ctx -> {
                            Integer bound = ctx.getOrDefault("bound", 5000);
                            TeleportationType type = ctx.get("type");

                            this.handle((Player) ctx.getSender(), bound, type);
                        }
                ));
    }

    public void handle(@NotNull Player player, Integer bound, TeleportationType type) {
        World world = player.getWorld();

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            int i = 3;

            @Override
            public void run() {
                Component msg = Component.text()
                        .append(Component.text("Teleporting in " + i + " seconds"))
                        .color(NamedTextColor.GRAY)
                        .build();
                player.sendMessage(msg);
                i--;
                if (i == 0) {
                    Random rand = new Random();
                    Location location = player.getLocation();
                    Component message;

                    double x = type == TeleportationType.PLAYER_ORIGIN ? location.x() : 0;
                    double z = type == TeleportationType.PLAYER_ORIGIN ? location.z() : 0;

                    x += 2 * rand.nextInt(bound) - bound;
                    z += 2 * rand.nextInt(bound) - bound;

                    message = Component.text(
                            "Changed x: " +
                                    String.format("%1$,.2f", x) + ", z: " +
                                    String.format("%1$,.2f", z) + ", type: " +
                                    type.name()
                    );

                    int y = world.getHighestBlockYAt((int) x, (int) z);
                    Location teleportLocation = new Location(world, x, y, z);
                    player.teleportAsync(teleportLocation);

                    player.sendMessage(message);
                    this.cancel();
                }
            }
        }, 0, 1000);
    }
}
