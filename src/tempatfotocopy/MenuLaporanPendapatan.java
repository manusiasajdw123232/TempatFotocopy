/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package tempatfotocopy;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.table.DefaultTableModel;
import javax.swing.JOptionPane;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.view.JasperViewer;
import java.util.HashMap;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
/**
 *
 * @author TEGUH_ADIGUNA
 */
public class MenuLaporanPendapatan extends javax.swing.JFrame {

    /**
     * Creates new form MenuLaporanPendapatan
     */
    public MenuLaporanPendapatan() {
        initComponents();
        setLocationRelativeTo(null);
        isiPeriode();
        
    lblPendapatan.setText("Rp 0");
    lblTransaksi.setText("0");
    lblPelanggan.setText("0");
    lblRata.setText("Rp 0");


    }
    
private void isiPeriode() {
    cmbPeriode.removeAllItems();

    cmbPeriode.addItem("Pilih Periode");
    cmbPeriode.addItem("Harian");
    cmbPeriode.addItem("Bulanan");
    cmbPeriode.addItem("Tahunan");

    cmbPeriode.setSelectedIndex(0);
}
private void tampilData() {

    if (cmbPeriode.getSelectedItem() == null ||
        cmbDari.getSelectedItem() == null ||
        cmbSampai.getSelectedItem() == null ||
        cmbPeriode.getSelectedItem().toString().equals("Pilih Periode")) {

        JOptionPane.showMessageDialog(this, "Silakan pilih periode terlebih dahulu.");
        return;
    }

    try {

        Connection con = TempatFotocopy.getKoneksi();

        String periode = cmbPeriode.getSelectedItem().toString();

        String sql = "";

        PreparedStatement ps;

        if (periode.equals("Harian")) {

            sql = "SELECT " +
                  "IFNULL(SUM(total_harga),0) AS pendapatan," +
                  "COUNT(id_transaksi) AS transaksi," +
                  "COUNT(DISTINCT id_pelanggan) AS pelanggan," +
                  "IFNULL(AVG(total_harga),0) AS rata " +
                  "FROM transaksi " +
                  "WHERE DAY(tanggal) BETWEEN ? AND ?";

            ps = con.prepareStatement(sql);

            ps.setInt(1, Integer.parseInt(cmbDari.getSelectedItem().toString()));
            ps.setInt(2, Integer.parseInt(cmbSampai.getSelectedItem().toString()));

        } else if (periode.equals("Bulanan")) {

            int bulanDari = cmbDari.getSelectedIndex() + 1;
            int bulanSampai = cmbSampai.getSelectedIndex() + 1;

            sql = "SELECT " +
                  "IFNULL(SUM(total_harga),0) AS pendapatan," +
                  "COUNT(id_transaksi) AS transaksi," +
                  "COUNT(DISTINCT id_pelanggan) AS pelanggan," +
                  "IFNULL(AVG(total_harga),0) AS rata " +
                  "FROM transaksi " +
                  "WHERE MONTH(tanggal) BETWEEN ? AND ?";

            ps = con.prepareStatement(sql);

            ps.setInt(1, bulanDari);
            ps.setInt(2, bulanSampai);

        } else {

            sql = "SELECT " +
                  "IFNULL(SUM(total_harga),0) AS pendapatan," +
                  "COUNT(id_transaksi) AS transaksi," +
                  "COUNT(DISTINCT id_pelanggan) AS pelanggan," +
                  "IFNULL(AVG(total_harga),0) AS rata " +
                  "FROM transaksi " +
                  "WHERE YEAR(tanggal) BETWEEN ? AND ?";

            ps = con.prepareStatement(sql);

            ps.setInt(1, Integer.parseInt(cmbDari.getSelectedItem().toString()));
            ps.setInt(2, Integer.parseInt(cmbSampai.getSelectedItem().toString()));

        }

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {

            lblPendapatan.setText("Rp " + rs.getInt("pendapatan"));
            lblTransaksi.setText(rs.getString("transaksi"));
            lblPelanggan.setText(rs.getString("pelanggan"));
            lblRata.setText("Rp " + rs.getInt("rata"));

        }

    } catch (Exception e) {

        JOptionPane.showMessageDialog(this, e);

    }

}
private void tampilTabel() {

    DefaultTableModel model = new DefaultTableModel();

    model.addColumn("ID");
    model.addColumn("Tanggal");
    model.addColumn("Pelanggan");
    model.addColumn("Total");

    if (cmbPeriode.getSelectedItem() == null ||
        cmbDari.getSelectedItem() == null ||
        cmbSampai.getSelectedItem() == null ||
        cmbPeriode.getSelectedItem().toString().equals("Pilih Periode")) {

        tblLaporan.setModel(model);
        return;
    }

    try {

        Connection con = TempatFotocopy.getKoneksi();

        String periode = cmbPeriode.getSelectedItem().toString();

        String sql = "";

        PreparedStatement ps;

        if (periode.equals("Harian")) {

            sql =
            "SELECT transaksi.id_transaksi," +
            "transaksi.tanggal," +
            "pelanggan.nama_pelanggan," +
            "transaksi.total_harga " +
            "FROM transaksi " +
            "INNER JOIN pelanggan ON transaksi.id_pelanggan = pelanggan.id_pelanggan " +
            "WHERE DAY(transaksi.tanggal) BETWEEN ? AND ?";

            ps = con.prepareStatement(sql);

            ps.setInt(1, Integer.parseInt(cmbDari.getSelectedItem().toString()));
            ps.setInt(2, Integer.parseInt(cmbSampai.getSelectedItem().toString()));

        } else if (periode.equals("Bulanan")) {

            int bulanDari = cmbDari.getSelectedIndex() + 1;
            int bulanSampai = cmbSampai.getSelectedIndex() + 1;

            sql =
            "SELECT transaksi.id_transaksi," +
            "transaksi.tanggal," +
            "pelanggan.nama_pelanggan," +
            "transaksi.total_harga " +
            "FROM transaksi " +
            "INNER JOIN pelanggan ON transaksi.id_pelanggan = pelanggan.id_pelanggan " +
            "WHERE MONTH(transaksi.tanggal) BETWEEN ? AND ?";

            ps = con.prepareStatement(sql);

            ps.setInt(1, bulanDari);
            ps.setInt(2, bulanSampai);

        } else {

            sql =
            "SELECT transaksi.id_transaksi," +
            "transaksi.tanggal," +
            "pelanggan.nama_pelanggan," +
            "transaksi.total_harga " +
            "FROM transaksi " +
            "INNER JOIN pelanggan ON transaksi.id_pelanggan = pelanggan.id_pelanggan " +
            "WHERE YEAR(transaksi.tanggal) BETWEEN ? AND ?";

            ps = con.prepareStatement(sql);

            ps.setInt(1, Integer.parseInt(cmbDari.getSelectedItem().toString()));
            ps.setInt(2, Integer.parseInt(cmbSampai.getSelectedItem().toString()));

        }

        ResultSet rs = ps.executeQuery();

        while (rs.next()) {

            model.addRow(new Object[]{

                rs.getString("id_transaksi"),
                rs.getString("tanggal"),
                rs.getString("nama_pelanggan"),
                rs.getDouble("total_harga")

            });

        }

        tblLaporan.setModel(model);

    } catch (Exception e) {

        JOptionPane.showMessageDialog(this, e);

    }

}

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        btnLogout = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        spLaporan = new javax.swing.JScrollPane();
        tblLaporan = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();
        cmbDari = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        cmbSampai = new javax.swing.JComboBox<>();
        lblPendapatan = new javax.swing.JLabel();
        lblTransaksi = new javax.swing.JLabel();
        lblPelanggan = new javax.swing.JLabel();
        lblRata = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        btnCetak = new javax.swing.JButton();
        btnTampilkan = new javax.swing.JButton();
        cmbPeriode = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel2.setBackground(new java.awt.Color(102, 102, 102));

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Gambar/logo admin (3).png"))); // NOI18N

        jButton1.setBackground(new java.awt.Color(0, 153, 255));
        jButton1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton1.setText("DASHBOARD");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setBackground(new java.awt.Color(0, 153, 255));
        jButton2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton2.setText("LAPORAN PENDAPATAN");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setBackground(new java.awt.Color(0, 153, 255));
        jButton3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton3.setText("MONITORING TRANSAKSI");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        btnLogout.setBackground(new java.awt.Color(255, 51, 51));
        btnLogout.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnLogout.setText("LOGOUT");
        btnLogout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLogoutActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnLogout, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel2)
                .addGap(33, 33, 33))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 204, Short.MAX_VALUE)
                .addComponent(btnLogout, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14))
        );

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("LAPORAN PENDAPATAN");

        tblLaporan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        spLaporan.setViewportView(tblLaporan);

        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Periode :");

        cmbDari.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Dari :");

        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Sampai : ");

        cmbSampai.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbSampai.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbSampaiActionPerformed(evt);
            }
        });

        lblPendapatan.setBackground(new java.awt.Color(204, 204, 204));
        lblPendapatan.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblPendapatan.setForeground(new java.awt.Color(255, 255, 255));
        lblPendapatan.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblPendapatan.setText("0");
        lblPendapatan.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        lblTransaksi.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblTransaksi.setForeground(new java.awt.Color(255, 255, 255));
        lblTransaksi.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTransaksi.setText("0");
        lblTransaksi.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        lblPelanggan.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblPelanggan.setForeground(new java.awt.Color(255, 255, 255));
        lblPelanggan.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblPelanggan.setText("0");
        lblPelanggan.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        lblRata.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblRata.setForeground(new java.awt.Color(255, 255, 255));
        lblRata.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblRata.setText("0");
        lblRata.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setText("Total Pendapatan");

        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setText("Total Transaksi");

        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setText("Total Pelanggan");

        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jLabel13.setText("Rata-Rata Pendapatan");

        btnCetak.setBackground(new java.awt.Color(0, 153, 255));
        btnCetak.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnCetak.setText("CETAK LAPORAN");
        btnCetak.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCetakActionPerformed(evt);
            }
        });

        btnTampilkan.setBackground(new java.awt.Color(0, 153, 255));
        btnTampilkan.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnTampilkan.setText("TAMPILKAN");
        btnTampilkan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTampilkanActionPerformed(evt);
            }
        });

        cmbPeriode.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbPeriode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbPeriodeActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(spLaporan)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel1)
                        .addGap(179, 179, 179))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jLabel5)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(cmbDari, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGap(6, 6, 6)
                                        .addComponent(jLabel10))
                                    .addComponent(lblPendapatan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGap(18, 18, 18)
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addComponent(jLabel6)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(cmbSampai, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(50, 50, 50)
                                                .addComponent(btnTampilkan))
                                            .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(lblTransaksi, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                                        .addGap(17, 17, 17)
                                                        .addComponent(jLabel11)))
                                                .addGap(36, 36, 36)
                                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                                        .addGap(6, 6, 6)
                                                        .addComponent(jLabel12))
                                                    .addComponent(lblPelanggan, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGap(49, 49, 49)
                                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(lblRata, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(jLabel13)))))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGap(155, 155, 155)
                                        .addComponent(btnCetak))))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cmbPeriode, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(17, 17, 17)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(cmbPeriode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(cmbDari, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(cmbSampai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnTampilkan))
                .addGap(28, 28, 28)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblPelanggan, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblPendapatan, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblTransaksi, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblRata, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(jLabel11)
                    .addComponent(jLabel12)
                    .addComponent(jLabel13))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnCetak, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(spLaporan, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogoutActionPerformed
        // TODO add your handling code here:
        int jawab = JOptionPane.showConfirmDialog(
            null,
            "Yakin ingin logout?",
            "Konfirmasi",
            JOptionPane.YES_NO_OPTION);

        if(jawab == JOptionPane.YES_OPTION){

            new login().setVisible(true);

            this.dispose();
    }//GEN-LAST:event_btnLogoutActionPerformed
    }
    private void cmbSampaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbSampaiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbSampaiActionPerformed

    private void btnTampilkanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTampilkanActionPerformed
        // TODO add your handling code here:
if (cmbPeriode.getSelectedItem() == null ||
        cmbPeriode.getSelectedItem().toString().equals("Pilih Periode") ||
        cmbDari.getSelectedItem() == null ||
        cmbSampai.getSelectedItem() == null) {

        JOptionPane.showMessageDialog(this,
                "Silakan pilih periode terlebih dahulu.");
        return;
    }

    tampilData();
    tampilTabel();
    }//GEN-LAST:event_btnTampilkanActionPerformed

    private void cmbPeriodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbPeriodeActionPerformed
        // TODO add your handling code here:
  if (cmbPeriode.getSelectedItem() == null) {
        return;
    }

    cmbDari.removeAllItems();
    cmbSampai.removeAllItems();

    String periode = cmbPeriode.getSelectedItem().toString();

    if (periode.equals("Pilih Periode")) {
        return;
    }

    if (periode.equals("Harian")) {

        for (int i = 1; i <= 31; i++) {
            cmbDari.addItem(String.valueOf(i));
            cmbSampai.addItem(String.valueOf(i));
        }

    } else if (periode.equals("Bulanan")) {

        String[] bulan = {
            "Januari","Februari","Maret","April","Mei","Juni",
            "Juli","Agustus","September","Oktober","November","Desember"
        };

        for (String b : bulan) {
            cmbDari.addItem(b);
            cmbSampai.addItem(b);
        }

    } else if (periode.equals("Tahunan")) {

        for (int i = 2024; i <= 2035; i++) {
            cmbDari.addItem(String.valueOf(i));
            cmbSampai.addItem(String.valueOf(i));
        }
    }
    }//GEN-LAST:event_cmbPeriodeActionPerformed

    private void btnCetakActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCetakActionPerformed
        // TODO add your handling code here:
         try {
        boolean selesai = tblLaporan.print();

        if (selesai) {
            JOptionPane.showMessageDialog(this, "Laporan berhasil dicetak.");
        } else {
            JOptionPane.showMessageDialog(this, "Pencetakan dibatalkan.");
        }

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, e.getMessage());
    }
    }//GEN-LAST:event_btnCetakActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
            DashboardPemilik Pemilik = new DashboardPemilik();
    Pemilik.setVisible(true);

    this.dispose();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
              MonitoringTransaksi Monitoring = new MonitoringTransaksi();
    Monitoring.setVisible(true);

    this.dispose();
    }//GEN-LAST:event_jButton3ActionPerformed
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MenuLaporanPendapatan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MenuLaporanPendapatan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MenuLaporanPendapatan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MenuLaporanPendapatan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MenuLaporanPendapatan().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCetak;
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton btnTampilkan;
    private javax.swing.JComboBox<String> cmbDari;
    private javax.swing.JComboBox<String> cmbPeriode;
    private javax.swing.JComboBox<String> cmbSampai;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JLabel lblPelanggan;
    private javax.swing.JLabel lblPendapatan;
    private javax.swing.JLabel lblRata;
    private javax.swing.JLabel lblTransaksi;
    private javax.swing.JScrollPane spLaporan;
    private javax.swing.JTable tblLaporan;
    // End of variables declaration//GEN-END:variables
}
