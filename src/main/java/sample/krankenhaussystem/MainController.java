package sample.krankenhaussystem;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.print.PrinterJob;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ResourceBundle;


public class MainController implements Initializable {

    @FXML
    private TextField name, surname, email, phone, street, plz, country, insuranceNumber, med11, med12, med21, med22, med31, med32;
    @FXML
    private Label breakfastQuantity, lunchQuantity, dinnerQuantity, total, tax;
    @FXML
    private CheckBox ambulanceYes, ambulanceNo, mealYes, mealNo, bloodTest, urineTest, stoolTest, mrt, ct, xray;
    @FXML
    private ComboBox roomtype, gender, section;
    @FXML
    private GridPane meal;
    @FXML
    private Button breakfastPlus, breakfastMinus, lunchPlus, lunchMinus, dinnerPlus, dinnerMinus;
    @FXML
    private DatePicker checkIn, checkOut, birthday;
    @FXML
    private TextArea bill;
    //Formatiert die double werten in String mit dem form 0.00.
    private DecimalFormat decimalFormat = new DecimalFormat("0.00");
    //Listen mit bestimmten werten um gleich zu den Comboboxes hinzufügen.
    private ObservableList<String> genderList = FXCollections.observableArrayList("Männlich", "Weiblich", "Andere");
    private ObservableList<String> sectionList = FXCollections.observableArrayList("Kardiologie", "Orthipädie", "Gynäkologie", "Kinderheilkunde", "Radiologie", "Notfall");
    private ObservableList<String> roomtypeList = FXCollections.observableArrayList("1-Bett Raum", "2-Bett Raum", "Intensivstation");
    Patient patient;
    ListOfPatients listOfPatients;
    public void setPatient(Patient patient) {
        this.patient = patient;
    }
    /*
    Wenn "ja" ausgewählt dann "nein" vernachlässigen und umgekehrt. Das gilt für beides Krankenwagen und Mahlzeit.
     */
    public void handelCheckBox(ActionEvent event) {
        if (event.getSource().equals(ambulanceYes)) {
            ambulanceYes.setSelected(true);
            if (ambulanceYes.isSelected()) {
                ambulanceNo.setSelected(false);
            }
        } else if (event.getSource().equals(ambulanceNo)) {
            ambulanceNo.setSelected(true);
            if (ambulanceNo.isSelected()) {
                ambulanceYes.setSelected(false);
            }
        } else if (event.getSource().equals(mealYes)) {
            mealYes.setSelected(true);
            /*
            Wenn Mahlzeiten "ja" angekruzt, dann die Transparenz des Blocks zu 1.0 setzen und aller "+", "-" Buttons
            wieder aktivieren.
             */
            if (mealYes.isSelected()) {
                mealNo.setSelected(false);
                meal.setOpacity(1.0);
                breakfastPlus.setDisable(false);
                breakfastMinus.setDisable(false);
                lunchPlus.setDisable(false);
                lunchMinus.setDisable(false);
                dinnerPlus.setDisable(false);
                dinnerMinus.setDisable(false);
            }
        } else if (event.getSource().equals(mealNo)) {
            mealNo.setSelected(true);
            /*
            Wenn Mahlzeiten "Nein" angekruzt, dann die Transparenz des Blocks zu 0.2 setzen und aller "+", "-" Buttons
            wieder deaktivieren.
             */
            if (mealNo.isSelected()) {
                mealYes.setSelected(false);
                meal.setOpacity(0.2);
                breakfastPlus.setDisable(true);
                breakfastMinus.setDisable(true);
                lunchPlus.setDisable(true);
                lunchMinus.setDisable(true);
                dinnerPlus.setDisable(true);
                dinnerMinus.setDisable(true);
                breakfastQuantity.setText("0");
                lunchQuantity.setText("0");
                dinnerQuantity.setText("0");
            }
        }
    }
    /*
    Öffne Druckenbox, um Inhalt von TextArea bill drucken oder als PDF speicheren.
    Quelle: http://www.java2s.com/Tutorials/Java/JavaFX_How_to/Print/Print_Text_out.htm
     */
    public void printTheBill() {
        if (!bill.getText().equals("")) {
            PrinterJob printerJob = PrinterJob.createPrinterJob();
            if (printerJob != null && printerJob.showPrintDialog(bill.getScene().getWindow())) {
                Label l = new Label();
                l.setText(bill.getText());
                boolean success = printerJob.printPage(l);
                if (success) {
                    printerJob.endJob();
                }
            }
        }
    }
    /*
    Entscheidet welche "+" "-" zu welche Mahlzeiten gehört, und wenn "+" aufgerufen wird, dann pluser(Label l) mit passenden
    Label aufrufen und das gleiche fall für minuser(Label l).
     */
    public void eatManipulateHandler(ActionEvent event) {
        if (event.getSource().equals(breakfastPlus)) {
            pluser(breakfastQuantity);
        } else if (event.getSource().equals(breakfastMinus)) {
            minuser(breakfastQuantity);
        } else if (event.getSource().equals(lunchPlus)) {
            pluser(lunchQuantity);
        } else if (event.getSource().equals(lunchMinus)) {
            minuser(lunchQuantity);
        } else if (event.getSource().equals(dinnerPlus)) {
            pluser(dinnerQuantity);
        } else {
            minuser(dinnerQuantity);
        }
    }
    //addiert zu Label l "1" bis maximum 100.
    private void pluser(Label l) {
        String x = l.getText();
        int y = Integer.parseInt(x);
        if (y < 100) {
            y++;
        }
        l.setText(y + "");
    }
    //zieht von Label l "1" bis minmum 0.
    private void minuser(Label l) {
        String x = l.getText();
        int y = Integer.parseInt(x);
        if (y > 0) {
            y--;
        }
        l.setText(y + "");
    }
    //Alle werten in patient zurücksetzen, alle Felder und Detpickeres in weiß färben und alle werte von patient zu view lieferen
    public void reset() {
        patient.reset();
        resetStyle();
        refreshView();
    }
    //Alle werte von patient zu view lieferen.
    public void refreshView() {
        name.setText(patient.getName());
        surname.setText(patient.getSurname());
        birthday.setValue(patient.getBirthday());
        email.setText(patient.getEmail());
        phone.setText(patient.getPhone());
        gender.setValue(patient.getGender());
        street.setText(patient.getStreet());
        plz.setText(patient.getPlz());
        country.setText(patient.getCountry());
        checkIn.setValue(patient.getCheckIn());
        checkOut.setValue(patient.getCheckOut());
        section.setValue(patient.getSection());
        roomtype.setValue(patient.getRoom());
        insuranceNumber.setText(patient.getInsuranceNumber());
        med11.setText(patient.getMedicines().get(0) + "");
        med12.setText(patient.getMedicines().get(1) + "");
        med21.setText(patient.getMedicines().get(2) + "");
        med22.setText(patient.getMedicines().get(3) + "");
        med31.setText(patient.getMedicines().get(4) + "");
        med32.setText(patient.getMedicines().get(5) + "");
        breakfastQuantity.setText(patient.getMeals().get(0) + "");
        lunchQuantity.setText(patient.getMeals().get(1) + "");
        dinnerQuantity.setText(patient.getMeals().get(2) + "");
        bloodTest.setSelected(patient.getDiagnoses().get(0));
        urineTest.setSelected(patient.getDiagnoses().get(1));
        stoolTest.setSelected(patient.getDiagnoses().get(2));
        mrt.setSelected(patient.getDiagnoses().get(3));
        ct.setSelected(patient.getDiagnoses().get(4));
        xray.setSelected(patient.getDiagnoses().get(5));
        total.setText(decimalFormat.format(patient.getResult()));
        tax.setText(decimalFormat.format(patient.getTax()));
    }
    /*
    Kontrolliert die TextFields, DatePickers und färbt in Rot wenn nicht ausgefüllt, gibt true zurück
    wenn alle Eingaben ausgefüllt und gültig, sonst gibts false zurück.
     */
    private boolean allIsGood() {
        //Resetstyle, um beim jeden versuch gültige Eingaben ausnehmen
        resetStyle();
        boolean flag = true;
        if (name.getText().equals("")) {
            flag = false;
            name.setStyle("-fx-background-color: rgb(210, 104, 103)");
        }
        if (surname.getText().equals("")) {
            flag = false;
            surname.setStyle("-fx-background-color: rgb(210, 104, 103)");
        }
        if (birthday.getValue() == null) {
            flag = false;
            birthday.setStyle("-fx-control-inner-background: rgb(210, 104, 103)");
        }
        if (email.getText().equals("")) {
            flag = false;
            email.setStyle("-fx-background-color: rgb(210, 104, 103)");
        }
        if (phone.getText().equals("")) {
            flag = false;
            phone.setStyle("-fx-background-color: rgb(210, 104, 103)");
        }
        if (street.getText().equals("")) {
            flag = false;
            street.setStyle("-fx-background-color: rgb(210, 104, 103)");
        }
        if (plz.getText().equals("")) {
            flag = false;
            plz.setStyle("-fx-background-color: rgb(210, 104, 103)");
        }
        if (country.getText().equals("")) {
            flag = false;
            country.setStyle("-fx-background-color: rgb(210, 104, 103)");
        }
        if (checkIn.getValue() == null) {
            flag = false;
            checkIn.setStyle("-fx-control-inner-background: rgb(210, 104, 103)");
        }
        //Färbt checkin in rot, wenn das Checkin Datum nach Checkout Datum.
        if (checkIn.getValue() != null && checkOut.getValue().isBefore(checkIn.getValue())) {
            flag = false;
            checkIn.setStyle("-fx-control-inner-background: rgb(210, 104, 103)");
        }
        if (checkOut.getValue() == null) {
            flag = false;
            checkOut.setStyle("-fx-control-inner-background: rgb(210, 104, 103)");
        }
        if (insuranceNumber.getText().equals("")) {
            flag = false;
            insuranceNumber.setStyle("-fx-background-color: rgb(210, 104, 103)");
        }
        return flag;
    }
    //Aktulisert den Inhalt von TextArea zu eine geschribene Rechnung.
    public void updateBill(){
        String content = "\n                   Krankenhaus Rechnung System                         \n" +
                "Reference:                                                             4204\n" +
                "=====================================\n" +
                "=====================================\n"+
                "Patient/in Ref:                                                      11376\n" +
                "Vorname:                                                               " + patient.getName()+"\n"+
                "Nachname:                                                             " +patient.getSurname()+"\n"+
                "Geschlecht:                                                           " +patient.getGender()+"\n"+
                "Geburtsdatum:                                                 " +patient.getBirthday()+"\n"+
                "Telefonnummer:                               " +patient.getPhone()+"\n"+
                "Email:                                     " +patient.getEmail()+"\n"+
                "Addresse:                                                              " +patient.getStreet()+"\n"+
                "plz,Land:                                                                " +patient.getPlz()+", "+patient.getCountry().substring(0,2)+"\n"+
                "Versicherungsnummer:                                      " +patient.getInsuranceNumber()+"\n"+
                "Einchecken:                                               " +patient.getCheckIn()+"\n" +
                "Auschecken:                                                 " +patient.getCheckOut()+"\n" +
                "Abteilung:                                                       "+patient.getSection()+"\n" +
                "Raum Typ:                                                        "+patient.getRoom()+"\n" +
                "=====================================\n";
        if(!checkIn.getValue().toString().equals(checkOut.getValue().toString())){
            double priceOfStay= 0.0;
            double priceOfOneNight = 0;
            int days= (int) Duration.between(checkIn.getValue().atStartOfDay(),checkOut.getValue().atStartOfDay()).toDays();
            if(roomtype.getValue().equals(roomtypeList.get(0))){
                priceOfOneNight=170.0;
                priceOfStay = days*priceOfOneNight;
            }
            if(roomtype.getValue().equals(roomtypeList.get(1))){
                priceOfOneNight=100.0;
                priceOfStay = days*priceOfOneNight;
            }
            if(roomtype.getValue().equals(roomtypeList.get(2))){
                priceOfOneNight=230.0;
                priceOfStay = days*priceOfOneNight;
            }
            content+=roomtype.getValue().toString()+" "+days+"x       ("+priceOfOneNight+"€ pro Nacht)"+"          "+priceOfStay+"€\n";
        }
        if (patient.isAmbulance()){
            content+="Krankenwagen:                                                     340€\n";
        }
        if (patient.consumedMed()){
            content+="Medikamenten:\n";
            if (patient.getMedicines().get(0)!=0){
                double price = patient.getMedicines().get(0)*0.20;
                content+="                            "+patient.getMedicines().get(0)+"x I.V. (1 Stk = 0.20€)             "+decimalFormat.format(price)+"€\n";
            }
            if (patient.getMedicines().get(1)!=0){
                double price = patient.getMedicines().get(1)*0.17;
                content+="                             "+patient.getMedicines().get(1)+"x S (1 Stk = 0.17€)             "+decimalFormat.format(price)+"€\n";
            }
            if (patient.getMedicines().get(2)!=0){
                double price = patient.getMedicines().get(2)*0.05;
                content+="                              "+patient.getMedicines().get(2)+"x K.T. (1 Stk = 0.05€)             "+decimalFormat.format(price)+"€\n";
            }
            if (patient.getMedicines().get(3)!=0){
                double price = patient.getMedicines().get(3)*0.10;
                content+="                               "+patient.getMedicines().get(3)+"x B (1 Stk = 0.10€)             "+decimalFormat.format(price)+"€\n";
            }
            if (patient.getMedicines().get(4)!=0){
                double price = patient.getMedicines().get(4)*0.23;
                content+="                                "+patient.getMedicines().get(4)+"x A.B. (1 Stk = 0.23€)             "+decimalFormat.format(price)+"€\n";
            }
            if (patient.getMedicines().get(5)!=0){
                double price = patient.getMedicines().get(5)*0.7;
                content+="                                "+patient.getMedicines().get(5)+"x T (1 Stk = 0.70€)             "+decimalFormat.format(price)+"€\n";
            }
        }
        if (patient.counsumedMeal()){
            content+="Mahlzeiten:\n";
            if (patient.getMeals().get(0)!=0){
                double price = patient.getMeals().get(0)*7.5;
                content+= "           "+breakfastQuantity.getText()+"x Frühstück (1 Frühstück = 7.50€)       "+decimalFormat.format(price)+"€\n";
            }
            if (patient.getMeals().get(1)!=0){
                double price = patient.getMeals().get(1)*9.8;
                content+= "            "+lunchQuantity.getText()+"x Mittagessen (1 Mittagessen = 9.80€)       "+decimalFormat.format(price)+"€\n";
            }
            if (patient.getMeals().get(2)!=0){
                double price = patient.getMeals().get(2)*5.3;
                content+= "             "+dinnerQuantity.getText()+"x Abendessen (1 Abendessen = 5.30€)       "+decimalFormat.format(price)+"€\n";
            }
        }
        if (patient.consumedDiagnoses()){
            content += "Diagnosesverfahren: \n";
            if (patient.getDiagnoses().get(0)){
                content+="                    Bluttest                                  77€\n";
            }
            if (patient.getDiagnoses().get(1)){
                content+="                    Urineanlyse                                 50€\n";
            }
            if (patient.getDiagnoses().get(2)){
                content+="                    Stuhlanalyse                                  50€\n";
            }
            if (patient.getDiagnoses().get(3)){
                content+="                    MRT                                              390€\n";
            }
            if (patient.getDiagnoses().get(4)){
                content+="                    CT                                                 280€\n";
            }
            if (patient.getDiagnoses().get(5)){
                content+="                    Röntgen                                        130€\n";
            }
        }

        content+="=====================================\n";
        content+="MwSt(19%):                                                 "+decimalFormat.format(patient.getTax())+"€\n" +
                "Preis ohne MwSt:                                         "+decimalFormat.format(patient.getResult())+"€\n" +
                "Summe:                                                   "+decimalFormat.format(patient.getResult()+patient.getTax())+"€\n";
        content+="=====================================\n";
        content+="Datum: "+LocalDate.now()+"" +
                "                       Zeit: "+ LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))+"\n";
        content+="\n              Danke und Bleiben Sie Gesund                 ";
        bill.setText(content);
    }
    /*
    Bearbeitet das TextField t inhalt, damit aller unnumerisch Eingaben mit leeren eingabe ersetzt wird.
    Quelle: https://stackoverflow.com/questions/7555564/what-is-the-recommended-way-to-make-a-numeric-textfield-in-javafx
     */
    private void toNumeric(TextField t){
        t.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue observableValue, String oldValue, String newValue) {
                if(!newValue.matches("\\d*")){
                    t.setText(newValue.replaceAll("[^\\d]",""));
                }
            }
        }
        );
    }
    //Zurücksetzen alle TextFields und DatePickers zur weiß
    public void resetStyle(){
            name.setStyle("-fx-background-color: white");
            surname.setStyle("-fx-background-color: white");
            birthday.setStyle("-fx-control-inner-background: white");
            email.setStyle("-fx-background-color: white");
            phone.setStyle("-fx-background-color: white");
            street.setStyle("-fx-background-color: white");
            plz.setStyle("-fx-background-color: white");
            country.setStyle("-fx-background-color: white");
            checkIn.setStyle("-fx-control-inner-background: white");
            checkOut.setStyle("-fx-control-inner-background: white");
            insuranceNumber.setStyle("-fx-background-color: white");
    }
    //Funktioniert als erstes wenn die App abgerufen

    public void initialize(URL url, ResourceBundle resourceBundle) {

        gender.setItems(genderList); // um die geschlecht Liste in ComboBox einzufügen
        section.setItems(sectionList);//um die Abteilung Liste in ComboBox einzufügen
        roomtype.setItems(roomtypeList);//um die Raumtype Liste in ComboBox einzufügen
        mealNo.setSelected(true);
        ambulanceNo.setSelected(true);
        checkOut.setValue(LocalDate.now());
        gender.setValue(genderList.get(1));
        section.setValue(sectionList.get(5));
        roomtype.setValue(roomtypeList.get(1));
        //um keine Buchstaben zur schreiben können
        toNumeric(plz);
        toNumeric(phone);
        toNumeric(med11);
        toNumeric(med12);
        toNumeric(med21);
        toNumeric(med22);
        toNumeric(med31);
        toNumeric(med32);
    }
    /*
    liefert aller werte von view zu patient, wenn die richtig angegebne sind und ruft calculte() in patient,dann aktulisere
    total und tax label in view, und dann ruft updateBill().
     */
    public void calc(){
        if (allIsGood()) {
            patient.setName(name.getText());
            patient.setSurname(surname.getText());
            patient.setBirthday(birthday.getValue());
            patient.setEmail(email.getText());
            patient.setPhone(phone.getText());
            patient.setGender(gender.getValue().toString());
            patient.setStreet(street.getText());
            patient.setPlz(plz.getText());
            patient.setCountry(country.getText());
            patient.setCheckIn(checkIn.getValue());
            patient.setCheckOut(checkOut.getValue());
            patient.setSection(section.getValue().toString());
            patient.setRoom(roomtype.getValue().toString());
            patient.setInsuranceNumber(insuranceNumber.getText());
            patient.setAmbulance(ambulanceYes.isSelected());
            ArrayList<Integer> medi = new ArrayList<Integer>();
            if (!med11.getText().equals("")){
                medi.add(Integer.parseInt(med11.getText()));
            } else {
                medi.add(0);
                med11.setText("0");
            }
            if (!med12.getText().equals("")){
                medi.add(Integer.parseInt(med12.getText()));
            } else {
                medi.add(0);
                med12.setText("0");
            }
            if (!med21.getText().equals("")){
                medi.add(Integer.parseInt(med21.getText()));
            } else {
                medi.add(0);
                med21.setText("0");
            }
            if (!med22.getText().equals("")){
                medi.add(Integer.parseInt(med22.getText()));
            } else {
                medi.add(0);
                med22.setText("0");
            }
            if (!med31.getText().equals("")){
                medi.add(Integer.parseInt(med31.getText()));
            } else {
                medi.add(0);
                med31.setText("0");
            }
            if (!med32.getText().equals("")){
                medi.add(Integer.parseInt(med32.getText()));
            } else {
                medi.add(0);
                med32.setText("0");
            }
            patient.setMedicines(medi);
            ArrayList<Integer> eat = new ArrayList<Integer>();
            eat.add(Integer.parseInt(breakfastQuantity.getText()));
            eat.add(Integer.parseInt(lunchQuantity.getText()));
            eat.add(Integer.parseInt(dinnerQuantity.getText()));
            patient.setMeals(eat);
            ArrayList<Boolean> diagnoses = new ArrayList<Boolean>();
            diagnoses.add(bloodTest.isSelected());
            diagnoses.add(urineTest.isSelected());
            diagnoses.add(stoolTest.isSelected());
            diagnoses.add(mrt.isSelected());
            diagnoses.add(ct.isSelected());
            diagnoses.add(xray.isSelected());
            patient.setDiagnoses(diagnoses);
            patient.calculate();
            total.setText(decimalFormat.format(patient.getResult()));
            tax.setText(decimalFormat.format(patient.getTax()));
            updateBill();
            save();
            System.out.println(listOfPatients.getSize());
        }
    }
    public void triger() throws IOException {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("load.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 300, 400);
        stage.setScene(scene);
        stage.show();
        stage.setTitle("Laden ein Patient!");
        load();
        LoadController lc = fxmlLoader.getController();
        lc.setListOfPatients(listOfPatients);
        lc.initListOfView();
        lc.setMainController(this);
    }
    public void save(){
        try {
            load();
            listOfPatients.add(patient);
            FileOutputStream fileOutputStream = new FileOutputStream("save.khs");
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(listOfPatients);
            objectOutputStream.close();
        }catch (IOException e){
            System.err.println("error in save file!!");
        }
    }
    public void load(){
        try {
            FileInputStream fileInputStream = new FileInputStream("save.khs");
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            listOfPatients = (ListOfPatients) objectInputStream.readObject();
            objectInputStream.close();
            System.out.println("File Loaded Successfully!");
        }catch (Exception e){
            try {
                FileOutputStream fileOutputStream = new FileOutputStream("save.khs");
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
                listOfPatients = new ListOfPatients();
                objectOutputStream.writeObject(listOfPatients);
                System.out.println("File Created successfully!!");
            }catch (IOException ee){
                System.err.println("Cant write File");
            }
        }
    }
}
