package mad.mad_app;

import java.util.Date;

/**
 * Created by Tim on 25/09/2016.
 */
public class Comment {
    private Long id;
    private Long parentId;

    private Date dateTime;
    private String comment;
    private String imagePath;

    private String parentTypeCode;

    public Comment() {
        this.dateTime = new Date();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }

    public Date getDateTime() { return this.dateTime; }
    public void setDateTime(Date dateTime) { this.dateTime = dateTime; }
    public void setDateTime(Long dateTime) { this.dateTime = new Date(dateTime); }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public String getParentTypeCode() { return this.parentTypeCode; }
    public void setParentTypeCode(String parentTypeCode) { this.parentTypeCode = parentTypeCode; }
}
