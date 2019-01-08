package cn.com.i_zj.udrive_az.utils.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;

import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.common.internal.Supplier;
import com.facebook.common.memory.MemoryTrimType;
import com.facebook.common.memory.MemoryTrimmable;
import com.facebook.common.memory.MemoryTrimmableRegistry;
import com.facebook.common.memory.NoOpMemoryTrimmableRegistry;
import com.facebook.common.util.ByteConstants;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.backends.okhttp3.OkHttpImagePipelineConfigFactory;
import com.facebook.imagepipeline.cache.MemoryCacheParams;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.decoder.ProgressiveJpegConfig;
import com.facebook.imagepipeline.image.ImmutableQualityInfo;
import com.facebook.imagepipeline.image.QualityInfo;
import com.facebook.imagepipeline.listener.RequestListener;
import com.facebook.imagepipeline.listener.RequestLoggingListener;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import okhttp3.OkHttpClient;

/**
 * Created by devin on 2016/8/10 14:52
 * Description Fresco配置
 */
public class ImagePipelineConfigFactory {

    private static final int MAX_HEAP_SIZE = (int) Runtime.getRuntime().maxMemory();
    public static final int MAX_DISK_CACHE_SIZE = 40 * ByteConstants.MB;
    public static final int MAX_MEMORY_CACHE_SIZE = MAX_HEAP_SIZE / 4;
    private static final String IMAGE_PIPELINE_CACHE_DIR = "fresco_cache";
    private static ImagePipelineConfig sImagePipelineConfig;
    private static ImagePipelineConfig sOkHttpImagePipelineConfig;

    /**
     * 使用Android自带的网络加载图片
     */
    public static ImagePipelineConfig getImagePipelineConfig(Context context) {
        if (sImagePipelineConfig == null) {
            ImagePipelineConfig.Builder configBuilder = ImagePipelineConfig.newBuilder(context);
            configBuilder.setProgressiveJpegConfig(mProgressiveJpegConfig);
            configBuilder.setBitmapsConfig(Bitmap.Config.ARGB_4444);
            configureCaches(configBuilder, context);
            configureLoggingListeners(configBuilder);
            configureOptions(configBuilder);

            MemoryTrimmableRegistry memoryTrimmableRegistry = NoOpMemoryTrimmableRegistry.getInstance();
            memoryTrimmableRegistry.registerMemoryTrimmable(new MemoryTrimmable() {
                @Override
                public void trim(MemoryTrimType trimType) {
                    final double suggestedTrimRatio = trimType.getSuggestedTrimRatio();

                    if (MemoryTrimType.OnCloseToDalvikHeapLimit.getSuggestedTrimRatio() == suggestedTrimRatio
                            || MemoryTrimType.OnSystemLowMemoryWhileAppInBackground.getSuggestedTrimRatio() == suggestedTrimRatio
                            || MemoryTrimType.OnSystemLowMemoryWhileAppInForeground.getSuggestedTrimRatio() == suggestedTrimRatio
                            ) {
                        //清除内存缓存
                        Fresco.getImagePipeline().clearMemoryCaches();
                    }
                }
            });
            configBuilder.setMemoryTrimmableRegistry(memoryTrimmableRegistry);
            sImagePipelineConfig = configBuilder.build();

        }
        return sImagePipelineConfig;
    }

    /**
     * 当内存紧张时采取的措施
     */
    public static MemoryTrimmableRegistry setMemoryTrimmableRegistry() {
        MemoryTrimmableRegistry memoryTrimmableRegistry = NoOpMemoryTrimmableRegistry.getInstance();
        memoryTrimmableRegistry.registerMemoryTrimmable(new MemoryTrimmable() {
            @Override
            public void trim(MemoryTrimType trimType) {
                final double suggestedTrimRatio = trimType.getSuggestedTrimRatio();

                if (MemoryTrimType.OnCloseToDalvikHeapLimit.getSuggestedTrimRatio() == suggestedTrimRatio
                        || MemoryTrimType.OnSystemLowMemoryWhileAppInBackground.getSuggestedTrimRatio() == suggestedTrimRatio
                        || MemoryTrimType.OnSystemLowMemoryWhileAppInForeground.getSuggestedTrimRatio() == suggestedTrimRatio
                        ) {
                    //清除内存缓存
                    Fresco.getImagePipeline().clearMemoryCaches();
                }
            }
        });
        return memoryTrimmableRegistry;
    }

    /**
     * 使用OkHttp网络库加载图片
     */
    public static ImagePipelineConfig getOkHttpImagePipelineConfig(Context context) {
        if (sOkHttpImagePipelineConfig == null) {
            OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                    .build();
            ImagePipelineConfig.Builder configBuilder =
                    OkHttpImagePipelineConfigFactory.newBuilder(context, okHttpClient);
            configBuilder.setBitmapsConfig(Bitmap.Config.ARGB_4444);
            configureCaches(configBuilder, context);
            configBuilder.setMemoryTrimmableRegistry(setMemoryTrimmableRegistry());
            configureLoggingListeners(configBuilder);
            sOkHttpImagePipelineConfig = configBuilder.build();
        }
        return sOkHttpImagePipelineConfig;
    }

    /**
     * 配置内存缓存和磁盘缓存
     */
    private static void configureCaches(ImagePipelineConfig.Builder configBuilder, Context context) {
//        File cacheDir = StorageUtils.getOwnCacheDirectory(TysApplication.getInstance(), Constants.IMAGE_CACHE_FOLDER_NAME);

        String fileName = Environment.getExternalStorageDirectory().toString()
                + File.separator
                + "tempCamera"
                + File.separator;
        File cacheDir = new File(fileName);
        final MemoryCacheParams bitmapCacheParams = new MemoryCacheParams(
                MAX_MEMORY_CACHE_SIZE, // Max total size of elements in the cache
                Integer.MAX_VALUE,                     // Max entries in the cache
                MAX_MEMORY_CACHE_SIZE, // Max total size of elements in eviction queue
                Integer.MAX_VALUE,                     // Max length of eviction queue
                Integer.MAX_VALUE);                    // Max cache entry size
        Supplier<MemoryCacheParams> memoryCacheParamsSupplier = new Supplier<MemoryCacheParams>() {
            public MemoryCacheParams get() {
                return bitmapCacheParams;
            }
        };
        DiskCacheConfig diskCacheConfig = DiskCacheConfig.newBuilder(context)
                .setBaseDirectoryPath(cacheDir)
                .setBaseDirectoryName(IMAGE_PIPELINE_CACHE_DIR)
                .setMaxCacheSize(MAX_DISK_CACHE_SIZE)
                .build();
        configBuilder
                .setBitmapMemoryCacheParamsSupplier(memoryCacheParamsSupplier)
                .setMainDiskCacheConfig(diskCacheConfig);


    }

    private static void configureLoggingListeners(ImagePipelineConfig.Builder configBuilder) {
        Set<RequestListener> requestListeners = new HashSet<>();
        requestListeners.add(new RequestLoggingListener());
        configBuilder.setRequestListeners(requestListeners);
    }

    private static void configureOptions(ImagePipelineConfig.Builder configBuilder) {
        configBuilder.setDownsampleEnabled(true);
    }

    //渐进式图片
    static ProgressiveJpegConfig mProgressiveJpegConfig = new ProgressiveJpegConfig() {
        @Override
        public int getNextScanNumberToDecode(int scanNumber) {
            return scanNumber + 2;
        }

        public QualityInfo getQualityInfo(int scanNumber) {
            boolean isGoodEnough = (scanNumber >= 5);
            return ImmutableQualityInfo.of(scanNumber, isGoodEnough, false);
        }
    };
}
