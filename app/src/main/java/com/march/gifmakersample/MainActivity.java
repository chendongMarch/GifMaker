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
import com.march.dev.utils.ToastUtils;
import com.march.gifmaker.GifMaker;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends BaseActivity {

    ImageView mImageView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void onInitViews(View view, Bundle saveData) {
        super.onInitViews(view, saveData);
        mImageView = getView(R.id.iv_image);

    }

//    @OnClick({R.id.btn_compose})
//    public void click(View view) {
//
//    }

    @Override
    protected void onClickView(View view) {
        super.onClickView(view);
        switch (view.getId()) {
            case R.id.btn_compose:
                SelectImageActivity.start(mActivity, 10);
                break;
        }
    }

    @Override
    protected int[] getViewsRegisterClickEvent() {
        return new int[]{R.id.btn_compose};
    }


    @Override
    protected String[] getPermission2Check() {
        return new String[]{PermissionUtils.PER_WRITE_EXTERNAL_STORAGE};
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMainEvent(SelectImageActivity.SelectImageEvent event) {
        switch (event.getMessage()) {
            case SelectImageActivity.SelectImageEvent.ON_SUCCESS:

                ToastUtils.show(event.mImageInfos.toString());

                Observable.just(event.mImageInfos)
                        .flatMap(new Function<List<ImageInfo>, ObservableSource<List<String>>>() {
                            @Override
                            public ObservableSource<List<String>> apply(@NonNull List<ImageInfo> imageInfos) throws Exception {
                                List<String> paths = new ArrayList<>();
                                for (ImageInfo imageInfo : imageInfos) {
                                    paths.add(imageInfo.getPath());
                                }
                                return Observable.just(paths);
                            }
                        })
                        .flatMap(new Function<List<String>, ObservableSource<List<Bitmap>>>() {
                            @Override
                            public ObservableSource<List<Bitmap>> apply(@NonNull List<String> strings) throws Exception {
                                List<Bitmap> bitmaps = new ArrayList<>();
                                for (String string : strings) {
                                    bitmaps.add(BitmapUtils.decodeFile(string, 600, 600));
                                }
                                return Observable.just(bitmaps);
                            }
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<List<Bitmap>>() {
                            @Override
                            public void accept(@NonNull List<Bitmap> bitmaps) throws Exception {
                                String absolutePath = new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis() + ".gif").getAbsolutePath();
                                new GifMaker(100).makeGifInThread(bitmaps, absolutePath, new GifMaker.OnGifMakerListener() {
                                    @Override
                                    public void onMakeGifSucceed(String outPath) {
                                        GlideUtils.with(mActivity, outPath).into(mImageView);
                                    }
                                });
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(@NonNull Throwable throwable) throws Exception {
                                throwable.printStackTrace();
                            }
                        });

                break;
        }

    }
}
