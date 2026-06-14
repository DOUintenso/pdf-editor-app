package com.ofimatica.pdfeditor;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.pdmodel.PDPage;
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream;
import com.tom_roush.pdfbox.pdmodel.graphics.image.PDImageXObject;
import com.tom_roush.pdfbox.rendering.PDFRenderer;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PDFEditorActivity extends AppCompatActivity {

    private PDDocument pdfDocument;
    private List<Bitmap> pageBitmaps;
    private LinearLayout pageContainer;
    private DrawingView currentDrawingView;
    private int currentPageIndex = 0;
    private Paint textPaint;
    private TextView pageCounter;
    private static final int PICK_IMAGE_REQUEST = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        initializeUI();
        loadPDF();
    }

    private void initializeUI() {
        pageContainer = findViewById(R.id.page_container);
        pageCounter = findViewById(R.id.page_counter);
        
        Button btnDraw = findViewById(R.id.btn_draw);
        Button btnAddText = findViewById(R.id.btn_add_text);
        Button btnDeletePage = findViewById(R.id.btn_delete_page);
        Button btnAddImage = findViewById(R.id.btn_add_image);
        Button btnSave = findViewById(R.id.btn_save);
        Button btnSaveWord = findViewById(R.id.btn_save_word);
        Button btnPrevPage = findViewById(R.id.btn_prev_page);
        Button btnNextPage = findViewById(R.id.btn_next_page);

        btnDraw.setOnClickListener(v -> enableDrawing());
        btnAddText.setOnClickListener(v -> addText());
        btnDeletePage.setOnClickListener(v -> deletePage());
        btnAddImage.setOnClickListener(v -> addImage());
        btnSave.setOnClickListener(v -> savePDF());
        btnSaveWord.setOnClickListener(v -> saveAsWord());
        btnPrevPage.setOnClickListener(v -> previousPage());
        btnNextPage.setOnClickListener(v -> nextPage());

        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(40);
        textPaint.setAntiAlias(true);
    }

    private void loadPDF() {
        String mode = getIntent().getStringExtra("mode");
        
        if ("new".equals(mode)) {
            crearNuevoPDF();
        } else {
            String pdfUri = getIntent().getStringExtra("pdfUri");
            cargarPDF(pdfUri);
        }
    }

    private void crearNuevoPDF() {
        pdfDocument = new PDDocument();
        PDPage page = new PDPage();
        pdfDocument.addPage(page);
        
        pageBitmaps = new ArrayList<>();
        Bitmap blank = Bitmap.createBitmap(600, 800, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(blank);
        canvas.drawColor(Color.WHITE);
        pageBitmaps.add(blank);
        
        mostrarPagina(0);
    }

    private void cargarPDF(String uri) {
        try {
            pdfDocument = PDDocument.load(new File(uri));
            PDFRenderer renderer = new PDFRenderer(pdfDocument);
            pageBitmaps = new ArrayList<>();
            
            for (int i = 0; i < pdfDocument.getNumberOfPages(); i++) {
                Bitmap bitmap = renderer.renderImage(i, 2);
                pageBitmaps.add(bitmap);
            }
            
            mostrarPagina(0);
        } catch (IOException e) {
            Toast.makeText(this, "✗ Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void mostrarPagina(int pageIndex) {
        if (pageIndex >= 0 && pageIndex < pageBitmaps.size()) {
            currentPageIndex = pageIndex;
            pageContainer.removeAllViews();
            
            currentDrawingView = new DrawingView(this, pageBitmaps.get(pageIndex));
            pageContainer.addView(currentDrawingView);
            
            pageCounter.setText("Página " + (pageIndex + 1) + " de " + pageBitmaps.size());
        }
    }

    private void enableDrawing() {
        if (currentDrawingView != null) {
            currentDrawingView.enableDrawing();
            Toast.makeText(this, "✏️ Modo dibujo activado", Toast.LENGTH_SHORT).show();
        }
    }

    private void addText() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Agregar Texto");
        
        final EditText input = new EditText(this);
        input.setHint("Escriba el texto aquí");
        input.setPadding(20, 20, 20, 20);
        builder.setView(input);
        
        builder.setPositiveButton("✓ Agregar", (dialog, which) -> {
            String text = input.getText().toString();
            if (!text.isEmpty() && currentDrawingView != null) {
                currentDrawingView.addText(text, textPaint);
                Toast.makeText(this, "✓ Texto agregado", Toast.LENGTH_SHORT).show();
            }
        });
        
        builder.setNegativeButton("✗ Cancelar", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void addImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            if (currentDrawingView != null) {
                currentDrawingView.addImage(this, imageUri);
                Toast.makeText(this, "🖼️ Imagen agregada", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void deletePage() {
        if (pageBitmaps.size() <= 1) {
            Toast.makeText(this, "⚠️ No puede eliminar la última página", Toast.LENGTH_SHORT).show();
            return;
        }
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Eliminar Página");
        builder.setMessage("¿Está seguro que desea eliminar esta página?");
        builder.setPositiveButton("✓ Sí", (dialog, which) -> {
            pageBitmaps.remove(currentPageIndex);
            pdfDocument.removePage(currentPageIndex);
            
            if (currentPageIndex >= pageBitmaps.size()) {
                currentPageIndex = pageBitmaps.size() - 1;
            }
            mostrarPagina(currentPageIndex);
            Toast.makeText(this, "✓ Página eliminada", Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("✗ No", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void previousPage() {
        if (currentPageIndex > 0) {
            mostrarPagina(currentPageIndex - 1);
        } else {
            Toast.makeText(this, "⚠️ Primera página", Toast.LENGTH_SHORT).show();
        }
    }

    private void nextPage() {
        if (currentPageIndex < pageBitmaps.size() - 1) {
            mostrarPagina(currentPageIndex + 1);
        } else {
            Toast.makeText(this, "⚠️ Última página", Toast.LENGTH_SHORT).show();
        }
    }

    private void savePDF() {
        try {
            File documentsDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
                "PDFEditor");
            if (!documentsDir.exists()) {
                documentsDir.mkdirs();
            }

            File outputFile = new File(documentsDir, 
                    "PDF_" + System.currentTimeMillis() + ".pdf");
            
            PDDocument newPdf = new PDDocument();
            for (Bitmap bitmap : pageBitmaps) {
                PDPage page = new PDPage();
                newPdf.addPage(page);
                
                File tempImage = new File(getCacheDir(), "temp_" + System.nanoTime() + ".png");
                FileOutputStream fos = new FileOutputStream(tempImage);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.close();
                
                PDImageXObject pdImage = PDImageXObject.createFromFile(
                        tempImage.getAbsolutePath(), newPdf);
                PDPageContentStream contentStream = new PDPageContentStream(newPdf, page);
                contentStream.drawImage(pdImage, 0, 0, 
                        page.getMediaBox().getWidth(), 
                        page.getMediaBox().getHeight());
                contentStream.close();
                
                tempImage.delete();
            }
            
            newPdf.save(outputFile);
            newPdf.close();
            
            Toast.makeText(this, "✓ PDF guardado\n" + outputFile.getAbsolutePath(), 
                    Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(this, "✗ Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void saveAsWord() {
        try {
            File documentsDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
                "PDFEditor");
            if (!documentsDir.exists()) {
                documentsDir.mkdirs();
            }

            File outputFile = new File(documentsDir, 
                    "Documento_" + System.currentTimeMillis() + ".docx");
            
            XWPFDocument document = new XWPFDocument();
            
            for (int i = 0; i < pageBitmaps.size(); i++) {
                // Título de página
                XWPFParagraph titleParagraph = document.createParagraph();
                XWPFRun titleRun = titleParagraph.createRun();
                titleRun.setText("═══ Página " + (i + 1) + " ═══");
                titleRun.setBold(true);
                titleRun.setFontSize(14);
                
                // Imagen de página
                Bitmap bitmap = pageBitmaps.get(i);
                File tempImage = new File(getCacheDir(), "page_" + i + "_" + System.nanoTime() + ".png");
                FileOutputStream fos = new FileOutputStream(tempImage);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.close();
                
                XWPFParagraph imageParagraph = document.createParagraph();
                XWPFRun imageRun = imageParagraph.createRun();
                imageRun.addPicture(new java.io.FileInputStream(tempImage), 
                        XWPFDocument.PICTURE_TYPE_PNG, 
                        tempImage.getName(), 5000000, 5000000);
                
                // Espaciador
                if (i < pageBitmaps.size() - 1) {
                    XWPFParagraph spacer = document.createParagraph();
                    spacer.setText("\n");
                }
                
                tempImage.delete();
            }
            
            FileOutputStream fileOut = new FileOutputStream(outputFile);
            document.write(fileOut);
            fileOut.close();
            document.close();
            
            Toast.makeText(this, "✓ Word guardado\n" + outputFile.getAbsolutePath(), 
                    Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, "✗ Error Word: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}