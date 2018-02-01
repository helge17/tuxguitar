#include <stdio.h>
#include <stdarg.h>
#include "VSTLogger.h"

void VSTLogger_log(const char *message,...)
{
	va_list args;
	va_start(args, message);
	
	vprintf(message, args);
	fflush(stdout);
	
	va_end(args);
}
