#include <stdio.h>
#include <string.h>
#include <alsa/asoundlib.h>
#include "org_herac_tuxguitar_player_impl_midiport_alsa_MidiSystem.h"

typedef struct {
	snd_seq_t *seq;
	snd_seq_addr_t *address;
	snd_seq_event_t ev;
} handle_t;

JNIEXPORT jlong JNICALL Java_org_herac_tuxguitar_player_impl_midiport_alsa_MidiSystem_malloc(JNIEnv* env, jobject obj)
{
	jlong ptr = 0;
	
	handle_t *handle = (handle_t *) malloc( sizeof(handle_t) );
	handle->seq = NULL;
	handle->address = NULL;
	
	memcpy(&ptr, &handle, sizeof( handle ));
	
	return ptr;
}

JNIEXPORT void JNICALL Java_org_herac_tuxguitar_player_impl_midiport_alsa_MidiSystem_free(JNIEnv* env, jobject obj, jlong ptr)
{
	handle_t *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if(handle != NULL){
		free( handle );
	}
}

JNIEXPORT void JNICALL Java_org_herac_tuxguitar_player_impl_midiport_alsa_MidiSystem_open(JNIEnv* env, jobject obj, jlong ptr)
{
	handle_t *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if(handle != NULL && handle->seq == NULL){
		//---open sequencer
		if(snd_seq_open(&handle->seq, "default", SND_SEQ_OPEN_DUPLEX, 0) < 0){
			handle->seq = NULL;
			return;
		}
		//---create port
		snd_seq_port_info_t *pinfo;
		snd_seq_port_info_alloca(&pinfo);
		snd_seq_port_info_set_port(pinfo,0);
		snd_seq_port_info_set_name(pinfo, "TuxGuitar");
		snd_seq_port_info_set_type(pinfo,SND_SEQ_PORT_TYPE_MIDI_GENERIC | SND_SEQ_PORT_TYPE_APPLICATION);
		if(snd_seq_create_port(handle->seq, pinfo) < 0){
			handle->seq = NULL;
			return;
		}
	}
}

JNIEXPORT void JNICALL Java_org_herac_tuxguitar_player_impl_midiport_alsa_MidiSystem_close(JNIEnv* env, jobject obj, jlong ptr)
{
	handle_t *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if(handle != NULL && handle->seq != NULL){
		snd_seq_close(handle->seq);
		handle->seq = NULL;
	}
}

JNIEXPORT void JNICALL Java_org_herac_tuxguitar_player_impl_midiport_alsa_MidiSystem_findPorts(JNIEnv* env, jobject obj, jlong ptr)
{
	handle_t *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if(handle != NULL && handle->seq != NULL){
		int bits = (SND_SEQ_PORT_CAP_WRITE | SND_SEQ_PORT_CAP_SUBS_WRITE);
		snd_seq_client_info_t *client_info;
		snd_seq_port_info_t *port_info;
		snd_seq_client_info_alloca(&client_info);
		snd_seq_port_info_alloca(&port_info);
		snd_seq_client_info_set_client(client_info, -1);
		while (snd_seq_query_next_client(handle->seq, client_info) >= 0) {
			snd_seq_port_info_set_client(port_info, snd_seq_client_info_get_client(client_info));
			snd_seq_port_info_set_port(port_info, -1);
			while (snd_seq_query_next_port(handle->seq, port_info) >= 0) {
				if ( (snd_seq_port_info_get_capability(port_info) & bits ) == bits ){
					jstring name = (*env)->NewStringUTF(env, snd_seq_port_info_get_name(port_info));
					jint client  = (snd_seq_port_info_get_addr(port_info))->client;
					jint port    = (snd_seq_port_info_get_addr(port_info))->port;
					
					//Add a new MidiPort to the java class
					jclass cl = (*env)->GetObjectClass(env, obj);
					jmethodID mid = (*env)->GetMethodID(env, cl, "addPort", "(Ljava/lang/String;II)V");
					if (mid != 0){
						(*env)->CallVoidMethod(env, obj, mid,name,client,port);
					}
				}
			}
		}
	}
}

JNIEXPORT void JNICALL Java_org_herac_tuxguitar_player_impl_midiport_alsa_MidiSystem_openPort(JNIEnv* env, jobject obj, jlong ptr, jint client, jint port)
{
	handle_t *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if(handle != NULL && handle->seq != NULL && handle->address == NULL){
		if ( snd_seq_connect_to(handle->seq,0, client,port) >= 0 ) {
			handle->address = (snd_seq_addr_t *) malloc( sizeof(snd_seq_addr_t) );
			handle->address->client = client;
			handle->address->port = port;
		}
	}
}

JNIEXPORT void JNICALL Java_org_herac_tuxguitar_player_impl_midiport_alsa_MidiSystem_closePort(JNIEnv* env, jobject obj, jlong ptr)
{
	handle_t *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if(handle != NULL && handle->seq != NULL && handle->address != NULL){
		snd_seq_disconnect_to(handle->seq, 0, handle->address->client,handle->address->port);
		free ( handle->address );
		handle->address = NULL;
	}
}

JNIEXPORT void JNICALL Java_org_herac_tuxguitar_player_impl_midiport_alsa_MidiSystem_noteOn(JNIEnv* env, jobject obj, jlong ptr, jint channel, jint note, jint velocity)
{
	handle_t *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if(handle != NULL && handle->seq != NULL && handle->address != NULL){
		snd_seq_ev_clear(&handle->ev);
		snd_seq_ev_set_direct(&handle->ev);
		snd_seq_ev_set_source(&handle->ev,0);
		snd_seq_ev_set_dest(&handle->ev,handle->address->client,handle->address->port);
		snd_seq_ev_set_noteon(&handle->ev, channel , note , velocity);
		if(snd_seq_event_output_direct(handle->seq, &handle->ev) < 0){
			return;
		}
		snd_seq_drain_output(handle->seq);
	}
}

JNIEXPORT void JNICALL Java_org_herac_tuxguitar_player_impl_midiport_alsa_MidiSystem_noteOff(JNIEnv* env, jobject obj, jlong ptr, jint channel, jint note, jint velocity)
{
	handle_t *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if(handle != NULL && handle->seq != NULL && handle->address != NULL){
		snd_seq_ev_clear(&handle->ev);
		snd_seq_ev_set_direct(&handle->ev);
		snd_seq_ev_set_source(&handle->ev,0);
		snd_seq_ev_set_dest(&handle->ev,handle->address->client,handle->address->port);
		snd_seq_ev_set_noteoff(&handle->ev, channel , note , velocity);
		if(snd_seq_event_output_direct(handle->seq, &handle->ev) < 0){
			return;
		}
		snd_seq_drain_output(handle->seq);
	}
}

JNIEXPORT void JNICALL Java_org_herac_tuxguitar_player_impl_midiport_alsa_MidiSystem_programChange(JNIEnv* env, jobject obj, jlong ptr, jint channel, jint program)
{
	handle_t *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if(handle != NULL && handle->seq != NULL && handle->address != NULL){
		snd_seq_ev_clear(&handle->ev);
		snd_seq_ev_set_direct(&handle->ev);
		snd_seq_ev_set_source(&handle->ev,0);
		snd_seq_ev_set_dest(&handle->ev,handle->address->client,handle->address->port);
		snd_seq_ev_set_pgmchange(&handle->ev,channel,program);
		if(snd_seq_event_output_direct(handle->seq, &handle->ev) < 0){
			return;
		}
		snd_seq_drain_output(handle->seq);
	}
}

JNIEXPORT void JNICALL Java_org_herac_tuxguitar_player_impl_midiport_alsa_MidiSystem_controlChange(JNIEnv* env, jobject obj, jlong ptr, jint channel, jint control, jint value)
{
	handle_t *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if(handle != NULL && handle->seq != NULL && handle->address != NULL){
		snd_seq_ev_clear(&handle->ev);
		snd_seq_ev_set_direct(&handle->ev);
		snd_seq_ev_set_source(&handle->ev,0);
		snd_seq_ev_set_dest(&handle->ev,handle->address->client,handle->address->port);
		snd_seq_ev_set_controller(&handle->ev,channel,control,value);
		if(snd_seq_event_output_direct(handle->seq, &handle->ev) < 0){
			return;
		}
		snd_seq_drain_output(handle->seq);
	}
}

JNIEXPORT void JNICALL Java_org_herac_tuxguitar_player_impl_midiport_alsa_MidiSystem_pitchBend(JNIEnv* env, jobject obj, jlong ptr, jint channel, jint value)
{
	handle_t *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if(handle != NULL && handle->seq != NULL && handle->address != NULL){
		snd_seq_ev_clear(&handle->ev);
		snd_seq_ev_set_direct(&handle->ev);
		snd_seq_ev_set_source(&handle->ev,0);
		snd_seq_ev_set_dest(&handle->ev,handle->address->client,handle->address->port);
		snd_seq_ev_set_pitchbend(&handle->ev,channel, ((value * 128) - 8192));
		if(snd_seq_event_output_direct(handle->seq, &handle->ev) < 0){
			return;
		}
		snd_seq_drain_output(handle->seq);
	}
}
