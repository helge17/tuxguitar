package org.herac.tuxguitar.ui.qt.resource;

import java.io.InputStream;

import org.herac.tuxguitar.ui.qt.QTComponent;
import org.herac.tuxguitar.ui.resource.UIImage;
import org.herac.tuxguitar.ui.resource.UIPainter;

import com.trolltech.qt.gui.QIcon;
import com.trolltech.qt.gui.QImage;
import com.trolltech.qt.gui.QImageReader;
import com.trolltech.qt.gui.QPainter;
import com.trolltech.qt.gui.QPixmap;

public class QTImage extends QTComponent<QImage> implements UIImage {
	
	public QTImage(QImage pixmap){
		super(pixmap);
	}
	
	public QTImage(float width, float height){
		this(new QImage(Math.round(width), Math.round(height), QImage.Format.Format_RGB32));
	}
	
	public QTImage(InputStream inputStream){
		this(new QImageReader(new QTInputStream(inputStream)).read());
	}
	
	public float getWidth() {
		return this.getControl().width();
	}
	
	public float getHeight() {
		return this.getControl().height();
	}
	
	public UIPainter createPainter() {
		return new QTPainter(new QPainter(this.getControl()));
	}
	
	public QIcon createIcon() {
		return new QIcon(this.createPixmap());
	}
	
	public QPixmap createPixmap() {
		return QPixmap.fromImage(this.getControl());
	}
}
