inkscape --export-filename=- --export-type=png --export-background-opacity=0 --export-width=$width --export-height=$height $icon 2>/dev/null | gm convert -operator all add 50% - $out_icon
