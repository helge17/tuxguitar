#include <stdio.h>
#include <string.h>
#include <unistd.h>
#include <stdlib.h>
#include <fcntl.h>
#include <sys/soundcard.h>
#include "org_herac_tuxguitar_player_impl_midiport_oss_MidiSystem.h"

typedef struct {
	int fd;
	int port;
} handle_t;

JNIEXPORT jlong JNICALL Java_org_herac_tuxguitar_player_impl_midiport_oss_MidiSystem_malloc(JNIEnv* env, jobject obj)
{
	jlong ptr = 0;
	
	handle_t *handle = (handle_t *) malloc( sizeof(handle_t) );
	handle->fd = -1;
	handle->port = -1;
	
	memcpy(&ptr, &handle, sizeof( handle ));
	
	return ptr;
}

JNIEXPORT void JNICALL Java_org_herac_tuxguitar_player_impl_midiport_oss_MidiSystem_free(JNIEnv* env, jobject obj, jlong ptr)
{
	handle_t *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if(handle != NULL){
		free( handle );
	}
}

JNIEXPORT void JNICALL Java_org_herac_tuxguitar_player_impl_midiport_oss_MidiSystem_open(JNIEnv* env, jobject obj, jlong ptr, jstring str)
{
	handle_t *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if(handle != NULL && handle->fd == -1)
	{
		const char *device = (*env)->GetStringUTFChars(env, str, 0);
		
		handle->port = -1;
		if (( handle->fd = open (device, O_WRONLY)) == -1)
		{
			handle = NULL;
			perror (device);
		}
		
		(*env)->ReleaseStringUTFChars(env, str, device);
	}
}

JNIEXPORT void JNICALL Java_org_herac_tuxguitar_player_impl_midiport_oss_MidiSystem_close(JNIEnv* env, jobject obj, jlong ptr)
{
	handle_t *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if(handle != NULL && handle->fd >= 0)
	{
		close(handle->fd);
		handle->fd = -1;
		handle->port = -1;
	}
}

JNIEXPORT void JNICALL Java_org_herac_tuxguitar_player_impl_midiport_oss_MidiSystem_findPorts(JNIEnv* env, jobject obj, jlong ptr)
{	
	handle_t *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if(handle != NULL && handle->fd >= 0)
	{
		int i;
		int count;
		struct midi_info info;
		ioctl(handle->fd, SNDCTL_SEQ_NRMIDIS, &count);
		for ( i = 0; i < count ; i++ )
		{
			info.device = i;
			ioctl(handle->fd, SNDCTL_MIDI_INFO, &info);
			
			//Add a new MidiDevice to the java class
			jint device  = info.device;
			jstring name = (*env)->NewStringUTF(env, info.name);
			jclass cl = (*env)->GetObjectClass(env, obj);
			jmethodID mid = (*env)->GetMethodID(env, cl, "addPort", "(Ljava/lang/String;I)V");
			if (mid != 0){
				(*env)->CallVoidMethod(env, obj, mid,name,device);
			}
		}
	}
}

JNIEXPORT void JNICALL Java_org_herac_tuxguitar_player_impl_midiport_oss_MidiSystem_openPort(JNIEnv* env, jobject obj, jlong ptr, jint device)
{
	handle_t *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if(handle != NULL)
	{
		handle->port = device;
	}
}

JNIEXPORT void JNICALL Java_org_herac_tuxguitar_player_impl_midiport_oss_MidiSystem_closePort(JNIEnv* env, jobject obj, jlong ptr)
{
	handle_t *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if(handle != NULL)
	{
		handle->port = -1;
	}
}

JNIEXPORT void JNICALL Java_org_herac_tuxguitar_player_impl_midiport_oss_MidiSystem_noteOn(JNIEnv* env, jobject obj, jlong ptr, jint channel, jint note, jint velocity)
{
	handle_t *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if(handle != NULL && handle->fd >= 0 && handle->port >= 0)
	{
		unsigned char packet[4] = {SEQ_MIDIPUTC, 0, handle->port, 1};
		packet[1] = (0x90 | channel);
		write(handle->fd, packet, sizeof(packet));
		packet[1] = note;
		write(handle->fd, packet, sizeof(packet));
		packet[1] = velocity;
		write(handle->fd, packet, sizeof(packet));
	}
}

JNIEXPORT void JNICALL Java_org_herac_tuxguitar_player_impl_midiport_oss_MidiSystem_noteOff(JNIEnv* env, jobject obj, jlong ptr, jint channel, jint note, jint velocity)
{
	handle_t *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if(handle != NULL && handle->fd >= 0 && handle->port >= 0)
	{
		unsigned char packet[4] = {SEQ_MIDIPUTC, 0, handle->port, 0};
		packet[1] = (0x80 | channel);
		write(handle->fd, packet, sizeof(packet));
		packet[1] = note;
		write(handle->fd, packet, sizeof(packet));
		packet[1] = velocity;
		write(handle->fd, packet, sizeof(packet));
	}
}

JNIEXPORT void JNICALL Java_org_herac_tuxguitar_player_impl_midiport_oss_MidiSystem_programChange(JNIEnv* env, jobject obj, jlong ptr, jint channel, jint program)
{
	handle_t *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if(handle != NULL && handle->fd >= 0 && handle->port >= 0)
	{
		unsigned char packet[4] = {SEQ_MIDIPUTC, 0, handle->port, 0};
		packet[1] = (0xC0 | channel);
		write(handle->fd, packet, sizeof(packet));
		packet[1] = program;
		write(handle->fd, packet, sizeof(packet));
	}
}

JNIEXPORT void JNICALL Java_org_herac_tuxguitar_player_impl_midiport_oss_MidiSystem_controlChange(JNIEnv* env, jobject obj, jlong ptr, jint channel, jint control, jint value)
{
	handle_t *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if(handle != NULL && handle->fd >= 0 && handle->port >= 0)
	{
		unsigned char packet[4] = {SEQ_MIDIPUTC, 0, handle->port, 0};
		packet[1] = (0xB0 | channel);
		write(handle->fd, packet, sizeof(packet));
		packet[1] = control;
		write(handle->fd, packet, sizeof(packet));
		packet[1] = value;
		write(handle->fd, packet, sizeof(packet));
	}
}

JNIEXPORT void JNICALL Java_org_herac_tuxguitar_player_impl_midiport_oss_MidiSystem_pitchBend(JNIEnv* env, jobject obj, jlong ptr, jint channel, jint value)
{
	handle_t *handle = NULL;
	memcpy(&handle, &ptr, sizeof(handle));
	if(handle != NULL && handle->fd >= 0 && handle->port >= 0)
	{
		unsigned char packet[4] = {SEQ_MIDIPUTC, 0, handle->port, 0};
		packet[1] = (0xE0 | channel);
		write(handle->fd, packet, sizeof(packet));
		packet[1] = 0;
		write(handle->fd, packet, sizeof(packet));
		packet[1] = value;
		write(handle->fd, packet, sizeof(packet));
	}
}
