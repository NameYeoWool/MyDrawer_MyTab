package kr.ac.kumho.mydrawer;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class NewsInputActivity extends AppCompatActivity{
    TextView mMemo = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newsinput);

        mMemo = (TextView) findViewById(R.id.txtMemo);
    }

    public void onWrite(View v){
        Intent i = new Intent();
        i.putExtra("memo", mMemo.getText().toString());
        setResult(RESULT_OK, i);
        finish();
    }
}