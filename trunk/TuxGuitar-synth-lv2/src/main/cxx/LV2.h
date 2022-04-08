#ifndef _Included_LV2
#define _Included_LV2

#include <lilv.h>
#include <lv2/lv2plug.in/ns/extensions/ui/ui.h>
#include <lv2/lv2plug.in/ns/ext/state/state.h>
#include <lv2/lv2plug.in/ns/ext/options/options.h>
#include <lv2/lv2plug.in/ns/ext/parameters/parameters.h>
#include <lv2/lv2plug.in/ns/ext/atom/atom.h>
#include <lv2/lv2plug.in/ns/ext/midi/midi.h>
#include <lv2/lv2plug.in/ns/ext/buf-size/buf-size.h>
#include <lv2/lv2plug.in/ns/ext/instance-access/instance-access.h>
#include <lv2/lv2plug.in/ns/ext/instance-access/instance-access.h>
#include <lv2/lv2plug.in/ns/ext/worker/worker.h>

#ifndef MIN
#    define MIN(a, b) (((a) < (b)) ? (a) : (b))
#endif

typedef uint32_t LV2Int32;

typedef float LV2Float64;

typedef enum {
	TYPE_UNKNOWN,
	TYPE_CONTROL,
	TYPE_AUDIO,
	TYPE_MIDI
} LV2PortType;

typedef enum {
	FLOW_UNKNOWN,
	FLOW_IN,
	FLOW_OUT,
} LV2PortFlow;

typedef struct {
	LV2Int32 bufferSize;
	LV2Float64 sampleRate;
} LV2Config;

typedef struct {
	LV2PortType type;
	LV2PortFlow flow;
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
	LV2Config *config;
	LV2_URID midiEventType;
	pthread_t* thread;
} LV2Instance;

typedef struct LV2WorkerImpl LV2Worker;

typedef struct LV2FeatureImpl LV2Feature;

typedef struct LV2UIImpl LV2UI;

#endif /* _Included_LV2 */
