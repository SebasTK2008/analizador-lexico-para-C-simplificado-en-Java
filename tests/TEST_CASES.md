# Tests del lexer

Cada archivo `.c` es una entrada y cada `.expected.txt` es su resultado esperado.

1. `01_basic_program`: asignación, declaraciones, agrupaciones. (Confirmado)
2. `02_identifiers_numbers`: identificadores, guiones bajos y números enteros. (Confirmado)
3. `03_single_symbols`: Todos los simbolos de un caracter. (Confirmado)
4. `04_compound_operators`: Todos los operadores compuestos por mas de un simbolo. (Confirmado)
5. `05_keywords`: Todas las palabras reservadas del C simplificado. (Confirmado)
6. `06_preprocessor`: directivas de preprocesamiento. (Confirmado)
7. `07_literals_escapes`: caracteres/strings y todos los caracteres de escape. (Confirmado)
8. `08_comments`: Comentarios de linea y bloque que deberían ser ignorados.
9. `09_invalid_literals`: secuencias invalidas de caracteres y escapes.
10. `10_invalid_input`: Caracteres invalidos.