package br.com.reservahotel.reserva_hotel.exceptions;

import java.time.Instant;

public class RespostaErroApi {

    private Instant dataHora;
    private Integer status;
    private String mensagem;
    private String path;

    public RespostaErroApi(Instant dataHora, Integer status, String mensagem, String path) {
        this.dataHora = dataHora;
        this.status = status;
        this.mensagem = mensagem;
        this.path = path;
    }

    public Instant getDataHora() {
        return dataHora;
    }

    public Integer getStatus() {
        return status;
    }

    public String getMensagem() {
        return mensagem;
    }

    public String getPath() {
        return path;
    }
}
