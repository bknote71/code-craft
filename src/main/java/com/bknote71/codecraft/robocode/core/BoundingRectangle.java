package com.bknote71.codecraft.robocode.core;

import java.awt.geom.Rectangle2D;

public class BoundingRectangle extends Rectangle2D.Float {

    public BoundingRectangle() {
        super();
    }

    public BoundingRectangle(double x, double y, double w, double h) {
        super((float) x, (float) y, (float) w, (float) h);
    }

}
