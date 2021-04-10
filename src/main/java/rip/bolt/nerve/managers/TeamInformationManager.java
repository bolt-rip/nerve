package rip.bolt.nerve.managers;

import java.util.concurrent.TimeUnit;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import rip.bolt.nerve.NervePlugin;
import rip.bolt.nerve.api.definitions.Match;
import rip.bolt.nerve.api.definitions.MatchStatus;
import rip.bolt.nerve.api.definitions.Team;
import rip.bolt.nerve.api.definitions.User;
import rip.bolt.nerve.utils.Messages;

public class TeamInformationManager implements MatchStatusListener {

    private static final float SPACE_WIDTH = 1.5f;

    @Override
    public void playerJoin(ProxiedPlayer player, Match match) {
        ProxyServer.getInstance().getScheduler().schedule(NervePlugin.getInstance(), () -> {
            if (match.getStatus() == MatchStatus.CREATED && match.getMap() == null) {
                Team team = match.getPlayerTeam(player);
                BaseComponent[] vs = generateVs(match, team);

                player.sendMessage(Messages.colour(ChatColor.GOLD, "A match has been found!"));
                player.sendMessage(Messages.formatTeam(team));

                for (Team other : match.getTeams()) {
                    if (other == team)
                        continue;

                    player.sendMessage(vs);
                    player.sendMessage(Messages.formatTeam(other));
                }
            }
        }, 1, TimeUnit.SECONDS);
    }

    @Override
    public void matchStatusUpdate(Match match) {
        if (match.getStatus() == MatchStatus.CREATED && match.getMap() == null) {
            BaseComponent matchFound = Messages.colour(ChatColor.GOLD, "A match has been found!");
            for (Team team : match.getTeams()) {
                BaseComponent[] theirTeam = Messages.formatTeam(team);
                BaseComponent[] vs = generateVs(match, team);

                for (User user : team.getPlayers()) {
                    ProxiedPlayer player = ProxyServer.getInstance().getPlayer(user.getUniqueId());
                    if (player == null)
                        continue;

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

    private BaseComponent[] generateVs(Match match, Team theirs) {
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

        ComponentBuilder vs = new ComponentBuilder();
        if (teamCount == 2) {
            vs.append(padding(length, 0.33f, 2.4f));
            vs.append(Messages.italic(Messages.colour(ChatColor.GRAY, theirs.getMMR())));

            vs.append(padding(length, 0.167f, 1));
            vs.italic(false);
            vs.append(Messages.bold(Messages.colour(ChatColor.YELLOW, "vs")));
            vs.append(padding(length, 0.167f, 1));

            vs.bold(false);
            vs.append(Messages.italic(Messages.colour(ChatColor.GRAY, other.getMMR())));
        } else {
            vs.append(padding(length, 0.5f, 1));
            vs.append(Messages.bold(Messages.colour(ChatColor.YELLOW, "vs")));
        }

        return vs.create();
    }

    private String repeat(int n, String s) {
        return new String(new char[n]).replace("\0", s);
    }

    private TextComponent padding(float length, float factor, float offset) {
        return Messages.colour(ChatColor.RESET, repeat((int) (length * factor * SPACE_WIDTH - offset), " "));
    }

}
