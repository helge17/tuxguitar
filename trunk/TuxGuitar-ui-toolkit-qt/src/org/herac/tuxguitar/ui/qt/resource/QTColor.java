package org.herac.tuxguitar.ui.qt.resource;

import org.herac.tuxguitar.ui.qt.QTComponent;
import org.herac.tuxguitar.ui.resource.UIColor;
import org.herac.tuxguitar.ui.resource.UIColorModel;

import com.trolltech.qt.gui.QColor;

public class QTColor extends QTComponent<QColor> implements UIColor{
	
	public QTColor(QColor handle){
		super(handle);
	}
	
	public QTColor(int red, int green, int blue ){
		this(new QColor(red, green, blue));
	}
	
	public QTColor(UIColorModel model){
		this(Math.round(model.getRed()), Math.round(model.getGreen()), Math.round(model.getBlue()));
	}
	
	public int getRed() {
		return this.getControl().red();
	}
	
	public int getGreen() {
		return this.getControl().green();
	}
	
	public int getBlue() {
		return this.getControl().blue();
	}
}
