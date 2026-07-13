#!/usr/bin/perl

use Getopt::Std;
use File::Basename;

STDOUT->autoflush(1);

$COMMAND=basename($0);
$SCRIPT_DIR=dirname($0);

$msg_list = "messages_*.properties";
$msg_dflt = "messages.properties";

if (!getopts('h') || $opt_h) {

  print "\n#\n";
  print "# This script formats and sorts all language files\n";
  print "#   $msg_list\n";
  print "# like the main file\n";
  print "#   $msg_dflt\n";
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

# Skip the default language codes
$skip = 143;
$msg_dir = "common/resources/lang"; process_list();

$skip = 0;
$msg_dir = "desktop/TuxGuitar-lilypond-ui/share/lang"; process_list();
$msg_dir = "desktop/TuxGuitar-jack-ui/share/lang"; process_list();
$msg_dir = "desktop/TuxGuitar-fluidsynth/share/lang"; process_list();
$msg_dir = "desktop/TuxGuitar-jack/share/lang"; process_list();
$msg_dir = "desktop/TuxGuitar-gtp-ui/share/lang"; process_list();
$msg_dir = "desktop/TuxGuitar-image-swt/share/lang"; process_list();
$msg_dir = "desktop/TuxGuitar-tuner/share/lang"; process_list();
$msg_dir = "desktop/TuxGuitar-synth-lv2/src/main/resources/lang"; process_list();
$msg_dir = "desktop/TuxGuitar-browser-ftp/share/lang"; process_list();
$msg_dir = "desktop/TuxGuitar-synth-export/share/lang"; process_list();
$msg_dir = "desktop/TuxGuitar-jsa/share/lang"; process_list();
$msg_dir = "desktop/TuxGuitar-pdf-ui/share/lang"; process_list();
$msg_dir = "desktop/TuxGuitar-AudioUnit/share/lang"; process_list();
$msg_dir = "desktop/TuxGuitar-synth-gervill/share/lang"; process_list();
$msg_dir = "desktop/TuxGuitar-converter/share/lang"; process_list();
$msg_dir = "desktop/TuxGuitar-gm-settings/share/lang"; process_list();
$msg_dir = "desktop/TuxGuitar-cocoa-integration-swt/share/lang"; process_list();
$msg_dir = "desktop/TuxGuitar-synth/share/lang"; process_list();

sub process_list {

  print "Processing $msg_dir:\n";

  foreach my $msg_LANG (glob("$SCRIPT_DIR/../$msg_dir/$msg_list")) {

   $base_msg_LANG=basename($msg_LANG);

    if ($base_msg_LANG eq "messages_en.properties") {
      print "Skipping $base_msg_LANG (dummy file).\n";
      next;
    }

    print "$base_msg_LANG ...";

    %msg_lang = ();

    open(MSG_DFLT, "<", "$SCRIPT_DIR/../$msg_dir/$msg_dflt") or die "\n** Error opening $SCRIPT_DIR/../$msg_dir/$msg_dflt: $!.\n\n";
    open(MSG_LANG, "<", "$msg_LANG") or die "\n** Error opening $msg_LANG: $!.\n\n";
    open(MSG_LNEW, ">", "$msg_LANG".'.new') or die "\n** Error opening $msg_LANG.new: $!.\n\n";

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
      # Skip the first $skip lines
      next if $line <= $skip;
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

    close(MSG_DFLT) or die "\n** Error closing $SCRIPT_DIR/../$msg_dir/$msg_dflt: $!.\n\n";
    close(MSG_LANG) or die "\n** Error closing $msg_LANG: $!.\n\n";
    close(MSG_LNEW) or die "\n** Error closing $msg_LANG.new: $!.\n\n";
    rename ($msg_LANG.'.new',$msg_LANG)  or die "\n** Error renaming $msg_LANG.new: $!.\n\n";

    print " OK.\n";

  }

}

print "Done.\n";
