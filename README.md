# AppMoviles – Gestión de Arriendos (Junta de Vecinos)

Repositorio para desarrollar el ramo de aplicaciones móviles.

App Android (Kotlin + Jetpack Compose + Room) para administrar el arriendo de la sede
de una junta de vecinos y transparentar sus finanzas.

## Roles
| Rol | Qué puede hacer |
|---|---|
| Vecino/a | Solicitar la sede, ver reservas y ver la transparencia financiera |
| Directiva / Secretaría | Confirmar, rechazar, editar y eliminar reservas |
| Tesorería | Registrar pagos y gastos (con foto), ver y eliminar movimientos |

## Estructura
```
data/local        Entidades y DAOs de Room (Reserva, Pago, Gastos)
data/repository   Repositorios
viewmodel         ReservaViewModel y FinanzasViewModel
ui/theme          Paleta beige / verde bosque / ámbar, tipografía y formas
ui/components     Componentes reutilizables (barra, tarjetas, chips, selectores)
ui/screens        Pantallas
ui/navigation     Rutas de la app
util              Validadores, formato de pesos/fechas, cámara y notificaciones
```

## Pruebas
`app/src/test/.../LogicaNegocioTest.kt` prueba validadores, cálculo de saldo y formato.
Ejecutar con: `./gradlew test`
