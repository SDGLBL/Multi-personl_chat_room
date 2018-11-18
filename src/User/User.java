package User;

public class User {
    private String name;
    private String password;
    private String ip;
    public  User(String name,String ip){
        this.name=name;
        this.ip=ip;
       /* this.password=password;*/
    }
    public String  getName(){
        return name;
    }
    public String getIp(){
        return ip;
    }
    public void setName(String name){
        this.name=name;
    }
    public void setIp(String ip){
        this.ip=ip;
    }
    protected void changePassword(String password){
        this.password=password;
    }
}
