TMPPNG=`mktemp --suffix=.png`
inkscape --export-filename=- --export-type=png --export-background-opacity=0 --export-width=10 --export-height=10 `dirname $icon`/list_add.svgz 2>/dev/null | gm convert -border 4x4 -bordercolor transparent - $TMPPNG
inkscape --export-filename=- --export-type=png --export-background-opacity=0 --export-width=$width --export-height=$height $icon 2>/dev/null | gm composite -gravity SouthEast $TMPPNG - $out_icon
rm -f $TMPPNG
