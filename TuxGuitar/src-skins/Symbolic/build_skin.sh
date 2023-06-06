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

declare -A ICONS=(
  # Icon path/name.svg -convert-> width1xheight1:TuxGuitar_icon1.png width2xheight2:TuxGuitar_icon2.png
  # Icon path/name.png -convert-> width1xheight1:TuxGuitar_icon1.png width2xheight2:TuxGuitar_icon2.png
  # Icon path/file.png --copy---> $SKIN_DIR/file1.png                $DARK_DIR/file2.png
  ["$TG/1.svg"]="24x24:1.png"
  ["$TG/2.svg"]="24x24:2.png"
  ["$TG/4.svg"]="24x24:4.png"
  ["$TG/8.svg"]="24x24:8.png"
  ["$TG/16.svg"]="24x24:16.png"
  ["$TG/32.svg"]="24x24:32.png"
  ["$TG/64.svg"]="24x24:64.png"
  ["$TG/layout_page.svg"]="20x20:layout_page.png"
  ["$TG/layout_linear.svg"]="20x20:layout_linear.png"
  ["$TG/layout_multitrack.svg"]="20x20:layout_multitrack.png"
  ["$TG/layout_score.svg"]="20x20:layout_score.png"
  ["$TG/layout_compact.svg"]="20x20:layout_compact.png"
  ["$TG/edit_voice_1.svg"]="24x24:edit_voice_1.png"
  ["$TG/edit_voice_2.svg"]="24x24:edit_voice_2.png"
  ["$TG/edit_mode_selection.svg"]="24x24:edit_mode_selection.png"
  ["$TG/edit_mode_edition_no_natural.svg"]="24x24:edit_mode_edition_no_natural.png"
  ["$TG/option_toolbars.svg"]="60x60:option_toolbars.png"
  ["$TG/timesignature.svg"]="24x24:timesignature.png"
  ["$DT/legacy/preferences-system-time-symbolic.svg"]="20x20:tempoicon.png"
  ["$TG/openrepeat.svg"]="24x24:openrepeat.png"
  ["$TG/closerepeat.svg"]="24x24:closerepeat.png"
  ["$TG/repeat_alternative.svg"]="24x24:repeat_alternative.png"
  ["$TG/dotted.svg"]="24x24:dotted.png"
  ["$TG/doubledotted.svg"]="24x24:doubledotted.png"
  ["$TG/division-type.svg"]="24x24:division-type.png"
  ["$TG/fretboard.svg"]="24x24:fretboard.png"
  ["$TG/firstfret.svg"]="1x50:firstfret.png"
  ["$TG/fret.svg"]="2x45:fret.png"
  ["$TG/chord.svg"]="24x24:chord.png"
  ["$TG/text.svg"]="20x20:text.png"
  ["$TG/tiednote.svg"]="24x24:tiednote.png"
  ["$TG/marker_remove.svg"]="20x20:marker_remove.png"
  ["$TG/mixer.svg"]="20x20:mixer.png"
  ["$TG/dynamic_ppp.svg"]="24x24:dynamic_ppp.png"
  ["$TG/dynamic_pp.svg"]="24x24:dynamic_pp.png"
  ["$TG/dynamic_p.svg"]="24x24:dynamic_p.png"
  ["$TG/dynamic_mp.svg"]="24x24:dynamic_mp.png"
  ["$TG/dynamic_mf.svg"]="24x24:dynamic_mf.png"
  ["$TG/dynamic_f.svg"]="24x24:dynamic_f.png"
  ["$TG/dynamic_ff.svg"]="24x24:dynamic_ff.png"
  ["$TG/dynamic_fff.svg"]="24x24:dynamic_fff.png"
  ["$TG/effect_dead.svg"]="24x24:effect_dead.png"
  ["$TG/effect_ghost.svg"]="24x24:effect_ghost.png"
  ["$TG/effect_accentuated.svg"]="24x24:effect_accentuated.png"
  ["$TG/effect_heavy_accentuated.svg"]="24x24:effect_heavy_accentuated.png"
  ["$TG/effect_harmonic.svg"]="24x24:effect_harmonic.png"
  ["$TG/effect_grace.svg"]="24x24:effect_grace.png"
  ["$TG/effect_bend.svg"]="24x24:effect_bend.png"
  ["$TG/effect_tremolo_bar.svg"]="24x24:effect_tremolo_bar.png"
  ["$TG/effect_slide.svg"]="24x24:effect_slide.png"
  ["$TG/effect_hammer.svg"]="24x24:effect_hammer.png"
  ["$TG/effect_vibrato.svg"]="24x24:effect_vibrato.png"
  ["$TG/effect_trill.svg"]="24x24:effect_trill.png"
  ["$TG/effect_tremolo_picking.svg"]="24x24:effect_tremolo_picking.png"
  ["$TG/effect_palm_mute.svg"]="24x24:effect_palm_mute.png"
  ["$TG/effect_staccato.svg"]="24x24:effect_staccato.png"
  ["$TG/effect_tapping.svg"]="24x24:effect_tapping.png"
  ["$TG/effect_slapping.svg"]="24x24:effect_slapping.png"
  ["$TG/effect_popping.svg"]="24x24:effect_popping.png"
  ["$TG/effect_fade_in.svg"]="24x24:effect_fade_in.png"
  ["$TG/stroke_up.svg"]="24x24:stroke_up.png"
  ["$TG/stroke_down.svg"]="24x24:stroke_down.png"
  ["$DT/categories/preferences-system-symbolic.svg"]="20x20:settings.png"
  ["$DT/actions/document-new-symbolic.svg"]="20x20:new.png"
  ["$DT/actions/document-open-symbolic.svg"]="20x20:open.png"
  ["$DT/actions/document-save-symbolic.svg"]="20x20:save.png"
  ["$DT/actions/document-save-as-symbolic.svg"]="20x20:save-as.png"
  ["$DT/actions/document-print-symbolic.svg"]="20x20:print.png"
  ["$DT/actions/document-print-preview-symbolic.svg"]="20x20:print-preview.png"
  ["$DT/actions/edit-undo-symbolic.svg"]="20x20:edit_undo.png"
  ["$DT/actions/edit-redo-symbolic.svg"]="20x20:edit_redo.png"
  ["$DT/actions/document-properties-symbolic.svg"]="20x20:song_properties.png"
  ["$DT/actions/list-add-symbolic.svg"]="20x20:list_add.png 20x20:track_add.png"
  ["$DT/actions/list-remove-symbolic.svg"]="20x20:list_remove.png 20x20:track_remove.png"
  ["$DT/actions/document-edit-symbolic.svg"]="20x20:list_edit.png 24x24:edit_mode_edition.png"
  ["$DT/ui/pan-up-symbolic.svg"]="20x20:list_move_up.png"
  ["$DT/ui/pan-down-symbolic.svg"]="20x20:list_move_down.png"
  ["$DT/actions/sidebar-show-symbolic.svg"]="20x20:toolbar_edit.png"
  ["$TG/sidebar-show-symbolic_rotated.svg"]="20x20:table_viewer.png"
  ["$DT/devices/multimedia-player-symbolic.svg"]="20x20:transport.png"
  ["$DT/actions/media-playback-start-symbolic.svg"]="32x32:transport_play_1.png 32x32:transport_play_2.png 20x20:transport_icon_play_1.png 20x20:transport_icon_play_2.png"
  ["$DT/actions/media-playback-pause-symbolic.svg"]="32x32:transport_pause.png 20x20:transport_icon_pause.png"
  ["$DT/actions/media-playback-stop-symbolic.svg"]="32x32:transport_stop_1.png 32x32:transport_stop_2.png 20x20:transport_icon_stop_1.png 20x20:transport_icon_stop_2.png"
  ["$DT/actions/media-seek-forward-symbolic.svg"]="32x32:transport_next_1.png 32x32:transport_next_2.png 20x20:transport_icon_next_1.png 20x20:transport_icon_next_2.png"
  ["$DT/actions/media-seek-backward-symbolic.svg"]="32x32:transport_previous_1.png 32x32:transport_previous_2.png 20x20:transport_icon_previous_1.png 20x20:transport_icon_previous_2.png"
  ["$DT/actions/media-skip-backward-symbolic.svg"]="32x32:transport_first_1.png 32x32:transport_first_2.png 20x20:transport_icon_first_1.png 20x20:transport_icon_first_2.png"
  ["$DT/actions/media-skip-forward-symbolic.svg"]="32x32:transport_last_1.png 32x32:transport_last_2.png 20x20:transport_icon_last_1.png 20x20:transport_icon_last_2.png"
  ["$DT/status/media-playlist-repeat-song-symbolic.svg"]="22x22:transport_mode.png"
  ["$DT/actions/bookmark-new-symbolic.svg"]="20x20:marker_add.png"
  ["$DT/places/user-bookmarks-symbolic.svg"]="20x20:marker_list.png 20x20:marker.png"
  ["$DT/actions/go-up-symbolic.svg"]="20x20:arrow_up.png"
  ["$DT/actions/go-down-symbolic.svg"]="20x20:arrow_down.png"
  ["$DT/actions/go-first-symbolic.svg"]="20x20:marker_first.png"
  ["$DT/actions/go-previous-symbolic.svg"]="20x20:marker_previous.png 20x20:browser_back.png 20x20:arrow_left.png"
  ["$DT/actions/go-next-symbolic.svg"]="20x20:marker_next.png 20x20:arrow_right.png"
  ["$DT/actions/go-last-symbolic.svg"]="20x20:marker_last.png"
  ["$DT/categories/preferences-other-symbolic.svg"]="60x60:option_view.png"
  ["$DT/legacy/preferences-desktop-font-symbolic.svg"]="60x60:option_style.png"
  ["$DT/legacy/preferences-desktop-locale-symbolic.svg"]="60x60:option_language.png"
  ["$DT/legacy/preferences-desktop-appearance-symbolic.svg"]="60x60:option_skin.png"
  ["$DT/devices/audio-speakers-symbolic.svg"]="60x60:option_sound.png 16x16:mute-disabled.png"
  ["$DT/actions/help-about-symbolic.svg"]="60x60:about_description.png"
  ["$DT/legacy/system-users-symbolic.svg"]="60x60:about_authors.png"
  ["$DT/legacy/accessories-dictionary-symbolic.svg"]="60x60:about_license.png"
  ["$DT/actions/folder-new-symbolic.svg"]="20x20:browser_new.png"
  ["$DT/actions/go-home-symbolic.svg"]="20x20:browser_root.png"
  ["$DT/actions/view-refresh-symbolic.svg"]="20x20:browser_refresh.png"
  ["$DT/mimetypes/audio-x-generic-symbolic.svg"]="20x20:browser_file.png"
  ["$DT/places/folder-symbolic.svg"]="20x20:browser_folder.png"
  ["$DT/status/dialog-error-symbolic.svg"]="64x64:status_error.png"
  ["$DT/status/dialog-information-symbolic.svg"]="64x64:status_info.png"
  ["$DT/status/dialog-question-symbolic.svg"]="64x64:status_question.png"
  ["$DT/status/dialog-warning-symbolic.svg"]="64x64:status_warning.png"
  ["$TG/transport_metronome.svg"]="22x22:transport_metronome.png"
  ["$DT/devices/audio-headphones-symbolic.svg"]="16x16:solo.png"
  ["$TG/audio-headphones-symbolic_dim.svg"]="16x16:solo-dim.png"
  ["$TG/audio-headphones-symbolic_disabled.svg"]="16x16:solo-disabled.png"
  ["$TG/audio-headphones-symbolic_disabled_dim.svg"]="16x16:solo-disabled-dim.png"
  ["$DT/status/audio-volume-muted-symbolic.svg"]="16x16:mute.png"
  ["$TG/audio-volume-muted-symbolic_dim.svg"]="16x16:mute-dim.png"
  ["$TG/audio-speakers-symbolic_dim.svg"]="16x16:mute-disabled-dim.png"
  ["$TG/logo.png"]="16x16:icon-16x16.png 24x24:icon-24x24.png 32x32:icon-32x32.png 48x48:icon-48x48.png 64x64:icon-64x64.png 96x96:icon-96x96.png 96x96:icon.png"
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

# Processing icons
for icon in "${!ICONS[@]}"; do

  echo "# Processing icon $icon:"

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
      echo -n "Converting to $SKIN_DIR/$png_icon ... "
      if [[ $icon =~ ^.+\.svg$ ]]; then
        rsvg-convert --stylesheet=$SKIN_CSS --width=$width --height=$height $THIS_DIR/$icon > $SKIN_DIR/$png_icon
        echo "done."
        echo -n "Converting to $DARK_DIR/$png_icon ... "
        rsvg-convert --stylesheet=$DARK_CSS --width=$width --height=$height $THIS_DIR/$icon > $DARK_DIR/$png_icon
        echo "done."
      elif [[ $icon =~ ^.+\.png$ ]]; then
        gm convert -scale $width"x"$height! $THIS_DIR/$icon $SKIN_DIR/$png_icon
        cp -a $SKIN_DIR/$png_icon $DARK_DIR
        echo "done."
      fi
    else
      echo -n "Copying to $out_icon ... "
      cp -a $icon $out_icon
      echo "done."
    fi
  done

  echo "# $icon done."
  echo

done
