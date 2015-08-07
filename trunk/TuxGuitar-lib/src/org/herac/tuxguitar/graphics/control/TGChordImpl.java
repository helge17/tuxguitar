/*
 * Created on 01-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.graphics.control;

import org.herac.tuxguitar.graphics.TGColor;
import org.herac.tuxguitar.graphics.TGFont;
import org.herac.tuxguitar.graphics.TGImage;
import org.herac.tuxguitar.graphics.TGPainter;
import org.herac.tuxguitar.graphics.TGResourceFactory;
import org.herac.tuxguitar.song.models.TGChord;
import org.herac.tuxguitar.song.models.TGString;
import org.herac.tuxguitar.song.models.TGTrack;
/**
 * @author julian
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class TGChordImpl extends TGChord {
	
	public static final int MAX_FRETS = 6;
	
	private int style;
	private float posX;
	private float posY;
	private float width;
	private float height;
	private int tonic;
	private float diagramWidth;
	private float diagramHeight;
	private float nameWidth;
	private float nameHeight;
	private TGImage diagram;
	private TGColor foregroundColor;
	private TGColor backgroundColor;
	private TGColor noteColor;
	private TGColor tonicColor;
	private TGColor color;
	private TGFont font;
	private TGFont firstFretFont;
	private float firstFretSpacing;
	private float stringSpacing;
	private float fretSpacing;
	private float noteSize;
	private float lineWidth;
	
	private boolean editing;
	private Object registryKey;
	
	public TGChordImpl(int length) {
		super(length);
	}

	public boolean isEditing() {
		return this.editing;
	}
	
	public void setEditing(boolean editing) {
		this.editing = editing;
	}
	
	public void setPosX(float posX){
		this.posX = posX;
	}
	
	public void setPosY(float posY){
		this.posY = posY;
	}
	
	public float getPosY() {
		return this.posY;
	}
	
	public float getWidth(){
		return this.width;
	}
	
	public float getHeight(){
		return this.height;
	}
	
	public void setStyle(int style) {
		this.style = style;
	}
	
	public void setTonic(int tonic){
		if(!this.isDisposed() && this.tonic != tonic){
			this.dispose();
		}
		this.tonic = tonic;
	}
	
	public TGColor getForegroundColor() {
		return this.foregroundColor;
	}
	
	public void setForegroundColor(TGColor foregroundColor) {
		if(!this.isDisposed() && !this.isSameColor(this.foregroundColor, foregroundColor)){
			this.dispose();
		}
		this.foregroundColor = foregroundColor;
	}
	
	public TGColor getBackgroundColor() {
		return this.backgroundColor;
	}
	
	public void setBackgroundColor(TGColor backgroundColor) {
		if(!this.isDisposed() && !this.isSameColor(this.backgroundColor, backgroundColor)){
			this.dispose();
		}
		this.backgroundColor = backgroundColor;
	}
	
	public TGColor getColor() {
		return this.color;
	}
	
	public void setColor(TGColor color) {
		if(!this.isDisposed() && !this.isSameColor(this.color, color)){
			this.dispose();
		}
		this.color = color;
	}
	
	public TGColor getNoteColor() {
		return this.noteColor;
	}
	
	public void setNoteColor(TGColor noteColor) {
		if(!this.isDisposed() && !this.isSameColor(this.noteColor, noteColor)){
			this.dispose();
		}
		this.noteColor = noteColor;
	}
	
	public TGColor getTonicColor() {
		return this.tonicColor;
	}
	
	public void setTonicColor(TGColor tonicColor) {
		if(!this.isDisposed() && !this.isSameColor(this.tonicColor, tonicColor)){
			this.dispose();
		}
		this.tonicColor = tonicColor;
	}
	
	public float getFirstFretSpacing() {
		return this.firstFretSpacing;
	}
	
	public void setFirstFretSpacing(float firstFretSpacing) {
		if(!this.isDisposed() && this.firstFretSpacing != firstFretSpacing){
			this.dispose();
		}
		this.firstFretSpacing = firstFretSpacing;
	}
	
	public float getFretSpacing() {
		return this.fretSpacing;
	}
	
	public void setFretSpacing(float fretSpacing) {
		if(!this.isDisposed() && this.fretSpacing != fretSpacing){
			this.dispose();
		}
		this.fretSpacing = fretSpacing;
	}
	
	public float getStringSpacing() {
		return this.stringSpacing;
	}
	
	public void setStringSpacing(float stringSpacing) {
		if(!this.isDisposed() && this.stringSpacing != stringSpacing){
			this.dispose();
		}
		this.stringSpacing = stringSpacing;
	}
	
	public float getNoteSize() {
		return this.noteSize;
	}
	
	public void setNoteSize(float noteSize) {
		if(!this.isDisposed() && this.noteSize != noteSize){
			this.dispose();
		}
		this.noteSize = noteSize;
	}
	
	public float getLineWidth() {
		return this.lineWidth;
	}
	
	public void setLineWidth(float lineWidth) {
		if(!this.isDisposed() && this.lineWidth != lineWidth){
			this.dispose();
		}
		this.lineWidth = lineWidth;
	}
	
	public TGFont getFont() {
		return this.font;
	}
	
	public void setFont(TGFont font) {
		if(!this.isDisposed() && !this.isSameFont(this.font, font)){
			this.dispose();
		}
		this.font = font;
	}
	
	public TGFont getFirstFretFont() {
		return this.firstFretFont;
	}
	
	public void setFirstFretFont(TGFont firstFretFont) {
		if(!this.isDisposed() && !this.isSameFont(this.firstFretFont, firstFretFont)){
			this.dispose();
		}
		this.firstFretFont = firstFretFont;
	}
	
	public void paint(TGLayout layout, TGPainter painter, float fromX, float fromY) {
		layout.setChordStyle(this);
		this.setPosY(getPaintPosition(TGTrackSpacing.POSITION_CHORD));
		this.setEditing(false);
		this.update(painter, (layout.isBufferEnabled() ? layout.getResourceBuffer() : null));
		this.paint(painter,getBeatImpl().getSpacing(layout) + fromX + Math.round(4f * layout.getScale()), fromY);
	}
	
	public void paint(TGPainter painter, float fromX, float fromY){
		float x = (fromX + getPosX());
		float y = (fromY + getPosY());
		if( (this.style & TGLayout.DISPLAY_CHORD_DIAGRAM) != 0 ){
			if(this.diagram != null){
				painter.drawImage(this.diagram,x - ( (this.diagramWidth - getFirstFretSpacing()) / 2) - getFirstFretSpacing() ,y);
			}else{
				paintDiagram(painter,x - ( (this.diagramWidth - getFirstFretSpacing()) / 2) - getFirstFretSpacing() ,y);
			}
		}
		if( (this.style & TGLayout.DISPLAY_CHORD_NAME) != 0 && getName() != null && getName().length() > 0){
			painter.setFont(getFont());
			painter.setForeground(getForegroundColor());
			painter.setBackground(getBackgroundColor());
			painter.drawString(getName(),x - (this.nameWidth / 2) , y + this.diagramHeight + painter.getFMTopLine());
		}
	}
	
	public void update(TGPainter painter, TGResourceBuffer buffer) {
		this.width = 0;
		this.height = 0;
		if( getFirstFret() <= 0 ){
			this.calculateFirstFret();
		}
		if( (this.style & TGLayout.DISPLAY_CHORD_NAME) != 0 ){
			this.updateName(painter);
			this.width = Math.max(this.width,this.nameWidth);
			this.height += this.nameHeight;
		}
		if( (this.style & TGLayout.DISPLAY_CHORD_DIAGRAM) != 0 ){
			this.updateDiagram((buffer != null ? painter : null ), buffer);
			this.width = Math.max(this.width,this.diagramWidth);
			this.height += this.diagramHeight;
		}
	}
	
	protected void updateName(TGPainter painter){
		String name = getName();
		if(painter == null || name == null || name.length() == 0){
			this.nameWidth = 0;
			this.nameHeight = 0;
			return;
		}
		this.nameWidth = painter.getFMWidth(name);
		this.nameHeight = painter.getFMHeight();
	}
	
	protected void updateDiagram(TGResourceFactory bufferFactory, TGResourceBuffer resourceBuffer){
		TGFont font = getFirstFretFont();
		this.diagramWidth = getStringSpacing() + (getStringSpacing() * countStrings()) + ((font != null)?getFirstFretSpacing():0);
		this.diagramHeight = getFretSpacing() + (getFretSpacing() * MAX_FRETS);
		if( bufferFactory != null && (this.diagram == null || this.diagram.isDisposed())){
			this.diagram = bufferFactory.createImage(this.diagramWidth, this.diagramHeight);
			TGPainter painterBuffer = this.diagram.createPainter();
			paintDiagram(painterBuffer, 0, 0);
			painterBuffer.dispose();
			
			if( this.registryKey == null ) {
				this.registerBuffer(resourceBuffer);
			}
			resourceBuffer.setResource(this.registryKey, this.diagram);
		}
	}
	
	protected void paintDiagram(TGPainter painter, float fromX, float fromY){
		TGFont font = getFirstFretFont();
		painter.setBackground(getBackgroundColor());
		painter.setLineWidth(getLineWidth());
		painter.initPath(TGPainter.PATH_FILL);
		painter.addRectangle(fromX, fromY, this.diagramWidth, this.diagramHeight);
		painter.closePath();
		painter.setForeground(getColor());
		
		//dibujo las cuerdas
		float x = fromX + getStringSpacing();
		float y = fromY + getFretSpacing();
		
		if( font != null ){
			String firstFretString = Integer.toString(getFirstFret());
			painter.setFont(font);
			painter.drawString(firstFretString, fromX + (getFirstFretSpacing() - painter.getFMWidth(firstFretString)), (y + ((getFretSpacing() / 2f) + painter.getFMMiddleLine())));
			x += getFirstFretSpacing();
		}
		
		painter.initPath();
		painter.setAntialias(false);
		for(int i = 0;i < getStrings().length;i++){
			float x1 = x + (i * getStringSpacing());
			float x2 = x + (i * getStringSpacing());
			float y1 = y;
			float y2 = y + ((getFretSpacing() * (MAX_FRETS - 1)));
			painter.moveTo(x1,y1);
			painter.lineTo(x2,y2);
		}
		painter.closePath();
		
		//dibujo las cegillas
		painter.initPath();
		painter.setAntialias(false);
		for(int i = 0;i < MAX_FRETS;i++){
			float x1 = x;
			float x2 = x + ((getStringSpacing() * (countStrings() - 1)));
			float y1 = y + (i * getFretSpacing());
			float y2 = y + (i * getFretSpacing());
			painter.moveTo(x1,y1);
			painter.lineTo(x2,y2);
		}
		painter.closePath();
		
		//dibujo las notas
		for(int i = 0;i < getStrings().length;i++){
			int fret = getFretValue(i);
			float noteX = x + ((getStringSpacing() * (countStrings() - 1)) - (getStringSpacing() * i));
			if(fret < 0){
				painter.initPath();
				painter.moveTo((noteX - (getNoteSize() / 2)), fromY);
				painter.lineTo((noteX + (getNoteSize() / 2)), fromY + getNoteSize());
				painter.moveTo((noteX + (getNoteSize() / 2)), fromY);
				painter.lineTo((noteX - (getNoteSize() / 2)), fromY + getNoteSize());
				painter.closePath();
			}
			else if(fret == 0){
				painter.initPath();
				painter.addOval(noteX - (getNoteSize() / 2),fromY,getNoteSize(),getNoteSize());
				painter.closePath();
			}
			else{
				painter.setBackground(isTonicFret(i, fret) ? getTonicColor() : getNoteColor());
				painter.initPath(TGPainter.PATH_FILL);
				fret -= (getFirstFret() - 1);
				float noteY = y + ((getFretSpacing() * fret) - (getFretSpacing() / 2 ));
				painter.addOval(noteX - (getNoteSize() / 2),noteY - (getNoteSize() / 2),(getNoteSize() + 1),(getNoteSize() + 1));
				painter.closePath();
			}
		}
	}
	
	public void calculateFirstFret(){
		int minimum = -1;
		int maximum = -1;
		boolean zero = false;
		for (int i = 0; i < getStrings().length; i++) {
			int fretValue = getFretValue(i);
			zero = (zero || fretValue == 0);
			if(fretValue > 0){
				minimum = (minimum < 0)?fretValue:Math.min(minimum,fretValue);
				maximum = (Math.max(maximum,fretValue));
			}
		}
		int firstFret = (zero && maximum < MAX_FRETS)?1:minimum;
		setFirstFret( Math.max(firstFret,1) );
	}
	
	private boolean isTonicFret(int stringIndex, int fret){
		if( this.tonic >= 0 ) {
			TGTrack track = getBeat().getMeasure().getTrack();
			if( track != null && track.stringCount() > stringIndex ) {
				TGString string = track.getString(stringIndex + 1);
				if( string != null ) {
					return (((string.getValue() + fret) % 12) == this.tonic);
				}
			}
		}
		return false;
	}

	public void registerBuffer(TGResourceBuffer resourceBuffer) {
		this.registerBuffer(resourceBuffer, this);
	}
	
	public void registerBuffer(TGResourceBuffer resourceBuffer, Object registryKey) {
		this.registryKey = registryKey;
		
		resourceBuffer.register(this.registryKey);
	}
	
	public boolean isDisposed(){
		return (this.diagram == null || this.diagram.isDisposed());
	}
	
	public void dispose(){
		if(!isDisposed()){
			this.diagram.dispose();
		}
	}
	
	public float getPosX() {
		return (isEditing()) ? this.posX : getBeatImpl().getPosX();
	}
	
	public float getPaintPosition(int index){
		return getBeatImpl().getMeasureImpl().getTs().getPosition(index);
	}
	
	public TGBeatImpl getBeatImpl(){
		return (TGBeatImpl)getBeat();
	}
	
	private boolean isSameFont(TGFont f1, TGFont f2){
		if( f1 == null && f2 == null ){
			return true;
		}
		if( f1 != null && f2 != null && !f1.isDisposed() && !f2.isDisposed()){
			boolean sameName = (f1.getName().equals(f2.getName()));
			boolean sameBold = (f1.isBold() == f2.isBold());
			boolean sameItalic = (f1.isItalic() == f2.isItalic());
			boolean sameHeight = (f1.getHeight() == f2.getHeight());
			
			return (sameName && sameBold && sameItalic && sameHeight);
		}
		return false;
	}
	
	private boolean isSameColor(TGColor c1, TGColor c2){
		if( c1 == null && c2 == null ){
			return true;
		}
		if( c1 != null && c2 != null && !c1.isDisposed() && !c2.isDisposed()){
			return ( c1.getRed() == c2.getRed() && c1.getGreen() == c2.getGreen() && c1.getBlue() == c2.getBlue() );
		}
		return false;
	}
	
	public void addFretValue(int string,int fret){
		if(!isDisposed() && this.getFretValue(string) != fret){
			this.dispose();
		}
		super.addFretValue(string, fret);
	}
	
	public void setFirstFret(int firstFret) {
		if(!isDisposed() && this.getFirstFret() != firstFret){
			this.dispose();
		}
		super.setFirstFret(firstFret);
	}
}