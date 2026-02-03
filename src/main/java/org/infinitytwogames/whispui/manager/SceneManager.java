package org.infinitytwogames.whispui.manager;

import org.infinitytwogames.whispui.Display;
import org.infinitytwogames.whispui.event.input.mouse.MouseButtonEvent;
import org.infinitytwogames.whispui.event.input.mouse.MouseHoverEvent;
import org.infinitytwogames.whispui.ui.base.Label;
import org.infinitytwogames.whispui.ui.base.UI;
import org.infinitytwogames.whispui.ui.base.layout.Scene;
import org.joml.Vector2i;

import java.util.*;

import static org.infinitytwogames.whispui.Display.transformWindowToVirtual;
import static org.infinitytwogames.whispui.VectorMath.isPointWithinRectangle;

// MODIFIED
public class SceneManager {
    protected static final Map<String, Scene> scenes = new HashMap<>();
    protected static final Stack<Scene> activeScenes = new Stack<>();
    
    private static final Vector2i lastMousePosition = new Vector2i();
    private static final Vector2i mouseTemp = new Vector2i();
    
    public static void register(String name, Scene scene) {
        scenes.put(name, scene);
        scene.setHandleInput(false);
    }
    
    // --- State Operations (New Features) ---
    
    /**
     * Pushes a new screen onto the stack, making it the top/active screen.
     * The new screen receives onOpen() notification.
     */
    public static void pushScreen(String name) {
        Scene scene = scenes.get(name);
        if (scene == null)
            throw new RuntimeException("Screen \"" + name + "\" not found in registry.");
        
        if (activeScenes.contains(scene))
            return; // already active — ignore or bring to front
        
        try {
            Scene s = activeScenes.peek();
            if (s != null) s.pause();
        } catch (EmptyStackException ignored) {}
        
        activeScenes.push(scene);
        scene.open();
    }
    
    public static void setScreen(String name) {
        while (!activeScenes.isEmpty()) {
            activeScenes.pop().pause();
        }
        pushScreen(name);
    }
    
    public static Scene getCurrent() {
        return activeScenes.isEmpty()? null : activeScenes.peek();
    }
    
    /**
     * Removes the top screen from the stack and notifies it that it's closing.
     */
    public static void popScreen() {
        if (activeScenes.isEmpty()) return;
        
        Scene closed = activeScenes.pop();
        closed.cleanup();
        
        if (!activeScenes.isEmpty()) {
            // Assuming the Scene class has a 'start()' or 'resume()' method to undo 'stop()'.
            // Using 'open()' is often used for resume if a separate 'resume()' doesn't exist.
            // I recommend adding a dedicated 'resume()' method to the Scene class.
            activeScenes.peek().open();
        }
    }
    
    /**
     * Draws all active screens from bottom to top.
     * The bottom screen is usually the main HUD or Game view.
     */
    public static void draw() {
        // Iterate through the stack to draw all screens in order (bottom to top)
        for (Scene scene : activeScenes) {
            scene.draw();
        }
    }
    
    public static void propagateMouseClick(MouseButtonEvent e, List<UI> uis, int offestX, int offestY) {
        Vector2i mousePosition = transformWindowToVirtual(e.window, e.x, e.y);
        
        // Iterate backward (front-most to back-most)
        for (int i = uis.size() -1; i >= 0; i--) {
            UI ui = uis.get(i);
            
            // If hidden, skip this element for interaction.
            if (ui.isHidden()) {
                continue;
            }
            
            // Check for intersection
            int x = ui.getLastDrawPosition().x + offestX, y = ui.getLastDrawPosition().y + offestY;
            int width = x + ui.getWidth(), height = y + ui.getHeight();
            if (isPointWithinRectangle(x, y, mousePosition.x, mousePosition.y, width, height)) {
                ui.onMouseClicked(e);
                break; // Stop after clicking the front-most, non-hidden element.
            }
        }
    }
    
    public static void propagateMouseHover(MouseHoverEvent e, List<UI> uis, int offsetX, int offsetY, float delta) {
        Vector2i mousePosition = transformWindowToVirtual(e.getWindow(), e.getMousePosition(), mouseTemp);
        
        boolean found = false;
        Scene scene = activeScenes.peek();
        Label tooltip = scene.getTooltip();
        
        for (int i = uis.size() - 1; i >= 0; i--) {
            UI ui = uis.get(i);
            if (ui.isHidden()) continue;
            
            int x = ui.getLastDrawPosition().x + offsetX;
            int y = ui.getLastDrawPosition().y + offsetY;
            int endX = x + ui.getWidth();
            int endY = y + ui.getHeight();
            
            if (isPointWithinRectangle(x, y, mousePosition.x, mousePosition.y, endX, endY)) {
                if (!found) {
                    if (!ui.isHovering()) {
                        ui.setHovering(true);
                        ui.onMouseHover(e);
                        Mouse.setCursor(ui.getCursorType());
                    }
                    
                    // Check if mouse is stationary
                    if (mousePosition.equals(lastMousePosition)) {
                        scene.addHoverTime(delta);
                    } else {
                        scene.setHoverTime(0);
                    }
                    
                    // Trigger tooltip if held for 1 second and tip exists
                    if (scene.getHoverTime() >= 1.0f && ui.getTip() != null) {
                        scene.setActiveTooltip(ui.getTip());
                        
                        // Dynamic Positioning: Follow mouse but check bounds
                        int virtualWidth = Display.getWidth();
                        int xOffset = 12;
                        
                        // If tooltip went off right edge, flip it to the left
                        if (mousePosition.x + tooltip.getWidth() + xOffset > virtualWidth) {
                            tooltip.setOffset(mousePosition.x - tooltip.getWidth() - xOffset, mousePosition.y + 8);
                        } else {
                            tooltip.setOffset(mousePosition.x + xOffset, mousePosition.y + 8);
                        }
                    }
                    
                    found = true;
                } else {
                    ui.setHovering(false);
                    ui.onMouseHoverEnded();
                }
                
            } else {
                ui.setHovering(false);
                ui.onMouseHoverEnded();
            }
        }
        
        if (!found) {
            scene.setHoverTime(0);
        }
        
        lastMousePosition.set(mousePosition);
    }
    
    public static void propagateMouseClick(MouseButtonEvent e, List<UI> uis) {
        propagateMouseClick(e, uis, 0, 0);
    }
    
    public static void propagateMouseHover(MouseHoverEvent e, List<UI> uis, float delta) {
        propagateMouseHover(e, uis, 0, 0, delta);
    }
    
    public static void propagateMouseHover(MouseHoverEvent e, List<UI> uis) {
        propagateMouseHover(e, uis, activeScenes.peek().getDelta());
    }
    
    public static void cleanup() {
        for (Scene scene : scenes.values()) {
            scene.cleanup();
        }
        activeScenes.clear();
        scenes.clear();
    }
    
    public static void unregister(String name, boolean popCurrent) {
        if (popCurrent) activeScenes.pop();
        Scene scene = scenes.remove(name);
        if (scene != null) {
            scene.cleanup();
        }
    }
}