package org.infinitytwogames.wispui.ui.base.component;

import org.infinitytwogames.wispui.data.RGBA;
import org.infinitytwogames.wispui.data.texture.Texture;
import org.infinitytwogames.wispui.event.input.mouse.MouseButtonEvent;
import org.infinitytwogames.wispui.event.input.mouse.MouseHoverEvent;
import org.infinitytwogames.wispui.renderer.UIRenderer;
import org.infinitytwogames.wispui.ui.base.UI;

/**
 * A UI component dedicated to rendering full, standalone textures.
 * <p>
 * This class is ideal for large-scale graphics like backgrounds or portraits.
 * It supports a {@code tint} color for dynamic color modulation (e.g., fading
 * an image to red or transparency).
 * </p>
 *
 *
 *
 * <h2>Key Features</h2>
 * <ul>
 * <li><b>Direct Rendering:</b> Uses {@code queueTextureDirect} to render the
 * texture in its entirety, mapped to the component's current bounds.</li>
 * <li><b>Color Modulation:</b> The {@code tint} (stored as {@code foregroundColor})
 * acts as a multiplier for the texture's colors.</li>
 * <li><b>Lifecycle Management:</b> Automatically calls {@code texture.cleanup()}
 * to free GPU memory when the UI component is closed.</li>
 * </ul>
 *
 * @author Infinity Two Games
 */
public class Image extends UI implements Component {
    protected Texture texture;
    protected RGBA tint = new RGBA(1,1,1,1);
    
    public Image(UIRenderer renderer) {
        super(renderer);
    }
    
    public void setForegroundColor(float r, float g, float b, float a) {
        tint.set(r, g, b, a);
    }
    
    public RGBA setForegroundColor(RGBA color) {
        return tint.set(color);
    }
    
    public Texture getTexture() {
        return texture;
    }
    
    public void setTexture(Texture texture) {
        this.texture = texture;
    }
    
    @Override
    public void draw() {
        if (texture == null) return;
        renderer.queueTextureDirect(
                texture,
                tint,
                this
        );
    }
    
    @Override
    public void onMouseClicked(MouseButtonEvent e) {
    
    }
    
    @Override
    public void onMouseHover(MouseHoverEvent e) {
    
    }
    
    @Override
    public void onMouseHoverEnded() {
    
    }
    
    @Override
    public void cleanup() {
        if (texture == null) return;
        texture.cleanup();
    }
    
    @Override
    public Component copy() {
        return new Image(renderer);
    }
}
