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
SKIN_DIR=$THIS_DIR/../../share/skins/$SKIN
DARK_DIR=$THIS_DIR/../../share/skins/$DARK
SKIN_CSS=$THIS_DIR/build_style.css
DARK_CSS=$THIS_DIR/build_style_dark.css

S_ICON=20x20     # Size of icons in menues and toolbars
S_INFO=60x60     # Size of icons in warnings and settings dialog
S_LOGO=96x96     # Size of TuxGuitar logo

declare -A ICONS=(
  # Icon path/name.svg -convert-> width1xheight1:TuxGuitar_icon1.png width2xheight2:TuxGuitar_icon2.png
  # Icon path/name.png -convert-> width1xheight1:TuxGuitar_icon1.png width2xheight2:TuxGuitar_icon2.png
  # Icon path/file.png --copy---> $SKIN_DIR/file1.png                $DARK_DIR/file2.png
  ["$TG/1.svg"]="$S_ICON:1.png"
  ["$TG/2.svg"]="$S_ICON:2.png"
  ["$TG/4.svg"]="$S_ICON:4.png"
  ["$TG/8.svg"]="$S_ICON:8.png"
  ["$TG/16.svg"]="$S_ICON:16.png"
  ["$TG/32.svg"]="$S_ICON:32.png"
  ["$TG/64.svg"]="$S_ICON:64.png"
  ["$TG/layout_page.svg"]="$S_ICON:layout_page.png"
  ["$TG/layout_linear.svg"]="$S_ICON:layout_linear.png"
  ["$TG/layout_multitrack.svg"]="$S_ICON:layout_multitrack.png"
  ["$TG/layout_score.svg"]="$S_ICON:layout_score.png"
  ["$TG/layout_compact.svg"]="$S_ICON:layout_compact.png"
  ["$TG/edit_voice_1.svg"]="$S_ICON:edit_voice_1.png"
  ["$TG/edit_voice_2.svg"]="$S_ICON:edit_voice_2.png"
  ["$DT/actions/edit-select-symbolic.svg"]="$S_ICON:edit_mode_selection.png"
  ["$TG/edit_mode_edition_no_natural.svg"]="$S_ICON:edit_mode_edition_no_natural.png"
  ["$TG/timesignature.svg"]="$S_ICON:timesignature.png"
  ["$DT/legacy/preferences-system-time-symbolic.svg"]="$S_ICON:tempoicon.png"
  ["$TG/openrepeat.svg"]="$S_ICON:openrepeat.png"
  ["$TG/closerepeat.svg"]="$S_ICON:closerepeat.png"
  ["$TG/repeat_alternative.svg"]="$S_ICON:repeat_alternative.png"
  ["$TG/dotted.svg"]="$S_ICON:dotted.png"
  ["$TG/doubledotted.svg"]="$S_ICON:doubledotted.png"
  ["$TG/division-type.svg"]="$S_ICON:division-type.png"
  ["$TG/fretboard.svg"]="$S_ICON:fretboard.png"
  ["$TG/firstfret.svg"]="1x50:firstfret.png"
  ["$TG/fret.svg"]="2x45:fret.png"
  ["$TG/chord.svg"]="$S_ICON:chord.png"
  ["$TG/text.svg"]="$S_ICON:text.png"
  ["$TG/tiednote.svg"]="$S_ICON:tiednote.png"
  ["$TG/mixer.svg"]="$S_ICON:mixer.png"
  ["$TG/dynamic_ppp.svg"]="$S_ICON:dynamic_ppp.png"
  ["$TG/dynamic_pp.svg"]="$S_ICON:dynamic_pp.png"
  ["$TG/dynamic_p.svg"]="$S_ICON:dynamic_p.png"
  ["$TG/dynamic_mp.svg"]="$S_ICON:dynamic_mp.png"
  ["$TG/dynamic_mf.svg"]="$S_ICON:dynamic_mf.png"
  ["$TG/dynamic_f.svg"]="$S_ICON:dynamic_f.png"
  ["$TG/dynamic_ff.svg"]="$S_ICON:dynamic_ff.png"
  ["$TG/dynamic_fff.svg"]="$S_ICON:dynamic_fff.png"
  ["$TG/effect_dead.svg"]="$S_ICON:effect_dead.png"
  ["$TG/effect_ghost.svg"]="$S_ICON:effect_ghost.png"
  ["$TG/effect_accentuated.svg"]="$S_ICON:effect_accentuated.png"
  ["$TG/effect_heavy_accentuated.svg"]="$S_ICON:effect_heavy_accentuated.png"
  ["$TG/effect_let_ring.svg"]="$S_ICON:effect_let_ring.png"
  ["$TG/effect_harmonic.svg"]="$S_ICON:effect_harmonic.png"
  ["$TG/effect_grace.svg"]="$S_ICON:effect_grace.png"
  ["$TG/effect_bend.svg"]="$S_ICON:effect_bend.png"
  ["$TG/effect_tremolo_bar.svg"]="$S_ICON:effect_tremolo_bar.png"
  ["$TG/effect_slide.svg"]="$S_ICON:effect_slide.png"
  ["$TG/effect_hammer.svg"]="$S_ICON:effect_hammer.png"
  ["$TG/effect_vibrato.svg"]="$S_ICON:effect_vibrato.png"
  ["$TG/effect_trill.svg"]="$S_ICON:effect_trill.png"
  ["$TG/effect_tremolo_picking.svg"]="$S_ICON:effect_tremolo_picking.png"
  ["$TG/effect_palm_mute.svg"]="$S_ICON:effect_palm_mute.png"
  ["$TG/effect_staccato.svg"]="$S_ICON:effect_staccato.png"
  ["$TG/effect_tapping.svg"]="$S_ICON:effect_tapping.png"
  ["$TG/effect_slapping.svg"]="$S_ICON:effect_slapping.png"
  ["$TG/effect_popping.svg"]="$S_ICON:effect_popping.png"
  ["$TG/effect_fade_in.svg"]="$S_ICON:effect_fade_in.png"
  ["$TG/stroke_up.svg"]="$S_ICON:stroke_up.png"
  ["$TG/stroke_down.svg"]="$S_ICON:stroke_down.png"
  ["$DT/categories/preferences-system-symbolic.svg"]="$S_ICON:settings.png"
  ["$DT/actions/document-new-symbolic.svg"]="$S_ICON:new.png"
  ["$DT/actions/document-open-symbolic.svg"]="$S_ICON:open.png"
  ["$DT/ui/window-close-symbolic.svg"]="$S_ICON:close.png"
  ["$DT/actions/document-save-symbolic.svg"]="$S_ICON:save.png"
  ["$DT/actions/document-save-as-symbolic.svg"]="$S_ICON:save-as.png"
  ["$TG/document-revert-symbolic_import.svg"]="$S_ICON:import.png"
  ["$TG/document-revert-symbolic_export.svg"]="$S_ICON:export.png"
  ["$DT/actions/document-print-symbolic.svg"]="$S_ICON:print.png"
  ["$DT/actions/document-print-preview-symbolic.svg"]="$S_ICON:print-preview.png"
  ["$DT/actions/document-open-recent-symbolic.svg"]="$S_ICON:history.png"
  ["$DT/actions/application-exit-symbolic.svg"]="$S_ICON:exit.png"
  ["$DT/actions/edit-cut-symbolic.svg"]="$S_ICON:edit_cut.png"
  ["$DT/actions/edit-copy-symbolic.svg"]="$S_ICON:edit_copy.png"
  ["$DT/actions/edit-paste-symbolic.svg"]="$S_ICON:edit_paste.png"
  ["$TG/edit-paste-symbolic_starred.svg"]="$S_ICON:edit_repeat.png"
  ["$DT/actions/edit-undo-symbolic.svg"]="$S_ICON:edit_undo.png"
  ["$DT/actions/edit-redo-symbolic.svg"]="$S_ICON:edit_redo.png"
  ["$DT/actions/document-properties-symbolic.svg"]="$S_ICON:song_properties.png"
  ["$DT/actions/zoom-in-symbolic.svg"]="$S_ICON:zoom_in.png"
  ["$DT/actions/zoom-original-symbolic.svg"]="$S_ICON:zoom_original.png"
  ["$DT/actions/zoom-out-symbolic.svg"]="$S_ICON:zoom_out.png"
  ["$DT/actions/list-add-symbolic.svg"]="$S_ICON:list_add.png $S_ICON:track_add.png"
  ["$DT/actions/list-remove-symbolic.svg"]="$S_ICON:list_remove.png $S_ICON:track_remove.png 14x14:mute-disabled.png 14x14:mute-disabled-dim.png 14x14:solo-disabled.png 14x14:solo-disabled-dim.png"
  ["$DT/devices/audio-headphones-symbolic.svg"]="$S_ICON:track_solo.png 14x14:solo.png 14x14:solo-dim.png"
  ["$DT/status/audio-volume-muted-symbolic.svg"]="$S_ICON:track_mute.png 14x14:mute.png 14x14:mute-dim.png"
  ["$DT/actions/document-edit-symbolic.svg"]="$S_ICON:list_edit.png $S_ICON:edit_mode_edition.png"
  ["$DT/ui/pan-up-symbolic.svg"]="$S_ICON:list_move_up.png"
  ["$DT/ui/pan-down-symbolic.svg"]="$S_ICON:list_move_down.png"
  ["$TG/sidebar-show-symbolic_top.svg"]="$S_ICON:toolbar_main.png"
  ["$DT/actions/sidebar-show-symbolic.svg"]="$S_ICON:toolbar_edit.png"
  ["$TG/sidebar-show-symbolic_bottom.svg"]="$S_ICON:table_viewer.png"
  ["$DT/devices/multimedia-player-symbolic.svg"]="$S_ICON:transport.png"
  ["$DT/actions/media-playback-start-symbolic.svg"]="$S_ICON:transport_play_1.png $S_ICON:transport_play_2.png $S_ICON:transport_icon_play_1.png $S_ICON:transport_icon_play_2.png"
  ["$DT/actions/media-playback-pause-symbolic.svg"]="$S_ICON:transport_pause.png $S_ICON:transport_icon_pause.png"
  ["$DT/actions/media-playback-stop-symbolic.svg"]="$S_ICON:transport_stop_1.png $S_ICON:transport_stop_2.png $S_ICON:transport_icon_stop_1.png $S_ICON:transport_icon_stop_2.png"
  ["$DT/actions/media-seek-forward-symbolic.svg"]="$S_ICON:transport_next_1.png $S_ICON:transport_next_2.png $S_ICON:transport_icon_next_1.png $S_ICON:transport_icon_next_2.png"
  ["$DT/actions/media-seek-backward-symbolic.svg"]="$S_ICON:transport_previous_1.png $S_ICON:transport_previous_2.png $S_ICON:transport_icon_previous_1.png $S_ICON:transport_icon_previous_2.png"
  ["$DT/actions/media-skip-backward-symbolic.svg"]="$S_ICON:transport_first_1.png $S_ICON:transport_first_2.png $S_ICON:transport_icon_first_1.png $S_ICON:transport_icon_first_2.png"
  ["$DT/actions/media-skip-forward-symbolic.svg"]="$S_ICON:transport_last_1.png $S_ICON:transport_last_2.png $S_ICON:transport_icon_last_1.png $S_ICON:transport_icon_last_2.png"
  ["$DT/status/media-playlist-repeat-song-symbolic.svg"]="$S_ICON:transport_mode.png"
  ["$DT/actions/bookmark-new-symbolic.svg"]="$S_ICON:marker_add.png"
  ["$DT/places/user-bookmarks-symbolic.svg"]="$S_ICON:marker_list.png $S_ICON:marker.png"
  ["$DT/actions/go-top-symbolic.svg"]="$S_ICON:track_first.png"
  ["$DT/actions/go-bottom-symbolic.svg"]="$S_ICON:track_last.png"
  ["$DT/actions/go-up-symbolic.svg"]="$S_ICON:track_previous.png $S_ICON:arrow_up.png"
  ["$DT/actions/go-down-symbolic.svg"]="$S_ICON:track_next.png $S_ICON:arrow_down.png"
  ["$DT/actions/go-first-symbolic.svg"]="$S_ICON:marker_first.png"
  ["$DT/actions/go-previous-symbolic.svg"]="$S_ICON:marker_previous.png $S_ICON:browser_back.png $S_ICON:arrow_left.png"
  ["$DT/actions/go-next-symbolic.svg"]="$S_ICON:marker_next.png $S_ICON:arrow_right.png"
  ["$DT/actions/go-last-symbolic.svg"]="$S_ICON:marker_last.png"
  ["$DT/categories/preferences-other-symbolic.svg"]="$S_INFO:option_view.png $S_ICON:tools_settings.png"
  ["$DT/mimetypes/application-x-addon-symbolic.svg"]="$S_ICON:tools_plugins.png"
  ["$DT/legacy/preferences-desktop-keyboard-shortcuts-symbolic.svg"]="$S_ICON:tools_shortcuts.png"
  ["$DT/legacy/preferences-desktop-font-symbolic.svg"]="$S_INFO:option_style.png"
  ["$DT/legacy/preferences-desktop-locale-symbolic.svg"]="$S_INFO:option_language.png"
  ["$DT/legacy/preferences-desktop-appearance-symbolic.svg"]="$S_INFO:option_skin.png"
  ["$DT/devices/audio-speakers-symbolic.svg"]="$S_INFO:option_sound.png"
  ["$DT/apps/help-contents-symbolic.svg"]="$S_ICON:help_doc.png"
  ["$DT/actions/help-about-symbolic.svg"]="$S_INFO:about_description.png $S_ICON:help_about.png"
  ["$DT/legacy/system-users-symbolic.svg"]="$S_INFO:about_authors.png"
  ["$DT/legacy/accessories-dictionary-symbolic.svg"]="$S_INFO:about_license.png"
  ["$DT/actions/folder-new-symbolic.svg"]="$S_ICON:browser_new.png"
  ["$DT/actions/go-home-symbolic.svg"]="$S_ICON:browser_root.png"
  ["$DT/actions/view-refresh-symbolic.svg"]="$S_ICON:browser_refresh.png"
  ["$DT/mimetypes/audio-x-generic-symbolic.svg"]="$S_ICON:browser_file.png"
  ["$DT/places/folder-symbolic.svg"]="$S_ICON:browser_folder.png"
  ["$DT/status/dialog-error-symbolic.svg"]="$S_INFO:status_error.png"
  ["$DT/status/dialog-information-symbolic.svg"]="$S_INFO:status_info.png"
  ["$DT/status/dialog-question-symbolic.svg"]="$S_INFO:status_question.png"
  ["$DT/status/dialog-warning-symbolic.svg"]="$S_INFO:status_warning.png"
  ["$TG/transport_metronome.svg"]="$S_ICON:transport_metronome.png"
  ["$TG/logo.png"]="$S_LOGO:icon.png"
  ["$TG/splash.png"]="$SKIN_DIR/splash.png $DARK_DIR/splash.png"
  ["$TG/skin-preview.png"]="$SKIN_DIR/skin-preview.png"
  ["$TG/skin-preview-dark.png"]="$DARK_DIR/skin-preview.png"
  ["$TG/skin.info"]="$SKIN_DIR/skin.info"
  ["$TG/skin-dark.info"]="$DARK_DIR/skin.info"
  ["$TG/skin.prop"]="$SKIN_DIR/skin.prop $DARK_DIR/skin.prop"

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

# Processing files
for icon in "${!ICONS[@]}"; do

  echo "# Processing file $icon:"

  # Download $DT SVG icon
  if [[ $icon =~ ^$DT/(.+\.svg)$ ]] && ([ $opt_d ] || [ ! -e $THIS_DIR/$icon ]); then
    icon_url=$DT_URL/Adwaita/symbolic/${BASH_REMATCH[1]}
    echo -n "Downloading $DT icon from $icon_url ... "
    curl --silent --output $THIS_DIR/$icon --create-dirs $icon_url
    echo "done."
  fi

  # Convert icon to PNG (normal and dark)
  for out_icon in ${ICONS[$icon]}; do
    if [[ $out_icon =~ ([0-9]+)"x"([0-9]+)":"(.+) ]]; then
      width=${BASH_REMATCH[1]}
      height=${BASH_REMATCH[2]}
      png_icon=${BASH_REMATCH[3]}
      if [[ $icon =~ ^.+\.svg$ ]]; then
        echo -n "Converting to $SKIN_DIR/$png_icon ... "
        rsvg-convert --stylesheet=$SKIN_CSS --width=$width --height=$height $THIS_DIR/$icon > $SKIN_DIR/$png_icon
        echo "done."
        echo -n "Converting to $DARK_DIR/$png_icon ... "
        rsvg-convert --stylesheet=$DARK_CSS --width=$width --height=$height $THIS_DIR/$icon > $DARK_DIR/$png_icon
        echo "done."
      elif [[ $icon =~ ^.+\.png$ ]]; then
        echo -n "Converting to $SKIN_DIR/$png_icon ... "
        gm convert -scale $width"x"$height! $THIS_DIR/$icon $SKIN_DIR/$png_icon
        echo "done."
        echo -n "Converting to $DARK_DIR/$png_icon ... "
        cp -p $SKIN_DIR/$png_icon $DARK_DIR
        echo "done."
      fi
    else
      echo -n "Copying to $out_icon ... "
      cp -p $THIS_DIR/$icon $out_icon
      echo "done."
    fi
  done

  echo "# $icon done."
  echo

done
