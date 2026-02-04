package app.tuxguitar.player.base;

public class MidiPlayerMode{

	public static final int SIMPLE_DEFAULT_TEMPO_PERCENT = 100;

	public static final int CUSTOM_DEFAULT_TEMPO_PERCENT_FROM = 80;
	public static final int CUSTOM_DEFAULT_TEMPO_PERCENT_TO = 100;
	public static final int CUSTOM_DEFAULT_TEMPO_PERCENT_INCREMENT = 5;

	public static final int TYPE_SIMPLE = 1;
	public static final int TYPE_CUSTOM = 2;

	private int type;
	private boolean loop;
	private int loopSHeader;
	private int loopEHeader;
	private int simplePercent;
	private int customPercentFrom;
	private int customPercentTo;
	private int customPercentIncrement;
	private int currentPercent;

	public MidiPlayerMode(){
		this.clear();
	}

	public void clear(){
		this.loop = false;
		this.type = TYPE_SIMPLE;
		this.simplePercent = SIMPLE_DEFAULT_TEMPO_PERCENT;
		this.customPercentFrom = CUSTOM_DEFAULT_TEMPO_PERCENT_FROM;
		this.customPercentTo = CUSTOM_DEFAULT_TEMPO_PERCENT_TO;
		this.customPercentIncrement = CUSTOM_DEFAULT_TEMPO_PERCENT_INCREMENT;
		this.loopSHeader = -1;
		this.loopEHeader = -1;
		this.reset();
	}

	public void reset(){
		if(getType() == TYPE_SIMPLE){
			this.setCurrentPercent(getSimplePercent());
		}
		else if(getType() == TYPE_CUSTOM){
			this.setCurrentPercent(getCustomPercentFrom());
		}
	}

	public void notifyLoop(){
		if(getType() == TYPE_SIMPLE){
			this.setCurrentPercent(getSimplePercent());
		}
		else if(getType() == TYPE_CUSTOM){
			this.setCurrentPercent(Math.min(getCustomPercentTo(),(getCurrentPercent() + getCustomPercentIncrement())));
		}
	}

	public int getCurrentPercent(){
		return this.currentPercent;
	}

	public void setCurrentPercent(int currentPercent){
		this.currentPercent = currentPercent;
	}

	public boolean isLoop() {
		return this.loop;
	}

	public void setLoop(boolean loop) {
		this.loop = loop;
	}

	public int getType() {
		return this.type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getCustomPercentFrom() {
		return this.customPercentFrom;
	}

	public void setCustomPercentFrom(int customPercentFrom) {
		this.customPercentFrom = customPercentFrom;
	}

	public int getCustomPercentIncrement() {
		return this.customPercentIncrement;
	}

	public void setCustomPercentIncrement(int customPercentIncrement) {
		this.customPercentIncrement = customPercentIncrement;
	}

	public int getCustomPercentTo() {
		return this.customPercentTo;
	}

	public void setCustomPercentTo(int customPercentTo) {
		this.customPercentTo = customPercentTo;
	}

	public int getSimplePercent() {
		return this.simplePercent;
	}

	public void setSimplePercent(int simplePercent) {
		this.simplePercent = simplePercent;
	}

	public int getLoopSHeader() {
		return this.loopSHeader;
	}

	public void setLoopSHeader(int loopSHeader) {
		this.loopSHeader = loopSHeader;
	}

	public int getLoopEHeader() {
		return this.loopEHeader;
	}

	public void setLoopEHeader(int loopEHeader) {
		this.loopEHeader = loopEHeader;
	}

	public void copyFrom(MidiPlayerMode mode) {
		if (mode != null) {
			this.type = mode.getType();
			this.loop = mode.isLoop();
			this.loopSHeader = mode.getLoopSHeader();
			this.loopEHeader = mode.getLoopEHeader();
			this.simplePercent = mode.getSimplePercent();
			this.customPercentFrom = mode.getCustomPercentFrom();
			this.customPercentTo = mode.getCustomPercentTo();
			this.customPercentIncrement = mode.getCustomPercentIncrement();
			this.currentPercent = mode.getCurrentPercent();
		}
	}
}
