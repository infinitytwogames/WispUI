package org.infinitytwogames.whispui.ui.base.interactive;

import org.infinitytwogames.whispui.Display;
import org.infinitytwogames.whispui.event.SubscribeEvent;
import org.infinitytwogames.whispui.event.bus.EventBus;
import org.infinitytwogames.whispui.event.input.mouse.MouseButtonEvent;
import org.infinitytwogames.whispui.event.input.mouse.MouseCoordinatesEvent;
import org.infinitytwogames.whispui.event.input.mouse.MouseHoverEvent;
import org.infinitytwogames.whispui.renderer.UIRenderer;
import org.infinitytwogames.whispui.ui.base.UI;
import org.infinitytwogames.whispui.ui.base.interactive.button.BasicButton;
import org.infinitytwogames.whispui.ui.base.layout.Anchor;
import org.infinitytwogames.whispui.ui.base.layout.Container;
import org.infinitytwogames.whispui.ui.base.layout.Pivot;

import java.util.List;

import static org.infinitytwogames.whispui.Display.transformWindowToVirtual;
import static org.joml.Math.clamp;
import static org.lwjgl.glfw.GLFW.*;

public abstract class SliderBar extends UI implements Container {
    protected BasicButton button;
    protected float scrollPercent;
    protected int mouseXOffset;
    protected int scrollX;
    protected int current;
    protected int max;
    protected boolean held;
    
    private final List<UI> temp;
    
    public SliderBar(UIRenderer renderer) {
        super(renderer);
        
        button = new BasicButton(renderer) {
            @Override
            public void clicked(MouseButtonEvent e) {
                if (e.action == GLFW_PRESS && e.button == GLFW_MOUSE_BUTTON_1) {
                    held = true;
                    mouseXOffset = Display.transformWindowToVirtual(e.window,e.x);
                }
            }
        };
        button.setParent(this);
        button.setWidth(32);
        button.setPosition(new Anchor(0.5f,0.5f), new Pivot(0.5f,0.5f));
        
        temp = List.of(button);
        
        EventBus.connect(this);
    }
    
    public int getCurrent() {
        return current;
    }
    
    public void setCurrent(int current) {
        this.current = current;
    }
    
    public int getMax() {
        return max;
    }
    
    public void setMax(int max) {
        this.max = max;
    }
    
    @SubscribeEvent
    public void onMouseMovement(MouseCoordinatesEvent e) {
        if (!held) return;
        
        int currentMouseX = transformWindowToVirtual(e.getWindow(), (int) e.getX());
        int parentScreenX = parent != null ? parent.getPosition().x : 0;
        int relativeX = currentMouseX - mouseXOffset - parentScreenX;
        
        int maxTravel = getWidth() - button.getWidth();
        int clampedX = clamp(0, maxTravel, relativeX);
        
        button.setOffset(clampedX, super.getOffset().y);
        
        scrollPercent = (maxTravel > 0) ? (float) clampedX / maxTravel : 0.0f;
        float maxScrollDistance = getWidth();
        
        scrollX = ((int) (scrollPercent * maxScrollDistance));
        current = (int) (scrollPercent * max);
    }
    
    @SubscribeEvent
    public void onMouseClick(MouseButtonEvent e) {
        if (held && e.action == GLFW_RELEASE) {
            held = false;
            onSelect(current);
        }
    }
    
    @Override
    public void setHeight(int height) {
        super.setHeight(height);
        button.setHeight(height *2);
    }
    
    @Override
    public void setBackgroundColor(float r, float g, float b, float a) {
        super.setBackgroundColor(r, g, b, a);
        button.setBackgroundColor(r - 0.25f,g - 0.25f,b - 0.25f,a);
    }
    
    @Override
    public void setBorderColor(float r, float g, float b, float a) {
        super.setBorderColor(r, g, b, a);
        button.setBorderColor(r - 0.25f,g - 0.25f,b - 0.25f,a);
    }
    
    @Override
    public void setCornerRadius(float cornerRadius) {
        super.setCornerRadius(cornerRadius);
        button.setCornerRadius(cornerRadius);
    }
    
    @Override
    public void setHidden(boolean hidden) {
        super.setHidden(hidden);
        button.setHidden(hidden);
    }
    
    @Override
    public void setDrawOrder(int drawOrder) {
        super.setDrawOrder(drawOrder);
        button.setDrawOrder(drawOrder+1);
    }
    
    @Override
    public void setBorderThickness(float borderThickness) {
        super.setBorderThickness(borderThickness);
        button.setBorderThickness(borderThickness);
    }
    
    @Override
    public void draw() {
        super.draw();
        button.draw();
    }
    
    @Override
    public void onMouseClicked(MouseButtonEvent e) {
        int x = Display.transformWindowToVirtual(e.window, e.x) - getPosition().x - (button.getWidth() / 2);
        int maxTravel = getWidth() - button.getWidth();
        scrollPercent = clamp(0.0f, 1.0f, (float) x / maxTravel);
        current = (int) (scrollPercent * max);
        onSelect(current);
    }
    
    @Override
    public void onMouseHover(MouseHoverEvent e) {
    
    }
    
    @Override
    public void onMouseHoverEnded() {
    
    }
    
    @Override
    public void cleanup() {
    
    }
    
    @Override
    public List<UI> getUIs() {
        return temp;
    }
    
    public abstract void onSelect(int selected);
}
