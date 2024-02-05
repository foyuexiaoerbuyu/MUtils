package cn.mvp.mlibs.weight;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

/**
 * 圆形图片
 * <p>
 * <p/>
 * 方式一:
 * // GlideApp.with(this).load("https://www.baidu.com/img/bd_logo.png").circleCrop().into(mImageView); //普通的Imageview就行
 * // 显示圆形的 ImageView
 * GlideApp.with(this) .load(R.drawable.update_app_top_bg) .transform(new MultiTransformation<>(new CenterCrop(), new CircleCrop())) .into(mCircleView);
 * // 显示圆角的 ImageView
 * GlideApp.with(this) .load(R.drawable.update_app_top_bg) .transform(new MultiTransformation<>(new CenterCrop(), new RoundedCorners((int) getResources().getDimension(R.dimen.dp_10)))) .into(mCornerView);
 * 方式二:
 * if (hasPerms) {
 * cabinet_home_ci.setImageBitmap(BitmapFactory.decodeFile(userImg));
 * } else {
 * cabinet_home_ci.setImageDrawable(UIUtils.getDrawable(R.drawable.ic_login_sl));
 * }
 */
public class CircleImage extends androidx.appcompat.widget.AppCompatImageView {

    /**
     * 3个构造函数
     *
     * @param context
     */
    public CircleImage(Context context) {
        super(context);
    }

    public CircleImage(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CircleImage(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * 对bitmap进行裁剪成圆形
     *
     * @param bmp
     * @param radius
     * @return
     */
    public static Bitmap getCroppedBitmap(Bitmap bmp, int radius) {
        Bitmap sbmp;
        if (bmp.getWidth() != radius || bmp.getHeight() != radius)
            sbmp = Bitmap.createScaledBitmap(bmp, radius, radius, false);
        else
            sbmp = bmp;

        Bitmap output = Bitmap.createBitmap(sbmp.getWidth(), sbmp.getHeight(),
                Bitmap.Config.ARGB_8888);
        final Rect rect = new Rect(0, 0, sbmp.getWidth(), sbmp.getHeight());

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        paint.setColor(Color.parseColor("#BAB399"));

        Canvas c = new Canvas(output);
        c.drawARGB(0, 0, 0, 0);
        c.drawCircle(sbmp.getWidth() / 2 + 0.7f, sbmp.getHeight() / 2 + 0.7f,
                sbmp.getWidth() / 2 + 0.1f, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        c.drawBitmap(sbmp, rect, rect, paint);

        return output;
    }

    /**
     * 重写的ondraw方法
     */
    @Override
    protected void onDraw(Canvas canvas) {
        Drawable drawable = getDrawable();
        if (drawable == null) {
            return;
        }

        if (getWidth() == 0 || getHeight() == 0) {
            return;
        }
        Bitmap b = ((BitmapDrawable) drawable).getBitmap();
        Bitmap bitmap = b.copy(Bitmap.Config.ARGB_8888, true);

        Bitmap roundBitmap = getCroppedBitmap(bitmap, getWidth());
        canvas.drawBitmap(roundBitmap, 0, 0, null);
    }

}