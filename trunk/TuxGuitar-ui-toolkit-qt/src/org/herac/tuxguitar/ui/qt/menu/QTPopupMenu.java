package org.herac.tuxguitar.ui.qt.menu;

import org.herac.tuxguitar.ui.event.UIMenuHideListener;
import org.herac.tuxguitar.ui.event.UIMenuShowListener;
import org.herac.tuxguitar.ui.menu.UIPopupMenu;
import org.herac.tuxguitar.ui.qt.event.QTMenuHideListenerManager;
import org.herac.tuxguitar.ui.qt.event.QTMenuShowListenerManager;
import org.herac.tuxguitar.ui.resource.UIPosition;

import com.trolltech.qt.core.QPoint;
import com.trolltech.qt.gui.QMenu;

public class QTPopupMenu extends QTMenu implements UIPopupMenu {
	
	private QTMenuShowListenerManager menuShowListener;
	private QTMenuHideListenerManager menuHideListener;
	
	public QTPopupMenu() {
		super(new QMenu());
		
		this.menuShowListener = new QTMenuShowListenerManager(this);
		this.menuHideListener = new QTMenuHideListenerManager(this);
	}
	
	public void open(UIPosition position) {
		this.getControl().exec(new QPoint(Math.round(position.getX()), Math.round(position.getY())));
	}
	
	public void addMenuShowListener(UIMenuShowListener listener) {
		if( this.menuShowListener.isEmpty() ) {
			this.getControl().aboutToShow.connect(this.menuShowListener, QTMenuShowListenerManager.SIGNAL_METHOD);
		}
		this.menuShowListener.addListener(listener);
	}
	
	public void addMenuHideListener(UIMenuHideListener listener) {
		if( this.menuHideListener.isEmpty() ) {
			this.getControl().aboutToHide.connect(this.menuHideListener, QTMenuHideListenerManager.SIGNAL_METHOD);
		}
		this.menuHideListener.addListener(listener);
	}
	
	public void removeMenuShowListener(UIMenuShowListener listener) {
		this.menuShowListener.removeListener(listener);
		if( this.menuShowListener.isEmpty() ) {
			this.getControl().aboutToShow.disconnect();
		}
	}
	
	public void removeMenuHideListener(UIMenuHideListener listener) {
		this.menuHideListener.removeListener(listener);
		if( this.menuHideListener.isEmpty() ) {
			this.getControl().aboutToHide.disconnect();
		}
	}
}
