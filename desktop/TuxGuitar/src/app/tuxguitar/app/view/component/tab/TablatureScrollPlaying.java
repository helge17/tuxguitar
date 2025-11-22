package app.tuxguitar.app.view.component.tab;

import app.tuxguitar.app.system.config.TGConfigKeys;
import app.tuxguitar.app.system.config.TGConfigManager;
import app.tuxguitar.graphics.control.TGLayout;
import app.tuxguitar.graphics.control.TGMeasureImpl;
import app.tuxguitar.player.base.MidiPlayerMode;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.ui.resource.UIRectangle;
import app.tuxguitar.util.TGContext;

public class TablatureScrollPlaying {

	private final static int SCROLL_MARGIN = 5;		// to avoid reaching target a bit early (that would stop scrolling for a short delay)

	private long tLastUpdateScrollMs;
	private long tlastUpdateTargetSpeedMs;
	private int lastUpdateTargetSpeedMeasureNumber;
	private float targetSpeed;
	private Integer lastDirection;
	private float toScroll = 0f;
	private float speed;
	private long minDT;
	
	public TablatureScrollPlaying(TGContext context) {
		this.lastDirection = null;
		this.minDT = 1000 / TGConfigManager.getInstance(context).getIntegerValue(TGConfigKeys.SCROLLING_MAX_FPS);
	}

	public void reset(int direction) {
		if ((this.lastDirection == null) || (this.lastDirection != direction)) {
			this.lastDirection = direction;
		}
		this.reset();
	}

	private void reset() {
		this.tLastUpdateScrollMs = 0;
		this.tlastUpdateTargetSpeedMs = 0;
		this.lastUpdateTargetSpeedMeasureNumber = 0;
		this.targetSpeed = 0f;
		this.toScroll = 0f;
		this.speed = 0f;
	}

	public Integer scrollTo(TGMeasureImpl measure, int pos, int target, MidiPlayerMode mode, UIRectangle area, TGLayout layout) {
		// target measure not visible? Immediate jump
		if (!layout.isFullyVisible(measure, area)) {
			return this.jumpTo(pos, target);
		}
		
		/* don't scroll if
		- end reached: last scrollable item(line or measure) = first measure with timeToNextScrollMs zero
		- OR endLoop already visible
		*/
		boolean needToScroll = true;
		TGTrack track = measure.getTrack();
		if (mode.isLoop() && layout.isFullyVisible((TGMeasureImpl)track.getMeasure(mode.getLoopEHeader()), area)) {
			needToScroll = false;
		} else {
			// get last scrollable measure
			int lastIndex = track.countMeasures()-1;
			for (int i=track.countMeasures()-2; i>=0; i--) {
				Integer timeToNextScroll =layout.getTimeToNextScrollMs(i); 
				if ((timeToNextScroll != null) && (timeToNextScroll == 0)) {
					lastIndex = i;
				}
				else {
					break;
				}
			}
			if (layout.isFullyVisible((TGMeasureImpl)track.getMeasure(lastIndex), area)) {
				needToScroll = false;
			}
		}
		if (needToScroll) {
			return this.continousScrollTo(pos, target, mode, measure, layout);
		}
		
		return null;
	}

	private Integer continousScrollTo(int pos, int target, MidiPlayerMode mode, TGMeasureImpl measure, TGLayout layout) {
		Integer scrollIncrement = null;
		int delta = pos - target + SCROLL_MARGIN;
		if (delta <= 0) {
			this.reset();
			return null;
		}
		long t = System.currentTimeMillis();
		long dt = t -this.tLastUpdateScrollMs;
		if (this.tLastUpdateScrollMs > 0) {
			if (dt >= this.minDT) {
				this.toScroll += dt * this.speed / 1000;
				if ((int)Math.round(toScroll) > 0) {
					scrollIncrement = (int)Math.round(toScroll);
					this.toScroll -= scrollIncrement;
				}
				this.updateSpeed(t, pos, target, mode, measure, layout);
			}
		}
		if (dt >= this.minDT || this.tLastUpdateScrollMs==0) {
			this.tLastUpdateScrollMs = t;
		}
		return scrollIncrement;
	}

	private void updateSpeed(long t, int pos, int target, MidiPlayerMode mode, TGMeasureImpl measure, TGLayout layout) {
		int distanceToScroll = pos - target;
		if (distanceToScroll <= 0) {
			this.reset();
			return;
		}
		Integer timeToScrollMs = layout.getTimeToNextScrollMs(measure.getNumber()-1);
		if (timeToScrollMs == null) {
			// layout not yet initialized
			return;
		}
		// consider effective tempo
		timeToScrollMs = timeToScrollMs * 100 / mode.getCurrentPercent();
		// update target speed : at initialization, then once per measure, but only if layout is initialized
		if ((timeToScrollMs != null) && ((this.targetSpeed == 0f) || (Boolean.TRUE.equals(measure.getNumber() != this.lastUpdateTargetSpeedMeasureNumber)))) {
			this.lastUpdateTargetSpeedMeasureNumber = measure.getNumber();
			this.tlastUpdateTargetSpeedMs = t;
			this.targetSpeed = 1000 * distanceToScroll / timeToScrollMs;
		} else {
			timeToScrollMs = (int) (timeToScrollMs - t + this.tlastUpdateTargetSpeedMs);
		}
		float maxSpeed = 0f;
		if (timeToScrollMs > 0) {
			maxSpeed = 1000 * distanceToScroll / timeToScrollMs;	// no faster than that, or played measure may get out of displayed area
			if (this.speed == 0) { //arbitrary
				// don't start too slow (use case: start with caret in first displayed measure)
				// but don't start too fast either (use case: start with caret in last displayed measure, 
				//		it's better here to fail the continuous scrolling than to speed up too much)
				this.speed = maxSpeed / 3;
			}
			else if ((this.speed >= maxSpeed) && (distanceToScroll <= layout.getMeasureScrollableSize(measure))){
				this.speed = maxSpeed;	// don't exceed this speed when getting close from start of display area (do not mask played measure)
			}
			else {
				// low pass filter, arbitrary parameters
				long lpf = this.speed < this.targetSpeed ? 
								  3 * measure.getHeader().getDurationInMs()			// short filter duration: brake fast
								: measure.getHeader().getDurationInMs() / 3;		// long  filter duration: accelerate slowly
				this.speed += (this.targetSpeed - this.speed) * (t - this.tLastUpdateScrollMs) / lpf;
			}
		}
	}

	// discrete scroll
	private int jumpTo(int pos, int target) {
		this.reset();
		return pos - target;
	}

}
