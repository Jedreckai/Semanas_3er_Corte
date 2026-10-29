# Decisiones de diseño — Semana 3

## 1. Punto de entrada

El proyecto mantiene un único punto de entrada:

```text
IngestaSensores.main()
```

No se crean aplicaciones independientes por semana.

`BancoDePruebas` es una clase auxiliar y no contiene `main`.

## 2. Búsqueda por timestamp

Se utilizan dos estrategias:

- Búsqueda lineal: no requiere ordenamiento.
- Búsqueda binaria: requiere que el arreglo esté ordenado por timestamp.

Los datos sintácticos de `GeneradorDatos` se generan en orden cronológico, por lo que la búsqueda binaria por timestamp cumple su precondición.

## 3. Búsqueda por PM2.5

No se asume que los datos estén ordenados por PM2.5.

Por tanto, la búsqueda binaria por PM2.5 se conserva como experimento para demostrar el efecto de una precondición incumplida.

## 4. Comparación de String

Los identificadores de estación se comparan mediante:

```java
equals()
```

y no mediante:

```java
==
```

porque se necesita comparar contenido.

## 5. Medición

La comparación principal entre algoritmos utiliza el número de comparaciones.

El tiempo en milisegundos se conserva como evidencia experimental, pero no es la única medida utilizada.

## 6. Evolución del proyecto

La Semana 3 agrega una nueva capacidad a la misma plataforma:

```text
Sensores
   ↓
Ingesta
   ↓
Repositorio
   ↓
Búsqueda
   ↓
Medición de eficiencia
```

La Semana 4 pudo extender esta misma arquitectura para estudiar ordenamiento.

---

# Decisiones de diseño — Semana 5

## 7. Elección de estructura para almacenamiento

Se implementa `RepositorioEnlazado` como alternativa a `RepositorioArreglo`.
Ambas cumplen la interfaz `Repositorio`, lo que permite intercambiarlas sin
modificar el código que las utiliza (polimorfismo).

**Cuándo conviene la lista enlazada:**
- Cuando predominan las inserciones al inicio: costo O(1) constante, sin
  desplazar elementos ni redimensionar.
- Cuando el tamaño de la colección varía frecuentemente y no se conoce de
  antemano.

**Cuándo conviene el arreglo:**
- Cuando predominan las consultas por posición (índice arbitrario): acceso O(1)
  directo, frente a O(n) de la lista que debe recorrer desde el primero.
- Cuando se requieren algoritmos de búsqueda binaria eficientes: la lista
  necesita recorrer hasta la posición media en cada paso, eliminando la ventaja
  de reducir comparaciones.

## 8. Atributo `ultimo` en RepositorioEnlazado

Se mantiene un puntero `ultimo` para que `agregarAlFinal()` sea O(1) sin
recorrer toda la cadena. Si se omitiera, cada inserción al final costaría O(n).
El atributo debe actualizarse correctamente al eliminar el último nodo.

## 9. Método `contarRecorriendo()`

Se agrega este método como herramienta de verificación: recorre la cadena
eslabón a eslabón y cuenta los nodos reales. Permite detectar inconsistencias
entre el atributo `cantidad` y el estado verdadero de la lista, especialmente
después de operaciones de eliminación.

## 10. Casos límite en `eliminar()`

Se identificaron y se cubren con pruebas los siguientes casos:
- Posición inválida (negativa o ≥ tamaño): devuelve `false`, sin excepción.
- Lista vacía: devuelve `false`.
- Eliminar el primero: actualiza `primero`; si queda vacía, también `ultimo = null`.
- Eliminar el último: actualiza `ultimo`; el anterior queda como nuevo último.
- Eliminar el único nodo: `primero` y `ultimo` terminan en `null`.
- Eliminar del medio: el nodo anterior enlaza directamente con el siguiente del
  eliminado, conservando el acceso al resto de la cadena.

## 11. Lista doblemente enlazada — HistorialNavegable

Se usa una lista doble para el panel de control porque permite navegar en ambas
direcciones (hacia el pasado y el futuro) sin volver al inicio.
El costo adicional es un enlace `anterior` por nodo y la responsabilidad de
mantener ambos enlaces consistentes en cada inserción o eliminación.

El cursor se inicializa en el primer nodo al agregar el primer elemento y
permite al operador avanzar y retroceder lectura por lectura.

## 12. Experimentos vs. pruebas unitarias

Los experimentos de `BancoDeListas` miden rendimiento (movimientos, saltos,
tiempo). Son útiles para observar tendencias algorítmicas pero no sustituyen
las pruebas automatizadas, que comprueban la corrección del comportamiento para
casos de entrada concretos y detectan regresiones cuando el código cambia.

## 13. Resultados experimentales medidos (BancoDeListas)

Mediciones obtenidas en el entorno de ejecuci�n local:

### Experimento 3: Insertar al inicio
| Lecturas | Arreglo: Movimientos | Arreglo: ms | Lista: Saltos | Lista: ms |
| :--- | :--- | :--- | :--- | :--- |
| 10.000 | 49.995.000 | 217 ms | 0 | 1 ms |
| 50.000 | 1.249.975.000 | 2.826 ms | 0 | 62 ms |
| 100.000 | 4.999.950.000 | 11.225 ms | 0 | 0 ms |

**Conclusi�n:** Insertar al inicio en una lista enlazada es O(1) independiente del tama�o de entrada. En el arreglo escala como O(n�), alcanzando casi 5 mil millones de desplazamientos para 100k lecturas.

### Experimento 4: Recorrer por posici�n (obtener(i))
| Lecturas | Arreglo: Accesos | Arreglo: ms | Lista: Saltos | Lista: ms |
| :--- | :--- | :--- | :--- | :--- |
| 1.000 | 1.000 | 1 ms | 499.500 | 2 ms |
| 10.000 | 10.000 | 1 ms | 49.995.000 | 144 ms |
| 50.000 | 50.000 | 0 ms | 1.249.975.000 | 2.203 ms |

**Conclusi�n:** Recorrer una lista llamando repetidamente a obtener(i) acumula O(n�) saltos porque cada llamada reanuda desde el primer nodo. La forma adecuada de recorrer la lista es mediante un iterador o puntero ctual = actual.getSiguiente().

### Experimento 5: B�squeda binaria sobre una lista
- Tama�o de muestra: 50.000 lecturas ordenadas.
- B�squeda Lineal: 50.000 saltos entre nodos.
- B�squeda Binaria: 749.990 saltos entre nodos.

**Conclusi�n:** Aunque la b�squeda binaria realiza �nicamente 16 comparaciones l�gicas, el costo acumulado de desplazar el puntero hasta el punto medio en una lista enlazada supera por un orden de magnitud completo al recorrido lineal continuo. No debe aplicarse b�squeda binaria indexada sobre listas enlazadas simples.
