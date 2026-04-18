package me.alii.streaks;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public abstract class AbstractStreakMilestone implements StreakMilestone {
    private final int threshold;
    private final List<String> messages;

    protected AbstractStreakMilestone(int threshold, List<String> messages) {
        this.threshold = threshold;
        this.messages = List.copyOf(messages);
    }

    @Override
    public int getThreshold() {
        return threshold;
    }

    @Override
    public String pickMessage() {
        return messages.get(ThreadLocalRandom.current().nextInt(messages.size()));
    }
}