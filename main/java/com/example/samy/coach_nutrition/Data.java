package com.example.samy.coach_nutrition;

public class Data {
    private int x;
    private float y;

    public Data(int x, float y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return new String("x : " + this.x + " y : " + this.y);
    }
}
