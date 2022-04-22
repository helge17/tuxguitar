#!/bin/bash
DIR_NAME=`dirname "$0"`
DIR_NAME=`cd "$DIR_NAME"; pwd`
cd "${DIR_NAME}"

mkdir -p logs

(
	echo "$(date "+%m%d%Y %T") : Starting work"
	
	./tuxguitar-synth-lv2.bin "$1" "$2" "$3" "$4"
	
	echo error 1>&2 # test stderr
	echo "$(date "+%m%d%Y %T") : Done"
) >& logs/session-${1}.log
