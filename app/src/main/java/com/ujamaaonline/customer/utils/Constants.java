package com.ujamaaonline.customer.utils;

public interface Constants {

    //Fontawesome file name
    String ICON_FILE = "fontawesome-webfont.ttf";

    String phoneFormat = "XXX-XXX-XXXX";
    String PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";

    String LANGUAGE_PREFERENCE = "LanguagePreference";
    String LOGIN_PREFERENCE = "LoginPreference";
    String SELECTED_LANG_ID = "languageId";
    String SELECTED_LANGUAGE = "selectedLanguage";
    String IS_LOGGED_IN = "isLoggedIn";
    String HEADER_TOKEN = "headerToken";
    String USER_ID = "userId";
    String USER_ROLE = "userRole";
    String USER_NAME = "userName";
    String USER_IMAGE = "userImage";
    String USER_POST_CODE = "user_post_code";
    String SUBSCRIPTION_ID = "subcription_id";

    String BUSINESS = "business";
    String CUSTOMER = "customer";

    boolean RESPONSE_SUCCESS = true;
    String BEARER = "Bearer ";

    String ENGLISH_LANGUAGE_ID = "en";
    String SPANISH_LANGUAGE_ID = "es";

    int REQUEST_CODE = 101;

    //String BASE_URL = "https://stag.employeelive.com/vamos/public/api/";
    String BASE_URL = " https://dev.theappkit.co.uk/Ujamaa/public/api/";

}
