/*
 * Please do not edit this file.
 * It was generated using rpcgen.
 */

#ifndef _PA1_H_RPCGEN
#define _PA1_H_RPCGEN

#include <rpc/rpc.h>


#ifdef __cplusplus
extern "C" {
#endif


#define PA1_PROG 0x2345111
#define PA1_VERS 1

#if defined(__STDC__) || defined(__cplusplus)
#define get_server_response 1
extern  char ** get_server_response_1(int *, CLIENT *);
extern  char ** get_server_response_1_svc(int *, struct svc_req *);
extern int pa1_prog_1_freeresult (SVCXPRT *, xdrproc_t, caddr_t);

#else /* K&R C */
#define get_server_response 1
extern  char ** get_server_response_1();
extern  char ** get_server_response_1_svc();
extern int pa1_prog_1_freeresult ();
#endif /* K&R C */

#ifdef __cplusplus
}
#endif

#endif /* !_PA1_H_RPCGEN */