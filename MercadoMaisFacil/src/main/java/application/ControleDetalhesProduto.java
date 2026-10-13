package application;


import java.util.function.Consumer;
import javafx.stage.Stage;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class ControleDetalhesProduto {
	
    @FXML
    private Label lblNome;
    
    @FXML
    private Label lblCodigo;
    
    @FXML
    private Label lblCodigoBarras;
    
    @FXML
    private Label lblCategoria;
    
    @FXML
    private Label lblMarca;
    
    @FXML
    private Label lblPreco;
    
    @FXML
    private Label lblEstoque;
    
    @FXML
    private Label lblEstoqueMinimo;
    
    @FXML
    private Label lblUnidade;
    
    @FXML
    private Label lblStatus;
    

    @FXML
    private Button btnEditar;
    
    @FXML
    private Button btnFechar;
    
    @FXML
	private Produtos produtoAtual;
	
	@FXML
	private Consumer<Produtos> acaoEditar;

    @FXML
    private void fechar() {
        Stage janela = (Stage) btnFechar.getScene().getWindow();
        janela.close();
    }
   
	public void setProduto(Produtos produto) {
		
		this.produtoAtual = produto;
	
	    lblNome.setText(produto.getNome());
	    lblCodigo.setText(
	            String.format("%04d", produto.getId())
	    );
	
	    lblCodigoBarras.setText(
	            produto.getCodigoBarras() == null
	                    || produto.getCodigoBarras().isBlank()
	                    ? "Não informado"
	                    : produto.getCodigoBarras()
	    );
	
	    lblCategoria.setText(
	            produto.getCategoria() == null
	                    || produto.getCategoria().isBlank()
	                    ? "Não informada"
	                    : produto.getCategoria()
	    );
	
	    lblMarca.setText(
	            produto.getMarca() == null
	                    || produto.getMarca().isBlank()
	                    ? "Não informada"
	                    : produto.getMarca()
	    );
	
	    lblPreco.setText(
	            java.text.NumberFormat
	                    .getCurrencyInstance(
	                            new java.util.Locale("pt", "BR")
	                    )
	                    .format(produto.getPreco())
	    );
	
	    lblEstoque.setText(
	            produto.getQuantidade()
	                    + " "
	                    + produto.getUnidade()
	    );
	
	    lblEstoqueMinimo.setText(
	            String.valueOf(produto.getEstoqueMinimo())
	    );
	
	    lblUnidade.setText(produto.getUnidade());
	    lblStatus.setText(produto.getStatus());
	}
	
	public void setAcaoEditar(Consumer<Produtos> acaoEditar) {
		this.acaoEditar = acaoEditar;
	}
	
	@FXML
	private void editar() {
		
		Stage janela = (Stage) btnEditar.getScene().getWindow();
		janela.close();
		
		if (acaoEditar != null && produtoAtual != null) {
			acaoEditar.accept(produtoAtual);
			
		}
	}

}