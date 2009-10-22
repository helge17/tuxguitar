/*
 * Created on 28-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.gui.editors.effects;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.editors.TGPainter;
import org.herac.tuxguitar.gui.util.DialogUtils;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.song.models.effects.TGEffectBend;
/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class BendEditor{
	public static final int X_SPACING = 30;
	public static final int Y_SPACING = 15;
	private static final int X_LENGTH = TGEffectBend.MAX_POSITION_LENGTH + 1;
	private static final int Y_LENGTH = TGEffectBend.MAX_VALUE_LENGTH + 1;
	
	private int[] x;
	private int[] y;
	private int width;
	private int height;
	private List points;
	protected Composite editor;
	protected DefaultBend[] defaultBends;
	protected TGEffectBend result;
	
	public BendEditor() {
		this.init();
	}
	
	private void init(){
		this.x = new int[X_LENGTH];
		this.y = new int[Y_LENGTH];
		this.width = ((X_SPACING * X_LENGTH) - X_SPACING);
		this.height = ((Y_SPACING * Y_LENGTH) - Y_SPACING);
		this.points = new ArrayList();
		
		for(int i = 0;i < this.x.length;i++){
			this.x[i] = ((i + 1) * X_SPACING);
		}
		for(int i = 0;i < this.y.length;i++){
			this.y[i] = ((i + 1) * Y_SPACING);
		}
	}
	
	public TGEffectBend show(Shell shell,final TGNote note){
		final Shell dialog = DialogUtils.newDialog(shell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		
		dialog.setLayout(new GridLayout());
		dialog.setText(TuxGuitar.getProperty("bend.editor"));
		
		//----------------------------------------------------------------------
		Composite composite = new Composite(dialog,SWT.NONE);
		composite.setLayout(new GridLayout(3,false));
		composite.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		
		Composite leftComposite = new Composite(composite,SWT.NONE);
		leftComposite.setLayout(new GridLayout());
		leftComposite.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		
		Composite rightComposite = new Composite(composite,SWT.NONE);
		rightComposite.setLayout(new GridLayout());
		rightComposite.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		
		//-------------EDITOR---------------------------------------------------
		this.editor = new Composite(leftComposite, SWT.BORDER | SWT.DOUBLE_BUFFERED);
		this.editor.setBackground(this.editor.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		this.editor.setLayoutData(resizeData(new GridData(SWT.FILL,SWT.FILL,true,true) , getWidth() + (X_SPACING * 2),getHeight() + (Y_SPACING * 2))  );
		this.editor.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				TGPainter painter = new TGPainter(e.gc);
				paintEditor(painter);
			}
		});
		this.editor.addMouseListener(new MouseAdapter() {
			public void mouseUp(org.eclipse.swt.events.MouseEvent e) {
				checkPoint(e.x,e.y);
				BendEditor.this.editor.redraw();
			}
		});
		
		//-------------DEFAULT BEND LIST---------------------------------------------------
		this.resetDefaultBends();
		
		final org.eclipse.swt.widgets.List defaultBendList = new org.eclipse.swt.widgets.List(rightComposite,SWT.BORDER | SWT.SINGLE);
		for(int i = 0;i < BendEditor.this.defaultBends.length;i++){
			defaultBendList.add(this.defaultBends[i].getName());
		}
		defaultBendList.select(0);
		defaultBendList.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		defaultBendList.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				int index = defaultBendList.getSelectionIndex();
				if(index >= 0 && index < BendEditor.this.defaultBends.length){
					setBend(BendEditor.this.defaultBends[defaultBendList.getSelectionIndex()].getBend());
					BendEditor.this.editor.redraw();
				}
			}
		});
		
		//------------------BUTTONS--------------------------
		Button buttonClean = new Button(rightComposite, SWT.PUSH);
		buttonClean.setLayoutData(resizeData(new GridData(SWT.FILL,SWT.BOTTOM,true,true), 80,25));
		buttonClean.setText(TuxGuitar.getProperty("clean"));
		buttonClean.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				BendEditor.this.result = null;
				dialog.dispose();
			}
		});
		Button buttonOK = new Button(rightComposite, SWT.PUSH);
		buttonOK.setLayoutData(resizeData(new GridData(SWT.FILL,SWT.BOTTOM,true,false), 80,25));
		buttonOK.setText(TuxGuitar.getProperty("ok"));
		buttonOK.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				BendEditor.this.result = getBend();
				dialog.dispose();
			}
		});
		Button buttonCancel = new Button(rightComposite, SWT.PUSH);
		buttonCancel.setLayoutData(resizeData(new GridData(SWT.FILL,SWT.BOTTOM,true,false), 80,25));
		buttonCancel.setText(TuxGuitar.getProperty("cancel"));
		buttonCancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				BendEditor.this.result = note.getEffect().getBend();
				dialog.dispose();
			}
		});
		
		if(note.getEffect().isBend()){
			setBend(note.getEffect().getBend());
		}else{
			setBend(this.defaultBends[0].getBend());
		}
		
		dialog.setDefaultButton( buttonOK );
		
		DialogUtils.openDialog(dialog, DialogUtils.OPEN_STYLE_CENTER | DialogUtils.OPEN_STYLE_PACK | DialogUtils.OPEN_STYLE_WAIT);
		
		return this.result;
	}
	
	private GridData resizeData(GridData data,int minimumWidth,int minimumHeight){
		data.minimumWidth = minimumWidth;
		data.minimumHeight = minimumHeight;
		return data;
	}
	
	protected void paintEditor(TGPainter painter){
		for(int i = 0;i < this.x.length;i++){
			this.setStyleX(painter,i);
			painter.initPath();
			painter.setAntialias(false);
			painter.moveTo(this.x[i],Y_SPACING);
			painter.lineTo(this.x[i],Y_SPACING + this.height);
			painter.closePath();
		}
		for(int i = 0;i < this.y.length;i++){
			this.setStyleY(painter,i);
			painter.initPath();
			painter.setAntialias(false);
			painter.moveTo(X_SPACING,this.y[i]);
			painter.lineTo(X_SPACING + this.width,this.y[i]);
			painter.closePath();
		}
		
		Iterator it = null;
		Point prevPoint = null;
		painter.setLineStyle(SWT.LINE_SOLID);
		painter.setLineWidth(2);
		painter.setForeground(this.editor.getDisplay().getSystemColor(SWT.COLOR_GRAY));
		
		it = this.points.iterator();
		while(it.hasNext()){
			Point point = (Point)it.next();
			if(prevPoint != null){
				painter.initPath();
				painter.moveTo(prevPoint.x,prevPoint.y);
				painter.lineTo(point.x,point.y);
				painter.closePath();
			}
			prevPoint = point;
		}
		
		painter.setLineWidth(5);
		painter.setForeground(this.editor.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		
		it = this.points.iterator();
		while(it.hasNext()){
			Point point = (Point)it.next();
			painter.initPath();
			painter.setAntialias(false);
			painter.addRectangle(point.x - 2,point.y - 2,5,5);
			painter.closePath();
		}
		painter.setLineWidth(1);
	}
	
	private void setStyleX(TGPainter painter,int i){
		painter.setLineStyle(SWT.LINE_SOLID);
		if(i == 0 || i == (X_LENGTH - 1)){
			painter.setForeground(this.editor.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		}else{
			painter.setForeground(this.editor.getDisplay().getSystemColor(SWT.COLOR_BLUE));
			if((i % 3) > 0){
				painter.setLineStyle(SWT.LINE_DOT);
			}
		}
	}
	
	private void setStyleY(TGPainter painter,int i){
		painter.setLineStyle(SWT.LINE_SOLID);
		if(i == 0 || i == (Y_LENGTH - 1)){
			painter.setForeground(this.editor.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		}else{
			painter.setForeground(this.editor.getDisplay().getSystemColor(SWT.COLOR_RED));
			
			if((i % 2) > 0){
				painter.setLineStyle(SWT.LINE_DOT);
				painter.setForeground(this.editor.getDisplay().getSystemColor(SWT.COLOR_GRAY));
			}else if((i % 4) > 0){
				painter.setLineStyle(SWT.LINE_DOT);
			}
		}
	}
	
	protected void checkPoint(int x,int y){
		Point point = new Point(this.getX(x),this.getY(y));
		if(!this.removePoint(point)){
			this.removePointsAtXLine(point.x);
			this.addPoint(point);
			this.orderPoints();
		}
	}
	
	protected boolean removePoint(Point point){
		Iterator it = this.points.iterator();
		while(it.hasNext()){
			Point currPoint = (Point)it.next();
			if(currPoint.x == point.x && currPoint.y == point.y){
				this.points.remove(point);
				return true;
			}
		}
		return false;
	}
	
	protected void orderPoints(){
		for(int i = 0;i < this.points.size();i++){
			Point minPoint = null;
			for(int noteIdx = i;noteIdx < this.points.size();noteIdx++){
				Point point = (Point)this.points.get(noteIdx);
				if(minPoint == null || point.x < minPoint.x){
					minPoint = point;
				}
			}
			this.points.remove(minPoint);
			this.points.add(i,minPoint);
		}
	}
	
	protected void removePointsAtXLine(int x){
		Iterator it = this.points.iterator();
		while(it.hasNext()){
			Point point = (Point)it.next();
			if(point.x == x){
				this.points.remove(point);
				break;
			}
		}
	}
	
	protected void addPoint(Point point){
		this.points.add(point);
	}
	
	protected int getX(int pointX){
		int currPointX = -1;
		for(int i = 0;i < this.x.length;i++){
			if(currPointX < 0){
				currPointX = this.x[i];
			}else{
				int distanceX = Math.abs(pointX - currPointX);
				int currDistanceX = Math.abs(pointX - this.x[i]);
				if(currDistanceX < distanceX){
					currPointX = this.x[i];
				}
			}
		}
		return currPointX;
	}
	
	protected int getY(int pointY){
		int currPointY = -1;
		for(int i = 0;i < this.y.length;i++){
			if(currPointY < 0){
				currPointY = this.y[i];
			}else{
				int distanceX = Math.abs(pointY - currPointY);
				int currDistanceX = Math.abs(pointY - this.y[i]);
				if(currDistanceX < distanceX){
					currPointY = this.y[i];
				}
			}
		}
		return currPointY;
	}
	
	public boolean isEmpty(){
		return this.points.isEmpty();
	}
	
	public TGEffectBend getBend(){
		if(this.points != null && !this.points.isEmpty()){
			TGEffectBend bend = TuxGuitar.instance().getSongManager().getFactory().newEffectBend();//new BendEffect();
			Iterator it = this.points.iterator();
			while(it.hasNext()){
				Point point = (Point)it.next();
				addBendPoint(bend,point);
			}
			return bend;
		}
		return null;
	}
	
	private void addBendPoint(TGEffectBend effect,Point point){
		int position = 0;
		int value = 0;
		for(int i=0;i<this.x.length;i++){
			if(point.x == this.x[i]){
				position = i;
			}
		}
		for(int i=0;i<this.y.length;i++){
			if(point.y == this.y[i]){
				value = (this.y.length - i) -1;
			}
		}
		effect.addPoint(position,value);
	}
	
	public void setBend(TGEffectBend effect){
		this.points.clear();
		Iterator it = effect.getPoints().iterator();
		while(it.hasNext()){
			TGEffectBend.BendPoint bendPoint = (TGEffectBend.BendPoint)it.next();
			this.makePoint(bendPoint);
		}
	}
	
	private void makePoint(TGEffectBend.BendPoint bendPoint){
		int indexX = bendPoint.getPosition();
		int indexY = (this.y.length - bendPoint.getValue()) - 1;
		if(indexX >= 0 && indexX < this.x.length && indexY >= 0 && indexY < this.y.length){
			Point point = new Point(0,0);
			point.x = this.x[indexX];
			point.y = this.y[indexY];
			this.points.add(point);
		}
	}
	
	public int getWidth(){
		return this.width;
	}
	
	public int getHeight(){
		return this.height;
	}
	
	private void resetDefaultBends(){
		this.defaultBends = new DefaultBend[5];
		
		this.defaultBends[0] = new DefaultBend(TuxGuitar.getProperty("bend.bend"),TuxGuitar.instance().getSongManager().getFactory().newEffectBend());
		this.defaultBends[0].getBend().addPoint(0,0);
		this.defaultBends[0].getBend().addPoint(6,(TGEffectBend.SEMITONE_LENGTH * 4));
		this.defaultBends[0].getBend().addPoint(12,(TGEffectBend.SEMITONE_LENGTH * 4));
		
		this.defaultBends[1] = new DefaultBend(TuxGuitar.getProperty("bend.bend-release"),TuxGuitar.instance().getSongManager().getFactory().newEffectBend());
		this.defaultBends[1].getBend().addPoint(0,0);
		this.defaultBends[1].getBend().addPoint(3,(TGEffectBend.SEMITONE_LENGTH * 4));
		this.defaultBends[1].getBend().addPoint(6,(TGEffectBend.SEMITONE_LENGTH * 4));
		this.defaultBends[1].getBend().addPoint(9,0);
		this.defaultBends[1].getBend().addPoint(12,0);
		
		this.defaultBends[2] = new DefaultBend(TuxGuitar.getProperty("bend.bend-release-bend"),TuxGuitar.instance().getSongManager().getFactory().newEffectBend());
		this.defaultBends[2].getBend().addPoint(0,0);
		this.defaultBends[2].getBend().addPoint(2,(TGEffectBend.SEMITONE_LENGTH * 4));
		this.defaultBends[2].getBend().addPoint(4,(TGEffectBend.SEMITONE_LENGTH * 4));
		this.defaultBends[2].getBend().addPoint(6,0);
		this.defaultBends[2].getBend().addPoint(8,0);
		this.defaultBends[2].getBend().addPoint(10,(TGEffectBend.SEMITONE_LENGTH * 4));
		this.defaultBends[2].getBend().addPoint(12,(TGEffectBend.SEMITONE_LENGTH * 4));
		
		this.defaultBends[3] = new DefaultBend(TuxGuitar.getProperty("bend.prebend"),TuxGuitar.instance().getSongManager().getFactory().newEffectBend());
		this.defaultBends[3].getBend().addPoint(0,(TGEffectBend.SEMITONE_LENGTH * 4));
		this.defaultBends[3].getBend().addPoint(12,(TGEffectBend.SEMITONE_LENGTH * 4));
		
		this.defaultBends[4] = new DefaultBend(TuxGuitar.getProperty("bend.prebend-release"),TuxGuitar.instance().getSongManager().getFactory().newEffectBend());
		this.defaultBends[4].getBend().addPoint(0,(TGEffectBend.SEMITONE_LENGTH * 4));
		this.defaultBends[4].getBend().addPoint(4,(TGEffectBend.SEMITONE_LENGTH * 4));
		this.defaultBends[4].getBend().addPoint(8,0);
		this.defaultBends[4].getBend().addPoint(12,0);
	}
	
	private class DefaultBend{
		private String name;
		private TGEffectBend bend;
		
		public DefaultBend(String name,TGEffectBend bend){
			this.name = name;
			this.bend = bend;
		}
		
		public TGEffectBend getBend() {
			return this.bend;
		}
		
		public String getName() {
			return this.name;
		}
	}
}
