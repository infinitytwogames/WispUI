package org.infinitytwogames.whispui.ui.base.layout.menu.scroll;

import org.infinitytwogames.whispui.Window;
import org.infinitytwogames.whispui.data.RGBA;
import org.infinitytwogames.whispui.event.SubscribeEvent;
import org.infinitytwogames.whispui.event.bus.EventBus;
import org.infinitytwogames.whispui.event.input.mouse.MouseButtonEvent;
import org.infinitytwogames.whispui.event.input.mouse.MouseCoordinatesEvent;
import org.infinitytwogames.whispui.event.input.mouse.MouseHoverEvent;
import org.infinitytwogames.whispui.renderer.UIRenderer;
import org.infinitytwogames.whispui.ui.Rectangle;
import org.infinitytwogames.whispui.ui.base.UI;

import static org.infinitytwogames.whispui.Display.transformWindowToVirtual;
import static org.joml.Math.clamp;
import static org.lwjgl.glfw.GLFW.*;

// MODIFIED
public class HorizontalScrollButton extends UI {
    
    protected final HorizontalScrollableMenu menu;
    protected final UI scrollTrack;
    protected final RGBA original = backgroundColor.copy();
    protected final Window window;
    
    protected volatile boolean hold = false;
    protected volatile int mouseXOffset = 0;
    
    public HorizontalScrollButton(UIRenderer renderer, HorizontalScrollableMenu menu, Window window) {
        super(renderer);
        this.menu = menu;
        this.window = window;
        
        scrollTrack = new Rectangle(renderer);
        scrollTrack.setBackgroundColor(
                backgroundColor.r() - 0.5f,
                backgroundColor.g() - 0.5f,
                backgroundColor.b() - 0.5f,
                backgroundColor.a()
        );
        
        super.setParent(scrollTrack);
        EventBus.connect(this);
    }
    
    public void setScrollWidth(int width) {
        super.setWidth(width);
    }
    
    @SubscribeEvent
    public void onMouseMovement(MouseCoordinatesEvent e) {
        if (!hold) return;
        
        int currentMouseX = transformWindowToVirtual(window, (int) e.getX());
        int parentScreenX = parent != null ? parent.getPosition().x : 0;
        int relativeX = currentMouseX - mouseXOffset - parentScreenX;
        
        int maxButtonX = scrollTrack.getWidth() - this.width;
        int clampedX = clamp(0, maxButtonX, relativeX);
        
        super.setOffset(clampedX, super.getOffset().y);
        
        float scrollPercent = (maxButtonX > 0) ? (float) clampedX / maxButtonX : 0.0f;
        float maxScrollDistance = Math.max(0, menu.getContentWidth() - menu.getWidth());
        
        menu.setScrollX((int) (scrollPercent * maxScrollDistance));
    }
    
    @SubscribeEvent
    public void onMouseClick(MouseButtonEvent e) {
        if (hold && e.action == GLFW_RELEASE) {
            hold = false;
            onMouseHoverEnded();
        }
    }
    
    @Override
    public void onMouseClicked(MouseButtonEvent e) {
        int virtualX = transformWindowToVirtual(window, e.x);
        
        if (e.action == GLFW_PRESS && e.button == GLFW_MOUSE_BUTTON_1) {
            hold = true;
            mouseXOffset = virtualX - getPosition().x;
        }
    }
    
    @Override
    public void onMouseHover(MouseHoverEvent e) {
        super.setBackgroundColor(
                original.r() - 0.25f,
                original.g() - 0.25f,
                original.b() - 0.25f,
                original.a()
        );
    }
    
    @Override
    public void onMouseHoverEnded() {
        if (hold) return;
        super.setBackgroundColor(original.r(), original.g(), original.b(), original.a());
    }
    
    @Override
    public void cleanup() {
    }
    
    @Override
    public void draw() {
        scrollTrack.draw();
        super.draw();
    }
    
    public void updateHandlePosition(int menuScrollX) {
        float maxScrollDistance = Math.max(0, menu.getContentWidth() - menu.getWidth());
        if (maxScrollDistance <= 0) {
            super.setOffset(0, super.getOffset().y);
            return;
        }
        
        float scrollPercent = (float) menuScrollX / maxScrollDistance;
        int maxButtonX = scrollTrack.getWidth() - this.width;
        
        super.setOffset((int) (scrollPercent * maxButtonX), super.getOffset().y);
    }
    
    @Override public void setParent(UI parent) { scrollTrack.setParent(parent); }
    @Override public UI getParent() { return scrollTrack.getParent(); }
    
    @Override
    public void setHidden(boolean hidden) {
        super.setHidden(hidden);
        scrollTrack.setHidden(hidden);
    }
}

