package org.infinitytwogames.wispui.data;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.lwjgl.BufferUtils.createByteBuffer;

/**
 * Utility for streaming assets into Direct ByteBuffers.
 * <p>
 * This class handles the complexity of locating files whether they are
 * sitting in a developer's local folder or packaged inside a compressed JAR file.
 * It is essential for loading TTF fonts and PNG textures into memory for the GPU.
 * </p>
 *
 *
 *
 * <h2>Technical Logic</h2>
 * <ul>
 * <li><b>Direct Buffers:</b> Uses {@code ByteBuffer.createByteBuffer()} to allocate
 * memory outside the Java Heap, allowing native libraries (OpenGL/STB) to
 * access the data directly without a performance-heavy copy operation.</li>
 * <li><b>Dynamic Resizing:</b> Implements a growth algorithm (1.5x scaling)
 * when reading from streams of unknown size, ensuring large assets can be loaded
 * without crashing.</li>
 * <li><b>Dual-Path Strategy:</b> Attempts to load from the Classpath (standard for assets)
 * and falls back to the local Filesystem (useful for external mods or configs).</li>
 * </ul>
 */
public class ResourceLoader {

    /**
     * Loads a resource into a ByteBuffer, handling both file system and classpath resources.
     *
     * @param resource The path to the resource (e.g., "textures/icon.png").
     * @param bufferSize The initial buffer size. Will be resized if needed.
     * @return A ByteBuffer containing the resource data.
     * @throws IOException If the resource cannot be read.
     */
    public static ByteBuffer ioResourceToByteBuffer(String resource, int bufferSize) throws IOException {
        ByteBuffer buffer;
        
        // 1. Always try the Classpath first.
        // This is the "Universal" way for both IDE and JAR.
        InputStream source = ResourceLoader.class.getClassLoader().getResourceAsStream(resource);
        
        if (source != null) {
            // Resource found in JAR or IDE Resources folder
            try (ReadableByteChannel rbc = Channels.newChannel(source)) {
                buffer = createByteBuffer(bufferSize);
                while (true) {
                    int bytes = rbc.read(buffer);
                    if (bytes == -1) break;
                    if (buffer.remaining() == 0) {
                        buffer = resizeBuffer(buffer, buffer.capacity() * 3 / 2);
                    }
                }
            }
        } else {
            // 2. Fallback: Try as a direct Absolute or Relative File Path
            Path path = Paths.get(resource);
            if (!Files.isReadable(path)) {
                throw new IOException("Resource not found on classpath or filesystem: " + resource);
            }
            try (SeekableByteChannel fc = Files.newByteChannel(path)) {
                buffer = createByteBuffer((int) fc.size() + 1);
                while (fc.read(buffer) != -1);
            }
        }
        
        buffer.flip();
        return buffer;
    }
    
    public static InputStream getFileFromResourceAsStream(String fileName) {
        ClassLoader classLoader = ResourceLoader.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(fileName);
        
        if (inputStream == null) {
            throw new IllegalArgumentException("File not found! " + fileName);
        } else {
            return inputStream;
        }
    }
    
    private static ByteBuffer resizeBuffer(ByteBuffer buffer, int newCapacity) {
        ByteBuffer newBuffer = createByteBuffer(newCapacity);
        buffer.flip(); // Prepare old buffer for reading
        newBuffer.put(buffer); // Copy contents
        return newBuffer;
    }
}