
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <sys/file.h>
#include <string.h>
#include <pthread.h>
#include <linux/videodev2.h>
#include <sys/ioctl.h>
#include <sys/mman.h>
#include <errno.h>
#include <fcntl.h>
#include <time.h>
#include <sys/time.h>
#include <signal.h>
#include <X11/Xlib.h>
#include "v4l2uvc.h"
#include "avilib.h"

int main(int argc, char *argv[])   // -s 1280x720   -d  /dev/video1   -a  test.avi  -i 30
{
	FILE *file = NULL;// 定义文件
	struct vdIn *vd; //定义索引
	unsigned char *tmpbuffer;//定义缓存区域
	char *filename = NULL;// 定义设备"/dev/video0"
	char *avifilename = NULL;//定义保存avi视频文件名
	
	char *sizestring = NULL;// 定义分辨率
	char *fpsstring  = NULL;//定义帧率
	char *separateur = NULL;//定义分隔符'x'
	int width ; 
	int height;
	int fps;
	int i;
		
	for (i = 0; i < argc; i++) 
	{		
		printf("No ffff %d\n" ,argc);
		if (1 == argc)
		{
			printf("No for for \n");
			
		    width = 640 ; 
	        height = 480 ;
	        fps   = 30 ;
	        filename = "/dev/video0";
	        avifilename = "test.avi";
		}
		
		if (argv[i] == NULL || *argv[i] == 0 || *argv[i] != '-') 
		{
			continue;
		}
		
		if (strcmp(argv[i], "-s") == 0) 
		{
			if (i + 1 >= argc) 
			{
				printf("No parameter specified with -s, aborting.\n");
				exit(1);
			}

			sizestring = strdup(argv[i + 1]);

			width = strtoul(sizestring, &separateur, 10);
			if (*separateur != 'x') 
			{
				printf("Error in size use -s widthxheight\n");
				exit(1);
			} 
			else 
			{
				++separateur;
				height = strtoul(separateur, &separateur, 10);
				if (*separateur != 0)
					printf("hmm.. dont like that!! trying this height\n");
			}
		}
		else 
		{
			width = 640; 
	        height = 480;
		}
		
		if (strcmp(argv[i], "-d") == 0) 
		{
			if (i + 1 >= argc) 
			{
				printf("No parameter specified with -d, aborting.\n");
				exit(1);
			}
			filename = strdup(argv[i + 1]);
			
		}
		
		if (strcmp(argv[i], "-a") == 0) 
		{
			if (i + 1 >= argc) 
			{
				printf("No parameter specified with -o, aborting.\n");
				exit(1);
			}
			avifilename = strdup(argv[i + 1]);
		}
		
		if (strcmp(argv[i], "-i") == 0)
		{
			if (i + 1 >= argc) 
			{
				printf("No parameter specified with -i, aborting.\n");
				exit(1);
			}
			fpsstring = argv[i + 1];
			fps = strtoul(fpsstring, &separateur, 10);
			if(*separateur != '\0') 
			{
				printf("Invalid frame rate '%s' specified with -i. "
						"Only integers are supported. Aborting.\n", fpsstring);
				exit(1);
			}
		}
		else 
		{
			fps = 30;
		}
		
	}
	
	if (filename == NULL || *filename == 0) {		
		filename = "/dev/video0";
	}

	if (avifilename == NULL || *avifilename == 0) {
		avifilename = "test.avi";
	}
	
	int format = V4L2_PIX_FMT_MJPEG;// 数据接受格式MJPEG
	int ret;
	int grabmethod = 1;//捕获方式
		
	file = fopen(filename, "wb");// 打开设备
	if(file == NULL) 
	{
		printf("Unable to open file for raw frame capturing\n ");		
		exit(1);
	}
	
	//v4l2 init
	vd = (struct vdIn *) calloc(1, sizeof(struct vdIn));//分配v4l 的配置信息结构体空间
	if(init_videoIn(vd, (char *) filename, width, height,fps,format,grabmethod,avifilename) < 0)
	{
		exit(1);
	}
	
	if (video_enable(vd))
	{
	   exit(1);
	}
	
	vd->avifile = AVI_open_output_file(vd->avifilename);// 创建avi文件
	if (vd->avifile == NULL ) 
	{
		printf("Error opening avifile %s\n",vd->avifilename);
		exit(1);
	}
	
	AVI_set_video(vd->avifile, vd->width, vd->height, fps, "MJPG");// 配置avi封装格式
	printf("recording to %s\n",vd->avifilename);

	while(1)
	{		
		memset(&vd->buf, 0, sizeof(struct v4l2_buffer));//一段内存块中填充某个给定的值，它是对较大的 结构体 或 数组 进行清零操作的一种最快方法
		vd->buf.type = V4L2_BUF_TYPE_VIDEO_CAPTURE;//宏定义中定义的相机的捕获类型
		vd->buf.memory = V4L2_MEMORY_MMAP;// PAGE_SIZE为单位进行映射
		ret = ioctl(vd->fd, VIDIOC_DQBUF, &vd->buf);// 操作相机，读出buf(n-1)
		if (ret < 0) 
		{
			printf("Unable to dequeue buffer");
			exit(1);
		}
		
		memcpy(vd->tmpbuffer, vd->mem[vd->buf.index],vd->buf.bytesused);// 拷贝buf
	
		AVI_write_frame(vd->avifile, vd->tmpbuffer,vd->buf.bytesused, vd->framecount);//写视频
	
		vd->framecount++;//帧数增加
		
		ret = ioctl(vd->fd, VIDIOC_QBUF, &vd->buf);// 操作相机，载入新buf
		if (ret < 0) 
		{
			printf("Unable to requeue buffer");
			exit(1);
		}
	}
	fclose(file);// 关闭设备
	close_v4l2(vd);//关闭分配v4l 的配置信息结构体空间
}
