package org.herac.tuxguitar.app.view.toolbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolItem;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.TGActionProcessorListener;
import org.herac.tuxguitar.app.system.icons.TGIconManager;
import org.herac.tuxguitar.app.view.component.tab.Caret;
import org.herac.tuxguitar.app.view.component.tab.Tablature;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.note.TGChangeVelocityAction;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.song.models.TGVelocities;

public class TGToolBarSectionDynamic implements TGToolBarSection {
	
	private ToolItem menuItem;
	
	private Menu menu;
	private List<MenuItem> menuItems;
	
	private Map<Integer, String> dynamicNameKeys;
	
	private Integer dynamicValue;
	
	public TGToolBarSectionDynamic() {
		this.dynamicNameKeys = new HashMap<Integer, String>();
		this.dynamicNameKeys.put(TGVelocities.PIANO_PIANISSIMO, "dynamic.piano-pianissimo");
		this.dynamicNameKeys.put(TGVelocities.PIANISSIMO, "dynamic.pianissimo");
		this.dynamicNameKeys.put(TGVelocities.PIANO, "dynamic.piano");
		this.dynamicNameKeys.put(TGVelocities.MEZZO_PIANO, "dynamic.mezzo-piano");
		this.dynamicNameKeys.put(TGVelocities.MEZZO_FORTE, "dynamic.mezzo-forte");
		this.dynamicNameKeys.put(TGVelocities.FORTE, "dynamic.forte");
		this.dynamicNameKeys.put(TGVelocities.FORTISSIMO, "dynamic.fortissimo");
		this.dynamicNameKeys.put(TGVelocities.FORTE_FORTISSIMO, "dynamic.forte-fortissimo");
	}
	
	public void createSection(final TGToolBar toolBar) {
		this.menuItem = new ToolItem(toolBar.getControl(), SWT.PUSH);
		this.menuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				displayMenu();
			}
		});
		
		this.menu = new Menu(this.menuItem.getParent().getShell());
		
		this.menuItems = new ArrayList<MenuItem>();
		this.menuItems.add(this.createMenuItem(toolBar, TGVelocities.PIANO_PIANISSIMO));
		this.menuItems.add(this.createMenuItem(toolBar, TGVelocities.PIANISSIMO));
		this.menuItems.add(this.createMenuItem(toolBar, TGVelocities.PIANO));
		this.menuItems.add(this.createMenuItem(toolBar, TGVelocities.MEZZO_PIANO));
		this.menuItems.add(this.createMenuItem(toolBar, TGVelocities.MEZZO_FORTE));
		this.menuItems.add(this.createMenuItem(toolBar, TGVelocities.FORTE));
		this.menuItems.add(this.createMenuItem(toolBar, TGVelocities.FORTISSIMO));
		this.menuItems.add(this.createMenuItem(toolBar, TGVelocities.FORTE_FORTISSIMO));
		
		this.loadIcons(toolBar);
		this.loadProperties(toolBar);
	}
	
	public void loadProperties(TGToolBar toolBar){
		Caret caret = TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret();
		int selection = ((caret.getSelectedNote() != null) ? caret.getSelectedNote().getVelocity() : caret.getVelocity());
		
		this.menuItem.setToolTipText(toolBar.getText("dynamic"));
		this.loadMenuProperties(toolBar, selection);
	}
	
	public void loadIcons(TGToolBar toolBar){
		this.loadDynamicIcon(toolBar, true);
		this.loadMenuIcons(toolBar);
	}
	
	public void loadDynamicIcon(TGToolBar toolBar, boolean force){
		int dynamicValue = TGVelocities.DEFAULT;
		
		Tablature tablature = toolBar.getTablature();
		if( tablature != null ) {
			Caret caret = tablature.getCaret();
			TGNote selectedNote = caret.getSelectedNote();
			dynamicValue = ((selectedNote != null) ? selectedNote.getVelocity() : caret.getVelocity());
		}
		
		if( force || (this.dynamicValue == null || !this.dynamicValue.equals(dynamicValue))) {
			Image icon = this.getDynamicIcon(toolBar, dynamicValue);
			if( icon != null ) { 
				this.menuItem.setImage(icon);
				this.dynamicValue = dynamicValue;
			}
		}
	}
	
	public void updateItems(TGToolBar toolBar){
		Caret caret = TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret();
		int selection = ((caret.getSelectedNote() != null) ? caret.getSelectedNote().getVelocity() : caret.getVelocity());
		boolean running = TuxGuitar.getInstance().getPlayer().isRunning();
		
		this.loadDynamicIcon(toolBar, false);
		this.updateMenuItems(toolBar, selection, running);
	}
	
	private MenuItem createMenuItem(TGToolBar toolBar, int velocity) {
		MenuItem menuItem = new MenuItem(this.menu, SWT.PUSH);
		menuItem.setData(velocity);
		menuItem.addSelectionListener(this.createChangeVelocityAction(toolBar, velocity));
		return menuItem;
	}
	
	private void updateMenuItems(TGToolBar toolBar, int selection, boolean running) {
		for(MenuItem menuItem : this.menuItems) {
			menuItem.setEnabled(!running);
			
			Integer velocity = (Integer) menuItem.getData();
			String nameKey = getNameKey(velocity);
			if( nameKey != null ) {
				menuItem.setText(toolBar.getText(nameKey, (velocity == selection)));
			}
		}
	}
	
	private void loadMenuProperties(TGToolBar toolBar, int selection) {
		for(MenuItem menuItem : this.menuItems) {
			Integer velocity = (Integer) menuItem.getData();
			String nameKey = getNameKey(velocity);
			if( nameKey != null ) {
				menuItem.setText(toolBar.getText(nameKey, (velocity == selection)));
			}
		}
	}
	
	private void loadMenuIcons(TGToolBar toolBar) {
		for(MenuItem menuItem : this.menuItems) {
			Integer velocity = (Integer) menuItem.getData();
			Image icon = this.getDynamicIcon(toolBar, velocity);
			if( icon != null ) {
				menuItem.setImage(icon);
			}
		}
	}
	
	public void displayMenu() {
		Rectangle rect = this.menuItem.getBounds();
		Point pt = this.menuItem.getParent().toDisplay(new Point(rect.x, rect.y));
		
		this.menu.setLocation(pt.x, pt.y + rect.height);
		this.menu.setVisible(true);
	}
	
	private String getNameKey(int velocity) {
		if( this.dynamicNameKeys.containsKey(velocity) ) {
			return this.dynamicNameKeys.get(velocity);
		}
		
		return null;
	}
	
	private Image getDynamicIcon(TGToolBar toolBar, int velocity) {
		TGIconManager iconManager = toolBar.getIconManager();
		if( velocity == TGVelocities.PIANO_PIANISSIMO ) {
			return iconManager.getDynamicPPP();
		}
		if( velocity == TGVelocities.PIANISSIMO ) {
			return iconManager.getDynamicPP();
		}
		if( velocity == TGVelocities.PIANO) {
			return iconManager.getDynamicP();
		}
		if( velocity == TGVelocities.MEZZO_PIANO ) {
			return iconManager.getDynamicMP();
		}
		if( velocity == TGVelocities.MEZZO_FORTE ) {
			return iconManager.getDynamicMF();
		}
		if( velocity == TGVelocities.FORTE) {
			return iconManager.getDynamicF();
		}
		if( velocity == TGVelocities.FORTISSIMO) {
			return iconManager.getDynamicFF();
		}
		if( velocity == TGVelocities.FORTE_FORTISSIMO) {
			return iconManager.getDynamicFFF();
		}
		return null;
	}
	
	public TGActionProcessorListener createChangeVelocityAction(TGToolBar toolBar, Integer velocity) {
		TGActionProcessorListener tgActionProcessor = toolBar.createActionProcessor(TGChangeVelocityAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_VELOCITY, velocity);
		return tgActionProcessor;
	}
}
