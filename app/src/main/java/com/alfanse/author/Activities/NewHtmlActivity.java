/*
 * Copyright (c) 2018. Alfanse Developers
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at
 *  http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */

package com.alfanse.author.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.alfanse.author.CustomViews.DialogBuilder;
import com.alfanse.author.Interfaces.ExceptionDialogButtonListener;
import com.alfanse.author.Interfaces.NetworkCallback;
import com.alfanse.author.Models.Image;
import com.alfanse.author.R;
import com.alfanse.author.Utilities.ApiUtils;
import com.alfanse.author.Utilities.CommonView;
import com.alfanse.author.Utilities.Constants;
import com.alfanse.author.Utilities.FontHelper;
import com.alfanse.author.Utilities.SharedManagement;
import com.alfanse.author.Utilities.Utils;
import com.github.irshulx.Editor;
import com.github.irshulx.EditorListener;
import com.github.irshulx.models.EditorTextStyle;
import com.google.gson.Gson;
import com.jrummyapps.android.colorpicker.ColorPickerDialog;
import com.jrummyapps.android.colorpicker.ColorPickerDialogListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.alfanse.author.Utilities.Constants.ASSETS_DIR_FONTS;

public class NewHtmlActivity extends BaseActivity implements ColorPickerDialogListener {
    Editor editor;
    private Context mContext;
    private Activity mActivity;

    @BindView(R.id.toolbar_new_post)
    Toolbar mToolbar;

    private final long backupInterval = 10 * 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_html);
        ButterKnife.bind(this);
        initToolbar();
        mContext = getApplicationContext();
        mActivity = NewHtmlActivity.this;
        editor = findViewById(R.id.editor);
        setUpEditor();
    }

    private void initToolbar() {

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(FontHelper.getCustomTypefaceTitle(getString(R.string.title_new_quote)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_new_post, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_reset_new_post:
                showResetWarningDialog();
                return true;

            case R.id.action_done_new_post:
                //to be done
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(menuItem);

        }
    }

    private void showResetWarningDialog() {

        DialogBuilder builder = new DialogBuilder(mActivity);
        // Add the buttons
        builder.setPositiveButton(R.string.action_reset, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                startActivity(getIntent());
                finish();
            }
        });
        builder.setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        // Set other dialog properties
        builder.setMessage(R.string.msg_reset_confirm);
        builder.setDialogType(DialogBuilder.WARNING);

        // Create the AlertDialog
        android.support.v7.app.AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void setUpEditor() {

        findViewById(R.id.action_h1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.updateTextStyle(EditorTextStyle.H1);
            }
        });

        findViewById(R.id.action_h2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.updateTextStyle(EditorTextStyle.H2);
            }
        });

        findViewById(R.id.action_h3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.updateTextStyle(EditorTextStyle.H3);
            }
        });

        findViewById(R.id.action_bold).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.updateTextStyle(EditorTextStyle.BOLD);
            }
        });

        findViewById(R.id.action_Italic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.updateTextStyle(EditorTextStyle.ITALIC);
            }
        });

        findViewById(R.id.action_indent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.updateTextStyle(EditorTextStyle.INDENT);
            }
        });

        findViewById(R.id.action_outdent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.updateTextStyle(EditorTextStyle.OUTDENT);
            }
        });

        findViewById(R.id.action_blockquote).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.updateTextStyle(EditorTextStyle.BLOCKQUOTE);
            }
        });

        findViewById(R.id.action_bulleted).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.insertList(false);
            }
        });

        findViewById(R.id.action_unordered_numbered).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.insertList(true);
            }
        });

        findViewById(R.id.action_hr).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.insertDivider();
            }
        });


        findViewById(R.id.action_color).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ColorPickerDialog.newBuilder()
                        .setDialogType(ColorPickerDialog.TYPE_CUSTOM)
                        .setAllowPresets(true)
                        .setDialogId(123)
                        .setColor(ContextCompat.getColor(mContext, R.color.colorBlack))
                        .setShowAlphaSlider(false)
                        .show(mActivity);
            }
        });

        findViewById(R.id.action_insert_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.openImagePicker();
            }
        });

        findViewById(R.id.action_insert_link).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.insertLink();
            }
        });

        findViewById(R.id.action_erase).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.clearAllContents();
            }
        });
        //editor.dividerBackground=R.drawable.divider_background_dark;
        //editor.setFontFace(R.string.fontFamily__serif);
        Map<Integer, String> headingTypeface = getHeadingTypeface();
        Map<Integer, String> contentTypeface = getContentface();
        editor.setHeadingTypeface(headingTypeface);
        editor.setContentTypeface(contentTypeface);
        editor.setDividerLayout(R.layout.tmpl_divider_layout);
        editor.setEditorImageLayout(R.layout.tmpl_image_view);
        editor.setListItemLayout(R.layout.tmpl_list_item);
        //editor.setNormalTextSize(10);
        // editor.setEditorTextColor("#FF3333");
        //editor.StartEditor();
        editor.setEditorListener(new EditorListener() {
            @Override
            public void onTextChanged(EditText editText, Editable text) {
                // Toast.makeText(EditorTestActivity.this, text, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onUpload(Bitmap image, String uuid) {

                uploadImageOnServer(image, uuid);

            }

            @Override
            public View onRenderMacro(String name, Map<String, Object> props, int index) {
                View view = getLayoutInflater().inflate(R.layout.layout_authored_by, null);
                return view;
            }
        });


        /**
         * Rendering html
         */
        //render();
        //editor.render();  // this method must be called to start the editor
        //String text = "<h1 data-tag=\"input\" style=\"color:#000000;\"><span style=\"color:#000000;\"><b>it is not available beyond that statue in a few days and then we could</b></span></h1><hr data-tag=\"hr\"/><author-tag data-tag=\"macro\"  data-date=\"12 July 2018\" data-author-name=\"Alex Wong\"></author-tag><p data-tag=\"input\" style=\"color:#000000;\">it is a free trial to get a great weekend a good day to you u can do that for.</p><p data-tag=\"input\" style=\"color:#000000;\">it is that I have to do the needful as early in life is not available beyond my imagination to be a good.</p><div data-tag=\"img\"><img src=\"http://www.videogamesblogger.com/wp-content/uploads/2015/08/metal-gear-solid-5-the-phantom-pain-cheats-640x325.jpg\" /><p data-tag=\"img-sub\" style=\"color:#006AFF;\" class=\"editor-image-subtitle\"><b>it is not available in the next week or two and I have a place where I</b></p></div><p data-tag=\"input\" style=\"color:#000000;\">it is not available in the next week to see you tomorrow morning to see you then.</p><hr data-tag=\"hr\"/><p data-tag=\"input\" style=\"color:#000000;\">it is not available in the next day delivery to you soon with it and.</p>";
        //String text = "";
        editor.render("<p>your content goes here...</p>");
        //editor.render(text);

        saveContentPeriodically();

    }

    private void uploadImageOnServer(final Bitmap image, final String uuid) {

        //region API_CALL_START
        CommonView.getInstance(mContext).showTransparentProgressDialog(mActivity, null);
        HashMap<String, String> param = new HashMap<>();
        param.put(Constants.API_PARAM_KEY_IMAGE, Utils.getInstance(mContext).getStringImage(image));
        ApiUtils api = new ApiUtils(mContext)
                .setActivity(mActivity)
                .setUrl(Constants.API_URL_UPLOAD_IMAGE)
                .setParams(param)
                .setShowError(false)
                .setMessage("NewHTmlActivity.java|uploadImageOnServer")
                .setStringResponseCallback(new NetworkCallback.stringResponseCallback() {
                    @Override
                    public void onSuccessCallBack(String stringResponse) {
                        try {
                            Image uploadedImage = new Gson().fromJson(stringResponse, Image.class);
                            editor.onImageUploadComplete(uploadedImage.getImageUrl(), uuid);
                        } catch (Exception e) {
                            editor.onImageUploadFailed(uuid);
                            Utils.getInstance(mContext).logException(e);
                        }
                        CommonView.getInstance(mContext).dismissProgressDialog();
                    }

                    @Override
                    public void onFailureCallBack(Exception e) {
                        editor.onImageUploadFailed(uuid);
                        CommonView.getInstance(mContext).showExceptionErrorDialog(mActivity, Utils.getInstance(mContext).getErrorMessage(e), new ExceptionDialogButtonListener() {
                            @Override
                            public void onRetryClick() {
                                uploadImageOnServer(image, uuid);
                            }

                            @Override
                            public void onCancelClick() {
                                CommonView.getInstance(mContext).dismissProgressDialog();
                            }
                        });
                    }
                });

        api.call();
        //endregion API_CALL_END

    }

    private void saveContentPeriodically() {

        /**
         * Since the endusers are typing the content, it's always considered good idea to backup the content every specific interval
         * to be safe.
         */

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                String text = editor.getContentAsHTML();
                SharedManagement.getInstance(mContext).setString(String.format("backup-{0}", new SimpleDateFormat("dd-MM-yyyy hh:mm:ss").format(new Date())), text);
            }
        }, 0, backupInterval);
    }

    private String colorHex(int color) {
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        return String.format(Locale.getDefault(), "#%02X%02X%02X", r, g, b);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == editor.PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                // Log.d(TAG, String.valueOf(bitmap));
                editor.insertImage(bitmap);
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            //Write your code if there's no result
            Toast.makeText(getApplicationContext(), "Cancelled", Toast.LENGTH_SHORT).show();
            // editor.RestoreState();
        }
    }

    @Override
    public void onBackPressed() {
        showExitWarningDialog();
    }

    private void showExitWarningDialog() {

        DialogBuilder builder = new DialogBuilder(mActivity);
        // Add the buttons
        builder.setPositiveButton(R.string.action_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });
        builder.setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        // Set other dialog properties
        builder.setMessage(R.string.msg_exit_confirm);
        builder.setDialogType(DialogBuilder.WARNING);

        // Create the AlertDialog
        android.support.v7.app.AlertDialog dialog = builder.create();
        dialog.show();
    }


    public Map<Integer, String> getHeadingTypeface() {
        Map<Integer, String> typefaceMap = new HashMap<>();
        typefaceMap.put(Typeface.NORMAL, ASSETS_DIR_FONTS + "DIN-Regular.otf");
        typefaceMap.put(Typeface.BOLD, ASSETS_DIR_FONTS + "DIN-Bold.otf");
        typefaceMap.put(Typeface.ITALIC, ASSETS_DIR_FONTS + "DIN-Italic.ttf");
        typefaceMap.put(Typeface.BOLD_ITALIC, ASSETS_DIR_FONTS + "DIN-BoldItalic.ttf");
        return typefaceMap;
    }

    public Map<Integer, String> getContentface() {
        Map<Integer, String> typefaceMap = new HashMap<>();
        typefaceMap.put(Typeface.NORMAL, ASSETS_DIR_FONTS + "DIN-Regular.otf");
        typefaceMap.put(Typeface.BOLD, ASSETS_DIR_FONTS + "DIN-Bold.otf");
        typefaceMap.put(Typeface.ITALIC, ASSETS_DIR_FONTS + "DIN-Italic.ttf");
        typefaceMap.put(Typeface.BOLD_ITALIC, ASSETS_DIR_FONTS + "DIN-BoldItalic.ttf");
        return typefaceMap;
    }

    @Override
    public void onColorSelected(int dialogId, int color) {
        Toast.makeText(NewHtmlActivity.this, "picked" + colorHex(color), Toast.LENGTH_LONG).show();
        editor.updateTextColor(colorHex(color));
    }

    @Override
    public void onDialogDismissed(int dialogId) {

    }
}