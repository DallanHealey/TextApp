import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.ArrayList;

public class GUI
{
    static JFrame frame;
    private static JTextPane messages;
    private static JTextField message;
    static JTabbedPane tabPane;
    private static JScrollPane scrollPane;
    static int TAB_NUMBER = -1;
    static ArrayList<JTextPane> messagesList;

    static boolean didWork;

    public static void main (String[] args) throws IOException
    {
        messagesList = new ArrayList<JTextPane>();

        //Set look and feel
        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        //Set basic features
        frame = new JFrame("Text App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        // Set Layout
        BorderLayout borderLayout = new BorderLayout();
        frame.setLayout(borderLayout);

        //Create message bar
        message = new JTextField();
        message.addKeyListener(new KeyListener()
        {
            @Override
            public void keyTyped (KeyEvent e)
            {
            }

            @Override
            public void keyPressed (KeyEvent e)
            {
            }

            @Override
            public void keyReleased (KeyEvent e)
            {
                if (message.getText().isEmpty() || message.getText() == null || message.getText() == "")
                    return;

                if (e.getKeyCode() == KeyEvent.VK_ENTER)
                {
                    if (message.getText().contains("!text"))
                        textNewUser(message.getText());
                    else if (message.getText().contains("!quit"))
                    {
                        try
                        {
                            TextServerThread.stop();
                        }
                        catch (IOException io)
                        {
                            io.printStackTrace();
                        }
                    }
                    else
                    {
                        try
                        {
                            TextServerThread.sendMessage(message.getText());
                        }
                        catch (Exception e2)
                        {
                        }
                    }
                    try
                    {
                        if(didWork)
                            insertString(messagesList.get(TAB_NUMBER), TextServerThread.usersPhoneNumber + ": " + message.getText());
                        else
                            insertString(messagesList.get(tabPane.getSelectedIndex()), TextServerThread.usersPhoneNumber + ": " + message.getText());
                    }
                    catch (Exception e1)
                    {
                        e1.printStackTrace();
                    }
                    message.setText("");
                }
            }
        });

        //Adds tab pane
        tabPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.WRAP_TAB_LAYOUT);
        //tabPane.add("Test", scrollPane);

        //frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(tabPane, BorderLayout.CENTER);
        frame.add(message, BorderLayout.SOUTH);

        frame.setSize(600, 400);
        message.requestFocus();
    }

    private static void textNewUser (String messages)
    {
        String[] data = messages.split(" ", 3);
        System.out.println("Tab number: " + TAB_NUMBER + ", selected tab: " + tabPane.getSelectedIndex());
        didWork = onNewText(data[1]);

        TextServerThread.sendMessage(data[2]);
    }

    static boolean onNewText (String phoneNumber)
    {
        if (TextServerThread.recipientsPhoneNumber.contains(phoneNumber))
            return false;

        createNewTab(phoneNumber);

        TextServerThread.recipientsPhoneNumber.put(TAB_NUMBER, phoneNumber);
        System.out.println(TextServerThread.recipientsPhoneNumber.toString());
        return true;
    }

    static void insertString (JTextPane selectedMessages, String message) throws BadLocationException
    {
        selectedMessages.getDocument().insertString(selectedMessages.getDocument().getLength(), message, null);
    }

    private static void createNewTab (String phoneNumber)
    {
        JPanel noWrapPanel = new JPanel(new BorderLayout());
        noWrapPanel.setPreferredSize(new Dimension(175, 200));

        JTextPane messages = new JTextPane();

        messages.setEditorKit(JEditorPane.createEditorKitForContentType("text/html"));
        noWrapPanel.add(messages, BorderLayout.CENTER);
        messages.setEditable(false);
        messages.setFocusable(false);

        JScrollPane scrollPane = new JScrollPane(noWrapPanel);
        scrollPane.setViewportView(messages);
        JScrollBar vBar = scrollPane.getVerticalScrollBar();
        vBar.setValue(vBar.getMaximum());
        messages.setName("messages");

        messagesList.add(messages);

        tabPane.add(phoneNumber, scrollPane);

        TAB_NUMBER++;
    }
}
