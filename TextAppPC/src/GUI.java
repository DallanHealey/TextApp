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
    private static JTextField message;
    static JTabbedPane tabPane;
    static int TAB_NUMBER = -1;

    static ArrayList<JTextPane> messagesList;

    static boolean didWork;

    public static void main (String[] args) throws IOException
    {
        messagesList = new ArrayList<>();

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
                    else if (message.getText().contains("!close"))
                        closeTab();
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
                        if (didWork)
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

        //Add the basic components
        frame.add(tabPane, BorderLayout.CENTER);
        frame.add(message, BorderLayout.SOUTH);

        //Default settings
        frame.setSize(600, 400);
        message.requestFocus();
    }

    /**
     * Called if user types "!text"
     * Sends a message to a new user
     *
     * @param messages Message containing "phone number": "message" to be sent to user
     */
    private static void textNewUser (String messages)
    {
        String[] data = messages.split(" ", 3);

        if (data[1].length() < 10 || data[1].length() > 10)
            return;

        didWork = onNewText(data[1]);

        TextServerThread.sendMessage(data[2]);
    }

    /**
     * Checks if the incoming phone number is in the number hash table and if so returns false, else it creates a new tab, adds the number to the hash table
     * and returns true
     *
     * @param phoneNumber Incoming text message's phone number
     */
    static boolean onNewText (String phoneNumber)
    {
        if (TextServerThread.recipientsPhoneNumber.contains(phoneNumber))
            return false;

        createNewTab(phoneNumber);

        TextServerThread.recipientsPhoneNumber.put(TAB_NUMBER, phoneNumber);
        TextServerThread.recipientsPhoneNumberBackwards.put(phoneNumber, TAB_NUMBER);
        System.out.println(TextServerThread.recipientsPhoneNumber.toString());

        return true;
    }

    /**
     * Inserts a message into the correct message area
     *
     * @param selectedMessages The correct tab's message area
     * @param message          Message to be inserted
     */
    static void insertString (JTextPane selectedMessages, String message) throws BadLocationException
    {
        if (message.contains("!close"))
            return;

        selectedMessages.getDocument().insertString(selectedMessages.getDocument().getLength(), message + "\n", null);
    }

    /**
     * Creates a new tab with the phone number and adds the messages area to the messageList
     *
     * @param phoneNumber phone number for the title of the new tab
     */
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

        messagesList.add(messages);

        tabPane.add(phoneNumber, scrollPane);

        TAB_NUMBER++;
    }

    /**
     * Closes the currently selected tab
     * WIP
     */
    private static void closeTab ()
    {
        int closingTab = tabPane.getSelectedIndex();

        String phoneNumber = TextServerThread.recipientsPhoneNumber.remove(tabPane.getSelectedIndex());
        TextServerThread.recipientsPhoneNumberBackwards.remove(phoneNumber);
        messagesList.remove(tabPane.getSelectedIndex());
        tabPane.remove(tabPane.getSelectedIndex());
        TAB_NUMBER--;

        for (int i = closingTab; i < TextServerThread.recipientsPhoneNumber.size(); i++)
            TextServerThread.recipientsPhoneNumber.put(i, TextServerThread.recipientsPhoneNumber.get(++closingTab));

        System.out.println("recipients list updated: " + TextServerThread.recipientsPhoneNumber.toString());

        message.setText("");
    }
}