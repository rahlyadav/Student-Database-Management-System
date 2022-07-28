import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.table.AbstractTableModel;

public class StudentInformationSystem extends WindowAdapter implements ActionListener{
    int student_roll;

    LinkedList<Student> studentList = new LinkedList<>();

    Stack<Student> recoverList = new Stack<>();

    JFrame frame;
    JPanel mainPanel, showPanel;
    JMenuItem addStudentMI, deleteStudentMI, searchStudentMI, modifyStudentMI, recoverStudentMI, resetStudentsMI;
    StudentTable tableModel;

    File rollFile, recordFile, logFile;

    public static void main(String[] args) {
        StudentInformationSystem obj = new StudentInformationSystem();

        obj.setFiles();

        obj.getFiles();

        obj.startGUI();
    }

    void setFiles() {
        rollFile = new File("roll.txt");
        recordFile = new File("record.txt");
        logFile = new File("log.txt");
        try {
            if (!rollFile.exists()) {
                rollFile.createNewFile();
                FileWriter fw = new FileWriter(rollFile);
                fw.write("1");
                fw.close();
            }
            if (!recordFile.exists()) {
                recordFile.createNewFile();
            }
            if (!logFile.exists()) {
                logFile.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void getFiles() {
        int i;
        String num = "";
        try {
            FileReader fr = new FileReader(rollFile);
            while ((i = fr.read()) != -1) {
            num += (char)i;
            }
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!num.equals("")) {
            student_roll = Integer.parseInt(num);
        }

        try {
            Scanner scan = new Scanner(recordFile);

            while (scan.hasNextLine()) {
                String input = scan.nextLine();
                String k[] = input.split(" ");
                
                int roll = Integer.parseInt(k[0]);
                String fname = k[1];
                String lname = k[2];
                char grade = k[3].charAt(0);
                int marks = Integer.parseInt(k[4]);
                studentList.add(new Student(roll, grade, fname, lname, marks));
            }
    
            scan.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        
    }

    void startGUI() {
        frame = new JFrame("Student Information System");
        frame.addWindowListener(this);
        frame.getContentPane().setBackground(Color.LIGHT_GRAY);

        mainPanel = new JPanel(new BorderLayout());

        showStudents();

        setMenu();

        frame.add(mainPanel);

        frame.setSize(525, 525);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        JOptionPane.showMessageDialog(frame, "Welcome to Student Information System");

        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    }

    void showStudents() {
        showPanel = new JPanel();
        showPanel.setLayout(new BorderLayout());

        tableModel = new StudentTable(studentList);

        JTable tableStudent = new JTable(tableModel);

        JScrollPane sp = new JScrollPane(tableStudent);

        showPanel.add(sp, BorderLayout.CENTER);

        showPanel.validate();

        mainPanel.add(showPanel);
    }

    void setMenu() {
        JMenuBar menuBar = new JMenuBar();

        JMenu operationMenu = new JMenu("Operations");
        JMenu moreMenu = new JMenu("More");

        addStudentMI = new JMenuItem("Add");
        addStudentMI.addActionListener(this);
        deleteStudentMI = new JMenuItem("Delete");
        deleteStudentMI.addActionListener(this);
        searchStudentMI = new JMenuItem("Search");
        searchStudentMI.addActionListener(this);
        modifyStudentMI = new JMenuItem("Modify");
        modifyStudentMI.addActionListener(this);
        recoverStudentMI = new JMenuItem("Undo");
        recoverStudentMI.addActionListener(this);
        resetStudentsMI = new JMenuItem("Reset");
        resetStudentsMI.addActionListener(this);

        moreMenu.add(recoverStudentMI);
        moreMenu.add(resetStudentsMI);

        operationMenu.add(addStudentMI);
        operationMenu.add(deleteStudentMI);
        operationMenu.add(searchStudentMI);
        operationMenu.add(modifyStudentMI);
        operationMenu.add(moreMenu);

        menuBar.add(operationMenu);

        frame.add(menuBar);
        frame.setJMenuBar(menuBar);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addStudentMI){
            addStudent();
        } else if (e.getSource() == deleteStudentMI) {
            deleteStudent();
        } else if (e.getSource() == searchStudentMI) {
            searchStudent();
        } else if (e.getSource() == modifyStudentMI) {
            modifyStudent();
        } else if (e.getSource() == recoverStudentMI) {
            recoverStudent();
        } else if (e.getSource() == resetStudentsMI) {
            resetStudentsData();
        }
    }

    void addStudent() {
        JPanel addPanel = new JPanel();
        addPanel.setLayout(new BoxLayout(addPanel, BoxLayout.Y_AXIS));

        JTextField tfFName = new JTextField("", 10);
        tfFName.setToolTipText("Enter First Name (only characters allowed)");
        JTextField tfLName = new JTextField("", 10);
        tfLName.setToolTipText("Enter Last Name (only characters allowed)");
        JTextField tfGrade = new JTextField("", 10);
        tfGrade.setToolTipText("Enter Grade (only single character allowed)");
        JTextField tfMarks = new JTextField("", 10);
        tfMarks.setToolTipText("Enter Marks (only numbers allowed)");

        JPanel wrapperFName = new JPanel();
        wrapperFName.add(new JLabel("First Name"));
        wrapperFName.add(Box.createHorizontalStrut(5));
        wrapperFName.add(tfFName);
        JPanel wrapperLName = new JPanel();
        wrapperLName.add(new JLabel("Last Name"));
        wrapperLName.add(Box.createHorizontalStrut(5));
        wrapperLName.add(tfLName);
        JPanel wrapperGrade = new JPanel();
        wrapperGrade.add(new JLabel("Grade"));
        wrapperGrade.add(Box.createHorizontalStrut(30));
        wrapperGrade.add(tfGrade);
        JPanel wrapperMarks = new JPanel();
        wrapperMarks.add(new JLabel("Marks"));
        wrapperMarks.add(Box.createHorizontalStrut(30));
        wrapperMarks.add(tfMarks);

        JButton btnOK = new JButton("OK");
        JButton btnCancel = new JButton("Cancel");

        JPanel wrapperButtons = new JPanel();
        wrapperButtons.add(btnOK);
        wrapperButtons.add(Box.createHorizontalStrut(15));
        wrapperButtons.add(btnCancel);

        addPanel.add(wrapperFName);
        addPanel.add(wrapperLName);
        addPanel.add(wrapperGrade);
        addPanel.add(wrapperMarks);
        addPanel.add(wrapperButtons);

        JDialog dialogAdd = new JDialog(frame, "Add Student Record", true);
        dialogAdd.add(addPanel);

        dialogAdd.setSize(300, 250);
        dialogAdd.setLocationRelativeTo(null);

        btnOK.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!(tfFName.getText().length() < 1) && !(tfFName.getText().matches("-?\\d+")) &&
                    !(tfLName.getText().length() < 1) && !(tfLName.getText().matches("-?\\d+")) &&
                    (tfGrade.getText().length() == 1) && !(tfGrade.getText().matches("-?\\d+")) &&
                    !(tfMarks.getText().length() < 1) && (tfMarks.getText().matches("-?\\d+"))) {

                    String fname = tfFName.getText();
                    String lname = tfLName.getText();
                    char g = tfGrade.getText().charAt(0);
                    int mar = Integer.parseInt(tfMarks.getText());

                    Student std = new Student(student_roll, g, fname, lname, mar);
                    System.out.println(std);

                    studentList.add(std);
                    writeToLog(1, std.getRoll());
                    student_roll += 1;

                    tableModel.fireTableDataChanged();

                    dialogAdd.dispose();

                    showMessageDialog("Student Record Added Successfully");
                }
            }
        });

        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialogAdd.dispose();
            }
        });

        dialogAdd.setVisible(true);
    }

    void deleteStudent() {
        JPanel deletePanel = new JPanel();
        deletePanel.setLayout(new BoxLayout(deletePanel, BoxLayout.Y_AXIS));

        JTextField tfRoll = new JTextField("", 10);
        tfRoll.setToolTipText("Enter Roll No.");

        JPanel wrapperRoll = new JPanel();
        wrapperRoll.add(new JLabel("Roll no."));
        wrapperRoll.add(Box.createHorizontalStrut(10));
        wrapperRoll.add(tfRoll);

        JButton btnOK = new JButton("OK");
        JButton btnCancel = new JButton("Cancel");

        JPanel wrapperButtons = new JPanel();
        wrapperButtons.add(btnOK);
        wrapperButtons.add(Box.createHorizontalStrut(15));
        wrapperButtons.add(btnCancel);

        deletePanel.add(wrapperRoll);
        deletePanel.add(wrapperButtons);

        JDialog dialogDelete = new JDialog(frame, "Delete Student Record", true);
        dialogDelete.add(deletePanel);

        dialogDelete.setSize(300, 125);
        dialogDelete.setLocationRelativeTo(null);

        btnOK.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(tfRoll.getText().matches("-?\\d+")) {
                    int del_roll = Integer.parseInt(tfRoll.getText());
                    Student student = null;
                    String messString;

                    for (Student std : studentList) {
                        if (std.getRoll() == del_roll) {
                            student = std;
                            break;
                        }
                    }

                    if (student != null) {
                        messString = "Student Record Deleted Successfully";
                        studentList.remove(student);
                        recoverList.push(student);
                        writeToLog(2, student.getRoll());
                        tableModel.fireTableDataChanged();
                    } else {
                        messString = "Student Record Not Found";
                    }

                    dialogDelete.dispose();

                    showMessageDialog(messString);
                }
            }
        });

        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialogDelete.dispose();
            }
        });

        dialogDelete.setVisible(true);
    }

    void searchStudent() {
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.Y_AXIS));

        JTextField tfRoll = new JTextField("", 10);
        tfRoll.setToolTipText("Enter Roll No.");
        JTextField tfFName = new JTextField("", 10);
        tfFName.setToolTipText("Enter First Name");
        JTextField tfLName = new JTextField("", 10);
        tfLName.setToolTipText("Enter Last Name");

        JPanel wrapperRoll  = new JPanel();
        wrapperRoll.add(new JLabel("Roll No."));
        wrapperRoll.add(Box.createHorizontalStrut(25));
        wrapperRoll.add(tfRoll);
        JPanel wrapperFName = new JPanel();
        wrapperFName.add(new JLabel("First Name"));
        wrapperFName.add(Box.createHorizontalStrut(5));
        wrapperFName.add(tfFName);
        JPanel wrapperLName = new JPanel();
        wrapperLName.add(new JLabel("Last Name"));
        wrapperLName.add(Box.createHorizontalStrut(5));
        wrapperLName.add(tfLName);

        searchPanel.add(wrapperRoll);
        searchPanel.add(wrapperFName);
        searchPanel.add(wrapperLName);

        LinkedList<Student> searchList = new LinkedList<>();

        int result = JOptionPane.showConfirmDialog(frame, searchPanel, "Search", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            Integer searchRoll = 0;
            String searchFName = tfFName.getText();
            String searchLName = tfLName.getText();

            if (tfRoll.getText().matches("-?\\d+")) {
                searchRoll = Integer.parseInt(tfRoll.getText());
            }

            for(Student std : studentList) {
                if ((searchRoll.equals(std.getRoll())) || (searchFName.equals(std.getFname())) || (searchLName.equals(std.getLname()))) {
                    System.out.println(searchList.add(std));
                }
            }

            StudentTable searchModel = new StudentTable(searchList);

            JTable searchTable = new JTable(searchModel);

            JScrollPane searchTableSP = new JScrollPane(searchTable);

            JPanel showSearchPanel = new JPanel();
            showSearchPanel.add(searchTableSP);
            showSearchPanel.validate();

            JOptionPane.showConfirmDialog(frame, showSearchPanel, "Table", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        }

    }

    void modifyStudent() {
        JPanel modifyPanel = new JPanel();

        JTextField tfRoll = new JTextField("", 10);
        tfRoll.setToolTipText("Enter Roll No.");

        modifyPanel.add(new JLabel("Roll No."));
        modifyPanel.add(Box.createHorizontalStrut(5));
        modifyPanel.add(tfRoll);

        int result = JOptionPane.showConfirmDialog(frame, modifyPanel, "Modify", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            if (tfRoll.getText().matches("-?\\d+")) {
                int modifyRoll = Integer.parseInt(tfRoll.getText());
                Student student = null;

                for (Student std : studentList) {
                    if (std.getRoll() == modifyRoll) {
                        student = std;
                        break;
                    }
                }

                if (student != null) {
                    JPanel showStudentPanel = new JPanel();
                    showStudentPanel.setLayout(new BoxLayout(showStudentPanel, BoxLayout.Y_AXIS));

                    JTextField tfShowRoll = new JTextField(10);
                    tfShowRoll.setText(student.getRoll()+"");
                    tfShowRoll.setEditable(false);
                    JTextField tfShowFName = new JTextField(10);
                    tfShowFName.setText(student.getFname());
                    JTextField tfShowLName = new JTextField(10);
                    tfShowLName.setText(student.getLname());
                    JTextField tfShowGrade = new JTextField(10);
                    tfShowGrade.setText(student.getGrade()+"");
                    JTextField tfShowMarks = new JTextField(10);
                    tfShowMarks.setText(student.getMarks()+"");

                    JPanel wrapperRoll = new JPanel();
                    wrapperRoll.add(new JLabel("Roll No."));
                    wrapperRoll.add(Box.createHorizontalStrut(25));
                    wrapperRoll.add(tfShowRoll);
                    JPanel wrapperFName = new JPanel();
                    wrapperFName.add(new JLabel("First Name"));
                    wrapperFName.add(Box.createHorizontalStrut(5));
                    wrapperFName.add(tfShowFName);
                    JPanel wrapperLname = new JPanel();
                    wrapperLname.add(new JLabel("Last Name"));
                    wrapperLname.add(Box.createHorizontalStrut(5));
                    wrapperLname.add(tfShowLName);
                    JPanel wrapperGrade = new JPanel();
                    wrapperGrade.add(new JLabel("Grade"));
                    wrapperGrade.add(Box.createHorizontalStrut(30));
                    wrapperGrade.add(tfShowGrade);
                    JPanel wrapperMarks = new JPanel();
                    wrapperMarks.add(new JLabel("Marks"));
                    wrapperMarks.add(Box.createHorizontalStrut(30));
                    wrapperMarks.add(tfShowMarks);

                    JButton btnOK = new JButton("OK");
                    JButton btnCancel = new JButton("Cancel");

                    JPanel wrapperButtons = new JPanel();
                    wrapperButtons.add(btnOK);
                    wrapperButtons.add(Box.createHorizontalStrut(5));
                    wrapperButtons.add(btnCancel);

                    showStudentPanel.add(wrapperRoll);
                    showStudentPanel.add(wrapperFName);
                    showStudentPanel.add(wrapperLname);
                    showStudentPanel.add(wrapperGrade);
                    showStudentPanel.add(wrapperMarks);
                    showStudentPanel.add(wrapperButtons);

                    JDialog dialogModify = new JDialog(frame, "Modify Student Record", true);
                    dialogModify.add(showStudentPanel);

                    dialogModify.setSize(300, 250);
                    dialogModify.setLocationRelativeTo(null);

                    btnOK.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            if (!(tfShowFName.getText().length() < 1) && !(tfShowFName.getText().matches("-?\\d+")) &&
                            !(tfShowLName.getText().length() < 1) && !(tfShowLName.getText().matches("-?\\d+")) &&
                            (tfShowGrade.getText().length() == 1) && !(tfShowGrade.getText().matches("-?\\d+")) &&
                            !(tfShowMarks.getText().length() < 1) && (tfShowMarks.getText().matches("-?\\d+"))) {

                                int roll = Integer.parseInt(tfShowRoll.getText());
                                String fname = tfShowFName.getText();
                                String lname = tfShowLName.getText();
                                char g = tfShowGrade.getText().charAt(0);
                                int marks = Integer.parseInt(tfShowMarks.getText());

                                changeStudentData(roll, fname, lname, g, marks);
                                writeToLog(3, roll);

                                tableModel.fireTableDataChanged();

                                dialogModify.dispose();

                                showMessageDialog("Student Record Modified Successfully");
                            }
                        }
                    });

                    btnCancel.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            dialogModify.dispose();
                        }
                    });

                    dialogModify.setVisible(true);

                } else {
                    showMessageDialog("Student Record Not Found");
                }
            }
        }
    }

    void changeStudentData(int roll, String fname, String lname, char g, int marks) {
        Student student = null;

        for (Student std : studentList) {
            if (std.getRoll() == roll) {
                student = std;
                break;
            }
        }

        student.setFname(fname);
        student.setLname(lname);
        student.setGrade(g);
        student.setMarks(marks);
    }

    void recoverStudent() {
        if (recoverList.size() > 0) {
            Student student = recoverList.pop();
            studentList.add(student);

            writeToLog(4, student.getRoll());

            studentList.sort(new Comparator<Student>() {
                @Override
                public int compare(Student o1, Student o2) {
                    return o1.getRoll() - o2.getRoll();
                }
            });

            tableModel.fireTableDataChanged();
        }
    }

    void resetStudentsData() {
        int result = JOptionPane.showConfirmDialog(frame, "Are you sure?", "Reset Student Database", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            student_roll = 1;

            studentList.clear();
            tableModel.fireTableDataChanged();

            try {
                FileWriter fwr = new FileWriter(rollFile);
                fwr.write(1+"");

                PrintWriter pwRecord = new PrintWriter(recordFile);
                PrintWriter pwLog = new PrintWriter(logFile);

                fwr.close();
                pwRecord.close();
                pwLog.close();
            } catch (Exception e) {
            }

            recoverList.clear();
        }
    }

    public void windowClosing(WindowEvent e) {
        int a = JOptionPane.showConfirmDialog(frame, "Are u sure?");
        if (a == JOptionPane.YES_OPTION) {
            try {
                FileWriter fwRoll = new FileWriter(rollFile);
                fwRoll.write(student_roll+"");
                fwRoll.close();

                FileWriter fwRecord = new FileWriter(recordFile);
                for (Student std : studentList) {
                    String input = std.getRoll()+" "+std.getFname()+" "+std.getLname()+" "+std.getGrade()+" "+std.getMarks();
                    fwRecord.write(input+"\n");
                }
                fwRecord.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        }
    }

    void writeToLog(int type, int roll) {
        Timestamp instance = Timestamp.from(Instant.now());
        String task = null;
        if (type == 1) {
            task = "Added_____";
        } else if (type == 2) {
            task = "Deleted___";
        } else if (type == 3) {
            task = "Modified__";
        } else if (type == 4) {
            task = "Recovered_";
        }
        String LOG = task+roll+"     "+instance.toString()+"\n";

        try {
            BufferedWriter logWriter = new BufferedWriter(new FileWriter(logFile, true));
            logWriter.write(LOG);
            logWriter.close();
        } catch (Exception e) {
        }
    }

    void showMessageDialog(String message) {
        JOptionPane messageOP = new JOptionPane(message, JOptionPane.INFORMATION_MESSAGE);
        JDialog dialogMessage = messageOP.createDialog("Message");
        
        Timer timer = new Timer(1500, event -> dialogMessage.setVisible(false));
        timer.setRepeats(false);
        timer.start();

        dialogMessage.setVisible(true);
    }

}


class StudentTable extends AbstractTableModel {

    private LinkedList<Student> myList;

    String columns[] = {"Roll no", "First Name", "Last Name", "Grade", "Marks"};

    public StudentTable(LinkedList<Student> myList){
        this.myList = myList;
    }

    @Override
    public int getRowCount() {
        return this.myList.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int column) {
        return columns[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Student std = myList.get(rowIndex);
        if (columnIndex == 0) {
            return std.getRoll();
        } else if (columnIndex == 1) {
            return std.getFname();
        } else if (columnIndex == 2) {
            return std.getLname();
        } else if (columnIndex == 3) {
            return std.getGrade();
        } else if (columnIndex == 4) {
            return std.getMarks();
        } else return null;
    }
    
}

class Student {
    int roll;
    char grade;
    String fname, lname;
    int marks;

    Student(int roll, char grade, String fname, String lname, int marks) {
        this.roll = roll;
        this.grade = grade;
        this.fname = fname;
        this.lname = lname;
        this.marks = marks;
    }

    @Override
    public String toString() {
        return "Student <Roll : " + roll + ", Grade : " + grade + ", Name : " + fname + " " + lname + ", Marks : "
                + marks+">";
    }

    public int getRoll() {
        return roll;
    }

    public String getFname() {
        return fname;
    }

    public String getLname() {
        return lname;
    }

    public char getGrade() {
        return grade;
    }

    public int getMarks() {
        return marks;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public void setGrade(char grade) {
        this.grade = grade;
    }

    public void setMarks(int marks) {
        this.marks = marks;
    }
}
