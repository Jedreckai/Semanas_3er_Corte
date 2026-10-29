import model.LecturaSensor;
import org.junit.jupiter.api.Test;
import repository.RepositorioEnlazado;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para RepositorioEnlazado.
 * Semana 5 - Estructuras de Datos - Plataforma de Monitoreo Ambiental Urbano
 *
 * Cubre todos los escenarios de la guia:
 *  - Lista nueva vacia
 *  - Insercion al inicio y al final
 *  - Eliminar en lista vacia (false, sin excepcion)
 *  - Indice invalido (false, sin cambio)
 *  - Eliminar el unico nodo (queda vacia)
 *  - Eliminar el primero (actualiza primero)
 *  - Eliminar del medio (cadena intacta)
 *  - Eliminar el ultimo (actualiza ultimo)
 *  - Consistencia tamano() vs contarRecorriendo()
 */
public class RepositorioEnlazadoTest {

    // ---------- helper ----------
    private LecturaSensor crearLectura(int n) {
        return new LecturaSensor(
            "EST-001",
            "2026-10-19T" + String.format("%02d", n % 24) + ":00:00",
            20.0 + n, 50.0, n
        );
    }

    // =====================================================
    //  ESCENARIO 1: Lista recien creada
    // =====================================================
    @Test
    public void testListaNuevaDebeEstarVacia() {
        RepositorioEnlazado lista = new RepositorioEnlazado();
        assertEquals(0, lista.tamano());
        assertEquals(0, lista.contarRecorriendo());
    }

    // =====================================================
    //  ESCENARIO 2: Insertar una lectura (inicio)
    // =====================================================
    @Test
    public void testAgregarAlInicioDebeAumentarElTamano() {
        RepositorioEnlazado lista = new RepositorioEnlazado();
        LecturaSensor lectura = crearLectura(1);
        lista.agregarAlInicio(lectura);
        assertEquals(1, lista.tamano());
        assertEquals(1, lista.contarRecorriendo());
        assertSame(lectura, lista.obtener(0));
    }

    // =====================================================
    //  ESCENARIO 3: Insertar una lectura (final) - usa ultimo
    // =====================================================
    @Test
    public void testAgregarAlFinalUsesUltimo() {
        RepositorioEnlazado lista = new RepositorioEnlazado();
        for (int i = 0; i < 5; i++) {
            LecturaSensor l = new LecturaSensor("EST-001", String.format("2026-03-02 %02d:00", i), 20.0, 50.0, i);
            lista.agregarAlFinal(l);
        }
        assertEquals(5, lista.tamano());
        LecturaSensor ultimo = lista.obtener(4);
        assertEquals(4.0, ultimo.getPm25());
        RepositorioEnlazado.reiniciarSaltos();
        assertEquals(0, RepositorioEnlazado.getSaltos());
    }

    // =====================================================
    //  ESCENARIO 4: Eliminar en lista vacia
    // =====================================================
    @Test
    public void testEliminarEnListaVaciaDevuelveFalse() {
        RepositorioEnlazado lista = new RepositorioEnlazado();
        // no debe lanzar excepcion y debe devolver false
        assertFalse(lista.eliminar(0));
        assertEquals(0, lista.tamano());
        assertEquals(0, lista.contarRecorriendo());
    }

    // =====================================================
    //  ESCENARIO 5: Indice invalido
    // =====================================================
    @Test
    public void testIndiceInvalidoDevuelveFalseYNoModificaContenido() {
        RepositorioEnlazado lista = new RepositorioEnlazado();
        lista.agregarAlFinal(crearLectura(0));
        lista.agregarAlFinal(crearLectura(1));
        assertFalse(lista.eliminar(-1));
        assertFalse(lista.eliminar(10));
        assertEquals(2, lista.tamano());       // contenido no cambio
        assertEquals(2, lista.contarRecorriendo());
    }

    // =====================================================
    //  ESCENARIO 6: Eliminar el unico nodo
    // =====================================================
    @Test
    public void testEliminarUnicoNodoDejaListaVacia() {
        RepositorioEnlazado lista = new RepositorioEnlazado();
        lista.agregarAlFinal(crearLectura(0));
        assertTrue(lista.eliminar(0));
        assertEquals(0, lista.tamano());
        assertEquals(0, lista.contarRecorriendo());
        // primero y ultimo deben quedar en null: obtener(0) debe ser null
        assertNull(lista.obtener(0));
    }

    // =====================================================
    //  ESCENARIO 7: Eliminar el primero (actualiza primero)
    // =====================================================
    @Test
    public void testEliminarFirstNode() {
        RepositorioEnlazado lista = new RepositorioEnlazado();
        LecturaSensor a = new LecturaSensor("EST-001", "t0", 20, 50, 0);
        LecturaSensor b = new LecturaSensor("EST-001", "t1", 21, 51, 1);
        LecturaSensor c = new LecturaSensor("EST-001", "t2", 22, 52, 2);
        lista.agregarAlFinal(a);
        lista.agregarAlFinal(b);
        lista.agregarAlFinal(c);
        assertTrue(lista.eliminar(0));
        assertEquals(2, lista.tamano());
        // el nuevo primero debe ser b
        LecturaSensor primero = lista.obtener(0);
        assertEquals(1.0, primero.getPm25());
    }

    // =====================================================
    //  ESCENARIO 8: Eliminar del medio (cadena intacta)
    // =====================================================
    @Test
    public void testEliminarMiddleNodeKeepsChain() {
        RepositorioEnlazado lista = new RepositorioEnlazado();
        for (int i = 0; i < 5; i++) {
            lista.agregarAlFinal(new LecturaSensor("EST-001", "t" + i, 20 + i, 50 + i, i));
        }
        assertTrue(lista.eliminar(2)); // elimina el elemento con pm25=2
        assertEquals(4, lista.tamano());
        // los elementos restantes deben estar en orden 0,1,3,4
        assertEquals(0.0, lista.obtener(0).getPm25());
        assertEquals(1.0, lista.obtener(1).getPm25());
        assertEquals(3.0, lista.obtener(2).getPm25());
        assertEquals(4.0, lista.obtener(3).getPm25());
    }

    // =====================================================
    //  ESCENARIO 9: Eliminar el ultimo (actualiza ultimo)
    // =====================================================
    @Test
    public void testEliminarLastNodeUpdatesUltimo() {
        RepositorioEnlazado lista = new RepositorioEnlazado();
        for (int i = 0; i < 3; i++) {
            lista.agregarAlFinal(new LecturaSensor("EST-001", "t" + i, 20 + i, 50 + i, i));
        }
        assertTrue(lista.eliminar(2)); // elimina el ultimo
        assertEquals(2, lista.tamano());
        // el ultimo elemento ahora debe ser el de indice 1
        assertEquals(1.0, lista.obtener(1).getPm25());
        // agregar otro al final debe funcionar correctamente
        lista.agregarAlFinal(crearLectura(99));
        assertEquals(3, lista.tamano());
        assertEquals(99.0, lista.obtener(2).getPm25());
    }

    // =====================================================
    //  ESCENARIO 10: Consistencia tamano() vs contarRecorriendo()
    // =====================================================
    @Test
    public void testContarRecorriendoMatchesCantidad() {
        RepositorioEnlazado lista = new RepositorioEnlazado();
        for (int i = 0; i < 7; i++) {
            lista.agregarAlFinal(new LecturaSensor("EST-001", "t" + i, 20 + i, 50 + i, i));
        }
        assertEquals(7, lista.tamano());
        assertEquals(7, lista.contarRecorriendo());
        // despues de eliminar, siguen coincidiendo
        lista.eliminar(3);
        assertEquals(lista.tamano(), lista.contarRecorriendo());
    }
}
