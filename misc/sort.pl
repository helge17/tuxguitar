#!/usr/bin/perl

use strict;

if ($#ARGV != 1){
	print "\n** This script formats and sorts a messages_XX.properties file like the main messages.properties.\n";
	print "** Untranslated messages are taken over from messages.properties and commented out.\n";
	print "** Entries in messages_XX.properties not present in messages.properties are deleted!\n";
	print "** Usage: $0 <messages.properties> <messages_XX.properties>\n";
	print "** The output is sent to stdout.\n\n";
	exit 1;
}

my $messages_default = $ARGV[0];
my $messages_lang_XX = $ARGV[1];

my %msg_lxx = ();

open(MSG_DEF, "<", $messages_default) or die "\n** $messages_default: $!.\n\n";
open(MSG_LXX, "<", $messages_lang_XX) or die "\n** $messages_lang_XX: $!.\n\n";

while (<MSG_LXX>) {
	next if /^\s*(#|$)/;
	chomp;
	/^(.+?)=(.+)$/;
	$msg_lxx{$1}=$2;
}

while (<MSG_DEF>) {
	chomp;
	if (/^(.+?)=(.+)$/) {
		if ($msg_lxx{$1} && $msg_lxx{$1} ne $2) {
			# message is translated and is not the same as in the default messages.properties
			print "$1=$msg_lxx{$1}\n";
		} else {
			print "# $1=\n";
		}
	} else {
		print "$_\n";
	}
}

