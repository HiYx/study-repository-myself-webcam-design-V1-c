package com.camera.hdusbcamera;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import java.io.DataOutputStream;

class CameraPreview extends SurfaceView implements SurfaceHolder.Callback, Runnable 
{
	static final String tag = "CameraPreview";
    private String tip;
	private static final boolean DEBUG = true;
	private static CameraPreview mCamera;
	protected Context context;
	private SurfaceHolder holder;
    Thread mainLoop = null;
	private Bitmap bmp=null;
	boolean  flag=false;
	
	private boolean cameraExists=false;
	private boolean shouldStop=false;
	
	private int cameraId=0;
	private int cameraBase=0;
    private int ret;
	
	static final int IMG_WIDTH=640;
	static final int IMG_HEIGHT=480;

    private int winWidth=0;
    private int winHeight=0;
    private Rect rect;
    private int dw, dh;
    private float rate;
    private int info=0;
    
    // JNI functions
    public native void  prepareRecording(int i,String path);
    public native int  prepareCameraID();
    public native int   prepareCamera();//判断摄像头是否就绪，-1代表打开摄像头失败
    public native int   processCamera(); //采集视频图像数据
    public native void  stopCamera();//关闭摄像头
    public native void  releaseCamera();
    public native void  pixeltobmp(Bitmap bitmap);//将图像数据绘制到bitmap
    public native int   nativeGetPid(); //读取摄像头id
    public native int   getRecordSize();
    public native int[] getPictureSize();
    public native String getLibVersion();
    
    //一下为摄像头默认参数
    public native int[] nativeGetBrightnessArray();
    public native int[] nativeGetContrastArray();
    public native int[] nativeGetSaturationArray();
    public native int[] nativeGetHueArray();
    public native int[] nativeGetExposureArray();
    
    public native int[] nativeGetWhiteBalanceArray();
    public native int[] nativeGetGammaArray();
    public native int[] nativeGetGainArray();
    public native int[] nativeGetSharpnessArray();
    
    public native int   startCapture(String path,String filename);//按指定filename存储照片
    public native int   startRecord(String file);//开始录像
    public native int   stopRecord(int time);//停止录像
    public native int   pauseRecord(int pause);//停止录像
    public native int   setPictureSize(int size);
    
    public native int   setBrightness( int value);//设置亮度
    public native int   setContrast(int value);//设置对比度
    public native int   setSaturation(int value);//设置饱和度
    public native int   setHue(int value);//设置色调
    public native int   setAutoExposure(int value);//是否自动曝光标志位
    public native int   setExposure(int value);//设置曝光度
    
    public native int   setPowerLineFrequency(int mvalue);//设置防闪烁
    public native int   setWhiteBalanceAuto(int flag);//设置白平衡
    public native int   setWhiteBalance(int value);//设置白平衡的值
    public native int   setGamma(int value);//设置珈玛
    public native int   setGain(int value);//设置增益
    public native int   setSharpness(int value);//设置清晰度
    
    
    static 
    {
        System.loadLibrary("HdUsbCamera");
    }

      Handler mhandler=new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what==-1){
            new AlertDialog.Builder(context)
                .setCancelable(false)
                .setMessage("摄像信号读取失败")
                .setPositiveButton("确定", new DialogInterface.OnClickListener(){

                   @Override
                   public void onClick(DialogInterface dialog, int which)
                   {
                       System.exit(0);
                   }

                })
                .show();
            }
           super.handleMessage(msg);
        }

    };
	private Paint mpaint;
    private CameraPreview(Context context)
    {
        super(context);
        this.context = context;
        mpaint=new Paint();
        mpaint.setAntiAlias(true);
        if (DEBUG)
            Log.d(tag, "CameraPreview constructed");
        setFocusable(true);
        holder = getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_NORMAL);
    }
   public static CameraPreview getInstance(Context context)
   {
       if(mCamera==null)
           mCamera=new CameraPreview(context);
       return mCamera;
   }

	int count = 0;
	
    @Override
    public synchronized void run()
    {
        while (true && cameraExists) 
        {
        	if(winWidth==0)
        	{
        		winWidth=this.getWidth();
        		winHeight=this.getHeight();

        		if(winWidth*3/4<=winHeight)
        		{
        			dw = 0;
        			dh = (winHeight-winWidth*3/4)/2;
        			rate = ((float)winWidth)/IMG_WIDTH;
        			rect = new Rect(dw,dh,dw+winWidth-1,dh+winWidth*3/4-1);
        		}else{
        			dw = (winWidth-winHeight*4/3)/2;
        			dh = 0;
        			rate = ((float)winHeight)/IMG_HEIGHT;
        			rect = new Rect(dw,dh,dw+winHeight*4/3 -1,dh+winHeight-1);
        		}
        	}

        	info=processCamera();
        	if(info<0)
        	{
        	    Message message = new Message();      
                message.what = -1;      
                mhandler.sendMessage(message);  
        	}
            pixeltobmp(bmp);
            Canvas canvas = getHolder().lockCanvas();
            if (canvas != null)
            {
            	
                canvas.setDrawFilter(new PaintFlagsDrawFilter(0,
                        Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));
            	canvas.drawBitmap(bmp,null,rect,null);
            	getHolder().unlockCanvasAndPost(canvas);
            }

            if(shouldStop)
            {
            	shouldStop = false;  
            	Log.i(tag, "break");
            	break;
            }	        
        }
        Log.i(tag, "线程退出12");
    }

	@Override
	public void surfaceCreated(SurfaceHolder holder) 
	{
		if(DEBUG) Log.d("WebCam", "surfaceCreated");
		if(bmp==null)
		{
			bmp = Bitmap.createBitmap(IMG_WIDTH,
                    IMG_HEIGHT, Bitmap.Config.ARGB_8888);
		}
        if (isRoot("/dev/video"+prepareCameraID())){
		ret = prepareCamera();
            if(ret>=0) {
                cameraExists = true;
                flag = true;
                mainLoop = new Thread(this);
                mainLoop.start();
            }else {
                switch (ret) {
                    case -1:
                        tip = "摄像头型号不匹配";
                        break;
                    case -2:
                        tip = "无权限打开摄像头";
                        break;
                    case -3:
                        tip = "摄像头被占用";
                        break;
                    case -4:
                        tip = "无摄像头设备";
                        break;
                    case -5:
                        tip = "无驱动程序";
                        break;
                }
                new AlertDialog.Builder(context)
                        .setCancelable(false)
                        .setMessage(tip)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                System.exit(0);
                            }

                        })
                        .show();
            }
        } else {
            Toast.makeText(context,
                    "Plese root you devices", Toast.LENGTH_SHORT);
        }
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) 
	{
		if(DEBUG) Log.d("WebCam", "surfaceChanged");
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) 
	{
		if(cameraExists){
			shouldStop = true;
			while(shouldStop){
				try{ 
					Thread.sleep(100); 
				}catch(Exception e){}
			}
		}
		stopCamera();
        if(DEBUG) Log.d("WebCam", "surfaceDestroyed");
	}
    public boolean isRoot(String videonPath) {

        Process process = null;
        DataOutputStream os = null;

        try {
            String cmd = "chmod 766 " + videonPath;
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(cmd + "\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (Exception e) {
            return false;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                process.destroy();
            } catch (Exception e) {
            }
        }
        return true;
    }

}  

 


