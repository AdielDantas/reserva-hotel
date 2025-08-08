package br.com.reservahotel.reserva_hotel.exceptions;

public class MensagemDeCampo {

    private String nomeDoCampo;
    private String mensagem;

    public MensagemDeCampo(String nomeDoCampo, String mensagem) {
        this.nomeDoCampo = nomeDoCampo;
        this.mensagem = mensagem;
    }

    public String getNomeDoCampo() {
        return nomeDoCampo;
    }

    public String getMensagem() {
        return mensagem;
    }
}
