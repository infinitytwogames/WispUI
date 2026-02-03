package org.infinitytwogames.whispui.ui.base.layout;

import org.joml.Vector2f;

public class Anchor {
    public float x;
    public float y;

    public Anchor(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Anchor(Vector2f pos) {
        this.x = pos.x;
        this.y = pos.y;
    }

    public Anchor() {
        x = 0;
        y = 0;
    }
    
    public Anchor(float v) {
        this(v,v);
    }
    
    @Override
    public String toString() {
        return "("+x+", "+y+")";
    }

    public void set(Anchor anchor) {
        x = anchor.x;
        y = anchor.y;
    }

    public float x() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float y() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void set(float x, float y) {
        this.x = x;
        this.y = y;
    }
}
