package org.herac.tuxguitar.app.view.toolbar.main;

import org.herac.tuxguitar.app.action.impl.layout.TGSetLayoutScaleDecrementAction;
import org.herac.tuxguitar.app.action.impl.layout.TGSetLayoutScaleIncrementAction;
import org.herac.tuxguitar.app.action.impl.layout.TGSetLayoutScaleResetAction;
import org.herac.tuxguitar.app.action.impl.view.TGToggleChannelsDialogAction;
import org.herac.tuxguitar.app.action.impl.view.TGToggleEditToolbarAction;
import org.herac.tuxguitar.app.action.impl.view.TGToggleFretBoardEditorAction;
import org.herac.tuxguitar.app.action.impl.view.TGToggleTableViewerAction;
import org.herac.tuxguitar.app.view.component.table.TGTableViewer;
import org.herac.tuxguitar.app.view.dialog.channel.TGChannelManagerDialog;
import org.herac.tuxguitar.app.view.dialog.fretboard.TGFretBoardEditor;
import org.herac.tuxguitar.app.view.toolbar.edit.TGEditToolBar;
import org.herac.tuxguitar.ui.toolbar.UIToolActionItem;
import org.herac.tuxguitar.ui.toolbar.UIToolBar;
import org.herac.tuxguitar.ui.toolbar.UIToolCheckableItem;
import org.herac.tuxguitar.util.TGContext;

public class TGMainToolBarSectionView extends TGMainToolBarSection {
	
	private UIToolCheckableItem showEditToolBar;
	private UIToolCheckableItem showTrackTable;
	private UIToolCheckableItem showFretBoard;
	private UIToolCheckableItem showInstruments;
	
	private UIToolActionItem zoomOut;
	private UIToolActionItem zoomReset;
	private UIToolActionItem zoomIn;

	public TGMainToolBarSectionView(TGContext context, UIToolBar toolBar) {
		super(context, toolBar);
	}
	
	public void createSection() {
		this.showEditToolBar = this.getToolBar().createCheckItem();
		this.showEditToolBar.addSelectionListener(this.createActionProcessor(TGToggleEditToolbarAction.NAME));
		
		this.showTrackTable = this.getToolBar().createCheckItem();
		this.showTrackTable.addSelectionListener(this.createActionProcessor(TGToggleTableViewerAction.NAME));

		//--INSTRUMENTS--
		this.showInstruments = this.getToolBar().createCheckItem();
		this.showInstruments.addSelectionListener(this.createActionProcessor(TGToggleChannelsDialogAction.NAME));
		
		//--FRETBOARD--
		this.showFretBoard = this.getToolBar().createCheckItem();
		this.showFretBoard.addSelectionListener(this.createActionProcessor(TGToggleFretBoardEditorAction.NAME));

		//--ZOOM--
		this.zoomOut = this.getToolBar().createActionItem();
		this.zoomOut.addSelectionListener(this.createActionProcessor(TGSetLayoutScaleDecrementAction.NAME));
		this.zoomReset = this.getToolBar().createActionItem();
		this.zoomReset.addSelectionListener(this.createActionProcessor(TGSetLayoutScaleResetAction.NAME));
		this.zoomIn = this.getToolBar().createActionItem();
		this.zoomIn.addSelectionListener(this.createActionProcessor(TGSetLayoutScaleIncrementAction.NAME));

		this.loadIcons();
		this.loadProperties();
	}
	
	public void loadProperties(){
		this.showEditToolBar.setToolTipText(this.getText("view.show-edit-toolbar"));
		this.showTrackTable.setToolTipText(this.getText("view.show-table-viewer"));
		this.showInstruments.setToolTipText(this.getText("view.show-instruments"));
		this.showFretBoard.setToolTipText(this.getText("view.show-fretboard"));
		this.zoomOut.setToolTipText(this.getText("view.zoom.out"));
		this.zoomReset.setToolTipText(this.getText("view.zoom.reset"));
		this.zoomIn.setToolTipText(this.getText("view.zoom.in"));
	}
	
	public void loadIcons(){
		this.showEditToolBar.setImage(this.getIconManager().getToolbarEdit());
		this.showTrackTable.setImage(this.getIconManager().getTableViewer());
		this.showInstruments.setImage(this.getIconManager().getInstruments());
		this.showFretBoard.setImage(this.getIconManager().getFretboard());
		this.zoomOut.setImage(this.getIconManager().getZoomOut());
		this.zoomReset.setImage(this.getIconManager().getZoomReset());
		this.zoomIn.setImage(this.getIconManager().getZoomIn());
	}
	
	public void updateItems(){
		this.showEditToolBar.setChecked(TGEditToolBar.getInstance(this.getContext()).isVisible());
		this.showTrackTable.setChecked(TGTableViewer.getInstance(this.getContext()).isVisible());
		this.showInstruments.setChecked(!TGChannelManagerDialog.getInstance(this.getContext()).isDisposed());
		this.showFretBoard.setChecked(TGFretBoardEditor.getInstance(this.getContext()).isVisible());
	}
}
