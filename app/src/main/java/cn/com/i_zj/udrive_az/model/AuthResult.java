package cn.com.i_zj.udrive_az.model;

import java.io.Serializable;

public class AuthResult implements Serializable {

    private int code;
    private String message;
    private DataBean data;

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

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean implements Serializable {

        private int userState;
        private boolean pass;
        private StateBean idcard;
        private StateBean driver;
        private StateBean deposit;

        public int getUserState() {
            return userState;
        }

        public void setUserState(int userState) {
            this.userState = userState;
        }

        public boolean isPass() {
            return pass;
        }

        public void setPass(boolean pass) {
            this.pass = pass;
        }

        public StateBean getIdcard() {
            return idcard;
        }

        public void setIdcard(StateBean idcard) {
            this.idcard = idcard;
        }

        public StateBean getDriver() {
            return driver;
        }

        public void setDriver(StateBean driver) {
            this.driver = driver;
        }

        public StateBean getDeposit() {
            return deposit;
        }

        public void setDeposit(StateBean deposit) {
            this.deposit = deposit;
        }

        public static class StateBean implements Serializable {
            private int state;
            private String desc;

            public int getState() {
                return state;
            }

            public void setState(int state) {
                this.state = state;
            }

            public String getDesc() {
                return desc;
            }

            public void setDesc(String desc) {
                this.desc = desc;
            }
        }
    }
}
