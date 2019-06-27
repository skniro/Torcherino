package torcherino.api;

public class Tier
{
	public final int MAX_SPEED;
	public final int XZ_RANGE;
	public final int Y_RANGE;

	public Tier(int maxSpeed, int xzRange, int yRange)
	{
		this.MAX_SPEED = maxSpeed;
		this.XZ_RANGE = xzRange;
		this.Y_RANGE = yRange;
	}
}