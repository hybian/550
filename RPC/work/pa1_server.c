/*
 * This is sample code generated by rpcgen.
 * These are only templates and you can use them
 * as a guideline for developing your own functions.
 */

#include "pa1.h"
#include <time.h>

char **
get_server_time_1_svc(void *argp, struct svc_req *rqstp)
{
	static char * result;

	printf("get_server_time is called\n");

	time_t rawtime;
  	struct tm * timeinfo;

  	time ( &rawtime );
  	timeinfo = localtime ( &rawtime );

	result = asctime (timeinfo);

	return &result;
}
