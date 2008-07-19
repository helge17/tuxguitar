#include <stdio.h>

#include "org_herac_tuxguitar_player_impl_midiport_coreaudio_MidiReceiverJNI.h"

//#include <CoreServices/CoreServices.h> //for file stuff
#include <AudioUnit/AudioUnit.h>
#include <AudioToolbox/AudioToolbox.h> //for AUGraph
#include <Carbon/Carbon.h>

#include <iostream>

// ------------------------------------------------------------------------------------------------

// This call creates the Graph and the Synth unit...
OSStatus	CreateAUGraph (AUGraph& outGraph, AudioUnit& outSynth)
{
	OSStatus result;
	//create the nodes of the graph
	AUNode synthNode, limiterNode, outNode;
	
	ComponentDescription cd;
	cd.componentManufacturer = kAudioUnitManufacturer_Apple;
	cd.componentFlags = 0;
	cd.componentFlagsMask = 0;
	
	require_noerr (result = NewAUGraph (&outGraph), home);
	
	cd.componentType = kAudioUnitType_MusicDevice;
	cd.componentSubType = kAudioUnitSubType_DLSSynth;
	
	require_noerr (result = AUGraphNewNode (outGraph, &cd, 0, NULL, &synthNode), home);
	
	cd.componentType = kAudioUnitType_Effect;
	cd.componentSubType = kAudioUnitSubType_PeakLimiter;
	
	require_noerr (result = AUGraphNewNode (outGraph, &cd, 0, NULL, &limiterNode), home);
	
	cd.componentType = kAudioUnitType_Output;
	cd.componentSubType = kAudioUnitSubType_DefaultOutput;
	require_noerr (result = AUGraphNewNode (outGraph, &cd, 0, NULL, &outNode), home);
	
	require_noerr (result = AUGraphOpen (outGraph), home);
	
	require_noerr (result = AUGraphConnectNodeInput (outGraph, synthNode, 0, limiterNode, 0), home);
	require_noerr (result = AUGraphConnectNodeInput (outGraph, limiterNode, 0, outNode, 0), home);
	
	// ok we're good to go - get the Synth Unit...
	require_noerr (result = AUGraphGetNodeInfo(outGraph, synthNode, 0, 0, 0, &outSynth), home);
	
home:
		return result;
}

OSStatus PathToFSSpec(const char *filename, FSSpec &outSpec)
{
	FSRef fsRef;
	OSStatus result;
	require_noerr (result = FSPathMakeRef ((const UInt8*)filename, &fsRef, 0), home);
	require_noerr (result = FSGetCatalogInfo(&fsRef, kFSCatInfoNone, NULL, NULL, &outSpec, NULL), home);
	
home:
		return result;
}


// some MIDI constants:
enum {
	kMidiMessage_ControlChange 		= 0xB,
	kMidiMessage_ProgramChange 		= 0xC,
	kMidiMessage_PitchBend			= 0xE,
	kMidiMessage_BankMSBControl 	= 0,
	kMidiMessage_BankLSBControl		= 32,
	kMidiMessage_NoteOn 			= 0x9
};

AUGraph graph = 0;
AudioUnit synthUnit;
char* bankPath = 0;
//UInt8 midiChannelInUse = 0; //we're using midi channel 1...

void init()
{
	
	OSStatus result;
	const int midiChannelInUse = 0;
	
	require_noerr (result = CreateAUGraph (graph, synthUnit), home);
	
	// initialize and start the graph
	require_noerr (result = AUGraphInitialize (graph), home);
	
	
	//set our bank
	require_noerr (result = MusicDeviceMIDIEvent(synthUnit,
												 kMidiMessage_ControlChange << 4 | midiChannelInUse,
												 kMidiMessage_BankMSBControl, 0,
												 0/*sample offset*/), home);
	
	require_noerr (result = MusicDeviceMIDIEvent(synthUnit,
												 kMidiMessage_ProgramChange << 4 | midiChannelInUse,
												 0/*prog change num*/, 0,
												 0/*sample offset*/), home);
	
	require_noerr (result = AUGraphStart (graph), home);
home:
		return;
}
void free()
{
	
	if (graph)
	{
		AUGraphStop (graph); // stop playback - AUGraphDispose will do that for us but just showing you what to do
		DisposeAUGraph (graph);
	}
}

void programChange(int channel, int instrument)
{
	OSStatus result;
	UInt32 progamChange = kMidiMessage_ProgramChange << 4 | channel;
	
	require_noerr (result = MusicDeviceMIDIEvent(synthUnit, progamChange, instrument, 0, 0), home);

home:
	return;
}

void controlChange(int channel, int controller, int value)
{
    /*
	// ignore these values, they mess up playback. i have no idea why TuxGuitar sends them or what they are supposed to do.
	if(controller==100 or controller==101)
	{
		return;
	}
	*/
	OSStatus result;
	UInt32 controlChange = kMidiMessage_ControlChange << 4 | channel;
	
	require_noerr (result = MusicDeviceMIDIEvent(synthUnit, controlChange, controller, value, 0), home);
	
home:
		return;
}

void pitchBend(int channel, short value)
{
	OSStatus result;
	UInt32 pitchChange = kMidiMessage_PitchBend << 4 | channel;

	require_noerr (result = MusicDeviceMIDIEvent(synthUnit, pitchChange, 0, value, 0), home);
home:
		return;
}

void noteOn(int pitchID, int volume, int channel)
{
	OSStatus result;
	UInt32 noteOnCommand = 	kMidiMessage_NoteOn << 4 | channel;
	
	/* note on */
	require_noerr (result = MusicDeviceMIDIEvent(synthUnit, noteOnCommand, pitchID, volume, 0), home);
	
home:
		
		return;
}

void noteOff(int pitchID, int volume, int channel)
{
	
	OSStatus result;
	UInt32 noteOffCommand = kMidiMessage_NoteOn << 4 | channel;
	
	// note off
	require_noerr (result = MusicDeviceMIDIEvent(synthUnit, noteOffCommand, pitchID, 0, 0), home);
	
home:
		return;
}

/* -------------------------------------------------------------------------------------------------------------------------------- */

/* open port */

JNIEXPORT void JNICALL Java_org_herac_tuxguitar_player_impl_midiport_coreaudio_MidiReceiverJNI_open(JNIEnv* env, jobject obj)
{
	
}
					
/* close port */
JNIEXPORT void JNICALL Java_org_herac_tuxguitar_player_impl_midiport_coreaudio_MidiReceiverJNI_close(JNIEnv* env, jobject obj)
{

}
/*					 
JNIEXPORT void JNICALL Java_org_herac_tuxguitar_player_impl_midiport_coreaudio_MidiReceiverJNI_findDevices(JNIEnv* env, jobject obj)
{	
	
	jstring name = env->NewStringUTF( "CoreAudio midi playback");

    //Add a new MidiDevice to the java class		
    jclass cl = env->GetObjectClass( obj);			 
    jmethodID mid = env->GetMethodID( cl, "addDevice", "(Ljava/lang/String;II)V");  	
    if (mid != 0){
        env->CallVoidMethod( obj, mid,name,client,port);
    }			

}
*/
JNIEXPORT void JNICALL Java_org_herac_tuxguitar_player_impl_midiport_coreaudio_MidiReceiverJNI_openDevice(JNIEnv* env, jobject obj)
{	
	init();
}

JNIEXPORT void JNICALL Java_org_herac_tuxguitar_player_impl_midiport_coreaudio_MidiReceiverJNI_closeDevice(JNIEnv* env, jobject obj)
{
	free();
}

JNIEXPORT void JNICALL Java_org_herac_tuxguitar_player_impl_midiport_coreaudio_MidiReceiverJNI_noteOn(JNIEnv* env, jobject ojb, jint channel, jint note, jint velocity)
{		

	noteOn(note, velocity, channel);
}

JNIEXPORT void JNICALL Java_org_herac_tuxguitar_player_impl_midiport_coreaudio_MidiReceiverJNI_noteOff(JNIEnv* env, jobject ojb, jint channel, jint note, jint velocity)
{
	noteOff(note, velocity, channel);
}

JNIEXPORT void JNICALL Java_org_herac_tuxguitar_player_impl_midiport_coreaudio_MidiReceiverJNI_programChange(JNIEnv* env, jobject ojb, jint channel, jint program)
{
	programChange(channel, program);
}

JNIEXPORT void JNICALL Java_org_herac_tuxguitar_player_impl_midiport_coreaudio_MidiReceiverJNI_controlChange(JNIEnv* env, jobject ojb, jint channel, jint control, jint value)
{
	controlChange(channel, control, value);
}

JNIEXPORT void JNICALL Java_org_herac_tuxguitar_player_impl_midiport_coreaudio_MidiReceiverJNI_pitchBend(JNIEnv* env, jobject ojb, jint channel, jint value)
{
	pitchBend(channel, value);

}
