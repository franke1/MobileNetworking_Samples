package com.vanillastepdev.example.smalltalkparser;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.common.io.ByteStreams;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.io.InputStream;

public class ComposeActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 2;
    private static final String TAG = "NewTalkTask";

    // 선택된 이미지
    private ImageView mImageView;
    private Uri mImageUri;
    private EditText mTalkInput;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        mImageView = (ImageView)findViewById(R.id.imageView);
        mTalkInput = (EditText)findViewById(R.id.talkInput);

    }

    // 사진 선택 버튼 리스너
    public void selectImage(View v) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // 사진 선택 결과
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ( PICK_IMAGE_REQUEST == requestCode ) {
            try {
                // 선택한 이미지 경로
                Log.d(TAG, "data : " + data);
                mImageUri = data.getData();
                // 이미지 뷰에 이미지 출력
                mImageView.setImageURI(mImageUri);
            } catch (Exception e) {
                Log.e(TAG, "URISyntaxException", e);
                e.printStackTrace();
            }
        }
    }

    // 취소 버튼 리스너
    public void cancelCompose(View v) {
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    // 완료 버튼 리스너
    public void completeCompose(View v) {
        try {
            ParseObject newTalk = new ParseObject("talks");

            String text = mTalkInput.getText().toString();
            newTalk.put("text", text);

            if ( null != mImageUri ) {
                InputStream is =  getContentResolver().openInputStream(mImageUri);
                // Guava Library (https://github.com/google/guava)
                byte[] imageData = ByteStreams.toByteArray(is);

                ParseFile image = new ParseFile("image", imageData);
                newTalk.put("image", image);
            }

            newTalk.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    // TODO : Error check!
                    Log.d(TAG, "Save Done");
                    setResult(Activity.RESULT_OK);
                    finish();
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Exception", e);
            setResult(Activity.RESULT_CANCELED);
            finish();
        }
    }


}
