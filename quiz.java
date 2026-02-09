import java.sql.*;
import java.util.Scanner;

public class quiz {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        int marks = 0;

        try {
            // 1. Connection
            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/quiz",
                    "root",
                    "MOONLIGHT@8984"
            );

            // 2. Login
            System.out.print("Enter Roll No: ");
            int rollno = sc.nextInt();

            System.out.print("Enter Password: ");
            String password = sc.next();

            CallableStatement login = con.prepareCall(
                    "{CALL login_student(?, ?, ?)}"
            );
            login.setInt(1, rollno);
            login.setString(2, password);
            login.registerOutParameter(3, Types.INTEGER);

            login.execute();

            int status = login.getInt(3);

            if (status == 0) {
                System.out.println("❌ Invalid Login");
                return;
            }

            System.out.println("✅ Login Successful\n");

            // 3. Get Questions
            CallableStatement cs = con.prepareCall("{CALL get_questions()}");
            ResultSet rs = cs.executeQuery();

            while (rs.next()) {
                System.out.println(rs.getInt("qid") + ". " + rs.getString("question"));
                System.out.println("1. " + rs.getString("option1"));
                System.out.println("2. " + rs.getString("option2"));
                System.out.println("3. " + rs.getString("option3"));
                System.out.println("4. " + rs.getString("option4"));

                System.out.print("Enter Answer: ");
                int ans = sc.nextInt();

                if (ans == rs.getInt("correct_answer")) {
                    marks++;
                }
            }

            // 4. Save Result
            CallableStatement save = con.prepareCall(
                    "{CALL save_result(?, ?)}"
            );
            save.setInt(1, rollno);
            save.setInt(2, marks);
            save.execute();

            System.out.println("\n🎯 Your Marks: " + marks);
            System.out.println("✅ Result Saved Successfully");

            con.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
