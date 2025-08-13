package br.com.reservahotel.reserva_hotel.exceptions;

public class MensagemDeCampo {

    private String campo;
    private String mensagem;

    public MensagemDeCampo(String campo, String mensagem) {
        this.campo = campo;
        this.mensagem = mensagem;
    }

    public String getCampo() {
        return campo;
    }

    public String getMensagem() {
        return mensagem;
    }
}
