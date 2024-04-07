#include <stdint.h>
#include <stdlib.h>
#include <string.h>    //strlen
#include <iostream>
#include <pthread.h>
#include "VSTLogger.h"
#include "VSTClient.h"
#include "VSTPlugin.h"
#include "VSTEffect.h"
#include "VSTEffectUI.h"

#define CMD_EFFECT_SET_ACTIVE 1
#define CMD_EFFECT_START_PROCESS 2
#define CMD_EFFECT_STOP_PROCESS 3
#define CMD_EFFECT_IS_UPDATED 4
#define CMD_EFFECT_GET_VERSION 5
#define CMD_EFFECT_GET_NUM_PARAMS 6
#define CMD_EFFECT_GET_NUM_INPUTS 7
#define CMD_EFFECT_GET_NUM_OUTPUTS 8
#define CMD_EFFECT_SET_BLOCK_SIZE 9
#define CMD_EFFECT_SET_SAMPLE_RATE 10
#define CMD_EFFECT_SET_PARAMETER 11
#define CMD_EFFECT_GET_PARAMETER 12
#define CMD_EFFECT_GET_PARAMETER_NAME 13
#define CMD_EFFECT_GET_PARAMETER_LABEL 14
#define CMD_EFFECT_GET_CHUNK 15
#define CMD_EFFECT_SET_CHUNK 16
#define CMD_EFFECT_BEGIN_SET_PROGRAM 17
#define CMD_EFFECT_END_SET_PROGRAM 18
#define CMD_EFFECT_SEND_MESSAGES 19
#define CMD_EFFECT_PROCESS_REPLACING 20
#define CMD_EFFECT_UI_IS_AVAILABLE 21
#define CMD_EFFECT_UI_IS_OPEN 22
#define CMD_EFFECT_UI_OPEN 23
#define CMD_EFFECT_UI_CLOSE 24
#define CMD_EFFECT_UI_FOCUS 25

int main(int argc, char *argv[]) 
{
	VSTLogger_log("VSTClient -> starting\n");
	VSTClientHandle *handle = (VSTClientHandle *) malloc( sizeof(VSTClientHandle) );
	
	ParseArguments(handle, argc, argv);
	
	VSTSocketCreate(&(handle->socket), handle->serverPort);
	
	if( handle->socket->connected) {
		VSTPlugin_malloc(&(handle->plugin), handle->filename);
		VSTEffect_malloc(&(handle->effect), handle->plugin);
		VSTEffectUI_malloc(handle->effect);
		VSTEffect_openEffect(handle->effect);

		int vstVersion = 0;
		VSTEffect_getVersion(handle->effect, &vstVersion);
		VSTLogger_log("VSTClient -> plugin version: %d\n", vstVersion);

		if( handle->effect != NULL && handle->effect->effect != NULL ) {
			pthread_t thread;
			if( pthread_create(&thread, NULL, VSTClient_processCommandsThread, handle)) {
				return 1;
			}
			
			while(handle->socket->connected) {
				VSTEffectUI_process(handle->effect);
			}
			
			pthread_join(thread, NULL);
		}
		
		VSTEffect_closeEffect(handle->effect);
		VSTEffectUI_delete(handle->effect);
		VSTEffect_delete(&(handle->effect));
		VSTPlugin_delete(&(handle->plugin));
	}
	
    VSTSocketDestroy(&(handle->socket));
	VSTLogger_log("VSTClient -> ended\n");
	
    return 0;
}

void* VSTClient_processCommandsThread(void* ptr)
{
	VSTClientHandle *handle = (VSTClientHandle *) ptr;

	VSTLogger_log("VSTClient -> sending session ID %d\n", handle->sessionId);
	VSTSocketWrite(handle->socket, &handle->sessionId, 4);
	
	int command = 0;
	while(handle->socket->connected) {
		command = 0;
		
		//Receive a reply from the server
		VSTSocketRead(handle->socket , &command , 4);
		
		ProcessCommand(handle, command);
	}
	return NULL;
}

void ParseArguments(VSTClientHandle *handle, int argc , char *argv[])
{
	VSTLogger_log("VSTClient -> parsing arguments\n");
	
	handle->sessionId = atoi(argv[1]);
	handle->serverPort = atoi(argv[2]);
	handle->filename = (const char*) argv[3];
	
	VSTLogger_log("VSTClient -> plugin path: %s\n", handle->filename);
}

void ProcessCommand(VSTClientHandle *handle, int command) 
{
	switch(command) {
		case CMD_EFFECT_GET_VERSION:
			ProcessGetVersionCommand(handle);
		break;
		case CMD_EFFECT_SET_ACTIVE:
			ProcessSetActiveCommand(handle);
		break;
		case CMD_EFFECT_START_PROCESS:
			ProcessStartProcessCommand(handle);
		break;
		case CMD_EFFECT_STOP_PROCESS:
			ProcessStopProcessCommand(handle);
		break;
		case CMD_EFFECT_IS_UPDATED:
			ProcessIsUpdatedCommand(handle);
		break;
		case CMD_EFFECT_GET_NUM_PARAMS:
			ProcessGetNumParamsCommand(handle);
		break;
		case CMD_EFFECT_GET_NUM_INPUTS:
			ProcessGetNumInputsCommand(handle);
		break;
		case CMD_EFFECT_GET_NUM_OUTPUTS:
			ProcessGetNumOutputsCommand(handle);
		break;
		case CMD_EFFECT_SET_BLOCK_SIZE:
			ProcessSetBlockSizeCommand(handle);
		break;
		case CMD_EFFECT_SET_SAMPLE_RATE:
			ProcessSetSampleRateCommand(handle);
		break;
		case CMD_EFFECT_SET_PARAMETER:
			ProcessSetParameterCommand(handle);
		break;
		case CMD_EFFECT_GET_PARAMETER:
			ProcessGetParameterCommand(handle);
		break;
		case CMD_EFFECT_GET_PARAMETER_NAME:
			ProcessGetParameterNameCommand(handle);
		break;
		case CMD_EFFECT_GET_PARAMETER_LABEL:
			ProcessGetParameterLabelCommand(handle);
		break;
		case CMD_EFFECT_SET_CHUNK:
			ProcessSetChunkCommand(handle);
		break;
		case CMD_EFFECT_GET_CHUNK:
			ProcessGetChunkCommand(handle);
		break;
		case CMD_EFFECT_BEGIN_SET_PROGRAM:
			ProcessBeginSetProgramCommand(handle);
		break;
		case CMD_EFFECT_END_SET_PROGRAM:
			ProcessEndSetProgramCommand(handle);
		break;
		case CMD_EFFECT_SEND_MESSAGES:
			ProcessSendMessagesCommand(handle);
		break;
		case CMD_EFFECT_PROCESS_REPLACING:
			ProcessReplacingCommand(handle);
		break;
		case CMD_EFFECT_UI_OPEN:
			ProcessOpenEffectUICommand(handle);
		break;
		case CMD_EFFECT_UI_CLOSE:
			ProcessCloseEffectUICommand(handle);
		break;
		case CMD_EFFECT_UI_FOCUS:
			ProcessFocusEffectUICommand(handle);
		break;
		case CMD_EFFECT_UI_IS_OPEN:
			ProcessIsEffectUIOpenCommand(handle);
		break;
		case CMD_EFFECT_UI_IS_AVAILABLE:
			ProcessIsEffectUIAvailableCommand(handle);
		break;
	}
}

void ProcessGetVersionCommand(VSTClientHandle *handle)
{
	int value = 0;
	VSTEffect_getVersion(handle->effect, &value);
	VSTSocketWrite(handle->socket, &value, 4);
}

void ProcessSetActiveCommand(VSTClientHandle *handle)
{
	int value = 0;
	VSTSocketRead(handle->socket, &value, 4);
	VSTEffect_setActive(handle->effect, value);
}

void ProcessStartProcessCommand(VSTClientHandle *handle)
{
	VSTEffect_startProcess(handle->effect);
}

void ProcessStopProcessCommand(VSTClientHandle *handle)
{
	VSTEffect_stopProcess(handle->effect);
}

void ProcessIsUpdatedCommand(VSTClientHandle *handle)
{
	bool value = 0;
	VSTEffect_getUpdated(handle->effect, &value);
	if( value ) {
		VSTEffect_setUpdated(handle->effect, false);
	}
	VSTSocketWrite(handle->socket, &value, 1);
}

void ProcessGetNumParamsCommand(VSTClientHandle *handle)
{
	int value = 0;
	VSTEffect_getNumParams(handle->effect, &value);
	VSTSocketWrite(handle->socket, &value, 4);
}

void ProcessGetNumInputsCommand(VSTClientHandle *handle)
{
	int value = 0;
	VSTEffect_getNumInputs(handle->effect, &value);
	VSTSocketWrite(handle->socket, &value, 4);
}

void ProcessGetNumOutputsCommand(VSTClientHandle *handle)
{
	int value = 0;
	VSTEffect_getNumOutputs(handle->effect, &value);
	VSTSocketWrite(handle->socket, &value, 4);
}

void ProcessSetBlockSizeCommand(VSTClientHandle *handle)
{
	int value = 0;
	VSTSocketRead(handle->socket, &value, 4);
	VSTEffect_setBlockSize(handle->effect, value);
}

void ProcessSetSampleRateCommand(VSTClientHandle *handle)
{
	float value = 0;
	VSTSocketRead(handle->socket, &value, 4);
	
	VSTEffect_setSampleRate(handle->effect, value);
}

void ProcessSetParameterCommand(VSTClientHandle *handle)
{
	int index = 0;
	float value = 0;
	float currentValue = 0;
	VSTSocketRead(handle->socket, &index, 4);
	VSTSocketRead(handle->socket, &value, 4);
	
	VSTEffect_getParameter(handle->effect, index, &currentValue);
	if( value != currentValue ) {
		VSTEffect_setParameter(handle->effect, index, value);
	}
}

void ProcessGetParameterCommand(VSTClientHandle *handle)
{
	int index = 0;
	float value = 0;
	VSTSocketRead(handle->socket, &index, 4);
	
	VSTEffect_getParameter(handle->effect, index, &value);
	
	VSTSocketWrite(handle->socket, &value, 4);
}

void ProcessGetParameterNameCommand(VSTClientHandle *handle)
{
	int index = 0;
	int length = 255;
	void *value = malloc(sizeof(char) * length);
	VSTSocketRead(handle->socket, &index, 4);
	
	VSTEffect_getParameterName(handle->effect, index, (const char *) value);
	
	VSTSocketWrite(handle->socket, &length, 4);
	
	VSTSocketWrite(handle->socket, value, 255);
}

void ProcessGetParameterLabelCommand(VSTClientHandle *handle)
{
	int index = 0;
	int length = 255;
	void *value = malloc( sizeof(char) * length );
	VSTSocketRead(handle->socket, &index, 4);
	
	VSTEffect_getParameterLabel(handle->effect, index, (const char *) value);
	
	VSTSocketWrite(handle->socket, &length, 4);
	
	VSTSocketWrite(handle->socket, value, 255);
}

void ProcessGetChunkCommand(VSTClientHandle *handle)
{
	int length = 0;
	char *value = 0;
	
	VSTEffect_getChunk(handle->effect, &length, &value);
	
	VSTSocketWrite(handle->socket, &length, 4);
	if( length > 0 ) {
		VSTSocketWrite(handle->socket, value, length);
	}
}

void ProcessSetChunkCommand(VSTClientHandle *handle)
{
	int length = 0;
	VSTSocketRead(handle->socket, &length, 4);
	
	char *value = (char *) malloc(sizeof(char) * length);
	VSTSocketRead(handle->socket, value, length);
	
	VSTEffect_setChunk(handle->effect, length, &value);
}

void ProcessBeginSetProgramCommand(VSTClientHandle *handle)
{
	VSTEffect_beginSetProgram(handle->effect);
}

void ProcessEndSetProgramCommand(VSTClientHandle *handle)
{
	VSTEffect_endSetProgram(handle->effect);
}

void ProcessSendMessagesCommand(VSTClientHandle *handle)
{
	int length = 0;
	VSTSocketRead(handle->socket, &length, 4);
	
	unsigned char** messages = (unsigned char **) malloc((sizeof(unsigned char *) * length));
	for(int i = 0; i < length; i++) {
		messages[i] = (unsigned char *) malloc((sizeof(unsigned char) * 4));
		VSTSocketRead(handle->socket, messages[i], 4);
	}
	
	VSTEffect_sendMessages(handle->effect, messages, length);
	
	delete [] messages;
}

void ProcessReplacingCommand(VSTClientHandle *handle)
{
	int blockSize = 0;	
	VSTSocketRead(handle->socket, &blockSize, 4);
	
	int inputLength = 0;
	int outputLength = 0;
	VSTEffect_getNumInputs(handle->effect, &inputLength);
	VSTEffect_getNumOutputs(handle->effect, &outputLength);
	
	float** inputs = (float **)malloc((sizeof(float *) * inputLength));
	for(int i = 0; i < inputLength; i++) {
		inputs[i] = (float *) malloc((sizeof(float) * blockSize));
		VSTSocketRead(handle->socket, inputs[i], (4 * blockSize));
	}
	
	float** outputs = (float **)malloc((sizeof(float *) * outputLength));
	for(int i = 0; i < outputLength; i++) {
		outputs[i] = (float *) malloc((sizeof(float) * blockSize));
	}
	
	VSTEffect_sendProcessReplacing(handle->effect, inputs, outputs, blockSize);
	
	for(int i = 0; i < outputLength; i++) {
		VSTSocketWrite(handle->socket, outputs[i], (4 * blockSize));
	}
	
	delete [] inputs;
	delete [] outputs;
}

void ProcessOpenEffectUICommand(VSTClientHandle *handle)
{
	VSTEffectUI_openEditor(handle->effect);
}

void ProcessCloseEffectUICommand(VSTClientHandle *handle)
{
	VSTEffectUI_closeEditor(handle->effect);
}

void ProcessFocusEffectUICommand(VSTClientHandle *handle)
{
	VSTEffectUI_focusEditor(handle->effect);
}

void ProcessIsEffectUIOpenCommand(VSTClientHandle *handle)
{
	bool value = 0;
	VSTEffectUI_isEditorOpen(handle->effect, &value);
	VSTSocketWrite(handle->socket, &value, 1);
}

void ProcessIsEffectUIAvailableCommand(VSTClientHandle *handle)
{
	bool value = 0;
	VSTEffectUI_isEditorAvailable(handle->effect, &value);
	VSTSocketWrite(handle->socket, &value, 1);
}
