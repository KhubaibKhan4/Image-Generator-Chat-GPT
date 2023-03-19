package com.codespacepro.chatgptimagegenerator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    EditText inputText;
    MaterialButton generateBtn;
    ProgressBar progressBar;
    ImageView imageView;

    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");

    OkHttpClient client = new OkHttpClient();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); //

        inputText = findViewById(R.id.input_text);
        generateBtn = findViewById(R.id.generate_btn);
        progressBar = findViewById(R.id.progress_bar);
        imageView = findViewById(R.id.image_view);

        generateBtn.setOnClickListener((v)->{
            String text = inputText.getText().toString().trim();
            if(text.isEmpty()){
                inputText.setError("Text can't be empty");
                return;
            }
            callAPI(text);
        });

    }
    void callAPI(String text){
        //API CALL
        setInProgress(true);
        JSONObject jsonBody = new JSONObject();
        try{
            jsonBody.put("prompt",text);
            jsonBody.put("size","256x256");
        }catch (Exception e){
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(jsonBody.toString(),JSON);
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/images/generations")
                .header("Authorization","Bearer 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")
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Toast.makeText(getApplicationContext(),"Failed to generate image",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                try{
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    String imageUrl = jsonObject.getJSONArray("data").getJSONObject(0).getString("url");
                    loadImage(imageUrl);
                    setInProgress(false);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

    }

    void setInProgress(boolean inProgress){
        runOnUiThread(()->{
            if(inProgress){
                progressBar.setVisibility(View.VISIBLE);
                generateBtn.setVisibility(View.GONE);
            }else{
                progressBar.setVisibility(View.GONE);
                generateBtn.setVisibility(View.VISIBLE);
            }
        });

    }

    void loadImage(String url){
        //load image

        runOnUiThread(()->{
            Picasso.get().load(url).into(imageView);
        });

    }
}