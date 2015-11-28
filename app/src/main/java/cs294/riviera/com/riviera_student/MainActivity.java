package cs294.riviera.com.riviera_student;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.TextUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseObject;

import java.nio.charset.Charset;

public class MainActivity extends AppCompatActivity {
    private Tag mTag;
    private Button resumeButton;
    private Button submitButton;

    private EditText studentName;
    private EditText studentEmail;
    private EditText studentGrad;
    private EditText studentWebsite;
    private EditText studentLinkedin;

    private String nameText;
    private String emailText;
    private String gradText;
    private String websiteText;
    private String linkedinText;

    private ParseWrapper mParseWrapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resumeButton = (Button) findViewById(R.id.resume_button);
        submitButton = (Button) findViewById(R.id.submit_button);

        studentName = (EditText) findViewById(R.id.student_name);
        studentEmail = (EditText) findViewById(R.id.student_email);
        studentGrad = (EditText) findViewById(R.id.student_grad);
        studentWebsite = (EditText) findViewById(R.id.student_website);
        studentLinkedin = (EditText) findViewById(R.id.student_linkedin);

        mParseWrapper = new ParseWrapper(MainActivity.this);
    }

    public void handleResumeButton(View view) {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        startActivity(intent);
    }

    public void handleSubmitButton(View view) {
        nameText = studentName.getText().toString();
        emailText = studentEmail.getText().toString();
        gradText = studentGrad.getText().toString();
        websiteText = studentWebsite.getText().toString();
        linkedinText = studentLinkedin.getText().toString();

        Boolean cancel = signUpCheck();

        if (!cancel) {
            displayOk("Success!");
            mParseWrapper.saveStudent(emailText, nameText, websiteText, null, Integer.parseInt(gradText));
        } else {
            Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean signUpCheck() {
        View focusView = null;
        Boolean cancel = false;
        if (TextUtils.isEmpty(nameText)) {
            studentName.setError(getString(R.string.error_field_required));
            focusView = studentName;
            cancel = true;
        }

        if (TextUtils.isEmpty(emailText)) {
            studentEmail.setError(getString(R.string.error_field_required));
            focusView = studentEmail;
            cancel = true;
        }

        if (TextUtils.isEmpty(gradText)) {
            studentGrad.setError(getString(R.string.error_field_required));
            focusView = studentGrad;
            cancel = true;
        }

        if (TextUtils.isEmpty(websiteText)) {
            studentWebsite.setError(getString(R.string.error_field_required));
            focusView = studentWebsite;
            cancel = true;
        }
        if (TextUtils.isEmpty(linkedinText)) {
            studentLinkedin.setError(getString(R.string.error_field_required));
            focusView = studentLinkedin;
            cancel = true;
        }
        return cancel;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTag = getIntent().getParcelableExtra(NfcAdapter.EXTRA_TAG);
        NdefRecord uriRecord = new NdefRecord(
                NdefRecord.TNF_ABSOLUTE_URI ,
                "http://developer.android.com/index.html".getBytes(Charset.forName("US-ASCII")),
                new byte[0], new byte[0]);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /** Displays a toast with a MESSAGE. */
    public void displayOk(String message) {
        hideSoftKeyboard(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Thanks for signing up! Please pick up your badge.")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        studentName.setText("");
                        studentEmail.setText("");
                        studentGrad.setText("");
                        studentWebsite.setText("");
                        studentLinkedin.setText("");
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /** Dismisses the soft-key board outside of EditText area. */
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }
}
