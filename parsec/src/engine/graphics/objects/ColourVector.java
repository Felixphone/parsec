package engine.graphics.objects;

public class ColourVector {

    private float red, green, blue, alpha;

    public ColourVector(float red, float green, float blue) {
        set(red, green, blue);
    }

    public ColourVector(float red, float green, float blue, float alpha) {
        set(red, green, blue, alpha);
    }

    public void set(float red, float green, float blue) {
        set(red, green, blue, 1.0f);
    }

    public void set(float red, float green, float blue, float alpha) {
        this.red = red;
        this.blue = blue;
        this.green = green;
        this.alpha = alpha;
    }

    public float getRed() {
        return red;
    }

    public float getGreen() {
        return green;
    }

    public float getBlue() {
        return blue;
    }

    public float getAlpha() {
        return alpha;
    }
}
