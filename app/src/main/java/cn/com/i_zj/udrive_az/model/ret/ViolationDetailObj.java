package cn.com.i_zj.udrive_az.model.ret;

import java.io.Serializable;

public class ViolationDetailObj implements Serializable {

    private int id;
    private String pn;
    private int fen;
    private int money;
    private String address;
    private String description;
    private int state;
    private String remark;
    private String processSheetPhoto;
    private long breakTime;
    private String cityName;
    private OrderInfo order;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPn() {
        return pn;
    }

    public void setPn(String pn) {
        this.pn = pn;
    }

    public int getFen() {
        return fen;
    }

    public void setFen(int fen) {
        this.fen = fen;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getProcessSheetPhoto() {
        return processSheetPhoto;
    }

    public void setProcessSheetPhoto(String processSheetPhoto) {
        this.processSheetPhoto = processSheetPhoto;
    }

    public long getBreakTime() {
        return breakTime;
    }

    public void setBreakTime(long breakTime) {
        this.breakTime = breakTime;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public OrderInfo getOrder() {
        return order;
    }

    public void setOrder(OrderInfo order) {
        this.order = order;
    }

    public class OrderInfo implements Serializable {

        /**
         * number : 537073954103847418000
         * orderId : 374
         * startTime : 1553222327000
         * endTime : 1553222362000
         * startPark : 菁蓉国际停车场1211222
         * endPark : 菁蓉国际停车场1211222
         * pn :
         * brand : 大众宝来
         */

        private String number;
        private int orderId;
        private long startTime;
        private long endTime;
        private String startPark;
        private String endPark;
        private String pn;
        private String brand;
        private int state;

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public int getOrderId() {
            return orderId;
        }

        public void setOrderId(int orderId) {
            this.orderId = orderId;
        }

        public long getStartTime() {
            return startTime;
        }

        public void setStartTime(long startTime) {
            this.startTime = startTime;
        }

        public long getEndTime() {
            return endTime;
        }

        public void setEndTime(long endTime) {
            this.endTime = endTime;
        }

        public String getStartPark() {
            return startPark;
        }

        public void setStartPark(String startPark) {
            this.startPark = startPark;
        }

        public String getEndPark() {
            return endPark;
        }

        public void setEndPark(String endPark) {
            this.endPark = endPark;
        }

        public String getPn() {
            return pn;
        }

        public void setPn(String pn) {
            this.pn = pn;
        }

        public String getBrand() {
            return brand;
        }

        public void setBrand(String brand) {
            this.brand = brand;
        }

        public int getState() {
            return state;
        }

        public void setState(int state) {
            this.state = state;
        }
    }
}
