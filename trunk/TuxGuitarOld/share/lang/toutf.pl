#!/usr/bin/perl

use strict;

if ($#ARGV < 0){
	print "** Please, specify a file to convert\n\n";
	exit 1;
}

my $file = $ARGV[0];

open(IN, "<:utf8", $file);
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
