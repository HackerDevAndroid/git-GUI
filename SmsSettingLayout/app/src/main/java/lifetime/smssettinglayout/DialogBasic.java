package lifetime.smssettinglayout;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * Created by MyPC on 26/10/2017.
 */


    public class DialogBasic extends DialogFragment {
        Spinner spConfirm;
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            //Cài đặt các thuộc tính
            builder.setTitle("Confirm !");
            builder.setMessage("Do you want to send SMS ?");

            // Cài đặt button Cancel- Hiển thị Toast
            builder.setPositiveButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(getActivity(), "No", Toast.LENGTH_SHORT).show();
                    dialog.cancel();

                }
            });
            // Cài đặt button Yes Dismiss ẩn Dialog
            builder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dismiss();
                }
            });
            return builder.create();
        }

    }

