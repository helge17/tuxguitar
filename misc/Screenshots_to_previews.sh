#!/bin/bash

echo "# Generating skin previews and website image from the screenshots ..."

THIS_DIR=`dirname $(realpath "$0")`
TG_DIR=`dirname "$THIS_DIR"`

function gen_skin_preview {
  shot=$THIS_DIR/Screenshot_$skin.png
  pdst=$TG_DIR/desktop/TuxGuitar/share/skins/$skin/skin-preview.png
  echo "Skin $skin"
  gm convert $shot -crop 450x324 $psrc
  # Normally done with build_skin.sh
  cp -p $psrc $pdst
}

skin=Breeze
psrc=$TG_DIR/desktop/TuxGuitar/src-skins/Breeze/TuxGuitar/skin-preview.png
gen_skin_preview

skin=Breeze-dark
psrc=$TG_DIR/desktop/TuxGuitar/src-skins/Breeze/TuxGuitar/skin-preview-dark.png
gen_skin_preview

skin=Symbolic
psrc=$TG_DIR/desktop/TuxGuitar/src-skins/Symbolic/TuxGuitar/skin-preview.png
gen_skin_preview

skin=Symbolic-dark
psrc=$TG_DIR/desktop/TuxGuitar/src-skins/Symbolic/TuxGuitar/skin-preview-dark.png
gen_skin_preview

skin=Oxygen
psrc=$TG_DIR/desktop/TuxGuitar/src-skins/Oxygen/Oxygen/copy/skin-preview.png
gen_skin_preview

skin=Oxygen-dark
psrc=$TG_DIR/desktop/TuxGuitar/src-skins/Oxygen/Oxygen-dark/copy/skin-preview.png
gen_skin_preview

echo "Website snapshot"
TMPDIR=$(mktemp --directory)
origx=1176
origy=775
shiftx=197
shifty=95
finalx=$(expr $origx + $shiftx + $shiftx)
finaly=$(expr $origy + $shifty + $shifty)
gm convert -background transparent -extent $finalx"x"$finaly           $THIS_DIR/Screenshot_Oxygen.png                                                      $TMPDIR/Screenshot_Oxygen_extent.png
gm composite -geometry +$(expr $shiftx + 1)+$shifty                    $THIS_DIR/Screenshot_Symbolic-dark.png  $TMPDIR/Screenshot_Oxygen_extent.png         $TMPDIR/Screenshot_Oxygen+Symbolic-dark.png
gm composite -geometry +$(expr $shiftx \* 2 + 2)+$(expr $shifty \* 2)  $THIS_DIR/Screenshot_Breeze.png         $TMPDIR/Screenshot_Oxygen+Symbolic-dark.png  $TG_DIR/website/snapshot.png
rm -rf $TMPDIR

echo "# done."
