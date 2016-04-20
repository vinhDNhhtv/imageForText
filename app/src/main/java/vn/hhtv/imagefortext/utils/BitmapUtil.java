package vn.hhtv.imagefortext.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;

/**
 * Created by vinhdn on 4/15/16.
 */
public class BitmapUtil {
    public Bitmap mergeTextToImage(Bitmap cropBitmapImage, Bitmap cropBitmapText, int type) {
        int bitmap1Width = cropBitmapImage.getWidth();
        int bitmap1Height = cropBitmapImage.getHeight();
        Bitmap overlayBitmap = Bitmap.createBitmap(bitmap1Width, bitmap1Height, cropBitmapImage.getConfig());
        int bitmap2Width = cropBitmapText.getWidth();
        int bitmap2Height = cropBitmapText.getHeight();
        Canvas canvas = new Canvas(overlayBitmap);
        canvas.drawBitmap(cropBitmapImage, new Matrix(), null);
        int pLeft = 0, pTop = 0;
        switch (type) {
            case 0: // Center Center
                pLeft = bitmap1Width / 2 - bitmap2Width / 2;
                pTop = bitmap1Height / 2 - bitmap2Height / 2;
                break;
            case 1: // Top Left
                pLeft = 0;
                pTop = 0;
                break;
            case 2: // Top Right
                pTop = 0;
                pLeft = bitmap1Width - bitmap2Width;
                break;
            case 3: // Top Center
                pTop = 0;
                pLeft = bitmap1Width / 2 - bitmap2Width / 2;
                break;
            case 4: // Center Left
                pTop = bitmap1Height / 2 - bitmap2Height / 2;
                pLeft = 0;
                break;
            case 5: // Center Right
                pTop = bitmap1Height / 2 - bitmap2Height / 2;
                pLeft = bitmap1Width - bitmap2Width;
                break;
            case 6: // Bottom Left
                pTop = bitmap1Height / 2 - bitmap2Height;
                pLeft = 0;
                break;
            case 7: // Bottom Center
                pTop = bitmap1Height / 2 - bitmap2Height;
                pLeft = bitmap1Width / 2 - bitmap2Width / 2;
                break;
            case 8: // Bottom Right
                pTop = bitmap1Height / 2 - bitmap2Height;
                pLeft = bitmap1Width / 2 - bitmap2Width;
                break;
            case 9:
                break;
        }

        canvas.drawBitmap(cropBitmapText, pLeft, pTop, null);
        return overlayBitmap;
    }
}
