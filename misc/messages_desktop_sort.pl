#!/usr/bin/perl

use Getopt::Std;
use File::Basename;

$COMMAND=basename($0);
$SCRIPT_DIR=dirname($0);

my $messages_list = "$SCRIPT_DIR/../common/resources/lang/messages_*.properties";
my $messages_dflt = "$SCRIPT_DIR/../common/resources/lang/messages.properties";

if (!getopts('h') || $opt_h) {

  print "\n#\n";
  print "# This script formats and sorts all language files\n";
  print "#   $messages_list\n";
  print "# like the main file\n";
  print "#   $messages_dflt\n";
  print "# Untranslated messages are taken over from messages.properties and commented out.\n";
  print "# Entries in messages_*.properties not present in messages.properties are deleted!\n";
  print "# The purpose of the script is to automatically take over new or deleted entries in\n";
  print "# messages.properties to the messages_*.properties files.\n";
  print "#\n";
  print "# Usage: $COMMAND [OPTIONS]\n";
  print "#\n";
  print "# -h     Display this help message and exit.\n";
  print "#\n\n";
  exit;

}

STDOUT->autoflush(1);

print "Processing $messages_list:\n";

foreach my $messages_LANG (glob("$messages_list")) {

 $base_msg_LANG=basename($messages_LANG);

  if ($base_msg_LANG eq "messages_en.properties") {
    print "Skipping $base_msg_LANG (dummy file).\n";
    next;
  }

  print "$base_msg_LANG ...";

  %msg_lang = ();

  open(MSG_DFLT, "<", $messages_dflt) or die "\n** Error opening $messages_dflt: $!.\n\n";
  open(MSG_LANG, "<", $messages_LANG) or die "\n** Error opening $messages_LANG: $!.\n\n";
  open(MSG_LNEW, ">", $messages_LANG.'.new') or die "\n** Error opening $messages_LANG.new: $!.\n\n";

  while (<MSG_LANG>) {
    next if /^\s*(#|$)/;
    chomp;
    /^(.+?)=(.+)$/;
    $msg_lang{$1}=$2;
  }

  $line=0;
  while (<MSG_DFLT>) {
    chomp;
    $line++;
    # Skip the first 144 lines (Default language codes)
    next if $line < 145;
    if (/^(.+?)=(.+)$/) {
      if ($msg_lang{$1} && $msg_lang{$1} ne $2) {
        # message is translated and is not the same as in the default messages.properties
        print MSG_LNEW "$1=$msg_lang{$1}\n";
      } else {
        print MSG_LNEW "# $1=\n";
      }
    } else {
      print MSG_LNEW "$_\n";
    }
  }

  close(MSG_DFLT) or die "\n** Error closing $messages_dflt: $!.\n\n";
  close(MSG_LANG) or die "\n** Error closing $messages_LANG: $!.\n\n";
  close(MSG_LNEW) or die "\n** Error closing $messages_LANG.new: $!.\n\n";
  rename ($messages_LANG.'.new',$messages_LANG)  or die "\n** Error renaming $messages_LANG.new: $!.\n\n";

  print " OK.\n";

}

print "Done.\n";
