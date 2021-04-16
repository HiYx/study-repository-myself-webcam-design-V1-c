package com.camera.hdusbcamera;

import android.util.Log;

public class mCParam {
    private int  _id;
    private String _name;
    private int min_val ;
    private int def_val ;
    private int max_val ;
    private int usr_val;
   
    public int getUsr_val() {
        return usr_val;
    }
    public void setUsr_val(int usr_val) {
        this.usr_val = usr_val;
    }
    public int  get_id() {
        return _id;
    }
    public void set_id(int  _id) {
        this._id = _id;
    }
    public String get_name() {
        return _name;
    }
    public void set_name(String _name) {
        this._name = _name;
    }
    public int getMin_val() {
        return min_val;
    }
    public void setMin_val(int min_val) {
        this.min_val = min_val;
    }
    public int getDef_val() {
        return def_val;
    }
    public void setDef_val(int def_val) {
        this.def_val = def_val;
    }
    public int getMax_val() {
        return max_val;
    }
    public void setMax_val(int max_val) {
        this.max_val = max_val;
    }
    
   
    @Override
    public String toString() {
        return "mCParam [_id=" + _id + ", _name=" + _name + ", min_val=" + min_val + ", def_val="
                + def_val + ", max_val=" + max_val + ", usr_val=" + usr_val + "]";
    }
    public mCParam() {};
    public mCParam(String name, int  id,  int[] a)  {
        Log.d(name, name+" --------------------------------> creat");
        this._name =name+id;
        this.min_val=a[0];
        this.def_val=a[1];
        this.max_val=a[2];
        this.usr_val=a[1];
        if(name== "Luminance")
        { this._id=100000+id;}
        else if(name== "contrast")
        {this._id=200000+id;}
        else if(name== "saturation")
        {this._id=300000+id;}
        else if(name=="tone")
        {this._id=400000+id;}
        else if(name== "exposure")
        {this._id=500000+id;}
        else if(name== "Gamma")
        {this._id=600000+id;}
        else if(name== "Gain")
        {this._id=700000+id;}
        else if(name== "WhiteBalance")
        {this._id=800000+id;}
        else if(name== "Sharpness")
        {this._id=900000+id;}
        else if(name== "Backlight")
        {this._id=110000+id;}

        Log.d(name, this.getMax_val()+" --------------------------------> creat");
    }
    

}
