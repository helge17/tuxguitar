package org.herac.tuxguitar.app.view.toolbar.edit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.TGActionProcessorListener;
import org.herac.tuxguitar.app.system.icons.TGIconManager;
import org.herac.tuxguitar.app.view.component.tab.Caret;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.note.TGChangeVelocityAction;
import org.herac.tuxguitar.song.models.TGVelocities;
import org.herac.tuxguitar.ui.resource.UIImage;
import org.herac.tuxguitar.ui.toolbar.UIToolBar;
import org.herac.tuxguitar.ui.toolbar.UIToolCheckableItem;

public class TGEditToolBarSectionDynamic extends TGEditToolBarSection {
	
	private static final String SECTION_TITLE = "dynamic";
	
	private static final String VELOCITY_VALUE = "velocity";
	
	private List<UIToolCheckableItem> menuItems;
	
	private Map<Integer, String> dynamicNameKeys;
	
	public TGEditToolBarSectionDynamic(TGEditToolBar toolBar) {
		super(toolBar, SECTION_TITLE);
		
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
	
	public void createSectionToolBars() {
		UIToolBar toolBar = this.createToolBar();
		
		this.menuItems = new ArrayList<UIToolCheckableItem>();
		this.menuItems.add(this.createToolItem(toolBar, TGVelocities.PIANO_PIANISSIMO));
		this.menuItems.add(this.createToolItem(toolBar, TGVelocities.PIANISSIMO));
		this.menuItems.add(this.createToolItem(toolBar, TGVelocities.PIANO));
		this.menuItems.add(this.createToolItem(toolBar, TGVelocities.MEZZO_PIANO));
		
		toolBar = this.createToolBar();
		
		this.menuItems.add(this.createToolItem(toolBar, TGVelocities.MEZZO_FORTE));
		this.menuItems.add(this.createToolItem(toolBar, TGVelocities.FORTE));
		this.menuItems.add(this.createToolItem(toolBar, TGVelocities.FORTISSIMO));
		this.menuItems.add(this.createToolItem(toolBar, TGVelocities.FORTE_FORTISSIMO));
		
		this.loadIcons();
		this.loadProperties();
	}
	
	public void loadProperties(){
		for(UIToolCheckableItem menuItem : this.menuItems) {
			Integer velocity = menuItem.getData(VELOCITY_VALUE);
			String nameKey = getNameKey(velocity);
			if( nameKey != null ) {
				menuItem.setToolTipText(this.getText(nameKey));
			}
		}
	}
	
	public void loadIcons(){
		for(UIToolCheckableItem menuItem : this.menuItems) {
			Integer velocity = menuItem.getData(VELOCITY_VALUE);
			UIImage icon = this.getDynamicIcon(velocity);
			if( icon != null ) {
				menuItem.setImage(icon);
			}
		}
	}
	
	public void updateItems(){
		Caret caret = TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret();
		int selection = ((caret.getSelectedNote() != null) ? caret.getSelectedNote().getVelocity() : caret.getVelocity());
		boolean running = TuxGuitar.getInstance().getPlayer().isRunning();
		
		for(UIToolCheckableItem menuItem : this.menuItems) {
			menuItem.setEnabled(!running);
			
			Integer velocity = menuItem.getData(VELOCITY_VALUE);
			String nameKey = getNameKey(velocity);
			if( nameKey != null ) {
				menuItem.setChecked(velocity == selection);
			}
		}
	}
	
	private UIToolCheckableItem createToolItem(UIToolBar toolBar, int velocity) {
		UIToolCheckableItem menuItem = toolBar.createCheckItem();
		menuItem.setData(VELOCITY_VALUE, velocity);
		menuItem.addSelectionListener(this.createChangeVelocityAction(velocity));
		return menuItem;
	}
	
	private String getNameKey(int velocity) {
		if( this.dynamicNameKeys.containsKey(velocity) ) {
			return this.dynamicNameKeys.get(velocity);
		}
		
		return null;
	}
	
	private UIImage getDynamicIcon(int velocity) {
		TGIconManager iconManager = this.getIconManager();
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
	
	public TGActionProcessorListener createChangeVelocityAction(Integer velocity) {
		TGActionProcessorListener tgActionProcessor = this.createActionProcessor(TGChangeVelocityAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_VELOCITY, velocity);
		return tgActionProcessor;
	}
}
