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
         * preferential_type : 2
         * discription : 新用户注册10优惠券
         * create_time : 1542691882000
         * rebate : 0.75
         * end_time : 21:00
         * preferential_amount : 100
         * type : 0
         * brand_id : 1
         * start_time : 09:00
         * distribute_time : 2018-11-20
         * instant_rebate_amount : 0
         * user_id : 649
         * preferential_id : 17
         * name : grtest
         * max_amount : 50
         * id : 11936
         * state : 0
         * validity : 5
         * area_name : 成都
         * orginazation_id : 2222
         */
        public static  final  int BAOLAI=1;
        public static  final  int POLO=2;
        public static  final  int LITE=3;

        private int preferential_type;
        private String discription;
        private long create_time;
        private double rebate;
        private String end_time;
        private int preferential_amount;
        private int type;
        private int brand_id;
        private String start_time;
        private String distribute_time;
        private int instant_rebate_amount;
        private int user_id;
        private int preferential_id;
        private String name;
        private int max_amount;
        private int id;
        private int state;
        private int validity;
        private String area_name;
        private int orginazation_id;

        public int getPreferential_type() {
            return preferential_type;
        }

        public void setPreferential_type(int preferential_type) {
            this.preferential_type = preferential_type;
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

        public double getRebate() {
            return rebate;
        }

        public void setRebate(double rebate) {
            this.rebate = rebate;
        }

        public String getEnd_time() {
            return end_time;
        }

        public void setEnd_time(String end_time) {
            this.end_time = end_time;
        }

        public int getPreferential_amount() {
            return preferential_amount;
        }

        public void setPreferential_amount(int preferential_amount) {
            this.preferential_amount = preferential_amount;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getBrand_id() {
            return brand_id;
        }

        public void setBrand_id(int brand_id) {
            this.brand_id = brand_id;
        }

        public String getStart_time() {
            return start_time;
        }

        public void setStart_time(String start_time) {
            this.start_time = start_time;
        }

        public String getDistribute_time() {
            return distribute_time;
        }

        public void setDistribute_time(String distribute_time) {
            this.distribute_time = distribute_time;
        }

        public int getInstant_rebate_amount() {
            return instant_rebate_amount;
        }

        public void setInstant_rebate_amount(int instant_rebate_amount) {
            this.instant_rebate_amount = instant_rebate_amount;
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

        public int getMax_amount() {
            return max_amount;
        }

        public void setMax_amount(int max_amount) {
            this.max_amount = max_amount;
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

        public String getArea_name() {
            return area_name;
        }

        public void setArea_name(String area_name) {
            this.area_name = area_name;
        }

        public int getOrginazation_id() {
            return orginazation_id;
        }

        public void setOrginazation_id(int orginazation_id) {
            this.orginazation_id = orginazation_id;
        }
    }
}
