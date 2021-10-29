package com.xxs.igcsandroid.socket;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.xxs.igcsandroid.activity.LoginActivity;
import com.xxs.igcsandroid.application.MyApplication;
import com.xxs.igcsandroid.golbal.Constants;
import com.xxs.igcsandroid.util.DlgUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.cookie.CookieJarImpl;
import com.zhy.http.okhttp.cookie.store.PersistentCookieStore;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;

public class AsyncSocketUtil {
    private static final String BASE_URL = Constants.SERVER_URL;
    private static AsyncHttpClient client = null;

    private static boolean bUseHttpClient = true;

    public interface onSuccessJSONArray {
        void OnJSONArrayResult(JSONArray response);
    }

    public interface onFailString {
        void OnStringResult(String errMsg);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }

    private static AsyncHttpClient getClient() {
        if (client == null) {
            client = new AsyncHttpClient();
            client.setTimeout(600000);
        }
        return client;
    }

    public static void initAsyncSocket(Context context) {
        if (bUseHttpClient) {
            return;
        }

        CookieJarImpl cookieJar = new CookieJarImpl(new PersistentCookieStore(context));
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectionSpecs(Arrays.asList(
                        ConnectionSpec.MODERN_TLS,
                        ConnectionSpec.COMPATIBLE_TLS,
                        ConnectionSpec.CLEARTEXT))
                .connectTimeout(600000L, TimeUnit.MILLISECONDS)  //链接超时
                .readTimeout(600000L, TimeUnit.MILLISECONDS)     //读取超时
                .cookieJar(cookieJar)
                //其他配置
                .build();

        OkHttpUtils.initClient(okHttpClient);
    }

    public static void post(final Context context, String url, Map<String, String> mpRequest, String promptInfo,
                            final onSuccessJSONArray cbSuccess, final onFailString cbFail) {
        String Tip = "正在从服务器获取数据......";
        if (promptInfo != null) {
            Tip = promptInfo;
        }
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(Tip);
        progressDialog.show();

        if (bUseHttpClient) {
            RequestParams params = new RequestParams();
            params.put("token", MyApplication.getInstance().getToken());
            if (mpRequest != null) {
                for (Map.Entry<String, String> entry : mpRequest.entrySet())
                params.put(entry.getKey(), entry.getValue());
            }

            getClient().post(getAbsoluteUrl(url), params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    progressDialog.dismiss();
                    super.onSuccess(statusCode, headers, response);

                    try {
                        JSONObject msgObj = response.getJSONObject(0);
                        String msg = msgObj.getString("message");
                        if (msg.equals("success")) {
                            cbSuccess.OnJSONArrayResult(response);
                        } else if (msg.equals("add")) {
                            cbSuccess.OnJSONArrayResult(response);
                        } else {
                            DlgUtil.showMsgInfo(context, msg);
                        }
                    } catch (Exception e) {
                        DlgUtil.showExceptionPrompt(context, e);
                    }
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    progressDialog.dismiss();
                    super.onSuccess(statusCode, headers, response);
                    try {
                        String msg = response.getString("msg");
                        DlgUtil.showMsgInfo(context, msg, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(context, LoginActivity.class);
                                context.startActivity(intent);
                            }
                        });
                    } catch (Exception e) {
                        DlgUtil.showExceptionPrompt(context, e);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, final Throwable throwable, JSONObject errorResponse) {
                    progressDialog.dismiss();
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    DlgUtil.showSocketPrompt(context, throwable, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            if (cbFail != null) {
                                cbFail.OnStringResult(throwable.getLocalizedMessage());
                            }
                        }
                    });
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, final Throwable throwable, JSONArray errorResponse) {
                    progressDialog.dismiss();
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    DlgUtil.showSocketPrompt(context, throwable, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            if (cbFail != null) {
                                cbFail.OnStringResult(throwable.getLocalizedMessage());
                            }
                        }
                    });
                }
            });
        } else {
            PostFormBuilder bulider = OkHttpUtils.post().url(getAbsoluteUrl(url));
            if (mpRequest != null) {
                for (Map.Entry<String, String> entry : mpRequest.entrySet())
                    bulider.addParams(entry.getKey(), entry.getValue());
            }
            bulider.build().execute(new StringCallback() {
                @Override
                public void onResponse(String response, int id) {
                    progressDialog.dismiss();

                    try {
                        JSONArray jArray = new JSONArray(response);
                        JSONObject msgObj = jArray.getJSONObject(0);
                        String msg = msgObj.getString("message");
                        if (msg.equals("success")) {
                            cbSuccess.OnJSONArrayResult(jArray);
                        } else if (msg.equals("add")) {
                            cbSuccess.OnJSONArrayResult(jArray);
                        } else {
                            DlgUtil.showMsgInfo(context, msg);
                        }
                    } catch (final Exception e) {
                        e.printStackTrace();
                        DlgUtil.showSocketPrompt(context, e, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                if (cbFail != null) {
                                    cbFail.OnStringResult(e.getLocalizedMessage());
                                }
                            }
                        });
                    }
                }

                @Override
                public void onError(Call call, final Exception e, int id) {
                    progressDialog.dismiss();

                    DlgUtil.showSocketPrompt(context, e, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            if (cbFail != null) {
                                cbFail.OnStringResult(e.getLocalizedMessage());
                            }
                        }
                    });
                }
            });
        }
    }

    public static void postWithFile(final Context context, String url, Map<String, String> mpRequest,
                                    String fileKey, File file, String promptInfo,
                                    final onSuccessJSONArray cbSuccess, final onFailString cbFail) {
        String Tip = "正在从服务器获取数据......";
        if (promptInfo != null) {
            Tip = promptInfo;
        }
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(Tip);
        progressDialog.show();

        if (bUseHttpClient) {
            RequestParams params = new RequestParams();
            params.put("token", MyApplication.getInstance().getToken());
            if (mpRequest != null) {
                for (Map.Entry<String, String> entry : mpRequest.entrySet())
                    params.put(entry.getKey(), entry.getValue());
            }
            try {
                if (file != null) {
                    params.put(fileKey, file);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            getClient().post(getAbsoluteUrl(url), params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    progressDialog.dismiss();
                    super.onSuccess(statusCode, headers, response);

                    try {
                        JSONObject msgObj = response.getJSONObject(0);
                        String msg = msgObj.getString("message");
                        if (msg.equals("success")) {
                            cbSuccess.OnJSONArrayResult(response);
                        } else {
                            DlgUtil.showMsgInfo(context, msg);
                        }
                    } catch (Exception e) {
                        DlgUtil.showExceptionPrompt(context, e);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, final Throwable throwable, JSONObject errorResponse) {
                    progressDialog.dismiss();

                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    DlgUtil.showSocketPrompt(context, throwable, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            if (cbFail != null) {
                                cbFail.OnStringResult(throwable.getLocalizedMessage());
                            }
                        }
                    });
                }
            });
        } else {
            PostFormBuilder bulider = OkHttpUtils.post().url(getAbsoluteUrl(url));
            if (mpRequest != null) {
                for (Map.Entry<String, String> entry : mpRequest.entrySet())
                    bulider.addParams(entry.getKey(), entry.getValue());
            }
            bulider.build().execute(new StringCallback() {
                @Override
                public void onResponse(String response, int id) {
                    progressDialog.dismiss();

                    try {
                        cbSuccess.OnJSONArrayResult(new JSONArray(response));
                    } catch (final JSONException e) {
                        e.printStackTrace();
                        DlgUtil.showSocketPrompt(context, e, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                if (cbFail != null) {
                                    cbFail.OnStringResult(e.getLocalizedMessage());
                                }
                            }
                        });
                    }
                }

                @Override
                public void onError(Call call, final Exception e, int id) {
                    progressDialog.dismiss();

                    DlgUtil.showSocketPrompt(context, e, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            if (cbFail != null) {
                                cbFail.OnStringResult(e.getLocalizedMessage());
                            }
                        }
                    });
                }
            });
        }
    }

    // 用于后台服务的调用，不会弹错误框
    public static void postBack(String url, Map<String, String> mpRequest, final onSuccessJSONArray cbSuccess, final onFailString cbFail) {
        if (bUseHttpClient) {
            RequestParams params = new RequestParams();
            params.put("token", MyApplication.getInstance().getToken());
            if (mpRequest != null) {
                for (Map.Entry<String, String> entry : mpRequest.entrySet())
                    params.put(entry.getKey(), entry.getValue());
            }

            getClient().post(getAbsoluteUrl(url), params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    super.onSuccess(statusCode, headers, response);

                    try {
                        JSONObject msgObj = response.getJSONObject(0);
                        String msg = msgObj.getString("message");
                        if (msg.equals("success")) {
                            cbSuccess.OnJSONArrayResult(response);
                        } else {

                        }
                    } catch (Exception e) {

                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, final Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    if (cbFail != null) {
                        cbFail.OnStringResult(throwable.getLocalizedMessage());
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, final Throwable throwable, JSONArray errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    if (cbFail != null) {
                        cbFail.OnStringResult(throwable.getLocalizedMessage());
                    }
                }
            });
        } else {

        }
    }

    public static void postLogin(final Context context, String url, String userId, String userPwd,
                            final onSuccessJSONArray cbSuccess, final onFailString cbFail) {
        String Tip = "正在登录......";
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(Tip);
        progressDialog.show();

        if (bUseHttpClient) {
            RequestParams params = new RequestParams();
            params.put("userId", userId);
            params.put("userPwd", userPwd);

            getClient().post(getAbsoluteUrl(url), params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    progressDialog.dismiss();
                    super.onSuccess(statusCode, headers, response);
                    cbSuccess.OnJSONArrayResult(response);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, final Throwable throwable, JSONObject errorResponse) {
                    progressDialog.dismiss();
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    cbFail.OnStringResult(throwable.getLocalizedMessage());
                }
            });
        } else {
//            PostFormBuilder bulider = OkHttpUtils.post().url(getAbsoluteUrl(url));
//            if (mpRequest != null) {
//                for (Map.Entry<String, String> entry : mpRequest.entrySet())
//                    bulider.addParams(entry.getKey(), entry.getValue());
//            }
//            bulider.build().execute(new StringCallback() {
//                @Override
//                public void onResponse(String response, int id) {
//                    progressDialog.dismiss();
//
//                    try {
//                        JSONArray jArray = new JSONArray(response);
//                        JSONObject msgObj = jArray.getJSONObject(0);
//                        String msg = msgObj.getString("message");
//                        if (msg.equals("success")) {
//                            cbSuccess.OnJSONArrayResult(jArray);
//                        } else if (msg.equals("add")) {
//                            cbSuccess.OnJSONArrayResult(jArray);
//                        } else {
//                            DlgUtil.showMsgInfo(context, msg);
//                        }
//                    } catch (final Exception e) {
//                        e.printStackTrace();
//                        DlgUtil.showSocketPrompt(context, e, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                                if (cbFail != null) {
//                                    cbFail.OnStringResult(e.getLocalizedMessage());
//                                }
//                            }
//                        });
//                    }
//                }
//
//                @Override
//                public void onError(Call call, final Exception e, int id) {
//                    progressDialog.dismiss();
//
//                    DlgUtil.showSocketPrompt(context, e, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//                            if (cbFail != null) {
//                                cbFail.OnStringResult(e.getLocalizedMessage());
//                            }
//                        }
//                    });
//                }
//            });
        }


    }
}
