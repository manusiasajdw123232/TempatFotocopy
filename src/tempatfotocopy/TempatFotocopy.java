/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package tempatfotocopy;
import java.sql.Connection;
import java.sql.DriverManager;
import javax.swing.JOptionPane;
/**
 *
 * @author TEGUH_ADIGUNA
 */
public class TempatFotocopy {
    private static Connection koneksi;

    public static Connection getKoneksi() {

        try {

            String url = "jdbc:mysql://localhost:3306/fotocopy";
            String user = "root";
            String pass = "";

            DriverManager.registerDriver(
                new com.mysql.cj.jdbc.Driver());

            koneksi = DriverManager.getConnection(
                url, user, pass);

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                null,
                "Koneksi Gagal : " + e.getMessage()
            );
        }

        return koneksi;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
    }
    
}
