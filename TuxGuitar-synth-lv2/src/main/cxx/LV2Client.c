#include <stdint.h>
#include <stdlib.h>
#include <string.h>    //strlen
#include <iostream>
#include <pthread.h>

#include "LV2.h"
#include "LV2Client.h"
#include "LV2UI.h"
#include "LV2World.h"
#include "LV2Plugin.h"
#include "LV2Feature.h"
#include "LV2Instance.h"
#include "LV2Logger.h"
#include "LV2Socket.h"
#include "LV2Lock.h"

#define CMD_GET_STATE 1
#define CMD_SET_STATE 2
#define CMD_GET_CONTROL_PORT_VALUE 3
#define CMD_SET_CONTROL_PORT_VALUE 4
#define CMD_PROCESS_MIDI_MESSAGES 5
#define CMD_PROCESS_AUDIO 6
#define CMD_UI_OPEN 7
#define CMD_UI_CLOSE 8
#define CMD_UI_FOCUS 9
#define CMD_UI_IS_OPEN 10
#define CMD_UI_IS_AVAILABLE 11

int main(int argc, char *argv[]) 
{
	LV2Logger_log("LV2Client -> starting\n");
	
	LV2Client *handle = (LV2Client *) malloc( sizeof(LV2Client) );
	LV2Client_createConfig(handle);
	LV2Client_parseArguments(handle, argc, argv);
	LV2Socket_create(&(handle->socket), handle->serverPort);
	
	if( handle->socket->connected) {
		LV2Lock_malloc(&handle->lock);
		LV2World_malloc(&handle->world);
		LV2World_getPluginByURI(handle->world, &handle->plugin, handle->pluginURI);
		LV2Feature_malloc(&(handle->feature), handle->config);
		LV2Instance_malloc(&handle->instance, handle->plugin, handle->feature, handle->config);
		LV2UI_malloc(&handle->ui, handle->feature, handle->instance, handle->lock);
		LV2Client_createBuffers(handle);
		LV2Feature_init(handle->feature, handle->instance);
		if( handle->plugin != NULL && handle->plugin->lilvPlugin != NULL ) {
			pthread_t thread;
			if( pthread_create(&thread, NULL, LV2Client_processCommandsThread, handle)) {
				return 1;
			}
			
			while(handle->socket->connected) {
				LV2UI_process(handle->ui);
			}
			
			pthread_join(thread, NULL);
		}
		
		LV2Client_destroyBuffers(handle);
		LV2UI_free(&handle->ui);
		LV2Instance_free(&handle->instance);
		LV2Feature_free(&handle->feature);
		LV2Plugin_free(&handle->plugin);
		LV2World_free(&handle->world);
		LV2Lock_free(&handle->lock);
	}
	
    LV2Socket_destroy(&handle->socket);
	LV2Logger_log("LV2Client -> ended\n");
	
	free(handle);

    return 0;
}

void LV2Client_createConfig(LV2Client *handle)
{
	handle->config = (LV2Config *) malloc( sizeof(LV2Config) );
	handle->config->sampleRate = 44100.00;
	handle->config->bufferSize = 0;
	handle->config->eventBufferSize = 32768;
}

void LV2Client_createBuffers(LV2Client *handle)
{
	LV2Int32 inputLength = 0;
	LV2Int32 outputLength = 0;
	LV2Plugin_getAudioInputPortCount(handle->plugin, &inputLength);
	LV2Plugin_getAudioOutputPortCount(handle->plugin, &outputLength);

	handle->inputsBuffer = (float **)malloc((sizeof(float *) * inputLength));
	for(int i = 0; i < inputLength; i++) {
		handle->inputsBuffer[i] = (float *) malloc((sizeof(float) * handle->instance->config->bufferSize));
	}
	
	handle->outputsBuffer = (float **)malloc((sizeof(float *) * outputLength));
	for(int i = 0; i < outputLength; i++) {
		handle->outputsBuffer[i] = (float *) malloc((sizeof(float) * handle->instance->config->bufferSize));
	}
}

void LV2Client_destroyBuffers(LV2Client *handle)
{
	LV2Int32 inputLength = 0;
	LV2Int32 outputLength = 0;
	LV2Plugin_getAudioInputPortCount(handle->plugin, &inputLength);
	LV2Plugin_getAudioOutputPortCount(handle->plugin, &outputLength);

	delete [] handle->inputsBuffer;
	delete [] handle->outputsBuffer;

	handle->inputsBuffer = NULL;
	handle->outputsBuffer = NULL;
}

void* LV2Client_processCommandsThread(void* ptr)
{
	LV2Client *handle = (LV2Client *) ptr;
	
	LV2Logger_log("LV2Client -> sending session ID %d\n", handle->sessionId);
	LV2Socket_write(handle->socket, &handle->sessionId, 4);
	
	int command = 0;
	while(handle->socket->connected) {
		command = 0;
		
		LV2Socket_read(handle->socket , &command , 4);
		
		
		if( LV2Lock_lock(handle->lock) ) {
			LV2Client_processCommand(handle, command);
			LV2Lock_unlock(handle->lock);
		}
	}
	return NULL;
}

void LV2Client_parseArguments(LV2Client *handle, int argc , char *argv[])
{
	LV2Logger_log("LV2Client -> parsing arguments\n");
	
	handle->sessionId = atoi(argv[1]);
	handle->serverPort = atoi(argv[2]);
	handle->config->bufferSize = atoi(argv[3]);
	handle->pluginURI = (const char*) argv[4];
	
	LV2Logger_log("LV2Client -> pluginURI: %s\n", handle->pluginURI);
}

void LV2Client_processCommand(LV2Client *handle, int command) 
{
	switch(command) {
		case CMD_GET_STATE:
			LV2Client_processGetState(handle);
		break;
		case CMD_SET_STATE:
			LV2Client_processSetState(handle);
		break;
		case CMD_GET_CONTROL_PORT_VALUE:
			LV2Client_processGetControlPortValueCommand(handle);
		break;
		case CMD_SET_CONTROL_PORT_VALUE:
			LV2Client_processSetControlPortValueCommand(handle);
		break;
		case CMD_PROCESS_MIDI_MESSAGES:
			LV2Client_processProcessMidiMessagesCommand(handle);
		break;
		case CMD_PROCESS_AUDIO:
			LV2Client_processProcessAudioCommand(handle);
		break;
		case CMD_UI_OPEN:
			LV2Client_processOpenUICommand(handle);
		break;
		case CMD_UI_CLOSE:
			LV2Client_processCloseUICommand(handle);
		break;
		case CMD_UI_FOCUS:
			LV2Client_processFocusUICommand(handle);
		break;
		case CMD_UI_IS_OPEN:
			LV2Client_processUIIsOpenCommand(handle);
		break;
		case CMD_UI_IS_AVAILABLE:
			LV2Client_processUIIsAvailableCommand(handle);
		break;
	}
}

void LV2Client_processGetState(LV2Client *handle)
{
	const char *state = NULL;
	LV2Instance_getState(handle->instance, &state);
	
	LV2Int32 length = strlen(state);
	LV2Socket_write(handle->socket, &length, 4);
	LV2Socket_write(handle->socket, (void *) state, length);
}

void LV2Client_processSetState(LV2Client *handle)
{
	LV2Int32 length = 0;
	LV2Socket_read(handle->socket, &length, 4);

	const char *state = (const char *) malloc(sizeof(char) * length);
	LV2Socket_read(handle->socket, (void *) state, length);
	
	LV2Instance_setState(handle->instance, state);
}

void LV2Client_processGetControlPortValueCommand(LV2Client *handle)
{
	LV2Int32 index = 0;
	LV2Socket_read(handle->socket, &index, 4);
	
	float value = 0;
	LV2Instance_getControlPortValue(handle->instance, index, &value);
	
	LV2Socket_write(handle->socket, &value, 4);
}

void LV2Client_processSetControlPortValueCommand(LV2Client *handle)
{
	LV2Int32 index = 0;
	float value = 0;
	float currentValue = 0;
	LV2Socket_read(handle->socket, &index, 4);
	LV2Socket_read(handle->socket, &value, 4);
	
	LV2Instance_getControlPortValue(handle->instance, index, &currentValue);
	if( value != currentValue ) {
		LV2Instance_setControlPortValue(handle->instance, index, value);
	}

	LV2UI_setControlPortValue(handle->ui, index, value);
}

void LV2Client_processProcessMidiMessagesCommand(LV2Client *handle)
{
	LV2Int32 length = 0;
	LV2Socket_read(handle->socket, &length, 4);
	
	unsigned char** messages = (unsigned char **) malloc((sizeof(unsigned char *) * length));
	for(int i = 0; i < length; i++) {
		messages[i] = (unsigned char *) malloc((sizeof(unsigned char) * 3));
		LV2Socket_read(handle->socket, messages[i], 3);
	}
	
	LV2Instance_setMidiMessages(handle->instance, messages, length);

	delete [] messages;
}

void LV2Client_processProcessAudioCommand(LV2Client *handle)
{
	LV2Int32 inputLength = 0;
	LV2Int32 outputLength = 0;
	LV2Plugin_getAudioInputPortCount(handle->plugin, &inputLength);
	LV2Plugin_getAudioOutputPortCount(handle->plugin, &outputLength);
	
	for(int i = 0; i < inputLength; i++) {
		LV2Socket_read(handle->socket, handle->inputsBuffer[i], (4 * handle->instance->config->bufferSize));
	}
	
	LV2Instance_processAudio(handle->instance, handle->inputsBuffer, handle->outputsBuffer);
	
	for(int i = 0; i < outputLength; i++) {
		LV2Socket_write(handle->socket, handle->outputsBuffer[i], (4 * handle->instance->config->bufferSize));
	}

	bool value = 0;
	LV2UI_isUpdated(handle->ui, &value);
	LV2Socket_write(handle->socket, &value, 1);
	if( value ) {
		LV2UI_setUpdated(handle->ui, false);
	}
	
	// Process features
	LV2Feature_processAudio(handle->feature);

	// Process UI Controls
	LV2UI_processAudio(handle->ui);
}

void LV2Client_processOpenUICommand(LV2Client *handle)
{
	LV2UI_open(handle->ui);
}

void LV2Client_processCloseUICommand(LV2Client *handle)
{
	LV2UI_close(handle->ui);
}

void LV2Client_processFocusUICommand(LV2Client *handle)
{
	LV2UI_focus(handle->ui);
}

void LV2Client_processUIIsOpenCommand(LV2Client *handle)
{
	bool value = 0;
	LV2UI_isOpen(handle->ui, &value);
	LV2Socket_write(handle->socket, &value, 1);
}

void LV2Client_processUIIsAvailableCommand(LV2Client *handle)
{
	bool value = 0;
	LV2UI_isAvailable(handle->ui, &value);
	LV2Socket_write(handle->socket, &value, 1);
}

