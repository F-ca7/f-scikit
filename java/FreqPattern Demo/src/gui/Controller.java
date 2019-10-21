package gui;

import apriori.MyApriori;
import fpgrowth.FpTree;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;

public class Controller {
    String filepath;
    public TextField txf_support;
    public TextField txf_confidence;
    public TextArea txa_apriori;
    public TextArea txa_fpgrowth;
    @FXML
    public void loadData(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("TXT", "*.txt"),
                new FileChooser.ExtensionFilter("CSV", "*.csv")
                );
        File file = fileChooser.showOpenDialog((Stage) ((Node) event.getSource()).getScene().getWindow());
        filepath = file.toString();
    }


    public void start(ActionEvent actionEvent) {
        if (filepath==null){
            return;
        }
        double support = Double.parseDouble(txf_support.getText());
        double confidence = Double.parseDouble(txf_confidence.getText());
        doApriori(support, confidence);
        doFpGrowth(support, confidence);
    }


    private void doApriori(double support, double confidence) {
        MyApriori.init(filepath, support, confidence);
        // loading dataset
        MyApriori.dataset.clear();
        MyApriori.associationRules.clear();
        MyApriori.dataset = MyApriori.loadDataFromFile(MyApriori.SEPARATOR);
        outputToApriori("Original dataset:");
        outputToApriori(MyApriori.datasetToString(MyApriori.dataset));

        // get 1‐item sets for candidate
        List<List<String>> oneItemSet = MyApriori.getFirstSet();

        // get frequent 1‐item sets for candidate
        List<List<String>> freqItemSet = MyApriori.prune(oneItemSet);
        outputToApriori("Frequent 1‐item sets:");
        outputToApriori(MyApriori.frequentSetToString(freqItemSet));

        // join and prune
        while(true){
            // join
            List<List<String>> candidateItemSet = MyApriori.join(freqItemSet);
            outputToApriori("CandidateItemSet after join:");
            outputToApriori(MyApriori.frequentSetToString(candidateItemSet));
            // prune
            List<List<String>> nextFrequentItemSet = MyApriori.prune(candidateItemSet);
            if(!MyApriori.endFlag){
                // update frequent item set
                freqItemSet = nextFrequentItemSet;
            } else {
                break;
            }
            outputToApriori("FrequentItemSet after prune:");
            outputToApriori(MyApriori.frequentSetToString(nextFrequentItemSet));

            MyApriori.generateAssociationRules(nextFrequentItemSet);

        }

        // until no more frequent k‐item sets can be found
        outputToApriori("----------------------------------");
        outputToApriori("Found frequent item set by Apriori:");
        outputToApriori(MyApriori.frequentSetToString(freqItemSet));
        outputToApriori("----------------------------------");
        outputToApriori("Association Rules by Apriori:");
        outputToApriori(MyApriori.rulesToString());
    }


    private void doFpGrowth(double support, double confidence) {
        FpTree.init(filepath, support, confidence);

        List<List<String>> dataset = FpTree
                .loadDataFromFile(FpTree.SEPARATOR);

        outputToFpGrowth("----------------------------------");
        outputToFpGrowth("Found frequent item set by FpTree:");
        FpTree.FPGrowthHelper(dataset, null);
        outputToFpGrowth(FpTree.getFreqResult());
        outputToFpGrowth("----------------------------------");
        outputToFpGrowth("Association Rules by FpTree:");
        outputToFpGrowth(FpTree.rulesToString());
    }


    private void outputToApriori(String line) {
        txa_apriori.appendText(line+"\n");
    }

    private void outputToFpGrowth(String line) {
        txa_fpgrowth.appendText(line+"\n");
    }
}
