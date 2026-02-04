package org.infinitytwogames.wispui.ui.base.component;

import org.infinitytwogames.wispui.Display;
import org.infinitytwogames.wispui.data.RGBA;
import org.infinitytwogames.wispui.renderer.FontRenderer;
import org.infinitytwogames.wispui.ui.base.UI;
import org.infinitytwogames.wispui.ui.base.layout.Anchor;
import org.infinitytwogames.wispui.ui.base.layout.Pivot;
import org.infinitytwogames.wispui.ui.base.layout.Scene;
import org.joml.Vector2i;

/**
 * A specialized component for rendering strings relative to a parent container.
 * <p>
 * The {@code Text} class calculates its screen position by combining a parent
 * UI's bounds with Anchor (relative positioning) and Pivot (internal alignment)
 * logic. It supports dynamic coloring, rotation, and vertical centering.
 * </p>
 *
 * <h2>Positioning Mathematics</h2>
 * <p>
 * The final position is calculated as:
 * {@code ScreenPos = (ParentSize * Anchor) + (TextSize * Pivot) + Offset + ParentPos}
 * </p>
 *
 * @author InfinityTwo Games
 */
public class Text implements Component {
    protected Anchor anchor = new Anchor();
    protected Pivot pivot = new Pivot(0, 0);
    protected Vector2i offset = new Vector2i();
    protected UI parent = null;
    protected FontRenderer renderer;
    protected String text = "";
    protected RGBA color = new RGBA(1, 1, 1, 1);
    protected boolean centerY = true;
    protected Scene scene;
    protected float angle;
    private int drawOrder;
    
    public Text(FontRenderer renderer, Scene scene) {
        this.renderer = renderer;
        this.scene = scene;
    }

    public boolean isCenterY() {
        return centerY;
    }

    public void setCenterY(boolean centerY) {
        this.centerY = centerY;
    }

    public Anchor getAnchor() {
        return anchor;
    }

    public void setAnchor(Anchor anchor) {
        this.anchor = anchor;
    }

    public Pivot getPivot() {
        return pivot;
    }

    public void setPivot(Pivot pivot) {
        this.pivot = pivot;
    }

    public Vector2i getOffset() {
        return offset;
    }

    public void setOffset(Vector2i offset) {
        this.offset = offset;
    }

    public UI getParent() {
        return parent;
    }

    public void setParent(UI parent) {
        this.parent = parent;
    }
    
    @Override
    public Component copy() {
        return new Text(renderer, scene);
    }
    
    public void setPosition(Anchor anchor, Pivot pivot, Vector2i offset) {
        setAnchor(anchor);
        setPivot(pivot);
        setOffset(offset);
    }
    
    /**
     * Calculates the exact pixel coordinates for the text.
     * <p>
     * This method accounts for the string's width (via {@code getStringWidth})
     * and height to ensure proper alignment regardless of font size or
     * string length.
     * </p>
     * @return A {@link Vector2i} representing the top-left drawing coordinate.
     */
    public Vector2i getPosition() {
        if (renderer == null) return new Vector2i();
        int xa, ya;
        Vector2i o;
        if (parent == null) {
            xa = (int) (Display.width * anchor.x);
            ya = (int) (Display.height * anchor.y);
            o = offset;
        } else {
            xa = (int) (parent.getWidth() * anchor.x);
            ya = (int) (parent.getHeight() * anchor.y);
            int div = centerY ? 2 : 1;
            o = new Vector2i(offset).add(parent.getPosition()).add(0, (int) (renderer.getFontHeight() /div));
        }

        int xp = (int) (renderer.getStringWidth(text) * pivot.x());
        int yp = (int) ((renderer.getFontHeight() / 2) * pivot.y());

        int x = xa + xp;
        int y = ya + yp;
        return new Vector2i(x + o.x, y + o.y);
    }

    public RGBA getColor() {
        return color;
    }

    public void setColor(RGBA color) {
        setColor(color.r(), color.g(), color.b(), color.a());
    }
    
    public void setColor(float r, float g, float b, float a) {
        color.set(r, g, b, a);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void draw() {
        Vector2i position = getPosition();
        renderer.renderText(scene.getRenderer(), text, position.x, position.y,drawOrder+1, color);
    }

    public void setPosition(Anchor anchor, Pivot pivot) {
        setAnchor(anchor);
        setPivot(pivot);
    }

    public int getTextSize(String string) {
        return (int) renderer.getStringWidth(string);
    }

    public void setOffset(int x, int y) {
        offset.set(x,y);
    }

    public void setRenderer(FontRenderer textRenderer) {
        renderer = textRenderer;
    }

    public FontRenderer getRenderer() {
        return renderer;
    }
    
    public float getAngle() {
        return angle;
    }
    
    @Override
    public void setAngle(float angle) {
        this.angle = angle;
    }
    
    @Override
    public void setDrawOrder(int z) {
        drawOrder = z;
    }
    
    @Override
    public int getDrawOrder() {
        return drawOrder;
    }
    
    @Override
    public void cleanup() {
        parent = null;
    }
    
    public Scene getScene() {
        return scene;
    }
}
