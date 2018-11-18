package Client;

import Server.creatServer;
import User.User;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class creatClient {
    private JFrame frame;
    private JTextArea contentArea;
    private JTextField txt_mes;
    private JTextField txt_ip;
    private JTextField txt_port;
    private JTextField txt_name;
    private JButton btn_start;
    private JButton btn_stop;
    private JButton btn_send;
    private JPanel upjpanel;
    private JPanel downjpanel;
    private JScrollPane rpane;
    private JScrollPane lpanel;
    private JSplitPane centerSplit;
    private JList userlist;
    private DefaultListModel listModel;
    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;
    private MessageThread messageThread;
    private boolean isConnected = false;
    private Map<String,User> onlineUsers=new HashMap<String, User>();

    public static void main(String[] args) {
        new creatClient();
    }

    public creatClient() {//构造函数
        frame = new JFrame("Client");
        contentArea = new JTextArea();
        contentArea.setEditable(false);
        txt_ip = new JTextField("127.0.0.1");
        txt_name = new JTextField("xiaohua");
        txt_mes = new JTextField();
        txt_port = new JTextField("10086");
        btn_send = new JButton("Send");
        btn_start = new JButton("Link");
        btn_stop = new JButton("Stop");
        listModel = new DefaultListModel();
        userlist = new JList(listModel);
        lpanel = new JScrollPane(userlist);
        lpanel.setBorder(new TitledBorder("Online users"));
        rpane = new JScrollPane(contentArea);
        rpane.setBorder(new TitledBorder("Content area"));
        centerSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, lpanel, rpane);
        centerSplit.setDividerLocation(100);
        downjpanel = new JPanel(new BorderLayout());
        isConnected = false;
        downjpanel.setBorder(new TitledBorder("Input messseage"));
        downjpanel.add(txt_mes, "Center");
        downjpanel.add(btn_send, "East");
        upjpanel = new JPanel();
        upjpanel.setLayout(new GridLayout(1, 6));
        upjpanel.add(new JLabel("IP"));
        upjpanel.add(txt_ip);
        upjpanel.add(new JLabel("Port"));
        upjpanel.add(txt_port);
        upjpanel.add(btn_start);
        upjpanel.add(btn_stop);
        upjpanel.setBorder(new TitledBorder("Connection info"));
        frame.setLayout(new BorderLayout());
        frame.add(upjpanel, "North");
        frame.add(downjpanel, "South");
        frame.add(centerSplit, "Center");
        frame.setSize(500, 400);
        int screen_width = Toolkit.getDefaultToolkit().getScreenSize().width;
        int screen_height = Toolkit.getDefaultToolkit().getScreenSize().height;
        frame.setLocation((screen_width - frame.getWidth()) / 2,
                (screen_height - frame.getHeight()) / 2);
        frame.setVisible(true);
        btn_send.addActionListener((new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                send();
            }
        }));
        btn_start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int port;
                if (isConnected) {
                    JOptionPane.showMessageDialog(frame, "已处于连接上状态，不要重复连接!",
                            "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                try {
                    try {
                        port = Integer.parseInt(txt_port.getText().trim());
                    } catch (NumberFormatException e2) {
                        throw new Exception("端口号不符合要求!端口为整数!");
                    }
                    String hostIp = txt_ip.getText().trim();
                    String name = txt_name.getText().trim();
                    if (name.equals("") || hostIp.equals("")) {
                        throw new Exception("姓名、服务器IP不能为空!");
                    }
                    boolean flag = connectServer(port, hostIp, name);
                    if (flag == false) {
                        throw new Exception("与服务器连接失败!");
                    }
                    frame.setTitle(name);
                    JOptionPane.showMessageDialog(frame, "成功连接!");
                } catch (Exception exc) {
                    JOptionPane.showMessageDialog(frame, exc.getMessage(),
                            "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
    public void send() {
        if (!isConnected) {
            JOptionPane.showMessageDialog(frame, "还没有连接服务器，无法发送消息！", "错误",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        String message = txt_mes.getText().trim();
        if (message == null || message.equals("")) {
            JOptionPane.showMessageDialog(frame, "消息不能为空！", "错误",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        sendmes(frame.getTitle() + "@" + "ALL" + "@" + message);
        txt_mes.setText(null);
    }
    //发送消息
    public synchronized  void sendmes(String messeage){
        writer.println(messeage);
        writer.flush();
    }
    //连接服务器
    public synchronized boolean connectServer(int port,String hostIp,String name){
        try{
            socket=new Socket(hostIp,port);
            writer=new PrintWriter(socket.getOutputStream());
            reader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
            sendmes(name+"@"+socket.getLocalAddress().toString());
            messageThread=new MessageThread(reader,contentArea);
            messageThread.start();
            isConnected=true;
            return true;
        } catch (IOException e) {
            contentArea.append("与端口为"+port+"IP地址为"+hostIp+"的服务器连接失败！\r\n");
            isConnected=false;
            return false;
        }
    }
    //主动断开连接
    public synchronized boolean closeConnection(){
        try{
            sendmes("CLOSE");
            messageThread.interrupt();
            reader.close();
            writer.close();
            socket.close();
            isConnected=false;
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            isConnected=true;
            return true;
        }
    }
    //消息线程
    class MessageThread extends Thread{
        private BufferedReader reader;
        private JTextArea contentArea;
        public MessageThread(BufferedReader reader,JTextArea textArea){
            this.reader=reader;
            this.contentArea=textArea;
        }
        //收到服务器关闭消息
        public synchronized void closGet() throws IOException {
            listModel.removeAllElements();
            reader.close();
            writer.close();
            socket.close();
            isConnected=false;
        }
        public void run(){
            String message=null;
            while(!this.isInterrupted()){
                try {
                    message=reader.readLine();
                    StringTokenizer tmp=new StringTokenizer(message,"/@");
                    String actioncmd=tmp.nextToken();
                    switch (actioncmd){
                        case "CLOSE":{//服务器关闭
                            contentArea.append("服务器已经关闭！\r\n");
                            closGet();
                            return;
                        }
                        case "ADD":{//新用户上线
                            String userip=null;
                            String username=null;
                            if((username==tmp.nextToken())&&(userip==tmp.nextToken())){
                                User user=new User(username,userip);
                                onlineUsers.put(username,user);
                                listModel.addElement(username);
                            }
                            break;
                        }
                        case "DELETE":{//删除用户
                            String username = tmp.nextToken();
                            User user = (User) onlineUsers.get(username);
                            onlineUsers.remove(user);
                            listModel.removeElement(username);
                            break;
                        }
                        case"USERLIST":{//加载用户列表
                            int size=Integer.parseInt(tmp.nextToken());
                            String userip=null;
                            String username=null;
                            for(int i=0;i<size;i++){//循环读取
                                username=tmp.nextToken();
                                userip=tmp.nextToken();
                                User user=new User(username,userip);
                                onlineUsers.put(username,user);
                                listModel.addElement(username);
                            }
                            break;
                        }
                        case "MAX":{
                            contentArea.append(tmp.nextToken()+tmp.nextToken()+"\r\n");
                            closGet();
                            JOptionPane.showMessageDialog(frame, "服务器缓冲区已满！", "错误",
                                    JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        default:{
                            contentArea.append(message+"\r\n");
                            break;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
