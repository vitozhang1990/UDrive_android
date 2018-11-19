package cn.com.i_zj.udrive_az.network;

import android.widget.Toast;

import com.alipay.android.phone.mrpc.core.HttpException;
import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.JsonParseException;

import java.io.InterruptedIOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.ConnectException;
import java.net.UnknownHostException;

import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.model.ret.BaseRetObj;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * @author JayQiu
 * @create 2018/11/13
 * @Describe
 */
public abstract class UObserver<T> implements Observer<BaseRetObj<T>> {
    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public void onNext(BaseRetObj<T> baseRetObj) {
        onFinish();
        if (baseRetObj.getCode() == 1) {
            onSuccess(baseRetObj.getDate());

        } else {
            onException(baseRetObj.getCode(), baseRetObj.getMessage());
        }
    }

    @Override
    public void onError(Throwable e) {
        onFinish();
        if (e instanceof HttpException) {     //   HTTP错误
            onException(((HttpException) e).getCode(), "连接错误");
        } else if (e instanceof ConnectException
                || e instanceof UnknownHostException) {   //   连接错误
            onException(1000, "连接错误");
        } else if (e instanceof InterruptedIOException) {   //  连接超时
            onException(1001, "连接超时");
        } else if (e instanceof JsonParseException) {   //  解析错误
            onException(1002, "解析数据失败");
        } else if (e instanceof RetrofitException) {
            dealWithRetrofitException((RetrofitException) e);
        } else {
            onException(1004, "未知错误");
        }


    }

    private void dealWithRetrofitException(RetrofitException re) {
        switch (re.getCode()) {
            case 0:
                onException(re.getCode(), "未知错误");
                break;
            case 1:
                onException(re.getCode(), "未知错误");
                break;
            case 404:
                onException(re.getCode(), "请求不存在");
                break;
            case 500:
                onException(re.getCode(), "服务器出现意外");
                break;
            case 501:
                onException(re.getCode(), "不支持请求的工具");
                break;
            case 503:
                onException(re.getCode(), "无法获得服务");
                break;
            default:
                onException(re.getCode(), re.getMessage());
                break;

        }

    }

    @Override
    public void onComplete() {

    }

    /**
     * 请求成功
     *
     * @param response 服务器返回的数据
     */
    abstract public void onSuccess(T response);

    abstract public void onException(int code, String message);

    abstract public void onFinish();
}
