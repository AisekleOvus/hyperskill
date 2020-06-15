import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextEditor extends JFrame {
    private final JFileChooser fChooser ;
    private static int searchIndex;
    private static volatile int regexSearchCrutch;
    public TextEditor() {
        searchIndex = 0;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(710, 600);
        setTitle("Text Editor");
        fChooser = new JFileChooser();
        fChooser.setName("FileChooser");
        JPanel mainPane = new JPanel();
        mainPane.setLayout(new BoxLayout(mainPane,BoxLayout.Y_AXIS));
            JPanel fileWorks = new JPanel();
                JTextField searchField = new JTextField();
                searchField.setName("SearchField");
                searchField.setPreferredSize(new Dimension(250,20));
                JButton saveFileButton = new JButton(new ImageIcon(new ImageIcon("ico/Save.png").getImage()
                .getScaledInstance(24,24, Image.SCALE_DEFAULT)));
                saveFileButton.setName("SaveButton");

                JButton loadFileButton = new JButton(new ImageIcon(new ImageIcon("ico/Load.png").getImage()
                        .getScaledInstance(24,24, Image.SCALE_DEFAULT)));
                loadFileButton.setName("OpenButton");

                JButton searchButton = new JButton(new ImageIcon(new ImageIcon("ico/Search.png").getImage()
                        .getScaledInstance(24,24, Image.SCALE_DEFAULT)));
                searchButton.setName("StartSearchButton");

                JButton prevMatchButton = new JButton(new ImageIcon(new ImageIcon("ico/Previous.png").getImage()
                        .getScaledInstance(24,24, Image.SCALE_DEFAULT)));
                prevMatchButton.setName("PreviousMatchButton");

                JButton nextMatchButton = new JButton(new ImageIcon(new ImageIcon("ico/Next.png").getImage()
                        .getScaledInstance(24,24, Image.SCALE_DEFAULT)));
                nextMatchButton.setName("NextMatchButton");

                JCheckBox useRegExCheckbox = new JCheckBox("Use regex");
                useRegExCheckbox.setName("UseRegExCheckbox");

            fileWorks.setPreferredSize(new Dimension(600,40));
            fileWorks.add(loadFileButton);
            fileWorks.add(saveFileButton);
            fileWorks.add(searchField);
            fileWorks.add(searchButton);
            fileWorks.add(prevMatchButton);
            fileWorks.add(nextMatchButton);
            fileWorks.add(useRegExCheckbox);

            JTextArea ta = new JTextArea();
            ta.setName("TextArea");
            JScrollPane taScrollPane = new JScrollPane(ta);
            taScrollPane.setName("ScrollPane");
            taScrollPane.setPreferredSize(new Dimension(600,520));
        mainPane.add(fileWorks);
        mainPane.add(Box.createVerticalStrut(2));
        mainPane.add(taScrollPane);
        mainPane.add(Box.createVerticalStrut(20));

        JMenuBar mb = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem openFileMItem = new JMenuItem("Load");
        openFileMItem.setName("MenuOpen");
        JMenuItem saveFileMItem = new JMenuItem("Save");
        saveFileMItem.setName("MenuSave");
        JMenuItem exitFileMItem = new JMenuItem("Exit");
        exitFileMItem.setName("MenuExit");
        JMenu searchMenu = new JMenu("Search");
        searchMenu.setName("MenuSearch");
        JMenuItem startSearchMItem = new JMenuItem("Start search");
        startSearchMItem.setName("MenuStartSearch");
        JMenuItem prevSearchMItem = new JMenuItem("Previous search");
        prevSearchMItem.setName("MenuPreviousMatch");
        JMenuItem nextSearchMItem = new JMenuItem("Next match");
        nextSearchMItem.setName("MenuNextMatch");
        JMenuItem regexMItem = new JMenuItem("Use regular expressions");
        regexMItem.setName("MenuUseRegExp");
/*      JCheckBoxMenuItem regexMItem = new JCheckBoxMenuItem("Use regular expressions");
        regexMItem.setName("MenuUSeRegExp");*/
        fileMenu.add(openFileMItem);
        fileMenu.add(saveFileMItem);
        fileMenu.addSeparator();
        fileMenu.add(exitFileMItem);
        fileMenu.setName("MenuFile");
        searchMenu.add(startSearchMItem);
        searchMenu.add(prevSearchMItem);
        searchMenu.add(nextSearchMItem);
        searchMenu.add(regexMItem);
        mb.add(fileMenu);
        mb.add(searchMenu);

        loadFileButton.addActionListener(actionEvent ->
            ta.setText(loadFromFile(fChooser))
        );
        saveFileButton.addActionListener(actionEvent ->
            saveToFile(ta.getText(), fChooser)
        );
        searchButton.addActionListener(actionEvent ->
                search(searchField.getText(), ta, 0, true, useRegExCheckbox.isSelected())
        );
        nextMatchButton.addActionListener(actionEvent ->
                search(searchField.getText(), ta, searchIndex, true, useRegExCheckbox.isSelected())
        );
        prevMatchButton.addActionListener(actionEvent ->
                search(searchField.getText(), ta, searchIndex, false,useRegExCheckbox.isSelected())
        );

        openFileMItem.addActionListener(actionEvent ->
            ta.setText(loadFromFile(fChooser))
        );
        saveFileMItem.addActionListener(actionEvent ->
            saveToFile(ta.getText(), fChooser)
        );
        exitFileMItem.addActionListener(actionEvent ->
            dispose()
        );
        startSearchMItem.addActionListener(actionEvent ->
                search(searchField.getText(), ta, 0, true, useRegExCheckbox.isSelected())
        );
        nextSearchMItem.addActionListener(actionEvent ->
                search(searchField.getText(), ta, searchIndex, true, useRegExCheckbox.isSelected())
        );
        prevSearchMItem.addActionListener(actionEvent ->
                search(searchField.getText(), ta, searchIndex, false, useRegExCheckbox.isSelected())
        );
        regexMItem.addActionListener(ActionEvent -> {
            useRegExCheckbox.setSelected(true);
            search(searchField.getText(), ta, 0, true, useRegExCheckbox.isSelected());
        });



        setJMenuBar(mb);
        add(fChooser);
        add(mainPane);
        setVisible(true);
        setLocationRelativeTo(null);
    }
    public static void main(String[] args) {
        new TextEditor();
    }

    /**
     *
     * @param what - search string
     * @param where - TextArea where to search
     * @param position - from which search goes on // position local link to searchIndex
     * @param direction - search direction: true - forward / false - backward
     * @param regex - should or not to use regex
     */
    public static void search(String what, JTextArea where, int position, boolean direction, boolean regex) {
        new Thread(()->{

            if(!regex) {
                String text = direction ? where.getText()
                        : position > what.length()
                        ? where.getText().substring(0, (position - what.length()))
                        : where.getText();
                int startIndex = direction ? text.indexOf(what, position)
                        : text.lastIndexOf(what);
                if(startIndex!=-1) {
                    searchIndex = startIndex + what.length();
                }else {
                    startIndex = direction ? where.getText().indexOf(what, 0)
                            : where.getText().lastIndexOf(what);
                    searchIndex = startIndex + what.length();
                }
                where.setCaretPosition(searchIndex);
                where.select(startIndex, searchIndex);
                where.grabFocus();

            }else{
              String text = direction ? where.getText()
                        : position > regexSearchCrutch
                        ? where.getText().substring(0,position-regexSearchCrutch)
                        : where.getText();
                Matcher matcher = Pattern.compile(what).matcher(text);
                MatchResult[] mResultArr = matcher.results().toArray(MatchResult[]::new);
                MatchResult peekResult = null;

                if(!direction && mResultArr.length == 0) {
                    matcher = Pattern.compile(what).matcher(where.getText());
                    mResultArr = matcher.results().toArray(MatchResult[]::new);
                }
                for(MatchResult mResult : mResultArr) {
                    if(direction && mResult.start()>=position){
                        peekResult = mResult;
                        break;
                    }
                    peekResult = !direction ? mResult : mResultArr[0];
                }
                if(peekResult!=null) {
                    regexSearchCrutch = peekResult.end() - peekResult.start();
                    where.setCaretPosition(peekResult.end());
                    where.select(peekResult.start(), peekResult.end());
                    where.grabFocus();
                    searchIndex = peekResult.end();
                }
            }
        }).start();
    }

    public static String loadFromFile(JFileChooser fChooser) {
        File file = fChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION
                ? fChooser.getSelectedFile() : null;
        StringBuilder text = new StringBuilder();
        int c;
        try(FileReader fr = new FileReader(file)) {
            while((c=fr.read())!=-1)
                text.append((char)c);
        }catch(Exception e) {
            //ioe.printStackTrace();
        }
        return text.toString();

    }

    public static void saveToFile(String text, JFileChooser fChooser) {
        File file = fChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION
                ? fChooser.getSelectedFile() : null;
        try(FileWriter fw = new FileWriter(file)) {
            fw.write(text);
            fw.flush();
        }catch(Exception e) {
            //ioe.printStackTrace();
        }
    }
}
