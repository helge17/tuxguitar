package org.herac.tuxguitar.gui.system.icons;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Resource;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.system.config.TGConfigKeys;
import org.herac.tuxguitar.gui.util.TGFileUtils;
import org.herac.tuxguitar.song.models.TGDuration;

public class IconManager {
	private String theme;
	private List loaders;
	private List disposableIcons;
	
	private Image[] durations;
	private Image editUndo;
	private Image editRedo;
	private Image editVoice1;
	private Image editVoice2;
	private Image editModeSelection;
	private Image editModeEdition;
	private Image editModeEditionNotNatural;
	private Image layoutPage;
	private Image layoutLinear;
	private Image layoutMultitrack;
	private Image layoutScore;
	private Image layoutCompact;
	private Image transport;
	private Image transportFirst1;
	private Image transportFirst2;
	private Image transportLast1;
	private Image transportLast2;
	private Image transportPrevious1;
	private Image transportPrevious2;
	private Image transportNext1;
	private Image transportNext2;
	private Image transportStop1;
	private Image transportStop2;
	private Image transportPlay1;
	private Image transportPlay2;
	private Image transportPause;
	private Image transportIconFirst1;
	private Image transportIconFirst2;
	private Image transportIconLast1;
	private Image transportIconLast2;
	private Image transportIconPrevious1;
	private Image transportIconPrevious2;
	private Image transportIconNext1;
	private Image transportIconNext2;
	private Image transportIconStop1;
	private Image transportIconStop2;
	private Image transportIconPlay1;
	private Image transportIconPlay2;
	private Image transportIconPause;
	private Image transportMetronome;
	private Image transportMode;
	private Image markerList;
	private Image markerAdd;
	private Image markerRemove;
	private Image markerFirst;
	private Image markerLast;
	private Image markerPrevious;
	private Image markerNext;
	private Image aboutDescription;
	private Image aboutLicense;
	private Image aboutAuthors;
	private Image appIcon;
	private Image appSplash;
	private Image optionMain;
	private Image optionStyle;
	private Image optionSound;
	private Image optionLanguage;
	private Image optionToolbars;
	private Image optionSkin;
	private Image trackAdd;
	private Image trackRemove;
	private Image paintableTempo;
	private Image paintableGrace;
	private Image fretboard;
	private Image fretboardFirstFret;
	private Image fretboardFret;
	private Image compositionTimeSignature;
	private Image compositionTempo;
	private Image compositionRepeatOpen;
	private Image compositionRepeatClose;
	private Image compositionRepeatAlternative;
	private Image songProperties;
	private Image durationDotted;
	private Image durationDoubleDotted;
	private Image divisionType;
	private Image fileNew;
	private Image fileOpen;
	private Image fileSave;
	private Image fileSaveAs;
	private Image filePrint;
	private Image filePrintPreview;
	private Image chord;
	private Image noteTied;
	private Image mixer;
	private Image dynamicPPP;
	private Image dynamicPP;
	private Image dynamicP;
	private Image dynamicMP;
	private Image dynamicMF;
	private Image dynamicF;
	private Image dynamicFF;
	private Image dynamicFFF;
	private Image effectDead;
	private Image effectGhost;
	private Image effectAccentuated;
	private Image effectHeavyAccentuated;
	private Image effectHarmonic;
	private Image effectGrace;
	private Image effectBend;
	private Image effectTremoloBar;
	private Image effectSlide;
	private Image effectHammer;
	private Image effectVibrato;
	private Image effectTrill;
	private Image effectTremoloPicking;
	private Image effectPalmMute;
	private Image effectStaccato;
	private Image effectTapping;
	private Image effectSlapping;
	private Image effectPopping;
	private Image effectFadeIn;
	private Image browserNew;
	private Image browserFile;
	private Image browserFolder;
	private Image browserRoot;
	private Image browserBack;
	private Image browserRefresh;
	private Image settings;
	
	public IconManager(){
		this.loaders = new ArrayList();
		this.disposableIcons = new ArrayList();
		this.loadIcons();
	}
	
	public void addLoader(IconLoader loader){
		if( !this.loaders.contains( loader )){
			this.loaders.add(loader);
		}
	}
	
	public void removeLoader(IconLoader loader){
		if( this.loaders.contains( loader )){
			this.loaders.remove(loader);
		}
	}
	
	private void fireChanges(){
		Iterator it = this.loaders.iterator();
		while(it.hasNext()){
			IconLoader loader = (IconLoader)it.next();
			loader.loadIcons();
		}
	}
	
	public void reloadIcons(){
		List disposableIcons = purgeDisposableIcons();
		this.loadIcons();
		this.fireChanges();
		this.disposeIcons(disposableIcons);
	}
	
	public void loadIcons(){
		this.theme = TuxGuitar.instance().getConfig().getStringConfigValue(TGConfigKeys.SKIN);
		this.durations = new Image[]{
			loadIcon("1.png"),
			loadIcon("2.png"),
			loadIcon("4.png"),
			loadIcon("8.png"),
			loadIcon("16.png"),
			loadIcon("32.png"),
			loadIcon("64.png")
		};
		this.paintableTempo = loadIcon("tempo.png");
		this.paintableGrace = loadIcon("grace.png");
		this.layoutPage = loadIcon("layout_page.png");
		this.layoutLinear = loadIcon("layout_linear.png");
		this.layoutMultitrack = loadIcon("layout_multitrack.png");
		this.layoutScore = loadIcon("layout_score.png");
		this.layoutCompact = loadIcon("layout_compact.png");
		this.fileNew = loadIcon("new.png");
		this.fileOpen = loadIcon("open.png");
		this.fileSave = loadIcon("save.png");
		this.fileSaveAs = loadIcon("save-as.png");
		this.filePrint = loadIcon("print.png");
		this.filePrintPreview = loadIcon("print-preview.png");
		this.editUndo = loadIcon("edit_undo.png");
		this.editRedo = loadIcon("edit_redo.png");
		this.editVoice1 = loadIcon("edit_voice_1.png");
		this.editVoice2 = loadIcon("edit_voice_2.png");
		this.editModeSelection = loadIcon("edit_mode_selection.png");
		this.editModeEdition = loadIcon("edit_mode_edition.png");
		this.editModeEditionNotNatural = loadIcon("edit_mode_edition_no_natural.png");
		this.appIcon = loadIcon("icon.png");
		this.appSplash = loadIcon("splash.png");
		this.aboutDescription = loadIcon("about_description.png");
		this.aboutLicense = loadIcon("about_license.png");
		this.aboutAuthors = loadIcon("about_authors.png");
		this.optionMain = loadIcon("option_view.png");
		this.optionStyle = loadIcon("option_style.png");
		this.optionSound = loadIcon("option_sound.png");
		this.optionSkin = loadIcon("option_skin.png");
		this.optionLanguage= loadIcon("option_language.png");
		this.optionToolbars = loadIcon("option_toolbars.png");
		this.compositionTimeSignature = loadIcon("timesignature.png");
		this.compositionTempo = loadIcon("tempoicon.png");
		this.compositionRepeatOpen = loadIcon("openrepeat.png");
		this.compositionRepeatClose = loadIcon("closerepeat.png");
		this.compositionRepeatAlternative = loadIcon("repeat_alternative.png");
		this.songProperties = loadIcon("song_properties.png");
		this.trackAdd = loadIcon("track_add.png");
		this.trackRemove = loadIcon("track_remove.png");
		this.durationDotted = loadIcon("dotted.png");
		this.durationDoubleDotted = loadIcon("doubledotted.png");
		this.divisionType = loadIcon("division-type.png");
		this.fretboard = loadIcon("fretboard.png");
		this.fretboardFirstFret = loadIcon("firstfret.png");
		this.fretboardFret = loadIcon("fret.png");
		this.chord = loadIcon("chord.png");
		this.noteTied = loadIcon("tiednote.png");
		this.transport = loadIcon("transport.png");
		this.transportFirst1 = loadIcon("transport_first_1.png");
		this.transportFirst2 = loadIcon("transport_first_2.png");
		this.transportLast1 = loadIcon("transport_last_1.png");
		this.transportLast2 = loadIcon("transport_last_2.png");
		this.transportPrevious1 = loadIcon("transport_previous_1.png");
		this.transportPrevious2 = loadIcon("transport_previous_2.png");
		this.transportNext1 = loadIcon("transport_next_1.png");
		this.transportNext2 = loadIcon("transport_next_2.png");
		this.transportStop1 = loadIcon("transport_stop_1.png");
		this.transportStop2 = loadIcon("transport_stop_2.png");
		this.transportPlay1 = loadIcon("transport_play_1.png");
		this.transportPlay2 = loadIcon("transport_play_2.png");
		this.transportPause = loadIcon("transport_pause.png");
		this.transportIconFirst1 = loadIcon("transport_icon_first_1.png");
		this.transportIconFirst2 = loadIcon("transport_icon_first_2.png");
		this.transportIconLast1 = loadIcon("transport_icon_last_1.png");
		this.transportIconLast2 = loadIcon("transport_icon_last_2.png");
		this.transportIconPrevious1 = loadIcon("transport_icon_previous_1.png");
		this.transportIconPrevious2 = loadIcon("transport_icon_previous_2.png");
		this.transportIconNext1 = loadIcon("transport_icon_next_1.png");
		this.transportIconNext2 = loadIcon("transport_icon_next_2.png");
		this.transportIconStop1 = loadIcon("transport_icon_stop_1.png");
		this.transportIconStop2 = loadIcon("transport_icon_stop_2.png");
		this.transportIconPlay1 = loadIcon("transport_icon_play_1.png");
		this.transportIconPlay2 = loadIcon("transport_icon_play_2.png");
		this.transportIconPause = loadIcon("transport_icon_pause.png");
		this.transportMetronome = loadIcon("transport_metronome.png");
		this.transportMode = loadIcon("transport_mode.png");
		this.markerList = loadIcon("marker_list.png");
		this.markerAdd = loadIcon("marker_add.png");
		this.markerRemove = loadIcon("marker_remove.png");
		this.markerFirst = loadIcon("marker_first.png");
		this.markerLast = loadIcon("marker_last.png");
		this.markerPrevious = loadIcon("marker_previous.png");
		this.markerNext = loadIcon("marker_next.png");
		this.mixer = loadIcon("mixer.png");
		this.dynamicPPP = loadIcon("dynamic_ppp.png");
		this.dynamicPP = loadIcon("dynamic_pp.png");
		this.dynamicP = loadIcon("dynamic_p.png");
		this.dynamicMP =loadIcon("dynamic_mp.png");
		this.dynamicMF = loadIcon("dynamic_mf.png");
		this.dynamicF = loadIcon("dynamic_f.png");
		this.dynamicFF = loadIcon("dynamic_ff.png");
		this.dynamicFFF = loadIcon("dynamic_fff.png");
		this.effectDead = loadIcon("effect_dead.png");
		this.effectGhost = loadIcon("effect_ghost.png");
		this.effectAccentuated = loadIcon("effect_accentuated.png");
		this.effectHeavyAccentuated = loadIcon("effect_heavy_accentuated.png");
		this.effectHarmonic = loadIcon("effect_harmonic.png");
		this.effectGrace = loadIcon("effect_grace.png");
		this.effectBend = loadIcon("effect_bend.png");
		this.effectTremoloBar = loadIcon("effect_tremolo_bar.png");
		this.effectSlide = loadIcon("effect_slide.png");
		this.effectHammer = loadIcon("effect_hammer.png");
		this.effectVibrato = loadIcon("effect_vibrato.png");
		this.effectTrill= loadIcon("effect_trill.png");
		this.effectTremoloPicking = loadIcon("effect_tremolo_picking.png");
		this.effectPalmMute= loadIcon("effect_palm_mute.png");
		this.effectStaccato = loadIcon("effect_staccato.png");
		this.effectTapping = loadIcon("effect_tapping.png");
		this.effectSlapping = loadIcon("effect_slapping.png");
		this.effectPopping = loadIcon("effect_popping.png");
		this.effectFadeIn = loadIcon("effect_fade_in.png");
		this.browserNew = loadIcon("browser_new.png");
		this.browserFile = loadIcon("browser_file.png");
		this.browserFolder = loadIcon("browser_folder.png");
		this.browserRoot = loadIcon("browser_root.png");
		this.browserBack = loadIcon("browser_back.png");
		this.browserRefresh = loadIcon("browser_refresh.png");
		this.settings = loadIcon("settings.png");
	}
	
	private Image loadIcon(String name){
		Image image = TGFileUtils.loadImage(this.theme,name);
		this.disposableIcons.add(image);
		return image;
	}
	
	private List purgeDisposableIcons(){
		List disposableIcons = new ArrayList();
		Iterator it = this.disposableIcons.iterator();
		while( it.hasNext() ){
			Resource resource = (Resource)it.next();
			disposableIcons.add( resource );
		}
		this.disposableIcons.clear();
		return disposableIcons;
	}
	
	public void disposeIcons(List resources){
		Iterator it = resources.iterator();
		while( it.hasNext() ){
			Image image = (Image)it.next();
			image.dispose();
		}
	}
	
	public void disposeIcons(){
		this.disposeIcons(purgeDisposableIcons());
	}
	
	public boolean shouldReload(){
		return (!this.theme.equals(TuxGuitar.instance().getConfig().getStringConfigValue(TGConfigKeys.SKIN)));
	}
	
	public Image getDuration(int value){
		switch(value){
		case TGDuration.WHOLE:
			return this.durations[0];
		case TGDuration.HALF:
			return this.durations[1];
		case TGDuration.QUARTER:
			return this.durations[2];
		case TGDuration.EIGHTH:
			return this.durations[3];
		case TGDuration.SIXTEENTH:
			return this.durations[4];
		case TGDuration.THIRTY_SECOND:
			return this.durations[5];
		case TGDuration.SIXTY_FOURTH:
			return this.durations[6];
		}
		return null;
	}
	
	public Image getAboutAuthors() {
		return this.aboutAuthors;
	}
	
	public Image getAboutDescription() {
		return this.aboutDescription;
	}
	
	public Image getAboutLicense() {
		return this.aboutLicense;
	}
	
	public Image getAppIcon() {
		return this.appIcon;
	}
	
	public Image getAppSplash() {
		return this.appSplash;
	}
	
	public Image getChord() {
		return this.chord;
	}
	
	public Image getCompositionRepeatClose() {
		return this.compositionRepeatClose;
	}
	
	public Image getCompositionRepeatAlternative() {
		return this.compositionRepeatAlternative;
	}
	
	public Image getCompositionRepeatOpen() {
		return this.compositionRepeatOpen;
	}
	
	public Image getCompositionTempo() {
		return this.compositionTempo;
	}
	
	public Image getCompositionTimeSignature() {
		return this.compositionTimeSignature;
	}
	
	public Image getDurationDotted() {
		return this.durationDotted;
	}
	
	public Image getDurationDoubleDotted() {
		return this.durationDoubleDotted;
	}
	
	public Image getDivisionType() {
		return this.divisionType;
	}
	
	public Image getDynamicF() {
		return this.dynamicF;
	}
	
	public Image getDynamicFF() {
		return this.dynamicFF;
	}
	
	public Image getDynamicFFF() {
		return this.dynamicFFF;
	}
	
	public Image getDynamicMF() {
		return this.dynamicMF;
	}
	
	public Image getDynamicMP() {
		return this.dynamicMP;
	}
	
	public Image getDynamicP() {
		return this.dynamicP;
	}
	
	public Image getDynamicPP() {
		return this.dynamicPP;
	}
	
	public Image getDynamicPPP() {
		return this.dynamicPPP;
	}
	
	public Image getEditModeEdition() {
		return this.editModeEdition;
	}
	
	public Image getEditModeEditionNotNatural() {
		return this.editModeEditionNotNatural;
	}
	
	public Image getEditModeSelection() {
		return this.editModeSelection;
	}
	
	public Image getEditRedo() {
		return this.editRedo;
	}
	
	public Image getEditUndo() {
		return this.editUndo;
	}
	
	public Image getEditVoice1() {
		return this.editVoice1;
	}
	
	public Image getEditVoice2() {
		return this.editVoice2;
	}
	
	public Image getEffectAccentuated() {
		return this.effectAccentuated;
	}
	
	public Image getEffectBend() {
		return this.effectBend;
	}
	
	public Image getEffectDead() {
		return this.effectDead;
	}
	
	public Image getEffectFadeIn() {
		return this.effectFadeIn;
	}
	
	public Image getEffectGhost() {
		return this.effectGhost;
	}
	
	public Image getEffectGrace() {
		return this.effectGrace;
	}
	
	public Image getEffectHammer() {
		return this.effectHammer;
	}
	
	public Image getEffectHarmonic() {
		return this.effectHarmonic;
	}
	
	public Image getEffectHeavyAccentuated() {
		return this.effectHeavyAccentuated;
	}
	
	public Image getEffectPalmMute() {
		return this.effectPalmMute;
	}
	
	public Image getEffectPopping() {
		return this.effectPopping;
	}
	
	public Image getEffectSlapping() {
		return this.effectSlapping;
	}
	
	public Image getEffectSlide() {
		return this.effectSlide;
	}
	
	public Image getEffectStaccato() {
		return this.effectStaccato;
	}
	
	public Image getEffectTapping() {
		return this.effectTapping;
	}
	
	public Image getEffectTremoloBar() {
		return this.effectTremoloBar;
	}
	
	public Image getEffectTremoloPicking() {
		return this.effectTremoloPicking;
	}
	
	public Image getEffectTrill() {
		return this.effectTrill;
	}
	
	public Image getEffectVibrato() {
		return this.effectVibrato;
	}
	
	public Image getFileNew() {
		return this.fileNew;
	}
	
	public Image getFileOpen() {
		return this.fileOpen;
	}
	
	public Image getFilePrint() {
		return this.filePrint;
	}
	
	public Image getFilePrintPreview() {
		return this.filePrintPreview;
	}
	
	public Image getFileSave() {
		return this.fileSave;
	}
	
	public Image getFileSaveAs() {
		return this.fileSaveAs;
	}
	
	public Image getFretboard() {
		return this.fretboard;
	}
	
	public Image getFretboardFirstFret() {
		return this.fretboardFirstFret;
	}
	
	public Image getFretboardFret() {
		return this.fretboardFret;
	}
	
	public Image getLayoutLinear() {
		return this.layoutLinear;
	}
	
	public Image getLayoutMultitrack() {
		return this.layoutMultitrack;
	}
	
	public Image getLayoutPage() {
		return this.layoutPage;
	}
	
	public Image getLayoutScore() {
		return this.layoutScore;
	}
	
	public Image getLayoutCompact() {
		return this.layoutCompact;
	}
	
	public Image getMarkerAdd() {
		return this.markerAdd;
	}
	
	public Image getMarkerFirst() {
		return this.markerFirst;
	}
	
	public Image getMarkerLast() {
		return this.markerLast;
	}
	
	public Image getMarkerList() {
		return this.markerList;
	}
	
	public Image getMarkerNext() {
		return this.markerNext;
	}
	
	public Image getMarkerPrevious() {
		return this.markerPrevious;
	}
	
	public Image getMarkerRemove() {
		return this.markerRemove;
	}
	
	public Image getMixer() {
		return this.mixer;
	}
	
	public Image getNoteTied() {
		return this.noteTied;
	}
	
	public Image getOptionLanguage() {
		return this.optionLanguage;
	}
	
	public Image getOptionMain() {
		return this.optionMain;
	}
	
	public Image getOptionSound() {
		return this.optionSound;
	}
	
	public Image getOptionStyle() {
		return this.optionStyle;
	}
	
	public Image getOptionToolbars() {
		return this.optionToolbars;
	}
	
	public Image getOptionSkin() {
		return this.optionSkin;
	}
	
	public Image getPaintableGrace() {
		return this.paintableGrace;
	}
	
	public Image getPaintableTempo() {
		return this.paintableTempo;
	}
	
	public Image getSongProperties() {
		return this.songProperties;
	}
	
	public Image getTrackAdd() {
		return this.trackAdd;
	}
	
	public Image getTrackRemove() {
		return this.trackRemove;
	}
	
	public Image getTransport() {
		return this.transport;
	}
	
	public Image getTransportFirst1() {
		return this.transportFirst1;
	}
	
	public Image getTransportFirst2() {
		return this.transportFirst2;
	}
	
	public Image getTransportIconFirst1() {
		return this.transportIconFirst1;
	}
	
	public Image getTransportIconFirst2() {
		return this.transportIconFirst2;
	}
	
	public Image getTransportIconLast1() {
		return this.transportIconLast1;
	}
	
	public Image getTransportIconLast2() {
		return this.transportIconLast2;
	}
	
	public Image getTransportIconNext1() {
		return this.transportIconNext1;
	}
	
	public Image getTransportIconNext2() {
		return this.transportIconNext2;
	}
	
	public Image getTransportIconPause() {
		return this.transportIconPause;
	}
	
	public Image getTransportIconPlay1() {
		return this.transportIconPlay1;
	}
	
	public Image getTransportIconPlay2() {
		return this.transportIconPlay2;
	}
	
	public Image getTransportIconPrevious1() {
		return this.transportIconPrevious1;
	}
	
	public Image getTransportIconPrevious2() {
		return this.transportIconPrevious2;
	}
	
	public Image getTransportIconStop1() {
		return this.transportIconStop1;
	}
	
	public Image getTransportIconStop2() {
		return this.transportIconStop2;
	}
	
	public Image getTransportLast1() {
		return this.transportLast1;
	}
	
	public Image getTransportLast2() {
		return this.transportLast2;
	}
	
	public Image getTransportNext1() {
		return this.transportNext1;
	}
	
	public Image getTransportNext2() {
		return this.transportNext2;
	}
	
	public Image getTransportPause() {
		return this.transportPause;
	}
	
	public Image getTransportPlay1() {
		return this.transportPlay1;
	}
	
	public Image getTransportPlay2() {
		return this.transportPlay2;
	}
	
	public Image getTransportPrevious1() {
		return this.transportPrevious1;
	}
	
	public Image getTransportPrevious2() {
		return this.transportPrevious2;
	}
	
	public Image getTransportStop1() {
		return this.transportStop1;
	}
	
	public Image getTransportStop2() {
		return this.transportStop2;
	}
	
	public Image getTransportMetronome() {
		return this.transportMetronome;
	}
	
	public Image getTransportMode() {
		return this.transportMode;
	}
	
	public Image getBrowserBack() {
		return this.browserBack;
	}
	
	public Image getBrowserFile() {
		return this.browserFile;
	}
	
	public Image getBrowserFolder() {
		return this.browserFolder;
	}
	
	public Image getBrowserRefresh() {
		return this.browserRefresh;
	}
	
	public Image getBrowserRoot() {
		return this.browserRoot;
	}
	
	public Image getBrowserNew() {
		return this.browserNew;
	}
	
	public Image getSettings() {
		return this.settings;
	}
	
}
