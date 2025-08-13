package br.com.reservahotel.reserva_hotel.exceptions;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class RespostaErroValidation extends RespostaErroApi{

    private List<MensagemDeCampo> mensagemErros = new ArrayList<>();

    public RespostaErroValidation(Instant dataHora, Integer status, String mensagem, String path) {
        super(dataHora, status, mensagem, path);
    }

    public List<MensagemDeCampo> getMensagemErros() {
        return mensagemErros;
    }

    public void addError(String campo, String mensagem) {
        mensagemErros.add(new MensagemDeCampo(campo, mensagem));
    }
}
