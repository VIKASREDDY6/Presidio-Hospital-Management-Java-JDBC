import java.sql.*;


class CreateDB
{
    public static void main(String args[])
    {
        try{
        Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
        Connection con = DriverManager.getConnection("jdbc:derby:C:/Users/vikas/Desktop/Presidio/Hospital_DB;create=true","","");  
        Statement stmt = con.createStatement();
        stmt.executeUpdate("CREATE TABLE DOCTORS (DOCTOR_ID INT PRIMARY KEY, DOCTOR_NAME VARCHAR(15), NUMPATIENTS INT)");
        System.out.println("Doctors Table Created");
        stmt.executeUpdate("INSERT INTO DOCTORS VALUES (1,'John',0),(2,'BILL',0),(3,'Jack',0),(4,'Harry',0),(5,'Ron',0)");
        System.out.println("Populated Doctor Table");
        stmt.executeUpdate("CREATE TABLE PATIENTS (PATIENT_ID INT PRIMARY KEY, PATIENT_NAME VARCHAR(15), PATIENT_AGE INT, PATIENT_CONDITION VARCHAR(10), ROOMTYPE VARCHAR(10), NUMDAYS INT, NUMWARDBOYS INT,DOCTOR_ID INT)");
        System.out.println("Patients Table Created");
        stmt.executeUpdate("CREATE TABLE DOCTOR_ASSIGNED_TO_PATIENT (DOCTOR_ID INT, PATIENT_ID INT PRIMARY KEY)");
        System.out.println("Third table created");


        }
        catch(Exception e){
            System.out.println("Exception raised: "+e.toString());
        }
    }
}