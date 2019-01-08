package cn.com.i_zj.udrive_az.model;

import java.io.Serializable;
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


    public static class DataBean implements Serializable {
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
        public static  final  int BAOLAI=1;
        public static  final  int POLO=2;
        public static  final  int LITE=3;
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
        private int brand_id;
        private int preferential_type;
        private float rebate;
        private float instant_rebate_amount;
        private String start_time;
        private String end_time;
        private int max_amount;

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

        public int getBrand_id() {
            return brand_id;
        }

        public void setBrand_id(int brand_id) {
            this.brand_id = brand_id;
        }

        public int getPreferential_type() {
            return preferential_type;
        }

        public void setPreferential_type(int preferential_type) {
            this.preferential_type = preferential_type;
        }

        public float getRebate() {
            return rebate;
        }

        public void setRebate(float rebate) {
            this.rebate = rebate;
        }

        public float getInstant_rebate_amount() {
            return instant_rebate_amount;
        }

        public void setInstant_rebate_amount(float instant_rebate_amount) {
            this.instant_rebate_amount = instant_rebate_amount;
        }

        public String getStart_time() {
            return start_time;
        }

        public void setStart_time(String start_time) {
            this.start_time = start_time;
        }

        public String getEnd_time() {
            return end_time;
        }

        public void setEnd_time(String end_time) {
            this.end_time = end_time;
        }

        public int getMax_amount() {
            return max_amount;
        }

        public void setMax_amount(int max_amount) {
            this.max_amount = max_amount;
        }
    }
}
