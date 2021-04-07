package in.thegforest.chatting.Main.Adapter;

public class ChatModel {
    String sender, receiver, msg,type;
    String time;
    String date;
    String deleteId;

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    String msgId;

    public String getDeleteId() {
        return deleteId;
    }

    public void setDeleteId(String deleteId) {
        this.deleteId = deleteId;
    }

    public boolean isIsseen() {
        return isseen;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean getIsIsseen() {
        return isseen;
    }

    boolean isseen;

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMsg() {
        return msg;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ChatModel(String sender, String receiver, String msg, boolean isseen,
                     String time, String date, String deleteId, String msgId,String type) {
        this.deleteId=deleteId;
        this.sender = sender;
        this.receiver = receiver;
        this.msg = msg;
        this.isseen = isseen;
        this.time=time;
        this.date=date;
        this.msgId=msgId;
        this.type=type;
    }

    public ChatModel() {
    }

}
