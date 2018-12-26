package com.apps.riswanda.icalorie;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {
    // ExpandableRelativeLayout tampilSarapan, tampilMakanSiang, tampilMakanMalam;

    Double  bbideal = 0.0;
    Double totalSangatTinggi = 0.0, totalTinggi = 0.0, totalNormal = 0.0, totalRendah = 0.0;
    Double sumTBLST = 0.0, sumTBT = 0.0, sumTBS = 0.0, sumTBR = 0.0;
    Double sumBBLST = 0.0, sumBBLT = 0.0, sumBBLN = 0.0, sumBBLR = 0.0;
    Double sumLPLST = 0.0, sumLPLT = 0.0, sumLPLN = 0.0, sumLPLR = 0.0;
    Double sumLKLST = 0.0, sumLKLT = 0.0, sumLKLN = 0.0, sumLKLR = 0.0;
    Double sumPRLST = 0.0, sumPRLT = 0.0, sumPRLN = 0.0, sumPRLR = 0.0;
    Double meanTBST = 0.0, meanTBT = 0.0, meanTBN = 0.0, meanTBR = 0.0;
    Double meanBBST = 0.0, meanBBT = 0.0, meanBBN = 0.0, meanBBR = 0.0;
    Double meanLPST = 0.0, meanLPT = 0.0, meanLPN = 0.0, meanLPR = 0.0;


    Double userTinggi, userBerat, userPerut;
    Double ngTinggiST, ngTinggiT, ngTinggiN, ngTinggiR;
    Double ngBeratST, ngBeratT, ngBeratN, ngBeratR;
    Double ngPerutST, ngPerutT, ngPerutN, ngPerutR;
    Double probLakiST, probLakiT, probLakiN, probLakiR;
    Double probPerST, probPerT, probPerN, probPerR;
    String userKelamin;

    Double sdTinggiSangatTinggi = 0.0, sdTinggiTinggi = 0.0, sdTinggiNormal = 0.0, sdTinggiRendah = 0.0;
    Double sdBeratSangatTinggi = 0.0, sdBeratTinggi = 0.0, sdBeratNormal = 0.0, sdBeratRendah = 0.0;
    Double sdPerutSangatTinggi = 0.0, sdPerutTinggi = 0.0, sdPerutNormal = 0.0, sdPerutRendah = 0.0;

//    Double likelihoodSanggatTinggi, likelihoodTinggi, likelihoodNormal, likelihoodRendah;


    ArrayList<String> tinggiBadanSangatTingi, tinggiBadanTinggi, tinggiBadanNormal, tinggiBadanRendah;
    ArrayList<String> beratBadanSangatTinggi, beratBadanTinggi, beratBadanNormal, beratBadanRendah;
    ArrayList<String> lingkarPinggangSangatTinggi, lingkarPinggangTinggi, lingkarPinggangNormal, lingkarPinggangRendah;
    ArrayList<String> jenisKelaminAL;

    private static final String TAG = "HomeActivity/";
    FirebaseAuth mAuth;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference();

    private TextView nama, umur, tinggi, berat, kelamin, pinggang, aktivitas, beratIdeal, kadarLemakUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();

        nama = (TextView) findViewById(R.id.textNamaUser);
        umur = (TextView) findViewById(R.id.textUmurUser);
        tinggi = (TextView) findViewById(R.id.textTinggiUser);
        berat = (TextView) findViewById(R.id.textBeratUser);
        kelamin = (TextView) findViewById(R.id.textKelaminUser);
        pinggang = (TextView) findViewById(R.id.textLpUser);
        aktivitas = (TextView) findViewById(R.id.textAktivUser);

        beratIdeal = (TextView) findViewById(R.id.textBbIdeal);


        userRetrieve();



    }

    private void userRetrieve() {
        String uID = mAuth.getCurrentUser().getUid();
        Log.d(TAG, "userRetrieve: " + uID);
        DatabaseReference refUser = ref.child("Users/" + uID);

        refUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Users user = dataSnapshot.getValue(Users.class);
                Log.d(TAG, "onDataChange: " + user.nama);

                nama.setText(user.nama);
                umur.setText(user.usia);
                tinggi.setText(user.tinggi);
                berat.setText(user.berat);
                kelamin.setText(user.jeniskelamin);
                pinggang.setText(user.lPerut);
//                aktivitas.setText(user.aktiv);

                beratIdeal();
                lemakNBC();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    //BeratBadan IDEAL

    private void beratIdeal() {

        Double tb = Double.parseDouble(tinggi.getText().toString());

        if (kelamin.getText().toString().equals("Laki-laki")) {
            if (tb >= 160)
                bbideal = 90 * (tb - 100) / 100;
            else bbideal = tb - 100;

        } else if (kelamin.getText().toString().equals("Perempuan")) {
            if (tb >= 150)
                bbideal = 90 * (tb - 100) / 100;
            else
                bbideal = tb - 100;
        }
        beratIdeal.setText(String.valueOf(bbideal));
    }

    //IMT
    private void imtExe() {
        double imt=0.0, bb,tb;
        bb = Double.valueOf(berat.getText().toString());
        tb = Double.valueOf(tinggi.getText().toString())/100;

        imt = bb/(tb*tb);

//
//        if (imt < 18.5) "Kurus (underweight)";
//        else if (18.5 <= imt < 25.0) "Normal (ideal)";
//        else if (25.0 <= imt < 30.0) "Kegemukan (overweight – Pre Obese)";
//        else if (30.0 <= imt < 35.0) "Obesitas tingkat 1";
//        else if (35.0 <= imt < 40.0) "Obesitas tingkat 2";
//        else if (imt >= 40.0) "Obesitas tingkat 3";
    }


    //KADAR LEMAK
    private void lemakNBC() {

        tinggiBadanSangatTingi = new ArrayList<>();
        tinggiBadanTinggi = new ArrayList<>();
        tinggiBadanNormal = new ArrayList<>();
        tinggiBadanRendah = new ArrayList<>();

        beratBadanSangatTinggi = new ArrayList<>();
        beratBadanTinggi = new ArrayList<>();
        beratBadanNormal = new ArrayList<>();
        beratBadanRendah = new ArrayList<>();

        lingkarPinggangSangatTinggi = new ArrayList<>();
        lingkarPinggangTinggi = new ArrayList<>();
        lingkarPinggangNormal = new ArrayList<>();
        lingkarPinggangRendah = new ArrayList<>();

        jenisKelaminAL = new ArrayList<>();

        userBerat = Double.parseDouble(berat.getText().toString());
        userTinggi = Double.parseDouble(tinggi.getText().toString());
        userPerut = Double.parseDouble(pinggang.getText().toString());
        userKelamin = kelamin.getText().toString();

        // class Tinggi badan
        ref.child("DataKasus").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                for (DataSnapshot kasus : dataSnapshot.getChildren()) {
                    String kadarLemak = kasus.child("kadarLemak").getValue().toString();
                    String tbS = kasus.child("tb").getValue().toString();
                    String bbS = kasus.child("bb").getValue().toString();
                    String lpS = kasus.child("lp").getValue().toString();
                    String jk = kasus.child("kategori").getValue().toString();

                    //  Perhitungan data kuantitatif


                    if (kadarLemak.equals("SANGAT TINGGI")) {
                        tinggiBadanSangatTingi.add(tbS);
                        beratBadanSangatTinggi.add(bbS);
                        lingkarPinggangSangatTinggi.add(lpS);
                        sumTBLST += Double.parseDouble(tbS);
                        sumBBLST += Double.parseDouble(bbS);
                        sumLPLST += Double.parseDouble(lpS);
                        totalSangatTinggi += 1;

                    } else if (kadarLemak.equals("TINGGI")) {
                        tinggiBadanTinggi.add(tbS);
                        beratBadanTinggi.add(bbS);
                        lingkarPinggangTinggi.add(lpS);
                        sumTBT += Double.parseDouble(tbS);
                        sumBBLT += Double.parseDouble(bbS);
                        sumLPLT += Double.parseDouble(lpS);
                        totalTinggi += 1;

                    } else if (kadarLemak.equals("NORMAL")) {
                        tinggiBadanNormal.add(tbS);
                        beratBadanNormal.add(bbS);
                        lingkarPinggangNormal.add(lpS);
                        sumTBS += Double.parseDouble(tbS);
                        sumBBLN += Double.parseDouble(bbS);
                        sumLPLN += Double.parseDouble(lpS);
                        totalNormal += 1;

                    } else if (kadarLemak.equals("RINGAN")) {
                        tinggiBadanRendah.add(tbS);
                        beratBadanRendah.add(bbS);
                        lingkarPinggangRendah.add(lpS);
                        sumTBR += Double.parseDouble(tbS);
                        sumBBLR += Double.parseDouble(bbS);
                        sumLPLR += Double.parseDouble(lpS);
                        totalRendah += 1;

                    }

                    if (kadarLemak.equals("SANGAT TINGGI") && jk.equals("Laki-laki")) {
                        sumLKLST += 1;
                    } else if (kadarLemak.equals("TINGGI") && jk.equals("Laki-laki")) {
                        sumLKLT += 1;
                    } else if (kadarLemak.equals("NORMAL") && jk.equals("Laki-laki")) {
                        sumLKLN += 1;
                    } else if (kadarLemak.equals("RINGAN") && jk.equalsIgnoreCase("Laki-laki")) {
                        sumLKLR += 1;
                    } else if (kadarLemak.equals("SANGAT TINGGI") && jk.equalsIgnoreCase("Perempuan")) {
                        sumPRLST += 1;
                    } else if (kadarLemak.equals("TINGGI") && jk.equalsIgnoreCase("Perempuan")) {
                        sumPRLT += 1;
                    } else if (kadarLemak.equals("NORMAL") && jk.equalsIgnoreCase("Perempuan")) {
                        sumPRLN += 1;
                    } else if (kadarLemak.equals("RINGAN") && jk.equalsIgnoreCase("Perempuan")) {
                        sumPRLR += 1;
                    }

                }

                // tinggi badan
                meanTBST = round(sumTBLST / totalSangatTinggi);
                meanTBT = round(sumTBT / totalTinggi);
                meanTBN = round(sumTBS / totalNormal);
                meanTBR = round(sumTBR / totalRendah);

                //Berat Badan
                meanBBST = round(sumBBLST / totalSangatTinggi);
                meanBBT = round(sumBBLT / totalTinggi);
                meanBBN = round(sumBBLN / totalNormal);
                meanBBR = round(sumBBLN / totalNormal);
                //LINGKAR PERUT
                meanLPST = round(sumLPLST / totalSangatTinggi);
                meanLPT = round(sumLPLT / totalTinggi);
                meanLPN = round(sumLPLN / totalNormal);
                meanLPR = round(sumLPLR / totalRendah);

                Double totalKasus = totalSangatTinggi + totalTinggi + totalNormal + totalRendah;

                for (int i = 0; i < tinggiBadanSangatTingi.size(); i++) {
                    String tbstMasuk = tinggiBadanSangatTingi.get(i);
                    Double tmp;
                    tmp = (Double.parseDouble(tbstMasuk) - meanTBST);
                    sdTinggiSangatTinggi += round(Math.pow(tmp, 2.0));
                }
                sdTinggiSangatTinggi = round(sdTinggiSangatTinggi / (tinggiBadanSangatTingi.size() - 1));
                sdTinggiSangatTinggi = round(Math.sqrt(sdTinggiSangatTinggi));

                for (String tbtMasuk : tinggiBadanTinggi) {
                    Double tmp;
                    tmp = (Double.parseDouble(tbtMasuk) - meanTBT);
                    sdTinggiTinggi += round(Math.pow(tmp, 2.0));
                }
                sdTinggiTinggi = round(sdTinggiTinggi / (tinggiBadanTinggi.size() - 1));
                sdTinggiTinggi = round(Math.sqrt(sdTinggiTinggi));
                for (String tbnMasuk : tinggiBadanNormal) {
                    Double tmp;
                    tmp = (Double.parseDouble(tbnMasuk) - meanTBN);
                    sdTinggiNormal += round(Math.pow(tmp, 2.0));
                }
                sdTinggiNormal = round(sdTinggiNormal / (tinggiBadanNormal.size() - 1));
                sdTinggiNormal = round(Math.sqrt(sdTinggiNormal));
                for (String tbrMasuk : tinggiBadanRendah) {
                    Double tmp;
                    tmp = (Double.parseDouble(tbrMasuk) - meanTBR);
                    sdTinggiRendah += round(Math.pow(tmp, 2.0));
                }
                sdTinggiRendah = round(sdTinggiRendah / (tinggiBadanRendah.size() - 1));
                sdTinggiRendah = round(Math.sqrt(sdTinggiRendah));

                //berat standar deviasi
                for (String bstMasuk : beratBadanSangatTinggi) {
                    Double tmp;
                    tmp = (Double.parseDouble(bstMasuk) - meanBBST);
                    sdBeratSangatTinggi += round(Math.pow(tmp, 2.0));
                }
                sdBeratSangatTinggi = round(sdBeratSangatTinggi / (beratBadanSangatTinggi.size() - 1));
                sdBeratSangatTinggi = round(Math.sqrt(sdBeratSangatTinggi));

                for (String tbtMasuk : beratBadanTinggi) {
                    Double tmp;
                    tmp = (Double.parseDouble(tbtMasuk) - meanBBT);
                    sdTinggiTinggi += round(Math.pow(tmp, 2.0));
                }
                sdBeratTinggi = round(sdBeratTinggi / (beratBadanTinggi.size() - 1));
                sdBeratTinggi = round(Math.sqrt(sdBeratTinggi));
                for (String tbnMasuk : beratBadanNormal) {
                    Double tmp;
                    tmp = (Double.parseDouble(tbnMasuk) - meanBBN);
                    sdBeratNormal += round(Math.pow(tmp, 2.0));
                }
                sdBeratNormal = round(sdBeratNormal / (beratBadanNormal.size() - 1));
                sdBeratNormal = round(Math.sqrt(sdBeratNormal));
                for (String tbrMasuk : beratBadanRendah) {
                    Double tmp;
                    tmp = (Double.parseDouble(tbrMasuk) - meanBBR);
                    sdBeratRendah += round( Math.pow(tmp, 2.0));
                }
                sdBeratRendah = round(sdBeratRendah / (beratBadanRendah.size() - 1));
                sdBeratRendah = round(Math.sqrt(sdBeratRendah));

                //perut standar deviasi
                for (String lpMasuk :
                        lingkarPinggangSangatTinggi) {
                    Double tmp;
                    tmp = (Double.parseDouble(lpMasuk) - meanLPST);
                    sdPerutSangatTinggi += round(Math.pow(tmp, 2.0));
                }
                sdPerutSangatTinggi = round(sdPerutSangatTinggi / (lingkarPinggangSangatTinggi.size() - 1));
                sdPerutSangatTinggi = round( Math.sqrt(sdBeratSangatTinggi));

                for (String lpMasuk :
                        lingkarPinggangTinggi) {
                    Double tmp;
                    tmp = (Double.parseDouble(lpMasuk) - meanLPT);
                    sdPerutTinggi += round(Math.pow(tmp, 2.0));
                }
                sdPerutTinggi = round(sdPerutTinggi / (lingkarPinggangTinggi.size() - 1));
                sdPerutTinggi = round(Math.sqrt(sdPerutTinggi));

                for (String lpMasuk :
                        lingkarPinggangNormal) {
                    Double tmp;
                    tmp = (Double.parseDouble(lpMasuk) - meanLPN);
                    sdPerutNormal += round(Math.pow(tmp, 2.0));
                }
                sdPerutNormal = round(sdPerutNormal / (lingkarPinggangNormal.size() - 1));
                sdPerutNormal = round(Math.sqrt(sdPerutNormal));

                for (String lpMasuk :
                        lingkarPinggangRendah) {
                    Double tmp;
                    tmp = (Double.parseDouble(lpMasuk) - meanLPR);
                    sdPerutRendah += round(Math.pow(tmp, 2.0));
                }
                sdPerutRendah = round(sdPerutRendah / (lingkarPinggangRendah.size() - 1));
                sdPerutRendah = round(Math.sqrt(sdPerutRendah));


                // pencocokan data
                //tinggi badan
                ngTinggiST = normalGaussian(userTinggi, meanTBST, sdTinggiSangatTinggi);
                ngTinggiT = normalGaussian(userTinggi, meanTBT, sdTinggiTinggi);
                ngTinggiN = normalGaussian(userTinggi, meanTBN, sdTinggiNormal);
                ngTinggiR = normalGaussian(userTinggi, meanTBR, sdTinggiRendah);

                //berat badan
                ngBeratST = normalGaussian(userBerat, meanBBST, sdBeratSangatTinggi);
                ngBeratT = normalGaussian(userBerat, meanBBT, sdBeratTinggi);
                ngBeratN = normalGaussian(userBerat, meanBBN, sdBeratNormal);
                ngBeratR = normalGaussian(userBerat, meanBBR, sdBeratRendah);

                //lingkar perut
                ngPerutST = normalGaussian(userPerut, meanLPST, sdPerutSangatTinggi);
                ngPerutT = normalGaussian(userPerut, meanLPT, sdPerutTinggi);
                ngPerutN = normalGaussian(userPerut, meanLPN, sdPerutNormal);
                ngPerutR = normalGaussian(userPerut, meanLPR, sdPerutRendah);



                //probabilitas kelamin
                probLakiST = round(sumLKLST / totalSangatTinggi);
                probLakiT = round(sumLKLT / totalTinggi);
                probLakiN = round(sumLKLN / totalNormal);
                probLakiR = round(sumLKLR / totalRendah);



                probPerST = round(sumPRLST / totalSangatTinggi);
                probPerT = round(sumPRLT / totalTinggi);
                probPerN = round(sumPRLN / totalNormal);
                probPerR = round(sumPRLR / totalRendah);


                //prob class
                Double probSangatTinggi = round(totalSangatTinggi / totalKasus);
                Double probTinggi = totalTinggi / totalKasus;
                Double probNormal = totalNormal / totalKasus;
                Double probRendah = totalRendah / totalKasus;
                Double likelihoodSanggatTinggi = ngTinggiST * ngBeratST * ngPerutST * probSangatTinggi;
                Double likelihoodTinggi = ngTinggiT * ngBeratT * ngPerutT * probTinggi;
                Double likelihoodNormal = ngTinggiN * ngBeratN * ngPerutN * probNormal;
                Double likelihoodRendah = ngTinggiR * ngBeratR * ngPerutR * probRendah;

                if ("Laki-laki".equals(userKelamin)) {
                    likelihoodSanggatTinggi *= probLakiST;
                    likelihoodTinggi *= probLakiT;
                    likelihoodNormal *= probLakiN;
                    likelihoodRendah *= probLakiR;

                } else if ("Perempuan".equals(userKelamin)) {
                    likelihoodSanggatTinggi *= probPerST;
                    likelihoodTinggi *= probPerT;
                    likelihoodNormal *= probPerN;
                    likelihoodRendah *= probPerR;
                }
                //likelihoodSanggatTinggi = round(likelihoodSanggatTinggi);

                Double fixSangatTinggi = likelihoodSanggatTinggi / (likelihoodSanggatTinggi + likelihoodTinggi + likelihoodNormal + likelihoodRendah);
                Double fixTinggi = likelihoodTinggi / (likelihoodSanggatTinggi + likelihoodTinggi + likelihoodNormal + likelihoodRendah);
                Double fixNormal = likelihoodNormal / (likelihoodSanggatTinggi + likelihoodTinggi + likelihoodNormal + likelihoodRendah);
                Double fixRendah = likelihoodRendah / (likelihoodSanggatTinggi + likelihoodTinggi + likelihoodNormal + likelihoodRendah);

                if (fixSangatTinggi > fixTinggi && fixSangatTinggi > fixNormal && fixSangatTinggi > fixRendah) {
                    kadarLemakUser.setText("SANGAT TINGGI");
                } else if (fixTinggi > fixSangatTinggi && fixTinggi > fixNormal && fixTinggi > fixRendah) {
                    kadarLemakUser.setText("TINGGI");
                } else if (fixNormal > fixTinggi && fixNormal > fixSangatTinggi && fixNormal > fixRendah) {
                    kadarLemakUser.setText("NORMAL");
                } else if (fixRendah > fixSangatTinggi && fixRendah > fixTinggi && fixRendah > fixNormal) {
                    kadarLemakUser.setText("RINGAN");
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private Double normalGaussian(Double userInfo, Double rata, Double standarDeviasi) {
        Double bawah, expo;
        expo = (Math.pow(userInfo - rata, 2) * -1) / (Math.pow(standarDeviasi, 2) * 2);
        bawah = Math.sqrt((2 * Math.PI * standarDeviasi));

        return 1 * Math.pow(Math.E, expo) / bawah;
    }

    private Double round(Double d){
        DecimalFormat x = new DecimalFormat("#.####");
        return Double.parseDouble(x.format(d));
    }


    //Expandable Menu Makanan

    /*public void tampilMakanMalam(View view) {
        tampilMakanMalam = (ExpandableRelativeLayout) findViewById(R.id.expandMakanMalam);
        tampilMakanMalam.expand();
    }

    public void tampilMakanSiang(View view) {
        tampilMakanSiang = (ExpandableRelativeLayout) findViewById(R.id.expandMakanSiang);
        tampilMakanSiang.expand();
    }

    public void tampilSarapan(View view) {
        tampilSarapan = (ExpandableRelativeLayout) findViewById(R.id.expandSarapan);
        tampilSarapan.expand();
    }*/


}
