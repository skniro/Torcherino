package torcherino.api;

public class Tier {
    public final int MAX_SPEED;
    public final int XZ_RANGE;
    public final int Y_RANGE;

    public Tier(final int maxSpeed, final int xzRange, final int yRange) {
        MAX_SPEED = maxSpeed;
        XZ_RANGE = xzRange;
        Y_RANGE = yRange;
    }
}
