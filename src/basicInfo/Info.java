package basicInfo;

public class Info { //信息传递类
    private String ip;
    private String port;
    private String name;
    public Info(String ip,String port,String name){
        this.ip=ip;
        this.name=name;
        this.port=port;
    }
    public String getIp(){
        return ip;
    }
    public String getPort(){
        return port;
    }
    public String getName(){
        return name;
    }
}
