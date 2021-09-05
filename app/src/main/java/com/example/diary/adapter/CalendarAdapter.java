package  com.example.diary.adapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Debug;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.diary.DiaryActivity;
import com.example.diary.R;
import com.example.diary.TimeCasuleActivity;
import com.example.diary.item.DayInfo;
import com.example.diary.ui.calendar.CalendarFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


/**
 * BaseAdapter를 상속받아 구현한 CalendarAdapter
 *
 * @author croute
 * @since 2011.03.08
 */
public class CalendarAdapter extends BaseAdapter
{
    DayViewHolde dayViewHolder;

    Date cal_day;
    Date present_day;
    String id;
    String Date;
    int Emotion;
    String state = "";

    private ArrayList<DayInfo> mDayList;
    private Context mContext;
    private int mResource;
    private LayoutInflater mLiInflater;


    DayInfo day;

    /**
     * Adpater 생성자
     *  @param context
     *            컨텍스트
     * @param textResource
     *            레이아웃 리소스
     * @param dayList
     */
    public CalendarAdapter(Context context, int textResource, ArrayList<DayInfo> dayList)
    {
        this.mContext = context;
        this.mDayList = dayList;
        this.mResource = textResource;
        this.mLiInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount()
    {
        // TODO Auto-generated method stub
        return mDayList.size();
    }

    @Override
    public Object getItem(int position)
    {
        // TODO Auto-generated method stub
        return mDayList.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return 0;
    }

    @SuppressLint({"WrongViewCast", "ResourceType"})
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        // 사용자 ID 받아오기
        Intent calendar = ((Activity) mContext).getIntent();
        id = calendar.getStringExtra("id");

        day = mDayList.get(position);

        // DayViewHolde dayViewHolder;

        if(convertView == null)
        {
            convertView = mLiInflater.inflate(mResource, null);

            if(position % 7 == 6)
            {
                convertView.setLayoutParams(new GridView.LayoutParams(getCellWidthDP()+getRestCellWidthDP(), getCellHeightDP()));
            }
            else
            {
                convertView.setLayoutParams(new GridView.LayoutParams(getCellWidthDP(), getCellHeightDP()));
            }


            dayViewHolder = new DayViewHolde();

            dayViewHolder.llBackground = (LinearLayout) convertView.findViewById(R.id.day_cell_ll_background);
            dayViewHolder.tvDay = (TextView) convertView.findViewById(R.id.cell_day);
            dayViewHolder.emotion = (ImageView) convertView.findViewById(R.id.calendar_emotion);

            convertView.setTag(dayViewHolder);
        }
        else
        {
            dayViewHolder = (DayViewHolde) convertView.getTag();
        }

        if(day != null)
        {
            dayViewHolder.tvDay.setText(day.getDay());
            if(day.isInMonth())
            {
                if(position % 7 == 0)
                {
                    if (day.getState() != null) {
                        if (day.getState().equals("happy")) {
                            dayViewHolder.emotion.setVisibility(View.VISIBLE);
                            dayViewHolder.emotion.setImageResource(R.drawable.happyemoty);
                        } else if (day.getState().equals("bujung")) {
                            dayViewHolder.emotion.setVisibility(View.VISIBLE);
                            dayViewHolder.emotion.setImageResource(R.drawable.bujungemoty);
                        }
                    }
                    dayViewHolder.tvDay.setTextColor(Color.RED);
                }
                else if(position % 7 == 6)
                {
                    if (day.getState() != null) {
                        if (day.getState().equals("happy")) {
                            dayViewHolder.emotion.setVisibility(View.VISIBLE);
                            dayViewHolder.emotion.setImageResource(R.drawable.happyemoty);
                        } else if (day.getState().equals("bujung")) {
                            dayViewHolder.emotion.setVisibility(View.VISIBLE);
                            dayViewHolder.emotion.setImageResource(R.drawable.bujungemoty);
                        }
                    }
                    dayViewHolder.tvDay.setTextColor(Color.BLUE);
                }
                else {
                    if (day.getState() != null) {
                        if (day.getState().equals("happy")) {
                            dayViewHolder.emotion.setVisibility(View.VISIBLE);
                            dayViewHolder.emotion.setImageResource(R.drawable.happyemoty);
                        } else if (day.getState().equals("bujung")) {
                            dayViewHolder.emotion.setVisibility(View.VISIBLE);
                            dayViewHolder.emotion.setImageResource(R.drawable.bujungemoty);
                        }
                    }
                        dayViewHolder.tvDay.setTextColor(Color.BLACK);
                    }
            }
            else
            {
                dayViewHolder.tvDay.setTextColor(Color.GRAY);
                dayViewHolder.tvDay.setVisibility(View.INVISIBLE);
                dayViewHolder.emotion.setVisibility(View.INVISIBLE);
            }
        }

        return convertView;
    }

    public void cleanDAY(int num){
        day = mDayList.get(num - 1);
        day.setState(null);
    }
    public class DayViewHolde
    {
        public LinearLayout llBackground;
        public TextView tvDay;
        public ImageView emotion;

    }

    private int getCellWidthDP()
    {
//      int width = mContext.getResources().getDisplayMetrics().widthPixels;
        //int cellWidth = 480/7;
        int cellWidth = 700/7;

        return cellWidth;
    }

    private int getRestCellWidthDP()
    {
//      int width = mContext.getResources().getDisplayMetrics().widthPixels;
        //int cellWidth = 480%7;
        int cellWidth = 700%7;

        return cellWidth;
    }

    private int getCellHeightDP()
    {
//      int height = mContext.getResources().getDisplayMetrics().widthPixels;
        int cellHeight = 1500/6;

        return cellHeight;
    }

}