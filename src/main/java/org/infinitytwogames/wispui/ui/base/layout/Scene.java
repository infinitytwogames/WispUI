package org.infinitytwogames.wispui.ui.base.layout;

import org.infinitytwogames.wispui.Display;
import org.infinitytwogames.wispui.Window;
import org.infinitytwogames.wispui.data.Constants;
import org.infinitytwogames.wispui.event.SubscribeEvent;
import org.infinitytwogames.wispui.event.bus.EventBus;
import org.infinitytwogames.wispui.event.input.keyboard.KeyPressEvent;
import org.infinitytwogames.wispui.event.input.mouse.MouseButtonEvent;
import org.infinitytwogames.wispui.event.input.mouse.MouseHoverEvent;
import org.infinitytwogames.wispui.manager.Mouse;
import org.infinitytwogames.wispui.renderer.UIRenderer;
import org.infinitytwogames.wispui.ui.base.Label;
import org.infinitytwogames.wispui.ui.base.UI;
import org.infinitytwogames.wispui.ui.base.animations.Updatable;
import org.joml.Vector2f;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.infinitytwogames.wispui.Display.transformWindowToVirtual;
import static org.infinitytwogames.wispui.VectorMath.isPointWithinRectangle;

/**
 * The root container and manager for a single UI screen or state.
 * <p>
 * The {@code Scene} class is responsible for the "Update-Draw" loop of the UI.
 * It handles the following core responsibilities:
 * <ul>
 * <li><b>Input Dispatching:</b> Routes mouse and keyboard events to the correct {@link UI} components.</li>
 * <li><b>Z-Order Management:</b> Maintains a sorted list of components based on their draw order.</li>
 * <li><b>Hover & Tooltips:</b> Tracks how long a mouse has hovered over an element to trigger tooltips.</li>
 * <li><b>Task Scheduling:</b> Executes thread-safe {@link Runnable} tasks via a concurrent queue.</li>
 * </ul>
 * </p>
 *
 * @author Infinity Two Games
 */
public class Scene {
    protected final UIRenderer renderer;
    protected final List<UI> uis;
    protected final Window window;
    protected float delta; // in seconds
    protected boolean handleInput = true;
    protected String activeTooltip = null;
    protected final ConcurrentLinkedQueue<Runnable> runs = new ConcurrentLinkedQueue<>();
    protected final Label tooltip;
    
    private final Vector2i lastMousePosition = new Vector2i();
    private final Vector2i mouseTemp = new Vector2i();
    private float hoverTime;
    private long lastFrameTime = System.nanoTime(); // nanoseconds
    private boolean tooltipShown;
    
    public Scene(UIRenderer renderer, Window window) {
        this.renderer = renderer;
        this.window = window;
        this.uis = Collections.synchronizedList(new ArrayList<>());
        
        tooltip = new Label(this, Constants.fontFilePath);
        tooltip.setSize(512, 64);
        tooltip.setBackgroundColor(0.05f, 0.05f, 0.05f, 0.75f);
        tooltip.setTextPosition(new Anchor(0, 0.5f), new Pivot(0, 0.5f));
        tooltip.setDrawOrder(100);
        
        EventBus.connect(this);
    }
    
    public UIRenderer getRenderer() {
        return renderer;
    }
    
    public int register(UI ui) {
        if (!uis.contains(ui)) {
            int l = uis.size();
            uis.add(ui);
            Collections.sort(uis);
            return l;
        }
        return 0;
    }
    
    /**
     * Renders the scene.
     * <p>
     * This method calculates the frame {@code delta}, processes hover logic,
     * updates {@link Updatable} components, and flushes the renderer.
     * </p>
     */
    public void draw() {
        long now = System.nanoTime();
        delta = (now - lastFrameTime) / 1_000_000_000.0f; // Convert to seconds
        lastFrameTime = now;
        
        activeTooltip = null;
        tooltipShown = false;
        
        // If NOTHING in the entire UI tree was hovered, reset cursor to ARROW
        if (!calculateHover(uis)) {
            hoverTime = 0;
            Mouse.setCursor(Mouse.CursorType.ARROW);
        }
        
        renderer.begin();
        drawUIs();
        
        if (activeTooltip != null) {
            drawTooltip(activeTooltip);
        }
        
        renderer.flush();
        
        Runnable r;
        while ((r = runs.poll()) != null) {
            r.run();
        }
    }
    
    private void drawTooltip(String activeTooltip) {
        tooltipShown = true;
        tooltip.setText(activeTooltip);
        tooltip.draw();
    }
    
    public String getActiveTooltip() {
        return activeTooltip;
    }
    
    public void setActiveTooltip(String activeTooltip) {
        this.activeTooltip = activeTooltip;
    }
    
    public float getHoverTime() {
        return hoverTime;
    }
    
    public void setHoverTime(float hoverTime) {
        this.hoverTime = hoverTime;
    }
    
    /**
     * Recursively determines which UI element is currently under the mouse cursor.
     * <p>
     * This follows a "Painter's Algorithm" in reverse: it checks the front-most
     * (last in list) elements first to ensure they block interaction with
     * elements behind them.
     * </p>
     * * @param uis The list of UI elements to check.
     *
     * @return {@code true} if a hover event was consumed by a UI element.
     */
    protected boolean calculateHover(List<UI> uis) {
        Vector2f m = window.getMousePosition();
        Vector2i mousePosition = transformWindowToVirtual(window, m, mouseTemp);
        
        boolean hoverHandled = false;
        
        for (int i = uis.size() - 1; i >= 0; i--) {
            UI ui = uis.get(i);
            
            if (ui.isHidden()) {
                if (ui.isHovering()) {
                    ui.setHovering(false);
                    ui.onMouseHoverEnded();
                }
                continue;
            }
            
            boolean hoveringNow = isPointWithinRectangle(ui.getLastDrawPosition(), mousePosition, ui.getEndPoint());
            
            if (hoveringNow && !hoverHandled) {
                if (ui instanceof Container con) {
                    if (!ui.isHovering()) {
                        ui.setHovering(true);
                        ui.onMouseHover(new MouseHoverEvent(mousePosition, window));
                        Mouse.setCursor(ui.getCursorType());
                    }
                    hoverHandled = calculateHover(con.getUIs());
                    if (hoverHandled) {
                        return true;
                    }
                    continue;
                }
                
                if (!ui.isHovering()) {
                    ui.setHovering(true);
                    ui.onMouseHover(new MouseHoverEvent(mousePosition, window));
                    Mouse.setCursor(ui.getCursorType());
                }
                
                if (mousePosition.equals(lastMousePosition)) {
                    hoverTime += delta;
                } else {
                    hoverTime = 0;
                }
                
                if (hoverTime >= 1.0f && ui.getTip() != null) {
                    activeTooltip = ui.getTip();
                    
                    int virtualWidth = Display.getWidth();
                    int xOffset = 12;
                    
                    if (mousePosition.x + tooltip.getWidth() + xOffset > virtualWidth) {
                        tooltip.setOffset(mousePosition.x - tooltip.getWidth() - xOffset, mousePosition.y + 8);
                    } else {
                        tooltip.setOffset(mousePosition.x + xOffset, mousePosition.y + 8);
                    }
                }
                
                hoverHandled = true;
            } else {
                if (ui.isHovering()) {
                    ui.setHovering(false);
                    ui.onMouseHoverEnded();
                }
            }
        }
        
        lastMousePosition.set(mousePosition);
        
        return hoverHandled;
    }
    
    private boolean isTooltipShown() {
        return tooltipShown;
    }
    
    public void addHoverTime(float delta) {
        hoverTime += delta;
    }
    
    public Label getTooltip() {
        return tooltip;
    }
    
    protected void drawUIs() {
        for (UI ui : uis) {
            if (!ui.isHidden()) {
                if (ui instanceof Updatable updatable) {
                    updatable.update(delta);
                }
                ui.draw();
            }
        }
    }
    
    @SubscribeEvent
    public void onMouseClicked(MouseButtonEvent e) {
        doMouseClick(e, uis);
    }
    
    /**
     * Dispatches click events through the UI hierarchy.
     * <p>
     * Containers take priority; the event is passed to children before the
     * container itself consumes the click.
     * </p>
     */
    private boolean doMouseClick(MouseButtonEvent e, List<UI> uis) {
        if (!handleInput) return false;
        Vector2i mousePosition = transformWindowToVirtual(window, e.x, e.y);
        
        // Iterate backward (front-most UI to back-most UI)
        for (int i = uis.size() - 1; i >= 0; i--) {
            UI ui = uis.get(i);
            
            if (ui.isHidden()) continue;
            
            // Check if the click is within this UI element
            if (isPointWithinRectangle(ui.getLastDrawPosition(), mousePosition, ui.getLastDrawEndPoint())) {
                
                // PRIORITY: If it's a container, try to click the children FIRST
                if (ui instanceof Container container) {
                    // If a child handles the click, we stop here (return true)
                    if (doMouseClick(e, container.getUIs())) {
                        return true;
                    }
                }
                
                // If no child handled it, this UI element handles it
                ui.onMouseClicked(e);
                resetHover();
                return true; // Click consumed!
            }
        }
        return false;
    }
    
    private void resetHover() {
        activeTooltip = null;
        hoverTime = 0;
    }
    
    @SubscribeEvent
    public void onKeyClicked(KeyPressEvent e) {
        resetHover();
    }
    
    public Window getWindow() {
        return window;
    }
    
    public void run(Runnable runnable) {
        runs.add(runnable);
    }
    
    public void cleanup() {
        for (UI ui : new ArrayList<>(uis)) {
            ui.close();
        }
        uis.clear();
        tooltip.close();
        
        setHandleInput(false);
    }
    
    public void open() {
        setHandleInput(true);
    }
    
    public void setHandleInput(boolean handleInput) {
        this.handleInput = handleInput;
    }
    
    public void pause() {
        setHandleInput(false);
    }
    
    public void unregister(UI ui) {
        uis.remove(ui);
    }
    
    public float getDelta() {
        return delta;
    }
}
