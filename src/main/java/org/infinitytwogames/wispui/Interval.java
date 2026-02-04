package org.infinitytwogames.wispui; // Or wherever you want to put utility classes

/**
 * A lightweight, non-blocking periodic timer designed for integration with game or render loops.
 * <p>
 * Unlike {@code java.util.Timer}, this class does not create its own thread. Instead, it relies
 * on an external caller invoking {@link #update()} (usually once per frame). This ensures the
 * {@link Runnable} action is executed on the same thread as the caller, maintaining
 * thread-safety for OpenGL/GLFW operations.
 * </p>
 * * <h2>Usage Example</h2>
 * <pre>{@code
 * Interval saveTimer = new Interval(5000, () -> saveGame());
 * saveTimer.start();
 * * while (running) {
 * saveTimer.update(); // Checks if 5 seconds have passed
 * }
 * }</pre>
 *
 * @author Infinity Two Games
 */
public class Interval {
    private volatile long lastActionTime; // Stores System.nanoTime() when the action last occurred
    private volatile long intervalDurationNanos; // Desired interval in nanoseconds
    private final Runnable action;
    private boolean run; // The code to execute when the interval passes
    
    /**
     * Creates a new non-blocking, one-threaded interval timer.
     * This timer must be updated by calling its `update()` method regularly
     * from your main application loop. The action will be executed on the same thread
     * that calls `update()`.
     *
     * @param intervalMillis
     *         The desired interval duration in milliseconds (e.g., 1000 for 1 second).
     * @param action
     *         The {@code Runnable} to execute when the interval passes. Cannot be null.
     *
     * @throws IllegalArgumentException
     *         if the {@code action} is null.
     */
    public Interval(long intervalMillis, Runnable action) {
        if (action == null) {
            throw new IllegalArgumentException("Runnable action cannot be null.");
        }
        this.intervalDurationNanos = intervalMillis * 1_000_000L; // Convert milliseconds to nanoseconds
        this.action = action;
        this.lastActionTime = System.nanoTime(); // Initialize the timer to the current time
        run = false;
    }
    
    /**
     * Call this method from your main application loop (e.g., game loop or render loop).
     * It checks if enough time has passed since the last execution of the action.
     * If the interval has elapsed, the associated {@code Runnable} action is executed,
     * and the timer is reset for the next interval.
     */
    public void update() {
        if (!run) return;
        long currentTime = System.nanoTime();
        if (currentTime - lastActionTime >= intervalDurationNanos) {
            action.run(); // Execute the custom action
            lastActionTime = currentTime; // Reset the timer for the next interval
        }
    }
    
    /**
     * Resets the timer, causing the next interval to start counting from the moment this method is called.
     * The action will be executed after a full interval duration from the reset time.
     */
    public void reset() {
        this.lastActionTime = System.nanoTime();
    }
    
    /**
     * Dynamically changes the interval duration for this timer.
     *
     * @param newIntervalMillis
     *         The new desired interval duration in milliseconds.
     */
    public void setIntervalMillis(long newIntervalMillis) {
        this.intervalDurationNanos = newIntervalMillis * 1_000_000L;
    }
    
    /**
     * Gets the current interval duration in milliseconds.
     *
     * @return The interval duration in milliseconds.
     */
    public long getIntervalMillis() {
        return intervalDurationNanos / 1_000_000L;
    }
    
    /**
     * Disables the timer. Subsequent calls to {@link #update()} will do nothing.
     */
    public void end() {
        run = false;
    }
    
    /**
     * Enables the timer. The timer will begin checking elapsed time during {@link #update()}.
     */
    public void start() {
        run = true;
    }
}