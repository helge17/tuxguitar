#ifndef _Included_VSTClient
#define _Included_VSTClient

#include "VST.h"
#include "VSTSocket.h"

typedef struct {
	int sessionId;
	int serverPort;
	const char *filename;
	VSTPluginHandle *plugin;
	VSTEffectHandle *effect;
	VSTSocketHandle *socket;
} VSTClientHandle;

int main(int argc, char *argv[]);

void ParseArguments(VSTClientHandle *handle, int argc , char *argv[]);

void CreateSocket(VSTClientHandle *handle);

void ProcessCommand(VSTClientHandle *handle, int command);

void ProcessSetActiveCommand(VSTClientHandle *handle);

void ProcessIsUpdatedCommand(VSTClientHandle *handle);

void ProcessGetNumParamsCommand(VSTClientHandle *handle);

void ProcessGetNumInputsCommand(VSTClientHandle *handle);

void ProcessGetNumOutputsCommand(VSTClientHandle *handle);

void ProcessSetBlockSizeCommand(VSTClientHandle *handle);

void ProcessSetSampleRateCommand(VSTClientHandle *handle);

void ProcessSetParameterCommand(VSTClientHandle *handle);

void ProcessGetParameterCommand(VSTClientHandle *handle);

void ProcessGetParameterNameCommand(VSTClientHandle *handle);

void ProcessGetParameterLabelCommand(VSTClientHandle *handle);

void ProcessSendMessagesCommand(VSTClientHandle *handle);

void ProcessReplacingCommand(VSTClientHandle *handle);

void ProcessOpenEffectUICommand(VSTClientHandle *handle);

void ProcessCloseEffectUICommand(VSTClientHandle *handle);

void ProcessIsEffectUIOpenCommand(VSTClientHandle *handle);

void ProcessIsEffectUIAvailableCommand(VSTClientHandle *handle);

void* VSTClient_processCommandsThread(void* ptr);

#endif /* _Included_VSTClient */
