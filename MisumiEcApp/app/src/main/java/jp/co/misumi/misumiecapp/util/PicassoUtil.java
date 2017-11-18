package jp.co.misumi.misumiecapp.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.os.Build;

import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import java.util.concurrent.TimeUnit;

import jp.co.misumi.misumiecapp.AppConst;
import jp.co.misumi.misumiecapp.BuildConfig;
import jp.co.misumi.misumiecapp.R;

/**
 * PicassoUtil
 */
public class PicassoUtil {

    public static final int PICASSO_LOAD_KEY = R.string.picasso_load_key;

//    public static final int PROGRESS_SMALL = R.drawable.progress_animation;
//    public static final int ERROR_DRAWABLE_ID = R.drawable.noimage_back60_60;
//    public static final int ERROR_DRAWABLE_FONT = R.drawable.noimage_60_60;
    public static final Picasso.Priority PICASSO_PRIORITY = Picasso.Priority.NORMAL;

	private static Context sContext;
	private static Picasso sPicasso;
	private static Drawable sDrawableNo;
	private static Drawable sDrawableBg;

	/**
	 * PicassoUtil
	 */
	public PicassoUtil() {
	}

	/**
	 * initialize
	 * @param context
	 */
	public static void initialize(final Context context) {

		sContext	= context;

		sPicasso = new Picasso.Builder(context)
				.downloader(new MyOkHttpDownloader(context))
				.listener(new Picasso.Listener() {
					@Override
					public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
						//AppLog.d(exception.toString());
//						AppLog.e("onImageLoadFailed: "+ uri.toString() + exception.toString());
					}
				})
				.build();

    	sDrawableNo = getDrawableResource(context, R.drawable.noimage_200_200);
    	sDrawableBg = getDrawableResource(context, R.drawable.noimage_back200_200);

		if (BuildConfig.DebugMode) {
//			sPicasso.setIndicatorsEnabled(true);
		}
	}


	/**
	 * PicassoLoad
	 * @param context
	 * @param imageView
	 * @param imageUrl
	 */
    private static void PicassoLoad(final Context context, final ImageView imageView, final String imageUrl) {

        PicassoLoad(context, imageView, null, imageUrl);
    }



	/**
	 * PicassoLoad
	 * @param context
	 * @param imageView
	 * @param progressView
	 * @param imageUrl
	 */
    private static void PicassoLoad(final Context context, final ImageView imageView, final View progressView, String imageUrl) {

        sContext = context;

		if (imageView == null) {
			return;
		}

		//リストで再利用時に前の画像がチラ見えするのでブランクを設定する
		imageView.setImageDrawable(null);
        imageView.setBackgroundDrawable(null);

		//読み込み完了識別タグ
		imageView.setTag(PICASSO_LOAD_KEY, Boolean.FALSE);

		if (imageUrl == null || imageUrl.isEmpty()) {

			//ListViewで高速でスクロールを繰り貸すと商品画像の裏にＮｏＩｍａｇｅ画像が表示されてしまう対応
			imageUrl = ".";
/*
			//ここで null処理をすると Picasso側が感知しないので無くす
			if (progressView != null) {
				progressView.setVisibility(View.GONE);
			}

//			imageView.setImageResource(R.mipmap.noimage_back80_80);
            imageView.setBackgroundResource(R.drawable.noimage_back200_200);
            imageView.setImageResource(R.drawable.noimage_200_200);

			return;
*/
		}

		if (progressView != null) {
			progressView.setVisibility(View.VISIBLE);
		}

		final String imageUrlF = imageUrl;

		//AppLog.d("load: "+ imageUrlF);

        imageView.setBackgroundColor(Color.WHITE);
		sPicasso	//.with(context)
                .load(imageUrlF)
                .priority(PICASSO_PRIORITY)
//                .error()
                .noPlaceholder()
                .skipMemoryCache()
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        //AppLog.d("onSuccess");
                        //AppLog.d("onSuccess: " + imageUrlF);

						//読み込み完了識別タグ
						imageView.setTag(PICASSO_LOAD_KEY, Boolean.TRUE);

                        setProgressGone();
                    }

                    @Override
                    public void onError() {
                        //AppLog.d("onError");
//                        AppLog.e("onError: " + imageUrlF);
//					imageView.setImageResource(R.mipmap.noimage);
                        imageView.setBackgroundDrawable(sDrawableBg);
                        imageView.setImageDrawable(sDrawableNo);

                        setProgressGone();
                    }

                    private void setProgressGone() {

                        if (progressView != null) {
                            progressView.setVisibility(View.GONE);
                        }
                    }
                });
    }

	//-- ADD NT-LWL 16/11/28 AliPay Payment FR -
	public static void PicassoLoadCategory(final ImageView imageView, final View progressView, final String imageUrl){

		PicassoLoadCategory(sContext, imageView, progressView, imageUrl);
	}
	/**
	 * @param context
	 * @param imageView
	 * @param progressView
	 * @param imageUrl
	 */
	private static void PicassoLoadCategory(final Context context, final ImageView imageView, final View progressView, String imageUrl) {

		sContext = context;

		if (imageView == null) {
			return;
		}

		//リストで再利用時に前の画像がチラ見えするのでブランクを設定する
		imageView.setImageDrawable(null);
		imageView.setBackgroundDrawable(null);

		//読み込み完了識別タグ
		imageView.setTag(PICASSO_LOAD_KEY, Boolean.FALSE);

		if (imageUrl == null || imageUrl.isEmpty()) {

			//ListViewで高速でスクロールを繰り貸すと商品画像の裏にＮｏＩｍａｇｅ画像が表示されてしまう対応
			imageUrl = ".";
		}

		if (progressView != null) {
			progressView.setVisibility(View.VISIBLE);
		}

		final String imageUrlF = imageUrl;


		imageView.setBackgroundColor(Color.WHITE);
		sPicasso	//.with(context)
				.load(imageUrlF)
				.priority(PICASSO_PRIORITY)
//                .error()
				.noPlaceholder()
				.skipMemoryCache()
				.into(imageView, new Callback() {
					@Override
					public void onSuccess() {
						//AppLog.d("onSuccess");
						//AppLog.d("onSuccess: " + imageUrlF);

						//読み込み完了識別タグ
						imageView.setTag(PICASSO_LOAD_KEY, Boolean.TRUE);
						imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
						setProgressGone();
					}

					@Override
					public void onError() {
						//AppLog.d("onError");
//                        AppLog.e("onError: " + imageUrlF);
//					imageView.setImageResource(R.mipmap.noimage);
						imageView.setBackgroundDrawable(sDrawableBg);
						imageView.setImageDrawable(getDrawableResource(context, R.drawable.noimage_category));
						imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

						setProgressGone();
					}

					private void setProgressGone() {

						if (progressView != null) {
							progressView.setVisibility(View.GONE);
						}
					}
				});
	}
	//-- ADD NT-LWL 16/11/28 AliPay Payment TO -

	/**
	 * PicassoLoad
	 * @param imageView
	 * @param progressView
	 * @param imageUrl
	 * @param roundedTransformation
	 */
	public static void PicassoLoadForCalendar(final ImageView imageView, final View progressView, String imageUrl, RoundedTransformation roundedTransformation) {

		if (imageView == null) {
			return;
		}

		if (imageUrl == null || imageUrl.isEmpty()) {

			imageUrl = ".";
		}

		imageView.setImageDrawable(null);
        imageView.setBackgroundDrawable(null);

		if (progressView != null) {
			progressView.setVisibility(View.VISIBLE);
		}

//		AppLog.d("load: "+ imageUrl);

		//TODO:高速でスクロールしたとき背景画像が出現するバグがある
        //imageView.setBackgroundColor(Color.WHITE);
		sPicasso	//.with(context)
				.load(imageUrl)
				.memoryPolicy(MemoryPolicy.NO_CACHE)
				.priority(PICASSO_PRIORITY)
				.noPlaceholder().transform(roundedTransformation)
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        //AppLog.d("onSuccess");
//						AppLog.d("onSuccess: " + imageUrl);

                        setProgressGone();
                    }

                    @Override
                    public void onError() {
                        //AppLog.d("onError");
//                        AppLog.e("onError: " + imageUrl);
//					imageView.setImageResource(R.mipmap.noimage);
                        setProgressGone();
                    }

                    private void setProgressGone() {

                        if (progressView != null) {
                            progressView.setVisibility(View.GONE);
                        }
                    }
                });
	}

	/**
	 * PicassoLoad
	 * @param imageView
	 * @param imageUrl
	 */
    public static void PicassoLoad(final ImageView imageView, final String imageUrl) {
		PicassoLoad(sContext, imageView, imageUrl);
    }

    public static void PicassoLoad(final ImageView imageView, final View progressView, final String imageUrl) {
		PicassoLoad(sContext, imageView, progressView, imageUrl);
    }


    private static class MyOkHttpDownloader extends OkHttpDownloader {

		public MyOkHttpDownloader(final Context context) {
			super(context);

			//タイムアウトを設定
			getClient().setConnectTimeout(AppConst.ConnectTimeout, TimeUnit.MILLISECONDS);
			getClient().setReadTimeout(AppConst.ConnectTimeout, TimeUnit.MILLISECONDS);
		}
	}

	public class RoundedTransformation implements com.squareup.picasso.Transformation {
		private final int radius;
		private final int margin;  // dp

		// radius is corner radii in dp
		// margin is the board in dp
		public RoundedTransformation(final int radius, final int margin) {
			this.radius = radius;
			this.margin = margin;
		}

		@Override
		public Bitmap transform(final Bitmap source) {
			final Paint paint = new Paint();
			paint.setAntiAlias(true);
			paint.setShader(new BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));

			Bitmap output = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
			Canvas canvas = new Canvas(output);
			canvas.drawRoundRect(new RectF(margin, margin, source.getWidth() - margin, source.getHeight() - margin), radius, radius, paint);

			if (source != output) {
				source.recycle();
			}

			return output;
		}

		@Override
		public String key() {
			return "rounded(radius=" + Integer.toString(radius) + ", margin=" + Integer.toString(margin) + ")";
		}
	}


    @SuppressWarnings("deprecation")
    public static Drawable getDrawableResource(Context context, int id){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            return context.getDrawable(id);
        }
        else{
            return context.getResources().getDrawable(id);
        }
    }
}

