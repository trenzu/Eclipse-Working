import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.Color;
import javax.swing.JButton;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.awt.event.ActionEvent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.JCheckBox;
import javax.swing.ImageIcon;

public class Application {

	private JFrame frame;
	private JTextField textField_ISBN;
	private JTextField textField_TITLE;
	private JTextField textField_AUTHOR;
	private JTable table;
	private JTextField textField_ID;
	private JTextField textField_CARD;
	private JTextField textField_FNAME;
	private JTextField textField_LNAME;
	private JTable table_1;
	private JTextField textField;
	private JTextField textField_1;
	private JTextField textField_2;
	private JTextField textField_3;
	private JTextField textField_4;
	private JTextField textField_5;
	private JTextField textField_6;
	private JTextField textField_7;
	private JTextField textField_8;
	private JTextField textField_9;
	private JTextField textField_10;
	private JTextField textField_11;
	private JTextField textField_12;
	private JTextField message;
	private JTextField textField_13;
	private JTextField textField_14;
	private JTable table_2;
	private JTextField message1;

	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Application window = new Application();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	
	public Application() {
		initialize();
	}

	
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 860, 491);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(0, 0, 844, 449);
		frame.getContentPane().add(tabbedPane);
		
		JPanel SEARCH = new JPanel();
		tabbedPane.addTab("SEARCH", null, SEARCH, null);
		SEARCH.setLayout(null);
		
		JLabel lblNewLabel_1 = new JLabel("SEARCH");
		lblNewLabel_1.setBounds(46, 13, 264, 49);
		lblNewLabel_1.setForeground(Color.BLACK);
		lblNewLabel_1.setFont(new Font("Times New Roman", Font.BOLD, 28));
		SEARCH.add(lblNewLabel_1);
		
		JLabel lblNewLabel_2 = new JLabel("ISBN");
		lblNewLabel_2.setBounds(32, 75, 81, 30);
		SEARCH.add(lblNewLabel_2);
		
		JLabel lblNewLabel_3 = new JLabel("TITLE");
		lblNewLabel_3.setBounds(32, 118, 81, 25);
		SEARCH.add(lblNewLabel_3);
		
		JLabel lblNewLabel_4 = new JLabel("AUTHOR");
		lblNewLabel_4.setBounds(31, 156, 56, 16);
		SEARCH.add(lblNewLabel_4);
		
		textField_ISBN = new JTextField();
		textField_ISBN.setBounds(143, 79, 116, 22);
		SEARCH.add(textField_ISBN);
		textField_ISBN.setColumns(10);
		
		textField_TITLE = new JTextField();
		textField_TITLE.setBounds(143, 119, 116, 22);
		SEARCH.add(textField_TITLE);
		textField_TITLE.setColumns(10);
		
		textField_AUTHOR = new JTextField();
		textField_AUTHOR.setBounds(143, 153, 116, 22);
		SEARCH.add(textField_AUTHOR);
		textField_AUTHOR.setColumns(10);
		
		JButton btnNewButton = new JButton("SEARCH");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				 DB_Connection con=null;
				try{
			        		DefaultTableModel model3 = (DefaultTableModel) table.getModel();
							model3.setRowCount(0);
							String id = textField_ISBN.getText();
							String title = textField_TITLE.getText();
							String author = textField_AUTHOR.getText();
							con = new DB_Connection();
							 
							String q= null;
							 
							
							String book_id,book_title,author_name, branch_id,branch_name,no_of_copies,available_copies="";
						        
						    
						            
						            if(!id.isEmpty()){
							             q="SELECT B.isbn,B.TITLE,B.BRANCH_ID,B.branch_name,fullname,no_of_copies" +
							                        " FROM book_loans L  RIGHT JOIN " +
							                        "(select c.branch_id , c.no_of_copies, lb.branch_name,bk.isbn, bk.title, au.fullname " +
							                        " from " +
							                        " (library_branch lb left join " +
							                        " ( book_copies c left join " +
							                        " (book bk left join (book_authors ba left join authors au  on ba.author_id = au.author_id ) on bk.isbn=ba.isbn  ) " +
							                        " on bk.isbn= c.isbn  ) " +
							                        " on c.branch_id = lb.branch_id )) as B " +
							                        " ON (L.isbn=B.isbn AND L.BRANCH_ID=B.BRANCH_ID) " +
							                        " where B.isbn like '%"+id+"%'or B.fullname like '%"+id+"%' or B.title like '%"+id+"%' Group by  B.branch_id, B.isbn;";
							            }
						            else{
						            	message.setText("Enter atleast on field");
						            }
						          
						          System.out.println(q);  
						        ResultSet r = con.Connection(q);
						            
						          
						           
						            if(!r.next()){
						                    message.setText("No books");
						            }
						            else{
						                do{
						                   book_id = r.getString("isbn");
						                   book_title = r.getString("title");
						                   author_name = r.getString("fullname");
						                   branch_id = r.getString("branch_id");
						                   branch_name=r.getString("branch_name");
						                   no_of_copies = r.getString("no_of_copies");
						                    String q2 = "select (c.no_of_copies- count(*)) as no_of_available_copies from book_loans l "
						                             + "right join book_copies c on l.isbn=c.isbn "+
						                             "where c.branch_id = l.branch_id and l.date_in='1885-01-01' and c.isbn = '"+book_id+"' and c.branch_id='"+branch_id+"';";
						                    
						                   
						                  ResultSet  rs1 = con.Connection(q2);
						                  if(rs1.next()){
						                        available_copies= rs1.getString("no_of_available_copies");
						                     if(Integer.parseInt(available_copies.toString())<0){
						                        Object obj1[] ={book_id,book_title,author_name,branch_id,branch_name,no_of_copies,"0"};
						                        model3.addRow(obj1);
						                    }
						                    else{
						                        Object obj1[] ={book_id,book_title,author_name,branch_id,branch_name,no_of_copies,available_copies};
						                        model3.addRow(obj1);
						                    }
						                  }
						                    }while(r.next());
						            }
						                
						        
						          
						}
				catch(SQLException e4)
				{
					e4.printStackTrace();
				}
				finally {
					if(con!=null)
						try {
							con.close();
						} catch (SQLException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
				}
			}});
		btnNewButton.setBounds(363, 118, 97, 25);
		SEARCH.add(btnNewButton);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(12, 197, 815, 209);
		SEARCH.add(scrollPane);
		
		table = new JTable();
		table.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"ISBN", "title", "author", "branch_id", "branch_name", "No_of_copies", "available_copies"
			}
		));
		scrollPane.setViewportView(table);
		
		message = new JTextField();
		message.setBounds(303, 43, 457, 68);
		SEARCH.add(message);
		message.setColumns(10);
		
		JPanel CHECK_IN = new JPanel();
		tabbedPane.addTab("CHECK-IN", null, CHECK_IN, null);
		CHECK_IN.setLayout(null);
		
		JLabel lblNewLabel_5 = new JLabel("CHECK-IN");
		lblNewLabel_5.setFont(new Font("Times New Roman", Font.BOLD, 28));
		lblNewLabel_5.setBounds(248, 13, 264, 44);
		CHECK_IN.add(lblNewLabel_5);
		
		JLabel lblNewLabel_6 = new JLabel("ISBN");
		lblNewLabel_6.setBounds(36, 81, 56, 16);
		CHECK_IN.add(lblNewLabel_6);
		
		JLabel lblNewLabel_7 = new JLabel("CARD_NO");
		lblNewLabel_7.setBounds(36, 143, 56, 16);
		CHECK_IN.add(lblNewLabel_7);
		
		JLabel lblNewLabel_8 = new JLabel("FNAME");
		lblNewLabel_8.setBounds(402, 81, 56, 16);
		CHECK_IN.add(lblNewLabel_8);
		
		JLabel lblNewLabel_9 = new JLabel("LNAME");
		lblNewLabel_9.setBounds(402, 128, 56, 16);
		CHECK_IN.add(lblNewLabel_9);
		
		textField_ID = new JTextField();
		textField_ID.setBounds(142, 78, 116, 22);
		CHECK_IN.add(textField_ID);
		textField_ID.setColumns(10);
		
		textField_CARD = new JTextField();
		textField_CARD.setBounds(142, 140, 116, 22);
		CHECK_IN.add(textField_CARD);
		textField_CARD.setColumns(10);
		
		textField_FNAME = new JTextField();
		textField_FNAME.setBounds(511, 78, 116, 22);
		CHECK_IN.add(textField_FNAME);
		textField_FNAME.setColumns(10);
		
		textField_LNAME = new JTextField();
		textField_LNAME.setBounds(511, 140, 116, 22);
		CHECK_IN.add(textField_LNAME);
		textField_LNAME.setColumns(10);
		
		JButton btnNewButton_1 = new JButton("BOOK_LOANS");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				try{
					
					String isbn = textField_ID.getText();
					String fname = textField_FNAME.getText();
					String lname = textField_LNAME.getText();
					String card_no = textField_CARD.getText();
					DefaultTableModel model1 = (DefaultTableModel) table_1.getModel();
					model1.setRowCount(0);
					String q1= "select l.isbn,l.card_no,bw.fname,bw.lname, l.loan_id, l.branch_id,datediff(curdate(),due_date) as diff from book_loans l "+
		                    "join borrower bw on l.card_no = bw.card_no "+
		                 " where l.date_in='1885-01-01' and (l.isbn='"+isbn+"' or bw.fname ='"+fname+"' or bw.lname = '"+lname+"' or l.card_no='"+card_no+"');";
					DB_Connection chk_in = new DB_Connection();
					ResultSet r = chk_in.Connection(q1);
					
					if(!r.next()){
						
					
						textField_13.setText("No book loans exists");
						
					}
					else{
						do{
							String id = r.getString("isbn");
							String card_number = r.getString("card_no");
							String First_Name = r.getString("fname");
							String Last_Name = r.getString("lname");
							String loan_id = r.getString("loan_id");
							String difference = r.getString("diff");
							
							Object obj1[] = {id,card_number,First_Name,Last_Name,loan_id,difference}; 
							model1.addRow(obj1);
						}while(r.next());
						
						
					}
					
					
					
				
			}catch(Exception ex){
				ex.printStackTrace();
			}
			}
			
				
			});
		btnNewButton_1.setBounds(83, 364, 132, 25);
		CHECK_IN.add(btnNewButton_1);
		
		JButton btnNewButton_2 = new JButton("CHECK-IN");
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int rowIndex=table_1.getSelectedRow();
				DB_Connection chk = new DB_Connection();
				if(rowIndex == -1){
					
					textField_13.setText("select atleast a single row");
				}
				else{
				String loan_id = (String) table_1.getValueAt(rowIndex, 4);
			        try{
			            String q1= "update book_loans set date_in = curdate() where loan_id = '"+loan_id+"';";
			           chk.Connection1(q1);
			       	textField_13.setText("Date in is updated in book_loans");
			            
			           }
			        catch (Exception e9) {
			            e9.printStackTrace();
			        }        
			        
			       
				}
			}
			
			
			
		});
		btnNewButton_2.setBounds(640, 364, 97, 25);
		CHECK_IN.add(btnNewButton_2);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(12, 172, 799, 176);
		CHECK_IN.add(scrollPane_1);
		
		table_1 = new JTable();
		table_1.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"ID", "card_no", "Fname", "LName", "Loan_ID", "Difference"
			}
		));
		scrollPane_1.setViewportView(table_1);
		
		textField_13 = new JTextField();
		textField_13.setBounds(258, 365, 307, 24);
		CHECK_IN.add(textField_13);
		textField_13.setColumns(10);
		
		JPanel CHECK_OUT = new JPanel();
		tabbedPane.addTab("CHECK-OUT", null, CHECK_OUT, null);
		CHECK_OUT.setLayout(null);
		
		JLabel lblNewLabel_10 = new JLabel("CHECK-OUT");
		lblNewLabel_10.setFont(new Font("Times New Roman", Font.BOLD, 28));
		lblNewLabel_10.setBounds(235, 23, 340, 50);
		CHECK_OUT.add(lblNewLabel_10);
		
		JLabel lblNewLabel_11 = new JLabel("ISBN*");
		lblNewLabel_11.setBounds(41, 101, 56, 16);
		CHECK_OUT.add(lblNewLabel_11);
		
		JLabel lblNewLabel_12 = new JLabel("CARD_NO*");
		lblNewLabel_12.setBounds(41, 149, 80, 16);
		CHECK_OUT.add(lblNewLabel_12);
		
		JLabel lblNewLabel_13 = new JLabel("BRANCH-ID*");
		lblNewLabel_13.setBounds(41, 198, 80, 16);
		CHECK_OUT.add(lblNewLabel_13);
		
		textField = new JTextField();
		textField.setBounds(153, 98, 116, 22);
		CHECK_OUT.add(textField);
		textField.setColumns(10);
		
		textField_1 = new JTextField();
		textField_1.setBounds(153, 146, 116, 22);
		CHECK_OUT.add(textField_1);
		textField_1.setColumns(10);
		
		textField_2 = new JTextField();
		textField_2.setBounds(153, 195, 116, 22);
		CHECK_OUT.add(textField_2);
		textField_2.setColumns(10);
		
		JButton btnNewButton_3 = new JButton("CHECK-OUT");
		btnNewButton_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				String book_id = textField.getText();
				String card = textField_1.getText();
				String branch_id = textField_2.getText();
				if(!book_id.isEmpty() && !card.isEmpty() && !branch_id.isEmpty()){
				String query1 = "select count(loan_id) from book_loans where date_in='1885-01-01' and card_no = '"+card+"';";
				DB_Connection ch0 = new DB_Connection();
				ResultSet cho = ch0.Connection(query1);
				
				try {
					if(cho.next()){
						
						String count = cho.getString(1);
					 int count1 = Integer.parseInt(count);
					 
					 if(count1<3){
						 
						 
							 
String query2 = "select (no_of_copies- count(l.isbn)) as copies from book_loans l "
        + "right join book_copies c on l.isbn=c.isbn "+
       "where l.branch_id=c.branch_id and l.date_in='1885-01-01' and c.isbn = '"+book_id+"' and c.branch_id='"+branch_id+"';";
                  ResultSet ch1 = ch0.Connection(query2) ;
                  
                  if(ch1.next()){
                	  
                	  String copies = ch1.getString("copies");
                	  int copies1 = Integer.parseInt(copies);
                	  System.out.println(copies1);
                	  
                	  if(copies1 >0){
                		  
  String query3 = "insert into book_loans (isbn,branch_id,card_no,date_out,due_date)" +
          "values ('"+book_id+"', '"+branch_id+"', '"+card+"', curdate(), DATE_ADD(date_out,INTERVAL 14 DAY));";              		  
                     int ch2 = ch0.Connection1(query3);
                     textField_3.setText("New book_loan has created");
                     
  }
                	  
                	  else{
                		  
                		  textField_3.setText("No available copies");
                	  }
                	  
                	  }
                  
                  
							 
							
							 
						 } 
					 
					 else{
						
						 textField_3.setText("Borrower already has 3 books");
						
					 }
						
						
					}
					
				
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				}
				
				else{
					
					textField_3.setText("Enter all required fileds");
				}
				
			}
		});
		btnNewButton_3.setBounds(450, 145, 125, 25);
		CHECK_OUT.add(btnNewButton_3);
		
		textField_3 = new JTextField();
		textField_3.setBounds(402, 332, 380, 55);
		CHECK_OUT.add(textField_3);
		textField_3.setColumns(10);
		
		JLabel lblNewLabel_14 = new JLabel("MESSAGE");
		lblNewLabel_14.setBounds(472, 294, 193, 25);
		CHECK_OUT.add(lblNewLabel_14);
		
		JLabel lblNewLabel_25 = new JLabel("All * are required fields");
		lblNewLabel_25.setBounds(536, 23, 265, 34);
		CHECK_OUT.add(lblNewLabel_25);
		
		JPanel BORROWER = new JPanel();
		tabbedPane.addTab("BORROWER", null, BORROWER, null);
		BORROWER.setLayout(null);
		
		JLabel lblNewLabel_15 = new JLabel("FNAME*");
		lblNewLabel_15.setBounds(27, 74, 56, 16);
		BORROWER.add(lblNewLabel_15);
		
		JLabel lblNewLabel_16 = new JLabel("LNAME*");
		lblNewLabel_16.setBounds(27, 119, 56, 16);
		BORROWER.add(lblNewLabel_16);
		
		JLabel lblNewLabel_17 = new JLabel("SSN*");
		lblNewLabel_17.setBounds(27, 172, 56, 16);
		BORROWER.add(lblNewLabel_17);
		
		JLabel lblNewLabel_18 = new JLabel("ADDRESS*");
		lblNewLabel_18.setBounds(27, 225, 71, 16);
		BORROWER.add(lblNewLabel_18);
		
		JLabel lblNewLabel_19 = new JLabel("STATE");
		lblNewLabel_19.setBounds(371, 74, 56, 16);
		BORROWER.add(lblNewLabel_19);
		
		JLabel lblNewLabel_20 = new JLabel("CITY");
		lblNewLabel_20.setBounds(371, 119, 56, 16);
		BORROWER.add(lblNewLabel_20);
		
		JLabel lblNewLabel_21 = new JLabel("EMAIL");
		lblNewLabel_21.setBounds(371, 175, 56, 16);
		BORROWER.add(lblNewLabel_21);
		
		JLabel lblNewLabel_22 = new JLabel("PHONE");
		lblNewLabel_22.setBounds(371, 225, 56, 16);
		BORROWER.add(lblNewLabel_22);
		
		textField_4 = new JTextField();
		textField_4.setBounds(111, 71, 116, 22);
		BORROWER.add(textField_4);
		textField_4.setColumns(10);
		
		textField_5 = new JTextField();
		textField_5.setBounds(497, 71, 116, 22);
		BORROWER.add(textField_5);
		textField_5.setColumns(10);
		
		textField_6 = new JTextField();
		textField_6.setBounds(111, 129, 116, 22);
		BORROWER.add(textField_6);
		textField_6.setColumns(10);
		
		textField_7 = new JTextField();
		textField_7.setBounds(111, 172, 116, 22);
		BORROWER.add(textField_7);
		textField_7.setColumns(10);
		
		textField_8 = new JTextField();
		textField_8.setBounds(111, 222, 116, 22);
		BORROWER.add(textField_8);
		textField_8.setColumns(10);
		
		textField_9 = new JTextField();
		textField_9.setBounds(497, 116, 116, 22);
		BORROWER.add(textField_9);
		textField_9.setColumns(10);
		
		textField_10 = new JTextField();
		textField_10.setBounds(497, 166, 116, 22);
		BORROWER.add(textField_10);
		textField_10.setColumns(10);
		
		textField_11 = new JTextField();
		textField_11.setBounds(497, 222, 116, 22);
		BORROWER.add(textField_11);
		textField_11.setColumns(10);
		
		JButton btnNewButton_4 = new JButton("ADD");
		btnNewButton_4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			
				String s1 = textField_4.getText();
				String s2 = textField_6.getText();
				String s3 = textField_7.getText();
				String s4 = textField_8.getText();
				String s5 = textField_5.getText();
				String s6 = textField_9.getText();
				String s7 = textField_10.getText();
				String s8 = textField_11.getText();
				try{
				if(!s1.isEmpty() && !s2.isEmpty() && !s3.isEmpty() && !s4.isEmpty()){
					if(s3.matches("^[0-9]{3}\\-[0-9]{2}\\-[0-9]{4}$")){
				String q1 = "select count(*)from borrower where ssn ='"+s3+"';";
				DB_Connection b = new DB_Connection();
				ResultSet r = b.Connection(q1);
					
					if(r.next()){
						
						String count = r.getString(1);
						int c = Integer.parseInt(count);
						
						if(c==0){
							
							
							String q2 = "Insert into borrower(ssn,fname,lname,email,address,city,state,phone) values ('"+s3+"','"+s1+"','"+s2+"','"+s7+"','"+s4+"','"+s5+"','"+s6+"','"+s8+"');";
							int r1 = b.Connection1(q2);
							if(r1!=0){
							textField_12.setText("New Borrower is created");
							}
						}
						}
						else{
							
							textField_12.setText("SSN already Exists");
						}
					}
					else{
						textField_12.setText("Enter a valid SSN");
					}
					}
					
				
					else{
					
					textField_12.setText("All required fields must be filled");
					}
				}catch(SQLException e){
					
					e.printStackTrace();
				}
				
					
					
				
			}
			
		});
		btnNewButton_4.setBounds(265, 299, 97, 25);
		BORROWER.add(btnNewButton_4);
		
		textField_12 = new JTextField();
		textField_12.setBounds(366, 346, 406, 46);
		BORROWER.add(textField_12);
		textField_12.setColumns(10);
		
		JLabel lblNewLabel_24 = new JLabel("All * are required fields");
		lblNewLabel_24.setBounds(496, 13, 295, 36);
		BORROWER.add(lblNewLabel_24);
		
		JPanel FINES = new JPanel();
		tabbedPane.addTab("FINES", null, FINES, null);
		FINES.setLayout(null);
		
		JLabel lblNewLabel_23 = new JLabel("FINES");
		lblNewLabel_23.setFont(new Font("Times New Roman", Font.BOLD, 28));
		lblNewLabel_23.setBounds(190, 13, 280, 44);
		FINES.add(lblNewLabel_23);
		
		JLabel lblNewLabel_26 = new JLabel("card_no");
		lblNewLabel_26.setBounds(81, 69, 122, 44);
		FINES.add(lblNewLabel_26);
		
		JLabel lblNewLabel_27 = new JLabel("ID");
		lblNewLabel_27.setBounds(190, 83, 56, 16);
		FINES.add(lblNewLabel_27);
		
		JCheckBox check_box = new JCheckBox("show previously paid");
		check_box.setBounds(642, 46, 167, 29);
		FINES.add(check_box);
		
		JButton btnNewButton_5 = new JButton("Get Details");
		btnNewButton_5.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				String isbn,f_loan_id,b_loan_id,paid,paid_1,book_returned,date_in,curdate_diff,fine;
		        String q2=null;
		        String card_no = textField_14.getText();
		        boolean show_previous1 = check_box.isSelected();
		        DefaultTableModel model3 = (DefaultTableModel) table_2.getModel();
		        model3.setRowCount(0);
		        try{
		            String q1= "select f.loan_id f_loan,l.loan_id b_loan, l.isbn, f.fine_amt,f.paid, l.date_in,datediff(curdate(),due_date)"
		                    + " from book_loans l left join fines f on l.loan_id=f.loan_id where (l.date_in > l.due_date or l.date_in='1885-01-01') and l.card_no='"+card_no+"';";
		            DB_Connection fines2 = new DB_Connection();
		            ResultSet rs = fines2.Connection(q1);
		            double  fine_amt =0;
		            if(!rs.next()){
		                if(card_no.matches("^[0-9]+"))
		                    message1.setText("No bookloans");
		                else
		                    message1.setText("Entered value is not valid");
		            }
		            else{
		                do{
		                isbn = rs.getString("isbn");
		                f_loan_id = rs.getString("f_loan");
		                b_loan_id = rs.getString("b_loan");
		                fine = rs.getString("fine_amt");
		                paid_1 = rs.getString("paid");
		                date_in = rs.getString("date_in");
		                curdate_diff=rs.getString("datediff(curdate(),due_date)");
		                //System.out.println(f_loan_id==null);
		                //System.out.println(paid_1 == null);
		                if(paid_1 !=null && paid_1.toString().equals("1"))
		                    paid="True";
		                else
		                    paid="False";
		                
		                if(f_loan_id==null){ //&& Integer.parseInt(datediff)<0 && date_in != "1885-01-01"){
		                    message1.setText("Your account has no fines to be paid");
		                }
		                else{
		                    if(show_previous1){
		                        if(date_in.equals("1885-01-01")){
		                            fine_amt = 0.25*Double.parseDouble(curdate_diff);
		                            book_returned = "No";
		                            paid = "False";
		                            Object obj[]={b_loan_id,isbn,fine_amt,paid,book_returned};
		                            model3.addRow(obj);
		                        }
		                        else{
		                            Object obj[]={f_loan_id,isbn,fine,paid,"Yes"};
		                            model3.addRow(obj);
		                            if(paid=="False")
		                                message1.setText("Select a row for which you wish to pay fine");
		                            else
		                                message1.setText("No Current Loans to pay");
		                        }
		                    }
		                    else{
		                        if(paid=="False"){
		                            if(date_in.equals("1885-01-01")){
		                            fine_amt = 0.25*Double.parseDouble(curdate_diff);
		                            book_returned = "No";
		                            Object obj[]={b_loan_id,isbn,fine_amt,paid,book_returned};
		                            model3.addRow(obj);
		                            }
		                            else{
		                            Object obj[]={f_loan_id,isbn,fine,paid,"Yes"};
		                            model3.addRow(obj);
		                            message1.setText("Select a row for which you wish to pay fine");
		                            }
		                        }
		                        else{
		                            message1.setText("No current loans to be paid");
		                        }
		                    }
		                    
		                }
		                }while(rs.next());
		            }
		            
		           }
		        catch (Exception e) {
		            e.printStackTrace();
		        }
		        
		      
				
			}
		});
		btnNewButton_5.setBounds(447, 79, 97, 25);
		FINES.add(btnNewButton_5);
		
		textField_14 = new JTextField();
		textField_14.setBounds(258, 80, 143, 25);
		FINES.add(textField_14);
		textField_14.setColumns(10);
		
		
		
		JButton btnNewButton_6 = new JButton("Pay_Fine");
		btnNewButton_6.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				 String q1=null;
				 DB_Connection fines3 = new DB_Connection();
				 
				 int rowIndex = table_2.getSelectedRow();
				 String book_returned = (String) table_2.getValueAt(rowIndex,4);
				 String loan_id = (String) table_2.getValueAt(rowIndex,0);
				 String paid = (String) table_2.getValueAt(rowIndex,3);
				 
			        try{
			        if(book_returned=="No")
			            message1.setText("The book is yet to be returned");
			        else{
			            if(paid=="False"){
			                q1="update fines set paid=true where loan_id='"+loan_id+"'";
			                fines3.Connection1(q1);
			                message1.setText("Paid is updated");
			            }
			            else
			                message1.setText("There is no fine to pay");
			        }    
			        }
			        catch (Exception e7) {
			            e7.printStackTrace();
			        }
			        
			}
		});
		btnNewButton_6.setBounds(81, 355, 97, 25);
		FINES.add(btnNewButton_6);
		
		JButton btnNewButton_7 = new JButton("Update Fines");
		
		
		btnNewButton_7.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				int rowIndex = table_2.getSelectedRow();
				 double fine_amt;
				 DB_Connection fines4 = new DB_Connection();
				 
			        
			            String q1="select l.loan_id,f.paid,l.date_in,datediff(date_in,due_date) dateIndiff,datediff(curdate(),l.due_date) cur_datediff from book_loans l "
			                    + "left join fines f on l.loan_id=f.loan_id where date_in > due_date or date_in='1885-01-01';";
			           
			            ResultSet rs1 = fines4.Connection(q1);
			            try {
							while(rs1.next()){
							String   loan_id=rs1.getString("loan_id");
							   String paid=rs1.getString("paid");
							    String date_in = rs1.getString("date_in");
							    String dateIndiff=rs1.getString("dateIndiff");
							    String cur_datediff=rs1.getString("cur_datediff");
							    
							    
							    if(!date_in.equals("1885-01-01")){
							    	//System.out.println(date_in=="1885-01-01");
							        fine_amt= 0.25*Double.parseDouble(dateIndiff);
							        String q3 = "select loan_id from fines where loan_id = '"+loan_id+"';";
								    ResultSet rs = fines4.Connection(q3);
								    if(!rs.next()){
							        String q2= "insert into fines (loan_id,fine_amt,paid)  values ('"+loan_id+"',"+fine_amt+","+paid+");";
							        int r = fines4.Connection1(q2);
							        System.out.println(r);
							        message1.setText("updated");
								    }
							    }
							    
							    
							    else{
							        message1.setText("updated");
							    }
							    
							}
						} catch (NumberFormatException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (SQLException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
			        }
			
			        
			     
			
			        });
		btnNewButton_7.setBounds(267, 355, 134, 25);
		FINES.add(btnNewButton_7);
		
		JButton btnNewButton_8 = new JButton("Total Fine");
		btnNewButton_8.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				  String sum="";
				  String card_no = textField_14.getText();
				  DB_Connection fines5 = new DB_Connection();
			        try{
			            String q1 = "select sum(fine_amt) as sum from fines f join book_loans l on l.loan_id=f.loan_id where card_no ='"+card_no+"'";
			           ResultSet rs=fines5.Connection(q1);
			            if(rs.next()){
			                sum=rs.getString("sum");
			                message1.setText(sum);
			            }
			            if(sum==null)
			                sum="0.0";
			           }
			        catch (Exception e8) {
			            e8.printStackTrace();
			        }
			}
		});
		btnNewButton_8.setBounds(521, 355, 97, 25);
		FINES.add(btnNewButton_8);
		
		JScrollPane scrollPane_2 = new JScrollPane();
		scrollPane_2.setBounds(25, 126, 791, 221);
		FINES.add(scrollPane_2);
		
		table_2 = new JTable();
		table_2.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"loan_id", "ISBN", "Fine_amt", "paid", "Book_Returned"
			}
		));
		
		
		scrollPane_2.setViewportView(table_2);
		
		message1 = new JTextField();
		message1.setBounds(543, 13, 266, 24);
		FINES.add(message1);
		message1.setColumns(10);
	}
}
