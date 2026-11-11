package app.tuxguitar.graphics.control;

import java.util.Iterator;

import app.tuxguitar.graphics.control.painters.TGNotePainter;
import app.tuxguitar.graphics.control.painters.TGSilencePainter;
import app.tuxguitar.song.factory.TGFactory;
import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.song.models.TGNote;
import app.tuxguitar.song.models.TGVoice;
import app.tuxguitar.ui.resource.UIPainter;

public class TGVoiceImpl extends TGVoice{
	/**
	 * desviacion a la izquierda
	 */
	public static final int JOINED_TYPE_NONE_LEFT = 1;
	/**
	 * desviacion a la derecha
	 */
	public static final int JOINED_TYPE_NONE_RIGHT = 2;
	/**
	 * Union a la izquierda
	 */
	public static final int JOINED_TYPE_LEFT = 3;
	/**
	 * Union a la derecha
	 */
	public static final int JOINED_TYPE_RIGHT = 4;

	private float width;
	private TGNoteImpl maxNote;
	private TGNoteImpl minNote;
	private boolean[] usedStrings;
	private int joinedType;
	private boolean joinedGreaterThanQuarter;
	private TGVoiceImpl join1;
	private TGVoiceImpl join2;
	private TGVoiceImpl previous;
	private TGVoiceImpl next;
	private TGBeatGroup group;

	private boolean hiddenSilence;

	private float maxY;
	private float minY;
	private float silenceY;
	private float silenceHeight;

	private int maxString;
	private int minString;

	public TGVoiceImpl(TGFactory factory,int index){
		super(factory, index);
	}

	public float getPosX() {
		return this.getBeatImpl().getPosX();
	}

	public float getWidth() {
		return this.width;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public TGNoteImpl getMinNote(){
		return this.minNote;
	}

	public TGNoteImpl getMaxNote(){
		return this.maxNote;
	}

	public TGBeatImpl getBeatImpl() {
		return (TGBeatImpl)getBeat();
	}

	public TGMeasureImpl getMeasureImpl() {
		return (TGMeasureImpl)getBeat().getMeasure();
	}

	public boolean[] getUsedStrings() {
		if( this.usedStrings == null ){
			this.usedStrings = new boolean[getBeat().getMeasure().getTrack().stringCount()];
		}
		return this.usedStrings;
	}

	public TGVoiceImpl getJoin1() {
		return this.join1;
	}

	public void setJoin1(TGVoiceImpl join1) {
		this.join1 = join1;
	}

	public TGVoiceImpl getJoin2() {
		return this.join2;
	}

	public void setJoin2(TGVoiceImpl join2) {
		this.join2 = join2;
	}

	public boolean isJoinedGreaterThanQuarter() {
		return this.joinedGreaterThanQuarter;
	}

	public void setJoinedGreaterThanQuarter(boolean joinedGreaterThanQuarter) {
		this.joinedGreaterThanQuarter = joinedGreaterThanQuarter;
	}

	public int getJoinedType() {
		return this.joinedType;
	}

	public void setJoinedType(int joinedType) {
		this.joinedType = joinedType;
	}

	public void setPreviousBeat(TGVoiceImpl previous){
		this.previous = previous;
	}

	public void setNextBeat(TGVoiceImpl next){
		this.next = next;
	}

	public TGBeatGroup getBeatGroup() {
		return this.group;
	}

	public void setBeatGroup(TGBeatGroup group) {
		this.group = group;
	}

	public boolean isPlaying(TGLayout layout){
		return layout.getComponent().isRunning(this.getBeat());
	}

	public void reset(){
		this.maxNote = null;
		this.minNote = null;
		this.hiddenSilence = false;
		this.usedStrings = new boolean[getBeat().getMeasure().getTrack().stringCount()];
		this.maxString = 1;
		this.minString = getBeat().getMeasure().getTrack().stringCount();
		this.group = null;
		this.previous = null;
		this.next = null;
	}

	public void check(TGLayout layout, TGNoteImpl note){
		int value = TGNotation.computePosition(layout, note);
		if( this.maxNote == null || value > TGNotation.computePosition(layout, this.maxNote)){
			this.maxNote = note;
		}
		if( this.minNote == null || value < TGNotation.computePosition(layout, this.minNote)){
			this.minNote = note;
		}
		this.getUsedStrings();
		this.usedStrings[note.getString() - 1] = true;

		if( note.getString() > this.maxString ){
			this.maxString = note.getString();
		}
		if( note.getString() < this.minString ){
			this.minString = note.getString();
		}
	}

	public void update(TGLayout layout) {
		this.minY = 0;
		this.maxY = 0;
		if(isRestVoice()){
			this.updateSilenceSpacing(layout);
		}
		else{
			this.updateNoteVoice(layout);
		}
	}

	public void updateNoteVoice(TGLayout layout) {
		this.joinedType = JOINED_TYPE_NONE_RIGHT;
		this.joinedGreaterThanQuarter = false;
		this.setJoin1(this);
		this.setJoin2(this);

		boolean noteJoined = false;
		boolean withPrev = false;

		//trato de unir con el componente anterior
		if (this.previous != null && !this.previous.isRestVoice()) {
			if (getMeasureImpl().canJoin(layout.getSongManager(),this, this.previous)) {
				withPrev = true;
				if (this.previous.getDuration().getValue() >= getDuration().getValue()) {
					this.setJoin1(this.previous);
					this.setJoin2(this);
					this.joinedType = JOINED_TYPE_LEFT;
					noteJoined = true;
				}
				if (this.previous.getDuration().getValue() > TGDuration.QUARTER){
					this.joinedGreaterThanQuarter = true;
				}
			}
		}

		//trato de unir con el componente que le sigue
		if (this.next != null && !this.next.isRestVoice() ) {
			if (getMeasureImpl().canJoin(layout.getSongManager(),this, this.next)) {
				if (this.next.getDuration().getValue() >= getDuration().getValue()) {
					this.setJoin2(this.next);
					if (this.previous == null || this.previous.isRestVoice() || this.previous.getDuration().getValue() < getDuration().getValue()) {
						this.setJoin1(this);
					}
					noteJoined = true;
					this.joinedType = JOINED_TYPE_RIGHT;
				}
				if (this.next.getDuration().getValue() > TGDuration.QUARTER){
					this.joinedGreaterThanQuarter = true;
				}
			}
		}

		//si no hubo union decido para que lado girar la figura
		if (!noteJoined && withPrev) {
			this.joinedType = JOINED_TYPE_NONE_LEFT;
		}

		if( (layout.getStyle() & TGLayout.DISPLAY_TABLATURE) != 0 && (layout.getStyle() & TGLayout.DISPLAY_SCORE) == 0){
			this.minY = 0;
			this.maxY = getBeatImpl().getMeasureImpl().getTrackImpl().getTabHeight();
			if( getBeatGroup().getDirection() == TGBeatGroup.DIRECTION_DOWN ){
				this.maxY += (((layout.getStringSpacing() / 2) * 5) + 1);
			}else{
				this.minY -= (((layout.getStringSpacing() / 2) * 5) + 1);
			}
		}
	}

	public void updateSilenceSpacing(TGLayout layout) {
		this.silenceY = 0;
		this.silenceHeight = 0;
		if( isHiddenSilence() ){
			return;
		}
		boolean score = ((layout.getStyle() & TGLayout.DISPLAY_SCORE) != 0);
		float lineSpacing = (score ? layout.getScoreLineSpacing() : layout.getStringSpacing());
		int lineCount = (score ? 5 : getBeat().getMeasure().getTrack().stringCount());
		float scale = (lineSpacing / (score ? 9.0f : 10.0f));

		this.placeSilence(score, lineSpacing, lineCount, scale);
		for( int v = 0; v < getBeat().countVoices(); v ++){
			TGVoiceImpl voice = getBeatImpl().getVoiceImpl( v );
			if( v != getIndex() && !voice.isEmpty() ){
				this.avoidVoice(layout, voice, lineSpacing);
			}
		}
		this.minY = this.silenceY;
		this.maxY = (this.silenceY + this.silenceHeight);
	}

	// the rest alone on its staff: whole and half rests hang from / sit on a line, shorter ones are centred
	private void placeSilence(boolean score, float lineSpacing, int lineCount, float scale) {
		int duration = getDuration().getValue();
		if( duration == TGDuration.WHOLE) {
			this.silenceHeight = (scale * 3.6513581f);
			this.silenceY = (score ? lineSpacing : (lineSpacing * ((lineCount - 1) / 2)));
		}
		else if(duration == TGDuration.HALF) {
			this.silenceHeight = (scale * 3.6513581f);
			this.silenceY = ((score ? lineSpacing * 2 : (lineSpacing * (lineCount / 2))) - this.silenceHeight);
		}
		else if(getSilenceHeight(duration) > 0) {
			this.silenceHeight = (scale * getSilenceHeight(duration));
			this.silenceY = ((lineSpacing * (lineCount - 1)) / 2) - (this.silenceHeight / 2);
		}
	}

	// height of a centred rest, in eighths of a score line spacing (0 for an unknown duration)
	private static int getSilenceHeight(int duration) {
		switch (duration) {
			case TGDuration.QUARTER: return 16;
			case TGDuration.EIGHTH: return 12;
			case TGDuration.SIXTEENTH: return 16;
			case TGDuration.THIRTY_SECOND: return 24;
			case TGDuration.SIXTY_FOURTH: return 28;
			default: return 0;
		}
	}

	// moves the rest out of the way of another voice of its beat
	private void avoidVoice(TGLayout layout, TGVoiceImpl voice, float lineSpacing) {
		if( voice.isRestVoice() ){
			if( !voice.isHiddenSilence() ){
				this.stackSilence(lineSpacing);
			}
		}
		else if((layout.getStyle() & TGLayout.DISPLAY_SCORE) != 0 ){
			TGBeatGroup group = voice.getBeatGroup();
			float y1 = group.getY1(layout, voice.getMaxNote());
			float y2 = group.getY1(layout, voice.getMinNote());
			this.avoidStem(group.getDirection(), (y1 + (lineSpacing * 2)), (y2 - (this.silenceHeight + lineSpacing)));
		}
		else if((layout.getStyle() & TGLayout.DISPLAY_TABLATURE) != 0 ){
			float below = (lineSpacing * voice.getMaxString() );
			float above = ((lineSpacing * (voice.getMinString() - 1) ) - (this.silenceHeight + lineSpacing));
			this.avoidStem(voice.getBeatGroup().getDirection(), below, above);
		}
	}

	// rests of several voices share a 3 lines high band, one slot per voice
	private void stackSilence(float lineSpacing) {
		float maxSilenceHeight = (lineSpacing * 3);
		float firstPosition = (this.silenceY - (maxSilenceHeight / getBeat().countVoices()));
		this.silenceY = (firstPosition + ( maxSilenceHeight * getIndex() ));
	}

	// up stems push the rest down to "below" at least, down stems push it up to "above" at most
	private void avoidStem(int direction, float below, float above) {
		if(direction == TGBeatGroup.DIRECTION_UP && below > this.silenceY){
			this.silenceY = below;
		}
		else if(direction == TGBeatGroup.DIRECTION_DOWN && above < this.silenceY){
			this.silenceY = above;
		}
	}

	public void paint(TGLayout layout,UIPainter painter, float fromX, float fromY) {
		if(!isEmpty()){
			if( isRestVoice() && !isHiddenSilence() ){
				paintSilence(layout, painter, fromX, fromY);
			}
			else{
				Iterator<TGNote> notes = getNotes().iterator();
				while (notes.hasNext()) {
					TGNoteImpl note = (TGNoteImpl)notes.next();
					note.paint(layout,painter,fromX ,fromY);
				}
				if(!layout.isPlayModeEnabled()){
					paintBeat(layout, painter, fromX, fromY) ;
				}
			}
		}
	}

	//----silence
	public void paintSilence(TGLayout layout,UIPainter painter, float fromX, float fromY) {
		int style = layout.getStyle();
		float x = 0;
		float lineSpacing = 0;
		float y = 0;
		float scale = 0;
		float layoutScale = layout.getScale();

		if((style & TGLayout.DISPLAY_SCORE) != 0 ){
			x = fromX + getPosX() + getBeatImpl().getSpacing(layout);
			y = fromY + getPaintPosition(TGTrackSpacing.POSITION_SCORE_MIDDLE_LINES) + this.silenceY;
			lineSpacing = layout.getScoreLineSpacing();
			scale = (lineSpacing / 9.0f);
		}else{
			x = fromX + getPosX() + getBeatImpl().getSpacing(layout) - 1;
			y = fromY + getPaintPosition(TGTrackSpacing.POSITION_TABLATURE) + this.silenceY;
			lineSpacing = layout.getStringSpacing();
			scale = (lineSpacing / 10.0f);
		}

		setStyle(layout, painter,(layout.isPlayModeEnabled() && isPlaying(layout)));
		painter.initPath(UIPainter.PATH_FILL);

		int duration = getDuration().getValue();
		if(duration == TGDuration.WHOLE){
			TGSilencePainter.paintWhole(painter, x, y , scale);
		}
		else if(duration == TGDuration.HALF){
			TGSilencePainter.paintHalf(painter, x, y , scale);
		}
		else if(duration == TGDuration.QUARTER){
			TGSilencePainter.paintQuarter(painter, x, y, scale);
		}
		else if(duration == TGDuration.EIGHTH){
			TGSilencePainter.paintEighth(painter, x, y, scale);
		}
		else if(duration == TGDuration.SIXTEENTH){
			TGSilencePainter.paintSixteenth(painter, x, y, scale);
		}
		else if(duration == TGDuration.THIRTY_SECOND){
			TGSilencePainter.paintThirtySecond(painter, x, y, scale);
		}
		else if(duration == TGDuration.SIXTY_FOURTH){
			TGSilencePainter.paintSixtyFourth(painter, x, y, scale);
		}

		painter.closePath();

		if( getDuration().isDotted() || getDuration().isDoubleDotted()) {
			paintDot(layout, painter, (x + 10f * layoutScale), (y + 2f * layoutScale), layoutScale);
		}
	}

	public void setStyle(TGLayout layout, UIPainter painter, boolean playMode){
		if((layout.getStyle() & TGLayout.DISPLAY_SCORE) != 0 ){
			layout.setScoreSilenceStyle(painter, playMode);
		}else{
			layout.setTabSilenceStyle(painter, playMode);
		}
	}

	public void paintBeat(TGLayout layout,UIPainter painter, float fromX, float fromY){
		if(!isRestVoice() ){
			int style = layout.getStyle();
			float spacing = getBeatImpl().getSpacing(layout);

			if((style & TGLayout.DISPLAY_SCORE) != 0){
				paintScoreBeat(layout, painter, fromX, fromY + getPaintPosition(TGTrackSpacing.POSITION_SCORE_MIDDLE_LINES), spacing);
			}
			if((style & TGLayout.DISPLAY_TABLATURE) != 0 && (style & TGLayout.DISPLAY_SCORE) == 0){
				paintTablatureBeat(layout, painter, fromX, fromY + getPaintPosition(TGTrackSpacing.POSITION_TABLATURE), spacing);
			}
		}
	}

	public void paintTablatureBeat(TGLayout layout,UIPainter painter, float fromX, float fromY, float spacing){
		if(!isRestVoice() ){
			float scale = layout.getScale();
			float xMove = (2f * scale);
			float x = ( fromX + getPosX() + spacing + xMove );
			float y1 = 0;
			float y2 = 0;
			float verticalLineWidth = layout.getLineWidth(1);
			float horizontalLineWidth = layout.getLineWidth(2);
			float stringSpacing = layout.getStringSpacing();
			float direction = (getBeatGroup().getDirection() == TGBeatGroup.DIRECTION_DOWN ? 1f : -1f);
			if( getBeatGroup().getDirection() == TGBeatGroup.DIRECTION_DOWN ){
				y1 = (fromY + getMeasureImpl().getTrackImpl().getTabHeight() + (stringSpacing / 2));
				y2 = (fromY + getMeasureImpl().getTrackImpl().getTabHeight() + ((stringSpacing / 2) * 5));
			}else{
				y1 = (fromY - (stringSpacing / 2) - horizontalLineWidth);
				y2 = (fromY - ((stringSpacing / 2) * 5));
			}
			if (getDuration().getValue() >= TGDuration.QUARTER) {
				layout.setTabNoteFooterStyle(painter);
				painter.initPath(UIPainter.PATH_DRAW | UIPainter.PATH_FILL);
				painter.setAntialias(false);
				painter.setLineWidth( verticalLineWidth );
				painter.moveTo(x, y1);
				painter.lineTo(x, y2);
				if (getDuration().getValue() >= TGDuration.EIGHTH) {
					float x1 = 0;
					float x2 = 0;
					int joinedType = getJoinedType();
					if(joinedType == TGVoiceImpl.JOINED_TYPE_NONE_RIGHT){
						x1 = getPosX() + xMove + spacing;
						x2 = getPosX() + xMove + spacing + (6 * scale);
					}else if(joinedType == TGVoiceImpl.JOINED_TYPE_NONE_LEFT){
						x1 = getPosX() + xMove + spacing - (5 * scale);
						x2 = getPosX() + xMove + spacing;
					}else{
						x1 = getJoin1().getPosX() + xMove + getJoin1().getBeatImpl().getSpacing(layout);
						x2 = getJoin2().getPosX() + xMove + getJoin2().getBeatImpl().getSpacing(layout);
					}
					int index = ( getDuration().getIndex() - 2);
					if(index > 0){
						float height = (layout.getStringSpacing() / 2);
						for(int i = index; i > 0 ;i --){
							painter.moveTo(fromX + x1, y2 + ((height - (i * height)) * direction));
							painter.lineTo(fromX + x2, y2 + ((height - (i * height)) * direction));
							painter.lineTo(fromX + x2, y2 + ((height - (i * height)) * direction) + (horizontalLineWidth * direction));
							painter.lineTo(fromX + x1, y2 + ((height - (i * height)) * direction) + (horizontalLineWidth * direction));
							painter.lineTo(fromX + x1, y2 + ((height - (i * height)) * direction));
						}
					}
				}
				painter.closePath();
			} else if (getDuration().getValue() == TGDuration.HALF) {
				layout.setTabNoteFooterStyle(painter);
				painter.initPath();
				painter.setAntialias(false);
				painter.setLineWidth( verticalLineWidth );
				painter.moveTo(x, (y1 + ((y2 - y1) / 2)));
				painter.lineTo(x, y2);
				painter.closePath();
			}

			//-------------puntillo--------------------------------------
			if (getDuration().isDotted() || getDuration().isDoubleDotted()) {
				int joinedType = getJoinedType();
				float posX = ((getDuration().getValue() > TGDuration.WHOLE)?((joinedType == TGVoiceImpl.JOINED_TYPE_NONE_RIGHT || joinedType == TGVoiceImpl.JOINED_TYPE_RIGHT)?(x+ (4.0f * scale)):(x- (5.0f * scale))):x);
				float posY = (y2 - ((getDuration().getValue() >= TGDuration.EIGHTH ? ((stringSpacing / 2f) * (getDuration().getIndex() - 2)) : scale) * direction));
				paintDot(layout, painter, posX, posY, scale);
			}
		}
	}

	public void paintScoreBeat(TGLayout layout,UIPainter painter, float fromX, float fromY, float spacing){
		float vX = ( fromX + getPosX() + spacing );

		//dibujo el pie
		if( getDuration().getValue() < TGDuration.HALF ){
			return;
		}
		layout.setScoreNoteFooterStyle(painter);

		float scale = layout.getScale();
		float lineSpacing = layout.getScoreLineSpacing();
		int direction = this.group.getDirection();
		int dir = (direction == TGBeatGroup.DIRECTION_DOWN ? 1 : -1);

		float scoreNoteWidth = layout.getScoreNoteWidth();
		float xMove = (direction == TGBeatGroup.DIRECTION_UP ? scoreNoteWidth : 0f);
		float yMove = ((lineSpacing / 2f) + (((lineSpacing / 10f) * 1.20f)) * dir);

		float vY1 = fromY + ( direction == TGBeatGroup.DIRECTION_DOWN ? this.minNote.getScorePosY() : this.maxNote.getScorePosY() );
		float vY2 = fromY + this.group.getY2(layout,getPosX() + spacing);

		paintScoreStem(layout, painter, vX + xMove, vY1 + yMove, vY2, scale);
		paintScoreFlagsOrBeams(layout, painter, fromX, fromY, spacing, xMove, dir, scale, lineSpacing);
	}

	private void paintScoreStem(TGLayout layout, UIPainter painter, float x, float y1, float y2, float scale){
		painter.setLineWidth(layout.getLineWidth(0));
		painter.initPath(UIPainter.PATH_FILL);
		painter.moveTo(x - (0.5f * scale), y1);
		painter.lineTo(x + (0.5f * scale), y1);
		painter.lineTo(x + (0.5f * scale), y2);
		painter.lineTo(x - (0.5f * scale), y2);
		painter.moveTo(x - (0.5f * scale), y1);
		painter.closePath();
	}

	private void paintScoreFlagsOrBeams(TGLayout layout, UIPainter painter, float fromX, float fromY, float spacing, float xMove, int dir, float scale, float lineSpacing){
		if( getDuration().getValue() < TGDuration.EIGHTH ) {
			return;
		}
		int index =  ( getDuration().getIndex() - 3);
		if( index < 0 ) {
			return;
		}
		int joinedType = getJoinedType();
		boolean joinedGreaterThanQuarter = isJoinedGreaterThanQuarter();

		if( isUnjoined(joinedType, joinedGreaterThanQuarter) ){
			paintScoreFlags(layout, painter, fromX, fromY, spacing, xMove, dir, scale, lineSpacing, index);
		} else {
			paintScoreBeams(layout, painter, fromX, fromY, spacing, xMove, dir, scale, index, joinedType);
		}
	}

	private static boolean isUnjoined(int joinedType, boolean joinedGreaterThanQuarter){
		return ((joinedType == TGVoiceImpl.JOINED_TYPE_NONE_LEFT || joinedType == TGVoiceImpl.JOINED_TYPE_NONE_RIGHT) && !joinedGreaterThanQuarter);
	}

	private void paintScoreFlags(TGLayout layout, UIPainter painter, float fromX, float fromY, float spacing, float xMove, int dir, float scale, float lineSpacing, int index){
		float hX = (fromX + xMove + getPosX() + spacing - (0.5f * scale));
		float hY = ((fromY + this.group.getY2(layout,getPosX() + spacing)) - ( (lineSpacing * 2)* dir )) ;
		for(int i = 0; i <= index; i ++){
			painter.initPath(UIPainter.PATH_FILL);
			TGNotePainter.paintFooter(painter, hX, (hY - ( (i * (lineSpacing / 2.0f)) * dir)),dir,lineSpacing);
			painter.closePath();
		}
	}

	private void paintScoreBeams(TGLayout layout, UIPainter painter, float fromX, float fromY, float spacing, float xMove, int dir, float scale, int index, int joinedType){
		float hX = (getPosX() + spacing);
		float hLineWidth = layout.getLineWidth(2);
		float hLineMargin = hLineWidth;
		int nIndex = getBeamIndex(this.next);
		int pIndex = getBeamIndex(this.previous);
		boolean pathInitialized = false;

		float hX1 = getBeamX1(layout, joinedType, hX, spacing, scale);
		float hX2 = getBeamX2(layout, joinedType, hX, spacing, scale);

		for(int i = 0; i <= index; i ++) {
			float currHX1 = getBeamSegmentX1(i, pIndex, hX, hX1);
			float currHX2 = getBeamSegmentX2(i, nIndex, hX, hX2);
			if( currHX1 != currHX2 ) {
				if(!pathInitialized) {
					pathInitialized = true;
					painter.initPath(UIPainter.PATH_FILL);
				}
				float hY1 = fromY + this.group.getY2(layout, currHX1);
				float hY2 = fromY + this.group.getY2(layout, currHX2);
				float yOffset = ((i * ((hLineWidth * 2f) + hLineMargin)) * dir);

				paintScoreBeamSegment(painter, fromX + xMove + currHX1 - (0.5f * scale), fromX + xMove + currHX2 + (0.5f * scale), hY1 - yOffset, hY2 - yOffset, hLineWidth);
			}
		}

		if( pathInitialized) {
			painter.closePath();
		}
	}

	private static int getBeamIndex(TGVoiceImpl voice){
		return (voice != null ? voice.getDuration().getIndex() - 3 : -1);
	}

	private float getBeamX1(TGLayout layout, int joinedType, float hX, float spacing, float scale){
		if(joinedType == TGVoiceImpl.JOINED_TYPE_NONE_RIGHT){
			return hX;
		}else if(joinedType == TGVoiceImpl.JOINED_TYPE_NONE_LEFT){
			return getPosX() + spacing - (5f * scale);
		}
		return getJoin1().getPosX() + getJoin1().getBeatImpl().getSpacing(layout);
	}

	private float getBeamX2(TGLayout layout, int joinedType, float hX, float spacing, float scale){
		if(joinedType == TGVoiceImpl.JOINED_TYPE_NONE_RIGHT){
			return getPosX() + spacing + (6f * scale);
		}else if(joinedType == TGVoiceImpl.JOINED_TYPE_NONE_LEFT){
			return hX;
		}
		return getJoin2().getPosX() + getJoin2().getBeatImpl().getSpacing(layout);
	}

	private float getBeamSegmentX1(int i, int pIndex, float hX, float hX1){
		if( isJoinedFromPrevious() && ((this.getJoin1() != null && this.getJoin1().equals(this.previous)) || i <= pIndex) ) {
			return hX;
		}
		return hX1;
	}

	private float getBeamSegmentX2(int i, int nIndex, float hX, float hX2){
		if( isJoinedToNext() && ((this.getJoin1() == null || !this.getJoin2().equals(this.next)) && i <= nIndex) ) {
			return hX;
		}
		return hX2;
	}

	private boolean isJoinedFromPrevious(){
		return ( this.previous != null && this.previous.getJoin2() != null && this.previous.getJoin2().equals(this) );
	}

	private boolean isJoinedToNext(){
		return ( this.next != null && this.next.getJoin1() != null && this.next.getJoin1().equals(this) );
	}

	private static void paintScoreBeamSegment(UIPainter painter, float x1, float x2, float y1, float y2, float hLineWidth){
		painter.moveTo(x1, y1 - hLineWidth);
		painter.lineTo(x1, y1 + hLineWidth);
		painter.lineTo(x2, y2 + hLineWidth);
		painter.lineTo(x2, y2 - hLineWidth);
		painter.lineTo(x1, y1 - hLineWidth);
	}

	public void paintDot(TGLayout layout,UIPainter painter,float fromX, float fromY, float scale){
		float dotSize = (3.0f * scale);
		float posX = fromX;
		float posY = fromY;
		layout.setDotStyle(painter);
		painter.setLineWidth(layout.getLineWidth(0));
		painter.initPath(UIPainter.PATH_FILL);
		painter.moveTo(posX, posY);
		painter.addCircle(posX, posY, dotSize);
		if(getDuration().isDoubleDotted()){
			painter.moveTo(posX + (dotSize + (2f * scale)), posY);
			painter.addCircle(posX + (dotSize + (2f * scale)), posY, dotSize);
		}
		painter.closePath();
	}

	public float getPaintPosition(int index){
		return getMeasureImpl().getTs().getPosition(index);
	}

	public int getMaxString() {
		return this.maxString;
	}

	public int getMinString() {
		return this.minString;
	}

	public float getMaxY() {
		return this.maxY;
	}

	public float getMinY() {
		return this.minY;
	}

	public boolean isHiddenSilence() {
		return this.hiddenSilence;
	}

	public void setHiddenSilence(boolean hiddenSilence) {
		this.hiddenSilence = hiddenSilence;
	}

	public float getEffectWidth(TGLayout layout)  {
		float voiceEffectWidth = 0.0f;
		Iterator<TGNote> it = getNotes().iterator();
		while(it.hasNext()){
			TGNoteImpl note = (TGNoteImpl)it.next();
			this.check(layout, note);
			float noteEffectWidth = note.getEffectWidth(layout);
			if (noteEffectWidth>voiceEffectWidth) voiceEffectWidth = noteEffectWidth;
		}
		return voiceEffectWidth;
	}

}
