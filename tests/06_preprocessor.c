#include <stdio.h>
#define SIZE 10
#if SIZE > 0
#ifdef SIZE
#ifndef OTHER
#elif OTHER
#else
#endif
#error failed
#endif
#undef SIZE