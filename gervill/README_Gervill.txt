Gervill - Software Sound Synthesizer
====================================

Introduction:
-------------

   "Gervill" is a software sound synthesizer
   which was created for the open source JDK 
   Audio Synthesis Engine Project.
      
   
Hightlight of features:
-----------------------   

   * Downloadable Sound Level 2.2
   * SoundFonts 2.04 (24 bit)
   * Use Audio files (AIFF, AU and WAV) as SoundBanks
   * General Midi Level 2
   * MIDI Tuning Standard
   * Drumkits can be used on any channel
   * Sinc interpolation with anti-aliasing


List of Technical Specification used:
-------------------------------------

   The Complete MIDI 1.0 Detailed Specification 
     (document  version 96.1 second edition). 
   General MIDI 2 (Version 1.1, September 10, 2003). 
   GM2 MIDI Tuning Amendment (RP-037)
   Downloadable Sounds Level 1 (Version 1.1b, September 2004). 
   Downloadable Sounds Level 2.2 (Version 1.0, April 2006). 
   MIDI IMPLEMENTATION CHART V2 INSTRUCTIONS
   CA-020, MIDI TUNING Extensions 
   CA# 22, Controller Destination Setting
   CA# 23, Key-Based Instrument Controllers
   CA# 24, Global Parameter Control
   CA# 25, Master Fine/Coarse Tuning
   CA# 26, Modulation Depth Range RPN
     The MIDI Manufacturers Association Los Angelese, CA.
     http://www.midi.org
     
   SoundFont 2.1 Application Note
     (Wednesday, August 12, 1998)
     Joint E-mu/Creative Technology Center E-mu Systems, Inc
   SoundFont(R) Technical Specification
     (Version 2.04, February 3, 2006)     
     http://www.soundfont.com
     
   
Features:
---------

Audio/Implementation Features:

* Synhtesizer default parameters.
    Interpolation: Linear
    Audioformat:   44100 Hz, stereo, 
                   16 bit, signed, little-endian
    Control rate:  147 Hz
    Max Polyphony: 64
    Latency:       400 msec 
                  (can be as low as 70 msec on windows plaform)
            
* Audio formats supported:
    Mono/Stereo.
    8,16,24,32 bit signed/unsigned pcm stream
    and 32 bit pcm floating point stream.
    Big/little endian.
    Any sample rate including 44100 Hz.


* Several interpolation algorithms are included:
    linear, cubic, lanczos and sinc interpolation.
    The sinc interpolation also includes a anti-alias filtering.

* The synthesizer looks for default soundbank in these locations:
    %JAVA_RUNTIME_LOCATION%\lib\audio\*.dls and *.sf2
    %WINDOWS_LOCATION%\system32\drivers\gm.dls

* Multi-format soundbank support.
    SoundFont 2.4, DLS 2.2
    and Simple audio files are also supported
    as soundbanks like  WAV/AIFF/AU
    (and other audio files available thru Java SPI)   
    
* SoundFont 2.01 NRPN messages are not supported.
        
* Editable soundbanks:
    SoundFont 2.4 and DLS 2.2 soundbanks
    can be edited in memory and saved back to disk.

* AudioSynthesizer interface is used to 
  route audio output from the synthesizer.

* The function getVoiceStatus is fully implemented 
  and it is the real location where active voice parameters are located.

* Support midi time stamping in the Receiver interface.
  And it is to be used with conjuction with 
  getMicrosecondPosition() method in the synthesizer class.

* Two global effect engine are included: Chorus and Reverb
  and they are fully configurated with
  standard Global Parameter Control Sysex messages.

* Emergency soundbank is include, 
  it used when no default soundbank can be found.


MIDI Features:

* The synthesizer is fully General Midi Level 2 compatible.

* Full support for MIDI tuning standard.
  Sysex mesages messages from programs like 
  Scala (http://www.xs4all.nl/~huygensf/scala/).
  This enables all kinds of microtuning.

* Drumkit and melodic instruments can be on any channel
  using the GM2 MSB banks 0x78 (drumkit) and 0x79 (melodic).

* Support for Midi Controller destination, 
  user can customize how midi controllers or channel/poly aftertouch
  affect internal synthesizer parameters.

* Other sysex messages support are:
    Master Volume, Master Balance, Global Parameter Control
    Key-based instrument ctrl, Master Fine/Coarse tune

* These RPN are supported:
    RPN 00 (Pitch Bend Sensitivity)
    RPN 01 (Channel Fine Tune) 
    RPN 02 (Channel Coarse Tune)
    RPN 03 (Tuning Program Select) 
    RPN 04 (Tuning Bank Select)
    RPN 05 (Modulation Depth Range) 

* Support for GS/XG NRPN voice editing parameters:
    NRPN 1,08 Vibrato Rate
    NRPN 1,09 Vibrato Depth
    NRPN 1,10 Vibrato Delay
    NRPN 1,32 Filter Cutoff
    NRPN 1,33 Filter Resonance
    NRPN 1,99 Attack Time
    NRPN 1,100 Decay Time
    NRPN 1,102 Release Time
    NRPN 24,rr Pitch per drum sound (rr)
    NRPN 26,rr Volume per drum sound (rr)
    NRPN 28,rr Pan per drum sound (rr)
    NRPN 29,rr Reverb per drum sound (rr)
    NRPN 30,rr Chorus per drum sound (rr)

* Both channel and poly aftertouch are supported.

* Full support for portamento control both in poly and mono mode.

* Midi controls supported:
    0 Bank Select MSB
    1 Modulation
    5 Portamento Time
    6 Data Entry MSB
    7 Volume
    8 Balance
    10 Pan
    11 Expression
    32 Ban Select LSB
    38 Data Entry LSB
    64 Sustain
    65 Portamento On/off
    66 Sostenuto
    67 Soft Pedal
    71 Filter Cutoff
    72 Release Time
    73 Attack Time
    74 Filter Resonance
    75 Decay Time
    76 Vibrato Rate
    77 Vibrato Depth
    78 Vibrato Delay
    84 Portamento Control
    91 Reverb send
    93 Chorus send
    96 Data Increment
    97 Data Decrement
    98 NRPN LSB
    99 NRPN MSB
    100 RPN LSB
    101 RPN MSB
    120 All Sound Off
    121 Reset all controllers
    123 All notes Off
    126 Poly Mode Off
    127 Poly Mode On  


MIDI Implementation Chart v. 2.0 (Page 1 of 3)              
----------------------------------------------

                                Transmit/Export Recognize/Import    Remarks
1. Basic Information                
MIDI channels                           No      1 - 16  
Note numbers                            No      1 - 128 
Program change                          No      1 - 128 
Bank Select response? (Yes/No)          No      Yes         Both MSB and LSB messages.
Modes supported :   
    Mode 1: Omni-On, Poly (Yes/No)      No      No
    Mode 2: Omni-On, Mono (Yes/No)      No      No
    Mode 3: Omni-Off, Poly (Yes/No)     No      Yes 
    Mode 4: Omni-Off, Mono (Yes/No)     No      Yes 
Multi Mode (Yes/No)                     No      Yes 
Note-On Velocity (Yes/No)               No      Yes 
Note-Off Velocity (Yes/No)              No      No  
Channel Aftertouch (Yes/No)             No      Yes 
Poly (Key) Aftertouch (Yes/No)          No      Yes 
Pitch Bend (Yes/No)                     No      Yes 
Active Sensing (Yes/No)                 No      Yes 
System Reset (Yes/No)                   No      Yes 
Tune Request (Yes/No)                   No      No  
Universal System Exclusive: 
    Sample Dump Standard (Yes/No)       No      No  
    Device Inquiry (Yes/No)             No      No  
    File Dump (Yes/No)                  No      No  
    MIDI Tuning (Yes/No)                No      Yes 
    Master Volume (Yes/No)              No      Yes 
    Master Balance (Yes/No)             No      Yes 
    Notation Information (Yes/No)       No      No  
    Turn GM1 System On (Yes/No)         No      Yes 
    Turn GM2 System On (Yes/No)         No      Yes 
    Turn GM System Off (Yes/No)         No      Yes 
    DLS-1 (Yes/No)                      No      Yes 
    File Reference (Yes/No)             No      No  
    Controller Destination (Yes/No)     No      Yes 
    Key-based Instrument Ctrl (Yes/No)  No      Yes 
    Master Fine/Coarse Tune (Yes/No)    No      Yes 
    Other Universal System Exclusive    No      Yes         Global Parameter Control
Manufacturer or Non-Commercial System Exclusive             
NRPNs (Yes/No)                          No      Yes         GS/XG NRPN voice editing parameters
RPN 00 (Pitch Bend Sensitivity) (Yes/No)No      Yes 
RPN 01 (Channel Fine Tune) (Yes/No)     No      Yes 
RPN 02 (Channel Coarse Tune) (Yes/No)   No      Yes 
RPN 03 (Tuning Program Select) (Yes/No) No      Yes 
RPN 04 (Tuning Bank Select) (Yes/No)    No      Yes 
RPN 05 (Modulation Depth Range) (Yes/No)No      Yes 
2. MIDI Timing and Synchronization              
MIDI Clock (Yes/No)                     No      No  
Song Position Pointer (Yes/No)          No      No  
Song Select (Yes/No)                    No      No  
Start (Yes/No)                          No      No  
Continue (Yes/No)                       No      No  
Stop (Yes/No)                           No      No  
MIDI Time Code (Yes/No)                 No      No  
MIDI Machine Control (Yes/No)           No      No  
MIDI Show Control (Yes/No)              No      No  
If yes, MSC Level supported             
3. Extensions Compatibility             
General MIDI compatible? (Level(s)/No)  Level 2 
Is GM default power-up mode? (Level/No) No*                 * Some non-gm NRPN are enabled.
DLS compatible? (Levels(s)/No)          Level 2 (DLS 2.2)   Also supports SoundFont 2.4
(DLS File Type(s)/No)                   .DLS    
Standard MIDI Files (Type(s)/No)        No  
XMF Files (Type(s)/No)                  No  
SP-MIDI compatible? (Yes/No)            No  
    
MIDI Implementation Chart v. 2.0 (Page 2 of 3)              
----------------------------------------------

Control#    Function#       Transmitted (Y/N)   Recognized (Y/N)    Remarks
0   Bank Select (MSB)                   N       Y   
1   Modulation Wheel (MSB)              N       Y   
2   Breath Controller (MSB)             N       N   
3                                       N       N   
4   Foot Controller (MSB)               N       N   
5   Portamento Time (MSB)               N       Y   10^(0.5-5*asin(cc5/64-1)/pi) = cent/msec
6   Data Entry (MSB)                    N       Y   
7   Channel Volume (MSB)                N       Y   40*log10(cc7/128) = dB
8   Balance (MSB)                       N       Y   
9                                       N       N   
10  Pan (MSB)                           N       Y   
11  Expression (MSB)                    N       Y   40*log10(cc11/128) = dB
12  Effect Control 1 (MSB)              N       N   
13  Effect Control 2 (MSB)              N       N   
14                                      N       N   
15                                      N       N   
16  General Purpose Controller 1 (MSB)  N       N   
17  General Purpose Controller 2 (MSB)  N       N   
18  General Purpose Controller 3 (MSB)  N       N   
19  General Purpose Controller 4 (MSB)  N       N   
20                                      N       N   
21                                      N       N   
22                                      N       N   
23                                      N       N   
24                                      N       N   
25                                      N       N   
26                                      N       N   
27                                      N       N   
28                                      N       N   
29                                      N       N   
30                                      N       N   
31                                      N       N   
32  Bank Select (LSB)                   N       Y   
33  Modulation Wheel (LSB)              N       N   
34  Breath Controller (LSB)             N       N   
35                                      N       N   
36  Foot Controller (LSB)               N       N   
37  Portamento Time (LSB)               N       N   
38  Data Entry (LSB)                    N       Y   
39  Channel Volume (LSB)                N       N   
40  Balance (LSB)                       N       N   
41                                      N       N   
42  Pan (LSB)                           N       N   
43  Expression (LSB)                    N       N   
44  Effect Control 1 (LSB)              N       N   
45  Effect Control 2 (LSB)              N       N   
46                                      N       N   
47                                      N       N   
48  General Purpose Controller 1 (LSB)  N       N   
49  General Purpose Controller 2 (LSB)  N       N   
50  General Purpose Controller 3 (LSB)  N       N   
51  General Purpose Controller 4 (LSB)  N       N   
52                                      N       N   
53                                      N       N   
54                                      N       N   
55                                      N       N   
56                                      N       N   
57                                      N       N   
58                                      N       N   
59                                      N       N   
60                                      N       N   
61                                      N       N   
62                                      N       N   
63                                      N       N   

MIDI Implementation Chart v. 2.0 (Page 3 of 3)              
----------------------------------------------

Control#    Function#       Transmitted (Y/N)   Recognized (Y/N)    Remarks
64  Sustain Pedal                       N       Y   
65  Portamento On/Off                   N       Y   
66  Sostenuto                           N       Y   
67  Soft Pedal                          N       Y   
68  Legato Footswitch                   N       N   
69  Hold 2                              N       N   
70  Sound Controller 1                  N       N   
71  Sound Controller 2 (Filter Cutoff)  N       Y    +/- 6400 cents
72  Sound Controller 3 (Release Time)   N       Y    +/- 6 sec
73  Sound Controller 4 (Attack Time)    N       Y    +/- 2 sec
74  Sound Controller 5 (Brightness)     N       Y    +/- 20 db (Filter Resonance)
75  Sound Controller 6 (Decay Time)     N       Y    +/- 6 sec
76  Sound Controller 7 (Vibrato Rate)   N       Y    +/- 2400 cents
77  Sound Controller 8 (Vibrato Depth)  N       Y    +/- 200 cents
78  Sound Controller 9 (Vibrato Delay)  N       Y    +/- 2 sec
79  Sound Controller 10                 N       N   
80  General Purpose Controller 5        N       N   
81  General Purpose Controller 6        N       N   
82  General Purpose Controller 7        N       N   
83  General Purpose Controller 8        N       N   
84  Portamento Control                  N       Y   
85                                      N       N   
86                                      N       N   
87                                      N       N   
88                                      N       N   
89                                      N       N   
90                                      N       N   
91  Effects 1 Depth, Reverb             N       Y   
92  Effects 2 Depth                     N       N   
93  Effects 3 Depth, chorus             N       Y   
94  Effects 4 Depth                     N       N   
95  Effects 5 Depth                     N       N   
96  Data Increment                      N       Y   
97  Data Decrement                      N       Y   
98  NRPN (LSB)                          N       Y   
99  NRPN (MSB)                          N       Y   
100 RPN (LSB)                           N       Y   
101 RPN (MSB)                           N       Y   
102                                     N       N   
103                                     N       N   
104                                     N       N   
105                                     N       N   
106                                     N       N   
107                                     N       N   
108                                     N       N   
109                                     N       N   
110                                     N       N   
111                                     N       N   
112                                     N       N   
113                                     N       N   
114                                     N       N   
115                                     N       N   
116                                     N       N   
117                                     N       N   
118                                     N       N   
119                                     N       N   
120 All Sound Off                       N       Y   
121 Reset All Controllers               N       Y   
122 Local Control On/Off                N       N   
123 All Notes Off                       N       Y   
124 Omni Mode Off                       N       N   
125 Omni Mode On                        N       N   
126 Poly Mode Off                       N       Y   
127 Poly Mode On                        N       Y       
    