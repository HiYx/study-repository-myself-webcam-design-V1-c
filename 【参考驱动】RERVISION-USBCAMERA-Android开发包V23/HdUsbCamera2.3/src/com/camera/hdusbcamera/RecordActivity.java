package com.camera.hdusbcamera;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by sr_mo on 14-12-15.
 */
public class RecordActivity extends Activity {

    private static final String TAG = "RecordActivity";
    private String vdPath="/mnt/sdcard/record/";//此路径可由用户指定
    private String vdFile;
    private ImageButton mstart;
    private ImageButton mpause;
    private ImageButton mstop;
    private TextView mTime,mSolution,mStatus,mPath;
    private ExpandableListView mMain;
    private List<String> GroupData;//定义组数据
    private List<List<String>> ChildrenData;//定义组中的子数据
    private int counts,recordparam;
    static boolean recording=false,stoped=true;
    private boolean firstrecord=true;
    private Timer mTimer;
    private CameraPreview cp;
    static SharedPreferences preferences;
    private Editor editor;
    private List<String> Child1;
    private RecordExpandableAdapter mExpandableAdapter;
    private int gTimes;

    Handler mHandler=new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case 0:
                    Date date = new Date();
                    SimpleDateFormat myFormatter =  new SimpleDateFormat("HH:mm:ss");
                    gTimes=(counts++)*1000;
                    date.setTime(gTimes-8*60*60*1000);
                    String RecordTime = myFormatter.format(date);
                    mTime.setText(RecordTime);
                    break;
                case 1:
                    mSolution.setText(Child1.get(preferences.getInt("recordparam",0)));
                    mPath.setText(vdFile);
                    mStatus.setText(recording?"正在录像":"暂停录像");                   
                    break;
                case 2:
                	mSolution.setText(Child1.get(preferences.getInt("recordparam",0)));
                    mPath.setText(vdFile);
                	mStatus.setText(recording?"正在录像":"停止录像");
                    break;
            }
            super.handleMessage(msg);
        }
    };
    private SoundPool sp;
    private int music;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record_layout);
        initData();
        init();

    }
    void init() {

        mstart = (ImageButton) findViewById(R.id.StartRecord);
        mstart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startRecord();
            }
        });
        mpause = (ImageButton) findViewById(R.id.PauseRecord);
        mpause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pauseRecord();
            }
        });
        mpause.setEnabled(false);
        mstop = (ImageButton) findViewById(R.id.StopRecord);
        mstop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopRecord();
            }
        });
        mstop.setEnabled(false);

        mTime = (TextView) findViewById(R.id.RecordTime);
        mStatus = (TextView) findViewById(R.id.RecordStute);
        mPath= (TextView) findViewById(R.id.RecordPath);
        mSolution = (TextView) findViewById(R.id.Recordsolution);

        sp = new SoundPool(4, AudioManager.STREAM_SYSTEM, 5);
        music = sp.load(this, R.raw.record, 1);

        mExpandableAdapter=new RecordExpandableAdapter(ChildrenData,GroupData,this);
        mMain = (ExpandableListView) findViewById(R.id.RecordMainls);
        mMain.setAdapter(mExpandableAdapter);
        mMain.expandGroup(0);
        mMain.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
                                        int childPosition, long id) {

                switch(childPosition){
                case 0:

                	break;
                case 1:

                	break;
                case 2:

                	break;
                }
                editor.putInt("recordparam", childPosition);
                editor.commit();
                if(firstrecord){
                    recordparam=preferences.getInt("recordparam",0);
                }
                int count=mExpandableAdapter.getChildrenCount(groupPosition);
                for(int k = 0; k <count; k++) {
                    boolean isLast = false;
                    if (k == (count - 1)){
                        isLast = true;
                    }
                    mExpandableAdapter.getChildView(groupPosition,
                            k, isLast, null, null).findViewById(R.id.checkBox_solution);
                    mExpandableAdapter.notifyDataSetChanged();
                }
                return true;
            }

        });
       

    }

    void initData(){

        GroupData = new ArrayList<String>();
        GroupData.add("分辨率");
        ChildrenData = new ArrayList<List<String>>();
        Child1= new ArrayList<String>();
        Child1.add("480P");
        Child1.add("720P");
        Child1.add("1080P");        
        ChildrenData.add(Child1);
        cp=CameraPreview.getInstance(this);
        preferences = getSharedPreferences("RecordParame", MODE_PRIVATE);
        editor =preferences.edit();
        recordparam=preferences.getInt("recordparam",0);
        if(recordparam>MainActivity.recordsize){
        	recordparam=MainActivity.recordsize;
        	 editor.putInt("recordparam", recordparam);
             editor.commit();
        }

    }

    void startRecord(){

        counts=0;
        if(recordparam==preferences.getInt("recordparam",0)){

            if(firstrecord){
               
                cp.prepareRecording(recordparam,vdPath);
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                firstrecord=false;
            }
            SimpleDateFormat sDateFormat =new SimpleDateFormat("yyyyMMddhhmmss");
            String date= sDateFormat.format(new Date(System.currentTimeMillis()));
            vdFile=vdPath+date+".avi";
            Thread mThread=new Thread(new Runnable() {

                @Override
                public void run() {
                    cp.startRecord(vdFile);
                }

            });
            mThread.start();
            recording=true;
            stoped=false;
            updateStatus(1);
            updateUI();
            mstart.setEnabled(false);
            mpause.setEnabled(true);
            mstop.setEnabled(true);
        }else{
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setCancelable(false)
                    .setMessage("参数已改变")
                    .setPositiveButton("更新设置", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            try{
                                Thread.sleep(100); // wait for thread stopping
                            }catch(Exception e){}
                            cp.releaseCamera();
                            recordparam=preferences.getInt("recordparam",0);
                            firstrecord=true;
                            Toast.makeText(cp.getContext(), "plese click start", Toast.LENGTH_LONG).show();
                        }
                    }).setNegativeButton("还原设置", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            editor.putInt("recordparam", recordparam);
                            editor.commit();
                        }
                    }).show();
        }

        sp.play(music, 1, 1, 0, 0, 1);

    }

    @Override
	protected void onStart() {
		super.onStart();
	}

	void pauseRecord(){

        if(recording){
            mTimer.cancel();
            mpause.setImageDrawable(getResources().getDrawable(R.drawable.vd_restart));
            recording=false;
            cp.pauseRecord(1);
        }else {
            mpause.setImageDrawable(getResources().getDrawable(R.drawable.vd_pause));
            updateUI();
            recording=true;
            cp.pauseRecord(0);
        }
        updateStatus(1);
    }

    void stopRecord(){

        mTimer.cancel();
        cp.stopRecord(gTimes);
        recording=false;
        stoped=true;
        updateStatus(2);

        mpause.setImageDrawable(getResources().getDrawable(R.drawable.vd_pause));
        mpause.setEnabled(false);
        mstart.setEnabled(true);
        mstop.setEnabled(false);

    }

    void updateUI(){

        mTimer=new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {

                Message mMessage=new Message();
                mMessage.what=0;
                mHandler.sendMessage(mMessage);

            }
        }, 0, 1000);

    }

    void updateStatus(int i){

        Message mMessage=new Message();
        mMessage.what=i;
        mHandler.sendMessage(mMessage);
    }

    
    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if (keyCode == KeyEvent.KEYCODE_BACK)
        {  if(recording){
        		Toast.makeText(this, "plese stop record", Toast.LENGTH_SHORT).show();
           }else{
        	   try{ 
					Thread.sleep(100); // wait for thread stopping
				}catch(Exception e){}
        	   cp.releaseCamera();
               firstrecord=true;
               AlertDialog dialog = new AlertDialog.Builder(this)
               .setMessage("退出录像模式吗？")
               .setPositiveButton("是", new DialogInterface.OnClickListener()
               {
                   @Override
                   public void onClick(DialogInterface dialog, int which)
                   {
                      finish();
                   }
               }).setNegativeButton("否", new DialogInterface.OnClickListener()
               {
                   @Override
                   public void onClick(DialogInterface dialog, int which)
                   {
                       
                   }
               }).show();
           }
            
           
        }
        return true;
	}
	@Override
    protected void onDestroy() {
        super.onDestroy();
        if(recording){
            mTimer.cancel();
          
        }

    }
}
