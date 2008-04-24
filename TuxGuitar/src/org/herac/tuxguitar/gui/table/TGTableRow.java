package org.herac.tuxguitar.gui.table;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

public class TGTableRow {
	private TGTable table;
	private Composite row;
	private CLabel number;
	private CLabel name;
	private CLabel instrument;
	private Composite painter;
	private MouseListener mouseListenerLabel;
	private MouseListener mouseListenerCanvas;
	private PaintListener paintListenerCanvas;
	
	public TGTableRow(TGTable table){
		this.table = table;
		this.init();
	}
	
	public void init(){
		MouseListener mouseListenerLabel = new MouseListenerLabel();
		MouseListener mouseListenerCanvas = new MouseListenerCanvas();
		PaintListener paintListenerCanvas = new PaintListenerCanvas();
		
		this.row = new Composite(this.table.getRowControl(),SWT.NONE );
		this.row.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false));
		
		this.number = new CLabel(this.row,SWT.LEFT);
		this.number.addMouseListener(mouseListenerLabel);
		this.table.addRowItem(this.table.getColumnNumber(),this.number,true);
		
		this.name = new CLabel(this.row,SWT.LEFT);
		this.name.addMouseListener(mouseListenerLabel);
		this.table.addRowItem(this.table.getColumnName(),this.name,true);
		
		this.instrument = new CLabel(this.row,SWT.LEFT);
		this.instrument.addMouseListener(mouseListenerLabel);
		this.table.addRowItem(this.table.getColumnInstrument(),this.instrument,true);
		
		this.painter = new Composite(this.row,SWT.DOUBLE_BUFFERED);
		this.painter.addMouseListener(mouseListenerCanvas);
		this.painter.addPaintListener(paintListenerCanvas);
		this.table.addRowItem(this.table.getColumnCanvas(),this.painter,false);
		
		this.row.pack();
	}
	
	public void setBackground(Color background){
		this.number.setBackground(background);
		this.name.setBackground(background);
		this.instrument.setBackground(background);
	}
	
	public void dispose(){
		this.row.dispose();
	}
	
	public Composite getPainter() {
		return this.painter;
	}
	
	public CLabel getInstrument() {
		return this.instrument;
	}
	
	public CLabel getName() {
		return this.name;
	}
	
	public CLabel getNumber() {
		return this.number;
	}
	
	public MouseListener getMouseListenerLabel() {
		return this.mouseListenerLabel;
	}
	
	public void setMouseListenerLabel(MouseListener mouseListenerLabel) {
		this.mouseListenerLabel = mouseListenerLabel;
	}
	
	public MouseListener getMouseListenerCanvas() {
		return this.mouseListenerCanvas;
	}
	
	public void setMouseListenerCanvas(MouseListener mouseListenerCanvas) {
		this.mouseListenerCanvas = mouseListenerCanvas;
	}
	
	public PaintListener getPaintListenerCanvas() {
		return this.paintListenerCanvas;
	}
	
	public void setPaintListenerCanvas(PaintListener paintListenerCanvas) {
		this.paintListenerCanvas = paintListenerCanvas;
	}
	
	private class MouseListenerLabel implements MouseListener{
		
		public MouseListenerLabel(){
			super();
		}
		
		public void mouseDoubleClick(MouseEvent e) {
			if(getMouseListenerLabel() != null){
				getMouseListenerLabel().mouseDoubleClick(e);
			}
		}
		
		public void mouseDown(MouseEvent e) {
			if(getMouseListenerLabel() != null){
				getMouseListenerLabel().mouseDown(e);
			}
		}
		
		public void mouseUp(MouseEvent e) {
			if(getMouseListenerLabel() != null){
				getMouseListenerLabel().mouseUp(e);
			}
		}
	}
	
	private class MouseListenerCanvas implements MouseListener{
		
		public MouseListenerCanvas(){
			super();
		}
		
		public void mouseDoubleClick(MouseEvent e) {
			if(getMouseListenerCanvas() != null){
				getMouseListenerCanvas().mouseDoubleClick(e);
			}
		}
		
		public void mouseDown(MouseEvent e) {
			if(getMouseListenerCanvas() != null){
				getMouseListenerCanvas().mouseDown(e);
			}
		}
		
		public void mouseUp(MouseEvent e) {
			if(getMouseListenerCanvas() != null){
				getMouseListenerCanvas().mouseUp(e);
			}
		}
	}
	
	private class PaintListenerCanvas implements PaintListener{
		
		public PaintListenerCanvas(){
			super();
		}
		
		public void paintControl(PaintEvent e) {
			if(getPaintListenerCanvas() != null){
				getPaintListenerCanvas().paintControl(e);
			}
		}
	}
}
