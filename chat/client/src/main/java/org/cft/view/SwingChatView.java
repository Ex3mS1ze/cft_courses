package org.cft.view;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.cft.common.dto.TextMessage;
import org.cft.controller.ChatController;
import org.cft.util.HtmlWrapper;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.text.*;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class SwingChatView extends JFrame implements ChatView {
    private final ChatController chatController;

    private static final Color CONNECTED_COLOR = new Color(0xC8008543);
    private static final Color DISCONNECTED_COLOR = new Color(0xCB5342);
    private static final Color OTHER_AUTHOR_COLOR = new Color(0x6AB5B1);
    private static final Color AUTHOR_COLOR = new Color(0x00BD7A);
    private static final Color NOTIFICATION_COLOR = new Color(0x2AA4C9B6);

    private static final int MAIN_WIDTH = 470;
    private static final int MAIN_HEIGHT = 420;

    private static final int PARTICIPANTS_WIDTH = 100;
    private static final int PARTICIPANTS_HEIGHT = 200;

    private static final int MESSAGES_AREA_WIDTH = 330;
    private static final int MESSAGES_AREA_HEIGHT = 330;

    private static final int INPUT_AREA_COLUMNS = 30;

    @SuppressWarnings("SuspiciousDateFormat")
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM HH:mm");

    private static final String TITLE = "Чат";
    private static final String CONNECTION_SETTINGS_TITLE = "Настройки";
    private static final String SEND_BUTTON_TEXT = "Отправить";
    private static final String INPUT_NAME_MESSAGE = "Введите имя";
    private static final String CONNECTED_MESSAGE = " подключился";
    private static final String DISCONNECTED_MESSAGE = " отключился";
    private static final String EXIT_MESSAGE = "Точно хотите выйти?";
    private static final String EXIT_TITLE = "Выход";
    private static final String NAME_UNAVAILABLE_TITLE = "Имя используется";
    private static final String NAME_UNAVAILABLE_MESSAGE = "Имя уже используется, попробуйте другое";

    private String[] participantNames = new String[]{"Андрей", "Кирилл", "Ольга", "Морс"};

    private Style otherAuthorStyle;
    private Style authorStyle;
    private Style notificationStyle;

    private StyledDocument messageDocument;

    private JTextArea messageField;
    private JTextPane messagesTextPanel;
    private JList<String> participantJlist;
    private JButton sendButton;

    private HTMLEditorKit htmlEditorKit;

    private final boolean browserSupported = Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE);
    private volatile boolean nameInputDialogOpen;

    public SwingChatView(ChatController chatController) {
        super(TITLE);
        this.chatController = chatController;

        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());

        createAndSetPanels();

        setPreferredSize(new Dimension(MAIN_WIDTH, MAIN_HEIGHT));
        pack();
        setResizable(false);
        setFocusable(true);
        setVisible(true);

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                messageField.requestFocusInWindow();
            }
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int confirm = JOptionPane.showOptionDialog(SwingChatView.this, EXIT_MESSAGE, EXIT_TITLE,
                                                           JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null,
                                                           null);
                if (confirm == JOptionPane.OK_OPTION) {
                    setVisible(false);
                    chatController.handleExit();
                }
            }
        });
    }

    private void createAndSetPanels() {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.VERTICAL;

        constraints.gridy = 0;
        constraints.gridx = 0;
        messagesTextPanel = new JTextPane();
        DefaultCaret caret = (DefaultCaret) messagesTextPanel.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        messagesTextPanel.setPreferredSize(new Dimension(MESSAGES_AREA_WIDTH, MESSAGES_AREA_HEIGHT));
        messagesTextPanel.setContentType("text/html");
        messagesTextPanel.setEnabled(true);
        htmlEditorKit = new HTMLEditorKit();
        messagesTextPanel.setEditorKit(htmlEditorKit);
        if (browserSupported) {
            messagesTextPanel.addHyperlinkListener(e -> {
                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    try {
                        Desktop.getDesktop().browse(e.getURL().toURI());
                    } catch (IOException | URISyntaxException ex) {
                        log.error("", ex);
                    }
                }
            });
        }

        createStyles();
        messagesTextPanel.setEditable(false);
        add(new JScrollPane(messagesTextPanel), constraints);
        messageDocument = messagesTextPanel.getStyledDocument();

        constraints.gridx = 1;
        participantJlist = new JList<>(participantNames);
        participantJlist.setPreferredSize(new Dimension(PARTICIPANTS_WIDTH, PARTICIPANTS_HEIGHT));
        add(new JScrollPane(participantJlist), constraints);

        constraints.gridy = 1;
        constraints.gridx = 0;
        messageField = new JTextArea(3, INPUT_AREA_COLUMNS);
        messageField.setFocusable(true);
        messageField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER && e.isControlDown()) {
                    log.info("Обработана комбинация CTRL+ENTER");
                    chatController.handleSendMessage(messageField.getText());
                    messageField.setText("");
                }
            }
        });
        add(new JScrollPane(messageField), constraints);

        constraints.gridx = 1;
        sendButton = new JButton(SEND_BUTTON_TEXT);
        sendButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    log.info("Нажатие ЛКМ по кнопке отправить");
                    chatController.handleSendMessage(messageField.getText());
                    messageField.setText("");
                }
            }
        });
        add(sendButton, constraints);
    }

    private void createStyles() {
        otherAuthorStyle = messagesTextPanel.addStyle("otherAuthorStyle", null);
        StyleConstants.setBold(this.otherAuthorStyle, true);
        StyleConstants.setBackground(this.otherAuthorStyle, OTHER_AUTHOR_COLOR);

        authorStyle = messagesTextPanel.addStyle("authorStyle", null);
        StyleConstants.setBold(this.authorStyle, true);
        StyleConstants.setBackground(authorStyle, AUTHOR_COLOR);

        notificationStyle = messagesTextPanel.addStyle("notificationStyle", null);
        StyleConstants.setBackground(notificationStyle, NOTIFICATION_COLOR);
    }

    @Override
    public void renderNameRequest() {
        if (nameInputDialogOpen) {
            log.info("Диалог запроса имена уже октрыт");
            return;
        }

        SwingUtilities.invokeLater(() -> {
            nameInputDialogOpen = true;
            String name = JOptionPane.showInputDialog(this, INPUT_NAME_MESSAGE);
            nameInputDialogOpen = false;

            if (name == null) {
                SwingChatView.this.setVisible(false);
                chatController.handleExit();
            } else {
                chatController.handleConfirmationUsername(name);
            }
        });
    }

    @SneakyThrows
    @Override
    public void renderNameUnavailable() {
        SwingUtilities.invokeAndWait(() -> JOptionPane.showMessageDialog(this, NAME_UNAVAILABLE_MESSAGE, NAME_UNAVAILABLE_TITLE,
                                                                         JOptionPane.INFORMATION_MESSAGE));
    }

    @Override
    public void renderConnectionSettings() {
        SwingMultipleInputPane multipleInputPane = new SwingMultipleInputPane();
        int paneResult = JOptionPane.showConfirmDialog(this, multipleInputPane, CONNECTION_SETTINGS_TITLE, JOptionPane.OK_CANCEL_OPTION);
        if (paneResult == JOptionPane.OK_OPTION) {
            chatController.handleConnectionSettings(multipleInputPane.getHostField().getText(),
                                                    multipleInputPane.getPortField().getText());
        } else {
            System.exit(0);
        }
    }

    @Override
    public void renderNewMessage(TextMessage message, boolean authored) {
        Style style = authored ?  authorStyle : otherAuthorStyle;
        String authorName = authored ? "Я" + "(" + message.getAuthor().getName() + ")" : message.getAuthor().getName();

        addStringToMessagePane(
                System.lineSeparator() + authorName + " [" + DATE_FORMAT.format(message.getCreationDate()) +
                "]: " + System.lineSeparator(), style);

        HtmlWrapper.wrapUrl(message.getText()).forEach(this::addHtmlStringToMessagePane);
    }

    @Override
    public void renderParticipantConnected(String participantName) {
        addStringToMessagePane(System.lineSeparator() + participantName + CONNECTED_MESSAGE, notificationStyle);

        if (ArrayUtils.contains(participantNames, participantName)) {
            return;
        }

        List<String> participantList = new ArrayList<>(Arrays.asList(participantNames));
        participantList.add(participantName);
        participantNames = participantList.toArray(new String[0]);
        participantJlist.setListData(participantNames);
    }

    @Override
    public void renderParticipantDisconnected(String participantName) {
        addStringToMessagePane(System.lineSeparator() + participantName + DISCONNECTED_MESSAGE, notificationStyle);

        List<String> participantList = new ArrayList<>(Arrays.asList(participantNames));
        participantList.remove(participantName);
        participantNames = participantList.toArray(new String[0]);
        participantJlist.setListData(participantNames);
    }

    @Override
    public void renderParticipants(String[] participantNames) {
        this.participantNames = participantNames;
        participantJlist.setListData(participantNames);
    }

    @Override
    public void renderConnected() {
        sendButton.setBackground(CONNECTED_COLOR);
    }

    @Override
    public void renderDisconnected() {
        sendButton.setBackground(DISCONNECTED_COLOR);
    }

    private void addHtmlStringToMessagePane(String htmlString) {
        try {
            htmlEditorKit.insertHTML((HTMLDocument) messageDocument, messageDocument.getLength(), htmlString, 0, 0, null);
        } catch (BadLocationException | IOException e) {
            log.error("", e);
        }
    }

    private void addStringToMessagePane(String string, Style style) {
        try {
            messageDocument.insertString(messageDocument.getLength(), string, style);
        } catch (BadLocationException e) {
            log.error("", e);
        }
    }
}
