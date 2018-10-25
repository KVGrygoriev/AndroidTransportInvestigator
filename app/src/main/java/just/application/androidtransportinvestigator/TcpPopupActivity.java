package just.application.androidtransportinvestigator;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import static just.application.androidtransportinvestigator.Defines.TCP_IP_KEY;

public class TcpPopupActivity extends AppCompatActivity {

    Button btnAccept, btnCancel;
    EditText ipEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tcp_popup);
        setTitle("TCP settings");

        InitWindowSize();

        InitWidgets();

        registerListener();

        ipEditor.setText(getIntent().getStringExtra(TCP_IP_KEY));
    }

    private void InitWindowSize() {

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width *.9), (int)(height *.22));
    }

    private void InitWidgets() {
        btnAccept = (Button) findViewById(R.id.btnAcceptTcpSettings);
        btnCancel = (Button) findViewById(R.id.btnCancelTcpSettings);

        ipEditor = (EditText) findViewById(R.id.ipEditor);
    }

    private void registerListener() {
        btnAccept.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(TCP_IP_KEY, ipEditor.getText().toString());
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        });
    }
}
