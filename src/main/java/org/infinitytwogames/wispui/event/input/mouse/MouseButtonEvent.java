package org.infinitytwogames.wispui.event.input.mouse;

import org.infinitytwogames.wispui.Window;
import org.infinitytwogames.wispui.event.Event;

public class MouseButtonEvent extends Event {
    public final int button;
    public final int action;
    public final int x;
    public final int y;
    public final Window window;
    public final int mods;

    public MouseButtonEvent(int button, int action, int mods, float x, float y, Window window) {
        this.button = button;
        this.action = action;
        this.x = (int) x;
        this.y = (int) y;
        this.window = window;
        this.mods = mods;
    }
}

