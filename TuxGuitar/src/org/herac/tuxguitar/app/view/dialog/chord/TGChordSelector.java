package org.herac.tuxguitar.app.view.dialog.chord;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.util.TGMusicKeyUtils;
import org.herac.tuxguitar.song.models.TGChord;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.resource.UICursor;
import org.herac.tuxguitar.ui.widget.UIButton;
import org.herac.tuxguitar.ui.widget.UICheckBox;
import org.herac.tuxguitar.ui.widget.UIContainer;
import org.herac.tuxguitar.ui.widget.UIDropDownSelect;
import org.herac.tuxguitar.ui.widget.UILabel;
import org.herac.tuxguitar.ui.widget.UIListBoxSelect;
import org.herac.tuxguitar.ui.widget.UIPanel;
import org.herac.tuxguitar.ui.widget.UISelectItem;
import org.herac.tuxguitar.ui.widget.UISeparator;
import org.herac.tuxguitar.ui.widget.UIToggleButton;
import org.herac.tuxguitar.util.TGException;
import org.herac.tuxguitar.util.TGSynchronizer;

/**
 * @author julian
 * @author Nikola Kolarovic
 *
 *    WIDGET SET that allows complex chord choosing<br>
 *    Chord theory according to <a href="http://www.jazzguitar.be/quick_crd_ref.html">http://www.jazzguitar.be/quick_crd_ref.html</a>.
 */
public class TGChordSelector {
	
	public static final String[][] KEY_NAMES = new String[][]{
		TGMusicKeyUtils.getSharpKeyNames(TGMusicKeyUtils.PREFIX_CHORD),
		TGMusicKeyUtils.getFlatKeyNames(TGMusicKeyUtils.PREFIX_CHORD),
	};
	
	private TGChordDialog dialog;
	private int[] tuning;
	private UIPanel control;
	private UIListBoxSelect<Integer> tonicList;
	private UIListBoxSelect<Integer> chordList;
	private UIListBoxSelect<Integer> alterationList;
	private UIToggleButton sharpButton;
	private UIToggleButton flatButton;
	private UIDropDownSelect<Integer> bassCombo;
	private UICheckBox addCheck;
	private UIListBoxSelect<Integer> plusMinusList;
	private UIListBoxSelect<Integer> _5List;
	private UIListBoxSelect<Integer> _9List;
	private UIListBoxSelect<Integer> _11List;
	
	private boolean refresh;
	
	public TGChordSelector(TGChordDialog dialog, UIContainer parent, int[] tuning) {
		this.dialog = dialog;
		this.tuning = tuning;
		this.refresh = true;
		
		this.createControl(parent);
	}
	
	
	public void createControl(UIContainer parent) {
		UIFactory uiFactory = this.dialog.getUIFactory();
		UITableLayout uiLayout = new UITableLayout();
		
		this.control = uiFactory.createPanel(parent, true);
		this.control.setLayout(uiLayout);
		
		UITableLayout tonicLayout = new UITableLayout(0f);
		UIPanel tonicComposite = uiFactory.createPanel(this.control, false);
		tonicComposite.setLayout(tonicLayout);
		uiLayout.set(tonicComposite, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		this.tonicList = uiFactory.createListBoxSelect(tonicComposite);
		tonicLayout.set(this.tonicList, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		// sharp & flat buttons
		UITableLayout buttonsLayout = new UITableLayout(0f);
		UIPanel buttonsComposite = uiFactory.createPanel(tonicComposite, false);
		buttonsComposite.setLayout(buttonsLayout);
		tonicLayout.set(buttonsComposite, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, false);
		
		this.sharpButton = uiFactory.createToggleButton(buttonsComposite);
		this.sharpButton.setText("#");
		buttonsLayout.set(this.sharpButton, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, false, false);
		buttonsLayout.set(this.sharpButton, UITableLayout.PACKED_WIDTH, 28f);
		buttonsLayout.set(this.sharpButton, UITableLayout.PACKED_HEIGHT, 28f);
		
		this.flatButton = uiFactory.createToggleButton(buttonsComposite);
		this.flatButton.setText("b");
		buttonsLayout.set(this.flatButton, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, false, false);
		buttonsLayout.set(this.flatButton, UITableLayout.PACKED_WIDTH, 28f);
		buttonsLayout.set(this.flatButton, UITableLayout.PACKED_HEIGHT, 28f);
		
		this.chordList = uiFactory.createListBoxSelect(this.control);
		uiLayout.set(this.chordList, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		UISeparator separator = uiFactory.createHorizontalSeparator(tonicComposite);
		tonicLayout.set(separator, 3, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_BOTTOM, true, true);
		
		UIButton customizeButton = uiFactory.createButton(tonicComposite);
		customizeButton.setText(TuxGuitar.getProperty("settings"));
		tonicLayout.set(customizeButton, 4, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_BOTTOM, true, false);
		
		customizeButton.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				TGChordSettingsDialog settingsDialog = new TGChordSettingsDialog(TGChordSelector.this.dialog.getContext().getContext());
				settingsDialog.open(TGChordSelector.this.dialog.getWindow(), new TGChordSettingsHandler() {
					public void onSettingsUpdated() {
						TGSynchronizer.getInstance(getDialog().getContext().getContext()).executeLater(new Runnable() {
							public void run() throws TGException {
								TGChordSelector.this.showChord();
								getChordList().redraw();
							}
						});
					}
				});
			}
		});
		
		
		initChordWidgets();
		
		// fill the List widgets with text
		insertTonicNames(true);
		
		for(int i = 0 ; i < TGChordDatabase.length(); i ++) {
			this.chordList.addItem(new UISelectItem<Integer>(TGChordDatabase.get(i).getName(), i));
		}
		
		this.chordList.setSelectedValue(0);
		
		String[] alterationNames = getAlterationNames();
		for(int i = 0; i < alterationNames.length;i++){
			this.alterationList.addItem(new UISelectItem<Integer>(alterationNames[i], i));
		}
		this.alterationList.setSelectedValue(0);
		
		String[] plusMinus = this.getPlusMinus("");
		for(int i = 0;i < plusMinus.length;i++){
			this.plusMinusList.addItem(new UISelectItem<Integer>(plusMinus[i], i));
		}
		this.plusMinusList.setSelectedValue(0);
		
		String[] plus5Minus = this.getPlusMinus("/5");
		for(int i = 0;i < plus5Minus.length;i++){
			this._5List.addItem(new UISelectItem<Integer>(plus5Minus[i], i));
		}
		this._5List.setSelectedValue(0);
		String[] plus9Minus = this.getPlusMinus("/9");
		for(int i = 0;i < plus9Minus.length;i++){
			this._9List.addItem(new UISelectItem<Integer>(plus9Minus[i], i));
		}
		this._9List.setSelectedValue(0);
		String[] plus11Minus = this.getPlusMinus("/11");
		for(int i = 0;i < plus11Minus.length;i++){
			this._11List.addItem(new UISelectItem<Integer>(plus11Minus[i], i));
		}
		this._11List.setSelectedValue(0);
		
		// LISTENERS
		
		this.tonicList.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				if (TGChordSelector.this.getRefresh()) {
					if( getDialog().getEditor() != null && getDialog().getList() != null){
						getBassCombo().setSelectedValue(getTonicList().getSelectedValue());
						showChord();
					}
				}
			}
		});
		
		this.bassCombo.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				if (TGChordSelector.this.getRefresh()) {
					if( getDialog().getEditor() != null && getDialog().getList() != null){
						showChord();
					}
				}
			}
		});
		
		this.chordList.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				if(getDialog().getEditor() != null && getDialog().getList() != null){
					adjustWidgetAvailability();
					if (TGChordSelector.this.getRefresh()) {
						showChord();
					}
				}
			}
		});
		
		this.alterationList.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				if(getDialog().getEditor() != null && getDialog().getList() != null){
					TGChordSelector.this.adjustWidgetAvailability();
					if (TGChordSelector.this.getRefresh()) {
						showChord();
					}
				}
			}
		});
		
		this.addCheck.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				if( getDialog().getEditor() != null && getDialog().getList() != null){
					TGChordSelector.this.adjustWidgetAvailability();
					if (TGChordSelector.this.getRefresh()) {
						showChord();
					}
				}
				
			}
		});
		
		this._5List.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				if( getDialog().getEditor() != null && getDialog().getList() != null){
					if (TGChordSelector.this.getRefresh()) {
						showChord();
					}
				}
			}
		});
		
		this._9List.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				if( getDialog().getEditor() != null && getDialog().getList() != null){
					if (TGChordSelector.this.getRefresh()) {
						showChord();
					}
				}
			}
		});
		
		this._11List.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				if( getDialog().getEditor() != null && getDialog().getList() != null){
					if (TGChordSelector.this.getRefresh()) {
						showChord();
					}
				}
			}
		});
		
		this.plusMinusList.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				if( getDialog().getEditor() != null && getDialog().getList() != null){
					if (TGChordSelector.this.getRefresh()) {
						showChord();
					}
				}
			}
		});
		
		this.sharpButton.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				insertTonicNames(true);
			}
		});
		
		this.flatButton.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				insertTonicNames(false);
			}
		});
		this.adjustWidgetAvailability();
	}
	
	protected void initChordWidgets() {
		UIFactory uiFactory = this.dialog.getUIFactory();
		
		UITableLayout alterationLayout = new UITableLayout(0f);
		UIPanel alterationComposite = uiFactory.createPanel(this.control, false);
		alterationComposite.setLayout(alterationLayout);
		
		UITableLayout controlLayout = (UITableLayout) this.control.getLayout();
		controlLayout.set(alterationComposite, 1, 3, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		UITableLayout aboveLayout = new UITableLayout(0f);
		UIPanel aboveComposite = uiFactory.createPanel(alterationComposite, false);
		aboveComposite.setLayout(aboveLayout);
		alterationLayout.set(aboveComposite, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		UITableLayout firstLayout = new UITableLayout(0f);
		UIPanel firstComposite = uiFactory.createPanel(aboveComposite, false);
		firstComposite.setLayout(firstLayout);
		aboveLayout.set(firstComposite, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		this.alterationList = uiFactory.createListBoxSelect(firstComposite);
		firstLayout.set(this.alterationList, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		this.plusMinusList = uiFactory.createListBoxSelect(firstComposite);
		firstLayout.set(this.plusMinusList, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		UITableLayout secondLayout = new UITableLayout(0f);
		UIPanel secondComposite = uiFactory.createPanel(aboveComposite, false);
		secondComposite.setLayout(secondLayout);
		aboveLayout.set(secondComposite, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		this._5List = uiFactory.createListBoxSelect(secondComposite);
		secondLayout.set(this._5List, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		this._9List = uiFactory.createListBoxSelect(secondComposite);
		secondLayout.set(this._9List, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		this._11List = uiFactory.createListBoxSelect(secondComposite);
		secondLayout.set(this._11List, 3, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		UITableLayout bassLayout = new UITableLayout(0f);
		UIPanel bassComposite = uiFactory.createPanel(alterationComposite, false);
		bassComposite.setLayout(bassLayout);
		alterationLayout.set(bassComposite, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_BOTTOM, true, true);
		
		this.addCheck = uiFactory.createCheckBox(bassComposite);
		this.addCheck.setText("add");
		bassLayout.set(this.addCheck, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_BOTTOM, true, true);
		
		UISeparator separator = uiFactory.createHorizontalSeparator(bassComposite);
		bassLayout.set(separator, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_BOTTOM, true, true);
		
		UILabel bText = uiFactory.createLabel(bassComposite);
		bText.setText(TuxGuitar.getProperty("chord.bass"));
		bassLayout.set(bText, 3, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_BOTTOM, true, false);
		
		this.bassCombo = uiFactory.createDropDownSelect(bassComposite);
		bassLayout.set(this.bassCombo, 4, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_BOTTOM, true, false);
	}
	
	protected void insertTonicNames(boolean sharp){
		
		String[] names = KEY_NAMES[ sharp?0:1 ];
		
		// update the buttons
		this.flatButton.setSelected(!sharp);
		this.sharpButton.setSelected(sharp);
		// keep the old position
		int indexL = this.toInt(this.tonicList.getSelectedValue(), 0);
		int indexC = this.toInt(this.bassCombo.getSelectedValue(), 0);
		
		// update the list
		this.tonicList.removeItems();
		this.bassCombo.removeItems();
		for(int i = 0;i < names.length;i++){
			this.tonicList.addItem(new UISelectItem<Integer>(names[i], i));
			this.bassCombo.addItem(new UISelectItem<Integer>(names[i], i));
		}
		this.tonicList.setSelectedValue(indexL);
		this.bassCombo.setSelectedValue(indexC);
	}
	
	private String[] getPlusMinus(String text){
		String[] names = new String[3];
		
		names[0] = " ";
		names[1] = text+"+";
		names[2] = text+"-";
		
		return names;
	}
	
	private String[] getAlterationNames(){
		String[] names = new String[4];
		
		names[0] = " ";
		names[1] = "9";
		names[2] = "11";
		names[3] = "13";
		
		return names;
	}
	
	protected void showChord(){
		loadCursor(UICursor.WAIT);
		TGChordCreatorListener listener = new TGChordCreatorListener() {
			public void notifyChords(final TGChordCreatorUtil instance,final java.util.List<TGChord> chords) {
				TGSynchronizer.getInstance(getDialog().getContext().getContext()).executeLater(new Runnable() {
					public void run() {
						if( instance.isValidProcess() && !getDialog().isDisposed() ){
							getDialog().getList().setChords(chords);
							loadCursor(UICursor.NORMAL);
						}
					}
				});
			}
		};
		
		TGChordCreatorUtil.getChords(listener,
		                           this.tuning,
		                           this.toInt(this.chordList.getSelectedValue()),
		                           this.toInt(this.alterationList.getSelectedValue()),
		                           this.toInt(this.plusMinusList.getSelectedValue()),
		                           this.addCheck.isSelected(),
		                           this.toInt(this._5List.getSelectedValue()),
		                           this.toInt(this._9List.getSelectedValue()),
		                           this.toInt(this._11List.getSelectedValue()),
		                           this.toInt(this.bassCombo.getSelectedValue()),
		                           this.toInt(this.tonicList.getSelectedValue()),
		                           this.sharpButton.isSelected());
	}
	
	protected void updateWidget(UIListBoxSelect<Integer> widget, boolean enabled) {
		widget.setEnabled(enabled);
		if(!enabled){
			widget.setSelectedValue(0);
		}
	}
	
	protected void updateWidget(UICheckBox widget, boolean enabled) {
		widget.setEnabled(enabled);
		if(!enabled){
			widget.setSelected(false);
		}
	}
	
	/**
	 * Sets all the widgets' fields into recognized chord 
	 * (tonic, bass, chord, alterations)
	 */
	public void adjustWidgets(int tonic, int chordBasic, int alteration, int bass, int plusMinus, int addBoolean, int index5, int index9, int index11) {
		this.setRefresh(false);
		// adjust widgets
		this.tonicList.setSelectedValue(tonic);
		this.alterationList.setSelectedValue(alteration);
		this.bassCombo.setSelectedValue(bass);
		this.plusMinusList.setSelectedValue(plusMinus);
		this.addCheck.setSelected(addBoolean != 0);
		this._5List.setSelectedValue(index5);
		this._9List.setSelectedValue(index9);
		this._11List.setSelectedValue(index11);
		this.chordList.setSelectedValue(chordBasic);
		this.adjustWidgetAvailability();
		this.setRefresh(true);
		this.showChord();
	}
	
	/**
	 * adjusts the widgets availability according to chord theory options
	 */
	protected void adjustWidgetAvailability() {
		Integer chordIndex = getChordList().getSelectedValue();
		String chordName = (chordIndex != null ? TGChordDatabase.get(chordIndex).getName() : null);
		if( chordName != null && (chordName.equals("dim") || chordName.equals("dim7") || chordName.equals("aug") || chordName.equals("5"))) {
			updateWidget(getAlterationList(),false);
			updateWidget(getAddCheck(),false);
			updateWidget(get_9List(),false);
			updateWidget(get_11List(),false);
			updateWidget(getPlusMinusList(),false);
			
			if (!chordName.equals("5")){
				updateWidget(get_5List(),false);//disableWidget(get_5List());
			}else{
				updateWidget(get_5List(),true);
			}
		}
		else {
			// enable and don't change the selection index
			//getAlterationList().setEnabled(true);
			//get_5List().setEnabled(true);
			updateWidget(getAlterationList(),true);
			updateWidget(get_5List(),true);
		}
		
		if(this.alterationList.isEnabled()){
			int currentIndex = this.toInt(this.alterationList.getSelectedValue());
			
			// handle the +- list and ADD checkbox
			// handle the 9 and 11 list
			updateWidget(this.plusMinusList,(currentIndex > 0));
			updateWidget(this.addCheck,(currentIndex > 0));
			updateWidget(this._9List, (currentIndex >= 2 && !this.addCheck.isSelected() ) );
			updateWidget(this._11List, (currentIndex >= 3 && !this.addCheck.isSelected() ) );
		}
	}
	
	public int toInt(Integer integer) {
		return toInt(integer, -1);
	}
	
	public int toInt(Integer integer, int nullValue) {
		return (integer != null ? integer : nullValue);
	}
	
	public void loadCursor(UICursor cursor) {
		this.dialog.loadCursor(cursor);
	}
	
	public boolean getRefresh() {
		return this.refresh;
	}
	
	public void setRefresh(boolean refresh) {
		this.refresh = refresh;
	}
	
	public void setTuning(int[] tuning){
		this.tuning = tuning;
	}
	
	public int[] getTuning(){
		return this.tuning;
	}
	
	public TGChordDialog getDialog() {
		return this.dialog;
	}
	
	public UIPanel getControl() {
		return control;
	}

	public UIListBoxSelect<Integer> getTonicList() {
		return this.tonicList;
	}
	
	public UIListBoxSelect<Integer> getChordList() {
		return this.chordList;
	}
	
	public UIListBoxSelect<Integer> getAlterationList() {
		return this.alterationList;
	}
	
	public UIToggleButton getSharpButton() {
		return this.sharpButton;
	}
	
	public UIToggleButton getFlatButton() {
		return this.flatButton;
	}
	
	public UIDropDownSelect<Integer> getBassCombo() {
		return this.bassCombo;
	}
	
	public UICheckBox getAddCheck() {
		return this.addCheck;
	}
	
	public UIListBoxSelect<Integer> getPlusMinusList() {
		return this.plusMinusList;
	}
	
	public UIListBoxSelect<Integer> get_5List() {
		return this._5List;
	}
	
	public UIListBoxSelect<Integer> get_9List() {
		return this._9List;
	}
	
	public UIListBoxSelect<Integer> get_11List() {
		return this._11List;
	}
}
