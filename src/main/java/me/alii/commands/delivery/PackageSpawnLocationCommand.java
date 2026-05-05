package me.alii.commands.delivery;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.ParseResult;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.*;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.Config;
import com.hypixel.hytale.server.core.util.TargetUtil;
import me.alii.AllomancyPlugin;
import me.alii.domain.PackageSpawnPoint;
import me.alii.domain.packages.PackageRarity;
import me.alii.managers.PackageSpawnConfig;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.joml.Vector3d;
import org.joml.Vector3i;

import java.awt.*;
import java.util.EnumSet;
import java.util.List;

public class PackageSpawnLocationCommand extends CommandBase {

    public PackageSpawnLocationCommand() {
        super("package", "");
        Config<PackageSpawnConfig> config = AllomancyPlugin.getInstance()
                .getPackageConfig();
        addSubCommand(new AddLocationCommand(config));
    }

    @Override
    protected void executeSync(@NonNullDecl CommandContext commandContext) {
    }

    private static class AddLocationCommand extends CommandBase {
        private static final ArgumentType<List<PackageRarity>> listArgumentType;

        private final Config<PackageSpawnConfig> config;
        private final RequiredArg<String> displayName;
        private final OptionalArg<List<PackageRarity>> packageRarities;

        public AddLocationCommand(Config<PackageSpawnConfig> config) {
            super("add-loc", "");
            this.config = config;
            this.packageRarities = withOptionalArg("PackageRarities", "A list of package rarities that can spawn at this point!",
                    listArgumentType);
            displayName = withRequiredArg("displayName", "Display name of the location", ArgTypes.STRING);
        }

        @Override
        protected void executeSync(@NonNullDecl CommandContext commandContext) {
            Ref<EntityStore> ref = commandContext.senderAsPlayerRef();
            if (ref == null) return;

            World world = ref.getStore().getExternalData()
                    .getWorld();

            world.execute(() -> {
                // Get the entity store
                Store<EntityStore> store = ref.getStore();

                // Get the block the player is looking at
                Vector3i targetBlock = TargetUtil.getTargetBlock(ref, 6, store);
                if (targetBlock == null) return;

                Vector3d spawnPosition = new Vector3d(targetBlock).add(0.5, 1, 0.5);

                List<PackageRarity> rarities = packageRarities.get(commandContext);
                if (rarities == null) {
                    rarities = List.of(PackageRarity.VALUES);
                }

                config.get().save(new PackageSpawnPoint(displayName.get(commandContext), spawnPosition, EnumSet.copyOf(rarities)));
                config.save();
                commandContext.sendMessage(Message.raw("Added package spawn location for this block!").color(Color.GREEN));
            });
        }

        static {
            MultiArgumentType<PackageRarity> multiArgumentType = new MultiArgumentType<>("PackageRarities", "PackageRarities") {
                private final WrappedArgumentType<String> rarityName;

                {
                    this.rarityName = this.withParameter("rarityName", "rarities.rarityName.usage", ArgTypes.STRING);
                }

                @Override
                public PackageRarity parse(@NonNullDecl MultiArgumentContext context, @NonNullDecl ParseResult parseResult) {
                    String rarityName = context.get(this.rarityName);

                    if ((rarityName == null || rarityName.isEmpty())) {
                        parseResult.fail(Message.raw("Invalid Rarity name!").color(Color.RED));
                        return null;
                    }

                    PackageRarity packageRarity;
                    try {
                        packageRarity = PackageRarity.valueOf(rarityName.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        parseResult.fail(Message.raw("Invalid Rarity name!").color(Color.RED));
                        return null;
                    }

                    return packageRarity;
                }
            };

            listArgumentType = new ListArgumentType<>(multiArgumentType);
        }
    }
}