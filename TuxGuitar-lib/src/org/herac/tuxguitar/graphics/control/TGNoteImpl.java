package org.herac.tuxguitar.graphics.control;

import java.util.Iterator;

import org.herac.tuxguitar.graphics.TGPainter;
import org.herac.tuxguitar.graphics.TGRectangle;
import org.herac.tuxguitar.graphics.control.painters.TGKeySignaturePainter;
import org.herac.tuxguitar.graphics.control.painters.TGNotePainter;
import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.song.models.TGNoteEffect;
import org.herac.tuxguitar.song.models.TGVoice;
import org.herac.tuxguitar.song.models.effects.TGEffectHarmonic;

public class TGNoteImpl extends TGNote {
	
	private TGRectangle noteOrientation;
	
	private float tabPosY;
	
	private float scorePosY;
	
	private int accidental;
	
	public TGNoteImpl(TGFactory factory) {
		super(factory);
		this.noteOrientation = new TGRectangle();
	}
	
	/**
	 * Actualiza los valores para dibujar
	 */
	public void update(TGLayout layout) {
		this.accidental = getMeasureImpl().getNoteAccidental( getRealValue() );
		this.tabPosY = ( (getString() * layout.getStringSpacing()) - layout.getStringSpacing() );
		this.scorePosY = getVoiceImpl().getBeatGroup().getY1(layout,this,getMeasureImpl().getKeySignature(),getMeasureImpl().getClef());
	}
	
	/**
	 * Pinta la nota
	 */
	public void paint(TGLayout layout,TGPainter painter, float fromX, float fromY) {
		float spacing = getBeatImpl().getSpacing(layout);
		paintScoreNote(layout, painter, fromX, fromY + getPaintPosition(TGTrackSpacing.POSITION_SCORE_MIDDLE_LINES),spacing);
		if(!layout.isPlayModeEnabled()){
			paintOfflineEffects(layout, painter,fromX,fromY, spacing);
		}
		paintTablatureNote(layout, painter, fromX, fromY + getPaintPosition(TGTrackSpacing.POSITION_TABLATURE),spacing);
	}
	
	private void paintOfflineEffects(TGLayout layout,TGPainter painter,float fromX, float fromY, float spacing){		
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
	
	/**
	 * Pinta la nota en la tablatura
	 */
	public void paintTablatureNote(TGLayout layout,TGPainter painter, float fromX, float fromY, float spacing) {
		int style = layout.getStyle();
		if((style & TGLayout.DISPLAY_TABLATURE) != 0){
			float scale = layout.getScale();
			float xMove = (2 * scale);
			float x = (fromX + getPosX() + spacing + xMove);
			float y = (fromY + getTabPosY());
			float stringSpacing = layout.getStringSpacing();
			
			this.noteOrientation.setX(Math.round(x));
			this.noteOrientation.setY(Math.round(y));
			this.noteOrientation.setWidth(1);
			this.noteOrientation.setHeight(1);
			
			layout.setTabNoteStyle(painter, (layout.isPlayModeEnabled() && getBeatImpl().isPlaying(layout)));
			//-------------ligadura--------------------------------------
			if (isTiedNote() && (style & TGLayout.DISPLAY_SCORE) == 0) {
				float tX = 0;
				float tY = 0;
				float tWidth = 0;
				float tHeight = (stringSpacing * 3);
				TGNoteImpl noteForTie = getNoteForTie();
				if (noteForTie != null) {
					tX = (fromX + noteForTie.getPosX() + noteForTie.getBeatImpl().getSpacing(layout));
					tY = (fromY + noteForTie.getTabPosY() + stringSpacing);
					tWidth = (x - tX);
				}else{
					TGRectangle r = layout.getNoteOrientation(painter, x, y, this);
					tX = r.getX() - (stringSpacing * 2);
					tY = (fromY + getTabPosY() + stringSpacing);
					tWidth = (stringSpacing * 2);
				}
				painter.setLineWidth(layout.getLineWidth(1));
				painter.initPath();
				painter.addArc(tX, (tY - tHeight ), tWidth, tHeight, 225, 90);
				painter.closePath();
				
			//-------------nota--------------------------------------
			} else if(!isTiedNote()){
				TGRectangle r = layout.getNoteOrientation(painter, x, y, this);
				this.noteOrientation.setX(r.getX());
				this.noteOrientation.setY(r.getY());
				this.noteOrientation.setWidth(r.getWidth());
				this.noteOrientation.setHeight(r.getHeight());
				String visualNote = (getEffect().isDeadNote()) ? "X" : Integer.toString(getValue());
				visualNote = (getEffect().isGhostNote()) ? "(" + visualNote + ")" : visualNote;
				painter.drawString(visualNote, this.noteOrientation.getX(), this.noteOrientation.getY());
			}
			
			//-------------efectos--------------------------------------
			if(! layout.isPlayModeEnabled() ){
				
				paintEffects(layout,painter,fromX,fromY,spacing);
				
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
	
	/**
	 * Pinta la nota en la partitura
	 */
	private void paintScoreNote(TGLayout layout,TGPainter painter, float fromX, float fromY, float spacing) {
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
			
			layout.setScoreNoteStyle(painter,playing);
			
			//----------ligadura---------------------------------------
			if (isTiedNote()) {
				TGNoteImpl noteForTie = getNoteForTie();
				float tX = x - (20.0f * layoutScale);
				float tY = y1 - (2.0f * layoutScale);
				float tWidth = (20.0f * layoutScale);
				float tHeight = (30.0f * layoutScale);
				if (noteForTie != null) {
					float tNoteX = (fromX + noteForTie.getPosX() + noteForTie.getBeatImpl().getSpacing(layout));
					float tNoteY = (fromY + getScorePosY());
					tX = tNoteX + (6.0f * layoutScale);
					tY = tNoteY - (3.0f * layoutScale);
					tWidth = (x - tNoteX) - (3.0f * layoutScale);
					tHeight = (35.0f * layoutScale);
				}
				painter.setLineWidth(layout.getLineWidth(1));
				painter.initPath();
				painter.addArc(tX,tY, tWidth, tHeight, 45, 90);
				painter.closePath();
			}
			//----------sostenido--------------------------------------
			if(this.accidental == TGMeasureImpl.NATURAL){
				painter.initPath(TGPainter.PATH_FILL);
				painter.setLineWidth(layout.getLineWidth(0));
				TGKeySignaturePainter.paintNatural(painter,(x - (scale - (scale / 4)) ),(y1 + (scale / 2)), scale);
				painter.closePath();
			}
			else if(this.accidental == TGMeasureImpl.SHARP){
				painter.initPath(TGPainter.PATH_FILL);
				painter.setLineWidth(layout.getLineWidth(0));
				TGKeySignaturePainter.paintSharp(painter,(x - (scale - (scale / 4)) ),(y1 + (scale / 2)), scale);
				painter.closePath();
			}
			else if(this.accidental == TGMeasureImpl.FLAT){
				painter.initPath(TGPainter.PATH_FILL);
				painter.setLineWidth(layout.getLineWidth(0));
				TGKeySignaturePainter.paintFlat(painter,(x - (scale - (scale / 4)) ),(y1 + (scale / 2)), scale);
				painter.closePath();
			}
			//----------fin sostenido--------------------------------------
			if(getEffect().isHarmonic()){
				boolean fill = (getVoice().getDuration().getValue() >= TGDuration.QUARTER);
				painter.setLineWidth(layout.getLineWidth(1));
				painter.initPath((fill ? (TGPainter.PATH_FILL | TGPainter.PATH_DRAW) : TGPainter.PATH_DRAW));
				TGNotePainter.paintHarmonic(painter, x, y1 + (1f * (scale / 10f)), (layout.getScoreLineSpacing() - ((scale / 10f) * 2f)));
				painter.closePath();
			}else{
				boolean fill = (getVoice().getDuration().getValue() >= TGDuration.QUARTER);
				float noteX = (fill ? (x - (0.60f * (scale / 10f))) : x);
				float noteY = (fill ? (y1 + (0.60f * (scale / 10f))) : (y1 + (1f * (scale / 10f))));
				float noteScale = (fill ? ((layout.getScoreLineSpacing() - ((scale / 10f) * 1f) )) : ((layout.getScoreLineSpacing() - ((scale / 10f) * 2f) )));
				
				painter.setLineWidth(layout.getLineWidth(1));
				painter.initPath((fill ? TGPainter.PATH_FILL : TGPainter.PATH_DRAW));
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
						painter.initPath(TGPainter.PATH_FILL);
						painter.moveTo(sX - (size / 2),sY - (size / 2));
						painter.addOval(sX - (size / 2),sY - (size / 2), size, size);
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
						painter.initPath(TGPainter.PATH_FILL);
						painter.moveTo(sX - (size / 2), sY - (size / 2));
						painter.addOval(sX - (size / 2), sY - (size / 2), size, size);
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
	
	/**
	 * Encuentra la nota a la que esta ligada
	 */
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
	
	/**
	 * Pinta los efectos
	 */
	private void paintEffects(TGLayout layout,TGPainter painter, float fromX, float fromY, float spacing){
		float x = fromX + getPosX() + spacing;
		float y = fromY + getTabPosY();
		TGNoteEffect effect = getEffect();
		if(effect.isGrace()){
			layout.setTabGraceStyle(painter);
			String value = Integer.toString(effect.getGrace().getFret());
			painter.drawString(value, (this.noteOrientation.getX() - painter.getFMWidth(value) - 2), this.noteOrientation.getY() );
		}
		if(effect.isBend()){
			paintBend(layout, painter,(this.noteOrientation.getX() + this.noteOrientation.getWidth()), y);
		}else if(effect.isTremoloBar()){
			paintTremoloBar(layout, painter,(this.noteOrientation.getX() + this.noteOrientation.getWidth()), y);
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
	
	private void paintBend(TGLayout layout,TGPainter painter,float fromX,float fromY){
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
	
	private void paintTremoloBar(TGLayout layout,TGPainter painter,float fromX,float fromY){
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
	
	private void paintSlide(TGLayout layout,TGPainter painter,TGNoteImpl nextNote,float fromX,float fromY,float nextFromX){
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
			painter.moveTo(x + (6.0f * xScale),y);
			painter.lineTo(nextX - (3.0f * xScale),nextY);
			painter.closePath();
		}else{
			painter.initPath();
			painter.moveTo(x + (6.0f * xScale),y - yMove);
			painter.lineTo(x + (17.0f * xScale),y - yMove);
			painter.closePath();
		}
	}
	
	private void paintHammer(TGLayout layout, TGPainter painter, TGNoteImpl nextNote, float fromX, float fromY,float nextFromX){
		float xScale = layout.getScale();
		float yScale = (layout.getStringSpacing() / 10.0f);
		
		float x = (fromX + (7.0f * xScale));
		float y = (fromY - (5.0f * yScale));
		
		float width = (nextNote != null)?( (nextNote.getPosX() + nextFromX + nextNote.getBeatImpl().getSpacing(layout)) - (4.0f * xScale) - x ):(10.0f * xScale);
		float height = (15.0f * yScale);
		layout.setTabEffectStyle(painter);
		painter.setLineWidth(layout.getLineWidth(1));
		painter.initPath();
		painter.addArc(x,y, width, height, 45,90);
		painter.closePath();
	}
	
	private void paintGrace(TGLayout layout, TGPainter painter,float fromX,float fromY){
		float scale = ( layout.getScoreLineSpacing() / 2.25f );
		
		float x = fromX - (2f * scale);
		float y = fromY + (scale / 3);
		
		layout.setScoreEffectStyle(painter);
		painter.setLineWidth(layout.getLineWidth(1));
		painter.initPath(TGPainter.PATH_FILL);
		TGNotePainter.paintFooter(painter,x, y , -1 , scale);
		painter.closePath();
		
		painter.initPath();
		painter.moveTo(x, y - (2f * scale));
		painter.lineTo(x, y + (2f * scale) - (scale / 4f));
		painter.closePath();
		
		painter.initPath(TGPainter.PATH_DRAW | TGPainter.PATH_FILL);
		TGNotePainter.paintNote(painter, x - scale * 1.33f, y + scale + (scale / 4f),  scale);
		painter.closePath();
		
		painter.initPath();
		painter.moveTo(x - scale, y );
		painter.lineTo(x + scale, y - scale);
		painter.closePath();
	}
	
	private void paintVibrato(TGLayout layout, TGPainter painter,float fromX,float fromY){
		float scale = layout.getScale();
		float x = fromX;
		float y = fromY + (2.0f * scale);
		float width = ( getVoiceImpl().getWidth() - (2.0f * scale) );
		
		int loops = Math.round(width / (6.0f * scale) );
		if( loops > 0 ){
			layout.setTabEffectStyle(painter);
			painter.setLineWidth(layout.getLineWidth(1));
			painter.initPath(TGPainter.PATH_FILL);
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
	
	private void paintTrill(TGLayout layout, TGPainter painter,float fromX,float fromY){
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
			painter.initPath(TGPainter.PATH_FILL);
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
	
	private void paintFadeIn(TGLayout layout, TGPainter painter,float fromX,float fromY){
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
	
	private void paintAccentuated(TGLayout layout, TGPainter painter,float fromX,float fromY){
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
	
	private void paintHeavyAccentuated(TGLayout layout, TGPainter painter,float fromX,float fromY){
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