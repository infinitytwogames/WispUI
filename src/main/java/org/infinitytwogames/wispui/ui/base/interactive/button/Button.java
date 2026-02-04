package org.infinitytwogames.wispui.ui.base.interactive.button;

import org.infinitytwogames.wispui.data.RGBA;
import org.infinitytwogames.wispui.event.input.mouse.MouseButtonEvent;
import org.infinitytwogames.wispui.event.input.mouse.MouseHoverEvent;
import org.infinitytwogames.wispui.manager.Mouse;
import org.infinitytwogames.wispui.ui.base.Label;
import org.infinitytwogames.wispui.ui.base.layout.Anchor;
import org.infinitytwogames.wispui.ui.base.layout.Pivot;
import org.infinitytwogames.wispui.ui.base.layout.Scene;
import org.joml.Vector2i;

/**
 * An interactive text-based button with built-in state management.
 * <p>
 * The {@code Button} class provides automatic visual feedback for hovering
 * and handles "Disabled" states by darkening the component and changing
 * the system cursor. It serves as the base for all clickable text widgets.
 * </p>
 *
 *
 *
 * <h2>Functional Logic</h2>
 * <ul>
 * <li><b>Hover Feedback:</b> Automatically darkens the background and border
 * by 0.25 when the mouse enters, and restores it on exit.</li>
 * <li><b>Enabled State:</b> When disabled, the button ignores clicks and
 * displays a "NOT_ALLOWED" cursor.</li>
 * <li><b>Text Centering:</b> Defaults to centering text perfectly within
 * the button bounds using Anchors and Pivots.</li>
 * </ul>
 */
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
    
    /**
     * Updates the button's interactivity.
     * <p>
     * Disabling the button darkens its appearance permanently and
     * switches the cursor to a 'blocked' icon.
     * </p>
     * @param enabled True to allow clicks, false to lock the button.
     */
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
    
    /**
     * Abstract callback for implementation-specific click logic.
     * @param e The mouse event details.
     */
    public abstract void click(MouseButtonEvent e);
}