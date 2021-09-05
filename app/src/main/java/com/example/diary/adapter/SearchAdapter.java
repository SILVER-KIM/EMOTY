package com.example.diary.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.diary.R;
import com.example.diary.ShowDiaryActivity;
import com.example.diary.item.DiaryItem;
import com.example.diary.item.SearchItem;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class SearchAdapter extends BaseAdapter {
    Context context;

    FirebaseDatabase db;
    DatabaseReference myDB;

    /* 아이템을 세트로 담기 위한 어레이 */
    private ArrayList<SearchItem> sItems = new ArrayList<>();

    public void removeItem(int position) {
        sItems.remove(position);
    }

    @Override
    public int getCount() {
        return sItems.size();
    }

    @Override
    public SearchItem getItem(int position) {
        return sItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        context = parent.getContext();

        db = FirebaseDatabase.getInstance();
        myDB = db.getReference();

        /* 'listview_custom' Layout을 inflate하여 convertView 참조 획득 */
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.search_item, parent, false);
        }

        /* 'listview_custom'에 정의된 위젯에 대한 참조 획득 */
        TextView search_day = (TextView) convertView.findViewById(R.id.search_day);
        TextView search_title = (TextView) convertView.findViewById(R.id.search_title);
        ImageButton search_emoty = (ImageButton) convertView.findViewById(R.id.search_showEmoty);

        /* 각 리스트에 뿌려줄 아이템을 받아오는데 mMyItem 재활용 */
        final SearchItem myItem = getItem(position);

        search_emoty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent diary = new Intent(context, ShowDiaryActivity.class);
                diary.putExtra("title", myItem.getTitle());
                diary.putExtra("date", myItem.getDate());
                diary.putExtra("text", myItem.getText());
                diary.putExtra("emotion", myItem.getEmotion());
                context.startActivity(diary);
            }
        });

        /* 각 위젯에 세팅된 아이템을 뿌려준다 */
        search_emoty.setImageDrawable(myItem.getEmoty());
        search_day.setText(myItem.getDate());
        search_title.setText(myItem.getTitle());

        return convertView;
    }


    public void clear(){
        sItems.clear();
    }
    /* 아이템 데이터 추가를 위한 함수. 자신이 원하는대로 작성 */
    public void addItem(String date, String title, Drawable emoty, String text, int emotion) {

        SearchItem sItem = new SearchItem();

        /* MyItem에 아이템을 setting한다. */
        sItem.setDate(date);
        sItem.setTitle(title);
        sItem.setEmoty(emoty);
        sItem.setText(text);
        sItem.setEmotion(emotion);

        /* mItems에 MyItem을 추가한다. */
        sItems.add(sItem);
    }
}

