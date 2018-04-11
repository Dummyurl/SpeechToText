package info.pratham.speechtotext;

/**
 * Created by Ameya on 24-Oct-17.
 */

public class SttData {
    public String ReordId;
    public String UserID;
    public String OriginalText;
    public String VoiceText;
    public String DateTime;


    public String getReordId() {
        return ReordId;
    }

    public void setReordId(String reordId) {
        ReordId = reordId;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getOriginalText() {
        return OriginalText;
    }

    public void setOriginalText(String originalText) {
        OriginalText = originalText;
    }

    public String getVoiceText() {
        return VoiceText;
    }

    public void setVoiceText(String voiceText) {
        VoiceText = voiceText;
    }

    public String getDateTime() {
        return DateTime;
    }

    public void setDateTime(String dateTime) {
        DateTime = dateTime;
    }

}