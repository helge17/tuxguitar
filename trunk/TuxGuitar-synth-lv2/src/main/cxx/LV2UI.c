#include <stdlib.h>
#include <string.h>

#include "LV2.h"
#include "LV2UI.h"
#include "LV2Plugin.h"
#include "LV2Instance.h"
#include "LV2Logger.h"

void LV2UI_isUpdated(LV2UI *handle, bool *updated)
{
	if( handle != NULL ){
		(*updated) = handle->updated;
	}
}

void LV2UI_setUpdated(LV2UI *handle, bool updated)
{
	if( handle != NULL ){
		handle->updated = updated;
	}
}
