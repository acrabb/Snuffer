package crabb.andre.Snuffer;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.codebutler.android_websockets.WebSocketClient;
import org.apache.http.message.BasicNameValuePair;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

public class Snuffer extends Activity {
    // Member variables
    private WebSocketClient mClient;
    private String lastStatus = null;

    // Constants
    private static final String TAG = "ACACAC";
    private String MI_URL           = "ws://glacial-shore-1996.herokuapp.com/android";
    private String SNUFF_MESSAGE    = "0";
    private String IGNITE_MESSAGE   = "1";
    private String SNUFFED_STATUS   = "0";
    private String SNUFFING_STATUS  = "2";
    private String IGNITED_STATUS   = "1";

    // View things
    private TextView statusView;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        statusView = (TextView) findViewById(R.id.status);

        setUp();
    }

    //-------------------------------------------------------------------------
    private void setUp() {
        List<BasicNameValuePair> extraHeaders = Arrays.asList(
                new BasicNameValuePair("Cookie", "session=abcd")
        );
        mClient = new WebSocketClient(URI.create(MI_URL), new WebSocketClient.Listener() {
            @Override
            public void onConnect() {
                Log.i(TAG, "Connected!");
            }
            @Override
            public void onMessage(String message) {
                Log.d(TAG, String.format("Got string message! %s", message));
                handleMessage(message);
            }
            @Override
            public void onMessage(byte[] data) {
                Log.d(TAG, String.format("Got binary message! %s", data));
            }
            @Override
            public void onDisconnect(int code, String reason) {
                Log.d(TAG, String.format("Disconnected! Code: %d Reason: %s", code, reason));
            }
            @Override
            public void onError(Exception error) {
                Log.e(TAG, "Error!", error);
            }
        }, extraHeaders);

        mClient.connect();
    }

    //-------------------------------------------------------------------------
    public void snuffTapped(View view) {
        String mess = "";
        if (IGNITED_STATUS.equals(lastStatus)) {
            mess = SNUFF_MESSAGE;
        } else if (SNUFFED_STATUS.equals(lastStatus)) {
            mess = IGNITE_MESSAGE;
        } else if (SNUFFING_STATUS.equals(lastStatus)) {
            // Do nothing.
        }
        if(mClient.isConnected()) {
            Log.d(TAG, String.format(">> Sending Message : %s", mess));
            mClient.send(mess);
        }
//        mClient.disconnect();
    }
    //-------------------------------------------------------------------------
    private void updateStatusLabel() {
        statusView.setText(lastStatus);
    }
    //-------------------------------------------------------------------------
    private void handleMessage(String message) {
        String mess = "";
        if (IGNITED_STATUS.equals(message)) {
            lastStatus = IGNITED_STATUS;
            mess = "ignited";
        } else if (SNUFFED_STATUS.equals(message)) {
            lastStatus = SNUFFED_STATUS;
            mess = "snuffed";
        } else if (SNUFFING_STATUS.equals(message)) {
            lastStatus = SNUFFING_STATUS;
            mess = "snuffing";
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateStatusLabel();
            }
        });
        Log.d(TAG, String.format(">> Candle is %s.", mess));
    }

}
