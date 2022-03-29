#ifndef _Included_LV2
#define _Included_LV2

#include <lilv.h>
#include <lv2/lv2plug.in/ns/ext/atom/atom.h>
#include <lv2/lv2plug.in/ns/ext/midi/midi.h>
#include <lv2/lv2plug.in/ns/extensions/ui/ui.h>
#include <lv2/instance-access/instance-access.h>

typedef uint32_t LV2Int32;

typedef enum {
	TYPE_CONTROL,
	TYPE_AUDIO_IN,
	TYPE_AUDIO_OUT,
	TYPE_MIDI_IN,
	TYPE_UNKNOWN
} LV2PortType;

typedef struct {
	LV2PortType type;
	const LilvPort* lilvPort;
} LV2Port;

typedef struct {
	LilvWorld* lilvWorld;
} LV2World;

typedef struct {
	LV2World* world;
	const LilvPlugin* lilvPlugin;
	LV2Port** ports;
	LV2Int32 portCount;
} LV2Plugin;

typedef struct {
	void* dataLocation;
} LV2PortConnection;

typedef struct {
	LilvInstance* lilvInstance;
	LV2Plugin* plugin;
	LV2PortConnection** connections;
	LV2Int32 bufferSize;
	
	pthread_t* thread;
} LV2Instance;

typedef struct {
	void* handle;
	bool updated;
} LV2UI;

#endif /* _Included_LV2 */
