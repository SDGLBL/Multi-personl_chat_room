package Client;

public class transformInfo implements  java.io.Serializable {//信息包装类
    private static final long serialVersionUID = 1L;
    private String sendUserName;
    private String getUserName;
    private String passwordHash;
    private String messeage;
    private String command;
    public transformInfo(String sendUserName,String getUserName,String passwordHash,String messeage,String command){
        this.sendUserName=sendUserName;
        this.getUserName=getUserName;
        this.passwordHash=passwordHash;
        this.messeage=messeage;
        this.command=command;
    }
    public String getSendUserName() {
        return sendUserName;
    }
    public String getGetUserName() {
        return getUserName;
    }
    public String getCommand() {
        return command;
    }
    public String getPasswordHash() {
        return passwordHash;
    }
    public String getMesseage() {
        return messeage;
    }
}