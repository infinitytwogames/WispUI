package org.infinitytwogames.wispui.event.input.mouse;

import org.infinitytwogames.wispui.Window;
import org.infinitytwogames.wispui.event.Event;

public class MouseScrollEvent extends Event {
    public final Window window;
    public final int x,y;

    public MouseScrollEvent(Window window, int x, int y) {
        this.window = window;
        this.x = x;
        this.y = y;
    }
}
