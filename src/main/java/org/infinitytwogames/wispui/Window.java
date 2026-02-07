package org.infinitytwogames.wispui;

import org.infinitytwogames.wispui.data.RGBA;
import org.infinitytwogames.wispui.data.ResourceLoader;
import org.infinitytwogames.wispui.data.template.window.DefaultWindowParameterHint;
import org.infinitytwogames.wispui.data.template.window.WindowParameterHint;
import org.infinitytwogames.wispui.event.bus.EventBus;
import org.infinitytwogames.wispui.event.input.keyboard.CharacterInputEvent;
import org.infinitytwogames.wispui.event.input.keyboard.KeyPressEvent;
import org.infinitytwogames.wispui.event.input.mouse.MouseButtonEvent;
import org.infinitytwogames.wispui.event.input.mouse.MouseCoordinatesEvent;
import org.infinitytwogames.wispui.event.input.mouse.MouseScrollEvent;
import org.infinitytwogames.wispui.event.state.WindowResizedEvent;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

/**
 * Manages the GLFW window lifecycle, OpenGL context initialization, and
 * bridges native hardware input to the WispUI {@link EventBus}.
 * <p>
 * This class serves as the primary container for the rendering surface. It handles
 * the creation of the GLFW window handle, manages callback memory, and provides
 * utility methods for window state (focus, size, icon).
 * </p>
 *
 * <h2>Threading Note</h2>
 * <p>
 * Most methods in this class, specifically {@link #update()} and {@link #init()},
 * <strong>must</strong> be called from the main thread (the thread where GLFW
 * was initialized).
 * </p>
 *
 * @author Infinity Two Games
 */
public class Window {
    private long window = 0;
    private String title;
    private int width;
    private int height;
    private final Logger logger = LoggerFactory.getLogger(Window.class);
    
    private final int[] tempW = new int[1], tempH = new int[1];
    private final double[] xpos = new double[1], ypos = new double[1];
    
    private GLFWFramebufferSizeCallback framebufferSizeCallback;
    private GLFWKeyCallback glfwKeyCallback;
    private GLFWMouseButtonCallback glfwMouseButtonCallback;
    private GLFWCharCallback glfwCharCallback;
    private GLFWScrollCallback scrollCallback;
    private GLFWCursorPosCallback cursorPosCallback;
    
    public int getHeight() {
        return height;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
        GLFW.glfwSetWindowTitle(window, title);
    }
    
    public int getWidth() {
        return width;
    }
    
    /**
     * Constructs a new Window and initializes the GLFW backend.
     * * @param width  Initial pixel width of the window.
     *
     * @param height
     *         Initial pixel height of the window.
     * @param title
     *         The text displayed in the window's title bar.
     */
    public Window(int width, int height, String title) {
        this.height = height;
        this.width = width;
        this.title = title;
        initGLFW(DefaultWindowParameterHint.get());
    }
    
    /**
     * Constructs a new Window and initializes the GLFW backend.
     * * @param width  Initial pixel width of the window.
     *
     * @param height
     *         Initial pixel height of the window.
     * @param title
     *         The text displayed in the window's title bar.
     *
     * @param parameter
     *         A custom window parameter.
     */
    public Window(int width, int height, String title, WindowParameterHint parameter) {
        this(width,height,title);
        initGLFW(parameter);
    }
    
    /**
     * Initializes the native GLFW library and creates the window handle.
     * Sets up OpenGL 3.3 Core Profile hints and connects input listeners
     * to the {@link EventBus}.
     *
     * @throws IllegalStateException
     *         if GLFW fails to initialize.
     * @throws RuntimeException
     *         if the window creation fails.
     */
    private void initGLFW(WindowParameterHint parameter) {
        if (!glfwInit()) throw new IllegalStateException("Unable to initiate GLFW");
        long primaryMonitor = GLFW.glfwGetPrimaryMonitor();
        GLFWVidMode vidMode = GLFW.glfwGetVideoMode(primaryMonitor);
        
        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        
        parameter.run();
        
        this.window = GLFW.glfwCreateWindow(width, height, title, MemoryUtil.NULL, MemoryUtil.NULL);
        if (window == MemoryUtil.NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        float[] xscale = new float[1];
        float[] yscale = new float[1];
        GLFW.glfwGetWindowContentScale(window, xscale, yscale);
        
        Window instance = this;
        
        if (vidMode != null) {
            int centerX = (vidMode.width() - width) / 2;
            int centerY = (vidMode.height() - height) / 2;
            GLFW.glfwSetWindowPos(window, centerX, centerY);
        }
        
        // Make the OpenGL context current
        GLFW.glfwMakeContextCurrent(window);
        
        // Enable v-sync
        GLFW.glfwSwapInterval(0);
        GLFW.glfwShowWindow(window);
        
        EventBus.connect(new WindowResizedEvent(width, height, this));
        
        glfwSetFramebufferSizeCallback(window, framebufferSizeCallback = new GLFWFramebufferSizeCallback() {
            @Override
            public void invoke(long window, int newWidth, int newHeight) {
                width = newWidth;
                height = newHeight;
                EventBus.dispatch(new WindowResizedEvent(width, height, instance));
                GL11.glViewport(0, 0, width, height);
            }
        });
        
        glfwSetScrollCallback(window, scrollCallback = new GLFWScrollCallback() {
            @Override
            public void invoke(long handle, double x, double y) {
                if (isFocused()) EventBus.dispatch(new MouseScrollEvent(instance, (int) x, (int) y));
            }
        });
        
        glfwSetKeyCallback(window, glfwKeyCallback = new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                if (isFocused()) EventBus.dispatch(new KeyPressEvent(key, action, mods));
            }
        });
        
        glfwSetMouseButtonCallback(window, glfwMouseButtonCallback = new GLFWMouseButtonCallback() {
            @Override
            public void invoke(long window, int button, int action, int mods) {
                if (isFocused()) {
                    Vector2f p = getMousePosition();
                    EventBus.dispatch(new MouseButtonEvent(button, action, mods, p.x, p.y, instance));
                }
            }
        });
        
        glfwSetCharCallback(window, glfwCharCallback = new GLFWCharCallback() {
            @Override
            public void invoke(long handle, int codepoint) {
                if (isFocused()) EventBus.dispatch(new CharacterInputEvent(codepoint, Character.toChars(codepoint)));
            }
        });
        
        glfwSetCursorPosCallback(window, cursorPosCallback = new GLFWCursorPosCallback() {
            @Override
            public void invoke(long window, double x, double y) {
                if (isFocused()) EventBus.dispatch(MouseCoordinatesEvent.get((float) x, (float) y, instance));
            }
        });
        
        init();
    }
    
    /**
     * Initializes the OpenGL capabilities for the current thread.
     * <p>
     * This <strong>must</strong> be called after the window is created and
     * the context is made current, but before any draw calls.
     * </p>
     *
     * <strong>This method is automatically called as soon as the constructor finishes.</strong>
     */
    public void init() {
        GL.createCapabilities();
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glViewport(0, 0, width, height);
    }
    
    public void reopen(WindowParameterHint hint) {
        cleanup();
        initGLFW(hint);
    }
    
    public long getWindow() {
        return window;
    }
    
    /**
     * Swaps the front and back buffers and polls for window events.
     * Should be called once per frame in the main loop.
     */
    public void update() {
        glfwSwapBuffers(window);
        glfwPollEvents();
    }
    
    public boolean isShouldClose() {
        return glfwWindowShouldClose(window);
    }
    
    /**
     * Frees all native GLFW callbacks and destroys the window handle.
     * <p>
     * Failure to call this will result in native memory leaks from the
     * GLFW callback function pointers.
     * </p>
     */
    public void cleanup() {
        if (glfwKeyCallback != null) glfwKeyCallback.free();
        if (glfwMouseButtonCallback != null) glfwMouseButtonCallback.free();
        if (glfwCharCallback != null) glfwCharCallback.free();
        if (framebufferSizeCallback != null) framebufferSizeCallback.free();
        if (scrollCallback != null) scrollCallback.free();
        if (cursorPosCallback != null) cursorPosCallback.free();
        
        GLFW.glfwDestroyWindow(window);
    }
    
    public static void terminateGLFW() {
        GLFW.glfwTerminate();
    }
    
    public Vector2f getMousePosition() {
        GLFW.glfwGetCursorPos(window, xpos, ypos);
        return new Vector2f((float) xpos[0], (float) ypos[0]);
    }
    
    /**
     * Sets the window icon using a resource path.
     * <p>
     * Decodes the image using {@code stb_image} and uploads the pixel buffer
     * to the OS window manager.
     * </p>
     *
     * @param iconPath
     *         Path to the image file (e.g., "assets/textures/icon.png").
     */
    public void setWindowIcon(String iconPath) {
        ByteBuffer imageBuffer;
        try (MemoryStack stack = MemoryStack.stackPush()) { // Use MemoryStack for temporary allocations
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer comp = stack.mallocInt(1); // Number of components (e.g., 3 for RGB, 4 for RGBA)
            
            // 1. Load the image into a ByteBuffer
            try {
                // Use the helper to load from classpath or filesystem
                imageBuffer = ResourceLoader.ioResourceToByteBuffer(iconPath, 4 * 1024); // 4KB initial buffer
            } catch (IOException e) {
                logger.error("Failed to load icon resource: {}", iconPath, e);
                return;
            }
            
            // Load image using STBImage (expects 4 elements for RGBA)
            ByteBuffer decodedImage = STBImage.stbi_load_from_memory(imageBuffer, w, h, comp, 4);
            if (decodedImage == null) {
                logger.error("Failed to decode image data for icon: {} - {}", iconPath, STBImage.stbi_failure_reason());
                return;
            }
            
            int width = w.get(0);
            int height = h.get(0);
            
            // 2. Prepare GLFWImage structure(s)
            // You can provide multiple icons (e.g., different sizes) for the OS to choose from.
            // For simplicity, we'll provide one.
            GLFWImage.Buffer icons = GLFWImage.malloc(1, stack); // Allocate space for 1 GLFWImage struct
            
            // Populate the GLFWImage struct
            icons.width(width);
            icons.height(height);
            icons.pixels(decodedImage); // The raw pixel data
            
            // 3. Set the window icon
            GLFW.glfwSetWindowIcon(window, icons);
            
            // 4. Clean up the decoded image data
            STBImage.stbi_image_free(decodedImage);
            
        } catch (Exception e) {
            logger.error("An error occurred while setting the window icon for: {}", iconPath, e);
        }
    }
    
    /**
     * @return {@code true} if the window has OS focus.
     */
    public boolean isFocused() {
        return glfwGetWindowAttrib(window, GLFW_FOCUSED) == GLFW.GLFW_TRUE;
    }
    
    public long getWindowHandle() {
        return window;
    }
    
    public Vector2i getSize() {
        GLFW.glfwGetFramebufferSize(window, tempW, tempH);
        
        this.width = tempW[0];
        this.height = tempH[0];
        
        return new Vector2i(tempW[0], tempH[0]);
    }
    
    public void updateSize() {
       GLFW.glfwGetFramebufferSize(window, tempW, tempH);
        
        this.width = tempW[0];
        this.height = tempH[0];
    }
    
    public void close() {
        glfwSetWindowShouldClose(window, true);
    }
    
    public void setBackgroundColor(RGBA color) {
        glClearColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }
    
    public String getClipboardContent() {
        return glfwGetClipboardString(window);
    }
}