package com.bknote71.codecraft.proto;


import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;

@JsonTypeName("sdie")
public class SDie extends Protocol {

    private int id;
    private double x;
    private double y;

    public SDie() {
        super(ProtocolType.S_Die);
    }

    public int getId() {
        return id;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }
}
