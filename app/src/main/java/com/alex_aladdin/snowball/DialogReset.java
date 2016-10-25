package com.alex_aladdin.snowball;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

public class DialogReset extends DialogFragment {
    private ResetDialogListener mListener;

    //Добавляем интерфейс ResetDialogListener в виде слушателя с одним методом для кнопки YES
    public interface ResetDialogListener {
        void onDialogPositiveClick(DialogFragment dialog);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.reset_title);
        builder.setMessage(R.string.reset_message);
        //Обработка события нажатия кнопки NO
        builder.setNegativeButton(R.string.reset_button_no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        //Обработка события нажатия кнопки YES
        builder.setPositiveButton(R.string.reset_button_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mListener.onDialogPositiveClick(DialogReset.this);
            }
        });
        builder.setCancelable(true);

        return builder.create();
    }

    //Предупреждаем активность, которая собирается использовать диалоговое окно, о необходимости реализовать методы интерфейса
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (ResetDialogListener) context;
        } catch (ClassCastException e) {
            //Активность не реализует интерфейс, создаем исключение
            throw new ClassCastException(context.toString() + " must implement ResetDialogListener");
        }
    }
}