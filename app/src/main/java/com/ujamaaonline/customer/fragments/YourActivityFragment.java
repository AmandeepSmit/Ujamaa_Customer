package com.ujamaaonline.customer.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.ujamaaonline.customer.R;
import com.ujamaaonline.customer.activities.GalleryActivity;
import com.ujamaaonline.customer.activities.NearMeActivity;
import com.ujamaaonline.customer.activities.UseFullReviewsActivity;


public class YourActivityFragment extends Fragment implements View.OnClickListener{

  private View view;
  private LinearLayout layoutBookmarked,layoutLikedImg,layoutUseFull;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.fragment_your_activity,container, false);
        initView();
        clickListeners();
        return view;
    }

    private void clickListeners() {
        layoutBookmarked.setOnClickListener(this);
        layoutLikedImg.setOnClickListener(this);
        layoutUseFull.setOnClickListener(this);
    }

    private void initView() {
        layoutBookmarked=view.findViewById(R.id.layout_bookmarked);
        layoutLikedImg=view.findViewById(R.id.layout_liked_img);
        layoutUseFull=view.findViewById(R.id.layout_usefull);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.layout_bookmarked:
                startActivity(new Intent(getActivity(), NearMeActivity.class).putExtra("profile",""));
                break;
            case R.id.layout_liked_img:
                startActivity(new Intent(getActivity(), GalleryActivity.class));
                break;
            case R.id.layout_usefull:
                startActivity(new Intent(getActivity(), UseFullReviewsActivity.class).putExtra("profile",""));
                break;

        }
    }
}