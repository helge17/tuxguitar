#include <stdio.h>

#include "org_herac_tuxguitar_player_impl_midiport_audiounit_MidiReceiverJNI.h"

#include <AudioUnit/AudioUnit.h>
#include <AudioToolbox/AudioToolbox.h>

// some MIDI constants:
enum
{
	kMidiMessage_ControlChange 		= 0xB,
	kMidiMessage_ProgramChange 		= 0xC,

    kMidiMessage_NoteOff			= 0x8,
	kMidiMessage_NoteOn 			= 0x9,
    kMidiMessage_PitchBend          = 0xE
};

enum Controllers
{
    kController_BankMSBControl 	= 0,
	kController_BankLSBControl	= 32,
};

const int PITCH_BEND_LOWEST = 0;
const int PITCH_BEND_CENTER = 8192;
const int PITCH_BEND_HIGHEST = 16383;

// Does not enforce the 'unsigned' aspect, this type only exists to make the calls clear what they expect
typedef char UInt7;

class MidiPlayer
    {
        AUGraph graph;
        AudioUnit synthUnit;
        OSStatus result;
                
        // ----------------------------------------------------------------------------------------
        /** Create the Graph and the Synth unit */
        OSStatus createAUGraph (AUGraph &outGraph, AudioUnit &outSynth)
        {
            OSStatus result;
            //create the nodes of the graph
            AUNode synthNode, limiterNode, outNode;
            
            ComponentDescription cd;
            cd.componentManufacturer = kAudioUnitManufacturer_Apple;
            cd.componentFlags = 0;
            cd.componentFlagsMask = 0;
            
            require_noerr (result = NewAUGraph (&outGraph), CreateAUGraph_home);
            
            cd.componentType = kAudioUnitType_MusicDevice;
            cd.componentSubType = kAudioUnitSubType_DLSSynth;
            
            require_noerr (result = AUGraphAddNode (outGraph, &cd, &synthNode), CreateAUGraph_home);
            
            cd.componentType = kAudioUnitType_Effect;
            cd.componentSubType = kAudioUnitSubType_PeakLimiter;  
            
            require_noerr (result = AUGraphAddNode (outGraph, &cd, &limiterNode), CreateAUGraph_home);
            
            cd.componentType = kAudioUnitType_Output;
            cd.componentSubType = kAudioUnitSubType_DefaultOutput;  
            require_noerr (result = AUGraphAddNode (outGraph, &cd, &outNode), CreateAUGraph_home);
            
            require_noerr (result = AUGraphOpen (outGraph), CreateAUGraph_home);
            
            require_noerr (result = AUGraphConnectNodeInput (outGraph, synthNode, 0, limiterNode, 0), CreateAUGraph_home);
            require_noerr (result = AUGraphConnectNodeInput (outGraph, limiterNode, 0, outNode, 0), CreateAUGraph_home);
            
            // ok we're good to go - get the Synth Unit...
            require_noerr (result = AUGraphNodeInfo(outGraph, synthNode, 0, &outSynth), CreateAUGraph_home);
            
        CreateAUGraph_home:
            return result;
        }
        
    public:
        
        // ----------------------------------------------------------------------------------------
        MidiPlayer(char* bankPath = 0)
        {
            graph = 0;            
            require_noerr (result = this->createAUGraph (graph, synthUnit), ctor_home);
            
            // if the user supplies a sound bank, we'll set that before we initialize and start playing
            if (bankPath) 
            {
                FSRef fsRef;
                require_noerr (result = FSPathMakeRef ((const UInt8*)bankPath, &fsRef, 0), ctor_home);
                
                printf ("Setting Sound Bank:%s\n", bankPath);
                
                require_noerr (result = AudioUnitSetProperty (synthUnit,
                                                              kMusicDeviceProperty_SoundBankFSRef,
                                                              kAudioUnitScope_Global, 0,
                                                              &fsRef, sizeof(fsRef)), ctor_home);
                
            }
            
            // initialize and start the graph
            require_noerr (result = AUGraphInitialize (graph), ctor_home);
            
            for (int n=0; n<15; n++)
            {
                this->programChange(0, n);
                this->setBank(n);
            }
            
            // print out the graph so we can see what it looks like...
            //CAShow (graph);
            
            require_noerr (result = AUGraphStart (graph), ctor_home);
            return;
            
        ctor_home:
            fprintf(stderr, "Error in MidiPlayer::MidiPlayer\n");
            
        }
        
        // ----------------------------------------------------------------------------------------
        ~MidiPlayer()
        {
            if (graph)
            {
                AUGraphStop (graph);
                DisposeAUGraph (graph);
            }
        }
        
        // ----------------------------------------------------------------------------------------
        /** Note on event */
        void noteOn(UInt32 noteNum, UInt32 onVelocity, UInt8 midiChannelInUse)
        {
            UInt32 noteOnCommand  = kMidiMessage_NoteOn << 4 | midiChannelInUse;
            require_noerr (result = MusicDeviceMIDIEvent(synthUnit, noteOnCommand, noteNum, onVelocity, 0), home_note_on);
            
            return;
            
        home_note_on:
            fprintf(stderr, "Error in MidiPlayer::noteOn\n");
        }
        
        // ----------------------------------------------------------------------------------------
        /** Note off event */
        void noteOff(UInt32 noteNum, UInt8 midiChannelInUse)
        {
            UInt32 noteOffCommand = kMidiMessage_NoteOn << 4 | midiChannelInUse;
            require_noerr (result = MusicDeviceMIDIEvent(synthUnit, noteOffCommand, noteNum, 0, 0), home_note_off);
            
            return;
            
        home_note_off:
            fprintf(stderr, "Error in MidiPlayer::noteOff\n");
        }
        
        // ----------------------------------------------------------------------------------------
        /** Program change event */
        void programChange(UInt8 progChangeNum, UInt8 midiChannelInUse)
        {
            require_noerr (result = MusicDeviceMIDIEvent(synthUnit, 
                                                         kMidiMessage_ProgramChange << 4 | midiChannelInUse, 
                                                         progChangeNum, 0,
                                                         0 /*sample offset*/), home_programChange);
            return;
        home_programChange:
            fprintf(stderr, "Error in MidiPlayer::programChange\n");
        }
        
        // ----------------------------------------------------------------------------------------
        /** Bank set event */
        void setBank(UInt8 midiChannelInUse)
        {
            require_noerr (result = MusicDeviceMIDIEvent(synthUnit, 
                                                         kMidiMessage_ControlChange << 4 | midiChannelInUse, 
                                                         kController_BankMSBControl, 0,
                                                         0 /*sample offset*/), home_setBank);
            return;
        home_setBank:
            fprintf(stderr, "Error in MidiPlayer::setBank\n");
        }
        
        // ----------------------------------------------------------------------------------------
        /** Control Event */
        void controlEvent(UInt8 controller, UInt7 value, UInt8 midiChannelInUse)
        {
            require_noerr (result = MusicDeviceMIDIEvent(synthUnit, 
                                                         kMidiMessage_ControlChange << 4 | midiChannelInUse, 
                                                         controller, value,
                                                         0 /*sample offset*/), home_setBank);
            return;
        home_setBank:
            fprintf(stderr, "Error in MidiPlayer::setBank\n");
        }
        
        // ----------------------------------------------------------------------------------------
        /** Pitch Bend Event */
        void pitchBend(UInt7 lsb_value, UInt7 msb_value, UInt8 midiChannelInUse)
        {
            require_noerr (result = MusicDeviceMIDIEvent(synthUnit, 
                                                         kMidiMessage_PitchBend << 4 | midiChannelInUse, 
                                                         lsb_value, msb_value,
                                                         0 /*sample offset*/), home_setBank);
            return;
        home_setBank:
            fprintf(stderr, "Error in MidiPlayer::setBank\n");
        }
        
        // ----------------------------------------------------------------------------------------
        /** Easier Pitch Bend Event 
         * \param relative_value Ranging from -2 to 2, where 0 means no pitch change, -2 is two half-steps lower, etc.
         */
        void pitchBend(float relative_value, UInt8 midiChannelInUse)
        {
            int converted_value = PITCH_BEND_CENTER;
            
            if (relative_value < -2.0f || relative_value > 2.0f)
            {
                fprintf(stderr, "Pitch event cannot handle %f\n", relative_value);
            }
            
            if (relative_value != 0.0f)
            {
                converted_value = PITCH_BEND_CENTER + relative_value*(PITCH_BEND_HIGHEST - PITCH_BEND_LOWEST);
                
                if (converted_value < PITCH_BEND_LOWEST) converted_value = PITCH_BEND_LOWEST;
                else if (converted_value > PITCH_BEND_HIGHEST) converted_value = PITCH_BEND_HIGHEST;
            }
            
            UInt7 lsb = converted_value & 0x7F; // extract the 7 least significant bytes
            UInt7 msb = (converted_value >> 7) & 0x7F; // extract the 7 most significant bytes

            pitchBend(lsb, msb, midiChannelInUse);
            
        }

    };


// ----------------------------------------------------------------------------------------
// ----------------------------------------------------------------------------------------

MidiPlayer* player;

void init()
{
	player = new MidiPlayer();
}

void free()
{
	delete player;
}

void programChange(int channel, int instrument)
{
    player->programChange(instrument, channel);
}

void controlChange(int channel, int controller, int value)
{
    player->controlEvent(controller, value, channel);
}

void pitchBend(int channel, short value)
{
    value *= 128; // tg has the center at 64, but audiounit at 8192
    UInt7 lsb = value & 0x7F; // extract the 7 least significant bytes
    UInt7 msb = (value >> 7) & 0x7F; // extract the 7 most significant bytes
    player->pitchBend(lsb, msb, channel);
}

void noteOn(int pitchID, int volume, int channel)
{
    player->noteOn(pitchID, volume, channel);
}

void noteOff(int pitchID, int volume, int channel)
{
    player->noteOff(pitchID, channel);

}

/* -------------------------------------------------------------------------------------------------------------------------------- */

/* open port */

JNIEXPORT void JNICALL Java_org_herac_tuxguitar_player_impl_midiport_audiounit_MidiReceiverJNI_open(JNIEnv* env, jobject obj)
{
	
}
					
/* close port */
JNIEXPORT void JNICALL Java_org_herac_tuxguitar_player_impl_midiport_audiounit_MidiReceiverJNI_close(JNIEnv* env, jobject obj)
{

}

JNIEXPORT void JNICALL Java_org_herac_tuxguitar_player_impl_midiport_audiounit_MidiReceiverJNI_openDevice(JNIEnv* env, jobject obj)
{	
	init();
}

JNIEXPORT void JNICALL Java_org_herac_tuxguitar_player_impl_midiport_audiounit_MidiReceiverJNI_closeDevice(JNIEnv* env, jobject obj)
{
	free();
}

JNIEXPORT void JNICALL Java_org_herac_tuxguitar_player_impl_midiport_audiounit_MidiReceiverJNI_noteOn(JNIEnv* env, jobject ojb, jint channel, jint note, jint velocity)
{		

	noteOn(note, velocity, channel);
}

JNIEXPORT void JNICALL Java_org_herac_tuxguitar_player_impl_midiport_audiounit_MidiReceiverJNI_noteOff(JNIEnv* env, jobject ojb, jint channel, jint note, jint velocity)
{
	noteOff(note, velocity, channel);
}

JNIEXPORT void JNICALL Java_org_herac_tuxguitar_player_impl_midiport_audiounit_MidiReceiverJNI_programChange(JNIEnv* env, jobject ojb, jint channel, jint program)
{
	programChange(channel, program);
}

JNIEXPORT void JNICALL Java_org_herac_tuxguitar_player_impl_midiport_audiounit_MidiReceiverJNI_controlChange(JNIEnv* env, jobject ojb, jint channel, jint control, jint value)
{
	controlChange(channel, control, value);
}

JNIEXPORT void JNICALL Java_org_herac_tuxguitar_player_impl_midiport_audiounit_MidiReceiverJNI_pitchBend(JNIEnv* env, jobject ojb, jint channel, jint value)
{
	pitchBend(channel, value);

}
