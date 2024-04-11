package com.example.joyserial.data;

import androidx.annotation.NonNull;

import com.example.joyserial.utils.PolarCord;

public class Gamepad {
    public int MAX_THETA = 255;
    public int MAX_RADIUS = 255;
    public char END_DELIMITER = '>';
    private boolean A, B, X, Y, pause;
    private int hat;
    private PolarCord rthumb, lthumb;
    private static Gamepad instance;
    private Gamepad() {
        this.A = false;
        this.B = false;
        this.X = false;
        this.Y = false;
        this.pause = false;
        this.hat = 0;

        this.rthumb = new PolarCord(0,0);
        this.lthumb = new PolarCord(0, 0);
    }

    public static synchronized Gamepad getInstance() {
        if (instance == null) {
            instance = new Gamepad();
            return instance;
        } else return instance;
    }

    public boolean A() {
        return this.A;
    }
    public boolean B() {
        return this.B;
    }
    public boolean X() {
        return this.X;
    }
    public boolean Y() {
        return this.Y;
    }
    public boolean pause() { return this.pause; }
    public int hat() {
        return this.hat;
    }
    public synchronized void hat(int __value) {
        this.hat = __value;
    }
    public synchronized void A(boolean v) {
        this.A = v;
    }
    public synchronized void B(boolean v) {
        this.B = v;
    }
    public synchronized void X(boolean v) {
        this.X = v;
    }
    public synchronized void Y(boolean v) { this.Y = v; }
    public synchronized void pause(boolean p) { this.pause = p; }
    public PolarCord rthumb () {
        return this.rthumb;
    }
    public PolarCord lthumb () {
        return this.lthumb;
    }
    public void rthumb (int radius, int theta) {
        this.rthumb.radius = radius * MAX_RADIUS/100;
        this.rthumb.theta = theta * MAX_THETA/360;
    }
    public void lthumb (int radius, int theta) {
        this.lthumb.radius = radius * MAX_RADIUS/100;
        this.lthumb.theta = theta * MAX_THETA/360;
    }
    @NonNull
    @Override
    public synchronized String toString() {
        return "A: " + this.A+ ", B:" + this.B + ", X:"  + this.X + ", Y:"  + this.Y + ", Hat:" + this.hat
                +", RightThumb:" + this.rthumb.radius + " " + this.rthumb.theta
                +", LeftThumb:" + this.lthumb.radius + " " + this.lthumb.theta;
    }
    public synchronized byte[] toByteArray() {
        byte[] data = new byte[10];
        data[0] = (byte) (this.A ? 1 : 0);
        data[1] = (byte) (this.B ? 1 : 0);
        data[2] = (byte) (this.X ? 1 : 0);
        data[3] = (byte) (this.Y ? 1 : 0);
        data[4] = (byte) this.hat;
        data[5] = escapeDelimiter(this.rthumb.radius);
        int rightTheta = this.rthumb.theta;
        data[6] = escapeDelimiter(rightTheta);
        data[7] = escapeDelimiter(this.lthumb.radius);
        int leftTheta = this.lthumb.theta;
        data[8] = escapeDelimiter(leftTheta);
        data[9] = (byte) '<';

        return data;
    }
    private byte escapeDelimiter(int val) {
        return (byte) ((char) val == END_DELIMITER ? val+1: val);
    }
}
