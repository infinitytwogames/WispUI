package org.infinitytwogames.wispui.ui.base.interactive.button;

import org.infinitytwogames.wispui.data.RGBA;
import org.infinitytwogames.wispui.event.input.mouse.MouseButtonEvent;
import org.infinitytwogames.wispui.event.input.mouse.MouseHoverEvent;
import org.infinitytwogames.wispui.ui.base.layout.Scene;

import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

/**
 * A specialized button that maintains a persistent "On/Off" state.
 * <p>
 * The {@code ToggleButton} switches its internal state every time it is clicked.
 * It automatically manages its visual appearance, darkening the background
 * when active to provide clear feedback to the user.
 * </p>
 *
 *
 *
 * <h2>Visual State Logic</h2>
 * <p>
 * The component uses a cumulative darkening algorithm in {@link #updateVisuals(boolean)}:
 * <ul>
 * <li><b>Base:</b> The original background color.</li>
 * <li><b>Toggled:</b> Darkened by 0.25 to indicate an active/pressed state.</li>
 * <li><b>Hovered:</b> Darkened by an additional 0.15 to show interactivity.</li>
 * </ul>
 * </p>
 *
 * @author Infinity Two Games
 */
public abstract class ToggleButton extends Button {
    protected boolean toggle;
    protected RGBA color;
    
    public ToggleButton(Scene renderer, String path, String s) {
        super(renderer, path, s);
        color = new RGBA(getBackgroundColor());
    }
    
    @Override
    public void click(MouseButtonEvent e) {
        if (e.action != GLFW_RELEASE) {
            setToggle(!toggle);
            onToggle(toggle);
        }
    }
    
    @Override
    public void setBackgroundColor(float r, float g, float b, float a) {
        color.set(r, g, b, a);
        super.setBackgroundColor(r, g, b, a);
    }
    
    /**
     * Abstract callback triggered whenever the state changes.
     * @param toggle The resulting state after the click.
     */
    public abstract void onToggle(boolean toggle);
    
    public boolean isToggled() {
        return toggle;
    }
    
    @Override
    public void onMouseHover(MouseHoverEvent e) {
        updateVisuals(true);
    }
    
    @Override
    public void onMouseHoverEnded() {
        updateVisuals(false);
    }
    
    /**
     * Changes the toggle state and updates the visuals.
     * @param toggle The new state to apply.
     */
    public void setToggle(boolean toggle) {
        this.toggle = toggle;
        updateVisuals(false); // Update to current toggle state
    }
    
    /**
     * Internal method to calculate the background color based on
     * both the toggle state and the mouse hover state.
     * @param isHovering Whether the mouse is currently over the button.
     */
    private void updateVisuals(boolean isHovering) {
        float d = 0.0f;
        if (toggle) d += 0.25f;      // Darken for toggle
        if (isHovering) d += 0.15f;  // Darken slightly more for hover
        
        backgroundColor.set(
                Math.max(0, original.r() - d),
                Math.max(0, original.g() - d),
                Math.max(0, original.b() - d),
                original.a()
        );
    }
}
