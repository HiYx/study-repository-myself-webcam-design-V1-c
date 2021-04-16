
package com.camera.hdusbcamera;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class RecordExpandableAdapter extends BaseExpandableListAdapter
{   private List<List<String>> ChildrenData;
    private List<String> GroupData;
    private Context mContext;
    RecordExpandableAdapter(List<List<String>> data1,List<String>data2,Context context){
        ChildrenData=data1;
        GroupData=data2;
        mContext=context;
    }

    @Override
    public int getGroupCount() {
        return GroupData.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return ChildrenData.get(i).size();
    }

    @Override
    public Object getGroup(int i) {
        return GroupData.get(i);
    }

    @Override
    public Object getChild(int i, int i2) {
        return ChildrenData.get(i).get(i2);
    }

    @Override
    public long getGroupId(int i) {
        return 0;
    }

    @Override
    public long getChildId(int i, int i2) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View convertView, ViewGroup viewGroup) {
        TextView myText ;
        if (convertView != null) {
            myText = (TextView)convertView;
            myText.setText(GroupData.get(i));
        } else {
            myText = createView(GroupData.get(i));
        }
        return myText;
    }

    @Override
    public View getChildView(int i, int i2, boolean b, View convertView , ViewGroup viewGroup) {
        CheckBox mCheckBox ;
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.solution_item, null);
            mCheckBox = (CheckBox)convertView.findViewById(R.id.checkBox_solution);
            convertView.setTag(mCheckBox);
        } else {
            mCheckBox = (CheckBox) convertView.getTag();
        }
        mCheckBox.setText(ChildrenData.get(i).get(i2));
        mCheckBox.setChecked(i2==RecordActivity.preferences.getInt("recordparam",0));
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int i, int k) {
    	if(MainActivity.recordsize>=k){
        if(RecordActivity.recording ){
            return false;
        }else{
        	if(RecordActivity.stoped){
        		return true;
        	}else{
        		 return false;
        	}
           
        }
    	}else{
    		return false;
    	}

    }

    private TextView createView(String content) {
        AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(
                ViewGroup.LayoutParams.FILL_PARENT, 60);
        TextView myText = new TextView(mContext);
        myText.setLayoutParams(layoutParams);
        myText.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
        myText.setPadding(40, 8, 3, 3);
        myText.setTextSize(24.0f);
        myText.setText(content);
        return myText;
    }
}
