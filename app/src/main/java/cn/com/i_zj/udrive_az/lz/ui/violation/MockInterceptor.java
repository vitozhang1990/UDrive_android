package cn.com.i_zj.udrive_az.lz.ui.violation;

import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MockInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Gson gson = new Gson();
        Response response = null;
        Response.Builder responseBuilder = new Response.Builder()
                .code(200)
                .message("")
                .request(chain.request())
                .protocol(Protocol.HTTP_1_0)
                .addHeader("content-type", "application/json");
        Request request = chain.request();
        if(request.url().toString().contains("mobile/illegal/list")) { //拦截指定地址
            String responseString = "{\"code\":1,\"message\":\"成功\",\"data\":{\"list\":[\"1\",\"2\",\"1\",\"2\",\"1\",\"2\",\"1\",\"2\",\"1\",\"2\",\"1\",\"2\",\"1\",\"2\",\"1\",\"2\",\"1\",\"2\",\"1\",\"2\",\"1\",\"2\",\"1\",\"2\"],\"total\":1,\"pageNum\":1,\"pageSize\":1,\"size\":1}}";
            responseBuilder.body(ResponseBody.create(MediaType.parse("application/json"), responseString.getBytes()));//将数据设置到body中
            response = responseBuilder.build(); //builder模式构建response
        }else{
            response = chain.proceed(request);
        }
        return response;
    }
}