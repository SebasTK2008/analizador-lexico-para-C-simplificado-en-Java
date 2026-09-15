int main() {

    int numero;
    int contador;
    int resultado123;
    int _variable;
    int __Hola;
    int arreglo;

    numero = 12345;
    contador = 0;

    resultado123 = numero + contador;
    resultado123 = numero - contador;
    resultado123 = numero * contador;
    resultado123 = numero / 2;

    /* Operadores relacionales */
    if (numero > contador) {
        resultado123 = 1;
    }

    if (numero >= contador) {
        resultado123 = 2;
    }

    if (numero < contador) {
        resultado123 = 3;
    }

    if (numero <= contador) {
        resultado123 = 4;
    }

    if (numero == contador) {
        resultado123 = 5;
    }

    if (numero != contador) {
        resultado123 = 6;
    }

    /* Operadores logicos */
    if (numero > 0 && contador < 10) {
        resultado123 = numero;
    }

    if (numero > 0 || contador > 10) {
        resultado123 = contador;
    }

    if (!numero) {
        resultado123 = 0;
    }

    /* Operadores bit a bit */
    resultado123 = numero & contador;
    resultado123 = numero | contador;
    resultado123 = numero ^ contador;
    resultado123 = ~numero;

    /* Desplazamientos */
    resultado123 = numero << 2;
    resultado123 = numero >> 2;

    /* Agrupacion */
    resultado123 = (numero + contador);

    arreglo = numero;
    
    /* Palabras reservadas */
    do {
        contador = contador + 1;
    } while (contador < 10);

    while (numero > 0) {
        numero = numero - 1;

        if (numero == 50) {
            break;
        }

        else {
            resultado123 = numero;
        }
    }

    /* Funciones indicadas en la especificacion */
    scanf(numero);
    printf(resultado123);

    return 0;
}