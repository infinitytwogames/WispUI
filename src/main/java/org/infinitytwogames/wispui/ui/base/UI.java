package org.infinitytwogames.wispui.ui.base;

import org.infinitytwogames.wispui.Display;
import org.infinitytwogames.wispui.data.RGBA;
import org.infinitytwogames.wispui.event.input.mouse.MouseButtonEvent;
import org.infinitytwogames.wispui.event.input.mouse.MouseHoverEvent;
import org.infinitytwogames.wispui.manager.Mouse;
import org.infinitytwogames.wispui.renderer.UIRenderer;
import org.infinitytwogames.wispui.ui.base.component.Component;
import org.infinitytwogames.wispui.ui.base.layout.Anchor;
import org.infinitytwogames.wispui.ui.base.layout.Pivot;
import org.joml.Vector2i;

import java.util.HashMap;
import java.util.Map;

/**
 * The base abstract class for all User Interface elements in WispUI.
 * <p>
 * This class provides the foundational logic for positioning, sizing, and
 * component management. It uses an <b>Anchor-Pivot</b> system to enable
 * resolution-independent layouts.
 * </p>
 *
 * <h2>Positioning Logic</h2>
 * <p>
 * The final screen position is calculated as:
 * <code>Position = (ParentOrigin + (ParentSize * Anchor)) + (MySize * Pivot) + Offset</code>
 * </p>
 *
 * <h2>Lifecycle</h2>
 * <ul>
 * <li><b>draw():</b> Queues the element for the {@link UIRenderer} and updates current state.</li>
 * <li><b>close():</b> Cleans up resources and shuts down attached {@link Component}s.</li>
 * </ul>
 *
 * @author Infinity Two Games
 */
public abstract class UI implements Comparable<UI>, AutoCloseable {
    protected UIRenderer renderer;
    protected RGBA backgroundColor = new RGBA();
    protected RGBA borderColor = new RGBA();
    
    protected int width = 0;
    protected int height = 0;
    protected float angle = 0;
    protected int drawOrder = 0; // z
    protected float cornerRadius = 0;
    protected float borderThickness = 0;
    
    protected boolean hovering = false;
    protected boolean hidden = false;
    
    protected Anchor anchor = new Anchor();
    protected Pivot pivot = new Pivot(0, 0);
    protected Vector2i offset = new Vector2i();
    protected UI parent;
    protected Map<String, Component> components = new HashMap<>();
    protected String tip;
    protected Mouse.CursorType cursorType = Mouse.CursorType.ARROW;
    
    private final Vector2i position = new Vector2i();
    private final Vector2i endPoint = new Vector2i();
    private final Vector2i lastDrawPos = new Vector2i();
    
    public Mouse.CursorType getCursorType() {
        return cursorType;
    }
    
    public void setCursorType(Mouse.CursorType type) {
        this.cursorType = type;
    }
    
    public UI(UIRenderer renderer) {
        this.renderer = renderer;
    }
    
    public void addComponent(Map<String, Component> components) {
        components.forEach((name, component) -> {
            Component copied = component.copy();
            copied.setParent(this);
            this.components.put(name, copied);
        });
    }
    
    public void addComponent(String name, Component component) {
        components.put(name, component);
        component.setParent(this);
    }
    
    public void addComponent(Component component) {
        addComponent(component.getClass().getSimpleName(), component);
    }
    
    @SuppressWarnings("unchecked")
    public <T extends Component> T getComponent(String name) {
        return (T) components.get(name);
    }
    
    public <T extends Component> T getComponent(Class<T> componentClass) {
        // Assuming a simple class name is used as the key for the type-based addComponent
        return getComponent(componentClass.getSimpleName());
    }
    
    public boolean isHovering() {
        return hovering && !isHidden();
    }
    
    public void setHovering(boolean hovering) {
        this.hovering = hovering;
    }
    
    /**
     * Calculates the absolute virtual coordinates of this UI element.
     * <p>
     * If this UI has a parent, the position is relative to the parent's top-left corner.
     * If parentless, it is relative to the {@link Display} bounds.
     * </p>
     *
     * @return The calculated top-left coordinate of the element.
     */
    public Vector2i getPosition() {
        int xa, ya;
        Vector2i o;
        
        if (parent == null) {
            xa = (int) (Display.width * anchor.x);
            ya = (int) (Display.height * anchor.y);
        } else {
            Vector2i parentOrigin = parent.getPosition(); // Get parent's calculated screen coordinates
            xa = parentOrigin.x + (int) (parent.width * anchor.x); // Anchor relative to parent's size
            ya = parentOrigin.y + (int) (parent.height * anchor.y);
        }
        o = offset;
        
        int xp = (int) (width * pivot.x());
        int yp = (int) (height * pivot.y());
        
        int x = xa + xp;
        int y = ya + yp;
        return position.set(x + o.x, y + o.y);
    }
    
    public Vector2i getLastDrawPosition() {
        return lastDrawPos;
    }
    
    public void setPosition(Anchor anchor, Pivot pivot) {
        setAnchor(anchor);
        setPivot(pivot);
    }
    
    public void setPosition(Anchor anchor, Pivot pivot, Vector2i offset) {
        setPosition(anchor, pivot);
        setOffset(offset);
    }
    
    public Vector2i getOffset() {
        return new Vector2i(offset);
    }
    
    public Vector2i getMutableOffset() {
        return offset;
    }
    
    public void setOffset(int same) {
        setOffset(same, same);
    }
    
    public void setOffset(Vector2i offset) {
        setOffset(offset.x, offset.y);
    }
    
    public void setOffset(int x, int y) {
        offset.set(x, y);
    }
    
    public void setAnchor(Anchor anchor) {
        setAnchor(anchor.x, anchor.y);
    }
    
    public void setAnchor(float x, float y) {
        anchor.set(x, y);
    }
    
    public void setPivot(float x, float y) {
        pivot.set(x, y);
    }
    
    public void setPivot(Pivot pivot) {
        setPivot(pivot.x, pivot.y);
    }
    
    public Anchor getAnchor() {
        return anchor;
    }
    
    public Pivot getPivot() {
        return pivot;
    }
    
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }
    
    public RGBA getBackgroundColor() {
        return backgroundColor;
    }
    
    public void setWidth(int width) {
        this.width = width;
    }
    
    public void setHeight(int height) {
        this.height = height;
    }
    
    public UI getParent() {
        return parent;
    }
    
    public void setParent(UI parent) {
        this.parent = parent;
    }
    
    public String getTip() {
        return tip;
    }
    
    public void setBackgroundColor(RGBA backgroundColor) {
        setBackgroundColor(backgroundColor.r(), backgroundColor.g(), backgroundColor.b(), backgroundColor.a());
    }
    
    public void setBackgroundColor(float r, float g, float b, float a) {
        this.backgroundColor.set(r, g, b, a);
    }
    
    /**
     * Queues this UI and its attached components for rendering.
     * <p>
     * This method updates {@link #lastDrawPos} before execution to ensure
     * frame-accurate hit testing even if the UI moves.
     * </p>
     */
    public void draw() {
        if (hidden) return;
        renderer.queue(this);
        lastDrawPos.set(getPosition());
        for (Component component : components.values()) component.draw();
    }
    
    public Vector2i getEndPoint() {
        return endPoint.set(getPosition()).add(width, height);
    }
    
    public Vector2i getLastDrawEndPoint() {
        return new Vector2i(getLastDrawPosition()).add(width, height);
    }
    
    public void addOffset(Vector2i v) {
        addOffset(v.x, v.y);
    }
    
    public void setSize(Vector2i size) {
        setSize(size.x, size.y);
    }
    
    public void setSize(int width, int height) {
        setWidth(width);
        setHeight(height);
    }
    
    public void setSize(int same) {
        setSize(same, same);
    }
    
    public void set(UI ui) {
        setSize(ui.getWidth(), ui.getHeight());
        setBackgroundColor(ui.getBackgroundColor());
        setPosition(ui.getAnchor(), ui.getPivot(), ui.getMutableOffset());
    }
    
    /**
     * Performs a bounds check to see if a point (usually mouse) is within this UI.
     *
     * @param px
     *         Virtual X coordinate.
     * @param py
     *         Virtual Y coordinate.
     *
     * @return {@code true} if the point is within the rectangle.
     */
    public boolean contains(int px, int py) {
        Vector2i start = getPosition();
        Vector2i end = getEndPoint();
        return px >= start.x && px <= end.x && py >= start.y && py <= end.y;
    }
    
    public void addOffset(int x, int y) {
        offset.add(x, y);
    }
    
    public void addOffset(int same) {
        addOffset(same, same);
    }
    
    public float getAngle() {
        return angle;
    }
    
    public void setAngle(float angle) {
        this.angle = angle % 360f;
        for (Component c : components.values()) c.setAngle(this.angle);
    }
    
    public boolean isHidden() {
        return hidden;
    }
    
    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }
    
    public int getDrawOrder() {
        return drawOrder;
    }
    
    public void setDrawOrder(int drawOrder) {
        this.drawOrder = drawOrder;
    }
    
    public float getBorderThickness() {
        return borderThickness;
    }
    
    public RGBA getBorderColor() {
        return borderColor;
    }
    
    public void setBorderColor(RGBA borderColor) {
        setBorderColor(borderColor.r(), borderColor.g(), borderColor.b(), borderColor.a());
    }
    
    public void setBorderColor(float r, float g, float b, float a) {
        borderColor.set(r, g, b, a);
    }
    
    public void setBorderThickness(float borderThickness) {
        this.borderThickness = borderThickness;
    }
    
    public float getCornerRadius() {
        return cornerRadius;
    }
    
    public void setCornerRadius(float cornerRadius) {
        this.cornerRadius = cornerRadius;
    }
    
    @Override
    public int compareTo(UI ui) {
        return Integer.compare(drawOrder, ui.drawOrder);
    }
    
    @Override
    public void close() {
        for (Component component : components.values()) {
            component.cleanup();
        }
        cleanup();
    }
    
    public abstract void onMouseClicked(MouseButtonEvent e);
    
    public abstract void onMouseHover(MouseHoverEvent e);
    
    public abstract void onMouseHoverEnded();
    
    public abstract void cleanup();
}