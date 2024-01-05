#!/bin/bash

SCRIPT_DIR=`dirname $0`

rsync --copy-links --verbose --archive --delete --compress $SCRIPT_DIR/../website/ www.tuxguitar.app:/var/www/html/*/www.tuxguitar.app/
