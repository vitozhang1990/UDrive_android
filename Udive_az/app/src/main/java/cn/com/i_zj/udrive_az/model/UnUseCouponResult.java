package cn.com.i_zj.udrive_az.model;

import java.util.List;

public class UnUseCouponResult {
    /**
     * code : 1
     * message : 成功
     * data : [{"distribute_time":"2018-08-30","discription":"注册赠送优惠券","create_time":1535638453000,"user_id":13,"preferential_id":2,"name":"注册优惠券","preferential_amount":10,"id":9,"state":0,"validity":2,"type":0,"forbidden_state":1}]
     */

    private int code;
    private String message;
    private List<DataBean> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * distribute_time : 2018-08-30
         * discription : 注册赠送优惠券
         * create_time : 1535638453000
         * user_id : 13
         * preferential_id : 2
         * name : 注册优惠券
         * preferential_amount : 10
         * id : 9
         * state : 0
         * validity : 2
         * type : 0
         * forbidden_state : 1
         */

        private String distribute_time;
        private String discription;
        private long create_time;
        private int user_id;
        private int preferential_id;
        private String name;
        private int preferential_amount;
        private int id;
        private int state;
        private int validity;
        private int type;
        private int forbidden_state;

        public String getDistribute_time() {
            return distribute_time;
        }

        public void setDistribute_time(String distribute_time) {
            this.distribute_time = distribute_time;
        }

        public String getDiscription() {
            return discription;
        }

        public void setDiscription(String discription) {
            this.discription = discription;
        }

        public long getCreate_time() {
            return create_time;
        }

        public void setCreate_time(long create_time) {
            this.create_time = create_time;
        }

        public int getUser_id() {
            return user_id;
        }

        public void setUser_id(int user_id) {
            this.user_id = user_id;
        }

        public int getPreferential_id() {
            return preferential_id;
        }

        public void setPreferential_id(int preferential_id) {
            this.preferential_id = preferential_id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getPreferential_amount() {
            return preferential_amount;
        }

        public void setPreferential_amount(int preferential_amount) {
            this.preferential_amount = preferential_amount;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getState() {
            return state;
        }

        public void setState(int state) {
            this.state = state;
        }

        public int getValidity() {
            return validity;
        }

        public void setValidity(int validity) {
            this.validity = validity;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getForbidden_state() {
            return forbidden_state;
        }

        public void setForbidden_state(int forbidden_state) {
            this.forbidden_state = forbidden_state;
        }
    }
}
