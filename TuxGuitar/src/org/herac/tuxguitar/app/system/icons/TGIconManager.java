package org.herac.tuxguitar.app.system.icons;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import org.eclipse.swt.graphics.Image;
import org.herac.tuxguitar.app.system.config.TGConfigKeys;
import org.herac.tuxguitar.app.system.config.TGConfigManager;
import org.herac.tuxguitar.app.util.TGFileUtils;
import org.herac.tuxguitar.event.TGEventListener;
import org.herac.tuxguitar.event.TGEventManager;
import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.ui.resource.UIImage;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.singleton.TGSingletonFactory;
import org.herac.tuxguitar.util.singleton.TGSingletonUtil;

public class TGIconManager {
	
	private TGContext context;
	
	private TGIconTheme theme;
	private Map<String, TGIconTheme> themeCache;
	
	private UIImage[] durations;
	private UIImage editUndo;
	private UIImage editRedo;
	private UIImage editVoice1;
	private UIImage editVoice2;
	private UIImage editModeSelection;
	private UIImage editModeEdition;
	private UIImage editModeEditionNotNatural;
	private UIImage layoutPage;
	private UIImage layoutLinear;
	private UIImage layoutMultitrack;
	private UIImage layoutScore;
	private UIImage layoutCompact;
	private UIImage transport;
	private UIImage transportFirst1;
	private UIImage transportFirst2;
	private UIImage transportLast1;
	private UIImage transportLast2;
	private UIImage transportPrevious1;
	private UIImage transportPrevious2;
	private UIImage transportNext1;
	private UIImage transportNext2;
	private UIImage transportStop1;
	private UIImage transportStop2;
	private UIImage transportPlay1;
	private UIImage transportPlay2;
	private UIImage transportPause;
	private UIImage transportIconFirst1;
	private UIImage transportIconFirst2;
	private UIImage transportIconLast1;
	private UIImage transportIconLast2;
	private UIImage transportIconPrevious1;
	private UIImage transportIconPrevious2;
	private UIImage transportIconNext1;
	private UIImage transportIconNext2;
	private UIImage transportIconStop1;
	private UIImage transportIconStop2;
	private UIImage transportIconPlay1;
	private UIImage transportIconPlay2;
	private UIImage transportIconPause;
	private UIImage transportMetronome;
	private UIImage transportMode;
	private UIImage markerList;
	private UIImage markerAdd;
	private UIImage markerRemove;
	private UIImage markerFirst;
	private UIImage markerLast;
	private UIImage markerPrevious;
	private UIImage markerNext;
	private UIImage aboutDescription;
	private UIImage aboutLicense;
	private UIImage aboutAuthors;
	private UIImage appIcon;
	private UIImage appIcon16;
	private UIImage appIcon24;
	private UIImage appIcon32;
	private UIImage appIcon48;
	private UIImage appIcon64;
	private UIImage appIcon96;
	private UIImage appSplash;
	private UIImage optionMain;
	private UIImage optionStyle;
	private UIImage optionSound;
	private UIImage optionLanguage;
	private UIImage optionToolbars;
	private UIImage optionSkin;
	private UIImage trackAdd;
	private UIImage trackRemove;
	private UIImage fretboard;
	private UIImage fretboardFirstFret;
	private UIImage fretboardFret;
	private UIImage compositionTimeSignature;
	private UIImage compositionTempo;
	private UIImage compositionRepeatOpen;
	private UIImage compositionRepeatClose;
	private UIImage compositionRepeatAlternative;
	private UIImage songProperties;
	private UIImage durationDotted;
	private UIImage durationDoubleDotted;
	private UIImage divisionType;
	private UIImage fileNew;
	private UIImage fileOpen;
	private UIImage fileSave;
	private UIImage fileSaveAs;
	private UIImage filePrint;
	private UIImage filePrintPreview;
	private UIImage chord;
	private UIImage text;
	private UIImage noteTied;
	private UIImage instruments;
	private UIImage dynamicPPP;
	private UIImage dynamicPP;
	private UIImage dynamicP;
	private UIImage dynamicMP;
	private UIImage dynamicMF;
	private UIImage dynamicF;
	private UIImage dynamicFF;
	private UIImage dynamicFFF;
	private UIImage effectDead;
	private UIImage effectGhost;
	private UIImage effectAccentuated;
	private UIImage effectHeavyAccentuated;
	private UIImage effectHarmonic;
	private UIImage effectGrace;
	private UIImage effectBend;
	private UIImage effectTremoloBar;
	private UIImage effectSlide;
	private UIImage effectHammer;
	private UIImage effectVibrato;
	private UIImage effectTrill;
	private UIImage effectTremoloPicking;
	private UIImage effectPalmMute;
	private UIImage effectStaccato;
	private UIImage effectTapping;
	private UIImage effectSlapping;
	private UIImage effectPopping;
	private UIImage effectFadeIn;
	private UIImage browserNew;
	private UIImage browserFile;
	private UIImage browserFolder;
	private UIImage browserRoot;
	private UIImage browserBack;
	private UIImage browserRefresh;
	private UIImage arrowUp;
	private UIImage arrowDown;
	private UIImage arrowLeft;
	private UIImage arrowRight;
	private UIImage statusQuestion;
	private UIImage statusError;
	private UIImage statusWarning;
	private UIImage statusInfo;
	private UIImage strokeUp;
	private UIImage strokeDown;
	private UIImage settings;
	private UIImage toolbarEdit;
	
	private TGIconManager(TGContext context){
		this.context = context;
		this.themeCache = new HashMap<String, TGIconTheme>();
		this.loadIcons();
	}
	
	public void addLoader(TGEventListener listener){
		TGEventManager.getInstance(this.context).addListener(TGIconEvent.EVENT_TYPE, listener);
	}
	
	public void removeLoader(TGEventListener listener){
		TGEventManager.getInstance(this.context).removeListener(TGIconEvent.EVENT_TYPE, listener);
	}
	
	private void fireChanges(){
		TGEventManager.getInstance(this.context).fireEvent(new TGIconEvent());
	}
	
	public TGIconTheme findIconTheme(String theme) {
		if( this.themeCache.containsKey(theme) ) {
			return this.themeCache.get(theme);
		}
		
		this.themeCache.put(theme, new TGIconTheme(theme));
		
		return this.findIconTheme(theme);
	}
	
	public String findConfiguredThemeName() {
		return TGConfigManager.getInstance(this.context).getStringValue(TGConfigKeys.SKIN);
	}
	
	public boolean shouldReload(){
		return ( this.theme == null || !this.theme.getName().equals(findConfiguredThemeName()));
	}
	
	public void reloadIcons(){
		this.loadIcons();
		this.fireChanges();
	}
	
	public void loadIcons(){
		this.theme = this.findIconTheme(this.findConfiguredThemeName());
		this.durations = new UIImage[]{
			loadIcon("1.png"),
			loadIcon("2.png"),
			loadIcon("4.png"),
			loadIcon("8.png"),
			loadIcon("16.png"),
			loadIcon("32.png"),
			loadIcon("64.png")
		};
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
		this.appIcon16 = loadIcon("icon-16x16.png");
		this.appIcon24 = loadIcon("icon-24x24.png");
		this.appIcon32 = loadIcon("icon-32x32.png");
		this.appIcon48 = loadIcon("icon-48x48.png");
		this.appIcon64 = loadIcon("icon-64x64.png");
		this.appIcon96 = loadIcon("icon-96x96.png");
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
		this.text = loadIcon("text.png");
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
		this.instruments = loadIcon("mixer.png");
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
		this.arrowUp = loadIcon("arrow_up.png");
		this.arrowDown = loadIcon("arrow_down.png");
		this.arrowLeft = loadIcon("arrow_left.png");
		this.arrowRight = loadIcon("arrow_right.png");
		this.statusQuestion = loadIcon("status_question.png");
		this.statusError = loadIcon("status_error.png");
		this.statusWarning = loadIcon("status_warning.png");
		this.statusInfo = loadIcon("status_info.png");
		this.strokeUp = loadIcon("stroke_up.png");
		this.strokeDown = loadIcon("stroke_down.png");
		this.settings = loadIcon("settings.png");
		this.toolbarEdit = loadIcon("toolbar_edit.png");
	}
	
	private UIImage loadIcon(String name) {
		UIImage image = this.theme.getResource(name);
		if( image == null ) {
			image = TGFileUtils.loadImage2(this.context, this.theme.getName(), name);
			
			this.theme.setResource(name, image);
		}
		return image;
	}
	
	public void disposeThemes() {
		List<String> themes = new ArrayList<String>(this.themeCache.keySet());
		for(String theme : themes) {
			this.disposeTheme(this.themeCache.remove(theme));
		}
	}
	
	public void disposeTheme(TGIconTheme theme) {
		List<UIImage> uiImages = new ArrayList<UIImage>(theme.getResources().values());
		for(UIImage uiImage : uiImages) {
			uiImage.dispose();
		}
	}
	
	public void disposeIcons(){
		this.disposeThemes();
	}
	
	public UIImage getDuration(int value){
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

	public UIImage getAboutAuthors() {
		return this.aboutAuthors;
	}
	
	public UIImage getAboutDescription() {
		return this.aboutDescription;
	}
	
	public UIImage getAboutLicense() {
		return this.aboutLicense;
	}
	
	public UIImage getAppIcon() {
		return this.appIcon;
	}
	
	public UIImage getAppIcon16() {
		return this.appIcon16;
	}
	
	public UIImage getAppIcon24() {
		return this.appIcon24;
	}
	
	public UIImage getAppIcon32() {
		return this.appIcon32;
	}
	
	public UIImage getAppIcon48() {
		return this.appIcon48;
	}
	
	public UIImage getAppIcon64() {
		return this.appIcon64;
	}
	
	public UIImage getAppIcon96() {
		return this.appIcon96;
	}
	
	public UIImage getAppSplash() {
		return this.appSplash;
	}
	
	public UIImage getChord() {
		return this.chord;
	}
	
	public UIImage getText() {
		return this.text;
	}
	
	public UIImage getCompositionRepeatClose() {
		return this.compositionRepeatClose;
	}
	
	public UIImage getCompositionRepeatAlternative() {
		return this.compositionRepeatAlternative;
	}
	
	public UIImage getCompositionRepeatOpen() {
		return this.compositionRepeatOpen;
	}
	
	public UIImage getCompositionTempo() {
		return this.compositionTempo;
	}
	
	public UIImage getCompositionTimeSignature() {
		return this.compositionTimeSignature;
	}
	
	public UIImage getDurationDotted() {
		return this.durationDotted;
	}
	
	public UIImage getDurationDoubleDotted() {
		return this.durationDoubleDotted;
	}
	
	public UIImage getDivisionType() {
		return this.divisionType;
	}
	
	public UIImage getDynamicF() {
		return this.dynamicF;
	}
	
	public UIImage getDynamicFF() {
		return this.dynamicFF;
	}
	
	public UIImage getDynamicFFF() {
		return this.dynamicFFF;
	}
	
	public UIImage getDynamicMF() {
		return this.dynamicMF;
	}
	
	public UIImage getDynamicMP() {
		return this.dynamicMP;
	}
	
	public UIImage getDynamicP() {
		return this.dynamicP;
	}
	
	public UIImage getDynamicPP() {
		return this.dynamicPP;
	}
	
	public UIImage getDynamicPPP() {
		return this.dynamicPPP;
	}
	
	public UIImage getEditModeEdition() {
		return this.editModeEdition;
	}
	
	public UIImage getEditModeEditionNotNatural() {
		return this.editModeEditionNotNatural;
	}
	
	public UIImage getEditModeSelection() {
		return this.editModeSelection;
	}
	
	public UIImage getEditRedo() {
		return this.editRedo;
	}
	
	public UIImage getEditUndo() {
		return this.editUndo;
	}
	
	public UIImage getEditVoice1() {
		return this.editVoice1;
	}
	
	public UIImage getEditVoice2() {
		return this.editVoice2;
	}
	
	public UIImage getEffectAccentuated() {
		return this.effectAccentuated;
	}
	
	public UIImage getEffectBend() {
		return this.effectBend;
	}
	
	public UIImage getEffectDead() {
		return this.effectDead;
	}
	
	public UIImage getEffectFadeIn() {
		return this.effectFadeIn;
	}
	
	public UIImage getEffectGhost() {
		return this.effectGhost;
	}
	
	public UIImage getEffectGrace() {
		return this.effectGrace;
	}
	
	public UIImage getEffectHammer() {
		return this.effectHammer;
	}
	
	public UIImage getEffectHarmonic() {
		return this.effectHarmonic;
	}
	
	public UIImage getEffectHeavyAccentuated() {
		return this.effectHeavyAccentuated;
	}
	
	public UIImage getEffectPalmMute() {
		return this.effectPalmMute;
	}
	
	public UIImage getEffectPopping() {
		return this.effectPopping;
	}
	
	public UIImage getEffectSlapping() {
		return this.effectSlapping;
	}
	
	public UIImage getEffectSlide() {
		return this.effectSlide;
	}
	
	public UIImage getEffectStaccato() {
		return this.effectStaccato;
	}
	
	public UIImage getEffectTapping() {
		return this.effectTapping;
	}
	
	public UIImage getEffectTremoloBar() {
		return this.effectTremoloBar;
	}
	
	public UIImage getEffectTremoloPicking() {
		return this.effectTremoloPicking;
	}
	
	public UIImage getEffectTrill() {
		return this.effectTrill;
	}
	
	public UIImage getEffectVibrato() {
		return this.effectVibrato;
	}
	
	public UIImage getFileNew() {
		return this.fileNew;
	}
	
	public UIImage getFileOpen() {
		return this.fileOpen;
	}
	
	public UIImage getFilePrint() {
		return this.filePrint;
	}
	
	public UIImage getFilePrintPreview() {
		return this.filePrintPreview;
	}
	
	public UIImage getFileSave() {
		return this.fileSave;
	}
	
	public UIImage getFileSaveAs() {
		return this.fileSaveAs;
	}
	
	public UIImage getFretboard() {
		return this.fretboard;
	}
	
	public UIImage getFretboardFirstFret() {
		return this.fretboardFirstFret;
	}
	
	public UIImage getFretboardFret() {
		return this.fretboardFret;
	}
	
	public UIImage getLayoutLinear() {
		return this.layoutLinear;
	}
	
	public UIImage getLayoutMultitrack() {
		return this.layoutMultitrack;
	}
	
	public UIImage getLayoutPage() {
		return this.layoutPage;
	}
	
	public UIImage getLayoutScore() {
		return this.layoutScore;
	}
	
	public UIImage getLayoutCompact() {
		return this.layoutCompact;
	}
	
	public UIImage getMarkerAdd() {
		return this.markerAdd;
	}
	
	public UIImage getMarkerFirst() {
		return this.markerFirst;
	}
	
	public UIImage getMarkerLast() {
		return this.markerLast;
	}
	
	public UIImage getMarkerList() {
		return this.markerList;
	}
	
	public UIImage getMarkerNext() {
		return this.markerNext;
	}
	
	public UIImage getMarkerPrevious() {
		return this.markerPrevious;
	}
	
	public UIImage getMarkerRemove() {
		return this.markerRemove;
	}
	
	public UIImage getInstruments() {
		return this.instruments;
	}
	
	public UIImage getNoteTied() {
		return this.noteTied;
	}
	
	public UIImage getOptionLanguage() {
		return this.optionLanguage;
	}
	
	public UIImage getOptionMain() {
		return this.optionMain;
	}
	
	public UIImage getOptionSound() {
		return this.optionSound;
	}
	
	public UIImage getOptionStyle() {
		return this.optionStyle;
	}
	
	public UIImage getOptionToolbars() {
		return this.optionToolbars;
	}
	
	public UIImage getOptionSkin() {
		return this.optionSkin;
	}
	
	public UIImage getSongProperties() {
		return this.songProperties;
	}
	
	public UIImage getTrackAdd() {
		return this.trackAdd;
	}
	
	public UIImage getTrackRemove() {
		return this.trackRemove;
	}
	
	public UIImage getTransport() {
		return this.transport;
	}
	
	public UIImage getTransportFirst1() {
		return this.transportFirst1;
	}
	
	public UIImage getTransportFirst2() {
		return this.transportFirst2;
	}
	
	public UIImage getTransportIconFirst1() {
		return this.transportIconFirst1;
	}
	
	public UIImage getTransportIconFirst2() {
		return this.transportIconFirst2;
	}
	
	public UIImage getTransportIconLast1() {
		return this.transportIconLast1;
	}
	
	public UIImage getTransportIconLast2() {
		return this.transportIconLast2;
	}
	
	public UIImage getTransportIconNext1() {
		return this.transportIconNext1;
	}
	
	public UIImage getTransportIconNext2() {
		return this.transportIconNext2;
	}
	
	public UIImage getTransportIconPause() {
		return this.transportIconPause;
	}
	
	public UIImage getTransportIconPlay1() {
		return this.transportIconPlay1;
	}
	
	public UIImage getTransportIconPlay2() {
		return this.transportIconPlay2;
	}
	
	public UIImage getTransportIconPrevious1() {
		return this.transportIconPrevious1;
	}
	
	public UIImage getTransportIconPrevious2() {
		return this.transportIconPrevious2;
	}
	
	public UIImage getTransportIconStop1() {
		return this.transportIconStop1;
	}
	
	public UIImage getTransportIconStop2() {
		return this.transportIconStop2;
	}
	
	public UIImage getTransportLast1() {
		return this.transportLast1;
	}
	
	public UIImage getTransportLast2() {
		return this.transportLast2;
	}
	
	public UIImage getTransportNext1() {
		return this.transportNext1;
	}
	
	public UIImage getTransportNext2() {
		return this.transportNext2;
	}
	
	public UIImage getTransportPause() {
		return this.transportPause;
	}
	
	public UIImage getTransportPlay1() {
		return this.transportPlay1;
	}
	
	public UIImage getTransportPlay2() {
		return this.transportPlay2;
	}
	
	public UIImage getTransportPrevious1() {
		return this.transportPrevious1;
	}
	
	public UIImage getTransportPrevious2() {
		return this.transportPrevious2;
	}
	
	public UIImage getTransportStop1() {
		return this.transportStop1;
	}
	
	public UIImage getTransportStop2() {
		return this.transportStop2;
	}
	
	public UIImage getTransportMetronome() {
		return this.transportMetronome;
	}
	
	public UIImage getTransportMode() {
		return this.transportMode;
	}
	
	public UIImage getBrowserBack() {
		return this.browserBack;
	}
	
	public UIImage getBrowserFile() {
		return this.browserFile;
	}
	
	public UIImage getBrowserFolder() {
		return this.browserFolder;
	}
	
	public UIImage getBrowserRefresh() {
		return this.browserRefresh;
	}
	
	public UIImage getBrowserRoot() {
		return this.browserRoot;
	}
	
	public UIImage getBrowserNew() {
		return this.browserNew;
	}
	
	public UIImage getStrokeUp() {
		return this.strokeUp;
	}

	public UIImage getStrokeDown() {
		return this.strokeDown;
	}

	public UIImage getSettings() {
		return this.settings;
	}
	
	public UIImage getToolbarEdit() {
		return this.toolbarEdit;
	}
	
	public UIImage getArrowUp() {
		return this.arrowUp;
	}

	public UIImage getArrowDown() {
		return this.arrowDown;
	}

	public UIImage getArrowLeft() {
		return this.arrowLeft;
	}

	public UIImage getArrowRight() {
		return this.arrowRight;
	}

	public UIImage getStatusQuestion() {
		return statusQuestion;
	}

	public UIImage getStatusError() {
		return statusError;
	}

	public UIImage getStatusWarning() {
		return statusWarning;
	}

	public UIImage getStatusInfo() {
		return statusInfo;
	}

	public static TGIconManager getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGIconManager.class.getName(), new TGSingletonFactory<TGIconManager>() {
			public TGIconManager createInstance(TGContext context) {
				return new TGIconManager(context);
			}
		});
	}
}
