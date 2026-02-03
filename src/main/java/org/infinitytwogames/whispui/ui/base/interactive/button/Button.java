package org.infinitytwogames.whispui.ui.base.interactive.button;

import org.infinitytwogames.whispui.data.RGBA;
import org.infinitytwogames.whispui.event.input.mouse.MouseButtonEvent;
import org.infinitytwogames.whispui.event.input.mouse.MouseHoverEvent;
import org.infinitytwogames.whispui.manager.Mouse;
import org.infinitytwogames.whispui.ui.base.Label;
import org.infinitytwogames.whispui.ui.base.layout.Anchor;
import org.infinitytwogames.whispui.ui.base.layout.Pivot;
import org.infinitytwogames.whispui.ui.base.layout.Scene;
import org.joml.Vector2i;

// MODIFIED
public abstract class Button extends Label {
    protected RGBA original;
    protected RGBA originalBorder;
    protected boolean hoverEnabled = true;
    protected boolean enabled;
    
    public Button(Scene renderer, String path, String s) {
        super(renderer, path);
        original = new RGBA(getBackgroundColor());
        originalBorder = getBorderColor().copy();

        setText(s);
        setTextPosition(new Anchor(0.5f,0.5f),new Pivot(0.5f,0.5f),new Vector2i());
        setCursorType(Mouse.CursorType.POINTING_HAND);
    }

    @Override
    public void setBackgroundColor(float r, float g, float b, float a) {
        original.set(r, g, b, a);
        backgroundColor.set(r,g,b,a);
        if (!enabled)
            backgroundColor.set(original.addNew(-0.25f));
    }

    @Override
    public void onMouseHover(MouseHoverEvent e) {
        if (!hoverEnabled || !isEnabled()) return;
        float d = 0.25f;
        backgroundColor.add(-d);
        borderColor.add(-d);
    }
    
    @Override
    public void setBorderColor(float r, float g, float b, float a) {
        super.setBorderColor(r, g, b, a);
        originalBorder.set(r, g, b, a);
        if (!enabled)
            originalBorder.set(originalBorder.addNew(-0.25f));
    }
    
    @Override
    public void onMouseHoverEnded() {
        if (!hoverEnabled || !isEnabled()) return;
        backgroundColor.set(original);
        borderColor.set(originalBorder);
    }
    
    public void hoverFeedback(boolean hover) {
        this.hoverEnabled = hover;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (isEnabled()) setCursorType(Mouse.CursorType.POINTING_HAND);
        else {
            super.setBackgroundColor(original.addNew(-0.25f));
            super.setBorderColor(originalBorder.addNew(-0.25f));
            setCursorType(Mouse.CursorType.NOT_ALLOWED);
        }
    }
    
    @Override
    public void onMouseClicked(MouseButtonEvent e) {
        super.onMouseClicked(e);
        if (enabled) click(e);
    }
    
    public abstract void click(MouseButtonEvent e);
}