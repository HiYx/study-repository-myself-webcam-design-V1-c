# study-repository-myself-webcam-design-V1-c
study-repository-myself-webcam-design-V1-c



博客https://blog.csdn.net/djfjkj52/category_10897556.html

【Webcam设计】利用底层V4L2+OPENCV进行图像处理以及移植策略



通过V4L2，从摄像头的像素格式，视频流格式，包括视频的输出格式等作设置和定制，包括视频帧显示的大小等都可以进行修改。

底层的V4L2真的那么恐怖吗，其实非也，底层V4L2非常容易理解，而且也不难，看我细细道来。

V4L是Linux环境下开发视频采集设备驱动程序的一套规范(API)，它为驱动程序的编写提供统一的接口，并将所有的视频采集设备的驱动程序都纳入其的管理之中。V4L不仅给驱动程序编写者带来极大的方便，同时也方便了应用程序的编写和移植。V4L2是V4L的升级版，我们使用的OOB是3.3的内核，不再支持V4L,所以是以v4l2作为底层的摄像头视频开发。

video4linux2(V4L2)是Linux内核中关于视频设备的内核驱动，它为Linux中视频设备访问提供了通用接口，在Linux系统中，V4L2驱动的Video设备节点路径通常/dev/video/中的videoX。

V4L2的主要作用使程序有发现设备和操作设备的能力.它主要是用一系列的回调函数来实现这些功能，像设置摄像头的频率、帧频、视频压缩格式和图像参数等等。此框架只能运行在Linux操作系统中，而且是针对uvc的免驱usb设备的编程框架。