package me.alii.streaks;

import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.SoundCategory;
import com.hypixel.hytale.protocol.packets.interface_.NotificationStyle;
import com.hypixel.hytale.server.core.asset.type.soundevent.config.SoundEvent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.SoundUtil;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.NotificationUtil;

import java.util.List;

public class DefaultStreakMilestone extends AbstractStreakMilestone {
    private final String soundKey;
    private final NotificationStyle style;

    public DefaultStreakMilestone(int threshold, List<String> messages,
                                   String soundKey, NotificationStyle style) {
        super(threshold, messages);
        this.soundKey = soundKey;
        this.style = style;
    }

    @Override
    public void onTrigger(Ref<EntityStore> ref, ComponentAccessor<EntityStore> accessor, World world) {
        world.execute(() -> {
            Store<EntityStore> store = world.getEntityStore().getStore();
            PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());

            NotificationUtil.sendNotification(playerRef.getPacketHandler(), pickMessage(), style);
            SoundUtil.playSoundEvent2d(
                    ref,
                    SoundEvent.getAssetMap().getIndex(soundKey),
                    SoundCategory.SFX,
                    1, 1, accessor
            );
        });
    }
}