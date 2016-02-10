package com.alvinhx.endlessImageGridView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by solor on 2016-02-09.
 */
public class ExtendedSimpleAdapter extends SimpleAdapter{
    List<HashMap<String, Object>> map;
    String[] from;
    int layout;
    int[] to;
    Context context;
    LayoutInflater mInflater;
    public ExtendedSimpleAdapter(Context context, List<HashMap<String, Object>> data,
                                 int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);
        layout = resource;
        map = data;
        this.from = from;
        this.to = to;
        this.context = context;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return this.createViewFromResource(position, convertView, parent, layout);
    }

    private View createViewFromResource(int position, View convertView,
                                        ViewGroup parent, int resource) {
        View v;
        ViewHolder holder;

        if (convertView == null) {
            v = mInflater.inflate(resource, parent, false);
            holder = new ViewHolder();
            holder.name = (TextView)v.findViewById(R.id.title);
            holder.icon = (ImageView)v.findViewById(R.id.img);
            v.setTag(holder);
        } else {
            v = convertView;
            holder = (ViewHolder)v.getTag();
        }
        this.bindView(position, v);

        return v;
    }



    private void bindView(int position, View view) {
        final Map dataSet = map.get(position);
        if (dataSet == null) {
            return;
        }

        final ViewBinder binder = super.getViewBinder();
        final int count = to.length;

        for (int i = 0; i < count; i++) {
            final View v = view.findViewById(to[i]);
            if (v != null) {
                final Object data = dataSet.get(from[i]);
                String text = data == null ? "" : data.toString();
                if (text == null) {
                    text = "";
                }

                boolean bound = false;
                if (binder != null) {
                    bound = binder.setViewValue(v, data, text);
                }

                if (!bound) {
                    if (v instanceof Checkable) {
                        if (data instanceof Boolean) {
                            ((Checkable) v).setChecked((Boolean) data);
                        } else if (v instanceof TextView) {
                            // Note: keep the instanceof TextView check at the bottom of these
                            // ifs since a lot of views are TextViews (e.g. CheckBoxes).
                            setViewText((TextView) v, text);
                        } else {
                            throw new IllegalStateException(v.getClass().getName() +
                                    " should be bound to a Boolean, not a " +
                                    (data == null ? "<unknown type>" : data.getClass()));
                        }
                    } else if (v instanceof TextView) {
                        // Note: keep the instanceof TextView check at the bottom of these
                        // ifs since a lot of views are TextViews (e.g. CheckBoxes).
                        setViewText((TextView) v, text);
                    } else if (v instanceof ImageView) {
                        if (data instanceof Integer) {
                            setViewImage((ImageView) v, (Integer) data);
                        } else if (data instanceof Bitmap){
                            setViewImage((ImageView) v, (Bitmap)data);
                        } else if (data instanceof Drawable){
                            setViewImage((ImageView) v, (Drawable) data);
                        } else if (data instanceof String){
                            Picasso.with(context).load((String) data).placeholder(R.drawable.logo500px_2).into((ImageView)v);
                        }else{
                            setViewImage((ImageView) v, text);
                        }
                    } else {
                        throw new IllegalStateException(v.getClass().getName() + " is not a " +
                                " view that can be bounds by this SimpleAdapter");
                    }
                }
            }
        }
    }

    static class ViewHolder{
        TextView name;
        ImageView icon;
    }


    private void setViewImage(ImageView v, Bitmap bmp){
        v.setImageBitmap(bmp);
    }

    private void setViewImage(ImageView v, Drawable db){
        v.setImageDrawable(db);
    }

    private int getDpfromPixel(int dp){
        final float scale = context.getResources().getDisplayMetrics().density;
        int pixels = (int) (dp * scale + 0.5f);
        return pixels;
    }


}