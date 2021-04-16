
package com.camera.hdusbcamera;

//import com.CES.usbcamera.CameraPreview;
//import com.CES.usbcamera.R;

//import com.CES.usbcamera.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class MainActivity extends Activity implements SeekBar.OnSeekBarChangeListener {

    private FrameLayout preview;
    private LinearLayout ctrlp;
    private LinearLayout left;
    private LinearLayout right;
    private LayoutInflater mInflater;
    private RelativeLayout parent;
    
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private mDBManager ndbm;
    private SoundPool sp;
    private  View  ctrl;
    private CameraPreview cp;
    private ListView mSolutionlist;
    boolean isExten;

    private int[] a ,b,c,d,e,f,g,h,i;
    private int[] mget;
    private int cpid;
    private int mdex=1;
    private int music;
    public static int recordsize;

    mCParam Luminance_param;
    mCParam contrast_param;
    mCParam saturation_param;
    mCParam tone_param;
    mCParam exposure_param;
    mCParam Gamma_param;
    mCParam Gain_param;
    mCParam WhiteBalance_param;
    mCParam Sharpness_param;

    private ImageButton pick;
    private ImageButton set;
    private ImageButton record;
    private ImageButton exit;
    private Button sav,solutionButton;
    private Button rec;

    private SeekBar mLuminance_seekbar;
    private SeekBar mContrast_seekbar;
    private SeekBar mSaturation_seekbar;
    private SeekBar mtone_seekbar;
    private SeekBar mexposure_seekbar;
    private SeekBar Gamma_seekbar;
    private SeekBar Gain_seekbar;
    private SeekBar WhiteBalance_seekbar;
    private SeekBar Sharpness_seekbar;

    private RadioButton mexposure_status_e;
    private RadioButton mexposure_status_p;
    private RadioGroup mexposure_status;
    private RadioButton WhiteBalance_status_e;
    private RadioButton WhiteBalance_status_p;
    private RadioGroup WhiteBalance_status;
    private RadioButton Frequency_status1;
    private RadioButton Frequency_status2;
    private RadioButton Frequency_status3;
    private RadioGroup Frequency_status;

    private TextView Luminance_text_value;
    private TextView contrast_text_value;
    private TextView saturation_text_value;
    private TextView tone_text_value;
    private TextView exposure_text_value;
    private TextView Gamma_text_value;
    private TextView Gain_text_value;
    private TextView WhiteBalance_text_value;
    private TextView Sharpness_text_value;

    private int Luminance_value;
    private int contrast_value;
    private int saturation_value;
    private int tone_value;
    private int exposure_value;
    private int Gamma_value;
    private int Gain_value;
    private int WhiteBalance_value;
    private int Sharpness_value;
   
    private int Luminance_max_value;
    private int contrast_max_value;
    private int saturation_max_value;
    private int tone_max_value;
    private int exposure_max_value;
    private int Gamma_max_value;
    private int Gain_max_value;
    private int WhiteBalance_max_value;
    private int Sharpness_max_value;

    private List<String> mList,mset;
    private String TAG = "MainActivity";
    private String sv_path="/mnt/sdcard/DCIM/";
    private String mLibV;

    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==1){
                updateView();
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
            cp = CameraPreview.getInstance(this);
            setContentView(R.layout.activity_main);
            preview = (FrameLayout)findViewById(R.id.cp1);
            preview.addView(cp);

            ndbm = new mDBManager(this);
            preferences = getSharedPreferences("PIDPreferences", MODE_PRIVATE);
            editor =preferences.edit();

            initView();

        super.onCreate(savedInstanceState);
    }


    private void initView()
    {
        left = (LinearLayout) findViewById(R.id.leftLay);
        right = (LinearLayout) findViewById(R.id.right_linearlayout);
        ctrlp=(LinearLayout)findViewById(R.id.ctrlp);

        mSolutionlist=(ListView)findViewById(R.id.solution_list);
     
        exit = (ImageButton) findViewById(R.id.exit);
        pick = (ImageButton) findViewById(R.id.pick);
        set = (ImageButton) findViewById(R.id.set);
        record=(ImageButton)findViewById(R.id.record);
        solutionButton=(Button)findViewById(R.id.solution_button);

        sp = new SoundPool(4, AudioManager.STREAM_SYSTEM, 5);
        music = sp.load(this, R.raw.start, 1);
        
        cp.setOnClickListener(new View.OnClickListener() 
        {
            public void onClick(View v)
            {
                if (right.getVisibility() ==View.VISIBLE||
                        left.getVisibility() ==View.VISIBLE) {
                    right.setVisibility(View.INVISIBLE);
                    left.setVisibility(View.INVISIBLE);
                } else {
                    right.setVisibility(View.VISIBLE);
                    left.setVisibility(View.VISIBLE);
                }
            }
        });
        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent= new Intent(MainActivity.this,RecordActivity.class);
                startActivity(mIntent);
            }
        });

        exit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                finish();
            }
        });
        pick.setOnClickListener(new View.OnClickListener() 
        {
            @Override
            public void onClick(View v)
            {
            	cp.setPictureSize(mdex);
            	Log.d(TAG,""+mdex);
                //获取当前系统时间
                SimpleDateFormat sDateFormat =new SimpleDateFormat("yyyyMMddhhmmss");
                String date= sDateFormat.format(new Date(System.currentTimeMillis()));

                cp.startCapture(sv_path,sv_path+date);
                Toast.makeText(cp.getContext(), "save in "+sv_path+date, Toast.LENGTH_LONG).show();
                sp.play(music, 1, 1, 0, 0, 1);
            }
        });
        set.setOnClickListener(new View.OnClickListener() 
        {
            @Override
            public void onClick(View v)
            {
                if(ctrl==null)
                    initPopupWindow();
                else if(ctrlp.getVisibility()!=View.VISIBLE)
                    ctrlp.setVisibility(View.VISIBLE);
                else
                    ctrlp.setVisibility(View.INVISIBLE);
            }
        });
        solutionButton.setOnClickListener(new View.OnClickListener() 
        {
            @Override
            public void onClick(View v)
            {
            	if(mSolutionlist.getVisibility()==View.VISIBLE){
                    mSolutionlist.setVisibility(View.INVISIBLE);
                }else {
                    mSolutionlist.setVisibility(View.VISIBLE);
                }
            }
        });

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMax(10);
        dialog.setMessage("正在初始化");
        dialog.show();

        new Thread(new Runnable(){
            @Override
            public void run(){
                try{
                    for(int i=0;i<3;i++){
                        Thread.sleep(400);
                        getCameraInfo();
                        dialog.incrementProgressBy(2);
                    }
                }catch (Exception e){
                    Toast.makeText(MainActivity.this, "摄像参数获取失败", Toast.LENGTH_SHORT);
                }finally{
                    initData(cpid);
                }
                dialog.dismiss();
            }
        }).start();
     }

    public void initControl(View v)
    {
       parent = (RelativeLayout)v.findViewById(R.id.linearLayout_parent);

       Luminance_max_value=saturation_param.getMax_val() - saturation_param.getMin_val();
       contrast_max_value=Luminance_param.getMax_val() - Luminance_param.getMin_val();
       saturation_max_value=contrast_param.getMax_val() - contrast_param.getMin_val();
       tone_max_value=tone_param.getMax_val() - tone_param.getMin_val();
       exposure_max_value=exposure_param.getMax_val() - exposure_param.getMin_val();
       Gamma_max_value=Gamma_param.getMax_val()-Gamma_param.getMin_val();
       Gain_max_value =Gain_param.getMax_val()-Gain_param.getMin_val();
       WhiteBalance_max_value=WhiteBalance_param.getMax_val()-WhiteBalance_param.getMin_val();
       Sharpness_max_value=Sharpness_param.getMax_val()-Sharpness_param.getMin_val();

        mLuminance_seekbar = (SeekBar) v.findViewById(R.id.Luminance_seekbar);
        mLuminance_seekbar.setOnSeekBarChangeListener(this);
        mContrast_seekbar = (SeekBar) v.findViewById(R.id.contrast_seekbar);
        mContrast_seekbar.setOnSeekBarChangeListener(this);
        mSaturation_seekbar = (SeekBar)v.findViewById(R.id.saturation_seekbar);
        mSaturation_seekbar.setOnSeekBarChangeListener(this);
        mtone_seekbar = (SeekBar) v.findViewById(R.id.tone_seekbar);
        mtone_seekbar.setOnSeekBarChangeListener(this);
        mexposure_seekbar = (SeekBar)v.findViewById(R.id.exposure_seekbar);
        mexposure_seekbar.setOnSeekBarChangeListener(this);
        Gamma_seekbar = (SeekBar)v.findViewById(R.id.Gamma_seekbar);
        Gamma_seekbar.setOnSeekBarChangeListener(this);
        Gain_seekbar = (SeekBar)v.findViewById(R.id.Gain_seekbar);
        Gain_seekbar.setOnSeekBarChangeListener(this);
        WhiteBalance_seekbar = (SeekBar)v.findViewById(R.id.WhiteBalance_seekbar);
        WhiteBalance_seekbar.setOnSeekBarChangeListener(this);
        Sharpness_seekbar = (SeekBar)v.findViewById(R.id.Sharpness_seekbar);
        Sharpness_seekbar.setOnSeekBarChangeListener(this);
        

        Luminance_text_value = (TextView) v.findViewById(R.id.Luminance_text_value);
        contrast_text_value = (TextView) v.findViewById(R.id.contrast_text_value);
        saturation_text_value = (TextView) v.findViewById(R.id.saturation_text_value);
        tone_text_value = (TextView) v.findViewById(R.id.tone_text_value);
        exposure_text_value = (TextView) v.findViewById(R.id.exposure_text_value);
        Gamma_text_value=(TextView) v.findViewById(R.id.Gamma_text_value);
        Gain_text_value=(TextView) v.findViewById(R.id.Gain_text_value);
        WhiteBalance_text_value=(TextView) v.findViewById(R.id.WhiteBalance_text_value);
        Sharpness_text_value=(TextView) v.findViewById(R.id.Sharpness_text_value);
      

        mSaturation_seekbar.setMax (Luminance_max_value);
        mLuminance_seekbar.setMax(contrast_max_value);
        mContrast_seekbar.setMax(saturation_max_value);
        mtone_seekbar.setMax(tone_max_value);
        mexposure_seekbar.setMax(exposure_max_value);
        Gamma_seekbar.setMax(Gamma_max_value);
        Gain_seekbar.setMax(Gain_max_value);
        WhiteBalance_seekbar.setMax(WhiteBalance_max_value);
        Sharpness_seekbar.setMax(Sharpness_max_value);
      
        
        mLuminance_seekbar.setProgress(Luminance_param.getUsr_val()-Luminance_param.getMin_val());
        mContrast_seekbar.setProgress(contrast_param.getUsr_val()-contrast_param.getMin_val());
        mSaturation_seekbar.setProgress(saturation_param.getUsr_val()-
                saturation_param.getMin_val());
        mtone_seekbar.setProgress(tone_param.getUsr_val()-tone_param.getMin_val());
        mexposure_seekbar.setProgress(exposure_param.getUsr_val()-exposure_param.getMin_val());
        Gamma_seekbar.setProgress(Gamma_param.getUsr_val()-Gamma_param.getMin_val());
        Gain_seekbar.setProgress(Gain_param.getUsr_val()-Gain_param.getMin_val());
        WhiteBalance_seekbar.setProgress(WhiteBalance_param.getUsr_val()-
                WhiteBalance_param.getMin_val());
        Sharpness_seekbar.setProgress(Sharpness_param.getUsr_val()-Sharpness_param.getMin_val());
      
       
        sav = (Button) v.findViewById(R.id.sava_cur);
        rec = (Button) v.findViewById(R.id.recorver);

        mexposure_status = (RadioGroup) v.findViewById(R.id.rg_state);
        mexposure_status_e=(RadioButton)v.findViewById(R.id.rg_state_e);
        mexposure_status_p=(RadioButton)v.findViewById(R.id.rg_state_p);
        WhiteBalance_status_e= (RadioButton) v.findViewById(R.id.rg_WhiteBalance_e);
        WhiteBalance_status_p= (RadioButton) v.findViewById(R.id.rg_WhiteBalance_p);
        WhiteBalance_status= (RadioGroup) v.findViewById(R.id.rg_WhiteBalance);
        Frequency_status1= (RadioButton) v.findViewById(R.id.rg_PowerLineFrequency1);
        Frequency_status2= (RadioButton) v.findViewById(R.id.rg_PowerLineFrequency2);
        Frequency_status3= (RadioButton) v.findViewById(R.id.rg_PowerLineFrequency3);
        Frequency_status= (RadioGroup) v.findViewById(R.id.rg_PowerLineFrequency);
        
        if(preferences.getBoolean("exposureflag", true)){
           mexposure_status_e.setChecked(true);
           mexposure_seekbar.setEnabled(false);
        }else{
           mexposure_status_p.setChecked(true);
        }
        if(preferences.getBoolean("WhiteBalanceFlag",true)){
            WhiteBalance_status_e.setChecked(true);
            WhiteBalance_seekbar.setEnabled(false);
        }else{
            WhiteBalance_status_p.setChecked(true);
        }
        switch(preferences.getInt("FrequencyValue",R.id.rg_PowerLineFrequency1)) {
            case R.id.rg_PowerLineFrequency1:
                Frequency_status1.setChecked(true); 
                break;
            case R.id.rg_PowerLineFrequency2:
                Frequency_status2.setChecked(true);
                break;
            case R.id.rg_PowerLineFrequency3:
                Frequency_status3.setChecked(true);
                break;
        }
        mexposure_status.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (group.getCheckedRadioButtonId()) {
                    case R.id.rg_state_e:
                        Toast.makeText(group.getContext(),
                                "已开启自动曝光", Toast.LENGTH_SHORT).show();
                        mexposure_seekbar.setEnabled(false);
                        cp.setAutoExposure(0);
                        break;
                    case R.id.rg_state_p:
                        mexposure_seekbar.setEnabled(true);
                        Toast.makeText(group.getContext(),
                                "请自定义曝光度", Toast.LENGTH_SHORT).show();
                        cp.setAutoExposure(1);
                        cp.setExposure(exposure_value);
                        break;
                    default:
                }
            }
        });
        WhiteBalance_status.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (group.getCheckedRadioButtonId()) {
                    case R.id.rg_WhiteBalance_e:
                        Toast.makeText(group.getContext(),
                                "已开启自动白平衡", Toast.LENGTH_SHORT).show();
                        WhiteBalance_seekbar.setEnabled(false);
                        cp.setWhiteBalanceAuto(1);
                        break;
                    case R.id.rg_WhiteBalance_p:
                        WhiteBalance_seekbar.setEnabled(true);
                        Toast.makeText(group.getContext(),
                                "请自定义白平衡度", Toast.LENGTH_SHORT).show();
                        cp.setWhiteBalanceAuto(0);
                        cp.setWhiteBalance(WhiteBalance_value);
                        break;
                    default:
                }
            }
        });
        Frequency_status.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (group.getCheckedRadioButtonId()) 
                {
                    case R.id.rg_PowerLineFrequency1:
                        Toast.makeText(group.getContext(),
                                "关闭防闪烁", Toast.LENGTH_SHORT).show();
                        cp.setPowerLineFrequency(0);
                        break;
                    case R.id.rg_PowerLineFrequency2:
                        Toast.makeText(group.getContext(),
                                "开启50HZ", Toast.LENGTH_SHORT).show();
                        cp.setPowerLineFrequency(1);
                        break;
                    case R.id.rg_PowerLineFrequency3:
                        Toast.makeText(group.getContext(),
                                "开启60HZ", Toast.LENGTH_SHORT).show();
                        cp.setPowerLineFrequency(2);
                        break;
                    default:
                }
            }
        });
        
        sav.setOnClickListener(new View.OnClickListener() 
        {
            @Override
            public void onClick(View v) 
            {
                Luminance_param.setUsr_val(Luminance_value);
                contrast_param.setUsr_val(contrast_value);
                saturation_param.setUsr_val(saturation_value);
                tone_param.setUsr_val(tone_value);
                exposure_param.setUsr_val(exposure_value);
                Gamma_param.setUsr_val(Gamma_value);
                Gain_param.setUsr_val(Gain_value);
                WhiteBalance_param.setUsr_val(WhiteBalance_value);
                Sharpness_param.setUsr_val(Sharpness_value);
                
                ndbm.update(Luminance_param);
                ndbm.update(contrast_param);
                ndbm.update(exposure_param);
                ndbm.update(saturation_param);
                ndbm.update(tone_param);
                ndbm.update(Gamma_param);
                ndbm.update(Gain_param);
                ndbm.update(WhiteBalance_param);
                ndbm.update(Sharpness_param);

                editor.putBoolean("exposureflag", mexposure_status_e.isChecked());
                editor.putBoolean("WhiteBalanceFlag",WhiteBalance_status_e.isChecked());
                editor.putInt("FrequencyValue",Frequency_status.getCheckedRadioButtonId());
                editor.commit();
            }
            
          });
        rec.setOnClickListener(new View.OnClickListener() 
        {
            @Override
            public void onClick(View v) {

                mLuminance_seekbar.setProgress(Luminance_param.getDef_val()-
                        Luminance_param.getMin_val());
                mContrast_seekbar.setProgress(contrast_param.getDef_val()-
                        contrast_param.getMin_val());
                mSaturation_seekbar.setProgress(saturation_param.getDef_val()-
                        saturation_param.getMin_val());
                mtone_seekbar.setProgress(tone_param.getDef_val()-tone_param.getMin_val());
                mexposure_seekbar.setProgress(exposure_param.getDef_val()-
                        exposure_param.getMin_val());
                Gamma_seekbar.setProgress(Gamma_param.getDef_val()-Gamma_param.getMin_val());
                Gain_seekbar.setProgress(Gain_param.getDef_val()-Gain_param.getMin_val());
                WhiteBalance_seekbar.setProgress(WhiteBalance_param.getDef_val()-
                        WhiteBalance_param.getMin_val());
                Sharpness_seekbar.setProgress(Sharpness_param.getDef_val()-
                        Sharpness_param.getMin_val());

                mexposure_status_e.setChecked(true);
                mexposure_status_e.setChecked(true);
            }
        });
        
        mexposure_status.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() 
        {
      
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (group.getCheckedRadioButtonId()) {
                    case R.id.rg_state_e:
                        Toast.makeText(group.getContext(),
                                "已开启自动曝光", Toast.LENGTH_SHORT).show();
                        mexposure_seekbar.setEnabled(false);
                        cp.setAutoExposure(0);
                        break;
                    case R.id.rg_state_p:
                        mexposure_seekbar.setEnabled(true);
                        Toast.makeText(group.getContext(),
                                "请自定义曝光度", Toast.LENGTH_SHORT).show();
                        cp.setAutoExposure(1);
                        cp.setExposure(exposure_value);
                        break;
                    default:
                }
            }
        });
    }

    public void updateView(){
        mSolutionlist.setAdapter(new ArrayAdapter<String>(this,
                R.layout.listview_item_style,
                mList));
        mSolutionlist.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        mSolutionlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mdex=mset.indexOf(mList.get(position));
                Log.d(TAG,""+mdex);
            }
        });
        mSolutionlist.setItemChecked(mdex, true);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) 
    {

        if (seekBar == mLuminance_seekbar)
        {
            Luminance_value =progress + Luminance_param.getMin_val();
            Luminance_text_value.setText("" + (progress + Luminance_param.getMin_val()));
            cp.setBrightness(progress + Luminance_param.getMin_val());
        }
        else if (seekBar == mContrast_seekbar)
        {
            contrast_value =progress + contrast_param.getMin_val();
            contrast_text_value.setText("" + (progress + contrast_param.getMin_val()));
            cp.setContrast(progress + contrast_param.getMin_val());
        }
        else if (seekBar == mSaturation_seekbar)
        {
            saturation_value =progress + saturation_param.getMin_val();
            saturation_text_value.setText("" + (progress + saturation_param.getMin_val()));
            cp.setSaturation(progress + saturation_param.getMin_val());
        }
        else if (seekBar == mtone_seekbar) 
        {
            tone_value =progress + tone_param.getMin_val();
            tone_text_value.setText("" + (progress + tone_param.getMin_val()));
            cp.setHue(progress + tone_param.getMin_val());
        } else if (seekBar == mexposure_seekbar) 
        {
            exposure_value = progress + exposure_param.getMin_val();
            exposure_text_value.setText("" + (progress + exposure_param.getMin_val()));
            cp.setExposure(progress + exposure_param.getMin_val());
        } else if (seekBar == Gamma_seekbar)
        {
            Gamma_value = progress + Gamma_param.getMin_val();
            Gamma_text_value.setText("" + (progress + Gamma_param.getMin_val()));
            cp.setGamma(progress + Gamma_param.getMin_val());
        } else if (seekBar == Gain_seekbar)
        {
            Gain_value = progress + Gain_param.getMin_val();
            Gain_text_value.setText("" + (progress + Gain_param.getMin_val()));
            cp.setGain(progress + Gain_param.getMin_val());
        } else if (seekBar == WhiteBalance_seekbar)
        {
            WhiteBalance_value = progress + WhiteBalance_param.getMin_val();
            WhiteBalance_text_value.setText("" + (progress + WhiteBalance_param.getMin_val()));
            cp.setWhiteBalance(progress + WhiteBalance_param.getMin_val());
        } else if (seekBar == Sharpness_seekbar)
        {
            Sharpness_value = progress + Sharpness_param.getMin_val();
            Sharpness_text_value.setText("" + (progress + Sharpness_param.getMin_val()));
            cp.setSharpness(progress + Sharpness_param.getMin_val());
        } 
       

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    public synchronized void initData(int pid) 
    {
      
      if (ndbm.dataIsExist(pid,"Luminance")) 
        {
            Luminance_param = new mCParam("Luminance", pid, a);
            contrast_param = new mCParam("contrast", pid, b);
            saturation_param = new mCParam("saturation", pid, c);
            tone_param = new mCParam("tone", pid, d);
            exposure_param = new mCParam("exposure", pid, e);
            Gamma_param=new mCParam("Gamma",pid,f);
            Gain_param=new mCParam("Gain",pid,g);
            WhiteBalance_param=new mCParam("WhiteBalance",pid,h);
            Sharpness_param=new mCParam("Sharpness",pid,i);
            
            ndbm.add(Luminance_param);
            ndbm.add(contrast_param);
            ndbm.add(saturation_param);
            ndbm.add(tone_param);
            ndbm.add(exposure_param);
            ndbm.add(Gamma_param);
            ndbm.add(Gain_param);
            ndbm.add(WhiteBalance_param);
            ndbm.add(Sharpness_param);
            
            Log.d(TAG, "存储到了数据库--> " );
           
            editor.putBoolean(pid+"", true);
            editor.commit();
            
        } else 
        {
            Luminance_param = ndbm.findByName(pid,"Luminance");
            contrast_param = ndbm.findByName(pid,"contrast");
            saturation_param = ndbm.findByName(pid,"saturation");
            tone_param = ndbm.findByName(pid,"tone");
            exposure_param = ndbm.findByName(pid,"exposure");
            Gamma_param=ndbm.findByName(pid,"Gamma");
            Gain_param=ndbm.findByName(pid,"Gain");
            WhiteBalance_param=ndbm.findByName(pid,"WhiteBalance");
            Sharpness_param=ndbm.findByName(pid,"Sharpness");
         
            Log.d(TAG, "从数据库读取了 --> " + Luminance_param.get_name());
        }

        mset= Arrays.asList(getResources()
                .getStringArray(R.array.picture_items));
        mList=new ArrayList<String>();
        for(int i=0;i<mget.length;i++){
            if(mget[i]!=0){
                mList.add(mset.get(i));
            }
        }

        Message message=new Message();
        message.what=1;
        mHandler.sendMessage(message);
 
    }

     private void initPopupWindow()
     {
         Luminance_value=Luminance_param.getUsr_val();
         contrast_value=contrast_param.getUsr_val();
         saturation_value=saturation_param.getUsr_val();
         tone_value=tone_param.getUsr_val();
         exposure_value=exposure_param.getUsr_val();
         Gamma_value=Gamma_param.getUsr_val();
         Gain_value=Gain_param.getUsr_val();
         WhiteBalance_value=WhiteBalance_param.getUsr_val();
         Sharpness_value=Sharpness_param.getUsr_val();
             
         mInflater = LayoutInflater.from(this); 
         ctrl = mInflater.inflate(R.layout.activity_two, null);
         initControl(ctrl);
         ctrlp.addView(ctrl);
     }
     
     protected void getCameraInfo()
     {
             cpid=cp.nativeGetPid();
             recordsize=cp.getRecordSize()-1;
             mLibV=cp.getLibVersion();
             mget=cp.getPictureSize();

             a = cp.nativeGetBrightnessArray();
             b = cp.nativeGetContrastArray();
             c = cp.nativeGetSaturationArray();
             d = cp.nativeGetHueArray();
             e = cp.nativeGetExposureArray();
             f = cp.nativeGetGammaArray();
             g = cp.nativeGetGainArray();
             h = cp.nativeGetWhiteBalanceArray();
             i = cp.nativeGetSharpnessArray();

            Log.d(TAG, "提取了设备值 --> " + cpid+" "+a[1]+" "+b[1]+" "+c[1]+" "+d[1]+
            		" "+e[1]+" "+f[1]+" " +g[1]+" "+h[1]+" "+i[1]+" "+mLibV );
     }

    @Override
    protected void onDestroy() {
        ndbm.closeDB();
        super.onDestroy();
        System.exit(0);
    }
        
}




