package com.example.weibo.retrofit;

import androidx.annotation.Nullable;

import com.example.weibo.bean.UserInfo;
import com.example.weibo.bean.WeiboInfo;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RetrofitApi {

    @POST("/weibo/api/auth/sendCode")
    Call<ApiResponse> sendCode(@Body SendCodeRequest sendCodeRequest);

    @POST("/weibo/api/auth/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @GET("/weibo/api/user/info")
    Call<UserInfoResponse> getUserInfo(@Header("Authorization") String token);

    @GET("/weibo/homePage")
    Call<GenericApiResponse<Page<WeiboInfo>>> getHomePage(@Header("Authorization") @Nullable String token, @Query("current") int current, @Query("size") int size);

    @POST("/weibo/like/up")
    Call<ApiResponse> like(@Header("Authorization") String token, @Body LikeRequest likeRequest);

    @POST("/weibo/like/down")
    Call<ApiResponse> cancelLike(@Header("Authorization") String token, @Body LikeRequest likeRequest);

    // Request and Response classes
    class SendCodeRequest {
        private String phone;

        public SendCodeRequest(String phone) {
            this.phone = phone;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }
    }

    class LoginRequest {
        private String phone;
        private String smsCode;

        public LoginRequest(String phone, String smsCode) {
            this.phone = phone;
            this.smsCode = smsCode;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getSmsCode() {
            return smsCode;
        }

        public void setSmsCode(String smsCode) {
            this.smsCode = smsCode;
        }
    }

    class LikeRequest {
        private long id;

        public LikeRequest(long id) {
            this.id = id;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }
    }

    class ApiResponse {
        private int code;
        private String msg;
        private boolean data;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public boolean isData() {
            return data;
        }

        public void setData(boolean data) {
            this.data = data;
        }
    }

    class LoginResponse {
        private int code;
        private String msg;
        private String data; // token

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }
    }

    class UserInfoResponse {
        private int code;
        private String msg;
        private UserInfo data;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public UserInfo getData() {
            return data;
        }

        public void setData(UserInfo data) {
            this.data = data;
        }
    }

    class Page<T> {
        private List<T> records;
        private int total;
        private int size;
        private int current;
        private int pages;

        public List<T> getRecords() {
            return records;
        }

        public void setRecords(List<T> records) {
            this.records = records;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public int getCurrent() {
            return current;
        }

        public void setCurrent(int current) {
            this.current = current;
        }

        public int getPages() {
            return pages;
        }

        public void setPages(int pages) {
            this.pages = pages;
        }
    }
}
