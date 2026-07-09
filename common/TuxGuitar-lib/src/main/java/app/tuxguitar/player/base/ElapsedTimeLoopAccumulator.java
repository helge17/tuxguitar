package app.tuxguitar.player.base;

public class ElapsedTimeLoopAccumulator {

	private long baseElapsedTime;
	private long lastTimestamp;

	public ElapsedTimeLoopAccumulator() {
		this.reset();
	}

	public long accumulate(long currentTimestamp) {
		if (currentTimestamp < this.lastTimestamp) {
			this.baseElapsedTime += this.lastTimestamp;
		}

		this.lastTimestamp = currentTimestamp;
		return (this.baseElapsedTime + currentTimestamp);
	}

	public void reset() {
		this.baseElapsedTime = 0;
		this.lastTimestamp = 0;
	}
}
