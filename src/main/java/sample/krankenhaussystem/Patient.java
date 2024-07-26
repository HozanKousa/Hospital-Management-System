package sample.krankenhaussystem;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;

public class Patient implements Serializable{
    private String name,surname,email,phone,gender,street,plz,country,section,insuranceNumber,room;
    private LocalDate birthday,checkIn,checkOut;
    private boolean ambulance;
    private ArrayList<Integer> medicines,meals;
    private ArrayList<Boolean> diagnoses;
    private double result,tax;
    //Alle Listen mit standarte werten ausfüllen.
    public Patient() {
        medicines = new ArrayList<Integer>(6);
        meals = new ArrayList<Integer>(3);
        diagnoses = new ArrayList<Boolean>(6);
        for (int i = 0; i < 6; i++) {
            medicines.add(0);
            diagnoses.add(false);
            if(i<3)
            meals.add(0);
        }
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setSurname(String surname) {
        this.surname = surname;
    }
    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }
    public void setStreet(String street) {
        this.street = street;
    }
    public void setPlz(String plz) {
        this.plz = plz;
    }
    public void setCountry(String country) {
        this.country = country;
    }
    public void setCheckIn(LocalDate checkIn) {
        this.checkIn = checkIn;
    }
    public void setCheckOut(LocalDate checkOut) {
        this.checkOut = checkOut;
    }
    public void setSection(String section) {
        this.section = section;
    }
    public void setRoom(String room) {
        this.room = room;
    }
    public void setInsuranceNumber(String insuranceNumber) {
        this.insuranceNumber = insuranceNumber;
    }
    public void setAmbulance(boolean ambulance) {
        this.ambulance = ambulance;
    }
    public void setMedicines(ArrayList<Integer> medicines) {
        this.medicines = medicines;
    }
    public void setMeals(ArrayList<Integer> meals) {
        this.meals = meals;
    }
    public void setDiagnoses(ArrayList<Boolean> diagnoses) {
        this.diagnoses = diagnoses;
    }
    public String getName() {
        return name;
    }
    public String getSurname() {
        return surname;
    }
    public LocalDate getBirthday() {
        return birthday;
    }
    public String getEmail() {
        return email;
    }
    public String getGender() {
        return gender;
    }
    public String getPhone() {
        return phone;
    }
    public String getStreet() {
        return street;
    }
    public String getCountry() {
        return country;
    }
    public String getPlz() {
        return plz;
    }
    public LocalDate getCheckIn() {
        return checkIn;
    }
    public LocalDate getCheckOut() {
        return checkOut;
    }
    public String getInsuranceNumber() {
        return insuranceNumber;
    }
    public String getSection() {
        return section;
    }
    public String getRoom() {
        return room;
    }
    public double getResult() {
        return result;
    }
    public double getTax() {
        return tax;
    }
    public boolean isAmbulance() {
        return ambulance;
    }
    public ArrayList<Integer> getMedicines() {
        return medicines;
    }
    public ArrayList<Integer> getMeals() {
        return meals;
    }
    public ArrayList<Boolean> getDiagnoses() {
        return diagnoses;
    }
    //Zurücksetzen alle werte.
    public void reset() {
        name = "";
        surname = "";
        birthday = null;
        email = "";
        phone = "";
        gender = "Weiblich";
        street = "";
        plz = "";
        country = "Deutschland";
        checkIn = null;
        checkOut = LocalDate.now();
        section = "Notfall";
        room = "2-Bett Raum";
        insuranceNumber = "";
        ambulance = false;
        result=0.00;
        tax=0.00;
        for (int i = 0; i < medicines.size(); i++) {
            medicines.set(i, 0);
        }
        for (int i = 0; i < meals.size(); i++) {
            meals.set(i, 0);
        }
        for (int i = 0; i < diagnoses.size(); i++) {
            diagnoses.set(i, false);
        }
    }
    /*
    Aktulsiert result nach dem Konsum des patients, und dann die MwSt rechnen (19% result).
     */
    public void calculate() {
        double r = 0.0;
        //Intitialisierung des Days um den Anzahl der Tage zwischen die checkin und checkout.
        int days = (int) Duration.between(checkIn.atStartOfDay(), checkOut.atStartOfDay()).toDays();
        if (room.equals("1-Bett Raum")) {
            r+= days * 170.0;
        } else if (room.equals("2-Bett Raum")) {
            r+= days * 100.0;
        } else {
            r+= days * 270.0;
        }
        if (ambulance) {
            r+=340.0;
        }
        r+=medicines.get(0)*0.20;
        r+=medicines.get(1)*0.17;
        r+=medicines.get(2)*0.05;
        r+=medicines.get(3)*0.10;
        r+=medicines.get(4)*0.23;
        r+=medicines.get(5)*0.7;

        r+=meals.get(0)*7.5;
        r+=meals.get(1)*9.8;
        r+=meals.get(2)*5.3;

        if (diagnoses.get(0)) r+=77.0;
        if (diagnoses.get(1)) r+=50.0;
        if (diagnoses.get(2)) r+=50.0;
        if (diagnoses.get(3)) r+=390.0;
        if (diagnoses.get(4)) r+=280.0;
        if (diagnoses.get(5)) r+=130.0;

        tax=(r*19.0)/100.0;

        result = r;
    }
    //Gibt "true" zurück, wenn der patient mindestens ein Medikament gebraucht hat.
    public boolean consumedMed(){
        for (int i = 0; i < medicines.size(); i++) {
            if (getMedicines().get(i)!=0) {
                return true;
            }
        }
        return false;
    }
    //Gibt "true" zurück, wenn der patient mindestens eine Mahlzeit konsumiert hat.
    public boolean counsumedMeal() {
        for (int i = 0; i < meals.size(); i++) {
            if (getMeals().get(i)!=0) {
                return true;
            }
        }
        return false;
    }
    //Gibt "true" zurück, wenn der patient mindestens ein Diagnosverfahren gemacht hat.
    public boolean consumedDiagnoses() {
        for (int i = 0; i < diagnoses.size(); i++) {
            if (getDiagnoses().get(i)) {
                return true;
            }
        }
        return false;
    }
}