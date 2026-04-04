#!/bin/bash

# Exit on error
set -e
# Print commands
#set -x

COMMAND=`basename $0`

THIS_DIR=`dirname $(realpath "$0")`
TG=TuxGuitar
DT=KDE
DT_URL=https://invent.kde.org/frameworks/breeze-icons/-/raw/master
SKIN=Breeze
DARK=$SKIN-dark
SKIN_DIR=`realpath $THIS_DIR/../../share/skins/$SKIN`
DARK_DIR=`realpath $THIS_DIR/../../share/skins/$DARK`

# Icon sizes: Breeze/KDE icons are aligned on a 22x22 grid, so we can use this resolution for the PNG images
S_ICON=22x22     # Size of icons in menus and toolbars
S_PLAY=36x36     # Size of icons in player
S_INFO=64x64     # Size of icons in warnings and settings dialog
S_TRAC=16x16     # Size of icons in track table

# Icon colors
C_DFLT=#232629   # Default color of Breeze icons
C_NORM=$C_DFLT   # Color of icons for TuxGuitar in normal mode
C_N_HI=20%       # Modify brightness for highlighted icons, normal mode
C_DARK=#eff0f1   # Color of icons for TuxGuitar in dark mode
C_D_HI=-20%      # Modify brightness for highlighted icons, dark mode

declare -A ICONS=(
  # Icon path/name.svg -convert-> width1xheight1:TuxGuitar_icon1.png width2xheight2+margin2:TuxGuitar_icon2.png width3xheight3+margin3-H:TuxGuitar_icon3.png
  # Icon path/file.xyz --copy---> $SKIN_DIR/file1.xyz                $DARK_DIR/file2.xyz

  ["$TG/icon.png"]="$SKIN_DIR/icon.png $DARK_DIR/icon.png"
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
  echo "Generate the skins $SKIN and $DARK from the SVG images in the directory"
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
for file in COPYING-ICONS COPYING.LIB; do
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
  sed -e "s/$C_DFLT/$OUT_COLR/g" $THIS_DIR/$icon | rsvg-convert --width=$width --height=$height --page-width=$page_width --page-height=$page_height --left=$margin --top=$margin > $OUT_FILE
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
    icon_url=$DT_URL/icons/${BASH_REMATCH[1]}
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
        OUT_COLR=$C_NORM
        OUT_HILI=$C_N_HI
        OUT_FILE=$SKIN_DIR/$png_icon
        convert_svg_to_png
        # Dark skin
        OUT_COLR=$C_DARK
        OUT_HILI=$C_D_HI
        OUT_FILE=$DARK_DIR/$png_icon
        convert_svg_to_png
      else
        echo -e "\nError: Only SVG files can be converted to PNG icons."
        echo -e "\nAborting.\n"
        exit 1
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
