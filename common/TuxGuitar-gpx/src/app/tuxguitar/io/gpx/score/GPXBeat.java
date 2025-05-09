package app.tuxguitar.io.gpx.score;

public class GPXBeat {

	private int id;
	private int rhythmId;
	private int[] noteIds;
	private String dynamic;
	private boolean slapped;
	private boolean popped;
	private String brush;	// up stroke, down stroke
	private String pickStroke;
	private int[] tremolo; // 1/8, 1/4, etc.
	private String fadding;
	private String text;
	private Integer chordId;
	private String graceNotes;

	private boolean whammyBarEnabled;
	private Integer whammyBarOriginValue;
	private Integer whammyBarMiddleValue;
	private Integer whammyBarDestinationValue;
	private Integer whammyBarOriginOffset;
	private Integer whammyBarMiddleOffset1;
	private Integer whammyBarMiddleOffset2;
	private Integer whammyBarDestinationOffset;

	public GPXBeat(){
		this.slapped = false;
		this.popped = false;
		this.tremolo = null;
		this.brush = new String();
		this.pickStroke = new String();
		this.text = new String();
		this.graceNotes = new String();
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getRhythmId() {
		return this.rhythmId;
	}

	public void setRhythmId(int rhythmId) {
		this.rhythmId = rhythmId;
	}

	public int[] getNoteIds() {
		return this.noteIds;
	}

	public void setNoteIds(int[] noteIds) {
		this.noteIds = noteIds;
	}

	public String getDynamic() {
		return this.dynamic;
	}

	public void setDynamic(String dynamic) {
		this.dynamic = dynamic;
	}

	public boolean isSlapped() {
		return slapped;
	}

	public void setSlapped(boolean slapped) {
		this.slapped = slapped;
	}

	public boolean isPopped() {
		return popped;
	}

	public void setPopped(boolean popped) {
		this.popped = popped;
	}

	public String getBrush() {
		return brush;
	}

	public void setBrush(String brush) {
		this.brush = brush;
	}

	public String getPickStroke() {
		return pickStroke;
	}

	public void setPickStroke(String pickStroke) {
		this.pickStroke = pickStroke;
	}

	public int[] getTremolo() {
		return tremolo;
	}

	public void setTremolo(int[] tremolo) {
		this.tremolo = tremolo;
	}

	public String getFadding() {
		return fadding;
	}

	public void setFadding(String fadding) {
		this.fadding = fadding;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Integer getChordId() {
		return chordId;
	}

	public void setChordId(Integer chordId) {
		this.chordId = chordId;
	}

	public String getGraceNotes() {
		return graceNotes;
	}

	public void setGraceNotes(String graceNotes) {
		this.graceNotes = graceNotes;
	}

	public boolean isWhammyBarEnabled() {
		return whammyBarEnabled;
	}

	public void setWhammyBarEnabled(boolean whammyBarEnabled) {
		this.whammyBarEnabled = whammyBarEnabled;
	}

	public Integer getWhammyBarOriginValue() {
		return whammyBarOriginValue;
	}

	public void setWhammyBarOriginValue(Integer whammyBarOriginValue) {
		this.whammyBarOriginValue = whammyBarOriginValue;
	}

	public Integer getWhammyBarMiddleValue() {
		return whammyBarMiddleValue;
	}

	public void setWhammyBarMiddleValue(Integer whammyBarMiddleValue) {
		this.whammyBarMiddleValue = whammyBarMiddleValue;
	}

	public Integer getWhammyBarDestinationValue() {
		return whammyBarDestinationValue;
	}

	public void setWhammyBarDestinationValue(Integer whammyBarDestinationValue) {
		this.whammyBarDestinationValue = whammyBarDestinationValue;
	}

	public Integer getWhammyBarOriginOffset() {
		return whammyBarOriginOffset;
	}

	public void setWhammyBarOriginOffset(Integer whammyBarOriginOffset) {
		this.whammyBarOriginOffset = whammyBarOriginOffset;
	}

	public Integer getWhammyBarMiddleOffset1() {
		return whammyBarMiddleOffset1;
	}

	public void setWhammyBarMiddleOffset1(Integer whammyBarMiddleOffset1) {
		this.whammyBarMiddleOffset1 = whammyBarMiddleOffset1;
	}

	public Integer getWhammyBarMiddleOffset2() {
		return whammyBarMiddleOffset2;
	}

	public void setWhammyBarMiddleOffset2(Integer whammyBarMiddleOffset2) {
		this.whammyBarMiddleOffset2 = whammyBarMiddleOffset2;
	}

	public Integer getWhammyBarDestinationOffset() {
		return whammyBarDestinationOffset;
	}

	public void setWhammyBarDestinationOffset(Integer whammyBarDestinationOffset) {
		this.whammyBarDestinationOffset = whammyBarDestinationOffset;
	}
}
