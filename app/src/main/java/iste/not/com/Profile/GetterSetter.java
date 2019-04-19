package iste.not.com.Profile;

public class GetterSetter
{
    String key;
    String lessonKey;
    String lessonName;
    String query;
    String seen;
    String sender;
    String senderUid;
    String title;
    String type;
    Long time;
    String userId;
    Long count;
    String getterUid;

    public GetterSetter(String key, String lessonKey, String lessonName, String query, String seen, String sender, String senderUid, String title, String type, Long time, String userId, Long count, String getterUid) {
        this.key = key;
        this.lessonKey = lessonKey;
        this.lessonName = lessonName;
        this.query = query;
        this.seen = seen;
        this.sender = sender;
        this.senderUid = senderUid;
        this.title = title;
        this.type = type;
        this.time = time;
        this.userId = userId;
        this.count = count;
        this.getterUid = getterUid;
    }

    public String getGetterUid() {
        return getterUid;
    }

    public void setGetterUid(String getterUid) {
        this.getterUid = getterUid;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public GetterSetter() {
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getLessonKey() {
        return lessonKey;
    }

    public void setLessonKey(String lessonKey) {
        this.lessonKey = lessonKey;
    }

    public String getLessonName() {
        return lessonName;
    }

    public void setLessonName(String lessonName) {
        this.lessonName = lessonName;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getSeen() {
        return seen;
    }

    public void setSeen(String seen) {
        this.seen = seen;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getSenderUid() {
        return senderUid;
    }

    public void setSenderUid(String senderUid) {
        this.senderUid = senderUid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
