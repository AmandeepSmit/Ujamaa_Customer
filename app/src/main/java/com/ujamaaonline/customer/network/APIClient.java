package com.ujamaaonline.customer.network;

import android.annotation.SuppressLint;

import com.ujamaaonline.customer.models.BannerResponse;
import com.ujamaaonline.customer.models.CategoryResponse;
import com.ujamaaonline.customer.models.LogoutRequest;
import com.ujamaaonline.customer.models.LogoutResponse;
import com.ujamaaonline.customer.models.NotificationListResponse;
import com.ujamaaonline.customer.models.VisitProfileRequest;
import com.ujamaaonline.customer.models.VisitProfileResponse;
import com.ujamaaonline.customer.models.businessList.BusinessListResponse;
import com.ujamaaonline.customer.models.business_data.GetBusinessData;
import com.ujamaaonline.customer.models.business_data.GetBusinessRequest;
import com.ujamaaonline.customer.models.cat_filter.AllOtherFeaturesResponse;
import com.ujamaaonline.customer.models.cat_filter.FilterBusinessRequest;
import com.ujamaaonline.customer.models.chat_models.RootModel;
import com.ujamaaonline.customer.models.chat_models.sendImageResponse;
import com.ujamaaonline.customer.models.customer_own_reviews.CustomerOwnReviews;
import com.ujamaaonline.customer.models.event_filter.EventDetailResponse;
import com.ujamaaonline.customer.models.event_filter.EventFilterCatResponse;
import com.ujamaaonline.customer.models.event_filter.FilterEventRequest;
import com.ujamaaonline.customer.models.faq_model.FaqResponse;
import com.ujamaaonline.customer.models.faq_model.GetAboutUsResponse;
import com.ujamaaonline.customer.models.feed_models.FeedLocationFilterRequest;
import com.ujamaaonline.customer.models.feed_models.GetEventsDateResponse;
import com.ujamaaonline.customer.models.feed_models.GetPostRequest;
import com.ujamaaonline.customer.models.feed_models.LinkClickRequest;
import com.ujamaaonline.customer.models.feed_models.PostResponse;
import com.ujamaaonline.customer.models.filter_cat.FilterCatResponse;
import com.ujamaaonline.customer.models.filter_heading.HeadingFilterResponse;
import com.ujamaaonline.customer.models.gallery_images.GalleryImagesResponse;
import com.ujamaaonline.customer.models.gallery_images.SingleImgDetailResponse;
import com.ujamaaonline.customer.models.get_business_reviews.AllReviewResponse;
import com.ujamaaonline.customer.models.lat_lng_response.GetLatLngResponse;
import com.ujamaaonline.customer.models.login.EOLoginRequest;
import com.ujamaaonline.customer.models.login.EOLoginResponse;
import com.ujamaaonline.customer.models.notification.GetNotificationResponse;
import com.ujamaaonline.customer.models.recent_search.RecentSearchResponse;
import com.ujamaaonline.customer.models.search_category.CatHeadingResponse;
import com.ujamaaonline.customer.models.search_category.GetNotificationRequest;
import com.ujamaaonline.customer.models.search_gallery.SearchGalleryResponse;
import com.ujamaaonline.customer.models.share_earn_models.GetCreditResponse;
import com.ujamaaonline.customer.models.share_earn_models.ReferralCodeResponse;
import com.ujamaaonline.customer.models.signup.EORegisterResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ujamaaonline.customer.models.terms_of_use.TermsOfUseResponse;
import com.ujamaaonline.customer.models.user_profile.UpdateProfileRequest;
import com.ujamaaonline.customer.models.user_profile.UserProfileResponse;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

import static com.ujamaaonline.customer.utils.Constants.BASE_URL;

public class APIClient {
    public static APIInterface getClient() {
        // Create a trust manager that does not validate certificate chains
        @SuppressLint("TrustAllX509TrustManager") final TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                    }
                    @Override
                    public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                    }
                    @Override
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return new java.security.cert.X509Certificate[]{};
                    }
                }
        };
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.MINUTES)
                .readTimeout(10, TimeUnit.MINUTES)
                .writeTimeout(10, TimeUnit.MINUTES)
                .sslSocketFactory(Objects.requireNonNull(getSSLSocketFactory()), (X509TrustManager) trustAllCerts[0])
                .addInterceptor(interceptor).build();
        Gson gson = new GsonBuilder().setLenient().create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL).client(client)
                //.addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        return retrofit.create(APIInterface.class);
    }

    private static SSLSocketFactory getSSLSocketFactory() {
        try {
            // Create a trust manager that does not validate certificate chains
            @SuppressLint("TrustAllX509TrustManager") final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                        }
                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                        }
                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };
            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            return sslContext.getSocketFactory();
        } catch (KeyManagementException | NoSuchAlgorithmException e) {
            return null;
        }
    }

    public interface APIInterface {

        @Headers({"Authorization: key=" + "AAAAugwFsDw:APA91bHGfl2GGiIN5ezDor8ovdp9F_jo_iXgqITtX_-GoMvK5PqjrQPo6ErjK3YfEFoxzCn8H_Bd4L_nCRsgv1TeWg_9lUsbdVxHfvJ7rXqO2pACXFeiw8il2YwkfnWWbU3zkd1jg-ee", "Content-Type:application/json"})
        @POST("https://fcm.googleapis.com/fcm/send")
        Call<ResponseBody> sendNotification(@Body RootModel root);

        @POST("login-customer ")
        @Headers("Content-type: application/json")
        Call<EOLoginResponse> loginUser(@Body EOLoginRequest loginRequest);

        @POST("business_review/{type}/{filter}")
        @Headers("Content-type: application/json")
        Call<AllReviewResponse> getAllReviews(
                @Path("type") String path, @Path("filter") String filter,
                @Body EOLoginRequest loginRequest);

        @POST("customer-usefull-reviews")
        @Headers("Content-type: application/json")
        Call<AllReviewResponse> getUseFullReviews(
                @Header("Authorization") String headerToken,
                @Body EOLoginRequest loginRequest);

        @POST("customer-my-reviewes")
        Call<CustomerOwnReviews> getCustomerReviews(
                @Header("Authorization") String headerToken,
                @Body EOLoginRequest loginRequest);

        @GET("categories-list")
        Call<FilterCatResponse> getFilterCat();

        @FormUrlEncoded
        @POST("event_category")
        Call<EventFilterCatResponse> getEventFilterCat(@Field("type") String type);

        @POST("forgot_password_customer")    //todo  forgot_password change into forgot_password_customer
        Call<com.ujamaaonline.business.models.ForgotPasswordResponse> forgotPassword(@Body com.ujamaaonline.business.models.ForgotPasswordRequest request);

        @POST("match_otp")
        Call<com.ujamaaonline.business.models.ForgotPasswordResponse> matchOtp(@Body com.ujamaaonline.business.models.VerifyOtpRequest request);

        @POST("change_password")
        Call<com.ujamaaonline.business.models.ChangePasswordResponse> resetPassword(@Body com.ujamaaonline.business.models.ChangePasswordRequest request);

        @GET("customer-faqs")
        Call<FaqResponse> getFaq();

        @GET("about-us")
        Call<GetAboutUsResponse> getAboutUs();

        @POST("report_review")
        @Headers("Content-type: application/json")
        Call<AllReviewResponse> reportReviews(
                @Header("Authorization") String headerToken,
                @Body EOLoginRequest loginRequest);


        @POST("customer-delete-review")
        @Headers("Content-type: application/json")
        Call<AllReviewResponse> deleteReview(
                @Header("Authorization") String headerToken,
                @Body EOLoginRequest loginRequest);

        @POST("usefull")
        @Headers("Content-type: application/json")
        Call<AllReviewResponse> usefull(
                @Header("Authorization") String headerToken,
                @Body EOLoginRequest loginRequest);


        @POST("logout")
        Call<LogoutResponse> logoutUser(@Header("Authorization") String headerToken, @Body LogoutRequest request);

        @GET
        Call<GetLatLngResponse> getLatLng(@Url String url);

        @GET("external-links")
        Call<TermsOfUseResponse> getTermsOfUse();


        @POST("customer-distance-unit")
        Call<UserProfileResponse> postDistanceUnit(@Header("Authorization") String headerToken,@Body EOLoginRequest loginRequest);

        @POST("{post_type}")
        Call<GetEventsDateResponse> likeEventPost(@Path("post_type") String postType,@Header("Authorization") String headerToken, @Body EOLoginRequest loginRequest);



        @POST("code_reveal")
        Call<GetEventsDateResponse> seeCode(@Header("Authorization") String headerToken, @Body EOLoginRequest loginRequest);



        @POST("event_detail")
        Call<EventDetailResponse> getEventDetail(@Header("Authorization") String headerToken, @Body EOLoginRequest loginRequest);


        @POST("event_date_list")
        Call<GetEventsDateResponse> getEventsDate(@Header("Authorization") String headerToken, @Body EOLoginRequest loginRequest);



        @POST("my-subscribe-businesses")
        Call<GetEventsDateResponse> getMySubscribeBusiness(@Header("Authorization") String headerToken, @Body EOLoginRequest loginRequest);

        @GET("customer-profile")
        Call<UserProfileResponse> getUserProfile(@Header("Authorization") String headerToken);

        @Multipart
        @POST("update-customer-profile")
        Call<BusinessListResponse> updateCustomerProfile(@Header("Authorization") String headerToken,@Part("firstname") RequestBody fName,
                                                         @Part("lastname") RequestBody lName,
                                                         @Part("mobile") RequestBody mobile,
                                                         @Part("postcode") RequestBody postCode,
                                                         @Part("gender") RequestBody gander,
                                                         @Part MultipartBody.Part imgFile);

        @GET("notification-setting")
        Call<GetNotificationResponse> getNotification(@Header("Authorization") String headerToken);

        @POST("customer-change-password")
        Call<BusinessListResponse> changePassword(@Header("Authorization") String headerToken,@Body EOLoginRequest loginRequest);

        @POST("notification-setting")
        Call<BusinessListResponse> updateNotificationSetting(@Header("Authorization") String headerToken,@Body GetNotificationRequest loginRequest);

        @POST("my-bookmarked-businesses")
        Call<BusinessListResponse> getBookmarkedBusinesses(@Header("Authorization") String headerToken,@Body EOLoginRequest loginRequest);


        @POST("my-subscribe-businesses")
        Call<BusinessListResponse> getBookmarkedBusinesses(@Header("Authorization") String headerToken);


        @POST("event_calender")
        Call<PostResponse> getEventByDate(@Header("Authorization") String headerToken,@Body EOLoginRequest loginRequest);

        @POST("my-liked-images")
        Call<SearchGalleryResponse> getLikedImages(@Header("Authorization") String headerToken,@Body EOLoginRequest loginRequest);

        @POST("subscribe_business")
        @Headers("Content-type: application/json")
        Call<AllReviewResponse> subscribeBusiness(
                @Header("Authorization") String headerToken,
                @Body GetBusinessRequest request);

        @POST("bookmark_business")
        @Headers("Content-type: application/json")
        Call<AllReviewResponse> bookmarkBusiness(
                @Header("Authorization") String headerToken,
                @Body GetBusinessRequest request);

        @POST("bookmark_business")
        @Headers("Content-type: application/json")
        Call<AllReviewResponse> checkBookmarkBusiness(
                @Header("Authorization") String headerToken,
                @Body GetBusinessRequest request);

        @POST("sub_heading_category")
        @Headers("Content-type: application/json")
        Call<CatHeadingResponse> getCatHeading(
                @Header("Authorization") String headerToken,
                @Body GetBusinessRequest request);

        @GET("recent-search-list")
        Call<RecentSearchResponse> getRecentSearch(
                @Header("Authorization") String headerToken);

        @Multipart
        @POST("register")
        Call<EORegisterResponse> registerCustomer(@Part("firstname") RequestBody fName,
                                                  @Part("lastname") RequestBody lName,
                                                  @Part("email") RequestBody email,
                                                  @Part("password") RequestBody password,
                                                  @Part("postcode") RequestBody postcode,
                                                  @Part("dob") RequestBody dob,
                                                  @Part MultipartBody.Part profileImage,
                                                  @Part("mobile") RequestBody mobile,
                                                  @Part("gender") RequestBody gender,
                                                  @Part("device_id")RequestBody deviceId,
                                                  @Part("firebase_token") RequestBody firebaseToken,
                                                  @Part("device_type")RequestBody deviceType,
                                                  @Part("lat") RequestBody custLat,
                                                  @Part("long")RequestBody custLong,
                                                  @Part("referral_code")RequestBody referralCode);


        @Multipart
        @POST("add_business_review")
        Call<EORegisterResponse> customerRating(
                @Header("Authorization") String headerToken,
                @Part("business_id") RequestBody fName,
                @Part("rateing") RequestBody lName,
                @Part("comment") RequestBody email,
                @Part List<MultipartBody.Part> ratingImg);

        @GET("all_features")
        Call<AllOtherFeaturesResponse> getOtherfeaturesFilter(
                @Header("Authorization") String headerToken
        );

        @POST("get-quick-filter")
        Call<HeadingFilterResponse> getHeadings(
                @Body GetBusinessRequest request
        );

        @GET("banners")
        Call<BannerResponse> getBanner();

        @GET("categories")
        Call<CategoryResponse> getCategories();

        @POST("get_business_data")
        Call<GetBusinessData> getBusinessData(@Body GetBusinessRequest request);
        @POST("image-search-with-filter")
        Call<SearchGalleryResponse> getImageSerch(@Body FilterBusinessRequest filterBusinessRequest);

//single-gallery-image
        @POST("like-gallery-img")
        Call<GalleryImagesResponse> likeImage(@Header("Authorization") String headerToken,
                                                   @Body GetBusinessRequest request);

        @POST("single-gallery-image")
        Call<SingleImgDetailResponse> getSingleImgDetail(@Header("Authorization") String headerToken,
                                                         @Body GetBusinessRequest request);


        @POST("business_gallery")
        Call<GalleryImagesResponse> getGalleryData(@Header("Authorization") String headerToken,
                                                   @Body GetBusinessRequest request);

        @POST("customer_filter")
        Call<PostResponse> getFilterEvents(@Header("Authorization") String headerToken,@Body GetBusinessRequest request);

        @Multipart
        @POST("upload-image")
        Call<sendImageResponse> uploadImage(@Header("Authorization") String headerToken, @Part MultipartBody.Part images);

//        @POST("subscribe_business")
//        @Headers("Content-type: application/json")
//        Call<AllReviewResponse> subscribeBusiness(
//                @Header("Authorization") String headerToken,
//                @Body GetBusinessRequest request);


        @POST("share-profile")
        Call<VisitProfileResponse> shareProfile(@Header("Authorization") String headerToken,
                                                @Body VisitProfileRequest visitProfileRequest);

        @POST("visit-profile")
        Call<VisitProfileResponse> visitProfile(@Header("Authorization") String headerToken,
                                                @Body VisitProfileRequest visitProfileRequest);

        @GET("saved_notification")
        Call<NotificationListResponse> getNotificationList(@Header("Authorization") String headerToken);

        @FormUrlEncoded
        @POST("business_list")
        Call<BusinessListResponse> getBusinessList(@Field("cat_id") String catId);

        @POST("search-data-with-filter")
        Call<BusinessListResponse> getBusinessList(@Body FilterBusinessRequest filterBusinessRequest);

        @POST("customer_search")
        Call<PostResponse> searchEvents(@Header("Authorization") String headerToken,
                                                     @Body GetPostRequest request);

        @POST("customer_search")
        Call<PostResponse> searchHashtagFeed(@Header("Authorization") String headerToken,
                                             @Body GetPostRequest request);

        @POST("customer_posts")
        Call<PostResponse> getFeedList(@Header("Authorization") String headerToken,
                                       @Body GetPostRequest request);

        @POST("customer_posts")
        Call<PostResponse> getLocalPost(@Header("Authorization") String headerToken,
                                       @Body GetPostRequest request);
        @POST("post_location")
        Call<PostResponse> getLocationFilter(@Header("Authorization") String headerToken,
                                        @Body FeedLocationFilterRequest locationFilterReques);
        @POST("{type}")
        Call<PostResponse> getEvents(@Path("type") String type,@Header("Authorization") String headerToken, @Body FilterEventRequest filterEventRequest);

        @GET("get-referral-code")
        Call<ReferralCodeResponse> getReferralCode(@Header("Authorization") String headerToken);

        @GET("total-credits")
        Call<GetCreditResponse> getCredits(@Header("Authorization") String headerToken);

        @POST("link_click")
        Call<HeadingFilterResponse> linkClick(@Header("Authorization") String headerToken, @Body LinkClickRequest request);

    }
}
