package cn.mvp.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "TODOS".
*/
public class TodosDao extends AbstractDao<Todos, Long> {

    public static final String TABLENAME = "TODOS";

    /**
     * Properties of entity Todos.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, long.class, "id", true, "_id");
        public final static Property MatterTitle = new Property(1, String.class, "matterTitle", false, "MATTER_TITLE");
        public final static Property CreatDate = new Property(2, String.class, "creatDate", false, "CREAT_DATE");
        public final static Property ImportantLevel = new Property(3, int.class, "importantLevel", false, "IMPORTANT_LEVEL");
        public final static Property RemindTime = new Property(4, String.class, "remindTime", false, "REMIND_TIME");
        public final static Property RemindRepeat = new Property(5, String.class, "remindRepeat", false, "REMIND_REPEAT");
        public final static Property NoteContent = new Property(6, String.class, "noteContent", false, "NOTE_CONTENT");
    }


    public TodosDao(DaoConfig config) {
        super(config);
    }
    
    public TodosDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"TODOS\" (" + //
                "\"_id\" INTEGER PRIMARY KEY NOT NULL ," + // 0: id
                "\"MATTER_TITLE\" TEXT," + // 1: matterTitle
                "\"CREAT_DATE\" TEXT," + // 2: creatDate
                "\"IMPORTANT_LEVEL\" INTEGER NOT NULL ," + // 3: importantLevel
                "\"REMIND_TIME\" TEXT," + // 4: remindTime
                "\"REMIND_REPEAT\" TEXT," + // 5: remindRepeat
                "\"NOTE_CONTENT\" TEXT);"); // 6: noteContent
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"TODOS\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, Todos entity) {
        stmt.clearBindings();
        stmt.bindLong(1, entity.getId());
 
        String matterTitle = entity.getMatterTitle();
        if (matterTitle != null) {
            stmt.bindString(2, matterTitle);
        }
 
        String creatDate = entity.getCreatDate();
        if (creatDate != null) {
            stmt.bindString(3, creatDate);
        }
        stmt.bindLong(4, entity.getImportantLevel());
 
        String remindTime = entity.getRemindTime();
        if (remindTime != null) {
            stmt.bindString(5, remindTime);
        }
 
        String remindRepeat = entity.getRemindRepeat();
        if (remindRepeat != null) {
            stmt.bindString(6, remindRepeat);
        }
 
        String noteContent = entity.getNoteContent();
        if (noteContent != null) {
            stmt.bindString(7, noteContent);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, Todos entity) {
        stmt.clearBindings();
        stmt.bindLong(1, entity.getId());
 
        String matterTitle = entity.getMatterTitle();
        if (matterTitle != null) {
            stmt.bindString(2, matterTitle);
        }
 
        String creatDate = entity.getCreatDate();
        if (creatDate != null) {
            stmt.bindString(3, creatDate);
        }
        stmt.bindLong(4, entity.getImportantLevel());
 
        String remindTime = entity.getRemindTime();
        if (remindTime != null) {
            stmt.bindString(5, remindTime);
        }
 
        String remindRepeat = entity.getRemindRepeat();
        if (remindRepeat != null) {
            stmt.bindString(6, remindRepeat);
        }
 
        String noteContent = entity.getNoteContent();
        if (noteContent != null) {
            stmt.bindString(7, noteContent);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.getLong(offset + 0);
    }    

    @Override
    public Todos readEntity(Cursor cursor, int offset) {
        Todos entity = new Todos( //
            cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // matterTitle
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // creatDate
            cursor.getInt(offset + 3), // importantLevel
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // remindTime
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // remindRepeat
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6) // noteContent
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, Todos entity, int offset) {
        entity.setId(cursor.getLong(offset + 0));
        entity.setMatterTitle(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setCreatDate(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setImportantLevel(cursor.getInt(offset + 3));
        entity.setRemindTime(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setRemindRepeat(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setNoteContent(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(Todos entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(Todos entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(Todos entity) {
        throw new UnsupportedOperationException("Unsupported for entities with a non-null key");
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
