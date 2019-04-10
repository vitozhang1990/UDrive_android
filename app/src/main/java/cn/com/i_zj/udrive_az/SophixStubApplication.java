package cn.com.i_zj.udrive_az;

import android.content.Context;
import android.support.annotation.Keep;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.taobao.sophix.PatchStatus;
import com.taobao.sophix.SophixApplication;
import com.taobao.sophix.SophixEntry;
import com.taobao.sophix.SophixManager;

public class SophixStubApplication extends SophixApplication {
    private final String TAG = "SophixStubApplication";

    // 此处SophixEntry应指定真正的Application，并且保证RealApplicationStub类名不被混淆。
    @Keep
    @SophixEntry(App.class)
    static class RealApplicationStub {
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
        initSophix();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SophixManager.getInstance().queryAndLoadNewPatch();
    }

    private void initSophix() {
        String appVersion = "0.0.0";
        try {
            appVersion = this.getPackageManager()
                    .getPackageInfo(this.getPackageName(), 0)
                    .versionName;
        } catch (Exception e) {
        }
        final SophixManager instance = SophixManager.getInstance();
        instance.setContext(this)
                .setAppVersion(appVersion)
                .setSecretMetaData("25997266", "5bd8b24742b0962b0de80c4e77282ee2", "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCU6HD5U3duxREZ8VJjprsaELgEJBfg4gp5+Ig9woYqtR5UPZM/sSNuKybJ8121OTKL3HZ1rUlLaYoM3wlC2y4JRjf9a704fnvSPgomV3GqVUZftHvaiyvWadDzZbyNL2vgUnUEciHa1+ybiMiUd3ztdODjwca+Mc3ERcYOWBCco+9p+1aqCD3y/GdtzwtIdC68SZ8egV1ySeiCoAAOasjjXsy5Dv+HRTCC8fJyVTo5vKyanNi2pkDMtY36ESzQDFCU6klKXoaBoPpTMe6kFhaw66WSjkKN6jeC5buAY3KQAsDs0fS9IU+AZeT/8wB8lm3PzCk0Cz8wsMZyYQNcNDblAgMBAAECggEAfAtN4UTYnxvm7ReMrJq+Di6TPE/c3Gcwlv6kb95V6cnLJB3PRiWIAEROIblr/IVTSBM1Uz4xanLWn3ZEvr7bELq/9YoErMUg3Lj1t47sjxpOb3doHVwF/q1TdQ7xDSPN16aY+EUjUqzA26gMfzEyjpToqpJHvzS51qDRJi3SWMkpGiKVbj2wOeRnbFyY9sxkD6zZjU3X+PiL/gHdYxuIpT89XjM1qNGhn57y+9uz8hoevlyDPstAtYdJmszxvE1E+DFXzCaQ55n4Aly+XipzIg4TEkaGxTfVvxh6Embkz0hsonCckZrdxhNfhh1Zx+vEwcS9hs31WlV700X6cIHamQKBgQDVYZqrhfQvpfcNu4IBZeBTug1veut4yFEeASzorY6RwZrxbByFEE834bLAwLPO8pM5XY6htehf8tq0xUurfE1P3uN37jWwVoLZTGVwNVN6EP8aYp79IRYkzSAfl10Q5N31eK8qi7js8+1kQiNyyK91UMN8/D4u1AEL1f4CI6wJ/wKBgQCypkDz/OP2LonU7IyxQ0Vkd2m2Ss3Fy8wIfmb8m6rW9ByoNcoWLDzZuPF0UrMxH7nMGu5qMWmxBJUf7XmVzbTZdx2KoY6wiE5TdiwVa1ofMln2865LFNj5MlfUj4EcIwfHUrl3cAxW8+Oy78Y7Yh2zCuUmpQrIKpfJDMltP1bXGwKBgERQqWo8yr7ujbgxzh+QdE+aA28rlXPn/kBy8+PSnRCBQZAFouPfIt2rPsWDbI3XD9eP4nhXevwtDmRNvjo6462smHrvIvU/3UigsuFH0WAFTQcvh+NW0nYZzi0PEtazz3QlnT8r2JrURa6RljPLmJEguFWtlGL9sRAPBPG+ZfgVAoGAa7v6+6iMSM/z6uXkSpoI5PdmVrpxsVCfcJfZv4iY1BxOk9osTobLA3mWktG0onFBeKbdFElMNaZPz/tEEWUGJQbCV/HpvOfWZFqlZx1gMeQJgPWfLEQcPNtxQAxyjedKL5fhpPd7WYymyify4ajfxDNT0aRCBkg5VNNTPYCu+/UCgYEAwb/SpoTG07285Q7qLSCCbM77cNPcvwHAHq/W2ZF2pPPtqsr2E0O5Jf6CA4OZL/Gist5v2hGHt5XFwpGDpK+UYBQXoxNUZSJCZIfpuqTpk9oemdaKiWuOWBLVTT7evTa7OPSTffHOncSKkDG0A5CICXjrB6DWqhVesJ8P5e7uO9c=")
                .setEnableDebug(true)
                .setEnableFullLog()
                .setPatchLoadStatusStub((mode, code, info, handlePatchVersion) -> {
                    if (code == PatchStatus.CODE_LOAD_SUCCESS) {
                        Log.i(TAG, "sophix load patch success!");
                    } else if (code == PatchStatus.CODE_LOAD_RELAUNCH) {
                        // 如果需要在后台重启，建议此处用SharePreference保存状态。
                        Log.i(TAG, "sophix preload patch success. restart app to make effect.");
                    }
                })
                .initialize();
    }
}