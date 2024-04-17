package com.email;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;

public class EmailSenderGUI extends JFrame implements ActionListener {

    private JTextField senderEmailField, senderPasswordField, recipientEmailField, ccEmailField, bccEmailField, subjectField;
    private JTextArea messageTextArea;
    private JButton sendButton, attachButton, loadTemplateButton;
    private JLabel statusLabel;
    private DefaultTableModel addressBookTableModel;
    private JTable addressBookTable;
    private Map<String, String> emailTemplates;

    public EmailSenderGUI() {
        setTitle("Email Sender");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Initialize email templates
        emailTemplates = new HashMap<>();
        loadTemplates(); // Load templates from file or database

        // Create tabs
        JTabbedPane tabbedPane = new JTabbedPane();

        // Create tab for sending emails
        JPanel sendEmailPanel = createSendEmailPanel();
        JPanel addressBookPanel = createAddressBookPanel();
        tabbedPane.addTab("Send Email", sendEmailPanel);
        tabbedPane.addTab("Address Book", addressBookPanel);

        // Add tabs to the frame
        add(tabbedPane);

        setVisible(true);
    }

    private JPanel createSendEmailPanel() {
        JPanel sendEmailPanel = new JPanel(new BorderLayout());

        // Create components for sending emails
        JPanel formPanel = new JPanel(new GridLayout(7, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        formPanel.setBackground(Color.WHITE); // Set background color

        JLabel senderEmailLabel = new JLabel("Sender Email:");
        senderEmailField = new JTextField();
        formPanel.add(senderEmailLabel);
        formPanel.add(senderEmailField);

        JLabel senderPasswordLabel = new JLabel("Sender Password:");
        senderPasswordField = new JPasswordField();
        formPanel.add(senderPasswordLabel);
        formPanel.add(senderPasswordField);

        JLabel recipientEmailLabel = new JLabel("Recipient Email:");
        recipientEmailField = new JTextField();
        formPanel.add(recipientEmailLabel);
        formPanel.add(recipientEmailField);

        JLabel ccEmailLabel = new JLabel("CC (Optional):");
        ccEmailField = new JTextField();
        formPanel.add(ccEmailLabel);
        formPanel.add(ccEmailField);

        JLabel bccEmailLabel = new JLabel("BCC (Optional):");
        bccEmailField = new JTextField();
        formPanel.add(bccEmailLabel);
        formPanel.add(bccEmailField);

        JLabel subjectLabel = new JLabel("Subject:");
        subjectField = new JTextField();
        formPanel.add(subjectLabel);
        formPanel.add(subjectField);

        JLabel messageLabel = new JLabel("Message:");
        messageTextArea = new JTextArea();
        formPanel.add(messageLabel);
        formPanel.add(new JScrollPane(messageTextArea));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(Color.WHITE); // Set background color

        attachButton = new JButton("Attach File");
        attachButton.addActionListener(e -> attachFile());
        buttonPanel.add(attachButton);

        sendButton = new JButton("Send");
        sendButton.addActionListener(this);
        buttonPanel.add(sendButton);

        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        statusPanel.setBackground(Color.WHITE); // Set background color

        statusLabel = new JLabel("");
        statusPanel.add(statusLabel);

        sendEmailPanel.add(formPanel, BorderLayout.CENTER);
        sendEmailPanel.add(buttonPanel, BorderLayout.SOUTH);
        sendEmailPanel.add(statusPanel, BorderLayout.NORTH);

        // Create buttons for loading templates and managing drafts
        loadTemplateButton = new JButton("Load Template");
        loadTemplateButton.addActionListener(this);

        // Add buttons to a separate panel
        JPanel templateButtonPanel = new JPanel();
        templateButtonPanel.add(loadTemplateButton);

        // Add the template button panel to the send email panel
        sendEmailPanel.add(templateButtonPanel, BorderLayout.NORTH);

        return sendEmailPanel;
    }
 // Create tab for address book
    private JPanel createAddressBookPanel() {
        JPanel addressBookPanel = new JPanel(new BorderLayout());

        addressBookTableModel = new DefaultTableModel();
        addressBookTableModel.addColumn("Name");
        addressBookTableModel.addColumn("Email");

        addressBookTable = new JTable(addressBookTableModel);
        JScrollPane addressBookScrollPane = new JScrollPane(addressBookTable);
        addressBookPanel.add(addressBookScrollPane, BorderLayout.CENTER);

        JPanel addressBookButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton addContactButton = new JButton("Add Contact");
        JButton editContactButton = new JButton("Edit Contact");
        JButton deleteContactButton = new JButton("Delete Contact");
        addressBookButtonPanel.add(addContactButton);
        addressBookButtonPanel.add(editContactButton);
        addressBookButtonPanel.add(deleteContactButton);
        addressBookPanel.add(addressBookButtonPanel, BorderLayout.SOUTH);

        // Add action listeners to address book buttons
        addContactButton.addActionListener(e -> addContact());
        editContactButton.addActionListener(e -> editContact());
        deleteContactButton.addActionListener(e -> deleteContact());

        return addressBookPanel;
    }

    private void addContact() {
        String name = JOptionPane.showInputDialog(this, "Enter contact name:");
        String email = JOptionPane.showInputDialog(this, "Enter contact email:");
        if (name != null && email != null) {
            addressBookTableModel.addRow(new Object[]{name, email});
        }
    }

    private void editContact() {
        int selectedRow = addressBookTable.getSelectedRow();
        if (selectedRow != -1) {
            String name = (String) addressBookTableModel.getValueAt(selectedRow, 0);
            String email = (String) addressBookTableModel.getValueAt(selectedRow, 1);
            String newName = JOptionPane.showInputDialog(this, "Enter new name:", name);
            String newEmail = JOptionPane.showInputDialog(this, "Enter new email:", email);
            if (newName != null && newEmail != null) {
                addressBookTableModel.setValueAt(newName, selectedRow, 0);
                addressBookTableModel.setValueAt(newEmail, selectedRow, 1);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a contact to edit.");
        }
    }

    private void deleteContact() {
        int selectedRow = addressBookTable.getSelectedRow();
        if (selectedRow != -1) {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this contact?", "Delete Contact", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                addressBookTableModel.removeRow(selectedRow);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a contact to delete.");
        }
    }


    private void loadTemplates() {
        // Load templates from file or database
        // For demonstration, let's add some hardcoded templates
        emailTemplates.put("Meeting Invitation", "Dear [Recipient],\n\nLet's schedule a meeting to discuss the project.\n\nBest regards,\n[Sender]");
        emailTemplates.put("Feedback Request", "Hi [Recipient],\n\nWe would appreciate your feedback on our recent product.\n\nThanks,\n[Sender]");
        emailTemplates.put("Attendence Problem", "Dear [Recipient],\n\nI am [your name] taking [course name], a student of yours.I was present on[date] but yet my collpoll is showing absent.\nPlease look into the matter and do the needful.\n\nBest regards,\n[Sender]\n[EnrollmentNumber]");
    }

    private void showTemplateDialog() {
        JComboBox<String> templateList = new JComboBox<>(emailTemplates.keySet().toArray(new String[0]));
        int option = JOptionPane.showConfirmDialog(this, templateList, "Select Template", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (option == JOptionPane.OK_OPTION) {
            String selectedTemplate = (String) templateList.getSelectedItem();
            if (selectedTemplate != null) {
                String template = emailTemplates.get(selectedTemplate);
                if (template != null) {
                    // Insert template into the message area
                    messageTextArea.setText(template);
                }
            }
        }
    }

    // Implement other methods as needed

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new EmailSenderGUI());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loadTemplateButton) {
            showTemplateDialog();
        } else if (e.getSource() == sendButton) {
            sendEmail();
        }
        // Handle other action events...
    }

    public void sendEmail() {
        // Implement sending email functionality here
        String senderEmail = senderEmailField.getText();
        String senderPassword = senderPasswordField.getText();
        String recipientEmail = recipientEmailField.getText();
        String ccEmail = ccEmailField.getText();
        String bccEmail = bccEmailField.getText();
        String subject = subjectField.getText();
        String message = messageTextArea.getText();

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });

        try {
            Message emailMessage = new MimeMessage(session);
            emailMessage.setFrom(new InternetAddress(senderEmail));
            emailMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));

            if (!ccEmail.isEmpty()) {
                emailMessage.setRecipients(Message.RecipientType.CC, InternetAddress.parse(ccEmail));
            }

            if (!bccEmail.isEmpty()) {
                emailMessage.setRecipients(Message.RecipientType.BCC, InternetAddress.parse(bccEmail));
            }

            emailMessage.setSubject(subject);
            emailMessage.setText(message);

            Transport.send(emailMessage);

            statusLabel.setText("Email sent successfully!");
            statusLabel.setForeground(Color.GREEN);

        } catch (MessagingException ex) {
            ex.printStackTrace();
            statusLabel.setText("Error sending email: " + ex.getMessage());
            statusLabel.setForeground(Color.RED);
        }
    }

    public void attachFile() {
        // Implement attaching file functionality here
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            String filePath = fileChooser.getSelectedFile().getAbsolutePath();
            String fileName = fileChooser.getSelectedFile().getName();
            MimeBodyPart attachmentPart = new MimeBodyPart();
            try {
                attachmentPart.attachFile(filePath);
                // Add attachment to message
                Multipart multipart = new MimeMultipart();
                multipart.addBodyPart(attachmentPart);
                // Set message content
                messageTextArea.setText("Attached file: " + fileName + "\n" + messageTextArea.getText());
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error attaching file: " + e.getMessage());
            }
        }
    }
}
