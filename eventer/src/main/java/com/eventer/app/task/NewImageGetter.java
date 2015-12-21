package com.eventer.app.task;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.Html;
import android.widget.TextView;

import com.eventer.app.Constant;
import com.eventer.app.R;
import com.eventer.app.util.FileUtil;

import java.io.InputStream;
import java.net.URL;

/**
 * Created by LiuNana on 2015/12/17.
 * for event detail
 */
public class NewImageGetter implements Html.ImageGetter {

    FileUtil fileUtil;
    private Context context;

    private TextView tv;


    public NewImageGetter(Context context, TextView tv) {

        this.context = context;
        fileUtil = new FileUtil(context, Constant.IMAGE_PATH);
        this.tv = tv;

    }


    @Override

    public Drawable getDrawable(String source) {

        InputStream is;
        String filename = source
                .substring(source.lastIndexOf("/") + 1) + "_e";
        String filepath = fileUtil.getAbsolutePath() + filename;
        Drawable d=Drawable.createFromPath(filepath);
        if (d != null) {
            return new URLDrawable(d);
        }

        Resources res = context.getResources();

        URLDrawable drawable = new URLDrawable(res.getDrawable(R.drawable.ic_launcher));

        new ImageAsync(drawable).execute(filepath, source);

        return drawable;

    }


    private class ImageAsync extends AsyncTask<String, Integer, Drawable> {


        private URLDrawable drawable;


        public ImageAsync(URLDrawable drawable) {

            this.drawable = drawable;

        }


        @Override

        protected Drawable doInBackground(String... params) {
            String filename = params[0];
            String url = params[1];
            InputStream is=null;
            Bitmap bitmap;
            Drawable d;

            try {

                is = (InputStream) new URL(url).getContent();
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = false;
                options.inSampleSize = 1; // width，hight设为原来的十分一

                bitmap = BitmapFactory.decodeStream(is,
                        null, options);

                fileUtil.saveBitmap(filename, bitmap);

                is.close();
                d = new BitmapDrawable(context.getResources(), bitmap);
                return d;

            } catch (Exception e) {

                try {
                    if (is!=null)
                        is.close();
                } catch (Exception e2) {
                    e.printStackTrace();
                }

            }


            return null;

        }


        @Override

        protected void onPostExecute(Drawable result) {
            // TODO Auto-generated method stub

            super.onPostExecute(result);
            if (result != null) {
                drawable.setDrawable(result);
                tv.setText(tv.getText()); // 更新UI

            }

        }


    }
    public class URLDrawable extends BitmapDrawable {



        private Drawable drawable;



        public URLDrawable(Drawable defaultDraw) {

            setDrawable(defaultDraw);

        }



        private void setDrawable(Drawable nDrawable) {

            drawable = nDrawable;

            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());

            setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());

        }



        @Override

        public void draw(Canvas canvas) {

            drawable.draw(canvas);

        }



    }


}