package org.infinitytwogames.wispui.manager;

import org.infinitytwogames.wispui.Window;
import org.joml.Vector2i;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Global utility for tracking mouse position, movement deltas, and managing system cursors.
 * <p>
 * This class provides a centralized way to access the mouse state across the engine.
 * It calculates the distance moved since the last frame (deltaX/deltaY), which is
 * essential for features like camera rotation or slider dragging.
 * </p>
 *
 *
 *
 * <h2>Key Features</h2>
 * <ul>
 * <li><b>Delta Tracking:</b> Calculates frame-to-frame movement. Essential for
 * "look" mechanics where the cursor is hidden but movement is still measured.</li>
 * <li><b>Cursor Management:</b> Provides an easy {@code setCursor(CursorType)}
 * API to change the OS cursor (e.g., switching to an I-Beam when hovering over a text field).</li>
 * <li><b>Resource Lifecycle:</b> Caches standard cursors on {@code init()} and
 * destroys them on {@code dispose()} to prevent native memory leaks.</li>
 * </ul>
 */
public class Mouse {
    private static double lastX, lastY;
    private static double deltaX, deltaY;
    private static boolean first = true;
    private static Window window;

    public static void setWindow(Window window) {
        Mouse.window = window;
    }

    public static Window getWindow() {
        return window;
    }
    
    /**
     * Calculates movement since the last update.
     * <p>
     * Logic: Subtracts the previous frame's position from the current position.
     * If it is the first update, it sets the baseline to avoid a massive
     * jump in delta.
     * </p>
     */
    public static void update() {
        double[] x = new double[1];
        double[] y = new double[1];
        glfwGetCursorPos(window.getWindow(), x, y);

        if (first) {
            lastX = x[0];
            lastY = y[0];
            first = false;
        }

        deltaX = x[0] - lastX;
        deltaY = y[0] - lastY;

        lastX = x[0];
        lastY = y[0];
    }
    
    public static void setDeltaX(double newDeltaX) {
        // 1. Get the current position
        double[] x = new double[1];
        double[] y = new double[1];
        glfwGetCursorPos(window.getWindow(), x, y);
        
        // 2. Set the last tracked position to the current position
        // This ensures the NEXT delta calculation (x[0] - lastX) is zero.
        Mouse.lastX = x[0];
        
        // 3. Set the delta itself to the desired value (usually 0 when resetting)
        Mouse.deltaX = newDeltaX;
    }
    
    public static void setDeltaY(double newDeltaY) {
        // 1. Get the current position
        double[] x = new double[1];
        double[] y = new double[1];
        glfwGetCursorPos(window.getWindow(), x, y);
        
        // 2. Set the last tracked position to the current position
        Mouse.lastY = y[0];
        
        // 3. Set the delta itself to the desired value (usually 0 when resetting)
        Mouse.deltaY = newDeltaY;
    }
    
    public static Vector2i getCurrentPosition() {
        double[] x = new double[1];
        double[] y = new double[1];
        glfwGetCursorPos(window.getWindowHandle(), x, y);

        return new Vector2i((int) x[0], (int) y[0]);
    }

    public static double getDeltaX() { return deltaX; }
    public static double getDeltaY() { return deltaY; }
    
    private static final Map<CursorType, Long> cursors = new HashMap<>();
    
    public enum CursorType {
        ARROW(GLFW.GLFW_ARROW_CURSOR),
        IBEAM(GLFW.GLFW_IBEAM_CURSOR),
        CROSSHAIR(GLFW.GLFW_CROSSHAIR_CURSOR),
        HAND(GLFW.GLFW_HAND_CURSOR),
        H_RESIZE(GLFW.GLFW_RESIZE_EW_CURSOR),
        V_RESIZE(GLFW.GLFW_RESIZE_NS_CURSOR),
        POINTING_HAND(GLFW_POINTING_HAND_CURSOR),
        NOT_ALLOWED(GLFW_NOT_ALLOWED_CURSOR);
        
        private final int glfwValue;
        CursorType(int value) { this.glfwValue = value; }
    }
    
    public static void init(Window window) {
        Mouse.window = window;
        // Pre-create standard system cursors
        for (CursorType type : CursorType.values()) {
            cursors.put(type, GLFW.glfwCreateStandardCursor(type.glfwValue));
        }
    }
    
    public static void setCursor(CursorType type) {
        GLFW.glfwSetCursor(window.getWindow(), cursors.get(type));
    }
    
    public static void dispose() {
        for (long cursorPtr : cursors.values()) {
            GLFW.glfwDestroyCursor(cursorPtr);
        }
    }
}
