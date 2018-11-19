package Client;

import basicInfo.Info;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class userLogin {
    private JFrame frame;
    private JTextField txt_name;
    private JTextField txt_password;
    private JButton login;
    private JTextField txt_ip;
    private JTextField txt_port;
    private JButton register;
    private JPanel northpanel;
    private JPanel centerpanel;
    private boolean confirm;
    private Info allInfo;
    volatile boolean next;
    public static void main(String[] args) {
        new userLogin();
    }
    public userLogin(){
        next=false;
        confirm=false;
        frame=new JFrame();
        frame.setLayout(new BorderLayout());
        txt_name=new JTextField();
        txt_name.setColumns(1);
        txt_ip = new JTextField("127.0.0.1");
        txt_port=new JTextField("10086");
        txt_name.setSize(100,20);
        login=new JButton("登陆");
        centerpanel=new JPanel();
        centerpanel.setLayout(new GridLayout(3,2));
        centerpanel.add(new JLabel("姓名:"));
        centerpanel.add(txt_name);
        centerpanel.add(new JLabel("服务器IP："));
        centerpanel.add(txt_ip);
        centerpanel.add(new JLabel("端口："));
        centerpanel.add(txt_port);
        frame.add(centerpanel,BorderLayout.CENTER);
        frame.add(login,BorderLayout.SOUTH);
        frame.setVisible(true);
        frame.setSize(300,150);
        int screen_width = Toolkit.getDefaultToolkit().getScreenSize().width;
        int screen_height = Toolkit.getDefaultToolkit().getScreenSize().height;
        frame.setLocation((screen_width - frame.getWidth()) / 2,
                (screen_height - frame.getHeight()) / 2);
        /*checkConfirm check=new checkConfirm();
        check.start();*/
        login.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                allInfo=new Info(txt_ip.getText().trim(),txt_port.getText().trim(),txt_name.getText().trim());
                sendConfirm();
                next=true;
                frame.setVisible(false);
            }
        });
    }
    public void stop(){
        System.exit(0);
    }
    public Info getAllInfo(){
        return allInfo;
    }
    public void sendConfirm(){
        confirm=true;
    }
    public boolean nextStep(){
        return false;
    }
    /*class checkConfirm extends Thread{
        private boolean confirm;
        public void run(){
            while(!confirm){
            }
            next=true;
        }
    }*/
}

