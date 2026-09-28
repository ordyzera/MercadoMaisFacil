package application;

public class UltimaVenda {
	
	private String produto;
	private String cliente;
	private String data;
	private String valor;
	
	public UltimaVenda(String produto, String cliente, String data, String valor) {
		this.produto = produto;
		this.cliente = cliente;
		this.data = data;
		this.valor = valor;
		
	}
	
	public String getProduto() {
		return produto;
	}
	
	public String getCliente() {
		return cliente;
	}
	
	public String getData() {
		return data;
	}
	
	public String getValor() {
		return valor;
	}

}
