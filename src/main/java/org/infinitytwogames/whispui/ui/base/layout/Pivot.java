package org.infinitytwogames.whispui.ui.base.layout;

public final class Pivot {
    public float x = 0, y = 0;

    public Pivot(float x, float y) {
        this.x = -x;
        this.y = -y;
    }

    public Pivot() {}
    
    public Pivot(float v) {
        this(v,v);
    }
    
    public void set(float x, float y) {
        this.x=x;
        this.y=y;
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

    public void set(Pivot pivot) {
        x = pivot.x();
        y = pivot.y();
    }
}
