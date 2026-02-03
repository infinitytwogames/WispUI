package org.infinitytwogames.whispui.ui.base.layout;

import org.infinitytwogames.whispui.data.RGBA;
import org.infinitytwogames.whispui.manager.Mouse;
import org.infinitytwogames.whispui.ui.base.UI;
import org.infinitytwogames.whispui.ui.base.component.Component;
import org.joml.Vector2i;

import java.util.HashMap;
import java.util.Map;

public class Template {
    protected RGBA backgroundColor = new RGBA();
    protected RGBA borderColor = new RGBA();
    
    protected int width = 0;
    protected int height = 0;
    protected float angle = 0;
    protected int drawOrder = 0; // z
    protected float cornerRadius = 0;
    protected float borderThickness = 0;
    
    protected Anchor anchor = new Anchor();
    protected Pivot pivot = new Pivot(0,0);
    protected Vector2i offset = new Vector2i();
    protected UI parent;
    protected Map<String, Component> components = new HashMap<>();
    protected Mouse.CursorType cursorType = Mouse.CursorType.ARROW;
    
    public void apply(UI ui) {
        ui.setBackgroundColor(backgroundColor);
        ui.setBorderColor(borderColor);
        ui.setWidth(width);
        ui.setHeight(height);
        ui.setAngle(angle);
        ui.setCornerRadius(cornerRadius);
        ui.setBorderThickness(borderThickness);
        ui.setPosition(anchor, pivot, offset);
        ui.setParent(parent);
        ui.addComponent(components);
        ui.setCursorType(cursorType);
    }
    
    public RGBA getBackgroundColor() {
        return backgroundColor;
    }
    
    public void setBackgroundColor(RGBA backgroundColor) {
        this.backgroundColor.set(backgroundColor);
    }
    
    public RGBA getBorderColor() {
        return borderColor;
    }
    
    public void setBorderColor(RGBA borderColor) {
        this.borderColor.set(borderColor);
    }
    
    public int getWidth() {
        return width;
    }
    
    public void setWidth(int width) {
        this.width = width;
    }
    
    public int getHeight() {
        return height;
    }
    
    public void setHeight(int height) {
        this.height = height;
    }
    
    public float getAngle() {
        return angle;
    }
    
    public void setAngle(float angle) {
        this.angle = angle;
    }
    
    public int getDrawOrder() {
        return drawOrder;
    }
    
    public void setDrawOrder(int drawOrder) {
        this.drawOrder = drawOrder;
    }
    
    public float getCornerRadius() {
        return cornerRadius;
    }
    
    public void setCornerRadius(float cornerRadius) {
        this.cornerRadius = cornerRadius;
    }
    
    public float getBorderThickness() {
        return borderThickness;
    }
    
    public void setBorderThickness(float borderThickness) {
        this.borderThickness = borderThickness;
    }
    
    public Anchor getAnchor() {
        return anchor;
    }
    
    public void setAnchor(Anchor anchor) {
        this.anchor = anchor;
    }
    
    public Pivot getPivot() {
        return pivot;
    }
    
    public void setPivot(Pivot pivot) {
        this.pivot = pivot;
    }
    
    public Vector2i getOffset() {
        return offset;
    }
    
    public void setOffset(Vector2i offset) {
        this.offset = offset;
    }
    
    public UI getParent() {
        return parent;
    }
    
    public void setParent(UI parent) {
        this.parent = parent;
    }
    
    public Map<String, Component> getComponents() {
        return components;
    }
    
    public void addComponent(Map<String, Component> components) {
        this.components.putAll(components);
    }
    
    public void addComponent(String name, Component component) {
        components.put(name,component);
    }
    
    public void addComponent(Component component) {
        addComponent(component.getClass().getSimpleName(), component);
    }
    
    public Mouse.CursorType getCursorType() {
        return cursorType;
    }
    
    public void setCursorType(Mouse.CursorType cursorType) {
        this.cursorType = cursorType;
    }
    
    public void setSize(int width, int height) {
        setWidth(width); setHeight(height);
    }
}
