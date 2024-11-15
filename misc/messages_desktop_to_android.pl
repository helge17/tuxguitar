#!/usr/bin/perl

use Getopt::Std;
use File::Basename;
use feature "switch";

$COMMAND=basename($0);
$SCRIPT_DIR=dirname($0);

my $messages_list = "$SCRIPT_DIR/../common/resources/lang/messages_*.properties";

if (!getopts('h') || $opt_h) {

  print "\n#\n";
  print "# This script extracts the messages in all languages from the desktop version in\n";
  print "#   $messages_list\n";
  print "# and writes them to the Android message files in\n";
  print "#   $SCRIPT_DIR/../android/TuxGuitar-android/res/values-b+*/strings.xml\n";
  print "#\n";
  print "# Usage: $COMMAND [OPTIONS]\n";
  print "#\n";
  print "# -h     Display this help message and exit.\n";
  print "#\n";
  print "# The script uses the following logic to determine the corresponding strings:\n";
  print "# Same english text => Entries mean the same, e.g.:\n";
  print "#   messages.properties:   key_desktop=english bla bla\n";
  print '#   strings.xml:           <string name="key_android">english bla bla</string>'."\n";
  print '#   => val_desktop = val_android = "english bla bla"'."\n";
  print "#   => val_desktop means the same as val_android => take over the content of val_desktop to val_android for all languages\n";
  print "#\n\n";
  exit;

}

STDOUT->autoflush(1);

# https://developer.android.com/training/basics/supporting-devices/languages
my $msgs_en_android="$SCRIPT_DIR/../android/TuxGuitar-android/res/values/strings.xml";
my $msgs_en_desktop="$SCRIPT_DIR/../common/resources/lang/messages.properties";

open(MSGS_EN_ANDROID, "<", $msgs_en_android) or die "\n** Error opening $msgs_en_android: $!.\n\n";
open(MSGS_EN_DESKTOP, "<", $msgs_en_desktop) or die "\n** Error opening $msgs_en_desktop: $!.\n\n";

sub special_cases {
  given ($_[0]) {
    when ("effects.harmonic.natural")         { return '[N.H] '.$_[1]; }
    when ("effects.harmonic.artificial")      { return '[A.H] '.$_[1]; }
    when ("effects.harmonic.tapped")          { return '[T.H] '.$_[1]; }
    when ("effects.harmonic.pinch")           { return '[P.H] '.$_[1]; }
    when ("effects.harmonic.semi")            { return '[S.H] '.$_[1]; }
    when ("tuning.strings.range-error") {
      $_[1] =~ s/\{0\}/%1\$d/g;
      $_[1] =~ s/\{1\}/%2\$d/g;
      return $_[1];
    }
    default {
      if ($_[2] =~ /:$/) {
        # Desktop messages contain no colon at the end (the colon is attached in the Java code),
        # in Android the colon is part of the message string
        return $_[1].':';
      }
      return $_[1];
    }
  }
}

################## Determine mapping of key_android - key_desktop based on the english messages of the desktop version

# Read english messages of desktop version
while (<MSGS_EN_DESKTOP>) {
  next if /^\s*(#|$)/;
  chomp;
  if (/^(.+?)=(.+)$/) {
    $key_desktop=$1;
    $val_desktop=$2;
    $msg_desktop{$key_desktop}=$val_desktop;
  }
}

my $key_list="/tmp/keys_android-keys_desktop-values_android.csv";

print "Write mapping of the english messages of the desktop and the Android version to\n";
print "$key_list ";

open(KEY_LIST, ">", $key_list) or die "\n** Error opening $key_list: $!.\n\n";
my %keys_desktop=();

printf KEY_LIST "%-60s %-60s %-s\n", "# keys_android", "keys_desktop", "values_android";

# Read Android messages and compare them with the desktop messages
while (<MSGS_EN_ANDROID>) {
  chomp;
  if (/^\s*<string name="(.+?)">(.+)<\/string>$/) {
    $trans_found=0;
    $key_android=$1;
    $val_android=$2;
    # Because for one single english expression there may exist different translations, it is important to sort the keys in the following foreach loop. Otherwise the result of the script may vary on every call. E.g:
    #   <string name="action_measure_remove">Remove Measure</string>
    # would randomly be translated to one of the following german messages:
    #   measure.remove=Remove Measure                         ->  measure.remove=Takt löschen
    #   action.gui.open-measure-remove-dialog=Remove Measure  ->  action.gui.open-measure-remove-dialog=Takt entfernen
    foreach $key_desktop (sort keys %msg_desktop) {
      $val_desktop=special_cases($key_desktop,$msg_desktop{$key_desktop},$val_android);
      if ($val_android eq $val_desktop) {
        # Translation matches exactly
        printf KEY_LIST "%-60s %-60s %-s\n", $key_android, $key_desktop, $val_android;
        $keys_desktop{$key_android}=$key_desktop;
        $trans_found=1;
      } elsif (lc($val_android) eq lc($val_desktop)) {
        # Translation matches except for upper/lowercase
        printf KEY_LIST "%-60s %-60s %-s\n", $key_android, $key_desktop, "$val_android CASEDIFFERENT $val_desktop";
        $trans_found=1;
      }
    }
    if (!$trans_found) {
      # No matching translation found
      printf KEY_LIST "%-60s %-60s %-s\n", $key_android, "UNKNOWN", $val_android;
    }
  }
}

print "\nOK.\n";

################## Fill val_android for all languages with the content of val_desktop according to the above mapping

print "Write Android language files to $SCRIPT_DIR/../android/TuxGuitar-android/res/:\n";

foreach my $msgs_XX_desktop (glob("$messages_list")) {

  @msgs_XX_desktop_content=`cat $msgs_XX_desktop`;

  $msgs_XX_desktop =~ /$SCRIPT_DIR\/\.\.\/common\/resources\/lang\/messages_(.*).properties/;
  (my $lang=$1)=~s/_/+/g;
  print "$lang ";

  my $langdir="$SCRIPT_DIR/../android/TuxGuitar-android/res/values-b+".$lang;
  mkdir $langdir;
  my $msgs_XX_android="$langdir/strings.xml";

  open(MSGS_XX_ANDROID, ">", $msgs_XX_android) or die "\n** Error opening $msgs_XX_android: $!.\n\n";

  seek MSGS_EN_ANDROID,0,0;
  while (<MSGS_EN_ANDROID>) {
    chomp;
    if (/^(\s*)(<string name=")(.+?)(">)(.+)(<\/string>)$/) {
      $trans_found=0;
      $indent=$1;
      $string_start=$2;
      $key_android=$3;
      $string_middle=$4;
      $val_android=$5;
      $string_end=$6;
      $key_desktop=$keys_desktop{$key_android};

      foreach $msgs_xx_desktop_line (@msgs_XX_desktop_content) {
        chomp ($msgs_xx_desktop_line);
        next if $msgs_xx_desktop_line =~ /^\s*(#|$)/;
        chomp;
        if ($msgs_xx_desktop_line =~ /^$key_desktop=(.+)$/) {
          $val_desktop=$1;

          $val_desktop=special_cases($key_desktop,$val_desktop,$val_android);

          # Some characters have to be masked on Android
          $val_desktop=~s/&/&amp;/sg;
          $val_desktop=~s/</&lt;/sg;
          $val_desktop=~s/>/&gt;/sg;
          $val_desktop=~s/"/&quot;/sg;
          $val_desktop=~s/'/\\&apos;/sg;

          print MSGS_XX_ANDROID "$indent$string_start$key_android$string_middle$val_desktop$string_end\n";
          $trans_found=1;
          last;
        }
      }
      if (!$trans_found) {
        # No matching translation found
        # Output without $val_android, because it may contain --, and -- is not allowed within comments (does not compile)
        #print MSGS_XX_ANDROID $indent."<!-- "."$string_start$key_android$string_middle$val_android$string_end"." -->\n";
        print MSGS_XX_ANDROID $indent."<!-- "."$string_start$key_android$string_middle$string_end"." -->\n";
      }
    } else {
      print MSGS_XX_ANDROID "$_\n";
      if (/^<\?xml version=/) {
        print MSGS_XX_ANDROID "\n<!-- =========================================================================== -->\n";
        print MSGS_XX_ANDROID "<!-- =  This file was created automatically by $COMMAND  = -->\n";
        print MSGS_XX_ANDROID "<!-- ====================== Do not edit this file by hand! ===================== -->\n";
        print MSGS_XX_ANDROID "<!-- =========================================================================== -->\n\n";
      }
    }

  }

}

print "\nOK.\n";
