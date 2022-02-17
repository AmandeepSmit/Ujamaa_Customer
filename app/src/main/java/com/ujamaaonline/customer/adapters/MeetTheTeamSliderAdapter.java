package com.ujamaaonline.customer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.viewpager.widget.PagerAdapter;

import com.squareup.picasso.Picasso;
import com.ujamaaonline.customer.R;
import com.ujamaaonline.customer.models.business_data.MeetTheTeam;

import java.util.ArrayList;
import java.util.List;

public class MeetTheTeamSliderAdapter extends PagerAdapter {
    Context context;
    List<MeetTheTeam> imagesList=new ArrayList<>();
    LayoutInflater layoutInflater;
    int imgOne,imgTwo,imgThree;
    int count=2;


    public MeetTheTeamSliderAdapter(Context context, List<MeetTheTeam> imagesList) {
        this.context = context;
        this.imagesList = imagesList;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return imagesList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == (object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View itemView = layoutInflater.inflate(R.layout.row_meet_the_team, container, false);
        ImageView imageView1 = itemView.findViewById(R.id.rmt_image1);
        ImageView imageView2 = itemView.findViewById(R.id.rmt_image2);
        ImageView imageView3 = itemView.findViewById(R.id.rmt_image);

        if ((position==count-2))
        {
            if (count-2<imagesList.size())
            Picasso.get().load(imagesList.get(count-2).getMemberProfile()).error(R.drawable.dummy).resize(1080,720).into(imageView1);
            if (count-1<imagesList.size())
            Picasso.get().load(imagesList.get(count-1).getMemberProfile()).error(R.drawable.dummy).resize(1080,720).into(imageView2);
            if (count<imagesList.size())
                Picasso.get().load(imagesList.get(count).getMemberProfile()).error(R.drawable.dummy).resize(1080,720).into(imageView3);
        }
        else if (position==count+1)
            count=count+3;

        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}