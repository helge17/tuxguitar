#include <stdio.h>
#include <stdarg.h>
#include "LV2Logger.h"

void LV2Logger_log(const char *message,...)
{
	va_list args;
	va_start(args, message);
	
	vprintf(message, args);
	fflush(stdout);
	
	va_end(args);
}
