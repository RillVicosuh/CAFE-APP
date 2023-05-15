// BELOW IS THE CORRECT CODE
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Cafe {
   // reference to physical database connection.
   private Connection _connection = null;
   private static String authorisedUser;
   private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
   // handling the keyboard inputs through a BufferedReader
   // This variable can be global for convenience.
   static BufferedReader in = new BufferedReader(
           new InputStreamReader(System.in));
   /**
    * Creates a new instance of Cafe
    *
    * @param hostname the MySQL or PostgreSQL server hostname
    * @param database the name of the database
    * @param username the user name used to login to the database
    * @param password the user login password
    * @throws java.sql.SQLException when failed to make a connection.
    */
   public Cafe(String dbname, String dbport, String user, String passwd) throws SQLException {
      System.out.print("Connecting to database...");
      try{
         // constructs the connection URL
         String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
         System.out.println ("Connection URL: " + url + "\n");
         // obtain a physical connection
         this._connection = DriverManager.getConnection(url, user, passwd);
         System.out.println("Done");
      }catch (Exception e){
         System.err.println("Error - Unable to Connect to Database: " + e.getMessage() );
         System.out.println("Make sure you started postgres on this machine");
         System.exit(-1);
      }//end catch
   }//end Cafe
   /**
    * Method to execute an update SQL statement.  Update SQL instructions
    * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
    *
    * @param sql the input SQL string
    * @throws java.sql.SQLException when update failed
    */
   public void executeUpdate (String sql) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();
      // issues the update instruction
      stmt.executeUpdate (sql);
      // close the instruction
      stmt.close ();
   }//end executeUpdate
   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and outputs the results to
    * standard out.
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQueryAndPrintResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and output them to standard out.
      boolean outputHeader = true;
      while (rs.next()){
         if(outputHeader){
            for(int i = 1; i <= numCol; i++){
               System.out.print(rsmd.getColumnName(i) + "\t");
            }
            System.out.println();
            outputHeader = false;
         }
         for (int i=1; i<=numCol; ++i)
            System.out.print (rs.getString (i) + "\t");
         System.out.println ();
         ++rowCount;
      }//end while
      stmt.close ();
      return rowCount;
   }//end executeQuery
   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the results as
    * a list of records. Each record in turn is a list of attribute values
    *
    * @param query the input query string
    * @return the query result as a list of records
    * @throws java.sql.SQLException when failed to execute the query
    */
   public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and saves the data returned by the query.
      boolean outputHeader = false;
      List<List<String>> result  = new ArrayList<List<String>>();
      while (rs.next()){
         List<String> record = new ArrayList<String>();
         for (int i=1; i<=numCol; ++i)
            record.add(rs.getString (i));
         result.add(record);
      }//end while
      stmt.close ();
      return result;
   }//end executeQueryAndReturnResult
   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the number of results
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQuery (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      int rowCount = 0;

      // iterates through the result set and count nuber of results.
      while (rs.next()){
         rowCount++;
      }//end while
      stmt.close ();
      return rowCount;
   }
   /**
    * Method to fetch the last value from sequence. This
    * method issues the query to the DBMS and returns the current
    * value of sequence used for autogenerated keys
    *
    * @param sequence name of the DB sequence
    * @return current value of a sequence
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int getCurrSeqVal(String sequence) throws SQLException {
      Statement stmt = this._connection.createStatement ();

      ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
      if (rs.next())
         return rs.getInt(1);
      return -1;
   }
   /**
    * Method to close the physical connection if it is open.
    */
   public void cleanup(){
      try{
         if (this._connection != null){
            this._connection.close ();
         }//end if
      }catch (SQLException e){
         // ignored.
      }//end try
   }//end cleanup
   /**
    * The main execution method
    *
    * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
    */
   public static void main (String[] args) {
      if (args.length != 3) {
         System.err.println (
                 "Usage: " +
                         "java [-classpath <classpath>] " +
                         Cafe.class.getName () +
                         " <dbname> <port> <user>");
         return;
      }//end if
      Greeting();
      Cafe esql = null;
      try{
         // use postgres JDBC driver.
         Class.forName ("org.postgresql.Driver").newInstance ();
         // instantiate the Cafe object and creates a physical
         // connection.
         String dbname = args[0];
         String dbport = args[1];
         String user = args[2];
         esql = new Cafe (dbname, dbport, user, "");

         boolean keepon = true;
         while(keepon) {
            // These are sample SQL statements
            System.out.println("MAIN MENU");
            System.out.println("---------");
            System.out.println("1. Create user");
            System.out.println("2. Log in");
            System.out.println("9. < EXIT");
            authorisedUser = null;
            switch (readChoice()){
               case 1: CreateUser(esql); break;
               case 2: authorisedUser = LogIn(esql); break;
               case 9: keepon = false; break;
               default : System.out.println("Unrecognized choice!"); break;
            }//end switch
            if (authorisedUser != null) {
               boolean usermenu = true;
               while(usermenu) {
                  System.out.println("MAIN MENU");
                  System.out.println("---------");
                  System.out.println("1. Go to Menu");
                  System.out.println("2. Update Profile");
                  System.out.println("3. Place a Order");
                  System.out.println("4. Update a Order");
                  System.out.println(".........................");
                  System.out.println("9. Log out");
                  switch (readChoice()){
                     case 1: Menu(esql); break;
                     case 2: UpdateProfile(esql); break;
                     case 3: PlaceOrder(esql); break;
                     case 4: UpdateOrder(esql); break;
                     case 9: usermenu = false; break;
                     default : System.out.println("Unrecognized choice!"); break;
                  }
               }
            }
         }//end while
      }catch(Exception e) {
         System.err.println (e.getMessage ());
      }finally{
         // make sure to cleanup the created table and close the connection.
         try{
            if(esql != null) {
               System.out.print("Disconnecting from database...");
               esql.cleanup ();
               System.out.println("Done\n\nBye !");
            }//end if
         }catch (Exception e) {
            // ignored.
         }//end try
      }//end try
   }//end main
   public static void Greeting(){
      System.out.println(
              "\n\n*******************************************************\n" +
                      "              User Interface      	               \n" +
                      "*******************************************************\n");
   }//end Greeting
   /*
    * Reads the users choice given from the keyboard
    * @int
    **/
   public static int readChoice() {
      int input;
      // returns only if a correct value is given.
      do {
         System.out.print("Please make your choice: ");
         try { // read the integer, parse it and break.
            input = Integer.parseInt(in.readLine());
            break;
         }catch (Exception e) {
            System.out.println("Your input is invalid!");
            continue;
         }//end try
      }while (true);
      return input;
   }//end readChoice
   /*
    * Creates a new user with privided login, passowrd and phoneNum
    **/
   public static void CreateUser(Cafe esql){
      try{
         System.out.print("\tEnter user login: ");
         String login = in.readLine();
         System.out.print("\tEnter user password: ");
         String password = in.readLine();
         System.out.print("\tEnter user phone: ");
         String phone = in.readLine();

         String type="Customer";
         String favItems="";

         String query = String.format("INSERT INTO USERS (phoneNum, login, password, favItems, type) VALUES ('%s','%s','%s','%s','%s')", phone, login, password, favItems, type);

         esql.executeUpdate(query);
         System.out.println ("User successfully created!");
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }//end CreateUser
   /*
    * Check log in credentials for an existing user
    * @return User login or null is the user does not exist
    **/
   public static String LogIn(Cafe esql){
      try{
         System.out.print("\tEnter user login: ");
         String login = in.readLine();
         System.out.print("\tEnter user password: ");
         String password = in.readLine();

         String query = String.format("SELECT * FROM USERS WHERE login = '%s' AND password = '%s'", login, password);
         int userNum = esql.executeQuery(query);
         if (userNum > 0)
            return login;
         return null;
      }catch(Exception e){
         System.err.println (e.getMessage ());
         return null;
      }
   }//end
   // Rest of the functions definition go in here
   // Returns one of the different types of users: Customer, Employee, Manager
   public static String userType(Cafe esql){
      String user = "NONE";
      try{
         String query = String.format("SELECT type FROM USERS WHERE login = '%s'", authorisedUser);
         List<List<String>> userResult = esql.executeQueryAndReturnResult(query);
         //ensure there is data
         if(userResult.size() >= 1){
            user = userResult.get(0).get(0);
         }
         else{
            System.err.println("No user detected.");
            return null;
         }
      }
      catch(Exception e){
         System.err.println(e.getMessage());
         return null;
      }
      return user;
   }//NOT SAME
   public static void Menu(Cafe esql){
      boolean menuContinue = true;
      String user = userType(esql);
      try{
         switch(user){
            case "Customer":
            case "Employee":
               while(menuContinue){
                  System.out.println("Menu Options");
                  System.out.println("------------");
                  System.out.println("1. Browse the menu through an item's name");
                  System.out.println("2. Browse the menu through an item's type");
                  System.out.println("3. Exit");
                  switch(readChoice()){
                     case 1: browseName(esql); break;
                     case 2: browseType(esql); break;
                     case 3: menuContinue = false; break;
                  }
               }
               break;
            case "Manager ":
               while(menuContinue){
                  System.out.println("Menu Options");
                  System.out.println("------------");
                  System.out.println("1. Browse the menu through an item's name");
                  System.out.println("2. Browse the menu through an item's type");
                  System.out.println("3. Update the menu");
                  System.out.println("4. Exit");
                  switch(readChoice()){
                     case 1: browseName(esql); break;
                     case 2: browseType(esql); break;
                     case 3: managerMenuUpdate(esql); break;
                     case 4: menuContinue = false; break;
                  }
               }
               break;
         }
      }
      catch(Exception e){
         System.err.println(e.getMessage());
      }
   }
   public static void browseName(Cafe esql){
      try{
         String query = "SELECT itemName, type, price, description FROM MENU WHERE itemName = ";
         System.out.print("Please enter the item's name: ");
         String userInput = in.readLine();
         userInput = "'" + userInput + "'";
         query += userInput;
         int result = esql.executeQueryAndPrintResult(query);
      }
      catch(Exception e){
         System.err.println(e.getMessage());
      }
   }//NOT SAME
   public static void browseType(Cafe esql){
      try{
         String query = "SELECT itemName, type, price, description FROM MENU WHERE type = ";
         System.out.print("Please enter the type of item: ");
         String userInput = in.readLine();
         userInput = "'" + userInput + "'";
         query += userInput;
         int result = esql.executeQueryAndPrintResult(query);
      }
      catch(Exception e){
         System.err.println(e.getMessage());
      }
   }//NOT SAME
   public static void managerMenuUpdate(Cafe esql){
      boolean updateContinue = true;
      String query;
      String itemName;
      String itemType;
      float itemPrice;
      String itemDesc;
      String itemImage;
      int result;
      while(updateContinue){
         try{
            System.out.println("UPDATE MENU OPTIONS");
            System.out.println("-------------------");
            System.out.println("1. Add an item");
            System.out.println("2. Delete an item");
            System.out.println("3. Update an item");
            System.out.println("4. Exit");
            switch(readChoice()){
               case 1:
                  System.out.println("Enter the item's name: ");
                  itemName = in.readLine();
                  if(itemName.length() == 0){
                     System.out.println("An item name needs to be entered.");
                     break;
                  }
                  System.out.println("Enter the type of item: ");
                  itemType = in.readLine();
                  if(itemType.length() == 0){
                     System.out.println("An item type needs to be entered.");
                     break;
                  }
                  System.out.println("Enter the price of the item: ");
                  try{
                     itemPrice = Float.parseFloat(in.readLine());
                  }
                  catch(Exception e){
                     System.out.println("A correct value needs to be entered");
                     break;
                  }
                  System.out.println("Enter the description for the item: ");
                  itemDesc = in.readLine();
                  System.out.println("Enter an image URL for the item: ");
                  itemImage = in.readLine();
                  query = String.format("INSERT INTO MENU (itemName, type, price, description, imageURL) VALUES ('%s', '%s', '%s', '%s', '%s', '%s')", itemName, itemType, itemPrice, itemDesc, itemImage);
                  break;
               case 2:
                  System.out.println("Enter the item's name: ");
                  itemName = in.readLine();
                  query = String.format("SELECT * FROM MENU WHERE itemName = '%s'", itemName);
                  result = esql.executeQuery(query);
                  if(result >= 1){
                     query = String.format("DELETE FROM MENU WHERE itemName = '%s'", itemName);
                     esql.executeUpdate(query);
                     System.out.println("Item deleted from the menu.");
                  }
                  else{
                     System.out.println("There is no item with this name in the menu.");
                  }
                  break;
               case 3:
                  boolean updateItemContinue = true;
                  System.out.println("Enter the item's name: ");
                  itemName = in.readLine();
                  query = String.format("SELECT * FROM MENU WHERE itemName = '%s'", itemName);
                  result = esql.executeQuery(query);
                  if(result >= 1){
                     while(updateItemContinue){
                        System.out.println("UPDATE ITEM OPTIONS");
                        System.out.println("-------------------");
                        System.out.println("1. Update type");
                        System.out.println("2. Update price");
                        System.out.println("3. Update description");
                        System.out.println("4. Update imageURL");
                        System.out.println("5. Exit");
                        switch(readChoice()){
                           case 1:
                              System.out.println("Enter the type of item: ");
                              itemType = in.readLine();
                              if(itemType.length() == 0){
                                 System.out.println("An item type needs to be entered.");
                                 break;
                              }
                              query = String.format("UPDATE MENU SET type = '%s' WHERE itemName = '%s'", itemType, itemName);
                              esql.executeUpdate(query);
                              System.out.println("The item type was update.");
                              break;
                           case 2:
                              System.out.println("Enter the price of the item: ");
                              try{
                                 itemPrice = Float.parseFloat(in.readLine());
                              }
                              catch(Exception e){
                                 System.out.println("A correct value needs to be entered.");
                                 break;
                              }
                              query = String.format("UPDATE MENU SET price = '%s' WHERE itemName = '%s'", itemPrice, itemName);
                              esql.executeUpdate(query);
                              System.out.println("The item price was updated.");
                              break;
                           case 3:
                              System.out.println("Enter the description for the item: ");
                              itemDesc = in.readLine();
                              query = String.format("UPDATE MENU SET description = '%s' WHERE itemName = '%s'", itemDesc, itemName);
                              esql.executeUpdate(query);
                              System.out.println("The item description was updated.");
                              break;
                           case 4:
                              System.out.println("Enter an image URL for the item: ");
                              itemImage = in.readLine();
                              query = String.format("UPDATE MENU SET imageURL = '%s' WHERE itemName = '%s'", itemImage, itemName);
                              esql.executeUpdate(query);
                              System.out.println("The item image URL was updated.");
                              break;
                           case 5:
                              updateItemContinue = false;
                              break;
                        }
                     }
                  }
                  else{
                     System.out.println("There is no item with this name in the menu.");
                  }
                  break;
               case 4:
                  updateContinue = false;
                  break;
            }
         }
         catch(Exception e){
            System.err.println(e.getMessage());
         }
      }
   } //
   public static void UpdateProfile(Cafe esql){
      //boolean profileContinue = true;
      String user = userType(esql);
      try{
         switch(user){
            case "Customer":
            case "Employee":
               System.out.println("REDIRECTING TO PROFILE UPDATE");
               System.out.println("------------");
               userProfileUpdate(esql);
               break;
            case "Manager ":
               System.out.println("REDIRECTING TO PROFILE UPDATE");
               System.out.println("------------");
               managerProfileUpdate(esql);
               break;
         }
      }
      catch(Exception e){
         System.err.println(e.getMessage());
      }
   }
   public static void userProfileUpdate(Cafe esql){
      boolean updateProfileContinue = true;
      String query;
      String itemName;
      String userPassword;
      int result;
      while(updateProfileContinue){
         try{
            System.out.println("UPDATE PROFILE OPTIONS");
            System.out.println("----------------------");
            System.out.println("1. Update your favorit items");
            System.out.println("2. Change your password");
            System.out.println("3. Exit");
            switch(readChoice()){
               case 1:
                  query = String.format("SELECT favItems FROM USERS WHERE login = '%s'", authorisedUser);
                  System.out.println("CURRENT FAVORITE ITEMS");
                  System.out.println("----------------------");
                  esql.executeQueryAndPrintResult(query);
                  System.out.println("Enter your new favorite items: ");
                  itemName = in.readLine();
                  query = String.format("UPDATE USERS SET favItems = '%s' WHERE login = '%s'", itemName, authorisedUser);
                  esql.executeUpdate(query);
                  System.out.println("Your favorite items have been updated.");
                  break;
               case 2:
                  System.out.println("Please enter your current password: ");
                  userPassword = in.readLine();
                  query = String.format("SELECT * FROM USERS WHERE login = '%s' AND password = '%s'", authorisedUser, userPassword);
                  result = esql.executeQuery(query);
                  if(result >= 1){
                     System.out.println("Please enter your new password: ");
                     userPassword = in.readLine();
                     if(userPassword.length() == 0){
                        System.out.println("A valid password needs to be entered.");
                        break;
                     }
                     query = String.format("UPDATE USERS SET password = '%s' WHERE login = '%s'", userPassword, authorisedUser);
                     esql.executeUpdate(query);
                     System.out.println("Your password has been updated.");
                  }
                  else{
                     System.out.println("Invalid password. Try again.");
                  }
                  break;
               case 3:
                  updateProfileContinue = false;
                  break;
            }
         }
         catch(Exception e){
            System.out.println(e.getMessage());
         }
      }
   }
   public static void managerProfileUpdate(Cafe esql){
      String selectedUser;
      boolean updateProfileContinue = true;
      String query;
      String itemName;
      String userPassword;
      int result;
      boolean typeChoose;
      while(updateProfileContinue){
         try{
            System.out.println("UPDATE PROFILE OPTIONS");
            System.out.println("----------------------");
            System.out.println("1. Update your favorit items");
            System.out.println("2. Change your password");
            System.out.println("3. Update the user's type");
            System.out.println("4. Exit");
            switch(readChoice()){
               case 1:
                  System.out.println("Enter the name of the user to update: ");
                  selectedUser = in.readLine();
                  query = String.format("SELECT * FROM USERS WHERE login = '%s'", selectedUser);
                  result = esql.executeQuery(query);
                  if(result >= 1){
                     query = String.format("SELECT favItems FROM USERS WHERE login = '%s'", selectedUser);
                     System.out.println("CURRENT FAVORITE ITEMS");
                     System.out.println("----------------------");
                     esql.executeQueryAndPrintResult(query);
                     System.out.println("Enter the new favorite items: ");
                     itemName = in.readLine();
                     query = String.format("UPDATE USERS SET favItems = '%s' WHERE login = '%s'", itemName, selectedUser);
                     esql.executeUpdate(query);
                     System.out.println("The favorite items have been updated.");
                  }
                  else{
                     System.out.println("This is not an existing user.");
                  }
                  break;
               case 2:
                  System.out.println("Enter the name of the user to update: ");
                  selectedUser = in.readLine();
                  query = String.format("SELECT * FROM USERS WHERE login = '%s'", selectedUser);
                  result = esql.executeQuery(query);
                  if(result >= 1){
                     System.out.println("Please enter the new password: ");
                     userPassword = in.readLine();
                     if(userPassword.length() == 0){
                        System.out.println("A valid password needs to be entered.");
                        break;
                     }
                     query = String.format("UPDATE USERS SET password = '%s' WHERE login = '%s'", userPassword, selectedUser);
                     esql.executeUpdate(query);
                     System.out.println("The password has been updated.");
                  }
                  else{
                     System.out.println("This is not an existing user.");
                  }
                  break;
               case 3:
                  System.out.println("Enter the name of the user to update: ");
                  selectedUser = in.readLine();
                  query = String.format("SELECT * FROM USERS WHERE login = '%s'", selectedUser);
                  result = esql.executeQuery(query);
                  if(result >= 1){
                     typeChoose = true;
                     while(typeChoose){
                        System.out.println("USER TYPE OPTIONS");
                        System.out.println("-----------------");
                        System.out.println("1. Customer");
                        System.out.println("2. Employee");
                        System.out.println("3. Manager");
                        System.out.println("4. Exit");
                        switch(readChoice()){
                           case 1:
                              query = String.format("UPDATE USERS SET type = 'Customer' WHERE login = '%s'", selectedUser);
                              esql.executeUpdate(query);
                              System.out.println("The user's type has been updated.");
                              break;
                           case 2:
                              query = String.format("UPDATE USERS SET type = 'Employee' WHERE login = '%s'", selectedUser);
                              esql.executeUpdate(query);
                              System.out.println("The user's type has been updated.");
                              break;
                           case 3:
                              query = String.format("UPDATE USERS SET type = 'Manager' WHERE login = '%s'", selectedUser);
                              esql.executeUpdate(query);
                              System.out.println("The user's type has been updated.");
                              break;
                           case 4:
                              typeChoose = false;
                              break;
                        }
                     }
                  }
                  else{
                     System.out.println("This is not an existing user.");
                  }
                  break;
               case 4:
                  updateProfileContinue = false;
                  break;
            }
         }
         catch(Exception e){
            System.out.println(e.getMessage());
         }
      }
   }
   public static boolean FindItem(Cafe esql,String name){
      try{
         String query = "SELECT m.itemName FROM Menu AS m WHERE m.itemName LIKE '%";
         query+=name+'%'+'\'';
         int row = esql.executeQuery(query);
         if(row==1){
            return true;
         }
         else{
            return false;
         }
      }catch(Exception e){
         System.err.println (e.getMessage ());
         return false;
      }
   }//end
   public static List<List<String>> FindMaxOrderID(Cafe var0) {
      try {
         String var1 = "SELECT * FROM Orders AS o WHERE o.orderid = (SELECT MAX(orderid) FROM Orders)";
         List var2 = var0.executeQueryAndReturnResult(var1);
         return var2.isEmpty() ? null : var2;
      } catch (Exception var3) {
         System.err.println(var3.getMessage());
         return null;
      }
   }
   public static void PlaceOrder(Cafe esql){
         try{
            boolean menuContinue= true;
            boolean paid = false;
            boolean validIn = true;
            float price =0;
            String[] item_array;
            String inputItemName;
            System.out.println();
            String userInfo = "SELECT * FROM Users WHERE login LIKE '%" + authorisedUser +'%'+'\''; // Global Login
            List<List<String>> info = esql.executeQueryAndReturnResult(userInfo);
            while(menuContinue){
               System.out.println();
               System.out.println("                  Place Order ");
               System.out.println("-------------------------------------------------");
               System.out.println("1: View Menu");
               System.out.println("2: Input Order");
               System.out.println("9: Exit Menu");
               switch(readChoice()){
                  case 1:
                     String query = "SELECT m.itemname, m.type, m.price, m.description FROM Menu AS m;";
                     esql.executeQueryAndPrintResult(query);
                     break;
                  case 2:
                     do{
                        System.out.println("          Input Order     ");
                        System.out.println("------------------------------");
                        System.out.println("Instructions: When inputting the items you wish to purchase, please seperate them by a |,| comma");
                        System.out.print("Enter the item names you wish to purchase: ");
                        inputItemName = in.readLine();
                        item_array = inputItemName.split(",");
                        for(int i=0; i < item_array.length;i++){
                           validIn = FindItem(esql,item_array[i].trim());
                           if(validIn==false){
                              break;
                           }
                           price += FindPrice(esql,item_array[i]);

                        }
                     }while(validIn == false);
                     String priceString = String.format("Total Price: %.2f", price);
                     System.out.println(priceString);
                     System.out.println();
                     System.out.print("Would you like to pay now or later enter (yes) for now and (no) for later: ");
                     String payNow = in.readLine();
                     payNow.toLowerCase();
                     System.out.println(payNow);
                     if(payNow.charAt(0)=='y'){
                        paid = true;
                     }
                     else{
                        paid = false;
                     }

                     Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                     Date date = new Date();
                     //////-----
                     String nowString = String.format("INSERT INTO ORDERS (orderid,login,paid,timeStampRecieved,total) VALUES(DEFAULT,'%s','%s','%s',%f);",info.get(0).get(0).trim(),String.valueOf(paid),sdf.format(timestamp),price);
                     esql.executeUpdate(nowString);

                     List<List<String>> orderInfo = FindMaxOrderID(esql);
                     if(orderInfo != null){
                        String hasnot = "Hasn''t Started";
                        for(int i=0; i<item_array.length;i++){
                           ///////-----
                           String insertItem = String.format("INSERT INTO ITEMSTATUS (orderid,itemName,lastUpdated,status,comments) VALUES ("+Integer.parseInt(orderInfo.get(0).get(0))+",'%s','%s','%s','%s');",item_array[i],sdf.format(timestamp),hasnot,"");
                           esql.executeUpdate(insertItem);
                        }
                        System.out.println("Success! Your Order details are below. ");
                        System.out.println("------------------------------------------------------------");
                        String test = String.format("OrderID: %s Login: %s Paid: %s TimeStamp: %s Total Price: = %s ",orderInfo.get(0).get(0),orderInfo.get(0).get(1).trim(),orderInfo.get(0).get(2),orderInfo.get(0).get(3),orderInfo.get(0).get(4));
                        System.out.println(test);
                     }
                     else{
                        System.out.println("Failed! Your order was not placed, please try again.");
                     }
                     price = 0;

                     break;
                  case 9:
                     menuContinue= false;
                     break;
                  default:System.out.println("Unrecognized choice!"); break;
               }
            }


         }catch(Exception e){
            System.err.println (e.getMessage ());
         }
   }
   public static float FindPrice(Cafe esql,String name){
      try{
         String query = "SELECT m.price FROM Menu AS m WHERE m.itemName LIKE '%";
         query+=name+'%'+'\'';
         List<List<String>> price = esql.executeQueryAndReturnResult(query);
         if(price.get(0).get(0)==""){
            return 0;
         }
         else{
            return Float.parseFloat(price.get(0).get(0));
         }
      }catch(Exception e){
         System.err.println (e.getMessage ());
         return 0;
      }
   }//end
   public static boolean OrderIDisFalse(Cafe var0, int var1) {
      try {
         String var2 = "SELECT o.paid FROM Orders AS o WHERE o.orderid =" + var1 + ";";
         List var3 = var0.executeQueryAndReturnResult(var2);
         if (((String)((List)var3.get(0)).get(0)).charAt(0) == 'f') {
            return true;
         } else {
            return ((String)((List)var3.get(0)).get(0)).isEmpty() ? false : false;
         }
      } catch (Exception var4) {
         System.err.println(var4.getMessage());
         return false;
      }
   }
   public static void UpdateOrder(Cafe esql){
      try{
         String user = userType(esql);
         boolean menuContinue= true;
         boolean inModifyMenu = true;
         String[] item_array={};
         String[] del_array ={};
         boolean validIn = true;
         boolean validDel = true;
         float del_price=0;
         String addItem;
         String delItem;
         //  List<List<String>> userOrder;
         float total_price = 0;
         while(menuContinue){
            System.out.println();
            System.out.println("                  Update Orders ");
            System.out.println("-------------------------------------------------");
            System.out.println("1: View Order History");
            System.out.println("2: Update a non-paid Order");
            switch(user){
               case "Employee":
               case "Manager ":
                  System.out.println("3: View non-paid order in the past 24 hours");
                  System.out.println("4: Change non-paid order to paid");
            }
            System.out.println("9: Exit Menu");
            switch(readChoice()){
               case 1:
                  System.out.println();
                  System.out.println("         Top 5 Recent Transaction ");
                  System.out.println("----------------------------------------------");
                  System.out.println();
                  String recentTrans = String.format("SELECT * FROM Orders As o WHERE o.login='%s' ORDER BY o.timeStampRecieved DESC LIMIT 5;",authorisedUser);
                  esql.executeQueryAndPrintResult(recentTrans);

                  System.out.println();
                  System.out.println("If you would like to see the status of your order, Please enter the OrderID you wish to see ");
                  System.out.print("otherwise, leave it empty and press enter: ");
                  String ans = in.readLine();
                  if(ans.isEmpty()){
                     break;
                  }
                  else{
                     String intoStatus = String.format("--------------- Now viewing orderID: %s ------------------ ",ans);
                     System.out.println(intoStatus);
                     System.out.println();
                     String itemStat = String.format("SELECT * FROM ItemStatus AS i WHERE i.orderid = "+Integer.parseInt(ans)+";");
                     esql.executeQueryAndPrintResult(itemStat);
                  }

                  break;
               case 2:
                  System.out.println();
                  System.out.println("          Modify Order Menu ");
                  System.out.println("----------------------------------------------");
                  System.out.println();
                  String modifyMenu = String.format("SELECT * FROM Orders As o WHERE o.login='%s' AND o.paid = 'f' ORDER BY o.timeStampRecieved DESC LIMIT 5;",authorisedUser);
                  esql.executeQueryAndPrintResult(modifyMenu);
                  System.out.println();
                  System.out.print("Please enter the OrderID you wish to modify: ");
                  String idChoice = in.readLine();
                  inModifyMenu= true;
                  while(inModifyMenu){
                     String grabOrderID = String.format("SELECT * FROM Orders as o WHERE o.orderid ="+Integer.parseInt(idChoice)+";");
                     List<List<String>> userOrder = esql.executeQueryAndReturnResult(grabOrderID);


                     System.out.println();
                     System.out.println("----------------------------------------------");
                     System.out.println("1: Add an item to your Order");
                     System.out.println("2: Delete an item in your Order");
                     System.out.println("3: Cancel your whole Order");
                     System.out.println("9: Exit Menu");
                     switch(readChoice()){
                        case 1: //INSERT ITEM
                           do{
                              System.out.println("          Input Order     ");
                              System.out.println("------------------------------");
                              System.out.println("Instructions: When inputting the items you wish to purchase, please seperate them by a |,| comma, If you wish to exit leave it blank and press enter");
                              System.out.print("Enter the item names you wish to purchase: ");
                              addItem = in.readLine();
                              if(addItem.isEmpty()){
                                 break;
                              }
                              item_array = addItem.split(",");
                              for(int i=0; i < item_array.length;i++){
                                 validIn = FindItem(esql,item_array[i].trim());
                                 if(validIn==false){
                                    break;
                                 }
                                 total_price += FindPrice(esql,item_array[i]);

                              }
                           }while(validIn == false);
                           if(addItem.isEmpty()){
                              break;
                           }
                           Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                           Date date = new Date();

                           total_price+= Float.parseFloat(userOrder.get(0).get(4));

                           String updatePrice = "UPDATE Orders SET total = "+total_price+", timeStampRecieved = \'"+sdf.format(timestamp) +" \'  WHERE orderid = "+userOrder.get(0).get(0)+";";
                           esql.executeUpdate(updatePrice);
                           System.out.println("Success! Your new total is $"+total_price+ " The order details will be below");
                           System.out.println("----------------------------------------------------------------------------");
                           String printDetails = "SELECT * FROM Orders As o WHERE o.orderid = "+userOrder.get(0).get(0)+";";
                           esql.executeQueryAndPrintResult(printDetails);
                           String hasnot = "Hasn''t Started";
                           for(int i=0; i < item_array.length; i++){
                              String insertItem = String.format("INSERT INTO ItemStatus (orderid,itemName,lastUpdated,status,comments) VALUES ("+Integer.parseInt(userOrder.get(0).get(0))+",'%s','%s','%s','%s');",item_array[i],sdf.format(timestamp),hasnot,"");
                              esql.executeUpdate(insertItem);
                           }

                           total_price = 0;
                           addItem = "";
                           break;
                        case 2: //DELETE ORDER
                           System.out.println("ENTERED DELTED SECTION: ");
                           do{
                              String delStatus = String.format("--------------- Now viewing orderID: %s ------------------ ",userOrder.get(0).get(0));
                              System.out.println(delStatus);
                              System.out.println();
                              String itemStat = String.format("SELECT * FROM ItemStatus AS i WHERE i.orderid = "+Integer.parseInt(userOrder.get(0).get(0))+";");
                              esql.executeQueryAndPrintResult(itemStat);
                              System.out.println();
                              System.out.println("          Delete Order     ");
                              System.out.println("------------------------------");
                              System.out.println("Instructions: When deleting the items you wish to delete, please seperate them by a |,| comma, If you wish to exit leave it blank and press enter");
                              System.out.print("Enter the item names you wish to delete: ");
                              delItem = in.readLine();
                              if(delItem.isEmpty()){
                                 break;
                              }
                              del_array = delItem.split(",");
                              for(int i=0; i < del_array.length;i++){
                                 validDel = FindItem(esql,del_array[i].trim());
                                 if(validDel==false){
                                    break;
                                 }
                                 del_price += FindPrice(esql,del_array[i]);
                              }
                           }while(validDel == false);
                           if(delItem.isEmpty()){
                              break;
                           }
                      /*(if(del_array.length==1){ ///need to create a error handling to checck if there is only one item left in the itemstatus
                         System.out.println("ENTERED DEL_LENGTH =1");
                         String delOrder = "DELETE FROM ItemStatus WHERE orderid ="+Integer.parseInt(userOrder.get(0).get(0))+"AND itemName = \'"+del_array[0]+"\' ;";
                         String delFromOrder = "DELETE FROM Orders WHERE orderid = "+Integer.parseInt(userOrder.get(0).get(0));
                         esql.executeUpdate(delOrder);
                         esql.executeUpdate(delOrder);
                         break;
                      }*/
                           float new_del_price = Float.parseFloat(userOrder.get(0).get(4))-del_price;
                           String updateOrderID = "UPDATE Orders SET total ="+(new_del_price)+" WHERE orderid = "+Integer.parseInt(userOrder.get(0).get(0));
                           esql.executeUpdate(updateOrderID);
                           for(int i=0; i< del_array.length;i++){
                              String findDelID = "DELETE FROM ItemStatus WHERE orderid ="+Integer.parseInt(userOrder.get(0).get(0))+"AND itemName = \'"+del_array[i]+"\' ;";
                              esql.executeUpdate(findDelID);
                              System.out.println();
                              System.out.print("This Item: "+del_array[i]+" has been deleted");
                           }
                           System.out.println();

                           String newitemStat = String.format("SELECT * FROM ItemStatus AS i WHERE i.orderid = "+Integer.parseInt(userOrder.get(0).get(0))+";");
                           esql.executeQueryAndPrintResult(newitemStat);
                           delItem = "";
                           del_price=0;

                           break;
                        case 3:
                           String delTrans = String.format("SELECT * FROM Orders As o WHERE o.login='%s' ORDER BY o.timeStampRecieved DESC LIMIT 5;",authorisedUser);
                           esql.executeQueryAndPrintResult(delTrans);
                           System.out.println("         Cancel Order     ");
                           System.out.println("------------------------------");
                           System.out.print("Enter the OrderID you wish to Cancel: ");
                           String cancelOrder = in.readLine();
                           String sqlCancelOrder = "DELETE FROM Orders WHERE orderid = "+Integer.parseInt(cancelOrder)+";";
                           String sqlCancelItem = "DELETE FROM ItemStatus WHERE orderid ="+Integer.parseInt(cancelOrder)+";";
                           esql.executeUpdate(sqlCancelItem);
                           esql.executeUpdate(sqlCancelOrder);
                           System.out.println("Sucess! OrderID:"+cancelOrder+" Has been Canceled!!");


                           break;
                        case 9:
                           inModifyMenu = false;
                           break;
                        default: System.out.println("Unrecognized choice!"); break;
                     }
                  }

                  break;
               case 3:
                  String nonPaid = "SELECT * FROM Orders AS o WHERE o.timeStampRecieved >=(NOW() - INTERVAL '24 HOURS') AND o.paid = 'f' ORDER BY o.timeStampRecieved DESC;";
                  esql.executeQueryAndPrintResult(nonPaid);
                  break;
               case 4:
                  System.out.println("----------------- Change an OrderID to Paid --------------");
                  System.out.print("Please enter the OrderID you wish to change to paid: ");
                  String idChange = in.readLine();
                  if(OrderIDisFalse(esql,Integer.parseInt(idChange))==false){
                     System.out.print("Either the OrderID you enter is already paid or there was an error in your OrderID");
                  }
                  else{
                     String updateID = String.format("UPDATE Orders SET paid = 't' WHERE orderid ="+Integer.parseInt(idChange)+";");

                     String successChange= String.format("Success! OrderID: %s changed from paid = 'f' ---> paid = 't'",idChange);
                     esql.executeUpdate(updateID);
                     System.out.println(successChange);
                   /*String updateStat = String.format("SELECT * FROM Orders AS o WHERE o.orderid = "+Integer.parseInt(idChange)+";");
                   esql.executeQueryAndPrintResult(updateStat);*/
                     System.out.println();
                  }
                  break;
               case 9:
                  menuContinue=false;
                  break;
               default: System.out.println("Unrecognized choice!"); break;
            }

         }

      }catch(Exception e){
         System.err.println (e.getMessage ());

      }
   }
}//end Cafe
