package org.infinitytwogames.whispui.ui.base.interactive.button;

import org.infinitytwogames.whispui.data.RGBA;
import org.infinitytwogames.whispui.data.texture.TextureAtlas;
import org.infinitytwogames.whispui.event.input.mouse.MouseHoverEvent;
import org.infinitytwogames.whispui.manager.Mouse;
import org.infinitytwogames.whispui.renderer.UIRenderer;
import org.infinitytwogames.whispui.ui.base.UI;
import org.infinitytwogames.whispui.ui.base.component.TextureRegion;

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
