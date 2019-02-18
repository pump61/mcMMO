package com.gmail.nossr50.commands.party;

import com.gmail.nossr50.core.data.UserManager;
import com.gmail.nossr50.core.datatypes.party.Party;
import com.gmail.nossr50.core.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.core.locale.LocaleLoader;
import com.gmail.nossr50.core.party.PartyManager;
import com.gmail.nossr50.core.events.party.McMMOPartyChangeEvent.EventReason;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PartyRenameCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (args.length) {
            case 2:
                McMMOPlayer mcMMOPlayer = UserManager.getPlayer((Player) sender);
                Party playerParty = mcMMOPlayer.getParty();

                String oldPartyName = playerParty.getName();
                String newPartyName = args[1];

                // This is to prevent party leaders from spamming other players with the rename message
                if (oldPartyName.equalsIgnoreCase(newPartyName)) {
                    sender.sendMessage(LocaleLoader.getString("Party.Rename.Same"));
                    return true;
                }

                Player player = mcMMOPlayer.getPlayer();

                // Check to see if the party exists, and if it does cancel renaming the party
                if (PartyManager.checkPartyExistence(player, newPartyName)) {
                    return true;
                }

                String leaderName = playerParty.getLeader().getPlayerName();

                for (Player member : playerParty.getOnlineMembers()) {
                    if (!PartyManager.handlePartyChangeEvent(member, oldPartyName, newPartyName, EventReason.CHANGED_PARTIES)) {
                        return true;
                    }

                    if (!member.getName().equalsIgnoreCase(leaderName)) {
                        member.sendMessage(LocaleLoader.getString("Party.InformedOnNameChange", leaderName, newPartyName));
                    }
                }

                playerParty.setName(newPartyName);

                sender.sendMessage(LocaleLoader.getString("Commands.Party.Rename", newPartyName));
                return true;

            default:
                sender.sendMessage(LocaleLoader.getString("Commands.Usage.2", "party", "rename", "<" + LocaleLoader.getString("Commands.Usage.PartyName") + ">"));
                return true;
        }
    }
}