package com.march.gifmakersample;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;

import com.march.dev.app.activity.BaseActivity;
import com.march.dev.model.ImageInfo;
import com.march.dev.uikit.selectimg.SelectImageActivity;
import com.march.dev.utils.BitmapUtils;
import com.march.dev.utils.GlideUtils;
import com.march.dev.utils.PermissionUtils;
import com.march.gifmaker.GifMaker;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends BaseActivity {

    @BindView(R.id.iv_image) ImageView mImageView;

    private ExecutorService mExecutorService = Executors.newCachedThreadPool();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void onInitViews(View view, Bundle saveData) {
        super.onInitViews(view, saveData);
        mExecutorService = Executors.newCachedThreadPool();
    }

    @OnClick({R.id.btn_compose})
    public void onClickView(View view) {
        switch (view.getId()) {
            case R.id.btn_compose:
                SelectImageActivity.start(mActivity, 20);
                break;
        }
    }

    @Override
    protected String[] getPermission2Check() {
        return new String[]{PermissionUtils.PER_WRITE_EXTERNAL_STORAGE};
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMainEvent(final SelectImageActivity.SelectImageEvent event) {
        switch (event.getMessage()) {
            case SelectImageActivity.SelectImageEvent.ON_SUCCESS:
                final List<Bitmap> bitmaps = new ArrayList<>();
                Observable.fromIterable(event.mImageInfos)
                        .map(new Function<ImageInfo, Bitmap>() {
                            @Override
                            public Bitmap apply(@NonNull ImageInfo imageInfo) throws Exception {
                                return BitmapUtils.decodeFile(imageInfo.getPath(), 600, 600);
                            }
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Bitmap>() {
                            @Override
                            public void accept(@NonNull Bitmap bitmap) throws Exception {
                                bitmaps.add(bitmap);
                                if (bitmaps.size() == event.mImageInfos.size()) {
                                    composeGif(bitmaps);
                                }
                            }
                        });

                break;
        }
    }

    private void composeGif(List<Bitmap> bitmaps) {
        String absolutePath = new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis() + ".gif").getAbsolutePath();
        new GifMaker(100, mExecutorService)
                .makeGifInThread(bitmaps, absolutePath, new GifMaker.OnGifMakerListener() {
                    @Override
                    public void onMakeGifSucceed(String outPath) {
                        if (!isFinishing()) {
                            GlideUtils.with(mActivity, outPath).into(mImageView);
                        }
                    }
                });
    }
}
