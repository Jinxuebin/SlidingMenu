package com.jin.slidingmenutest;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.jin.slidingmenu.ListDataInterface;
import com.jin.slidingmenu.SlidingMenu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends Activity {

    private SlidingMenu slidingMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initData();
        initEvent();
    }

    private void initView() {
        slidingMenu = (SlidingMenu) findViewById(R.id.slidingMenu);
    }

    private void initData() {
        //设置昵称
        slidingMenu.setNickName("小玩子");
        Bitmap above_bg = BitmapFactory.decodeResource(getResources(), com.jin.slidingmenu.R.drawable.above_bg);
        //设置背景图片
        slidingMenu.setAboveBackgroudImg(above_bg);
        Bitmap photo = BitmapFactory.decodeResource(getResources(), com.jin.slidingmenu.R.drawable.photo);
        //设置头像
        slidingMenu.setmPhotoBitmap(photo);
        slidingMenu.setListDividerHeight(0);

        //设置list列表数据
//        customBlowView.setData(initListData());
        slidingMenu.setListData(new ListDataInterface() {
            @Override
            public List<Map<String, Object>> getData() {
                return initListData();
            }
        });
    }

    private void initEvent() {
        slidingMenu.setPhotoOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"我是头像",Toast.LENGTH_SHORT).show();
            }
        });

        slidingMenu.setNickNameOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"我是昵称",Toast.LENGTH_SHORT).show();
            }
        });

        slidingMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this, position+"",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private List<Map<String,Object>> initListData() {
        ArrayList<Map<String, Object>> lists = new ArrayList<>();
        Map<String, Object> map1 = new HashMap<>();
        Map<String, Object> map2 = new HashMap<>();
        Map<String, Object> map3 = new HashMap<>();
        Map<String, Object> map4 = new HashMap<>();
        Map<String, Object> map5 = new HashMap<>();
        map1.put("iv_icon", com.jin.slidingmenu.R.drawable.slidingmenu_05);
        map1.put("tv_list_name", "手机应用");
        lists.add(map1);
        map2.put("iv_icon", com.jin.slidingmenu.R.drawable.slidingmenu_08);
        map2.put("tv_list_name", "系统应用");
        lists.add(map2);
        map3.put("iv_icon", com.jin.slidingmenu.R.drawable.slidingmenu_10);
        map3.put("tv_list_name", "加星应用");
        lists.add(map3);
        map4.put("iv_icon", com.jin.slidingmenu.R.drawable.slidingmenu_12);
        map4.put("tv_list_name", "设置");
        lists.add(map4);
        return lists;
    }

}
