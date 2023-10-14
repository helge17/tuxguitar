#!/usr/bin/perl

use strict;

if ($#ARGV != 1){
	print "\n** This script formats and sorts a messages_LANG.properties file like the main messages.properties.\n";
	print "** Untranslated messages are taken over from messages.properties and commented out.\n";
	print "** Entries in messages_LANG.properties not present in messages.properties are deleted!\n";
	print "** Usage: $0 <messages.properties> <messages_LANG.properties>\n";
	print "** The output is sent to stdout.\n\n";
	exit 1;
}

my $messages_dflt = $ARGV[0];
my $messages_LANG = $ARGV[1];

my %msg_lang = ();

open(MSG_DFLT, "<", $messages_dflt) or die "\n** $messages_dflt: $!.\n\n";
open(MSG_LANG, "<", $messages_LANG) or die "\n** $messages_LANG: $!.\n\n";

while (<MSG_LANG>) {
	next if /^\s*(#|$)/;
	chomp;
	/^(.+?)=(.+)$/;
	$msg_lang{$1}=$2;
}

my $line=0;
while (<MSG_DFLT>) {
	chomp;
	$line++;
	# Skip the first 144 lines (Default language codes)
	next if $line < 145;
	if (/^(.+?)=(.+)$/) {
		if ($msg_lang{$1} && $msg_lang{$1} ne $2) {
			# message is translated and is not the same as in the default messages.properties
			print "$1=$msg_lang{$1}\n";
		} else {
			print "# $1=\n";
		}
	} else {
		print "$_\n";
	}
}

