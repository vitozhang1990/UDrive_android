package cn.com.i_zj.udrive_az.utils.image;

import android.net.Uri;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;


/**
 * @author jayqiu
 * @describe Fresco  图片
 * @date 2016/8/5 10:00
 */
public class FrescoImgUtil {

    public static void loadImage(String url, SimpleDraweeView img) {
//        url="https://passport.tiyushe.com/avatar/000/24/95/39_avatar_middle.jpg?148162364";
        try {
            Uri uri = Uri.parse(url+"");
            img.setImageURI(uri);
        }catch (Exception e){

        }
    }

    public static void loadGif(String url, SimpleDraweeView img) {
        Uri uri = Uri.parse(url);
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setAutoPlayAnimations(true)
                .setUri(uri)
                .build();
        img.setController(controller);
    }


    /**
     * 自定义每个圆角大小
     *
     * @param url
     * @param img
     * @param topLeft
     * @param topRight
     * @param bottomLeft
     * @param bottomRight
     */
    public static void loadImage(String url, SimpleDraweeView img, float topLeft, float topRight, float bottomLeft, float bottomRight) {
        Uri uri = Uri.parse(url);
        img.setImageURI(uri);
        RoundingParams roundingParams = img.getHierarchy().getRoundingParams();
        roundingParams.setCornersRadii(topLeft, topRight, bottomLeft, bottomRight);
        img.getHierarchy().setRoundingParams(roundingParams);

    }


}
