TMPSVG=`mktemp --suffix=.svg`
GRDSVG="$THIS_DIR/$skin/other/Breeze_Gradient.svg#Gradient1"
sed 's|fill="#232629"|fill="url('$GRDSVG')"|g' $icon > $TMPSVG
inkscape --export-filename=$out_icon --export-background-opacity=0 --export-width=$width --export-height=$height $TMPSVG > /dev/null 2>&1
rm -f $TMPSVG
