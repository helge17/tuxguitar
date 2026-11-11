package app.tuxguitar.graphics.control;

import java.util.Iterator;
import java.util.List;

import app.tuxguitar.graphics.control.painters.TGKeySignaturePainter;
import app.tuxguitar.graphics.control.painters.TGNotePainter;
import app.tuxguitar.graphics.control.painters.TGNumberPainter;
import app.tuxguitar.song.factory.TGFactory;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGNote;
import app.tuxguitar.song.models.TGNoteEffect;
import app.tuxguitar.song.models.TGVoice;
import app.tuxguitar.ui.resource.UIInset;
import app.tuxguitar.ui.resource.UIPainter;
import app.tuxguitar.ui.resource.UIRectangle;
import app.tuxguitar.util.TGMusicKeyUtils;
import app.tuxguitar.song.models.effects.TGEffectBend;

public class TGNoteImpl extends TGNote {
	private float tabPosY;

	private float scorePosY;

	private int accidental;

	public TGNoteImpl(TGFactory factory) {
		super(factory);
	}

	public void update(TGLayout layout) {
		if(!getMeasureImpl().getTrack().isPercussion() && !this.getEffect().isDeadNote()) {
			this.accidental = getMeasureImpl().getNoteAccidental( layout.getSongManager().getMeasureManager().getRealNoteValue(this), this.isAltEnharmonic() );
		}

		this.tabPosY = ( (getString() * layout.getStringSpacing()) - layout.getStringSpacing() );
		this.scorePosY = getVoiceImpl().getBeatGroup().getY1(layout,this);
	}

	public void paint(TGLayout layout,UIPainter painter, float fromX, float fromY) {
		float spacing = getBeatImpl().getSpacing(layout);
		// fromX,fromY = top-left corner of drawing zone in current measure
		// this leaves some space on the left (with respect to the measure bar)

		paintScoreNote(layout, painter, fromX, fromY + getPaintPosition(TGTrackSpacing.POSITION_SCORE_MIDDLE_LINES),spacing);
		if(!layout.isPlayModeEnabled()){
			paintOfflineEffects(layout, painter,fromX,fromY, spacing);
		}
		paintTablatureNote(layout, painter, fromX, fromY, spacing);
	}

	void paintOfflineEffects(TGLayout layout,UIPainter painter,float fromX, float fromY, float spacing){
		TGSpacing bs = getBeatImpl().getBs();
		TGSpacing ts = getMeasureImpl().getTs();
		float tsY = (fromY + ts.getPosition(TGTrackSpacing.POSITION_EFFECTS));
		// tsY = top level of space to display "offline" effects (effects displayed ABOVE tab)
		float bsY = (tsY + (ts.getSize(TGTrackSpacing.POSITION_EFFECTS) - bs.getSize( )));
		// bsY = top level of 1st empty space for "offline" effects (i.e. y where to draw the next one)

		// in the following "offline effects", x,y = top-left corner of the effect drawing
		OfflineRows rows = new OfflineRows(fromX + getPosX() + spacing, bsY, bs);
		layout.setOfflineEffectStyle(painter);
		paintOfflineAccents(layout, painter, rows);
		paintOfflineLabels(layout, painter, rows);
		paintOfflineOrnaments(layout, painter, rows);
	}

	/** Where offline effects are drawn: one column at x, one row per effect kind below bsY. */
	private static class OfflineRows {
		private final float x;
		private final float bsY;
		private final TGSpacing bs;

		OfflineRows(float x, float bsY, TGSpacing bs) {
			this.x = x;
			this.bsY = bsY;
			this.bs = bs;
		}

		float y(int position) {
			return (this.bsY + this.bs.getPosition(position));
		}
	}

	private void paintOfflineAccents(TGLayout layout, UIPainter painter, OfflineRows rows){
		TGNoteEffect effect = getEffect();
		if(effect.isAccentuatedNote()){
			paintAccentuated(layout, painter, rows.x, rows.y(TGBeatSpacing.POSITION_ACCENTUATED_EFFECT));
		}
		if(effect.isHeavyAccentuatedNote()){
			paintHeavyAccentuated(layout, painter, rows.x, rows.y(TGBeatSpacing.POSITION_HEAVY_ACCENTUATED_EFFECT));
		}
		if(effect.isFadeIn()){
			paintFadeIn(layout, painter, rows.x, rows.y(TGBeatSpacing.POSITION_FADE_IN_EFFECT));
		}
	}

	private void paintOfflineLabels(TGLayout layout, UIPainter painter, OfflineRows rows){
		TGNoteEffect effect = getEffect();
		if(effect.isHarmonic() && (layout.getStyle() & TGLayout.DISPLAY_SCORE) == 0 ){
			paintOfflineLabel(layout, painter, effect.getHarmonic().getKey(), rows, TGBeatSpacing.POSITION_HARMONIC_EFFECT);
		}
		paintPlayingTechniqueLabels(layout, painter, rows);
	}

	private void paintPlayingTechniqueLabels(TGLayout layout, UIPainter painter, OfflineRows rows){
		TGNoteEffect effect = getEffect();
		if(effect.isTapping()){
			paintOfflineLabel(layout, painter, "T", rows, TGBeatSpacing.POSITION_TAPPING_EFFECT);
		}
		if(effect.isSlapping()){
			paintOfflineLabel(layout, painter, "S", rows, TGBeatSpacing.POSITION_SLAPPING_EFFECT);
		}
		if(effect.isPopping()){
			paintOfflineLabel(layout, painter, "P", rows, TGBeatSpacing.POSITION_POPPING_EFFECT);
		}
		if(effect.isPalmMute()){
			paintOfflineLabel(layout, painter, "P.M", rows, TGBeatSpacing.POSITION_PALM_MUTE_EFFECT);
		}
		if(effect.isLetRing()){
			paintOfflineLabel(layout, painter, "L.R", rows, TGBeatSpacing.POSITION_LET_RING_EFFECT);
		}
	}

	private void paintOfflineLabel(TGLayout layout, UIPainter painter, String label, OfflineRows rows, int position){
		painter.drawString(label, rows.x, (rows.y(position) + painter.getFMTopLine() + (2f * layout.getScale())));
	}

	private void paintOfflineOrnaments(TGLayout layout, UIPainter painter, OfflineRows rows){
		TGNoteEffect effect = getEffect();
		if(effect.isVibrato()){
			paintVibrato(layout, painter, rows.x, rows.y(TGBeatSpacing.POSITION_VIBRATO_EFFECT));
		}
		if(effect.isTrill()){
			paintTrill(layout, painter, rows.x, rows.y(TGBeatSpacing.POSITION_TRILL_EFFECT));
		}
	}

	public void paintTablatureNoteValue(TGLayout layout, UIPainter painter, UIInset margin, float fromX, float fromY, boolean running) {
		if( layout.isTabNotePathRendererEnabled() ) {
			this.paintTablatureNoteValuePathMode(layout, painter, margin, fromX, fromY, running);
		} else {
			this.paintTablatureNoteValueTextMode(layout, painter, margin, fromX, fromY, running);
		}
	}

	public void paintTablatureNoteValuePathMode(TGLayout layout, UIPainter painter, UIInset margin, float fromX, float fromY, boolean running) {
		float noteSize = (layout.getStringSpacing() - 2f);
		float noteWidth = (this.getEffect().isDeadNote() ? 6f * layout.getScale() : TGNumberPainter.getDigitsWidth(getValue(), noteSize));
		float ghostWidth = (this.getEffect().isGhostNote() ? 3f * layout.getScale() : 0f);

		margin.setTop(noteSize / 2f);
		margin.setBottom(noteSize / 2f);
		margin.setLeft((noteWidth / 2f) + ghostWidth);
		margin.setRight((noteWidth / 2f) + ghostWidth);

		this.fillBackground(layout, painter, margin, fromX, fromY);
		layout.setTabNotePathStyle(painter, running);
		if( this.getEffect().isDeadNote() ) {
			painter.initPath(UIPainter.PATH_DRAW);
			painter.moveTo(fromX - (margin.getLeft() - ghostWidth), fromY - margin.getTop());
			painter.lineTo(fromX + (margin.getRight() - ghostWidth), fromY + margin.getBottom());
			painter.moveTo(fromX + (margin.getRight() - ghostWidth), fromY - margin.getTop());
			painter.lineTo(fromX - (margin.getLeft() - ghostWidth), fromY + margin.getBottom());

			painter.closePath();
		} else {
			TGNumberPainter.paintDigits(getValue(), painter, fromX - (margin.getLeft() - ghostWidth), fromY - margin.getTop(), noteSize);
		}

		if( this.getEffect().isGhostNote() ) {
			float ghostLineWidth = (2f * layout.getScale());
			float ghostY1 = fromY - margin.getTop();
			float ghostY2 = fromY + margin.getBottom();
			float ghostLeftX1 = fromX - (margin.getLeft() - ghostWidth);
			float ghostLeftX2 = fromX - margin.getLeft();
			float ghostRightX1 = fromX + (margin.getRight() - ghostWidth);
			float ghostRightX2 = fromX + margin.getRight();

			painter.initPath(UIPainter.PATH_FILL);
			painter.moveTo(ghostLeftX1, ghostY1);
			painter.cubicTo(ghostLeftX2 - ghostLineWidth, ghostY1, ghostLeftX2 - ghostLineWidth, ghostY2, ghostLeftX1, ghostY2);
			painter.cubicTo(ghostLeftX2, ghostY2, ghostLeftX2, ghostY1, ghostLeftX1, ghostY1);
			painter.closePath();

			painter.initPath(UIPainter.PATH_FILL);
			painter.moveTo(ghostRightX1, ghostY1);
			painter.cubicTo(ghostRightX2 + ghostLineWidth, ghostY1, ghostRightX2 + ghostLineWidth, ghostY2, ghostRightX1, ghostY2);
			painter.cubicTo(ghostRightX2, ghostY2, ghostRightX2, ghostY1, ghostRightX1, ghostY1);
			painter.closePath();
		}
	}

	public void paintTablatureNoteValueTextMode(TGLayout layout, UIPainter painter, UIInset margin, float fromX, float fromY, boolean running) {
		layout.setTabNoteFontStyle(painter, running);

		String label = this.getNoteLabel(this);
		float fmWidth = painter.getFMWidth(label);
		float fmTopLine = painter.getFMTopLine();
		float fmMiddleLine = painter.getFMMiddleLine();
		float fmBaseLine = painter.getFMBaseLine();

		margin.setTop((fmTopLine - fmBaseLine) / 2);
		margin.setBottom((fmTopLine - fmBaseLine) / 2);
		margin.setLeft(fmWidth / 2);
		margin.setRight(fmWidth / 2);

		this.fillBackground(layout, painter, margin, fromX, fromY);
		layout.setTabNoteFontStyle(painter, running);
		painter.drawString(label, fromX - margin.getLeft(), fromY + fmMiddleLine);
	}

	public void paintTablatureNote(TGLayout layout,UIPainter painter, float fromX, float fromY, float spacing) {
		// fromX,fromY = top-left corner of display zone in current measure (not current note!), with some left distance to previous measure bar (or score start)
		float fromXtab  = fromX + 2.0f*layout.getScale();
		float fromYtab = fromY + getPaintPosition(TGTrackSpacing.POSITION_TABLATURE);
		// fromXtab,fromYtab = top-left corner of display zone within current measure, in tablature
		// this includes some spacing on the left (x), and is fully aligned with 1st string (y)

		if((layout.getStyle() & TGLayout.DISPLAY_TABLATURE) == 0) {
			return;
		}
		UIInset margin = new UIInset();
		float x = (fromXtab + getPosX() + spacing);
		// x = center of the displayed fret number in the tab (middle of the digits)
		boolean scoreHidden = ((layout.getStyle() & TGLayout.DISPLAY_SCORE) == 0);
		boolean running = (layout.isPlayModeEnabled() && getBeatImpl().isPlaying(layout));

		if(!isTiedNote()) {
			this.paintTablatureNoteValue(layout, painter, margin, x, (fromYtab + getTabPosY()), running);
		} else if(scoreHidden) {
			this.paintTablatureTie(layout, painter, fromXtab, fromYtab, x, running);
		}

		if(!layout.isPlayModeEnabled()) {
			this.paintEffects(layout, painter, margin, fromXtab, fromY, spacing);
			if(scoreHidden && getEffect().isTremoloPicking()) {
				this.paintTremoloPicking(layout, painter, fromYtab, x);
			}
		}
	}

	// the "tied to preceding note" curve, just under the fret, ending at x (the center of the fret number)
	private void paintTablatureTie(TGLayout layout, UIPainter painter, float fromXtab, float fromYtab, float x, boolean running) {
		float scale = layout.getScale();
		float stringSpacing = layout.getStringSpacing();
		float tX = tablatureTieStartX(layout, fromXtab);
		float tY = (fromYtab + getTabPosY() + (stringSpacing / 2f));
		// tY = in tab, just under fret digit, the top-right point of "tied to preceding note" drawing

		float tWidth = (x - tX);
		float tHeight1 = (stringSpacing / 3f);
		float tHeight2 = (tHeight1 + (scale * 2f));

		layout.setTabTiedStyle(painter, running);
		painter.initPath(UIPainter.PATH_FILL);
		painter.moveTo(tX, tY);
		painter.cubicTo(tX, tY + tHeight1, tX + tWidth, tY + tHeight1, tX + tWidth, tY);
		painter.cubicTo(tX + tWidth, tY + tHeight2, tX, tY + tHeight2, tX, tY);
		painter.closePath();
	}

	// left point of the tie: on the right of the note tied to (note n-1 or n-2... n-x) if any,
	// else at a fixed distance from this note
	private float tablatureTieStartX(TGLayout layout, float fromXtab) {
		TGNoteImpl noteForTie = getNoteForTie();
		if (noteForTie != null) {
			return (fromXtab + noteForTie.getPosX() + noteForTie.getBeatImpl().getSpacing(layout) + (5.0f * layout.getScale()));
		}
		return (fromXtab + this.getPosX() + this.getBeatImpl().getSpacing(layout) - (layout.getStringSpacing() * 2));
	}

	// one stroke per halving of the duration from an eighth, under the tablature (shown only when the score is hidden)
	private void paintTremoloPicking(TGLayout layout, UIPainter painter, float fromYtab, float x) {
		float scale = layout.getScale();
		float stringSpacing = layout.getStringSpacing();
		float y1 = (fromYtab + getMeasureImpl().getTrackImpl().getTabHeight() + (stringSpacing / 2));
		float y2 = (fromYtab + getMeasureImpl().getTrackImpl().getTabHeight() + ((stringSpacing / 2) * 5));
		// y1 = top-level of "tremolo picking" under tab, y2 = bottom-level

		layout.setTabEffectStyle(painter);
		painter.setLineWidth(layout.getLineWidth(2));
		painter.initPath();
		float posy = (y1 + ((y2 - y1) / 2));
		for(int i = TGDuration.EIGHTH;i <= getEffect().getTremoloPicking().getDuration().getValue(); i += i){
			painter.moveTo(x - (3f * scale), posy - (1f * scale));
			painter.lineTo(x + (4f * scale), posy + (1f * scale));
			posy += (4f * scale);
		}
		painter.closePath();
		painter.setLineWidth(layout.getLineWidth(1));
	}

	private void paintScoreNote(TGLayout layout,UIPainter painter, float fromX, float fromY, float spacing) {
		// fromX,fromY = top left corner of 1st note of measure in score

		if((layout.getStyle() & TGLayout.DISPLAY_SCORE) == 0 ){
			return;
		}
		float scale = layout.getScoreLineSpacing();
		float layoutScale = layout.getScale();
		int direction = getVoiceImpl().getBeatGroup().getDirection();

		float x = ( fromX + getPosX() + spacing );
		float y1 = ( fromY + getScorePosY() ) ;

		//-------------foreground--------------------------------------
		boolean playing = (layout.isPlayModeEnabled() && getBeatImpl().isPlaying(layout));

		if (isTiedNote()) {
			paintScoreTie(layout, painter, fromX, fromY, x, y1, layoutScale, direction, playing);
		}

		layout.setScoreNoteStyle(painter,playing);

		paintScoreAccidental(layout, painter, x, y1, scale);
		paintScoreNoteHead(layout, painter, x, y1, scale);

		if(!layout.isPlayModeEnabled() ){
			paintScoreNoteEffects(layout, painter, fromY, spacing, x, y1, scale, layoutScale, direction);
		}
	}

	private void paintScoreTie(TGLayout layout, UIPainter painter, float fromX, float fromY, float x, float y1, float layoutScale, int direction, boolean playing) {
		TGNoteImpl noteForTie = getNoteForTie();
		float yDirection = ((direction == TGBeatGroup.DIRECTION_UP ? 1.0f : -1.0f));
		float tX = x - (20.0f * layoutScale);
		float tY = (y1 + (layout.getScoreLineSpacing() / 2f));
		if (noteForTie != null) {
			float tNoteX = (fromX + noteForTie.getPosX() + noteForTie.getBeatImpl().getSpacing(layout));
			float tNoteY = (fromY + getScorePosY());
			tX = tNoteX + (10.0f * layoutScale);
			tY = (tNoteY + (layout.getScoreLineSpacing() / 2f));
			// minimize conflict with preceding dotted/double-dotted note
			if (isDottedOrDoubleDotted(noteForTie.getVoice().getDuration())) {
				tY += (yDirection * 1.5f * layoutScale);
			}
		}
		float tWidth = (x - tX) - (3.0f * layoutScale);
		float tHeight1 = (layout.getScoreLineSpacing() / 2f);
		float tHeight2 = (tHeight1 - (layoutScale * 2f));

		layout.setTiedStyle(painter, playing);
		painter.initPath(UIPainter.PATH_FILL);
		painter.moveTo(tX, tY);
		painter.cubicTo(tX, tY + yDirection*tHeight1, tX + tWidth, tY + yDirection*tHeight1, tX + tWidth, tY);
		painter.cubicTo(tX + tWidth, tY + yDirection*tHeight2, tX, tY + yDirection*tHeight2, tX, tY);
		painter.closePath();
	}

	private boolean isDottedOrDoubleDotted(TGDuration duration) {
		return (duration.isDotted() || duration.isDoubleDotted());
	}

	private void paintScoreAccidental(TGLayout layout, UIPainter painter, float x, float y1, float scale) {
		if(!hasScoreAccidental() || this.getEffect().isDeadNote()){
			return;
		}
		painter.initPath(UIPainter.PATH_FILL);
		painter.setLineWidth(layout.getLineWidth(0));
		float accidentalX = (x - (scale - (scale / 4)) );
		float accidentalY = (y1 + (scale / 2));
		if(this.accidental == TGMusicKeyUtils.NATURAL){
			TGKeySignaturePainter.paintNatural(painter, accidentalX, accidentalY, scale);
		}
		else if(this.accidental == TGMusicKeyUtils.SHARP){
			TGKeySignaturePainter.paintSharp(painter, accidentalX, accidentalY, scale);
		}
		else {
			TGKeySignaturePainter.paintFlat(painter, accidentalX, accidentalY, scale);
		}
		painter.closePath();
	}

	private boolean hasScoreAccidental() {
		return (this.accidental == TGMusicKeyUtils.NATURAL || this.accidental == TGMusicKeyUtils.SHARP || this.accidental == TGMusicKeyUtils.FLAT);
	}

	private void paintScoreNoteHead(TGLayout layout, UIPainter painter, float x, float y1, float scale) {
		if(getEffect().isHarmonic()) {
			paintScoreHarmonicNote(layout, painter, x, y1, scale);
		}
		else if (getMeasureImpl().getTrack().isPercussion()) {
			this.paintPercussionScoreNote(layout, painter, x, y1);
		}
		else {
			paintScoreStandardNote(layout, painter, x, y1, scale);
		}
	}

	private void paintScoreHarmonicNote(TGLayout layout, UIPainter painter, float x, float y1, float scale) {
		boolean fill = (getVoice().getDuration().getValue() >= TGDuration.QUARTER);
		painter.setLineWidth(layout.getLineWidth(1));
		painter.initPath((fill ? (UIPainter.PATH_FILL | UIPainter.PATH_DRAW) : UIPainter.PATH_DRAW));
		TGNotePainter.paintHarmonic(painter, x, y1 + (1f * (scale / 10f)), (layout.getScoreLineSpacing() - ((scale / 10f) * 2f)));
		painter.closePath();
	}

	private void paintScoreStandardNote(TGLayout layout, UIPainter painter, float x, float y1, float scale) {
		boolean isDeadNote = hasDeadNoteEffect();
		boolean fill = !isDeadNote && (getVoice().getDuration().getValue() >= TGDuration.QUARTER);
		float noteX = (fill ? (x - (0.60f * (scale / 10f))) : x);
		float noteY = (fill ? (y1 + (0.60f * (scale / 10f))) : (y1 + (1f * (scale / 10f))));
		float noteScale = (fill ? ((layout.getScoreLineSpacing() - ((scale / 10f) * 1f) )) : ((layout.getScoreLineSpacing() - ((scale / 10f) * 2f) )));

		painter.setLineWidth(layout.getLineWidth(1));
		painter.initPath((fill ? UIPainter.PATH_FILL : UIPainter.PATH_DRAW));
		paintScoreNoteShape(painter, isDeadNote, noteX, noteY, noteScale);
		painter.closePath();
	}

	private boolean hasDeadNoteEffect() {
		return (this.getEffect()!=null && this.getEffect().isDeadNote());
	}

	private void paintScoreNoteShape(UIPainter painter, boolean isDeadNote, float noteX, float noteY, float noteScale) {
		if (isDeadNote) {
			TGNotePainter.paintXNote(painter, noteX, noteY, noteScale);
		} else {
			TGNotePainter.paintNote(painter, noteX, noteY, noteScale);
		}
	}

	private void paintScoreNoteEffects(TGLayout layout, UIPainter painter, float fromY, float spacing, float x, float y1, float scale, float layoutScale, int direction) {
		float scoreNoteWidth = layout.getScoreNoteWidth();

		if(getEffect().isGrace()){
			paintGrace(layout, painter,x ,y1);
		}

		//PUNTILLO y DOBLE PUNTILLO
		if (isDottedOrDoubleDotted(getVoice().getDuration())) {
			getVoiceImpl().paintDot(layout, painter,( x + (12.0f * (scale / 8.0f) ) ), ( y1 + (layout.getScoreLineSpacing()/ 2)), (scale / 10.0f) );
		}

		//dibujo el pie
		if( getVoice().getDuration().getValue() >= TGDuration.HALF ){
			paintStemmedScoreNoteEffects(layout, painter, fromY, spacing, x, scoreNoteWidth, layoutScale, direction);
		}else{
			paintWholeScoreNoteEffects(layout, painter, fromY, x, scoreNoteWidth, layoutScale);
		}
	}

	private void paintStemmedScoreNoteEffects(TGLayout layout, UIPainter painter, float fromY, float spacing, float x, float scoreNoteWidth, float layoutScale, int direction) {
		layout.setScoreNoteFooterStyle(painter);
		float xMove = ((direction == TGBeatGroup.DIRECTION_UP ? scoreNoteWidth : 0));
		float y2 = (fromY + getVoiceImpl().getBeatGroup().getY2(layout,getPosX() + spacing));

		//staccato
		if (getEffect().isStaccato()) {
			float sX = x + xMove;
			float sY = (y2 + ((4f * layoutScale) * ((direction == TGBeatGroup.DIRECTION_UP) ? -1 : 1 )));
			paintScoreStaccato(layout, painter, sX, sY, layoutScale);
		}
		//tremolo picking
		if(getEffect().isTremoloPicking()){
			paintScoreTremoloPicking(layout, painter, x + xMove, getStemmedTremoloPickingY(layout, fromY, layoutScale, direction), layoutScale);
		}
	}

	private float getStemmedTremoloPickingY(TGLayout layout, float fromY, float layoutScale, int direction) {
		float tpY = fromY;
		if((direction == TGBeatGroup.DIRECTION_UP)){
			tpY += (getVoiceImpl().getBeatGroup().getMinNote().getScorePosY() - layout.getScoreLineSpacing() - (4f * layoutScale));
		}else{
			tpY += (getVoiceImpl().getBeatGroup().getMaxNote().getScorePosY() + layout.getScoreLineSpacing() + (4f * layoutScale));
		}
		return tpY;
	}

	private void paintWholeScoreNoteEffects(TGLayout layout, UIPainter painter, float fromY, float x, float scoreNoteWidth, float layoutScale) {
		//staccato
		if (getEffect().isStaccato()) {
			float sX = (x + (scoreNoteWidth / 2));
			float sY = (fromY + getVoiceImpl().getBeatGroup().getMaxNote().getScorePosY() + layout.getScoreLineSpacing()) + (2f * layoutScale);
			paintScoreStaccato(layout, painter, sX, sY, layoutScale);
		}
		//tremolo picking
		if(getEffect().isTremoloPicking()){
			float tpX = ((x + (scoreNoteWidth / 2)));
			float tpY = (fromY + (getVoiceImpl().getBeatGroup().getMinNote().getScorePosY() - layout.getScoreLineSpacing() - (4f  * layoutScale)));
			paintScoreTremoloPicking(layout, painter, tpX, tpY, layoutScale);
		}
	}

	private void paintScoreStaccato(TGLayout layout, UIPainter painter, float sX, float sY, float layoutScale) {
		float size = (3f * layoutScale);
		layout.setScoreEffectStyle(painter);
		painter.setLineWidth(layout.getLineWidth(1));
		painter.initPath(UIPainter.PATH_FILL);
		painter.moveTo(sX, sY);
		painter.addCircle(sX, sY, size);
		painter.closePath();
	}

	private void paintScoreTremoloPicking(TGLayout layout, UIPainter painter, float tpX, float tpY, float layoutScale) {
		layout.setScoreEffectStyle(painter);
		painter.setLineWidth(layout.getLineWidth(2));
		painter.initPath();
		for(int i = TGDuration.EIGHTH;i <= getEffect().getTremoloPicking().getDuration().getValue(); i += i){
			painter.moveTo(tpX - (3f * layoutScale), tpY + (1f * layoutScale));
			painter.lineTo(tpX + (4f * layoutScale), tpY - (1f * layoutScale));
			tpY += (4f * layoutScale);
		}
		painter.closePath();
		painter.setLineWidth(layout.getLineWidth(1));
	}

	private void paintPercussionScoreNote(TGLayout layout,UIPainter painter, float fromX, float fromY) {
		// drum gets special treatment according to value.
		int renderType = layout.getDrumMap().getRenderType(getValue());
		this.paintPercussionNoteHead(layout, painter, renderType, fromX, fromY);
		this.paintPercussionMarks(layout, painter, renderType, fromX, fromY);
	}

	private static boolean isKind(int renderType, int kind) {
		return ((renderType & kind) != 0);
	}

	private static void startPath(TGLayout layout, UIPainter painter, int style) {
		painter.setLineWidth(layout.getLineWidth(1));
		painter.initPath(style);
	}

	// from the quarter on, note heads are filled
	private boolean isFilledHead() {
		return (getVoice().getDuration().getValue() >= TGDuration.QUARTER);
	}

	private void paintPercussionNoteHead(TGLayout layout, UIPainter painter, int renderType, float fromX, float fromY) {
		float scale = layout.getScoreLineSpacing();
		float unit = (scale / 10f);
		int outlineStyle = (isFilledHead() ? (UIPainter.PATH_FILL | UIPainter.PATH_DRAW) : UIPainter.PATH_DRAW);
		if (isKind(renderType, TGDrumMap.KIND_CYMBAL)) {
			// paint as X
			startPath(layout, painter, UIPainter.PATH_DRAW);
			TGNotePainter.paintXNote(painter, fromX, fromY + 1, scale - 2);
			painter.closePath();
		}
		if (isKind(renderType, TGDrumMap.KIND_NOTE)) {
			this.paintPercussionRoundHead(layout, painter, fromX, fromY);
		}
		if (isKind(renderType, TGDrumMap.KIND_SLANTED_DIAMOND)) {
			// paint as harmonic
			startPath(layout, painter, outlineStyle);
			TGNotePainter.paintHarmonic(painter, fromX, fromY + (1f * unit), (scale - (unit * 2f)));
			painter.closePath();
		}
		if (isKind(renderType, TGDrumMap.KIND_TRIANGLE)) {
			// paint as triangle thing
			startPath(layout, painter, outlineStyle);
			TGNotePainter.paintTriangle(painter, fromX, fromY + (1f * unit), (scale - (unit * 2f)));
			painter.closePath();
		}
		if (isKind(renderType, TGDrumMap.KIND_EFFECT_CYMBAL)) {
			// paint as weird X thing
			startPath(layout, painter, UIPainter.PATH_DRAW);
			TGNotePainter.paintEffectCymbalXNote(painter, fromX, fromY + 1, scale - 2);
			painter.closePath();
		}
	}

	// a filled head is a little larger, and shifted left
	private void paintPercussionRoundHead(TGLayout layout, UIPainter painter, float fromX, float fromY) {
		float scale = layout.getScoreLineSpacing();
		float unit = (scale / 10f);
		if (isFilledHead()) {
			startPath(layout, painter, UIPainter.PATH_FILL);
			TGNotePainter.paintNote(painter, (fromX - (0.60f * unit)), (fromY + (0.60f * unit)), (scale - (unit * 1f)));
		} else {
			startPath(layout, painter, UIPainter.PATH_DRAW);
			TGNotePainter.paintNote(painter, fromX, (fromY + (1f * unit)), (scale - (unit * 2f)));
		}
		painter.closePath();
	}

	// other render artifacts: open circle or closed cross above the note, circle around it
	private void paintPercussionMarks(TGLayout layout, UIPainter painter, int renderType, float fromX, float fromY) {
		float scale = layout.getScoreLineSpacing();
		if (isKind(renderType, TGDrumMap.KIND_OPEN)) {
			startPath(layout, painter, UIPainter.PATH_DRAW);
			painter.addCircle(fromX + (0.58f * scale), fromY + (-0.66f * scale), 0.5f * scale);
			painter.closePath();
		}
		// a closed hi-hat right after another one has no cross (but e.g. a muted triangle always has)
		if (isKind(renderType, TGDrumMap.KIND_CLOSED) && (this.getValue() != 42 || !this.isLastBeatContainingValue(layout, this.getValue()))) {
			startPath(layout, painter, UIPainter.PATH_DRAW);
			painter.moveTo(fromX + (0.25f * scale), fromY + (-0.66f * scale));
			painter.lineTo(fromX + (0.91f * scale), fromY + (-0.66f * scale));
			painter.moveTo(fromX + (0.58f * scale), fromY + (-0.33f * scale));
			painter.lineTo(fromX + (0.58f * scale), fromY + (-0.99f * scale));
			painter.closePath();
		}
		if (isKind(renderType, TGDrumMap.KIND_CIRCLE_AROUND)) {
			startPath(layout, painter, UIPainter.PATH_DRAW);
			painter.addCircle(fromX + (0.5f * scale), fromY + (0.425f * scale), scale * 1.6f);
			painter.closePath();
		}
	}

	private boolean isLastBeatContainingValue(TGLayout layout, int value) {
		TGBeat lastBeat = this.getBeatImpl().getPreviousBeat();

		// get note from last beat if it exists
		if (lastBeat == null && this.getMeasureImpl().getPreviousMeasure() != null) {
			TGMeasure lastMeasure = this.getMeasureImpl().getPreviousMeasure();

			layout.addUpdateDependant(lastMeasure.getHeader(), this.getMeasureImpl().getHeader());

			// get last beat of measure
			if (lastMeasure.countBeats() > 0 ) {
				lastBeat = lastMeasure.getBeat(lastMeasure.countBeats() - 1);
			}
		}

		// make sure not null
		if (lastBeat != null) {
			for(int v = 0 ; v < lastBeat.countVoices(); v ++){
				TGVoice lastVoice = lastBeat.getVoice(v);

				// loop through last notes until closed hi-hat is found (if it is found)
				for(int n = 0 ; n < lastVoice.countNotes(); n ++){
					TGNote note = lastVoice.getNote(n);
					if (layout.getSongManager().getMeasureManager().getRealNoteValue(note) == value) {
						return true;
					}
				}
			}
		}
		return false;
	}

	// the last note on this string, in this voice, before this beat; none when a rest comes first
	private TGNoteImpl getNoteForTie() {
		for (int i = getMeasureImpl().countBeats() - 1; i >= 0; i--) {
			TGBeat beat = getMeasureImpl().getBeat(i);
			if (beat.getStart() < getBeatImpl().getStart()) {
				TGVoice voice = beat.getVoice( getVoice().getIndex() );
				if (voice.isRestVoice()) {
					return null;
				}
				TGNoteImpl note = findNoteOnString(voice);
				if (note != null) {
					return note;
				}
			}
		}
		return null;
	}

	private TGNoteImpl findNoteOnString(TGVoice voice) {
		for (TGNote note : voice.getNotes()) {
			if (note.getString() == getString()) {
				return (TGNoteImpl) note;
			}
		}
		return null;
	}

	// >=2 bent notes on same beat?
	// conflict if there is another bend with different value, or with higher string
	// this way, if all values are identical then one does not raise conflict and is drawn
	private boolean multipleBendConflicts() {
		TGVoice voice = getVoice();
		Iterator<TGNote> it = voice.getNotes().iterator();
		while(it.hasNext()){
			TGNoteImpl note = (TGNoteImpl)it.next();
			if (note.getEffect().isBend() && ((note.getString() < getString())
					|| (note.getEffect().getBend().getMovements().size() !=0 && getEffect().getBend().getMovements().size()!=0
					    && note.getEffect().getBend().getMovements().get(0) != getEffect().getBend().getMovements().get(0)))) return true;
		}
		return false;
	}
	// another possible bend conflict: releasing while playing a note on a higher string / releasing from string 1
	private boolean bendReleaseConflicts()  {
		if (getString()==1) return true;
		TGVoice voice = getVoice();
		Iterator<TGNote> it = voice.getNotes().iterator();
		while(it.hasNext()){
			TGNoteImpl note = (TGNoteImpl)it.next();
			if (note.getString() < getString()) return true;
		}
		return false;
	}

	private void paintEffects(TGLayout layout,UIPainter painter, UIInset margin, float fromX, float fromY, float spacing){
		float fromYtab = fromY + getPaintPosition(TGTrackSpacing.POSITION_TABLATURE);
		float x = fromX + getPosX() + spacing;
		float y = fromYtab + getTabPosY();
		// x,y = center of fret number in tab

		TGNoteEffect effect = getEffect();
		if(effect.isGrace()){
			layout.setTabGraceStyle(painter);
			String value = Integer.toString(effect.getGrace().getFret());
			painter.drawString(value, (x - margin.getLeft() - painter.getFMWidth(value)), y + painter.getFMMiddleLine());
		}
		if(effect.isBend()){
			paintBend(layout, painter, fromX+spacing, fromY, margin, effect.getBend());
		}else if(effect.isTremoloBar()){
			paintTremoloBar(layout, painter, (x + margin.getRight()), y);
		}else if(effect.isSlide() || effect.isHammer()){
			float nextFromX = fromX;
			TGNoteImpl nextNote = (TGNoteImpl)layout.getSongManager().getMeasureManager().getNextNote(getMeasureImpl(),getBeatImpl().getStart(),getVoice().getIndex(),getString());
			if(effect.isSlide()){
				paintSlide(layout, painter, nextNote, x, y, nextFromX);
			}else if(effect.isHammer()){
				paintHammer(layout, painter, nextNote, x, y, nextFromX);
			}
		}
	}

	public float getEffectWidth(TGLayout layout) {
		if (getEffect().isBend()) return getBendWidth(layout, getEffect().getBend());
		return(0.0f);
	}

	private float getBendWidth(TGLayout layout, TGEffectBend bend) {
		return paintBend(layout, null, 0.0f, 0.0f, new UIInset() , bend);
	}

	private float paintBend(TGLayout layout,UIPainter painter,float fromX,float fromY, UIInset margin, TGEffectBend bend){
		// fromX, fromY: top-left corner of drawing zone in current measure
		return new BendDrawing(layout, painter, fromX, fromY, margin).paint(bend.getMovements());
	}

	private static final String[] BEND_AMPLITUDES = { "", "1/4", "1/2", "3/4", "1", "1¼", "1½", "1¾", "2", "2¼", "2½", "2¾", "3" };

	/**
	 * Draws a bend on the tablature: a dashed line for a hold, else an arrow per movement (bend or release),
	 * labelled with its amplitude; or, without painter, only measures the width the arrows need.
	 */
	private class BendDrawing {

		private final TGLayout layout;
		private final UIPainter painter;
		private final float scale;
		private final float arrowWidth;
		private final boolean compactMode;
		private final boolean canPaint;
		// x
		private final float xLeft;		// start of hold
		private final float xCenter;
		private final float xRight;		// start of bend
		private final float xMax;		// end of hold
		// y
		private float yAmplitude;
		private float yLow;		// start of arrow (bend)
		private float yMiddle;
		private float yHigh;	// high position of arrows (bend/release), or dashed line (hold)

		BendDrawing(TGLayout layout, UIPainter painter, float fromX, float fromY, UIInset margin) {
			this.layout = layout;
			this.painter = painter;
			this.scale = layout.getScale();
			this.arrowWidth = 10.0f * this.scale;
			this.compactMode = getMeasureImpl().getCompactMode();

			float width = ( getVoiceImpl().getWidth() - (2.0f * this.scale) );
			this.xLeft   = fromX + getPosX() - margin.getLeft() - (4.0f * this.scale);
			this.xCenter = fromX + getPosX();
			this.xRight  = fromX + getPosX() + margin.getRight() + 1.0f * this.scale;
			this.xMax    = fromX + getPosX() + width - (8.0f * this.scale);

			// without painter or track spacing, the bend is only measured, not painted
			TGSpacing ts = getMeasureImpl().getTs();
			this.canPaint = (ts != null && painter != null);
			if (this.canPaint) {
				this.yAmplitude = fromY + ts.getPosition(TGTrackSpacing.POSITION_BEND);
				this.yHigh = this.yAmplitude  + 8.0f * this.scale;
				this.yLow = fromY + getPaintPosition(TGTrackSpacing.POSITION_TABLATURE) + getTabPosY();
				this.yMiddle = this.yLow - 6.0f * this.scale;
			}
		}

		// returns the width taken by the arrows (none in compact mode, where only the first movement is drawn)
		float paint(List<Integer> movements) {
			if (movements.isEmpty()) {
				if (this.canPaint) {
					paintHold();
				}
				return 0.0f;
			}
			float bendWidth = 0.0f;
			float x0 = this.xRight;
			boolean isFirstMovement = true;
			for (int movement : movements) {
				x0 = paintMovement(movement, isFirstMovement, x0);
				if (this.compactMode) break;
				bendWidth += this.arrowWidth;
				isFirstMovement = false;
			}
			return bendWidth;
		}

		private void paintHold() {
			this.painter.initPath();
			float xHold = this.xLeft;
			float xStep = 2.5f * this.scale;
			while (xHold < this.xMax)  {
				this.painter.moveTo( xHold, this.yHigh);
				this.painter.lineTo( xHold + xStep , this.yHigh);
				this.painter.moveTo( xHold + 2*xStep, this.yHigh);
				this.painter.lineTo( xHold + 3*xStep, this.yHigh);
				xHold += 4*xStep;
			}
			this.painter.closePath();
		}

		// draws the arrow of one movement starting at x0; returns where it ends
		private float paintMovement(int movement, boolean isFirstMovement, float x0) {
			boolean release = (movement < 0);
			boolean shortRelease = isShortRelease(release);
			float xStart = (shortRelease ? this.xCenter - (3.0f * this.scale) : x0);
			float y0 = (release ? this.yHigh : this.yLow);
			float y1 = getArrowEndY(release, shortRelease);
			float x1 = xStart + getArrowLength();
			if (this.canPaint) {
				paintArrow(release && isFirstMovement, xStart, y0, x1, y1, (release ? -1.0f : 1.0f));
				paintAmplitude(movement, isFirstMovement, xStart, x1);
			}
			return x1;
		}

		// a release starts high; in compact mode it starts at the note, unless it would cross a note on a higher string
		private boolean isShortRelease(boolean release) {
			return (release && this.compactMode && !bendReleaseConflicts());
		}

		private float getArrowEndY(boolean release, boolean shortRelease) {
			if (!release) {
				return this.yHigh;
			}
			return (shortRelease ? this.yMiddle : this.yLow);
		}

		// 2nd part of arrow: shorter in compact mode
		private float getArrowLength() {
			return (this.compactMode ? (3.0f * this.scale) : this.arrowWidth);
		}

		private void paintArrow(boolean preBend, float x0, float y0, float x1, float y1, float bendDirection) {
			this.layout.setTabEffectStyle(this.painter);
			this.painter.setLineWidth(this.layout.getLineWidth(1));
			this.painter.initPath();
			if (!this.compactMode && preBend) {
				this.painter.moveTo( x0, y0 );
				this.painter.lineTo( x0, y1 );
				paintArrowHead(x0, y0, 1.0f);
			}
			this.painter.moveTo( x0, y0 );
			this.painter.lineTo( x0 + (1.0f * this.scale), y0 );
			// 2nd part of arrow: shorter in compact mode
			if (this.compactMode) {
				this.painter.cubicTo(x0 + (1.0f * this.scale), y0,
								x0 + (3.0f * this.scale), y0,
								x0 + (3.0f * this.scale), y0 - 2.0f * bendDirection * this.scale);
				this.painter.moveTo( x0 + (3.0f * this.scale), y0 - 2.0f * bendDirection * this.scale);
				this.painter.lineTo( x0 + (3.0f * this.scale), y1);
			} else {
				this.painter.cubicTo(x0 + (1.0f * this.scale), y0,     x1, y0,    x1 , y1);
			}
			paintArrowHead(x1, y1, bendDirection);
			this.painter.closePath();
		}

		private void paintArrowHead(float x, float y, float bendDirection) {
			this.painter.moveTo( x, y);
			this.painter.lineTo( x - (2.0f * this.scale), y + 2.0f * bendDirection * this.scale);
			this.painter.moveTo( x, y);
			this.painter.lineTo( x + (2.0f * this.scale), y + 2.0f * bendDirection * this.scale);
		}

		// bends are labelled, and a first release (from a pre-bend); not when other bent notes of the beat conflict
		private void paintAmplitude(int movement, boolean isFirstMovement, float x0, float x1) {
			if (multipleBendConflicts() || (movement <= 0 && !isFirstMovement)) {
				return;
			}
			int amount = Math.abs(movement);
			float xAmplitude = (movement > 0 ? x1 : x0);
			if (amount % 4 != 0) xAmplitude -= 4.0f * this.scale;	// left shift except for short strings (n*full)
			String amplitude = (amount < BEND_AMPLITUDES.length ? BEND_AMPLITUDES[amount] : "");
			this.layout.setOfflineEffectStyle(this.painter);
			this.painter.drawString(amplitude, xAmplitude, this.yAmplitude + this.painter.getFMTopLine());
		}
	}

	private void paintTremoloBar(TGLayout layout,UIPainter painter,float fromX,float fromY){
		float scale = layout.getScale();
		float x1 = fromX + (1.0f * scale);
		float x2 = x1 + (8.0f * scale);
		float y1 = fromY;
		float y2 = y1 + (9.0f * scale);
		layout.setTabEffectStyle(painter);
		painter.setLineWidth(layout.getLineWidth(1));
		painter.initPath();
		painter.moveTo(x1,y1);
		painter.lineTo(x1 + ( (x2 - x1) / 2 ),y2);
		painter.moveTo(x1 + ( (x2 - x1) / 2 ),y2);
		painter.lineTo(x2,y1);
		painter.closePath();
	}

	private void paintSlide(TGLayout layout,UIPainter painter,TGNoteImpl nextNote,float fromX,float fromY,float nextFromX){
		float xScale = layout.getScale();
		float yScale = (layout.getStringSpacing() / 10.0f);
		float yMove = (3.0f * yScale);
		float x = fromX;
		float y = fromY;
		layout.setTabEffectStyle(painter);
		painter.setLineWidth(layout.getLineWidth(1));
		if(nextNote != null){
			float nextX = nextNote.getPosX() + nextFromX + nextNote.getBeatImpl().getSpacing(layout);
			float nextY = y;

			if(nextNote.getValue() < getValue()){
				y -= yMove;
				nextY += yMove;
			}else if(nextNote.getValue() > getValue()){
				y += yMove;
				nextY -= yMove;
			}else{
				y -= yMove;
				nextY -= yMove;
			}
			painter.initPath();
			painter.moveTo(x + (5f * xScale), y);
			painter.lineTo(nextX - (5f * xScale), nextY);
			painter.closePath();
		}else{
			painter.initPath();
			painter.moveTo(x + (5f * xScale), y - yMove);
			painter.lineTo(x + (18f * xScale), y - yMove);
			painter.closePath();
		}
	}

	private void paintHammer(TGLayout layout, UIPainter painter, TGNoteImpl nextNote, float fromX, float fromY,float nextFromX){
		float scale = layout.getScale();
		float x = (fromX + (5.0f * scale));
		float y = fromY;

		float width = (nextNote != null ? ((nextNote.getPosX() + nextFromX + nextNote.getBeatImpl().getSpacing(layout)) - (5f * scale) - x) : (10f * scale));
		float height1 = (layout.getStringSpacing() / 2f);
		float height2 = (height1 - (scale * 2f));

		layout.setTabEffectStyle(painter);
		painter.setLineWidth(layout.getLineWidth(1));
		painter.initPath(UIPainter.PATH_FILL);
		painter.moveTo(x, y);
		painter.cubicTo(x, y - height1, x + width, y - height1, x + width, y);
		painter.cubicTo(x + width, y - height2, x, y - height2, x, y);
		painter.closePath();
	}

	private void paintGrace(TGLayout layout, UIPainter painter,float fromX,float fromY){
		float scale = (layout.getScoreLineSpacing() / 2.25f);

		float x = fromX - (2f * scale);
		float y = fromY + (scale / 3);

		layout.setScoreEffectStyle(painter);
		painter.setLineWidth(layout.getLineWidth(1));
		painter.initPath(UIPainter.PATH_FILL);
		TGNotePainter.paintFooter(painter,x, y , -1 , scale);
		painter.closePath();

		painter.initPath();
		painter.moveTo(x, y - (2f * scale));
		painter.lineTo(x, y + (2f * scale) - (scale / 4f));
		painter.closePath();

		painter.initPath(UIPainter.PATH_DRAW | UIPainter.PATH_FILL);
		TGNotePainter.paintNote(painter, x - scale * 1.33f, y + scale + (scale / 4f),  scale);
		painter.closePath();

		painter.initPath();
		painter.moveTo(x - scale, y );
		painter.lineTo(x + scale, y - scale);
		painter.closePath();
	}

	private void paintVibrato(TGLayout layout, UIPainter painter,float fromX,float fromY){
		float scale = layout.getScale();
		float x = fromX;
		float y = fromY + (2.0f * scale);
		float width = ( getVoiceImpl().getWidth() - (2.0f * scale) );


		int loops = Math.round(width / (6.0f * scale) );
		if( loops > 0 ){
			layout.setTabEffectStyle(painter);
			painter.setLineWidth(layout.getLineWidth(1));
			painter.initPath(UIPainter.PATH_FILL);
			painter.moveTo(( x + ((2.0f) * scale) ),( y + (1.0f * scale) ));
			for( int i = 0; i < loops ; i ++ ){
				x = (fromX + ( (6.0f * scale) * i ) );
				painter.lineTo(( x + (2.0f * scale) ),( y + (1.0f * scale) ));
				painter.cubicTo(( x + (2.0f * scale) ),( y + (1.0f * scale) ),( x + (3.0f * scale) ), y ,( x + (4.0f * scale) ),( y + (1.0f * scale) ));
				painter.lineTo(( x + (6.0f * scale) ),( y + (3.0f * scale) ));
			}

			painter.lineTo(( x + (7.0f * scale) ),( y + (2.0f * scale) ));
			painter.cubicTo(( x + (7.0f * scale) ),( y + (2.0f * scale) ),( x + (8.0f * scale) ),( y + (2.0f * scale) ),( x + (7.0f * scale) ),( y + (3.0f * scale) ));

			for( int i = (loops - 1); i >= 0 ; i -- ){
				x = (fromX + ( (6.0f * scale) * i ) );
				painter.lineTo(( x + (6.0f * scale) ),( y + (4.0f * scale) ));
				painter.cubicTo(( x + (6.0f * scale) ),( y + (4.0f * scale) ),( x + (5.0f * scale) ),( y + (5.0f * scale) ),( x + (4.0f * scale) ),( y + (4.0f * scale) ));
				painter.lineTo(( x + (2.0f * scale) ),( y + (2.0f * scale) ));
				painter.lineTo(( x + (1.0f * scale) ),( y + (3.0f * scale) ));
			}
			painter.cubicTo(( x + (1.0f * scale) ),( y + (3.0f * scale) ), x ,( y + (3.0f * scale) ),( x + (1.0f * scale) ),( y + (2.0f * scale) ));
			painter.lineTo(( x + (2.0f * scale) ),( y + (1.0f * scale) ));
			painter.closePath();
		}
	}

	private void paintTrill(TGLayout layout, UIPainter painter,float fromX,float fromY){
		String string = "Tr";
		float fmWidth = painter.getFMWidth( string );
		float scale = layout.getScale();
		float x = fromX + fmWidth;
		float y = fromY + (4.0f * scale);
		float textY = (fromY + (2.0f * scale) + painter.getFMTopLine());
		float width = ( getVoiceImpl().getWidth() - fmWidth - (2.0f * scale) );

		int loops = Math.round(width / (6.0f * scale) );
		if( loops > 0 ){
			painter.drawString(string, fromX, textY);

			layout.setTabEffectStyle(painter);
			painter.setLineWidth(layout.getLineWidth(1));
			painter.initPath(UIPainter.PATH_FILL);
			painter.moveTo(( x + (2.0f * scale) ),( y + (1.0f * scale) ));
			for( int i = 0; i < loops ; i ++ ){
				x = (fromX + fmWidth + ( (6.0f * scale) * i ) );
				painter.lineTo(( x + (2.0f * scale) ),( y + (1.0f * scale) ));
				painter.cubicTo(( x + (2.0f * scale) ),( y + (1.0f * scale) ),( x + (3.0f * scale) ), y ,( x + (4.0f * scale) ),( y + (1.0f * scale) ));
				painter.lineTo(( x + (6.0f * scale) ),( y + (3.0f * scale) ));
			}

			painter.lineTo(( x + (7.0f * scale) ),( y + (2.0f * scale) ));
			painter.cubicTo(( x + (7.0f * scale) ),( y + (2.0f * scale) ),( x + (8.0f * scale) ),( y + (2.0f * scale) ),( x + (7.0f * scale) ),( y + (3.0f * scale) ));

			for( int i = (loops - 1); i >= 0 ; i -- ){
				x = (fromX + fmWidth + ( (6.0f * scale) * i ) );
				painter.lineTo(( x + (6.0f * scale) ),( y + (4.0f * scale) ));
				painter.cubicTo(( x + (6.0f * scale) ),( y + (4.0f * scale) ),( x + (5.0f * scale) ),( y + (5.0f * scale) ),( x + (4.0f * scale) ),( y + (4.0f * scale) ));
				painter.lineTo(( x + (2.0f * scale) ),( y + (2.0f * scale) ));
				painter.lineTo(( x + (1.0f * scale) ),( y + (3.0f * scale) ));
			}
			painter.cubicTo(( x + (1.0f * scale) ),( y + (3.0f * scale) ), x ,( y + (3.0f * scale) ),( x + (1.0f * scale) ),( y + (2.0f * scale) ));
			painter.lineTo(( x + (2.0f * scale) ),( y + (1.0f * scale) ));
			painter.closePath();
		}
	}

	private void paintFadeIn(TGLayout layout, UIPainter painter,float fromX,float fromY){
		float scale = layout.getScale();
		float x = fromX;
		float y = fromY + (4.0f * scale );
		float width = getVoiceImpl().getWidth();
		painter.setLineWidth(layout.getLineWidth(1));
		painter.initPath();
		painter.moveTo ( x , y );
		painter.cubicTo( x , y , x + width, y,  x + width, y - (4.0f * scale ));
		painter.moveTo ( x , y );
		painter.cubicTo( x , y , x + width, y,  x + width, y + (4.0f * scale ));
		painter.moveTo ( x + width, y + (4.0f * scale ) );
		painter.closePath();
	}

	// ">" opening to the left
	private void paintAccentuated(TGLayout layout, UIPainter painter,float fromX,float fromY){
		float scale = layout.getScale();
		float x = fromX;
		float y = fromY + (2f * scale );
		paintChevron(layout, painter, x, y, x + (6.0f * scale ), y + (2.5f * scale ), x, y + (5.0f * scale ));
	}

	// "^" opening downwards
	private void paintHeavyAccentuated(TGLayout layout, UIPainter painter,float fromX,float fromY){
		float scale = layout.getScale();
		float x = fromX;
		float y = fromY;
		paintChevron(layout, painter, x, y + (5.0f * scale ), x + (3.0f * scale ), y + (1.0f * scale ), x + (6.0f * scale ), y + (5.0f * scale ));
	}

	// two strokes meeting at the tip (tipX, tipY)
	private void paintChevron(TGLayout layout, UIPainter painter, float x1, float y1, float tipX, float tipY, float x2, float y2){
		painter.setLineWidth(layout.getLineWidth(1));
		painter.initPath();
		painter.moveTo( x1 , y1 );
		painter.lineTo( tipX , tipY );
		painter.moveTo( tipX , tipY );
		painter.lineTo( x2 , y2 );
		painter.closePath();
	}

	public void fillBackground(TGLayout layout, UIPainter painter, UIInset margin, float fromX, float fromY) {
		UIRectangle uiRectangle = new UIRectangle();
		uiRectangle.getPosition().setX(fromX - margin.getLeft());
		uiRectangle.getPosition().setY(fromY - margin.getTop());
		uiRectangle.getSize().setWidth(margin.getLeft() + margin.getRight());
		uiRectangle.getSize().setHeight(margin.getTop() + margin.getBottom());

		layout.fillBackground(painter, uiRectangle, getMeasureImpl().isPlaying(layout));
	}

	public String getNoteLabel(TGNote note) {
		String label = null;
		if( note.isTiedNote()) {
			label = "L";
		} else if(note.getEffect().isDeadNote()) {
			label = "X";
		} else {
			label = Integer.toString(note.getValue());
		}
		return (note.getEffect().isGhostNote() ? "(" + label + ")" : label);
	}

	public float getScorePosY() {
		return this.scorePosY;
	}

	public float getTabPosY() {
		return this.tabPosY;
	}

	public TGMeasureImpl getMeasureImpl(){
		return getBeatImpl().getMeasureImpl();
	}

	public float getPaintPosition(int index){
		return getMeasureImpl().getTs().getPosition(index);
	}

	public TGBeatImpl getBeatImpl() {
		return getVoiceImpl().getBeatImpl();
	}

	public TGVoiceImpl getVoiceImpl() {
		return (TGVoiceImpl)super.getVoice();
	}

	public float getPosX() {
		return getBeatImpl().getPosX();
	}
}
