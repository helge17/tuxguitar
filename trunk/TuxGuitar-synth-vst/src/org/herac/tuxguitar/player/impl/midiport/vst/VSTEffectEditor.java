package org.herac.tuxguitar.player.impl.midiport.vst;

import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.view.util.TGDialogUtil;
import org.herac.tuxguitar.event.TGEvent;
import org.herac.tuxguitar.event.TGEventListener;
import org.herac.tuxguitar.event.TGEventManager;
import org.herac.tuxguitar.midi.synth.remote.TGSession;
import org.herac.tuxguitar.midi.synth.ui.TGAudioProcessorUICallback;
import org.herac.tuxguitar.thread.TGThreadManager;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.event.UIDisposeEvent;
import org.herac.tuxguitar.ui.event.UIDisposeListener;
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

public class VSTEffectEditor implements TGEventListener {
	
	private VSTEffect effect;
	private TGContext context;
	private UIWindow dialog;
	private TGAudioProcessorUICallback callback;
	private UIScale[] scaleParameterValue;
	private UILabel[] labelParameterValue; 
	private Integer paramCount;
	
	public VSTEffectEditor(TGContext context, VSTEffect effect, TGAudioProcessorUICallback callback) {
		this.context = context;
		this.effect = effect;
		this.callback = callback;
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
		
		this.paramCount = Math.min(this.effect.getNumParams(), 50);		
		this.scaleParameterValue = new UIScale[this.paramCount];
		this.labelParameterValue = new UILabel[this.paramCount];
		for( int i = 0 ; i < this.paramCount ; i ++ ){
			final int index = i;
			final String name = this.effect.getParameterName( i );
			final String label = this.effect.getParameterLabel( i );
			
			final UILabel labelParameterName = uiFactory.createLabel(panel);
			labelParameterName.setText( (name != null ? name : ("") ) );
			panelLayout.set(labelParameterName, (1 + i), 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, false, false);
			
			final UILabel labelParameterLabel = uiFactory.createLabel(panel);
			labelParameterLabel.setText( (label != null ? label : ("") ) );
			panelLayout.set(labelParameterLabel, (1 + i), 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, false, false);
			
			this.scaleParameterValue[i] = uiFactory.createHorizontalScale(panel);
			this.scaleParameterValue[i].setMaximum(100);
			this.scaleParameterValue[i].setMinimum(0);
			this.scaleParameterValue[i].setIncrement(1);
			panelLayout.set(this.scaleParameterValue[i], (1 + i),3, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, false);
			
			this.labelParameterValue[i] = uiFactory.createLabel(panel);
			panelLayout.set(this.labelParameterValue[i], (1 + i), 4, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, false, false);
			
			this.scaleParameterValue[i].addSelectionListener(new UISelectionListener() {
				public void onSelect(UISelectionEvent event) {
					float selection = (VSTEffectEditor.this.scaleParameterValue[index].getValue() / 100f);
					
					VSTEffectEditor.this.labelParameterValue[index].setText(Float.toString(selection));
					VSTEffectEditor.this.effect.setParameter(index, selection);
					VSTEffectEditor.this.callback.onChange(false);
				}
			});
		}
		
		//-------------------------------------------------------------------------
		if( this.effect.isEditorAvailable() ) {
			UIButton nativeEditor = uiFactory.createButton(this.dialog);
			nativeEditor.setText("Native Editor");
			nativeEditor.addSelectionListener(new UISelectionListener() {
				public void onSelect(UISelectionEvent event) {
					TGThreadManager.getInstance(VSTEffectEditor.this.context).start(new Runnable() {
						public void run() throws TGException {
							toggleNativeEditor();
						}
					});
				}
			});
			dialogLayout.set(nativeEditor, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, false);
		}
		
		this.updateItems();
		this.addEventListeners();
		
		this.dialog.addDisposeListener(new UIDisposeListener() {
			public void onDispose(UIDisposeEvent event) {
				VSTEffectEditor.this.removeEventListeners();
			}
		});
		
		TGDialogUtil.openDialog(this.dialog, TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
	}
	
	public void addEventListeners() {
		TGEventManager.getInstance(this.context).addListener(VSTParamsEvent.EVENT_TYPE, this);
	}
	
	public void removeEventListeners() {
		TGEventManager.getInstance(this.context).removeListener(VSTParamsEvent.EVENT_TYPE, this);
	}
	
	public void updateItems() {
		int paramCount = Math.min(this.effect.getNumParams(), 50);
		for( int i = 0 ; i < paramCount && i < this.paramCount ; i ++ ){
			final float value = this.effect.getParameter(i);
			this.scaleParameterValue[i].setIgnoreEvents(true);
			this.scaleParameterValue[i].setValue( Math.round(100 * value) );
			this.scaleParameterValue[i].setIgnoreEvents(false);
			this.labelParameterValue[i].setText( Float.toString(value) );
		}
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
	
	public void openInUiThread(final UIWindow parent) {
		TGSynchronizer.getInstance(this.context).executeLater(new Runnable() {
			public void run() {
				VSTEffectEditor.this.open(parent);
			}
		});
	}
	
	public void updateItemsUiThread() {
		TGSynchronizer.getInstance(this.context).executeLater(new Runnable() {
			public void run() {
				VSTEffectEditor.this.updateItems();
			}
		});
	}
	
	public void processEvent(TGEvent event) {
		if( VSTParamsEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			TGSession session = event.getAttribute(VSTParamsEvent.PROPERTY_SESSION);
			if( VSTParamsEvent.ACTION_RESTORE.equals(event.getAttribute(VSTParamsEvent.PROPERTY_ACTION))) {
				if(!this.effect.isClosed() && this.effect.getSession().getId().equals(session.getId())) {
					this.updateItemsUiThread();
				}
			}
		}
	}
}
