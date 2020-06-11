package com.chengshang.ad.Model;

/**
 * Created by ysh on 2018/4/12.
 *
 * @描述 ${TODO}\
 * <p>
 * Update by $Author on ${}.
 * @描述 ${TODO}\
 */

public class BaseEntityResponse<V> extends BaseResponse {
    private static final long serialVersionUID = -4111791272356261875L;
    private V data;
    private V datas;

    public V getData() {
        return data;
    }

    public void setData(V data) {
        this.data = data;
    }

    public V getDatas() {
        return datas;
    }

    public void setDatas(V datas) {
        this.datas = datas;
    }
}
