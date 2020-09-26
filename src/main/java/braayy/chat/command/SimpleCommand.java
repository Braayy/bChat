package braayy.chat.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@FunctionalInterface
public interface SimpleCommand {

    void execute(CommandSender sender, String[] args);

    static void add(SimpleCommand executor, String... aliases) {
        CommandMap map = Bukkit.getServer().getCommandMap();

        Command commandWrapper = new Command(aliases[0]) {
            @Override
            public List<String> getAliases() {
                if (aliases.length > 1) {
                    return Arrays.asList(Arrays.copyOfRange(aliases, 1, aliases.length));
                }

                return Collections.emptyList();
            }

            @Override
            public boolean execute(CommandSender sender, String label, String[] args) {
                executor.execute(sender, args);

                return true;
            }
        };

        String pluginName = JavaPlugin.getProvidingPlugin(SimpleCommand.class).getName();

        map.register(pluginName, commandWrapper);
    }

}