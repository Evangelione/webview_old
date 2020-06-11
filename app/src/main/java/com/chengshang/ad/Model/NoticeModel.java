package com.chengshang.ad.Model;

import java.util.List;

/**
 * APP名： InitialProject
 * 包名：com.chengshang.ad.Model
 * 作者：jiang-pc
 * 版本：
 * 创建日期：2019/6/6
 * 描述：
 * 修订历史：
 */
public class NoticeModel {
    /**
     * list : [{"id":"5","title":"0","remark":"请您及时处理！","uid":"589","dateline":"1560219234","href":null},{"id":"4","title":"0","remark":"请您及时处理！","uid":"589","dateline":"1560219109","href":null},{"id":"3","title":"0","remark":"请您及时处理！","uid":"589","dateline":"1560154649","href":null},{"id":"2","title":"0","remark":"请您及时处理！","uid":"589","dateline":"1560154622","href":null},{"id":"1","title":"0","remark":"请您及时处理！","uid":"589","dateline":"1560150395","href":null}]
     * count : 1
     */

    private int count;
    private List<ListBean> list;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class ListBean {
        /**
         * id : 5
         * title : 0
         * remark : 请您及时处理！
         * uid : 589
         * dateline : 1560219234
         * href : null
         */

        private String id;
        private String title;
        private String remark;
        private String uid;
        private String dateline;
        private String href;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getDateline() {
            return dateline;
        }

        public void setDateline(String dateline) {
            this.dateline = dateline;
        }

        public String getHref() {
            return href;
        }

        public void setHref(String href) {
            this.href = href;
        }
    }
}
