#ifndef _Included_LV2Feature
#define _Included_LV2Feature

typedef struct LV2URILinkImpl LV2URILink;

typedef struct LV2URILinkedMapImpl LV2URILinkedMap;

struct LV2FeatureImpl {
    LV2Config* config;
    LV2Worker* worker;
	LV2URILinkedMap* uriMap;
    LV2_Feature** features;

    LV2_Options_Option         options[4];
};

struct LV2URILinkImpl {
    LV2_URID id;
    char* uri;
	
    LV2URILink* next;
};

struct LV2URILinkedMapImpl {
	LV2_URID sequence;
	LV2URILink* next;
};

void LV2Feature_malloc(LV2Feature **handle, LV2Config *config);

void LV2Feature_free(LV2Feature **handle);

void LV2Feature_init(LV2Feature *handle, LV2Instance *instance);

void LV2Feature_processAudio(LV2Feature *handle);

const LV2_Feature* const* LV2Feature_getFeatures(LV2Feature *handle);

const LV2_Feature* LV2Feature_getFeature(LV2Feature *handle, const char* uri);

LV2_URID LV2Feature_map(LV2_URID_Map_Handle mapHandle, const char* uri);

#endif
