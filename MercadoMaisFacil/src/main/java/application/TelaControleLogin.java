package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class TelaControleLogin {

    @FXML
    private Button btnEntrar;

    @FXML
    private ImageView imgFundoLogin;

    @FXML
    private ImageView imgIconeCadeado;

    @FXML
    private ImageView imgIconeEntrar;

    @FXML
    private ImageView imgIconeLogin;

    @FXML
    private ImageView imgIconeLojinha;

    @FXML
    private ImageView imgIconeUsuario;
    
    @FXML
    private ImageView imgIconeOlho;

    @FXML
    private ImageView imgLogoMercadoMaisFacil;

    @FXML
    private ImageView imgSecaoClientes;

    @FXML
    private ImageView imgSecaoEstoque;

    @FXML
    private ImageView imgSecaoFornecedores;

    @FXML
    private ImageView imgSecaoProdutos;

    @FXML
    private Label lblAcesseSistema;

    @FXML
    private Label lblBemVindo;

    @FXML
    private Label lblCadastrese;

    @FXML
    private Label lblLogin;

    @FXML
    private Label lblSeuMercadinho;

    @FXML
    private Label lblSistema;

    @FXML
    private Label lblTudoQVcPrecisa;

    @FXML
    private PasswordField txtSenha;

    @FXML
    private TextField txtSenhaVisivel;

    @FXML
    private TextField txtUsuario;

    private boolean senhaVisivel = false;

    @FXML
    public void initialize() {
        if (txtSenhaVisivel != null && txtSenha != null) {
            txtSenhaVisivel.textProperty().bindBidirectional(txtSenha.textProperty());
        }
    }

    @FXML
    void alternarSenhaVisivel(MouseEvent event) {
        senhaVisivel = !senhaVisivel;

        if (senhaVisivel) {
            txtSenhaVisivel.setVisible(true);
            txtSenha.setVisible(false);
        } else {
            txtSenhaVisivel.setVisible(false);
            txtSenha.setVisible(true);
        }
    }

    @FXML
    void logar(ActionEvent event) {
        
    }

}
