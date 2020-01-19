package io.virtualapp.widgets.filepicker;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;

import io.virtualapp.R;

public class FilePicker {
    private Activity mActivity;
    private Fragment mFragment;
    private android.support.v4.app.Fragment mSupportFragment;
    private String mTitle;
    private String mTitleColor;
    private int theme = R.style.FilePickerTheme;
    private int mTitleStyle = R.style.FilePickerToolbarTextStyle;
    private String mBackgroundColor;
    private int mBackStyle;
    private int mRequestCode;
    private boolean mMutilyMode = true;
    private boolean mChooseMode = true;
    private String mAddText;
    private int mIconStyle;
    private String[] mFileTypes;
    private String mNotFoundFiles;
    private int mMaxNum;
    private String mStartPath;

    /**
     * 绑定Activity
     *
     * @param activity
     * @return
     */
    public FilePicker withActivity(Activity activity) {
        this.mActivity = activity;
        return this;
    }

    /**
     * 绑定Fragment
     *
     * @param fragment
     * @return
     */
    public FilePicker withFragment(Fragment fragment) {
        this.mFragment = fragment;
        return this;
    }

    /**
     * 绑定v4包Fragment
     *
     * @param supportFragment
     * @return
     */
    public FilePicker withSupportFragment(android.support.v4.app.Fragment supportFragment) {
        this.mSupportFragment = supportFragment;
        return this;
    }


    /**
     * 设置主标题
     *
     * @param title
     * @return
     */
    public FilePicker withTitle(String title) {
        this.mTitle = title;
        return this;
    }

    /**
     * 设置辩题颜色
     *
     * @param color
     * @return
     */
    @Deprecated
    public FilePicker withTitleColor(String color) {
        this.mTitleColor = color;
        return this;
    }

    /**
     * 设置主题
     *
     * @param theme
     * @return
     */
    public FilePicker withTheme(@StyleRes int theme) {
        this.theme = theme;
        return this;
    }

    /**
     * 设置标题的颜色和字体大小
     *
     * @param style
     * @return
     */
    public FilePicker withTitleStyle(@StyleRes int style) {
        this.mTitleStyle = style;
        return this;
    }

    /**
     * 设置背景色
     *
     * @param color
     * @return
     */
    public FilePicker withBackgroundColor(String color) {
        this.mBackgroundColor = color;
        return this;
    }

    /**
     * 请求码
     *
     * @param requestCode
     * @return
     */
    public FilePicker withRequestCode(int requestCode) {
        this.mRequestCode = requestCode;
        return this;
    }

    /**
     * 设置返回图标
     *
     * @param backStyle
     * @return
     */
    public FilePicker withBackIcon(int backStyle) {
        this.mBackStyle = 0;//默认样式
        this.mBackStyle = backStyle;
        return this;
    }

    /**
     * 设置选择模式，默认为true,多选；false为单选
     *
     * @param isMutily
     * @return
     */
    public FilePicker withMutilyMode(boolean isMutily) {
        this.mMutilyMode = isMutily;
        return this;
    }

    /**
     * 设置多选时按钮文字
     *
     * @param text
     * @return
     */
    public FilePicker withAddText(String text) {
        this.mAddText = text;
        return this;
    }

    /**
     * 设置文件夹图标风格
     *
     * @param style
     * @return
     */
    public FilePicker withIconStyle(int style) {
        this.mIconStyle = style;
        return this;
    }
    /**
     * 文件过滤
     *
     * @param arrs
     * @return
     */
    public FilePicker withFileFilter(String[] arrs) {
        this.mFileTypes = arrs;
        return this;
    }

    /**
     * 没有选中文件时的提示信息
     *
     * @param notFoundFiles
     * @return
     */
    public FilePicker withNotFoundBooks(String notFoundFiles) {
        this.mNotFoundFiles = notFoundFiles;
        return this;
    }

    /**
     * 设置最大选中数量
     *
     * @param num
     * @return
     */
    public FilePicker withMaxNum(int num) {
        this.mMaxNum = num;
        return this;
    }

    /**
     * 设置初始显示路径
     *
     * @param path
     * @return
     */
    public FilePicker withStartPath(String path) {
        this.mStartPath = path;
        return this;
    }

    /**
     * 设置选择模式，true为文件选择模式，false为文件夹选择模式，默认为true
     *
     * @param chooseMode
     * @return
     */
    public FilePicker withChooseMode(boolean chooseMode) {
        this.mChooseMode = chooseMode;
        return this;
    }

    public void start() {
        if (mActivity == null && mFragment == null && mSupportFragment == null) {
            throw new RuntimeException("You must pass Activity or Fragment by withActivity or withFragment or withSupportFragment method");
        }
        Intent intent = initIntent();
        Bundle bundle = getBundle();
        intent.putExtras(bundle);

        if (mActivity != null) {
            mActivity.startActivityForResult(intent, mRequestCode);
        } else if (mFragment != null) {
            mFragment.startActivityForResult(intent, mRequestCode);
        } else {
            mSupportFragment.startActivityForResult(intent, mRequestCode);
        }
    }


    private Intent initIntent() {
        Intent intent;
        if (mActivity != null) {
            intent = new Intent(mActivity, FilePickerActivity.class);
        } else if (mFragment != null) {
            intent = new Intent(mFragment.getActivity(), FilePickerActivity.class);
        } else {
            intent = new Intent(mSupportFragment.getActivity(), FilePickerActivity.class);
        }
        return intent;
    }

    @NonNull
    private Bundle getBundle() {
        ParamEntity paramEntity = new ParamEntity();
        paramEntity.setTitle(mTitle);
        paramEntity.setTheme(theme);
        paramEntity.setTitleColor(mTitleColor);
        paramEntity.setTitleStyle(mTitleStyle);
        paramEntity.setBackgroundColor(mBackgroundColor);
        paramEntity.setBackIcon(mBackStyle);
        paramEntity.setMutilyMode(mMutilyMode);
        paramEntity.setAddText(mAddText);
        paramEntity.setIconStyle(mIconStyle);
        paramEntity.setFileTypes(mFileTypes);
        paramEntity.setNotFoundFiles(mNotFoundFiles);
        paramEntity.setMaxNum(mMaxNum);
        paramEntity.setChooseMode(mChooseMode);
        paramEntity.setPath(mStartPath);
        Bundle bundle = new Bundle();
        bundle.putSerializable("param", paramEntity);
        return bundle;
    }
}
