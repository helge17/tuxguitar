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
#include "LV2Instance.h"
#include "LV2Logger.h"
#include "LV2Socket.h"

#define CMD_GET_CONTROL_PORT_VALUE 1
#define CMD_SET_CONTROL_PORT_VALUE 2
#define CMD_PROCESS_MIDI_MESSAGES 3
#define CMD_PROCESS_AUDIO 4
#define CMD_UI_OPEN 5
#define CMD_UI_CLOSE 6
#define CMD_UI_IS_OPEN 7
#define CMD_UI_IS_AVAILABLE 8
#define CMD_UI_IS_UPDATED 9

int main(int argc, char *argv[]) 
{
	LV2Logger_log("LV2Client -> starting\n");
	
	LV2Client *handle = (LV2Client *) malloc( sizeof(LV2Client) );
	LV2Client_parseArguments(handle, argc, argv);

	LV2Socket_create(&(handle->socket), handle->serverPort);
	
	if( handle->socket->connected) {
		LV2World_malloc(&handle->world);
		LV2World_getPluginByURI(handle->world, &handle->plugin, handle->pluginURI);
		LV2Instance_malloc(&handle->instance, handle->plugin, handle->bufferSize);
		LV2UI_malloc(&handle->ui, handle->instance);
		LV2Client_createBuffers(handle);

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
		LV2Plugin_free(&handle->plugin);
		LV2World_free(&handle->world);
	}
	
    LV2Socket_destroy(&(handle->socket));
	LV2Logger_log("LV2Client -> ended\n");
	
	free(handle);

    return 0;
}

void LV2Client_createBuffers(LV2Client *handle)
{
	LV2Int32 inputLength = 0;
	LV2Int32 outputLength = 0;
	LV2Plugin_getAudioInputPortCount(handle->plugin, &inputLength);
	LV2Plugin_getAudioOutputPortCount(handle->plugin, &outputLength);

	handle->inputsBuffer = (float **)malloc((sizeof(float *) * inputLength));
	for(int i = 0; i < inputLength; i++) {
		handle->inputsBuffer[i] = (float *) malloc((sizeof(float) * handle->instance->bufferSize));
	}
	
	handle->outputsBuffer = (float **)malloc((sizeof(float *) * outputLength));
	for(int i = 0; i < outputLength; i++) {
		handle->outputsBuffer[i] = (float *) malloc((sizeof(float) * handle->instance->bufferSize));
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
		
		LV2Client_processCommand(handle, command);
	}
	return NULL;
}

void LV2Client_parseArguments(LV2Client *handle, int argc , char *argv[])
{
	LV2Logger_log("LV2Client -> parsing arguments\n");
	
	handle->sessionId = atoi(argv[1]);
	handle->serverPort = atoi(argv[2]);
	handle->bufferSize = atoi(argv[3]);
	handle->pluginURI = (const char*) argv[4];
	
	LV2Logger_log("LV2Client -> pluginURI: %s\n", handle->pluginURI);
}

void LV2Client_processCommand(LV2Client *handle, int command) 
{
	switch(command) {
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
		case CMD_UI_IS_OPEN:
			LV2Client_processUIIsOpenCommand(handle);
		break;
		case CMD_UI_IS_AVAILABLE:
			LV2Client_processUIIsAvailableCommand(handle);
		break;
		case CMD_UI_IS_UPDATED:
			LV2Client_processUIIsUpdatedCommand(handle);
		break;
	}
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
	int length = 0;
	LV2Socket_read(handle->socket, &length, 4);
	
	unsigned char** messages = (unsigned char **) malloc((sizeof(unsigned char *) * length));
	for(int i = 0; i < length; i++) {
		messages[i] = (unsigned char *) malloc((sizeof(unsigned char) * 4));
		LV2Socket_read(handle->socket, messages[i], 4);
	}
	
	//LV2Instance_setMidiMessages(handle->instance, ????);	
	
	delete [] messages;
	
}

void LV2Client_processProcessAudioCommand(LV2Client *handle)
{
	LV2Int32 inputLength = 0;
	LV2Int32 outputLength = 0;
	LV2Plugin_getAudioInputPortCount(handle->plugin, &inputLength);
	LV2Plugin_getAudioOutputPortCount(handle->plugin, &outputLength);

	for(int i = 0; i < inputLength; i++) {
		LV2Socket_read(handle->socket, handle->inputsBuffer[i], (4 * handle->instance->bufferSize));
	}
	
	LV2Instance_processAudio(handle->instance, handle->inputsBuffer, handle->outputsBuffer);

	for(int i = 0; i < outputLength; i++) {
		LV2Socket_write(handle->socket, handle->outputsBuffer[i], (4 * handle->instance->bufferSize));
	}

	bool value = 0;
	LV2UI_isUpdated(handle->ui, &value);
	LV2Socket_write(handle->socket, &value, 1);
	if( value ) {
		LV2UI_setUpdated(handle->ui, false);
	}
	/*
	float** inputs = (float **)malloc((sizeof(float *) * inputLength));
	for(int i = 0; i < inputLength; i++) {
		inputs[i] = (float *) malloc((sizeof(float) * handle->instance->bufferSize));
		LV2Socket_read(handle->socket, inputs[i], (4 * handle->instance->bufferSize));
	}
	
	float** outputs = (float **)malloc((sizeof(float *) * outputLength));
	for(int i = 0; i < outputLength; i++) {
		outputs[i] = (float *) malloc((sizeof(float) * handle->instance->bufferSize));
	}
	
	LV2Instance_processAudio(handle->instance, inputs, outputs);

	for(int i = 0; i < outputLength; i++) {
		LV2Socket_write(handle->socket, outputs[i], (4 * handle->instance->bufferSize));
	}
	
	delete [] inputs;
	delete [] outputs;
	*/
}

void LV2Client_processOpenUICommand(LV2Client *handle)
{
	LV2UI_open(handle->ui);
}

void LV2Client_processCloseUICommand(LV2Client *handle)
{
	LV2UI_close(handle->ui);
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

void LV2Client_processUIIsUpdatedCommand(LV2Client *handle)
{
	bool value = 0;
	LV2UI_isUpdated(handle->ui, &value);
	if( value ) {
		LV2UI_setUpdated(handle->ui, false);
	}
	LV2Socket_write(handle->socket, &value, 1);
}
