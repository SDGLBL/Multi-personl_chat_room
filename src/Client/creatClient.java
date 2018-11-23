package Client;

import User.User;
import basicInfo.Info;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
    public creatClient(Info allInfo) {//构造函数
        frame = new JFrame("Client");
        contentArea = new JTextArea();
        contentArea.setEditable(false);
        txt_ip = new JTextField(allInfo.getIp());
        txt_name = new JTextField(allInfo.getName());
        txt_port = new JTextField(allInfo.getPort());
        txt_mes = new JTextField();
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
        upjpanel.add(new JLabel("Name"));
        upjpanel.add(txt_name);
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
        //启动直接自动连接
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
        //
        txt_mes.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                send();
            }
        });

        // 单击发送按钮时事件
        btn_send.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                send();
            }
        });

        // 单击连接按钮时事件
        btn_start.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (isConnected) {
                    JOptionPane.showMessageDialog(frame, "已处于连接上状态，不要重复连接!",
                            "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                int port;
                try {
                    if(onlineUsers.containsKey(txt_name.getText().trim())){
                        JOptionPane.showMessageDialog(frame,"一个用户只能登陆一次",
                                "错误", JOptionPane.ERROR_MESSAGE);
                    }
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

        // 单击断开按钮时事件
        btn_stop.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!isConnected) {
                    JOptionPane.showMessageDialog(frame, "已处于断开状态，不要重复断开!",
                            "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                try {
                    boolean flag = closeConnection();// 断开连接
                    if (flag == false) {
                        throw new Exception("断开连接发生异常！");
                    }
                    JOptionPane.showMessageDialog(frame, "成功断开!");
                } catch (Exception exc) {
                    JOptionPane.showMessageDialog(frame, exc.getMessage(),
                            "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // 关闭窗口时事件
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                if (isConnected) {
                    closeConnection();// 关闭连接
                }
                System.exit(0);// 退出程序
            }
        });
    }
    //移动光标
    public void moveLight(){
        contentArea.setCaretPosition(contentArea.getText().length());
    }
    /*连接*/
    public void send() {
        if (!isConnected) {
            JOptionPane.showMessageDialog(frame, "还没有连接服务器！", "错误",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        String message = txt_mes.getText().trim();
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
            messageThread.stop();
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
            while(true){
                try {
                    message = reader.readLine();
                    StringTokenizer stringTokenizer = new StringTokenizer(message,"/@");
                    String command = stringTokenizer.nextToken();// 命令
                    System.out.println(command);
                    if (command.equals("CLOSE"))// 服务器已关闭命令
                    {
                        contentArea.append("服务器已关闭!\r\n");
                        closGet();// 被动的关闭连接
                        return;// 结束线程
                    } else if (command.equals("ADD")) {// 有用户上线更新在线列表
                        String username = "";
                        String userIp = "";
                        if ((username = stringTokenizer.nextToken()) != null
                                && (userIp = stringTokenizer.nextToken()) != null) {
                            User user = new User(username, userIp);
                            onlineUsers.put(username, user);
                            listModel.addElement(username);
                        }
                    } else if (command.equals("DELETE")) {// 有用户下线更新在线列表
                        String username = stringTokenizer.nextToken();
                        User user = (User) onlineUsers.get(username);
                        onlineUsers.remove(user);
                        listModel.removeElement(username);
                    } else if (command.equals("USERLIST")) {// 加载在线用户列表
                        int size = Integer
                                .parseInt(stringTokenizer.nextToken());
                        String username = null;
                        String userIp = null;
                        for (int i = 0; i < size; i++) {
                            username = stringTokenizer.nextToken();
                            userIp = stringTokenizer.nextToken();
                            User user = new User(username, userIp);
                            onlineUsers.put(username, user);
                            listModel.addElement(username);
                        }
                    } else if (command.equals("MAX")) {// 人数已达上限
                        contentArea.append(stringTokenizer.nextToken() + "\r\n");
                        closGet();// 被动的关闭连接
                        JOptionPane.showMessageDialog(frame, "服务器已满！", "通知",
                                JOptionPane.ERROR_MESSAGE);
                        return;// 结束线程
                    }
                    else if(command.equals("TICK")){//被踢了
                        contentArea.append("你已被服务器T掉。");
                        closeConnection();//主动关闭
                        JOptionPane.showMessageDialog(frame, "你已经被服务器T掉", "通知",
                                JOptionPane.ERROR_MESSAGE);
                        return;//结束进程
                    }
                        else {// 普通消息
                        contentArea.append(message + "\r\n");
                        moveLight();
                    }
                    } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            }
        }
    }
