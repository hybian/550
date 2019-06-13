#include <linux/kernel.h>
#include <linux/sysinfo.h>
#include <stdio.h>
#include <sys/sysinfo.h>
#include <malloc.h>
#include <unistd.h>
#include <pwd.h>
#include <stdlib.h>
#include <string.h>

int main ()
{
    // /* Conversion constants. */
    // const long minute = 60;
    // const long hour = minute * 60;
    // const long day = hour * 24;
    // const double megabyte = 1024 * 1024;
    // /* Obtain system statistics. */
    // struct sysinfo si;
    // sysinfo(&si);
    // /* Summarize interesting values. */
    // printf("system uptime : %ld days, %ld:%02ld:%02ld\n", si.uptime / day, (si.uptime % day) / hour, (si.uptime % hour) / minute, si.uptime % minute);
    // printf("total RAM   : %5.1f MB\n", si.totalram / megabyte);
    // printf("free RAM   : %5.1f MB\n", si.freeram / megabyte);
    // printf("process count : %d\n", si.procs);

    // struct mallinfo info = mallinfo ();
    // char * test = malloc(100);
    // printf("non-mapped space: %d\n", info.arena);
    // printf("ordblks = %d\n", info.ordblks);
    // printf("smblks = %d\n", info.smblks);
    // printf("hblks = %d\n", info.hblks);
    // printf("hblkhd = %d\n", info.hblkhd);
    // printf("usmblks = %d\n", info.usmblks);
    // printf("fsmblks = %d\n", info.fsmblks);
    // printf("uordblks = %d\n", info.uordblks);
    // printf("fordblks = %d\n", info.fordblks);
    // printf("keepcost = %d\n", info.keepcost);
    // printf("page size = %d\n", getpagesize());
    // printf("num of phy pages = %ld\n", get_phys_pages());
    // printf("num of available phy pages = %ld\n", get_avphys_pages());

    // struct passwd *user;
    // FILE *stream;
    // stream = fopen("/etc/passwd", "r");
    // while((user = fgetpwent(stream)) != 0)
    // {
    //     printf("%s\n", user->pw_name);
    // }

    // double loadavg[3] = {1, 5, 15};
    // printf("load processes: %d\n", getloadavg(loadavg, 3));

    // char* result = malloc(1024);
    // FILE* fp;
    // fp = fopen("top", "r");
    // fgets(result, 100, fp);
    // printf("%s\n", result);


    // printf("%s\n", result);
	// char * line = NULL;
    // size_t len = 0;
    // ssize_t read;

	// FILE *fp;
	// fp = fopen("top","r");
    // printf("asd\n");
	// //fseek(fp, 0L, SEEK_END);
    
	// size_t size = ftell(fp);
    
	// char * result = malloc(size);
	// int i=0;
    
	// while ((read = getline(&line, &len, fp)) != -1 && i<3) 
	// {
    //     printf("%d\n",i);
    //     strcat(result, line);
	// 	i++;
    // }
    // fclose(fp);

    FILE* fp;
    char* buffer = malloc(1024);
    system("top -b -n 1 | head -n 5 > top.temp");
    fp = fopen("top.temp", "r");


    char * line = NULL;
    size_t len = 0;
    ssize_t read;
    while ((read = getline(&line, &len, fp)) != -1) 
	{
        //printf("%s\n", line);
        strcat(buffer, line);
    }
    fgets(buffer, 1024, fp);
    fclose(fp);
    remove("top.temp");
    printf("%s\n", buffer);

    return 0;
}