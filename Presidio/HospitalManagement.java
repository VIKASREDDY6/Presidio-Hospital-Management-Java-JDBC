import java.util.*;
import java.sql.*;

class HospitalManagement
{
    public static Connection conn;
    public static void main(String args[]) throws Exception
    {
        try{
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
            conn = DriverManager.getConnection("jdbc:derby:C:/Users/vikas/Desktop/Presidio/Hospital_DB","","");
        }
        catch(SQLException e){
            System.out.println("While Making Connection: "+e.toString());
        }
        Scanner s = new Scanner(System.in);
        int end = 1;
        Admin admin = new Admin(conn,s);
        Doctor doctor = new Doctor(conn,s);
        while(end == 1)
        {
            System.out.println("Enter login Role: 1.Admin 2.Doctor 3. End :");
            int role = s.nextInt();
            if(role == 1)
            admin.adminFunctionality();
            else if(role == 2)
            doctor.doctorFunctionality();
            else
            end = 0;
        }
    }
}


class Admin
{
    int pat_id,doc_id;
    PreparedStatement stmt;
    ResultSet rs;
    Connection conn;
    Scanner s;
    public Admin(Connection con,Scanner sc)
    {
        this.conn = con;
        this.s = sc;
    }
    public void addPatients() throws Exception
    {
        System.out.println("Enter Patient ID:");
        pat_id = s.nextInt();
        System.out.println("Enter Patient Name:");
        String pat_name = s.next();
        System.out.println("Enter Patient Age:");
        int pat_age = s.nextInt();
        System.out.println("Enter Patient Condition:");
        String pat_condition = s.next();
        System.out.println("Enter Patient Room Type:");
        String query = "INSERT INTO PATIENTS VALUES (?,?,?,?,?,?,?,?)";
        stmt = conn.prepareStatement(query);
        stmt.setInt(1,pat_id); stmt.setString(2,pat_name); stmt.setInt(3,pat_age); stmt.setString(4,pat_condition);
        stmt.setString(5,"NULL"); stmt.setInt(6,0); stmt.setInt(7,0); stmt.setInt(8,0);
        int i = stmt.executeUpdate();
        System.out.println(i+" rows inserted.");
    }
    public void assignRoomType() throws Exception
    {
        System.out.println("Enter Patient ID:");
        pat_id = s.nextInt();
        System.out.println("Enter Room Type for patient:");
        String room = s.next();
        stmt = conn.prepareStatement("UPDATE PATIENTS SET ROOMTYPE = ? WHERE PATIENT_ID = ?");
        stmt.setString(1,room); stmt.setInt(2,pat_id);
        stmt.executeUpdate();
        System.out.println("Room Type updated in Patients table.");
    }
    public void assignDoctor() throws Exception
    {
            System.out.println("Enter Patient ID:");
            pat_id = s.nextInt();
            stmt = conn.prepareStatement("SELECT DOCTOR_ID FROM DOCTORS WHERE NUMPATIENTS < 3");
            rs = stmt.executeQuery();
            if(!rs.next())
                System.out.println("All doctors are assigned.");
            else{
                int doc_id = rs.getInt(1);
                stmt = conn.prepareStatement("UPDATE PATIENTS SET DOCTOR_ID = ? WHERE PATIENT_ID = ?");
                stmt.setInt(1,doc_id); stmt.setInt(2,pat_id);
                stmt.executeUpdate();
                System.out.println("Doctor assigned to patient.");
                Statement stmt1 = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
                //for updating patients count in doctor table
                ResultSet rs = stmt1.executeQuery("SELECT * FROM DOCTORS");
                while(rs.next())
                {
                    if(rs.getInt(1)==doc_id)
                    {
                        rs.updateInt(3,rs.getInt(3)+1);
                        rs.updateRow();
                        System.out.println("Number of patients in doctor table updated.");
                        break;
                    }
                }
            }
    }
    public void updatePatientDetails() throws Exception
    {
            System.out.println("Enter Patient ID for Updation:");
            pat_id = s.nextInt();
            System.out.println("Enter new Patient ID:");
            int pat_id_new = s.nextInt();
            System.out.println("Enter patient Name:");
            String name = s.next();
            System.out.println("Enter Patient Age:");
            int age = s.nextInt();
            System.out.println("Enter Patient condition:");
            String cond = s.next();
            System.out.println("Enter Room Type:");
            String roomtype = s.next();
            System.out.println("Enter Num of Days:");
            int days = s.nextInt();
            System.out.println("Enter NUmber of Ward Boys:");
            int numwards = s.nextInt();
            stmt = conn.prepareStatement("UPDATE PATIENTS SET PATIENT_ID = ?,PATIENT_NAME = ?,PATIENT_AGE = ?,PATIENT_CONDITION = ?,ROOMTYPE = ?,NUMDAYS = ?,NUMWARDBOYS = ? WHERE PATIENT_ID = ?");
            stmt.setInt(1,pat_id_new); stmt.setString(2,name); stmt.setInt(3,age); stmt.setString(4,cond);
            stmt.setString(5,roomtype); stmt.setInt(6,days); stmt.setInt(7,numwards); stmt.setInt(8,pat_id);
            stmt.executeUpdate();
            System.out.println("Updated Patient details.");
    }
    public void checkOut() throws Exception
    {
            System.out.println("Enter Patient ID for Bill generation:");
            int pid = s.nextInt();
            int bill = 0;
            int numdays = 0;
            int numwardboys = 0;
            stmt = conn.prepareStatement("SELECT * FROM PATIENTS WHERE PATIENT_ID = ?");
            stmt.setInt(1,pid);
            rs = stmt.executeQuery();
            if(!rs.next())
                System.out.println("Patient not found");
            else{
                numdays = rs.getInt(6);
                numwardboys = rs.getInt(7);
                bill += numdays*1000 + numwardboys*100;
                System.out.println("At checkout Bill is: "+bill);
            }
    }
    public void adminFunctionality() throws Exception
    {
        int end = 1;
        while(end == 1)
        {
            System.out.println("Enter operation to be performed: 1. Add patient details 2. Assign Room Type 3. Assign Doctor  4. Update Patient details 5. Check Out and Generate Bill 6. End :");
            int operation = s.nextInt();
            try
            {
                switch(operation){

                    case 1:// adding new patient record
                    addPatients();
                    break;

                    case 2:// assigning room type to patient
                    assignRoomType();
                    break;

                    case 3:// assigning doctor to patient
                    assignDoctor();
                    break;

                    case 4:// for updating patient details
                    updatePatientDetails();
                    break;

                    case 5:
                    checkOut();
                    break;
                    case 6:
                    end = 0;
                    System.out.println("Ending Admin operations.");
                    break;
                }

            }
            catch(Exception e){
                    System.out.println("In Admin: "+e.toString());
            }
            
        }
    }
}


class Doctor
{
    PreparedStatement stmt;
    Connection conn;
    Scanner s;
    public Doctor(Connection con,Scanner sc)
    {
        this.conn = con;
        this.s = sc;
    }
    public void viewPatientDetails(int doc_id) throws Exception
    {
        stmt = conn.prepareStatement("SELECT * FROM PATIENTS WHERE DOCTOR_ID = ?");
        stmt.setInt(1,doc_id);
        ResultSet rs = stmt.executeQuery();
        int i = 1;
        while(rs.next())
        {
            System.out.println("Patient "+i+": ");
            System.out.println("Patient ID: "+rs.getInt(1));
            System.out.println("Patient Name: "+rs.getString(2));
            System.out.println("Patient Age: "+rs.getInt(3));
            System.out.println("Patient Condition: "+rs.getString(4));
            System.out.println("Patient Room Type: "+rs.getString(5));
            i++;
            System.out.println();
        }
    }
    public void updateNumdaysAndNumwardboys() throws Exception
    {
        System.out.println("Enter Patient ID:");
        int pid = s.nextInt();
        System.out.println("Enter Number of days:");
        int num = s.nextInt();
        System.out.println("Enter Number of Ward Boys:");
        int numwardboys = s.nextInt();
        stmt = conn.prepareStatement("UPDATE PATIENTS SET NUMDAYS = ?, NUMWARDBOYS = ? WHERE PATIENT_ID = ?");
        stmt.setInt(1,num); stmt.setInt(2,numwardboys); stmt.setInt(3,pid);
        stmt.executeUpdate();
        System.out.println("Patients Table Updated");
    }
    public void doctorFunctionality()
    {
        int end = 1;
        while(end == 1)
        {
            System.out.println("Enter Doctor ID:");
            int doc_id = s.nextInt();
            System.out.println("Enter operations 1. View Patients data 2. Assign Number of days and Assign Number of Ward Boys 3. End :");
            int operation = s.nextInt();
            try
            {
                switch(operation){

                    case 1:
                    viewPatientDetails(doc_id);
                    break;
                    case 2:
                    updateNumdaysAndNumwardboys();
                    break;
                    case 3:
                    end = 0;
                    System.out.println("Ending Doctors operations.");
                    break;

                }
            }
            catch(Exception e){
                System.out.println("In Doctor: "+e.toString());
            }
        }
    }
}