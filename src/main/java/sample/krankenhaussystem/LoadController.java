package sample.krankenhaussystem;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;


public class LoadController {
    @FXML
    ListView listView;
    ListOfPatients listOfPatients;
    MainController mainController;

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void setListOfPatients(ListOfPatients listOfPatients) {
        this.listOfPatients = listOfPatients;
    }
    public void initListOfView(){
        String name = "";
        for (int i = 0; i< listOfPatients.getSize(); i++){
            name = listOfPatients.get(i).getName()+", "+listOfPatients.get(i).getSurname();
            listView.getItems().add(name);
        }
        if(listView.getItems().size()>0){
            listView.getSelectionModel().select(0);
        }
    }

    public void loadPatient(){
        int indexOfSelectedItem = listView.getSelectionModel().getSelectedIndex();
        Patient loadedPatient = listOfPatients.get(indexOfSelectedItem);
        mainController.setPatient(loadedPatient);
        mainController.refreshView();
        mainController.updateBill();
    }
}
