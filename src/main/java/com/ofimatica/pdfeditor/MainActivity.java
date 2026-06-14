package com.ofimatica.pdfeditor;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final int FILE_PICKER_REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnOpenPDF = findViewById(R.id.btn_open_pdf);
        Button btnNewPDF = findViewById(R.id.btn_new_pdf);

        // Solicitar permisos
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (ContextCompat.checkSelfPermission(this, 
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.MANAGE_EXTERNAL_STORAGE
                        },
                        PERMISSION_REQUEST_CODE);
            }
        }

        btnOpenPDF.setOnClickListener(v -> abrirPDF());
        btnNewPDF.setOnClickListener(v -> crearNuevoPDF());
    }

    private void abrirPDF() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        startActivityForResult(intent, FILE_PICKER_REQUEST_CODE);
    }

    private void crearNuevoPDF() {
        Intent intent = new Intent(this, PDFEditorActivity.class);
        intent.putExtra("mode", "new");
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == FILE_PICKER_REQUEST_CODE && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            Intent editor = new Intent(this, PDFEditorActivity.class);
            editor.putExtra("pdfUri", uri.toString());
            editor.putExtra("mode", "edit");
            startActivity(editor);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "✓ Permisos otorgados", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "✗ Se necesitan permisos para continuar", Toast.LENGTH_SHORT).show();
            }
        }
    }
}