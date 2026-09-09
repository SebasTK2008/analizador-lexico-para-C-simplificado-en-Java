
int main() {
   
    int contador = 10;
    float factor = 3.1416;
    double precision_alta = 0.000123;
    char letra = 'A';
    char *mensaje = "Hola, Analizador Lexico!";
    
    
    if (contador >= 5 && factor != 0.0) {
        contador = contador + 5;
        contador--; 
    } else {
        contador = 0;
    }

    
    while (contador > 0) {
        contador--;
        if (contador == 3) {
            continue; 
        }
        break; 
    }

    
    for (int i = 0; i < 10; i += 2) {
        factor *= 1.5;
    }

  
    int resultado = (contador <= 5) ? 1 : 0;

   
    printf("Resultado de la prueba: %d\n", resultado);
    printf("Mensaje: %s\n", mensaje);

    return 0;
}
