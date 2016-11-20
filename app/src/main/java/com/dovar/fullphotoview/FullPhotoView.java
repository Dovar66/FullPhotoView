package com.dovar.fullphotoview;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;


import java.util.ArrayList;
import java.util.List;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * 可点击放大全屏显示的imageView
 * Created by Administrator on 2016/11/16 0016.
 */
public class FullPhotoView extends ImageView implements View.OnClickListener {
    private int photoViewId = -1;
    private int popupId = -1;
    private OnClickListener mListener;
    private List<Integer> ids;

    public FullPhotoView(Context context) {
        this(context, null);
    }

    public FullPhotoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FullPhotoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOnClickListener(this);
    }

    public FullPhotoView(Context context, int popupId, int photoViewId) {
        this(context);
        setCustomView(popupId, photoViewId);
    }

    public FullPhotoView(Context context, int popupId, int photoViewId, OnClickListener mListener, int... ids) {
        this(context);
        setCustomView(popupId, photoViewId, mListener, ids);
    }


    /**
     * 无需设置其他子控件点击事件
     *
     * @param popupId     popupWindow的布局ID
     * @param photoViewId popupWindow中的photoViewID
     */
    public void setCustomView(int popupId, int photoViewId) {
        this.popupId = popupId;
        this.photoViewId = photoViewId;
    }


    /**
     * 设置popupWindow的布局文件及其子控件点击事件
     *
     * @param popupId     popupWindow的布局ID
     * @param photoViewId popupWindow中的photoViewID
     * @param mListener   点击事件监听     mListener的实现中需要根据子控件id匹配处理
     * @param ids         popupWindow中除photoView以外的其他需要设置点击事件监听的子控件的ID
     */
    public void setCustomView(int popupId, int photoViewId, OnClickListener mListener, int... ids) {
        this.popupId = popupId;
        this.photoViewId = photoViewId;
        this.mListener = mListener;
//    mListener.onClick()方法的实现范例：
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case ids[0]:    do something            break;
//            case ids[1]:    do something            break;
//                ...
//            case ids[ids.length]:    do something            break;
//        }
//    }
        this.ids = new ArrayList<>();
        for (int id : ids
                ) {
            this.ids.add(id);
        }
    }


    @Override
    public void setOnClickListener(OnClickListener l) {
        //内部为此控件添加点击事件监听
        //使外部设置的点击事件监听无效
        super.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        View popupView;
        PhotoView photoView;
        if (popupId == -1 || photoViewId == -1) {
            //如果没有自定义popupWindow布局则加载默认布局文件
            popupView = View.inflate(getContext(), R.layout.popup_default, null);
            photoView = (PhotoView) popupView.findViewById(R.id.photoView);
        } else {
            popupView = View.inflate(getContext(), popupId, null);
            photoView = (PhotoView) popupView.findViewById(photoViewId);
        }
        //设置popupView中除photoView以外的子控件的点击事件
        if (mListener != null && ids != null) {
            for (int id : ids) {
                View child = popupView.findViewById(id);
                if (child != null) {
                    child.setOnClickListener(mListener);
                }
            }
        }
        //从MyPopupView获取bitmap设置给photoView，如果MyPopupView未设置src则给photoView设置安卓机器人图片
        //可修改为从uri加载原缩略图的大图，有需要的同学可以自己实现
        Bitmap mBitmap = ((BitmapDrawable) getDrawable()).getBitmap();
        if (mBitmap == null) {
            photoView.setImageResource(R.mipmap.ic_launcher);
        } else {
            photoView.setImageBitmap(mBitmap);
        }
        PopupWindow mPopupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
        PhotoViewAttacher mAttacher = new PhotoViewAttacher(photoView);
        //设置photoView单击、双击监听
        mAttacher.setOnDoubleTapListener(new MyOnDoubleTapListener(mAttacher, getContext(), mPopupWindow));
        //设置popupWindow背景色
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        //沉浸式全屏
        DRAppUtil.getInstance().hideSystemUI(mPopupWindow.getContentView());
        //设置popupWindow出现与消失的动画
        mPopupWindow.setAnimationStyle(R.style.popup_anim_style);
        //全屏显示
        mPopupWindow.showAtLocation(((Activity) getContext()).getWindow().getDecorView(), Gravity.CENTER, 0, 0);
    }
}
