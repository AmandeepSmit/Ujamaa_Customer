package com.ujamaaonline.customer.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ujamaaonline.customer.R;
import com.ujamaaonline.customer.models.chat_models.ChatModel;


import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ProfileImageDialog extends DialogFragment implements View.OnClickListener {

    private View view;
    private ImageView iv_cross;
    private List<String> imageArrayList = new ArrayList<>();
    private int imgPosition = 0;
    private TextView tv_image_count;
    private ViewPager imageViewPage;

    public static ProfileImageDialog newInstance(List<String> imageArrayList) {
        ProfileImageDialog fragment = new ProfileImageDialog();
        Bundle bundle = new Bundle();
        bundle.putSerializable("PROFILE_DATA", (Serializable) imageArrayList);
//        bundle.putInt("imgPos", imgPosition);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle);

        if (getArguments() != null && getArguments().containsKey("PROFILE_DATA")) {
            this.imageArrayList = (List<String>) getArguments().getSerializable("PROFILE_DATA");
            this.imgPosition = getArguments().getInt("imgPos");
        }
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        this.view = inflater.inflate(R.layout.dialog_profile_image_fullscreen, container, false);
        this.initView();
        this.setOnClickListener();
        this.dataToView();
        return this.view;
    }

    private void initView() {
        this.iv_cross = this.view.findViewById(R.id.back_btn);
        this.imageViewPage = this.view.findViewById(R.id.imageViewPage);
        this.tv_image_count = this.view.findViewById(R.id.tv_image_count);
    }

    private void setOnClickListener() {
        this.iv_cross.setOnClickListener(this);
    }

    private void dataToView() {
        if (imageArrayList.size() > 0) {
            tv_image_count.setText((imgPosition + 1) + " of ".concat(String.valueOf(imageArrayList.size())));

            this.imageViewPage.setAdapter(new SlidingImageAdapter(getActivity(), imageArrayList));
            imageViewPage.setCurrentItem(imgPosition);
            imageViewPage.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

                public void onPageScrollStateChanged(int state) {
                }

                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                public void onPageSelected(int position) {
                    imgPosition = position;
                    tv_image_count.setText(String.valueOf(imgPosition + 1).concat(" of ").concat(String.valueOf(imageArrayList.size())));
                }
            });
        } else {
            Toast.makeText(getActivity(), "No images found to show", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            Objects.requireNonNull(dialog.getWindow()).setLayout(width, height);
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.back_btn) {
            this.dismiss();
        }
    }

    private class SlidingImageAdapter extends PagerAdapter {

        private final Context context;
        private final List<String> imageArray;

        private SlidingImageAdapter(Context context, List<String> imageArray) {
            this.context = context;
            this.imageArray = imageArray;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, @NotNull Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return imageArray.size();
        }

        @NonNull
        @Override
        public Object instantiateItem(@NotNull ViewGroup view, int position) {
            View imageLayout = LayoutInflater.from(context).inflate(R.layout.row_profile_image_dialog, view, false);
            assert imageLayout != null;
            final ImageView profileImage = imageLayout.findViewById(R.id.iv_profile_image);
            final ImageView iv_play_video = imageLayout.findViewById(R.id.iv_play_video);

            iv_play_video.setOnClickListener(v -> {
                Intent playIntent = new Intent(context, VideoPlayerExo.class);
                playIntent.putExtra("url", imageArray.get(position));
                startActivity(playIntent);
            });

            loadImages(imageArray.get(position), profileImage, iv_play_video);
            view.addView(imageLayout, 0);
            return imageLayout;
        }

        private void loadImages(String imagePath, ImageView imageView, ImageView btnPlay) {
            String[] filenameArray = imagePath.split("\\.");
            String extension = filenameArray[filenameArray.length - 1];

            if (extension.equalsIgnoreCase("jpg") || extension.equalsIgnoreCase("jpeg") || extension.equalsIgnoreCase("png")) {
                btnPlay.setVisibility(View.GONE);
                if (!imagePath.isEmpty())
                    loadImages(imagePath, imageView);
            } else {
                btnPlay.setVisibility(View.VISIBLE);
                loadImages(imagePath, imageView);
            }
        }

        private void loadImages(String imagePath, ImageView imageView) {
            Glide.with(view.getContext())
                    .load(imagePath)
                    .fitCenter()
                    .error(R.drawable.app_logo)
                    .placeholder(R.drawable.app_logo)
                    .thumbnail(0.10f)
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                    .into(imageView);
        }

        @Override
        public boolean isViewFromObject(View view, @NotNull Object object) {
            return view.equals(object);
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }
    }

}
