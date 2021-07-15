package rip.bolt.nerve.match.listeners;

import static net.kyori.adventure.text.Component.text;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;

import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import rip.bolt.nerve.NervePlugin;
import rip.bolt.nerve.api.definitions.Match;
import rip.bolt.nerve.api.definitions.MatchStatus;
import rip.bolt.nerve.api.definitions.Team;
import rip.bolt.nerve.api.definitions.User;
import rip.bolt.nerve.match.MatchStatusListener;
import rip.bolt.nerve.utils.Messages;

public class TeamInformationManager implements MatchStatusListener {

    private ProxyServer server;
    private NervePlugin plugin;

    private static final float SPACE_WIDTH = 1.5f;

    @Inject
    public TeamInformationManager(ProxyServer server, NervePlugin plugin) {
        this.server = server;
        this.plugin = plugin;
    }

    @Override
    public void playerJoin(Player player, Match match) {
        server.getScheduler().buildTask(plugin, () -> {
            if (match.getStatus() == MatchStatus.CREATED && match.getMap() == null) {
                Team team = match.getPlayerTeam(player);
                TextComponent vs = generateVs(match, team);

                player.sendMessage(Messages.colour(NamedTextColor.GOLD, "A match has been found!"));
                player.sendMessage(Messages.formatTeam(team));

                for (Team other : match.getTeams()) {
                    if (other == team)
                        continue;

                    player.sendMessage(vs);
                    player.sendMessage(Messages.formatTeam(other));
                }
            }
        }).delay(1, TimeUnit.SECONDS).schedule();
    }

    @Override
    public void matchStatusUpdate(Match match) {
        if (match.getStatus() == MatchStatus.CREATED && match.getMap() == null) {
            TextComponent matchFound = Messages.colour(NamedTextColor.GOLD, "A match has been found!");
            for (Team team : match.getTeams()) {
                TextComponent theirTeam = Messages.formatTeam(team);
                TextComponent vs = generateVs(match, team);

                for (User user : team.getPlayers()) {
                    Optional<Player> optionalPlayer = server.getPlayer(user.getUniqueId());
                    if (!optionalPlayer.isPresent())
                        continue;
                    Player player = optionalPlayer.get();

                    player.sendMessage(matchFound);
                    player.sendMessage(theirTeam);

                    for (Team other : match.getTeams()) {
                        if (other == team)
                            continue;

                        player.sendMessage(vs);
                        player.sendMessage(Messages.formatTeam(other));
                    }
                }
            }
        }
    }

    private TextComponent generateVs(Match match, Team theirs) {
        Team other = match.getTeams().stream().filter(t -> t != theirs).findFirst().get();

        int teamCount = match.getTeams().size();
        float oneOver = 1f / (float) teamCount;
        float length = 0;

        for (Team team : match.getTeams()) {
            for (User user : team.getPlayers())
                length += user.getUsername().length();

            length += (team.getParticipations().size() - 1) * 2;
            length *= oneOver;
        }

        TextComponent.Builder vs = text();
        if (teamCount == 2) {
            vs.append(padding(length, 0.33f, 2.4f));
            vs.append(Messages.italic(Messages.colour(NamedTextColor.GRAY, theirs.getMMR())));

            vs.append(padding(length, 0.167f, 1));
            vs.append(Messages.bold(Messages.colour(NamedTextColor.YELLOW, "vs")));
            vs.append(padding(length, 0.167f, 1));

            vs.append(Messages.italic(Messages.colour(NamedTextColor.GRAY, other.getMMR())));
        } else {
            vs.append(padding(length, 0.5f, 1));
            vs.append(Messages.bold(Messages.colour(NamedTextColor.YELLOW, "vs")));
        }

        return vs.build();
    }

    private String repeat(int n, String s) {
        return new String(new char[n]).replace("\0", s);
    }

    private TextComponent padding(float length, float factor, float offset) {
        return Messages.colour(NamedTextColor.WHITE, repeat((int) (length * factor * SPACE_WIDTH - offset), " "));
    }

}
