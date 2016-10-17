package mad.mad_app;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.SQLException;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.MapView;

import java.util.ArrayList;
import java.util.List;

public class CommentsActivity extends AppCompatActivity {

    public static final String TAG = CommentsActivity.class.getSimpleName();

    private TextView info;
    private TextView extraInfoLeft;
    private TextView extraInfoRight;
    private MapView mapView;

    private ImageView image;
    private ImageButton newComment;
    private ImageButton deleteItem;

    private ListView commentsList;
    private CommentListAdapter adapter;

    private long parentId;
    private long id;
    private String parentTypeCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        parentId = getIntent().getLongExtra("PARENT_ID", -1);
        id = getIntent().getLongExtra("ID", -1);
        parentTypeCode = getIntent().getStringExtra("PARENT_TYPE_CODE");

        info = (TextView)findViewById(R.id.tvInfo);
        info.setText(getIntent().getStringExtra("INFO"));

        extraInfoLeft = (TextView)findViewById(R.id.tvExtraInfoLeft);
        extraInfoLeft.setText(getIntent().getStringExtra("EXTRA_INFO_LEFT"));

        extraInfoRight = (TextView)findViewById(R.id.tvExtraInfoRight);
        extraInfoRight.setText(getIntent().getStringExtra("EXTRA_INFO_RIGHT"));

        image = (ImageView)findViewById(R.id.imageView);

        commentsList = (ListView)findViewById(R.id.listView);

        List<Comment> comments = new ArrayList<>();
        loadComments(comments);
        adapter = new CommentListAdapter(this, R.layout.comments_list_item, comments);
        commentsList.setAdapter(adapter);

        newComment = (ImageButton)findViewById(R.id.btnNewComment);
        newComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newCommentIntent = new Intent(CommentsActivity.this, EditCommentActivity.class);
                newCommentIntent.putExtra("PARENT_ID", id);
                newCommentIntent.putExtra("PARENT_TYPE_CODE", parentTypeCode);

                startActivity(newCommentIntent);
            }
        });

        deleteItem = (ImageButton)findViewById(R.id.btnDelete);
        deleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Need to confirm delete in dialog before doing it
                // NOTE: This delete means we are deleting the location group or speed test we're associated with

                String typeString = parentTypeCode.equals("LOCATION_GROUP") ? "Location" : "Test";

                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(CommentsActivity.this);
                alertBuilder.setTitle("Delete " + typeString + "?")
                        .setMessage("This will permanently delete the record and all child records.")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Confirmed, do delete
                                deleteItem();
                                finish();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // cancel, do nothing
                            }
                        });
                AlertDialog dialog = alertBuilder.create();
                dialog.show();
            }
        });
    }

    @Override
    protected void onResume() {
        ArrayList<Comment> comments = new ArrayList<>();
        loadComments(comments);

        adapter.clear();
        adapter.addAll(comments);

        super.onResume();
    }

    private void loadComments(List<Comment> output) {
        CommentManager cm = new CommentManager(this);
        try {
            cm.open();
            output.addAll(cm.getAllForParent(id, parentTypeCode));
        } catch (SQLException e) {
            Toast.makeText(this, "Error loading records from database.", Toast.LENGTH_LONG).show();
        } finally {
            cm.close();
        }
    }

    private void deleteItem() {
        SpeedTestManager stm = new SpeedTestManager(this);
        CommentManager cm = new CommentManager(this);

        if(parentTypeCode.equals("LOCATION_GROUP")) {
            // Need to go through and delete all comments on the locationg group
            // as well as the speed tests and the comments on the speed test

            LocationGroupManager lgm = new LocationGroupManager(this);
            try {
                lgm.open();
                stm.open();
                cm.open();

                LocationGroup lg = lgm.get(id);
                for(SpeedTest st : stm.getAllForParent(lg.getId())) {
                    for(Comment c: cm.getAllForParent(st.getId(), "SPEED_TEST")) {
                        cm.delete(c);
                    }
                    stm.delete(st);
                }

                for(Comment c: cm.getAllForParent(lg.getId(), "LOCATION_GROUP")) {
                    cm.delete(c);
                }
                lgm.delete(lg);
            } catch(SQLException e) {
                Toast.makeText(this, "Error deleting record from database.", Toast.LENGTH_LONG).show();
            } finally {
                lgm.close();
                stm.close();
                cm.close();
            }
        } else {
            try {
                stm.open();
                cm.open();

                SpeedTest st = stm.get(id);
                for(Comment c: cm.getAllForParent(st.getId(), "SPEED_TEST")) {
                    cm.delete(c);
                }

                stm.delete(st);
            } catch(SQLException e) {
                Toast.makeText(this, "Error deleting record from database.", Toast.LENGTH_LONG).show();
            } finally {
                stm.close();
                cm.close();
            }
        }
    }
}
