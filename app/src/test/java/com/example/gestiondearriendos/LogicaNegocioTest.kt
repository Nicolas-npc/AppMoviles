package com.example.gestiondearriendos

import com.example.gestiondearriendos.util.CalcularFinanzas
import com.example.gestiondearriendos.util.FormatoUtil
import com.example.gestiondearriendos.util.ValidadorMovimiento
import com.example.gestiondearriendos.util.ValidadorReserva
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

/** Pruebas de la lógica que no depende de Android (se ejecutan en el PC). */
class LogicaNegocioTest {

    // ── ValidadorReserva ──
    @Test
    fun reservaValida_noDevuelveError() {
        assertNull(ValidadorReserva.validar("Ana", "2026-12-01", "10:00", "12:00", "Cumpleaños"))
    }

    @Test
    fun reservaConCamposVacios_devuelveError() {
        assertEquals(
            "Completa todos los campos",
            ValidadorReserva.validar("", "2026-12-01", "10:00", "12:00", "Cumpleaños")
        )
    }

    @Test
    fun reservaConHoraFinAntesQueInicio_devuelveError() {
        assertNotNull(ValidadorReserva.validar("Ana", "2026-12-01", "15:00", "12:00", "Reunión"))
    }

    @Test
    fun reservaConHoraMalEscrita_devuelveError() {
        assertNotNull(ValidadorReserva.validar("Ana", "2026-12-01", "25:00", "26:00", "Reunión"))
    }

    // ── ValidadorMovimiento ──
    @Test
    fun movimientoValido_noDevuelveError() {
        assertNull(ValidadorMovimiento.validar("Luz", "15000", "2026-11-13"))
    }

    @Test
    fun movimientoConMontoCero_devuelveError() {
        assertEquals("El monto tiene que ser mayor a 0", ValidadorMovimiento.validar("Luz", "0", "2026-11-13"))
    }

    @Test
    fun movimientoConMontoNoNumerico_devuelveError() {
        assertNotNull(ValidadorMovimiento.validar("Luz", "abc", "2026-11-13"))
    }

    // ── CalcularFinanzas ──
    @Test
    fun saldo_esIngresosMenosGastos() {
        assertEquals(7000, CalcularFinanzas.saldo(10000, 3000))
        assertEquals(-500, CalcularFinanzas.saldo(0, 500))
    }

    // ── FormatoUtil ──
    @Test
    fun pesos_usaPuntoComoSeparadorDeMiles() {
        assertEquals("$15.000", FormatoUtil.pesos(15000))
        assertEquals("$1.234.567", FormatoUtil.pesos(1234567))
        assertEquals("$0", FormatoUtil.pesos(0))
        assertEquals("-$2.500", FormatoUtil.pesos(-2500))
    }

    @Test
    fun iniciales_tomaLasDosPrimerasPalabras() {
        assertEquals("ML", FormatoUtil.iniciales("maría lópez soto"))
        assertEquals("A", FormatoUtil.iniciales("Ana"))
        assertEquals("?", FormatoUtil.iniciales("   "))
    }

    @Test
    fun fechaLegible_conTextoInvalido_loDevuelveIgual() {
        assertEquals("no-es-fecha", FormatoUtil.fechaLegible("no-es-fecha"))
    }
}
