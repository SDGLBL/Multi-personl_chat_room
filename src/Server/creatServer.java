package Server;


import User.User;

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
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.StringTokenizer;


public class creatServer {
    private JFrame frame;
    private JTextArea contentArea;
    private  JTextField txt_mes;
    private  JTextField txt_max;
    private  JTextField txt_port;
    private JButton btn_start;
    private JButton btn_stop;
    private JButton btn_send;
    private JPanel upjpanel;
    private JPanel downjpanel;
    private  JScrollPane rpane;
    private  JScrollPane lpanel;
    private  JSplitPane centerSplit;
    private  JList users;
    private ServerSocket serverSocket;
    private ServerThread serverThread;
    private ArrayList<clientThread> clients;
    private DefaultListModel listModel;
    private boolean isStart=false;

    public static void main(String[] args) {
        new creatServer();
    }
    public creatServer(){//构造函数
        frame=new JFrame("Server");
        contentArea= new JTextArea();
        contentArea.setEditable(false);
        txt_max=new JTextField("20");
        txt_mes=new JTextField();
        txt_port=new JTextField("10086");
        btn_send=new JButton("Send");
        btn_start=new JButton("Start");
        btn_stop=new JButton("Stop");
        listModel = new DefaultListModel();
        users = new JList(listModel);
        lpanel=new JScrollPane(users);
        lpanel.setBorder(new TitledBorder("Online users"));
        rpane=new JScrollPane(contentArea);
        rpane.setBorder(new TitledBorder("Content area"));
        centerSplit=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,lpanel,rpane);
        centerSplit.setDividerLocation(100);
        downjpanel=new JPanel(new BorderLayout());
        downjpanel.setBorder(new TitledBorder("Input messseage"));
        downjpanel.add(txt_mes,"Center");
        downjpanel.add(btn_send,"East");
        upjpanel=new JPanel();
        upjpanel.setLayout(new GridLayout(1,6));
        upjpanel.add(new JLabel("Max users"));
        upjpanel.add(txt_max);
        upjpanel.add(new JLabel("Port"));
        upjpanel.add(txt_port);
        upjpanel.add(btn_start);
        upjpanel.add(btn_stop);
        upjpanel.setBorder(new TitledBorder("Server info"));
        frame.setLayout(new BorderLayout());
        frame.add(upjpanel,"North");
        frame.add(downjpanel,"South");
        frame.add(centerSplit,"Center");
        frame.setSize(500,400);
        int screen_width = Toolkit.getDefaultToolkit().getScreenSize().width;
        int screen_height = Toolkit.getDefaultToolkit().getScreenSize().height;
        frame.setLocation((screen_width - frame.getWidth()) / 2,
                (screen_height - frame.getHeight()) / 2);
        frame.setVisible(true);
        frame.addWindowListener(new WindowAdapter() {//设置关闭事件
            @Override
            public void windowClosing(WindowEvent e) {
                if(isStart) closeServer();//关闭服务器
                System.exit(0);//退出程序
            }
        });
        txt_mes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                send();
            }
        });
        btn_send.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                send();
            }
        });
        btn_start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isStart) {
                    JOptionPane.showMessageDialog(frame, "Server has been started",
                            "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                int max= Integer.parseInt(txt_max.getText());
                int port=Integer.parseInt(txt_port.getText());
                try {
                    startServer(max, port);
                } catch (BindException e1) {
                    e1.printStackTrace();
                }
                contentArea.append("服务器已成功启动!人数上限：" + max + ",端口：" + port
                            + "\r\n");
                    JOptionPane.showMessageDialog(frame, "Server start!");
                    btn_start.setEnabled(false);
                    txt_max.setEnabled(false);
                    txt_port.setEnabled(false);
                    btn_stop.setEnabled(true);
            }

        });
        btn_stop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isStart) {
                    JOptionPane.showMessageDialog(frame, "The server didn`t start", "Eorr",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                try {
                    closeServer();
                    btn_start.setEnabled(true);
                    txt_max.setEnabled(true);
                    txt_port.setEnabled(true);
                    btn_stop.setEnabled(false);
                    contentArea.append("Server has been stoped!\r\n");
                    JOptionPane.showMessageDialog(frame, "Server has been stoped!！");
                } catch (Exception exc) {
                    JOptionPane.showMessageDialog(frame, "stop eorr！", "Eorr",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    //启动服务器
    public void startServer(int max,int port) throws BindException {
        try {
            clients = new ArrayList<clientThread>();
            serverSocket = new ServerSocket(port);
            serverThread = new ServerThread(serverSocket,max);
            serverThread.start();
            isStart = true;
        } catch (BindException e) {
            isStart = false;
            throw new BindException("The port has been used!Please change a new one.");
        } catch (Exception e1) {
            e1.printStackTrace();
            isStart = false;
            throw new BindException("Start server error.");
        }
    }
    //关闭服务器
    public void closeServer(){
        try {
            if (serverThread != null)
                serverThread.interrupt();// 停止服务器线程
            for (int i =0; i<clients.size(); i++) {
                // 给所有在线用户发送关闭命令
                clients.get(i).getWriter().println("CLOSE");
                clients.get(i).getWriter().flush();
                // 释放资源
                clients.get(i).interrupt();// 停止此条为客户端服务的线程
                clients.get(i).reader.close();
                clients.get(i).writer.close();
                clients.get(i).socket.close();
                clients.remove(i);
            }
            if (serverSocket != null) {
                serverSocket.close();// 关闭服务器端连接
            }
            listModel.removeAllElements();// 清空用户列表
            isStart = false;
        } catch (IOException e) {
            e.printStackTrace();
            isStart = true;
        }
    }
    //转发给其他所有人
    private void sendToOther(String message) {
        StringTokenizer stringTokenizer = new StringTokenizer(message, "@");
        String source = stringTokenizer.nextToken();
        String owner = stringTokenizer.nextToken();
        String content = stringTokenizer.nextToken();
        message = source + "said：" + content;
        contentArea.append(message + "\r\n");
        if (owner.equals("ALL")) {// 群发
            for (int i = clients.size() - 1; i >= 0; i--) {
                clients.get(i).getWriter().println(message + "(to all)");
                clients.get(i).getWriter().flush();
            }
        }

    }
    // 执行消息发送
    public void send() {
        if (!isStart) {
            JOptionPane.showMessageDialog(frame, "The server didn`t start", "Eorr",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        String message = txt_mes.getText().trim();
        sendServerMessage(message);// 群发服务器消息
        contentArea.append("Server said：" + txt_mes.getText() + "\r\n");
        txt_mes.setText(null);
    }
    //发送服务器消息
    public void sendServerMessage(String message) {
        for (int i =0; i < clients.size(); i++) {
            clients.get(i).getWriter().println("Server：" + message + "(to all)");
            clients.get(i).getWriter().flush();
        }
    }
    //服务端线程
    class ServerThread extends Thread {
        private ServerSocket serverSocket;
        private int max;
        public ServerThread(ServerSocket serverSocket, int max) {
            this.serverSocket = serverSocket;
            this.max = max;
        }
        public  void run(){
            while(!this.isInterrupted()){
                //循环等待用户端连接
                try{
                    Socket socket=serverSocket.accept();
                    if(clients.size()==max){
                        BufferedReader reader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        PrintWriter writer=new PrintWriter(socket.getOutputStream());
                        //接收用户信息
                        String info=reader.readLine();
                        //分割字符串
                        StringTokenizer st=new StringTokenizer(info,"@");
                        User user=new User(st.nextToken(),st.nextToken());
                        //返还信息
                        writer.println("MAX@Server：Sorry"+user.getName()+"the server had connected 30 users.");
                        //释放资源
                        writer.flush();
                        writer.close();
                        reader.close();
                        socket.close();
                        continue;
                    }
                    clientThread client=new clientThread(socket);//创建该用户的服务线程
                    client.start();
                    clients.add(client);
                    listModel.addElement(client.getUser().getName());
                    contentArea.append(client.getUser().getName() + client.getUser().getIp() + "online!\r\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    //为客户端处理线程构造
    class clientThread extends  Thread{
        private User user;
        private Socket socket;
        private BufferedReader reader;
        private PrintWriter writer;
        public BufferedReader getReader(){
            return reader;
        }
        public PrintWriter getWriter(){
            return writer;
        }
        public User getUser(){
            return user;
        }
        public  clientThread(Socket socket){
            try{
                this.socket=socket;
                reader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
                writer=new PrintWriter(socket.getOutputStream());
                String info=reader.readLine();
                StringTokenizer st=new StringTokenizer(info,"@");//分割字符串
                user=new User(st.nextToken(),st.nextToken());
                //返回连接成功消息
                writer.println(user.getName()+user.getIp()+"succesed connect server");
                writer.flush();
                //反馈当前在线用户信息
                if(clients.size()>0){
                    String temp="";
                    for(int i=0;i<clients.size();i++){
                        temp+=(clients.get(i).getUser().getName()+"/"+clients.get(i).getUser().getIp()+"@");
                    }
                    writer.println("USERLIST@"+clients.size()+"@"+temp);
                    writer.flush();
                }
                //告知当前在线用户xxx上线
                for(int i=0;i<clients.size();i++){
                    clients.get(i).getWriter().println("ADD@"+user.getName()+user.getIp());
                    clients.get(i).getWriter().flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        public void run(){
            String message=null;
            while(!this.isInterrupted()){
                try{//接收客户端消息
                    message=reader.readLine();
                    if(message.equals("CLOSE")){
                        contentArea.append(this.user.getName()+" offline!\r\n");
                        reader.close();
                        writer.close();
                        socket.close();
                        //通知其他用户
                        for(int i=0;i<clients.size();i++){
                            clients.get(i).getWriter().println("DELETE@"+user.getName());
                            clients.get(i).getWriter().flush();
                        }
                        //更新在线列表
                        listModel.removeElement(user.getName());
                        //清除此线程
                        for(int i=0;i<clients.size();i++){
                            if (clients.get(i).getUser() == user) {
                                clientThread temp = clients.get(i);
                                clients.remove(i);// 删除此用户的服务线程
                                temp.interrupt();
                                return;
                            }
                        }

                    }
                    else{
                        sendToOther(message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}


