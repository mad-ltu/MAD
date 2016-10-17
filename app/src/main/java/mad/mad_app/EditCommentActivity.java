package mad.mad_app;

import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

public class EditCommentActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_CALLBACK_ID = 1516;

    private long id;
    private long parentId;
    private String parentTypeCode;

    private TextView tvComment;
    private ImageButton btnImage;

    private Button btnCancel;
    private Button btnDelete;
    private Button btnSubmit;

    private Comment content;

    private String imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_comment);

        id = getIntent().getLongExtra("ID", -1);
        parentId = getIntent().getLongExtra("PARENT_ID", -1);
        parentTypeCode = getIntent().getStringExtra("PARENT_TYPE_CODE");
        imagePath = getIntent().getStringExtra("IMAGE_PATH");

        btnCancel = (Button)findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnDelete = (Button)findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Confirm in dialog before doing delete
                // NOTE: This delete means we are deleting this comment.

                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(EditCommentActivity.this);
                alertBuilder.setTitle("Delete Comment?")
                        .setMessage("This will permanently delete this comment and cannot be undone.")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Confirmed, do delete
                                deleteComment();
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
        btnSubmit = (Button)findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveComment();
                finish();
            }
        });

        tvComment = (TextView)findViewById(R.id.editText);
        btnImage = (ImageButton)findViewById(R.id.imageButton);
        btnImage.setFocusable(false);
        btnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open image picker to return an image to us
                Intent picIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                picIntent.setType("image/*");

                startActivityForResult(picIntent, PICK_IMAGE_CALLBACK_ID);
            }
        });

        if(id != -1 && parentId != -1) {
            loadComment();
            if(content != null) {
                if(content.getComment() != null) {
                    tvComment.setText(content.getComment());
                }

                if(content.getImagePath() != null) {
                    btnImage.setImageBitmap(BitmapFactory.decodeFile(content.getImagePath()));
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case PICK_IMAGE_CALLBACK_ID:
                if(resultCode == RESULT_OK) {
                    Uri imgUri = data.getData();
                    String imgPath = getPathFromUri(imgUri);

                    Bitmap img = BitmapFactory.decodeFile(imgPath);
                    btnImage.setImageBitmap(img);
                    imagePath = imgPath;
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    private String getPathFromUri(Uri uri) {
        String result = null;

        if(uri.toString().startsWith("file://")) {
            // it's already a real filepath, just return it.
            result = uri.toString();
        } else {
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(uri, projection, null, null, null, null);
            if(cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                result = cursor.getString(column_index);
            }
            cursor.close();
        }

        return result;
    }

    private void loadComment() {
        CommentManager cm = new CommentManager(this);
        try {
            cm.open();
            content = cm.get(id);
        } catch (SQLException e) {
            Toast.makeText(this, "Error loading records from database.", Toast.LENGTH_LONG).show();
        } finally {
            cm.close();
        }
    }

    private void saveComment() {
        String comment = null;
        if(tvComment.getText() != null) {
            comment = tvComment.getText().toString().trim();
        }

        if(id != -1 && parentId != -1 && content != null) {
            // existing comment, update
            content.setComment(comment);
            content.setImagePath(imagePath);

            CommentManager cm = new CommentManager(this);
            try {
                cm.open();
                cm.update(content);
            } catch (SQLException e) {
                Toast.makeText(this, "Error saving record to database.", Toast.LENGTH_LONG).show();
            } finally {
                cm.close();
            }

        } else {
            // New comment, insert
            content = new Comment();
            content.setComment(comment);
            content.setImagePath(imagePath);
            content.setParentTypeCode(parentTypeCode);

            CommentManager cm = new CommentManager(this);
            try {
                cm.open();
                content.setParentId(parentId);
                content.setId(cm.insert(content));
            } catch (SQLException e) {
                Toast.makeText(this, "Error saving record to database.", Toast.LENGTH_LONG).show();
            } finally {
                cm.close();
            }
        }
    }

    private void deleteComment() {
        if(content != null) {
            // if content is null it's not saved in the first place
            CommentManager cm = new CommentManager(this);
            try {
                cm.open();
                cm.delete(content);
            } catch(SQLException e) {
                Toast.makeText(this, "Error deleting record from database.", Toast.LENGTH_LONG).show();
            } finally {
                cm.close();
            }
        }
    }
}
