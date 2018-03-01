package uxt6.psu.com.a1000books.utility;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnEditorAction;
import uxt6.psu.com.a1000books.R;

/**
 * Created by aisyahumar on 2/25/2018.
 */

public class BaseDialog extends AlertDialog {

    @BindView(R.id.tv_title) TextView tvTitle;
    @BindView(R.id.tv_message) TextView tvMessage;
    @BindView(R.id.btn_ok) Button btnOk;
    @BindView(R.id.btn_cancel) Button btnCancel;

    private String title = "no title";
    private String message = "no message";

    protected BaseDialog(Context context) {
        super(context);
    }

    /**
     * register a callback to be invoked when this dialog's button is clicked
     * @param listener
     * @return
     */
    public BaseDialog setBaseDialogListener(BaseDialog.BaseDialogListener listener){
        try{
            baseDialogListener = (BaseDialog.BaseDialogListener) listener;
        }catch(ClassCastException e){
            e.printStackTrace();
        }
        return this;
    }

    /**
     * static method for creating BaseDialog instance
     * and passing the object that implements BaseDialogListener interface
      * @param context
     * @return
     */
    public static BaseDialog getInstance(Context context) {
        BaseDialog dialog = new BaseDialog(context);
        dialog.setBaseDialogListener((BaseDialog.BaseDialogListener) context);
        return dialog;
    }

    /**
     * define interface
     */
    public interface BaseDialogListener{
        // this method is called to handle positive action
        void onPositiveClick();
        // this method is called to handle negative action as well as cancel an action
        void onNegativeClick();
    }

    BaseDialogListener baseDialogListener;

    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.dialog_alert);
        ButterKnife.bind(this);

        tvTitle.setText(title); // dialog's title
        tvMessage.setText(message); // dialog's message

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { // positive button
                // invoking the interface method
                baseDialogListener.onPositiveClick();
                cancel();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() { // negative button
            @Override
            public void onClick(View view) {
                // invoking the interface method
                baseDialogListener.onNegativeClick();
                cancel();
            }
        });

        setCancelable(true);
    }

    /**
     * set dialog's title
     * @param title
     * @return
     */
    public BaseDialog setDialogTitle(CharSequence title){
        this.title = title.toString();
        return this;
    }

    /**
     * set dialog's message
     * @param message
     * @return
     */
    public BaseDialog setDialogMessage(CharSequence message){
        this.message = message.toString();
        return this;
    }
}
