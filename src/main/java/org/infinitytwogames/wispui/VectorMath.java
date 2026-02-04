package org.infinitytwogames.wispui;

import org.infinitytwogames.wispui.buffer.NIntBuffer;
import org.joml.*;
import org.joml.Math;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

/**
 * A high-performance utility class for spatial math, vector conversion, and serialization.
 * <p>
 * This class provides optimized methods for common UI and voxel operations, such as
 * point-in-rectangle collision detection and efficient memory reuse when converting
 * between JOML vector types.
 * </p>
 *
 * <h2>Performance Optimization</h2>
 * <p>
 * To minimize Garbage Collection (GC) pressure, prefer methods that accept a
 * <code>dest</code> (destination) object rather than methods that return new instances.
 * </p>
 *
 * @author Infinity Two Games
 */
public class VectorMath {
    
    /**
     * Checks if a 2D point is contained within a rectangular area.
     * * @param topLeft     The minimum X and Y coordinates (top-left corner).
     *
     * @param point
     *         The point to test.
     * @param bottomRight
     *         The maximum X and Y coordinates (bottom-right corner).
     *
     * @return {@code true} if the point is inside or on the boundary of the rectangle.
     */
    public static boolean isPointWithinRectangle(Vector2i topLeft, Vector2i point, Vector2i bottomRight) {
        int minX = topLeft.x();
        int maxX = bottomRight.x();
        int minY = topLeft.y();
        int maxY = bottomRight.y();
        
        int x = point.x();
        int y = point.y();
        
        return x >= minX && x <= maxX && y >= minY && y <= maxY;
    }
    
    /**
     * Primitive-based rectangle check for performance-critical logic.
     * <p>
     * Note: This method casts inputs to {@code float} to handle potential
     * coordinate comparisons involving UI scaling factors.
     * </p>
     */
    public static boolean isPointWithinRectangle(Vector2i topRight, int pointX, int pointY, Vector2i bottomLeft) {
        return isPointWithinRectangle(topRight.x, topRight.y, pointX, pointY, bottomLeft.x, bottomLeft.y);
    }
    
    public static boolean isPointWithinRectangle(int x, int y, Vector2i target, Vector2i endPoint) {
        return isPointWithinRectangle(x, y, target.x, target.y, endPoint.x, endPoint.y);
    }
    
    public static boolean isPointWithinRectangle(int minX, int minY, int pointX, int pointY, int maxX, int maxY) {
        return (float) pointX >= minX && (float) pointX <= maxX &&
                (float) pointY >= minY && (float) pointY <= maxY;
    }
    
    /**
     * Optimized rectangle check using position and size.
     *
     * @param pos
     *         The top-left starting position of the rectangle.
     * @param size
     *         The width and height of the rectangle.
     * @param px
     *         The target point X coordinate.
     * @param py
     *         The target point Y coordinate.
     */
    public static boolean isPointInRect(Vector2i pos, Vector2i size, int px, int py) {
        return isPointWithinRectangle(pos.x, pos.y, px, py, size.x, size.y);
    }
    
    /**
     * Converts an integer vector to a float vector without new allocation.
     * * @param source The integer vector containing input data.
     *
     * @param dest
     *         The destination vector to be modified.
     *
     * @return The {@code dest} parameter for chaining.
     */
    public static Vector3f toFloat(Vector3i source, Vector3f dest) {
        return dest.set(source.x, source.y, source.z);
    }
    
    /**
     * Calculates the squared distance on the XZ plane.
     * <p>
     * Useful for voxel chunk-loading logic where the vertical (Y) distance
     * is irrelevant for range checks. Squared distance is used to avoid
     * the expensive {@code Math.sqrt()} operation.
     * </p>
     */
    public static float distanceSquaredXZ(float x1, float z1, float x2, float z2) {
        float dx = x1 - x2;
        float dz = z1 - z2;
        return dx * dx + dz * dz;
    }
    
    /**
     * Converts a vector to a human-readable hyphenated ID string (e.g., "10-20").
     */
    public static String toStringAsId(Vector2i v) {
        // Using StringBuilder is faster than string concatenation in IDs
        return String.valueOf(v.x) + '-' + v.y;
    }
    
    /**
     * Efficiently writes a 3D coordinate to an off-heap native buffer.
     * <p>
     * Automatically ensures the buffer has enough capacity (3 ints)
     * before writing.
     * </p>
     * * @param buffer The native buffer to write to.
     *
     * @param x,
     *         y, z The coordinates to store.
     */
    public static void fill(NIntBuffer buffer, int x, int y, int z) {
        buffer.require(3);
        buffer.put(x);
        buffer.put(y);
        buffer.put(z);
    }
    
    /**
     * Serializes a Vector3f into a byte array for networking or file storage.
     * * @param v The vector to serialize.
     *
     * @return A byte array of length 12 (3 floats * 4 bytes).
     */
    public static byte[] serialize(Vector3f v) {
        ByteBuffer buffer = ByteBuffer.allocate(3 * Float.BYTES);
        buffer.putFloat(v.x);
        buffer.putFloat(v.y);
        buffer.putFloat(v.z);
        return buffer.array();
    }
    
    public static Vector3f toFloat(Vector3i vector) {
        return new Vector3f(vector);
    }
    
    /**
     * Rounds float coordinates to the nearest integer components.
     * * @return A new {@link Vector3i} instance.
     */
    public static Vector3i toInt(Vector3f vector) {
        return new Vector3i(Math.round(vector.x), Math.round(vector.y), Math.round(vector.z));
    }
    
    /**
     * Converts a vector to a human-readable string (e.g., "x: 10, y: 20, z: 30").
     */
    public static String toString(Vector3i vector) {
        return "x: " + vector.x + ", y: " + vector.y + ", z: " + vector.z;
    }
    
    public static Vector2i copy(Vector2i vector) {
        return new Vector2i(vector);
    }
    
    public static float distanceSquared(float t1, float h1, float t2, float h2) {
        float dt = t1 - t2;
        float dh = h1 - h2;
        return dt * dt + dh * dh;
    }
    
    public static <T> List<T> toList(T[] array) {
        return Arrays.stream(array).toList();
    }
    
    /**
     * Converts a vector to a human-readable string (e.g., "x: 10, y: 20").
     */
    public static String toString(Vector2i vector) {
        return "x: " + vector.x + ", y: " + vector.y;
    }
}