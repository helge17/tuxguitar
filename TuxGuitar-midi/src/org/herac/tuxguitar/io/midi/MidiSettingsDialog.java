package org.herac.tuxguitar.io.midi;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.view.main.TGWindow;
import org.herac.tuxguitar.app.view.util.TGDialogUtil;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.widget.UIButton;
import org.herac.tuxguitar.ui.widget.UIDropDownSelect;
import org.herac.tuxguitar.ui.widget.UILabel;
import org.herac.tuxguitar.ui.widget.UILegendPanel;
import org.herac.tuxguitar.ui.widget.UIPanel;
import org.herac.tuxguitar.ui.widget.UISelectItem;
import org.herac.tuxguitar.ui.widget.UIWindow;
import org.herac.tuxguitar.util.TGContext;

public class MidiSettingsDialog {
	
	public static final int MAX_TRANSPOSE = 24;
	public static final int MIN_TRANSPOSE = -24;
	
	private static final int STATUS_NONE = 0;
	private static final int STATUS_CANCELLED = 1;
	private static final int STATUS_ACCEPTED = 2;
	
	private TGContext context;
	private MidiSettings settings;
	private int status;
	
	public MidiSettingsDialog(TGContext context){
		this.context = context;
		this.settings = new MidiSettings();
	}
	
	public MidiSettings open() {
		this.status = STATUS_NONE;
		
		final UIFactory uiFactory = TGApplication.getInstance(this.context).getFactory();
		final UIWindow parent = TGWindow.getInstance(this.context).getWindow();
		final UITableLayout dialogLayout = new UITableLayout();
		final UIWindow dialog = uiFactory.createWindow(parent, true, false);
		
		dialog.setLayout(dialogLayout);
		dialog.setText("Options");
		
		//------------------TRACK SELECTION------------------
		UITableLayout groupLayout = new UITableLayout();
		UILegendPanel group = uiFactory.createLegendPanel(dialog);
		group.setLayout(groupLayout);
		group.setText("Transpose notes");
		dialogLayout.set(group, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 320f, null, null);
		
		final UILabel transposeLabel = uiFactory.createLabel(group);
		transposeLabel.setText("Transpose:");
		groupLayout.set(transposeLabel, 1, 1, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_CENTER, false, false);
		
		final UIDropDownSelect<Integer> transposeCombo = uiFactory.createDropDownSelect(group);
		for(int i = MIN_TRANSPOSE;i <= MAX_TRANSPOSE;i ++){
			transposeCombo.addItem(new UISelectItem<Integer>(Integer.toString(i), i));
		}
		transposeCombo.setSelectedValue(0);
		groupLayout.set(transposeCombo, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, false);
		
		//------------------BUTTONS--------------------------
		UITableLayout buttonsLayout = new UITableLayout(0f);
		UIPanel buttons = uiFactory.createPanel(dialog, false);
		buttons.setLayout(buttonsLayout);
		dialogLayout.set(buttons, 2, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, true, true);
		
		final UIButton buttonOK = uiFactory.createButton(buttons);
		buttonOK.setText(TuxGuitar.getProperty("ok"));
		buttonOK.setDefaultButton();
		buttonOK.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				Integer transposition = transposeCombo.getSelectedValue();
				MidiSettingsDialog.this.status = STATUS_ACCEPTED;
				MidiSettingsDialog.this.settings.setTranspose(transposition != null ? transposition : 0);
				dialog.dispose();
			}
		});
		buttonsLayout.set(buttonOK, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 80f, 25f, null);
		
		UIButton buttonCancel = uiFactory.createButton(buttons);
		buttonCancel.setText(TuxGuitar.getProperty("cancel"));
		buttonCancel.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				MidiSettingsDialog.this.status = STATUS_CANCELLED;
				dialog.dispose();
			}
		});
		buttonsLayout.set(buttonCancel, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 80f, 25f, null);
		buttonsLayout.set(buttonCancel, UITableLayout.MARGIN_RIGHT, 0f);
		
		TGDialogUtil.openDialog(dialog, TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK | TGDialogUtil.OPEN_STYLE_WAIT);
		
		return ((this.status == STATUS_ACCEPTED)?MidiSettingsDialog.this.settings:null);
	}
}
