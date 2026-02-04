## WispUI

**WispUI** is a lightweight, high-performance UI library designed for LWJGL-based voxel engines and games. 
Built by [InfinityTwo Games](https://github.com/infinitytwogames),
it focuses on minimal overhead, off-heap memory management.

---
## 🚀 Features

* **Off-Heap Memory:** Uses `NFloatBuffer` and `NIntBuffer` to bypass the JVM Garbage Collector for vertex data.
* **Retained-Mode UI:** Efficiently manages components and only updates what is necessary.
* **Flexible Layouts:** Support for Anchors, Pivots, and Aspect Ratio scaling.
* **Event-Driven:** Easy-to-use listeners for mouse interaction and window resizing.
* **Thematic Design:** Optimized for dark/nocturnal game aesthetics (Nyctotile compatible).
---
## 🛠️ Requirements

This library requires **LWJGL3**:
- **LWJGL v3** (Core, OpenGL, GLFW, STB)
- **JOML** (Java OpenGL Math Library)
- **Java 17+**

### **⚠️ Critical Notes**
1. This library is **not** compatible with **Java Swing** or **JavaFX**.
2. The UI system is **not** thread-safe.
3. Some UIs **may** support multi-threaded loading.
4. Minimal knowledge of **LWJGL v3** is advised.
5. **DO NOT DRAW UI ELEMENTS OUTSIDE THE OPENGL CONTEXT THREAD.**
---
## 📦 Installation

WispUI is hosted via JitPack. Add it to your `build.gradle`:

```gradle
repositories {
    mavenCentral()
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.infinitytwogames:WispUI:v1.0.0'
}
```

---
## 🛠️ Quick Start

```java
public static void main(String[] args) {
    // First: Create a window
    int width = 1024;
    int height = 512;
    String title = "WispUI Demo";
    
    Window window = new Window(width, height, title);
    window.init();
    Display.init();
    
    // Second: Create a scene
    UIRenderer renderer = new UIRenderer(window);
    Scene scene = new Scene(renderer);
    
    SceneManager.register("mainscene",scene);
    
    // Create an example button
    Button button = new Button(scene, "path/to/font/file.ttf", "Example Button") {
        @Override
        public void click(MouseButtonEvent e) {
            if (e.action == GLFW_RELEASE) {
                System.out.println("Clicked");
            }
        }
    };
    
    // Set the position
    button.setPosition(new Anchor(0.5f,0.5f), new Pivot(0.5f,0.5f));
    
    // Set the size
    int bWidth = 256, bHeight = 128; 
    button.setSize(256, 128);
    
    // Set the color
    button.setBackgroundColor(new RGBA(0.5f,0.5f,0.5f, 1));
    
    // Register the button to the scene
    scene.register(button);
    
    // Third: Make a loop
    while (window.isShouldClose()) {
        Display.clearBufferBits();
        
        SceneManager.draw();
        
        window.update();
    }
    
    // Cleanup the created resources
    SceneManager.cleanup();
    renderer.cleanup();
    window.cleanup();
    Window.terminateGLFW();
}
```
---
## 🧠 Memory Safety
WispUI uses native memory for performance. When using native buffers, always ensure proper cleanup:

```Java
try (NFloatBuffer buffer = new NFloatBuffer()) {
    // Use buffer...
} // Automatically cleaned up
```
---
## 👋 Contribution

We welcome contributions\! As a project in development and more features are needed, all contributions are valuable, from code implementation to bug reporting.

### How to Contribute

1.  **Report Issues:** Found a bug? Please open a detailed **Issue** on the repository. Include the full stacktrace and steps to reproduce.
2.  **Submit Code:** Fork the repository, create a descriptive branch, and submit a **Pull Request (PR)** with your changes. New features or bug fixes should align with the project's architectural principles.
---
## ⚖️ Licensing & Trademarks

This project is licensed under the **GNU GPL v3**. 
This ensures that the software remains free and open-source. Anyone who modifies
or distributes this code must also share their source code under the same license.

**Trademark Notice:**
The names **WispUI**, **Infinity Two Games**, and all associated logos are trademarks of Infinity Two Games.
While the source code is open-source, this license does not grant you permission to use our brand names or logos 
for your own distributions or commercial products without express written consent.
