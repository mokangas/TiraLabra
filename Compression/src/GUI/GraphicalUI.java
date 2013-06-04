package GUI;

import compression.Compression;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicOptionPaneUI;
import sun.awt.HorizBagLayout;

public class GraphicalUI extends JFrame {

    private JLabel message;
    private JRadioButton bmpToWtf;
    private JRadioButton wtfTBmp;
    private JTextField sourcePathField;
    private JTextField objectPathField;
    private static int MAX_LOL = 20;
    private JSpinner spinner;

    public GraphicalUI() {

        // Set the look and feel:

//        try {
//            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
//        } catch (Exception e) {
//            try {
//                UIManager.setLookAndFeel("Windows");
//            } catch (Exception e2) {
//                //Uses the default.
//            }
//        }

        setDefaultCloseOperation(EXIT_ON_CLOSE);

        createContent();
        pack();
        setVisible(true);
    }

    private void createContent() {

        setTitle("WTF!?");
        message = new JLabel("Welcome to WTF!? converter");

        bmpToWtf = new JRadioButton("BMP -> WTF", true);
        wtfTBmp = new JRadioButton("WTF -> BMP");
        ButtonGroup operationChoice = new ButtonGroup();
        operationChoice.add(bmpToWtf);
        operationChoice.add(wtfTBmp);
        JPanel buttonContainer = new JPanel();
        buttonContainer.setLayout(new BasicOptionPaneUI.ButtonAreaLayout(rootPaneCheckingEnabled, WIDTH));
        buttonContainer.add(bmpToWtf);
        buttonContainer.add(wtfTBmp);

        Container container = getContentPane();
        GroupLayout layout = new GroupLayout(container);
        container.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        JLabel sourceLabel = new JLabel("Source file: ");
        JLabel objectLabel = new JLabel("Object file: ");
        sourcePathField = new JTextField(30);
        objectPathField = new JTextField(30);
        JButton sourceBrowse = new JButton("Browse");
        JButton objectBrowse = new JButton("Browse");
        sourceBrowse.addActionListener(new BrowseButtonListener(sourcePathField, this, true));
        objectBrowse.addActionListener(new BrowseButtonListener(objectPathField, this, false));
        
        JButton convertButton = new JButton("Convert");
        convertButton.addActionListener(new ConvertButtonListener(this));
        JLabel lolLabel = new JLabel("Level of loss: ");
        SpinnerNumberModel spinModel = new SpinnerNumberModel(1, 0, MAX_LOL, 1);
        spinner = new JSpinner(spinModel);
        spinner.setMaximumSize(new Dimension(50,100));
        
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(message)
                .addComponent(buttonContainer)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(sourceLabel)
                    .addComponent(sourcePathField)
                    .addComponent(sourceBrowse))
                .addGroup(layout.createSequentialGroup()
                    .addComponent(objectLabel)
                    .addComponent(objectPathField)
                    .addComponent(objectBrowse))
                .addGroup(layout.createSequentialGroup()
                    .addComponent(convertButton)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(lolLabel)
                    .addComponent(spinner))
                );

        layout.setVerticalGroup(
                layout.createSequentialGroup()
                .addComponent(message)
                .addComponent(buttonContainer)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                    .addComponent(sourceLabel)
                    .addComponent(sourcePathField)
                    .addComponent(sourceBrowse)
                ).addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                    .addComponent(objectLabel)
                    .addComponent(objectPathField)
                    .addComponent(objectBrowse)
                ).addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                    .addComponent(convertButton)
                    .addComponent(lolLabel)
                    .addComponent(spinner)
                ));
    }

    private void convert() {
        if (bmpToWtf.isSelected()) {
            convertBmpToWtf();
        } else {
            convertWtfToBmp();
        }
    }

    private void convertBmpToWtf() {
        String[] arguments = new String[3];
        arguments[0] = "" +spinner.getValue();
        arguments[1] = sourcePathField.getText().trim();
        arguments[2] = objectPathField.getText().trim();
        try {
            Compression.commandLineFromBmpToWtf(arguments);
            message.setText("File converted.");
        } catch (IOException ex) {
            message.setText("Conversion failed.");
        }
    }

    private void convertWtfToBmp() {
        String[] arguments = new String[2];
        arguments[0] = sourcePathField.getText().trim();
        arguments[1] = objectPathField.getText().trim();
        try {
            Compression.commandLineFromWtfToBmp(arguments);
            message.setText("File converted.");
        } catch (FileNotFoundException ex) {
            message.setText("File not found.");
        } catch (IOException ex) {
            message.setText("Write error!");
        }
    }

    private class BrowseButtonListener implements ActionListener {

        private JTextField textField;
        private JFrame motherWindow;
        private boolean openFile;

        public BrowseButtonListener(JTextField textField, JFrame motherWindow, boolean openFile) {
            this.textField = textField;
            this.motherWindow = motherWindow;
            this.openFile = openFile;
        }

        @Override
        public void actionPerformed(ActionEvent ae) {
            if (openFile) {
                JFileChooser chooser = new JFileChooser();
                if (chooser.showOpenDialog(motherWindow) == JFileChooser.APPROVE_OPTION) {
                    File f = chooser.getSelectedFile();
                    textField.setText(f.getAbsolutePath());
                }
            } else {
                JFileChooser chooser = new JFileChooser();
                if (chooser.showSaveDialog(motherWindow) == JFileChooser.APPROVE_OPTION) {
                    File f = chooser.getSelectedFile();
                    textField.setText(f.getAbsolutePath());
                }
            }
        }
    }
    
    private class ConvertButtonListener implements ActionListener {
        
        private GraphicalUI gui;
        
        public ConvertButtonListener(GraphicalUI gui){
            this.gui = gui;
        }

        @Override
        public void actionPerformed(ActionEvent ae) {
            gui.convert();
        }
    }   
     
   
}
