/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



import java.sql.*;
//import java.awt.Dimension;
//import java.awt.Toolkit;
import javax.swing.table.DefaultTableModel;

public class Sql_Connect {
    static Connection conn;
    Statement stmnt = null;
    ResultSet rs = null;
    String message;
    
    public Sql_Connect() {
		
	try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            String url = "jdbc:mysql://localhost:3306/library";
            String user = "root";
            String password = "admin";
            conn = DriverManager.getConnection(url, user, password);
            stmnt = conn.createStatement();
        } 
		
	catch (Exception e) {
            e.printStackTrace();
        }
		
    }
    
    void disConnect(){
			try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
			try { if (stmnt != null) stmnt.close(); } catch (SQLException e) { e.printStackTrace(); }
			try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace();}
    }
    
    String validate(String value1, String value2,String value3){
        
        if(value1 == null || value2 == null || value3== null)
            message = "The given values are invalid. Enter Valid Values.";
        else
            message="";
        return message;
    }
    
    String borrowerNew(String fname, String lname, String ssn, String address, String city, String state, String email, String phone){
        String num="";
        try{
            if(ssn.matches("^[0-9]{3}\\-[0-9]{2}\\-[0-9]{4}$")){
            String q1 = "Select ssn from borrower where ssn= '"+ ssn+ "';";
            rs = stmnt.executeQuery(q1);    
            if(rs.next()){
                String s = rs.getString("ssn");
                if(s.toString().equals(ssn))
                    message = "User already exists with the SSN: "+ ssn; 
            }
            
            else{
                String q2 = "insert into borrower (ssn,fname,lname,email,address,city,state,phone) "
                        + "values (\""+ssn+"\", \""+fname+"\", \""+lname+"\", \""+email+"\", \""+address+"\", \""+city+"\", \""+state+"\", \""+phone+"\");";
                stmnt.executeUpdate(q2);  
                String q3 = "select card_no from borrower where ssn='"+ssn+"';";
                rs = stmnt.executeQuery(q3);
                if(rs.next())
                {
                    String card_no = rs.getString("card_no");
                    for(int i=0;i<6-card_no.length();i++)
                                num +="0";
                            
                    message = "The Borrower's Card Number is: ID"+num+card_no;
                }
            }
            }
            
            else{
                message = "Please provide the SSN in the number format XXX-XX-XXX";
            }
        }     
       catch (Exception e) {
            e.printStackTrace();
        }
        return message;
    }
    
    String checkOut(String isbn,String branch_id,String card_no){
        String num="";
        try{
            
            String q1= "select card_no, count(*) as count from book_loans where date_in='1885-01-01' and card_no='"+card_no+"';";
            rs = stmnt.executeQuery(q1);
            
           if(rs.next()){
                String card = rs.getString("card_no");
                String c = rs.getString("count");
                message = validate(c,card_no,"");
                if(message==""){
                  int count= Integer.parseInt(c.toString());
                  if(count<3){
                     String q2 = "select l.isbn,l.branch_id,(c.no_of_copies- count(l.isbn)) as no_of_available_copies from book_loans l "
                                +"right join book_copies c on l.isbn=c.isbn "+
                                 "where c.branch_id=l.branch_id and l.date_in='1885-01-01' and c.isbn = '"+isbn+"' and c.branch_id='"+branch_id+"';";
                    rs = stmnt.executeQuery(q2);
                    
                    if(rs.next()){
                        String ci=rs.getString("isbn");
                        String cb=rs.getString("branch_id");
                        String c1 = rs.getString("no_of_available_copies");
                        //message = validate(c1,cb,ci);
                        //if(message==""){
                            int copies = Integer.parseInt(c1.toString());
                            if(copies>0){
                            String q3 = "insert into book_loans (isbn,branch_id,card_no,date_out,due_date)" +
                                        "values ('"+isbn+"', '"+branch_id+"', '"+card_no+"', curdate(), DATE_ADD(date_out,INTERVAL 14 DAY));";
                            stmnt.executeUpdate(q3);
                            String q4="select title from book where isbn="+isbn+";";
                            rs = stmnt.executeQuery(q4);
                            String title=null;
                            if(rs.next())
                                title=rs.getString("title");
                            for(int i=0;i<6-card_no.length();i++)
                                num +="0";
                            message= "The Book "+isbn+": "+title+" is checked out with Card Number: ID"+num+card_no;
                             }
                            else{
                                message="No available copies at the selected branch";
                            }
                        //}
                    }
                }
                
                else{
                    message = "Maximum Number of Book Loans allowed are 3";
                }
                
                }
           }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return message;
    }
    
    
    String checkInDisplay(String isbn,String card_no,String fname,String lname,DefaultTableModel model1){
            double fines=0;
            String book_id,cardNum,name,loanNum,branchNum,date_diff;
                
        try{
            String q1= "select l.isbn,l.card_no,bw.fname,bw.lname, l.loan_id, l.branch_id,datediff(curdate(),due_date) as date_diff from book_loans l "+
                    "join borrower bw on l.card_no = bw.card_no "+
                 " where l.date_in='1885-01-01' and (l.isbn='"+isbn+"' or bw.fname ='"+fname+"' or bw.lname = '"+lname+"' or l.card_no='"+card_no+"');";
            rs = stmnt.executeQuery(q1);
            if(!rs.next()){
                    message = "No books pending to be checked-in or Entered Values are not valid";
            }
            else{
                do{
                    book_id = rs.getString("isbn");
                    cardNum = rs.getString("card_no");
                    name = rs.getString("fname")+rs.getString("lname");
                    loanNum = rs.getString("loan_id");
                    branchNum = rs.getString("branch_id");
                    date_diff = rs.getString("date_diff");
                    //System.out.println(loanNum == null);
                    if(Integer.parseInt(date_diff)>0){
                        fines = 0.25*Double.parseDouble(date_diff);
                        Object obj1[] ={book_id,cardNum,name,loanNum,branchNum,fines};
                        model1.addRow(obj1);
                    }
                    else{
                        Object obj2[] ={book_id,cardNum,name,loanNum,branchNum,fines};
                        model1.addRow(obj2);
                    }
                    message = "Select the book which you want to check-in";
                    
                }while(rs.next());
            }
           }
        catch (Exception e) {
            e.printStackTrace();
        }

        return message;
    }
    
    String checkIn(String loan_id){

        try{
            String q1= "update book_loans set date_in = curdate() where loan_id = '"+loan_id+"';";
            stmnt.executeUpdate(q1);
            message="Thank You..!! Your book has been checked in.";
           }
        catch (Exception e) {
            e.printStackTrace();
        }        
        
        return message;
    }
    
    void fineInsert(String loan_id, double fine_amt,boolean paid){
        
        try{
            String q1 = "select loan_id from fines where loan_id='"+loan_id+"'";
            rs=stmnt.executeQuery(q1);
            if(!rs.next()){
            String q2= "insert into fines (loan_id,fine_amt,paid)  values ('"+loan_id+"',"+fine_amt+","+paid+");";
            stmnt.executeUpdate(q2);
            }
           }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return;
    }
    
    String paidUpdate(String loan_id,String paid, String book_returned){
        String q1=null;
        try{
        if(book_returned=="No")
            message="The book is yet to be returned";
        else{
            if(paid=="False"){
                q1="update fines set paid=true where loan_id='"+loan_id+"'";
                stmnt.executeUpdate(q1);
                message="Paid is updated";
            }
            else
                message="There is no fine to pay";
        }    
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return message;
    }
    
    void fineUpdate(){
        String loan_id,paid,cur_datediff,date_in,dateIndiff;
        double fine_amt;
        try{
            String q1="select l.loan_id,f.paid,l.date_in,datediff(date_in,due_date) dateIndiff,datediff(curdate(),l.due_date) cur_datediff from book_loans l "
                    + "left join fines f on l.loan_id=f.loan_id where date_in>due_date or date_in='1885-01-01';";
            Statement stmnt1 = conn.createStatement();
            ResultSet rs1=stmnt1.executeQuery(q1);
            while(rs1.next()){
                loan_id=rs1.getString("loan_id");
                paid=rs1.getString("paid");
                //System.out.println(loan_id);
                date_in = rs1.getString("date_in");
                dateIndiff=rs1.getString("dateIndiff");
                cur_datediff=rs1.getString("cur_datediff");
                //System.out.println((date_in == "1885-01-01"));
                if(!date_in.equals("1885-01-01")){
                    fine_amt= 0.25*Double.parseDouble(dateIndiff);
                    fineInsert(loan_id,fine_amt,false);
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
         
    String finesDisplay(String card_no, boolean show_previous, DefaultTableModel model3){
        double fine_amt=0;
        String isbn,f_loan_id,b_loan_id,paid,paid_1,book_returned,date_in,curdate_diff,fine;
        String q2=null;
        try{
            String q1= "select f.loan_id f_loan,l.loan_id b_loan, l.isbn, f.fine_amt,f.paid, l.date_in,datediff(curdate(),due_date)"
                    + " from book_loans l left join fines f on l.loan_id=f.loan_id where (l.date_in > l.due_date or l.date_in='1885-01-01') and l.card_no='"+card_no+"';";
            rs = stmnt.executeQuery(q1);
            //System.out.println(rs.next());
            if(!rs.next()){
                if(card_no.matches("^[0-9]+"))
                    message="You dont have any Book Loans";
                else
                    message="Entered value is not valid";
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
                   if(date_in.equals("1885-01-01")){
                       if(Integer.parseInt(curdate_diff)>0){
                       fine_amt = 0.25*Double.parseDouble(curdate_diff);
                       book_returned = "No";
                       paid = "False";
                       Object obj[]={b_loan_id,isbn,fine_amt,paid,book_returned};
                       model3.addRow(obj);
                       }
                   }
                   else
                        message="Your account has no fines to be paid";
                }
                else{
                    if(show_previous){
                        if(date_in.equals("1885-01-01")){
                            fine_amt = 0.25*Double.parseDouble(curdate_diff);
                            book_returned = "No";
                            paid = "False";
                            //Object obj[]={b_loan_id,isbn,fine_amt,paid,book_returned};
                            //model3.addRow(obj);
                        }
                        else{
                            Object obj[]={f_loan_id,isbn,fine,paid,"Yes"};
                            model3.addRow(obj);
                            if(paid=="False")
                                message="Select a row for which you wish to pay fine";
                            else
                                message="No Current Loans to pay";
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
                            message="Select a row for which you wish to pay fine";
                            }
                        }
                        else{
                            message="No current loans to be paid";
                        }
                    }
                    
                }
                }while(rs.next());
            }
            
           }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return message;
    }
    
    String fineTotal(String card_no){
        String sum="";
        try{
            String q1 = "select sum(fine_amt) as sum from fines f join book_loans l on l.loan_id=f.loan_id "
                    + "where l.date_in>l.due_date and f.paid='false' and card_no ='"+card_no+"'";
            String q2="select sum(0.25*datediff(curdate(),due_date)) as sum_date_diff from book_loans where date_in='1885-01-01' and card_no='"+card_no+"'";
            rs=stmnt.executeQuery(q1);
            Statement stmt1=conn.createStatement();
            ResultSet rs1 = stmt1.executeQuery(q2);
            if(rs.next()){
                sum=rs.getString("sum");
            }
            if(rs1.next()){
                sum=rs1.getString("sum_date_diff");
            }
            if(sum==null){
                sum="0.0";
            }
            
           }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return sum;
    }
    
   
    
    
    String searchDisplay(String isbn, DefaultTableModel model2){
        String book_id,book_title,author_name, branch_id,branch_name,no_of_copies,available_copies="";
        String q1=null;
        q1="SELECT B.isbn,B.TITLE,B.BRANCH_ID,B.branch_name,fullname,no_of_copies" +
                        " FROM book_loans L  RIGHT JOIN " +
                        "(select c.branch_id , c.no_of_copies, lb.branch_name,bk.isbn, bk.title, au.fullname " +
                        " from " +
                        " (library_branch lb left join " +
                        " ( book_copies c left join " +
                        " (book bk left join (book_authors ba left join authors au  on ba.author_id = au.author_id ) on bk.isbn=ba.isbn  ) " +
                        " on bk.isbn= c.isbn  ) " +
                        " on c.branch_id = lb.branch_id )) as B " +
                        " ON ( L.isbn=B.isbn AND L.BRANCH_ID=B.BRANCH_ID) " ;
        try{
            
            if(!isbn.isEmpty()){
                        q1 +=" where B.isbn like '%"+isbn+
                        "%' or B.fullname like '%"+isbn+"%' or B.title like '%"+isbn+"%' Group by  B.branch_id, B.isbn;";
            }
          
            
            //System.out.println(q1);
            
            if(q1 !=null){
            rs = stmnt.executeQuery(q1);
            if(!rs.next()){
                    message = "No books found for the given Query. Enter Valid Values";
            }
            else{
                do{
                    book_id = rs.getString("isbn");
                    book_title = rs.getString("title");
                    author_name = rs.getString("fullname");
                    branch_id = rs.getString("branch_id");
                    branch_name=rs.getString("branch_name");
                    no_of_copies = rs.getString("no_of_copies");
                    String q2 = "select (c.no_of_copies- count(*)) as no_of_available_copies from book_loans l "
                             + "right join book_copies c on l.isbn=c.isbn "+
                            "where c.branch_id = l.branch_id and l.date_in='1885-01-01' and c.isbn = '"+book_id+"' and c.branch_id='"+branch_id+"';";
                    Statement stmt1=conn.createStatement();
                    ResultSet rs1 = stmt1.executeQuery(q2);
                    if(rs1.next()){
                        available_copies= rs1.getString("no_of_available_copies");
                     if(Integer.parseInt(available_copies.toString())<0){
                        Object obj1[] ={book_id,book_title,author_name,branch_id,branch_name,no_of_copies,"0"};
                        model2.addRow(obj1);
                    }
                    else{
                        Object obj1[] ={book_id,book_title,author_name,branch_id,branch_name,no_of_copies,available_copies};
                        model2.addRow(obj1);
                    }
                    }
                }while(rs.next());
                message ="Select a row to either check-in or check-out";
            }
            }
           }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return message;
    }
}
