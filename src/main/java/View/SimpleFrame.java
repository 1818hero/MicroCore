package View;


import javax.swing.*;

import static com.sun.javafx.scene.control.skin.ScrollBarSkin.DEFAULT_WIDTH;


/**
 */
public class SimpleFrame extends JFrame {

    private static final int DEFAULT_WIDTH = 900;
    private static final int DEFAULT_HEIGHT = 600;

    public SimpleFrame() {
        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    public static void drawWindow(){
        SimpleFrame frame = new SimpleFrame();
        JPanel panel = new JPanel();
        frame.add(panel);
        panel.setLayout(null);
        JLabel RTLLabel= new JLabel("上期消费余额");
        RTLLabel.setBounds(10,20,80,25);
        panel.add(RTLLabel);

        JTextField RTLField = new JTextField(20);
        RTLField.setBounds(100,20,165,25);
        panel.add(RTLField);



        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public static void main(String[] args){
        drawWindow();
    }



}
