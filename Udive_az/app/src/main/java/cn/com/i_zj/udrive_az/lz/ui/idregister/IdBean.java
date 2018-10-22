package cn.com.i_zj.udrive_az.lz.ui.idregister;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by wo on 2018/9/3.
 */

public class IdBean implements Parcelable{

    /**
     * address : 华盛顿特区宜宾法尼亚大道1600号
     * birth : 19610804
     * config_str : {"side":"face"}
     * face_rect : {"angle":-2.150944232940674,"center":{"x":1233.5045166015625,"y":456.1241455078125},"size":{"height":213.15013122558594,"width":233.1735382080078}}
     * name : 奥巴马
     * nationality : 汉
     * num : 123496196108047890
     * request_id : 20180903225745_38ed672bce3f236e8dd3d659ef38c862
     * sex : 男
     * success : true
     */

    private String address;
    private String birth;
    private String config_str;
    private FaceRectBean face_rect;
    private String name;
    private String nationality;
    private String num;
    private String request_id;
    private String sex;
    private boolean success;

    protected IdBean(Parcel in) {
        address = in.readString();
        birth = in.readString();
        config_str = in.readString();
        name = in.readString();
        nationality = in.readString();
        num = in.readString();
        request_id = in.readString();
        sex = in.readString();
        success = in.readByte() != 0;
    }

    public static final Creator<IdBean> CREATOR = new Creator<IdBean>() {
        @Override
        public IdBean createFromParcel(Parcel in) {
            return new IdBean(in);
        }

        @Override
        public IdBean[] newArray(int size) {
            return new IdBean[size];
        }
    };

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public String getConfig_str() {
        return config_str;
    }

    public void setConfig_str(String config_str) {
        this.config_str = config_str;
    }

    public FaceRectBean getFace_rect() {
        return face_rect;
    }

    public void setFace_rect(FaceRectBean face_rect) {
        this.face_rect = face_rect;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getRequest_id() {
        return request_id;
    }

    public void setRequest_id(String request_id) {
        this.request_id = request_id;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(address);
        parcel.writeString(birth);
        parcel.writeString(config_str);
        parcel.writeString(name);
        parcel.writeString(nationality);
        parcel.writeString(num);
        parcel.writeString(request_id);
        parcel.writeString(sex);
        parcel.writeByte((byte) (success ? 1 : 0));
    }

    public static class FaceRectBean {
        /**
         * angle : -2.150944232940674
         * center : {"x":1233.5045166015625,"y":456.1241455078125}
         * size : {"height":213.15013122558594,"width":233.1735382080078}
         */

        private double angle;
        private CenterBean center;
        private SizeBean size;

        public double getAngle() {
            return angle;
        }

        public void setAngle(double angle) {
            this.angle = angle;
        }

        public CenterBean getCenter() {
            return center;
        }

        public void setCenter(CenterBean center) {
            this.center = center;
        }

        public SizeBean getSize() {
            return size;
        }

        public void setSize(SizeBean size) {
            this.size = size;
        }

        public static class CenterBean {
            /**
             * x : 1233.5045166015625
             * y : 456.1241455078125
             */

            private double x;
            private double y;

            public double getX() {
                return x;
            }

            public void setX(double x) {
                this.x = x;
            }

            public double getY() {
                return y;
            }

            public void setY(double y) {
                this.y = y;
            }
        }

        public static class SizeBean {
            /**
             * height : 213.15013122558594
             * width : 233.1735382080078
             */

            private double height;
            private double width;

            public double getHeight() {
                return height;
            }

            public void setHeight(double height) {
                this.height = height;
            }

            public double getWidth() {
                return width;
            }

            public void setWidth(double width) {
                this.width = width;
            }
        }
    }

    @Override
    public String toString() {
        return "IdBean{" +
                "address='" + address + '\'' +
                ", birth='" + birth + '\'' +
                ", config_str='" + config_str + '\'' +
                ", face_rect=" + face_rect +
                ", name='" + name + '\'' +
                ", nationality='" + nationality + '\'' +
                ", num='" + num + '\'' +
                ", request_id='" + request_id + '\'' +
                ", sex='" + sex + '\'' +
                ", success=" + success +
                '}';
    }
}
