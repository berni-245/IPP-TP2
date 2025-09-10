# Introducción a la Programación Paralela TP2

## 👋 Introducción
En este código se puso a prueba 2 problemas clásicos de programación (multiplicación de matrices y el problema de las N-Reinas) y probó distintas estrategias de la api de concurrencia de java para paralelizarlos.

Este fue el [enunciado](docs/Enunciado%20TP2.pdf).

### ❗ Requisitos

- Java 21+
- Maven
- Python3 y [UV](https://docs.astral.sh/uv/getting-started/installation) (En caso de querer correr el análisis hecho en jupyter lab)

## 🏃 Compilación y Ejecución

Primero se requiere compilar el proyecto:
```bash
mvn clean package
```

Finalmente se puede correr el programa por consola con el siguiente comando:
```bash
java {args} -cp {JAR_FILE} {MAIN_CLASS}
```
Con:
- `JAR_FILE` = target/IPP-TP2-1.0.jar
- `MAIN_CLASS` = matrix.MatrixMain or nqueens.NQueensMain
- `args` obligatorios listados en la siguiente sección

### 🛠️ Argumentos
Donde los argumentos `generales` son los siguientes:
- `numThreads`: Cantidad de threads para los algoritmos paralelos
- `type`: **sequential**/**parallel**/**fork_join**/**virtual_per_row**/**virtual_per_chunk** (el último solo para MatrixMain)
- `times`: Cantidad de veces que se correrá e imprimirá el algoritmo seleccionado

Argumentos exclusivos de `MatrixMain`:
- `size`: Cantidad de filas y columnas en las matrices 
- `DshowFirstCell`: true para mostrar result\[0]\[0] tras correr el algoritmo, cualquier otra cosa (o no incluir) para false

Argumentos exclusivos de `NQueensMain`:
- `N`: Cantidad de filas y columnas en las matrices NxN
- `showResult`: true para mostrar la cantidad de soluciones tras correr el algoritmo, cualquier otra cosa (o no incluir) para false


### ⭐ Ejemplo

```bash
mvn clean package
```

MatrixMain
```bash
java -Dsize=1024 -DnumThreads=8 -Dtype=parallel -Dtimes=3 -DshowFirstCell=true -cp target/IPP-TP2-1.0.jar matrix.MatrixMain
```

NQueensMain
```bash
java -DN=15 -DnumThreads=8 -Dtype=fork_join -Dtimes=3 -DshowResult=true -cp target/IPP-TP2-1.0.jar nqueens.NQueensMain
```

Ambos mains imprimirán siempre el tiempo de ejecución de cada time.


## 🔎 Análisis
Se necesitará tener UV instalado para el entorno virtual con las dependencias, podés seguir el [tutorial oficial](https://docs.astral.sh/uv/getting-started/installation) para instalarlo en tu sistema operativo.

Luego, correr:
```bash
uv run jupyter lab
```
Para crear el entorno virtual con las dependencias necesarias y visualizar los datos dentro del notebook.
Se creará una carpeta .venv, podés borrarla al terminar de usar uv.
