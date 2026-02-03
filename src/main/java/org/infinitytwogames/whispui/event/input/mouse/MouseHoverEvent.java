package org.infinitytwogames.whispui.event.input.mouse;

import org.infinitytwogames.whispui.Window;
import org.infinitytwogames.whispui.event.Event;
import org.joml.Vector2i;

// MODIFIED
public class MouseHoverEvent extends Event {
    private final Vector2i mousePosition;
    private final Window window;

    public MouseHoverEvent(Vector2i mousePosition, Window window) {
        this.mousePosition = mousePosition;
        this.window = window;
    }

    public MouseHoverEvent(int x, int y, Window window) {
        this.window = window;
        this.mousePosition = new Vector2i(x,y);
    }
    
    public MouseHoverEvent(MouseCoordinatesEvent e) {
        this((int) e.getX(), (int) e.getY(), e.getWindow());
    }
    
    public Vector2i getMousePosition() {
        return mousePosition;
    }
    
    public Window getWindow() {
        return window;
    }
}
