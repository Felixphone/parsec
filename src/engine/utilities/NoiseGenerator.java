package engine.utilities;
public class NoiseGenerator {

    private int    octaves;
    private double amplitude;
    private double frequency;
    private double persistence;
    private int    seed;

    public NoiseGenerator(int seed, double persistence, double frequency, double amplitude, int octaves) {
        this.seed        = seed * seed;
        this.octaves     = octaves;
        this.amplitude   = amplitude;
        this.frequency   = frequency;
        this.persistence = persistence;
    }

    public double get(double x, double y) {
        return amplitude * total(x,y);
    }

    private double total(double x, double y) {
        double total    = 0.0;
        double amplitude  = 1;
        double freq = frequency;

        for(int i = 0; i < octaves; i++) {

            total += getValue(y * freq + seed, x * freq + seed) * amplitude;
            amplitude *= persistence;
            freq *= 2;
        }

        return total;
    }

    private double getValue(double x, double y) {
        int intX = (int) x;
        int intY = (int) y;
        double Xfrac = x - intX;
        double Yfrac = y - intY;

        //get noise
        double noise1 = noise(intX - 1, intY - 1);
        double noise2 = noise(intX + 1, intY - 1);
        double noise3 = noise(intX - 1, intY + 1);
        double noise4 = noise(intX + 1, intY + 1);
        double noise5 = noise(intX - 1, intY);
        double noise6 = noise(intX + 1, intY);
        double noise7 = noise(intX, intY - 1);
        double noise8 = noise(intX, intY + 1);
        double noise9 = noise(intX, intY);
        double noise10 = noise(intX + 2, intY - 1);
        double noise11 = noise(intX + 2, intY + 1);
        double noise12 = noise(intX + 2, intY);
        double noise13 = noise(intX - 1, intY + 2);
        double noise14 = noise(intX + 1, intY + 2);
        double noise15 = noise(intX, intY + 2);
        double noise16 = noise(intX + 2, intY + 2);

        // generate corner noise
        double bottomLeft = 0.0625 * (noise1 + noise2 + noise3 + noise4) + 0.125 * (noise5 + noise6 + noise7 + noise8) + 0.25 * (noise9);
        double topLeft = 0.0625 * (noise7 + noise10 + noise8 + noise11) + 0.125 * (noise9 + noise12 + noise2 + noise4) + 0.25 * (noise6);
        double bottomRight = 0.0625 * (noise5 + noise6 + noise13 + noise14) + 0.125 * (noise3 + noise4 + noise9 + noise15) + 0.25 * (noise8);
        double topRight = 0.0625 * (noise9 + noise12 + noise15 + noise16) + 0.125 * (noise8 + noise11 + noise6 + noise14) + 0.25 * (noise4);

        // interpolate
        double v1 = interpolate(bottomLeft, topLeft, Xfrac); // x
        double v2 = interpolate(bottomRight, topRight, Xfrac); // x
        double result = interpolate(v1, v2, Yfrac);    // y

        return result;
    }

    private double interpolate(double x, double y, double a) {
        double negA = 1.0 - a;
        double negASqr = negA * negA;
        double fac1 = 3.0 * (negASqr) - 2.0 * (negASqr * negA);
        double aSqr = a * a;
        double fac2 = 3.0 * aSqr - 2.0 * (aSqr * a);

        return x * fac1 + y * fac2;
    }

    private double noise(int x, int y) {
        int n = x + y * 57;
        n = (n << 13) ^ n;
        int t = (n * (n * n * 15731 + 789221) + 1376312589) & 0x7fffffff;
        return 1.0 - (double) (t) * 0.931322574615478515625e-9;
    }
}