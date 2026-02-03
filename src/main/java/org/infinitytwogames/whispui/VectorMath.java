package org.infinitytwogames.whispui;

import org.infinitytwogames.whispui.buffer.NIntBuffer;
import org.joml.*;
import org.joml.Math;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

// MODIFIED
public class VectorMath {
    public static boolean isPointWithinRectangle(Vector2i topLeft, Vector2i point, Vector2i bottomRight) {
        int minX = topLeft.x();
        int maxX = bottomRight.x();
        int minY = topLeft.y();
        int maxY = bottomRight.y();

        int x = point.x();
        int y = point.y();

        return x >= minX && x <= maxX && y >= minY && y <= maxY;
    }

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
     * Optimized rectangle check.
     * Assumes topLeft is actually top-left (minX, minY) and bottomRight is (maxX, maxY).
     */
    public static boolean isPointInRect(Vector2i pos, Vector2i size, int px, int py) {
        return isPointWithinRectangle(pos.x, pos.y, px, py, size.x, size.y);
    }
    
    /**
     * Converts Vector3i to Vector3f without creating a NEW object if possible.
     * Pass the 'dest' vector to reuse memory.
     */
    public static Vector3f toFloat(Vector3i source, Vector3f dest) {
        return dest.set(source.x, source.y, source.z);
    }
    
    /**
     * Voxel-specific distance squared (XZ plane only).
     * Useful for checking if a player is within chunk-loading range.
     */
    public static float distanceSquaredXZ(float x1, float z1, float x2, float z2) {
        float dx = x1 - x2;
        float dz = z1 - z2;
        return dx * dx + dz * dz;
    }
    
    public static String toStringAsId(Vector2i v) {
        // Using StringBuilder is faster than string concatenation in IDs
        return String.valueOf(v.x) + '-' + v.y;
    }
    
    public static void fill(NIntBuffer buffer, int x, int y, int z) {
        buffer.require(3);
        buffer.put(x);
        buffer.put(y);
        buffer.put(z);
    }

    public static byte[] serialize(Vector3f v) {
        ByteBuffer buffer = ByteBuffer.allocate(3 * Float.BYTES);
        buffer.putFloat(v.x); buffer.putFloat(v.y); buffer.putFloat(v.z);
        return buffer.array();
    }
    
    public static Vector3f toFloat(Vector3i vector) {
        return new Vector3f(vector);
    }
    
    public static Vector3i toInt(Vector3f vector) {
        return new Vector3i(Math.round(vector.x), Math.round(vector.y), Math.round(vector.z));
    }
    
    public static String toString(Vector3i vector) {
        return "x: "+vector.x + ", y: "+ vector.y+ ", z: "+vector.z;
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
    
    public static String toString(Vector2i vector) {
        return "x: "+vector.x + ", y: "+ vector.y;
    }
}