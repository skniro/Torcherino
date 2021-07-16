package torcherino.api;

// todo: 1.17, convert to record
@SuppressWarnings("ClassCanBeRecord")
public class Tier {
    private final int maxSpeed;
    private final int xzRange;
    private final int yRange;

    public Tier(int maxSpeed, int xzRange, int yRange) {
        this.maxSpeed = maxSpeed;
        this.xzRange = xzRange;
        this.yRange = yRange;
    }

    public int getMaxSpeed() {
        return maxSpeed;
    }

    public int getXZRange() {
        return xzRange;
    }

    public int getYRange() {
        return yRange;
    }
}
