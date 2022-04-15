#ifndef _Included_LV2
#define _Included_LV2

#include <lilv.h>
#include <lv2/lv2plug.in/ns/extensions/ui/ui.h>
#include <lv2/lv2plug.in/ns/ext/state/state.h>
#include <lv2/lv2plug.in/ns/ext/options/options.h>
#include <lv2/lv2plug.in/ns/ext/parameters/parameters.h>
#include <lv2/lv2plug.in/ns/ext/atom/atom.h>
#include <lv2/lv2plug.in/ns/ext/atom/util.h>
#include <lv2/lv2plug.in/ns/ext/midi/midi.h>
#include <lv2/lv2plug.in/ns/ext/buf-size/buf-size.h>
#include <lv2/lv2plug.in/ns/ext/instance-access/instance-access.h>
#include <lv2/lv2plug.in/ns/ext/instance-access/instance-access.h>
#include <lv2/lv2plug.in/ns/ext/worker/worker.h>

#ifndef MIN
#    define MIN(a, b) (((a) < (b)) ? (a) : (b))
#endif

#ifndef MAX
#    define MAX(a, b) (((a) > (b)) ? (a) : (b))
#endif

#define EVENT_LENGTH 100
#define EVENT_SIZE ((sizeof(LV2_Atom_Event) + 4) + 7)
#define EVENT_SEQUENCE_SIZE (sizeof(LV2_Atom_Sequence) + (EVENT_SIZE * EVENT_LENGTH))

typedef int32_t LV2Int32;

typedef float LV2Float64;

typedef enum {
	TYPE_UNKNOWN,
	TYPE_CONTROL,
	TYPE_AUDIO,
	TYPE_EVENT
} LV2PortType;

typedef enum {
	FLOW_UNKNOWN,
	FLOW_IN,
	FLOW_OUT,
} LV2PortFlow;

typedef struct {
	LV2Float64 sampleRate;
	LV2Int32 bufferSize;
	LV2Int32 eventBufferSize;
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
	void* workBuffer;
} LV2PortConnection;

typedef struct {
	uint32_t index;
	uint32_t protocol;
	uint32_t size;
	uint8_t  body[];
} LV2ControlChange;

typedef struct LV2LockImpl LV2Lock;

typedef struct LV2InstanceImpl LV2Instance;

typedef struct LV2WorkerImpl LV2Worker;

typedef struct LV2FeatureImpl LV2Feature;

typedef struct LV2UIImpl LV2UI;

#endif /* _Included_LV2 */
