import java.io.RandomAccessFile;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.*;
import java.util.Scanner;
import java.util.TreeMap;



@SuppressWarnings("unused")
public class DavisBaseLite {
	// This can be changed to whatever you like
	static String prompt = "DavisSql> ";

	static String schemaName="information_schema";
	@SuppressWarnings({ "unchecked", "rawtypes" })
	static TreeMap<Object, ArrayList> map = new TreeMap();
	static int rows=0;

	@SuppressWarnings("resource")
	public static void main(String[] args) {
		/* Display the welcome splash screen */
		splashScreen();
		MakeInformationSchema.main(args);

		/* 
		 *  The Scanner class is used to collect user commands from the prompt
		 *  There are many ways to do this. This is just one.
		 *
		 *  Each time the semicolon (;) delimiter is entered, the userCommand String
		 *  is re-populated.
		 */
		Scanner scanner = new Scanner(System.in).useDelimiter(";");
		String userCommand; // Variable to collect user input from the prompt

		do {  // do-while !exit
			System.out.print(prompt);
			userCommand = scanner.next().trim();
			//System.out.println("user comaand is "+userCommand);
			/*
			 *  This switch handles a very small list of commands of known syntax.
			 *  You will probably want to write a parse(userCommand) method to
			 *  to interpret more complex commands. 
			 */
			String[] cmd = userCommand.split(" ");

			if(userCommand.equalsIgnoreCase("SHOW SCHEMAS"))
			{
				showSchemas();
			}
			else if(userCommand.equalsIgnoreCase("SHOW TABLES"))
			{
				showTables();
			}
			else if(cmd[0].equalsIgnoreCase("USE"))
			{
				useSchema(userCommand);
			}
			else if(cmd[0].equalsIgnoreCase("INSERT"))
			{
				insert(userCommand);
			}
			else if(cmd[0].equalsIgnoreCase("CREATE") && cmd[1].equalsIgnoreCase("TABLE"))
			{
				splitter(userCommand);
			}
			else if(cmd[0].equalsIgnoreCase("CREATE") && cmd[1].equalsIgnoreCase("SCHEMA"))
			{
				createSchema(userCommand);
			}
			else if(cmd[0].equalsIgnoreCase("SELECT")){
				if(userCommand.contains("WHERE") || userCommand.contains("where"))
					selectWhere(userCommand);
				else
					select(userCommand);
			}
			else if(userCommand.equalsIgnoreCase("version"))
			{
				version();
			}
			else if(userCommand.equalsIgnoreCase("help"))
			{
				help();
			}
			else if(userCommand.equalsIgnoreCase("exit"))
			{

			}
		}while(!userCommand.equals("exit"));
		System.out.println("Exiting...");
	} /* End main() method */


	//  ===========================================================================
	//  STATIC METHOD DEFINTIONS BEGIN HERE
	//  ===========================================================================

	@SuppressWarnings({ "resource", "rawtypes" })
	private static void selectWhere(String userCommand) {
		String[] token = userCommand.split(" ");
		String tableName =token[3];
		String col_key;
		String value;
		String operator ="";
		int num = -1;
		String type;
		String coltype;
		String[] strArr = userCommand.split("[ =\\>\\<]");
		ArrayList<ArrayList<String>> arrayList = new ArrayList<ArrayList<String>>(); 
		ArrayList<String> col = new ArrayList<String>();
		ArrayList<String> typ = new ArrayList<String>();
		try{
			col_key=strArr[5];
			value=strArr[strArr.length-1];
			if(userCommand.contains("=")){
				operator="=";
			}
			if(userCommand.contains(">")){
				operator = ">";
			}
			if(userCommand.contains("<")){
				operator = "<";
			}
			arrayList = getColumn(tableName);
			col=arrayList.get(0);
			typ=arrayList.get(1);
			lineDisplay();
			for(int i=0;i<col.size();i++){
				System.out.print(col.get(i)+"     |    ");
				if(col.get(i).equalsIgnoreCase(col_key)){
					num=i;
				}
			}
			System.out.println();
			lineDisplay();
			if(num != -1){
				coltype=typ.get(num);
				//initializeMap(tableName,col_key,type);
				type="";
				RandomAccessFile  tableFile = new RandomAccessFile(schemaName.toLowerCase()+"."+tableName.toLowerCase()+".tbl","rw");

				ArrayList<ArrayList<String>> outList = new ArrayList<ArrayList<String>>();
				//list1.add(col_name);

				for(int i=0;i<tableFile.length();i++)
				{
					if(tableFile.getFilePointer()<tableFile.length())
					{
						ArrayList<String> insideList = new ArrayList<String>();
						for(int j=0;j<typ.size();j++)
						{
							type = (String) typ.get(j);

							if(type.equalsIgnoreCase("int"))
							{
								int k =tableFile.readInt();
								//System.out.print(k+ "  |");
								insideList.add(Integer.toString(k));
							}
							else if(type.equalsIgnoreCase("long") || type.equalsIgnoreCase("long int"))
							{
								long k= tableFile.readLong();
								//System.out.print(k+"  |");
								insideList.add(Long.toString(k));
							}
							else if(type.equalsIgnoreCase("short") || type.equalsIgnoreCase("short int"))
							{
								short k = tableFile.readShort();
								//System.out.print(k+"  |");
								insideList.add(Short.toString(k));
							}
							else if(type.equalsIgnoreCase("float"))
							{
								float k= tableFile.readFloat();
								//System.out.print(k+"  |");
								insideList.add(Float.toString(k));
							}
							else if(type.equalsIgnoreCase("double"))
							{
								double k=tableFile.readDouble();
								//System.out.print(k+"  |");
								insideList.add(Double.toString(k));
							}
							else if(type.equalsIgnoreCase("byte"))
							{
								byte k =tableFile.readByte();
								//System.out.print(k+"  |");
								insideList.add(Byte.toString(k));
							}
							else 
							{	
								int length = tableFile.readByte();
								String out="";
								for(int g=0; g< length ;g++)
								{
									out+= (char)tableFile.readByte();
								}
								//System.out.print(out+"  |");
								insideList.add(out);
							}
						}

						//	System.out.println();
						outList.add(insideList);
					}
				}
				int flag=0;
				ArrayList<Integer> intlist = new ArrayList<Integer>();
				for(int k=0;k<outList.size();k++)
				{
					flag=0;
					ArrayList l = outList.get(k);
					// **** equals comparison ** //
					if(operator.equals("="))
					{
						String str="";
						if(value.contains("\'") || value.contains("\"")){
							str=value.substring(1, value.length()-1);
						}
						else{
							str=value;
						}
						//System.out.println(" str "+ str.toLowerCase());
						int p=l.get(num).toString().toLowerCase().compareTo(str.toLowerCase());
						if(p==0)
						{
							intlist.add(k);
						}
					}
					if(operator.equals(">"))
					{
						int g = -1;
						int v = -1;
						if(coltype.equalsIgnoreCase("int")){
							v= Integer.parseInt(value);
							g=Integer.parseInt((String) l.get(num));
						}
						else if(coltype.equalsIgnoreCase("short")|| coltype.equalsIgnoreCase("short int")){
							v= Short.parseShort(value);
							g=Short.parseShort((String) l.get(num));
						}
						else if(coltype.equalsIgnoreCase("long")|| coltype.equalsIgnoreCase("long int")){
							long f= Long.parseLong(value);
							long m = Long.parseLong((String) l.get(num));
							if(m>f){
								intlist.add(k);
							}
							flag=1;
						}
						else if(coltype.equalsIgnoreCase("float")){
							float f= Float.parseFloat(value);
							float m = Float.parseFloat((String) l.get(num));
							if(m>f){
								intlist.add(k);
							}
							flag=1;
						}
						else if(coltype.equalsIgnoreCase("double")){
							double f= Double.parseDouble(value);
							double m = Double.parseDouble((String) l.get(num));
							if(m>f){
								intlist.add(k);
							}
							flag=1;
						}
						else if(coltype.equalsIgnoreCase("byte")){
							byte f= Byte.parseByte(value);
							byte m = Byte.parseByte((String) l.get(num));
							if(m>f){
								intlist.add(k);
							}
							flag=1;
						}
						else{
							String str="";
							if(value.contains("\'") || value.contains("\"")){
								str=value.substring(1, value.length()-1);
							}
							else{
								str=value;
							}
							int p=l.get(num).toString().toLowerCase().compareTo(str.toLowerCase());
							if(p>0)
							{
								intlist.add(k);
							}
							flag=1;
						}

						if(g>v && flag==0)
						{
							intlist.add(k);
						}

					}

					if(operator.equals("<"))
					{
						int g = -1;
						int v = -1;
						if(coltype.equalsIgnoreCase("int")){
							v= Integer.parseInt(value);
							g=Integer.parseInt((String) l.get(num));
						}
						else if(coltype.equalsIgnoreCase("short")|| coltype.equalsIgnoreCase("short int")){
							v= Short.parseShort(value);
							g=Short.parseShort((String) l.get(num));
						}
						else if(coltype.equalsIgnoreCase("long")|| coltype.equalsIgnoreCase("long int")){
							long f= Long.parseLong(value);
							long m = Long.parseLong((String) l.get(num));
							//System.out.println(f+" f value");
							//System.out.println(m+" f value");
							if(m<f){
								intlist.add(k);
							}
							//System.out.println("in long");
							flag=1;
						}
						else if(coltype.equalsIgnoreCase("float")){
							float f= Float.parseFloat(value);
							float m = Float.parseFloat((String) l.get(num));
							if(m<f){
								intlist.add(k);
							}
							flag=1;
						}
						else if(coltype.equalsIgnoreCase("double")){
							double f= Double.parseDouble(value);
							double m = Double.parseDouble((String) l.get(num));
							if(m<f){
								intlist.add(k);
							}
							flag=1;
						}
						else if(coltype.equalsIgnoreCase("byte")){
							byte f= Byte.parseByte(value);
							byte m = Byte.parseByte((String) l.get(num));
							if(m<f){
								intlist.add(k);
							}
							flag=1;
						}
						else{
							String str="";
							if(value.contains("\'") || value.contains("\"")){
								str=value.substring(1, value.length()-1);
							}
							else{
								str=value;
							}
							int p=l.get(num).toString().toLowerCase().compareTo(str.toLowerCase());
							if(p<0)
							{
								intlist.add(k);
							}
							flag=1;
						}

						if(g<v && flag==0)
						{
							intlist.add(k);
						}
					}
				}

				for(int t1=0; t1<intlist.size();t1++)
				{
					//	System.out.println("indide disp");
					ArrayList result = outList.get(intlist.get(t1));

					for(int h=0;h<result.size();h++)
					{
						System.out.print(result.get(h)+"    |     ");
					}
					System.out.println();
				}
			}
			else{
				System.out.println("The column name doesnot exist.");
			}


		} catch(Exception e){
			e.printStackTrace();
		}
	}

	@SuppressWarnings("resource")
	private static ArrayList<ArrayList<String>> getColumn(String tableName){
		String str;
		String str1;
		String columnName = "";
		String type="";
		int flag1=0;
		int flag=0;
		int num=0;
		//String array[] = new String[2];
		ArrayList<ArrayList<String>> arrayList=new ArrayList<ArrayList<String>>();
		ArrayList<String> col = new ArrayList<String>();
		ArrayList<String> typ = new ArrayList<String>();

		try{
			RandomAccessFile columnsTableFile = new RandomAccessFile("information_schema.columns.tbl", "rw");
			for(int i=0;i<columnsTableFile.length();i++){
				str="";
				flag1=0;
				flag=0;
				num=0;
				if(columnsTableFile.getFilePointer()<columnsTableFile.length()){
					//System.out.println(schemataTableFile.getFilePointer()<schemataTableFile.length());
					for(int k=0;k<3;k++){
						str1="";
						columnName="";
						byte len= columnsTableFile.readByte();
						for(int j=0;j<len;j++)
							str1 += (char)columnsTableFile.readByte();
						//System.out.println("Value of str1: "+ str1);
						if(schemaName.equalsIgnoreCase(str1)){
							//	System.out.println("111111111");
							flag = 1;
						}
						if(tableName.equalsIgnoreCase(str1) && flag==1){
							//System.out.println("222222222");
							flag1=1;
						}
						if(k==2 && flag1==1){
							//System.out.println("3333");
							columnName = str1;
						}
					}
					if(flag1==1){
						num = columnsTableFile.readInt();
						//System.out.println("ordinal position: "+num);
						//System.out.println("comp "+(num));
					}
					else{
						//System.out.println("5");
						columnsTableFile.readInt();
					}
					byte len= columnsTableFile.readByte();
					for(int j=0;j<len;j++){
						str += (char)columnsTableFile.readByte();
					}
					//System.out.println("Value of str: "+ str);
					if(flag1==1){
						//System.out.println("6");
						type = str;
						//System.out.println(type+ " in flag2");
					}
					byte len1= columnsTableFile.readByte();
					for(int j=0;j<len1;j++)
						columnsTableFile.readByte();
					byte len2= columnsTableFile.readByte();
					//System.out.println(len2+ " len-2 ");
					//System.out.println(Integer.compare(len2, 0)+"  comparison");
					if(len2 !=0){
						//System.out.println("hiiii");
						for(int j=0;j<len2;j++)
							columnsTableFile.readByte();
					}
					else{
						//System.out.println("hiiii 222");
					}
					if(flag1==1){
						col.add(columnName);
						typ.add(type);
					}
				}
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		arrayList.add(col);
		arrayList.add(typ);
		return arrayList;
	}

	private static void select(String userCommand) {
		String[] toke = userCommand.split(" ");
		String tableName =toke[3];
		ArrayList<String> col = new ArrayList<String>();
		ArrayList<String> typ = new ArrayList<String>();
		ArrayList<ArrayList<String>> arrayList = new ArrayList<ArrayList<String>>();
		try {
			arrayList = getColumn(tableName);
			col=arrayList.get(0);
			typ=arrayList.get(1);
			displayTable(col,typ,tableName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("resource")
	private static void displayTable(ArrayList<String> col, ArrayList<String> typ, String tableName) {
		// **** Display of Column Names *** //
		lineDisplay();
		for(int i=0;i<col.size();i++)
			System.out.print(col.get(i)+" | ");
		System.out.println();
		lineDisplay();
		//** end of display  **//

		String type="";
		try {
			RandomAccessFile tableFile = new RandomAccessFile(schemaName.toLowerCase()+"."+tableName.toLowerCase()+".tbl","rw");
			for(int j=0;j<tableFile.length();j++){
				if(tableFile.getFilePointer()<tableFile.length()){
					for(int k=0;k<typ.size();k++){
						type=typ.get(k);
						if(type.equalsIgnoreCase("int")){
							System.out.print(tableFile.readInt()+"     |     ");
						}
						else if(type.equalsIgnoreCase("long")|| type.equalsIgnoreCase("long int") ){
							System.out.print(tableFile.readLong()+"      |      ");
						}
						else if(type.equalsIgnoreCase("short") || type.equalsIgnoreCase("short int")){
							System.out.print(tableFile.readShort()+"      |     ");
						}
						else if(type.equalsIgnoreCase("double")){
							System.out.print(tableFile.readDouble()+"     |     ");
						}
						else if(type.equalsIgnoreCase("float")){
							System.out.print(tableFile.readFloat()+"      |      ");
						}
						else if(type.equalsIgnoreCase("byte")){
							System.out.print(tableFile.readByte()+"      |      ");
						}
						else { // for varchar, char, dateTime, date
							byte len =tableFile.readByte();
							String str="";
							for(int l=0;l<len;l++)
								str += (char) tableFile.readByte();
							System.out.print(str+"        |       ");
						}
					}
					System.out.println();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// ** line display
	private static void lineDisplay(){
		for(int i=0;i<10;i++)
			System.out.print("-------------");
		System.out.println();
	}

	// ***** Initializing the Tree Map with the Index File *******//
	@SuppressWarnings("resource")
	private static void initializeMap(String tableName,String columnName,String type){
		String key="";
		int size=0;
		map.clear();
		try {
			RandomAccessFile indexFile = new RandomAccessFile(schemaName.toLowerCase()+"."+tableName.toLowerCase()+"."+columnName.toLowerCase()+".ndx","rw");
			//indexFile.seek(indexFile.length());
			for(int m=0;m<indexFile.length();m++){
				if(indexFile.getFilePointer()<indexFile.length()){
					if(type.equalsIgnoreCase("int") || type.equalsIgnoreCase("long") || type.equalsIgnoreCase("short") || type.equalsIgnoreCase("double") || type.equalsIgnoreCase("float") || type.equalsIgnoreCase("byte")){
						ArrayList<Integer> address = new ArrayList<Integer>();
						int value= indexFile.readInt();
						key = Integer.toString(value);
						size =indexFile.readInt();
						for(int j=0;j<size;j++){
							//	System.out.println("key is : "+ key);
							address.add(indexFile.readInt());
						}
						map.put(key, address);
					}
					else{
						ArrayList<Integer> address1 = new ArrayList<Integer>();
						byte len = indexFile.readByte();
						String str ="";
						for(int i=0;i<len;i++){
							str += (char) indexFile.readByte();
						}
						key =str;
						size =indexFile.readInt();
						for(int j=0;j<size;j++){
							address1.add(indexFile.readInt());
						}
						map.put(key, address1);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes", "resource" })
	private static void insert(String command) {
		String[] names=command.split("[(]");
		String[] tableN = names[0].split(" ");
		String tableName = tableN[2];
		String[] value = names[1].split("[,)]");
		ArrayList<ArrayList<String>> colType= new ArrayList<ArrayList<String>>();
		ArrayList<ArrayList<String>> checkPN= new ArrayList<ArrayList<String>>();
		ArrayList<String> colName = new ArrayList<String>();
		ArrayList<String> pri = new ArrayList<String>();
		ArrayList<String> notN = new ArrayList<String>();
		String primary = "";
		String notNull = "";
		String[] colName_type=new String[2];
		String[] type;
		ArrayList<Integer> address = new ArrayList<Integer>();
		int flag=0;
		try {
			RandomAccessFile tableFile = new RandomAccessFile(schemaName.toLowerCase()+"."+tableName.toLowerCase()+".tbl","rw");
			tableFile.seek(tableFile.length());
			int k=(int) tableFile.getFilePointer();

			checkPN = checkPrimaryNull(tableName);
			colName=checkPN.get(0);
			pri=checkPN.get(3);
			notN=checkPN.get(2);

			if(colName.size()==value.length){
				for(int i=0;i<value.length;i++){
					colName_type =valueType(tableName,i+1);
					initializeMap(tableName,colName_type[0],colName_type[1]); // Initializing the tree map with the existing indices.
					if(map.containsKey(value[i])){
						//System.out.println(pri.get(i));
						if(pri.get(i).equalsIgnoreCase("PRI")){
							//System.out.println("Duplicate Primary Key");
							flag=1;
							break;
						}
						else{
						ArrayList<Integer> address1 = (ArrayList<Integer>) map.get(value[i]);
						address1.add(k);
						}
					}
					else{
						address = new ArrayList<Integer>();
						address.add(k);
						map.put(value[i], address);
					}

					// *** Writing in table file *****
					tableFile.seek(tableFile.length());
					type=colName_type[1].split(" ");
					if(type[0].equalsIgnoreCase("int")){
						tableFile.writeInt(Integer.parseInt(value[i]));
					}
					else if(type[0].equalsIgnoreCase("long")){
						tableFile.writeLong(Long.parseLong(value[i]));
					}
					else if(type[0].equalsIgnoreCase("short")){
						tableFile.writeShort(Short.parseShort(value[i]));
					}
					else if(type[0].equalsIgnoreCase("double")){
						tableFile.writeDouble(Double.parseDouble(value[i]));
					}
					else if(type[0].equalsIgnoreCase("float")){
						tableFile.writeFloat(Float.parseFloat(value[i]));
					}
					else if(type[0].equalsIgnoreCase("byte")){
						tableFile.writeByte(Byte.parseByte(value[i]));
					}
					else { // for varchar, char, dateTime, date
						tableFile.writeByte(value[i].length()-2);
						tableFile.writeBytes(value[i].substring(1, value[i].length()-1));
					}

					// ****** creating index file ****//
					RandomAccessFile indexFile = new RandomAccessFile(schemaName.toLowerCase()+"."+tableName.toLowerCase()+"."+colName_type[0].toLowerCase()+".ndx","rw");
					for(Entry<Object,ArrayList> entry : map.entrySet()) {
						Object key = entry.getKey();         // Get the index key
						ArrayList value1 = entry.getValue();   // Get the list of record addresses
						if(type[0].equalsIgnoreCase("int") || type[0].equalsIgnoreCase("long") || type[0].equalsIgnoreCase("short") || type[0].equalsIgnoreCase("double") || type[0].equalsIgnoreCase("float") || type[0].equalsIgnoreCase("byte")){
							int size =value1.size();
							indexFile.writeInt(Integer.parseInt(key.toString()));
							indexFile.writeInt(size);
							for(int j=0;j<size;j++){
								indexFile.writeInt((int) value1.get(j));
							}
						}
						else{
							int size =value1.size();
							indexFile.writeByte(key.toString().length());
							indexFile.writeBytes(key.toString());
							indexFile.writeInt(size);
							for(int j=0;j<size;j++){
								indexFile.writeInt((int) value1.get(j));
							}
						}
					}
				}
			}
			else{
				System.out.println("Enter All values for all columns.");
			}
			if(flag==0){
				if(colName.size()==value.length){
					incrementRows(schemaName,tableName); //increment number of rows in the table of information_schema
					System.out.println("Row Inserted Successfully.");
				}
			}
			else{
				if(flag==1)
				System.out.println("Duplicate Primary Key");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("resource")
	private static ArrayList<ArrayList<String>> checkPrimaryNull(String tableName){
		String str;
		String str1;
		String columnName = "";
		String type="";
		int flag1=0;
		int flag=0;
		ArrayList<ArrayList<String>> arrayList=new ArrayList<ArrayList<String>>();
		ArrayList<String> colName = new ArrayList<String>();
		ArrayList<String> typ = new ArrayList<String>();
		ArrayList<String> pri = new ArrayList<String>();
		ArrayList<String> notNull = new ArrayList<String>();
		try{
			RandomAccessFile columnsTableFile = new RandomAccessFile("information_schema.columns.tbl", "rw");
			for(int i=0;i<columnsTableFile.length();i++){
				str="";
				flag1=0;
				flag=0;
				if(columnsTableFile.getFilePointer()<columnsTableFile.length()){
					//System.out.println(schemataTableFile.getFilePointer()<schemataTableFile.length());
					for(int k=0;k<3;k++){
						str1="";
						columnName="";
						byte len= columnsTableFile.readByte();
						for(int j=0;j<len;j++)
							str1 += (char)columnsTableFile.readByte();
						//System.out.println("Value of str1: "+ str1);
						if(schemaName.equalsIgnoreCase(str1)){
							//	System.out.println("111111111");
							flag = 1;
						}
						if(tableName.equalsIgnoreCase(str1) && flag==1){
							//System.out.println("222222222");
							flag1=1;
						}
						if(k==2 && flag1==1){
							//System.out.println("3333");
							columnName = str1;
						}
					}
					if(flag1==1){
						columnsTableFile.readInt();
						//	System.out.println("ordinal position: "+num);
						//	System.out.println("comp "+(num==col));

					}
					else{
						//System.out.println("5");
						columnsTableFile.readInt();
					}
					byte len= columnsTableFile.readByte();
					for(int j=0;j<len;j++){
						str += (char)columnsTableFile.readByte();
					}
					//	System.out.println("Value of str: "+ str);
					if(flag1==1){
						//System.out.println("6");
						type = str;
						//System.out.println(type+ " in flag2");

						byte len1= columnsTableFile.readByte();
						String str2="";
						for(int j=0;j<len1;j++)
							str2 += (char) columnsTableFile.readByte();
						byte len2= columnsTableFile.readByte();
						String str3="";
						if(len2 !=0){
							for(int j=0;j<len2;j++)
								str3 += (char) columnsTableFile.readByte();
						}
						else{
							str3="";
						}
						colName.add(columnName);
						typ.add(type);
						notNull.add(str2);
						pri.add(str3);
					}
					else{
						type = str;
						byte len1= columnsTableFile.readByte();
						String str2="";
						for(int j=0;j<len1;j++)
							str2 +=columnsTableFile.readByte();
						byte len2= columnsTableFile.readByte();
						String str3="";
						if(len2 !=0){
							for(int j=0;j<len2;j++)
								str3 +=columnsTableFile.readByte();
						}
						else{
							str3="";
						}

					}
				}
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		arrayList.add(colName);
		arrayList.add(typ);
		arrayList.add(notNull);
		arrayList.add(pri);
		return arrayList;
	}

	@SuppressWarnings("resource")
	private static String[] valueType(String tableName, int col){
		int flag=0;
		int flag1=0;
		int flag2=0;
		int num=0;
		String columnName="";
		String str="";
		String type="";
		String str1="";
		String[] array=new String[2];
		try{
			RandomAccessFile columnsTableFile = new RandomAccessFile("information_schema.columns.tbl", "rw");
			for(int i=0;i<columnsTableFile.length();i++){
				str="";
				flag2=0;
				flag1=0;
				flag=0;
				if(columnsTableFile.getFilePointer()<columnsTableFile.length()){
					//System.out.println(schemataTableFile.getFilePointer()<schemataTableFile.length());
					for(int k=0;k<3;k++){
						str1="";
						columnName="";
						byte len= columnsTableFile.readByte();
						for(int j=0;j<len;j++)
							str1 += (char)columnsTableFile.readByte();
						//System.out.println("Value of str1: "+ str1);
						if(schemaName.equalsIgnoreCase(str1)){
							//	System.out.println("111111111");
							flag = 1;
						}
						if(tableName.equalsIgnoreCase(str1) && flag==1){
							//System.out.println("222222222");
							flag1=1;
						}
						if(k==2 && flag1==1){
							//System.out.println("3333");
							columnName = str1;
						}
					}
					if(flag1==1){
						num = columnsTableFile.readInt();
						//	System.out.println("ordinal position: "+num);
						//	System.out.println("comp "+(num==col));
						if(num==col){
							flag2=1;
						}
					}
					else{
						//System.out.println("5");
						columnsTableFile.readInt();
					}
					byte len= columnsTableFile.readByte();
					for(int j=0;j<len;j++){
						str += (char)columnsTableFile.readByte();
					}
					//	System.out.println("Value of str: "+ str);
					if(flag2==1){
						//System.out.println("6");
						type = str;
						//System.out.println(type+ " in flag2");
					}
					byte len1= columnsTableFile.readByte();
					for(int j=0;j<len1;j++)
						columnsTableFile.readByte();
					byte len2= columnsTableFile.readByte();
					//System.out.println(len2+ " len-2 ");
					//System.out.println(Integer.compare(len2, 0)+"  comparison");
					if(len2 !=0){
						//System.out.println("hiiii");
						for(int j=0;j<len2;j++)
							columnsTableFile.readByte();
					}
					else{
						//System.out.println("hiiii 222");
					}
					if(flag2==1){
						array[0]=columnName;
						array[1]=type;
					}
				}
			}

		}catch (Exception e) {
			e.printStackTrace();
		}
		return array;
	}

	@SuppressWarnings("resource")
	private static void incrementRows(String schema, String tableName){
		int flag=0,flag1=0;
		long rows=0;
		try{
			RandomAccessFile tablesTableFile = new RandomAccessFile("information_schema.tables.tbl", "rw");
			for(int i=0;i<tablesTableFile.length();i++){
				flag=0;
				flag1=0;
				//System.out.println((tablesTableFile.getFilePointer()<tablesTableFile.length())+ " i is: "+ i);
				if(tablesTableFile.getFilePointer()<tablesTableFile.length()){
					for(int k=0;k<2;k++){
						String str1="";
						byte len= tablesTableFile.readByte();
						for(int j=0;j<len;j++)
							str1 += (char)tablesTableFile.readByte();
						if(schema.equalsIgnoreCase(str1)){
							flag = 1;
							//System.out.println("hi 0000");
						}
						if(tableName.equalsIgnoreCase(str1) && flag==1){
							flag1=1;
							//System.out.println("hi 1111");
						}
					}
					if(flag1==1){
						rows = tablesTableFile.readLong();
						//System.out.println("hii 3333 " + rows);
					}
					else{
						tablesTableFile.readLong();
						//System.out.println("hiii 4444");
					}
				}
			}
			flag=0;
			flag1=0;
			RandomAccessFile tablesTableFile1 = new RandomAccessFile("information_schema.tables.tbl", "rw");
			for(int i=0;i<tablesTableFile1.length();i++){
				flag=0;
				flag1=0;
				if(tablesTableFile1.getFilePointer()<tablesTableFile1.length()){
					//System.out.println(schemataTableFile.getFilePointer()<schemataTableFile.length());
					for(int k=0;k<2;k++){
						String str1="";
						byte len= tablesTableFile1.readByte();
						for(int j=0;j<len;j++)
							str1 += (char)tablesTableFile1.readByte();
						if(schema.equalsIgnoreCase(str1)){
							flag = 1;
						}
						if(tableName.equalsIgnoreCase(str1) && flag==1){
							flag1=1;
						}
					}
					if(flag1==1){
						//System.out.println("hi 2222");
						rows=rows+1;
						tablesTableFile1.writeLong(rows);
					}
					else
						tablesTableFile1.readLong();
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("resource")
	private static void showTables() {
		int flag=0;
		String str1 = null;
		String str;
		long len1;
		try {
			RandomAccessFile tablesTableFile = new RandomAccessFile("information_schema.tables.tbl", "rw");
			for(int i=0;i<tablesTableFile.length();i++){
				str1="";
				if(tablesTableFile.getFilePointer()<tablesTableFile.length()){
					//System.out.println(schemataTableFile.getFilePointer()<schemataTableFile.length());
					for(int k=0;k<2;k++){
						str="";
						byte len= tablesTableFile.readByte();
						for(int j=0;j<len;j++){
							str += (char)tablesTableFile.readByte();
							if(flag==1){
								str1 = str;
							}
						}
						if(schemaName.equalsIgnoreCase(str)){
							flag=1;
						}
						else
							flag=0;
						if(!str1.isEmpty()){
							System.out.println(str1);
						}
					}

					tablesTableFile.readLong();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void splitter(String command){
		String[] call = null;
		String[] n = command.split("[(]");
		int flag=0,j=0;
		String str=n[0];
		String sub = command.substring(str.length()+1, command.length()-1);
		String[] toke =null;
		//System.out.println(sub);
		for(int k= j+1; k<n.length; k++)
		{
			toke = sub.split("[,]");
		}
		call = new String[toke.length+1];
		call[0]=str;
		for(int y=0; y< toke.length ;y++)
		{
			call[y+1] = toke[y];
		}
		createTable(call);

	}

	@SuppressWarnings("resource")
	public static void createTable(String[] strArray)
	{

		int j=0;
		String strLine="";
		String tablefilename=null;
		String tableName= null;
		try {
			RandomAccessFile columnsTableFile = new RandomAccessFile("information_schema.columns.tbl", "rw");
			for (int h=0; h<strArray.length;h++)   {
				strLine = strArray[h];
				String[] tokens = strLine.split(" ");
				for(int i=0; i<tokens.length; i++)
				{

					if(tokens[i].equalsIgnoreCase("TABLE"))
					{
						tableName = tokens[i+1];
						tablefilename = schemaName+"."+tokens[i+1]+".tbl";
						//System.out.println("table name"+tablefilename);
					}

				}  
				int len=tokens.length;
				if(tokens[0].equals("")){
					for(int u=0;u<tokens.length-1;u++)
					{
						tokens[u]=tokens[u+1];
						//System.out.println("i is "+u+" "+tokens[u]);
					}
					len = tokens.length-1;
				}		

				if(j!=0){
					incrementRows("information_schema","columns");
					columnsTableFile.seek(columnsTableFile.length());
					columnsTableFile.writeByte(schemaName.length()); // TABLE_SCHEMA
					columnsTableFile.writeBytes(schemaName);
					columnsTableFile.writeByte(tableName.length()); // TABLE_NAME
					columnsTableFile.writeBytes(tableName);
					columnsTableFile.writeByte(tokens[0].length()); // COLUMN_NAME
					columnsTableFile.writeBytes(tokens[0]);
					columnsTableFile.writeInt(j); // ORDINAL_POSITION
					RandomAccessFile indexFile = new RandomAccessFile(schemaName.toLowerCase()+"."+tableName.toLowerCase()+"."+tokens[0].toLowerCase()+".ndx","rw");
					if(len>2){
						if(tokens[1].equalsIgnoreCase("SHORT") || tokens[1].equalsIgnoreCase("LONG")){
							if(tokens[2].equalsIgnoreCase("INT")){
								String str = tokens[1]+" "+tokens[2];
								columnsTableFile.writeByte(str.length()); // COLUMN_TYPE
								columnsTableFile.writeBytes(str);
								if(len>3){
									if(tokens[3].equalsIgnoreCase("PRIMARY")){
										columnsTableFile.writeByte("NO".length()); // IS_NULLABLE
										columnsTableFile.writeBytes("NO");
										columnsTableFile.writeByte("PRI".length()); // COLUMN_KEY
										columnsTableFile.writeBytes("PRI");
									}

									if(tokens[3].equalsIgnoreCase("NOT")){
										columnsTableFile.writeByte("NO".length()); // IS_NULLABLE
										columnsTableFile.writeBytes("NO");
										columnsTableFile.writeByte("".length()); // COLUMN_KEY
										columnsTableFile.writeBytes("");
									}
								}
								else{
									//System.out.println("in length yes: "+len);
									columnsTableFile.writeByte("YES".length()); // IS_NULLABLE
									columnsTableFile.writeBytes("YES");
									columnsTableFile.writeByte("".length()); // COLUMN_KEY
									columnsTableFile.writeBytes("");
								}
							}
							else{
								String str = tokens[1];
								columnsTableFile.writeByte(str.length()); // COLUMN_TYPE
								columnsTableFile.writeBytes(str);
								if(len>2){
									if(tokens[2].equalsIgnoreCase("PRIMARY")){
										columnsTableFile.writeByte("NO".length()); // IS_NULLABLE
										columnsTableFile.writeBytes("NO");
										columnsTableFile.writeByte("PRI".length()); // COLUMN_KEY
										columnsTableFile.writeBytes("PRI");
									}

									if(tokens[2].equalsIgnoreCase("NOT")){
										columnsTableFile.writeByte("NO".length()); // IS_NULLABLE
										columnsTableFile.writeBytes("NO");
										columnsTableFile.writeByte("".length()); // COLUMN_KEY
										columnsTableFile.writeBytes("");
									}
								}
								else{
									//System.out.println("in length yes: "+len);
									columnsTableFile.writeByte("YES".length()); // IS_NULLABLE
									columnsTableFile.writeBytes("YES");
									columnsTableFile.writeByte("".length()); // COLUMN_KEY
									columnsTableFile.writeBytes("");
								}
							}
						}

						else{
							columnsTableFile.writeByte(tokens[1].length()); // COLUMN_TYPE
							columnsTableFile.writeBytes(tokens[1]);
							if(tokens[2].equalsIgnoreCase("PRIMARY")){
								columnsTableFile.writeByte("NO".length()); // IS_NULLABLE
								columnsTableFile.writeBytes("NO");
								columnsTableFile.writeByte("PRI".length()); // COLUMN_KEY
								columnsTableFile.writeBytes("PRI");
							}

							if(tokens[2].equalsIgnoreCase("NOT")){
								columnsTableFile.writeByte("NO".length()); // IS_NULLABLE
								columnsTableFile.writeBytes("NO");
								columnsTableFile.writeByte("".length()); // COLUMN_KEY
								columnsTableFile.writeBytes("");
							}
						}
					}

					else{
						//String type= tokens[1].substring(0, tokens[1].length());
						columnsTableFile.writeByte(tokens[1].length()); // COLUMN_TYPE
						columnsTableFile.writeBytes(tokens[1]);
						columnsTableFile.writeByte("YES".length()); // IS_NULLABLE
						columnsTableFile.writeBytes("YES");
						columnsTableFile.writeByte("".length()); // COLUMN_KEY
						columnsTableFile.writeBytes("");
					}
				}

				j++;
			}
			incrementRows("information_schema","tables");
			tablefilename = schemaName.toLowerCase()+"."+tableName.toLowerCase()+".tbl";
			//System.out.println("table name is : "+tablefilename);
			RandomAccessFile newTable = new RandomAccessFile(tablefilename.toLowerCase(),"rw");
			System.out.println("Table Successfully Created");
			RandomAccessFile tablesTableFile = new RandomAccessFile("information_schema.tables.tbl", "rw");
			tablesTableFile.seek(tablesTableFile.length());
			tablesTableFile.writeByte(schemaName.length());
			tablesTableFile.writeBytes(schemaName);
			tablesTableFile.writeByte(tableName.length());
			tablesTableFile.writeBytes(tableName);
			tablesTableFile.writeLong(0);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	private static void useSchema(String command) {
		RandomAccessFile schemataTableFile;
		String str=command;
		int flag=0;
		try {
			if(str != null){
				String[] tokens = str.split(" ");
				schemaName=tokens[1];
			}
			schemataTableFile = new RandomAccessFile("information_schema.schemata.tbl", "rw");
			for(int i=0;i<schemataTableFile.length();i++){
				String str1="";
				if(schemataTableFile.getFilePointer()<schemataTableFile.length()){
					//System.out.println(schemataTableFile.getFilePointer()<schemataTableFile.length());
					byte len= schemataTableFile.readByte();
					for(int j=0;j<len;j++)
						str1 += (char)schemataTableFile.readByte();
					if(schemaName.equalsIgnoreCase(str1)){
						flag=1;
					}
				}
			}
			if(flag !=1)
				System.out.println("Schema doesnot exist");
			else
				System.out.println("Schema changed");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("resource")
	private static void showSchemas() {
		try {
			RandomAccessFile schemataTableFile = new RandomAccessFile("information_schema.schemata.tbl", "rw");
			for(int i=0;i<schemataTableFile.length();i++){
				if(schemataTableFile.getFilePointer()<schemataTableFile.length()){
					//System.out.println(schemataTableFile.getFilePointer()<schemataTableFile.length());
					byte len= schemataTableFile.readByte();
					for(int j=0;j<len;j++)
						System.out.print((char)schemataTableFile.readByte());
					System.out.println();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private static void createSchema(String command) {
		RandomAccessFile schemataTableFile;
		try {
			String str=command;
			if(str != null){
				String[] tokens = str.split(" ");
				//for(int i=0;i<tokens.length;i++)
				//System.out.println("i is "+ i + " "+ tokens[i]);
				schemaName=tokens[2];
				//System.out.println(schemaName);
				incrementRows("information_schema","schemata");
				schemataTableFile = new RandomAccessFile("information_schema.schemata.tbl", "rw");
				schemataTableFile.seek(schemataTableFile.length());
				schemataTableFile.writeByte(schemaName.length());
				schemataTableFile.writeBytes(schemaName);
				System.out.println("Schema Created Successfully");
				schemaName="information_schema";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}


	}


	/**
	 *  Help: Display supported commands
	 */
	public static void help() {
		System.out.println(line("*",80));
		System.out.println();
		System.out.println("\tSHOW SCHEMAS            Displays all schemas defined in database.");
		System.out.println("\tUSE           		Chooses a schema.");
		System.out.println("\tSHOW TABLES		Displays all tables in the currently chosen schema.");
		System.out.println("\tCREATE SCHEMA    	Creates a new schema to hold tables. ");
		System.out.println("\tCREATE TABLE	 	Creates a new table schema, i.e. a new empty table");
		System.out.println("\tINSERT INTO TABLE 	Inserts a row/record into a table");
		System.out.println("\tSELECT-FROM-WHERE 	style query");
		System.out.println("\tversion;       		Show the program version.");
		System.out.println("\thelp;          		Show this help information");
		System.out.println("\texit;          		Exit the program");
		System.out.println();
		System.out.println();
		System.out.println(line("*",80));
	}

	/**
	 *  Display the welcome "splash screen"
	 */
	public static void splashScreen() {
		System.out.println(line("*",80));
		System.out.println("Welcome to DavisBaseLite"); // Display the string.
		version();
		System.out.println("Type \"help;\" to display supported commands.");
		System.out.println(line("*",80));
	}

	/**
	 * @param s The String to be repeated
	 * @param num The number of time to repeat String s.
	 * @return String A String object, which is the String s appended to itself num times.
	 */
	public static String line(String s,int num) {
		String a = "";
		for(int i=0;i<num;i++) {
			a += s;
		}
		return a;
	}

	/**
	 * @param num The number of newlines to be displayed to <b>stdout</b>
	 */
	public static void newline(int num) {
		for(int i=0;i<num;i++) {
			System.out.println();
		}
	}

	public static void version() {
		System.out.println("DavisBaseLite v1.0\n");
	}

}



