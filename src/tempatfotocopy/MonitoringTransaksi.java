/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package tempatfotocopy;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.text.SimpleDateFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ListSelectionModel;
/**
 *
 * @author TEGUH_ADIGUNA
 */
public class MonitoringTransaksi extends javax.swing.JFrame {
        Connection con;
PreparedStatement pst;
ResultSet rs;
Timer refreshTimer;
DefaultTableModel model;
SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
NumberFormat rupiah = NumberFormat.getCurrencyInstance(new Locale("id","ID"));
    /**
     * Creates new form MonitoringTransaksi
     */
    public MonitoringTransaksi() {
        initComponents();
        setLocationRelativeTo(null);
        con = TempatFotocopy.getKoneksi();
    isiComboStatus();
    isiComboLayanan();
    isiComboTanggal(); 
    tampilData();
    tampilStatistik();
    warnaStatus();
    }
private void tampilData() {

    model = new DefaultTableModel();

    model.addColumn("No");
    model.addColumn("ID Transaksi");
    model.addColumn("Tanggal");
    model.addColumn("Pelanggan");
    model.addColumn("Layanan");
    model.addColumn("Jumlah");
    model.addColumn("Total");
    model.addColumn("Status");

    try {

       String sql =
                    "SELECT " +
                    "t.id_transaksi, " +
                    "t.tanggal, " +
                    "p.nama_pelanggan, " +
                    "GROUP_CONCAT(l.nama_layanan SEPARATOR ', ') AS layanan, " +
                    "SUM(d.jumlah) AS jumlah_item, " +
                    "t.total_harga, " +
                    "t.status " +
                    "FROM transaksi t " +
                    "JOIN pelanggan p ON t.id_pelanggan = p.id_pelanggan " +
                    "JOIN detail_transaksi d ON t.id_transaksi = d.id_transaksi " +
                    "JOIN layanan l ON d.id_layanan = l.id_layanan " +
                    "GROUP BY " +
                    "t.id_transaksi, " +
                    "t.tanggal, " +
                    "p.nama_pelanggan, " +
                    "t.total_harga, " +
                    "t.status " +
                    "ORDER BY t.tanggal DESC";

        Statement st = con.createStatement();

        ResultSet rs = st.executeQuery(sql);

        int no = 1;

        while(rs.next()){

                model.addRow(new Object[]{

                    no++,

                    rs.getString("id_transaksi"),

                    rs.getString("tanggal"),

                    rs.getString("nama_pelanggan"),

                    rs.getString("layanan"),

                    rs.getInt("jumlah_item"),

                    rupiah.format(rs.getDouble("total_harga")),

                    rs.getString("status")

                });

            }

        tblMonitoring.setModel(model);

    } catch (Exception e) {

        JOptionPane.showMessageDialog(null, e.getMessage());

    }

}

private void tampilStatistik() {

    try {

        Statement st = con.createStatement();

        // ==========================
        // TOTAL TRANSAKSI HARI INI
        // ==========================

        String sqlTotal =
        "SELECT COUNT(*) AS total " +
        "FROM transaksi " +
        "WHERE DATE(tanggal)=CURDATE()";

        ResultSet rsTotal = st.executeQuery(sqlTotal);

        if(rsTotal.next()){

            lblTotal.setText(rsTotal.getString("total"));

        }

        // ==========================
        // TOTAL DIPROSES
        // ==========================

        String sqlDiproses =
        "SELECT COUNT(*) AS diproses " +
        "FROM transaksi " +
        "WHERE status='Diproses'";

        ResultSet rsDiproses = st.executeQuery(sqlDiproses);

        if(rsDiproses.next()){

            lblDiproses.setText(rsDiproses.getString("diproses"));

        }

        // ==========================
        // TOTAL SELESAI
        // ==========================

        String sqlSelesai =
        "SELECT COUNT(*) AS selesai " +
        "FROM transaksi " +
        "WHERE status='Selesai'";

        ResultSet rsSelesai = st.executeQuery(sqlSelesai);

        if(rsSelesai.next()){

            lblSelesai.setText(rsSelesai.getString("selesai"));

        }

        // ==========================
        // PENDAPATAN HARI INI
        // ==========================

        String sqlPendapatan =
        "SELECT IFNULL(SUM(total_harga),0) AS pendapatan " +
        "FROM transaksi " +
        "WHERE DATE(tanggal)=CURDATE()";

        ResultSet rsPendapatan = st.executeQuery(sqlPendapatan);

        if(rsPendapatan.next()){

            lblPendapatan.setText(
                    rupiah.format(
                            rsPendapatan.getDouble("pendapatan")
                    )
            );

        }

    } catch (Exception e) {

        JOptionPane.showMessageDialog(null, e.getMessage());

    }

}
private void filterData() {

    model = new DefaultTableModel();

    model.addColumn("No");
    model.addColumn("ID Transaksi");
    model.addColumn("Tanggal");
    model.addColumn("Pelanggan");
    model.addColumn("Layanan");
    model.addColumn("Jumlah");
    model.addColumn("Total");
    model.addColumn("Status");
    try{
    String sql =
            "SELECT " +
            "t.id_transaksi, " +
            "t.tanggal, " +
            "p.nama_pelanggan, " +
            "GROUP_CONCAT(l.nama_layanan SEPARATOR ', ') AS layanan, " +
            "SUM(d.jumlah) AS jumlah_item, " +
            "t.total_harga, " +
            "t.status " +
            "FROM transaksi t " +
            "JOIN pelanggan p ON t.id_pelanggan = p.id_pelanggan " +
            "JOIN detail_transaksi d ON t.id_transaksi = d.id_transaksi " +
            "JOIN layanan l ON d.id_layanan = l.id_layanan " +
            "WHERE DATE(t.tanggal) BETWEEN ? AND ?";

        if(!cmbStatus.getSelectedItem().toString().equals("Semua")){

            sql += " AND t.status=?";

        }

        if(!cmbLayanan.getSelectedItem().toString().equals("Semua")){

            sql += " AND l.nama_layanan=?";

        }
        
        sql +=
                " GROUP BY " +
                "t.id_transaksi, " +
                "t.tanggal, " +
                "p.nama_pelanggan, " +
                "t.total_harga, " +
                "t.status " +
                "ORDER BY t.tanggal DESC";

        PreparedStatement ps = con.prepareStatement(sql);

        int index = 1;

        ps.setString(index++, cmbDari.getSelectedItem().toString());
        ps.setString(index++, cmbSampai.getSelectedItem().toString());

        if(!cmbStatus.getSelectedItem().toString().equals("Semua")){

            ps.setString(index++, cmbStatus.getSelectedItem().toString());

        }

        if(!cmbLayanan.getSelectedItem().toString().equals("Semua")){

            ps.setString(index++, cmbLayanan.getSelectedItem().toString());

        }

        ResultSet rs = ps.executeQuery();

        int no = 1;

        while(rs.next()){

           model.addRow(new Object[]{

                    no++,

                    rs.getString("id_transaksi"),

                    rs.getString("tanggal"),

                    rs.getString("nama_pelanggan"),

                    rs.getString("layanan"),

                    rs.getInt("jumlah_item"),

                    rupiah.format(rs.getDouble("total_harga")),

                    rs.getString("status")

                });

        }

        tblMonitoring.setModel(model);
        tblMonitoring.getTableHeader().setReorderingAllowed(false);

        tblMonitoring.setRowHeight(28);

        tblMonitoring.setSelectionMode(
        ListSelectionModel.SINGLE_SELECTION);

    } catch(Exception e){

        JOptionPane.showMessageDialog(null,e.getMessage());

    }

}
private void filterStatistik() {

    try {

        String sql =
        "SELECT " +
        "COUNT(*) AS total, " +
        "SUM(CASE WHEN status='Diproses' THEN 1 ELSE 0 END) AS diproses, " +
        "SUM(CASE WHEN status='Selesai' THEN 1 ELSE 0 END) AS selesai, " +
        "IFNULL(SUM(total_harga),0) AS pendapatan " +
        "FROM transaksi " +
        "WHERE DATE(tanggal) BETWEEN ? AND ?";

        if(!cmbStatus.getSelectedItem().toString().equals("Semua")){
            sql += " AND status=?";
        }

        PreparedStatement ps = con.prepareStatement(sql);

        int i = 1;

        ps.setString(i++, cmbDari.getSelectedItem().toString());
        ps.setString(i++, cmbSampai.getSelectedItem().toString());

        if(!cmbStatus.getSelectedItem().toString().equals("Semua")){
            ps.setString(i++, cmbStatus.getSelectedItem().toString());
        }

        ResultSet rs = ps.executeQuery();

        if(rs.next()){

            lblTotal.setText(rs.getString("total"));
            lblDiproses.setText(rs.getString("diproses"));
            lblSelesai.setText(rs.getString("selesai"));

            lblPendapatan.setText(
                rupiah.format(rs.getDouble("pendapatan"))
            );

        }

    } catch (Exception e) {

        JOptionPane.showMessageDialog(this, e.getMessage());

    }

}
private void isiComboStatus() {

    cmbStatus.removeAllItems();

    cmbStatus.addItem("Semua");
    cmbStatus.addItem("Diproses");
    cmbStatus.addItem("Selesai");
    cmbStatus.addItem("Batal");

    cmbStatus.setSelectedIndex(0);

}

private void isiComboLayanan() {

    try {

        cmbLayanan.removeAllItems();

        cmbLayanan.addItem("Semua");

        String sql = "SELECT nama_layanan FROM layanan ORDER BY nama_layanan ASC";

        Statement st = con.createStatement();

        ResultSet rs = st.executeQuery(sql);

        while (rs.next()) {

            cmbLayanan.addItem(rs.getString("nama_layanan"));

        }

    } catch (Exception e) {

        JOptionPane.showMessageDialog(null, e.getMessage());

    }

}
private void isiComboTanggal() {

    try {

        cmbDari.removeAllItems();
        cmbSampai.removeAllItems();

        String sql =
        "SELECT DISTINCT DATE(tanggal) AS tanggal " +
        "FROM transaksi " +
        "ORDER BY tanggal ASC";

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(sql);

        while (rs.next()) {

            String tanggal = rs.getString("tanggal");

            cmbDari.addItem(tanggal);
            cmbSampai.addItem(tanggal);

        }

    } catch (Exception e) {

        JOptionPane.showMessageDialog(this, e.getMessage());

    }

}
private void warnaStatus(){

    tblMonitoring.getColumnModel().getColumn(7)
            .setCellRenderer(new DefaultTableCellRenderer(){

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column){

            Component c=
                    super.getTableCellRendererComponent(
                            table,value,isSelected,
                            hasFocus,row,column);

            String status=value.toString();

            if(status.equalsIgnoreCase("Selesai")){

                c.setForeground(new Color(0,153,0));

            }

            else if(status.equalsIgnoreCase("Diproses")){

                c.setForeground(new Color(255,140,0));

            }

            else{

                c.setForeground(Color.RED);

            }

            return c;

        }

    });

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
        btnDash = new javax.swing.JButton();
        btnLaporanPendapatan = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        btnLogout = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        spMonitoring = new javax.swing.JScrollPane();
        tblMonitoring = new javax.swing.JTable();
        cmbDari = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        cmbSampai = new javax.swing.JComboBox<>();
        lblTotal = new javax.swing.JLabel();
        lblDiproses = new javax.swing.JLabel();
        lblSelesai = new javax.swing.JLabel();
        lblPendapatan = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        btnCetak = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        cmbStatus = new javax.swing.JComboBox<>();
        cmbLayanan = new javax.swing.JComboBox<>();
        btnTampilkan = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel2.setBackground(new java.awt.Color(102, 102, 102));

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Gambar/logo admin (3).png"))); // NOI18N

        btnDash.setBackground(new java.awt.Color(0, 153, 255));
        btnDash.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnDash.setText("DASHBOARD");
        btnDash.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDashActionPerformed(evt);
            }
        });

        btnLaporanPendapatan.setBackground(new java.awt.Color(0, 153, 255));
        btnLaporanPendapatan.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnLaporanPendapatan.setText("LAPORAN PENDAPATAN");
        btnLaporanPendapatan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLaporanPendapatanActionPerformed(evt);
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
                    .addComponent(btnLaporanPendapatan, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnDash, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                .addComponent(btnDash, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnLaporanPendapatan, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnLogout, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14))
        );

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("MONITORING TRANSAKSI");

        tblMonitoring.setModel(new javax.swing.table.DefaultTableModel(
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
        spMonitoring.setViewportView(tblMonitoring);

        cmbDari.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Dari Tanggal :");

        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Sampai Tanggal : ");

        cmbSampai.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbSampai.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbSampaiActionPerformed(evt);
            }
        });

        lblTotal.setBackground(new java.awt.Color(204, 204, 204));
        lblTotal.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblTotal.setForeground(new java.awt.Color(255, 255, 255));
        lblTotal.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTotal.setText("0");
        lblTotal.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        lblDiproses.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblDiproses.setForeground(new java.awt.Color(255, 255, 255));
        lblDiproses.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblDiproses.setText("0");
        lblDiproses.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        lblSelesai.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblSelesai.setForeground(new java.awt.Color(255, 255, 255));
        lblSelesai.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblSelesai.setText("0");
        lblSelesai.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        lblPendapatan.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblPendapatan.setForeground(new java.awt.Color(255, 255, 255));
        lblPendapatan.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblPendapatan.setText("0");
        lblPendapatan.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setText("Total Hari Ini");

        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setText("Diproses");

        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setText("Selesai");

        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jLabel13.setText("Pendapatan Hari ini");

        btnCetak.setBackground(new java.awt.Color(0, 153, 255));
        btnCetak.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnCetak.setText("CETAK LAPORAN");
        btnCetak.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCetakActionPerformed(evt);
            }
        });

        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Status :");

        jLabel14.setForeground(new java.awt.Color(255, 255, 255));
        jLabel14.setText("Layanan :");

        cmbStatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        cmbLayanan.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        btnTampilkan.setBackground(new java.awt.Color(0, 153, 255));
        btnTampilkan.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnTampilkan.setText("Tampilkan");
        btnTampilkan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTampilkanActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel1)
                        .addGap(187, 187, 187))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(24, 24, 24)
                                .addComponent(jLabel10)))
                        .addGap(12, 12, 12)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblDiproses, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel11)
                                .addGap(30, 30, 30)))
                        .addGap(12, 12, 12)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblSelesai, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel12)
                                .addGap(34, 34, 34)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblPendapatan, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel13)
                                .addGap(32, 32, 32)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel5)
                                    .addComponent(cmbDari, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGap(41, 41, 41)
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(cmbSampai, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel6))
                                        .addGap(28, 28, 28)
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel3)
                                            .addComponent(cmbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(18, 18, 18)
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel14)
                                            .addComponent(cmbLayanan, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGap(137, 137, 137)
                                        .addComponent(btnTampilkan, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(26, 26, 26)
                                        .addComponent(btnCetak))))
                            .addComponent(spMonitoring, javax.swing.GroupLayout.PREFERRED_SIZE, 607, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(26, 26, 26)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6)
                    .addComponent(jLabel3)
                    .addComponent(jLabel14))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmbDari, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbSampai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbLayanan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(44, 44, 44)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addComponent(lblTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(13, 13, 13)
                            .addComponent(jLabel10))
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addComponent(lblPendapatan, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(28, 28, 28)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(lblDiproses, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel11))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(lblSelesai, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel12)
                                    .addComponent(jLabel13))))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnTampilkan, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCetak, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(spMonitoring, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
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

    private void btnCetakActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCetakActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnCetakActionPerformed

    private void btnTampilkanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTampilkanActionPerformed
        // TODO add your handling code here:
         if(cmbDari.getSelectedIndex()==-1){
            JOptionPane.showMessageDialog(this,
                "Silakan pilih tanggal awal.");
            return;
        }

       

    if(cmbSampai.getSelectedIndex()==-1){
    JOptionPane.showMessageDialog(this,
            "Silakan pilih tanggal akhir.");
    return;
}

    filterData();

    filterStatistik();
    }//GEN-LAST:event_btnTampilkanActionPerformed

    private void btnLaporanPendapatanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLaporanPendapatanActionPerformed
        // TODO add your handling code here:
              MenuLaporanPendapatan Laporan = new MenuLaporanPendapatan();
    Laporan.setVisible(true);

    this.dispose();
    }//GEN-LAST:event_btnLaporanPendapatanActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        
    }//GEN-LAST:event_jButton3ActionPerformed

    private void btnDashActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDashActionPerformed
        // TODO add your handling code here:
              DashboardPemilik Pemilik = new DashboardPemilik();
    Pemilik.setVisible(true);

    this.dispose();
    }//GEN-LAST:event_btnDashActionPerformed

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
            java.util.logging.Logger.getLogger(MonitoringTransaksi.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MonitoringTransaksi.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MonitoringTransaksi.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MonitoringTransaksi.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MonitoringTransaksi().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCetak;
    private javax.swing.JButton btnDash;
    private javax.swing.JButton btnLaporanPendapatan;
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton btnTampilkan;
    private javax.swing.JComboBox<String> cmbDari;
    private javax.swing.JComboBox<String> cmbLayanan;
    private javax.swing.JComboBox<String> cmbSampai;
    private javax.swing.JComboBox<String> cmbStatus;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JLabel lblDiproses;
    private javax.swing.JLabel lblPendapatan;
    private javax.swing.JLabel lblSelesai;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JScrollPane spMonitoring;
    private javax.swing.JTable tblMonitoring;
    // End of variables declaration//GEN-END:variables
}
