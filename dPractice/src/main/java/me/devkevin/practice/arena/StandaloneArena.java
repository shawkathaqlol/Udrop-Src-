package me.devkevin.practice.arena;

import me.devkevin.practice.CustomLocation;

public class StandaloneArena {
    private CustomLocation a;
    private CustomLocation b;
    private CustomLocation min;
    private CustomLocation max;

    public StandaloneArena(final CustomLocation a, final CustomLocation b, final CustomLocation min, final CustomLocation max) {
        this.a = a;
        this.b = b;
        this.min = min;
        this.max = max;
    }

    public StandaloneArena() {
    }

    public CustomLocation getA() {
        return this.a;
    }

    public void setA(final CustomLocation a) {
        this.a = a;
    }

    public CustomLocation getB() {
        return this.b;
    }

    public void setB(final CustomLocation b) {
        this.b = b;
    }

    public CustomLocation getMin() {
        return this.min;
    }

    public void setMin(final CustomLocation min) {
        this.min = min;
    }

    public CustomLocation getMax() {
        return this.max;
    }

    public void setMax(final CustomLocation max) {
        this.max = max;
    }
}
