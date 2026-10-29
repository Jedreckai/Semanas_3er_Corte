# Bitácora — Semana 5: La Cadena

## Pregunta 1
### ¿Por qué insertar al inicio es más económico en una lista enlazada que en un arreglo?

Imagina que tienes una fila de sillas numeradas en un salón.
Si alguien nuevo quiere sentarse en la silla número 1 (al inicio),
tienes que pedirle a TODO EL MUNDO que se levante y se mueva una
silla hacia atrás para hacerle espacio. Si hay 100 personas, haces
100 movimientos. Eso es lo que hace el **arreglo**.

Ahora imagina que en vez de sillas, tienes cajas conectadas con flechas.
Cada caja dice "la siguiente caja está allá". Si alguien nuevo llega al
inicio, solo haces DOS cosas:
1. Le dices al nuevo: "apunta a la primera caja que ya existía".
2. Dices que el nuevo es ahora el primero.

¡Listo! Sin importar cuántas cajas haya, siempre son solo 2 pasos.
Eso es lo que hace la **lista enlazada**: insertar al inicio cuesta
siempre lo mismo, sin mover a nadie.

---

## Pregunta 2
### ¿Por qué el acceso a una posición arbitraria es más costoso en una lista?

Piensa en un collar de perlas. Para llegar a la perla número 50,
tienes que empezar desde la primera y contar una por una: 1, 2, 3...
hasta llegar a la 50. No puedes "saltar" directo a ella.

En cambio, un arreglo es como una regla numerada: si quieres el número
50, vas directo al espacio 50. No cuentas nada.

Por eso, en una lista enlazada acceder a una posición cualquiera puede
requerir recorrer muchos nodos, mientras que en un arreglo siempre
es un salto directo, sin importar la posición.

---

## Pregunta 3
### ¿Qué diferencia existe entre un experimento de rendimiento y una prueba unitaria?

Un **experimento de rendimiento** es como cronometrar a dos personas
a ver quién corre más rápido. Te dice QUIÉN ES MÁS RÁPIDO, pero no te
dice si están corriendo bien o haciendo trampa.

Una **prueba unitaria** es como un árbitro que verifica que cada corredor
siga las reglas: que salga desde la línea correcta, que no se salte
obstáculos, que llegue a la meta de verdad.

El experimento de `BancoDeListas` mide cuántos movimientos o cuánto tiempo
tarda cada estructura. Las pruebas de `RepositorioEnlazadoTest` verifican
que los métodos hagan exactamente lo que deben hacer (insertar bien,
eliminar bien, sin perder datos).

Ambas son útiles, pero por razones distintas.

---

## Pregunta 4
### ¿Qué caso límite consideras más importante probar en `eliminar()` y por qué?

El caso más importante es **eliminar el único nodo que existe en la lista**.

¿Por qué? Porque en ese momento pasan DOS cosas a la vez:
- El nodo que se va era el PRIMERO → hay que poner `primero = null`
- El nodo que se va era también el ÚLTIMO → hay que poner `ultimo = null`

Si nos olvidamos de actualizar cualquiera de los dos, la lista queda
"rota": dice que está vacía (tamaño 0) pero internamente sigue apuntando
a un nodo que ya no debería existir. Cuando intentes agregar algo nuevo,
lo conectas al nodo fantasma y todo se desordena.

Es como si borras el único contacto de tu agenda pero el teléfono sigue
marcando ese número borrado cuando abres la app: parece que no hay nadie,
pero el teléfono recuerda algo que ya no debe existir.

---

## Pregunta 5
### ¿Qué estructura elegirías según la operación más frecuente?

**Si predominan las inserciones al inicio → elegiría la lista enlazada.**

Porque cada inserción al inicio solo cambia 2 punteros, sin importar
cuántos datos haya. En el arreglo, insertar al inicio obliga a mover
TODOS los elementos una posición. Con 100.000 datos, eso son hasta
5.000.000.000 de movimientos (¡cinco mil millones!), mientras que
la lista hace exactamente 0 saltos.

**Si predominan las consultas por posición → elegiría el arreglo.**

Porque el arreglo llega directo a cualquier posición como si supiera
exactamente dónde está cada cosa. La lista, en cambio, tiene que caminar
nodo por nodo desde el inicio cada vez que buscas por posición, lo cual
se vuelve muy lento cuando la lista es grande.

La regla de oro es: **no existe una estructura perfecta para todo**.
Cada una paga un costo diferente. La lista paga al consultar por
posición; el arreglo paga al insertar al inicio.
