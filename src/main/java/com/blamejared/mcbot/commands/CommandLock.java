package com.blamejared.mcbot.commands;

import com.blamejared.mcbot.commands.api.Command;
import com.blamejared.mcbot.commands.api.CommandBase;
import com.blamejared.mcbot.commands.api.CommandException;
import com.blamejared.mcbot.util.Requirements;
import sx.blah.discord.handle.obj.*;
import java.util.*;

@Command
public class CommandLock extends CommandBase {

    public CommandLock() {
        super("lock", false);
    }

    @Override
    public void process(IMessage message, List<String> flags, List<String> args) throws CommandException {
        if (args.size() >= 1) {
                throw new CommandException("Invalid number of arguments.");
            }

        IChannel channel = message.getChannel();
        IGuild server = channel.getGuild();
        List<IRole> roles = server.getRoles();
        List<IRole> moderators = new ArrayList<>();
        EnumSet<Permissions> sendPerm = EnumSet.noneOf(Permissions.class);

        //Get all roles that have moderation privileges
        for (IRole current : roles) {
            if (current.getPermissions().contains(Permissions.MANAGE_CHANNEL)| current.getPermissions().contains(Permissions.MANAGE_MESSAGES) && !current.getPermissions().contains(Permissions.ADMINISTRATOR)){
                //DEBUG - Prints out all matching user roles and their permissions
                //System.out.println(current.toString() + " - " + current.getPermissions().toString());
                moderators.add(current);
            }
        }

        //Lock Channel
        sendPerm.add(Permissions.SEND_MESSAGES);
        if (channel.getModifiedPermissions(roles.get(0)).contains(Permissions.SEND_MESSAGES)){
            for (IRole current  : moderators) {
                channel.overrideRolePermissions(current, sendPerm,null);
            }
            channel.overrideRolePermissions(roles.get(0),null, sendPerm);

            channel.sendMessage("Channel has been locked");
        }else {
            channel.overrideRolePermissions(roles.get(0), sendPerm, null );
            channel.sendMessage("Channel has been unlocked");
        }

        System.out.println(roles.get(0).toString());
    }

    @Override
    public Requirements requirements() {
        return Requirements.builder()
                .with(Permissions.MANAGE_MESSAGES, Requirements.RequiredType.ONE_OF)
                .with(Permissions.MANAGE_CHANNEL, Requirements.RequiredType.ONE_OF)
                .build();
    }

    @Override
    public String getUsage() {
        return "Locks the channel for all users without moderation privileges";
    }
}
