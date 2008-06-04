/*
 * Created on 02-ene-2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.gui.editors.chord;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.helper.SyncThread;
import org.herac.tuxguitar.gui.util.TGMusicKeyUtils;
import org.herac.tuxguitar.util.TGSynchronizer;

/**
 * @author julian
 * @author Nikola Kolarovic
 *
 *    WIDGET SET that allows complex chord choosing<br>
 *    Chord theory according to <a href="http://www.jazzguitar.be/quick_crd_ref.html">http://www.jazzguitar.be/quick_crd_ref.html</a>.
 */
public class ChordSelector extends Composite{
	
	public static final String[][] KEY_NAMES = new String[][]{
		TGMusicKeyUtils.getSharpKeyNames(TGMusicKeyUtils.PREFIX_CHORD),
		TGMusicKeyUtils.getFlatKeyNames(TGMusicKeyUtils.PREFIX_CHORD),
	};
	
	private ChordDialog dialog;
	private int[] tuning;
	private List tonicList;
	private List chordList;
	private List alterationList;
	private Button sharpButton;
	private Button flatButton;
	private Combo bassCombo;
	private Button addCheck;
	private List plusMinusList;
	private List _5List;
	private List _9List;
	private List _11List;
	
	private boolean refresh;
	
	public ChordSelector(ChordDialog dialog,Composite parent,int style,int[] tuning) {
		super(parent,style);
		this.setLayout(new GridLayout(3,false));
		this.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		this.dialog = dialog;
		this.tuning = tuning;
		
		this.refresh = true;
		this.init();
	}
	
	
	public void init(){
		Composite tonicComposite = new Composite(this,SWT.NONE);
		tonicComposite.setLayout(this.dialog.gridLayout(1,false,0,0));
		tonicComposite.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		
		this.tonicList = new List(tonicComposite,SWT.BORDER);
		this.tonicList.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		
		// sharp & flat buttons
		Composite buttonsComposite = new Composite(tonicComposite,SWT.NONE);
		buttonsComposite.setLayout(this.dialog.gridLayout(2,true,0,0));
		GridData buttonGd = new GridData(SWT.FILL,SWT.TOP,true,false);
		buttonGd.heightHint = 28;
		buttonGd.widthHint = 28;
		this.sharpButton = new Button(buttonsComposite,SWT.TOGGLE);
		this.sharpButton.setLayoutData(buttonGd);
		this.flatButton = new Button(buttonsComposite,SWT.TOGGLE);
		this.flatButton.setLayoutData(buttonGd);
		// TODO: maybe put an image instead of #,b
		this.sharpButton.setText("#");
		this.flatButton.setText("b");
		this.chordList = new List(this,SWT.BORDER);
		this.chordList.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		
		Label separator = new Label(tonicComposite,SWT.SEPARATOR | SWT.HORIZONTAL);
		separator.setLayoutData(new GridData(SWT.FILL,SWT.BOTTOM,true,true));
		Button customizeButton = new Button(tonicComposite,SWT.PUSH);
		customizeButton.setLayoutData(new GridData(SWT.FILL,SWT.BOTTOM,true,false));
		customizeButton.setText(TuxGuitar.getProperty("settings"));
		
		customizeButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				if(new ChordSettingsDialog().open(ChordSelector.this.getShell())){
					new SyncThread(new Runnable() {
						public void run() {
							ChordSelector.this.showChord();
							getChordList().redraw();
						}
					}).start();
				}
			}
		});
		
		
		initChordWidgets();
		
		// fill the List widgets with text
		insertTonicNames(true);
		
		for(int i = 0 ; i < ChordDatabase.length(); i ++) {
			this.chordList.add( ChordDatabase.get(i).getName() );
		}
		/*
		Iterator chordInfo = ChordCreatorUtil.getChordData().getChords().iterator();
		while(chordInfo.hasNext()) {
			this.chordList.add( ((ChordDatabase.ChordInfo)chordInfo.next()).getName() );
		}
		*/
		
		this.chordList.setSelection(0);
		
		String[] alterationNames = getAlterationNames();
		for(int i = 0;i < alterationNames.length;i++){
			this.alterationList.add(alterationNames[i]);
		}
		this.alterationList.setSelection(0);
		
		String[] plusMinus = this.getPlusMinus("");
		for(int i = 0;i < plusMinus.length;i++){
			this.plusMinusList.add(plusMinus[i]);
		}
		this.plusMinusList.setSelection(0);
		
		String[] plus5Minus = this.getPlusMinus("/5");
		for(int i = 0;i < plus5Minus.length;i++){
			this._5List.add(plus5Minus[i]);
		}
		this._5List.setSelection(0);
		String[] plus9Minus = this.getPlusMinus("/9");
		for(int i = 0;i < plus9Minus.length;i++){
			this._9List.add(plus9Minus[i]);
		}
		this._9List.setSelection(0);
		String[] plus11Minus = this.getPlusMinus("/11");
		for(int i = 0;i < plus11Minus.length;i++){
			this._11List.add(plus11Minus[i]);
		}
		this._11List.setSelection(0);
		
		// LISTENERS
		
		this.tonicList.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (ChordSelector.this.getRefresh()) {
					if(getDialog().getEditor() != null && getDialog().getList() != null){
						getBassCombo().select(getTonicList().getSelectionIndex());
						showChord();
					}
				}
			}
		});
		
		this.bassCombo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (ChordSelector.this.getRefresh()) {
					if(getDialog().getEditor() != null && getDialog().getList() != null){
						showChord();
					}
				}
			}
		});
		
		this.chordList.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if(getDialog().getEditor() != null && getDialog().getList() != null){
					adjustWidgetAvailability();
					if (ChordSelector.this.getRefresh()) {
						showChord();
					}
				}
			}
		});
		
		this.alterationList.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if(getDialog().getEditor() != null && getDialog().getList() != null){
					ChordSelector.this.adjustWidgetAvailability();
					if (ChordSelector.this.getRefresh()) {
						showChord();
					}
				}
			}
		});
		
		this.addCheck.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				if(getDialog().getEditor() != null && getDialog().getList() != null){
					
					ChordSelector.this.adjustWidgetAvailability();
					/*
					if (getAddCheck().getSelection()) {
						updateWidget(get_9List(), false);
						updateWidget(get_11List(), false);
					}
					*/
					if (ChordSelector.this.getRefresh()) {
						showChord();
						//ChordSelector.this.dialog.getList().redraw();
					}
				}
				
			}
		});
		
		this._5List.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if(getDialog().getEditor() != null && getDialog().getList() != null){
					if (ChordSelector.this.getRefresh()) {
						showChord();
					}
				}
			}
		});
		
		this._9List.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if(getDialog().getEditor() != null && getDialog().getList() != null){
					if (ChordSelector.this.getRefresh()) {
						showChord();
					}
				}
			}
		});
		
		this._11List.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if(getDialog().getEditor() != null && getDialog().getList() != null){
					if (ChordSelector.this.getRefresh()) {
						showChord();
					}
				}
			}
		});
		
		this.plusMinusList.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if(getDialog().getEditor() != null && getDialog().getList() != null){
					if (ChordSelector.this.getRefresh()) {
						showChord();
						//ChordSelector.this.dialog.getList().redraw();
					}
				}
			}
		});
		
		this.sharpButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				insertTonicNames(true);
			}
		});
		
		this.flatButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				insertTonicNames(false);
			}
		});
		this.adjustWidgetAvailability();
	}
	
	protected void initChordWidgets() {
		Composite alterationComposite = new Composite(this,SWT.NONE);
		alterationComposite.setLayout(this.dialog.gridLayout(1,true,0,0));
		alterationComposite.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		Composite aboveComposite = new Composite(alterationComposite,SWT.NONE);
		aboveComposite.setLayout(this.dialog.gridLayout(2,true,0,0));
		aboveComposite.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		Composite firstComposite = new Composite(aboveComposite,SWT.NONE);
		firstComposite.setLayout(this.dialog.gridLayout(1,false,0,0));
		firstComposite.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		this.alterationList = new List(firstComposite,SWT.BORDER);
		this.alterationList.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		this.plusMinusList = new List(firstComposite,SWT.BORDER);
		this.plusMinusList.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		
		Composite secondComposite = new Composite(aboveComposite,SWT.NONE);
		secondComposite.setLayout(this.dialog.gridLayout(1,false,0,0));
		secondComposite.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		this._5List = new List(secondComposite,SWT.BORDER);
		this._5List.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		
		this._9List = new List(secondComposite,SWT.BORDER);
		this._9List.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		
		this._11List = new List(secondComposite,SWT.BORDER);
		this._11List.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		
		Composite bassComposite = new Composite(alterationComposite,SWT.NONE);
		bassComposite.setLayout(this.dialog.gridLayout(1,true,0,0));
		bassComposite.setLayoutData(new GridData(SWT.FILL,SWT.BOTTOM,true,true));
		this.addCheck = new Button(bassComposite, SWT.CHECK | SWT.LEFT);
		this.addCheck.setText("add");
		//this.addCheck.setSelection(false);
		//this.addCheck.setEnabled(false);
		this.addCheck.setLayoutData(new GridData(SWT.FILL,SWT.BOTTOM,true,true));
		
		Label separator = new Label(bassComposite,SWT.SEPARATOR | SWT.HORIZONTAL );
		separator.setLayoutData(new GridData(SWT.FILL,SWT.BOTTOM,true,true));
		
		Label bText = new Label(bassComposite,SWT.LEFT);
		bText.setLayoutData(new GridData(SWT.FILL,SWT.BOTTOM,true,false));
		bText.setText(TuxGuitar.getProperty("chord.bass"));
		this.bassCombo = new Combo(bassComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
		this.bassCombo.setLayoutData(new GridData(SWT.FILL,SWT.BOTTOM,true,false));
	}
	
	protected void insertTonicNames(boolean sharp){
		
		String[] names = KEY_NAMES[ sharp?0:1 ];
		
		// update the buttons
		this.flatButton.setSelection(!sharp);
		this.sharpButton.setSelection(sharp);
		// keep the old position
		int indexL = this.tonicList.getSelectionIndex();
		if (indexL==-1) indexL=0;
		int indexC = this.bassCombo.getSelectionIndex();
		if (indexC==-1) indexC=0;
		
		// update the list
		this.tonicList.removeAll();
		this.bassCombo.removeAll();
		for(int i = 0;i < names.length;i++){
			this.tonicList.add(names[i]);
			this.bassCombo.add(names[i]);
		}
		this.tonicList.setSelection(indexL);
		this.bassCombo.select(indexC);
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
		TuxGuitar.instance().loadCursor(getShell(),SWT.CURSOR_WAIT);
		ChordCreatorListener listener = new ChordCreatorListener() {
			public void notifyChords(final ChordCreatorUtil instance,final java.util.List chords) {
				try {
					TGSynchronizer.instance().addRunnable(new TGSynchronizer.TGRunnable() {
						public void run() {
							if(instance.isValidProcess() && !getDialog().isDisposed()){
								getDialog().getList().setChords(chords);
								TuxGuitar.instance().loadCursor(getShell(),SWT.CURSOR_ARROW);
							}
						}
					});
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		};
		
		ChordCreatorUtil.getChords(listener,
		                           this.tuning,
		                           this.chordList.getSelectionIndex(),
		                           this.alterationList.getSelectionIndex(),
		                           this.plusMinusList.getSelectionIndex(),
		                           this.addCheck.getSelection(),
		                           this._5List.getSelectionIndex(),
		                           this._9List.getSelectionIndex(),
		                           this._11List.getSelectionIndex(),
		                           this.bassCombo.getSelectionIndex(),
		                           this.tonicList.getSelectionIndex(),
		                           this.sharpButton.getSelection());
	}
	
	protected void updateWidget(List widget, boolean enabled) {
		widget.setEnabled(enabled);
		if(!enabled){
			widget.setSelection(0);
		}
	}
	
	protected void updateWidget(Button widget, boolean enabled) {
		widget.setEnabled(enabled);
		if(!enabled){
			widget.setSelection(false);
		}
	}
	
	/**
	 * Sets all the widgets' fields into recognized chord 
	 * (tonic, bass, chord, alterations)
	 */
	public void adjustWidgets(int tonic, int chordBasic, int alteration, int bass, int plusMinus, int addBoolean, int index5, int index9, int index11) {
		this.setRefresh(false);
		// adjust widgets
		this.tonicList.setSelection(tonic);
		this.alterationList.setSelection(alteration);
		this.bassCombo.select(bass);
		this.plusMinusList.setSelection(plusMinus);
		this.addCheck.setSelection(addBoolean != 0);
		this._5List.setSelection(index5);
		this._9List.setSelection(index9);
		this._11List.setSelection(index11);
		this.chordList.setSelection(chordBasic);
		this.adjustWidgetAvailability();
		this.setRefresh(true);
		this.showChord();
	}
	
	/**
	 * adjusts the widgets availability according to chord theory options
	 */
	protected void adjustWidgetAvailability() {
		String chordName = ChordDatabase.get(getChordList().getSelectionIndex()).getName();
		if (chordName.equals("dim") || chordName.equals("dim7") || chordName.equals("aug") || chordName.equals("5") ) {
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
			int currentIndex = this.alterationList.getSelectionIndex();
			// handle the +- list and ADD checkbox
			// handle the 9 and 11 list
			updateWidget(this.plusMinusList,(currentIndex > 0));
			updateWidget(this.addCheck,(currentIndex > 0));
			updateWidget(this._9List, (currentIndex >= 2 && !this.addCheck.getSelection() ) );
			updateWidget(this._11List, (currentIndex >= 3 && !this.addCheck.getSelection() ) );
		}
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
	
	protected ChordDialog getDialog() {
		return this.dialog;
	}
	
	protected List getTonicList() {
		return this.tonicList;
	}
	
	protected List getChordList() {
		return this.chordList;
	}
	
	protected List getAlterationList() {
		return this.alterationList;
	}
	
	protected Button getSharpButton() {
		return this.sharpButton;
	}
	
	protected Button getFlatButton() {
		return this.flatButton;
	}
	
	protected Combo getBassCombo() {
		return this.bassCombo;
	}
	
	protected Button getAddCheck() {
		return this.addCheck;
	}
	
	protected List getPlusMinusList() {
		return this.plusMinusList;
	}
	
	protected List get_5List() {
		return this._5List;
	}
	
	protected List get_9List() {
		return this._9List;
	}
	
	protected List get_11List() {
		return this._11List;
	}
}
