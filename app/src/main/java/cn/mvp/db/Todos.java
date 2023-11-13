package cn.mvp.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Speedy on 2017/6/30.
 */
@Entity
public class Todos {
    @Id
    private long id;
    private String matterTitle;//事项名称
    private String creatDate;//创建时间
    private int importantLevel;//重要等级
    private String remindTime;//提醒时间
    private String remindRepeat;//重复提醒
    private String noteContent;//备注

    @Generated(hash = 116988399)
    public Todos(long id, String matterTitle, String creatDate, int importantLevel,
            String remindTime, String remindRepeat, String noteContent) {
        this.id = id;
        this.matterTitle = matterTitle;
        this.creatDate = creatDate;
        this.importantLevel = importantLevel;
        this.remindTime = remindTime;
        this.remindRepeat = remindRepeat;
        this.noteContent = noteContent;
    }

    @Generated(hash = 650556132)
    public Todos() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMatterTitle() {
        return matterTitle;
    }

    public void setMatterTitle(String matterTitle) {
        this.matterTitle = matterTitle;
    }

    public String getCreatDate() {
        return creatDate;
    }

    public void setCreatDate(String creatDate) {
        this.creatDate = creatDate;
    }

    public int getImportantLevel() {
        return importantLevel;
    }

    public void setImportantLevel(int importantLevel) {
        this.importantLevel = importantLevel;
    }

    public String getRemindTime() {
        return remindTime;
    }

    public void setRemindTime(String remindTime) {
        this.remindTime = remindTime;
    }

    public String getRemindRepeat() {
        return remindRepeat;
    }

    public void setRemindRepeat(String remindRepeat) {
        this.remindRepeat = remindRepeat;
    }

    public String getNoteContent() {
        return noteContent;
    }

    public void setNoteContent(String noteContent) {
        this.noteContent = noteContent;
    }
}
