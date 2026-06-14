# 📄 PDF Editor - Aplicación Ofimática

Aplicación Android para editar PDF y convertir a Word **completamente offline**.

## ✨ Características

✅ **COMPLETAMENTE OFFLINE** - Sin conexión a internet  
✅ Dibujar/Escribir en PDF  
✅ Agregar texto con teclado  
✅ Insertar imágenes  
✅ Eliminar páginas  
✅ Crear nuevo PDF desde cero  
✅ **Convertir a WORD (.docx)**  
✅ Guardar en carpeta de Documentos  
✅ Compatible Android 5.0+  
✅ Interfaz simple y fácil de usar  

## 🛠️ Requisitos

- Android Studio 4.0+
- JDK 11+
- Android SDK 21+ (Mínimo API 21)

## 📦 Instalación

1. **Clonar repositorio**
```bash
git clone https://github.com/DOUintenso/pdf-editor-app.git
cd pdf-editor-app
```

2. **Abrir en Android Studio**
   - File → Open → Seleccionar carpeta del proyecto

3. **Compilar APK**
   - Build → Build APK (o Build Bundle/APK)
   - El APK se generará en `app/release/app-release.apk`

4. **Instalar en dispositivo**
   ```bash
   adb install app/release/app-release.apk
   ```

## 🎮 Uso

### Pantalla Principal
- **📂 Abrir PDF** - Selecciona un PDF del dispositivo
- **✏️ Crear Nuevo PDF** - Inicia un PDF en blanco

### Editor
- **✏️ Dibujar** - Modo libre para dibujar con el dedo
- **📝 Texto** - Agrega texto a la página
- **🖼️ Imagen** - Inserta imágenes desde la galería
- **🗑️ Eliminar** - Borra la página actual
- **◄ Anterior / Siguiente ►** - Navega entre páginas
- **💾 PDF** - Guarda como PDF
- **📄 Word** - Exporta como documento Word (.docx)

## 📁 Estructura del Proyecto

```
pdf-editor-app/
├── src/main/
│   ├── java/com/ofimatica/pdfeditor/
│   │   ├── MainActivity.java
│   │   ├── PDFEditorActivity.java
│   │   └── DrawingView.java
│   └── res/
│       ├── layout/
│       │   ├── activity_main.xml
│       │   └── activity_editor.xml
│       └── values/
│           ├── strings.xml
│           └── styles.xml
├── build.gradle
├── AndroidManifest.xml
└── proguard-rules.pro
```

## 📚 Dependencias

- **PDFBox** - Manipulación de archivos PDF
- **Apache POI** - Creación de documentos Word
- **Android Support Library** - Compatibilidad

## 📄 Licencia

MIT License - Libre para usar y modificar

## 👨‍💻 Autor

DOUintenso

## 🤝 Contribuciones

Las contribuciones son bienvenidas. Por favor abre un Issue o Pull Request.
