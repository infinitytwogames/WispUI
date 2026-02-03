package org.infinitytwogames.whispui.buffer;

import java.util.concurrent.ConcurrentLinkedDeque;

public class FloatBufferPool {
    // We store idle buffers here for reuse
    private static final ConcurrentLinkedDeque<NFloatBuffer> pool = new ConcurrentLinkedDeque<>();
    
    // Limits the pool size so we don't hog all your laptop's RAM
    private static final int MAX_POOL_SIZE = 64;
    
    public static NFloatBuffer acquire() {
        NFloatBuffer buffer = pool.pollFirst();
        
        if (buffer == null) {
            // Pool was empty, create a new native buffer
            return new NFloatBuffer();
        }
        
        // Prepare the buffer for fresh writing
        buffer.reset();
        return buffer;
    }
    
    public static void release(NFloatBuffer buffer) {
        if (buffer == null) return;
        
        if (pool.size() < MAX_POOL_SIZE && buffer.capacity() <= 1024 * 512) {
            pool.addFirst(buffer);
        } else {
            // Pool is full, actually delete the native memory
            buffer.cleanup();
        }
    }
}