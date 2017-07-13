public class GifUtil {

    public static final String TAG = GifUtil.class.getSimpleName();

    private static GifUtil instance;

    static {
        System.loadLibrary("gifflen");
    }

    public GifUtil() {
    }

    public static synchronized GifUtil getInstance() {
        if(instance == null) {
            instance = new GifUtil();
        }

        return instance;
    }

    public boolean encode(String fileName, Bitmap[] bitmaps, int delay) {
        if(bitmaps != null && bitmaps.length != 0) {
            try {
                int e = bitmaps[0].getWidth();
                int height = bitmaps[0].getHeight();
                if(this.Init(fileName, e, height, 256, 100, delay) != 0) {
                    Log.e(TAG, "GifUtil init failed");
                    return false;
                } else {
                    Bitmap[] var9 = bitmaps;
                    int var8 = bitmaps.length;

                    for(int var7 = 0; var7 < var8; ++var7) {
                        Bitmap bp = var9[var7];
                        int[] pixels = new int[e * height];
                        bp.getPixels(pixels, 0, e, 0, 0, e, height);
                        this.AddFrame(pixels);
                    }

                    this.Close();
                    return true;
                }
            } catch (Exception var11) {
                Log.e(TAG, "Encode error", var11);
                return false;
            }
        } else {
            throw new NullPointerException("Bitmaps should have content!!!");
        }
    }

    public native int Init(String var1, int var2, int var3, int var4, int var5, int var6);

    public native int AddFrame(int[] var1);

    public native void Close();
}
