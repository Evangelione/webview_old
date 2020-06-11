package com.chengshang.ad.Model;

/**
 * APP名： InitialProject
 * 包名：com.chengshang.ad.Model
 * 作者：jiang-pc
 * 版本：
 * 创建日期：2019/6/11
 * 描述：
 * 修订历史：
 */
public class BaseModelResponse<V> extends BaseModel {
    private V result;

    public V getResult() {
        return result;
    }

    public void setResult(V result) {
        this.result = result;
    }
}
