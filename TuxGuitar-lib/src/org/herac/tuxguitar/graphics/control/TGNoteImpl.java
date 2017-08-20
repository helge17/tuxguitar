package org.herac.tuxguitar.graphics.control;

import java.util.Iterator;

import org.herac.tuxguitar.graphics.control.painters.TGKeySignaturePainter;
import org.herac.tuxguitar.graphics.control.painters.TGNotePainter;
import org.herac.tuxguitar.graphics.control.painters.TGNumberPainter;
import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.song.models.TGNoteEffect;
import org.herac.tuxguitar.song.models.TGVoice;
import org.herac.tuxguitar.song.models.effects.TGEffectHarmonic;
import org.herac.tuxguitar.ui.resource.UIInset;
import org.herac.tuxguitar.ui.resource.UIPainter;
import org.herac.tuxguitar.ui.resource.UIRectangle;

public class TGNoteImpl extends TGNote {
	
	private float tabPosY;
	
	private float scorePosY;
	
	private int accidental;
	
	public TGNoteImpl(TGFactory factory) {
		super(factory);
	}
	
	public void update(TGLayout layout) {
		this.accidental = getMeasureImpl().getNoteAccidental( getRealValue() );
		this.tabPosY = ( (getString() * layout.getStringSpacing()) - layout.getStringSpacing() );
		this.scorePosY = getVoiceImpl().getBeatGroup().getY1(layout,this,getMeasureImpl().getKeySignature(),getMeasureImpl().getClef());
	}
	
	public void paint(TGLayout layout,UIPainter painter, float fromX, float fromY) {
		float spacing = getBeatImpl().getSpacing(layout);
		float tabMoveX = (2f * layout.getScale());
		
		paintScoreNote(layout, painter, fromX, fromY + getPaintPosition(TGTrackSpacing.POSITION_SCORE_MIDDLE_LINES),spacing);
		if(!layout.isPlayModeEnabled()){
			paintOfflineEffects(layout, painter,fromX,fromY, spacing);
		}
		paintTablatureNote(layout, painter, fromX + tabMoveX, fromY + getPaintPosition(TGTrackSpacing.POSITION_TABLATURE),spacing);
	}
	
	private void paintOfflineEffects(TGLayout layout,UIPainter painter,float fromX, float fromY, float spacing){		
		TGSpacing bs = getBeatImpl().getBs();
		TGSpacing ts = getMeasureImpl().getTs();
		TGNoteEffect effect = getEffect();
		
		float scale = layout.getScale();
		float tsY = (fromY + ts.getPosition(TGTrackSpacing.POSITION_EFFECTS));
		float bsY = (tsY + (ts.getSize(TGTrackSpacing.POSITION_EFFECTS) - bs.getSize( )));
		
		layout.setOfflineEffectStyle(painter);
		if(effect.isAccentuatedNote()){
			float x = fromX + getPosX() + spacing;
			float y = (bsY + bs.getPosition( TGBeatSpacing.POSITION_ACCENTUATED_EFFECT ));
			paintAccentuated(layout, painter, x, y);
		}
		if(effect.isHeavyAccentuatedNote()){
			float x = fromX + getPosX() + spacing;
			float y = (bsY + bs.getPosition( TGBeatSpacing.POSITION_HEAVY_ACCENTUATED_EFFECT ));
			paintHeavyAccentuated(layout, painter, x, y);
		}
		if(effect.isFadeIn()){
			float x = fromX + getPosX() + spacing;
			float y = (bsY + bs.getPosition( TGBeatSpacing.POSITION_FADE_IN ));
			paintFadeIn(layout, painter, x, y);
		}
		if(effect.isHarmonic() && (layout.getStyle() & TGLayout.DISPLAY_SCORE) == 0 ){
			float x = fromX + getPosX() + spacing;
			float y = (bsY + bs.getPosition( TGBeatSpacing.POSITION_HARMONIC_EFFEC ));
			String key = new String();
			key = effect.getHarmonic().isNatural() ? TGEffectHarmonic.KEY_NATURAL : key;
			key = effect.getHarmonic().isArtificial() ? TGEffectHarmonic.KEY_ARTIFICIAL : key;
			key = effect.getHarmonic().isTapped() ? TGEffectHarmonic.KEY_TAPPED : key;
			key = effect.getHarmonic().isPinch() ? TGEffectHarmonic.KEY_PINCH : key;
			key = effect.getHarmonic().isSemi() ? TGEffectHarmonic.KEY_SEMI : key;
			painter.drawString(key, x, (y + painter.getFMTopLine() + (2f * scale)));
		}
		if(effect.isTapping()){
			float x = fromX + getPosX() + spacing;
			float y = (bsY + bs.getPosition( TGBeatSpacing.POSITION_TAPPING_EFFEC ));
			painter.drawString("T", x, (y + painter.getFMTopLine() + (2f * scale)));
		}
		if(effect.isSlapping()){
			float x = fromX + getPosX() + spacing;
			float y = (bsY + bs.getPosition( TGBeatSpacing.POSITION_SLAPPING_EFFEC ));
			painter.drawString("S", x, (y + painter.getFMTopLine() + (2f * scale)));
		}
		if(effect.isPopping()){
			float x = fromX + getPosX() + spacing;
			float y = (bsY + bs.getPosition( TGBeatSpacing.POSITION_POPPING_EFFEC ));
			painter.drawString("P", x, (y + painter.getFMTopLine() + (2f * scale)));
		}
		if(effect.isPalmMute()){
			float x = fromX + getPosX() + spacing;
			float y = (bsY + bs.getPosition( TGBeatSpacing.POSITION_PALM_MUTE_EFFEC ));
			painter.drawString("P.M", x, (y + painter.getFMTopLine() + (2f * scale)));
		}
		if(effect.isLetRing()){
			float x = fromX + getPosX() + spacing;
			float y = (bsY + bs.getPosition( TGBeatSpacing.POSITION_LET_RING_EFFEC ));
			painter.drawString("L.R", x, (y + painter.getFMTopLine() + (2f * scale)));
		}
		if(effect.isVibrato()){
			float x = fromX + getPosX() + spacing;
			float y = (bsY + bs.getPosition( TGBeatSpacing.POSITION_VIBRATO_EFFEC ));
			paintVibrato(layout, painter, x, y);
		}
		if(effect.isTrill()){
			float x = fromX + getPosX() + spacing;
			float y = (bsY + bs.getPosition( TGBeatSpacing.POSITION_TRILL_EFFEC ));
			paintTrill(layout, painter, x, y);
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
		int style = layout.getStyle();
		if((style & TGLayout.DISPLAY_TABLATURE) != 0) {
			UIInset margin = new UIInset();
			
			float scale = layout.getScale();
			float x = (fromX + getPosX() + spacing);
			float y = (fromY + getTabPosY());
			float stringSpacing = layout.getStringSpacing();
			
			boolean running = (layout.isPlayModeEnabled() && getBeatImpl().isPlaying(layout));
			
			//-------------ligadura--------------------------------------
			if (isTiedNote() && (style & TGLayout.DISPLAY_SCORE) == 0) {
				float tX = 0;
				float tY = (fromY + getTabPosY() + (stringSpacing / 2f));
				TGNoteImpl noteForTie = getNoteForTie();
				if (noteForTie != null) {
					tX = (fromX + noteForTie.getPosX() + noteForTie.getBeatImpl().getSpacing(layout) + (5.0f * scale));
				}else{
					tX = (fromX + this.getPosX() + this.getBeatImpl().getSpacing(layout) - (stringSpacing * 2));
				}
				
				float tWidth = (x - tX);
				float tHeight1 = (stringSpacing / 3f);
				float tHeight2 = (tHeight1 + (scale * 2f));
				
				layout.setTabTiedStyle(painter, running);
				painter.initPath(UIPainter.PATH_FILL);
				painter.moveTo(tX, tY);
				painter.cubicTo(tX, tY + tHeight1, tX + tWidth, tY + tHeight1, tX + tWidth, tY);
				painter.cubicTo(tX + tWidth, tY + tHeight2, tX, tY + tHeight2, tX, tY);
				painter.closePath();
				
			//-------------nota--------------------------------------
			} else if(!isTiedNote()) {
				this.paintTablatureNoteValue(layout, painter, margin, x, y, running);
			}
			
			//-------------efectos--------------------------------------
			if(! layout.isPlayModeEnabled() ){
				
				paintEffects(layout, painter, margin, fromX, fromY, spacing);
				
				if((style & TGLayout.DISPLAY_SCORE) == 0){
					
					//-------------tremolo picking--------------------------------------
					if(getEffect().isTremoloPicking()){
						float y1 = (fromY + getMeasureImpl().getTrackImpl().getTabHeight() + (stringSpacing / 2));
						float y2 = (fromY + getMeasureImpl().getTrackImpl().getTabHeight() + ((stringSpacing / 2) * 5));
						
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
				}
			}
		}
	}
	
	private void paintScoreNote(TGLayout layout,UIPainter painter, float fromX, float fromY, float spacing) {
		if((layout.getStyle() & TGLayout.DISPLAY_SCORE) != 0 ){
			float scale = layout.getScoreLineSpacing();
			float layoutScale = layout.getScale();
			int direction = getVoiceImpl().getBeatGroup().getDirection();
			int key = getMeasureImpl().getKeySignature();
			int clef = getMeasureImpl().getClef();
			
			float x = ( fromX + getPosX() + spacing );
			float y1 = ( fromY + getScorePosY() ) ;
			
			
			
			//-------------foreground--------------------------------------
			boolean playing = (layout.isPlayModeEnabled() && getBeatImpl().isPlaying(layout));
			
			//----------ligadura---------------------------------------
			if (isTiedNote()) {
				TGNoteImpl noteForTie = getNoteForTie();
				float tX = x - (20.0f * layoutScale);
				float tY = (y1 + (layout.getScoreLineSpacing() / 2f));
				if (noteForTie != null) {
					float tNoteX = (fromX + noteForTie.getPosX() + noteForTie.getBeatImpl().getSpacing(layout));
					float tNoteY = (fromY + getScorePosY());
					tX = tNoteX + (10.0f * layoutScale);
					tY = (tNoteY + (layout.getScoreLineSpacing() / 2f));
				}
				float tWidth = (x - tX) - (3.0f * layoutScale);
				float tHeight1 = (layout.getScoreLineSpacing() / 2f);
				float tHeight2 = (tHeight1 - (layoutScale * 2f));
				
				layout.setTiedStyle(painter, playing);
				painter.initPath(UIPainter.PATH_FILL);
				painter.moveTo(tX, tY);
				painter.cubicTo(tX, tY - tHeight1, tX + tWidth, tY - tHeight1, tX + tWidth, tY);
				painter.cubicTo(tX + tWidth, tY - tHeight2, tX, tY - tHeight2, tX, tY);
				painter.closePath();
			}
			
			layout.setScoreNoteStyle(painter,playing);
			
			//----------sostenido--------------------------------------
			if(this.accidental == TGMeasureImpl.NATURAL){
				painter.initPath(UIPainter.PATH_FILL);
				painter.setLineWidth(layout.getLineWidth(0));
				TGKeySignaturePainter.paintNatural(painter,(x - (scale - (scale / 4)) ),(y1 + (scale / 2)), scale);
				painter.closePath();
			}
			else if(this.accidental == TGMeasureImpl.SHARP){
				painter.initPath(UIPainter.PATH_FILL);
				painter.setLineWidth(layout.getLineWidth(0));
				TGKeySignaturePainter.paintSharp(painter,(x - (scale - (scale / 4)) ),(y1 + (scale / 2)), scale);
				painter.closePath();
			}
			else if(this.accidental == TGMeasureImpl.FLAT){
				painter.initPath(UIPainter.PATH_FILL);
				painter.setLineWidth(layout.getLineWidth(0));
				TGKeySignaturePainter.paintFlat(painter,(x - (scale - (scale / 4)) ),(y1 + (scale / 2)), scale);
				painter.closePath();
			}
			//----------fin sostenido--------------------------------------
			if(getEffect().isHarmonic()){
				boolean fill = (getVoice().getDuration().getValue() >= TGDuration.QUARTER);
				painter.setLineWidth(layout.getLineWidth(1));
				painter.initPath((fill ? (UIPainter.PATH_FILL | UIPainter.PATH_DRAW) : UIPainter.PATH_DRAW));
				TGNotePainter.paintHarmonic(painter, x, y1 + (1f * (scale / 10f)), (layout.getScoreLineSpacing() - ((scale / 10f) * 2f)));
				painter.closePath();
			}else{
				boolean fill = (getVoice().getDuration().getValue() >= TGDuration.QUARTER);
				float noteX = (fill ? (x - (0.60f * (scale / 10f))) : x);
				float noteY = (fill ? (y1 + (0.60f * (scale / 10f))) : (y1 + (1f * (scale / 10f))));
				float noteScale = (fill ? ((layout.getScoreLineSpacing() - ((scale / 10f) * 1f) )) : ((layout.getScoreLineSpacing() - ((scale / 10f) * 2f) )));
				
				painter.setLineWidth(layout.getLineWidth(1));
				painter.initPath((fill ? UIPainter.PATH_FILL : UIPainter.PATH_DRAW));
				TGNotePainter.paintNote(painter, noteX, noteY, noteScale);
				painter.closePath();
			}
			
			if(!layout.isPlayModeEnabled() ){
				float scoreNoteWidth = layout.getScoreNoteWidth();
				
				if(getEffect().isGrace()){
					paintGrace(layout, painter,x ,y1);
				}
				
				//PUNTILLO y DOBLE PUNTILLO
				if (getVoice().getDuration().isDotted() || getVoice().getDuration().isDoubleDotted()) {
					getVoiceImpl().paintDot(layout, painter,( x + (12.0f * (scale / 8.0f) ) ), ( y1 + (layout.getScoreLineSpacing()/ 2)), (scale / 10.0f) );
				}
				
				//dibujo el pie
				if( getVoice().getDuration().getValue() >= TGDuration.HALF ){
					layout.setScoreNoteFooterStyle(painter);
					float xMove = ((direction == TGBeatGroup.DIRECTION_UP ? scoreNoteWidth : 0));
					float y2 = (fromY + getVoiceImpl().getBeatGroup().getY2(layout,getPosX() + spacing, key, clef));
					
					//staccato
					if (getEffect().isStaccato()) {
						float size = (3f * layoutScale);
						float sX = x + xMove;
						float sY = (y2 + ((4f * layoutScale) * ((direction == TGBeatGroup.DIRECTION_UP) ? -1 : 1 )));
						layout.setScoreEffectStyle(painter);
						painter.setLineWidth(layout.getLineWidth(1));
						painter.initPath(UIPainter.PATH_FILL);
						painter.moveTo(sX, sY);
						painter.addCircle(sX, sY, size);
						painter.closePath();
					}
					//tremolo picking
					if(getEffect().isTremoloPicking()){
						layout.setScoreEffectStyle(painter);
						painter.setLineWidth(layout.getLineWidth(2));
						painter.initPath();
						float tpY = fromY;
						if((direction == TGBeatGroup.DIRECTION_UP)){
							tpY += (getVoiceImpl().getBeatGroup().getMaxNote().getScorePosY() - layout.getScoreLineSpacing() - (4f * layoutScale));
						}else{
							tpY += (getVoiceImpl().getBeatGroup().getMinNote().getScorePosY() + layout.getScoreLineSpacing() + (4f * layoutScale));
						}
						for(int i = TGDuration.EIGHTH;i <= getEffect().getTremoloPicking().getDuration().getValue(); i += i){
							painter.moveTo(x + xMove - (3f * layoutScale), tpY + (1f * layoutScale));
							painter.lineTo(x + xMove + (4f * layoutScale), tpY - (1f * layoutScale));
							tpY += (4f * layoutScale);
						}
						painter.closePath();
						painter.setLineWidth(layout.getLineWidth(1));
					}
				}else{
					
					//staccato
					if (getEffect().isStaccato()) {
						float size = (3f * layoutScale);
						float sX = (x + (scoreNoteWidth / 2));
						float sY = (fromY + getVoiceImpl().getBeatGroup().getMinNote().getScorePosY() + layout.getScoreLineSpacing()) + (2f * layoutScale);
						layout.setScoreEffectStyle(painter);
						painter.setLineWidth(layout.getLineWidth(1));
						painter.initPath(UIPainter.PATH_FILL);
						painter.moveTo(sX, sY);
						painter.addCircle(sX, sY, size);
						painter.closePath();
					}
					//tremolo picking
					if(getEffect().isTremoloPicking()){
						layout.setScoreEffectStyle(painter);
						painter.setLineWidth(layout.getLineWidth(2));
						painter.initPath();
						float tpX = ((x + (scoreNoteWidth / 2)));
						float tpY = (fromY + (getVoiceImpl().getBeatGroup().getMaxNote().getScorePosY() - layout.getScoreLineSpacing() - (4f  * layoutScale)));
						for(int i = TGDuration.EIGHTH;i <= getEffect().getTremoloPicking().getDuration().getValue(); i += i){
							painter.moveTo(tpX - (3f * layoutScale), tpY + (1f * layoutScale));
							painter.lineTo(tpX + (4f * layoutScale),tpY - (1f * layoutScale));
							tpY += (4f * layoutScale);
						}
						painter.closePath();
						painter.setLineWidth(layout.getLineWidth(1));
					}
				}
			}
		}
	}
	
	private TGNoteImpl getNoteForTie() {
		for (int i = getMeasureImpl().countBeats() - 1; i >= 0; i--) {
			TGBeat beat = getMeasureImpl().getBeat(i);
			TGVoice voice = beat.getVoice( getVoice().getIndex() );
			if (beat.getStart() < getBeatImpl().getStart() && !voice.isRestVoice()) {
				Iterator<TGNote> it = voice.getNotes().iterator();
				while(it.hasNext()){
					TGNoteImpl note = (TGNoteImpl)it.next();
					if (note.getString() == getString()) {
						return note;
					}
				}
			}
		}
		return null;
	}
	
	private void paintEffects(TGLayout layout,UIPainter painter, UIInset margin, float fromX, float fromY, float spacing){
		float x = fromX + getPosX() + spacing;
		float y = fromY + getTabPosY();
		TGNoteEffect effect = getEffect();
		if(effect.isGrace()){
			layout.setTabGraceStyle(painter);
			String value = Integer.toString(effect.getGrace().getFret());
			painter.drawString(value, (x - margin.getLeft() - painter.getFMWidth(value)), y + painter.getFMMiddleLine());
		}
		if(effect.isBend()){
			paintBend(layout, painter, (x + margin.getRight()), y);
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
	
	private void paintBend(TGLayout layout,UIPainter painter,float fromX,float fromY){
		float scale = layout.getScale();
		float x = (fromX + (1.0f * scale));
		float y = (fromY - (2.0f * scale));
		
		layout.setTabEffectStyle(painter);
		painter.setLineWidth(layout.getLineWidth(1));
		painter.initPath();
		painter.moveTo( x, y );
		painter.lineTo( x + (1.0f * scale), y );
		painter.cubicTo(x + (1.0f * scale), y,  x + (3.0f * scale), y , x + (3.0f * scale), y - (2.0f * scale));
		painter.moveTo( x + (3.0f * scale), y - (2.0f * scale) );
		painter.lineTo( x + (3.0f * scale), y - (12.0f * scale));
		painter.moveTo( x + (3.0f * scale), y - (12.0f * scale));
		painter.lineTo( x + (1.0f * scale), y - (10.0f * scale));
		painter.moveTo( x + (3.0f * scale), y - (12.0f * scale));
		painter.lineTo( x + (5.0f * scale), y - (10.0f * scale));
		painter.closePath();
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
	
	private void paintAccentuated(TGLayout layout, UIPainter painter,float fromX,float fromY){
		float scale = layout.getScale();
		float x = fromX;
		float y = fromY + (2f * scale );
		painter.setLineWidth(layout.getLineWidth(1));
		painter.initPath();
		painter.moveTo( x , y );
		painter.lineTo( x + (6.0f * scale ) , y + (2.5f * scale ));
		painter.moveTo( x + (6.0f * scale ) , y + (2.5f * scale ));
		painter.lineTo( x , y + (5.0f * scale ));
		painter.closePath();
	}
	
	private void paintHeavyAccentuated(TGLayout layout, UIPainter painter,float fromX,float fromY){
		float scale = layout.getScale();
		float x = fromX;
		float y = fromY;
		painter.setLineWidth(layout.getLineWidth(1));
		painter.initPath();
		painter.moveTo( x , y + (5.0f * scale ) );
		painter.lineTo( x + (3.0f * scale ) , y + (1.0f * scale ));
		painter.moveTo( x + (3.0f * scale ) , y + (1.0f * scale ));
		painter.lineTo( x + (6.0f * scale ) , y + (5.0f * scale ) );
		painter.closePath();
	}
	
	public void fillBackground(TGLayout layout, UIPainter painter, UIInset margin, float fromX, float fromY) {
		UIRectangle uiRectangle = new UIRectangle();
		uiRectangle.getPosition().setX(fromX - margin.getLeft());
		uiRectangle.getPosition().setY(fromY - margin.getTop());
		uiRectangle.getSize().setWidth(margin.getLeft() + margin.getRight());
		uiRectangle.getSize().setHeight(margin.getTop() + margin.getBottom());
		
		layout.fillBackground(painter, uiRectangle);
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
	
	public int getRealValue(){
		return (getValue() + getMeasureImpl().getTrack().getString(getString()).getValue());
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