package com.alfanse.author.Utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.alfanse.author.Models.ApiResponse;
import com.alfanse.author.R;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Sachin Rana
 * @use Singleton class used to call all the API requests from central point. This has to be
 * incorporated in the prestashop in order to get a centralized API calling place in the
 * application and ease the calling of the function to request the data.
 * @see Volley
 */

public class NetworkUtils {

    private static NetworkUtils mInstance;
    private static Context mContext;
    public boolean isConnected;
    private RequestQueue mRequestQueue;


    /**
     * Constructor for the NetworkUtils class to be used in the application
     *
     * @param context the context that should be used to initialize the variables in the singleton
     *                class
     */
    private NetworkUtils(Context context) {
        mContext = context;
        mRequestQueue = getRequestQueue();
    }

    /**
     * getInstance is used to retrieve an instance of the NetworkUtils by passing a context which will be
     * used to initialize the constructor if no instance of the NetworkUtils class is found
     *
     * @param context the context that will be used to initialize the constructor if no instance is
     *                found
     * @return mInstance is the instance of NetworkUtils class that is to be used throughout the application
     */
    public static synchronized NetworkUtils getInstance(Context context) {
        if (mInstance == null) { // if no instance is found
            mInstance = new NetworkUtils(context);
        }
        return mInstance;
    }

    /**
     * getRequestQueue is used initialize a volley requestQueue which will be holding all the API calls
     * used in the application
     *
     * @return mRequestQueue is the instance of the requestQueue which is returned by the application
     */
    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }
        return mRequestQueue;
    }

    /**
     * addToRequestQueue is used to add a Generic Volley Request such as JSONObjectRequest, StringRequest
     * et. to the requestQueue
     *
     * @param request is the request which has to be added to the requestQueue in the application
     * @param <T>     is to symbolize the generic type of the request that has to be added to the request
     *                queue.
     */
    public <T> void addToRequestQueue(Request<T> request) {
        RetryPolicy policy = new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        getRequestQueue().add(request);
    }

    /**
     * isNetworkConnected  function is used ot check whether the internet service is connected in the
     * device and return true/false
     *
     * @return true/false on the basis of the state of the network connectivity.
     */
    public Boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        return isConnected;
    }

    /**
     * sendStringRequest is used to call the string request method when called with all the parameters
     * that are used in the API calling from the central Point
     *
     * @param apiUtils is the holder for holding all the references for the API calling
     */
    public void sendStringRequest(final ApiUtils apiUtils) {

        final String URL = apiUtils.getUrl();
        String message = apiUtils.getMessage(); // Message for the application

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {

                    /**
                     *
                     * the response for the API calls made for that particular API calls
                     *
                     * @param response the string response that will be parsed by the Activity making
                     *                 the API requests for the application.
                     *
                     */
                    @Override
                    public void onResponse(String response) {
                        // send the callback for the API request in the following class
                        apiUtils.getStringResponseCallback().onSuccessCallBack(response);
                    }
                },
                new Response.ErrorListener() {

                    /**
                     *
                     * The error response function for the API calls which identifies the error type
                     * of the API calls and perform actions based on that
                     *
                     * @param volleyError - volleyError is used to identify the type of the error
                     *                    that occurs in the application due to the API not sending
                     *                    any response to the listener
                     */
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        if (apiUtils.showError()) {
                            if (volleyError instanceof TimeoutError) {
                                CommonView.showToast(mContext, mContext.getString(R.string.error_slow_network), Toast.LENGTH_LONG, CommonView.ToastType.ERROR);
                            } else if (volleyError instanceof ServerError) {
                                CommonView.showToast(mContext, mContext.getString(R.string.error_server_down), Toast.LENGTH_LONG, CommonView.ToastType.ERROR);
                            } else if (volleyError instanceof AuthFailureError) {
                                CommonView.showToast(mContext, mContext.getString(R.string.error_authentication_failed), Toast.LENGTH_LONG, CommonView.ToastType.ERROR);
                            } else if (volleyError instanceof NetworkError) {
                                CommonView.showToast(mContext, mContext.getString(R.string.error_bad_network), Toast.LENGTH_LONG, CommonView.ToastType.ERROR);
                            } else if (volleyError instanceof NoConnectionError) {
                                CommonView.showToast(mContext, mContext.getString(R.string.error_bad_network), Toast.LENGTH_LONG, CommonView.ToastType.ERROR);
                            } else if (volleyError instanceof ParseError) {
                                CommonView.showToast(mContext, mContext.getString(R.string.error_parsing), Toast.LENGTH_LONG, CommonView.ToastType.ERROR);
                            } else {
                                CommonView.showToast(mContext, mContext.getString(R.string.error_unknown_exception), Toast.LENGTH_LONG, CommonView.ToastType.ERROR);
                            }
                        }
                        apiUtils.getStringResponseCallback().onFailureCallBack(volleyError);
                    }
                }) {
            /**
             *
             * getParams() is used to send the key-value parameters in API call request ,i.e., the
             * POST parameters
             *
             * @return params - is the variable that holds all the POST parameters in the API call
             *
             */
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                if (apiUtils.getParams() != null) { // if the parameters are not empty
                    params.putAll(apiUtils.getParams());
                }
                return params;
            }

            /**
             *
             * getHeaders() is used to send the key-value headers in the API call request
             *
             * @return headers - is the variable that holds all the headers for the API calls
             * @throws AuthFailureError is thrown when the application does not errorneous data
             */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                if (apiUtils.getHeaderParams() != null) {
                    headers.putAll(apiUtils.getHeaderParams());
                }
                return headers;
            }

        };
        if (apiUtils.shouldCache()) {
            stringRequest.setShouldCache(true);
        }
        // add to the request queue that is used to hold all the reference for all the API calls
        addToRequestQueue(stringRequest);
    }


    private void parseApiResponse(String response) {

        ApiResponse apiResponse = new Gson().fromJson(response, ApiResponse.class);


    }
}
