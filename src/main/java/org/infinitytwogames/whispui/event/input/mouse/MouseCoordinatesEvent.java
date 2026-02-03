package org.infinitytwogames.whispui.event.input.mouse;

import org.infinitytwogames.whispui.Window;
import org.infinitytwogames.whispui.event.Event;

// MODIFIED
public class MouseCoordinatesEvent extends Event {
    private float x;
    private float y;
    private Window window;
    
    private static final MouseCoordinatesEvent event = new MouseCoordinatesEvent();
    
    private MouseCoordinatesEvent() {}
    
    public static MouseCoordinatesEvent get(float x, float y, Window window) {
        return event.set(x,y,window);
    }
    
    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
    
    private MouseCoordinatesEvent set(float x, float y, Window window) {
        this.x = x;
        this.y = y;
        this.window = window;
        return this;
    }
    
    public Window getWindow() {
        return window;
    }
}