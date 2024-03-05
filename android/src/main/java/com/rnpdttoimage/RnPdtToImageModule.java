package com.rnpdttoimage;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.module.annotations.ReactModule;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.pdf.PdfRenderer;
import android.os.ParcelFileDescriptor;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Base64;


@ReactModule(name = RnPdtToImageModule.NAME)
public class RnPdtToImageModule extends ReactContextBaseJavaModule {
  public static final String NAME = "RnPdtToImage";
  private ReactApplicationContext context;

  public RnPdtToImageModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.context = reactContext;
  }

  @Override
  @NonNull
  public String getName() {
    return NAME;
  }


  @ReactMethod
  public void onPdtToImages(String base64String, int dpi, Promise promise) {
      try {
          WritableMap map = Arguments.createMap();
          WritableArray files = Arguments.createArray();

          File cacheDir = this.context.getCacheDir();
          File file = File.createTempFile("pdfToImage", "pdf", cacheDir);
          file.setWritable(true);
          FileOutputStream fos = new FileOutputStream(file);
          byte[] decoder = Base64.getDecoder().decode(base64String);
          fos.write(decoder);

          ParcelFileDescriptor parcelFileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);

          PdfRenderer renderer = new PdfRenderer(parcelFileDescriptor);

          final int pageCount = renderer.getPageCount();

          for (int i = 0; i < pageCount; i++) {
              PdfRenderer.Page page = renderer.openPage(i);

              Bitmap bitmap = Bitmap.createBitmap(dpi, dpi * page.getHeight() / page.getWidth(), Bitmap.Config.ARGB_8888);
              Canvas canvas = new Canvas(bitmap);
              canvas.drawColor(Color.WHITE);

              page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_PRINT);
              File output = this.saveImage(bitmap, this.context.getCacheDir());
              page.close();

              files.pushString(output.getAbsolutePath());
          }

          map.putArray("files", files);

          promise.resolve(map);

          renderer.close();

          file.delete();

      } catch (Exception e) {
          promise.reject( "Error converting pdf to images", e);
      }
  }

  private File saveImage(Bitmap finalBitmap, File cacheDir) {
    File file = new File(cacheDir.getAbsolutePath() + File.separator + System.currentTimeMillis() + "_pdf.png");
    if (file.exists()) file.delete();
    try {
        FileOutputStream out = new FileOutputStream(file);
        finalBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        out.flush();
        out.close();
    } catch (Exception e) {
        e.printStackTrace();
        return null;
    }
    return file;
}
}
