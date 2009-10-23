/*
 * Created on 01-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.gui.editors.tab;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;

import org.herac.tuxguitar.gui.editors.TGPainter;
import org.herac.tuxguitar.gui.editors.tab.layout.ViewLayout;
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
	private int diagramWidth;
	private int diagramHeight;
	private int nameWidth;
	private int nameHeight;
	private Image diagram;
	private Color foregroundColor;
	private Color backgroundColor;
	private Color noteColor;
	private Color tonicColor;
	private Color color;
	private Font font;
	private Font firstFretFont;
	private int firstFretSpacing;
	private int stringSpacing;
	private int fretSpacing;
	private int noteSize;
	
	private boolean editing;
	
	public TGChordImpl(int length) {
		super(length);
	}
	
	public void setStyle(int style) {
		this.style = style;
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
	
	public Color getForegroundColor() {
		return this.foregroundColor;
	}
	
	public void setForegroundColor(Color foregroundColor) {
		this.foregroundColor = foregroundColor;
	}
	
	public Color getBackgroundColor() {
		return this.backgroundColor;
	}
	
	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}
	
	public Color getColor() {
		return this.color;
	}
	
	public void setColor(Color color) {
		this.color = color;
	}
	
	public Color getNoteColor() {
		return this.noteColor;
	}
	
	public void setNoteColor(Color noteColor) {
		this.noteColor = noteColor;
	}
	
	public Color getTonicColor() {
		return this.tonicColor;
	}
	
	public void setTonicColor(Color tonicColor) {
		this.tonicColor = tonicColor;
	}
	
	public int getFirstFretSpacing() {
		return this.firstFretSpacing;
	}
	
	public void setFirstFretSpacing(int firstFretSpacing) {
		this.firstFretSpacing = firstFretSpacing;
	}
	
	public int getFretSpacing() {
		return this.fretSpacing;
	}
	
	public void setFretSpacing(int fretSpacing) {
		this.fretSpacing = fretSpacing;
	}
	
	public int getNoteSize() {
		return this.noteSize;
	}
	
	public void setNoteSize(int noteSize) {
		this.noteSize = noteSize;
	}
	
	public int getStringSpacing() {
		return this.stringSpacing;
	}
	
	public void setStringSpacing(int stringSpacing) {
		this.stringSpacing = stringSpacing;
	}
	
	public Font getFont() {
		return this.font;
	}
	
	public void setFont(Font font) {
		this.font = font;
	}
	
	public Font getFirstFretFont() {
		return this.firstFretFont;
	}
	
	public void setFirstFretFont(Font firstFretFont) {
		this.firstFretFont = firstFretFont;
	}
	
	public boolean isEditing() {
		return this.editing;
	}
	
	public void setEditing(boolean editing) {
		this.editing = editing;
	}
	
	public void paint(ViewLayout layout, TGPainter painter, int fromX, int fromY) {
		layout.setChordStyle(this);
		this.setPosY(getPaintPosition(TGTrackSpacing.POSITION_CHORD));
		this.setEditing(false);
		this.update(painter, layout.isBufferEnabled());
		this.paint(painter,getBeatImpl().getSpacing() + fromX + Math.round(4f * layout.getScale()), fromY);
	}
	
	public void paint(TGPainter painter, int fromX, int fromY){
		int x = (fromX + getPosX());
		int y = (fromY + getPosY());
		if( (this.style & ViewLayout.DISPLAY_CHORD_DIAGRAM) != 0 ){
			if(this.diagram != null){
				painter.drawImage(this.diagram,x - ( (this.diagramWidth - getFirstFretSpacing()) / 2) - getFirstFretSpacing() ,y);
			}else{
				paintDiagram(painter,x - ( (this.diagramWidth - getFirstFretSpacing()) / 2) - getFirstFretSpacing() ,y);
			}
		}
		if( (this.style & ViewLayout.DISPLAY_CHORD_NAME) != 0 && getName() != null && getName().length() > 0){
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
		if( (this.style & ViewLayout.DISPLAY_CHORD_NAME) != 0 ){
			this.updateName(painter);
			this.width = Math.max(this.width,this.nameWidth);
			this.height += this.nameHeight;
		}
		if( (this.style & ViewLayout.DISPLAY_CHORD_DIAGRAM) != 0 ){
			this.updateDiagram(makeBuffer);
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
		Point point = painter.getStringExtent(name);
		this.nameWidth = point.x;
		this.nameHeight = point.y;
	}
	
	protected void updateDiagram(boolean makeBuffer){
		Font font = getFirstFretFont();
		this.diagramWidth = getStringSpacing() + (getStringSpacing() * countStrings()) + ((font != null)?getFirstFretSpacing():0);
		this.diagramHeight = getFretSpacing() + (getFretSpacing() * MAX_FRETS);
		if(this.diagram == null && makeBuffer){
			this.diagram = new BufferedImage(this.diagramWidth,this.diagramHeight,BufferedImage.TYPE_INT_RGB);
			TGPainter painter = new TGPainter(this.diagram);
			paintDiagram(painter, 0, 0);
			painter.dispose();
		}
	}
	
	protected void paintDiagram(TGPainter painter, int fromX, int fromY){
		Font font = getFirstFretFont();
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
			Point size = painter.getStringExtent(firstFretString);
			painter.drawString(firstFretString,fromX + (getFirstFretSpacing() - size.x),Math.round(y + ((getFretSpacing() / 2f) - (size.y / 2f))));
			x += getFirstFretSpacing();
		}
		
		painter.initPath();
		painter.setAntialias(false);
		for(int i = 0;i < getStrings().length;i++){
			int x1 = x + (i * getStringSpacing());
			int x2 = x + (i * getStringSpacing());
			int y1 = y;
			int y2 = y + ((getFretSpacing() * (MAX_FRETS - 1)));
			//painter.drawLine(x1,y1,x2,y2);
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
			//painter.drawLine(x1,y1,x2,y2);
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
				painter.setBackground( getNoteColor() );
				painter.initPath(TGPainter.PATH_FILL);
				fret -= (getFirstFret() - 1);
				int noteY = y + ((getFretSpacing() * fret) - (getFretSpacing() / 2 ));
				painter.addOval(noteX - (getNoteSize() / 2),noteY - (getNoteSize() / 2),(getNoteSize() + 1),(getNoteSize() + 1));
				painter.closePath();
			}
		}
	}
	
	public void calculateFirstFret(){
		int minimun = -1;
		int maximun = -1;
		boolean zero = false;
		for (int i = 0; i < getStrings().length; i++) {
			int fretValue = getFretValue(i);
			zero = (zero || fretValue == 0);
			if(fretValue > 0){
				minimun = (minimun < 0)?fretValue:Math.min(minimun,fretValue);
				maximun = (Math.max(maximun,fretValue));
			}
		}
		int firstFret = (zero && maximun < MAX_FRETS)?1:minimun;
		setFirstFret( Math.max(firstFret,1) );
	}
	
	protected int getStringValue(int number){
		TGString string = getBeat().getMeasure().getTrack().getString(number);
		return string.getValue();
	}
	
	public boolean isDisposed(){
		return (this.diagram == null);
	}
	
	public void dispose(){
		this.diagram = null;
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
}