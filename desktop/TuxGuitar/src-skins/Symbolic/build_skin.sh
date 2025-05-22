#!/bin/bash

# Exit on error
set -e
# Print commands
#set -x

COMMAND=`basename $0`

THIS_DIR=`dirname $(realpath "$0")`
TG=TuxGuitar
DT=Gnome
DT_URL=https://gitlab.gnome.org/GNOME/adwaita-icon-theme/-/raw/master
SKIN=Symbolic
DARK=$SKIN-dark
SKIN_DIR=`realpath $THIS_DIR/../../share/skins/$SKIN`
DARK_DIR=`realpath $THIS_DIR/../../share/skins/$DARK`

# Icon sizes: Symbolic/Gnome icons are aligned on a 16x16 grid, so we keep this resolution for the PNG images to make them look sharp and add a 3px margin to get the size of 22x22px
S_ICOS=16x16+3   # Size of icons in menus and toolbars
# Icons shared with KDE/Breeze are aligned on a 22x22 grid
S_ICOB=22x22     # Size of icons in menus and toolbars
S_PLAY=36x36     # Size of icons in player
S_INFO=64x64     # Size of icons in warnings and settings dialog
S_LOGO=96x96     # Size of TuxGuitar logo
S_TRAC=16x16     # Size of icons in track table

# Icon colors are defined in the CSS files
SKIN_CSS=$THIS_DIR/build_style.css       # Color of icons for TuxGuitar in normal mode
C_N_HI=40%                               # Modify brightness for highlighted icons, normal mode
DARK_CSS=$THIS_DIR/build_style_dark.css  # Color of icons for TuxGuitar in dark mode
C_D_HI=-30%                              # Modify brightness for highlighted icons, dark mode

declare -A ICONS=(
  # Icon path/name.svg -convert-> width1xheight1:TuxGuitar_icon1.png width2xheight2+margin2:TuxGuitar_icon2.png width3xheight3+margin3-H:TuxGuitar_icon3.png
  # Icon path/name.png -convert-> width1xheight1:TuxGuitar_icon1.png width2xheight2:TuxGuitar_icon2.png
  # Icon path/file.xyz --copy---> $SKIN_DIR/file1.xyz                $DARK_DIR/file2.xyz
  ["$TG/1.svg"]="$S_ICOB:1.png"
  ["$TG/2.svg"]="$S_ICOB:2.png"
  ["$TG/2dotted.svg"]="$S_ICOB:2dotted.png"
  ["$TG/4.svg"]="$S_ICOB:4.png"
  ["$TG/4dotted.svg"]="$S_ICOB:4dotted.png"
  ["$TG/8.svg"]="$S_ICOB:8.png"
  ["$TG/8dotted.svg"]="$S_ICOB:8dotted.png"
  ["$TG/16.svg"]="$S_ICOB:16.png"
  ["$TG/32.svg"]="$S_ICOB:32.png"
  ["$TG/64.svg"]="$S_ICOB:64.png"
  ["$TG/layout_page.svg"]="$S_ICOS:layout_page.png"
  ["$TG/layout_linear.svg"]="$S_ICOS:layout_linear.png"
  ["$TG/layout_multitrack.svg"]="$S_ICOS:layout_multitrack.png"
  ["$TG/layout_score.svg"]="$S_ICOS:layout_score.png"
  ["$TG/layout_tablature.svg"]="$S_ICOS:layout_tablature.png"
  ["$TG/layout_compact.svg"]="$S_ICOS:layout_compact.png"
  ["$TG/highlight_played_beat.svg"]="$S_ICOS:highlight_played_beat.png"
  ["$TG/edit_voice_1.svg"]="$S_ICOB:edit_voice_1.png"
  ["$TG/edit_voice_2.svg"]="$S_ICOB:edit_voice_2.png"
  ["$DT/actions/edit-select-symbolic.svg"]="$S_ICOS:edit_mode_selection.png"
  ["$TG/edit_mode_edition_no_natural.svg"]="$S_ICOS:edit_mode_edition_no_natural.png"
  ["$TG/edit_mode_free.svg"]="$S_ICOS:edit_mode_free.png"
  ["$TG/timesignature.svg"]="$S_ICOB:timesignature.png"
  ["$DT/legacy/preferences-system-time-symbolic.svg"]="$S_ICOS:tempoicon.png"
  ["$TG/clef.svg"]="$S_ICOB:clef.png"
  ["$TG/keysignature.svg"]="$S_ICOB:keysignature.png"
  ["$TG/tripletfeel.svg"]="$S_ICOB:tripletfeel.png"
  ["$TG/openrepeat.svg"]="$S_ICOB:openrepeat.png"
  ["$TG/closerepeat.svg"]="$S_ICOB:closerepeat.png"
  ["$TG/repeat_alternative.svg"]="$S_ICOB:repeat_alternative.png"
  ["$TG/dotted.svg"]="$S_ICOB:dotted.png"
  ["$TG/doubledotted.svg"]="$S_ICOB:doubledotted.png"
  ["$TG/division-type-1.svg"]="$S_ICOB:division-type-1.png"
  ["$TG/division-type-3.svg"]="$S_ICOB:division-type-3.png"
  ["$TG/division-type-5.svg"]="$S_ICOB:division-type-5.png"
  ["$TG/division-type-6.svg"]="$S_ICOB:division-type-6.png"
  ["$TG/division-type-7.svg"]="$S_ICOB:division-type-7.png"
  ["$TG/division-type-9.svg"]="$S_ICOB:division-type-9.png"
  ["$TG/division-type-10.svg"]="$S_ICOB:division-type-10.png"
  ["$TG/division-type-11.svg"]="$S_ICOB:division-type-11.png"
  ["$TG/division-type-12.svg"]="$S_ICOB:division-type-12.png"
  ["$TG/division-type-13.svg"]="$S_ICOB:division-type-13.png"
  ["$TG/fretboard.svg"]="$S_ICOS:fretboard.png"
  ["$TG/fretboard_smaller.svg"]="$S_ICOS:fretboard_smaller.png"
  ["$TG/fretboard_bigger.svg"]="$S_ICOS:fretboard_bigger.png"
  ["$TG/firstfret.svg"]="5x50:firstfret.png"
  ["$TG/fret.svg"]="2x45:fret.png"
  ["$TG/chord.svg"]="$S_ICOB:chord.png"
  ["$TG/text.svg"]="$S_ICOB:text.png"
  ["$TG/tiednote.svg"]="$S_ICOB:tiednote.png"
  ["$TG/mixer.svg"]="$S_ICOS:mixer.png"
  ["$TG/dynamic_ppp.svg"]="$S_ICOB:dynamic_ppp.png"
  ["$TG/dynamic_pp.svg"]="$S_ICOB:dynamic_pp.png"
  ["$TG/dynamic_p.svg"]="$S_ICOB:dynamic_p.png"
  ["$TG/dynamic_mp.svg"]="$S_ICOB:dynamic_mp.png"
  ["$TG/dynamic_mf.svg"]="$S_ICOB:dynamic_mf.png"
  ["$TG/dynamic_f.svg"]="$S_ICOB:dynamic_f.png"
  ["$TG/dynamic_ff.svg"]="$S_ICOB:dynamic_ff.png"
  ["$TG/dynamic_fff.svg"]="$S_ICOB:dynamic_fff.png"
  ["$TG/effect_dead.svg"]="$S_ICOB:effect_dead.png"
  ["$TG/effect_ghost.svg"]="$S_ICOB:effect_ghost.png"
  ["$TG/effect_accentuated.svg"]="$S_ICOB:effect_accentuated.png"
  ["$TG/effect_heavy_accentuated.svg"]="$S_ICOB:effect_heavy_accentuated.png"
  ["$TG/effect_let_ring.svg"]="$S_ICOB:effect_let_ring.png"
  ["$TG/effect_harmonic.svg"]="$S_ICOB:effect_harmonic.png"
  ["$TG/effect_grace.svg"]="$S_ICOB:effect_grace.png"
  ["$TG/effect_bend.svg"]="$S_ICOB:effect_bend.png"
  ["$TG/effect_tremolo_bar.svg"]="$S_ICOB:effect_tremolo_bar.png"
  ["$TG/effect_slide.svg"]="$S_ICOB:effect_slide.png"
  ["$TG/effect_hammer.svg"]="$S_ICOB:effect_hammer.png"
  ["$TG/effect_vibrato.svg"]="$S_ICOB:effect_vibrato.png"
  ["$TG/effect_trill.svg"]="$S_ICOB:effect_trill.png"
  ["$TG/effect_tremolo_picking.svg"]="$S_ICOB:effect_tremolo_picking.png"
  ["$TG/effect_palm_mute.svg"]="$S_ICOB:effect_palm_mute.png"
  ["$TG/effect_staccato.svg"]="$S_ICOB:effect_staccato.png"
  ["$TG/effect_tapping.svg"]="$S_ICOB:effect_tapping.png"
  ["$TG/effect_slapping.svg"]="$S_ICOB:effect_slapping.png"
  ["$TG/effect_popping.svg"]="$S_ICOB:effect_popping.png"
  ["$TG/effect_fade_in.svg"]="$S_ICOB:effect_fade_in.png"
  ["$TG/stroke_up.svg"]="$S_ICOB:stroke_up.png"
  ["$TG/stroke_down.svg"]="$S_ICOB:stroke_down.png"
  ["$TG/pick_stroke_up.svg"]="$S_ICOB:pick_stroke_up.png"
  ["$TG/pick_stroke_down.svg"]="$S_ICOB:pick_stroke_down.png"
  ["$TG/alt_enharmonic.svg"]="$S_ICOB:alt_enharmonic.png"
  ["$DT/categories/preferences-system-symbolic.svg"]="$S_ICOS:settings.png"
  ["$DT/actions/document-new-symbolic.svg"]="$S_ICOS:new.png"
  ["$DT/actions/document-open-symbolic.svg"]="$S_ICOS:open.png"
  ["$DT/ui/window-close-symbolic.svg"]="$S_ICOS:close.png"
  ["$DT/actions/document-save-symbolic.svg"]="$S_ICOS:save.png"
  ["$DT/actions/document-save-as-symbolic.svg"]="$S_ICOS:save-as.png"
  ["$TG/document-revert-symbolic_import.svg"]="$S_ICOS:import.png"
  ["$TG/document-revert-symbolic_export.svg"]="$S_ICOS:export.png"
  ["$DT/actions/document-print-symbolic.svg"]="$S_ICOS:print.png"
  ["$DT/actions/document-print-preview-symbolic.svg"]="$S_ICOS:print-preview.png"
  ["$DT/actions/document-open-recent-symbolic.svg"]="$S_ICOS:history.png"
  ["$DT/actions/application-exit-symbolic.svg"]="$S_ICOS:exit.png"
  ["$DT/actions/edit-cut-symbolic.svg"]="$S_ICOS:edit_cut.png"
  ["$DT/actions/edit-copy-symbolic.svg"]="$S_ICOS:edit_copy.png $S_ICOS:measure_copy.png $S_ICOS:track_clone.png"
  ["$DT/actions/edit-paste-symbolic.svg"]="$S_ICOS:edit_paste.png $S_ICOS:measure_paste.png"
  ["$DT/actions/edit-clear-symbolic.svg"]="$S_ICOS:measure_clean.png"
  ["$TG/edit-paste-symbolic_starred.svg"]="$S_ICOS:edit_repeat.png"
  ["$DT/actions/edit-undo-symbolic.svg"]="$S_ICOS:edit_undo.png"
  ["$DT/actions/edit-redo-symbolic.svg"]="$S_ICOS:edit_redo.png"
  ["$DT/actions/document-properties-symbolic.svg"]="$S_ICOS:song_properties.png"
  ["$DT/actions/zoom-in-symbolic.svg"]="$S_ICOS:zoom_in.png"
  ["$DT/actions/zoom-original-symbolic.svg"]="$S_ICOS:zoom_original.png"
  ["$DT/actions/zoom-out-symbolic.svg"]="$S_ICOS:zoom_out.png"
  ["$DT/actions/list-add-symbolic.svg"]="$S_ICOS:list_add.png $S_ICOS:track_add.png $S_ICOS:measure_add.png"
  ["$DT/actions/list-remove-symbolic.svg"]="$S_ICOS:list_remove.png $S_ICOS:track_remove.png $S_ICOS:measure_remove.png $S_TRAC-H:mute-disabled.png $S_TRAC:mute-disabled-dim.png $S_TRAC-H:solo-disabled.png $S_TRAC:solo-disabled-dim.png"
  ["$DT/devices/audio-headphones-symbolic.svg"]="$S_ICOS:track_solo.png $S_TRAC-H:solo.png $S_TRAC:solo-dim.png"
  ["$DT/status/audio-volume-muted-symbolic.svg"]="$S_ICOS:track_mute.png $S_TRAC-H:mute.png $S_TRAC:mute-dim.png"
  ["$DT/actions/document-edit-symbolic.svg"]="$S_ICOS:list_edit.png $S_ICOS:edit_mode_edition.png"
  ["$TG/sidebar-show-symbolic_top.svg"]="$S_ICOS:toolbar_main.png"
  ["$DT/actions/sidebar-show-symbolic.svg"]="$S_ICOS:toolbar_edit.png"
  ["$TG/sidebar-show-symbolic_bottom.svg"]="$S_ICOS:table_viewer.png"
  ["$DT/devices/multimedia-player-symbolic.svg"]="$S_ICOS:transport.png"
  ["$DT/actions/media-playback-start-symbolic.svg"]="$S_PLAY:transport_play.png $S_ICOS:transport_icon_play.png"
  ["$DT/actions/media-playback-pause-symbolic.svg"]="$S_PLAY:transport_pause.png $S_ICOS:transport_icon_pause.png"
  ["$DT/actions/media-playback-stop-symbolic.svg"]="$S_PLAY:transport_stop.png $S_ICOS:transport_icon_stop.png"
  ["$DT/actions/media-seek-forward-symbolic.svg"]="$S_PLAY:transport_next.png $S_ICOS:transport_icon_next.png"
  ["$DT/actions/media-seek-backward-symbolic.svg"]="$S_PLAY:transport_previous.png $S_ICOS:transport_icon_previous.png"
  ["$DT/actions/media-skip-backward-symbolic.svg"]="$S_PLAY:transport_first.png $S_ICOS:transport_icon_first.png"
  ["$DT/actions/media-skip-forward-symbolic.svg"]="$S_PLAY:transport_last.png $S_ICOS:transport_icon_last.png"
  ["$DT/status/media-playlist-repeat-song-symbolic.svg"]="$S_ICOS:transport_mode.png"
  ["$TG/media-playlist-repeat-song-symbolic_start.svg"]="$S_ICOS:transport_loop_start.png"
  ["$TG/media-playlist-repeat-song-symbolic_end.svg"]="$S_ICOS:transport_loop_end.png"
  ["$DT/actions/bookmark-new-symbolic.svg"]="$S_ICOS:marker_add.png"
  ["$DT/places/user-bookmarks-symbolic.svg"]="$S_ICOS:marker_list.png $S_ICOS:marker.png"
  ["$DT/actions/go-top-symbolic.svg"]="$S_ICOS:track_first.png"
  ["$DT/actions/go-bottom-symbolic.svg"]="$S_ICOS:track_last.png"
  ["$DT/actions/go-up-symbolic.svg"]="$S_ICOS:track_previous.png"
  ["$DT/actions/go-down-symbolic.svg"]="$S_ICOS:track_next.png"
  ["$DT/ui/pan-up-symbolic.svg"]="$S_ICOS:arrow_up.png"
  ["$DT/ui/pan-down-symbolic.svg"]="$S_ICOS:arrow_down.png"
  ["$DT/actions/go-first-symbolic.svg"]="$S_ICOS:marker_first.png $S_ICOS:measure_first.png"
  ["$DT/actions/go-previous-symbolic.svg"]="$S_ICOS:marker_previous.png $S_ICOS:measure_previous.png $S_ICOS:browser_back.png $S_ICOS:arrow_left.png"
  ["$DT/actions/go-next-symbolic.svg"]="$S_ICOS:marker_next.png $S_ICOS:measure_next.png $S_ICOS:arrow_right.png"
  ["$DT/actions/go-last-symbolic.svg"]="$S_ICOS:marker_last.png $S_ICOS:measure_last.png"
  ["$TG/measure_status_check.svg"]="$S_ICOS:measure_status_check.png"
  ["$DT/actions/object-select-symbolic.svg"]="$S_ICOS:measure_status_ok.png"
  ["$DT/actions/process-stop-symbolic.svg"]="$S_ICOS:measure_status_error.png"
  ["$DT/categories/preferences-other-symbolic.svg"]="$S_INFO:option_view.png $S_ICOS:tools_settings.png"
  ["$DT/mimetypes/application-x-addon-symbolic.svg"]="$S_ICOS:tools_plugins.png"
  ["$DT/legacy/preferences-desktop-keyboard-shortcuts-symbolic.svg"]="$S_ICOS:tools_shortcuts.png"
  ["$DT/legacy/preferences-desktop-font-symbolic.svg"]="$S_INFO:option_style.png"
  ["$DT/legacy/preferences-desktop-locale-symbolic.svg"]="$S_INFO:option_language.png"
  ["$DT/legacy/preferences-desktop-appearance-symbolic.svg"]="$S_INFO:option_skin.png"
  ["$DT/devices/audio-speakers-symbolic.svg"]="$S_INFO:option_sound.png"
  ["$DT/apps/help-contents-symbolic.svg"]="$S_ICOS:help_doc.png"
  ["$DT/actions/help-about-symbolic.svg"]="$S_INFO:about_description.png $S_ICOS:help_about.png"
  ["$DT/legacy/system-users-symbolic.svg"]="$S_INFO:about_authors.png"
  ["$DT/legacy/accessories-dictionary-symbolic.svg"]="$S_INFO:about_license.png"
  ["$DT/actions/folder-new-symbolic.svg"]="$S_ICOS:browser_new.png"
  ["$DT/actions/go-home-symbolic.svg"]="$S_ICOS:browser_root.png"
  ["$DT/actions/view-refresh-symbolic.svg"]="$S_ICOS:browser_refresh.png"
  ["$DT/actions/view-grid-symbolic.svg"]="$S_ICOS:matrix.png"
  ["$TG/sidebar-show-symbolic_piano.svg"]="$S_ICOS:piano.png"
  ["$DT/mimetypes/audio-x-generic-symbolic.svg"]="$S_ICOS:browser_file.png"
  ["$DT/places/folder-symbolic.svg"]="$S_ICOS:browser_folder.png"
  ["$DT/places/folder-remote-symbolic.svg"]="$S_ICOS:browser_folder_remote.png"
  ["$DT/places/folder-music-symbolic.svg"]="$S_ICOS:browser_collection.png"
  ["$DT/status/dialog-error-symbolic.svg"]="$S_INFO:status_error.png"
  ["$DT/status/dialog-information-symbolic.svg"]="$S_INFO:status_info.png"
  ["$DT/status/dialog-question-symbolic.svg"]="$S_INFO:status_question.png"
  ["$DT/status/dialog-warning-symbolic.svg"]="$S_INFO:status_warning.png"
  ["$TG/transport_metronome.svg"]="$S_ICOS:transport_metronome.png"
  ["$TG/transport_count_in.svg"]="$S_ICOS:transport_count_in.png"
  ["$TG/logo.png"]="$S_LOGO:icon.png"
  ["$TG/splash.png"]="$SKIN_DIR/splash.png $DARK_DIR/splash.png"
  ["$TG/skin-preview.png"]="$SKIN_DIR/skin-preview.png"
  ["$TG/skin-preview-dark.png"]="$DARK_DIR/skin-preview.png"
  ["$TG/skin.info"]="$SKIN_DIR/skin.info"
  ["$TG/skin-dark.info"]="$DARK_DIR/skin.info"
  ["$TG/skin.prop"]="$SKIN_DIR/skin.prop"
  ["$TG/skin-dark.prop"]="$DARK_DIR/skin.prop"

)

function usage {
  echo
  echo "Usage: $COMMAND [OPTIONS]"
  echo
  echo "Generate the skins $SKIN and $DARK from the SVG/PNG images in the directory"
  echo "  $THIS_DIR"
  echo "  The subfolder \"$DT\" contains the icons that are taken over from the $DT Desktop without modifications."
  echo "  The subfolder \"$TG\" contains icons and other files that are specific to $TG."
  echo "The skins will be written to"
  echo "  $SKIN_DIR"
  echo "  $DARK_DIR"
  echo "These two folders can be safely removed. They will be rebuilt the next time $COMMAND is called."
  echo
  echo "-d"
  echo "  Download/refresh all $DT SVG icons and license files from"
  echo "    $DT_URL"
  echo "  to"
  echo "    $THIS_DIR/$DT"
  echo "  Missing icons are downloaded even if -d is not specified."
  echo
  echo "-h"
  echo "  Display this help message and exit."
  echo
}

# Parse command line options
while getopts "dh" CMDopt
do
  case $CMDopt in
    d) opt_d=1;;
    *) usage
       [ $CMDopt == "h" ] && exit || exit 1
       ;;
  esac
done
# No argument should be left here
shift $((OPTIND-1))
if [ $1 ]; then
  echo "Unknown argument $1."
  usage
  exit 1
fi

mkdir -p $SKIN_DIR $DARK_DIR $THIS_DIR/$DT $THIS_DIR/$TG

# Download $DT license files
for file in COPYING COPYING_CCBYSA3 COPYING_LGPL; do
  if ([ $opt_d ] || [ ! -e $THIS_DIR/$DT/$file ]); then
    echo -n "Downloading $DT $file file from $DT_URL ... "
    curl --silent --output-dir $THIS_DIR/$DT --remote-name $DT_URL/$file
    echo "done."
    echo
  fi
done

# Convert single SVG icon to PNG
function convert_svg_to_png {
  echo -n "Converting to $OUT_FILE ... "
  rsvg-convert --stylesheet=$OUT_CSS --width=$width --height=$height --page-width=$page_width --page-height=$page_height --left=$margin --top=$margin $THIS_DIR/$icon > $OUT_FILE
  if [ "$hili" ]; then
    # Modify icon brightness (man convert)
    gm convert -operator all add $OUT_HILI $OUT_FILE $OUT_FILE.hili
    mv $OUT_FILE.hili $OUT_FILE
  fi
  echo "done."
}

# Processing input files
for icon in "${!ICONS[@]}"; do

  echo "# Processing file $icon:"

  # Download $DT SVG icon
  if [[ $icon =~ ^$DT/(.+\.svg)$ ]] && ([ $opt_d ] || [ ! -e $THIS_DIR/$icon ]); then
    icon_url=$DT_URL/Adwaita/symbolic/${BASH_REMATCH[1]}
    echo -n "Downloading $DT icon from $icon_url ... "
    curl --silent --output $THIS_DIR/$icon --create-dirs $icon_url
    echo "done."
  fi

  # Generate output files
  for out_icon in ${ICONS[$icon]}; do
    # Examples, $margin and $hili are  optional:
    # $out_icon          $width x   $height +   $margin   $hili :  $png_icon
    # 22x24+2:img1.png   22     x   24      +   2               :  img1.png
    # 26x28:img2.png     26     x   28                    -H    :  img2.png
    if [[ $out_icon =~ ([0-9]+)"x"([0-9]+)("+"([0-9]+))?("-H")?":"(.+) ]]; then
      width=${BASH_REMATCH[1]}
      height=${BASH_REMATCH[2]}
      margin=${BASH_REMATCH[4]}
      hili=${BASH_REMATCH[5]}
      png_icon=${BASH_REMATCH[6]}
      if [[ $icon =~ ^.+\.svg$ ]]; then
        # If no margin is given, set it to zero
        [ $margin ] || margin=0
        page_width=$(( $width + 2 * $margin ))
        page_height=$(( $height + 2 * $margin ))
        # Normal skin
        OUT_CSS=$SKIN_CSS
        OUT_HILI=$C_N_HI
        OUT_FILE=$SKIN_DIR/$png_icon
        convert_svg_to_png
        # Dark skin
        OUT_CSS=$DARK_CSS
        OUT_HILI=$C_D_HI
        OUT_FILE=$DARK_DIR/$png_icon
        convert_svg_to_png
      elif [[ $icon =~ ^.+\.png$ ]]; then
        echo -n "Converting to $SKIN_DIR/$png_icon ... "
        if [ $margin ]; then
          echo -e "\n\nError: Margin $margin given, but margin is not supported for PNGs."
          echo -e "\nAborting.\n"
          exit 1
        fi
        gm convert -scale $width"x"$height! $THIS_DIR/$icon $SKIN_DIR/$png_icon
        echo "done."
        echo -n "Converting to $DARK_DIR/$png_icon ... "
        cp -p $SKIN_DIR/$png_icon $DARK_DIR
        echo "done."
      fi
    elif [[ $out_icon =~ .+/.+ ]]; then
      echo -n "Copying to $out_icon ... "
      cp -p $THIS_DIR/$icon $out_icon
      echo "done."
    else
      echo -e "\nError: $out_icon does not contain a /, this is probably wrong."
      echo -e "\nAborting.\n"
      exit 1
    fi
  done

  echo "# $icon done."
  echo

done
