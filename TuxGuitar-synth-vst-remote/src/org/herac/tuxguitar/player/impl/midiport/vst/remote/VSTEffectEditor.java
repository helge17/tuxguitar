package org.herac.tuxguitar.player.impl.midiport.vst.remote;

import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.view.util.TGDialogUtil;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.layout.UIScrollBarPanelLayout;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.widget.UIButton;
import org.herac.tuxguitar.ui.widget.UILabel;
import org.herac.tuxguitar.ui.widget.UILegendPanel;
import org.herac.tuxguitar.ui.widget.UIPanel;
import org.herac.tuxguitar.ui.widget.UIScale;
import org.herac.tuxguitar.ui.widget.UIScrollBarPanel;
import org.herac.tuxguitar.ui.widget.UIWindow;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGException;
import org.herac.tuxguitar.util.TGSynchronizer;

public class VSTEffectEditor {
	
	private VSTEffect effect;
	private TGContext context;
	private UIWindow dialog;
	
	public VSTEffectEditor(TGContext context, VSTEffect effect) {
		this.context = context;
		this.effect = effect;
	}
	
	public void open(UIWindow parent) {
		UIFactory uiFactory = TGApplication.getInstance(this.context).getFactory();
		UITableLayout dialogLayout = new UITableLayout();
		
		this.dialog = uiFactory.createWindow(parent, false, false);
		this.dialog.setLayout(dialogLayout);
		this.dialog.setText("VST Effect");
		
		//-------------------------------------------------------------------------
		UITableLayout groupLayout = new UITableLayout();
		UILegendPanel group = uiFactory.createLegendPanel(this.dialog);
		group.setLayout(groupLayout);
		group.setText("VST Effect Parameters");
		dialogLayout.set(group, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		//-------------------------------------------------------------------------
		final UIScrollBarPanel scrollBarPanel = uiFactory.createScrollBarPanel(group, true, false, false);
		scrollBarPanel.setLayout(new UIScrollBarPanelLayout(false, true, true, true, false, true));
		scrollBarPanel.getVScroll().setIncrement(10);
		scrollBarPanel.getVScroll().addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				scrollBarPanel.layout();
			}
		});
		
		groupLayout.set(scrollBarPanel, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 400f, 400f, null);
		
		UITableLayout panelLayout = new UITableLayout();
		UIPanel panel = uiFactory.createPanel(scrollBarPanel, false);
		panel.setLayout(panelLayout);
		
		int params = this.effect.getNumParams();
		params = params > 50 ? 50 : params;
		for( int i = 0 ; i < params ; i ++ ){
			final int index = i;
			final float value = this.effect.getParameter(i);
			final String name = this.effect.getParameterName( i );
			final String label = this.effect.getParameterLabel( i );
			
			final UILabel labelParameterName = uiFactory.createLabel(panel);
			labelParameterName.setText( (name != null ? name : ("") ) );
			panelLayout.set(labelParameterName, (1 + i), 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, false, false);
			
			final UILabel labelParameterLabel = uiFactory.createLabel(panel);
			labelParameterLabel.setText( (label != null ? label : ("") ) );
			panelLayout.set(labelParameterLabel, (1 + i), 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, false, false);
			
			final UIScale scaleParameterValue = uiFactory.createHorizontalScale(panel);
			scaleParameterValue.setMaximum(100);
			scaleParameterValue.setMinimum(0);
			scaleParameterValue.setIncrement(1);
			scaleParameterValue.setValue( Math.round(100 * value) );
			panelLayout.set(scaleParameterValue, (1 + i),3, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, false);
			
			final UILabel labelParameterValue = uiFactory.createLabel(panel);
			labelParameterValue.setText( Float.toString(value) );
			panelLayout.set(labelParameterValue, (1 + i), 4, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, false, false);
			
			scaleParameterValue.addSelectionListener(new UISelectionListener() {
				public void onSelect(UISelectionEvent event) {
					float selection = (scaleParameterValue.getValue() / 100f);
					labelParameterValue.setText(Float.toString(selection));
					
					VSTEffectEditor.this.effect.setParameter(index, selection);
				}
			} );
		}
		
		//-------------------------------------------------------------------------
		if( this.effect.isEditorAvailable() ) {
			UIButton nativeEditor = uiFactory.createButton(this.dialog);
			nativeEditor.setText("Native Editor");
			nativeEditor.addSelectionListener(new UISelectionListener() {
				public void onSelect(UISelectionEvent event) {
					new Thread(new Runnable() {
						public void run() throws TGException {
							toggleNativeEditor();
						}
					}).start();
				}
			});
			dialogLayout.set(nativeEditor, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, false);
		}
		
		TGDialogUtil.openDialog(this.dialog, TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
	}
	
	public void openInUiThread(final UIWindow parent) {
		TGSynchronizer.getInstance(this.context).executeLater(new Runnable() {
			public void run() {
				VSTEffectEditor.this.open(parent);
			}
		});
	}
	
	public void close() {
		if( this.isOpen() ){
			this.dialog.dispose();
			this.dialog = null;
		}
	}
	
	public boolean isOpen() {
		return (this.dialog != null && !this.dialog.isDisposed());
	}
	
	public void toggleNativeEditor() {
		if( this.effect.isNativeEditorOpen() ){
			this.effect.closeNativeEditor();
		}else{
			this.effect.openNativeEditor();
		}
	}
}
