# Analizador Léxico para C Simplificado en Java

**Materia:** Compiladores 2 (COMPG2) — Tarea 1: *Diseñar un escáner para C*

Este proyecto implementa una **escáner (analizador léxico)** para un subconjunto simplificado del lenguaje C. La tarea consiste en transformar un archivo de código fuente C en una serie de **tokens** (palabras reservadas, símbolos especiales, `INT_NUM`, `ID`, literales de cadena/carácter, etc.) que se imprimen en la salida estándar.

El escáner fue construido **a mano, sin herramientas de generación automática** (Lex/Flex no está permitido) y bajo la norma de *no usar código generado por IA ni plagio*. La **segmentación de lexemas** se realiza mediante un **Autómata Finito Determinista (AFD)** representado como una tabla de transiciones; los literales de cadena/carácter y las directivas de preprocesamiento se resuelven con rutinas complementarias del `Scanner` (secciones 2.4, 2.5 y 5).

---

## 1. Descripción general

El programa funciona en tres etapas:

1. **Lectura del archivo**: mediante un diálogo nativo de Windows (AWT `FileDialog`) el usuario selecciona un archivo `.c` o `.txt`. El contenido se lee como un arreglo de caracteres.
2. **Segmentación (análisis léxico por AFD)**: la clase `Scanner` recorre el arreglo de caracteres carácter por carácter. Un AFD (clase `Automaton`) mantiene el estado actual y determina dónde termina cada **lexema** (la secuencia de caracteres que forma un token). Los literales de cadena/carácter no pasan por la tabla del AFD: se leen y validan con una rutina propia (`readQuotedLiteral`), y cada salto de línea se registra como un lexema interno para poder validar las directivas de preprocesamiento.
3. **Clasificación**: cada lexema se clasifica en un **tipo de token** (`TokenType`) mediante tablas de palabras reservadas, símbolos especiales y reglas de formación de identificadores y números, y se imprime con el formato `Token: TIPO "lexema"`. En esta etapa también se validan las directivas de preprocesamiento (`#` seguido de una directiva válida, al inicio de línea).

---

## 2. Tokens soportados

El escáner reconoce los 4 tipos de tokens exigidos por el enunciado, más extensiones (literales, directivas de preprocesamiento y otros operadores de C completo).

### 2.1 Palabras reservadas

Tokens de las palabras reservadas del C simplificado (sección 2.1 del enunciado):

| Palabra | Token     | Palabra | Token     |
|---------|-----------|---------|-----------|
| `int`   | `INT`     | `return`| `RETURN`  |
| `main`  | `MAIN`    | `scanf` | `READ`    |
| `void`  | `VOID`    | `printf`| `WRITE`   |
| `break` | `BREAK`   | `do`    | `DO`      |
| `else`  | `ELSE`    | `if`    | `IF`      |
| `while` | `WHILE`   |         |           |

Además se incluyen las palabras reservadas completas de C (según la tabla de la sección 3.3):

| Palabra     | Token     | Palabra     | Token     | Palabra   | Token    |
|-------------|-----------|-------------|-----------|-----------|----------|
| `auto`      | `AUTO`    | `double`    | `DOUBLE`  | `struct`  | `STRUCT` |
| `break`     | `BREAK`   | `else`      | `ELSE`    | `long`    | `LONG`   |
| `case`      | `CASE`    | `enum`      | `ENUM`    | `register`| `REGISTER` |
| `char`      | `CHAR`    | `extern`    | `EXTERN`  | `return`  | `RETURN` |
| `const`     | `CONST`   | `float`     | `FLOAT`   | `short`   | `SHORT`  |
| `continue`  | `CONTINUE`| `for`       | `FOR`     | `signed`  | `SIGNED` |
| `default`   | `DEFAULT` | `goto`      | `GOTO`    | `sizeof`  | `SIZEOF` |
| `do`        | `DO`      | `if`        | `IF`      | `static`  | `STATIC` |
| `int`       | `INT`     | `switch`    | `SWITCH`  | `typedef` | `TYPEDEF`|
| `union`     | `UNION`   | `unsigned`  | `UNSIGNED`| `volatile`| `VOLATILE`|
| `void`      | `VOID`    | `while`     | `WHILE`   |           |          |

### 2.2 Símbolos especiales y operadores

| Símbolos | Token               | Símbolos | Token                |
|----------|---------------------|----------|----------------------|
| `{`      | `LBRACE`            | `}`      | `RBRACE`             |
| `[`      | `LSQUARE`           | `]`      | `RSQUARE`            |
| `(`      | `LPAR`              | `)`      | `RPAR`               |
| `;`      | `SEMI`              | `,`      | `COMMA`              |
| `+`      | `PLUS`              | `-`      | `MINUS`              |
| `*`      | `MUL_OP`            | `/`      | `DIV_OP`             |
| `&`      | `AND_OP` (bit a bit) | `|`     | `OR_OP` (bit a bit)  |
| `^`      | `XOR_OP`            | `~`      | `BW_NOT`             |
| `!`      | `NOT_OP`            | `=`      | `ASSIGN`             |
| `<`      | `LT`                | `>`      | `GT`                 |
| `<<`     | `SHL_OP`            | `>>`     | `SHR_OP`             |
| `==`     | `EQ`                | `!=`     | `NOTEQ`              |
| `<=`     | `LTEQ`              | `>=`     | `GTEQ`               |
| `&&`     | `ANDAND` (lógico)   | `\|\|`   | `OROR` (lógico)      |
| `.`      | `DOT`               | `->`     | `ARROW`              |
| `#`      | `HASH`              |          |                      |

### 2.3 `INT_NUM` e `ID`

Se aplican las reglas del enunciado:

```
digit  = [0-9]
letter = [a-z A-Z]
INT_NUM = digit+
ID      = ( letter | '_' ) ( digit | letter | '_' )*
```

- `INT_NUM`: una o más cifras decimales, p. ej. `12345`, `0`.
- `ID`: comienza con letra o guion bajo (`_`), seguido de letras, dígitos o guiones bajos, p. ej. `numero`, `_variable`, `resultado123`, `__Hola`.

### 2.4 Literales de cadena, carácter y secuencias de escape

El escáner gestiona de forma especial los **literales de cadena** (`"..."`) y los **literales de carácter** (`'...'`), ya que su contenido puede incluir cualquier caracter que el alfabeto general no reconoce. Ambos admiten las secuencias de escape del enunciado:

```
"\n", "\0", "\r", "\t", "\\", "\v", "\f", "\a", "\'", "\""
```

- Se generan los tokens `STRING` (para `"..."`) y `CHAR_LIT` (para `'...'`). El lexema conserva las comillas.
- Dentro de un literal se acepta cualquier carácter (incluidas tildes, `ñ`, `¡`, etc.) y también la comilla del otro tipo (`'"'`, `"it's"`).
- Validaciones (cualquiera de ellas detiene el análisis con un error):
  - **Secuencia de escape inválida** (p. ej. `\q`): error con la posición del `\`.
  - **Escape incompleto** (`\` al final del archivo): error con la posición.
  - **Salto de línea real dentro del literal**: error con la posición del salto. Es el caso típico de un literal que no se cierra en su línea.
  - **Literal sin cerrar hasta el final del archivo**: error con la posición de inicio del literal.
  - **Literal de carácter con longitud inválida**: `'...'` debe contener exactamente un carácter o una secuencia de escape, por lo que `''` y `'ab'` son errores (este mensaje no incluye posición).

### 2.5 Directivas de preprocesamiento

El símbolo `#` se clasifica como token `HASH` y la palabra que lo sigue se reconoce como directiva usando un mapa propio (`preprocessorDirectives` en `Scanner`), separado del mapa de palabras reservadas:

| Directiva     | Token               | Directiva  | Token               |
|---------------|---------------------|------------|---------------------|
| `include`     | `PREPROC_INCLUDE`   | `define`   | `PREPROC_DEFINE`    |
| `if`          | `PREPROC_IF`        | `else`     | `PREPROC_ELSE`      |
| `elif`        | `PREPROC_ELIF`      | `endif`    | `PREPROC_ENDIF`     |
| `error`       | `PREPROC_ERROR`     | `ifdef`    | `PREPROC_IFDEF`     |
| `ifndef`      | `PREPROC_IFNDEF`    | `message`  | `PREPROC_MESSAGE`   |
| `undef`       | `PREPROC_UNDEF`     |            |                     |

Reglas de validación:

- `#` solo puede aparecer **al inicio de una línea** (se permiten espacios o tabulaciones antes). En otro lugar se lanza `El símbolo '#' debe aparecer al inicio de una línea.`
- Después de `#` debe venir, en la misma línea, una **directiva válida**. Si viene otra palabra se lanza `Directiva de preprocesamiento no reconocida: ...` (p. ej. `#pragma`); si la línea o el archivo terminan justo después de `#`, se lanza `Se esperaba una directiva de preprocesamiento después del símbolo '#'`.
- El resto de la línea se tokeniza con las reglas normales. Por ejemplo, `#include <stdio.h>` produce `HASH`, `PREPROC_INCLUDE`, `LT`, `ID`, `DOT`, `ID`, `GT`, y `#include "mi.h"` produce `HASH`, `PREPROC_INCLUDE`, `STRING`.
- Fuera de un `#`, los nombres de directiva son identificadores normales (`int include;` es válido), y `if`/`else` siguen siendo las palabras reservadas `IF`/`ELSE`.

---

## 3. Arquitectura del proyecto

```
analizador-lexico-para-C-simplificado-en-Java/
├── README.md
├── .vscode/
│   └── settings.json          # configuración del proyecto Java en VS Code
├── bin/                       # clases compiladas (.class) y archivos de entrada de ejemplo
├── tests/                     # casos de prueba: cada .c tiene su salida esperada (.expected.txt)
│   ├── 01_basic_program.c / .expected.txt
│   ├── 02_identifiers_numbers.c / .expected.txt
│   ├── 03_single_symbols.c / .expected.txt
│   ├── 04_compound_operators.c / .expected.txt
│   ├── 05_keywords.c / .expected.txt
│   ├── 06_preprocessor.c / .expected.txt
│   ├── 07_literals_escapes.c / .expected.txt
│   ├── 08_invalid_literals.c / .expected.txt
│   ├── 09_invalid_input.c / .expected.txt
│   └── TEST_CASES.md          # descripción de los casos
└── src/                       # código fuente
    ├── Main.java              # punto de entrada
    ├── input/
    │   ├── CFileReader.java   # lectura del archivo .c/.txt mediante FileDialog
    │   ├── prueba_lexer.c     # archivo de prueba (caso con operadores + scanf/printf)
    │   └── prueba_lexer2.c    # archivo de prueba (directiva #include + literal de cadena)
    ├── lexer/
    │   ├── Alphabet.java      # alfabeto de entrada del autómata (enum)
    │   ├── Status.java        # estados del autómata (enum)
    │   ├── Automaton.java     # tabla de transiciones, clasificación y estados de aceptación
    │   └── Scanner.java       # recorrido de caracteres, obtención y clasificación de lexemas
    └── token/
        ├── Token.java         # par (tipo de token, lexema)
        └── TokenType.java     # tipos de token (enum)
```

### Responsabilidades de cada clase

| Clase            | Responsabilidad                                                                 |
|------------------|---------------------------------------------------------------------------------|
| `Main`           | Orquesta el flujo: lee el archivo, invoca al escáner e imprime los tokens.      |
| `CFileReader`    | Abre un diálogo nativo para seleccionar el archivo y lo convierte en `char[]`.  |
| `Alphabet`       | Enum del alfabeto de entrada del AFD (`LETTER`, `DIGIT`, `SIMBOL`, `EQUAL`, ...). |
| `Status`         | Enum de los estados del AFD (`START`, `IN_IDENTIFIER`, `IN_NUMBER`, ..., `STOP`). |
| `Automaton`      | Tabla de transiciones del AFD, método `classify` y conjunto de estados de aceptación. |
| `Scanner`        | Recorre el arreglo de caracteres y aísla lexemas (`getLexemas`), lee y valida literales (`readQuotedLiteral`), valida directivas de preprocesamiento (`classifyAllTokens`) y clasifica cada lexema (`classifyToken`). |
| `Token`          | Representación de un token con su tipo y su lexema.                              |
| `TokenType`      | Enum con todos los tipos de token reconocidos.                                   |

---

## 4. Diseño del autómata (AFD)

> Esta sección responde a las preguntas 1 y 2 del informe técnico.

### 4.1 ¿Qué tipo de autómata se diseñó?

Se diseñó un **Autómata Finito Determinista (AFD)**.

#### ¿Por qué AFD y no AFND?

La elección entre un AFND y un AFD (DFA, *Deterministic Finite Automaton*) se decidió a favor del **AFD** por las siguientes razones:

1. **Determinismo estricto en cada paso.** En un AFD, para cada par `(estado, símbolo de entrada)` existe **exactamente una** transición definida. En el escáner esto se garantiza porque el `Alphabet.classify(char)` mapea cada carácter *posible* a un único elemento del alfabeto y la tabla de transiciones `Automaton.table[estado][símbolo]` tiene una celda (y solo una) por combinación. No hay transiciones ε (movimientos espontáneos sin consumir entrada), ni múltiples destinos alternativos entre los que haya que adivinar la ruta "correcta". Todo carácter leído determina unívocamente el siguiente estado, por lo que el análisis se puede hacer **en una sola pasada, de izquierda a derecha y sin retroceso**.

2. **Eficiencia y simplicidad del algoritmo de escaneo.** Como no hay ramificación ni retroceso, al analizar un archivo con `n` caracteres la complejidad es **O(n)**, lo que hace que el escáner sea lineal en el tamaño de la entrada (cada carácter se consulta y consume como máximo una vez como cierre de un lexema y una vez como inicio del siguiente). Un AFND habría requerido o bien retroceder probando caminos alternativos, o bien convertirse previamente a un AFD equivalente (paso que no aporta nada para reconocer los patrones regulares de este lenguaje).

3. **Implementación directa con una tabla de transiciones.** Un AFND se implementaría típicamente con conjuntos de estados activos o con backtracking; en cambio, un AFD se traduce de forma inmediata y legible a una **matriz bidimensional** donde `fila = estado actual`, `columna = símbolo del alfabeto` y `celda = estado siguiente`. Esto evita cadenas largas de `if/else if` o `switch` y deja el diseño del autómata **explicitado como dato** (la tabla), que además es fácil de revisar, depurar y justificar en el informe técnico.

4. **Equivalencia de poder expresivo.** Todo lenguaje regular que un AFND puede reconocer puede ser reconocido por un AFD equivalente (los AFND y los AFD son equivalentes en cuanto a los lenguajes que aceptan). Dado que los tokens del C simplificado se definen con expresiones regulares (palabras reservadas fijas, `INT_NUM = [digit]+`, `ID = (letter | _)(digit | letter | _)*`, y operadores de longitud limitada como `==`, `<<`, `->`, ...), todos pertenecen al conjunto de los lenguajes regulares y, por lo tanto, **siempre existe un AFD que los reconoce sin ambigüedad**. Elegir el AFD no significa perder capacidad de reconocimiento respecto de un AFND.

#### Definición formal del AFD implementado

Formalmente, el AFD construido es la tupla:

```
M = ( Q, Σ, δ, q₀, F )
```

Donde:

- **`Q`** — conjunto finito de estados: `{ START, IN_IDENTIFIER, IN_LESS_THAN, IN_GREATER_THAN, IN_NEGATION, IN_OR, IN_AND, IN_NUMBER, IN_EQUAL, IN_SIMBOL, IN_MINUS, IN_ARROW, STOP }` (enum `Status`).
- **`Σ`** — alfabeto de entrada: los 16 símbolos de `Alphabet` (`UNDERSCORE`, `LETTER`, `DIGIT`, `SIMBOL`, `EQUAL`, `LESS_THAN`, `GREATER_THAN`, `NEGATION`, `OR`, `AND`, `WHITE_SPACE`, `HASH`, `CARET`, `DOT`, `TILDE`, `MINUS`). Los caracteres que no caen en ninguno se clasifican como `INVALID` y se reportan como error léxico.
- **`δ`** — función de transición `δ: Q × Σ → Q`, representada por la matriz `Automaton.table[estado.ordinal()][symbol.ordinal()]`.
- **`q₀`** — estado inicial: `START`.
- **`F`** — conjunto de estados de aceptación (`Automaton.aceptance`): `{ IN_IDENTIFIER, IN_NUMBER, IN_LESS_THAN, IN_GREATER_THAN, IN_NEGATION, IN_OR, IN_AND, IN_EQUAL, IN_SIMBOL, IN_MINUS, IN_ARROW }`.

Algunas propiedades de diseño que se deducen de la formalización:

- **Función total**: la tabla tiene una entrada para *todas* las combinaciones estado–símbolo; la celda marcada como `STOP` no es un "agujero" sino la transición explícita que indica *"aquí terminó el lexema actual"* (retorno controlado y sin ambigüedad).
- **Máximo lookahead de 1 carácter**: del estado `STOP` se "reprocesa" el carácter actual como inicio del siguiente lexema, así que el autómata lee un carácter más allá del final de cada lexema (token `STOP` como recepción de lexema, distinción de `<=` vs `<`, etc.).
- **Lenguaje reconocido**: la unión de los lenguajes regulares de todos los tipos de token del lenguaje C simplificado descritos en la sección 2. El escáner **reconoce individualmente** (uno por uno) los lexemas que se van agrupando en la lista, de modo que la traza de tokens es la transcripción línea a línea del enunciado (`Token: TIPO "lexema"`).
- **Alcance del AFD**: la tabla reconoce identificadores/palabras reservadas, enteros, símbolos y operadores. Los literales de cadena/carácter (`readQuotedLiteral`) y las directivas de preprocesamiento (`classifyAllTokens`) se resuelven con rutinas complementarias del `Scanner`, no con filas de la tabla; por eso el AFD no tiene estados para las comillas y trata `#` solo como un símbolo de un carácter.

### 4.2 ¿Cómo se diseñó el autómata?

**Alfabeto de entrada.** Cada carácter del archivo se clasifica primero en un elemento del alfabeto (`Alphabet.java`) mediante el método `Automaton.classify(char)`:

`UNDERSCORE`, `LETTER`, `DIGIT`, `SIMBOL`, `EQUAL`, `LESS_THAN`, `GREATER_THAN`, `NEGATION`, `OR`, `AND`, `WHITE_SPACE`, `HASH`, `CARET`, `DOT`, `TILDE`, `MINUS`, `INVALID`.

**Estados.** Se definieron en `Status.java`:

| Estado            | Significado                                                                 |
|-------------------|-----------------------------------------------------------------------------|
| `START`           | Estado inicial; ningún lexema en construcción.                              |
| `IN_IDENTIFIER`   | Se está leyendo un identificador (o palabra reservada; se decide después).  |
| `IN_NUMBER`       | Se está leyendo un entero (`INT_NUM`).                                      |
| `IN_LESS_THAN`    | Se leyó `<`; puede continuar con `=` o `<` formando `<=` o `<<`.            |
| `IN_GREATER_THAN` | Se leyó `>`; puede continuar con `=` o `>` formando `>=` o `>>`.            |
| `IN_NEGATION`     | Se leyó `!`; puede continuar con `=` formando `!=`.                         |
| `IN_OR`           | Se leyó `|`; puede continuar con `|` formando `\|\|`.                       |
| `IN_AND`          | Se leyó `&`; puede continuar con `&` formando `&&`.                         |
| `IN_EQUAL`        | Se leyó `=`; puede continuar con `=` formando `==`.                         |
| `IN_MINUS`        | Se leyó `-`; puede continuar con `>` formando `->`.                         |
| `IN_ARROW`        | Lexema `->` completo (estado de aceptación que ya no se extiende).          |
| `IN_SIMBOL`       | Símbolo de un solo carácter (`{ } [ ] ( ) ; + * / , # ^ . ~`).                      |
| `STOP`            | Estado de terminación: el lexema actual no continúa (no es de aceptación).  |

**Tabla de transiciones.** Cada fila es el estado actual, cada columna un símbolo del alfabeto y cada celda el estado siguiente (`Automaton.table[estado][símbolo]`). Cuando la celda da `STOP`, significa que el lexema terminó y el carácter que provocó la parada inicia un nuevo lexema.

Columna de referencia (16 columnas):

```
UNDERSCORE, LETTER, DIGIT, SIMBOL, EQUAL, LESS_THAN, GREATER_THAN,
NEGATION, OR, AND, WHITE_SPACE, HASH, CARET, DOT, TILDE, MINUS
```

**Estados de aceptación.** Un lexema se da por completo si el estado en el que se detuvo pertenece al conjunto `aceptance` (`Automaton.isAceptance`):

`IN_IDENTIFIER`, `IN_NUMBER`, `IN_LESS_THAN`, `IN_GREATER_THAN`, `IN_NEGATION`, `IN_OR`, `IN_AND`, `IN_EQUAL`, `IN_SIMBOL`, `IN_MINUS`, `IN_ARROW`.

---

## 5. Función de escaneo

> Esta sección responde a la pregunta 3 del informe técnico.

El escaneo se implementa en la clase `Scanner` en dos fases:

### 5.1 Fase 1: obtención de lexemas — `getLexemas()`

Recorre el arreglo de caracteres de izquierda a derecha manteniendo el estado actual del autómata y un `StringBuilder` con el lexema en construcción:

1. **Carácter inicial**: se clasifica con `automaton.classify(c)`.
   - Si es `WHITE_SPACE`, se cierra el lexema en construcción (si es de aceptación) y se avanza. Espacios y tabulaciones son *separadores* y no producen tokens. Un salto de línea `\n` además agrega a la lista un lexema interno `"\n"` (`NEW_LINE`), que `classifyAllTokens` usa para saber cuándo empieza una línea (necesario para validar `#`) y descarta sin generar token.
   - Si es un símbolo **no válido** (`INVALID`), se lanza `IllegalArgumentException` indicando el carácter y la posición.
2. **Transición**: se consulta `tabla[estado][símbolo]`.
   - Si la celda es **distinta de `STOP`**, el carácter se agrega al lexema y se actualiza el estado (el lexema continúa).
   - Si la celda es **`STOP`**, el lexema terminó: si el estado es de aceptación se guarda en la lista, se reinicia el estado a `START` y el carácter actual (el que no pudo continuar el lexema) **comienza un nuevo lexema**.
3. **Literal de cadena/carácter**: cuando se encuentra `"` o `'` se delega a `readQuotedLiteral`, que copia el contenido hasta la comilla de cierre validando cada secuencia de escape, y lo agrega como un único lexema (si un lexema estaba en construcción, primero se cierra). Lanza un error si hay un escape inválido o incompleto, un salto de línea real dentro del literal, si no se cierra antes del final del archivo, o si un literal de carácter no contiene exactamente un carácter (ver sección 2.4).
4. Al llegar al final del arreglo, si el estado es de aceptación se guarda el último lexema.

Resultado: una lista de lexemas completa del archivo.

> **Nota sobre comentarios:** el escáner **no** elimina comentarios (`/* ... */` ni `//`); estos se tokenizan carácter por carácter (`/` → `DIV_OP`, `*` → `MUL_OP`, y el texto interior como `ID`/`INT_NUM`). Si el texto del comentario contiene un carácter que el escáner no acepta (tildes, `:`, `%`, `@`) o una comilla sin cerrar (como un apóstrofo), el análisis se detiene con un error. Se documenta como limitación en la sección 8.

### 5.2 Fase 2: clasificación — `classifyAllTokens()` y `classifyToken()`

`classifyAllTokens()` recorre la lista de lexemas y, antes de clasificar cada uno, maneja el preprocesador:

- Los lexemas `NEW_LINE` no generan token; solo marcan que la siguiente línea comienza (y es un error que una línea termine justo después de un `#`).
- Un `#` genera el token `HASH` (error si no está al inicio de línea) y deja al escáner esperando una directiva.
- El lexema que sigue a `#` se busca en `preprocessorDirectives` y genera su token `PREPROC_*` (error si no es una directiva válida).
- Cualquier otro lexema se pasa a `classifyToken()`.

`classifyToken()` recibe un lexema y decide su tipo, en este orden de prioridad:

1. **Literal de cadena** (`"..."` de apertura y cierre) → `STRING`.
2. **Literal de carácter** (`'...'` de apertura y cierre) → `CHAR_LIT`.
   > Los literales se revisan primero porque su contenido puede ser cualquier secuencia de caracteres y no debe buscarse en los mapas.
3. **Palabra reservada** (búsqueda en `reservedWords`) → el `TokenType` correspondiente (`INT`, `IF`, `RETURN`, `READ`, ...).
4. **Símbolo especial** (búsqueda en `specialSymbols`) → el `TokenType` correspondiente (`ASSIGN`, `EQ`, `LBRACE`, ...). Nota: aunque el AFD agrupa `<=`, `<<`, etc. en un mismo lexema, la clasificación final aquí distingue el token exacto.
5. **Entero** (todos sus caracteres son dígitos) → `INT_NUM`.
6. **Identificador** (empieza con letra o `_`, el resto letras/dígitos/`_`) → `ID`.
7. Si nada coincide → `IllegalArgumentException("Lexema no reconocido: ...")`.

El par `(Tipo, lexema)` se representa con la clase `Token`, y `Main` los imprime con el formato:

```
Token: MAIN "main"
Token: LPAR "("
```

---

## 6. Compilación y ejecución

### Requisitos

- **JDK 11 o superior** (se usa `java.nio.file.Files.readString`, JDK 11+). El proyecto se abre directamente en **VS Code** con la extensión *Extension Pack for Java*.

### Compilar

Desde la raíz del proyecto:

```
javac -d bin -sourcepath src src\Main.java
```

(Con `-sourcepath src` esta única instrucción compila también el resto de paquetes por dependencia. Sin esa opción `javac` no encuentra los paquetes `input`, `lexer` y `token` y falla con `package input does not exist`. Desde VS Code la compilación se hace automáticamente.)

### Ejecutar

```
java -cp bin Main
```

Al ejecutar aparece un **diálogo de selección de archivo**: elige un archivo `.c` o `.txt` (por ejemplo `src\input\prueba_lexer.c`). El programa imprime en la consola la ruta elegida (o el mensaje de cancelación) y luego la lista de tokens, uno por línea.

### Archivos de prueba incluidos

| Archivo                              | Contenido de prueba                                                    |
|--------------------------------------|------------------------------------------------------------------------|
| `src\input\prueba_lexer.c`           | Declaraciones, operadores aritméticos, relacionales, lógicos, bit a bit, desplazamientos, `do-while`, `break`, `scanf`, `printf`. |
| `src\input\prueba_lexer2.c`          | Directiva `#include`, literal de cadena con secuencia de escape `\n`.  |

> Nota: `prueba_lexer.c` contiene comentarios `/* ... */`, que se tokenizan como se explica en la sección 8.

### Casos de prueba (`tests/`)

Cada archivo `.c` de la carpeta `tests/` tiene un `.expected.txt` con el resultado esperado. Los 9 casos pasan con la implementación actual.

| Caso                       | Qué prueba                                                                                   | Resultado esperado |
|----------------------------|----------------------------------------------------------------------------------------------|--------------------|
| `01_basic_program`         | Programa del enunciado: declaraciones, asignación, suma y `return`.                           | Lista de tokens    |
| `02_identifiers_numbers`   | Identificadores con guiones bajos y números enteros.                                          | Lista de tokens    |
| `03_single_symbols`        | Todos los símbolos de un carácter.                                                            | Lista de tokens    |
| `04_compound_operators`    | `==`, `!=`, `<=`, `>=`, `<<`, `>>`, `&&`, `\|\|`, `->`.                                        | Lista de tokens    |
| `05_keywords`              | Todas las palabras reservadas.                                                                | Lista de tokens    |
| `06_preprocessor`          | `#include`, `#define`, `#if`, `#ifdef`, `#ifndef`, `#elif`, `#else`, `#endif`, `#error`, `#undef`. | Lista de tokens |
| `07_literals_escapes`      | Literales de carácter y de cadena con todas las secuencias de escape.                         | Lista de tokens    |
| `08_invalid_literals`      | `''`, `'ab'`, `'\q'` y `"bad\q"`.                                                             | Error (se reporta el primero) |
| `09_invalid_input`         | Carácter inválido `@` y cadena sin cerrar.                                                    | Error en `@` con su posición |

Para comprobar un caso, ejecuta `Main`, selecciona el `.c` y compara la salida con su `.expected.txt`. En los casos de error el programa imprime `Error: <mensaje>` y se detiene en el primer error. Como la lectura usa `FileDialog`, la comparación es manual por ahora (no hay un ejecutor automático de pruebas).

---

## 7. Ejemplo

Dado el archivo de entrada (tomado del enunciado):

```c
int main() {
  int a;
  int b;
  a = b + 1;
  return 0;
}
```

La salida del escáner es:

```
Token: INT "int"
Token: MAIN "main"
Token: LPAR "("
Token: RPAR ")"
Token: LBRACE "{"
Token: INT "int"
Token: ID "a"
Token: SEMI ";"
Token: INT "int"
Token: ID "b"
Token: SEMI ";"
Token: ID "a"
Token: ASSIGN "="
Token: ID "b"
Token: PLUS "+"
Token: INT_NUM "1"
Token: SEMI ";"
Token: RETURN "return"
Token: INT_NUM "0"
Token: SEMI ";"
Token: RBRACE "}"
```

---

## 8. Limitaciones y decisiones conocidas

- **Comentarios no se ignoran**: las secuencias `/* ... */` y `//` no se tratan como comentarios sino que se tokenizan carácter por carácter (`/` → `DIV_OP`, `*` → `MUL_OP` y el texto interior como `ID`/`INT_NUM`). Además, si el texto del comentario contiene un carácter fuera del alfabeto (tildes, `:`, `%`, `@`, ...) o una comilla sin cerrar (por ejemplo un apóstrofo), el análisis se detiene con un error, así que los comentarios deben evitar esos caracteres. Es una decisión de alcance (el formato de salida del enunciado no los contempla).
- **`++`, `--` y asignaciones compuestas**: el AFD no tiene estados combinados para `++`, `--`, `+=`, `-=`, etc.; cada signo se emite como token independiente (`PLUS` `PLUS`, `PLUS` `ASSIGN`, ...).
- **Lectura por `FileDialog`**: la selección de archivo usa AWT `FileDialog`, por lo que la ejecución requiere entorno gráfico (no funciona en una consola pura sin ventanas).
- **Solo enteros en `INT_NUM`**: no se reconocen números reales/flotantes (el enunciado solo define `INT_NUM = [digit]+`); por ejemplo, `3.14` produce `INT_NUM` `DOT` `INT_NUM`.
- **Caracteres no soportados**: `:`, `%` y `?` no pertenecen al alfabeto, por lo que fuera de un literal provocan un error. Esto incluye el `:` de `case x:` y `default:`, aunque `case` y `default` sí son palabras reservadas.
- **Directivas de preprocesamiento**: solo se reconocen las 11 directivas de la sección 2.5 (otras, como `#pragma`, dan error) y `#` debe estar al inicio de línea. El contenido de la directiva no se interpreta: no hay expansión de macros ni resolución de `#include`.
- **Detección de errores**: un carácter fuera del alfabeto (p. ej. `@`, `%`, `?`, `:` o letras con tilde fuera de un literal), un literal inválido (escape desconocido, salto de línea dentro, sin cerrar, literal de carácter con longitud distinta de 1) o una directiva mal formada detiene el análisis con una `IllegalArgumentException`; `Main` imprime `Error: <mensaje>`. Solo se reporta el primer error. La mayoría de los mensajes incluye la posición, excepto el de literal de carácter con longitud inválida.

---

## 9. Criterios de evaluación cubiertos (sección 3.3 del enunciado)

- **Corrección del programa (80%)**: el escáner pasa los 9 casos de la carpeta `tests/` (programa básico, identificadores y números, símbolos, operadores compuestos, palabras reservadas, directivas, literales con escapes, literales inválidos y entrada inválida) y procesa los archivos de ejemplo de `src/input/`. Cubre el conjunto de tokens de la especificación (palabras reservadas simplificadas y completas, símbolos, operadores, `INT_NUM`, `ID`, directivas, literales y secuencias de escape).
- **Estilo de código y comentario (10%)**: el código está modularizado por paquetes (`input`, `lexer`, `token`), con nombres descriptivos y comentarios que explican el diseño del autómata, la tabla de transiciones y el flujo de escaneo.
- **Informe técnico (10%)**: las tres preguntas requeridas (tipo de autómata, diseño del autómata y función de escaneo) se responden de forma clara y concisa en las secciones 4 y 5 de este documento, listas para adaptar al informe en PDF.