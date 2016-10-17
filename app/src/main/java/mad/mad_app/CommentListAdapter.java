package mad.mad_app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.util.List;

/**
 * Created by Tim on 16/10/2016.
 */
public class CommentListAdapter extends ArrayAdapter<Comment> {

    public static final String TAG = CommentListAdapter.class.getSimpleName();

    public CommentListAdapter(Context context, int resource) {
        super(context, resource);
    }

    public CommentListAdapter(Context context, int resource, List<Comment> items) {
        super(context, resource, items);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.comments_list_item, null);
        }

        Comment item = getItem(position);

        ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView);
        if(item.getImagePath() != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(item.getImagePath());
            imageView.setImageBitmap(bitmap);
            imageView.setAdjustViewBounds(true);
            imageView.setVisibility(View.VISIBLE);
        } else {
            imageView.setVisibility(View.GONE);
        }

        if(item.getComment() != null) {
            TextView textView = (TextView) convertView.findViewById(R.id.textView);
            textView.setText(item.getComment());
        }

        final ViewGroup parentInner = parent;
        final Comment itemInner = item;

        ImageButton btnEdit = (ImageButton)convertView.findViewById(R.id.btnEdit);
        btnEdit.setFocusable(false);
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editCommentIntent = new Intent(parentInner.getContext(), EditCommentActivity.class);
                editCommentIntent.putExtra("ID", itemInner.getId());
                editCommentIntent.putExtra("PARENT_ID", itemInner.getParentId());
                editCommentIntent.putExtra("PARENT_TYPE_CODE", itemInner.getParentTypeCode());
                editCommentIntent.putExtra("IMAGE_PATH", itemInner.getImagePath());

                parentInner.getContext().startActivity(editCommentIntent);
            }
        });

        return convertView;
    }
}
