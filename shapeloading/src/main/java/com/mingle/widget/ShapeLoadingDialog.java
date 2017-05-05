package com.mingle.widget;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatDialog;
import android.view.View;

import com.mingle.shapeloading.R;

public class ShapeLoadingDialog extends AppCompatDialog{

    private ShapeLoadingView mShapeLoadingView;

    private Builder mBuilder;

    private ShapeLoadingDialog(Builder builder) {
        super(builder.mContext, R.style.custom_dialog);
        mBuilder = builder;
        setCancelable(mBuilder.mCancelable);
        setCanceledOnTouchOutside(mBuilder.mCanceledOnTouchOutside);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_shape_loading);

        mShapeLoadingView = (ShapeLoadingView) findViewById(R.id.shape_loading_view);

        mShapeLoadingView.setDelay(mBuilder.mDelay);
        mShapeLoadingView.setLoadingText(mBuilder.mLoadText);

        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mShapeLoadingView.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void show() {
        super.show();
        mShapeLoadingView.setVisibility(View.VISIBLE);
    }

    public Builder getBuilder() {
        return mBuilder;
    }

    public static class Builder{

        private Context mContext;

        private int mDelay = 80;

        private CharSequence mLoadText;

        private boolean mCancelable = true;

        private boolean mCanceledOnTouchOutside = true;

        public Builder(Context context) {
            mContext = context;
        }

        public Builder delay(int delay) {
            mDelay = delay;
            return this;
        }

        public Builder loadText(CharSequence loadText) {
            mLoadText = loadText;
            return this;
        }

        public Builder loadText(@StringRes int resId) {
            mLoadText = mContext.getString(resId);
            return this;
        }

        public Builder cancelable(boolean cancelable) {
            mCancelable = cancelable;
            mCanceledOnTouchOutside = cancelable;
            return this;
        }

        public Builder canceledOnTouchOutside(boolean canceledOnTouchOutside) {
            mCanceledOnTouchOutside = canceledOnTouchOutside;
            return this;
        }

        public ShapeLoadingDialog build(){
            return new ShapeLoadingDialog(this);
        }

        public ShapeLoadingDialog show(){
            ShapeLoadingDialog dialog = build();
            dialog.show();
            return dialog;
        }
    }
}
