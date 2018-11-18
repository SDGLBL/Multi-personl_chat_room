package Server;

import Client.client;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

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
    private ArrayList<client> Client;
    private boolean isStart=false;

    public static void main(String[] args) {
        new creatServer();
    }
    public creatServer(){//构造函数
        frame=new JFrame("Server");
        contentArea= new JTextArea();
        contentArea.setEditable(false);
        txt_max=new JTextField("100");
        txt_mes=new JTextField();
        txt_port=new JTextField("256615");
        btn_send=new JButton("Send");
        btn_start=new JButton("Start");
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
        
    }
}
