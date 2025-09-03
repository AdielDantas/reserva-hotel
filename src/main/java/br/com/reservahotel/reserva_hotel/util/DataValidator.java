package br.com.reservahotel.reserva_hotel.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Slf4j
@UtilityClass
public class DataValidator {

    public static void validarDatasReserva(LocalDate checkin, LocalDate checkout) {
        if (checkin == null || checkout == null) {
            log.error("Datas de check-in ou check-out são nulas");
            throw new IllegalArgumentException("Datas de check-in e check-out são obrigatórias");
        }

        LocalDate hoje = LocalDate.now();

        if (checkin.isAfter(checkout)) {
            log.error("Check-in ({}) não pode ser depois do Check-out ({})", checkin, checkout);
            throw new IllegalArgumentException("Check-in não pode ser depois do Check-out");
        }

        if (checkin.isBefore(hoje)) {
            log.error("Check-in ({}) não pode ser no passado", checkin);
            throw new IllegalArgumentException("Check-in não pode ser no passado");
        }

        if (checkout.isBefore(hoje)) {
            log.error("Check-out ({}) não pode ser no passado", checkout);
            throw new IllegalArgumentException("Check-out não pode ser no passado");
        }

        if (checkin.isEqual(checkout)) {
            log.error("Check-in ({}) não pode ser igual ao Check-out ({})", checkin, checkout);
            throw new IllegalArgumentException("Período de hospedagem deve ser de pelo menos 1 dia");
        }

        log.debug("Datas validadas com sucesso: Check-in={}, Check-out={}", checkin, checkout);
    }

    public static void validarPeriodo(LocalDate checkin, LocalDate checkout, int diasMinimos) {
        validarDatasReserva(checkin, checkout);

        long diasHospedagem = java.time.temporal.ChronoUnit.DAYS.between(checkin, checkout);

        if (diasHospedagem < diasMinimos) {
            log.error("Período inválido: {} dias (mínimo requerido: {})", diasHospedagem, diasMinimos);
            throw new IllegalArgumentException("Período mínimo de hospedagem é " + diasMinimos + " dias");
        }
    }

    public static long calcularDiasHospedagem(LocalDate checkin, LocalDate checkout) {
        validarDatasReserva(checkin, checkout);
        return java.time.temporal.ChronoUnit.DAYS.between(checkin, checkout);
    }

    public static void validarDatasPeriodo(LocalDate dataInicial, LocalDate dataFinal) {
        // Verifica se ambas são nulas (consulta sem período)
        if (dataInicial == null && dataFinal == null) {
            log.debug("Consulta sem período específico");
            return; // Permite consulta sem período
        }

        // Verifica se apenas uma é nula (erro)
        if ((dataInicial != null && dataFinal == null) || (dataInicial == null && dataFinal != null)) {
            log.error("Datas incompletas - Data inicial: {}, Data final: {}", dataInicial, dataFinal);
            throw new IllegalArgumentException("Ambas as datas devem ser fornecidas ou ambas nulas");
        }

        // Verifica se data inicial é depois da data final
        if (dataInicial.isAfter(dataFinal)) {
            log.error("Datas inválidas - Data inicial {} é depois da data final {}", dataInicial, dataFinal);
            throw new IllegalArgumentException("Data inicial não pode ser depois da data final");
        }

        // Verifica se as datas estão no passado
        LocalDate hoje = LocalDate.now();
        if (dataInicial.isBefore(hoje)) {
            log.error("Data inicial ({}) não pode ser no passado", dataInicial);
            throw new IllegalArgumentException("Data inicial não pode ser no passado");
        }

        if (dataFinal.isBefore(hoje)) {
            log.error("Data final ({}) não pode ser no passado", dataFinal);
            throw new IllegalArgumentException("Data final não pode ser no passado");
        }

        log.debug("Período validado com sucesso: Data inicial={}, Data final={}", dataInicial, dataFinal);
    }

    public static void validarPeriodoCompleto(LocalDate dataInicial, LocalDate dataFinal, int diasMinimos) {
        validarDatasPeriodo(dataInicial, dataFinal);

        if (dataInicial != null && dataFinal != null) {
            long diasPeriodo = ChronoUnit.DAYS.between(dataInicial, dataFinal);

            if (diasPeriodo < diasMinimos) {
                log.error("Período muito curto: {} dias (mínimo: {})", diasPeriodo, diasMinimos);
                throw new IllegalArgumentException("Período mínimo é " + diasMinimos + " dias");
            }
        }
    }
}