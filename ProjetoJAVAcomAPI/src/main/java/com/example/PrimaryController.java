package com.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

public class PrimaryController implements Initializable {

    @FXML Pagination pagination;

    private int pagina = 1;
    private int currentPage = 0;
    private List<Personagem> lista = new ArrayList<>();

    public FlowPane carregar(){

        try {
            int pageSize = 10;
            int startIndex = currentPage * pageSize;
            var url = new URL("https://thronesapi.com/api/v2/Characters?pageSize=" + pageSize + "&startIndex=" + startIndex);
            var con = url.openConnection();
            con.connect();
            var is = con.getInputStream();
        
            var reader = new BufferedReader(new InputStreamReader(is));
            var json = reader.readLine();
        
            var lista = jsonParaLista(json);
        
            return mostrarPersonagens(lista);
        
        } catch (IOException e) {
            e.printStackTrace();
            mostrarMensagem("Erro. " + e.getMessage());
            return null;
        }

    }

    private int carregarDadosDaAPI() {
        try {
            var url = new URL("https://thronesapi.com/api/v2/Characters");
            var con = url.openConnection();
            con.connect();
            var is = con.getInputStream();
    
            var reader = new BufferedReader(new InputStreamReader(is));
            var json = reader.readLine();
    
            lista = jsonParaLista(json);
    
            return lista.size();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarMensagem("Erro. " + e.getMessage());
        }
    
        return 0; 
    }

    private FlowPane mostrarPersonagens(List<Personagem> lista) {
        var flow = new FlowPane();
        flow.setVgap(20);
        flow.setHgap(20);

        lista.forEach(personagem ->{
            var image = new ImageView(new Image(personagem.getImageUrl()));
            image.setFitHeight(150);
            image.setFitWidth(150);
            var labelName = new Label(personagem.getFirstName());
            var labelSpecie = new Label(personagem.getTitle());
            flow.getChildren().add(new VBox(image, labelName, labelSpecie));
        }); 
        return flow;
    }

    private List<Personagem> jsonParaLista(String json) throws JsonMappingException, JsonProcessingException {
    var mapper = new ObjectMapper();
    JsonNode jsonNode = mapper.readTree(json);

    if (jsonNode.isArray()) {
        List<Personagem> lista = new ArrayList<>();

        for (JsonNode personagemNode : jsonNode) {
            try {
                lista.add(mapper.treeToValue(personagemNode, Personagem.class));
            } catch (JsonMappingException e) {
                e.printStackTrace();
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }

        return lista;
    } else {
        return Collections.emptyList();
    }
}

    private void mostrarMensagem(String mensagem){
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setContentText(mensagem);
        alert.show();
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        int totalPersonagens = carregarDadosDaAPI(); 
        int pageSize = 10;
        int totalPages = (int) Math.ceil((double) totalPersonagens / pageSize);
    
        pagination.setPageCount(totalPages);
    
        pagination.setPageFactory(pag -> {
            int startIndex = pag * pageSize;
            int endIndex = Math.min(startIndex + pageSize, totalPersonagens);
    
            List<Personagem> sublist = lista.subList(startIndex, endIndex);
    
            return mostrarPersonagens(sublist);
        });
    }
 
}
