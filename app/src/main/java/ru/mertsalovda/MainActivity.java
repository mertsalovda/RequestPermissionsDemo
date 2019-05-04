package ru.mertsalovda;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public static final int WRITE_PERMISSION_RC = 123;
    private EditText mInput;
    private Button mWrite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mInput = findViewById(R.id.et_input);
        mWrite = findViewById(R.id.btn_write);

        mWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textToWrite = mInput.getText().toString();
                writeToFileIfNotEmpty(textToWrite);
            }
        });
    }

    private void writeToFileIfNotEmpty(String textToWrite) {
        //Если текст пустой
        if (TextUtils.isEmpty(textToWrite)) {
            //показать тост
            Toast.makeText(this, "text is empty",
                    Toast.LENGTH_SHORT).show();
        } else {
            //Иначе записать в файл
            writeToFileWithPermissionRequestIfNeeded(textToWrite);
        }
    }

    private void writeToFileWithPermissionRequestIfNeeded(String textToFile) {
        //Если есть разрешение записи
        if (isWritePermissionGranted()) {
            //Записать файл
            writeToFile(textToFile);
        } else {
            //Запросить разрешение
            requestWritePermission();
        }
    }

    //Показывает запрос на разрешение записи в файл
    private void requestWritePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            //Показать объяснение
            new AlertDialog.Builder(this)
                    .setMessage("Без разрешения невозможно записать текст в файл")
            .setPositiveButton("Понятно", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            WRITE_PERMISSION_RC);
                }
            }).show();
        } else {
            //Иначе зарпосить разрешение
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    WRITE_PERMISSION_RC);
        }
    }

    //Запись в файл
    private void writeToFile(String textToFile) {
        Toast.makeText(this, textToFile + " is write to file",
                Toast.LENGTH_SHORT).show();
    }

    //Проверяет есть ли разрешение записи в файл
    private boolean isWritePermissionGranted() {
        return ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }
    //Обработка ответа на запрос разрешения
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //Если requestCode не равет WRITE_PERMISSION_RC. выходим из метода
        if (requestCode != WRITE_PERMISSION_RC){
            return;
        }
        //Если массив grantResults пустой, то выходим из метода
        if (grantResults.length != 1) {
            return;
        }

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            String textToWrite = mInput.getText().toString();
            writeToFile(textToWrite);
        } else {
            //Если пользователь не дал разрешения, мы ему сообщаем, что он всегда может сделтаь это
            //в настройках приложения
            new AlertDialog.Builder(this)
                    .setMessage("Вы можете дать разрешение в настройках устройства в любой момент")
                    .setPositiveButton("Понятно", null).show();
        }
    }
}
