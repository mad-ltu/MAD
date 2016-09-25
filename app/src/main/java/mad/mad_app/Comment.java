package mad.mad_app;

/**
 * Created by Tim on 25/09/2016.
 */
public class Comment {
    private Long id;
    private Long parentId;

    private String comment;
    private byte[] imageData;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public byte[] getImageData() { return imageData; }
    public void setImageData(byte[] imageData) { this.imageData = imageData; }
}
