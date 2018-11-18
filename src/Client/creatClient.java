package Client;

import Server.creatServer;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class creatClient {
    private JFrame frame;
    private JTextArea contentArea;
    private  JTextField txt_mes;
    private  JTextField txt_ip;
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
    private Socket Socket;
    private PrintWriter writer;
    private BufferedReader reader;
    private boolean isConnected=false;

    public static void main(String[] args) {
        new creatClient();
    }
    public creatClient(){//构造函数
        frame=new JFrame("Client");
        contentArea= new JTextArea();
        contentArea.setEditable(false);
        txt_ip=new JTextField("127.0.0.1");
        txt_mes=new JTextField();
        txt_port=new JTextField("256615");
        btn_send=new JButton("Send");
        btn_start=new JButton("Link");
        btn_stop=new JButton("Stop");
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
        upjpanel.add(new JLabel("IP"));
        upjpanel.add(txt_ip);
        upjpanel.add(new JLabel("Port"));
        upjpanel.add(txt_port);
        upjpanel.add(btn_start);
        upjpanel.add(btn_stop);
        upjpanel.setBorder(new TitledBorder("Connection info"));
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
}}
