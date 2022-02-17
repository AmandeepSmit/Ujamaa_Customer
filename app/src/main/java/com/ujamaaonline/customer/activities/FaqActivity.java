package com.ujamaaonline.customer.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.ujamaaonline.customer.R;
import com.ujamaaonline.customer.application.ApplicationHelper;
import com.ujamaaonline.customer.components.SessionSecuredPreferences;
import com.ujamaaonline.customer.components.TimeAgo2;
import com.ujamaaonline.customer.dialogs.GlobalProgressDialog;
import com.ujamaaonline.customer.fragments.YourReviewsFragment;
import com.ujamaaonline.customer.models.customer_own_reviews.CustomerAllReviewe;
import com.ujamaaonline.customer.models.faq_model.FaqPayload;
import com.ujamaaonline.customer.models.faq_model.FaqResponse;
import com.ujamaaonline.customer.models.filter_cat.FilterCatResponse;
import com.ujamaaonline.customer.network.APIClient;
import com.ujamaaonline.customer.utils.GlobalUtil;
import com.ujamaaonline.customer.utils.UIUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;

import static com.ujamaaonline.customer.utils.Constants.HEADER_TOKEN;
import static com.ujamaaonline.customer.utils.Constants.LOGIN_PREFERENCE;

public class FaqActivity extends AppCompatActivity {
    private List<FaqPayload> faqList = new ArrayList<>();
    private APIClient.APIInterface apiInterface;
    private GlobalProgressDialog progress;
    private SessionSecuredPreferences loginPreferences;
    private String headerToken;
    private RecyclerView recFaq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);
        initView();
    }

    private void initView() {
        this.progress = new GlobalProgressDialog(FaqActivity.this);
        this.apiInterface = APIClient.getClient();
        this.loginPreferences = ApplicationHelper.application().loginPreferences(LOGIN_PREFERENCE);
        this.headerToken = this.loginPreferences.getString(HEADER_TOKEN, "");
        recFaq = findViewById(R.id.rec_faq);
        recFaq.setHasFixedSize(true);
        recFaq.setAdapter(new FaqAdapter());
        getFilterCat();
    }

    private void getFilterCat() {
        if (!GlobalUtil.isNetworkAvailable(FaqActivity.this)) {
            UIUtil.showNetworkDialog(FaqActivity.this);
            return;
        }
        progress.showProgressBar();
        apiInterface.getFaq().enqueue(new Callback<FaqResponse>() {
            @EverythingIsNonNull
            @Override
            public void onResponse(Call<FaqResponse> call, Response<FaqResponse> response) {
                progress.hideProgressBar();
                if (response.isSuccessful()) {
                    if (response.body().getStatus()) {
                        if (response.body().getPayload().size() > 0) {
                            faqList.addAll(response.body().getPayload());
                            recFaq.getAdapter().notifyDataSetChanged();
                        }
                    }
                }
            }

            @EverythingIsNonNull
            @Override
            public void onFailure(Call<FaqResponse> call, Throwable t) {
                progress.hideProgressBar();
            }
        });
    }

    public void backBtnCick(View view) {
        this.onBackPressed();
    }


    //todo ======================= Faq Adapter ===========================

    class FaqAdapter extends RecyclerView.Adapter<FaqAdapter.FaqViewHolder> {
        private TimeAgo2 timeAgo2 = new TimeAgo2();

        @NonNull
        @Override
        public FaqAdapter.FaqViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new FaqAdapter.FaqViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_faq, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull FaqAdapter.FaqViewHolder holder, int position) {
            FaqPayload faqPayload = faqList.get(position);
            holder.tvQuestion.setText(Html.fromHtml(faqPayload.getQuestion()));
            holder.tvAnswers.setText(Html.fromHtml(faqPayload.getAnswer()));
        }

        @Override
        public int getItemCount() {
            return faqList.size();
        }

        class FaqViewHolder extends RecyclerView.ViewHolder {
            TextView tvQuestion, tvAnswers;

            public FaqViewHolder(@NonNull View itemView) {
                super(itemView);
                tvQuestion = itemView.findViewById(R.id.tv_question);
                tvAnswers = itemView.findViewById(R.id.tv_answer);
            }
        }
    }


}