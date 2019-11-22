/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mybank.chat;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.net.Socket;

/**
 *
 * @author virtus-local
 */
public class ChatClient {

    private JTextArea output;
    private JTextField input;
    private JButton sendButton;
    private JButton quitButton;

    private Socket connection = null;
    private BufferedReader serverin = null;
    private PrintStream serverOut = null;

    private void doConnect() {
        String serverIP = System.getProperty("serverIP", "127.0.0.1");
        String serverPort = System.getProperty("serverSort", "2000");

        try {
            connection = new Socket(serverIP, Integer.parseInt(serverPort));
            InputStream is = connection.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            serverin = new BufferedReader(isr);
            serverOut = new PrintStream(connection.getOutputStream());
            Thread t = new Thread(new RemoteReader());
            t.start();
        } catch (Exception e) {
            System.err.print("ERROR: unable to connect to server");
            e.printStackTrace();
        }
    }

    public ChatClient() {
        this.output = new JTextArea(10, 50);
        this.input = new JTextField(50);
        this.sendButton = new JButton("Send");
        this.quitButton = new JButton("Quite");
    }

    public void LounchFrame() {
        JFrame frame = new JFrame("Bank Chat Room");
        frame.setLayout(new BorderLayout());

        frame.add(output, BorderLayout.CENTER);
        frame.add(input, BorderLayout.SOUTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(2, 1));
        buttonPanel.add(sendButton);
        buttonPanel.add(quitButton);

        frame.add(buttonPanel, BorderLayout.WEST);
        output.setEditable(false);

        input.addActionListener(new SendHandler());
        sendButton.addActionListener(new SendHandler());
        quitButton.addActionListener((ActionEvent e) -> {
            System.exit(0);
        });

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setAlwaysOnTop(true);
        frame.setLocationRelativeTo(null);
        frame.pack();

        frame.setVisible(true);
        doConnect();
    }

    public static void main(String[] args) {
        ChatClient myChat = new ChatClient();
        myChat.LounchFrame();
    }

    private class RemoteReader implements Runnable {

        @Override
        public void run() {
            try {
                while (true) {
                    String nextLine = serverin.readLine();
                    output.append(nextLine + "/n");
                }
            } catch (Exception e) {
                System.out.println("ERROR: can`t read fron the server!");
            }
        }

    }

    private class SendHandler implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            String massage = input.getText();
            //output.append(massage + "/n");
            serverOut.print("New message" + massage + "/n");
            input.setText("");
        }

    }

}
