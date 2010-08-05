/*
 * Created on 01-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.graphics.control;

import org.herac.tuxguitar.graphics.TGColor;
import org.herac.tuxguitar.graphics.TGDimension;
import org.herac.tuxguitar.graphics.TGFont;
import org.herac.tuxguitar.graphics.TGImage;
import org.herac.tuxguitar.graphics.TGPainter;
import org.herac.tuxguitar.graphics.TGResourceFactory;
import org.herac.tuxguitar.song.models.TGChord;
import org.herac.tuxguitar.song.models.TGString;
/**
 * @author julian
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class TGChordImpl extends TGChord {
	
	public static final int MAX_FRETS = 6;
	
	private int style;
	private int posX;
	private int posY;
	private int width;
	private int height;
	private int tonic;
	private int diagramWidth;
	private int diagramHeight;
	private int nameWidth;
	private int nameHeight;
	private TGImage diagram;
	private TGColor foregroundColor;
	private TGColor backgroundColor;
	private TGColor noteColor;
	private TGColor tonicColor;
	private TGColor color;
	private TGFont font;
	private TGFont firstFretFont;
	private int firstFretSpacing;
	private int stringSpacing;
	private int fretSpacing;
	private int noteSize;
	
	private boolean editing;
	
	public TGChordImpl(int length) {
		super(length);
	}
	
	public boolean isEditing() {
		return this.editing;
	}
	
	public void setEditing(boolean editing) {
		this.editing = editing;
	}
	
	public void setPosX(int posX){
		this.posX = posX;
	}
	
	public void setPosY(int posY){
		this.posY = posY;
	}
	
	public int getPosY() {
		return this.posY;
	}
	
	public int getWidth(){
		return this.width;
	}
	
	public int getHeight(){
		return this.height;
	}
	
	public void setStyle(int style) {
		this.style = style;
	}
	
	public void setTonic(int tonic){
		if(!isDisposed() && this.tonic != tonic){
			this.dispose();
		}
		this.tonic = tonic;
	}
	
	public TGColor getForegroundColor() {
		return this.foregroundColor;
	}
	
	public void setForegroundColor(TGColor foregroundColor) {
		if(!isDisposed() && !isSameColor(this.foregroundColor, foregroundColor)){
			this.dispose();
		}
		this.foregroundColor = foregroundColor;
	}
	
	public TGColor getBackgroundColor() {
		return this.backgroundColor;
	}
	
	public void setBackgroundColor(TGColor backgroundColor) {
		if(!isDisposed() && !isSameColor(this.backgroundColor, backgroundColor)){
			this.dispose();
		}
		this.backgroundColor = backgroundColor;
	}
	
	public TGColor getColor() {
		return this.color;
	}
	
	public void setColor(TGColor color) {
		if(!isDisposed() && !isSameColor(this.color, color)){
			this.dispose();
		}
		this.color = color;
	}
	
	public TGColor getNoteColor() {
		return this.noteColor;
	}
	
	public void setNoteColor(TGColor noteColor) {
		if(!isDisposed() && !isSameColor(this.noteColor, noteColor)){
			this.dispose();
		}
		this.noteColor = noteColor;
	}
	
	public TGColor getTonicColor() {
		return this.tonicColor;
	}
	
	public void setTonicColor(TGColor tonicColor) {
		if(!isDisposed() && !isSameColor(this.tonicColor, tonicColor)){
			this.dispose();
		}
		this.tonicColor = tonicColor;
	}
	
	public int getFirstFretSpacing() {
		return this.firstFretSpacing;
	}
	
	public void setFirstFretSpacing(int firstFretSpacing) {
		if(!isDisposed() && this.firstFretSpacing != firstFretSpacing){
			this.dispose();
		}
		this.firstFretSpacing = firstFretSpacing;
	}
	
	public int getFretSpacing() {
		return this.fretSpacing;
	}
	
	public void setFretSpacing(int fretSpacing) {
		if(!isDisposed() && this.fretSpacing != fretSpacing){
			this.dispose();
		}
		this.fretSpacing = fretSpacing;
	}
	
	public int getStringSpacing() {
		return this.stringSpacing;
	}
	
	public void setStringSpacing(int stringSpacing) {
		if(!isDisposed() && this.stringSpacing != stringSpacing){
			this.dispose();
		}
		this.stringSpacing = stringSpacing;
	}
	
	public int getNoteSize() {
		return this.noteSize;
	}
	
	public void setNoteSize(int noteSize) {
		if(!isDisposed() && this.noteSize != noteSize){
			this.dispose();
		}
		this.noteSize = noteSize;
	}
	
	public TGFont getFont() {
		return this.font;
	}
	
	public void setFont(TGFont font) {
		if(!isDisposed() && !isSameFont(this.font, font)){
			this.dispose();
		}
		this.font = font;
	}
	
	public TGFont getFirstFretFont() {
		return this.firstFretFont;
	}
	
	public void setFirstFretFont(TGFont firstFretFont) {
		if(!isDisposed() && !isSameFont(this.firstFretFont, firstFretFont)){
			this.dispose();
		}
		this.firstFretFont = firstFretFont;
	}
	
	public void paint(TGLayout layout, TGPainter painter, int fromX, int fromY) {
		layout.setChordStyle(this);
		this.setPosY(getPaintPosition(TGTrackSpacing.POSITION_CHORD));
		this.setEditing(false);
		this.update(painter, layout.isBufferEnabled());
		this.paint(painter,getBeatImpl().getSpacing() + fromX + Math.round(4f * layout.getScale()), fromY);
	}
	
	public void paint(TGPainter painter, int fromX, int fromY){
		int x = (fromX + getPosX());
		int y = (fromY + getPosY());
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
			painter.drawString(getName(),x - (this.nameWidth / 2) , y + (this.height - this.nameHeight ) );
		}
	}
	
	public void update(TGPainter painter, boolean makeBuffer) {
		this.width = 0;
		this.height = 0;
		if(getFirstFret() <= 0 ){
			this.calculateFirstFret();
		}
		if( (this.style & TGLayout.DISPLAY_CHORD_NAME) != 0 ){
			this.updateName(painter);
			this.width = Math.max(this.width,this.nameWidth);
			this.height += this.nameHeight;
		}
		if( (this.style & TGLayout.DISPLAY_CHORD_DIAGRAM) != 0 ){
			this.updateDiagram( (makeBuffer ? painter : null ) );
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
		TGDimension size = painter.getStringExtent(name);
		this.nameWidth = size.getWidth();
		this.nameHeight = size.getHeight();
	}
	
	protected void updateDiagram(TGResourceFactory bufferFactory){
		TGFont font = getFirstFretFont();
		this.diagramWidth = getStringSpacing() + (getStringSpacing() * countStrings()) + ((font != null)?getFirstFretSpacing():0);
		this.diagramHeight = getFretSpacing() + (getFretSpacing() * MAX_FRETS);
		if(bufferFactory != null && (this.diagram == null || this.diagram.isDisposed())){
			this.diagram = bufferFactory.createImage(this.diagramWidth,this.diagramHeight);
			TGPainter painterBuffer = this.diagram.createPainter();
			paintDiagram(painterBuffer, 0, 0);
			painterBuffer.dispose();
		}
	}
	
	protected void paintDiagram(TGPainter painter, int fromX, int fromY){
		TGFont font = getFirstFretFont();
		painter.setBackground(getBackgroundColor());
		painter.initPath(TGPainter.PATH_FILL);
		painter.addRectangle(fromX, fromY, this.diagramWidth, this.diagramHeight);
		painter.closePath();
		painter.setForeground(getColor());
		
		//dibujo las cuerdas
		int x = fromX + getStringSpacing();
		int y = fromY + getFretSpacing();
		
		if(font != null){
			String firstFretString = Integer.toString(getFirstFret());
			painter.setFont(font);
			TGDimension size = painter.getStringExtent(firstFretString);
			painter.drawString(firstFretString,fromX + (getFirstFretSpacing() - size.getWidth()),Math.round(y + ((getFretSpacing() / 2f) - (size.getHeight() / 2f))));
			x += getFirstFretSpacing();
		}
		
		painter.initPath();
		painter.setAntialias(false);
		for(int i = 0;i < getStrings().length;i++){
			int x1 = x + (i * getStringSpacing());
			int x2 = x + (i * getStringSpacing());
			int y1 = y;
			int y2 = y + ((getFretSpacing() * (MAX_FRETS - 1)));
			painter.moveTo(x1,y1);
			painter.lineTo(x2,y2);
		}
		painter.closePath();
		
		//dibujo las cegillas
		painter.initPath();
		painter.setAntialias(false);
		for(int i = 0;i < MAX_FRETS;i++){
			int x1 = x;
			int x2 = x + ((getStringSpacing() * (countStrings() - 1)));
			int y1 = y + (i * getFretSpacing());
			int y2 = y + (i * getFretSpacing());
			painter.moveTo(x1,y1);
			painter.lineTo(x2,y2);
		}
		painter.closePath();
		
		painter.setLineWidth(1);
		//dibujo las notas
		for(int i = 0;i < getStrings().length;i++){
			int fret = getFretValue(i);
			int noteX = x + ((getStringSpacing() * (countStrings() - 1)) - (getStringSpacing() * i));
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
				painter.setBackground( (this.tonic >= 0 && ( (getStringValue(i + 1) + fret) % 12) == this.tonic)?getTonicColor():getNoteColor());
				painter.initPath(TGPainter.PATH_FILL);
				fret -= (getFirstFret() - 1);
				int noteY = y + ((getFretSpacing() * fret) - (getFretSpacing() / 2 ));
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
	
	private int getStringValue(int number){
		TGString string = getBeat().getMeasure().getTrack().getString(number);
		return string.getValue();
	}
	
	public boolean isDisposed(){
		return (this.diagram == null || this.diagram.isDisposed());
	}
	
	public void dispose(){
		if(!isDisposed()){
			this.diagram.dispose();
		}
	}
	
	public int getPosX() {
		return (isEditing())?this.posX:getBeatImpl().getPosX();
	}
	
	public int getPaintPosition(int index){
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