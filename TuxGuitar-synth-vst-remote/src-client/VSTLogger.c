#include <stdio.h>
#include <stdarg.h>
#include "VSTLogger.h"

void VSTLogger_log(const char *message,...)
{
	printf(message);
	fflush(stdout);
}
