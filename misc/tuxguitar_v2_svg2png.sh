#!/bin/bash

echo "# Converting the TuxGuitar logo and splash SVG images to all needed formats and copying the files to the right places ..."

THIS_DIR=`dirname $(realpath "$0")`
TG_DIR=`dirname "$THIS_DIR"`

SPLASH=$THIS_DIR/tuxguitar_v2_splash_500x300.svg
LOGO=$THIS_DIR/tuxguitar_v2_logo_512x512.svg

# Logo

inkscape $LOGO   --export-filename=$TG_DIR/fastlane/metadata/android/en-US/images/icon.png

inkscape $LOGO   --export-filename=$TG_DIR/website/logo.png                                                                               --export-background-opacity=0 --export-width=155 --export-height=155

inkscape $LOGO   --export-filename=$TG_DIR/desktop/TuxGuitar/src-skins/Breeze/TuxGuitar/icon.png                                          --export-background-opacity=0 --export-width=96  --export-height=96
  cp -p                            $TG_DIR/desktop/TuxGuitar/src-skins/Breeze/TuxGuitar/icon.png $TG_DIR/desktop/build-scripts/common-resources/common-linux/share/pixmaps/tuxguitar.png
  cp -p                            $TG_DIR/desktop/TuxGuitar/src-skins/Breeze/TuxGuitar/icon.png $TG_DIR/desktop/TuxGuitar-synth-lv2/src/main/cxx/tuxguitar-synth-lv2.png
  convert                          $TG_DIR/desktop/TuxGuitar/src-skins/Breeze/TuxGuitar/icon.png                                          -alpha on -define icon:auto-resize="96,64,48,32,24,16" \
          $TG_DIR/website/favicon.ico
    cp -p $TG_DIR/website/favicon.ico $TG_DIR/desktop/build-scripts/common-resources/common-windows-installer-icons/TG_icon_install.ico
    cp -p $TG_DIR/website/favicon.ico $TG_DIR/desktop/build-scripts/common-resources/common-windows/dist/tuxguitar.ico

inkscape --export-filename=- --export-type=png --export-background-opacity=0 --export-width=48 --export-height=48 $TG_DIR/desktop/TuxGuitar/src-skins/Oxygen/KDE/scalable/actions/small/22x22/edit-delete.svgz | \
  gm composite -gravity SouthWest - $TG_DIR/desktop/TuxGuitar/src-skins/Breeze/TuxGuitar/icon.png - | \
  convert - -alpha on -define icon:auto-resize="96,64,48,32,24,16" $TG_DIR/desktop/build-scripts/common-resources/common-windows-installer-icons/TG_icon_uninstall.ico

inkscape $LOGO   --export-filename=$TG_DIR/desktop/build-scripts/common-resources/common-windows-installer-icons/TG_logo_48x48.png        --export-background-opacity=0 --export-width=48  --export-height=48

inkscape $LOGO   --export-filename=$TG_DIR/android/TuxGuitar-android/res/drawable-hdpi/ic_launcher.png                                    --export-background-opacity=0 --export-width=72  --export-height=72
inkscape $LOGO   --export-filename=$TG_DIR/android/TuxGuitar-android/res/drawable-mdpi/ic_launcher.png                                    --export-background-opacity=0 --export-width=48  --export-height=48
inkscape $LOGO   --export-filename=$TG_DIR/android/TuxGuitar-android/res/drawable-xhdpi/ic_launcher.png                                   --export-background-opacity=0 --export-width=96  --export-height=96
inkscape $LOGO   --export-filename=$TG_DIR/android/TuxGuitar-android/res/drawable-xxhdpi/ic_launcher.png                                  --export-background-opacity=0 --export-width=144 --export-height=144

TMPDIR=$(mktemp --directory --suffix -macos)
inkscape $LOGO   --export-filename=$TMPDIR/icon_320x320.png                                                                               --export-background-opacity=0 --export-width=320  --export-height=320
  magick -size 512x512 xc:transparent -fill 'gradient:#536586-#154374' -draw "roundrectangle 50,50 462,462 95,95" $TMPDIR/frame_512x512.png
  gm composite -gravity Center $TMPDIR/icon_320x320.png $TMPDIR/frame_512x512.png $TMPDIR/icns_512x512.png
    gm convert -scale 256x256 $TMPDIR/icns_512x512.png $TMPDIR/icns_256x256.png
    gm convert -scale 128x128 $TMPDIR/icns_512x512.png $TMPDIR/icns_128x128.png
    gm convert -scale 32x32   $TMPDIR/icns_512x512.png $TMPDIR/icns_32x32.png
    gm convert -scale 16x16   $TMPDIR/icns_512x512.png $TMPDIR/icns_16x16.png
      png2icns $TG_DIR/desktop/build-scripts/common-resources/common-macosx/Contents/Resources/icon.icns $TMPDIR/icns_*.png
inkscape $LOGO   --export-filename=$TMPDIR/icon_412x412.png                                                                               --export-background-opacity=0 --export-width=412 --export-height=412
  gm convert -border 50x50 -bordercolor transparent $TMPDIR/icon_412x412.png $TMPDIR/tgdoc_512x512.png
    gm convert -scale 256x256 $TMPDIR/tgdoc_512x512.png $TMPDIR/tgdoc_256x256.png
    gm convert -scale 128x128 $TMPDIR/tgdoc_512x512.png $TMPDIR/tgdoc_128x128.png
    gm convert -scale 32x32   $TMPDIR/tgdoc_512x512.png $TMPDIR/tgdoc_32x32.png
    gm convert -scale 16x16   $TMPDIR/tgdoc_512x512.png $TMPDIR/tgdoc_16x16.png
      png2icns $TG_DIR/desktop/build-scripts/common-resources/common-macosx/Contents/Resources/tgdoc.icns $TMPDIR/tgdoc_*.png
rm -rf $TMPDIR

# Splash screen

inkscape $SPLASH --export-filename=$TG_DIR/desktop/TuxGuitar/src-skins/Breeze/TuxGuitar/splash.png                                        --export-background-opacity=1
  cp -p                            $TG_DIR/desktop/TuxGuitar/src-skins/Breeze/TuxGuitar/splash.png $TG_DIR/desktop/build-scripts/common-resources/common-windows-installer-icons/TG_splash_500x300.png

inkscape $SPLASH --export-filename=$TG_DIR/desktop/build-scripts/common-resources/common-windows-installer-icons/TG_logo_163x314.png --export-type=png --export-background-opacity=1 --export-height=314
gm mogrify -shave 55x0 -crop 163x314 $TG_DIR/desktop/build-scripts/common-resources/common-windows-installer-icons/TG_logo_163x314.png

# Normally done with build_skin.sh
for skin in Symbolic Symbolic-dark Breeze Breeze-dark Oxygen Oxygen-dark; do
  cp -p $TG_DIR/desktop/TuxGuitar/src-skins/Breeze/TuxGuitar/splash.png $TG_DIR/desktop/TuxGuitar/src-skins/Breeze/TuxGuitar/icon.png $TG_DIR/desktop/TuxGuitar/share/skins/$skin
done

echo "# done."
