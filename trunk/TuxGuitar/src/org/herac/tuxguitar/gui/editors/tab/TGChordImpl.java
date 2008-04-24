/*
 * Created on 01-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.gui.editors.tab;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.herac.tuxguitar.gui.editors.TGPainter;
import org.herac.tuxguitar.gui.editors.tab.layout.TrackSpacing;
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
	private int tonic;
	private int diagramWidth;
	private int diagramHeight;
	private int nameWidth;
	private int nameHeight;
	private Image diagram;
	private Color backgroundColor;
	private Color noteColor;
	private Color tonicColor;
	private Color color;
	private Font font;
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
	
	public void setTonic(int tonic){
		this.tonic = tonic;
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
	
	public Font getFirstFretFont() {
		return this.font;
	}
	
	public void setFirstFretFont(Font font) {
		this.font = font;
	}
	
	public boolean isEditing() {
		return this.editing;
	}
	
	public void setEditing(boolean editing) {
		this.editing = editing;
	}
	
	public void paint(ViewLayout layout, TGPainter painter, int fromX, int fromY) {
		layout.setChordStyle(painter,this);
		this.posY = (fromY + getPaintPosition(TrackSpacing.POSITION_CHORD));
		this.setEditing(false);
		this.update(painter.getGC().getDevice());
		this.paint(painter,getBeatImpl().getSpacing() + fromX, fromY);
	}
	
	public void paint(TGPainter painter, int fromX, int fromY){
		int x = (fromX + getPosX()) + 4;
		int y = (fromY + getPosY());
		if( (this.style & ViewLayout.DISPLAY_CHORD_DIAGRAM) != 0 ){
			//painter.drawImage(this.diagram,x - (this.diagramWidth / 2) ,y);
			painter.drawImage(this.diagram,x - ( (this.diagramWidth - getFirstFretSpacing()) / 2) - getFirstFretSpacing() ,y);
		}
		if( (this.style & ViewLayout.DISPLAY_CHORD_NAME) != 0 && getName() != null && getName().length() > 0){
			painter.drawString(getName(),x - (this.nameWidth / 2) , y + (this.height - this.nameHeight ) );
			//painter.drawString(getName(),x - (this.nameWidth / 2) + 4, y + (this.height - this.nameHeight ) );
		}
	}
	
	public void update(Device device) {
		this.width = 0;
		this.height = 0;
		if(getFirstFret() <= 0 ){
			this.calculateFirstFret();
		}
		if( (this.style & ViewLayout.DISPLAY_CHORD_NAME) != 0 ){
			this.updateName(device);
			this.width = Math.max(this.width,this.nameWidth);
			this.height += this.nameHeight;
		}
		if( (this.style & ViewLayout.DISPLAY_CHORD_DIAGRAM) != 0 ){
			this.updateDiagram(device);
			this.width = Math.max(this.width,this.diagramWidth);
			this.height += this.diagramHeight;
		}
	}
	
	protected void updateName(Device device){
		String name = getName();
		if(name == null || name.length() == 0){
			this.nameWidth = 0;
			this.nameHeight = 0;
			return;
		}
		TGPainter painter = new TGPainter(new GC(device));
		Point point = painter.getStringExtent(name);
		this.nameWidth = point.x;
		this.nameHeight = point.y;
		painter.dispose();
	}
	
	protected void updateDiagram(Device device){
		if(this.diagram == null || this.diagram.isDisposed()){
			Font font = getFirstFretFont();
			this.diagramWidth = getStringSpacing() + (getStringSpacing() * countStrings()) + ((font != null)?getFirstFretSpacing():0);
			this.diagramHeight = getFretSpacing() + (getFretSpacing() * MAX_FRETS);
			
			this.diagram = new Image(device,this.diagramWidth,this.diagramHeight);
			TGPainter painter = new TGPainter(new GC(this.diagram));
			painter.setBackground(getBackgroundColor());
			painter.initPath(TGPainter.PATH_FILL);
			painter.addRectangle(0, 0, this.diagramWidth, this.diagramHeight);
			painter.closePath();
			painter.setForeground(getColor());
			
			//dibujo las cuerdas
			int x = getStringSpacing();
			int y = getFretSpacing();
			
			if(font != null){
				String firstFretString = Integer.toString(getFirstFret());
				painter.setFont(font);
				painter.drawString(firstFretString,(getFirstFretSpacing() - painter.getStringExtent(firstFretString).x),y);
				x += getFirstFretSpacing();
			}
			
			painter.initPath();
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
					painter.moveTo((noteX - (getNoteSize() / 2)), 0);
					painter.lineTo((noteX + (getNoteSize() / 2)), getNoteSize());
					painter.moveTo((noteX + (getNoteSize() / 2)), 0);
					painter.lineTo((noteX - (getNoteSize() / 2)), getNoteSize());
					painter.closePath();
				}
				else if(fret == 0){
					painter.initPath();
					painter.addOval(noteX - (getNoteSize() / 2),0,getNoteSize(),getNoteSize());
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
			painter.dispose();
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
}