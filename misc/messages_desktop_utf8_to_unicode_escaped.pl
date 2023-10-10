#!/usr/bin/perl

use strict;

if ($#ARGV != 0){
	print "\n** This script converts a utf8 text file to Java unicode escaped format (\\uHHHH).\n";
	print "** You can convert the Java unicode escaped format back to utf8 with: uconv -x hex-any <messages_XX.properties>\n";
	print "** Usage: $0 <messages_XX.properties_utf8>\n";
	print "** The output is sent to stdout.\n\n";
	exit 1;
}

my $file = $ARGV[0];

open(IN, "<:utf8", $file) or die "\n** $file: $!.\n\n";
my @data = <IN>;
close(IN);

my ($i)=0;
my $line;

foreach (@data){
	chop;

	my @chars = unpack ("U*", $_);
	my $line = "";

	foreach my $c (@chars){
		if ($c > 128){
			$line .= "\\u".sprintf("%04x",$c);
		}
		else {
			$line .= pack ("U*", $c);
		}
	}

	print "$line\n";
}
