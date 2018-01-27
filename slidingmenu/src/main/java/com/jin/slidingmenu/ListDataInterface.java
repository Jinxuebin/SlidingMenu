package com.jin.slidingmenu;

import java.util.List;
import java.util.Map;

/**
 * 更新侧滑栏的list数据
 * Created by Jin on 2018/1/3.
 */

public interface ListDataInterface {

    String ICON = "iv_icon";
    String NAME = "tv_list_name";

    List<Map<String, Object>> getData();

}
