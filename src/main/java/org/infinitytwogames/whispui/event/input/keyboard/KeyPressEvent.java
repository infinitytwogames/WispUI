package org.infinitytwogames.whispui.event.input.keyboard;

import org.infinitytwogames.whispui.event.Event;

public class KeyPressEvent extends Event {
    public final int key;
    public final int action;
    public final int mods;

    public KeyPressEvent(int key, int action, int mods) {
        this.key = key;
        this.action = action;
        this.mods = mods;
    }

    public int getKey() {
        return key;
    }

    public int getAction() {
        return action;
    }
}