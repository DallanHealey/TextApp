import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class GUI
{
    public static JFrame frame;
    public static JTextPane messages;
    public static JTextArea message;

    public static void main(String[] args)
    {
        //Set basic features
        frame = new JFrame("Text App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.setSize(100, 100);

        // Set Layout
        BorderLayout borderLayout = new BorderLayout();
        frame.setLayout(borderLayout);

        // Components and their properties
        JPanel noWrapPanel = new JPanel(new BorderLayout());
        noWrapPanel.setPreferredSize(new Dimension(175, 200));

        //Create messages pane and add
        messages = new JTextPane();
        messages.setEditorKit(JEditorPane.createEditorKitForContentType("text/html"));
        noWrapPanel.add(messages);
        messages.setEditable(false);
        messages.setFocusable(false);

        //Create message bar
        message = new JTextArea();
        message.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (message.getText().isEmpty() || message.getText() == null)
                    return;
                if (e.getKeyCode() == KeyEvent.VK_ENTER)
                {
                    if(message.getText().contains("!text"))
                        textNewUser(message.getText());
                    else
                        TextServerThread.out.println(TextServerThread.recipientsPhoneNumber + ": " + message.getText());
                    //System.out.println("Phone number: " + TextServerThread.recipientsPhoneNumber);
                    try {
                        messages.getDocument().insertString(messages.getDocument().getLength(), TextServerThread.usersPhoneNumber + ": " + message.getText(), null);
                    }
                    catch (Exception e1)
                    {
                        e1.printStackTrace();
                    }
                    message.setText("");
                }
            }
        });

        //Add the scroll pane so the messages don't go off screen
        JScrollPane scrollPane = new JScrollPane(noWrapPanel);
        scrollPane.setViewportView(messages);
        JScrollBar vBar = scrollPane.getVerticalScrollBar();
        vBar.setValue(vBar.getMaximum());

        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(message, BorderLayout.SOUTH);

        frame.pack();
    }

    public static void textNewUser(String messages)
    {
        String[] data = messages.split(" ");
        TextServerThread.out.println(data[1] + ": " + data[2]);
    }
}
