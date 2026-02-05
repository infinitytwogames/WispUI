package org.infinitytwogames.wispui.ui.base.interactive.button;

import org.infinitytwogames.wispui.data.RGBA;
import org.infinitytwogames.wispui.data.TextureAtlas;
import org.infinitytwogames.wispui.event.input.mouse.MouseHoverEvent;
import org.infinitytwogames.wispui.manager.Mouse;
import org.infinitytwogames.wispui.renderer.UIRenderer;
import org.infinitytwogames.wispui.ui.base.UI;
import org.infinitytwogames.wispui.ui.base.component.TextureRegion;

/**
 * A graphical button component that renders a specific region of a Texture Atlas.
 * <p>
 * Unlike standard buttons, the {@code ImageButton} uses a {@link TextureRegion}
 * to display icons. It automatically handles hover states by applying a semi-transparent
 * foreground tint and changes the system cursor to a pointing hand.
 * </p>
 *
 *
 *
 * <h2>Technical Integration</h2>
 * <ul>
 * <li><b>Texture Atlas:</b> Efficiently renders icons by referencing specific
 * indices within a larger shared texture.</li>
 * <li><b>Visual Feedback:</b> Uses {@code setForegroundColor} on hover to create
 * a "dimming" effect, signaling to the user that the icon is interactive.</li>
 * <li><b>Layout Sync:</b> The {@code texture.set(this)} call in the draw method
 * ensures the icon scales and moves according to the UI component's bounds.</li>
 * </ul>
 *
 * @author Infinity Two Games
 */
public abstract class ImageButton extends UI {
    protected TextureRegion texture;

    public ImageButton(UIRenderer renderer, TextureAtlas atlas, int textureIndex) {
        super(renderer);
        texture = new TextureRegion(textureIndex,atlas,renderer);
        setCursorType(Mouse.CursorType.POINTING_HAND);
    }

    @Override
    public void draw() {
        super.draw();
        texture.set(this);
    }

    public void setTextureAtlas(TextureAtlas atlas) {
        texture.setAtlas(atlas);
    }

    public void setTextureIndex(int index) {
        texture.setTextureIndex(index);
    }

    @Override
    public void onMouseHover(MouseHoverEvent e) {
        texture.setForegroundColor(new RGBA(0,0,0,0.5f));
    }

    @Override
    public void onMouseHoverEnded() {
        texture.setForegroundColor(new RGBA());
    }

    @Override
    public void cleanup() {

    }
}
