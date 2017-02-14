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
            Manifest.permission.WRITE_EXTERNAL_STORAGE };

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

    private void updateCollectState() {
        //需要补齐数据，因为有可能从web中来
        gallery.setNimages(list.size());
        if (gallery.getImages() == null) {
            gallery.setImages(list.subList(0, 3));
        }
    }


    private void requestData() {
   /*     mStartLoadTime = System.currentTimeMillis();
        Map<String, String> map = new HashMap<>();
        map.put(Net.Field.id, String.valueOf(id));
        AsyncHttpRequest.doASynGetRequest(getActivity(), UrlContainer.GALLERY_LIST, map, true, new AsyncHttpRequest.CallBack() {
            @Override
            public void call(String data) {
                onGetDataSucceeded(data);
            }

            @Override
            public void fail(String ret) {
                handler.obtainMessage(MSG_CALL_DATA_FAIL).sendToTarget();
            }
        });*/
        String data = "{\n" +
                "  \"data\": {\n" +
                "    \"image\": [\n" +
                "      {\n" +
                "        \"brief\": \"\\u8fd8\\u8bb0\\u5f97\\u5357\\u975e\\u4e16\\u754c\\u676f\\u4e0a\\u7684\\u90a3\\u4f4d\\u201c\\u80f8\\u5939\\u624b\\u673a\\u201d\\u7684\\u5df4\\u62c9\\u572d\\u4e73\\u795e\\u91cc\\u514b\\u5c14\\u6885\\u4e48\\uff1f\\u81ea\\u4ece\\u5357\\u975e\\u4e16\\u754c\\u676f\\u201c\\u4e00\\u6218\\u6210\\u540d\\u201d\\u4e4b\\u540e\\uff0c\\u91cc\\u514b\\u5c14\\u6885\\u66fe\\u7ecf\\u4e00\\u5ea6\\u662f\\u516b\\u5366\\u5a92\\u4f53\\u4e0a\\u7099\\u624b\\u53ef\\u70ed\\u7684\\u5927\\u7ea2\\u4eba\\u3002\", \n" +
                "        \"image\": \"http://image.sports.baofeng.com/9cb7b5569f088f110c4e66f515a8aac7\", \n" +
                "        \"key\": \"\", \n" +
                "        \"title\": \"\\u4e73\\u795e\\u590d\\u51fa!\\\"\\u80f8\\u5939\\u624b\\u673a\\\"\\u6216\\u518d\\u73b0\\u6c5f\\u6e56 \\u4e3a\\u7537\\u53cb\\u53ef\\u4e00\\u8131\\u5230\\u5e95\", \n" +
                "        \"type\": \"image\"\n" +
                "      }, \n" +
                "      {\n" +
                "        \"brief\": \"\\u5728\\u5a92\\u4f53\\u4e0a\\u9500\\u58f0\\u533f\\u8ff9\\u4e86\\u4e00\\u6bb5\\u65f6\\u95f4\\u4e4b\\u540e\\uff0c\\u8fd9\\u4f4d\\u4e73\\u795e\\u7ea7\\u7684\\u7403\\u5458\\u51c6\\u5907\\u5377\\u571f\\u91cd\\u6765\\uff0c\\u5e76\\u4e14\\u8a93\\u8a00\\u53ea\\u8981\\u81ea\\u5df1\\u7537\\u53cb\\u7684\\u7403\\u961f\\u80fd\\u591f\\u6700\\u7ea2\\u8d62\\u5f97\\u8054\\u8d5b\\u51a0\\u519b\\u7684\\u8bdd\\uff0c\\u90a3\\u4e48\\u5979\\u5c31\\u518d\\u6b21\\u5168\\u88f8\\u51fa\\u955c\\u3002\", \n" +
                "        \"image\": \"http://image.sports.baofeng.com/e94e5706be21d751e619eb36d8dadda0\", \n" +
                "        \"key\": \"\", \n" +
                "        \"title\": \"\\u4e73\\u795e\\u590d\\u51fa!\\\"\\u80f8\\u5939\\u624b\\u673a\\\"\\u6216\\u518d\\u73b0\\u6c5f\\u6e56 \\u4e3a\\u7537\\u53cb\\u53ef\\u4e00\\u8131\\u5230\\u5e95\", \n" +
                "        \"type\": \"image\"\n" +
                "      }, \n" +
                "      {\n" +
                "        \"brief\": \"\\u5176\\u5b9e\\uff0c\\u5728\\u5357\\u975e\\u4e16\\u754c\\u676f\\u4e4b\\u540e\\u7684\\u5f88\\u957f\\u4e00\\u6bb5\\u65f6\\u95f4\\u91cc\\uff0c\\u91cc\\u514b\\u5c14\\u6885\\u5c31\\u4e00\\u76f4\\u6d3b\\u8dc3\\u5728\\u5a92\\u4f53\\u7684\\u89c6\\u91ce\\u91cc\\u3002\\u5728\\u5f53\\u5e74\\u7684\\u6027\\u611f\\u6742\\u5fd7\\u4e2d\\uff0c\\u6211\\u4eec\\u7ecf\\u5e38\\u80fd\\u591f\\u770b\\u5230\\u5f88\\u591a\\u5173\\u4e8e\\u91cc\\u514b\\u5c14\\u6885\\u5168\\u88f8\\u6216\\u8005\\u534a\\u88f8\\u7684\\u7167\\u7247\\u3002\", \n" +
                "        \"image\": \"http://image.sports.baofeng.com/18b812433edb380d6c9882fd07134579\", \n" +
                "        \"key\": \"\", \n" +
                "        \"title\": \"\\u4e73\\u795e\\u590d\\u51fa!\\\"\\u80f8\\u5939\\u624b\\u673a\\\"\\u6216\\u518d\\u73b0\\u6c5f\\u6e56 \\u4e3a\\u7537\\u53cb\\u53ef\\u4e00\\u8131\\u5230\\u5e95\", \n" +
                "        \"type\": \"image\"\n" +
                "      }, \n" +
                "      {\n" +
                "        \"brief\": \"\\u7136\\u800c\\uff0c\\u968f\\u7740\\u4e00\\u4f4d\\u540d\\u4e3a\\u4e54\\u7eb3\\u68ee-\\u6cd5\\u5e03\\u7f57\\uff08Jonathan Fabbro\\uff09\\u6210\\u4e3a\\u4e86\\u5979\\u7684\\u7537\\u670b\\u53cb\\uff0c\\u5979\\u5c31\\u51b3\\u5b9a\\u201c\\u91d1\\u76c6\\u6d17\\u624b\\u201d\\uff0c\\u6e10\\u6e10\\u7684\\u8fd9\\u4f4d\\u66fe\\u7ecf\\u7ea2\\u6781\\u4e00\\u65f6\\u7684\\u6027\\u611f\\u5973\\u90ce\\u6de1\\u51fa\\u4e86\\u5a92\\u4f53\\u7684\\u89c6\\u7ebf\\u3002\", \n" +
                "        \"image\": \"http://image.sports.baofeng.com/09f30bc24100b9b7074076e3a8e75813\", \n" +
                "        \"key\": \"\", \n" +
                "        \"title\": \"\\u4e73\\u795e\\u590d\\u51fa!\\\"\\u80f8\\u5939\\u624b\\u673a\\\"\\u6216\\u518d\\u73b0\\u6c5f\\u6e56 \\u4e3a\\u7537\\u53cb\\u53ef\\u4e00\\u8131\\u5230\\u5e95\", \n" +
                "        \"type\": \"image\"\n" +
                "      }, \n" +
                "      {\n" +
                "        \"brief\": \"\\u636e\\u4e86\\u89e3\\uff0c\\u4e54\\u7eb3\\u68ee\\u76ee\\u524d\\u5728\\u58a8\\u897f\\u54e5\\u8e22\\u7403\\uff0c\\u800c\\u4ed6\\u6240\\u6548\\u529b\\u7684\\u7f8e\\u6d32\\u864e\\u4ff1\\u4e50\\u90e8\\u672c\\u8d5b\\u5b63\\u7684\\u76ee\\u6807\\u5c31\\u662f\\u593a\\u51a0\\u3002\\u201c\\u4ed6\\uff08\\u4e54\\u7eb3\\u68ee\\uff09\\u662f\\u4e00\\u4f4d\\u975e\\u5e38\\u597d\\u7684\\u5c04\\u624b\\uff0c\\u5728\\u8d5b\\u573a\\u4e0a\\u4ed6\\u7ecf\\u5e38\\u80fd\\u591f\\u6253\\u5165\\u4e00\\u4e9b\\u6f02\\u4eae\\u7684\\u8fdb\\u7403\\u3002\\u201d\\u5728\\u8c08\\u5230\\u81ea\\u5df1\\u7684\\u7537\\u53cb\\u65f6\\uff0c\\u91cc\\u514b\\u5c14\\u6885\\u5982\\u6b64\\u8bb2\\u9053\\u3002\", \n" +
                "        \"image\": \"http://image.sports.baofeng.com/4d740989a20155ca7d079387aa87cfe7\", \n" +
                "        \"key\": \"\", \n" +
                "        \"title\": \"\\u4e73\\u795e\\u590d\\u51fa!\\\"\\u80f8\\u5939\\u624b\\u673a\\\"\\u6216\\u518d\\u73b0\\u6c5f\\u6e56 \\u4e3a\\u7537\\u53cb\\u53ef\\u4e00\\u8131\\u5230\\u5e95\", \n" +
                "        \"type\": \"image\"\n" +
                "      }, \n" +
                "      {\n" +
                "        \"brief\": \"\\u201c\\u6bcf\\u6bcf\\u5f53\\u4ed6\\u6253\\u8fdb\\u6f02\\u4eae\\u7684\\u8fdb\\u7403\\u65f6\\uff0c\\u6211\\u90fd\\u4f1a\\u5bf9\\u4ed6\\u8bf4\\uff1a\\u2018\\u4eb2\\u7231\\u7684\\uff0c\\u4f60\\u597d\\u68d2\\uff0c\\u4f60\\u662f\\u4e0d\\u662f\\u628a\\u7403\\u95e8\\u5f53\\u6210\\u662f\\u6211\\uff0c\\u7136\\u540e\\u731b\\u7684\\u628a\\u7403\\u5c04\\u8fdb\\u53bb\\u3002\\u2019\\u201d\\u5f53\\u5728\\u5a92\\u4f53\\u4e0a\\u8bf4\\u8d77\\u8fd9\\u4e9b\\u975e\\u5e38\\u79c1\\u5bc6\\u7684\\u8bdd\\u9898\\u65f6\\uff0c\\u91cc\\u514b\\u5c14\\u6885\\u4e5f\\u5fcd\\u4e0d\\u4f4f\\u7684\\u7b11\\u4e86\\u3002\", \n" +
                "        \"image\": \"http://image.sports.baofeng.com/14e4a9a3b43356380bfbe4a91d82b15f\", \n" +
                "        \"key\": \"\", \n" +
                "        \"title\": \"\\u4e73\\u795e\\u590d\\u51fa!\\\"\\u80f8\\u5939\\u624b\\u673a\\\"\\u6216\\u518d\\u73b0\\u6c5f\\u6e56 \\u4e3a\\u7537\\u53cb\\u53ef\\u4e00\\u8131\\u5230\\u5e95\", \n" +
                "        \"type\": \"image\"\n" +
                "      }, \n" +
                "      {\n" +
                "        \"brief\": \"\\u6b64\\u5916\\uff0c\\u5f53\\u5a92\\u4f53\\u95ee\\u5230\\u5982\\u679c\\u7f8e\\u6d32\\u864e\\u6700\\u7ec8\\u593a\\u51a0\\u7684\\u8bdd\\uff0c\\u90a3\\u4e48\\u5979\\u662f\\u5426\\u4f1a\\u9009\\u62e9\\u5728\\u6b64\\u8131\\u6389\\u81ea\\u5df1\\u7684\\u8863\\u670d\\u65f6\\uff0c\\u91cc\\u514b\\u5c14\\u6885\\u7684\\u56de\\u7b54\\u662f\\u80af\\u5b9a\\u7684\\u3002\\u201c\\u8fd9\\u5f88\\u6709\\u5fc5\\u8981\\uff0c\\u8fd9\\u4e2a\\u8d5b\\u5b63\\u4ed6\\u4eec\\u6709\\u673a\\u4f1a\\u593a\\u51a0\\uff0c\\u6211\\u4e5f\\u5e0c\\u671b\\u80fd\\u591f\\u4e3a\\u6b64\\u505a\\u4e00\\u4e9b\\u529b\\u6240\\u80fd\\u53ca\\u7684\\u4e8b\\u60c5\\uff0c\\u7403\\u5458\\u4eec\\u9700\\u8981\\u52a8\\u529b\\u3002\\u201d\", \n" +
                "        \"image\": \"http://image.sports.baofeng.com/c3acb7233f272f498ce853cb8415157f\", \n" +
                "        \"key\": \"\", \n" +
                "        \"title\": \"\\u4e73\\u795e\\u590d\\u51fa!\\\"\\u80f8\\u5939\\u624b\\u673a\\\"\\u6216\\u518d\\u73b0\\u6c5f\\u6e56 \\u4e3a\\u7537\\u53cb\\u53ef\\u4e00\\u8131\\u5230\\u5e95\", \n" +
                "        \"type\": \"image\"\n" +
                "      }, \n" +
                "      {\n" +
                "        \"brief\": \"\\u4ece\\u91cc\\u514b\\u5c14\\u6885\\u7684\\u53ea\\u8a00\\u7247\\u8bed\\u4e2d\\uff0c\\u6211\\u4eec\\u4e0d\\u96be\\u770b\\u51fa\\u5979\\u5634\\u4e2d\\u6240\\u8bf4\\u7684\\u52a8\\u529b\\u5c31\\u662f\\u8131\\u6389\\u8863\\u670d\\uff0c\\u4ee5\\u81ea\\u5df1\\u6700\\u6027\\u611f\\u7684\\u8eab\\u6750\\u8ba9\\u6240\\u6709\\u7403\\u5458\\u8840\\u8109\\u81a8\\u80c0\\u3002\", \n" +
                "        \"image\": \"http://image.sports.baofeng.com/93a12a8a2522aecd442eba5bd3925cf6\", \n" +
                "        \"key\": \"\", \n" +
                "        \"title\": \"\\u4e73\\u795e\\u590d\\u51fa!\\\"\\u80f8\\u5939\\u624b\\u673a\\\"\\u6216\\u518d\\u73b0\\u6c5f\\u6e56 \\u4e3a\\u7537\\u53cb\\u53ef\\u4e00\\u8131\\u5230\\u5e95\", \n" +
                "        \"type\": \"image\"\n" +
                "      }, \n" +
                "      {\n" +
                "        \"brief\": \"\\u201c\\u6211\\u505a\\u6240\\u4ee5\\u80fd\\u591f\\u8fd9\\u6837\\u8bf4\\uff0c\\u662f\\u56e0\\u4e3a\\u7403\\u5458\\u4eec\\u6709\\u65f6\\u5019\\u9700\\u8981\\u9f13\\u52b1\\uff0c\\u5c3d\\u7ba1\\u73b0\\u9636\\u6bb5\\u8054\\u8d5b\\u521a\\u521a\\u5f00\\u59cb\\uff0c\\u4f46\\u662f\\u5728\\u5173\\u952e\\u65f6\\u523b\\uff0c\\u8fd9\\u6837\\u7684\\u9f13\\u52b1\\u5bf9\\u4e8e\\u7403\\u5458\\u4f1a\\u8d77\\u5230\\u5f88\\u5173\\u952e\\u7684\\u4f5c\\u7528\\u3002\\u201d\\u91cc\\u514b\\u5c14\\u6885\\u7ee7\\u7eed\\u8bb2\\u9053\\u3002\", \n" +
                "        \"image\": \"http://image.sports.baofeng.com/54a3f8baa8af49d4a08771602db862fd\", \n" +
                "        \"key\": \"\", \n" +
                "        \"title\": \"\\u4e73\\u795e\\u590d\\u51fa!\\\"\\u80f8\\u5939\\u624b\\u673a\\\"\\u6216\\u518d\\u73b0\\u6c5f\\u6e56 \\u4e3a\\u7537\\u53cb\\u53ef\\u4e00\\u8131\\u5230\\u5e95\", \n" +
                "        \"type\": \"image\"\n" +
                "      }, \n" +
                "      {\n" +
                "        \"brief\": \"\\u201c\\u4ed6\\uff08\\u4e54\\u68ee\\u7eb3\\uff09\\u7684\\u961f\\u53cb\\u90fd\\u8ba4\\u8bc6\\u6211\\uff0c\\u4ed6\\u4eec\\u66fe\\u7ecf\\u4e5f\\u5728\\u6742\\u5fd7\\u4e0a\\u770b\\u5230\\u8fc7\\u6211\\u7684\\u7167\\u7247\\uff0c\\u6211\\u4e5f\\u5e0c\\u671b\\u80fd\\u591f\\u5e2e\\u4e54\\u68ee\\u7eb3\\u505a\\u4e00\\u4e9b\\u4e8b\\u60c5\\u3002\\u201d\", \n" +
                "        \"image\": \"http://image.sports.baofeng.com/5449e5b403d1e2298ddd120ac8cab623\", \n" +
                "        \"key\": \"\", \n" +
                "        \"title\": \"\\u4e73\\u795e\\u590d\\u51fa!\\\"\\u80f8\\u5939\\u624b\\u673a\\\"\\u6216\\u518d\\u73b0\\u6c5f\\u6e56 \\u4e3a\\u7537\\u53cb\\u53ef\\u4e00\\u8131\\u5230\\u5e95\", \n" +
                "        \"type\": \"image\"\n" +
                "      }, \n" +
                "      {\n" +
                "        \"brief\": \"\\u7684\\u786e\\uff0c\\u4e73\\u795e\\u201c\\u91cd\\u51fa\\u6c5f\\u6e56\\u201d\\u8fd9\\u5bf9\\u4e8e\\u65e0\\u6570\\u7403\\u8ff7\\u6765\\u8bf4\\u7684\\u786e\\u662f\\u4e00\\u4e2a\\u632f\\u594b\\u4eba\\u5fc3\\u7684\\u6d88\\u606f\\u3002\\u6bd5\\u7adf\\u57282010\\u5e74\\u7684\\u65f6\\u5019\\uff0c\\u91cc\\u514b\\u5c14\\u6885\\u90a3\\u6027\\u611f\\u7684\\u4e3e\\u52a8\\u5438\\u5f15\\u4e86\\u65e0\\u6570\\u7403\\u8ff7\\u7684\\u773c\\u7403\\uff0c\\u4e5f\\u4e0d\\u77e5\\u9053\\u5728\\u590d\\u51fa\\u4e4b\\u540e\\uff0c\\u8fd9\\u4f4d\\u66fe\\u7ecf\\u4e73\\u795e\\u4f1a\\u4e3a\\u7403\\u8ff7\\u4eec\\u5e26\\u6765\\u600e\\u6837\\u7684\\u60ca\\u559c\\u3002\", \n" +
                "        \"image\": \"http://image.sports.baofeng.com/73bada6962075ebf746867d4d5164109\", \n" +
                "        \"key\": \"\", \n" +
                "        \"title\": \"\\u4e73\\u795e\\u590d\\u51fa!\\\"\\u80f8\\u5939\\u624b\\u673a\\\"\\u6216\\u518d\\u73b0\\u6c5f\\u6e56 \\u4e3a\\u7537\\u53cb\\u53ef\\u4e00\\u8131\\u5230\\u5e95\", \n" +
                "        \"type\": \"image\"\n" +
                "      }, \n" +
                "      {\n" +
                "        \"brief\": \"\\u5df4\\u62c9\\u572d\\u4e73\\u795e\\u91cc\\u514b\\u5c14\\u6885\\u6027\\u611f\\u79c1\\u7167\", \n" +
                "        \"image\": \"http://image.sports.baofeng.com/5449e5b403d1e2298ddd120ac8cab623\", \n" +
                "        \"key\": \"\", \n" +
                "        \"title\": \"\\u4e73\\u795e\\u590d\\u51fa!\\\"\\u80f8\\u5939\\u624b\\u673a\\\"\\u6216\\u518d\\u73b0\\u6c5f\\u6e56 \\u4e3a\\u7537\\u53cb\\u53ef\\u4e00\\u8131\\u5230\\u5e95\", \n" +
                "        \"type\": \"image\"\n" +
                "      }, \n" +
                "      {\n" +
                "        \"brief\": \"\\u5df4\\u62c9\\u572d\\u4e73\\u795e\\u91cc\\u514b\\u5c14\\u6885\\u6027\\u611f\\u79c1\\u7167\", \n" +
                "        \"image\": \"http://image.sports.baofeng.com/aa69735c7ef8ef80611ab788a2c98c80\", \n" +
                "        \"key\": \"\", \n" +
                "        \"title\": \"\\u4e73\\u795e\\u590d\\u51fa!\\\"\\u80f8\\u5939\\u624b\\u673a\\\"\\u6216\\u518d\\u73b0\\u6c5f\\u6e56 \\u4e3a\\u7537\\u53cb\\u53ef\\u4e00\\u8131\\u5230\\u5e95\", \n" +
                "        \"type\": \"image\"\n" +
                "      }, \n" +
                "      {\n" +
                "        \"brief\": \"\\u5df4\\u62c9\\u572d\\u4e73\\u795e\\u91cc\\u514b\\u5c14\\u6885\\u6027\\u611f\\u79c1\\u7167\", \n" +
                "        \"image\": \"http://image.sports.baofeng.com/38bd410257b579e8151dec350137b80f\", \n" +
                "        \"key\": \"\", \n" +
                "        \"title\": \"\\u4e73\\u795e\\u590d\\u51fa!\\\"\\u80f8\\u5939\\u624b\\u673a\\\"\\u6216\\u518d\\u73b0\\u6c5f\\u6e56 \\u4e3a\\u7537\\u53cb\\u53ef\\u4e00\\u8131\\u5230\\u5e95\", \n" +
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
                        Environment.getExternalStorageDirectory().getAbsolutePath() + "/bfsports",
                        "bfsports" + System.currentTimeMillis(), new ImageSaveListener() {
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
     *
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
        }else{
            saveImage();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {

        if (requestCode == REQUEST_EXTERNAL_STORAGE)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                saveImage();
            } else
            {
                // Permission Denied
                ToastUtils.toastCenter(getActivity(), R.string.permission_denied);
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
