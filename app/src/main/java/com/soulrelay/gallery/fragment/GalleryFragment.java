package com.soulrelay.gallery.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.soulrelay.gallery.R;
import com.soulrelay.gallery.adapter.GalleryAdapter;
import com.soulrelay.gallery.bean.GalleryItem;
import com.soulrelay.gallery.bean.ImageItem;
import com.soulrelay.gallery.handler.CommonHandler;
import com.soulrelay.gallery.handler.IHandlerMessage;
import com.soulrelay.gallery.utils.FileOperationUtils;
import com.soulrelay.gallery.utils.ThreadPoolUtils;
import com.soulrelay.gallery.utils.ToastUtils;
import com.soulrelay.gallery.view.gallery.MyViewPager;
import com.soulrelay.library.imageloader.ImageLoaderUtil;
import com.soulrelay.library.imageloader.ImageSaveListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Author:    SuS
 * Version    V1.0
 * Date:      17/2/14
 * Description:  图集
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 17/2/14          SuS                 1.0               1.0
 * Why & What is modified:
 */
public class GalleryFragment extends Fragment implements IHandlerMessage, View.OnClickListener, DialogInterface.OnShowListener, DialogInterface.OnDismissListener,
        ViewPager.OnPageChangeListener, GalleryAdapter.OnGalleryAdapterCallback {

    private CommonHandler<GalleryFragment> handler;
    private View rootView;
    private static final int LOAD_STATE_FAIL = 1;
    private static final int LOAD_STATE_SUCC = 2;
    private static final int LOAD_STATE_LOADING = 3;
    private static final int MSG_CALL_DATA_FAIL = 21;
    private static final int MSG_CALL_DATA_SUCC = 22;
    private static final int MSG_CALL_NO_DATA = 23;
    private static final int MSG_PIC_SAVE_SUCC = 24;
    private static final int MSG_PIC_SAVE_FAIL = 25;
    private MyViewPager viewPager;
    private TextView mDesc;
    private TextView mDescTitle;
    private TextView mDescNumber;
    private View mSavePicParent;
    private TextView mSaveCurrPos;
    private View mSaveBtn;
    private View mDescParent;
    private GalleryAdapter adapter;
    private List<ImageItem> list;
    private GalleryItem gallery;
    private long id;
    private boolean isDismiss;
    private OnPhotoTapListener onPhotoTapListener;
    private int loadState = LOAD_STATE_LOADING;
    private int fini_rate;//0-100

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    public static GalleryFragment newInstance(@Nullable Bundle bundle) {
        GalleryFragment fragment = new GalleryFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_gallery, container, false);
            initView(rootView);
        }
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
        Bundle bundle = getArguments();
        if (bundle != null) {
            gallery = (GalleryItem) bundle.getSerializable("gallery");
            //  id = gallery.getId();
            initData();
        }
    }

    private void initView(View rootView) {
        handler = new CommonHandler<GalleryFragment>(this);
        mSaveCurrPos = (TextView) rootView.findViewById(R.id.gallery_curr_pos);
        mSaveBtn = rootView.findViewById(R.id.gallery_save_btn);
        mSavePicParent = rootView.findViewById(R.id.gallery_save_layout);
        viewPager = (MyViewPager) rootView.findViewById(R.id.fvp_gallery);
        mDesc = (TextView) rootView.findViewById(R.id.tv_desc);
        mDescTitle = (TextView) rootView.findViewById(R.id.tv_desc_title);
        mDescNumber = (TextView) rootView.findViewById(R.id.tv_desc_number);
        mDescParent = rootView.findViewById(R.id.gallery_desc_layout);
        mDescParent.setOnClickListener(this);
        mSaveBtn.setOnClickListener(this);
        viewPager.addOnPageChangeListener(this);
        viewPager.setCurrentItem(0);
        viewPager.setOffscreenPageLimit(3);
        adapter = new GalleryAdapter(this.getContext(), this);
        viewPager.setAdapter(adapter);
    }

    private void initData() {
        requestData();
    }


    private void requestData() {
        String data = "{\n" +
                "  \"data\": {\n" +
                "    \"image\": [\n" +
                "      {\n" +
                "        \"brief\": \"\\u5317\\u4eac\\u65f6\\u95f42\\u670815\\u65e5\\u51cc\\u6668\\uff0c2017\\u52b3\\u4f26\\u65af\\u4e16\\u754c\\u4f53\\u80b2\\u5956\\u9881\\u5956\\u5178\\u793c\\u4e3e\\u884c\\uff0c\\u661f\\u5149\\u71a0\\u71a0\\u3002\\u535a\\u5c14\\u7279\\u3001\\u62dc\\u5c14\\u65af\\u9886\\u8854\\u51fa\\u5e2d\\uff0c\\u4e2d\\u56fd\\u524d\\u4f53\\u64cd\\u8fd0\\u52a8\\u5458\\u674e\\u5c0f\\u9e4f\\u5e05\\u6c14\\u4eae\\u76f8\\u3002\", \n" +
                "        \"image\": \"http://image.sports.baofeng.com/847fa94a8d37f58a435b9f61e9743b76\", \n" +
                "        \"key\": \"\", \n" +
                "        \"title\": \"\\u52b3\\u4f26\\u65af\\u7ea2\\u6bef\\u79c0:\\u535a\\u5c14\\u7279\\u9886\\u8854 \\u674e\\u5c0f\\u9e4f\\u5e05\\u6c14\\u4eae\\u76f8\", \n" +
                "        \"type\": \"image\"\n" +
                "      }, \n" +
                "      {\n" +
                "        \"brief\": \"\\u5317\\u4eac\\u65f6\\u95f42\\u670815\\u65e5\\u51cc\\u6668\\uff0c2017\\u52b3\\u4f26\\u65af\\u4e16\\u754c\\u4f53\\u80b2\\u5956\\u9881\\u5956\\u5178\\u793c\\u4e3e\\u884c\\uff0c\\u661f\\u5149\\u71a0\\u71a0\\u3002\\u535a\\u5c14\\u7279\\u3001\\u62dc\\u5c14\\u65af\\u9886\\u8854\\u51fa\\u5e2d\\uff0c\\u4e2d\\u56fd\\u524d\\u4f53\\u64cd\\u8fd0\\u52a8\\u5458\\u674e\\u5c0f\\u9e4f\\u5e05\\u6c14\\u4eae\\u76f8\\u3002\", \n" +
                "        \"image\": \"http://image.sports.baofeng.com/fb1bf83c731cb914fdd655b8d740804b\", \n" +
                "        \"key\": \"\", \n" +
                "        \"title\": \"\\u52b3\\u4f26\\u65af\\u7ea2\\u6bef\\u79c0:\\u535a\\u5c14\\u7279\\u9886\\u8854 \\u674e\\u5c0f\\u9e4f\\u5e05\\u6c14\\u4eae\\u76f8\", \n" +
                "        \"type\": \"image\"\n" +
                "      }, \n" +
                "      {\n" +
                "        \"brief\": \"\\u5317\\u4eac\\u65f6\\u95f42\\u670815\\u65e5\\u51cc\\u6668\\uff0c2017\\u52b3\\u4f26\\u65af\\u4e16\\u754c\\u4f53\\u80b2\\u5956\\u9881\\u5956\\u5178\\u793c\\u4e3e\\u884c\\uff0c\\u661f\\u5149\\u71a0\\u71a0\\u3002\\u535a\\u5c14\\u7279\\u3001\\u62dc\\u5c14\\u65af\\u9886\\u8854\\u51fa\\u5e2d\\uff0c\\u4e2d\\u56fd\\u524d\\u4f53\\u64cd\\u8fd0\\u52a8\\u5458\\u674e\\u5c0f\\u9e4f\\u5e05\\u6c14\\u4eae\\u76f8\\u3002\", \n" +
                "        \"image\": \"http://image.sports.baofeng.com/1f9361c7b63c2874928a3f7a308d806f\", \n" +
                "        \"key\": \"\", \n" +
                "        \"title\": \"\\u52b3\\u4f26\\u65af\\u7ea2\\u6bef\\u79c0:\\u535a\\u5c14\\u7279\\u9886\\u8854 \\u674e\\u5c0f\\u9e4f\\u5e05\\u6c14\\u4eae\\u76f8\", \n" +
                "        \"type\": \"image\"\n" +
                "      }, \n" +
                "      {\n" +
                "        \"brief\": \"\\u5317\\u4eac\\u65f6\\u95f42\\u670815\\u65e5\\u51cc\\u6668\\uff0c2017\\u52b3\\u4f26\\u65af\\u4e16\\u754c\\u4f53\\u80b2\\u5956\\u9881\\u5956\\u5178\\u793c\\u4e3e\\u884c\\uff0c\\u661f\\u5149\\u71a0\\u71a0\\u3002\\u535a\\u5c14\\u7279\\u3001\\u62dc\\u5c14\\u65af\\u9886\\u8854\\u51fa\\u5e2d\\uff0c\\u4e2d\\u56fd\\u524d\\u4f53\\u64cd\\u8fd0\\u52a8\\u5458\\u674e\\u5c0f\\u9e4f\\u5e05\\u6c14\\u4eae\\u76f8\\u3002\", \n" +
                "        \"image\": \"http://image.sports.baofeng.com/7723c593374cd585603e0ed57e810d7a\", \n" +
                "        \"key\": \"\", \n" +
                "        \"title\": \"\\u52b3\\u4f26\\u65af\\u7ea2\\u6bef\\u79c0:\\u535a\\u5c14\\u7279\\u9886\\u8854 \\u674e\\u5c0f\\u9e4f\\u5e05\\u6c14\\u4eae\\u76f8\", \n" +
                "        \"type\": \"image\"\n" +
                "      }, \n" +
                "      {\n" +
                "        \"brief\": \"\\u5317\\u4eac\\u65f6\\u95f42\\u670815\\u65e5\\u51cc\\u6668\\uff0c2017\\u52b3\\u4f26\\u65af\\u4e16\\u754c\\u4f53\\u80b2\\u5956\\u9881\\u5956\\u5178\\u793c\\u4e3e\\u884c\\uff0c\\u661f\\u5149\\u71a0\\u71a0\\u3002\\u535a\\u5c14\\u7279\\u3001\\u62dc\\u5c14\\u65af\\u9886\\u8854\\u51fa\\u5e2d\\uff0c\\u4e2d\\u56fd\\u524d\\u4f53\\u64cd\\u8fd0\\u52a8\\u5458\\u674e\\u5c0f\\u9e4f\\u5e05\\u6c14\\u4eae\\u76f8\\u3002\", \n" +
                "        \"image\": \"http://image.sports.baofeng.com/bacd0a06f8a93278bd95bd813ad4e7a4\", \n" +
                "        \"key\": \"\", \n" +
                "        \"title\": \"\\u52b3\\u4f26\\u65af\\u7ea2\\u6bef\\u79c0:\\u535a\\u5c14\\u7279\\u9886\\u8854 \\u674e\\u5c0f\\u9e4f\\u5e05\\u6c14\\u4eae\\u76f8\", \n" +
                "        \"type\": \"image\"\n" +
                "      }, \n" +
                "      {\n" +
                "        \"brief\": \"\\u5317\\u4eac\\u65f6\\u95f42\\u670815\\u65e5\\u51cc\\u6668\\uff0c2017\\u52b3\\u4f26\\u65af\\u4e16\\u754c\\u4f53\\u80b2\\u5956\\u9881\\u5956\\u5178\\u793c\\u4e3e\\u884c\\uff0c\\u661f\\u5149\\u71a0\\u71a0\\u3002\\u535a\\u5c14\\u7279\\u3001\\u62dc\\u5c14\\u65af\\u9886\\u8854\\u51fa\\u5e2d\\uff0c\\u4e2d\\u56fd\\u524d\\u4f53\\u64cd\\u8fd0\\u52a8\\u5458\\u674e\\u5c0f\\u9e4f\\u5e05\\u6c14\\u4eae\\u76f8\\u3002\", \n" +
                "        \"image\": \"http://image.sports.baofeng.com/40c39504ab285f349d847fdd76cffaae\", \n" +
                "        \"key\": \"\", \n" +
                "        \"title\": \"\\u52b3\\u4f26\\u65af\\u7ea2\\u6bef\\u79c0:\\u535a\\u5c14\\u7279\\u9886\\u8854 \\u674e\\u5c0f\\u9e4f\\u5e05\\u6c14\\u4eae\\u76f8\", \n" +
                "        \"type\": \"image\"\n" +
                "      }, \n" +
                "      {\n" +
                "        \"brief\": \"\\u5317\\u4eac\\u65f6\\u95f42\\u670815\\u65e5\\u51cc\\u6668\\uff0c2017\\u52b3\\u4f26\\u65af\\u4e16\\u754c\\u4f53\\u80b2\\u5956\\u9881\\u5956\\u5178\\u793c\\u4e3e\\u884c\\uff0c\\u661f\\u5149\\u71a0\\u71a0\\u3002\\u535a\\u5c14\\u7279\\u3001\\u62dc\\u5c14\\u65af\\u9886\\u8854\\u51fa\\u5e2d\\uff0c\\u4e2d\\u56fd\\u524d\\u4f53\\u64cd\\u8fd0\\u52a8\\u5458\\u674e\\u5c0f\\u9e4f\\u5e05\\u6c14\\u4eae\\u76f8\\u3002\", \n" +
                "        \"image\": \"http://image.sports.baofeng.com/c101f601ae1a3a427ca786859327aabe\", \n" +
                "        \"key\": \"\", \n" +
                "        \"title\": \"\\u52b3\\u4f26\\u65af\\u7ea2\\u6bef\\u79c0:\\u535a\\u5c14\\u7279\\u9886\\u8854 \\u674e\\u5c0f\\u9e4f\\u5e05\\u6c14\\u4eae\\u76f8\", \n" +
                "        \"type\": \"image\"\n" +
                "      }\n" +
                "    ]\n" +
                "  }, \n" +
                "  \"errno\": 10000, \n" +
                "  \"message\": \"OK\"\n" +
                "}";
        onGetDataSucceeded(data);
    }

    private void onGetDataSucceeded(String data) {
        int status = 0;
        String message = null;
        try {
            JSONObject json = new JSONObject(data);
            status = json.optInt("errno");
            if (status == 10000) {
                List<ImageItem> galleries = new ArrayList<>();
                JSONArray array = json.optJSONObject("data").optJSONArray("image");
                if (array.length() == 0) {
                    handler.obtainMessage(MSG_CALL_NO_DATA).sendToTarget();
                }
                for (int i = 0; i < array.length(); i++) {
                    ImageItem item = FileOperationUtils.fromJson(array.optString(i), ImageItem.class);
                    if (item == null) {
                        continue;
                    }
                    galleries.add(item);
                }
                handler.obtainMessage(MSG_CALL_DATA_SUCC, galleries).sendToTarget();
            } else {
                handler.obtainMessage(MSG_CALL_DATA_FAIL).sendToTarget();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            sendFailureMsg();
        }
    }

    private void sendFailureMsg() {
        Message msg = new Message();
        msg.what = MSG_CALL_DATA_FAIL;
        handler.sendMessage(msg);
    }

    private void updateGalleryItems(List<ImageItem> items) {
        adapter.update(items);
        onPageSelected(0);
    }

    @Override
    public void handlerCallback(Message msg) {
        switch (msg.what) {
            case MSG_CALL_DATA_SUCC:
                list = (List<ImageItem>) msg.obj;
                if (list != null && list.size() > 0) {
                    loadState = LOAD_STATE_SUCC;
                    updateGalleryItems(list);
                }
                break;
            case MSG_CALL_DATA_FAIL:
                loadState = LOAD_STATE_FAIL;
                break;
            case MSG_CALL_NO_DATA:
                loadState = LOAD_STATE_SUCC;
                break;
            case MSG_PIC_SAVE_SUCC:
                ToastUtils.toastCenter(getActivity(), R.string.save_image_succ);
                break;
            case MSG_PIC_SAVE_FAIL:
                ToastUtils.toastCenter(getActivity(), R.string.save_image_fail);
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        rootView = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onPageSelected(int position) {
        mDescTitle.setText(list.get(position).getTitle());
        mDesc.setText(list.get(position).getBrief());
        mDescNumber.setText((position + 1) + "/" + list.size());
        if (mSaveCurrPos.getVisibility() == View.VISIBLE) {
            mSaveCurrPos.setText((position + 1) + "/" + list.size());
        }
        int rate = (position + 1) * 100 / list.size();
        if (rate > fini_rate) {
            fini_rate = rate;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.gallery_desc_layout:
                onPhotoTap(null);
                break;
            case R.id.gallery_save_btn:
                verifyStoragePermissions(getActivity());
                break;
        }
    }

    @Override
    public void onPhotoTap(ImageItem item) {
        if (isDismiss) {
            //保存相关
            mSavePicParent.setVisibility(View.GONE);

            //下方描述输入相关
            mDescParent.setVisibility(View.VISIBLE);
            if (onPhotoTapListener != null) {
                onPhotoTapListener.onShowView();
            }
        } else {
            mSavePicParent.setVisibility(View.VISIBLE);
            if (viewPager != null && list != null) {
                mSaveCurrPos.setText((viewPager.getCurrentItem() + 1) + "/" + list.size());
            }

            //下方描述输入相关
            mDescParent.setVisibility(View.GONE);
            if (onPhotoTapListener != null) {
                onPhotoTapListener.onDismissView();
            }
        }
        isDismiss = !isDismiss;
    }

    private void saveImage() {
        if (list == null) {
            ToastUtils.toastCenter(getActivity(), R.string.save_image_fail);
            return;
        }
        final String url = list.get(viewPager.getCurrentItem()).getImage();
        ThreadPoolUtils.execute(new Runnable() {
            @Override
            public void run() {
                ImageLoaderUtil.getInstance().saveImage(getActivity(), url,
                        Environment.getExternalStorageDirectory().getAbsolutePath() + "/gallery",
                        "pic" + System.currentTimeMillis(), new ImageSaveListener() {
                            @Override
                            public void onSaveSuccess() {
                                handler.obtainMessage(MSG_PIC_SAVE_SUCC).sendToTarget();
                            }

                            @Override
                            public void onSaveFail() {
                                handler.obtainMessage(MSG_PIC_SAVE_FAIL).sendToTarget();
                            }
                        });
            }
        });
    }

    public boolean isLastItem() {
        if (list == null) {
            return true;
        }
        return viewPager.getCurrentItem() == list.size() - 1;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {

    }

    @Override
    public void onShow(DialogInterface dialog) {

    }

    public void setOnPhotoTapListener(OnPhotoTapListener listener) {
        this.onPhotoTapListener = listener;
    }

    public interface OnPhotoTapListener {
        void onShowView();

        void onDismissView();
    }

    /**
     * Checks if the app has permission to write to device storage
     * <p>
     * If the app does not has permission then the user will be prompted to
     * grant permissions
     *
     * @param activity
     */
    public void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            GalleryFragment.this.requestPermissions(PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE);
        } else {
            saveImage();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == REQUEST_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                saveImage();
            } else {
                // Permission Denied
                ToastUtils.toastCenter(getActivity(), R.string.permission_denied);
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
