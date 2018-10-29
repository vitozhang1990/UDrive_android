package cn.com.i_zj.udrive_az.utils;

import cn.com.i_zj.udrive_az.R;

/**
 * @author JayQiu
 * @create 2018/10/29
 * @Describe
 */
public class CarTypeImageUtils  {
    public static  int getCarImageByBrand(String brand,String carColor){
        int imageId=R.mipmap.pic_cartype_baolai;
        if(StringUtils.isEmpty(brand)){
            imageId= R.mipmap.pic_cartype_baolai;
        }else if("大众宝来".equals(brand)){
            imageId= R.mipmap.pic_cartype_baolai;
        }else if("北汽LITE".equals(brand)){
            imageId=getLiteCarColor(carColor);
        }else if("大众Polo".equals(brand)){
            imageId= R.mipmap.pic_cartype_polo;
        }else {
            imageId= R.mipmap.pic_cartype_baolai;
        }
        return imageId;
    }
    public static int getLiteCarColor(String carColor){
        int imageId=R.mipmap.pic_cartype_lite_blue;
        if(StringUtils.isEmpty(carColor)){
            imageId=R.mipmap.pic_cartype_lite_blue;
        }else if("蓝色".equals(carColor)){
            imageId=R.mipmap.pic_cartype_lite_blue;
        }else if("灰色".equals(carColor)){
            imageId=R.mipmap.pic_cartype_lite_grey;
        }else if("橘色".equals(carColor)){
            imageId=R.mipmap.pic_cartype_lite_orange;
        }else if("粉色".equals(carColor)){
            imageId=R.mipmap.pic_cartype_lite_pink;
        }else if("红色".equals(carColor)){
            imageId=R.mipmap.pic_cartype_lite_red;
        }else if("黄色".equals(carColor)){
            imageId=R.mipmap.pic_cartype_lite_yellow;
        }else {
            imageId=R.mipmap.pic_cartype_lite_blue;
        }
        return  imageId;
    }
}
