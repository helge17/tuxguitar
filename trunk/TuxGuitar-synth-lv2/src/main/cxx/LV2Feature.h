#ifndef _Included_LV2Feature
#define _Included_LV2Feature

typedef struct LV2URILinkImpl LV2URILink;

typedef struct LV2URILinkedMapImpl LV2URILinkedMap;

struct LV2FeatureImpl {
	LV2URILinkedMap* uriMap;

    LV2_Feature** features;
};

struct LV2URILinkImpl {
    LV2_URID id;
    const char* uri;
	
    LV2URILink* next;
};

struct LV2URILinkedMapImpl {
	LV2_URID sequence;
	LV2URILink* next;
};

void LV2Feature_malloc(LV2Feature **handle);

void LV2Feature_free(LV2Feature **handle);

const LV2_Feature* const* LV2Feature_getFeatures(LV2Feature *handle);

LV2_URID LV2Feature_map(LV2_URID_Map_Handle mapHandle, const char* uri);

#endif
